/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * GPS unit
 *
 *  ert   19/08/2009   creating
 */
#include "stdafx.h"
#include "GPSUnit.h"

#include <Msgqueue.h>
#include <Pm.h>
#include <Pmpolicy.h>

#include <string>
#include <algorithm>
#include <StdFuncs.h>

const DWORD SYNC_TIMEOUT = 5 * 60 * 1000; // 5 minutes
HANDLE hPwr;

class Handlers : public std::vector<GPSUnit::Handler*>
{
public:
   Handlers();
   ~Handlers();

   void AddHandler(GPSUnit::Handler *h);
   void RemoveHandler(GPSUnit::Handler *h);

   void DataChanged(const GPSUnitData &newData, GPSUnit *unit);
   void StateChanged(GPSUnit::EState state, GPSUnit *unit);

protected:
   HANDLE mutex;
} handlers;


class GPSComPort
{
public:
   GPSComPort(WORD portNo, DWORD baudRate = 4096) : handle(INVALID_HANDLE_VALUE)
   {
      this->port = portNo;
      this->baudRate = baudRate;
   }

   ~GPSComPort()
   {
      if( handle != INVALID_HANDLE_VALUE ) Close(); 
   }

   bool Open();
   bool ReadSentence(std::string *val);
   void Close();

   bool IsClosed() const { return (handle == INVALID_HANDLE_VALUE); }

protected:
   HANDLE handle;
   WORD   port;
   DWORD  baudRate;
   DWORD  waitStart;

protected:

   bool ReadTill(char sym, std::string *val);
   BYTE CRC(const std::string &str);
};

//class GSMHelper
//{
//public:
//   struct ProcData
//   {
//      HANDLE stopEvent;
//      HANDLE thread;
//
//      WORD fireInterval;
//
//      GSMData data, cdata;
//      HANDLE  evDone;
//   };
//
//   GSMHelper();
//
//   void Destroy();
//
//   void SetData(WORD fireInterval);
//
//   ~GSMHelper() {}
//
//protected:
//   static DWORD Do(ProcData *data);
//
//protected:
//   ProcData data;
//};

GPSUnit* GPSUnit::runned = NULL;
//HANDLE GPSUnit::hThread = 0;
//HANDLE GPSUnit::evStop = 0;

//static bool logStarted = false;
//void Log(const char* msg, ... )
//{
//   wchar_t name[MAX_PATH];
//   GetModuleFileName(0, name, sizeof(name)/sizeof(name[0]));
//   wcscat(name, L".txt");
//
//   if( !logStarted )
//   {
//      DeleteFile(name);
//      logStarted = true;
//   }
//
//   va_list args;
//   va_start(args, msg);
//
//   FILE *file = _wfopen(name, L"at");
//   if( file )
//   {
//      SYSTEMTIME st;
//      GetLocalTime(&st);
//      fprintf(file, "%02d:%02d:%02d ", st.wHour, st.wMinute, st.wSecond);
//
//      vfprintf(file, msg, args);
//      fputs("\n", file);
//
//      fclose(file);
//   }
//}


//
// ------------------------------- GPSUnit ----------------------------------
//
GPSUnit::GPSUnit(WORD flags) : state(NotOpened), /*fireInterval(0), */port(NULL), evSyncedState(0), accuracy(1)
{
   lastDataTick = 0;
   lastParseTick = 0;

   this->flags = flags;

   //if( (flags & UseGSM) != 0 )
   //   gsmHelper = new GSMHelper();
   //else
   //   gsmHelper = NULL;

   //Log("Started");
}

bool GPSUnit::Runned()
{
   return (runned != NULL);
}

struct FindData
{
   GPSComPort *port;
   bool isGps;
};

static DWORD TryReadPort(FindData* data)
{
   std::string val;
   data->isGps = data->port->ReadSentence(&val);

   return 0;
}

int GPSUnit::FindGPSPort()
{
   int i;
   for( i=0; i<10; i++ )
   {
      GPSComPort port(i);
      if( port.Open() )
      {
         FindData param;
         param.port = &port;
         param.isGps = false;

         HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryReadPort, &param, 0, NULL);
         WaitForSingleObject(thread, 5000);

         port.Close();
         CloseHandle(thread);
         if( param.isGps )
            break;
      }
   }

   return (i < 10) ? i : -1;
}

GPSUnit::~GPSUnit()
{
   //if( gsmHelper != NULL )
   //{
   //   gsmHelper->Destroy();
   //   delete gsmHelper;
   //}
   if( port != NULL ) delete port;
}

void GPSUnit::ChangeAccuracy(DWORD newAccuracy)
{
   if( newAccuracy != 0 )
      accuracy = newAccuracy;
}

GPSUnit::EState GPSUnit::WaitData(GPSData* data, WORD portNo, DWORD milliseconds, HANDLE hEvStop)
{
   GPSUnit gps(false);
   GPSUnit *cur = (runned != NULL) ? runned : &gps;

   EState st = cur->State();

   if( st != Synced )
      st = cur->WaitSync(portNo, milliseconds, hEvStop);

   if( st == Synced )
      cur->CopyCurData(data);

   return st;
}

bool GPSUnit::CopyCurData(GPSData *data) const
{
   *data = this->data.value.gps;

   //if( this->data.gpsData )
   //{
   //   *data = this->data.value.gps;
   //   return true;
   //}
   return false;
}


bool GPSUnit::Init(WORD portNo)
{
   if( state != NotOpened ) return false;

   SetState(Opening);

   if( port != NULL )
      delete port;

   port = new GPSComPort(portNo);
   return port->Open();
}

void GPSUnit::ReadPort(DWORD doSeconds)
{
   std::string val;
   DWORD curtick = GetTickCount();
   bool readed = false;

   doSeconds *= 1000;
   do
   {
      if( port == NULL )
         return;

      val.clear();
      if( port->ReadSentence(&val) )
      {
         readed = true;

         if( state == NotOpened )
            SetState(Opening);

         const char *str = val.c_str();
         bool failRead = (strncmp(str, "GPRMC", sizeof("GPRMC")-1) != 0 || !ParseData(str + sizeof("GPRMC")) );
         if( failRead )
         {
            if( GetTickCount() - lastParseTick > SYNC_TIMEOUT )
               SetState(Opening);
         }
         else
         {
            lastParseTick = GetTickCount();
            Log("%s", str);
            break;
         }
      }
   } while( (GetTickCount() - curtick) < doSeconds );

   if( !readed )
   {
      if( port->IsClosed() )
         SetState(NotOpened);
   } else
   {
#if defined(Test) && defined (DEBUG)
#else
      port->Close();
#endif
   }
}

static int ValueToDeg(const char *valA)
{
   const char *p = strchr(valA, '.');
   if( p == NULL ) p = valA + strlen(valA);
   while( *valA == ' ' ) valA++;

   DWORD val = 0;
   p -= 2;
   while( valA < p )
   {
      val *= 10;
      if( !isdigit(*valA) )
         return -1;

      val += *valA - '0';

      valA++;
   }
   if( val >= 180 ) return -1;

   val *= GPS_SCALE;

   DWORD rest = ((*valA - '0') * 10 + (valA[1] - '0'));
   DWORD cur = 1;

   valA += 2;
   if( *valA == '.' ) valA++;
   while( cur < GPS_SCALE )
   {
      char sym = *valA;
      rest *= 10;
      if( sym )
      {
         if( !isdigit(sym) )
            sym = 0;
         else
            rest += *valA - '0';
      }

      cur *= 10;
      if( sym ) valA++;
   }
   return val + rest/60;
}

static DWORD DateDiff(const FILETIME &ft1, const FILETIME &ft2)
{
   __int64 val1 = (__int64)ft1.dwHighDateTime * 1000000000 + ft1.dwLowDateTime;
   __int64 val2 = (__int64)ft2.dwHighDateTime * 1000000000 + ft2.dwLowDateTime;
   return (DWORD)((val1 - val2) / 10000000);
}

static bool CopyToken(char *dest, const char *src, const char **ep)
{
   *dest = '\0';
   if( *src == '\0' )
   {
      *ep = src;
      return false;
   }

   while( *src != ',' && *src != '\0' )
   {
      *dest++ = *src++;
   }

   *dest = '\0';
   *ep = (*src == '\0') ? src : src+1;

   return true;
}

bool GPSUnit::ParseData(const char *str)
{
   SYSTEMTIME st;
   GPSData cdata;
   DWORD value;
   int ival;
   char *buf;

   buf = (char*)alloca(strlen(str)+1);

   // time
   CopyToken(buf, str, &str);
   value = atoi(buf);

   // warning
   CopyToken(buf, str, &str);
   while( *buf == ' ' ) buf++;
   if( *buf != 'A' ) return false;

   st.wHour = (WORD)(value / 10000);
   value %= 10000;
   st.wMinute = (WORD)(value / 100);
   st.wSecond = value % 100;

   // latitude
   CopyToken(buf, str, &str);
   ival = ValueToDeg(buf);
   if( ival < 0 ) return false;
   // North / South
   CopyToken(buf, str, &str);
   while( *buf == ' ' ) buf++;
   if( *buf == 'S' ) ival = -ival;
   cdata.latitude = ival;

   // longitude
   CopyToken(buf, str, &str);
   ival = ValueToDeg(buf);
   if( ival < 0 ) return false;
   // East / West
   CopyToken(buf, str, &str);
   while( *buf == ' ' ) buf++;
   if( *buf == 'W' ) ival = -ival;
   cdata.longitude = ival;

   // speed
   CopyToken(buf, str, &str);
   cdata.speed = (WORD)(atof(buf) * 1609 / 10);

   // course -- skip
   CopyToken(buf, str, &str);

#if defined(Test) && defined (DEBUG)
   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &cdata.date);
#else
   // date
   CopyToken(buf, str, &str);
   value = atoi(buf);
   st.wDay = (WORD)(value / 10000);
   value %= 10000;
   st.wMonth = (WORD)(value / 100);
   st.wYear = value % 100 + 2000;
   st.wMilliseconds = 0;
   st.wDayOfWeek = 0;

   FILETIME ft;
   SystemTimeToFileTime(&st, &ft);
   FileTimeToLocalFileTime(&ft, &cdata.date);
#endif

   CheckData(cdata);

   return true;
}

void GPSUnit::AddHandler(Handler *handler)
{
   handlers.AddHandler(handler);
}

void GPSUnit::RemoveHandler(Handler *handler)
{
   handlers.RemoveHandler(handler);
}

//void GPSUnit::SetData(WORD fireInterval)
//{  
//   if( this->fireInterval == 0 )
//      this->fireInterval = fireInterval * 1000;
//
//   //if( gsmHelper != NULL )
//   //   gsmHelper->SetData(fireInterval);
//}

bool GPSUnit::DoReadPort(DWORD doSeconds)
{
   if( runned )
      runned->ReadPort(doSeconds);

   return true;
}

//static HANDLE CreateMQ(WORD port, DWORD cbPowerMsgSize)
//{
//   MSGQUEUEOPTIONS mqo;
//   mqo.dwSize = sizeof(MSGQUEUEOPTIONS);
//   mqo.dwFlags = MSGQUEUE_NOPRECOMMIT;
//   mqo.dwMaxMessages = 4;
//   mqo.cbMaxMessage = cbPowerMsgSize;
//   mqo.bReadAccess = TRUE;
//
//   return CreateMsgQueue(NULL, &mqo);
//}

//DWORD GPSUnit::DoGPS(void* port)
//{
   //wchar_t portBuf[50];
   //wsprintf(portBuf, L"COM%d:", (WORD)port);
   //HANDLE hpr = SetPowerRequirement(portBuf, D0, POWER_NAME|POWER_FORCE, NULL, NULL);

   //PowerPolicyNotify(PPN_UNATTENDEDMODE, TRUE);

   //BYTE buf[sizeof(POWER_BROADCAST) + (MAX_PATH * sizeof(TCHAR))];
   //DWORD cbPowerMsgSize = sizeof(buf);
   //POWER_BROADCAST *ppb = (POWER_BROADCAST*)buf;
   //HANDLE hmq = CreateMQ((WORD)port, cbPowerMsgSize);
   //HANDLE hpn = RequestPowerNotifications(hmq, PBT_TRANSITION);

   //runned->Init((WORD)port);

   //HANDLE events[2];
   //events[0] = evStop;
   //events[1] = hmq;
   //bool resetTimer = false;

   //while( true )
   //{
   //   //DWORD res = WaitForMultipleObjects(1, events, FALSE, 100);
   //   DWORD res = WaitForMultipleObjects(2, events, FALSE, 100);
   //   if( res == WAIT_OBJECT_0 ) break;
   //   if( res == WAIT_OBJECT_0 + 1 )
   //   {
   //      DWORD cbReaded, dwFlags;
   //      while(ReadMsgQueue(hmq, ppb, cbPowerMsgSize, &cbReaded, 0, &dwFlags))
   //      {
   //         if( ppb->Message == PBT_TRANSITION )
   //         {
   //            if( (ppb->Flags & POWER_STATE_SUSPEND) )
   //               SetSystemPowerState(NULL, POWER_STATE_IDLE, POWER_FORCE);

   //            resetTimer = ((ppb->Flags & (POWER_STATE_UNATTENDED|POWER_STATE_SUSPEND)) != 0); //!wcsicmp(ppb->SystemPowerState, L"unattended" );
   //            
   //            //char buf[300];
   //            //int len = wcstombs(buf, ppb->SystemPowerState, sizeof(buf) - 1);
   //            //buf[len] = '\0';
   //            //Log("State %s, %X", buf, ppb->Flags);
   //         }
   //      }

   //      continue;
   //   }
   //   else if( res != WAIT_TIMEOUT ) break;

   //   if( resetTimer )
   //   {
   //      //Log("Rest timert");
   //      SystemIdleTimerReset(); 
   //   }

   //   runned->ReadPort();
   //}

   //ReleasePowerRequirement(hpr);
   //CloseHandle(hpr);

   //StopPowerNotifications(hpn);
   //CloseHandle(hpn);
   //CloseMsgQueue(hmq);

   //PowerPolicyNotify(PPN_UNATTENDEDMODE, FALSE);

//   return 0;
//}

bool GPSUnit::InitGPS(WORD portNo)
{
   if( runned != NULL )
      return false;

   //evStop = CreateEvent(NULL, TRUE, FALSE, NULL);
   //if( evStop == 0 )
   //   return false;g

   WORD flags = 0;
   //if( useGSMCell ) flags |= UseGSM;

   wchar_t portBuf[50];
   wsprintf(portBuf, L"COM%d:", (WORD)portNo);
   hPwr = SetPowerRequirement(portBuf, D0, POWER_NAME|POWER_FORCE, NULL, NULL);
   PowerPolicyNotify(PPN_UNATTENDEDMODE, TRUE);

   runned = new GPSUnit(flags);
   //runned->SetData(fireInterval);
   return runned->Init(portNo);

   //hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoGPS, (void*)(DWORD)portNo, 0, 0);
   //return true;
}

void GPSUnit::SetAccuracy(DWORD accuracy)
{
   if( runned != NULL )
      runned->ChangeAccuracy(accuracy);
}

void GPSUnit::Stop()
{
   HCURSOR sv = SetCursor(LoadCursor(NULL, IDC_WAIT));

   //Log("Stopping...");
   if( runned != 0 )
   {
      //SetEvent(evStop);

      //if( hThread && WaitForSingleObject(hThread, 500) != WAIT_OBJECT_0 )
      //{
      //   TerminateThread(hThread, 0);
      //}

      delete runned;
      runned = NULL;

      ReleasePowerRequirement(hPwr);
      CloseHandle(hPwr);
      PowerPolicyNotify(PPN_UNATTENDEDMODE, FALSE);

      //CloseHandle(evStop);
      //evStop = 0;

      //CloseHandle(hThread);
      //hThread = 0;
   }

   //Log("Stopped");
   SetCursor(sv);
}

void GPSUnit::CheckData(const GPSData &cdata)
{
   if( state != Synced )
      SetState(Synced);

   //if( (fireInterval == 0) || ((GetTickCount() - lastDataTick) >= fireInterval) )
   {
      bool dataChanged = false;
      if( accuracy == 1 )
         dataChanged = (data.value.gps.latitude != cdata.latitude || data.value.gps.longitude != cdata.longitude);
      else
      {
         int distance = Distance(data.value.gps.latitude, data.value.gps.longitude, cdata.latitude, cdata.longitude);
         dataChanged = ( (DWORD)((distance > 0) ? distance : -distance) > accuracy);
      }

      if( dataChanged )
      {
         data = cdata;

         lastDataTick = GetTickCount();
         handlers.DataChanged(data, this);
      }
   }
}

void GPSUnit::SetState(EState newState)
{
   //if( newState == Synced && gsmHelper != NULL )
   //{
   //   gsmHelper->Destroy();
   //   delete gsmHelper;
   //   gsmHelper = NULL;
   //}

   if( state != newState )
   {
      //if( state == Synced && (flags & UseGSM) != 0 )
      //{
      //   if( gsmHelper == NULL )
      //   {
      //      // при переходе в нерабочее состояние включаем GSM 
      //      gsmHelper = new GSMHelper();
      //   }
      //}

      state = newState;
      handlers.StateChanged(state, this);
   }

   if( evSyncedState != 0 && newState == Synced )
   {
      SetEvent(evSyncedState);
   }
}

GPSUnit::EState GPSUnit::WaitSync(WORD portNo, DWORD milliseconds, HANDLE hEvStop)
{
   if( state != Synced ) 
   {
      if( port != NULL )
      {
         evSyncedState = CreateEvent(NULL, TRUE, FALSE, NULL);

         HANDLE obj[2];
         obj[0] = evSyncedState;
         obj[1] = hEvStop;

         WORD count = (hEvStop == 0) ? 1 : 2;
         WaitForMultipleObjects(count, obj, FALSE, milliseconds);

         CloseHandle(evSyncedState);
         evSyncedState = NULL;

      } else
      {
         DWORD st = GetTickCount();
         if( Init(portNo) )
         {
            do
            {
               ReadPort(0);

               if( hEvStop != 0 && WaitForSingleObject(hEvStop, 0) == WAIT_OBJECT_0 )
                  break;
            } while( state != Synced && (GetTickCount() - st) < milliseconds );
         }
      }
   }

   return state;
}

//
//--------------------------- GPSComPort ---------------------------------
//
//static DWORD TryReadPort(HANDLE handle)
//{
//   BYTE sym;
//   DWORD cb;
//
//   ReadFile(handle, &sym, sizeof(sym), &cb, NULL);
//
//   return 0;
//}

bool GPSComPort::Open()
{
   wchar_t buf[20];
   wsprintf(buf, L"COM%d:", port);
   handle = CreateFile(buf, GENERIC_READ, 0, NULL, OPEN_EXISTING, 0, NULL);

   if( handle == INVALID_HANDLE_VALUE )
   {
      Log( "Open COM%d error %d", port, GetLastError());
      return false;
   }
   //Log( "Open port %d", port);

   SetupComm(handle, 4096, 4096 ) ;

   DCB dcb = { 0 };
   dcb.DCBlength = sizeof(dcb);
   //GetCommState(handle, &dcb);

   dcb.BaudRate = baudRate;
   dcb.ByteSize = 8;
   dcb.StopBits = 2;
   dcb.XonLim = 2048;
   dcb.XoffLim = 512;
   dcb.fBinary = 1;
   SetCommState(handle, &dcb);

   COMMTIMEOUTS ct = {0};
   //GetCommTimeouts(handle, &ct);
   ct.ReadIntervalTimeout = baudRate/100;
   ct.ReadTotalTimeoutMultiplier = 0;
   ct.ReadTotalTimeoutConstant = 0;
   SetCommTimeouts(handle, &ct);

   waitStart = 0;

   // check port for read
   //HANDLE hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryReadPort, (void*)handle, 0, NULL);
   //if( WaitForSingleObject(hThread, 300) == WAIT_TIMEOUT )
   //{
   //   TerminateThread(hThread, 0);

   //   CloseHandle(handle);
   //   handle = INVALID_HANDLE_VALUE;

   //   return false;
   //}
   return true;
}

void GPSComPort::Close()
{
   if( handle != INVALID_HANDLE_VALUE )
   {
      //Log("Closing port");

      PurgeComm(handle, PURGE_RXABORT | PURGE_TXABORT);
      CloseHandle(handle);
      handle = INVALID_HANDLE_VALUE;
   }
}

const DWORD COM_MAX_WAIT_TIME = 60 * 1000;

bool GPSComPort::ReadTill(char endSym, std::string *val)
{
   if( handle == INVALID_HANDLE_VALUE ) return false;

   DWORD cb;
   char sym = 0;
   bool retVal = false;

   std::string dbgStr;
   if( val == NULL ) val = &dbgStr;

   DWORD readWait = GetTickCount();
   do
   {
      if( ReadFile(handle, &sym, sizeof(sym), &cb, NULL) == FALSE || cb == 0 )
      {
         if( waitStart == 0 )
            waitStart = GetTickCount();
         else if( (GetTickCount() - waitStart) > COM_MAX_WAIT_TIME ) 
         {
            waitStart = 0;
            Close();
            Open();
         }

         if( (GetTickCount() - readWait) > 1000 )
            break;
         continue;
      }

      readWait = GetTickCount();
      waitStart = 0;

      if( sym == endSym )
      {
         retVal = true;
         break;
      }
      if( val ) val->append(1, sym);
   } while( true );

   //if( !retVal )
   //{
   //   if( val == NULL || val->empty() )
   //      Log("No read");
   //   else
   //      Log( "Read %s", val->c_str());
   //}
   return retVal;
}

inline BYTE ToHex(char sym) { return (sym >= '0' && sym <= '9') ? sym - '0' : (toupper(sym) - 'A') + 10; }

BYTE GPSComPort::CRC(const std::string &str)
{
   BYTE crc = 0;
   std::string::const_iterator i = str.begin();
   for( ; i != str.end(); i++ )
      crc ^= (*i);

   return crc;
}

bool GPSComPort::ReadSentence(std::string *val)
{
   if( handle == INVALID_HANDLE_VALUE && !Open() )
   {
      //Log("Error Open");
      return false;
   }

   if( !ReadTill('$', NULL) )
   {
      //Log("Error read $");
      return false;
   }
   if( !ReadTill('*', val) )
   {
      //Log("Error Read *");
      return false;
   }

   BYTE crc;
   char sym;
   DWORD cb;

   std::string tv;

   if( ReadFile(handle, &sym, sizeof(sym), &cb, NULL) == FALSE ) return false;
   crc = ToHex(sym) * 0x10;
   tv.append(1, sym);
   if( ReadFile(handle, &sym, sizeof(sym), &cb, NULL) == FALSE ) return false;
   crc += ToHex(sym);
   tv.append(1, sym);

   if( CRC(*val) != crc )
   {
      //Log("%s*%s, crc error %d", val->c_str(), tv.c_str(), crc);
      //Log("Error CRC");
      return false;
   }

   //Log("Read %s", val->c_str());
   return true;
}

//
//-------------------------- GSMHelper -------------------------
//
//GSMHelper::GSMHelper()
//{
//   data.stopEvent = NULL;
//   data.thread = NULL;
//}
//
//void GSMHelper::Destroy()
//{
//   if( data.thread != NULL )
//   {
//      SetEvent(data.stopEvent);
//
//      if( WaitForSingleObject(data.thread, 500) != WAIT_OBJECT_0 )
//         TerminateThread(data.thread, 0);
//
//      CloseHandle(data.thread);
//      data.thread = NULL;
//   }
//
//   if( data.stopEvent != NULL )
//   {
//      CloseHandle(data.stopEvent);
//      data.stopEvent = NULL;
//   }
//}
//
//void GSMHelper::SetData(WORD fireInterval)
//{
//   if( data.thread == NULL )
//   {
//      data.stopEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
//      data.fireInterval = fireInterval;
//
//      data.thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)Do, &data, 0, NULL);
//   }
//}
//
//
////
//// -------------------- from ril.h -----------------------------
////
//#define MAXLENGTH_BCCH                              (48)      // @constdefine 48
//#define MAXLENGTH_NMR                               (16)      // @constdefine 16
// 
//typedef HANDLE HRIL, *LPHRIL;
//
//typedef void (CALLBACK *RILRESULTCALLBACK)(
//    DWORD dwCode,           // @parm result code
//    HRESULT hrCmdID,        // @parm ID returned by the command that originated this response
//    const void* lpData,     // @parm data associated with the notification
//    DWORD cbData,           // @parm size of the strcuture pointed to lpData
//    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
//);
//
//typedef void (CALLBACK *RILNOTIFYCALLBACK)(
//    DWORD dwCode,           // @parm notification code
//    const void* lpData,     // @parm data associated with the notification
//    DWORD cbData,           // @parm size of the strcuture pointed to lpData
//    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
//);
// 
//typedef HRESULT (*RIL_InitializeT)(
//    DWORD dwIndex,                      // @parm index of the RIL port to use (e.g., 1 for RIL1:)
//    RILRESULTCALLBACK pfnResult,        // @parm function result callback
//    RILNOTIFYCALLBACK pfnNotify,        // @parm notification callback
//    DWORD dwNotificationClasses,        // @parm classes of notifications to be enabled for this client
//    DWORD dwParam,                      // @parm custom parameter passed to result and notififcation callbacks
//    HRIL* lphRil                        // @parm returned handle to RIL instance
//);
//
//typedef HRESULT (*RIL_DeinitializeT)(
//    HRIL hRil                           // @parm handle to an RIL instance returned by <f RIL_Initialize>
//);
//
//typedef HRESULT (*RIL_GetCellTowerInfoT)(
//    HRIL hRil                           // @parm handle to RIL instance returned by <f RIL_Initialize>
//);
// 
//typedef struct rilcelltowerinfo_tag {
//    DWORD cbSize;                       // @field structure size in bytes
//    DWORD dwParams;                     // @field indicates valid parameters
//    DWORD dwMobileCountryCode;          // @field TBD
//    DWORD dwMobileNetworkCode;          // @field TBD
//    DWORD dwLocationAreaCode;           // @field TBD
//    DWORD dwCellID;                     // @field TBD
//    DWORD dwBaseStationID;              // @field TBD
//    DWORD dwBroadcastControlChannel;    // @field TBD
//    DWORD dwRxLevel;                    // @field Value from 0-63 (see GSM 05.08, 8.1.4)
//    DWORD dwRxLevelFull;                // @field Value from 0-63 (see GSM 05.08, 8.1.4)
//    DWORD dwRxLevelSub;                 // @field Value from 0-63 (see GSM 05.08, 8.1.4)
//    DWORD dwRxQuality;                  // @field Value from 0-7  (see GSM 05.08, 8.2.4)
//    DWORD dwRxQualityFull;              // @field Value from 0-7  (see GSM 05.08, 8.2.4)
//    DWORD dwRxQualitySub;               // @field Value from 0-7  (see GSM 05.08, 8.2.4)
//    DWORD dwIdleTimeSlot;               // @field TBD
//    DWORD dwTimingAdvance;              // @field TBD
//    DWORD dwGPRSCellID;                 // @field TBD
//    DWORD dwGPRSBaseStationID;          // @field TBD
//    DWORD dwNumBCCH;                    // @field TBD
//    BYTE rgbBCCH[MAXLENGTH_BCCH];       // @field TBD
//    BYTE rgbNMR[MAXLENGTH_NMR];         // @field TBD
//} RILCELLTOWERINFO, *LPRILCELLTOWERINFO;
//
//static void CALLBACK GetTower(DWORD dwCode, HRESULT hrCmdID, const void* lpData, DWORD cbData, DWORD dwParam)
//{
//   ((GSMHelper::ProcData*)dwParam)->cdata.areaCode = ((RILCELLTOWERINFO*)lpData)->dwLocationAreaCode;
//   ((GSMHelper::ProcData*)dwParam)->cdata.baseStation = ((RILCELLTOWERINFO*)lpData)->dwCellID / 10;
//   ((GSMHelper::ProcData*)dwParam)->cdata.cellNo = ((RILCELLTOWERINFO*)lpData)->dwCellID % 10;
//   ((GSMHelper::ProcData*)dwParam)->cdata.timeAdvance = ((RILCELLTOWERINFO*)lpData)->dwTimingAdvance;
//
//   SetEvent(((GSMHelper::ProcData*)dwParam)->evDone);
//}
//
//static bool IsEqualData(const GSMData &v1, const GSMData &v2)
//{
//   return (v1.baseStation == v2.baseStation && v1.cellNo == v2.cellNo && 
//      v1.areaCode == v2.areaCode && v1.timeAdvance == v2.timeAdvance);
//}
//
//DWORD GSMHelper::Do(GSMHelper::ProcData *data)
//{
//   return 0;
//
//   HINSTANCE lib = LoadLibrary(L"ril.dll");
//   if( lib == NULL )
//      return 0;
//
//   RIL_InitializeT init = (RIL_InitializeT)GetProcAddress(lib, L"RIL_Initialize");
//   RIL_DeinitializeT deinit = (RIL_DeinitializeT)GetProcAddress(lib, L"RIL_Deinitialize");
//   RIL_GetCellTowerInfoT cti = (RIL_DeinitializeT)GetProcAddress(lib, L"RIL_GetCellTowerInfo");
//
//   if( init == NULL || deinit == NULL || cti == NULL )
//   {
//      FreeLibrary(lib);
//      return 0;
//   }
//
//
//   HRIL hRil = 0;
//   HRESULT res = init(1, GetTower, NULL, 0, (DWORD)data, &hRil);
//   if( res != S_OK )
//   {
//      if( hRil ) deinit(hRil);
//      FreeLibrary(lib);
//      return 0;
//   }
//
//   data->data.areaCode = 0;
//   data->data.cellNo = 0;
//   data->data.areaCode = 0;
//   data->data.timeAdvance = 0;
//
//   DWORD wait = (DWORD)data->fireInterval * 1000;
//   if( wait == 0 )
//      wait = 10000;
//
//   data->evDone = CreateEvent(NULL, TRUE, FALSE, NULL);
//
//   HANDLE events[2];
//   events[0] = data->evDone;
//   events[1] = data->stopEvent;
//
//   do
//   {
//      cti(hRil);
//      DWORD res = WaitForMultipleObjects(2, events, FALSE, INFINITE);
//      if( res != WAIT_OBJECT_0 ) break;
//
//      //WaitForSingleObject(data->evDone, INFINITE);
//
//      if( (data->fireInterval != 0 || IsEqualData(data->data, data->cdata)) )
//      {
//         SYSTEMTIME st;
//         GetLocalTime(&st);
//
//         SystemTimeToFileTime(&st, &data->cdata.date);
//         data->data = data->cdata;
//
//         GPSUnitData udata;
//         udata = data->data;
//
//         handlers.DataChanged(udata, NULL);
//      }
//    } while( WaitForSingleObject(data->stopEvent, wait) == WAIT_TIMEOUT );
//
//   deinit(hRil);
//   CloseHandle(data->evDone);
//
//   FreeLibrary(lib);
//   return 0;
//}

//
// -------------------------- Handlers -----------------------
//
Handlers::Handlers()
{
   mutex = CreateMutex(NULL, FALSE, NULL);
}

Handlers::~Handlers()
{
   CloseHandle(mutex);
}

void Handlers::AddHandler(GPSUnit::Handler *h)
{
   if( WaitForSingleObject(mutex, 100) == WAIT_OBJECT_0 )
   {
      const_iterator fnd = find(begin(), end(), h);
      if( fnd == end() )
         push_back(h);
      
      ReleaseMutex(mutex);
   }
}

void Handlers::RemoveHandler(GPSUnit::Handler *h)
{
   if( WaitForSingleObject(mutex, 100) == WAIT_OBJECT_0 )
   {
      const_iterator fnd = find(begin(), end(), h);
      if( fnd != end() )
         erase(fnd);
      
      ReleaseMutex(mutex);
   }
}

void Handlers::DataChanged(const GPSUnitData &newData, GPSUnit *unit)
{
   if( WaitForSingleObject(mutex, 100) == WAIT_OBJECT_0 )
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         (*i)->DataChanged(newData, unit);
      ReleaseMutex(mutex);
   }
}

void Handlers::StateChanged(GPSUnit::EState state, GPSUnit *unit)
{
   if( WaitForSingleObject(mutex, 100) == WAIT_OBJECT_0 )
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         (*i)->StateChanged(state, unit);
      ReleaseMutex(mutex);
   }
}
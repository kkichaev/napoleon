/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Модуль GSM координат
 * 
 *  ert   30/04/2011   creating
 */ 

#include "stdafx.h"
#include <vector>

#define DEFINE_EXPORT
#include "AppsModule.h"
#include <Pm.h>
//#include <Pmpolicy.h>

#include <gpsapi.h>
HANDLE hGps;
HMODULE hGpsLib;
FILETIME lastGPSTime;

typedef HANDLE (*TGPSOpenDevice)(HANDLE hNewLocationData, HANDLE hDeviceStateChange, const WCHAR *szDeviceName, DWORD dwFlags);
typedef DWORD  (*TGPSCloseDevice)(HANDLE hGPSDevice);
typedef DWORD  (*TGPSGetPosition)(HANDLE hGPSDevice, GPS_POSITION *pGPSPosition, DWORD dwMaximumAge, DWORD dwFlags);

inline BYTE ToHex(char sym) { return (sym >= '0' && sym <= '9') ? sym - '0' : (toupper(sym) - 'A') + 10; }

GPSModule::GPSModule()
{
}

void GPSModule::SetComPort(WORD port)
{
   if( gpsPort.IsOpened() )
      gpsPort.Close();

   gpsPort.SetPort(port);
}

bool GPSModule::Init()
{
   if( state == stInit )
      return false;

   if( hGpsLib == NULL )
   {
      hGpsLib = LoadLibrary(L"GPSAPI.dll");
      if( hGpsLib )
      {
         TGPSOpenDevice GOD = (TGPSOpenDevice)GetProcAddress(hGpsLib, L"GPSOpenDevice");
         hGps = GOD(NULL, NULL, NULL, NULL);
         if( hGps )
         {
            state = stWork;
            Log("GPS Driver init");
            return true;
         }
      }
   }

   if( (short)gpsPort.GetPort() < 0 )
   {
      Log("Port not assigned");
      return false;
   }

   state = stInit;

   std::string val;
   if( gpsPort.ReadSentence(&val) )
   {
      Log("GPS port inited on COM%d", (int)gpsPort.GetPort());
      state = stWork;
      return true;
   } else
   {
      Log("Can't read data from COM%d", (int)gpsPort.GetPort());
      state = stFail;
      return false;
   }
}

// если модуль долго не выдает координат, то он останавливается
void GPSModule::Stop()
{
   if( hGps )
   {
      TGPSCloseDevice GCD = (TGPSCloseDevice)GetProcAddress(hGpsLib, L"GPSCloseDevice");
      GCD(hGps);
      hGps = NULL;
      FreeLibrary(hGpsLib);
      hGpsLib = NULL;
   } else
      gpsPort.Close();
   state = stOff;
}

#ifndef POWER_STATE_BACKLIGHTON
#define POWER_STATE_BACKLIGHTON  (DWORD)(0x02000000)        // device scree backlight on
#endif

// выдает только location время и флаг ставится в другом месте
bool GPSModule::Do(Location *location)
{
   if( state == stInit )
      return false;
   
   if( state != stWork )
   {
      if( !Init() )
         return false;
   }

   if( hGps )
   {
      GPS_POSITION pos = {0};
      pos.dwVersion = GPS_VERSION_1;
      pos.dwSize = sizeof(pos);

      bool ret = false;
      TGPSGetPosition GGP = (TGPSGetPosition)GetProcAddress(hGpsLib, L"GPSGetPosition");
      if( GGP(hGps, &pos, 5 * 60 * 1000, 0) == ERROR_SUCCESS )
      {
         if( (pos.dwValidFields & (GPS_VALID_UTC_TIME|GPS_VALID_LATITUDE|GPS_VALID_LONGITUDE|GPS_VALID_SPEED)) == 
            (GPS_VALID_UTC_TIME|GPS_VALID_LATITUDE|GPS_VALID_LONGITUDE|GPS_VALID_SPEED) )
         {
            SystemTimeToFileTime(&pos.stUTCTime, &location->date);
            if( *(__int64*)&location->date != *(__int64*)&lastGPSTime )
            {
               location->latitude = (int)(pos.dblLatitude * GPS_SCALE);
               location->longitude = (int)(pos.dblLongitude * GPS_SCALE);
               location->speed = (int)(pos.flSpeed * GPS_SPEED_SCALE);

               lastGPSTime = location->date;
               Log("GPS got at(%02d:%02d:%02d)", pos.stUTCTime.wHour, pos.stUTCTime.wMinute, pos.stUTCTime.wSecond);
               ret = true;
            }
         }
      }

      if( !ret )
         Log("GPS fail");
      return ret;
   }

   DWORD doSeconds = 10 * 1000, starttick = GetTickCount();
   bool readed = false;
   std::string logStr;

   // remove all data from ports
   gpsPort.Purge();
   //Log("Read gps...");

   wchar_t buf[50];
   DWORD flags;
   GetSystemPowerState(buf, sizeof(buf)/sizeof(buf[0]), &flags);
   Log("Power state %X", flags);
   if( (flags & POWER_STATE_BACKLIGHTON) == 0 || (flags & POWER_STATE_UNATTENDED) != 0 )
   {
      Log("Reset timer");
      SystemIdleTimerReset();
   }

   do
   {
      std::string val;
      if( gpsPort.ReadSentence(&val) )
      {
         const char *str = val.c_str();
         if( (strncmp(str, "GPRMC", sizeof("GPRMC")-1) == 0) )
         {
            if( ParseData(location, str + sizeof("GPRMC")) )
            {
               ////lastParseTick = GetTickCount();
               //Log("%s", str);

               logStr = str;
               location->isGPS = true;
               readed = true;
               //break;
            } else
            {
               if( !readed )
                  logStr = str;
               //if( logStr.compare(val) )
               //{
               //   logStr = val;
               //   Log("Unparse GPS %s", str);
               //}
            }
         }
      }
   } while( (GetTickCount() - starttick) < doSeconds );

   if( readed ) Log("%s", logStr.c_str());
   else Log("Unparse GPS %s", logStr.c_str());

   return readed;
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

bool GPSModule::ParseData(Location* location, const char *str)
{
   SYSTEMTIME st;
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
   location->latitude = ival;

   // longitude
   CopyToken(buf, str, &str);
   ival = ValueToDeg(buf);
   if( ival < 0 ) return false;
   // East / West
   CopyToken(buf, str, &str);
   while( *buf == ' ' ) buf++;
   if( *buf == 'W' ) ival = -ival;
   location->longitude = ival;

   // speed
   CopyToken(buf, str, &str);
   location->speed = (WORD)(atof(buf) * 1609 / 10);

   // course -- skip
   CopyToken(buf, str, &str);

#if defined(Test) && defined (DEBUG)
   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &location->date);
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
   FileTimeToLocalFileTime(&ft, &location->date);
#endif
   return true;
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

   //if( data->isGps )
   //{
   //   Log("Read %s while find port", val.c_str());
   //}

   return 0;
}

#include <devload.h>
#include <set>
static void FindBTPorts(std::set<int> *ports)
{
   HKEY hKey;
   RegOpenKeyEx(HKEY_LOCAL_MACHINE, DEVLOAD_BUILT_IN_KEY, 0, 0, &hKey);

   wchar_t name[100];
   DWORD cb = sizeof(name)/sizeof(name[0]);
   DWORD index = 0;
   while( RegEnumKeyEx(hKey, index, name, &cb, 0, NULL, NULL, NULL) == ERROR_SUCCESS )
   {
      HKEY ckey;
      if( RegOpenKeyEx(hKey, name, 0, 0, &ckey) == ERROR_SUCCESS)
      {
         cb = sizeof(name) / sizeof(name[0]);
         if( RegQueryValueEx(ckey, DEVLOAD_PREFIX_VALNAME, 0, NULL, (LPBYTE)name, &cb) == ERROR_SUCCESS )
         {
            if( wcsncmp(name, L"COM", 3) == 0 )
            {
               cb = sizeof(name) / sizeof(name[0]);
               if( RegQueryValueEx(ckey, L"FriendlyName", 0, NULL, (LPBYTE)name, &cb) == ERROR_SUCCESS )
               {
                  if( wcsnicmp(name, L"Bluetooth", sizeof(L"Bluetooth") / sizeof(wchar_t) - 1) == 0 )
                  {
                     DWORD value;
                     cb = sizeof(value);
                     if( RegQueryValueEx(ckey, L"Index", 0, NULL, (LPBYTE)&value, &cb) == ERROR_SUCCESS )
                     {
                        ports->insert(value);
                        Log("Find BT port COM%d", value);
                     }
                  }
               }
            }
         }
         
         RegCloseKey(ckey);
      }
      index++;
      cb = sizeof(name)/sizeof(name[0]);
   }

   RegCloseKey(hKey);
}

short GPSModule::FindGPSPort()
{
   std::set<int> btPorts;
   int i;
   FindBTPorts(&btPorts);
   for( i=0; i<10; i++ )
   {
      if( btPorts.find(i) != btPorts.end() )
         continue;

      GPSComPort port;
      port.SetPort(i);
      if( port.Open() )
      {
         FindData param;
         param.port = &port;
         param.isGps = false;

         HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryReadPort, &param, 0, NULL);
         if( WaitForSingleObject(thread, 5000) != WAIT_OBJECT_0 )
            TerminateThread(thread, 0);
         port.Close();
         CloseHandle(thread);

         if( param.isGps )
            break;
      }
   }

   return (i < 10) ? i : -1;
}

//
//---------------------------- GPSComPort --------------------------
//
GPSComPort::GPSComPort(DWORD baudRate) : handle(INVALID_HANDLE_VALUE), hPowerReq(INVALID_HANDLE_VALUE)
{
   this->port = (WORD)-1;
   this->baudRate = baudRate;
}

GPSComPort::~GPSComPort()
{
   Close(); 
}

bool GPSComPort::Open()
{
   wchar_t buf[20];
   wsprintf(buf, L"COM%d:", port);
   handle = CreateFile(buf, GENERIC_READ, 0, NULL, OPEN_EXISTING, 0, NULL);

   if( handle == INVALID_HANDLE_VALUE )
   {
      Log("Open COM%d error %d", port, GetLastError());
      return false;
   }

   SetupComm(handle, 4096, 4096 ) ;

   DCB dcb = { 0 };
   dcb.DCBlength = sizeof(dcb);

   dcb.BaudRate = baudRate;
   dcb.ByteSize = 8;
   dcb.StopBits = 2;
   dcb.XonLim = 2048;
   dcb.XoffLim = 512;
   dcb.fBinary = 1;
   SetCommState(handle, &dcb);

   COMMTIMEOUTS ct = {0};
   ct.ReadIntervalTimeout = baudRate/100;
   ct.ReadTotalTimeoutMultiplier = 0;
   ct.ReadTotalTimeoutConstant = 0;
   SetCommTimeouts(handle, &ct);

   hPowerReq = SetPowerRequirement(buf, D0, POWER_NAME|POWER_FORCE, NULL, NULL);
   return true;
}

void GPSComPort::Purge()
{
   if( handle && handle != INVALID_HANDLE_VALUE )
      PurgeComm(handle, PURGE_RXABORT | PURGE_TXABORT);
}

void GPSComPort::Close()
{
   if( handle && handle != INVALID_HANDLE_VALUE )
   {
      PurgeComm(handle, PURGE_RXABORT | PURGE_TXABORT);
      CloseHandle(handle);
   }
   handle = INVALID_HANDLE_VALUE;

   if( hPowerReq && hPowerReq != INVALID_HANDLE_VALUE )
   {
      ReleasePowerRequirement(hPowerReq);
      CloseHandle(hPowerReq);
   }
   hPowerReq = INVALID_HANDLE_VALUE;
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

bool GPSComPort::ReadTill(char endSym, std::string *val)
{
   if( handle == INVALID_HANDLE_VALUE ) return false;

   DWORD cb;
   char sym = 0;
   bool retVal = false;

   //std::string dbgStr;
   //if( val == NULL ) val = &dbgStr;

   DWORD readWait = GetTickCount();
   do
   {
      if( ReadFile(handle, &sym, sizeof(sym), &cb, NULL) == FALSE || cb == 0 )
      {
         if( (GetTickCount() - readWait) > 1000 )
            break;
         continue;
      }

      readWait = GetTickCount();
      if( sym == endSym )
      {
         retVal = true;
         break;
      }
      if( val ) val->append(1, sym);
   } while( true );

   //if( val->empty() ) Log("Can't read port");
   //else Log( "reading %s", val->c_str());

   return retVal;
}

BYTE GPSComPort::CRC(const std::string &str)
{
   BYTE crc = 0;
   std::string::const_iterator i = str.begin();
   for( ; i != str.end(); i++ )
      crc ^= (*i);

   return crc;
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Модуль приложения + globals
 * 
 *  ert   08/08/2007   creating
 *  ert   13/08/2007   modifing
 */ 
#include "stdafx.h"

#include <Exchange.h>
#include <Reflector.h>

#include <Module.h>
#include <Compress.h>

#include <StringHolder.h>
#include <Network.h>
#include "Progress.h"
#include "NplConfig.h"
#include <DocType.h>
#include "PrfDlg.h"

#ifdef NAPOLEON_APPS

//#include <notify.h>
#include <Apps.h>
//#include <GPSUnit.h>
#include <ObjImpl.h>

const wchar_t* TRACKING = L"Tracking";

typedef void (*GetStatesT)(ModuleStates* states);;
typedef bool (*TimeGetLocalT)(FILETIME *ft);
typedef void (*InitT)(AppsConfig* config);
typedef void (*NoParamT)();

HINSTANCE hApps;
GetStatesT GetAppsState;

HINSTANCE NapoleonApp::AppsIntance() const { return hApps; }

void Log(const char* msg, ... );

#endif

void NapoleonApp::DeleteFile(const char *fileName)
{
   int len = strlen(fileName) + 1;
   wchar_t *buf = (wchar_t*)malloc(len * sizeof(wchar_t));
   mbstowcs(buf, fileName, len);

   std::wstring fn;
   _Module.MakeFileName(&fn, buf ); 
   ::DeleteFile(fn.c_str());

   free(buf);
}

void NapoleonApp::MakeFileName(std::string *fullName, const char *fileName)
{
   if( *fileName == '\\' )
   {
      *fullName = fileName;
      return;
   }
   wchar_t buf[MAX_PATH];
   GetModuleFileName(_Module.GetModuleInstance(), buf, sizeof(buf));

   wchar_t *p = wcsrchr(buf, L'\\');
   if( p ) p++;
   else p = buf;
   *p = L'\0';

   char abuf[MAX_PATH];
   wcstombs(abuf, buf, MAX_PATH);
   (*fullName) = abuf;
   (*fullName) += fileName;
}

void NapoleonApp::MakeFileName(std::wstring *fullName, const wchar_t *fileName)
{
   if( *fileName == L'\\' )
   {
      *fullName = fileName;
      return;
   }
   wchar_t buf[MAX_PATH];
   GetModuleFileName(_Module.GetModuleInstance(), buf, sizeof(buf));

   wchar_t *p = wcsrchr(buf, L'\\');
   if( p ) p++;
   else p = buf;
   *p = L'\0';

   (*fullName) = buf;
   (*fullName) += fileName;
}

NapoleonApp::NapoleonApp() : /*preferenceLoaded(false), */frame(NULL), createOrder(false), preferenceHandler(NULL)
{
   SetStartTick();

#ifdef RCV_MESSAGE
   msgDate.dwHighDateTime = 0;
   msgDate.dwLowDateTime = 0;
#endif
}

NapoleonApp::~NapoleonApp()
{
}

void NapoleonApp::WaitThreadComplete(HANDLE thread)
{
   if( thread != INVALID_HANDLE_VALUE )
   {
      CMessageLoop *ml = GetMessageLoop();
      while( true )
      {
         DWORD res = WaitForSingleObject(thread, 0);
         if( res == WAIT_OBJECT_0 || res == WAIT_FAILED )
            break;

         MSG msg;
         if( ::PeekMessage(&msg, NULL, 0, 0, PM_REMOVE) == FALSE )
            continue;
         
         if( msg.message == WM_QUIT )
            break;

         if( ml && ml->PreTranslateMessage(&msg) )
            continue;

         ::TranslateMessage(&msg);
         ::DispatchMessage(&msg);
      }
      CloseHandle(thread);
   }
}

void NapoleonApp::GetLocalTime(FILETIME* ft)
{
#ifdef NAPOLEON_APPS
   bool done = false;
   if( hApps )
   {
      TimeGetLocalT TimeGetLocal = (TimeGetLocalT)GetProcAddress(hApps, L"GetCurTime");
      if( TimeGetLocal )
         done = TimeGetLocal(ft);
   }
   if( !done )
   {
      SYSTEMTIME st;
      ::GetLocalTime(&st);
      st.wMilliseconds = 0;
      SystemTimeToFileTime(&st, ft);
   }
#else
   SYSTEMTIME st;
   ::GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, ft);
#endif
}


#ifdef NAPOLEON_APPS

//#if defined(Test) && defined (DEBUG)
//static const __int64 GPS_INTERVAL = (__int64)100000000;
//#elif TKSibir
//static const __int64 GPS_INTERVAL = (__int64)300000000;
//#else
//static const __int64 GPS_INTERVAL = (__int64)600000000;
//#endif

NapoleonApp::Tracking NapoleonApp::GetTracking()
{
   Tracking ret = trkNone;

   ConfigImpl cfg;
   cfg.key = (wchar_t*)TRACKING;
   if( cfg.Read() )
   {
      if( wcscmp(cfg.value, L"GSM") == 0 ) ret = trkGSM;
      else if( wcscmp(cfg.value, L"GPSpoint") == 0 ) ret = trkGPSpoint;
      else if( wcscmp(cfg.value, L"GPSroute") == 0 ) ret = trkGPSroute ;
   }

   char buf[100];
   int len = wcstombs(buf, cfg.value, sizeof(buf));
   buf[len] = 0;
   Log("Track mode %s", buf);

   return ret;
}

bool NapoleonApp::GSMTracking() const
{
   if( GetAppsState )
   {
      ModuleStates states;
      GetAppsState(&states);
      return (states.gsm == Module::stWork);
   }
   return false;
}

bool NapoleonApp::GPSTracking() const
{
   if( GetAppsState )
   {
      ModuleStates states;
      GetAppsState(&states);
      return (states.gps == Module::stWork);
   }
   return false;
}

static void DoInitApps()
{
   if( hApps == NULL )
      return;

   AppsConfig cfg;
   InitT Init = (InitT)GetProcAddress(hApps, L"Init");

   GetModuleFileName(_Module.GetModuleInstance(), cfg.progName, MAX_PATH);
   Preference p;

   NapoleonApp::Tracking t = _Module.GetTracking();
   p.Load();
   TIME_ZONE_INFORMATION tzi;

   cfg.runTimer = true;
   cfg.runGPS = (t == NapoleonApp::trkGPSpoint || t == NapoleonApp::trkGPSroute);
   cfg.runGSM = (t == NapoleonApp::trkGSM || cfg.runGPS);
   cfg.gpsPort = (int)p.gpsPort - 1;
   cfg.gpsAccuracy = p.gpsAccuracy;
   cfg.doInterval = p.gpsInterval;

   cfg.timeShift = 0;
   DWORD res = GetTimeZoneInformation(&tzi);
   if( res != TIME_ZONE_ID_UNKNOWN )
   {
      cfg.timeShift = tzi.Bias;
      if( res == TIME_ZONE_ID_DAYLIGHT )
         cfg.timeShift += tzi.DaylightBias;
   }

   Init(&cfg);
}

void NapoleonApp::StartApps()
{
   //MessageBox(NULL, L"!", L"!", MB_OK);
   hApps = LoadLibrary(APPS_DLL_NAME);
   if( hApps )
   {
      GetAppsState = (GetStatesT)GetProcAddress(hApps, L"GetStates");
      DoInitApps();
   }
}

void NapoleonApp::StopApps()
{
   if( !hApps )
      return;
   NoParamT Stop = (NoParamT)GetProcAddress(hApps, L"Stop");

   Stop();

   FreeLibrary(hApps);
   hApps = NULL;
}

void NapoleonApp::UpdateApps()
{
   DoInitApps();
}

void NapoleonApp::DoCheckTime()
{
   if( !hApps )
      return;

   NoParamT _DoCheckTime = (NoParamT)GetProcAddress(hApps, L"CheckTime");
   _DoCheckTime();
}

//static DWORD UpdateData(void*)
//{
//   //Log("Update Data start");
//   bool runAgain = false;
//   if( useTracking && !useGSMTracker )
//   {
//      if( !updatingApps )
//         GPSUnit::DoReadPort(20);
//      runAgain = true;
//   }
//
//   if( hApps )
//   {
//      if( useTracking && useGSMTracker )
//      {
//         TRefreshGPS RefreshGPS = (TRefreshGPS)GetProcAddress(hApps, L"RefreshGPS");
//         if( RefreshGPS() )
//         {
//            if( !updatingApps )
//                  GPSUnit::DoReadPort(0);
//         }
//      }
//
//      TimeProcT TimeStep = (TimeProcT)GetProcAddress(hApps, L"TimeStep");
//      TimeStep();
//      runAgain = true;
//   }
//
//   if( runAgain )
//   {
//      SYSTEMTIME st;
//      FILETIME ft;
//
//      TimeGetLocalT TimeGetLocal = (TimeGetLocalT)GetProcAddress(hApps, L"TimeGetLocal");
//      if( TimeGetLocal )
//         TimeGetLocal(&ft);
//      else
//      {
//         GetLocalTime(&st);
//         SystemTimeToFileTime(&st, &ft);
//      }
//      Preference p;
//      p.Load();
//      int interval = p.gpsInterval;
//      if( interval < 10 ) interval = 10;
//      if( interval > 60 ) interval = 60;
//      *(__int64*)&ft += (__int64)interval * (__int64)10000000;
//      FileTimeToSystemTime(&ft, &st);
//
//      wchar_t buf[MAX_PATH];
//      GetModuleFileName(_Module.GetModuleInstance(), buf, MAX_PATH);
//      CeRunAppAtTime(buf, NULL);
//      CeRunAppAtTime(buf, &st);
//   }
//
//   CloseHandle(hUpdateThread);
//   hUpdateThread = NULL;
//   //Log("Update data Done");
//   return 0;
//}

#include <notify.h>
void NapoleonApp::DoApps(const wchar_t* cmd)
{
   if( !hApps )
      return;

   if( wcscmp(cmd, APP_RUN_AFTER_TIME_CHANGE) == 0 )
   {
      NoParamT SysTimeChanged = (NoParamT)GetProcAddress(hApps, L"SysTimeChanged");
      SysTimeChanged();
   } else
   {
      NoParamT Do = (NoParamT)GetProcAddress(hApps, L"Do");
      Do();
   }
}

static bool logStarted = false;
void Log(const char* msg, ... )
{
   wchar_t name[MAX_PATH];
   GetModuleFileName(0, name, sizeof(name)/sizeof(name[0]));
   wcscat(name, L".txt");

   if( !logStarted )
   {
      DeleteFile(name);
      logStarted = true;
   }

   va_list args;
   va_start(args, msg);

   FILE *file = _wfopen(name, L"at");
   if( file )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      fprintf(file, "%02d:%02d:%02d ", st.wHour, st.wMinute, st.wSecond);

      vfprintf(file, msg, args);
      fputs("\n", file);

      fclose(file);
   }
}
#endif
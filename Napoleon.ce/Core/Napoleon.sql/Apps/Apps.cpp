/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Napoleon Apps entry points
 * 
 *  ert   10/09/2010   creating
 */ 

#include "stdafx.h"
#include <vector>

#include <Pmpolicy.h>
#include <notify.h>
#define DEFINE_EXPORT
#include "AppsModule.h"

HANDLE hInstance;

const int MAX_THREAD_WAIT = 60 * 1000;
const int WAIT_BEFORE_DO = 10 * 1000;

static TimeModule timeModule;
static GPSModule gpsModule;
static GSMModule gsmModule;

static Location currentLocation;

static AppsConfig config;

static DWORD lastGPSTick, lastGSMTick;
const DWORD MAX_GPS_TIMEOUT = 5 * 60 * 1000;

static std::vector<StateCallback> stateCallback;
static std::vector<LocationCallback> locationCallback;

static HANDLE hInitThread, hDoThread;
static DWORD InternalDo(void*);
static DWORD InternalInit(void*);

static bool unattendedRun = false;

// coord - GPS_SCALE
// return in meters
int Distance(int latitude1, int longitude1, int latitude2, int longitude2);

inline bool IsThreadRun(HANDLE thread) { return (thread != NULL && thread != INVALID_HANDLE_VALUE ); }

static std::vector<StateCallback>::iterator Find(StateCallback scbk)
{
   std::vector<StateCallback>::iterator i = stateCallback.begin();
   for( ; i != stateCallback.end(); i++ )
      if( (*i) == scbk )
         break;

   return i;
}

static std::vector<LocationCallback>::iterator Find(LocationCallback lcbk)
{
   std::vector<LocationCallback>::iterator i = locationCallback.begin();
   for( ; i != locationCallback.end(); i++ )
      if( (*i) == lcbk )
         break;

   return i;
}

static void StateChanged(const ModuleStates* newState)
{
   std::vector<StateCallback>::iterator i = stateCallback.begin();
   for( ; i != stateCallback.end(); i++ )
      (*i)(newState);
}

static void LocationChanged(const Location* location)
{
   std::vector<LocationCallback>::iterator i = locationCallback.begin();
   for( ; i != locationCallback.end(); i++ )
      (*i)(location);
}

static void StopThread(HANDLE* thread, DWORD waitTime)
{
   if( IsThreadRun(*thread) )
   {
      if( WaitForSingleObject(*thread, waitTime) != WAIT_OBJECT_0 )
         TerminateThread(*thread, 0);

      CloseHandle(*thread);
      (*thread) = NULL;
   }
}

static void StopModules()
{
   timeModule.Stop();
   gsmModule.Stop();
   gpsModule.Stop();
}

void GetConfig(AppsConfig* cfg)
{
   *cfg = config;
}

static DWORD InternalInit(void*)
{
   if( *config.progName )
      CeRunAppAtTime(config.progName, NULL);

   if( !unattendedRun )
   {
      unattendedRun = (PowerPolicyNotify(PPN_UNATTENDEDMODE, TRUE) != FALSE);
      //Log( "Do unattanded %d", (int)unattendedRun);
   }

   // если мы что-то выполняли - дождемся окончания действий
   StopThread(&hDoThread, MAX_THREAD_WAIT);
   StopModules();

   //MessageBox(NULL, L"!", L"!", MB_OK);

   // реализуем простой вариант
   if( config.runTimer )
   {
      //Log("Init timer");
      timeModule.SetTimeShift(config.timeShift);
      timeModule.Init();
      Log("Timer state %d", (int)timeModule.GetState());
   }

   if( config.runGSM || config.runGPS )
   {
      //Log("Init gsm");
      gsmModule.Init();
      Log("GSM state %d", (int)gsmModule.GetState());
   }
   if( config.runGPS && config.gpsPort >= 0 )
   {
      //Log("Init gps COM%d", config.gpsPort);
      gpsModule.SetComPort(config.gpsPort);
      gpsModule.Init();
      Log("GPS state %d", (int)gpsModule.GetState());
   }

   if( *config.progName )
   {
      CeRunAppAtEvent(config.progName, NOTIFICATION_EVENT_NONE);
      if( timeModule.GetState() == Module::stWork )
         CeRunAppAtEvent(config.progName, NOTIFICATION_EVENT_TIME_CHANGE);
   }

   ModuleStates states;
   GetStates(&states);
   StateChanged(&states);

   CloseHandle(hInitThread);
   hInitThread = NULL;

   if( hDoThread == NULL )
      hDoThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)InternalDo, NULL, 0, NULL);

   return 0;
}

static void MakeRunAgain()
{
   if( *config.progName && (config.runGPS || config.runGSM)  )
   {
      SYSTEMTIME st;
      FILETIME ft;

      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft);
      *(__int64*)&ft += (__int64)config.doInterval * (__int64)10000000;
      FileTimeToSystemTime(&ft, &st);

      CeRunAppAtTime(config.progName, NULL);
      CeRunAppAtTime(config.progName, &st);

      Log("Make next run at %02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);
   } else
      Log("No run again");
}

static DWORD InternalDo(void*)
{
   ModuleStates states;
   GetStates(&states);

   Log("Do");

   if( config.runTimer )
   {
      //Log("Timer...");
      timeModule.Do();
   }

   Location current;
   bool positionCatched = false;
   bool getGSM = config.runGSM;
   if( config.runGPS )
   {
      //Log( "GPS..." );
      positionCatched = gpsModule.Do(&current);
      if( positionCatched )
      {
         lastGPSTick = GetTickCount();
         getGSM = false;
      } else
      {
         if( lastGPSTick == 0 )
            getGSM = true;
         else
         {
            //Log("No GPS %d sec", (GetTickCount() - lastGPSTick) / 1000 );

            // если не пришли координаты GPS проверим таймаут и, если надо, переведем на GSM
            if( GetTickCount() - lastGPSTick > MAX_GPS_TIMEOUT )
            {
               if( !getGSM )
                  Log("GPS lost");

               getGSM = true;
            } else
            {
               getGSM = false;
            }
         }
      }
   }

   if( getGSM )
   {
      //Log( "GSM..." );
      positionCatched = gsmModule.Do(&current);
      if( positionCatched )
         lastGSMTick = GetTickCount();
   }

   ModuleStates states1;
   GetStates(&states1);
   if( states != states1 )
   {
      Log("State changes");

      states = states1;
      StateChanged(&states);
   }

   if( positionCatched )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &current.date);

      if( (Distance(current.latitude, current.longitude, currentLocation.latitude, currentLocation.longitude) > config.gpsAccuracy) )
      {
         currentLocation = current;
         LocationChanged(&currentLocation);
      } else      
         currentLocation.date = current.date;
   }

   MakeRunAgain();

   CloseHandle(hDoThread);
   hDoThread = NULL;
   return 0;
}

void Init(AppsConfig* cfg)
{
   if( IsThreadRun(hInitThread) )
      return;

   config = *cfg;
   hInitThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)InternalInit, NULL, 0, NULL);
}

void Do()
{
   if( IsThreadRun(hInitThread) )
      return;

   StopThread(&hDoThread, WAIT_BEFORE_DO);
   hDoThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)InternalDo, NULL, 0, NULL);
}

void Stop()
{
   if( *config.progName )
   {
      CeRunAppAtEvent(config.progName, NOTIFICATION_EVENT_NONE);
      CeRunAppAtTime(config.progName, NULL);
   }

   stateCallback.clear();
   locationCallback.clear();

   StopThread(&hInitThread, 0);
   StopThread(&hDoThread, 0);

   StopModules();

   if( unattendedRun )
   {
      PowerPolicyNotify(PPN_UNATTENDEDMODE, FALSE);
      //Log( "Unattanded done");
      unattendedRun = false;
   }
}

short FindGPSPort()
{
   return gpsModule.FindGPSPort();
}

bool GetLocation(Location* location)
{
   bool ret = true;

   if( gpsModule.GetState() == Module::stWork )
      *location = currentLocation;
   else if( gsmModule.GetState() == Module::stWork )
      *location = currentLocation;
   else
      ret = false;

   return ret;
}

void GetStates(ModuleStates* states)
{
   states->timer = timeModule.GetState();

   states->gps = gpsModule.GetState();
   if( states->gps == Module::stWork )
      states->gps = (lastGPSTick != 0 && GetTickCount() - lastGPSTick < MAX_GPS_TIMEOUT) ? Module::stWork : Module::stInit;

   states->gsm = gsmModule.GetState();
   if( states->gsm == Module::stWork )
      states->gsm = (lastGSMTick != 0 && GetTickCount() - lastGSMTick < MAX_GPS_TIMEOUT) ? Module::stWork : Module::stInit;
}

void AddCallback(StateCallback scbk, LocationCallback lcbk)
{
   if( scbk )
   {
      std::vector<StateCallback>::iterator f = Find(scbk);
      if( f == stateCallback.end() )
         stateCallback.push_back(scbk);
   }

   if( lcbk )
   {
      std::vector<LocationCallback>::iterator f = Find(lcbk);
      if( f == locationCallback.end() )
         locationCallback.push_back(lcbk);
   }
}

void RemoveCallback(StateCallback scbk, LocationCallback lcbk)
{
   if( scbk )
   {
      std::vector<StateCallback>::iterator f = Find(scbk);
      if( f != stateCallback.end() )
         stateCallback.erase(f);
   }

   if( lcbk )
   {
      std::vector<LocationCallback>::iterator f = Find(lcbk);
      if( f != locationCallback.end() )
         locationCallback.erase(f);
   }
}



static bool logStarted = false;
void Log(const char* msg, ... )
{
   if( hInstance == INVALID_HANDLE_VALUE || hInstance == 0 )
      return;

   wchar_t name[MAX_PATH], *p;
   GetModuleFileName((HMODULE)hInstance, name, sizeof(name)/sizeof(name[0]));
   p = wcsrchr(name, L'.');
   if( p == NULL ) wcscat(p, L".txt");
   else wcscpy(p, L".txt");

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

bool GetCurTime(FILETIME *ft)
{
   return timeModule.GetCurTime(ft);
}

void SysTimeChanged()
{
   timeModule.SysTimeChanged();
   MakeRunAgain();
}

void CheckTime()
{
   timeModule.CheckTime();
}

BOOL APIENTRY DllMain( HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved )
{
	switch ( ul_reason_for_call )
	{
	case DLL_PROCESS_ATTACH:
		break;
	case DLL_PROCESS_DETACH:
		break;
	case DLL_THREAD_ATTACH:
      hInstance = hModule;
		break;
	case DLL_THREAD_DETACH:
		break;
	}
	return TRUE;
}

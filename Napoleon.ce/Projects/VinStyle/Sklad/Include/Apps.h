/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Napoleon Apps entry points
 * 
 *  ert   10/09/2010   creating
 */ 

#ifndef _NPL_APPS_H
#define _NPL_APPS_H

#include <string>

#define APPS_DLL_NAME L"NapoleonApps.dll"

#ifdef DEFINE_EXPORT
   #define FUNC_SPEC __declspec(dllexport)
#else
   #define FUNC_SPEC __declspec(dllimport)
#endif

struct AppsConfig
{
   bool runTimer;
   bool runGPS;   // при включении GPS запускается и GSM
   bool runGSM;

   short gpsPort; // 

   WORD gpsAccuracy;    // in meters (1, 10, 100, 1000)
   WORD doInterval;     // in seconds

   int  timeShift; // timezone in minutes < 0 восточней UTC GetTimeZoneInformation()

   wchar_t progName[MAX_PATH]; // для запуска программы по таймеру
};

#ifndef GPS_SCALE_DEFINED
#define GPS_SCALE_DEFINED

const DWORD GPS_SCALE = 100000;
const DWORD GPS_SPEED_SCALE = 100;

#endif

struct Location
{
   int longitude;
   int latitude;

   WORD speed;

   FILETIME date;

   bool isGPS;
};

class Module
{
public:
   enum State { stOff, stInit, stWork, stFail };

   Module() : state(stOff)
   {
   }

   virtual bool Init() = 0;
   virtual void Stop() = 0;

   //
   // функции Do разные - не делаем виртуальных
   //

   State GetState() const { return state; }

protected:
   State state;
};

struct ModuleStates
{
   Module::State timer;
   Module::State gps;
   Module::State gsm;

   bool operator == (const ModuleStates& src) const
   {
      return (timer == src.timer && gps == src.gps && gsm == src.gsm);
   }

   bool operator != (const ModuleStates& src) const { return !(operator ==(src)); }
};


//
//------------------------------- exported functions -----------------------------
//
typedef void (*StateCallback)(const ModuleStates* newState);
typedef void (*LocationCallback)(const Location* location);

extern "C" FUNC_SPEC void GetConfig(AppsConfig* config);

// устанавливает новую конфигурацию и запускает модули
extern "C" FUNC_SPEC void Init(AppsConfig* config);

extern "C" FUNC_SPEC void Do();

extern "C" FUNC_SPEC void Stop();

extern "C" FUNC_SPEC void SysTimeChanged();

extern "C" FUNC_SPEC void CheckTime();

extern "C" FUNC_SPEC short FindGPSPort();

extern "C" FUNC_SPEC void GetStates(ModuleStates* states);

extern "C" FUNC_SPEC bool GetLocation(Location* location);

extern "C" FUNC_SPEC bool GetCurTime(FILETIME *ft);

extern "C" FUNC_SPEC void AddCallback(StateCallback scbk, LocationCallback lcbk);

extern "C" FUNC_SPEC void RemoveCallback(StateCallback scbk, LocationCallback lcbk);

#endif

/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Napoleon Apps entry points
 * 
 *  ert   27/04/2011   creating
 */ 
#ifndef __APPS_MODULE_H
#define __APPS_MODULE_H

#include "Apps.h"

class TimeModule : public Module
{
public:
   TimeModule();

   void SetTimeShift(int tz); // in minutes

   virtual bool Init();
   virtual void Stop();
   void Do();

   bool CheckTime();
   bool GetCurTime(FILETIME *ft);

   // сообщение об изменении системного времени
   void SysTimeChanged();

   void GetTime(SYSTEMTIME *st);

   typedef void (*STimeProc)(SYSTEMTIME* st);
   static STimeProc SysTimeProc;

protected:
   __int64 timeBias, timeChanges;

   FILETIME sysTime;
};

class LocationModule : public Module
{
public:
   virtual bool Do(Location *location) = 0;
};

class GPSComPort
{
public:
   GPSComPort(DWORD baudRate = 4096);
   ~GPSComPort();


   bool Open();
   bool ReadSentence(std::string *val);
   void Close();

   WORD GetPort() const { return port; }
   void SetPort(WORD port) { this->port = port; }
   bool IsOpened() const { return (handle != INVALID_HANDLE_VALUE); }

   void Purge();

protected:
   HANDLE handle, hPowerReq;
   WORD   port;
   DWORD  baudRate;

protected:
   bool ReadTill(char sym, std::string *val);
   BYTE CRC(const std::string &str);
};

class GPSModule : public LocationModule
{
public:
   GPSModule();

   void SetComPort(WORD port);

   virtual bool Init();
   virtual void Stop();

   // выдает только location время и флаг ставится в другом месте
   virtual bool Do(Location *location);

   short FindGPSPort();

protected:
   GPSComPort gpsPort;
   bool ParseData(Location* res, const char* str);
};

class GSMModule : public LocationModule
{
public:
   GSMModule();

   virtual bool Init();
   virtual void Stop();
   virtual bool Do(Location *location);

protected:
   HINSTANCE hLib;

   bool RILInit();
   void RILClose();

   bool GetMCC_MNC(DWORD *country, DWORD *op);
   bool GetLAC_CellID(DWORD *lac, DWORD *cell);
   bool GetLocation(int* lon, int* lat, DWORD mcc, DWORD mnc, DWORD lac, DWORD cell);
   void StopLocation();
};

void Log(const char* msg, ... );
extern HANDLE hInstance;

#endif
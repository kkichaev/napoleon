/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * GPS unit
 *
 *  ert   19/08/2009   creating
 */

#ifndef __GPS_UNIT_H
#define __GPS_UNIT_H

#include <vector>

#ifndef GPS_SCALE_DEFINED
#define GPS_SCALE_DEFINED

const DWORD GPS_SCALE = 100000;
const DWORD GPS_SPEED_SCALE = 100;

#endif

struct GPSData
{
   FILETIME date;

   int longitude; // -180 ... 0 West, 0 ... 180 East, 10^4 scale
   int latitude;  // -90 ... 0 North, 0 ... 90 South, 10^4 scale
   WORD  speed;   // km/h 10^2 scale
};

struct GSMData
{
   FILETIME date;

   DWORD baseStation;
   WORD cellNo;
   DWORD areaCode;
   DWORD timeAdvance;
}; 

struct GPSUnitData
{
   bool gpsData;

   union
   {
      GPSData gps;
      GSMData gsm;
   } value;

   GPSUnitData& operator= (const GPSData& data)
   {
      gpsData = true;
      value.gps = data;

      return *this;
   }

   GPSUnitData& operator= (const GSMData& data)
   {
      gpsData = false;
      value.gsm = data;

      return *this;
   }
};

class GPSComPort;

class GPSUnit
{
public:
   enum EState { NotOpened, Opening, Synced };

   struct Handler
   {
      virtual void DataChanged(const GPSUnitData &newData, GPSUnit *unit) = 0;
      virtual void StateChanged(EState newState, GPSUnit *unit) = 0;
   };

   static int FindGPSPort();

   static EState WaitData(GPSData* data, WORD portNo, DWORD milliseconds, HANDLE evStop);

   static void SetAccuracy(DWORD accuracy);

   static void AddHandler(Handler *handler);
   static void RemoveHandler(Handler *handler);

   static EState RunnedState() { return (runned != NULL) ? runned->state : NotOpened; }

   static bool InitGPS(WORD portNo);
   static void Stop();
   static bool Runned();

   static bool DoReadPort(DWORD doSeconds);

protected:
   enum Flags { UseGSM = 1, };

   DWORD lastDataTick, lastParseTick;
   //WORD fireInterval;
   GPSComPort *port;

   WORD flags;

   HANDLE evSyncedState;

   DWORD accuracy;
   EState  state;
   GPSUnitData data;

   static GPSUnit *runned;
   //static HANDLE hThread;
   //static HANDLE evStop;

protected:
   GPSUnit(WORD flags);
   ~GPSUnit();

   EState State() const { return state; }
   bool CopyCurData(GPSData *data) const;

   void SetData(WORD fireInterval);

   bool Init(WORD port);
   void ReadPort(DWORD doSeconds);
   bool ParseData(const char *str);
   void CheckData(const GPSData &cdata);

   void ChangeAccuracy(DWORD accuracy);

   void SetState(EState newState);

   EState WaitSync(WORD portNo, DWORD milliseconds, HANDLE hEvStop = 0);

   //static DWORD DoGPS(void*);
};

// coord - GPS_SCALE
// return in meters
int Distance(int latitude1, int longitude1, int latitude2, int longitude2);

#endif

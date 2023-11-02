/*
 * Copyright (C), 2007 - 2011, ƒенис ћос€гин
 *
 * ћодуль таймера
 * 
 *  ert   28/04/2011   creating
 */ 

#include "stdafx.h"
#include <vector>

#define DEFINE_EXPORT
#include "AppsModule.h"
#include "DeepIATHook.h"

#include <winsock2.h>
#include <ws2tcpip.h>
#include <Network.h>
#include "ntpdef.h"

const char* host = "pool.ntp.org";

static TimeModule *current;
TimeModule::STimeProc TimeModule::SysTimeProc = NULL;

TimeModule::TimeModule() : timeBias(0), timeChanges(0)
{
}

static long DateToMjd (int y, int m, int d)
{
    return
        367 * y
        - 7 * (y + (m + 9) / 12) / 4
        - 3 * ((y + (m - 9) / 7) / 100 + 1) / 4
        + 275 * m / 9
        + d
        + 1721028
        - 2400000;
}

static long SystemTimeToNTP(const SYSTEMTIME &st)
{
    long times;
    times = (DateToMjd(st.wYear, st.wMonth, st.wDay) - DateToMjd(1900, 1, 1)) * 86400 +
       st.wHour * 3600 + st.wMinute * 60 + st.wSecond;

    return times;
}

class NetworkEx : public Network
{
public:
   SOCKET GetSock() const { return socket; }
};

static bool GetNetTime(FILETIME *ft, __int64 timeBias)
{
   NetworkEx network;

   if( !network.ConnectByName(host, TIME_SERVICE_PORT, true, false) )
   {
      Log("Can't connect to %s", host);
      return false;
   }

   pkt packet = {0};
   packet.li_vn_mode = PKT_LI_VN_MODE(0, 3, 3);

   l_fp org;
   SYSTEMTIME st;
   GetSystemTime(&st);
   org.Ul_i.Xl_ui = htonl(SystemTimeToNTP(st));
   org.Ul_f.Xl_uf = 0;
   packet.xmt = org;

   DWORD cb = sizeof(packet);
   network.Send((BYTE*)&packet, cb);
   if( !network.Receive((BYTE*)&packet, &cb)/* resval > 0*/ && packet.org.Ul_i.Xl_ui == org.Ul_i.Xl_ui )
   {
      Log("Receive time packet error");
      return false;
   }

   //MessageBox(NULL, L"!", L"!", MB_OK);

   unsigned txTime = ntohl(packet.xmt.Ul_i.Xl_i);
   // get current system time
   __int64 current = txTime * (__int64)10000000 + (__int64)0x014f373bfde04000; // 01.01.1900 00:00 0 sec

   current -= timeBias;
   *(__int64*)ft = current;

   return true;
}

void GlbGetCurTime(SYSTEMTIME* st)
{
   if( current == NULL )
   {
      if( TimeModule::SysTimeProc )
         TimeModule::SysTimeProc(st);
      else
         GetLocalTime(st);
   }
   else
      current->GetTime(st);
}

bool TimeModule::CheckTime()
{
   bool ret = false;

   FILETIME ft1, ft2;
   if( GetNetTime(&ft1, timeBias) )
   {
      ret = true;
      SYSTEMTIME st;
      if( SysTimeProc )
         SysTimeProc(&st);
      else
         GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft2);
      sysTime = ft2;

      timeChanges = *(__int64*)&ft1 - *(__int64*)&ft2;
   }

   return ret;
}

bool TimeModule::Init()
{
   if( state == stInit )
      return false;

   state = stInit;
   current = this;

      //MessageBox(NULL, L"!", L"!", MB_OK);

//#ifdef DEBUG
//   {
//      SYSTEMTIME st;
//      GetLocalTime(&st);
//      SystemTimeToFileTime(&st, &sysTime );
//
//      //MessageBox(NULL, L"!", L"!", MB_OK);
//      if( SysTimeProc == NULL )
//         SysTimeProc = (STimeProc)DeepHookImportedFunction(L"coredll.dll", L"GetLocalTime", (PROC)GlbGetCurTime, NULL);
//
//   }
//#endif

   if( CheckTime() )
   {
      if( SysTimeProc == NULL )
         SysTimeProc = (STimeProc)DeepHookImportedFunction(L"coredll.dll", L"GetLocalTime", (PROC)GlbGetCurTime, NULL);
      state = stWork;
   } else
      state = stFail;

   return (state == stWork);
}

void TimeModule::Stop()
{
   if( SysTimeProc != NULL )
   {
      if( DeepHookRestoreFunction(L"coredll.dll", L"GetLocalTime", (PROC)GlbGetCurTime, NULL) == TRUE )
         SysTimeProc = NULL;
   }

   state = stOff;
   timeChanges = 0;
   current = NULL;
}

void TimeModule::Do()
{
   FILETIME ft;
   if( GetCurTime(&ft) )
   {
      __int64 diff = *(__int64*)&sysTime - *(__int64*)&ft;
      if( diff < 0) diff = -diff;
      // если разница больше 2 минут - считаем что есть кос€к и просто прибавл€ем минуту
      if( diff >= (__int64)1200000000 )
         *(__int64*)&sysTime += (__int64)600000000;
      else
         sysTime = ft;
   } else if( state != stInit ) // try Init
   {
      Init();
   }
}

bool TimeModule::GetCurTime(FILETIME *ft)
{
   if( state != stWork || SysTimeProc == NULL )
      return false;

   SYSTEMTIME st;
   SysTimeProc(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, ft);
   return true;
}

void TimeModule::SysTimeChanged()
{
   FILETIME ft;
   if( GetCurTime(&ft) )
   {
      __int64 diff = *(__int64*)&ft - *(__int64*)&sysTime + (__int64)600000000; // добавим одну минуту чтобы врем€ было вперед
      timeChanges -= diff;
      sysTime = ft;
   }
}

void TimeModule::SetTimeShift(int tz)
{
   timeBias = (__int64)tz * 60 * 10000000;
}

void TimeModule::GetTime(SYSTEMTIME *ret)
{
   FILETIME ft;
   SYSTEMTIME st;

   if( state == stWork )
   {
      SysTimeProc(&st);

      SystemTimeToFileTime(&st, &ft);
      *(__int64*)&ft += timeChanges;
      FileTimeToSystemTime(&ft, ret);
   } else
   {
      SysTimeProc(ret);
   }
}

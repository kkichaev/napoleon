/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Создание заказа
 *
 *  ert   20/08/2007   creating
 *  ert   17/07/2009   changing (SQL)
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <Exchange.h>
#include <DocImpl.h>

#include <NapoleonRes.h>
#include "FormEntries.h"

const DWORD DEMO_LIMIT = 3600 * 200; // демо период 200 рабочих часов
#include "RegHash.h"
#include "Preference.h"

#ifdef GPS_POS
const __int64 diff = (__int64)7 * 60 * 10000000;
Location gCurrentGPSPos;

typedef bool (*GetLocationT)(Location* location);
bool CheckGPSPos(const wchar_t *message, DWORD waitTime)
{
   HANDLE hApps = _Module.AppsIntance();
   NapoleonApp::Tracking t = _Module.GetTracking();
   if( t == NapoleonApp::trkNone || hApps == NULL ) return true;

   GetLocationT GetLocation = (GetLocationT)GetProcAddress((HMODULE)hApps, L"GetLocation");

   if( !GetLocation(&gCurrentGPSPos) )
   {
      gCurrentGPSPos.longitude = 0;
      gCurrentGPSPos.latitude = 0;
   }

   return true;

   //FILETIME ft;
   //SYSTEMTIME st;
   //GetLocalTime(&st);
   //st.wMilliseconds = 0;
   //SystemTimeToFileTime(&st, &ft);

   //__int64 val1 = *(__int64*)&ft;
   //__int64 val2 = *(__int64*)&gCurrentGPSPos.date;

   //bool doit = true;
   //bool ret = true;
   //if( val1 - val2 > diff )
   //{
   //   gCurrentGPSPos.longitude = 0;
   //   gCurrentGPSPos.latitude = 0;

   //   if( message != NULL && *message != L'\0' )
   //   {
   //      if( MessageBox(GetActiveWindow(), message, L"Информация", MB_YESNO | MB_ICONQUESTION) == IDNO )
   //      {
   //         gGPSState = GPSUnit::NotOpened;
   //         doit = false;
   //      }
   //   }

   //   if( doit )
   //   {
   //      WaitProgress wp(waitTime);
   //      wp.DoModal();  
   //   }

   //   ret = (gGPSState == GPSUnit::Synced);
   //   if( !ret )
   //   {
   //      ret = (MessageBox(GetActiveWindow(), L"Получить координаты GPS не удалось. Продолжить?", L"Ошибка",
   //         MB_YESNO | MB_ICONQUESTION) == IDYES);
   //   }
   //}

   //return ret;
}
#endif // GPS_POS

bool OrderImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) == true )
   {
      AddFromPriceList();
      return true;
   }

   return false;
}

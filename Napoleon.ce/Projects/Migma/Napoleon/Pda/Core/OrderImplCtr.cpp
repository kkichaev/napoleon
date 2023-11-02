/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
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

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

#include <ObjImpl.h>

#ifdef GPS_POS
#include <OrgDocs.h>
#include <FormEntries.h>
#endif

bool OrderImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Перед заказом надо получить координаты GPS\nПолучить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl o;
   o.Read(orgID);
   const wchar_t *dcost = o.dcost;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);

   const wchar_t *p = wcschr(dcost, L':');
   ResetTime(&st);
   if( p != NULL )
   {
      st.wHour = (WORD)_wtoi(p-2);
      st.wMinute = (WORD)_wtoi(p+1);
   }
   SystemTimeToFileTime(&st, &date);

   //  C9 2A69 C000 - 1 день
   // 25B 7F3D 4000 - 3 дня
   //if( st.wDayOfWeek == 5 ) // сегодня пятница добавим 3 дня
   //{
   //   date.dwLowDateTime += 0x7F3D4000l;
   //   date.dwHighDateTime += 0x25B;
   //   if( date.dwLowDateTime < 0x7F3D4000l )
   //      date.dwHighDateTime++;
   //} else
   {
      date.dwLowDateTime += 0x2A69C000l;
      date.dwHighDateTime += 0xC9;
      if( date.dwLowDateTime < 0x2A69C000l )
         date.dwHighDateTime++;
   }

   params = 0;

   if( wcschr(dcost, L'Б') != 0 ) params |= ofBankPay;
   if( wcslen(dcost) >= 6 )
   {
      wchar_t *tstr = wcsdup(dcost+1);
      wchar_t *ep = wcschr(tstr, L'=');
      if( ep ) *ep = L'\0';
      discount = (short)GetValue(tstr, DISCOUNT_SCALE);
      free(tstr);
   }
   else discount = 0;
   sumType = (WORD)_wtoi(dcost);
   if( sumType > 0 ) sumType--;

   remark = L"";
   podRemark = L"";

#ifdef ORD_DLV_BIND
   number = L"";
#endif

   supplyer = 0;

   id = holder.Add(o.id);
   rid = NO_ROWID;
   return EditDetail();
}


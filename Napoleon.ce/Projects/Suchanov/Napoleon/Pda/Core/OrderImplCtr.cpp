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

#include "ObjImpl.h"
#include <DocImpl.h>
#include <StdFuncs.h>

#ifdef SHEDULE
#include <FldOrgs.h>
#endif

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

   OrgImpl org;
   org.Read(orgID);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &created);

   _Module.GetLocalTime(&date);
   FileTimeToSystemTime(&date, &st);
   st.wMilliseconds = 0;
   ResetTime(&st);
   SystemTimeToFileTime(&st, &date);

   params = 0;
   remark = L"";

#ifdef ORD_DLV_BIND
   number = L"";
#endif

#ifdef POD_COMMENT
   podRemark = L"";
#endif

   supplyer = 0;
   sumType = 0;
   id = holder.Add(org.id);
   rid = NO_ROWID;

#ifdef ORG_COST_TYPE
   sumType = org.costype;            
#endif

#ifdef PAY_DELAY
   delay = 5;
#endif

#ifdef ORG_UNITS_STR
   unitCode = L"";
#endif

#ifdef ORDER_DISCOUNT
   discount = 0;
#endif

#ifdef SHEDULE
   if( SheduleData::OrgShedule().wYear != 0 )
      SystemTimeToFileTime(&SheduleData::OrgShedule(), &shedule);
   else
   {
      shedule.dwLowDateTime  = 0;
      shedule.dwHighDateTime = 0;
   }
#endif

#ifdef ORG_UNITS_STR
   unitCode = L"";
#endif

#ifdef MULTI_WH
   warehouseCode = L"";
#endif

   pay.dwHighDateTime = date.dwHighDateTime;
   pay.dwLowDateTime = date.dwLowDateTime;
   flags = 0;
   bank = 0;
   specCondition = 0;
   discount = 0;


   return EditDetail();
}


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
#include <NplConfig.h>

#include "BaseDialog.h"
#include <FldOrgs.h>

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

   supplyer = 0;
   sumType = -1;
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

#ifdef POD_COMMENT
   podRemark = L"";
#endif

   paySum = 0;
   supplDate = date;

   return EditDetail();
}


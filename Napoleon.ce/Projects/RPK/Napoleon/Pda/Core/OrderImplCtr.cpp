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

#ifdef VAN_SELLING
static void MakeDocNumber(std::wstring *num)
{
   wchar_t buf[100];
   DWORD nnum = 0;

   OrderImpl oi;
   SQLTable table(oi.Name());
   if( table.Select(&oi, L"ORDER BY created DESC") )
   {
      const wchar_t* p = oi.docNum;
      while( iswdigit(*p) == 0 && *p != L'\0')
      {
         num->append(1, *p);
         p++;
      }
      nnum = _wtoi(p);
   }

   wsprintf(buf, L"%d", nnum + 1);

   num->append(buf);
}
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

#ifdef STOP_LIST
   if((org.flags & ofStopList) != 0)
   {
      if( MessageBox(NULL, L"Клиент находится в стоп-листе, заявка может быть не обработана. Продолжать?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDNO )
         return false;
   }
#endif

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &created);

   _Module.GetLocalTime(&date);
   FileTimeToSystemTime(&date, &st);
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
   delay = org.payDelay;
#endif

#ifdef ORG_UNITS_STR
   unitCode = L"";
#endif

#ifdef ORDER_DISCOUNT
   discount = org.discount;
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

#ifdef VAN_SELLING
   std::wstring tn;
   MakeDocNumber(&tn);
   docNum = holder.Add(tn.c_str());
#endif

   return EditDetail();
}


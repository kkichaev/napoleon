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

#include <exchange.h>
#include <ceint.h>
#include <Table.h>
#include <Sync.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

OrderImpl::OrderImpl(CEOID orgID, SyncFormat *format, DocumentTypes dt)
{
   if( format == NULL ) syncOrder = new SyncOrder();
   else syncOrder = format;

   docType = dt;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   ResetTime(&st);

   SystemTimeToFileTime(&st, &date);
   //  C9 2A69 C000 - 1 день
   // 25B 7F3D 4000 - 3 дня
   if( st.wDayOfWeek == 5 ) // сегодня пятница добавим 3 дня
   {
      date.dwLowDateTime += 0x7F3D4000l;
      date.dwHighDateTime += 0x25B;
      if( date.dwLowDateTime < 0x7F3D4000l )
         date.dwHighDateTime++;
   } else
   {
      date.dwLowDateTime += 0x2A69C000l;
      date.dwHighDateTime += 0xC9;
      if( date.dwLowDateTime < 0x2A69C000l )
         date.dwHighDateTime++;
   }

   params = 0;
   remark = L"";

#ifdef ORD_DLV_BIND
   number = L"";
#endif

   supplyer = 0;
   sumType = 0;
   id = orgID;
   orderOID = 0;
}


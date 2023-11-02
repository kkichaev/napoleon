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
#include <DocImpl.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

#ifdef GPS_POS
#include <OrgDocs.h>
#include <FormEntries.h>
#endif


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

   params = 0;
   remark = L"";

#ifdef ORD_DLV_BIND
   number = L"";
#endif

   supplyer = 0;
   sumType = 0;
   id = orgID;
   orderOID = 0;

#ifdef ORG_COST_TYPE
   SyncOrg so;
   CEDBFormat fmt(so);
   CETable table(fmt);
   if( table.Open(so.FileName()) && table.Seek(orgID) )
   {
      Org org;

      table.GetCurrent(&org);
      sumType = org.costype;            
   }
#endif

#ifdef PAY_DELAY
   delay = 5;
#endif

   discount = 0;
}


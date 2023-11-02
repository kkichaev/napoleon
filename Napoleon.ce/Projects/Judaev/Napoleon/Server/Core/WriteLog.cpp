/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация объектов синхронизации
 *
 *  ert   08/08/2007   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include "Server.h"

#include <Exchange.h>
#include <Sync.h>


#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

void WriteToLog(const Order& order, const char *userID)
{
   const char *lf = LogFile();
   if( lf == NULL || *lf == '\0' )
      return;

   _set_fmode(_O_TEXT);
   FILE *fw = fopen(lf, "at");
   if( !fw )
      return;

   USES_CONVERSION;

   SYSTEMTIME ct, st, ot, closeT;
   GetLocalTime(&ct);

   FileTimeToSystemTime(&order.created, &st);
   FileTimeToSystemTime(&order.date, &ot);

   __int64 val = *(__int64*)&order.invoiceClose;
   val -= *(__int64*)&order.created;
   FileTimeToSystemTime((FILETIME*)&val, &closeT);

   Summing orderSum;
   orderSum.sum = 0;
   double sum = (double)std::for_each(order.items.begin(), order.items.end(), orderSum);

   std::string on = OrgName(ExchangeFolder(), W2A_CP(order.id, CP_OEMCP), userID);
   std::string::size_type pos = on.find('"');
   while( pos != std::string::npos )
   {
      on.replace(pos, 1, " ");
      pos = on.find('"');
   }

   fprintf(fw, "%02d/%02d/%d;%02d:%02d;""%02d/%02d/%d;%02d:%02d;""%02d:%02d:%02d;""%02d/%02d/%d;\"%s\";%.2f;",
           ct.wDay, ct.wMonth, ct.wYear, ct.wHour, ct.wMinute,
           st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute,
           closeT.wHour, closeT.wMinute, closeT.wSecond,
           ot.wDay, ot.wMonth, ot.wYear,
           on.c_str(), sum);

   fprintf(fw, "\"%s\"\n", UserName(ExchangeFolder(), userID));

   fclose(fw); 
}

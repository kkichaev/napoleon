/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации заказа
 *
 *  ert   08/08/2007   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include <exchange.h>
#include <sync.h>
#include "Server.h"
#include "Config.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

//
//------------------------------------ Sync Order -------------------------------------
//
const char* SyncOrder::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "ORD%s.DBF", userID);
   return buf; 
}

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   return true;
}


// ansi <-> oem check
bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;

   NapoleonConfig config;
   const OrderSend &data = (const OrderSend&)_data;

   SYSTEMTIME st;
   FileTimeToSystemTime(&data.created, &st);

   char buf[20];

   wsprintf(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   db->Fill("CREATED", buf);
   db->Fill("NUM", st.wHour*3600+st.wMinute*60+st.wSecond);//atoi(buf));
   db->Fill("CREDIT", 0);

   FileTimeToSystemTime(&data.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("DATE", buf);

   Summing orderSum;
   double sum = std::for_each(data.items.begin(), data.items.end(), orderSum);

   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));
   db->Fill("SUM", sum);
   db->Fill("PARAMS", (double)data.params);
   db->Fill("REMARK", W2A_CP(data.remark, CP_OEMCP));

   char tBuf[100];
   CharToOem(config.GetStringItem(SUPPL_TYPE, data.supplyer), tBuf);
   db->Fill("ORG", tBuf);

   int count = data.items.size();
   if( count )
   {
      for( int i=0; i<count; i++ )
      {
         const OrderItemSend &item = data.items[i];
         db->Fill("ID_I", W2A_CP(item.id, CP_OEMCP));
         db->Fill("QTY", ((double)item.qty)/QTY_SCALE);
         db->Fill("COST", ((double)item.cost)/SUM_SCALE);
         if( i > 0 )
            db->Fill("REMARK", "");
         db->Append();
      }
   }
   WriteToLog(data, userID);
   return true;
}

DBRec* SyncOrder::BaseHeader(int *count) const
{
   static DBRec dbrec[] = 
   {
      {"CREATED",'C',15,0},
      {"DATE",'D',8,0},
      {"ID",'C',MAX_ORG_ID,0},
      {"SUM",'N',10,2},
      {"PARAMS",'N',10,0},
      {"ID_I",'C',MAX_ITEM_ID,0},
      {"QTY",'N',10,2},
      {"COST",'N',10,2},
      {"REMARK",'C',100,0},
      {"ORG", 'C', 30, 0},
      {"NUM", 'N', 8, 0},
      {"CREDIT", 'N',1,0}, 
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}


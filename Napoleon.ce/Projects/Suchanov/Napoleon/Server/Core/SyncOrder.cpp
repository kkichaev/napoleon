/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации заказа
 *
 *  ert   09/09/2007   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include <Exchange.h>
#include <Sync.h>
#include "Server.h"
#include <Config.h>

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

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder*) const
{
   return false;
}

bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;

   NapoleonConfig config;

   const Order &data = (const Order&)_data;
   SYSTEMTIME st;
   FileTimeToSystemTime(&data.created, &st);

   char buf[20];
   wsprintf(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   db->Fill("CREATED", buf);
   db->Fill("NUM", buf);

   FileTimeToSystemTime(&data.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("DATE", buf);

   Summing orderSum;
   double sum = std::for_each(data.items.begin(), data.items.end(), orderSum);

   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));
   db->Fill("SUM", sum);
   db->Fill("PARAMS", (double)data.params);

   if( data.flags & ofTopicB )
      db->Fill("TOPIC", "Ѓ");

   char remBuf[150];
   char tBuf[100];
   strcpy(tBuf, config.GetStringItem(COST_TYPE, data.sumType));
   CharToOem(tBuf, tBuf);
   db->Fill("COSTYPE", tBuf);
   strcpy(remBuf, tBuf);
   strcat(remBuf, ";");

   strcpy(tBuf, config.GetStringItem(SUPPL_TYPE, data.supplyer));
   CharToOem(tBuf, tBuf);
   db->Fill("SUPPL", tBuf);
   strcat(remBuf, tBuf);
   strcat(remBuf, ";");

   WORD wval = data.specCondition;
   if( wval > 0 )
   {
      WORD discount = data.discount;
      unsigned char specInt, specDec;

      specInt = discount / DISCOUNT_SCALE;
      specDec = discount % DISCOUNT_SCALE;

      strcpy(tBuf, config.GetStringItem(SPEC_TYPE, wval-1));
      CharToOem(tBuf, tBuf);

      wsprintf(remBuf+strlen(remBuf),"%s %d.%d%%;", tBuf, specInt, specDec);
   }

   strcpy(tBuf,config.GetStringItem(BANK, data.bank));
   CharToOem(tBuf, tBuf);
   strcat(remBuf,tBuf);
   strcat(remBuf,";");

   if( st.wHour || st.wMinute )
   {
      CharToOem(";время ", remBuf+strlen(remBuf));
      wsprintf(remBuf+strlen(remBuf), "%02d:%02d", st.wHour, st.wMinute);
   }

   FileTimeToSystemTime(&data.pay, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("PDATE",buf);
   if( st.wHour || st.wMinute )
   {
      CharToOem(";лимит ", remBuf+strlen(remBuf));
      wsprintf(remBuf+strlen(remBuf), "%02d:%02d", st.wHour, st.wMinute);
   }

   db->Fill("REMARK", remBuf);
   db->Fill("REMARK2", W2A_CP(data.remark, CP_OEMCP));

   int count = data.items.size();
   if( count )
   {
      for( int i=0; i<count; i++ )
      {
         const OrderItem &item = data.items[i];
         db->Fill("ID_I", W2A_CP(item.id, CP_OEMCP));
         db->Fill("QTY", ((double)item.qty)/QTY_SCALE);
         db->Fill("COST", ((double)item.cost)/SUM_SCALE);
         db->Fill("ORDFLAG", (i == 0) ? 1 : 0);
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
      {"NUM",'C',15,0},
      {"DATE",'D',8,0},
      {"ID",'C',MAX_ORG_ID,0},
      {"SUM",'N',10,2},
      {"PARAMS",'N',10,0},
      {"ID_I",'C',MAX_ITEM_ID,0},
      {"QTY",'N',10,2},
      {"COST",'N',10,2},
      {"REMARK",'C',100,0},
      {"REMARK2",'C',200,0},
      {"ORDFLAG",'N',1,0},
      {"TOPIC", 'C', 1,0},
      {"COSTYPE", 'C', 50, 0},
      {"SUPPL", 'C', 20, 0},
      {"BANK", 'C', 20, 0},
      {"PDATE",'D',8,0},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}


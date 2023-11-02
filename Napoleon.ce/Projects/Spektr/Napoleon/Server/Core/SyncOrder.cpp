/*
 * Copyright (C), 2007, ─хэшё ╠юё ушэ
 *
 * ╨хрышчрЎш  ёшэїЁюэшчрЎшш чрърчр
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
#include <Server.h>
#include <Config.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

//
//------------------------------------ Sync Order -------------------------------------
//
const char* SyncOrder::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "INV%d.DBF", userID);
   return buf; 
}

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder*) const
{
   return false;
}

// ansi <-> oem check
bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   char buf[50];
   USES_CONVERSION;
   NapoleonConfig config;

   const Order &data = (const Order&)_data;
   SYSTEMTIME st;

   FileTimeToSystemTime(&data.created, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("WDATE", buf);

   char timeBuf[10];
   wsprintf(timeBuf, "%02d:%02d", st.wHour, st.wMinute);
   db->Fill("WTIME", timeBuf);

   SYSTEMTIME ct;
   GetLocalTime(&ct); 

   wsprintf(buf, "%d%02d%02d", ct.wYear, ct.wMonth, ct.wDay);
   db->Fill("SDATE",buf);
   wsprintf(timeBuf, "%02d:%02d", ct.wHour, ct.wMinute);
   db->Fill("STIME", timeBuf);

   FileTimeToSystemTime(&data.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("Дата", buf);

   int count = data.items.size();
   if( count )
   {
      for( int i=0; i<count; i++ )
      {
         if( !i )
         {
            char remBuf[300];
            strcpy(remBuf, W2A(data.remark));
            strcat(remBuf, "|");
            if( st.wHour || st.wMinute )
               wsprintf(remBuf+strlen(remBuf),"тЁхь  %02d:%02d",st.wHour, st.wMinute);
            CharToOem(remBuf, remBuf);
            
            char innbuf[100];
            int code;
            strncpy(innbuf, W2A_CP(data.id, CP_OEMCP), sizeof(innbuf));
            innbuf[sizeof(innbuf)-1] = '\0';
            char *p = strchr(innbuf, '\t');
            if( p )
            {
               *p = '\0';
               code = atoi(p+1);
            } else
               code = 0;
            
            char sumTypeBuf[100];
            strcpy(sumTypeBuf, config.GetStringItem(COST_TYPE, data.sumType));
            Summing orderSum;
            CharToOem(sumTypeBuf, sumTypeBuf);
            db->Fill("ВидЦены", sumTypeBuf);
            
            double sum = std::for_each(data.items.begin(), data.items.end(), orderSum);
            db->Fill("Сумма", sum);
            db->Fill("ТабНомер", userID);
            db->Fill("ИНН", innbuf);
            db->Fill("Катег", code);
            db->Fill("Прим", remBuf);
         } else
         {
            db->Fill("ТабНомер", "");
            db->Fill("ИНН", "");
            db->Fill("Катег", "");
            db->Fill("Прим", "");
         }
         
         const OrderItem &item = data.items[i];
         db->Fill("НомНомер", W2A_CP(item.id, CP_OEMCP) );

         double val, qty;
         val = ((double)item.cost)/SUM_SCALE;
         qty = ((double)item.qty)/QTY_SCALE;
         
         db->Fill("СуммаЦен", val*qty);
         db->Fill("Кол_во",qty);
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
      {"Дата",'D',8,0},
      {"Сумма",'N',20,2},
      {"ВидЦены",'C',20,0},
      {"Прим",'C',80,0},
      {"SDATE", 'D', 8, 0},
      {"STIME", 'C', 8, 0},
      {"WDATE", 'D', 8, 0},
      {"WTIME", 'C', 8, 0},
      {"НомНомер",'C',MAX_ITEM_ID,0},
      {"Кол_во",'N',20,5},
      {"СуммаЦен",'N',20,2},
      {"ТабНомер",'C',50,0},
      {"ИНН",'C',MAX_ORG_ID,0},
      {"Катег",'N',3,0},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}


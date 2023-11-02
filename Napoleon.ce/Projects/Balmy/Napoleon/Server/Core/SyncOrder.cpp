/*
 * Copyright (C), 2007, ─хэшё ╠юё ушэ
 *
 * ╨хрышчрЎш  ёшэїЁюэшчрЎшш чрърчр
 *
 *  ert   09/09/2007   creating
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
#include <Config.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

DBRec billFields[] = {
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
   {"Нал", 'N', 1, 0},
   {"WHNO", 'N', 3, 0},
   {"USERID", 'C', 20, 0},
   {"CREATED", 'C', 20, 0},
};

//
//------------------------------------ Sync Order -------------------------------------
//
static long GetValue(const char *p, const char *ep)
{
   long val = 0;
   while( p != ep && *p )
   {
      val *= 10;
      val += *p - '0';
      p++;
   }
   return val;
}

const char* SyncOrder::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "ORD%s.DBF", userID);
   return buf;

   //return "invoice.dbf"; 
}

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder*) const
{
   return false;
}

static void SetDate(DateType &dta, struct tm &sTime)
{
   wsprintf((char*)dta.month,"%02d",sTime.tm_mon+1);
   wsprintf((char*)dta.day,"%02d",sTime.tm_mday);
   _itoa(sTime.tm_year+1900,(char*)dta.year,10);
}
 
// ansi <-> oem check
static long WriteBillRecord(DataForm &bill, const Order &item, 
                            int orderNo, const char *inn, int code, const char *userID)
{
   USES_CONVERSION;

   bill.ResetRec();
   Summing orderSum;
   double val = std::for_each(item.items.begin(), item.items.end(), orderSum);
   bill.Fill("Сумма",val);

   struct tm locTime;
   DateType dta;

   char sumTypeBuf[100];
   NapoleonConfig config;
   strcpy(sumTypeBuf, config.GetStringItem(COST_TYPE, item.sumType));
   CharToOem(sumTypeBuf, sumTypeBuf);
   bill.Fill("ВидЦены",sumTypeBuf);

   SYSTEMTIME st;
   FileTimeToSystemTime(&item.date, &st);

   locTime.tm_mday = st.wDay;
   locTime.tm_mon = st.wMonth-1;
   locTime.tm_year = st.wYear-1900;
   SetDate(dta, locTime);
   bill.Fill("Дата",&dta);

   FileTimeToSystemTime(&item.created, &st);
   char cbuf[20];
   wsprintf(cbuf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   bill.Fill("CREATED", cbuf);

   locTime.tm_mday = st.wDay;
   locTime.tm_mon = st.wMonth-1;
   locTime.tm_year = st.wYear-1900;
   SetDate(dta, locTime);
   bill.Fill("WDATE", &dta);

   bill.Fill("USERID", userID);

   char timeBuf[10];
   wsprintf(timeBuf, "%02d:%02d", st.wHour, st.wMinute);
   bill.Fill("WTIME", timeBuf);

   SYSTEMTIME ct;
   GetLocalTime(&ct); 

   locTime.tm_mday = ct.wDay;
   locTime.tm_mon = ct.wMonth-1;
   locTime.tm_year = ct.wYear-1900;
   SetDate(dta,locTime);
   bill.Fill("SDATE",&dta);
   wsprintf(timeBuf, "%02d:%02d", ct.wHour, ct.wMinute);
   bill.Fill("STIME", timeBuf);

   for( int i=0; i<(int)item.items.size(); i++ )
   {
      if( i ==0 )
      {
         char remBuf[300];
         *remBuf = '\0';

         FileTimeToSystemTime(&item.date, &st);
         if( st.wHour || st.wMinute )
            wsprintf(remBuf+strlen(remBuf),"тЁхь  %02d:%02d;",st.wHour, st.wMinute);

         strcat(remBuf, W2A(item.remark));
         //if( item.params & ofCachePay )
         //   bill.Fill("Нал", "1");
         CharToOemBuff(remBuf, remBuf, sizeof(remBuf));
         remBuf[sizeof(remBuf)-1] = '\0';
         bill.Fill("ТабНомер", userID);
         bill.Fill("ИНН", inn);
         bill.Fill("Катег", code);
         bill.Fill("Прим", remBuf);
      } else
      {
         bill.Fill("ТабНомер", "");
         bill.Fill("ИНН", "");
         bill.Fill("Катег", "");
         bill.Fill("Прим", "");
         bill.Fill("Нал", "");
     }

      char buf[50];
      strcpy(buf, W2A_CP(item.items[i].id, CP_OEMCP));
      char *p = strchr(buf, '\t');
      int whNo = 0;
      if( p )
      {
         *p = '\0';
         whNo = atoi(p+1);
      } 
      bill.Fill("НомНомер", buf);
      bill.Fill("WHNO", whNo);

      double val;
      val = ((double)item.items[i].cost)/SUM_SCALE;

      double nQty = (double)item.items[i].qty/QTY_SCALE; 
      bill.Fill("СуммаЦен", val*nQty);
      bill.Fill("Кол_во",nQty);
      bill.Append();
   }
   return bill.GetRecNo();
}

static bool OpenBase( DataForm& base, const char* name, const char *path, int nEls, DBRec* fields )
{
   char fullName[MAX_PATH];
   strcpy(fullName,path);
   if( path[strlen(path)-1] != '\\' )
      strcat(fullName,"\\");
   strcat(fullName,name);

   if( base.Open(fullName) == False )
   {
      if( nEls )
         return (base.Create(fullName,nEls,fields)==True) ? true : false;
      return false;
   }
   return true;
} 

bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;
   const Order &order = (const Order&)_data;
   char innbuf[100];
   int code;
   strncpy(innbuf, W2A_CP(order.id, CP_OEMCP), sizeof(innbuf));
   innbuf[sizeof(innbuf)-1] = '\0';
   char *p = strchr(innbuf, '\t');
   if( p )
   {
      *p = '\0';
      code = atoi(p+1);
   } else
      code = 0;
   WriteBillRecord(*db, order, db->GetRecNo(), innbuf, code, userID);
   WriteToLog(order, userID);

   return true;
}

DBRec* SyncOrder::BaseHeader(int *count) const
{
   *count = sizeof(billFields)/sizeof(billFields[0]);
   return billFields;
}

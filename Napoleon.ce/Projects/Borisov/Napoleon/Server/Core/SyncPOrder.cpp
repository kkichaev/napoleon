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

FILETIME Mul(DWORD val1, DWORD val2)
{
   WORD a = LOWORD(val1), b = HIWORD(val1), 
        c = LOWORD(val2), d = HIWORD(val2);

   DWORD tval1 = (DWORD)a * d, tval2 = (DWORD)c * b,
         tval3 = (DWORD)a * c, tval4 = (DWORD)b * d;

   DWORD t = (DWORD)LOWORD(tval1) + LOWORD(tval2) + HIWORD(tval3);

   FILETIME res;
   res.dwLowDateTime = MAKELONG(LOWORD(tval3), LOWORD(t));
   res.dwHighDateTime = tval4 + (DWORD)HIWORD(t) + HIWORD(tval1) + HIWORD(tval2);

   return res;
}

//
// Не проверяем на переполнение
//
FILETIME Mul(const FILETIME& val1, DWORD val2)
{
   FILETIME resLo = Mul(val1.dwLowDateTime, val2);
   FILETIME resHi = Mul(val1.dwHighDateTime, val2);
   resHi.dwLowDateTime += resLo.dwHighDateTime;

   resHi.dwHighDateTime = resHi.dwLowDateTime;
   resHi.dwLowDateTime = resLo.dwLowDateTime;
   return resHi;
}

FILETIME Add(const FILETIME &v1, const FILETIME &v2)
{
   FILETIME res;
   DWORD v = (DWORD)HIWORD(v1.dwLowDateTime) + HIWORD(v2.dwLowDateTime);

   res.dwLowDateTime = (DWORD)LOWORD(v1.dwLowDateTime) + LOWORD(v2.dwLowDateTime) + MAKELONG(0, LOWORD(v));
   res.dwHighDateTime = v1.dwHighDateTime + v2.dwHighDateTime + HIWORD(v);

   return res;
}

#ifdef ORD_SURVAY
static DBRec SurvayRec[] = 
{
   {"DATE",'D',8,0},
   {"ID",'C',MAX_ORG_ID,0},
   {"TIME",'C', 15, 0}, 
   {"FOLDER",'N',9,0},
   {"CHOICE",'C',30,0},
};


void WriteSurvay(const char *userID, const Order &order)
{
   USES_CONVERSION;

   char userFile[50];
   OemToChar(userID, userFile);
   std::string fileName(ExchangeFolder());
   fileName += "OF";
   fileName += userFile;
   fileName += ".DBF";

   DataForm base;
   if( !base.Open(fileName.c_str()) )
   {
      if( !base.Create(fileName.c_str(), sizeof(SurvayRec)/sizeof(SurvayRec[0]), SurvayRec) )
         return;
   }

   base.Fill("ID", W2A_CP(order.id, CP_OEMCP));

   SYSTEMTIME st;
   FileTimeToSystemTime(&order.created, &st);

   char buf[20];
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   base.Fill("DATE", buf);

   wsprintf(buf, "%02d:%02d", st.wHour, st.wMinute);
   base.Fill("TIME", buf);

   vector_t<Survay>::const_iterator i = order.survay.begin();
   for( ; i != order.survay.end(); i++ )
   {
      base.Fill("FOLDER", (double)i->folder);
      base.Fill("CHOICE", W2A_CP(i->choice, CP_OEMCP));
      base.Append();
   }
}
#endif //ORD_SURVAY
//
//------------------------------------ Sync Order -------------------------------------
//
const char* SyncOrder::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "PRD%s.DBF", userID);
   return buf; 
}

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   Order *data = (Order*)_data;

   const char *p = db["CREATED"];
   std::string saveid(p);

   DateToFileTime(&data->created, p);
   data->params = atol(db["PARAMS"]);
   data->id = sh->Add(Trunc(db["ID"]), CP_OEMCP);
   data->items.clear();

   long curRec = db.GetRecNo();
   do
   {
      if( saveid.compare(db["CREATED"]) )
         break;

      OrderItem item;
      item.id = sh->Add(Trunc(db["ID_I"]), CP_OEMCP);
      item.cost = (DWORD)ScaleDouble(atof(db["COST"]), SUM_SCALE);
      item.flags = 0;
      item.qty = (DWORD)ScaleDouble(atof(db["QTY"]), QTY_SCALE);
      data->items.push_back(item);

      curRec++;
   } while( db.ReadRec(++curRec) );

   db.ReadRec(curRec-1);
   return true;
}


// ansi <-> oem check
bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;

   NapoleonConfig config;
   const Order &data = (const Order&)_data;

#ifdef ORD_SURVAY
   WriteSurvay(userID, data);
#endif

   SYSTEMTIME st;
   FileTimeToSystemTime(&data.created, &st);

   char buf[20];
   wsprintf(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   db->Fill("CREATED", buf);
   db->Fill("NUM", buf);

   FileTimeToSystemTime(&data.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("DATE", buf);

   //  1 день
   FILETIME payDate = { 0x2A69C000, 0xC9 }; // 1 день в FILETIME
   FILETIME res = Mul(payDate, data.delay);
   res = Add(data.date, res);
   FileTimeToSystemTime(&res, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("PAYDATE", buf);

   Summing orderSum;
   double sum = std::for_each(data.items.begin(), data.items.end(), orderSum);

   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));
   db->Fill("SUM", sum);
   db->Fill("CASH", ((data.params & ofCash) != 0) ? 1 : 0);
   db->Fill("REMARK", W2A_CP(data.remark, CP_OEMCP));

   char tBuf[100];
   CharToOem(config.GetStringItem(SUPPL_TYPE, data.supplyer), tBuf);
   db->Fill("FIRMA", tBuf);

   CharToOem(config.GetStringItem(COST_TYPE, data.sumType), tBuf);
   db->Fill("CTYPE", tBuf);

#ifdef ORG_UNITS_STR
   db->Fill("IDU", W2A_CP(data.unitCode, CP_OEMCP));
#endif
#ifdef ORDER_DISCOUNT
   db->Fill("DISCOUNT", ((double)data.discount)/DISCOUNT_SCALE);
#endif

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
      {"PAYDATE",'D',8,0},
      {"ID",'C',MAX_ORG_ID,0},
      {"SUM",'N',10,2},
      {"CASH",'N',1,0},
      {"ID_I",'C',MAX_ITEM_ID,0},
      {"QTY",'N',10,2},
      {"COST",'N',10,2},
      {"REMARK",'C',255,0},
      {"CTYPE", 'C',50},
      {"FIRMA", 'C',50},
      {"ORDFLAG",'N',1,0},
#ifdef ORG_UNITS_STR
      {"IDU",'C',10,0},
#endif
#ifdef ORDER_DISCOUNT
      {"DISCOUNT", 'N', 6, 1},
#endif

   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}


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
#include <Config.h>

#include <fcntl.h>

#include <algorithm>

#include <exchange.h>
#include <sync.h>
#include <Server.h>

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

   const Order &data = (const Order&)_data;
   SYSTEMTIME st;
   FileTimeToSystemTime(&data.created, &st);

   char buf[20];
   wsprintf(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   db->Fill("CREATED", buf);

   FileTimeToSystemTime(&data.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("DATE", buf);

   Summing orderSum;
   double sum = std::for_each(data.items.begin(), data.items.end(), orderSum);

   NapoleonConfig config;
   char sumTypeBuf[100];
   strcpy(sumTypeBuf, config.GetStringItem(COST_TYPE, data.sumType));
   CharToOem(sumTypeBuf, sumTypeBuf);

   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));
   db->Fill("SUM", sum);
   db->Fill("PARAMS", (double)data.params);
   db->Fill("REMARK", W2A_CP(data.remark, CP_OEMCP));
   db->Fill("COSTYPE", sumTypeBuf);
   db->Fill("UNIT", W2A_CP(data.unitCode, CP_OEMCP));
   db->Fill("WHCODE", W2A_CP(data.warehouseCode, CP_OEMCP));

   int count = data.items.size();
   if( count )
   {
      for( int i=0; i<count; i++ )
      {
         const OrderItem &item = data.items[i];
         db->Fill("ID_I", W2A_CP(item.id, CP_OEMCP));
         db->Fill("QTY", ((double)item.qty)/QTY_SCALE);
         db->Fill("COST", ((double)item.cost)/SUM_SCALE);
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
      {"COSTYPE", 'C', 50, 0},
      {"REMARK",'C',100,0},
      {"UNIT", 'C',200,0},
      {"WHCODE",'C',20},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}


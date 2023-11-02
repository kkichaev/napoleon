/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации накладных и оплат
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
//------------------------------------ Sync Delivery -------------------------------------
//
SyncDelivery::SyncDelivery(const char *userID) : SyncFormat(userID)
{
}

const char* SyncDelivery::FileName() const
{ 
   static char buf[50];
   LoadUserOrgs(&orgs, userID);

   wsprintf(buf, "DVS%s.DBF", userID);

   OemToChar(buf, buf);
   return buf; 
}

bool SyncDelivery::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;

   const DeliverySend &data = (const DeliverySend&)_data;
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

   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));
   db->Fill("SUM", sum);
   db->Fill("PARAMS", (double)data.flags);
   db->Fill("REMARK", W2A_CP(data.remark, CP_OEMCP));
   db->Fill("NUMBER", W2A_CP(data.number, CP_OEMCP));
   db->Fill("PAYMENT", W2A_CP(data.link, CP_OEMCP));

   NapoleonConfig config;
   char sumTypeBuf[100];
   strcpy(sumTypeBuf, config.GetStringItem(COST_TYPE, data.costType));
   CharToOem(sumTypeBuf, sumTypeBuf);
   db->Fill("COSTYPE", sumTypeBuf);

   int count = data.items.size();
   if( count )
   {
      for( int i=0; i<count; i++ )
      {
         const DeliveryItemSend &item = data.items[i];
         db->Fill("ID_I", W2A_CP(item.id, CP_OEMCP));
         double qty = ((double)item.qty)/QTY_SCALE;
         double cost = (((double)item.sum)/SUM_SCALE) / qty;
         db->Fill("QTY", qty);
         db->Fill("COST", cost);
         db->Append();
      }
   }
   WriteToLog(data, userID);
   return true;
}

bool SyncDelivery::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   USES_CONVERSION;
   DeliverySend *data = (DeliverySend*)_data;

   std::string id = Trunc(db["ID"]);
   if( orgs.find(id) == orgs.end() )
      return false;

   DateToFileTime(&data->date, db["DATE"]);
   data->id = sh->Add(id.c_str(), CP_OEMCP);

   const char *numField = "NUMBER";
   if( db[numField] == NULL ) numField = "NUM";

   std::string saveid(Trunc(db[numField]));
   data->number = sh->Add(saveid.c_str(), CP_OEMCP);

   long curRec = db.GetRecNo();
   data->items.clear();
   do
   {
      if( saveid.compare(Trunc(db[numField])) )
         break;

      DeliveryItemSend item;
      item.id = sh->Add(Trunc(db["ID_I"]), CP_OEMCP);
      item.sum = (DWORD)ScaleDouble(atof(db["SUM"]), SUM_SCALE);
      item.qty = (DWORD)ScaleDouble(atof(db["QTY"]), QTY_SCALE);
      data->items.push_back(item);

   } while( db.ReadRec(++curRec) );

   db.ReadRec(curRec-1);
   return true;
}

DBRec* SyncDelivery::BaseHeader(int *count) const
{
   static DBRec dbrec[] = 
   {
      {"CREATED",'C',15,0},
      {"DATE",'D',8,0},
      {"NUMBER", 'C', 30},
      {"PAYMENT", 'C', 30},
      {"ID",'C',MAX_ORG_ID,0},
      {"SUM",'N',10,2},
      {"PARAMS",'N',10,0},
      {"ID_I",'C',MAX_ITEM_ID,0},
      {"QTY",'N',10,2},
      {"COST",'N',10,2},
      {"COSTYPE", 'C', 50, 0},
      {"REMARK",'C',255,0},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}

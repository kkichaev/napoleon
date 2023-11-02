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

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

void LoadUserOrgs(ORG_SET *orgs, const char *userID)
{
   char buf[50];
   wsprintf(buf, "O%s.DBF", userID);

   std::string fileName(ExchangeFolder());
   OemToChar(buf, buf);
   fileName += buf;

   DataForm obase;
   bool opened = obase.Open(fileName.c_str());

   if( !opened )
   {
      fileName = ExchangeFolder();
      fileName += "ORGS.DBF";
      opened = obase.Open(fileName.c_str());
   }
   if( opened )
   {
      for( int rc=0; obase.ReadRec(rc); rc++ )
      {
         const char *p = Trunc(obase["ID"]);
         orgs->insert(p);
      }
   }
}

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

   wsprintf(buf, "DLV%s.DBF", userID);
   return buf; 
}

bool SyncDelivery::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   USES_CONVERSION;
   Delivery *data = (Delivery*)_data;

   std::string id = Trunc(db["ID"]);
   if( orgs.find(id) == orgs.end() )
      return false;

   DateToFileTime(&data->date, db["DATE"]);
   data->id = sh->Add(id.c_str(), CP_OEMCP);

   const char *numField = "NUMBER";
   if( db[numField] == NULL ) numField = "NUM";

   std::string saveid(Trunc(db[numField]));
   data->number = sh->Add(saveid.c_str(), CP_OEMCP);

#ifdef ORD_DLV_BIND
   const char *ptest = db["CREATED"];
   data->created.dwLowDateTime = 0;
   data->created.dwHighDateTime = 0;
   if( ptest != NULL )
   {
      std::string created(Trunc(ptest));
      SYSTEMTIME st;
      
      if( created.size() > 0 )
      {
         sscanf(created.c_str(), "%4d%2d%2d%2d%2d%2d", &st.wYear, &st.wMonth, &st.wDay, &st.wHour, &st.wMinute, &st.wSecond);
         SystemTimeToFileTime(&st, &data->created);
      }
   }
#endif

   long curRec = db.GetRecNo();
   data->items.clear();
   do
   {
      if( saveid.compare(Trunc(db[numField])) )
         break;

      DeliveryItem item;
      item.id = sh->Add(Trunc(db["ID_I"]), CP_OEMCP);
      item.sum = (DWORD)ScaleDouble(atof(db["SUM"]), SUM_SCALE);
      item.qty = (DWORD)ScaleDouble(atof(db["QTY"]), QTY_SCALE);
      data->items.push_back(item);

   } while( db.ReadRec(++curRec) );

   db.ReadRec(curRec-1);
   return true;
}

//
//------------------------------------ Sync Payment -------------------------------------
//
const char* SyncPayment::FileName() const
{ 
   static char buf[50];
   LoadUserOrgs(&orgs, userID);

   wsprintf(buf, "PAY%s.DBF", userID);
   return buf; 
}

bool SyncPayment::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   USES_CONVERSION;
   Payment *data = (Payment*)_data;

   std::string id = Trunc(db["ID"]);
   if( orgs.find(id) == orgs.end() )
      return false;

   const char *pdate = Trunc(db["DATE"]);
   data->sum = (DWORD)ScaleDouble(atof(db["SUM"]), SUM_SCALE);
   if( *pdate == L'\0' || data->sum == 0 ) return false;

   DateToFileTime(&data->date, pdate);
   data->id = sh->Add(id.c_str(), CP_OEMCP);

   const char *numField = "NUM";
   data->number = sh->Add(Trunc(db[numField]), CP_OEMCP);

   const char *p = db["DOCDATE"];
   if( p != NULL ) DateToFileTime(&data->dlvDate, p);
   else
   {
      data->dlvDate.dwLowDateTime = 0;
      data->dlvDate.dwHighDateTime = 0;
   }

   p = db["DOCSUM"];
   if( p != NULL ) data->dlvSum = (DWORD)ScaleDouble(atof(p), SUM_SCALE);
   else data->dlvSum = 0;

   p = db["PAYDELAY"];
   if( p != NULL ) data->payDelay = atoi(p);
   else data->dlvSum = 0;


   return true;
}


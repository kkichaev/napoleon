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

#include <Exchange.h>
#include <Sync.h>
#include <Config.h>
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

void LoadDocProps(Delivery* data, const char* file, StringHolder* sh)
{
   char buf[MAX_PATH];
   OemToChar(Trunc(file), buf);
   std::string fileName;
   if( buf[1] == ':' || (*buf == '\\' && buf[1] == '\\') ) fileName = buf;
   else
   {
      fileName = ExchangeFolder();
      fileName += buf;
   }

   NapoleonConfig config(fileName.c_str());
   const std::map<std::string, std::string>& keys = config.Keys();
   std::map<std::string, std::string>::const_iterator i = keys.begin();

   for( ;i != keys.end(); i++ )
   {
      KeyValue cs;
      cs.key = sh->Add(i->first.c_str(), CP_OEMCP);
      cs.value = sh->Add(i->second.c_str(), CP_OEMCP);
      data->values.push_back(cs);
   }
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

   const char *p = db["PAYDATE"];
   if( p == NULL )
   {
      data->payDate.dwLowDateTime = 0;
      data->payDate.dwHighDateTime = 0;
   } else
      DateToFileTime(&data->payDate, p);

   p = db["PROP"];
   if( p != NULL )
      LoadDocProps(data, p, sh);

   p = db["AGENT"];
   data->agent = (p==NULL) ? L"" : sh->Add(p, CP_OEMCP);

   const char *psum = db["SUMD"]; 
   bool haveSumD = (psum != NULL);
   DWORD docSum = (haveSumD) ? (DWORD)ScaleDouble(atof(psum), SUM_SCALE) : 0;
   long curRec = db.GetRecNo();
   data->items.clear();
   do
   {
      if( saveid.compare(Trunc(db[numField])) )
         break;

      DeliveryItem item;
      item.id = sh->Add(Trunc(db["ID_I"]), CP_OEMCP);

      DWORD sumItem = (DWORD)ScaleDouble(atof(db["SUM"]), SUM_SCALE);
      if( !haveSumD )
         docSum += sumItem;
      item.sum = sumItem;
      item.qty = (DWORD)ScaleDouble(atof(db["QTY"]), QTY_SCALE);
      data->items.push_back(item);

   } while( db.ReadRec(++curRec) );

#ifdef MAKE_BALANCE
#else
   data->sumD = docSum;
#endif

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

   const char *numField = "NUMBER";
   if( db[numField] == NULL ) numField = "NUM";
   data->number = sh->Add(Trunc(db[numField]), CP_OEMCP);

   return true;
}


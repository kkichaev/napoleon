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

   OemToChar(buf, buf);
   std::string fileName(ExchangeFolder());
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

void LoadPays(const char *userID, SyncDelivery::Pays *pays)
{
   char buf[50];
   wsprintf(buf, "PAY%s.DBF", userID);

   OemToChar(buf, buf);
   std::string fileName(ExchangeFolder());
   fileName += buf;

   DataForm base;
   bool opened = base.Open(fileName.c_str());

   if( !opened )
   {
      fileName = ExchangeFolder();
      fileName += "PAYMENT.DBF";
      opened = base.Open(fileName.c_str());
   }

   if( !opened ) return;

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      SyncDelivery::PayData pd;
      pd.number = Trunc(base["NUM"]);
      pd.tabNum = Trunc(base["TAB_NUM"]);

      pd.sumT = (DWORD)ScaleDouble(atof(base["SUMT"]), SUM_SCALE);

      pays->insert(pd);
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

   LoadPays(userID, &pays);
   return buf; 
}

bool SyncDelivery::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder *sh) const
{
   USES_CONVERSION;
   Delivery *data = (Delivery*)_data;

   std::string id = Trunc(db["ID"]);

   char *buf = (char*)alloca(id.size()+20);
   strcpy(buf, id.c_str());
   char *p = strchr(buf, '\t');
   if( p )
   {
      int code = atoi(p+1);
      wsprintf(p+1, "%3d", code);
      id = buf;
   }   
   if( orgs.find(id) == orgs.end() )
      return false;

   DateToFileTime(&data->date, db["DATE"]);
   data->id = sh->Add(id.c_str(), CP_OEMCP);

   const char *numField = "NUMBER";
   if( db[numField] == NULL ) numField = "NUM";

   std::string saveid(Trunc(db[numField]));
   data->number = sh->Add(saveid.c_str(), CP_OEMCP);

   PayData pd;
   pd.number = saveid;
   pd.tabNum = userID;

   Pays::const_iterator fnd = pays.find(pd);
   if( fnd != pays.end() ) data->sumT = fnd->sumT;
   else data->sumT = 0;

   const char *psum = db["SUMD"]; 
   data->sumD = (psum == NULL) ? 0 : (DWORD)ScaleDouble(atof(psum), SUM_SCALE);

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
   return false;

   USES_CONVERSION;
   Payment *data = (Payment*)_data;

   std::string id = Trunc(db["ID"]);

   char *buf = (char*)alloca(id.size()+20);
   strcpy(buf, id.c_str());
   char *p = strchr(buf, '\t');
   if( p )
   {
      int code = atoi(p+1);
      wsprintf(p+1, "%3d", code);
      id = buf;
   }   
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


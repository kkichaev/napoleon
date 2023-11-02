/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации прайс-листа
 *
 *  ert   04/02/2008   creating
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

#ifdef MULTI_WH
bool SyncPrice::LoadWHOrder(std::map<std::string, int> *whOrders, const char* exchangeFolder)
{
   DataForm base;
   std::string fileName(exchangeFolder);
   fileName += "SKLAD.DBF";

   if( !base.Open(fileName.c_str()) ) return false;

   int order = 0;
   for( long rc=0; base.ReadRec(rc); rc++ )
      (*whOrders)[Trunc(base["ID"])] = order++;

   return true;
}

void SyncPrice::LoadQTYs(const char* exchangeFolder)
{
   char buf[30];
   DataForm base;
   std::string fileName(exchangeFolder);

   wsprintf(buf, "WHR%s.DBF", userID);
   OemToChar(buf, buf);
   fileName += buf;
   if( !base.Open(fileName.c_str()) )
   {
      fileName = exchangeFolder;
      fileName += "WHRESTS.DBF";

      if( !base.Open(fileName.c_str()) )
         return;
   }

   std::map<std::string, int> whOrders;
   if( !LoadWHOrder(&whOrders, exchangeFolder) )
      return;

   for( long rc=0; base.ReadRec(rc); rc++ )
   {
      std::string code = Trunc(base["ID"]);
      vector_t<QtyItem> &vqtys = qtys[code];

      code = Trunc(base["IDW"]);
      if( vqtys.size() == 0 )
      {
         QtyItem q;
         q.qty = 0;
         vqtys.insert(vqtys.begin(), whOrders.size(), q);
      }

      vqtys[whOrders[code]].qty = ScaleDouble(atof(base["QTY"]), QTY_SCALE);
   }
}
#endif

//
//------------------------------------ Sync Price -------------------------------------
//
SyncPrice::SyncPrice(const char *userID) : SyncFormat(userID)
{
#ifdef MULTI_WH
   LoadQTYs(ExchangeFolder());
#endif
}

const char* SyncPrice::FileName() const
{ 
   static char buf[50];

   wsprintf(buf, "F%s.DBF", userID);
   std::string fileName(ExchangeFolder());
   OemToChar(buf, buf);
   fileName += buf;

   DataForm fbase;
   if( fbase.Open(fileName.c_str()) )
   {
      folders.clear();
      for( long rc=0; fbase.ReadRec(rc); rc++ )
      {
         DWORD val = atoi(fbase["ID"]);
         folders.insert(val);
      }
   }

   wsprintf(buf, "W%s.DBF", userID);
   return buf; 
}

bool SyncPrice::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   DWORD folderID = atoi(db["FOLDER"]);
   if( folders.size() && folders.find(folderID) == folders.end() )
      return false;

   std::string code = Trunc(db["ID"]);
   ((Price*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Price*)data)->id = sh->Add(code.c_str(), CP_OEMCP);
   ((Price*)data)->folderID = folderID;
   ((Price*)data)->photo = L"";

   const char *p;
#ifndef Suchanov
   p = db["WEIGHT"];
   ((Price*)data)->weight = (DWORD)((p != NULL) ? ScaleDouble(atof(p), WEIGHT_SCALE) : 0);
#endif

   for( int i=0; true; i++ )
   {
      char buf[10];
      wsprintf(buf, "COST%d", i+1);
      const char *p = db[buf];

      if( p == NULL )
         break;

      CostItem cs = (DWORD)ScaleDouble(atof(p), SUM_SCALE);
      ((Price*)data)->cost.push_back(cs);
   }

#ifdef MULTI_WH
   std::map<std::string, vector_t<QtyItem>>::const_iterator qf = qtys.find(code);
   if( qf != qtys.end() )
      ((Price*)data)->qty = qf->second;
#else
   ((Price*)data)->qty = (DWORD)(atof(db["QTY"]) * QTY_SCALE);
#endif

   ((Price*)data)->qtyInPack = (DWORD)ScaleDouble(atof(db["INPACK"]), QTY_SCALE);

   p = db["FLAGS"];
   ((Price*)data)->flags = ( p ) ? atoi(p) : 0;

   p = db["TAX1"];
   ((Price*)data)->tax1 = ( p ) ? atoi(p) : 0;

#ifdef PRICE_COLOR
   p = db["COLOR"];
   if( p != NULL )
      ((Price*)data)->color = strtol(p, (char**)&p, 16);
   else
      ((Price*)data)->color = 0;
#endif

   return true;
}

bool SyncPrice::SetToDB(DataForm *db, const IReflectableData &data) const
{
   USES_CONVERSION;

   db->Fill("NAME", W2A_CP(((const Folder&)data).name, CP_OEMCP));
   db->Fill("ID", (double)((const Folder&)data).id);
   return true;
}

DBRec* SyncPrice::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",'C',MAX_ITEM_ID,0},
      {"NAME",'C',MAX_ITEM_NAME,0},
      {"FOLDER",'N',9,0},
      {"COST1",'N',9,0},
      {"COST2",'N',9,0},
      {"COST3",'N',9,0},
      {"QTY",'N',9,3},
      {"INPACK",'N',9,3},
      {"FLAGS",'N',5,0},
      {"TAX1",'N',2,0},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}



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

//
//------------------------------------ Sync Price -------------------------------------
//
SyncPrice::SyncPrice(const char *userID) : SyncFormat(userID)
{
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

   ((Price*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Price*)data)->id = sh->Add(Trunc(db["ID"]), CP_OEMCP);
   ((Price*)data)->folderID = folderID;
   ((Price*)data)->photo = L"";

   const char *p;
   p = db["REMARK"];
   if( p != NULL ) ((Price*)data)->remark = sh->Add(Trunc(p), CP_OEMCP);
   else ((Price*)data)->remark = L"";

#ifndef Suchanov
   p = db["WEIGHT"];
   ((Price*)data)->weight = (DWORD)((p != NULL) ? ScaleDouble(atof(p), WEIGHT_SCALE) : 0);
#endif

   DWORD curCost = 0;
   for( int i=0; i<MAX_NUM_COST; i++ )
   {
      char buf[10];
      wsprintf(buf, "COST%d", i+1);
      const char *p = db[buf];
      if( p )
      {
         curCost = (DWORD)ScaleDouble(atof(p), SUM_SCALE);
      }
      CostItem cs = curCost;
      ((Price*)data)->cost.push_back(cs);
      //((Price*)data)->cost[i] = curCost;
   }

   ((Price*)data)->qty = (DWORD)(atof(db["QTY"]) * QTY_SCALE);
   ((Price*)data)->qtyInPack = (DWORD)ScaleDouble(atof(db["INPACK"]), QTY_SCALE);

   p = db["FLAGS"];
   ((Price*)data)->flags = ( p ) ? atoi(p) : 0;

   p = db["TAX1"];
   ((Price*)data)->tax1 = ( p ) ? atoi(p) : 0;

   p = db["SORT"];
   ((Price*)data)->sort = ( p ) ? atoi(p) : 0;
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



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

#include "add.h"

//
//------------------------------------ Sync Price -------------------------------------
//
SyncPrice::SyncPrice(const char *userID) : SyncFormat(userID)
{
   loadSecondPrice = false;
}

const char* SyncPrice::FileName() const
{ 
   static char buf[50];

   wsprintf(buf, "F%s.DBF", userID);
   std::string fileName(ExchangeFolder());
   OemToChar(buf, buf);
   fileName += buf;

   DataForm fbase;
   bool opened = fbase.Open(fileName.c_str());
   if( !opened )
   {
      fileName = ExchangeFolder();
      fileName += SyncFolder(userID).AltFileName();

      opened = fbase.Open(fileName.c_str());
   }
   if( opened )
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

void RecodeFoldersID(const char *folder, std::set<DWORD> *src, std::map<DWORD, DWORD> *recode, bool loadFolders)
{
   char fileName [MAX_PATH];
   DataForm folders;

   wsprintf(fileName, "%s%s", folder, SECOND_FOLDER);

   if( folders.Open(fileName) == False )
      return;

   for( long rc=0; folders.ReadRec(rc); rc++ )
   {
      int code = atoi(folders["ID"]);

      int newCode = code;
      while( src->find(newCode) != src->end() )
         newCode++;

      if( newCode != code )
         recode->insert(std::map<int,int>::value_type(code, newCode));

      if( loadFolders )
         src->insert(newCode);
   }
}

void SyncPrice::PrepareAltPrice() const
{
   loadSecondPrice = true;

   RecodeFoldersID(ExchangeFolder(), &folders, &recode, true);
}

bool SyncPrice::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   DWORD folderID = atoi(db["FOLDER"]);

   if( loadSecondPrice )
   {
      std::map<DWORD,DWORD>::const_iterator fnd = recode.find(folderID);
      if( fnd != recode.end() )
         folderID = fnd->second;
   }
   if( folders.size() && folders.find(folderID) == folders.end() )
      return false;

   ((Price*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Price*)data)->id = sh->Add(Trunc(db["ID"]), CP_OEMCP);
   ((Price*)data)->folderID = folderID;
   ((Price*)data)->photo = L"";

   const char *p;
#ifndef Suchanov
   p = db["WEIGHT"];
   ((Price*)data)->weight = (DWORD)((p != NULL) ? ScaleDouble(atof(p), WEIGHT_SCALE) : 0);
#endif

   for( int i=0; i<MAX_NUM_COST; i++ )
   {
      char buf[10];
      wsprintf(buf, "COST%d", i+1);
      const char *p = db[buf];

      if( p == NULL )
         break;

      CostItem cs = (DWORD)ScaleDouble(atof(p), SUM_SCALE);
      ((Price*)data)->cost.push_back(cs);
   }

   ((Price*)data)->qty = (DWORD)ScaleDouble(atof(db["QTY"]), QTY_SCALE);
   ((Price*)data)->qtyInPack = (DWORD)ScaleDouble(atof(db["INPACK"]), QTY_SCALE);

   p = db["FLAGS"];
   ((Price*)data)->flags = ( p ) ? atoi(p) : 0;

   p = db["TAX1"];
   ((Price*)data)->tax1 = ( p ) ? atoi(p) : 0;
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



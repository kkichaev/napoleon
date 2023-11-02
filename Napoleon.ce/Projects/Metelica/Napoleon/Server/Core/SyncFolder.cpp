/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации папок
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
//------------------------------------ Sync Folder -------------------------------------
//
SyncFolder::SyncFolder(const char *userID) : SyncFormat(userID) 
{
   loadSecondPrice = false; 
   upLevelLoaded = false;
   upLevel = 0;
   baseOrder = 0;
}

const char* SyncFolder::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "F%s.DBF", userID);
   return buf; 
}

void SyncFolder::PrepareAltPrice() const
{
   loadSecondPrice = true;

   std::string fileName(ExchangeFolder());
   DataForm base;
   fileName += FileName();
   bool opened = base.Open(fileName.c_str());
   if( ! opened )
   {
      fileName = ExchangeFolder();
      fileName += AltFileName();
      opened = base.Open(fileName.c_str());
   }


   baseOrder = 0;
   if( !opened )
      return;

   long rc = 0;
   for( ; base.ReadRec(rc); rc++)
   {
      if( rc == 0 )
         upLevel = atoi(base["LEVEL"]);
      folders.insert(atoi(base["ID"]));
   }

   baseOrder = rc;
   RecodeFoldersID(ExchangeFolder(), &folders, &recode, false);
}

bool SyncFolder::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   DWORD id = atoi(db["ID"]);
   if( loadSecondPrice )
   {
      std::map<DWORD,DWORD>::const_iterator fnd = recode.find(id);
      if( fnd != recode.end() )
         id = fnd->second;      

      if( upLevelLoaded == false )
      {
         upLevelLoaded = true;
         upLevel -= atoi(db["LEVEL"]);
      }
   }

   ((Folder*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Folder*)data)->id = id;
   ((Folder*)data)->level = atoi(db["LEVEL"]) - upLevel;
   ((Folder*)data)->sort = db.GetRecNo() + baseOrder;
   ((Folder*)data)->size = 0;
   ((Folder*)data)->firstID = -1;

   return true;
}

bool SyncFolder::SetToDB(DataForm *db, const IReflectableData &data) const
{
   USES_CONVERSION;

   db->Fill("NAME", W2A_CP(((const Folder&)data).name, CP_OEMCP));
   db->Fill("ID", (double)((const Folder&)data).id);
   db->Fill("LEVEL", ((const Folder&)data).level);
   return true;
}

DBRec* SyncFolder::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",'N',9,0},
      {"LEVEL",'N',2,0},
      {"NAME",'C',MAX_ORG_NAME,0}
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}

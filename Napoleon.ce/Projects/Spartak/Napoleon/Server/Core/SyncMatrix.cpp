/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Синхронизация матриц товара
 *
 *  ert   01/06/2009  creating
 */ 
#include "stdafx.h"
#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include "Server.h"

#include <Exchange.h>
#include <Sync.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

void SyncMatrix::LoadMatrix(const char *exchangeFolder, const char *userName)
{
   std::string fileName(exchangeFolder);

   char userFile[50];
   OemToChar(userName, userFile);

   DataForm base;
   fileName += "M";
   fileName += userFile;
   fileName += ".DBF";
   if( !base.Open(fileName.c_str()) )
   {
      fileName = exchangeFolder;
      fileName += "MATRIX.DBF";

      if( !base.Open(fileName.c_str()) )
         return;
   }

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      const char *p = base["IDO"];
      if( p != NULL && *Trunc(p) != '\0' ) continue;

      std::string code = Trunc(base["NAME"]);
      MatrixItem mi;
      mi.id = holder.Add(Trunc(base["ID"]), CP_OEMCP);

      matrix[code].push_back(mi);
   }

   current = matrix.begin();
}

SyncMatrix::SyncMatrix(const char *userID) : SyncFormat(userID)
{
   LoadMatrix(ExchangeFolder(), userID);
}

bool SyncMatrix::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   if( current == matrix.end() )
      return false;

   ((Matrix*)data)->name = sh->Add(current->first.c_str(), CP_OEMCP);
   ((Matrix*)data)->items = current->second;

   current++;
   return true;
}

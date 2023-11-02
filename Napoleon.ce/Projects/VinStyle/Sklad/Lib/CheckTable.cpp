/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * SQLCheckTable
 *
 *  ert   19/10/2009   creating
 */ 
#include "stdafx.h"
#include "DBImpl.h"
#include <StdFuncs.h>

bool SQLCheckTable(const IDBData &dbdata)
{
   SQLTable table(dbdata.Name());
   if( table.IsTableExist(dbdata.Name()) )
      return table.CheckDBFormat(dbdata.Type());

   if( !table.Create(dbdata.Type(), dbdata.KeyFields()) )
      return false;

   const wchar_t **index = dbdata.Indexes();
   if( index != NULL )
   {
      while( *index )
         table.CreateIndex(*index++);
   }
   return true;
}

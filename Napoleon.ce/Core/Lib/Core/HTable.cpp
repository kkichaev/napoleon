/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Работа с иерархическими таблицами
 *
 *  ert   13/08/2008   creating
 */
#include "stdafx.h"
#include <HTable.h>

HTable::HTable(const IDBFormat& _format) : CETable(_format)
{
}

void HTable::GetLeaf(CEOID folderID, DWORD *firstIndex, DWORD *itemSize)
{
   const DataReflector &reflector = DataType();
   const MemberType &levelT = reflector.Type(L"level");
   const MemberType &idT = reflector.Type(L"id");
   const MemberType &sizeT = reflector.Type(L"size");
   const MemberType &firstIDT = reflector.Type(L"firstID");

   IReflectableData *folder = reflector.Create();

   *itemSize = 0;
   *firstIndex = -1;

   SetTag(L"sort");

   WORD checkLevel = -1;

   if( Seek(folderID) )
   {
      while( true )
      {
         GetCurrent(folder);

         WORD curL = *(WORD*)levelT.GetValue(*folder);
         if( checkLevel == (WORD)-1 )
            checkLevel = curL;
         else if( curL <= checkLevel )
            break; 

         DWORD fi = *(DWORD*)firstIDT.GetValue(*folder);
         WORD size = *(WORD*)sizeT.GetValue(*folder);

         if( *firstIndex == -1 )
            *firstIndex = fi;
         if( size > 0 )
            *itemSize += size;

         if( MoveNext(true) == NULL )
            break;
      }
   }

   delete folder;
}



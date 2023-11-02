/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Последние продажи, коэффициент автозаказа
 *
 *  ert   22/09/2011   creating
 */
#include "stdafx.h"

#include <FormEntries.h>
#include "DocType.h"

#include "OrgRmnts.h"
#include <StdFuncs.h>
#include "OffTake.h"

#include <NplConfig.h>

#include "Add.h"

BEGIN_TYPE_REFLECTION(FolderCoef)
   REGISTER_ULONG_MEMBER(FolderCoef, id)
   REGISTER_USHORT_SCALE_MEMBER(FolderCoef, coef, SUM_SCALE)
END_TYPE_REFLECTION(FolderCoef)


DWORD GetOffTakeCoef(const wchar_t* itemId)
{
   FolderCoefImpl fi;
   PriceImpl pi;

   pi.id = (wchar_t*)itemId;
   pi.Read();
   fi.id = pi.folderID;
   if( fi.Read() )
      return fi.coef;

   DWORD offTakeCoef = 120;
   std::wstring val;
   NapoleonConfig cfg;
   if( cfg.ReadValue(&val, OFFTAKE_COEF) )
      offTakeCoef = _wtoi(val.c_str());

   return offTakeCoef;
}


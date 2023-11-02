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

DWORD GetOffTakeCoef(const wchar_t* itemId)
{
   DWORD offTakeCoef = 150;
   std::wstring val;
   NapoleonConfig cfg;
   if( cfg.ReadValue(&val, OFFTAKE_COEF) )
      offTakeCoef = _wtoi(val.c_str());

   return offTakeCoef;
}


/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список заказов
 * 
 *  ert   01/09/2007   creating
 */ 
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>
#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include <DateDialog.h>

#include "DocListData.h"

void ListDocData::RemoveOrdersTill(const SYSTEMTIME &check)
{
   FILETIME ft;
   SystemTimeToFileTime(&check, &ft);

     wchar_t buf[200];
   __int64 val = ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
   wsprintf(buf,  L"date <= %d%09d", (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));

   const_cast<DocType*>(docType)->RemoveDocuments(L"", buf);
   OpenDocType(docType->Type());
}

LRESULT ListDoc::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   RemoveDateDialog dlg;
   if( dlg.DoModal() == IDOK )
   {
      ((ListDocData*)data)->RemoveOrdersTill(dlg.date);
      Refresh();
   }
   return 0;
}

/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Список документов - изменения
 * 
 *  ert   18/08/2010   creating
 */ 
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>
#include <SQLTable.h>
#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>
#include "OrgDocs.h"
#include "DocListData.h"

bool ListDocData::IsClosed(int index) const
{
   IDocument *d = docList->Get(index);
   if( d != NULL )
   {
      const wchar_t* text = d->Description();

      int len = wcslen(text) + 1;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      wcscpy(buf, text);
      CharUpper(buf);

      return (wcscmp(buf, L"ЗАКРЫТА") == 0);
   }
   return false;
}


LRESULT ListDoc::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( ListForm::SetCellInfo(hdr) == FALSE )
   {
      if( di->item.mask & LVIF_IMAGE )
         di->item.iImage = 0;
      return FALSE;
   }

   int index = di->item.iItem;

#ifdef ORD_DLV_BIND
   if( di->item.mask & LVIF_IMAGE )
   {
      if( ((ListDocData*)data)->IsClosed(index) )
         di->item.iImage = 3;
      else
      {
         DWORD pindex = 0;
         if( ((ListDocData*)data)->OrderHandled(index) ) pindex = 3;
         else if( ((ListDocData*)data)->IsProceeded(index) ) pindex = 2;
         else if( !((ListDocData*)data)->IsDirty(index) ) pindex = 1;
         di->item.iImage = pindex;
      }
   }
#else
   if( di->item.mask & LVIF_IMAGE )
   {
      if( ((ListDocData*)data)->IsClosed(index) )
         di->item.iImage = 3;
      else
      {
         DWORD params = ((ListDocData*)data)->GetParams(index);
         di->item.iImage = (params & ofProceeded) ? 2 : (params & ofExported) ? 1 : ((ListDocData*)data)->IsDirty(index) ? 0 : 1;
      }
   }
#endif
   return TRUE;
}


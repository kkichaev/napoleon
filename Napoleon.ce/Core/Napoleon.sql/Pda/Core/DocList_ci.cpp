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

#ifdef Migma 
   // галочки ставим наоборот - черная пришло подтверждение, красная заявка отправлена
#ifndef ORD_DLV_BIND
   if( di->item.mask & LVIF_IMAGE )
   {
      DWORD params = ((ListDocData*)data)->GetParams(index);
      di->item.iImage = (params & ofProceeded) ? 1 : (params & ofExported) ? 2 : 0;
   }
#else
   if( di->item.mask & LVIF_IMAGE )
   {
      DWORD pindex = 0;
      if( ((ListDocData*)data)->OrderHandled(index) ) pindex = 3;
      else if( ((ListDocData*)data)->IsProceeded(index) ) pindex = 1;
      else if( !((ListDocData*)data)->IsDirty(index) ) pindex = 2;
      di->item.iImage = pindex;
   }
#endif
#else
#ifdef ORD_DLV_BIND
   if( di->item.mask & LVIF_IMAGE )
   {
      DWORD pindex = 0;
      if( ((ListDocData*)data)->OrderHandled(index) ) pindex = 3;
      else if( ((ListDocData*)data)->IsProceeded(index) ) pindex = 2;
      else if( !((ListDocData*)data)->IsDirty(index) ) pindex = 1;
      di->item.iImage = pindex;
   }
#else
   if( di->item.mask & LVIF_IMAGE )
   {
      DWORD pindex = 0;
      if( ((ListDocData*)data)->IsProceeded(index) ) pindex = 2;
      else if( !((ListDocData*)data)->IsDirty(index) ) pindex = 1;
      di->item.iImage = pindex;
   }
#endif
#endif // Migma
   return TRUE;
}


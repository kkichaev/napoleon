/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Общая функция создания документа (для OrgList & OrgDocs)
 *
 *  ert   30/08/2010   creating
 *
 */
#include "stdafx.h"
#include "DocType.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DBImpl.h>

#include <ListForm.h>
#include "FormEntries.h"

#include <Visit.h>

bool AddNewDocument(ListForm* owner, const DocType* docType, const ROWID& orgID)
{
   if( docType->Type() != dtOrder )
      return docType->CreateDocument(orgID);

   bool res = false;
   CRect rc;
   owner->GetWindowRect(rc);

   HMENU hMenu = LoadMenu(_Module.GetModuleInstance(), MAKEINTRESOURCE(IDC_DOCUMENTS));
   HMENU hPopup = GetSubMenu(hMenu, 0);

   int ret = ::TrackPopupMenuEx(hPopup, TPM_LEFTALIGN | TPM_BOTTOMALIGN | TPM_RETURNCMD, rc.left, rc.bottom, owner->m_hWnd, NULL);

   const wchar_t* createdDocType = NULL;
   const DocType *dt = NULL;
   if( ret == IDD_VISIT )
   {
      createdDocType = dtVisit;
      SetNextCreatedDoc(dtOrder);
   }
   else if( ret == IDR_NEW_ORDER )
      createdDocType = dtOrder;

   if( createdDocType != NULL )
   {
      dt = docTypeManager.GetDocType(createdDocType);
      res = dt->CreateDocument(orgID);
   }

   return res;
}
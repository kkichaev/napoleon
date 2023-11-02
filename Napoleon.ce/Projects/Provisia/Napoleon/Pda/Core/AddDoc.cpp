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

#include "AgentTask.h"

#include <Preference.h>

bool AddNewDocument(ListForm* owner, const DocType* docType, const ROWID& orgID)
{
   Preference preference;
   preference.Load();

   SetAgentNextDocType(docType);
   if( docType->Type() == dtProxy || (preference.flags & apfShowSKU) == 0 || docType->Type() == dtAgentTask )
      return docType->CreateDocument(orgID);

   bool res = false;
   CRect rc;
   owner->GetWindowRect(rc);

   HMENU hMenu = LoadMenu(_Module.GetModuleInstance(), MAKEINTRESOURCE(IDC_DOCUMENTS));
   HMENU hPopup = GetSubMenu(hMenu, 0);

   int ret = ::TrackPopupMenuEx(hPopup, TPM_LEFTALIGN | TPM_BOTTOMALIGN | TPM_RETURNCMD, rc.left, rc.bottom, owner->m_hWnd, NULL);
   if( ret == IDC_TASK )
   {
      OutOfPlan = false;
      OpenAgentTask(orgID, false, docType);
      return false;
   } else if ( ret == IDC_ADD )
   {
      OutOfPlan = true;
   } else
      return false;

   return docType->CreateDocument(orgID);
}
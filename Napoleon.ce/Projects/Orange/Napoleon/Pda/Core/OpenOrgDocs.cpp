/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список организаций
 *
 *  ert   13/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocType.h>

#include <ObjImpl.h>
#include "OrgDocs.h"
#include "FormEntries.h"

#ifdef MARK_SYNCED
#include <DoSync.h>
#endif

#include "Refr.h"

struct OrgDocsListDataAdd : public OrgDocsListData
{
   OrgDocsListDataAdd(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type) {}

   virtual const wchar_t* DocOrderField(const wchar_t* type) const
   {
      return (type == dtRfrDoc ) ? L"created" : L"date"; 
   }
};

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   OrgDocsListData *od = new OrgDocsListDataAdd(orgID, type);
#ifdef MARK_SYNCED
   CheckSync();
#endif
   _Module.GetFrame()->Load(IDD_ORG_DOCS, od);
}

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


void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   OrgDocsListData *od = new OrgDocsListData(orgID, type);
#ifdef MARK_SYNCED
   CheckSync();
#endif
   _Module.GetFrame()->Load(IDD_ORG_DOCS, od);
}

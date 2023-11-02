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

#include "OrgDocs.h"
#include "FormEntries.h"

struct OrgDocsData : public OrgDocsListData
{
   OrgDocsData(const wchar_t *orgID, const wchar_t* type) : OrgDocsListData(orgID, type) {}

   virtual int ColumnsCount() const { return 2; }
};

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( type == dtOrder || type == dtDelivery || type == dtBalance )
      _Module.GetFrame()->Load(IDD_ORG_DOCS, new OrgDocsData(orgID, type));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}

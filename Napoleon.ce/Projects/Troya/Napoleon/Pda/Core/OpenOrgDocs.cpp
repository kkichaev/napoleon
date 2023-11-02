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
#include "AutoOrder.h"
#include <Reports.h>

class OrgDocsAdd : public OrgDocsList
{
public:
   OrgDocsAdd() {}


   // Shortcut Menu для формы
   virtual DWORD GetMenuID() const { return OrgDocsList::GetMenuID(); }

   // Menu bar ID
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS_ADD/*IDD_ORG_REMNANTS*/; }
   
   BEGIN_MSG_MAP(OrgDocsAdd)
      COMMAND_ID_HANDLER(IDC_AUTO_ORDER, AutoOrder)
      COMMAND_ID_HANDLER(IDC_REPORTS, Reports)
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

   LRESULT AutoOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

IMPLEMENT_FORM(OrgDocsAdd)

LRESULT OrgDocsAdd::AutoOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   MakeAutoOrder(((OrgDocsListData*)data)->OrgID(), false);
   return 0;
}

LRESULT OrgDocsAdd::Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenReports(((OrgDocsListData*)data)->OrgID());
   return 0;
}

void OpenOrgDocs(CEOID oid, DocumentTypes type)
{
   if( type == dtOrder || type == dtDelivery || type == dtBalance )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(oid, type));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenListForm(oid);
   }
}

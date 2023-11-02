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

#include "OrgList.h"
#include "FldOrgs.h"
#include <DocType.h>
#include "Reports.h"

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

class OrgListAdd : public OrgList
{
public:
   OrgListAdd();

   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST_ADD; }

   BEGIN_MSG_MAP(OrgListAdd)
      COMMAND_ID_HANDLER(IDC_REPORTS, Reports)
      CHAIN_MSG_MAP(OrgList)
   END_MSG_MAP()

   LRESULT Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)
};

class FOrgListAdd : public OrgFolders
{
public:
   FOrgListAdd();

   BEGIN_MSG_MAP(OrgListAdd)
      COMMAND_ID_HANDLER(IDC_REPORTS, Reports)
      CHAIN_MSG_MAP(OrgFolders)
   END_MSG_MAP()

   LRESULT Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   DECLARE_FORM(FOrgListAdd, IDD_FLDORGS_ADD)
};

IMPLEMENT_FORM(OrgListAdd)
IMPLEMENT_FORM(FOrgListAdd)

OrgListAdd::OrgListAdd()
{
}

LRESULT OrgListAdd::Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenReports();
   return 0;
}

FOrgListAdd::FOrgListAdd()
{
}
LRESULT FOrgListAdd::Reports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenReports();
   return 0;
}

void OpenOrgFolders(DocumentTypes type)
{
   OrgFoldersData *od = new OrgFoldersData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS_ADD, od);
}

void OpenOrgList(DocumentTypes type)
{
   SyncFOrg sf;
   CEDBFormat format(sf);
   CETable table(format);

   if( table.Open(sf.FileName()) == true && table.Count() != 0 )
   {
      OpenOrgFolders(type);
      return;
   }

   OrgListData *od = new OrgListData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}

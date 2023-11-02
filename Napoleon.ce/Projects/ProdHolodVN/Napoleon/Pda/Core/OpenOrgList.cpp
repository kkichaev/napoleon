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
#include "Add.h"

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

class OrgFoldersAdd : public OrgFolders
{
public:
   OrgFoldersAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS_ADD; }
   //virtual DWORD GetResourceID() const { return IDD_FLDORGS; }

   BEGIN_MSG_MAP(OrgFoldersAdd)
      COMMAND_ID_HANDLER(IDD_PLAN, ShowPlan)
      CHAIN_MSG_MAP(OrgFolders)
   END_MSG_MAP()

   LRESULT ShowPlan(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenPlans(((OrgFoldersData*)data)->GetDocType()->Type());
      return 0;
   }

   DECLARE_FORM(OrgFoldersAdd, IDD_FLDORGS_ADD)
};

class OrgListAdd : public OrgList
{
public:
   OrgListAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST_ADD; }
   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }

   BEGIN_MSG_MAP(OrgListAdd)
      COMMAND_ID_HANDLER(IDD_PLAN, ShowPlan)
      CHAIN_MSG_MAP(OrgList)
   END_MSG_MAP()

   LRESULT ShowPlan(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenPlans(((OrgListData*)data)->GetDocType()->Type());
      return 0;
   }

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)
};

IMPLEMENT_FORM(OrgFoldersAdd)
IMPLEMENT_FORM(OrgListAdd)


void OpenOrgFolders(const wchar_t *type)
{
   OrgFoldersData *od = new OrgFoldersData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS_ADD, od);
}

void OpenOrgList(const wchar_t* type)
{
   OrgFolderImpl of;
   SQLTable table(of.Name());

   if( SQLTable::IsTableExist(of.Name()) && table.Count() != 0 )
   {
      Preference prf;
      prf.Load();
      if( prf.flags & opfCalendarView )
      {
         OpenOrgFolders(type);
         return;
      }
   }

   OrgListData *od = new OrgListData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}

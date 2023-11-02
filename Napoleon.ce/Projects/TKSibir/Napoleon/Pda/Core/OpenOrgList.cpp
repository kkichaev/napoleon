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

#include <algorithm>
#include <set>
#include <StdFuncs.h>

#include "Plan.h"

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

static bool ShowOrgInfo(const OrgImpl& org)
{
   if( *org.info != L'\0' )
   {
      MessageBox(GetActiveWindow(), org.info, L"Информация", MB_OK|MB_ICONINFORMATION);
   }
   return true;
}

struct OrgListDataAdd : public OrgListData
{
   OrgListDataAdd()
   {
   }

   virtual OrgListData* Clone()
   {
      OrgListData *od = new OrgListDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   virtual bool Selecting(int index)
   {
      ROWID id = GetOID(index);
      if( org.Read(id) )
      {
         if( ShowOrgInfo(org) )
            docType->OpenForm(org.id, NULL);
      }
      return false;
   }
};

struct FoldersAdd : public OrgFoldersData
{
   FoldersAdd()
   {
   }

   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new FoldersAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   virtual bool SelectLeaf(int index)
   {
      ROWID id = GetOID(index);
      if( org.Read(id)/* && (org.flags & ofStopList) == 0*/ )
      {
         if( owner ) OrgFolders::lastViewed = owner->GetLastVisibleItem();
         if( ShowOrgInfo(org) )
            docType->OpenForm(org.id, NULL);
      }
      return false;
   }
};

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
   OrgFoldersData *od = new FoldersAdd();
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

   OrgListData *od = new OrgListDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}

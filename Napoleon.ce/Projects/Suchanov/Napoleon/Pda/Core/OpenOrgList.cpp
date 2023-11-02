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

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

struct IDs : public IReflectableData
{
   wchar_t *id;

   DECLARE_TYPE_REFLECTION(IDs);
};

BEGIN_TYPE_REFLECTION(IDs)
   REGISTER_STRING_MEMBER(IDs, id)
END_TYPE_REFLECTION(IDs)

static bool ShowOrgInfo(const OrgImpl& org)
{
   if( *org.info != L'\0' )
   {
      MessageBox(GetActiveWindow(), org.info, L"Информация", MB_OK|MB_ICONINFORMATION);
   }
   return true;
}

static void LoadIDS(std::set<std::wstring> *ids)
{
   FILETIME ft;
   SYSTEMTIME st;
   SQLTable t(DeliveryImpl().Name());

   GetLocalTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &ft);

   wchar_t buf[100];
   __int64 val = ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
   wsprintf(buf,  L"where payDate < %d%09d and payDate != 0", val / 1000000000, val % 1000000000);

   IDs id;
   bool bdo = t.Select(&id, buf);
   while( bdo )
   {
      ids->insert(id.id);
      bdo = t.SelectNext(&id);
   }
}

static COLORREF OrgColor(const ROWID &orgID, const std::set<std::wstring> &ids, COLORREF defaultColor)
{
   if( orgID == NO_ROWID ) return defaultColor;

   OrgImpl org;
   org.Read(orgID);

   if( ids.find(org.id) != ids.end() ) return RGB(255,0,0);
   return defaultColor;
}

struct OrgListDataAdd : public OrgListData
{
   OrgListDataAdd()
   {
      LoadIDS(&ids);
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      return OrgColor(GetOID(index), ids, defaultColor);
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
         {
            if( owner ) OrgList::lastViewed = owner->GetLastVisibleItem();
            docType->OpenForm(org.id, NULL);
         }
      }
      return false;
   }
   std::set<std::wstring> ids;
};

struct FoldersAdd : public OrgFoldersData
{
   std::set<std::wstring> ids;

   FoldersAdd()
   {
      LoadIDS(&ids);
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor) const
   {
      return (folders.size() > 0) ? defaultColor : OrgColor(GetOID(index), ids, defaultColor);
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

void OpenOrgFolders(const wchar_t *type)
{
   OrgFoldersData *od = new FoldersAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS, od);
}


void OpenOrgList(const wchar_t* type)
{
   OrgFolderImpl of;
   SQLTable table(of.Name());

   if( SQLTable::IsTableExist(of.Name()) && table.Count() != 0 )
   {
      OpenOrgFolders(type);
      return;
   }

   OrgListData *od = new OrgListDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST, od);
}

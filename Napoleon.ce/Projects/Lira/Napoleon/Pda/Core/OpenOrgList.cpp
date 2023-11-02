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

static bool ShowOrgInfo(const OrgImpl& org, bool showAlert)
{
   if( IsStartDate(org.endLicense) )
      return true;

   SYSTEMTIME st;
   __int64 ft;
   _Module.GetLocalTime((FILETIME*)&ft);
   FileTimeToSystemTime((FILETIME*)&ft, &st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, (FILETIME*)&ft);

   if( ct - ft < twoDays / 2 )
   {
      if( showAlert )
         MessageBox(NULL, L"У клиента просрочена лицензия", L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
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
         if( ShowOrgInfo(org, true) )
            docType->OpenForm(org.id, NULL);
      }
      return false;
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      OrgImpl org;
      if( org.Read(GetOID(index)) && ShowOrgInfo(org, false) == false )
         return RGB(192,192,192);
      return defaultColor;
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
         if( ShowOrgInfo(org, true) )
            docType->OpenForm(org.id, NULL);
      }
      return false;
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      OrgImpl org;
      if( org.Read(GetOID(index)) && ShowOrgInfo(org, false) == false )
         return RGB(192,192,192);
      return defaultColor;
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
   _Module.GetFrame()->Load(IDD_ORG_LIST, od);
}

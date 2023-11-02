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

#include <StdFuncs.h>
#include <ScriptDoc.h>

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

static void SumToText(std::wstring* txt, long sumV)
{
   wchar_t buf[50], src[50];

   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);

   txt->assign(buf);
}

static void ShowOrgInfo(const OrgImpl& org)
{
   if( *org.stopMsg != L'\0' )
   {
      MessageBox(NULL, org.stopMsg, L"Внимание!", MB_OK | MB_ICONEXCLAMATION);
   } else
   {
      int limit = (int)org.limit;
      if( limit != 0 )
      {
         std::wstring val;
         SumToText(&val, limit);
         val.insert(0, L"Лимит отгрузки\n");
         val.append(L" р.");
         MessageBox(NULL, val.c_str(), L"Информация", MB_OK | MB_ICONINFORMATION);
      }
   }
}

static COLORREF OrgColor(const ROWID &orgID, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd)
{
   if( orgID == NO_ROWID ) return defaultColor;

   OrgImpl org;
   org.Read(orgID);

   if( *org.stopMsg != L'\0' )
      lvcd->clrTextBk = RGB(210,210,210);

   return org.color;
}

struct OrgListDataAdd : public OrgListData
{
   OrgListDataAdd()
   {
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      return OrgColor(GetOID(index), defaultColor, lvcd);
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
         ShowOrgInfo(org);
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

   virtual bool SelectLeaf(int index)
   {
      ROWID id = GetOID(index);
      if( org.Read(id) )
      {
         if( owner ) OrgFolders::lastViewed = owner->GetLastVisibleItem();
         ShowOrgInfo(org);
         docType->OpenForm(org.id, NULL);
      }
      return false;
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      return (folders.size() > 0) ? defaultColor : OrgColor(GetOID(index), defaultColor, lvcd);
   }

   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new FoldersAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
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
   if( type == dtOrder )
      type = dtScript;

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

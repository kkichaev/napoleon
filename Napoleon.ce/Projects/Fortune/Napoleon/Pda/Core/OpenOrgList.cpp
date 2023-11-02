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

static void SetBkColor(const OrgImpl& org, NMLVCUSTOMDRAW *lvcd)
{
   if( *org.stopMsg != L'\0' )
      lvcd->clrTextBk = RGB(255, 0, 0);
   else if( *org.debtMsg != L'\0' )
      lvcd->clrTextBk = RGB(255, 255, 0);
}

static void ShowOrgInfo(const OrgImpl& org)
{
   const wchar_t* title = NULL;
   const wchar_t* msg = NULL;
   if( *org.stopMsg != L'\0' )
   {
      msg = org.stopMsg;
      title = L"Клиент заблокирован";
   } else if( *org.debtMsg != L'\0' )
   {
      msg = org.debtMsg;
      title = L"Просрочка";
   }

   if( msg )
      MessageBox(GetActiveWindow(), msg, title, MB_OK|MB_ICONINFORMATION);
}

struct OrgListDataEx : public OrgListData
{
   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      COLORREF ret = OrgListData::GetItemColor(index, defaultColor, lvcd);
      OrgImpl org;
      if( org.Read(GetOID(index)) )
         SetBkColor(org, lvcd);

      return ret;
   }

   virtual OrgListData* Clone()
   {
      OrgListData *od = new OrgListDataEx();
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

struct OrgFolderDataEx : public OrgFoldersData
{
   virtual bool SelectLeaf(int index)
   {
      ROWID id = GetOID(index);
      if( org.Read(id)/* && (org.flags & ofStopList) == 0*/ )
      {
         if( owner ) OrgFolders::lastViewed = owner->GetLastVisibleItem();
         ShowOrgInfo(org);
         docType->OpenForm(org.id, NULL);
      }
      return false;
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      COLORREF ret = OrgFoldersData::GetItemColor(index, defaultColor, lvcd);
      OrgImpl org;
      if( org.Read(GetOID(index)) )
         SetBkColor(org, lvcd);

      return ret;
   }

   virtual OrgFoldersData* Clone()
   {
      OrgFoldersData *od = new OrgFolderDataEx();
      od->SetDocType(GetDocType()->Type());
      return od;
   }
};

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new OrgFolderDataEx();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS, od);
#endif
}

void OpenOrgList(const wchar_t* type)
{
   OrgFolderImpl of;
   SQLTable table(of.Name());
#ifdef SHEDULE
   SheduleData::ClearShedule();
#endif

   if( SQLTable::IsTableExist(of.Name()) && table.Count() != 0 )
   {
//#ifdef SHEDULE
      Preference prf;
      prf.Load();
      if( prf.flags & opfCalendarView )
      {
         OpenOrgFolders(type);
         return;
      }
//#else
//      OpenOrgFolders(type);
//      return;
//#endif
   }

   OrgListData *od = new OrgListDataEx();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST, od);
}

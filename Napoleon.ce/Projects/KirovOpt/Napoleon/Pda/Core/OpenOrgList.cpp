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

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

void CountOrderSum(DWORD *sum, DWORD *weight)
{
   *sum = 0;
   *weight = 0;

   SYSTEMTIME st;
   __int64 from, till;
   GetLocalTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, (FILETIME*)&from);
   till = from + (__int64)3600 * 24 * 10000000;
   wchar_t buf[200];
   wsprintf(buf,  L"date >= %d%09d and date < %d%09d", 
      (DWORD)(from / 1000000000), (DWORD)(from % 1000000000),
      (DWORD)(till / 1000000000), (DWORD)(till % 1000000000));

   const DocType *dt = docTypeManager.GetDocType(dtOrder);
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(L"", &orgDocs, buf, L"") )
   {
      for( unsigned i=0; i<orgDocs->Count(); i++ )
      {
         IDocument *d = orgDocs->Get(i);
         OrderImpl *oi = (OrderImpl*)d->Data();
         *sum += oi->Sum();
         *weight += oi->Weight();
      }
   }
   delete orgDocs;
}

void SetWeight(SumLabel &sumLabel, DWORD weight)
{
   if( weight > WEIGHT_SCALE/2 )
   {
      wchar_t buf[40], src[40];
      if(weight % WEIGHT_SCALE) // округление
         weight = ((weight + WEIGHT_SCALE/2) / WEIGHT_SCALE) * WEIGHT_SCALE;

      ConvertScaling(src, weight, WEIGHT_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), weight % WEIGHT_SCALE, WEIGHT_SCALE, true);
      wcscat(buf, L" кг");
      sumLabel.SetInfoText(buf);
   }
}

struct OrgDataAdd : public OrgListData
{
   virtual OrgListData* Clone()
   {
      OrgListData *od = new OrgDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   virtual void AfterSetName() const
   {
      name += L" ";
      wchar_t *p = wcschr(org.address, L'\t');
      if( p != NULL )
         name.append(org.address, (p - org.address));
      else
         name += org.address;
   }

   virtual DWORD GetSum() const { return orgSum; }

   DWORD CountWeight()
   {
      DWORD weight;
      CountOrderSum(&orgSum, &weight);
      return weight; 
   }
   DWORD orgSum;
};

struct FoldersDataAdd : public OrgFoldersData
{
   virtual OrgFoldersData* Clone()
   {
      OrgFoldersData *od = new FoldersDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   virtual void AfterSetName(const Org& org) const
   {
      name += L" ";
      wchar_t *p = wcschr(org.address, L'\t');
      if( p != NULL )
         name.append(org.address, (p - org.address));
      else
         name += org.address;
   }

   virtual DWORD GetSum() const { return orgSum; }

   DWORD CountWeight()
   {
      DWORD weight;
      CountOrderSum(&orgSum, &weight);
      return weight; 
   }
   DWORD orgSum;
};

class FolderAdd : public OrgFolders
{
 public:
   FolderAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS; }
   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuID() const { return IDR_NEW_ORDER; }

   virtual void LoadMenuBar(bool hideSIP)
   {
      ListForm::LoadMenuBar(hideSIP);
      DWORD weight = ((FoldersDataAdd*)data)->CountWeight();
      SetWeight(sumLabel, weight);
   }

   DECLARE_FORM(FolderAdd, IDD_FLDORGS_ADD)
};

class OrgListAdd : public OrgList
{
 public:
   OrgListAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuID() const { return IDD_ORG_LIST; }
   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }

   virtual void LoadMenuBar(bool hideSIP)
   {
      ListForm::LoadMenuBar(hideSIP);
      DWORD weight = ((OrgDataAdd*)data)->CountWeight();
      SetWeight(sumLabel, weight);
   }

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)
};

IMPLEMENT_FORM(FolderAdd)
IMPLEMENT_FORM(OrgListAdd)

void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new FoldersDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS_ADD, od);
#endif
}

void OpenOrgList(const wchar_t *type)
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

   OrgListData *od = new OrgDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}

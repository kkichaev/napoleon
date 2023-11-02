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

#include <set>
#include <StdFuncs.h>

static void LoadOrgs(std::set<ROWID>* yellowOrgs, std::set<ROWID>* redOrgs)
{
   __int64 redVal, yellowVal;
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wHour = 0;
   st.wMinute = 0;
   st.wSecond = 0;
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, (FILETIME*)&redVal);
   yellowVal = redVal + (__int64)10000000 * 3 * 24 * 3600;

   std::map<std::wstring, ROWID> readed;
   wchar_t buf[200];
   wsprintf(buf, L"WHERE payDate < %d%09d", (DWORD)(yellowVal / 1000000000), (DWORD)(yellowVal % 1000000000));
   PaymentImpl p;
   OrgImpl o;
   SQLTable t(p.Name());
   bool bdo = t.Select(&p, buf);
   while( bdo )
   {
      ROWID current = 0;
      if( p.sum > 0 && !IsStartDate(p.payDate) )
      {
         std::map<std::wstring, ROWID>::const_iterator fnd = readed.find(p.id);
         if( fnd == readed.end() )
         {
            o.id = p.id;
            o.Read();
            readed[o.id] = o.rid;
            current = o.rid;
         } else
            current = fnd->second;

         yellowOrgs->insert(current);
         if( *(__int64*)&p.payDate < redVal )
            redOrgs->insert(current);
      }
         
      bdo = t.SelectNext(&p);
   }
}

static COLORREF OrgColor(const ROWID &orgID, const std::set<ROWID> &ylwIds, const std::set<ROWID> &redIds, COLORREF defaultColor)
{
   if( orgID != NO_ROWID )
   {
      if( redIds.find(orgID) != redIds.end() ) return RGB(255,0,0);
      if( ylwIds.find(orgID) != ylwIds.end() ) return RGB(192, 192, 0);
   }
   return defaultColor;
}

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

struct OrgFoldersDataAdd : public OrgFoldersData
{
   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new OrgFoldersDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   OrgFoldersDataAdd()
   {
      LoadOrgs(&yellowOrgs, &redOrgs);
   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      COLORREF c = OrgColor(GetOID(index), yellowOrgs, redOrgs, defaultColor);
      if( c == defaultColor )
         return OrgFoldersData::GetItemColor(index, defaultColor, lvcd);
      return c;
   }

   virtual void RefreshCurrent()
   {
      OrgFoldersData::RefreshCurrent();
      yellowOrgs.clear();
      redOrgs.clear();
      LoadOrgs(&yellowOrgs, &redOrgs);
   }

   std::set<ROWID> yellowOrgs, redOrgs;
};

enum OrgShowEnum { oseAll, oseYlw, oseRed };
struct OrgListDataAdd : public OrgListData
{
   virtual OrgListData* Clone() const
   {
      OrgListData *od = new OrgListDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   OrgListDataAdd()
   {
      curView = oseAll;
      ylwSum = 0;
      redSum = 0;
      LoadOrgs(&yellowOrgs, &redOrgs);
   }

   virtual void Refresh()
   {
      OrgListData::Refresh();
      yellowOrgs.clear();
      redOrgs.clear();
      LoadOrgs(&yellowOrgs, &redOrgs);
      SetOrgFilter(curView);
   }

   virtual void SetDocType(const wchar_t *type)
   {
      OrgListData::SetDocType(type);
      LoadSums(type);
   }

   void LoadSums(const wchar_t *type)
   {
      ylwSum = 0;
      redSum = 0;
      OrgImpl oi;
      OrgSumImpl os;
      wchar_t buf[100];
      wsprintf(buf, L"WHERE type='%s'", type);
      SQLTable t(os.Name());
      bool bdo = t.Select(&os, buf);
      while( bdo )
      {
         oi.id = (wchar_t*)os.id;
         if( oi.Read() )
         {
            if( yellowOrgs.find(oi.rid) != yellowOrgs.end() )
               ylwSum += os.sum;
            if( redOrgs.find(oi.rid) != redOrgs.end() )
               redSum += os.sum;
         }
         bdo = t.SelectNext(&os);
      }

   }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      COLORREF c = OrgColor(GetOID(index), yellowOrgs, redOrgs, defaultColor);
      if( c == defaultColor )
         return OrgListData::GetItemColor(index, defaultColor, lvcd);
      return c;
   }

   virtual DWORD GetSum() const
   { 
      if( curView == oseAll )
         return docType->GetSum();

      return (curView == oseYlw) ? ylwSum : redSum;
   }

   void SetOrgFilter(OrgShowEnum ose)
   {
      if( rowsSave.size() == 0 )
         rowsSave = rows;

      rows = rowsSave;
      std::set<ROWID> *ref = (ose == oseRed) ? &redOrgs : (ose == oseYlw) ? &yellowOrgs : NULL;
      if( ref != NULL )
      {
         std::vector<ROWID>::iterator i = rows.begin();
         for( ; i != rows.end(); )
         {
            if( ref->find((*i)) == ref->end() )
               i = rows.erase(i);
            else
              i++;
         }
      }
      curView = ose;
   }

   OrgShowEnum curView;
   std::set<ROWID> yellowOrgs, redOrgs;
   std::vector<ROWID> rowsSave;
   DWORD ylwSum, redSum;
};

class OrgListAdd : public OrgList
{
public:
   OrgListAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST_ADD; }
   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }

   BEGIN_MSG_MAP(OrgListAdd)
      COMMAND_ID_HANDLER(IDC_ALL, ShowAll)
      COMMAND_ID_HANDLER(IDC_YLW_FILTER, ShowYlw)
      COMMAND_ID_HANDLER(IDC_RED_FILTER, ShowRed)
      CHAIN_MSG_MAP(OrgList)
   END_MSG_MAP()

   LRESULT ShowAll(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled) { ShowOrgs(oseAll); return 0; }
   LRESULT ShowYlw(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled) { ShowOrgs(oseYlw); return 0; }
   LRESULT ShowRed(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled) { ShowOrgs(oseRed); return 0; }

   void ShowOrgs(OrgShowEnum ose)
   {
      ((OrgListDataAdd*)data)->SetOrgFilter(ose);

      ListForm::Refresh();

      if( sumLabel.m_hWnd != NULL )
         sumLabel.SetSum(((OrgListData*)data)->GetSum());
   }

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)
};

IMPLEMENT_FORM(OrgListAdd)

void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new OrgFoldersDataAdd();
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

   OrgListData *od = new OrgListDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}

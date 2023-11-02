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

#include <Add.h>

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
   wsprintf(buf,  L"where payDate < %d%09d AND sumD > 0", val / 1000000000, val % 1000000000);
   //wsprintf(buf,  L"where payDate < %d%09d", val / 1000000000, val % 1000000000);

   IDs id;
   bool bdo = t.Select(&id, buf);
   while( bdo )
   {
      ids->insert(id.id);
      bdo = t.SelectNext(&id);
   }
}

static void SumToText(std::wstring* txt, long sumV)
{
   wchar_t buf[50], src[50];

   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);

   txt->assign(buf);
}

static __int64 CurTime()
{
   SYSTEMTIME st;
   FILETIME ft;
   GetLocalTime(&st);

   SystemTimeToFileTime(&st, &ft);
   return (__int64)ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
}

static DWORD OutDebs(const wchar_t *id)
{
   Sum outDeb;

   DeliveryImpl d;
   SQLTable tbl(d.Name());

   wchar_t buf[50];
   __int64 ct = CurTime();
   wsprintf(buf,  L"%d%09d", ct / 1000000000, ct % 1000000000);

   std::wstring q(L"SELECT SUM(sumD) FROM ");
   q += d.Name();
   q += L" WHERE paydate < ";
   q += buf;
   q += L" AND id='";
   q += id;
   q += L"'";

   outDeb.sum = 0;
   tbl.Select(q.c_str(), &outDeb);

   return outDeb.sum;
}

static bool ShowOrgInfo(const OrgImpl& org)
{
   std::wstring text, tval;
   DWORD outDeb = OutDebs(org.id);

   int plan = (int)org.plan;
   int fact = (int)org.fact;

   DWORD pc = (plan <= 0 || fact < 0) ? 0 : (DWORD)((__int64)fact * SUM_SCALE * SUM_SCALE / plan);

   OrgSumImpl os;
   os.GetSum(dtBalance, org.id);

   SumToText(&tval, plan);

   text = L"ПЛАН: ";
   text += tval;
   text += L" р.\nОТГР.: ";
   SumToText(&tval, fact);
   text += tval;
   text += L" р.\nВЫПОЛН.: ";
   SumToText(&tval, pc);
   text += tval;
   text += L" %\n\nДОЛГ: ";
   SumToText(&tval, os.sum);
   text += tval;
   text += L" р.\nПР.Д.: ";
   SumToText(&tval, outDeb);
   text += tval;
   text += L" р.";

   if( (org.flags & ofCheckRest) != 0 )
      text += L"\n\n\nСъем остатков";

   MessageBox(GetActiveWindow(), text.c_str(), L"Информация", MB_OK|MB_ICONINFORMATION);

   bool ret = true;
   if( outDeb > 0 )
   {
      if( MessageBox(GetActiveWindow(), 
         L"ДАННЫЙ КЛИЕНТ ЗАБЛОКИРОВАН , ЗАЯВКА БУДЕТ ПРИОСТАНОВЛЕННА В БАЗЕ ДО МОМЕНТА ЗАКРЫТИЯ ДОЛГА, ВЫ ТОЧНО ХОТИТЕ ПРОДОЛЖИТЬ СОСТАЛЕНИЕ ЗАКАЗА?",
         L"Вопрос",
         MB_YESNO|MB_ICONQUESTION) == IDNO )
      {
         ret = false;
      }
   }

   return ret;
}

static COLORREF OrgColor(const ROWID &orgID, const std::set<std::wstring> &ids, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd)
{
   if( orgID == NO_ROWID ) return defaultColor;

   OrgImpl org;
   org.Read(orgID);

   if( (org.flags & ofCheckRest) != 0 )
      lvcd->clrTextBk = RGB(210,210,210);

   if( ids.find(org.id) != ids.end() )
   {
      return RGB(255,0,0);
   }
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
      return OrgColor(GetOID(index), ids, defaultColor, lvcd);
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
   std::set<std::wstring> ids;
};

static int DayToInt(const wchar_t *day)
{
   if( !_wcsicmp(day, L"понедельник") ) return 0;
   if( !_wcsicmp(day, L"вторник") ) return 1;
   if( !_wcsicmp(day, L"среда") ) return 2;
   if( !_wcsicmp(day, L"четверг") ) return 3;
   if( !_wcsicmp(day, L"пятница") ) return 4;
   if( !_wcsicmp(day, L"суббота") ) return 5;
   //L"воскресенье"
   return 6;
}

struct SortDate
{
   mutable OrgFolderImpl of;

   bool operator()(TreeNode* _left, TreeNode* _right) const
   {
      of.Read(_left->id);
      int val = DayToInt(of.name);

      of.Read(_right->id);
      return (val < DayToInt(of.name));
   }
};

struct FoldersAdd : public OrgFoldersData
{
   std::set<std::wstring> ids;

   FoldersAdd()
   {
      LoadIDS(&ids);
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

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const
   {
      return (folders.size() > 0) ? defaultColor : OrgColor(GetOID(index), ids, defaultColor, lvcd);
   }

   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new FoldersAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }
   virtual void LoadTree()
   {
      OrgFoldersData::LoadTree();
      sort(root.childs.begin(), root.childs.end(), SortDate());
   }

   bool IsSelected(int index) const
   {
      if( IsTopLevel() && index < (int)folders.size() )
      {
         SYSTEMTIME st;
         GetLocalTime(&st);

         if( st.wDayOfWeek == 0 ) st.wDayOfWeek = 7;

         orgFolder.Read(folders[index], true, false);
         if( DayToInt(orgFolder.name) == st.wDayOfWeek - 1)
            return true;
      }
      return false;
   }
};

class OrgFoldersAdd : public OrgFolders
{
public:
   OrgFoldersAdd() {}

#ifdef Autopteka
   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS_ADD; }

   LRESULT OpenIncome(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenIncomes(((OrgFoldersData*)data)->GetDocType()->Type());
      return 0;
   }
#else
   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS; }
#endif

   virtual int ImageIndex(int index) const;

   BEGIN_MSG_MAP(OrgFoldersAdd)
      COMMAND_ID_HANDLER(IDC_CONTACTS, ChangeView)
#ifdef Autopteka
      COMMAND_ID_HANDLER(IDD_INCOME, OpenIncome)
#endif
      CHAIN_MSG_MAP(OrgFolders)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data)
   {
      CheckSync();
      return OrgFolders::SetData(_data);
   }

   LRESULT ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      Preference prf;
      prf.Load();
      prf.flags &= (~opfCalendarView);
      prf.Save();

      OpenOrgList(((OrgFoldersData*)data)->GetDocType()->Type());

      return 0;
   }

   DECLARE_FORM(OrgFoldersAdd, IDD_FLDORGS_ADD)
};

class OrgListAdd : public OrgList
{
public:
   OrgListAdd() {}

   BEGIN_MSG_MAP(OrgFoldersAdd)
      COMMAND_ID_HANDLER(IDC_CALENDAR, ChangeView)
#ifdef Autopteka
      COMMAND_ID_HANDLER(IDD_INCOME, OpenIncome)
#endif
      CHAIN_MSG_MAP(OrgList)
   END_MSG_MAP()

#ifdef Autopteka
   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST_ADD; }

   LRESULT OpenIncome(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      OpenIncomes(((OrgListData*)data)->GetDocType()->Type());
      return 0;
   }
#else
   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST; }
#endif

   virtual bool SetData(IFormData *_data)
   {
      CheckSync();
      return OrgList::SetData(_data);
   }

   LRESULT ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      Preference prf;
      prf.Load();

      prf.flags |= opfCalendarView;
      prf.Save();

      OpenOrgFolders(((OrgListData*)data)->GetDocType()->Type());
      return 0;
   }

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)
};

IMPLEMENT_FORM(OrgFoldersAdd)
IMPLEMENT_FORM(OrgListAdd)

int OrgFoldersAdd::ImageIndex(int index) const
{
   int i = OrgFolders::ImageIndex(index);
   if( i < 0 ) 
      return i;

   return (((FoldersAdd*)data)->IsSelected(index)) ? 3 : i;
}

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

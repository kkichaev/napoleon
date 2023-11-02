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

#include <DocType.h>

#include "OrgDocs.h"
#include "NplConfig.h"
#include "FormEntries.h"

#include <Invoice.h>
#include <InitDoc.h>

#include <Add.h>
#include <Task.h>
#include <Visit.h>
#include <OrgRmnts.h>

class OrgDocsAdd : public OrgDocsList
{
   static std::wstring leaveID;
   std::wstring curID;

 public:
   OrgDocsAdd() {}
   ~OrgDocsAdd() { leaveID = curID; }

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS_ADD; }

   BEGIN_MSG_MAP(OrgDocsAdd)
      COMMAND_ID_HANDLER(IDC_WARNING, Task)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

   virtual bool SetData(IFormData *_data);

 protected:
   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
      {
         if( nt != dtBalance )
         {
            ((OrgDocsListData*)data)->SetDocType(nt);
            Refresh();
         } else
            OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
      }
   }

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Task(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Right, L"Номер/Просрочено", L"flags", 50 },
   { ListFormData::Header::Right, L"Дата/Дата платежа", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма/Долг", L"sum", 50 },
};

struct PayItem : public IReflectableData
{
   const wchar_t *date;
   const wchar_t *flags;
   const wchar_t *sum;

   DECLARE_TYPE_REFLECTION(PayItem)
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, date)
   REGISTER_STRING_MEMBER(PayItem, flags)
   REGISTER_STRING_MEMBER(PayItem, sum)
END_TYPE_REFLECTION(PayItem)


struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring sum, date, num;
};

class PayForm : public OrgDocsList, public CCustomDraw<PayForm>
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS_ADD; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS_ADD; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual bool SetData(IFormData *_data);

   DECLARE_FORM(PayForm, IDD_PAY)

   BEGIN_MSG_MAP(PayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(CCustomDraw<PayForm>)      
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DWORD OnPrePaint(int idCtrl, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      if( idCtrl == IDC_CONTACTS ) return CDRF_DODEFAULT;
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
         OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
   }

protected:
   void LoadMenuBar(bool hideSIP);
   virtual void UpdateLayout(bool forceRecalc);

   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
}

int DateDiff(const FILETIME &ft1, const FILETIME &ft2)
{
   __int64 val1 = ft1.dwLowDateTime | (((__int64)ft1.dwHighDateTime) << 32);
   __int64 val2 = ft2.dwLowDateTime | (((__int64)ft2.dwHighDateTime) << 32);

   int val = (int)((val1 - val2) / ((__int64)1000000 * 24 * 3600));
   return val;
}

COLORREF PayData::GetItemColor(int index, COLORREF defaultColor) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL || !((BalanceDoc*)doc)->isDelivery ) return defaultColor;

   const Delivery &d = *(Delivery*)((BalanceDoc*)doc)->Data();

   int dateToPay = 0, arrearsDay = 0;
   if( d.payDate.dwHighDateTime != 0 )
   {
      SYSTEMTIME st;
      FILETIME ft;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft);
   
      dateToPay = DateDiff(d.payDate, d.date);
      arrearsDay = DateDiff(ft, d.date) - dateToPay;
   }

   if( arrearsDay > 0 ) return RGB(255, 0, 0);

   return defaultColor;
}

bool PayData::Get(IReflectableData* data, int index) const
{
   wchar_t buf[50], src[40];
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   if( !((BalanceDoc*)doc)->isDelivery  )
   {
      const Payment &p = *(Payment*)((BalanceDoc*)doc)->Data();
      num = p.number;

      SYSTEMTIME st;
      FileTimeToSystemTime(&p.date, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

      date = buf;

      long sumV = p.sum;
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);

      sum = buf;

      ((PayItem*)data)->sum = sum.c_str();
      ((PayItem*)data)->flags = num.c_str();
      ((PayItem*)data)->date = date.c_str();

      return true;
   }

   const DeliveryImpl &d = *(DeliveryImpl*)((BalanceDoc*)doc)->Data();
   num = d.number;

   int dateToPay = 0, arrearsDay = 0;
   if( d.payDate.dwHighDateTime != 0 )
   {
      SYSTEMTIME st;
      FILETIME ft;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft);

      dateToPay = DateDiff(d.payDate, d.date);
      arrearsDay = DateDiff(ft, d.date) - dateToPay;
      dateToPay--; // вычтем день накладной
   }

   num = d.number;
   if( arrearsDay > 0 )
   {
      wsprintf(buf, L"%d", arrearsDay);
      num += L"\n";
      num += buf;
   }


   SYSTEMTIME st;
   FileTimeToSystemTime(&d.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date = buf;
   if( dateToPay > 0 )
   {
      FileTimeToSystemTime(&d.payDate, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date += L'\n';
      date += buf;

      //wsprintf(buf, L"%d", dateToPay);
      //date += L'\n';
      //date += buf;
   }

   long sumV = d.Sum();
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);

   sum = buf;
   sum += L'\n';

   sumV = d.sumD;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum += buf;

   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->flags = num.c_str();
   ((PayItem*)data)->date = date.c_str();

   return true;
}

PayForm::PayForm()
{
}

DWORD PayForm::OnItemPrePaint(int idCtrl, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   if( idCtrl == IDC_CONTACTS ) return CDRF_DODEFAULT;

   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;

   lvcd->clrText = ((PayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec, listCtrl.GetTextColor());
   return CDRF_NOTIFYITEMDRAW;
}

void PayForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));

   const DocType *pt = docTypeManager.GetDocType(dtBalance);

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)pt->Type();
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   if( hideSIP )
   {
      sumLabel.CreateLabel(menuBar.m_hWnd);
      sumLabel.SetSum(((PayData*)data)->GetSum());
   }

   menuBar.EnableButton(IDC_ADD, FALSE);
}

void PayForm::UpdateLayout(bool forceRecalc)
{
   OrgDocsList::UpdateLayout(forceRecalc);
}

LRESULT PayForm::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL )
   {
      if( dt->Type() != dtBalance )
         OpenOrgDocs(((PayData*)data)->ID(), dt->Type());
   }
   return 0;
}

LRESULT PayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenOrgList(dtBalance);
   return 0;
}

bool PayForm::SetData(IFormData *_data)
{
   if( !OrgDocsList::SetDataEx(_data, 2) ) return false;

   LoadMenuBar(false);
   menuBar.EnableWindow(FALSE);
   OpenNote(m_hWnd, ((PayData*)data)->ID(), true);
   menuBar.EnableWindow(TRUE);
   //LoadMenuBar(true);

   return true;
}

//
//------------------------------------------ OrgDocsAdd ---------------------------------------------------------
//
bool OrgDocsAdd::SetData(IFormData *_data)
{
   if( !OrgDocsList::SetData(_data) ) return false;

   LoadMenuBar(false);
   menuBar.EnableWindow(FALSE);

   const wchar_t *cid = ((OrgDocsListData*)data)->ID();
   curID = cid;
   OpenNote(m_hWnd, cid, true);
   menuBar.EnableWindow(TRUE);
   //LoadMenuBar(true);

   return true;
}

LRESULT OrgDocsAdd::Task(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   TaskImpl::EditTask(((OrgDocsListData*)data)->ID(), true);
   return 0;
}

LRESULT OrgDocsAdd::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   const wchar_t *cid = ((OrgDocsListData*)data)->ID();
   int cmp = leaveID.compare(cid);
   curID.clear();

   if( cmp == 0 && TaskImpl::HaveTask(cid) )
   {
      MessageBox(L"Перед выходом из клиента надо отправить задачи", L"Ошибка", MB_OK|MB_ICONSTOP);
      TaskImpl::EditTask(((OrgDocsListData*)data)->ID(), true);

      return 0;
   }
   return OrgDocsList::Backing(nCode, id, hWnd, bHandled);
}

std::wstring OrgDocsAdd::leaveID;

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   CheckSync();

   if( type == dtOrder || type == dtDelivery || type == dtVisit || type == dtRemnants )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}

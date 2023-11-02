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

#include <Visit.h>
#include <StdFuncs.h>
#include "Incass.h"

const int INCASS_FLAG = 0x100;

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Номер/Агента", L"num", 50 },
   { ListFormData::Header::Right, L"Дата/Оплата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 },
};

struct PayItem : public IReflectableData
{
   const wchar_t *num;
   const wchar_t *date;
   long sum;

   DECLARE_TYPE_REFLECTION(PayItem)
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, num)
   REGISTER_STRING_MEMBER(PayItem, date)
   REGISTER_LONG_SCALE_MEMBER(PayItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(PayItem)


struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring num, sum, date;
   __int64 redVal, yellowVal;
};

class PayForm : public OrgDocsList, public CCustomDraw<PayForm>
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS_ADD; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuID() const { return 0; }

   DECLARE_FORM(PayForm, IDD_PAY)

   BEGIN_MSG_MAP(PayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(CCustomDraw<PayForm>)      
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 2); }

 protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

class OrgDocsAdd : public OrgDocsList
{
 public:
   OrgDocsAdd() {}
   ~OrgDocsAdd() {}

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() )
      {
         if( nt == dtOrder || nt == dtDelivery || nt == dtVisit )
         {
            ((OrgDocsListData*)data)->SetDocType(nt);
            Refresh();
         } else
            OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
      }
   }

   virtual BOOL EnableAddButton() const
   {
      if( ((OrgDocsListData*)data)->GetDocType()->Type() == dtIncass )
      {
         OrgImpl o;
         o.Read(((OrgDocsListData*)data)->OrgID());
         return ((o.flags & INCASS_FLAG) != 0) ? TRUE : FALSE;
      }
      return OrgDocsList::EnableAddButton();
   }

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wHour = 0;
   st.wMinute = 0;
   st.wSecond = 0;
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, (FILETIME*)&redVal);
   yellowVal = redVal + (__int64)10000000 * 3 * 24 * 3600;
}

COLORREF PayData::GetItemColor(int index, COLORREF defaultColor) const
{
   IDocument *doc = docList->Get(index);

   if( doc != NULL && !((BalanceDoc*)doc)->isDelivery )
   {
      const Payment &p = *((Payment*)((BalanceDoc*)doc)->Data());
      if( !IsStartDate(p.payDate) )
      {
         if( *(__int64*)&p.payDate < redVal )
            return RGB(255, 0, 0);
         if( *(__int64*)&p.payDate < yellowVal )
            return RGB(192, 192, 0);
      }
   }
   return defaultColor;
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL || ((BalanceDoc*)doc)->isDelivery ) return false;

   const Payment &d = *(Payment*)((BalanceDoc*)doc)->Data();

   num = d.number;
   num += L'\n';
   num += d.agent;

   wchar_t buf[50];
   SYSTEMTIME st;
   FileTimeToSystemTime(&d.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
   date = buf;
   if( !IsStartDate(d.payDate) )
   {
      date += L'\n';
      FileTimeToSystemTime(&d.payDate, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date += buf;
   }

   ((PayItem*)data)->num = num.c_str();
   ((PayItem*)data)->date = date.c_str();
   ((PayItem*)data)->sum = d.sum;

   return true;
}

PayForm::PayForm()
{
}

DWORD PayForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((PayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec, listCtrl.GetTextColor());
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT PayForm::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL && dt->Type() != dtBalance )
   {
      OpenOrgDocs(((OrgDocsListData*)data)->ID(), dt->Type());
   }

   return 0;
}

LRESULT PayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenOrgList(dtBalance);
   return 0;
}

static bool inForm = false;
void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( inForm )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else
   {
      inForm = true;
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
      inForm = false;
   }
}

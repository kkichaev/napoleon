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
#include <OrgRmnts.h>

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Номер", L"num", 50 },
   { ListFormData::Header::Right, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 },
};


struct PayItem : public IReflectableData
{
   const wchar_t *num;
   const wchar_t *date;
   const wchar_t *sum;

   DECLARE_TYPE_REFLECTION(PayItem)
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, num)
   REGISTER_STRING_MEMBER(PayItem, date)
   REGISTER_STRING_MEMBER(PayItem, sum)
END_TYPE_REFLECTION(PayItem)

struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id, const wchar_t *type);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }
   virtual const DataReflector& DataType() const { return PayItem().GetType(); }

   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring num, date, sum;
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
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(CCustomDraw<PayForm>)      
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

 protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);

   virtual bool SetData(IFormData *_data);
};

class OrgDocAdd : public OrgDocsList
{
public:
   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
      {
         if( nt != dtBalance && nt != dtDelivery )
         {
            ((OrgDocsListData*)data)->SetDocType(nt);
            Refresh();
         } else
            OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
      }
   }

   DECLARE_FORM(OrgDocAdd, IDD_ORG_DOCS_ADD)
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocAdd)

PayData::PayData(const wchar_t *id, const wchar_t *type) : OrgDocsListData(id, type)
{
}

COLORREF PayData::GetItemColor(int index, COLORREF defaultColor) const
{
   IDocument *doc = docList->Get(index);

   if( doc != NULL )
   {
      if( docType->Type() == dtDelivery || ((BalanceDoc*)doc)->isDelivery )
      {
         const Delivery* dlv = (docType->Type() == dtDelivery) ? (Delivery*)doc->Data() : (Delivery*)((BalanceDoc*)doc)->Data();

         SYSTEMTIME st;
         FILETIME ft;

         GetLocalTime(&st);
         SystemTimeToFileTime(&st, &ft);

         if( CompareFileTime(&ft, &(dlv->payDate)) > 0 && dlv->sumD > 0)
            return RGB(255, 0, 0);
      }
   }
   return defaultColor;
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   wchar_t buf[50], src[40];
   SYSTEMTIME st;
   FileTimeToSystemTime(&doc->Date(), &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
   date = buf;

   if( docType->Type() == dtDelivery || ((BalanceDoc*)doc)->isDelivery )
   {
      const DeliveryImpl* dlv = (docType->Type() == dtDelivery) ? (DeliveryImpl*)doc->Data() : (DeliveryImpl*)((BalanceDoc*)doc)->Data();

      num = dlv->number;
      //num += L'\n';
      //num += dlv->agent;

      FileTimeToSystemTime(&dlv->payDate, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date += L'\n';
      date += buf;

      long sumV = dlv->Sum();
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
      sum = buf;

      sum += L'\n';
      sumV = dlv->sumD;
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
      sum += buf;
   } else
   {
      num = doc->Description();

      long sumV = doc->Sum();
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
      sum = buf;
   }

   ((PayItem*)data)->date = date.c_str();
   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->num = num.c_str();

   return true;
}

PayForm::PayForm()
{
}

bool PayForm::SetData(IFormData *_data)
{
   if( ListForm::SetDataEx(_data, 2) == false )
      return false;

   OrgImpl org;
   org.Read(((OrgDocsListData*)_data)->OrgID());

#ifdef ORG_INFO
   contactData.SetContacts(org.contacts);

   CWindow address(GetDlgItem(IDC_ADDRESS_LABEL));
   address.SetWindowText(org.address);
#endif

   contactList.SubclassWindow(GetDlgItem(IDC_CONTACTS));
   SetupListCtrl(&contactList, 2, &contactData);

   CStatic title(GetDlgItem(IDC_ORG_TITLE));
   title.SetWindowTextW(org.name);

   LoadMenuBar(true); // call UpdateLayout internal

   return true;
}

DWORD PayForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((PayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec, listCtrl.GetTextColor());
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT PayForm::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   switch( ((NMTOOLBAR*)header)->iItem )
   {
   case IDC_VIEW_TYPE:
   {
      const DocType *dt = SelectDocType(&menuBar, m_hWnd);
      const wchar_t *type = dt->Type();
      if( dt != NULL && type != dtBalance && type != dtDelivery )
      {
         OrgImpl o;
         if( o.Read(((OrgDocsListData*)data)->OrgID()) )
            dt->OpenForm(o.id, NULL);
      } else
      {
         ((OrgDocsListData*)data)->SetDocType(type);
         Refresh();
      }
      break;
   }
   }

   return 0;
}

LRESULT PayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenOrgList(((PayData*)data)->GetDocType()->Type());
   return 0;
}

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( type == dtOrder || type == dtVisit || type == dtRemnants )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else if( type == dtBalance || type == dtDelivery )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID, type));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}

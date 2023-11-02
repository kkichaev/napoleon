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
#include <DoSync.h>

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

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring num, date;
};

class PayForm : public OrgDocsList
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
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

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

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   wchar_t buf[50];
   if( ((BalanceDoc*)doc)->isDelivery )
   {
      const Delivery &d = *(Delivery*)((BalanceDoc*)doc)->Data();

      num = d.number;

      SYSTEMTIME st;
      FileTimeToSystemTime(&d.date, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date = buf;
      date += L"\n";
      FileTimeToSystemTime(&d.payDate, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date += buf;

      ((PayItem*)data)->sum = d.sumD;
   } else
   {
      const Payment &p = *(Payment*)((BalanceDoc*)doc)->Data();

      num = p.number;

      SYSTEMTIME st;
      FileTimeToSystemTime(&p.date, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      date = buf;

      ((PayItem*)data)->sum = p.sum;
   }
   ((PayItem*)data)->num = num.c_str();
   ((PayItem*)data)->date = date.c_str();
   return true;
}

PayForm::PayForm()
{
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

static boolean inOpenDocs = false;
void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   std::wstring oid(orgID);
   orgID = oid.c_str();

   if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else if( inOpenDocs )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else
   {
      inOpenDocs = true;
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
   inOpenDocs = false;
}

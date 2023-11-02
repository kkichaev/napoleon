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

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Номер/дней", L"flags", 50 },
   { ListFormData::Header::Center, L"Накл/Оплата", L"date", 50 },
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

class PayForm : public OrgDocsList
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuID() const { return -1; }

   DECLARE_FORM(PayForm, IDD_PAY)

   BEGIN_MSG_MAP(PayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
         OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
   }

protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};


class OrgDocsAdd : public OrgDocsList
{
 public:
   OrgDocsAdd() {}

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

 protected:
   virtual void SetViewType(const DocType *newDT);
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
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
      if( p.payDelay != 0 )
      {
         wsprintf(buf, L"%d", p.payDelay);
         num += L'\n';
         num += buf;
      }

      SYSTEMTIME st;
      FileTimeToSystemTime(&p.date, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

      date = buf;
      if( p.dlvDate.dwHighDateTime != 0 )
      {
         FileTimeToSystemTime(&p.dlvDate, &st);
         GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

         date += L'\n';
         date += buf;
      }

      long sumV = p.dlvSum;
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);

      sum = buf;
      sum += L'\n';

      sumV = p.sum;
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
      sum += buf;

      ((PayItem*)data)->sum = sum.c_str();
      ((PayItem*)data)->flags = num.c_str();
      ((PayItem*)data)->date = date.c_str();
   }
   return true;
}


PayForm::PayForm()
{
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

void OrgDocsAdd::SetViewType(const DocType *newDT)
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

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( type == dtOrder || type == dtDelivery )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}


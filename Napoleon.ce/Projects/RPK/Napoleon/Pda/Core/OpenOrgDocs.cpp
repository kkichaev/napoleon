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

#include <DBImpl.h>
#include "OrgDocs.h"
#include "FormEntries.h"
#include <InitDoc.h>
#include <OrgRmnts.h>
#include <Add.h>

struct ODLData : public OrgDocsListData
{
   ODLData(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type) {}

   virtual void GetTitle(const Org &org, std::wstring *title)
   {
      title->assign(org.name);
      title->append(L"\nДог:");
      title->append(org.dogovor);
   }
};

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Дата/Номер", L"num", 50 },
   { ListFormData::Header::Right, L"Долг", L"sum", 50 },
   { ListFormData::Header::Right, L"Пр.долг", L"sumT", 50 },
};

struct PayItem : public IReflectableData
{
   const wchar_t *num;
   FILETIME date;
   long sum;
   long sumT;

   DECLARE_TYPE_REFLECTION(PayItem)
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, num)
   REGISTER_FILETIME_MEMBER(PayItem, date)
   REGISTER_LONG_SCALE_MEMBER(PayItem, sum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(PayItem, sumT, SUM_SCALE)
END_TYPE_REFLECTION(PayItem)


struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   DWORD OutSum() const { return outSum; }

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   virtual void GetTitle(const Org &org, std::wstring *title);

   mutable std::wstring num;
   DWORD outSum;
};

class PayForm : public OrgDocsList, public CCustomDraw<PayForm>
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_PAY; }
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

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
         OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
   }

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
         if( nt != dtBalance )
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

struct TSum : public IReflectableData
{
   DWORD sum;
   DECLARE_TYPE_REFLECTION(TSum)
};

BEGIN_TYPE_REFLECTION(TSum)
   REGISTER_ULONG_MEMBER(TSum, sum)
END_TYPE_REFLECTION(TSum)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
   TSum sum;
   PaymentImpl pay;

   SQLTable table(pay.Name());

   std::wstring stmt(L"select sum(outSum) from ");
   stmt += pay.Name();
   stmt += L" where id='";
   stmt += id;
   stmt += L"'";

   if( table.Select(stmt.c_str(), &sum) )
      outSum = sum.sum;
   else
      outSum = 0;
}

void PayData::GetTitle(const Org &org, std::wstring *title)
{
   title->assign(org.name);

   SYSTEMTIME st;
   GetLocalTime(&st);
   wchar_t buf[100];

   int cch = sizeof(buf)/sizeof(buf[0]);
   int wch = GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, cch);
   buf[wch-1] = L' ';
   GetTimeFormatW(LOCALE_USER_DEFAULT, TIME_NOSECONDS, &st, NULL, buf + wch, cch - wch - 1);

   title->append(L"\nИнформация на ");
   title->append(buf);

   wchar_t src[50];
   wcscpy(buf, L"\nДолг/Пр.долг: ");

   int bufLen = wcslen(buf);
   DWORD val = GetSum();
   ConvertScaling(src, val, SUM_SCALE);
   FormatScaling(src, buf + bufLen, sizeof(buf)/sizeof(buf[0]) - bufLen, val % SUM_SCALE, SUM_SCALE, false);

   wcscat(buf, L"/");
   bufLen = wcslen(buf);
   ConvertScaling(src, outSum, SUM_SCALE);
   FormatScaling(src, buf + bufLen, sizeof(buf)/sizeof(buf[0]) - bufLen, outSum % SUM_SCALE, SUM_SCALE, false);

   //wcscat(buf, L" руб");
   title->append(buf);
}

COLORREF PayData::GetItemColor(int index, COLORREF defaultColor) const
{
   IDocument *doc = docList->Get(index);
   if( doc != NULL && !((BalanceDoc*)doc)->isDelivery )
      return ((Payment*)((BalanceDoc*)doc)->Data())->color;

   return defaultColor;
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL || ((BalanceDoc*)doc)->isDelivery ) return false;

   const Payment &p = *(Payment*)((BalanceDoc*)doc)->Data();

   wchar_t buf[100];
   SYSTEMTIME st;

   FileTimeToSystemTime(&p.date, &st);
   int cch = sizeof(buf)/sizeof(buf[0]);
   int wch = GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, cch);

   num = buf;
   num.append(L"\n");
   num.append(p.number);

   ((PayItem*)data)->num = num.c_str();
   ((PayItem*)data)->sum = p.sum;
   ((PayItem*)data)->sumT = p.outSum;

   return true;
}

PayForm::PayForm()
{
}

bool PayForm::SetData(IFormData *_data)
{
   if( OrgDocsList::SetDataEx(_data, 2) == false )
      return false;

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
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL && dt->Type() != dtBalance )
   {
      OrgImpl o;
      if( o.Read(((OrgDocsListData*)data)->OrgID()) )
         dt->OpenForm(o.id, this);
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

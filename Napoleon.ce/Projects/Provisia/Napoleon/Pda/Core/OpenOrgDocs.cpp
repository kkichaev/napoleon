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

#include <ObjImpl.h>
#include "InitDoc.h"
#include "OrgDocs.h"
#include <OrgRmnts.h>
#include <Visit.h>
#include "AgentTask.h"
#include "Add.h"
#include "FormEntries.h"
#include "Refr.h"

ListFormData::Header payHeader[] = 
{
   { ListFormData::Header::Left, L"Номер", L"flags", 50 },
   { ListFormData::Header::Center, L"Накл/Оплата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма/Дней", L"sum", 50 },
   { ListFormData::Header::Right, L"Тип", L"type", 35 },
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, date)
   REGISTER_STRING_MEMBER(PayItem, flags)
   REGISTER_STRING_MEMBER(PayItem, sum)
   REGISTER_STRING_MEMBER(PayItem, type)
END_TYPE_REFLECTION(PayItem)


struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id);

   virtual const Header *GetHeader() const { return payHeader; }
   virtual int ColumnsCount() const { return sizeof(payHeader)/sizeof(payHeader[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   virtual bool Selecting(int index);

   COLORREF GetItemColor(int index) const;

   mutable std::wstring sum, date, num, type;
};

class PayForm : public OrgDocsList, public CCustomDraw<PayForm>
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS_ADD; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual bool SetData(IFormData *_data);

   DECLARE_FORM(PayForm, IDD_PAY)

   BEGIN_MSG_MAP(PayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(CCustomDraw<PayForm>)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
   {
      NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
      lvcd->clrTextBk = ((PayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec);
      if( lvcd->clrTextBk == 0 ) lvcd->clrTextBk = 0xFFFFFF;
      return CDRF_NOTIFYITEMDRAW;
   }
 protected:
   //void LoadMenuBar(bool hideSIP);
   //virtual void UpdateLayout(bool forceRecalc);

   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

IMPLEMENT_FORM(PayForm)

struct OrgDocsListDataAdd : public OrgDocsListData
{
   OrgDocsListDataAdd(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type)
   {
   }

   virtual const wchar_t* DocOrderField(const wchar_t* type) const
   {
      return (type == dtRfrDoc ) ? L"created" : L"date"; 
   }

};

class OrgDocsAdd : public OrgDocsList
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

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)
};

IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL || ((BalanceDoc*)doc)->isDelivery )
      return false;

   Payment&p = *((Payment*)((BalanceDoc*)doc)->Data());

   wchar_t buf[50], src[40];

   num = p.number;
   type = p.type;

   SYSTEMTIME st;
   FileTimeToSystemTime(&p.dlvDate, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date = buf;
   FileTimeToSystemTime(&p.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date += L'\n';
   date += buf;

   long sumV = p.sum;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum = buf;
   if( p.delay != 0 )
   {
      wsprintf(buf, L"%d", p.delay);
      sum += L'\n';
      sum += buf;
   }

   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->flags = num.c_str();
   ((PayItem*)data)->date = date.c_str();
   ((PayItem*)data)->type = type.c_str();

   return true;
}

bool PayData::Selecting(int index)
{
   return false;
}

COLORREF PayData::GetItemColor(int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc != NULL && !((BalanceDoc*)doc)->isDelivery )
      return ((Payment*)((BalanceDoc*)doc)->Data())->color;

   return 0;
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

bool PayForm::SetData(IFormData *_data)
{
   if( !OrgDocsList::SetDataEx(_data, 2) ) return false;
   
   return true;
}

static bool inOpenDoc = false;
void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   //if( type == dtOrder || type == dtDelivery || type == dtRemnants || type == dtProxy || 
   //   type == dtVisit || type == dtAgentTask || type == dtPPay || type == dtDocPay )
   if( inOpenDoc )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListDataAdd(orgID, type));
   else if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else
   {
      inOpenDoc = true;
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
   inOpenDoc = false;
}

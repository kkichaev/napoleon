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

#include "Visit.h"
#include "OrgDocs.h"
#include "InitDoc.h"
#include "FormEntries.h"

static ListFormData::Header payHeader[] = 
{
   { ListFormData::Header::Left, L"Номер", L"num", 50 },
   { ListFormData::Header::Right, L"Дата", L"date", 35 },
   { ListFormData::Header::Right, L"Долг", L"sum", 50 },
   { ListFormData::Header::Right, L"Сумма", L"dlvSum", 50 },
   { ListFormData::Header::Right, L"Просчка/Отсрочка", L"payDelay", 50 },
   { ListFormData::Header::Center, L"Проект", L"manager", 100 },
};

struct PayItem : public IReflectableData
{
   const wchar_t *num;
   DWORD sum;
   DWORD dlvSum;
   const wchar_t* payDelay;
   const wchar_t *manager;
   FILETIME date;

   DECLARE_TYPE_REFLECTION(PayItem)
};

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, num)
   REGISTER_ULONG_SCALE_MEMBER(PayItem, sum, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(PayItem, dlvSum, SUM_SCALE)
   REGISTER_FILETIME_MEMBER(PayItem, date)
   REGISTER_STRING_MEMBER(PayItem, payDelay)
   REGISTER_STRING_MEMBER(PayItem, manager)
END_TYPE_REFLECTION(PayItem)

struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   virtual const Header *GetHeader() const { return payHeader; }
   virtual int ColumnsCount() const { return sizeof(payHeader)/sizeof(payHeader[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring num, payDelay, manager;
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

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

 protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

class OrgDocsAdd : public OrgDocsList
{
public:
   OrgDocsAdd();

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

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

protected:
};

IMPLEMENT_FORM(OrgDocsAdd)
IMPLEMENT_FORM(PayForm)

PayData::PayData(const wchar_t *id) : OrgDocsListData(id, dtBalance)
{
}

bool PayData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL  )
      return false;


   num = doc->Description();
   ((PayItem*)data)->num = num.c_str();
   ((PayItem*)data)->date = doc->Date();
   ((PayItem*)data)->sum = doc->Sum();
   ((PayItem*)data)->dlvSum = 0;


   payDelay.clear();
   manager.clear();

   if( !((BalanceDoc*)doc)->isDelivery  )
   {
      const Payment &d = *(Payment*)((BalanceDoc*)doc)->Data();
      ((PayItem*)data)->dlvSum = d.dlvSum;

      wchar_t buf[100];
      wsprintf(buf, L"%d\n%d", d.payDelay, d.overDelay);
      payDelay = buf;

      manager = d.manager;
   }

   ((PayItem*)data)->payDelay = payDelay.c_str();
   ((PayItem*)data)->manager = manager.c_str();

   return true;
}

PayForm::PayForm()
{
}

bool PayForm::SetData(IFormData *_data)
{
   if( !SetDataEx(_data, 2) )
      return false;

   Sum s;
   PaymentImpl p;

   std::wstring q(L"SELECT SUM(dlvSum) as SUM FROM ");
   q += p.Name();
   q += L" WHERE sum <> 0 ";
   q += L" AND id='";
   q += ((PayData*)data)->ID();
   q += L"'";


   s.sum = 0;
   SQLTable tbl(p.Name());
   tbl.Select(q.c_str(), &s);

   wchar_t buf[100];
   wsprintf(buf, L"Сумма накладных %d.%02d руб.", s.sum / SUM_SCALE, s.sum % SUM_SCALE);

   CWindow wnd(GetDlgItem(IDC_INFO));
   wnd.SetWindowText(buf);

   return true;
}

void PayForm::UpdateLayout(bool forceRecalc)
{
   const int infoSize = 20;
   int docsTop;
   CRect rc;

   // надо установить размер до вызова UpdateLayout
   GetParent().GetClientRect(rc);
   int bottom = rc.Height() - infoSize;
   SetWindowPos(NULL, 0, 0, rc.right, rc.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);

   orgInfo.UpdateLayout(&docsTop, forceRecalc);
   
   rc.left = 0;
   rc.top = docsTop;
   if( rc.right < 500 )
      rc.right = 500;
   rc.bottom = bottom;

   listCtrl.SetLayout(forceRecalc, rc, data);

   sumLabel.UpdateLayout();

   CWindow wnd(GetDlgItem(IDC_INFO));
   wnd.SetWindowPos(NULL, 0, bottom, rc.right, infoSize, SWP_NOZORDER|SWP_NOOWNERZORDER);
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

OrgDocsAdd::OrgDocsAdd()
{
}

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   static bool inOpenOrg = false;
   if( type == dtBalance )
   {
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   } else if( !inOpenOrg )
   {
      inOpenOrg = true;
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   } else
   {
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   }
   inOpenOrg = false;

   //if( type == dtOrder || type == dtDelivery || type == dtVisit )
   //   _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   //else if( type == dtBalance )
   //   _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   //else
   //{
   //   const DocType *dt = docTypeManager.GetDocType(type);
   //   dt->OpenForm(orgID, NULL);
   //}
}

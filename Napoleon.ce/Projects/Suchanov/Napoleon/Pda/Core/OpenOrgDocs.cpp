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

#ifdef VISIT_DOC
#include <Visit.h>
#endif

#include <Invoice.h>
#include <InitDoc.h>

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

const wchar_t AllAgents[] = L"все";
struct PayData : public OrgDocsListData
{
   PayData(const wchar_t *id, const wchar_t *type);

   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   void SetAgent(const wchar_t* agent);
   void GetAgents(std::vector<std::wstring> *agents);
   virtual DWORD GetSum() const;

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
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS_ADD; }
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

struct Agent : public IReflectableData
{
   wchar_t* agent;
   DECLARE_TYPE_REFLECTION(Agent)
};

BEGIN_TYPE_REFLECTION(Agent)
   REGISTER_STRING_MEMBER(Agent, agent)
END_TYPE_REFLECTION(Agent)

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

         if( CompareFileTime(&ft, &(dlv->payDate)) > 0 )
            return RGB(255, 0, 0);
      }
   }
   return defaultColor;
}

void PayData::SetAgent(const wchar_t* agent)
{
   std::wstring whereStr;

   if( wcscmp(agent, AllAgents) != 0 )
   {
      whereStr += L"agent = '";
      whereStr += agent;
      whereStr += L"'";
   }

   delete docList;
   docType->GetDocuments(id.c_str(), &docList, whereStr.c_str(), L"date");
}

void PayData::GetAgents(std::vector<std::wstring> *agents)
{
   agents->push_back(AllAgents);

   SQLTable dlv(DeliveryImpl().Name());
   Agent a;
   std::wstring add(L"WHERE id='");
   add += id;
   add += L"'";

   bool bdo = dlv.Select(&a, add.c_str(), true);
   while( bdo )
   {
      agents->push_back(a.agent);
      bdo = dlv.SelectNext(&a);
   }
}

DWORD PayData::GetSum() const
{
   DWORD sum = 0;
   if( docList != NULL )
   {
      for( int i=docList->Count()-1; i>=0; i-- )
      {
         IDocument *doc = docList->Get(i);
         if( doc != NULL ) sum += doc->Sum();
      }
   }

   return sum;
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
      num += L'\n';
      num += dlv->agent;

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

   orgInfo.Init(((OrgDocsListData*)_data)->OrgID(), *this, IDC_ORG_TITLE, IDC_ADDRESS_LABEL, IDC_CONTACTS);

   LoadMenuBar(true); // call UpdateLayout internal

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = L"все";
   menuBar.SetButtonInfo(IDC_AGENT, &bi);

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
   case IDC_AGENT:
   {
      std::vector<std::wstring> agents;
      ((PayData*)data)->GetAgents(&agents);

      CRect menuBounds;
      menuBar.GetRect(IDC_AGENT, menuBounds);
      menuBar.ClientToScreen(menuBounds);
      HMENU hm = CreatePopupMenu();

      int ctr = 1;
      MENUITEMINFO mi;
      mi.cbSize = sizeof(mi);
      mi.fMask = 0;

      std::vector<std::wstring>::const_iterator i = agents.begin();
      for( ; i != agents.end(); i++ )
      {
         UINT flag = MF_STRING;
         std::wstring name(L"&");
         name += (*i);

         AppendMenu(hm, flag, ctr, name.c_str());
         ctr++;
      }

      int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN,  menuBounds.left, menuBounds.top, m_hWnd, NULL);
      DestroyMenu(hm);

      if( res > 0 )
      {
         const wchar_t* str = agents.at(res-1).c_str();
         ((PayData*)data)->SetAgent(str);

         Refresh();

         TBBUTTONINFO bi;
         bi.cbSize = sizeof(bi);
         bi.dwMask = TBIF_TEXT;
         bi.pszText = (LPWSTR)str;
         menuBar.SetButtonInfo(IDC_AGENT, &bi);
      }

      break;
   }
   case IDC_VIEW_TYPE:
   {
      const DocType *dt = SelectDocType(&menuBar, m_hWnd);
      if( dt != NULL )
      {
         const wchar_t *type = dt->Type();
         if( type != dtBalance && type != dtDelivery )
         {
            OrgImpl o;
            if( o.Read(((OrgDocsListData*)data)->OrgID()) )
               dt->OpenForm(o.id, NULL);
         } else
         {
            ((OrgDocsListData*)data)->SetDocType(type);
            Refresh();
         }
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
   if( type == dtOrder || type == dtVisit )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
   else if( type == dtBalance || type == dtDelivery )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID, type));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}

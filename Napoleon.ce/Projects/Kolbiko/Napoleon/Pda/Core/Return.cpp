/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Возвраты
 *
 *  ert   02/08/2010   creating
 */
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include <Invoice.h>
#include <StdFuncs.h>
#include <BaseDialog.h>
#include <NplConfig.h>
#include "Add.h"

class Selector : public IPriceSelect
{
public:
   Selector(OrderImpl* r)
   {
      if( r->rid == NO_ROWID )
         r->Write();
      rid = r->rid; 
   }

   virtual bool IsSelected(const wchar_t* id) const { return false; }
   virtual void Select(const wchar_t* id) {}
   virtual void Backing()
   {
      ReturnImpl *r = new ReturnImpl();
      r->Read(rid);
      r->EditDocument(0);
   }

   virtual bool CanSelect() const { return false; }
   virtual bool CanBacking() const { return true; }

protected:
   ROWID rid;
};

class ReturnData : public InvoiceDlvData
{
public:
   ReturnData(OrderImpl *_order, bool retToDocList) : InvoiceDlvData(_order, retToDocList) {}

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   //bool EditDetail();

   virtual bool Adding()
   {
      OrderImpl *co = order;
      order = NULL;
      SelectPriceItem(new Selector(co), co);
      return false;
   }
};

class ReturnForm : public Invoice
{
public:
   DECLARE_FORM(ReturnForm, IDD_RETURN)

   BEGIN_MSG_MAP(ReturnForm)
      CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }
};

IMPLEMENT_FORM(ReturnForm)

class ReturnDetail : public BaseDialog
{
public:
   ReturnDetail(ReturnImpl* _doc) : BaseDialog(IDD_RETURN), doc(_doc) {}

   ~ReturnDetail() { }

   BEGIN_MSG_MAP(ReturnDetail)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

protected:
   ReturnImpl* doc;
   StringHolder sh;

   struct OrderData
   {
      FILETIME date;
      FILETIME created;
   };

   typedef std::vector<OrderData> OrderList;
   OrderList orders;
};

LRESULT ReturnDetail::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   if( doc->IsExported() )
      DisableChilds();

   SYSTEMTIME st;
   FileTimeToSystemTime(&doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   GetDlgItem(IDC_REMARK).SetWindowText(doc->remark);

   const ::DocType* dt = docTypeManager.GetDocType(dtOrder);
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(doc->id, &orgDocs, L"", L"created") )
   {
      wchar_t buf[500];
      CComboBox cb(GetDlgItem(IDD_ORDER_LIST));

      for( unsigned i=0; i<orgDocs->Count(); i++ )
      {
         IDocument *d = orgDocs->Get(i);
         if( d != NULL )
         {
            OrderImpl *odoc = (OrderImpl*)d->Data();
            
            SYSTEMTIME st;
            DWORD sum = odoc->Sum();
            FileTimeToSystemTime(&odoc->date, &st);
            wsprintf(buf, L"Заказ %2d/%2d/%d Сумма %d.%02d р", st.wDay, st.wMonth, st.wYear, sum / SUM_SCALE, sum % SUM_SCALE);

            int index = cb.AddString(buf);
            if( CompareFileTime(&odoc->created, &doc->shedule) == 0 )
               cb.SetCurSel(index);

            OrderData od;
            od.date = odoc->date;
            od.created = odoc->created;

            orders.push_back(od);
         }
      }
   }
   delete orgDocs;

   return 0;
}

LRESULT ReturnDetail::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   MoveButtons(wdh, hgh);

   CRect bounds, bounds2;
   GetDlgItemRect(bounds, IDC_REMARK);
   GetDlgItemRect(bounds2, IDCANCEL);
   GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

   GetDlgItemRect(bounds, IDD_ORDER_LIST);
   GetDlgItem(IDD_ORDER_LIST).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

   return 0;
}

LRESULT ReturnDetail::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   bHandled = FALSE;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   SystemTimeToFileTime(&st, &doc->date);

   CComboBox cb(GetDlgItem(IDD_ORDER_LIST));
   int selected = cb.GetCurSel();
   if( selected >= 0 )
   {
      const OrderData& od = orders.at(selected);
      doc->shedule = od.created;
      doc->ordDate = od.date;
   }

   CWindow wnd(GetDlgItem(IDC_REMARK));
   int len = wnd.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(len* sizeof(wchar_t));
   wnd.GetWindowText(buf, len);
   if( wcscmp(doc->remark, buf) != 0 )
      doc->remark = doc->holder.Add(buf);

   return 0;
}

bool ReturnImpl::EditDetail()
{
   ReturnDetail rd(this);
   return (rd.DoModal() == IDOK);
}

void ReturnImpl::HiddenInit(const ROWID &orgID)
{
   ClearMembers(this);

   OrgImpl org;
   org.Read(orgID);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &date);

   id = holder.Add(org.id);
   rid = NO_ROWID;

   sumType = 0;
}

bool ReturnImpl::Init(const ROWID &orgID)
{
   HiddenInit(orgID);
   return EditDetail();
}

bool ReturnImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) == true )
   {
      SelectPriceItem(new Selector(this), this);
      return true;
   }

   return false;
}

bool ReturnImpl::CanRemove() const
{
   bool needDelete = false;
   int id = MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
   if( id == IDYES )
      needDelete = true;

   return needDelete;
}

void ReturnImpl::EditDocument(UINT retForm)
{
   _Module.GetFrame()->Load(IDD_RETURN, new ReturnData(this, (retForm != IDD_ORDER_LIST)));
}

ReturnImpl* ReturnImpl::GetAssociated(const OrderImpl& src)
{
   ReturnImpl *r = new ReturnImpl();
   SQLTable t(r->Name());

   SYSTEMTIME st;
   FILETIME ft1, ft2;
   FileTimeToSystemTime(&src.created, &st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &ft1);
   ft2 = ft1;
   *(__int64*)&ft2 += (__int64)24 * 3600 * 10000000;

   wchar_t buf[500];
   wsprintf(buf, L" WHERE id='%s' AND created >= %d%09d AND created <= %d%09d ORDER BY created", src.id, 
      (DWORD)(*(__int64*)&ft1 / 1000000000), (DWORD)(*(__int64*)&ft1 % 1000000000),
      (DWORD)(*(__int64*)&ft2 / 1000000000), (DWORD)(*(__int64*)&ft2 % 1000000000));

   std::vector<ROWID> rid;
   t.RIDList(&rid, buf);
   while( rid.size() > 0 )
   {
      r->Read(rid.front());
      if( CompareFileTime(&r->shedule, &src.created) == 0 )
         break;
      //if( r->IsDirty() )
      //   break;
      rid.erase(rid.begin());
   }

   if( rid.size() == 0 )
   {
      OrgImpl oi;
      oi.id = src.id;
      oi.Read();
      r->HiddenInit(oi.RID());
      r->shedule = src.created;
      r->ordDate = src.date;
   }

   return r;
}

//bool ReturnData::EditDetail()
//{
//   if( EditReturnDetail((ReturnImpl*)order))
//   {
//      order->Write();
//      return true;
//   }
//   return false;
//}

//LRESULT ReturnForm::ShowDetail(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
//{
//   LoadMenuBar(false);
//   if( ((ReturnData*)data)->EditDetail() )
//      Refresh();
//   LoadMenuBar(true);
//   return 0;
//}

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 },
};
const ListFormData::Header* ReturnData::GetHeader() const
{
   return header;
}

int ReturnData::ColumnsCount() const
{
   return sizeof(header) / sizeof(header[0]);
}

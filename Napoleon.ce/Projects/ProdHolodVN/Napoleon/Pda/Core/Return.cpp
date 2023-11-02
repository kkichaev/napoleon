/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Возвраты
 *
 *  ert   02/08/2010   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include "Add.h"
#include <Invoice.h>
#include <StdFuncs.h>
#include <BaseDialog.h>
#include <NplConfig.h>

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

   ~ReturnDetail() { FreeDogovors(); }

   BEGIN_MSG_MAP(ReturnDetail)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      COMMAND_HANDLER(IDC_SUPPL, CBN_SELCHANGE, SetDogovors)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   LRESULT SetDogovors(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      CComboBox cbx(GetDlgItem(IDC_SUPPL));
      int idx = cbx.GetCurSel();
      const wchar_t* scode = (const wchar_t*)cbx.GetItemDataPtr(idx);
   
      CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
      LoadDogovors(dogs, doc->id, scode, NULL);

      return 0;
   }
protected:
   ReturnImpl* doc;
   StringHolder sh;
};

LRESULT ReturnDetail::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   if( doc->IsExported() )
      DisableChilds();

   SYSTEMTIME st;
   FileTimeToSystemTime(&doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   GetDlgItem(IDC_DOC_NUMBER).SetWindowText(doc->retNum);

   if( (doc->params & ofCash) != 0 )
      CheckDlgButton(IDC_CACHE, BST_CHECKED);

   NapoleonConfig config;
   std::wstring val;
   config.ReadValue(&val, SUPPL_TYPE);
   LoadComboboxWithCode(val, IDC_SUPPL, doc->suplCode, L'\t', &sh);

   CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
   LoadDogovors(dogs, doc->id, doc->suplCode, doc->dogovor);

   GetDlgItem(IDC_REMARK).SetWindowText(doc->remark);

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

   GetDlgItemRect(bounds, IDC_SUPPL);
   GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

   GetDlgItemRect(bounds2, IDC_DOGOVORS);
   GetDlgItem(IDC_DOGOVORS).MoveWindow(bounds2.left, bounds2.top, wdh - bounds2.left - offset, bounds2.Height());

   return 0;
}

LRESULT ReturnDetail::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   bHandled = FALSE;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   SystemTimeToFileTime(&st, &doc->date);

   if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) doc->params |= ofCash;
   else doc->params &= (~ofCash);

   CWindow wnd(GetDlgItem(IDC_REMARK));
   int len = wnd.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(len* sizeof(wchar_t));
   wnd.GetWindowText(buf, len);
   if( wcscmp(doc->remark, buf) != 0 )
      doc->remark = doc->holder.Add(buf);

   wnd = GetDlgItem(IDC_DOC_NUMBER);
   len = wnd.GetWindowTextLength() + 1;
   buf = (wchar_t*)alloca(len* sizeof(wchar_t));
   wnd.GetWindowText(buf, len);
   if( wcscmp(doc->retNum, buf) != 0 )
      doc->retNum = doc->holder.Add(buf);

   CComboBox cbx(GetDlgItem(IDC_SUPPL));
   int idx = cbx.GetCurSel();
   if( idx >= 0 )
   {
      const wchar_t* scode = (const wchar_t*)cbx.GetItemDataPtr(idx);
      if( wcscmp(scode, doc->suplCode) )
         doc->suplCode = doc->holder.Add(scode);
   }

   CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
   int ct = dogs.GetCurSel();
   if( ct >= 0 )
   {
      ItemData* d = (ItemData*)dogs.GetItemDataPtr(ct);
      if( wcscmp(d->dog, doc->dogovor) )
         doc->dogovor = doc->holder.Add(d->dog);
   }

   return 0;
}

static void MakeDocNumber(std::wstring *num)
{
   wchar_t buf[100];
   DWORD nnum = 0;

   AgentPrefixImpl::GetPrefix(num);

   ReturnImpl pi;
   SQLTable table(pi.Name());
   if( table.Select(&pi, L"ORDER BY created DESC") )
   {
      const wchar_t* p = pi.retNum;
      if( wcslen(p) > num->size() )
      {
         p += num->size();
      } else
      {
         while( iswdigit(*p) == 0 && *p != L'\0')
         {
            //num->append(1, *p);
            p++;
         }
      }
      nnum = _wtoi(p);
   }


   wsprintf(buf, L"%04d", nnum + 1);
   num->append(buf);
}

bool ReturnImpl::EditDetail()
{
   ReturnDetail rd(this);
   return (rd.DoModal() == IDOK);
}

bool ReturnImpl::Init(const ROWID &orgID)
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

   std::wstring num;
   MakeDocNumber(&num);
   retNum = holder.Add(num.c_str());

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

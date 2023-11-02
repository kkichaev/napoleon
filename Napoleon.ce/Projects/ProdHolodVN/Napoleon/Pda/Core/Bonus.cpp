/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Бонусы - делаются на основе заявок
 *
 *  ert   03/02/2011   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include "Add.h"
#include <Invoice.h>
#include <StdFuncs.h>
#include "BaseDialog.h"
#include <NplConfig.h>
#include <SAnchor.h>

class BSelector : public IPriceSelect
{
public:
   BSelector(OrderImpl* r)
   {
      if( r->rid == NO_ROWID )
         r->Write();
      rid = r->rid; 
   }

   virtual bool IsSelected(const wchar_t* id) const { return false; }
   virtual void Select(const wchar_t* id) {}
   virtual void Backing()
   {
      BonusImpl *r = new BonusImpl();
      r->Read(rid);
      r->EditDocument(0);
   }

   virtual bool CanSelect() const { return false; }
   virtual bool CanBacking() const { return true; }

protected:
   ROWID rid;
};

class BonusForm : public Invoice
{
public:
   DECLARE_FORM(BonusForm, IDD_BONUS)

   BEGIN_MSG_MAP(BonusForm)
      //COMMAND_ID_HANDLER(IDC_DETAIL, ShowDetail)
      CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }
};

IMPLEMENT_FORM(BonusForm)

class BonusData : public InvoiceDlvData
{
public:
   BonusData(OrderImpl *_order, bool retToDocList) : InvoiceDlvData(_order, retToDocList) {}

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual bool Adding()
   {
      OrderImpl *co = order;
      order = NULL;
      SelectPriceItem(new BSelector(co), co);
      return false;
   }
};

class BonusDetail : public BaseDialog
{
   StaticAnchor units;
   UnitList list;

public:
   BonusDetail(BonusImpl* _doc) : BaseDialog(IDD_BONUS), doc(_doc) {}

   ~BonusDetail() { FreeDogovors(); }

   BEGIN_MSG_MAP(BonusDetail)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      COMMAND_HANDLER(IDC_SUPPL, CBN_SELCHANGE, SetDogovors)
      COMMAND_HANDLER(IDD_DELIVERY, BN_CLICKED, SetDlvDate)
      NOTIFY_HANDLER(IDC_UNIT_LIST, NM_CLICK, CheckAddressItem)
      COMMAND_HANDLER(IDC_UNIT_TEXT, STN_CLICKED, SetUnit)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      REFLECT_NOTIFICATIONS()
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   LRESULT SetDlvDate(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      GetDlgItem(IDC_ORDER_DATE).EnableWindow((IsDlgButtonChecked(IDD_DELIVERY) == BST_CHECKED) ? TRUE : FALSE);
      return 0;
   }

   LRESULT SetDogovors(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      CComboBox cbx(GetDlgItem(IDC_SUPPL));
      int idx = cbx.GetCurSel();
      const wchar_t* scode = (const wchar_t*)cbx.GetItemDataPtr(idx);
   
      CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
      LoadDogovors(dogs, doc->id, scode, NULL);

      return 0;
   }

   LRESULT SetUnit(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      list.UpdateLayout();
      list.BringWindowToTop();
      list.ShowWindow(SW_SHOW);

      return 0;
   }

   LRESULT CheckAddressItem( int id, LPNMHDR hdr, BOOL &bHandled)
   {
      list.ShowWindow(SW_HIDE);

      BringWindowToTop();
      int index = list.GetSelectedIndex();
      if( index < 0 )
         return 0;
      
      const wchar_t *c = (const wchar_t*)list.GetItemData(index);
      if( wcscmp(c, doc->dlvCode) )
      {
         doc->dlvCode = doc->holder.Add(c);

         OrgImpl o;
         o.id = (wchar_t*)c;
         o.Read();

         SetDlgItemText(IDC_UNIT_TEXT, o.name);
      }
      return 0;
   }

protected:
   BonusImpl* doc;
   StringHolder sh;
};

LRESULT BonusDetail::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   if( doc->IsExported() )
      DisableChilds();

   SYSTEMTIME st;
   FileTimeToSystemTime(&doc->date, &st);

   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   NapoleonConfig config;
   std::wstring val;
   config.ReadValue(&val, SUPPL_TYPE);
   LoadComboboxWithCode(val, IDC_SUPPL, doc->suplCode, L'\t', &sh);

   CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
   LoadDogovors(dogs, doc->id, doc->suplCode, doc->dogovor);

   GetDlgItem(IDC_REMARK).SetWindowText(doc->remark);

   OrgImpl org;
   org.id = doc->id;
   org.Read();

   if( wcscmp(doc->id, doc->dlvCode) )
   {
      org.id = doc->dlvCode;
      org.Read();
   }

   GetDlgItem(IDC_UNIT_TEXT).SetWindowText(org.name);
   units.SubclassWindow(GetDlgItem(IDC_UNIT_TEXT));

   list.SubclassWindow(GetDlgItem(IDC_UNIT_LIST));
   list.Init(org.ido, doc->dlvCode);
   list.ShowWindow(SW_HIDE);

   if( (doc->params & ofDelivery) != 0 )
      CheckDlgButton(IDD_DELIVERY, BST_CHECKED);
   else
      GetDlgItem(IDC_ORDER_DATE).EnableWindow(FALSE);

   return 0;
}

LRESULT BonusDetail::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
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

   GetDlgItemRect(bounds2, IDC_UNIT_TEXT);
   GetDlgItem(IDC_UNIT_TEXT).MoveWindow(bounds2.left, bounds2.top, wdh - bounds2.left - offset, bounds2.Height());

   return 0;
}

LRESULT BonusDetail::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   if( wID == IDOK )
   {
      SYSTEMTIME st;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      SystemTimeToFileTime(&st, &doc->date);

      if( IsDlgButtonChecked(IDD_DELIVERY) == BST_CHECKED ) doc->params |= ofDelivery;
      else doc->params &= (~ofDelivery);

      CWindow wnd(GetDlgItem(IDC_REMARK));
      int len = wnd.GetWindowTextLength() + 1;
      wchar_t *buf = (wchar_t*)alloca(len* sizeof(wchar_t));
      wnd.GetWindowText(buf, len);
      if( wcscmp(doc->remark, buf) != 0 )
         doc->remark = doc->holder.Add(buf);

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
   }
   EndDialog(m_hWnd, wID);
   return 0;
}

bool BonusImpl::EditDetail()
{
   BonusDetail rd(this);
   return (rd.DoModal() == IDOK);
}

bool BonusImpl::Init(const ROWID &orgID)
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

   params = ofDelivery;

   return EditDetail();
}

bool BonusImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) == true )
   {
      SelectPriceItem(new BSelector(this), this);
      return true;
   }

   return false;
}
bool BonusImpl::CanRemove() const
{
   bool needDelete = false;
   int id = MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
   if( id == IDYES )
      needDelete = true;

   return needDelete;
}

void BonusImpl::EditDocument(UINT retForm)
{
   _Module.GetFrame()->Load(IDD_BONUS, new BonusData(this, (retForm != IDD_ORDER_LIST)));
}

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 },
};
const ListFormData::Header* BonusData::GetHeader() const
{
   return header;
}

int BonusData::ColumnsCount() const
{
   return sizeof(header) / sizeof(header[0]);
}

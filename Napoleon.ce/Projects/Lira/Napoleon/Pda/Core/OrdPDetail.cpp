/*
* Copyright (C), 2007, Денис Мосягин
*
* Детали заказа
*
*  ert   07/02/2008   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <Exchange.h>
#include <ObjImpl.h>

#include "NplConfig.h"
#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "PropDialog.h"
#include "SAnchor.h"

class OrderPage : public PropPage
{
public:
   OrderPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}
   virtual void Save(OrderImpl *order) = 0;

   void DisableChilds()
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));
   }

   void ResizeWindow(UINT id, WORD width)
   {
      CWindow wnd(GetDlgItem(id));
      CRect rc;

      wnd.GetWindowRect(rc);
      ScreenToClient(rc);
      rc.right = width - offset;
      wnd.MoveWindow(rc);
   }

   void MoveButtons(WORD wdh, WORD hgh)
   {
      int bwdh = 0;
      CRect bounds;
      if( GetDlgItemRect(bounds, IDCANCEL) )
      {
         bwdh = bounds.Width();
         GetDlgItem(IDCANCEL).MoveWindow(offset, hgh - bounds.Height() - offset, bwdh, bounds.Height(), FALSE);      
      }

      if( GetDlgItemRect(bounds, IDOK) )
      {
         GetDlgItem(IDOK).MoveWindow(bwdh + 3*offset, hgh - bounds.Height() - offset, 
            bounds.Width(), bounds.Height(), FALSE);
      }
   }

   bool GetDlgItemRect(LPRECT r, int id)
   {
      CWindow w(GetDlgItem(id));
      
      if( w.m_hWnd == NULL ) return false;

      w.GetWindowRect(r);
      ScreenToClient(r);
      return true;
   }

   void LoadCombobox(const std::wstring &val, int id, int value, int start = 0)
   {
      CComboBox cbBox(GetDlgItem(id));

      std::wstring::size_type sp = 0;
      for( int i=start; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         if( ep == std::wstring::npos )
            cbBox.InsertString(i, val.substr(sp, ep).c_str());
         else
            cbBox.InsertString(i, val.substr(sp, ep - sp).c_str());

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
      cbBox.SetCurSel(value);
   }
};

class OrderDetailDialog : public PropDialog
{
public:
   OrderDetailDialog(OrderImpl *_order);

   ~OrderDetailDialog() {}

   bool OnOK()
   {
      std::vector<PropPage*>::iterator i = pages.begin();
      for( ;i != pages.end(); i++ )
         ((OrderPage*)(*i))->Save(order);

      return true; 
   }

   OrderImpl* Order() const { return order; }

protected:
   OrderImpl *order;
   std::wstring title;
};

class AddressList : public CWindowImpl<CListViewCtrl, CListViewCtrl>
{
public:
   AddressList()
   {
   }

   DECLARE_WND_CLASS(L"ADDR_LIST")

   BEGIN_MSG_MAP(AddressList)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
   END_MSG_MAP()

   void Init()
   {
      CRect rc;
      GetClientRect(rc);

      HFONT hf = GetFont();
      LOGFONT lf;
      GetObject(hf, sizeof(lf), &lf);
      lf.lfHeight *= 3;
      HFONT newFont = CreateFontIndirect(&lf);
      SetFont(newFont);

      InsertColumn(0, L"Название", LVCFMT_LEFT, rc.Width() - GetSystemMetrics(SM_CXVSCROLL));
      SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
      ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);

      SetFont(hf);
   }

   void UpdateLayout()
   {
      CRect rc;
      GetParent().GetClientRect(rc);
      MoveWindow(rc);

      GetClientRect(rc);
      SetColumnWidth(0, rc.Width());
  }

   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
   {
      LPDRAWITEMSTRUCT ds = (LPDRAWITEMSTRUCT)lParam;
      
      COLORREF textColor;
      HBRUSH bkBrsh;
      if( ds->itemState & ODS_SELECTED )
      {
         bkBrsh = GetSysColorBrush(COLOR_HIGHLIGHT);
         textColor = GetSysColor(COLOR_HIGHLIGHTTEXT);
      } else
      {
         bkBrsh = GetSysColorBrush(COLOR_WINDOW);
         textColor = GetSysColor(COLOR_WINDOWTEXT);
      }
      FillRect(ds->hDC, &ds->rcItem, bkBrsh);
      ::SetTextColor(ds->hDC, textColor);

      if( ds->itemState & ODS_FOCUS )
         DrawFocusRect(ds->hDC, &ds->rcItem);

      CRect textBounds(ds->rcItem);
      textBounds.InflateRect(-1, -1);

      wchar_t buf[500];
      GetItemText(ds->itemID, 0, buf, sizeof(buf)/sizeof(buf[0]));
      DrawText(ds->hDC, buf, -1, textBounds, DT_WORDBREAK | DT_SINGLELINE);

      HPEN cpen = ::CreatePen(PS_SOLID,0,RGB(192,192,192));
      SelectObject(ds->hDC, cpen);
      MoveToEx(ds->hDC, ds->rcItem.left, ds->rcItem.bottom-1, NULL);
      LineTo(ds->hDC, ds->rcItem.right, ds->rcItem.bottom-1);
      DeleteObject(cpen);
      return 0;
   }
protected:
};

class OrderPage1 : public OrderPage
{
public:
   OrderPage1() : OrderPage(IDD_ORDER_DETAIL, L"Основная") {}

   BEGIN_MSG_MAP(OrderDetailDialog)
      COMMAND_HANDLER(IDC_UNIT_TEXT_LABEL, STN_CLICKED, ShowUnits)
      NOTIFY_HANDLER(IDC_UNIT_LIST, NM_CLICK, CheckAddressItem);
      CHAIN_MSG_MAP(OrderPage)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   StaticAnchor unitAddress;
   AddressList  addressList;

protected:
   void LoadAddressList(Order* order, const Org& org)
   {
      addressList.SubclassWindow(GetDlgItem(IDC_UNIT_LIST));
      addressList.Init();
      addressList.ShowWindow(SW_HIDE);

      if( org.units.size() > 0 )
      {
         if( org.units.size() > 1 )
         {
            unitAddress.SubclassWindow(GetDlgItem(IDC_UNIT_TEXT_LABEL));
            unitAddress.ModifyStyle(0, SS_NOTIFY);
         }

         std::vector<OrgUnit>::const_iterator i = org.units.begin();

         selIndex = 0;
         if( order->unitCode == 0 )
            order->unitCode = i->id;

         int ctr = 0;
         for( ; i != org.units.end(); i++ )
         {
            LVITEM li = { 0 };
            li.mask = LVIF_TEXT | LVIF_PARAM;
            li.iItem = ctr++;
            li.lParam = i->id;
            li.pszText = i->name;
            addressList.InsertItem(&li);  

            if( i->id == order->unitCode )
               selIndex = ctr-1;
         }

         addressList.SetItemState(selIndex, LVIS_SELECTED, LVIS_SELECTED);
         OnSelChange();
      } else
      {
         CRect ub, remb;
         CWindow ua(GetDlgItem(IDC_UNIT_TEXT_LABEL));

         ua.ShowWindow(SW_HIDE);
         ua.GetWindowRect(ub);
         ScreenToClient(ub);
            
         CWindow remark(GetDlgItem(IDC_REMARK));
         remark.GetWindowRect(remb);
            
         ScreenToClient(remb);
         remark.MoveWindow(offset, ub.top, remb.Width(), remb.Height());

         selIndex = -1;
      }
   }

   virtual void Init()
   {
      OrderImpl *order = ((OrderDetailDialog*)owner)->Order();

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      orderTime.SetFormat(L"HH:mm");
      orderTime.SetSystemTime(GDT_VALID, &st);

      OrgImpl o;
      o.id = order->id;
      o.Read();
      SetWindowText(o.name);
      LoadAddressList(order, o);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

   }

   LRESULT CheckAddressItem( int id, LPNMHDR hdr, BOOL &bHandled)
   {
      addressList.ShowWindow(SW_HIDE);
      OnSelChange();
      return 0;
   }

   LRESULT ShowUnits(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      addressList.BringWindowToTop();
      addressList.SetItemState(selIndex, LVIS_SELECTED, LVIS_SELECTED);
      addressList.ShowWindow(SW_SHOW);
      return 0;
   }

   LRESULT OnSelChange()
   {
      wchar_t buf[500];
      int index = addressList.GetSelectedIndex();
      if( index < 0 )
         return 0;

      addressList.GetItemText(index, 0, buf, sizeof(buf)/sizeof(buf[0]));
      SetDlgItemText(IDC_UNIT_TEXT, buf);
      selIndex = index;
      return 0;
   }

   virtual void Sizing(WORD wdh, WORD hgh)
   {
      OrderPage::Sizing(wdh, hgh);

      MoveButtons(wdh, hgh);

      addressList.UpdateLayout();

      CRect bounds, bounds2;

      GetDlgItemRect(bounds, IDC_SUPPL);
      GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_COST_TYPE);
      GetDlgItem(IDC_COST_TYPE).MoveWindow(bounds.left, bounds2.top, wdh - bounds.left - offset, bounds.Height());
      
      CWindow address(GetDlgItem(IDC_UNIT_TEXT));                                      
      address.GetWindowRect(bounds);
      ScreenToClient(bounds);
      address.MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height()); 
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL )
         return;

      SYSTEMTIME st, st1;

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);

      st.wHour = st1.wHour;
      st.wMinute = st1.wMinute;
      st.wSecond = st1.wSecond;

      SystemTimeToFileTime(&st, &order->date);

      if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCash;
      else order->params &= (~ofCash);

      order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();
      order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

      CComboBox costs = GetDlgItem(IDC_COST_TYPE);
      int ct = costs.GetCurSel();
      if( order->sumType != ct )
      {
         WORD st = ct; //(WORD)costs.GetItemData(ct);
         if( order->items.size() > 0 && MessageBox(L"Пересчитать цену товара?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
            order->ChangeSumType(st);
         else
            order->sumType = st;
      }

      if( selIndex >= 0 )
         order->unitCode = addressList.GetItemData(selIndex);
      else
         order->unitCode = 0;
   }
protected:
   int selIndex;
};

class OrderPage2 : public OrderPage
{
public:
   OrderPage2() : OrderPage(IDD_DETAIL_PAGE2, L"Дополнительно") {}

   virtual void Init()
   {
      OrderImpl *order = ((OrderDetailDialog*)owner)->Order();

      if( order->IsExported() )
         DisableChilds();

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);
   }

   void GetDlgItemRect(CRect &bounds, UINT id)
   {
      GetDlgItem(id).GetWindowRect(bounds);
      ScreenToClient(bounds);
   }

   virtual void Sizing(WORD wdh, WORD hgh)
   {
      OrderPage::Sizing(wdh, hgh);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL )
         return;

      CWindow wnd(GetDlgItem(IDC_REMARK));
      int len = wnd.GetWindowTextLength();

      wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
      wnd.GetWindowText(buf, len+1);
      order->AssignRemark(buf);
      free(buf);
   }
};

OrderDetailDialog::OrderDetailDialog(OrderImpl *_order) : order(_order)
{
   //OrgImpl o;
   //o.id = order->id;
   //o.Read();
   //title = o.name;
   //SetTitle(title.c_str());

   AddPage(new OrderPage1());
   AddPage(new OrderPage2());
}

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

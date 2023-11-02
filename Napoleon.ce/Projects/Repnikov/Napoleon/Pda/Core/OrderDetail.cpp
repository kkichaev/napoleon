/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Детали заказа
 *
 *  ert   09/09/2007   creating
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

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "NplConfig.h"
#include "SAnchor.h"

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

class OrderDetailDialog : public BaseDialog//<IDD_ORDER_DETAIL>
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   typedef BaseDialog/*<IDD_ORDER_DETAIL>*/ BaseClass;   

   BEGIN_MSG_MAP(OrderDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      COMMAND_HANDLER(IDC_UNIT_TEXT_LABEL, STN_CLICKED, ShowUnits)
      NOTIFY_HANDLER(IDC_UNIT_LIST, NM_CLICK, CheckAddressItem);
      CHAIN_MSG_MAP(BaseClass)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   StaticAnchor unitAddress;
   AddressList  addressList;
   int selIndex;

protected:
   StringHolder holder;

   void LoadAddressList(const Org& org)
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
         if( *order->unitCode == L'\0' )
            order->unitCode = i->id;

         int ctr = 0;
         for( ; i != org.units.end(); i++ )
         {
            LVITEM li = { 0 };
            li.mask = LVIF_TEXT;
            li.iItem = ctr++;
            li.pszText = i->id;
            addressList.InsertItem(&li);  

            if( wcscmp(i->id, order->unitCode) == 0 )
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

         GetDlgItem(IDC_UNIT_TEXT).ShowWindow(SW_HIDE);
            
         CWindow remark(GetDlgItem(IDC_REMARK));
         remark.GetWindowRect(remb);
            
         ScreenToClient(remb);
         remark.MoveWindow(offset, ub.top, remb.Width(), remb.Height());

         selIndex = -1;
      }
   }

   void LoadOrgData()
   {
      OrgImpl org;
      org.id = order->id;
      if( org.Read() )
         LoadAddressList(org);
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);
      CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      orderTime.SetFormat(L"HH:mm");
      orderTime.SetSystemTime(GDT_VALID, &st);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      OrgImpl oi;
      oi.id = order->id;
      oi.Read();
      SetWindowText(oi.name);
      
      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      config.ReadValue(&val, WAREHOUSES);
      LoadComboboxWithCode(val, IDC_WAREHOUSE, order->warehouseCode, L'\t', &holder);
      if( *order->warehouseCode == '\0' )
      {
         CComboBox wh(GetDlgItem(IDC_WAREHOUSE));
         wh.SetCurSel(0);
      }

      LoadOrgData();
      return TRUE;
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

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;

      addressList.UpdateLayout();

      CWindow address(GetDlgItem(IDC_UNIT_TEXT));                                      
      address.GetWindowRect(bounds);
      ScreenToClient(bounds);
      address.MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height()); 

      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);
      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( wID == IDOK )
      {
         SYSTEMTIME st, st1;
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);
         st.wHour = st1.wHour;
         st.wMinute = st1.wMinute;
         st.wSecond = st1.wSecond;
         SystemTimeToFileTime(&st, &order->date);

         order->sumType = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         if( selIndex >= 0 )
         {
            wchar_t tbuf[300];
            addressList.GetItemText(selIndex, 0, tbuf, sizeof(tbuf)/sizeof(tbuf[0]));
            order->unitCode = order->holder.Add(tbuf);
         }
         else
            order->unitCode = L"";

         CComboBox wh(GetDlgItem(IDC_WAREHOUSE));
         int cs = wh.GetCurSel();
         if( cs >= 0 )
         {
            order->warehouseCode = order->holder.Add((const wchar_t*)wh.GetItemData(cs));
         }
      }

      return 0;
   }
protected:
   OrderImpl *order;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);  
   return (dlg.DoModal() == IDOK);
}

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

class OrderDetailDialog : public BaseDialog
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   typedef BaseDialog BaseClass;

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

protected:
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

   void LoadOrgData()
   {
      NapoleonConfig cfg;
      OrgImpl org;
      org.id = order->id;
      org.Read();

      std::wstring cost;
         
      order->sumType = org.costype;
      cfg.GetStringItem(&cost, COST_TYPE, org.costype);

      //if( order->sumType >= MAX_NUM_COST )
      //   order->sumType = MAX_NUM_COST-1;

      std::wstring costext(L"Цена: ");
      costext += cost;

      SetDlgItemText(IDC_COST_TEXT, costext.c_str());

      wchar_t buf[50];
      wsprintf(buf, L"Отсрочка: %d", org.delay);
      SetDlgItemText(IDC_DELAY, buf);

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

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      if( (order->params & ofCache) != 0 ) CheckDlgButton(IDC_CACHE, BST_CHECKED);
      if( (order->params & ofWaybill) != 0 ) CheckDlgButton(IDC_WAYBILL, BST_CHECKED);

      OrgImpl o;
      o.id = order->id;
      o.Read();
      SetWindowText(o.name);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

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

      addressList.UpdateLayout();

      CRect bounds, bounds2;
      CWindow suppl(GetDlgItem(IDC_SUPPL));

      suppl.GetWindowRect(bounds);
      ScreenToClient(bounds);
      suppl.MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      CWindow costText(GetDlgItem(IDC_COST_TEXT));
      CWindow delayText(GetDlgItem(IDC_DELAY));
      delayText.GetWindowRect(bounds2);
      ScreenToClient(bounds2);

      costText.GetWindowRect(bounds);
      ScreenToClient(bounds);

      int left = 2 * wdh / 5;
      costText.MoveWindow(left, bounds.top, wdh - left - offset, bounds.Height());
      delayText.MoveWindow(left, bounds2.top, wdh - left - offset, bounds2.Height());

      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);
      
      CWindow address(GetDlgItem(IDC_UNIT_TEXT));                                      
      address.GetWindowRect(bounds);
      ScreenToClient(bounds);
      address.MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height()); 
      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( wID == IDOK )
      {
         SYSTEMTIME st;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         SystemTimeToFileTime(&st, &order->date);

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();

         if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCache;
         else order->params &= (~ofCache);

         if( IsDlgButtonChecked(IDC_WAYBILL) == BST_CHECKED ) order->params |= ofWaybill;
         else order->params &= (~ofWaybill);

         if( selIndex >= 0 )
            order->unitCode = addressList.GetItemData(selIndex);
         else
            order->unitCode = 0;
      }

      return 0;
   }
protected:
   OrderImpl *order;
   int selIndex;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

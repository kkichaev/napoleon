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
#include <ObjImpl.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

#include <NplConfig.h>

class OrderDetailDialog : public BaseDialog//<IDD_ORDER_DETAIL>
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   //typedef BaseDialog<IDD_ORDER_DETAIL> BaseClass;
   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(OrderDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

protected:
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

#ifdef ORG_COST_TYPE
      bool canChange = false;
      if( config.ReadValue(&val, ALLOW_CHG_COST) && _wtoi(val.c_str()) == 1 )
         canChange = true;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      if( !canChange )
         GetDlgItem(IDC_COST_TYPE).EnableWindow(FALSE);
#else

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

#endif

      CComboBox discount(GetDlgItem(IDC_DISCOUNT));
      discount.AddString(L"<без скидки>");
      discount.SetItemData(0, 0);
      discount.SetCurSel(0);

      if( config.ReadValue(&val, DISCOUNT) )
         LoadComboboxWithCode(val, IDC_DISCOUNT, -order->discount, L'\t', 1);

      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

      GetDlgItemRect(bounds, IDC_SUPPL);
      GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_COST_TYPE);
      GetDlgItem(IDC_COST_TYPE).MoveWindow(bounds.left, bounds2.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_DISCOUNT);
      GetDlgItem(IDC_DISCOUNT).MoveWindow(bounds.left, bounds2.top, wdh - bounds.left - offset, bounds.Height());

      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( wID == IDOK )
      {
         SYSTEMTIME st;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);

         st.wHour = 0;
         st.wMinute = 0;
         st.wSecond = 0;

         SystemTimeToFileTime(&st, &order->date);

         if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCash;
         else order->params &= (~ofCash);

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();
         order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

         bool updateCost = false;

         CComboBox costs = GetDlgItem(IDC_COST_TYPE);
         int ct = costs.GetCurSel();
         if( order->sumType != ct )
         {
            WORD st = ct;
            order->sumType = st;
            updateCost = true;
         }

         CComboBox discount(GetDlgItem(IDC_DISCOUNT));
         ct = discount.GetCurSel();
         if( ct >= 0 )
         {
            ct = discount.GetItemData(ct);
            if( order->discount != -ct )
            {
               order->discount = -ct;
               updateCost = true;
            }
         }
         if( updateCost )
            UpdateCost();

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);
      }

      return 0;
   }

   void UpdateCost()
   {
      PriceImpl p;

      vector_t<OrderItem>::iterator oi = order->items.begin();
      while( oi != order->items.end() )
      {
         p.id = oi->id;
         p.Read();

         DWORD curCost = p.cost[order->sumType];
         curCost += ((int)curCost * order->discount / DISCOUNT_SCALE) / SUM_SCALE;
         oi->cost = curCost;

         oi++;
      }
   }
protected:
   OrderImpl *order;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

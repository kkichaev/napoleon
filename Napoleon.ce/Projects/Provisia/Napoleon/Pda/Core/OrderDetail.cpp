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

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "NplConfig.h"
#include <ObjImpl.h>


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

      CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      orderTime.SetFormat(L"HH:mm");
      orderTime.SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);
      CheckDlgButton((order->sendBefore != 0) ? IDC_TIME_START : IDC_TIME_END, BST_CHECKED);

      if( (order->params & ofNetCost) )
         CheckDlgButton(IDC_NET_COST, BST_CHECKED);
      SetScalingValue(IDC_DISCOUNT, order->discount, DISCOUNT_SCALE, false);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      //LoadCombobox(val, IDC_SUPPL, order->supplyer);
      LoadComboboxWithCode(val, IDC_SUPPL, order->supplCode, L'\t', &sh);

      config.ReadValue(&val, PAY_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      OrgImpl oi;
      oi.id = (wchar_t*)order->id;
      oi.Read();
      SetWindowText(oi.name);
      
      return TRUE;
   }

	LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
	{
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
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

         order->sendBefore = (IsDlgButtonChecked(IDC_TIME_START) == BST_CHECKED) ? 1 : 0;

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         wchar_t dbuf[20];
         GetDlgItemText(IDC_DISCOUNT, dbuf, sizeof(dbuf)/sizeof(dbuf[0]));
         order->discount = (WORD)GetValue(dbuf, DISCOUNT_SCALE);

         CComboBox cbs(GetDlgItem(IDC_SUPPL));
         int cs = cbs.GetCurSel();
         if( cs >= 0 )
         {
            order->supplCode = order->holder.Add((wchar_t*)cbs.GetItemDataPtr(cs));
         }

         order->sumType = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();

         if( IsDlgButtonChecked(IDC_NET_COST) == BST_CHECKED )
            order->params |= ofNetCost;
         else
            order->params &= (~ofNetCost);
      }

      return 0;
   }
protected:
   OrderImpl *order;
   StringHolder sh;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

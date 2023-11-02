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
#include <NplConfig.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

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
      COMMAND_ID_HANDLER(IDC_COST_DSC_LABEL, ChangeSign)
      COMMAND_RANGE_HANDLER(IDC_DIG_0, IDC_DIG_BS, OnDigPressed)

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

      if( order->params & ofBankPay )
         CheckDlgButton(IDC_BANK, BST_CHECKED);

      CEdit qty(GetDlgItem(IDC_DISCOUNT));
      qty.LimitText(5);
      SetScalingValue(IDC_DISCOUNT, order->discount, DISCOUNT_SCALE, false);
      qty.SetSel(0, -1);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

#ifdef ORG_COST_TYPE
      order->sumType = org.costype;            
#endif
      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect digB;
      GetDlgItemRect(digB, IDC_DIG_1);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, digB.left - 2*offset, bounds2.top - bounds.top - offset);

      /*
      int remBottom = bounds.top + bounds2.top - bounds.top - offset;

      int ctrls[] = {IDC_DIG_PT, IDC_DIG_0, IDC_DIG_BS };
      for( int i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
      {
         CRect b;
         GetDlgItemRect(b, ctrls[i]);
         GetDlgItem(ctrls[i]).MoveWindow(b.left, b.top, b.Width(), remBottom - b.top);
      }
      */
      return 0;
   }

   LRESULT OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      CEdit qty(GetDlgItem(IDC_DISCOUNT));
      qty.SetFocus();

      wchar_t nbuf[10];
      qty.GetWindowText(nbuf, sizeof(nbuf));

      int start, end;
      qty.GetSel(start, end);

      if( wID == IDC_DIG_BS )
      {
         if( start != 0 && start == end )
         {
            qty.SetSel(start-1, start);
            start--;
         }
         if( start != end )
            qty.ReplaceSel(L"");
         return 0;
      }

      if( start == end && start == 0 && *nbuf == L'-' )
         qty.SetSel(++start, start);

      wchar_t buf[2];
      buf[1] = L'\0';
      switch(wID)
      {
      case IDC_DIG_0:
         *buf = L'0';
         break;
      case IDC_DIG_1:
         *buf = L'1';
         break;
      case IDC_DIG_2:
         *buf = L'2';
         break;
      case IDC_DIG_3:
         *buf = L'3';
         break;
      case IDC_DIG_4:
         *buf = L'4';
         break;
      case IDC_DIG_5:
         *buf = L'5';
         break;
      case IDC_DIG_6:
         *buf = L'6';
         break;
      case IDC_DIG_7:
         *buf = L'7';
         break;
      case IDC_DIG_8:
         *buf = L'8';
         break;
      case IDC_DIG_9:
         *buf = L'9';
         break;
      case IDC_DIG_PT:
      {
         wchar_t *p = wcschr(nbuf, L'.');
         if( p == NULL ) p = wcschr(nbuf, L',');

         if( p == NULL ) 
            *buf = L'.';
         else
         {
            qty.SetSel(p + 1 - nbuf, p + 1 - nbuf);
            return 0;
         }
         break;
      }
      }
      qty.ReplaceSel(buf);
      return 0;
   }

   LRESULT ChangeSign(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      CEdit wnd(GetDlgItem(IDC_DISCOUNT));
      wchar_t buf[30];
      wnd.GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));

      if( *buf == L'-' )
      {
         wnd.SetWindowText(buf+1);
      } else
      {
         for( int i = wcslen(buf); i >= 0; i-- )
            buf[i+1] = buf[i];
         *buf = L'-';
         wnd.SetWindowText(buf);

         int start, end;
         wnd.GetSel(start, end);
         if( start == 0 && start == end )
            wnd.SetSel(1, 1);
      }
      wnd.SetFocus();
      return 0;
   }

   void UpdateCost()
   {
      PriceImpl p;

      vector_t<OrderItem>::iterator oi = order->items.begin();
      while( oi != order->items.end() )
      {
         p.id = oi->id;
         if( p.Read() )
         {
            DWORD curCost = p.cost[order->sumType];
            curCost += ((int)curCost * order->discount / DISCOUNT_SCALE) / SUM_SCALE;
            oi->cost = curCost;
         }

         oi++;
      }
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( wID == IDOK )
      {
         SYSTEMTIME st, st1;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);

         if( IsDlgButtonChecked(IDC_BANK) == BST_CHECKED ) order->params |= ofBankPay;
         else order->params &= (~ofBankPay);

         wchar_t dbuf[20];
         GetDlgItemText(IDC_DISCOUNT, dbuf, sizeof(dbuf)/sizeof(dbuf[0]));
         order->discount = (SHORT)GetValue(dbuf, DISCOUNT_SCALE);

         order->sumType = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();

         st.wHour = st1.wHour;
         st.wMinute = st1.wMinute;
         st.wSecond = st1.wSecond;

         SystemTimeToFileTime(&st, &order->date);

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         UpdateCost();

         FileTimeToSystemTime(&order->date, &st);
         int discount = order->discount;
         wchar_t bn = L'Н';
         wchar_t sign = L'+';
         if( discount < 0 )
         {
            discount = -discount;
            sign = '-';
         }

         if( order->params & ofBankPay ) bn = 'Б';

         wchar_t ttbuf[100];
         wsprintf(ttbuf, L"%d%c%02d.%d=%c=%02d.%02d.%d=%02d:%02d", order->sumType + 1, sign, discount/DISCOUNT_SCALE, 
            discount % DISCOUNT_SCALE, bn, st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute);
         order->costtype = order->holder.Add(ttbuf);
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

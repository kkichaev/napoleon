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

#include "EnterNumber.h"
#include "SAnchor.h"

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
      COMMAND_HANDLER(IDC_DISCOUNT, STN_CLICKED, ChangeDiscount)
      COMMAND_HANDLER(IDC_DELAY, STN_CLICKED, ChangeDelay)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

protected:
      StaticAnchor discount, delay;

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( order->IsExported() )
         DisableChilds();

      discount.SubclassWindow(GetDlgItem(IDC_DISCOUNT));
      discount.ModifyStyle(0, SS_NOTIFY);

      delay.SubclassWindow(GetDlgItem(IDC_DELAY));
      delay.ModifyStyle(0, SS_NOTIFY);

      SetScalingValue(IDC_DISCOUNT, order->discount, DISCOUNT_SCALE, false);

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      orderTime.SetFormat(L"HH:mm");
      orderTime.SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl o;
      o.id = order->id;
      o.Read();
      SetWindowText(o.name);

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

         if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCash;
         else order->params &= (~ofCash);

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();
         order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         UpdateCost();
      }

      return 0;
   }

   class ED : public EnterNumberT<IDD_ENTER_QTY, DISCOUNT_SCALE, false>
   {
   public:
      typedef EnterNumberT<IDD_ENTER_QTY, DISCOUNT_SCALE, false> BaseDialog;

      BEGIN_MSG_MAP(ED)
         COMMAND_ID_HANDLER(IDC_MINUS, ChangeSign)
         CHAIN_MSG_MAP(BaseDialog)
      END_MSG_MAP()

      LRESULT ChangeSign(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
      {
         CEdit wnd(GetDlgItem(IDD_ENTER_VALUE));

         int len = wnd.GetWindowTextLength();
         wchar_t *buf = (wchar_t*)alloca((len+2) * sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);

         wnd.SetSel(0, len);
         if( *buf == L'-' )
         {
            wnd.ReplaceSel(buf+1);
         } else
         {
            for( int i = len; i >= 0; i-- )
               buf[i+1] = buf[i];
            *buf = L'-';

            wnd.ReplaceSel(buf);
         }
         //wnd.SetFocus();
         return 0;
      }
   };

   LRESULT ChangeDiscount(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ED ev;
      ev.value = order->discount;
      if( ev.DoModal() == IDOK )
      {
         order->discount = (short)ev.value;
         SetScalingValue(IDC_DISCOUNT, order->discount, DISCOUNT_SCALE, false);
      }
      return 0;
   }

   class EV : public EnterNumberT<IDD_ENTER_VALUE, 1, true>
   {
   public:
      typedef EnterNumberT<IDD_ENTER_VALUE, 1, true> BaseDialog;

      EV() : limit(0) {}

      void LimitText(WORD limit)
      {
         this->limit = limit;
         
         if( m_hWnd )
         {
            CEdit edit(GetDlgItem(IDD_ENTER_VALUE));
            if( edit.m_hWnd != NULL )
               edit.LimitText(limit);
         }
      }

      virtual void Init()
      {   
         if( limit != 0 )
         {
            CEdit edit(GetDlgItem(IDD_ENTER_VALUE));
            edit.LimitText(limit);
         }
      }

      int limit;
   };

   LRESULT ChangeDelay(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      EV  ev;
      ev.LimitText(2);
      ev.value = order->delay;
      if( ev.DoModal() == IDOK )
      {
         order->delay = (short)ev.value;
         SetDlgItemInt(IDC_DELAY, order->delay);
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

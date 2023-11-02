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

#include "PropDialog.h"
#include <TopApp.h>
#include <ObjImpl.h>

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);

class OrderPage : public PropPage
{
public:
   OrderPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}
   virtual void Save(OrderImpl *order) = 0;

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

class OrderPage1 : public OrderPage
{
public:
   OrderPage1() : OrderPage(IDD_ORDER_DETAIL, L"Основная") {}

   BEGIN_MSG_MAP(OrderDetailDialog)
      COMMAND_HANDLER(IDC_SPEC_CND, CBN_SELCHANGE, CheckDiscount)
      CHAIN_MSG_MAP(OrderPage)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()


protected:
   LRESULT CheckDiscount(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      CComboBox cb(hWndCtl);
      GetDlgItem(IDC_DISCOUNT_VALUE).ShowWindow( ( cb.GetCurSel() != 0 ) ? SW_SHOW : SW_HIDE);
      return 0;
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
      //if( st.wHour || st.wMinute )
         orderTime.SetSystemTime(GDT_VALID, &st);
      //else
      //   orderTime.SetSystemTime(GDT_NONE, NULL);

      FileTimeToSystemTime(&order->pay, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).SetSystemTime(GDT_VALID, &st);
      CDateTimePickerCtrl payTime(GetDlgItem(IDC_PAY_TIME));
      payTime.SetFormat(L"HH:mm");
      //if( st.wHour || st.wMinute )
         payTime.SetSystemTime(GDT_VALID, &st);
      //else
      //   payTime.SetSystemTime(GDT_NONE, NULL);

      //GetDlgItem(IDC_REMARK).SetWindowText(order->remark);
      if( order->flags & ofTopicB )
         CheckDlgButton(IDC_TOPIC_B, BST_CHECKED);
      if( order->flags & ofDiscount )
         CheckDlgButton(IDC_DISCOUNT, BST_CHECKED);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, BANK);
      LoadCombobox(val, IDC_BANK, order->bank);
      
      config.ReadValue(&val, SPEC_TYPE);
      ((CComboBox)GetDlgItem(IDC_SPEC_CND)).InsertString(0, L"нет");
      LoadCombobox(val, IDC_SPEC_CND, order->specCondition, 1);

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      if( order->specCondition )
      {
         SetScalingValue(IDC_DISCOUNT_VALUE, order->discount, DISCOUNT_SCALE, false);
         GetDlgItem(IDC_DISCOUNT_VALUE).ShowWindow(SW_SHOW);
      } else
         GetDlgItem(IDC_DISCOUNT_VALUE).ShowWindow(SW_HIDE);
   }

	//LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
	//{
 //     WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

 //     MoveButtons(wdh, hgh);

 //     CRect bounds, bounds2;
 //     GetDlgItemRect(bounds2, IDCANCEL);
 //     return 0;
 //  }

   virtual void Save(OrderImpl *order)
   {
      SYSTEMTIME st, st1;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);
      st.wHour = st1.wHour;
      st.wMinute = st1.wMinute;
      st.wSecond = st1.wSecond;
      SystemTimeToFileTime(&st, &order->date);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).GetSystemTime(&st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_TIME)).GetSystemTime(&st1);
      st.wHour = st1.wHour;
      st.wMinute = st1.wMinute;
      st.wSecond = st1.wSecond;
      SystemTimeToFileTime(&st, &order->pay);

      if( IsDlgButtonChecked(IDC_TOPIC_B) == BST_CHECKED )
         order->flags |= ofTopicB;
      if( IsDlgButtonChecked(IDC_DISCOUNT) == BST_CHECKED )
         order->flags |= ofDiscount;

      order->bank = ((CComboBox)GetDlgItem(IDC_BANK)).GetCurSel();
      order->specCondition = ((CComboBox)GetDlgItem(IDC_SPEC_CND)).GetCurSel();
      order->sumType = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();
      order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();

      if( order->specCondition )
      {
         wchar_t buf[50];
         GetDlgItemText(IDC_DISCOUNT_VALUE, buf, sizeof(buf)/sizeof(buf[0]));
         order->discount = (short)GetValue(buf, DISCOUNT_SCALE);
      }
   }
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

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd != NULL )
      {
         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);
      }
   }
};

OrderDetailDialog::OrderDetailDialog(OrderImpl *_order) : order(_order)
{
   OrgImpl o;
   o.id = _order->id;
   o.Read();
   title = o.name;
   SetTitle(title.c_str());

   AddPage(new OrderPage1());
   AddPage(new OrderPage2());
}

bool EditOrderDetail(OrderImpl *order)
{
   TopApp::EnableDoneButton(true);

   OrderDetailDialog dlg(order);
   bool res = (dlg.DoModal() == IDOK);

   TopApp::EnableDoneButton(false);

   return res;
}
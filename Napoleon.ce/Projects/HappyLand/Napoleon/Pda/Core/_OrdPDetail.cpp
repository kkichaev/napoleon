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

#include <NplConfig.h>
#include "ObjImpl.h"

#include <Costs.h>

class OrderDetailDialog : public BaseDialog
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

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

      NapoleonConfig config;
      std::wstring val;

      FirmImpl f;
      CComboBox fcb(GetDlgItem(IDC_SUPPL));
      SQLTable ft(f.Name());
      bool bdo = ft.Select(&f, L"ORDER BY name");
      while( bdo )
      {
         int index = fcb.AddString(f.name);
         fcb.SetItemDataPtr(index, holder.Add(f.id));
         if( !wcscmp(order->supplCode, f.id) )
            fcb.SetCurSel(index);
         bdo = ft.SelectNext(&f);
      }

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      const CostManager::CostList& cl = CostManager::CostTypes();
      CComboBox ccb(GetDlgItem(IDC_COST_TYPE));
      CostManager::CostList::const_iterator ci = cl.begin();
      for( ; ci != cl.end(); ci++ )
      {
         wchar_t cbuf[100];
         int len = mbstowcs(cbuf, ci->name.c_str(), ci->name.size());
         cbuf[len] = L'\0';

         int index = ccb.AddString(cbuf);
         len = mbstowcs(cbuf, ci->id.c_str(), ci->id.size());
         cbuf[len] = L'\0';
         ccb.SetItemDataPtr(index, holder.Add(cbuf));
         if( !wcscmp(order->costType, cbuf) )
            ccb.SetCurSel(index);
      }

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

         int cs;
         CComboBox fcb(GetDlgItem(IDC_SUPPL));
         cs = fcb.GetCurSel();
         if( cs >= 0 )
            order->supplCode = order->holder.Add((const wchar_t*)fcb.GetItemData(cs));
         CComboBox ccb(GetDlgItem(IDC_COST_TYPE));
         cs = ccb.GetCurSel();
         if( cs >= 0 )
         {
            order->costType = order->holder.Add((const wchar_t*)ccb.GetItemData(cs));
            order->sumType = (WORD)CostManager::CostIndex(order->costType);
         }
         order->supplyer = 0;
         order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);
      }

      return 0;
   }
protected:
   OrderImpl *order;
   StringHolder holder;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

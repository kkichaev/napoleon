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

#ifdef COST_MANAGER
#include <Costs.h>
#endif

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

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      CComboBox cards(GetDlgItem(IDC_DOGOVORS));
      vector_t<Card>::const_iterator i = org.cards.begin();
      for( ; i != org.cards.end(); i++ )
      {
         int index = cards.AddString(i->name);
         cards.SetItemData(index, (DWORD)holder.Add(i->id));

         if( wcscmp(i->id, order->card) == 0 || (*order->card == L'\0' && org.cards.size() == 1) )
            cards.SetCurSel(index);
      }

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

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

      GetDlgItemRect(bounds2, IDC_DOGOVORS);
      GetDlgItem(IDC_DOGOVORS).MoveWindow(bounds2.left, bounds2.top, wdh - bounds2.left - offset, bounds2.Height());

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

         CComboBox card(GetDlgItem(IDC_DOGOVORS));
         int ct = card.GetCurSel();
         if( ct >= 0 )
         {
            const wchar_t* cid = (wchar_t*)card.GetItemData(ct);
            if( wcscmp(order->card, cid) != 0 )
               order->card = order->holder.Add(cid);
         }

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

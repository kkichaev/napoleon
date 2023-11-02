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

class OrderDetailDialog : public BaseDialog
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(OrderDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_DOGOVORS, CBN_SELCHANGE, OnChangeDogovor)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

protected:
   StringHolder holder;
   wchar_t* curDogovor;

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
      LoadComboboxWithCode(val, IDC_SUPPL, order->supplCode, L'\t', &holder);
      if( *order->supplCode == L'\0' ) ((CComboBox)GetDlgItem(IDC_SUPPL)).SetCurSel(0);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      config.ReadValue(&val, COST_TYPE);
      LoadComboboxWithCode(val, IDC_COST_TYPE, order->costCode, L'\t', &holder);
      if( *order->costCode == L'\0' ) ((CComboBox)GetDlgItem(IDC_COST_TYPE)).SetCurSel(0);

      CComboBox cb(GetDlgItem(IDC_VIEW_TYPE));
      cb.AddString(L"0");
      cb.AddString(L"1");
      cb.SetCurSel( ((order->params & ofCash) != 0) ? 1 : 0 );

      CComboBox dcb(GetDlgItem(IDC_DOGOVORS));
      vector_t<Dogovor>::const_iterator i = org.dogovors.begin();
      for( ; i != org.dogovors.end(); i++ )
      {
         int index = dcb.AddString(holder.Add(i->name));
         const wchar_t* number = i->number;
         dcb.SetItemData(index, (DWORD)holder.Add(number));
         if( wcscmp(number, order->dogovor) == 0 )
            dcb.SetCurSel(index);

         curDogovor = L"";
      }

      OnDogovorChanged();
      return TRUE;
   }

   void ChangeCurSel(UINT cbID, const wchar_t *code)
   {
      CComboBox cb(GetDlgItem(cbID));
      int sz = cb.GetCount() - 1;
      for( ; sz >= 0; sz-- )
      {
         wchar_t *p = (wchar_t*)cb.GetItemData(sz);
         if( p != NULL && wcscmp(p, code) == 0 )
         {
            cb.SetCurSel(sz);
            break;
         }
      }
   }

   LRESULT OnChangeDogovor(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      OnDogovorChanged();
      return 0;
   }

   void OnDogovorChanged()
   {
      CComboBox dcb(GetDlgItem(IDC_DOGOVORS));
      int index = dcb.GetCurSel();
      if( index < 0 ) return;
      curDogovor = (wchar_t*)dcb.GetItemData(index);

      OrgImpl oi;
      oi.id = order->id;
      oi.Read();

      if( (unsigned)index < oi.dogovors.size() )
      {
         Dogovor& d = oi.dogovors[index];
         if( *d.costType != L'\0' )
            ChangeCurSel(IDC_COST_TYPE, d.costType);

         if( *d.firm !=L'\0' )
            ChangeCurSel(IDC_SUPPL, d.firm);
      }
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

      GetDlgItemRect(bounds, IDC_DOGOVORS);
      GetDlgItem(IDC_DOGOVORS).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

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
         SYSTEMTIME st;
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         SystemTimeToFileTime(&st, &order->date);

         CComboBox cb(GetDlgItem(IDC_VIEW_TYPE));
         if( cb.GetCurSel() == 1 ) order->params |= ofCash;
         else order->params &= (~ofCash);

         int cs;
         CComboBox scb(GetDlgItem(IDC_SUPPL));
         cs = scb.GetCurSel();
         if( cs >= 0 ) order->supplCode = order->holder.Add((wchar_t*)scb.GetItemData(cs));

         CComboBox costs = GetDlgItem(IDC_COST_TYPE);
         cs = costs.GetCurSel();
         if( order->sumType != cs )
         {
            WORD st = cs;
            if( order->items.size() > 0 && MessageBox(L"Пересчитать цену товара?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
               order->ChangeSumType(st);
            else
               order->sumType = st;
         }

         order->costCode = order->holder.Add((wchar_t*)costs.GetItemData(cs));
         order->dogovor = order->holder.Add(curDogovor);

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
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

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

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      NapoleonConfig config;
      std::wstring val;

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      CListViewCtrl table(GetDlgItem(IDC_TABLE));
      table.SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
      table.ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);
      table.InsertColumn(0, L"Название", LVCFMT_LEFT, 100);
      table.InsertColumn(1, L"Значение", LVCFMT_LEFT, 100);

      int index = 0;
      vector_t<OrgProps>::const_iterator i = org.params.begin();
      for( ; i != org.params.end(); i++, index++ )
      {
         table.InsertItem(index, i->key);
         
         LVITEM item;
         item.iItem = index;
         item.iSubItem = 1;
         item.mask = LVIF_TEXT;
         item.pszText = i->value;
         table.SetItem(&item);
      }

      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds2, IDC_ORDER_DATE);
      GetDlgItemRect(bounds, IDC_TABLE);
      GetDlgItem(IDC_TABLE).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

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
         SYSTEMTIME st;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         SystemTimeToFileTime(&st, &order->date);

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

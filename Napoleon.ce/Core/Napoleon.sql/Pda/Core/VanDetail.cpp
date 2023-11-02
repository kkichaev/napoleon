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
#include "ObjImpl.h"


class OrderDetailDialog : public BaseDialog
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
      GetDlgItem(IDC_DOC_NUMBER).SetWindowText(order->docNum);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      CComboBox cb(GetDlgItem(IDC_FIRMS));
      FirmImpl fi;
      SQLTable table(fi.Name());
      bool res = table.Select(&fi, L" ORDER BY name");
      while( res )
      {
         int index = cb.AddString(fi.name);
         cb.SetItemDataPtr(index, sh.Add(fi.id));

         if( !wcscmp(order->supplCode, fi.id) )
            cb.SetCurSel(index);

         res = table.SelectNext(&fi);
      }

#ifdef ORG_COST_TYPE
      order->sumType = org.costype;            
#endif
      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_FIRMS);
      GetDlgItem(IDC_FIRMS).MoveWindow(bounds.left, bounds.top, wdh - offset - bounds.left, bounds.Height());

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
         wchar_t *buf = (wchar_t*)alloca((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);

         wnd = GetDlgItem(IDC_DOC_NUMBER);
         len = wnd.GetWindowTextLength();
         buf = (wchar_t*)alloca((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->docNum = order->holder.Add(buf);

         CComboBox cb(GetDlgItem(IDC_FIRMS));
         int idx = cb.GetCurSel();
         if( idx >= 0 )
         {
            wchar_t* code = (wchar_t*)cb.GetItemDataPtr(idx);
            if( wcscmp(code, order->supplCode) )
               order->supplCode = order->holder.Add(code);
         }
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

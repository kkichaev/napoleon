/*
* Copyright (C), 2007, ƒенис ћос€гин
*
* ƒетали заказа
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
#include "Add.h"
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
   int LoadCombobox(UINT id, const wchar_t* key, const wchar_t* selValue)
   {
      int sel = -1;

      CComboBox wh(GetDlgItem(id));
      std::wstring value;
      NapoleonConfig cfg;
      if( cfg.ReadValue(&value, key) )
      {
         std::wstring::size_type off = 0, nextOff = 0;
         int ctr = 0;
         while( true )
         {
            nextOff = value.find(SEP_SYM, off);
            std::wstring tval = value.substr(off, (nextOff != std::wstring::npos) ? 
                  nextOff - off : std::wstring::npos);

            std::wstring::size_type amp = tval.find(L'\t');
            if( amp != std::wstring::npos )
            {
               wh.AddString(tval.substr(0, amp).c_str());
               if( wcscmp(tval.substr(amp+1).c_str(), selValue) == 0 )
                  sel = ctr;
            }

            if( nextOff == std::wstring::npos )
               break;
            off = nextOff + 1;
            ctr++;
         }
      }

      if( sel >= 0 )
         wh.SetCurSel(sel);
      return sel;
   }

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

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

#ifdef ORG_COST_TYPE
      order->sumType = org.costype;            
#endif
      LoadCombobox(IDC_WAREHOUS, L"—клады", order->whCode);
      LoadCombobox(IDC_COST_TYPE, L"“ип÷ен", order->prcCode);
      SetCurrentWH(order);

      //CComboBox wh(GetDlgItem(IDC_WAREHOUS));
      //std::wstring value;
      //NapoleonConfig cfg;
      //if( cfg.ReadValue(&value, L"—клады") )
      //{
      //   std::wstring::size_type off = 0, nextOff = 0;
      //   int ctr = 0;
      //   while( true )
      //   {
      //      nextOff = value.find(SEP_SYM, off);
      //      std::wstring tval = value.substr(off, (nextOff != std::wstring::npos) ? 
      //            nextOff - off : std::wstring::npos);

      //      std::wstring::size_type amp = tval.find(L'\t');
      //      if( amp != std::wstring::npos )
      //         wh.AddString(tval.substr(0, amp).c_str());

      //      if( nextOff == std::wstring::npos )
      //         break;
      //      off = nextOff + 1;
      //      ctr++;
      //   }
      //}
      //wh.SetCurSel(SetCurrentWH(order));
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

      GetDlgItemRect(bounds, IDC_WAREHOUS);
      GetDlgItem(IDC_WAREHOUS).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds, IDC_COST_TYPE);
      GetDlgItem(IDC_COST_TYPE).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());
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

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)alloca((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);

         order->whCode = L"";
         int idx = ((CComboBox)GetDlgItem(IDC_WAREHOUS)).GetCurSel();
         if( idx >= 0 )
         {
            std::wstring value;
            NapoleonConfig cfg;
            if( cfg.GetStringItem(&value, L"—клады", idx) )
            {
               std::wstring::size_type amp = value.find(L'\t');
               if( amp != std::wstring::npos )
                  order->whCode = order->holder.Add(value.substr(amp+1).c_str());
            }
         }

         idx = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();
         if( idx >= 0 )
         {
            std::wstring value;
            NapoleonConfig cfg;
            if( cfg.GetStringItem(&value, L"“ип÷ен", idx) )
            {
               std::wstring::size_type amp = value.find(L'\t');
               if( amp != std::wstring::npos )
                  order->prcCode = order->holder.Add(value.substr(amp+1).c_str());
               order->sumType = idx;
            }
         }
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

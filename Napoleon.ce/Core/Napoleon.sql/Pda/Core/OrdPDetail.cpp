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

//#ifdef FIRMS_TABLE
//      FirmsImpl f;
//      CComboBox fcb(GetDlgItem(IDC_SUPPL));
//      SQLTable ft(f.Name());
//      bool bdo = ft.Select(&f, L"ORDER BY name");
//      while( bdo )
//      {
//         int index = fcb.AddString(f.name);
//         fcb.GetItemDataPtr(index, holder.Add(f.id));
//         if( !wcscmp(order->supplyer, f.id) )
//            fcb.SetCurSel(index);
//         bdo = ft.SelectNext(&f);
//      }
//#else
      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);
//#endif

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);


#ifdef COST_MANAGER
      const CostManager::CostList& cl = CostManager::CostTypes();
      CComboBox ccb(GetDlgItem(IDC_COST_TYPE));
      int i = 0;
      wchar_t name[300];
      CostManager::CostList::const_iterator ci = cl.begin();
      for( ; ci != cl.end(); ci++,i++ )
      {
         mbstowcs(name, ci->name.c_str(), strlen(ci->name.c_str()) + 1);
         int index = ccb.AddString(name);
         ccb.SetItemData(index, i);
         if( i == order->sumType)
            ccb.SetCurSel(index);
      }
#elif ORG_COST_TYPE
      bool canChange = false;
      if( config.ReadValue(&val, ALLOW_CHG_COST) && _wtoi(val.c_str()) == 1 )
         canChange = true;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      if( !canChange )
         GetDlgItem(IDC_COST_TYPE).EnableWindow(FALSE);
#else

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

#endif

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

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();
         order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

         CComboBox costs = GetDlgItem(IDC_COST_TYPE);
         int ct = costs.GetCurSel();
#ifdef COST_MANAGER
         ct = costs.GetItemData(ct);
#endif
         if( order->sumType != ct )
         {
            WORD st = ct; //(WORD)costs.GetItemData(ct);
            if( order->items.size() > 0 && MessageBox(L"Пересчитать цену товара?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
               order->ChangeSumType(st);
            else
               order->sumType = st;
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

#if defined(FIRMS_TABLE) || defined(COST_MANAGER)
   StringHolder holder;
#endif
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

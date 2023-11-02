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

#include <exchange.h>
#include <ceint.h>
#include <Table.h>
#include <Sync.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "NplConfig.h"

class DeliveryDetailDialog : public BaseDialog
{
public:
   DeliveryDetailDialog(DeliveryImpl *_doc) : BaseDialog(IDD_DELIVERY_DETAIL), delivery(_doc) {}

   //typedef BaseDialog<IDD_ORDER_DETAIL> BaseClass;
   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(DeliveryDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

protected:
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( delivery->CanEdit() == false )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&delivery->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(delivery->remark);
      GetDlgItem(IDC_DOC_NUMBER).SetWindowText(delivery->number);

      OrgImpl oi;
      if( oi.Read(delivery->id) )
         SetWindowText(oi.name);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, delivery->costType);

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

      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( wID == IDOK )
      {
         SYSTEMTIME st;
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);

         ResetTime(&st);
         SystemTimeToFileTime(&st, &delivery->date);

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();
         len++;
         wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
         wnd.GetWindowText(buf, len);
         delivery->AssignRemark(buf);

         CWindow wnd1(GetDlgItem(IDC_DOC_NUMBER));
         len = wnd1.GetWindowTextLength();
         len++;
         buf = (wchar_t*)alloca(len * sizeof(wchar_t));
         wnd1.GetWindowText(buf, len);
         delivery->AssignNumber(buf);

         DWORD ct = ((CComboBox)GetDlgItem(IDC_COST_TYPE)).GetCurSel();
         if( ct != delivery->costType )
         {
            if( MessageBox(L"Пересчитать цены в документе?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
            {
               SyncPrice sp;
               CEDBFormat format(sp);
               CETable table(format);
               table.Open(sp.FileName());

               vector_t<DeliveryItem>::iterator i = delivery->items.begin();
               for( ;i != delivery->items.end(); i++ )
               {
                  PriceImpl p;
                  p.ReadTable(table, i->id);
                  DWORD cost = (p.cost.size() > ct) ? p.cost[ct] : 0;
                  i->sum = ItemSum(cost, i->qty);
               }               
            }
         }
         delivery->costType = ct;
      }

      return 0;
   }
protected:
   DeliveryImpl *delivery;
};

bool DeliveryImpl::EditDeliveryDetail(DeliveryImpl *doc)
{
   DeliveryDetailDialog dlg(doc);
   return (dlg.DoModal() == IDOK);
}

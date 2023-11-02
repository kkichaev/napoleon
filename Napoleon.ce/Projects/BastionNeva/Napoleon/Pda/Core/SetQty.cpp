/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества, custom
 *
 *  ert   17/08/2007   creating
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include <MainFrame.h>
#include <Table.h>

class QtyDialog : public CQTYDialog
{
 public:
   QtyDialog(QTYData *data) : CQTYDialog(data)
   {
   }

 protected:
   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      limit = price.limit;
      if( limit == 0 ) limit = QTY_SCALE;

      SetScalingValue(IDC_MIN_LOAD, limit, QTY_SCALE, true);
   }

   virtual void MoveControls(WORD wdh, WORD hgh)
   {
      CRect rc1, rc2;

      GetDlgItemRect(rc1, IDC_DISCOUNT);
      GetDlgItemRect(rc2, IDC_MIN_LOAD_LABEL);

      GetDlgItem(IDC_MIN_LOAD_LABEL).MoveWindow(rc1.left, rc2.top, rc2.right - rc1.left, rc2.Height());

      GetDlgItemRect(rc1, IDC_DISCOUNT_VALUE);
      GetDlgItemRect(rc2, IDC_MIN_LOAD);

      GetDlgItem(IDC_MIN_LOAD).MoveWindow(rc1.right - rc2.Width(), rc2.top, rc1.right, rc2.Height());
   }
 
   // если осталось одна коробка и меньше - не выписываем товар
   virtual bool CheckQty()
   {
      if( data->qty != 0 && (int)data->qty < limit )
      {
         MessageBox(L"Товар не будет выписан!\nЗаказ меньше минимальной отгрузки", L"Предупреждение", MB_OK | MB_ICONSTOP);
         return false;
      }

      return CQTYDialog::CheckQty();
   }

   int limit;
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QtyDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

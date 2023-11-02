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

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *data) : CQTYDialog(data) {}

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      DWORD uc = data->cost;
      if( price.weight > 0 )
         uc = (__int64)uc * WEIGHT_SCALE / price.weight;

      SetScalingValue(IDC_UNIT_TEXT, uc, SUM_SCALE, false);
   }

   virtual void MoveControls(WORD wdh, WORD hgh)
   {
      CQTYDialog::MoveControls(wdh, hgh);

      CRect rc1, rc2;

      GetDlgItemRect(rc1, IDC_INPACK_LABEL);
      GetDlgItemRect(rc2, IDC_UNIT_TEXT_LABEL);
      GetDlgItem(IDC_UNIT_TEXT_LABEL).MoveWindow(rc1.left, rc2.top, rc2.right - rc1.left, rc2.Height());

      GetDlgItemRect(rc1, IDC_INPACK);

      GetDlgItemRect(rc2, IDC_UNIT_TEXT);
      GetDlgItem(IDC_UNIT_TEXT).MoveWindow(rc1.right - rc2.Width(), rc2.top, rc1.right, rc2.Height());
   }
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

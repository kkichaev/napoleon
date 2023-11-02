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
#include <SAnchor.h>

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *data) : CQTYDialog(data)
   {
      flags = 0;
   }

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      itemQty = price.itemQty;
      if( itemQty == 0 ) itemQty = QTY_SCALE;
      cost = (DWORD)((__int64)price.cost[data->costType] * QTY_SCALE / itemQty);
      SetScalingValue(IDC_DISCOUNT_VALUE, cost, SUM_SCALE, false);
   }

protected:
   DWORD cost;
   DWORD itemQty;
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

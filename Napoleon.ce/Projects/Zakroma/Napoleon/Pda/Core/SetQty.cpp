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
   // если осталось одна коробка и меньше - не выписываем товар
   virtual bool CheckQty()
   {
      int check = priceQty + prevQty - qtyInPack;
      if( check < 0 ) check = 0;
      if( (int)data->qty > check )
      {
         data->qty = check;
         if( check == 0 )
            MessageBox(L"Остаток меньше одной коробки, товар не будет выписан", L"Предупреждение", MB_OK);
         else
            MessageBox(L"Количество в заказе уменьшено", L"Предупреждение", MB_OK);
      }

      return true;
   }
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QtyDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

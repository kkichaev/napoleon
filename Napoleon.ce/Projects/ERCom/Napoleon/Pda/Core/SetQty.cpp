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
   int maxDsc, maxMargin;
public:
   QTYDialog(QTYData *_data) : CQTYDialog(_data), maxDsc(0), maxMargin(0)
   {
      ConfigImpl ci;
      ci.key = (wchar_t*)L"МаксимальнаяСкидкаНаКПК";
      if( ci.Read() )
         maxDsc = GetValue(ci.value, DISCOUNT_SCALE);

      ci.key = (wchar_t*)L"МаксимальнаяНаценкаНаКПК";
      if( ci.Read() )
         maxMargin = GetValue(ci.value, DISCOUNT_SCALE);
   }

   bool CanSetDiscount(int dsc)
   {
      if((dsc <0 && -dsc < maxDsc) || (dsc >= 0 && dsc < maxMargin))
         return true;

      ::MessageBox(NULL, (dsc < 0) ? L"Значение скидки больше максимальной" : L"Значение наценки больше максимальной",
         L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
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

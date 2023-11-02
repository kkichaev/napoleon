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

#include <TopApp.h>

class QTYAdd : public CQTYDialog
{
public:
   QTYAdd(QTYData *data) : CQTYDialog(data)
   {
   }



   //virtual DWORD PriceQty(const Price &price) const
   //{
   //   if( price.qty.size() == 0 ) return 0;

   //   if( (int)price.qty.size() < saveWh ) return price.qty[0];
   //   return price.qty[saveWh].qty;
   //}
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   //if( saveWh == 0 )
   //{
   //   Preference p;
   //   if( p.Load() ) saveWh = p.whDefault;
   //}

   TopApp::EnableDoneButton(true);

   QTYAdd dlg(data);
   int code = dlg.DoModal();

   TopApp::EnableDoneButton(false);

   SetFocus(oldFocus);
   return (code == IDOK);
}

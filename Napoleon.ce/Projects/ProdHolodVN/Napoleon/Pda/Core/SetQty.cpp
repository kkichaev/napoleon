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
   BEGIN_MSG_MAP(QTYDialog)
     COMMAND_HANDLER(IDC_DISCOUNT_VALUE, STN_CLICKED, ChangeNac)
     CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

   QTYDialog(QTYData *data) : CQTYDialog(data) {}

   LRESULT ChangeNac(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      return 0;
   }

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      SetDlgItemText(IDC_UNIT_TEXT, price.unitName);
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

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
   QTYDialog(QTYData *_data) : CQTYDialog(_data) {}

   virtual DWORD GetStartQty(bool inPack, int qip) { return 0; }
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

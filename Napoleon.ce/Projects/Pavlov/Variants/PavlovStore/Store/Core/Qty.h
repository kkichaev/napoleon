/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   17/08/2007   creating
 */
#ifndef __QTY_H
#define __QTY_H

#include <Form.h>
#include "FormEntries.h"
#include "BaseDialog.h"
#include "SAnchor.h"
#include "NumInput.h"
#include "ObjImpl.h"
#include "RADrawer.h"

#include <atlcrack.h>

class CQTYDialog : public BaseDialog
{
public:
   CQTYDialog(const wchar_t* name, int qty = QTY_SCALE);

   BEGIN_MSG_MAP(CQTYDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseDialog)

   END_MSG_MAP()

   int GetQty() const { return qty; }

protected:
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

protected:
   int qty;
   const wchar_t* name;
};

#endif

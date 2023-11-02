/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества
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

const DWORD QTY_FLAGS = DEFAULT_FLAGS;

CQTYDialog::CQTYDialog(const wchar_t *name, int qty) : BaseDialog(IDD_QTY, QTY_FLAGS)
{
   flags &= (~ShowSIP);
   this->name = name;
   this->qty = qty / QTY_SCALE;
}

LRESULT CQTYDialog::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   if( wID == IDOK )
   {
      std::wstring buf;
      GetString(&buf, GetDlgItem(IDC_QTY));
      qty = GetValue(buf.c_str(), QTY_SCALE);
   }

   bHandled = FALSE;
   return 0;
}

LRESULT CQTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   SetDlgItemInt(IDC_QTY, qty);
   SetDlgItemText(IDC_ITEM_NAME, name);

   return TRUE;
}


bool SetQTY(QTYData *data)
{
   return false;
}
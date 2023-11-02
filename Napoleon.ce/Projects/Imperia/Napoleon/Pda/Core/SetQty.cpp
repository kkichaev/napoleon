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
   QTYDialog(QTYData *_data) : CQTYDialog(_data), setOnlyInPack(false) {}

   BEGIN_MSG_MAP(QTYDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_PACK, BN_CLICKED, CheckPack)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT CheckPack(WORD code, WORD id, HWND hWnd, BOOL& bHandled);

   bool setOnlyInPack;
};

LRESULT QTYDialog::CheckPack(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   if( !setOnlyInPack ) bHandled = FALSE;
   else CheckDlgButton(IDC_PACK, BST_CHECKED);

   return 0;
}

LRESULT QTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   PriceImpl pData;
   pData.id = (wchar_t*)data->id.c_str();
   pData.Read();

   setOnlyInPack = (pData.flags & 1);
   if( setOnlyInPack )
      data->flags |= oiInPack;

   SetScalingValue(IDC_COST1, pData.cost[1], SUM_SCALE, false);
   return 0;
}

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

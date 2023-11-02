/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   16/04/2008   creating
 */
#ifndef __ENTER_QTy_H
#define __ENTER_QTY_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>

class EnterQty : public CSimpleDialog<IDD_ENTER_QTY, TRUE>
{
public:
   EnterQty() { value = 0; inPack = false; hideInPack = false; }

   void HideInPack() { hideInPack = true; inPack = false; }

   DWORD value;
   bool inPack;
   bool hideInPack;

   typedef CSimpleDialog<IDD_ENTER_QTY, TRUE> BaseClass;

   BEGIN_MSG_MAP(EnterQty)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      COMMAND_RANGE_HANDLER(IDC_DIG_0, IDC_DIG_BS, OnDigPressed)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled);
   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

protected:
   void SetScalingValue(int id, int value, DWORD scale, bool hideRest);
   DWORD GetValue(int id, DWORD scale);
};


#endif

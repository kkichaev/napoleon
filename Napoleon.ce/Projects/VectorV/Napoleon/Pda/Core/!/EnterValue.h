/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   22/01/2009   creating
 */
#ifndef __ENTER_VALUE_H
#define __ENTER_VALUE_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>

class EnterValue : public CSimpleDialog<IDD_ENTER_QTY, TRUE>
{
public:
   EnterValue(WORD scale, bool isSigned);

   DWORD value;

   typedef CSimpleDialog<IDD_ENTER_QTY, TRUE> BaseClass;

   BEGIN_MSG_MAP(EnterValue)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      COMMAND_ID_HANDLER(IDC_MINUS, ChangeSign)
      COMMAND_RANGE_HANDLER(IDC_DIG_0, IDC_DIG_BS, OnDigPressed)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled);
   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT ChangeSign(WORD code, WORD id, HWND hWnd, BOOL& bHandled);

   void LimitText(WORD limit);

protected:
   void SetScalingValue(int id, int value, DWORD scale, bool hideRest);
   DWORD GetValue(int id, DWORD scale);

   void SetChildFont();

   WORD scale, limit;
   bool isSigned;
};


#endif

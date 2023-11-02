/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   24/08/2007   creating
 */ 
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <NapoleonRes.h>
#include <Password.h>

LRESULT Password::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = false;
   CEdit edit((CEdit)GetDlgItem(IDC_PWD));
   edit.LimitText(MAX_PASSWORD);
   //edit.SetFocus();

   if( IsSquareScreen() == false )
      SHSipPreference(m_hWnd, SIP_UP);

   return TRUE;
}

LRESULT Password::OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   bHandled = false;

   wchar_t pwd[MAX_PASSWORD];
   int len = GetDlgItemText(IDC_PWD, pwd, MAX_PASSWORD);
   password.assign(pwd, len);

   return 0;
}

LRESULT Password::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   CRect bounds;
   GetDlgItemRect(bounds, IDOK);

   GetDlgItem(IDOK).MoveWindow(bounds.left, hgh - bounds.Height() - offset, 
                               bounds.Width(), bounds.Height(), FALSE);

   bounds.OffsetRect(bounds.Width() + 5, 0);
   GetDlgItem(IDCANCEL).MoveWindow(bounds.left, hgh - bounds.Height() - offset, 
                               bounds.Width(), bounds.Height(), FALSE);

   return 0;
}

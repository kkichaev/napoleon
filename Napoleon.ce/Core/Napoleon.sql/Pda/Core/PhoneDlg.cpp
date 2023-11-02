/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Диалог позвонить/отправить SMS
 *
 *  ert   25/11/2008   creating
 */
#include "stdafx.h"
#include "PhoneDlg.h"

PhoneDlg::PhoneDlg(const wchar_t *number) : BaseDialog(IDC_PHONE)
{
   this->number = number;
}

LRESULT PhoneDlg::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
#ifdef WIN32_PLATFORM_PSPC
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   int bwdh = 0, btop = hgh, pbottom = nTitleHeight + 2;
   CRect bounds;
   if( GetDlgItemRect(bounds, IDC_CALL) )
   {
      btop = hgh - bounds.Height() - offset;
      bwdh = bounds.Width();
      GetDlgItem(IDC_CALL).MoveWindow(offset, btop, bwdh, bounds.Height(), FALSE);      
   }

   if( GetDlgItemRect(bounds, IDC_SMS) )
   {
      if( btop == hgh )
         btop = hgh - bounds.Height() - offset;
      GetDlgItem(IDC_SMS).MoveWindow(bwdh + 3*offset, btop, 
         bounds.Width(), bounds.Height(), FALSE);
   }

   GetDlgItem(IDC_TEXT).MoveWindow(offset, pbottom+offset, wdh-2*offset, btop - pbottom - 2 * offset, FALSE);
#endif
   return 0;
}

LRESULT PhoneDlg::OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   bHandled = FALSE;

   wchar_t buf[500];
   wsprintf(buf, L"Телефон %s", number);
   //SetDlgItemText(IDC_PHONE, buf);

   SetWindowText(buf);
   return FALSE;
}

LRESULT PhoneDlg::Close(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   EndDialog(m_hWnd, wID);
   SHSipPreference(m_hWnd, SIP_DOWN);

   CWindow w(GetDlgItem(IDC_TEXT));
   int wl = w.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(wl * sizeof(wchar_t));
   w.GetWindowText(buf, wl);

   text = buf;
   return FALSE;
}

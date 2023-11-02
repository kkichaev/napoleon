/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   16/04/2008   creating
 */
#include "stdafx.h"
#include <Module.h>
#include "EnterQty.h"

LRESULT EnterQty::OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
{
   bHandled = FALSE;

   SetScalingValue(IDC_QTY, value, QTY_SCALE, true);

   if( hideInPack )
   {
      CWindow w(GetDlgItem(IDC_PACK));
      CRect rc, bounds;

      w.GetWindowRect(rc);
      GetWindowRect(bounds);
      bounds.bottom = rc.top;

      w.ShowWindow(SW_HIDE);
      MoveWindow(bounds);
   }
   if( inPack ) CheckDlgButton(IDC_PACK, BST_CHECKED);
   
   CEdit edit(GetDlgItem(IDC_QTY));
   edit.SetSelAll(TRUE);
   return 0;
}

DWORD EnterQty::GetValue(int id, DWORD scale)
{
   CWindow wnd(GetDlgItem(id));

   int len = wnd.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));
   wnd.GetWindowText(buf, len+1);

   wchar_t decBuf[4], sepBuf[4];
   int cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_SDECIMAL, decBuf, sizeof(decBuf)/sizeof(decBuf[0]));
   decBuf[cch] = L'\0';
   cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_STHOUSAND, sepBuf, sizeof(sepBuf)/sizeof(sepBuf[0]));
   sepBuf[cch] = L'\0';

   DWORD val = 0;
   while( *buf != L'\0' && *buf != *decBuf && *buf != L'.' )
   {
      if( *buf != *sepBuf )
         val = val * 10 + *buf - L'0';
      buf++;
   }

   val *= scale;
   if( *buf == *decBuf || *buf == L'.' )
   {
      while( *(++buf) && scale > 1 )
      {
         scale /= 10;
         val += (*buf - L'0') * scale;
      }
   }
   return val;
}

void EnterQty::SetScalingValue(int id, int value, DWORD scale, bool hideRest)
{
   wchar_t buf[20], src[20];

   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
   SetDlgItemText(id, buf);
}

LRESULT EnterQty::Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   bHandled = FALSE;
   value = GetValue(IDC_QTY, QTY_SCALE);
   inPack = (IsDlgButtonChecked(IDC_PACK) == BST_CHECKED);
   return 0;
}

LRESULT EnterQty::OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   CEdit qty(GetDlgItem(IDC_QTY));

   if( wID == IDC_DIG_BS || wID == IDC_DIG_PT )
   {
      int start, end;
      int len = qty.GetWindowTextLength();
      wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));

      qty.GetSel(start, end);
      qty.GetWindowText(buf, len+1);

      buf[len] = L'\0';
      if( wID == IDC_DIG_PT )
      {
         bool canReplace = false;
         if( wcschr(buf, L'.') == NULL ) canReplace = true;
         else
         {
            while( start < end ) if( buf[start++] == L'.' )
            {
               canReplace = true;
               break;
            }
         }

         if( canReplace ) qty.ReplaceSel(L".");
      } else
      {
         if( start != end )
            qty.ReplaceSel(L"");
         else
         {
            if( len > 0 )
            {
               wcscpy(buf+len-1, buf+len);
               qty.SetWindowText(buf);
               qty.SetSel(len,len);
            }
         }
      }
      return 0;
   }

   wchar_t buf[2];
   buf[1] = L'\0';
   switch(wID)
   {
      case IDC_DIG_0:
         *buf = L'0';
         break;
      case IDC_DIG_1:
         *buf = L'1';
         break;
      case IDC_DIG_2:
         *buf = L'2';
         break;
      case IDC_DIG_3:
         *buf = L'3';
         break;
      case IDC_DIG_4:
         *buf = L'4';
         break;
      case IDC_DIG_5:
         *buf = L'5';
         break;
      case IDC_DIG_6:
         *buf = L'6';
         break;
      case IDC_DIG_7:
         *buf = L'7';
         break;
      case IDC_DIG_8:
         *buf = L'8';
         break;
      case IDC_DIG_9:
         *buf = L'9';
         break;
   }
   qty.ReplaceSel(buf);
   return 0;
}

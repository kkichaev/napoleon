/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   16/04/2008   creating
 */
#include "stdafx.h"
#include <Module.h>
#include "EnterValue.h"

EnterValue::EnterValue(WORD scale, bool isSigned) : limit(0)
{ 
   value = 0;

   if( scale == 0 ) scale = 1;

   this->scale = scale;
   this->isSigned = isSigned;
}

void EnterValue::SetChildFont()
{
   LOGFONT lf;
   if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) )
   {
      if( lf.lfHeight < 0 ) lf.lfHeight++;
      else lf.lfHeight--;
      lf.lfWeight = FW_BOLD;

      HFONT font = CreateFontIndirect(&lf);
      if( font != NULL )
      {
         CWindow child(GetWindow(GW_CHILD));

         while( child.m_hWnd != NULL )
         {
            child.SetFont(font);
            child = child.GetWindow(GW_HWNDNEXT);
         }
      }
   }
}

void EnterValue::LimitText(WORD limit)
{
   this->limit = limit;
   
   CEdit edit(GetDlgItem(IDC_QTY));
   if( edit.m_hWnd != NULL )
      edit.LimitText(limit);
}

LRESULT EnterValue::OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
{
   bHandled = FALSE;

   SetChildFont();

   if( limit != 0 )
   {
      CEdit edit(GetDlgItem(IDC_QTY));
      edit.LimitText(limit);
   }

   SetScalingValue(IDC_QTY, value, scale, true);

   if( scale == 1 )
   {
      CRect ptBounds, bounds;
      CWindow pt(GetDlgItem(IDC_DIG_PT)), btn0(GetDlgItem(IDC_DIG_0));

      pt.GetWindowRect(ptBounds);
      pt.ShowWindow(SW_HIDE);

      btn0.GetWindowRect(bounds);

      ScreenToClient(ptBounds);
      ScreenToClient(bounds);

      bounds.right = ptBounds.right;
      btn0.MoveWindow(bounds);
   }

   if( !isSigned )
   {
      CRect ptBounds, bounds;
      CWindow pt(GetDlgItem(IDC_MINUS)), btn0(GetDlgItem(IDC_DIG_BS));

      pt.GetWindowRect(ptBounds);
      pt.ShowWindow(SW_HIDE);

      btn0.GetWindowRect(bounds);

      ScreenToClient(ptBounds);
      ScreenToClient(bounds);

      bounds.bottom = ptBounds.bottom;
      btn0.MoveWindow(bounds);
   }

   CEdit edit(GetDlgItem(IDC_QTY));
   edit.SetSelAll(TRUE);
   return 0;
}

DWORD EnterValue::GetValue(int id, DWORD scale)
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
   DWORD sign = 1;

   if( *buf == '-' )
   {
      sign = (DWORD)-1;
      buf++;
   }

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
   return val * sign;
}

void EnterValue::SetScalingValue(int id, int value, DWORD scale, bool hideRest)
{
   wchar_t buf[20], src[20];

   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
   SetDlgItemText(id, buf);
}

LRESULT EnterValue::Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   bHandled = FALSE;
   value = GetValue(IDC_QTY, scale);
   return 0;
}

LRESULT EnterValue::ChangeSign(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   CEdit wnd(GetDlgItem(IDC_QTY));

   int len = wnd.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+2) * sizeof(wchar_t));
   wnd.GetWindowText(buf, len+1);

   wnd.SetSel(0, len);
   if( *buf == L'-' )
   {
      wnd.ReplaceSel(buf+1);
   } else
   {
      for( int i = len; i >= 0; i-- )
         buf[i+1] = buf[i];
      *buf = L'-';

      wnd.ReplaceSel(buf);
   }
   //wnd.SetFocus();
   return 0;
}

LRESULT EnterValue::OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   CEdit qty(GetDlgItem(IDC_QTY));
 
   int len = qty.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));
   
   qty.GetWindowText(buf, len+1);
      
   if( wID == IDC_DIG_BS || wID == IDC_DIG_PT )
   { 
      int start, end;
      qty.GetSel(start, end);

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

   if( *buf == L'-' && len == 2 && buf[1] == L'0' ) qty.SetSel(1, len);
   else if( len == 1 && *buf == L'0' ) qty.SetSel(0, len);

   wchar_t dbuf[2];
   dbuf[1] = L'\0';
   switch(wID)
   {
      case IDC_DIG_0:
         *dbuf = L'0';
         break;
      case IDC_DIG_1:
         *dbuf = L'1';
         break;
      case IDC_DIG_2:
         *dbuf = L'2';
         break;
      case IDC_DIG_3:
         *dbuf = L'3';
         break;
      case IDC_DIG_4:
         *dbuf = L'4';
         break;
      case IDC_DIG_5:
         *dbuf = L'5';
         break;
      case IDC_DIG_6:
         *dbuf = L'6';
         break;
      case IDC_DIG_7:
         *dbuf = L'7';
         break;
      case IDC_DIG_8:
         *dbuf = L'8';
         break;
      case IDC_DIG_9:
         *dbuf = L'9';
         break;
   }
   qty.ReplaceSel(dbuf);
   return 0;
}

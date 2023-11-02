/*
 * Copyright (C), 2007 - 2009, Денис Мосягин
 *
 * Высота текста
 *
 *  ert   03/06/2009   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

void CalcTextHeight(HWND wnd, RECT *bounds)
{
   int len = SendMessage(wnd, WM_GETTEXTLENGTH, 0, 0) + 1;
   wchar_t *tstr = (wchar_t*)alloca(len * sizeof(wchar_t));
   SendMessage(wnd, WM_GETTEXT, len, (LPARAM)tstr);
   tstr[len-1] = L'\0';

   HDC dc = GetDC(wnd);
   HFONT hFont = (HFONT)SendMessage(wnd, WM_GETFONT, 0, 0);
   if( hFont == NULL )
      hFont = (HFONT)GetStockObject(SYSTEM_FONT);

   HGDIOBJ svFont = SelectObject(dc, hFont);
   DrawText(dc, tstr, len-1, bounds, DT_CALCRECT|DT_WORDBREAK);
   SelectObject(dc, svFont);
   ReleaseDC(wnd, dc);
}


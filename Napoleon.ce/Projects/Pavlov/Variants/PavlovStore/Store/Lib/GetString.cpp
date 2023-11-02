/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Вернуть строку из элемента
 *
 *  ert   20/04/2011   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

bool GetString(std::wstring* dest, HWND hWnd)
{
   if( hWnd == NULL )
      return false;

   dest->clear();
   int len = GetWindowTextLength(hWnd) + 1;
   if( len > 1 )
   {
      wchar_t* buf = (wchar_t*)alloca(sizeof(wchar_t) * len);
      GetWindowText(hWnd, buf, len);
      dest->assign(buf);
   }

   return true;
}

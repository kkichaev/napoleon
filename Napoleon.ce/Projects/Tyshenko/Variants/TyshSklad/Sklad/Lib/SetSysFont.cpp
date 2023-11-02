/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Установить шрифт из настрек системы
 *
 *  ert   22/05/2008   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

static HFONT sysFont;

void SetSystemFont(HWND hWnd, BOOL redraw)
{
   if( sysFont == NULL )
   {
      HFONT hf = (HFONT)GetStockObject(SYSTEM_FONT);
      LOGFONT lf;

      GetObject(hf, sizeof(lf), &lf);

#if defined WIN32_PLATFORM_PSPC && !defined(NATIVE_CE)
      int size;
      SHGetUIMetrics(SHUIM_FONTSIZE_PIXEL, &size, sizeof(size), NULL);
      lf.lfHeight = -size;
#endif

      sysFont = CreateFontIndirect(&lf);
   }

   if( sysFont != NULL )
      SendMessage(hWnd, WM_SETFONT, (WPARAM)sysFont, MAKELPARAM(redraw, 0));
}

void FreeSystemFont()
{
   if( sysFont != NULL )
      DeleteObject(sysFont);

   sysFont = NULL;
}

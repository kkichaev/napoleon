/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Отрисовка битмапа с масштабом
 *
 *  ert   20/05/2010   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

void PaintScale(HDC hdc, HBITMAP hBmp, int width, int height)
{
   BITMAP info;
   int scaleX = 0, scaleY = 0;
   HDC memDC = CreateCompatibleDC(hdc);
   HGDIOBJ svBmp = SelectObject(memDC, hBmp);

   GetObject(hBmp, sizeof(info), &info);

   if( info.bmHeight > height ) scaleY = info.bmHeight * 100 / height;
   if( info.bmWidth > width ) scaleX = info.bmWidth * 100 / width;

   if( scaleY > 0 || scaleX > 0 )
   {
      if( scaleY > scaleX ) scaleX = scaleY;
      else scaleY = scaleX;

      int newWidth = info.bmWidth * 100 / scaleX, 
         newHeight = info.bmHeight * 100 / scaleY;

      StretchBlt(hdc, (width - newWidth) / 2, (height - newHeight) / 2,
         newWidth, newHeight, memDC, 
         0, 0, info.bmWidth, info.bmHeight, SRCCOPY);
   } else
      BitBlt(hdc, (width - info.bmWidth) / 2, (height - info.bmHeight) / 2,
         info.bmWidth, info.bmHeight, memDC, 0, 0, SRCCOPY);

   SelectObject(memDC, svBmp);
   DeleteDC(memDC);
}


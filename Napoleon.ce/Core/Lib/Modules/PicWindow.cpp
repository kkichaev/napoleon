/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Окно c картинкой
 *
 *  ert   18/07/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include "PicWindow.h"
#include <StdFuncs.h>

void PicWindow::Show(CWindow parent)
{
   CRect rc;
   parent.GetWindowRect(rc);
   parent.ScreenToClient(rc);
   Create(parent, rc, NULL, WS_CHILD|WS_VISIBLE);

   BringWindowToTop();
   SetFocus();

   CMessageLoop *ml = _Module.GetMessageLoop();
   while( m_hWnd != NULL )
   {
      MSG msg;
      if( ::PeekMessage(&msg, NULL, 0, 0, PM_REMOVE) == FALSE )
         continue;
      
      if( msg.message == WM_QUIT )
         break;

      if( ml && ml->PreTranslateMessage(&msg) )
         continue;

      ::TranslateMessage(&msg);
      ::DispatchMessage(&msg);
   }
}

void PicWindow::OnPaint(HDC )
{
   CRect rc;
   PAINTSTRUCT pPaint;

   HDC hdc = BeginPaint(&pPaint);
   GetClientRect(rc);

   FillRect(hdc, rc, (HBRUSH)GetStockObject(BLACK_BRUSH/*WHITE_BRUSH*/));

   PaintScale(hdc, hBmp, rc.Width(), rc.Height());

   EndPaint(&pPaint);
}
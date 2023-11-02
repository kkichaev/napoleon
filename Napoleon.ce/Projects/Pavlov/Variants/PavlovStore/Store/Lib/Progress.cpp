/*
* Copyright (C), 2007, Денис Мосягин
*
* Progress Window
*
* ert   08/08/2007   creating
*/ 

#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include "Progress.h"
#include <StdFuncs.h>

#ifdef Kirov_Pavel
const int MIN_TIMER_INTERVAL = 10000;
#else
const int MIN_TIMER_INTERVAL = 5000;
#endif

LRESULT ProgressWindow::OnCreating(LPCREATESTRUCT cs)
{
   int progressWdh = (GetSystemMetrics(SM_CXSCREEN) > 320) ? 36 : 18;
   ::RECT bounds;
   bounds.left = 2;
   bounds.top = 2;
   bounds.right = cs->cx - 4;
   bounds.bottom = cs->cy - progressWdh;
   text.Create(m_hWnd, bounds, NULL, WS_CHILD|WS_VISIBLE);

   bounds.left = 1;
   bounds.right = cs->cx - 2;
   bounds.top = bounds.bottom + 3;
   bounds.bottom = bounds.top + progressWdh - 6;
   progressBar.Create(m_hWnd, bounds, NULL, WS_CHILD|WS_VISIBLE);
   progressBar.SetRange(0, 100);
   return 0;
}

void ProgressWindow::CreateSTDWindow(HWND parent)
{
   RECT rc;
   DWORD style = WS_BORDER;
   if( parent == NULL )
   {
      parent = GetDesktopWindow();
      style |= WS_POPUP;
   } else
      style |= WS_CHILD;
   ::GetClientRect(parent, &rc);

   int width = (GetSystemMetrics(SM_CXSCREEN) > 320) ? 100 : 50;
   rc.top = (rc.bottom - width) / 2;
   rc.left = 10;
   rc.right -= 20;
   rc.bottom = rc.top + width;

   Create(parent, rc, NULL, style);
   BringWindowToTop();
   ShowWindow(SW_SHOW);
}

void ProgressWindow::SetMax(int maxValue)
{ 
   maxPos = maxValue;
   if( maxValue > 0 )
      progressBar.SetRange32(0, maxValue); 
   setTime = GetTickCount();

   PowerUp();
}

void ProgressWindow::SetPos(int pos)
{ 
   DWORD ct = GetTickCount();
   if ( ct - setTime > MIN_TIMER_INTERVAL || pos >= maxPos )
   {
      setTime = ct;
      progressBar.SetPos(pos); 

      //SystemIdleTimerReset();
   }
}

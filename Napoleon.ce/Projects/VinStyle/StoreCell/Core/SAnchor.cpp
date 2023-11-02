/*
* Copyright (C), 2007, Денис Мосягин
*
* Static Anchor
*
*  ert   05/03/2008   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "SAnchor.h"

LRESULT StaticAnchor::DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);

   int len = GetWindowTextLength();
   if( len )
   {
      wchar_t *buf = (wchar_t*)malloc((len + 1) * sizeof(wchar_t));
      GetWindowText(buf, len+1);

      HFONT hFont = GetParent().GetFont();
      if( hFont == NULL )
         hFont = (HFONT)GetStockObject(SYSTEM_FONT);

      LOGFONT lf;
      GetObject(hFont, sizeof(lf), &lf);
      lf.lfUnderline = TRUE;

      hFont = CreateFontIndirect(&lf);
      HFONT prevFont = (HFONT)SelectObject(dc, hFont);

      CRect bounds;
      GetClientRect(bounds);

      HBRUSH brsh = CreateSolidBrush(GetSysColor(COLOR_WINDOW));
      FillRect(dc, bounds, brsh);
      DeleteObject(brsh);

      DWORD flags = (drawFlags) ? drawFlags : DT_SINGLELINE;
      if( GetStyle() & SS_RIGHT ) flags |= DT_RIGHT;
      SetTextColor(dc, RGB(0,0,255));
      DrawText(dc, buf, len, bounds, flags);

      SelectObject(dc, prevFont);
      DeleteObject(hFont);
      free(buf);
   }

   EndPaint(&ps);
   return 0;
}

void StaticAnchor::RemoveClickHandler(IClickHandler* handler)
{
   std::vector<IClickHandler*>::iterator i = handlers.begin();
   for( ; i != handlers.end(); i++ )
      if( (*i) == handler )
      {
         handlers.erase(i);
         break;
      }
}

LRESULT StaticAnchor::OnClick(UINT uMsg, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled)
{
   if( HIWORD(wParam) == STN_CLICKED )
   {
      std::vector<IClickHandler*>::iterator i = handlers.begin();
      for( ; i != handlers.end(); i++ )
         (*i)->Click(this);
   } else
      bHandled = FALSE;

   return 0;
}

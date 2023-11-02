/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Элемент Сумма
 *
 *  ert   22/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlcrack.h>

#include <NapoleonRes.h>

#include "SumLabel.h"

//
//--------------------------- SumLabel -------------------------------------
//
SumLabel::SumLabel(int _scale) : newFont(NULL)
{
   this->scale = _scale;
}

SumLabel::~SumLabel()
{
   if( m_hWnd ) DestroyWindow();
   if( newFont != NULL ) DeleteObject(newFont);
}

void SumLabel::UpdateLayout(int bottomPos)
{
   if( m_hWnd == NULL ) return;

   CRect rc, bnds;
   GetParent().GetClientRect(rc);
   GetWindowRect(bnds);
   
   rc.left = rc.right - bnds.Width() - offset;//GetSystemMetrics(SM_CXVSCROLL);
   rc.right = rc.left + bnds.Width();
   if( bottomPos >= 0 )
      rc.bottom = bottomPos;
   rc.bottom-=2;
   rc.top = rc.bottom - 40;

   MoveWindow(rc, FALSE);
}

bool SumLabel::CreateLabel(HWND hOwner, int width, int offset)
{
   CRect rc;
   ::GetClientRect(hOwner, rc);
   rc.left = rc.right - width - offset;//GetSystemMetrics(SM_CXVSCROLL);
   rc.right = rc.left + width;
   rc.bottom-=2;
   rc.top = rc.bottom - 40;
   Create(hOwner, rc, NULL, WS_CHILD|SS_RIGHT|WS_VISIBLE);
   this->offset = offset;

   BringWindowToTop();

   LOGFONT lf;
   if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) )
   {
      lf.lfWeight = 18;
      lf.lfWeight = FW_BOLD;

      newFont = CreateFontIndirect(&lf);
      if( newFont != NULL )
         SetFont(newFont);
   }

   return true;
}

void SumLabel::UpdatePosition(const wchar_t *txt)
{
   CRect bounds;
   GetWindowRect(bounds);
   GetParent().ScreenToClient(bounds);

   RECT bds = {0};
   HFONT font = GetFont();
   HDC dc = CreateCompatibleDC(NULL);
   if( font != NULL )
      SelectObject(dc, font);
   const wchar_t *p = txt, *ep;
   int width = 0;
   while(true)
   {
      ep = wcschr(p, L'\n');
      DrawText(dc, p, (ep) ? p - ep : -1, &bds, DT_CALCRECT|DT_SINGLELINE);
      if( width < bds.right )
         width = bds.right;
      if( ep == NULL )
         break;
      p = ep + 1;
   }
   DeleteDC(dc);

   SetWindowPos(HWND_TOP, bounds.right - width, bounds.top, 
      width, bounds.Height(), 
      SWP_NOREDRAW);
}

void SumLabel::SetSum(long qty, long sum, bool hideRest)
{
   if( m_hWnd == NULL ) return;

   wchar_t buf[20], dest[20];

   std::wstring tstr, text;

   _Module.LoadString(&tstr, IDS_QTY_TOTAL);
   ConvertScaling(buf, (long)qty, QTY_SCALE);
   FormatScaling(buf, dest, sizeof(dest)/sizeof(dest[0]), qty % QTY_SCALE, QTY_SCALE, true);
   text = tstr;
   text += L": ";
   text += dest;
   text += L"\n";

   _Module.LoadString(&tstr, IDS_SUM);
   ConvertScaling(buf, (long)sum, scale);
   FormatScaling(buf, dest, sizeof(dest)/sizeof(dest[0]), sum % scale, scale, hideRest);
   text += tstr;
   text += L": ";
   text += dest;
   
   UpdatePosition(text.c_str());
   SetWindowText(text.c_str());
}

void SumLabel::Paint(HDC)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);
   int bkMode = SetBkMode(dc, TRANSPARENT);
   
   HGDIOBJ hobj = SelectObject(dc, GetFont());

   CRect rc;
   GetClientRect(rc);
   
   HBRUSH brsh = CreateSolidBrush(GetSysColor(COLOR_WINDOW));
   FillRect(dc, rc, brsh);
   DeleteObject(brsh);

   int len = GetWindowTextLength();
   if( len )
   {
      len++;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      GetWindowText(buf, len);

      SetTextColor(dc, RGB(0,0,0));
      //ExtTextOut(dc, rc.left, rc.top, 0, NULL, buf, len-1, NULL);
      DrawText(dc, buf, len-1, rc, DT_RIGHT);
   }

   SelectObject(dc, hobj);
   SetBkMode(dc, bkMode);
   EndPaint(&ps);
}


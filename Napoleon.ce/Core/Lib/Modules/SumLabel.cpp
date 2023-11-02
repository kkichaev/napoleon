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

void SumLabel::UpdateLayout()
{
   if( m_hWnd == NULL ) return;

   CRect rc, bnds;
   GetParent().GetClientRect(rc);
   GetWindowRect(bnds);
   
   rc.left = rc.right - bnds.Width() - offset;//GetSystemMetrics(SM_CXVSCROLL);
   rc.right = rc.left + bnds.Width();
   rc.bottom-=2;

   MoveWindow(rc, FALSE);
}

bool SumLabel::CreateLabel(HWND hOwner, int width, int offset)
{
   CRect rc;
   ::GetClientRect(hOwner, rc);
   rc.left = rc.right - width - offset;//GetSystemMetrics(SM_CXVSCROLL);
   rc.right = rc.left + width;
   rc.bottom-=2;
   Create(hOwner, rc, NULL, WS_CHILD|SS_RIGHT|WS_VISIBLE);
   this->offset = offset;

   BringWindowToTop();

   LOGFONT lf;
   if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) )
   {
      if( lf.lfHeight < 0 ) lf.lfHeight++;
      else lf.lfHeight--;
      lf.lfWeight = FW_BOLD;

      newFont = CreateFontIndirect(&lf);
      if( newFont != NULL )
         SetFont(newFont);
   }

#ifdef Alehichev
   ShowWindow(SW_HIDE);
#endif
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
   DrawText(dc, txt, -1, &bds, DT_CALCRECT | DT_SINGLELINE);
   DeleteDC(dc);

   SetWindowPos(HWND_TOP, bounds.right - bds.right, bounds.top, 
      bds.right, bounds.Height(), 
      SWP_NOREDRAW);
}

void SumLabel::SetSum(long sum, bool hideRest)
{
   if( m_hWnd == NULL ) return;

   wchar_t buf[20], dest[20];
   ConvertScaling(buf, (long)sum, scale);
   FormatScaling(buf, dest, sizeof(dest)/sizeof(dest[0]), sum % scale, scale, hideRest);

   std::wstring tstr = text;
   tstr += L"\t";
   tstr += dest;
   UpdatePosition(tstr.c_str());

   SetWindowText(dest);
}

void SumLabel::SetInfoText(const wchar_t *text)
{
   this->text = text;

   if( m_hWnd == NULL ) return;

   int len = GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   GetWindowText(buf, len);

   std::wstring tstr = text;
   tstr += L"\t";
   tstr += buf;
   UpdatePosition(tstr.c_str());

   UpdateWindow();
}

void SumLabel::Paint(HDC)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);
   int bkMode = SetBkMode(dc, TRANSPARENT);
   
   HGDIOBJ hobj = SelectObject(dc, GetFont());

   CRect rc;
   GetClientRect(rc);
   
   HBRUSH brsh = CreateSolidBrush(GetSysColor(COLOR_3DFACE));
   FillRect(dc, rc, brsh);
   DeleteObject(brsh);

   if( text.size() != 0 )
   {
      SetTextColor(dc, RGB(70,70,70));
      DrawText(dc, text.c_str(), -1, rc, DT_LEFT|DT_VCENTER|DT_SINGLELINE);
   }

   int len = GetWindowTextLength();
   if( len )
   {
      len++;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      GetWindowText(buf, len);

      SetTextColor(dc, RGB(0,0,0));
      DrawText(dc, buf, len-1, rc, DT_RIGHT|DT_VCENTER|DT_SINGLELINE);
   }

   SelectObject(dc, hobj);
   SetBkMode(dc, bkMode);
   EndPaint(&ps);
}


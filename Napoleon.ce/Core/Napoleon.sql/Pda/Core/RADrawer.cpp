/*
* Copyright (C), 2006-2010, Денис Мосягин
*
* RightAlignDrawer
*
*  ert   06/08/2010   extract from QTY.cpp
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "RADrawer.h"

static void SplitString(wchar_t *str, std::vector<wchar_t*> *strings)
{
   wchar_t *spLine = str, *epLine;
   while(true)
   {
      strings->push_back(spLine);
      epLine = wcschr(spLine, L'\n');
      if( epLine ) *epLine = L'\0';
      else break;

      spLine = epLine + 1;
   }
}

const int OFFSET = 5;
void RightAlignDrawer::SetCellsBounds(std::vector<int> *widths, int* lineHeight, const std::vector<wchar_t*> &strings, HDC dc)
{
   for( int i=0; i<(int)strings.size(); i++ )
   {
      int curItem = 0;
      wchar_t *sp = strings[i];

      while( true )
      {
         wchar_t *ep = wcschr(sp, L'\t');
         if( ep ) *ep = L'\0';

         CRect rc;
         rc.left = 0;
         rc.top = 0;
         rc.right = 0;
         rc.bottom = 0;
         DrawText(dc, sp, -1, rc, DT_CALCRECT|DT_SINGLELINE);

         if( *lineHeight < rc.bottom ) *lineHeight = rc.bottom;

         int curWidth = rc.Width() + OFFSET;

         if( i == 0 )
            widths->push_back(curWidth);
         else
         {
            if( (*widths)[curItem] < curWidth )
               (*widths)[curItem] = curWidth;
         }
         if( ep == NULL ) break;
         *ep = L'\t';
         sp = ep + 1;
         curItem++;
      }
   }
}

RightAlignDrawer::RightAlignDrawer()
{
   bkColor = GetSysColor(COLOR_WINDOW);
   rightSide = false;
   labelWidth = 0;
   labelBuf = NULL;
}

RightAlignDrawer::~RightAlignDrawer()
{
   free(labelBuf);
}

void RightAlignDrawer::SetLabels(const wchar_t* line, int width, bool rightSide)
{
   this->rightSide = rightSide;
   labelWidth = width;

   labelBuf = wcsdup(line);
   SplitString(labelBuf, &labels);
}

LRESULT RightAlignDrawer::DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);
   CRect bounds;

   GetClientRect(bounds);
   HBRUSH brsh = CreateSolidBrush(GetSysColor(COLOR_WINDOW));
   FillRect(dc, bounds, brsh);
   DeleteObject(brsh);

   HFONT svFont = NULL;
   HFONT cf = GetFont();
   if( cf != NULL )
      svFont = (HFONT)SelectObject(dc, cf);

   int len = GetWindowTextLength();
   if( len )
   {
      wchar_t *buf = (wchar_t*)malloc((len + 1) * sizeof(wchar_t));

      GetWindowText(buf, len+1);

      std::vector<wchar_t*> strings;
      std::vector<int> widths;
      int lineHeight = 0;

      SplitString(buf, &strings);
      SetCellsBounds(&widths, &lineHeight, strings, dc);

      int curY = 0;
      for( int i=0; i<(int)strings.size(); i++ )
      {
         COLORREF prevBack;
         wchar_t *sp, *ep;

         int width = bounds.Width();
         CRect rc(bounds);
         rc.top = curY;
         rc.bottom = rc.top + lineHeight;

         sp = strings[i];

         if( labelWidth > 0 )
         {
            CRect labelRc(rc);
            if( rightSide )
            {
               width -= labelWidth + OFFSET;
               if( i < (int)labels.size() )
               {
                  labelRc.right -= OFFSET;
                  labelRc.left = labelRc.right - labelWidth;

                  DrawText(dc, labels[i], -1, labelRc, DT_SINGLELINE|DT_VCENTER);
               }
               rc.right -= labelWidth + OFFSET * 2;
            } else
            {
               if( i < (int)labels.size() )
               {
                  labelRc.left = OFFSET;
                  labelRc.right = labelRc.left + labelWidth;

                  DrawText(dc, labels[i], -1, labelRc, DT_SINGLELINE|DT_VCENTER);
               }
               rc.left = labelWidth + OFFSET * 2;
            }
         }

         COLORREF curColor;
         if( (i % 2) == 0 ) curColor = bkColor;
         else curColor = GetSysColor(COLOR_WINDOW);

         brsh = CreateSolidBrush(curColor);
         FillRect(dc, rc, brsh);
         DeleteObject(brsh);
         prevBack = ::SetBkColor(dc, curColor);

         int curItem = 0, curPos = 0;
         if( rightSide )
            curPos = rc.right - OFFSET;
         while( true )
         {
            ep = wcschr(sp, L'\t');
            if( ep ) *ep = L'\0';
            int cw = widths[curItem++];

            COLORREF svTxtColor = ::GetTextColor(dc);;
            if( *sp == L'\x1' )
            {
               sp++;
               svTxtColor = ::SetTextColor(dc, RGB(255,0,0));
            }
            if( rightSide )
               curPos -= cw;

            rc.left = curPos;
            rc.right = curPos + cw;

            if( (rightSide && curPos <= 0 ) || (!rightSide && cw + curPos > width) )
            {
               DrawText(dc, sp, -1, rc, DT_SINGLELINE|DT_RIGHT|DT_VCENTER);
               ::SetTextColor(dc, svTxtColor);
               break;
            }

            DrawText(dc, sp, -1, rc, DT_SINGLELINE|DT_RIGHT|DT_VCENTER);
            ::SetTextColor(dc, svTxtColor);
            if( ep == NULL ) break;
            sp = ep + 1;

            if( !rightSide )
               curPos += cw;
         }

         curY += lineHeight;
         ::SetBkColor(dc, prevBack);
      }
      free(buf);
   }

   if( svFont != NULL )
      SelectObject(dc, svFont);

   EndPaint(&ps);
   return 0;
}
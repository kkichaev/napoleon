/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Список задач
 *
 *  ert   09/08/2010   creating
 */ 
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include "TaskList.h"
#include "ResAdd.h"

TaskList::TaskList(int id) : CtrlID(id)
{
   HFONT hf = (HFONT)GetStockObject(SYSTEM_FONT);
   LOGFONT lf;
   GetObject(hf, sizeof(lf), &lf);
   lf.lfWeight = FW_BOLD;
   lf.lfHeight -= 3;

   hFont = CreateFontIndirect(&lf);
}

TaskList::~TaskList()
{
   if( hFont )
      DeleteObject(hFont);
}

static void DrawBitmap(HDC dc, int left, int top, int width, int height, int id)
{
   HBITMAP bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(id));
   if( bmp != NULL )
   {
      HDC memDC = CreateCompatibleDC(dc);
      HDC memDC2 = CreateCompatibleDC(dc);
      BITMAP bm;

      GetObject(bmp, sizeof(bm), &bm);

      SelectObject(memDC2, bmp);
      HBITMAP dst = CreateCompatibleBitmap(memDC2, width, height);
      SelectObject(memDC2, dst);
      SelectObject(memDC, bmp);

      StretchBlt(memDC2, 0, 0, width, height, memDC, 0, 0, bm.bmWidth, bm.bmHeight, SRCCOPY);

      COLORREF clrBack = GetPixel(memDC2, 0, 0);
      TransparentBlt(dc, left, top, width, height, memDC2, 0, 0, width, height, clrBack);

      DeleteDC(memDC);
      DeleteDC(memDC2);

      DeleteObject(bmp);
      DeleteObject(dst);
   }
}

LRESULT TaskList::DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   const int bwdh = 24;

   LPDRAWITEMSTRUCT ds = (LPDRAWITEMSTRUCT)lParam;
   Item* td = (Item*)ds->itemData;
   if( ds->CtlID == CtrlID && td != NULL )
   {
      int textWdh = ds->rcItem.bottom - ds->rcItem.top;
      int imgOffset = (ds->rcItem.bottom - ds->rcItem.top - bwdh) / 2;

      COLORREF backClr = 
         ((ds->itemState & ODS_SELECTED) && (td->flags & Item::Disabled) == 0) ? 
            RGB(200,200,200) : 
            RGB(255,255,255);
      HBRUSH brsh = CreateSolidBrush(backClr);
      FillRect(ds->hDC, &ds->rcItem, brsh);
      DeleteObject(brsh);

      HFONT hf = (HFONT)SelectObject(ds->hDC, hFont);
      if( td->id > 0 )
         DrawBitmap(ds->hDC, ds->rcItem.left + imgOffset, ds->rcItem.top + imgOffset, bwdh, bwdh, td->id);
      if( (td->flags & Item::Checked) )
         DrawBitmap(ds->hDC, ds->rcItem.right - bwdh - imgOffset, ds->rcItem.top + imgOffset, bwdh, bwdh, IDD_TASK_OK_BMP);

      //
      // draw text
      CRect rc(ds->rcItem);
      COLORREF textClr = SetTextColor(ds->hDC, 
         ((td->flags & Item::Disabled)) ? RGB(127, 127, 127) : RGB(0, 0, 0));
      backClr = SetBkColor(ds->hDC, backClr);
      rc.left = textWdh;
      DrawText(ds->hDC, td->text, -1, rc, DT_LEFT | DT_VCENTER);
      SetTextColor(ds->hDC, textClr);
      SetBkColor(ds->hDC, backClr);
      // draw text
      //

      //
      // draw lines
      HPEN cpen = ::CreatePen(PS_SOLID,0,RGB(50,50,50));
      HGDIOBJ svpen = SelectObject(ds->hDC, cpen);
      MoveToEx(ds->hDC, ds->rcItem.left, ds->rcItem.bottom, NULL);
      LineTo(ds->hDC, ds->rcItem.right, ds->rcItem.bottom);
      MoveToEx(ds->hDC, ds->rcItem.left, ds->rcItem.top, NULL);
      LineTo(ds->hDC, ds->rcItem.right, ds->rcItem.top);
      SelectObject(ds->hDC, svpen);
      DeleteObject(cpen);
      // draw down line
      //

      if( hf != NULL )
         SelectObject(ds->hDC, hf);
   }
   return 0;
}

void TaskList::UpdateLayout()
{
   if( m_hWnd != NULL )
   {
      CRect rc;
      GetParent().GetClientRect(rc);
      MoveWindow(rc);
   }
}

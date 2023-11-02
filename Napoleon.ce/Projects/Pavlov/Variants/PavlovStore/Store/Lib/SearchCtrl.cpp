/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Контрол для поиска
 *
 *  ert   05/08/2008   creating
 */ 
#include "stdafx.h"
#include "SearchCtrl.h"
#include <Module.h>
#include <NapoleonRes.h>
#include <StdFuncs.h>

const wchar_t SearchControl::TYPE_NAME[] = L"Введите название...";
int SearchControl::SEARCH_DELAY = 500;
//int SearchControl::SEARCH_DELAY = 1000;

SearchControl::SearchControl(UINT textID, UINT searchIco, const wchar_t *prompt) : 
   timerID(0), inSearch(false), clearingString(false), doSearch(false)
{
   this->searchPrompt = prompt;
   this->textID = textID;
   this->searchIco = searchIco;
}

void SearchControl::Paint(HDC)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);

   CRect rc;
   GetClientRect(rc);

   int wdh = GetSystemMetrics(SM_CXSMICON);
   int xPos = (SearchOffset() - wdh) / 2;

   HWND htxt = GetDlgItem(textID);
   UINT iid = (doSearch) ? IDC_BACK : searchIco;
   HICON hIco = (HICON)LoadImage(_Module.GetResourceInstance(), MAKEINTRESOURCE(iid), IMAGE_ICON, wdh, wdh, 0);
   DrawIconEx(dc, xPos, (rc.Height() - wdh) / 2, hIco, wdh, wdh, 0, NULL, DI_NORMAL);
   DestroyIcon(hIco);

   HPEN curPen = ::CreatePen(PS_SOLID, wdh/16, RGB(0,0,0));
   HGDIOBJ svBrush = SelectObject(dc, GetStockObject(NULL_BRUSH));
   HGDIOBJ svPen = SelectObject(dc, curPen);

   Rectangle(dc, rc.left, rc.top, rc.right, rc.bottom);

   SelectObject(dc, svBrush);
   SelectObject(dc, svPen);
   DeleteObject(curPen);

   EndPaint(&ps);
}

int SearchControl::SearchOffset() const
{
   int icoWidth = GetSystemMetrics(SM_CXSMICON);
   return icoWidth + icoWidth/2;
}

void SearchControl::SetHandler(HWND hParent, ISearchEvent *handler)
{
   this->handler = handler;

   CWindow parent(hParent);
   CWindow fnd(parent.GetDlgItem(textID));
   DWORD style = fnd.GetStyle() & (~WS_BORDER);
   CRect bounds;

   fnd.GetWindowRect(bounds);
   parent.ScreenToClient(bounds);

   Create(hParent, bounds);

   int icoWidth = SearchOffset();
   HWND hfnd = ::CreateWindow(L"EDIT", L"", style, bounds.left + icoWidth, bounds.top + 1, bounds.Width() - icoWidth - 1, 
      bounds.Height() - 2, m_hWnd, (HMENU)textID, _Module.GetResourceInstance(), NULL);

   fnd.DestroyWindow();
   ::SetWindowText(hfnd, searchPrompt);
   //SetDlgItemText(parent, textID, searchPrompt);
}

void SearchControl::OnClick(UINT flags, const CPoint &pt)
{
   SetMsgHandled(FALSE);
   if( !doSearch ) return;

   CRect rc;

   GetClientRect(rc);
   rc.right = SearchOffset();

   if( rc.PtInRect(pt) == TRUE )
   {
      SetMsgHandled(TRUE);
      NewSearch();
   }
}

void SearchControl::DoSearch(bool doing)
{
   if( doSearch != doing )
   {
      doSearch = doing;

      Invalidate();
      UpdateWindow();
   }
}

LRESULT SearchControl::HandleText(WORD nCode, WORD id, HWND hText, BOOL &bHandled)
{
   int textLen = ::GetWindowTextLength(hText) + 1;
   wchar_t *text = (wchar_t*)alloca(textLen * sizeof(wchar_t));
   ::GetWindowText(hText, text, textLen);

   switch(nCode)
   {
      case EN_SETFOCUS:
         if( wcscmp(text, searchPrompt) == 0 )
         {
            clearingString = true;
            ::SetWindowText(hText, L"");

#ifdef ZEBEX
         if( !IsSquareScreen() )
            SHSipPreference(hText, SIP_UP);
#endif
         }
         break;

      case EN_KILLFOCUS:
         if( *text == L'\0' )
            ::SetWindowText(hText, searchPrompt);
#ifdef ZEBEX
         if( !IsSquareScreen() )
            SHSipPreference(hText, SIP_DOWN);
#endif
			break;

      case EN_UPDATE:
         if( clearingString && *text == L'\0' )
         {
            inSearch = false;
            clearingString = false;
         } else
         {
            if( timerID != NULL ) KillTimer(timerID);
            timerID = SetTimer(1, SEARCH_DELAY, NULL);
         }
         break;

      default:
         return false;
   }
   return true;
}

void SearchControl::NewSearch(bool fireEvent)
{
   HWND hText = GetDlgItem(textID);
   ::SetWindowText(hText, L"");
   GetParent().SetFocus();

   if( timerID != NULL ) KillTimer(timerID);

   if( handler != NULL && fireEvent )
      handler->SearchClear();

   DoSearch(false);
}

void SearchControl::HandleTimer(UINT timer, TIMERPROC)
{
   if( inSearch || timer != timerID ) return;

   KillTimer(timerID);
   timerID = 0;
   
   inSearch = true;

   HWND hText = GetDlgItem(textID);
   if( hText == NULL ) return;

   int textLen = ::GetWindowTextLength(hText) + 1;
   wchar_t *text = (wchar_t*)alloca(textLen * sizeof(wchar_t));
   ::GetWindowText(hText, text, textLen);

   if( wcscmp(text, searchPrompt) == 0 )
   {
      inSearch = false;
      return;
   }

   if( handler != NULL )
      handler->SearchDo(text);

   DoSearch(true);
   inSearch = false;
}

void SearchControl::UpdateLayout(int top, int width, int *height, HFONT font, int left)
{
   if( m_hWnd == NULL ) return;

   int offset = GetSystemMetrics(SM_CXSMICON) / 4;
   int searchOffset = SearchOffset();
   CWindow fnd(GetDlgItem(textID));

   TEXTMETRIC tm;
   HDC dc = GetDC();
   SelectObject(dc, font);
   GetTextMetrics(dc, &tm);
   ReleaseDC(dc);

   *height = tm.tmHeight +  offset + offset/2;

   MoveWindow(left, top, width, *height);
   fnd.SetFont(font);
   fnd.MoveWindow(searchOffset, offset, width - searchOffset - 1, *height - offset - offset/2);

   Invalidate();
   UpdateWindow();
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   16/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlcrack.h>
#include <atlwince.h>

#include <Exchange.h>
#include "ObjImpl.h"

class NoteInfo : public CWindowImpl<NoteInfo>
{
   struct LinkInfo
   {
      std::wstring text;
      DWORD left;
      DWORD right;
      UINT code;
   };

   UINT exitCode;
   DWORD linkTop, linkBottom, textHeight;
   WORD lineHeight;
   int viewOffset;

   LinkInfo links[3];

   CEdit edit;
   bool updateLayout;

   std::wstring text;

   static const int offset = 6;

public:
   enum LinkType {Left, Right, Center};

   NoteInfo() : linkTop(0), linkBottom(0), exitCode(0), updateLayout(false), lineHeight(0), viewOffset(0), textHeight(0) {}

   UINT ExitCode() const { return exitCode; }

   void AddLink(const wchar_t *text, LinkType linkType, UINT code);

   void SetText(const wchar_t *text)
   { 
      updateLayout = true;
      edit.SetWindowText(text);
   }

   void SetTitle(const wchar_t *title) { SetWindowText(title); }

   bool Create(HWND parent, CRect &bounds, const wchar_t *title = L"")
   {
      CWindowImpl<NoteInfo>::Create(parent, bounds, title, WS_POPUP|WS_BORDER|WS_CAPTION);
      if( m_hWnd != NULL )
      {
         CRect rc;
         GetClientRect(rc);
         edit.Create(m_hWnd, rc, L"", WS_VISIBLE|WS_CHILD|ES_MULTILINE|ES_AUTOVSCROLL|ES_WANTRETURN|WS_VSCROLL);
         edit.SetFocus();
      }
      return m_hWnd != NULL;
   }

   void GetText(std::wstring *text)
   {
      *text = this->text;
   }

   void GetText()
   {
      int len = edit.GetWindowTextLength() + 1;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      edit.GetWindowText(buf, len);
      text.assign(buf, len-1);
   }

   UINT ShowModal()
   {
      ShowWindow(SW_SHOW);
      CMessageLoop *ml = _Module.GetMessageLoop();

      while( m_hWnd != NULL )
      {
         MSG msg;
         if( ::PeekMessage(&msg, NULL, 0, 0, PM_REMOVE) == FALSE ) continue;
         if( msg.message == WM_QUIT ) break;
         if( ml && ml->PreTranslateMessage(&msg) ) continue;

         ::TranslateMessage(&msg);
         ::DispatchMessage(&msg);
      }

      return ExitCode();
   }

   DECLARE_WND_CLASS(L"NOTEWND");

   BEGIN_MSG_MAP(NoteInfo)
      MSG_WM_PAINT(OnPaint)
      MESSAGE_HANDLER(WM_LBUTTONDOWN, OnButtonDown)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
   END_MSG_MAP()

   void OnPaint(HDC);
   void DrawLinks(HDC dc);

   LRESULT OnButtonDown(WORD msg, WPARAM, LPARAM, BOOL &bHandled);

   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);
};

void NoteInfo::AddLink(const wchar_t *text, LinkType linkType, UINT code)
{
   if( linkType > 2 ) return;

   LinkInfo &l = links[linkType];
   l.text = text;
   l.code = code;
   l.left = 0;
   l.right = 0;

   Invalidate();
   UpdateWindow();
}

void NoteInfo::DrawLinks(HDC dc)
{
   HFONT hFont = GetFont();
   if( hFont == NULL )
      hFont = (HFONT)GetStockObject(SYSTEM_FONT);

   LOGFONT lf;
   GetObject(hFont, sizeof(lf), &lf);
   lf.lfUnderline = TRUE;

   hFont = CreateFontIndirect(&lf);
   HFONT prevFont = (HFONT)SelectObject(dc, hFont);

   CRect bounds;
   GetClientRect(bounds);

   COLORREF svColor = SetTextColor(dc, RGB(0,0,255));

   for( int i=0; i < 3; i++ )
   {
      LinkInfo &link = links[i];
      if( link.text.empty() ) continue;

      RECT rc = { 0 };
      if( updateLayout )
      {
         DrawText(dc, link.text.c_str(), -1, &rc, DT_CALCRECT | DT_SINGLELINE);

         linkBottom = bounds.bottom - offset;
         linkTop = linkBottom - rc.bottom;
      }

      if( updateLayout )
      {
         if( rc.right == 0 ) DrawText(dc, link.text.c_str(), -1, &rc, DT_CALCRECT | DT_SINGLELINE);
         switch( i )
         {
         case Left:
            link.left = offset;
            break;
         case Center:
            link.left = (bounds.right - 2 * offset - rc.right) / 2;
            break;
         case Right:
            link.left = bounds.right - rc.right - offset;
            break;
         }
         link.right = link.left + rc.right;
      }
      rc.left = link.left; rc.right = link.right; rc.top = linkTop, rc.bottom = linkBottom;
      DrawText(dc, link.text.c_str(), -1, &rc, DT_SINGLELINE);
   }

   SetTextColor(dc, svColor);

   SelectObject(dc, prevFont);
   DeleteObject(hFont);
}

void NoteInfo::OnPaint(HDC )
{
   CRect rc;
   PAINTSTRUCT pPaint;
   HDC dc = BeginPaint(&pPaint);

   GetClientRect(rc);

   DrawLinks(dc);

   if( linkTop > 0 )
   {
      rc.bottom = linkTop - offset;

      HPEN svPen = (HPEN)SelectObject(dc, GetStockObject(BLACK_PEN));
      MoveToEx(dc, 0, rc.bottom, NULL);
      LineTo(dc, rc.right, rc.bottom);
      SelectObject(dc, svPen);
   }

   if( updateLayout )
   {
      edit.MoveWindow(rc);
      updateLayout = false;
   }

   EndPaint(&pPaint);
}

LRESULT NoteInfo::OnButtonDown(WORD msg, WPARAM, LPARAM lParam, BOOL &bHandled)
{
   WORD xPos = LOWORD(lParam); 
   WORD yPos = HIWORD(lParam);
   if( yPos >= linkTop && yPos <= linkBottom )
   {
      for( int i = 0; i <3; i++ )
      {
         LinkInfo &l = links[i];
         if( l.text.empty() ) continue;
         if( xPos >= l.left && xPos <= l.right )
         {
            exitCode = l.code;
            GetText();
            DestroyWindow();
            break;
         }
      }
   }
   return 0;
}

LRESULT NoteInfo::CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
   if( wParam == SPI_SETSIPINFO )
   {
      SIPINFO si;
      memset (&si, 0, sizeof (si));
      si.cbSize = sizeof (si);

      if (SipGetInfo(&si)) 
      {
         updateLayout = true;

         LONG bottom = min(si.rcVisibleDesktop.bottom, si.rcSipRect.bottom);
         MoveWindow(si.rcVisibleDesktop.left,
            si.rcVisibleDesktop.top, si.rcVisibleDesktop.right - si.rcVisibleDesktop.left,
            bottom - si.rcVisibleDesktop.top, TRUE);
      }
   }
   return 0;
}

void OpenNote(HWND hParent, const wchar_t *orgID, bool openIfExist)
{
   OrgNoteImpl on;

   if( SQLTable::IsTableExist(on.Name()) == false )
      on.CreateTable();

   on.id = (wchar_t*)orgID;
   on.note = L"";

   if( on.Read() == false && openIfExist ) return; 

   CRect rc;
   CWindow parent(hParent);
   parent.GetClientRect(rc);
   parent.ClientToScreen(rc);

   NoteInfo ni;
   ni.Create(parent, rc, L"Заметка");

   ni.SetText(on.note);
   ni.AddLink(L"Удалить", NoteInfo::Left, 1);
   ni.AddLink(L"Закрыть", NoteInfo::Right, 3);

   int res = ni.ShowModal();
   if( res == 1 )
   {
      on.Remove();
   } else
   {
      std::wstring text;
      ni.GetText(&text);
      on.note = (wchar_t*)text.c_str();
      on.Write();
   }

   SetForegroundWindow(parent);
}

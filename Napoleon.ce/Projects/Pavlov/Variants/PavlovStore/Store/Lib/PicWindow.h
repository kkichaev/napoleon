/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Окно c картинкой
 *
 *  ert   18/07/2009   creating
 */
#ifndef __PICTURE_WINDOW_H
#define __PICTURE_WINDOW_H

#include <atlcrack.h>

class PicWindow : public CWindowImpl<PicWindow>
{
public:
   PicWindow(HBITMAP _hBmp) : hBmp(_hBmp) {}
   ~PicWindow() { DeleteObject(hBmp); }

   DECLARE_WND_CLASS(L"PICWND");

   BEGIN_MSG_MAP(PicWindow)
      MESSAGE_HANDLER(WM_KILLFOCUS, OnButtonDown)
      MESSAGE_HANDLER(WM_LBUTTONDOWN, OnButtonDown)
      MSG_WM_PAINT(OnPaint)
   END_MSG_MAP()

   LRESULT OnButtonDown(WORD msg, WPARAM, LPARAM, BOOL &bHandled)
   {
      DestroyWindow();
      return 0;
   }

   void Cancel() { DestroyWindow(); }

   void Show(CWindow parent);
   void OnPaint(HDC );

protected:
   HBITMAP hBmp;
};


#endif

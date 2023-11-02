/*
* Copyright (C), 2007, Денис Мосягин
*
* Progress Window
*
* ert   08/08/2007   creating
*/ 
#ifndef __PROGRESS_WINDOW_H
#define __PROGRESS_WINDOW_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atlcrack.h>

class ProgressWindow : public CWindowImpl<ProgressWindow>, public IProgressIndicator
{
public:
   ProgressWindow() { maxPos = 100; setTime = GetTickCount(); }
   ~ProgressWindow() {}

   typedef CWindowImpl<ProgressWindow> BaseClass;

   BEGIN_MSG_MAP(ProgressWindow)
      MSG_WM_CREATE(OnCreating)
   END_MSG_MAP()

   void CreateSTDWindow(HWND parent);

   virtual void SetText(const wchar_t *_text, bool resetPos)
   {
      text.SetWindowText(_text);
      if( resetPos ) progressBar.SetPos(0);
   }

   virtual void SetMax(int maxValue);
   virtual void SetPos(int pos);

protected:
   LRESULT OnCreating(LPCREATESTRUCT cs);

protected:
   CProgressBarCtrl progressBar;
   CStatic          text;

   int maxPos;
   DWORD setTime;
};

#endif

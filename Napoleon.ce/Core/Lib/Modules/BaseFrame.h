/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Фрейм 
 * 
 *  ert   06/12/2007   creating
 */ 
#ifndef __BASE_FRAME_H
#define __BASE_FRAME_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <BaseForm.h>
#include <Progress.h>
#include <NapoleonRes.h>
#include <ListForm.h>
#include <StdFuncs.h>

#include <map>

const int WM_LOAD_FORM = WM_USER + 1;

#define CHAIN_TO_CHILD_FORM()  if((uMsg == WM_NOTIFY || uMsg == WM_COMMAND) && current) \
      { lResult = ::SendMessage( current->m_hWnd, uMsg, wParam, lParam ); return (bHandled = TRUE); } 

typedef CWinTraits< WS_CLIPCHILDREN | WS_CLIPSIBLINGS ,0> FrameTraits;

template <class T, class Traits = FrameTraits>
class BaseFrame : 
   public IFrame,
#ifdef WIN32_PLATFORM_PSPC
   public CFullScreenFrame<T, true>,
#endif
   public CFrameWindowImpl<T, CWindow, Traits>,
   public CAppWindow<T>
{
public:
   DECLARE_FRAME_WND_CLASS(NULL, IDR_MAIN_FRAME)

#ifdef WIN32_PLATFORM_PSPC
   typedef CFullScreenFrame<T, true> BaseFullScreen;
#endif
   typedef CFrameWindowImpl<T, CWindow, Traits> Base;

   BEGIN_MSG_MAP(BaseFrame)
      MESSAGE_HANDLER(WM_MEASUREITEM, NotifyForm)
      MESSAGE_HANDLER(WM_DRAWITEM, NotifyForm)

      MESSAGE_HANDLER(WM_CREATE, OnCreate)
      MESSAGE_HANDLER(WM_LOAD_FORM, OnLoadForm)
      MESSAGE_HANDLER(WM_SIZE, OnSize)

      CHAIN_TO_CHILD_FORM()
#ifdef WIN32_PLATFORM_PSPC
      CHAIN_MSG_MAP(BaseFullScreen)
#endif
      CHAIN_MSG_MAP(Base)
   END_MSG_MAP()

   BaseFrame() : current(NULL)
   {
      _Module.SetFrame(this);
   }

   ~BaseFrame()
   { 
      if( current ) current->Destroy();
      _Module.SetFrame(NULL);
   }

   virtual bool Load(DWORD formID, IFormData *data)
   {
      FormCreator creator = GetFormCreator(formID);
      if( creator == NULL )
         return false;

      PostMessage(WM_LOAD_FORM, (WPARAM)creator, (LPARAM)data);
      return true;
   }
   
   virtual HWND LoadMenuBar(DWORD barID, DWORD barV5 = 0, DWORD flags = SHCMBF_HIDESIPBUTTON)
   {
      if( m_hWndCECommandBar != NULL )
      {
         ::DestroyWindow(m_hWndCECommandBar);
         m_hWndCECommandBar = NULL;
      }

      CreateSimpleCEMenuBar(barID, flags, IDB_TOOLBAR, COUNT_BTIMAPS);

      CommandBar_AddBitmap(m_hWndCECommandBar, HINST_COMMCTRL, IDB_STD_SMALL_COLOR, STD_PRINT, 16, 16);
      CommandBar_AddBitmap(m_hWndCECommandBar, HINST_COMMCTRL, IDB_VIEW_SMALL_COLOR, VIEW_NEWFOLDER, 16, 16);

      return m_hWndCECommandBar;
   }

   virtual void SetTitle(const wchar_t *title)
   {
      SetWindowText(title);
   }

   virtual void Quit() { PostMessage(WM_CLOSE); }
#ifdef SQL_TABLES
#else
   virtual void PrepareData()
   {
      CheckFormData(true);
   }

   bool CheckFormData(bool forceCreate)
   {
      ProgressWindow pw;
      pw.CreateSTDWindow((IsWindowVisible()) ? m_hWnd : NULL);

      std::map<DWORD, IFrame::FormCreator>::iterator i = formCreatorMap.begin();
      while( i != formCreatorMap.end() )
      {
         IForm *frm = (*i->second)();
         bool res = frm->PrepareData(forceCreate, &pw);
         delete frm;

         if( res == false )
         {
            pw.DestroyWindow();
            return false;
         }
         i++;
      }

      pw.DestroyWindow();
      if( IsWindowVisible() == FALSE )
         ::SetForegroundWindow(m_hWnd);
      return true;
   }
#endif
   // специфические функции
public:

   // message handlers
protected:
   virtual LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled) = 0;

   LRESULT NotifyForm(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      if( current != NULL )
         return ::SendMessage(current->m_hWnd, uMsg, wParam, lParam);
      return 0;
   }

   LRESULT OnLoadForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      if( current )
      {
         current->Destroy();
         current = NULL;
      }

      current = (BaseForm*)((FormCreator)wParam)();
      if( current == NULL )
         return 0;

      if( current->Load(m_hWnd) == false )
      {
         delete current;
         current = NULL;
         return 0;
      }
      
      current->SetData((IFormData*)lParam);
      current->ShowWindow(SW_SHOW);
      return 0;
   }

   LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      if( BaseForm::screenWidth == 0 ) BaseForm::screenWidth = GetSystemMetrics(SM_CXSCREEN);
      if( current )
      {
         bool recalc = false;
         if( BaseForm::screenWidth != GetSystemMetrics(SM_CXSCREEN) )
         {
            recalc = true;
            BaseForm::screenWidth = GetSystemMetrics(SM_CXSCREEN);
         }
         current->UpdateLayout(recalc);
      }
      return 0;
   }
   
   virtual void OnFinalMessage(HWND /*hWnd*/) { PostQuitMessage(0); }

protected:
   BaseForm *current;
};



#endif

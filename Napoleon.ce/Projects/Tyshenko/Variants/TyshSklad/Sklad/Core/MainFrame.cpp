/*
* Copyright (C), 2007, Денис Мосягин
*
* Фрейм
* 
*  ert   13/08/2007   creating
*/ 
#include "stdafx.h"
#include <Module.h>
#include "MainFrame.h"
#include <map>

#include "Progress.h"
#include "BaseDialog.h"
#include "About.h"
#include <NapoleonRes.h>
#include "Progress.h"

#include <StdFuncs.h>

#include <SQLTable.h>
#include "FormEntries.h"
#include "Preference.h"

#include "InitDoc.h"

//
//--------------------- Main Frame -------------------------
//
MainFrame::MainFrame()
#ifdef ZEBEX
: bcHandler(NULL)
#endif
{
}

MainFrame::~MainFrame()
{
}

void MainFrame::Quit()
{
   DestroyDocTypeSet();
   BaseFrame::Quit();
}

BOOL MainFrame::PreTranslateMessage(MSG* pMsg)
{
	if(m_hAccel != NULL && ::TranslateAccelerator(m_hWnd, m_hAccel, pMsg))
		return TRUE;

   return FALSE;
}

HWND MainFrame::LoadMenuBar(DWORD barID, DWORD barV5, DWORD flags)
{
 	SHFullScreen(m_hWnd, SHFS_HIDETASKBAR|SHFS_HIDESIPBUTTON);
	return 0;

	//return BaseFrame::LoadMenuBar(IDD_ORDER_LIST, barV5, flags);
   //return m_hWndCECommandBar;
}

LRESULT MainFrame::OnBarcodeNotify(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
#ifdef ZEBEX
   if( bcHandler )
      bcHandler->HandleEvent();
#endif
   return 0;
}

LRESULT MainFrame::OnLoadForm(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   LRESULT res = BaseFrame::OnLoadForm(uMsg, wParam, lParam, bHandled);

   if( current != NULL )
   {
	   RECT rect = { 0 };
	   GetClientRect(&rect);
      UpdateBarsPosition(rect, FALSE);
	   ::SetWindowPos(current->m_hWnd, NULL, rect.left, rect.top, 
         rect.right - rect.left, rect.bottom - rect.top,
		   SWP_NOZORDER | SWP_NOACTIVATE);

      current->UpdateLayout(rect, true);
   }
   return res;
}

LRESULT MainFrame::OnSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
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
		RECT rect = { 0 };
		GetClientRect(&rect);
      UpdateBarsPosition(rect, recalc ? TRUE : FALSE);
		::SetWindowPos(current->m_hWnd, NULL, rect.left, rect.top, 
         rect.right - rect.left, rect.bottom - rect.top,
			SWP_NOZORDER | SWP_NOACTIVATE);
   
      current->UpdateLayout(rect, recalc);
   }
   return 0;
}

LRESULT MainFrame::OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   _Module.DataInit(NULL);

   CMessageLoop *ml = _Module.GetMessageLoop();
   ml->AddMessageFilter(this);

   ShowWindow(SW_MAXIMIZE);

   InitDocTypeSet();

	OpenMainForm();

   bHandled = FALSE;
   return 0;
}

LRESULT MainFrame::OnActivate(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled)
{
   return 0;
}

LRESULT MainFrame::CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
   if( current != NULL )
      current->SendMessage(WM_SETTINGCHANGE, wParam, lParam);
   return 0;
}

LRESULT MainFrame::About(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   HWND focused = GetFocus();
   CAboutDlg dlg;

#ifdef WIN32_PLATFORM_PSPC // Pocket PC code
   LRESULT res = FSDoModal(dlg);
#else
   LRESULT res = dlg.DoModal();
#endif

   ::SetFocus(focused);
   return res;
}

LRESULT MainFrame::OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   Quit();
   return 1;
}

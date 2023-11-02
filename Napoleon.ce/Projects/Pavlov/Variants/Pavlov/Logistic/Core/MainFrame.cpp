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
//
//--------------------- Main Frame -------------------------
//
MainFrame::~MainFrame()
{
}

LRESULT MainFrame::OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   _Module.DataInit(NULL);

   //InitDocTypeSet();
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
   CAboutDlg dlg;

#ifdef WIN32_PLATFORM_PSPC // Pocket PC code
   LRESULT res = FSDoModal(dlg);
#else
   LRESULT res = dlg.DoModal();
#endif

   return res;
}

LRESULT MainFrame::OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   Quit();
   return 1;
}

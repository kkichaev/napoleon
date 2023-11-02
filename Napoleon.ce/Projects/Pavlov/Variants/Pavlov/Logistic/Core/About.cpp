/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* О программе
*
*  ert   04/07/2008   creating
*/ 
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <BaseForm.h>
#include <BaseFrame.h>
#include <NapoleonRes.h>
#include <ListForm.h>

#include <BaseDialog.h>
#include <About.h>

CAboutDlg::CAboutDlg() : BaseDialog(IDD_ABOUTBOX, SHIDIF_DONEBUTTON | SHIDIF_FULLSCREENNOMENUBAR)
{
}

LRESULT CAboutDlg::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = false;

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
      SetDlgItemText(IDC_VERSION, ver.c_str());

   return TRUE;
}

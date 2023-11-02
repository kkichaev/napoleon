/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* О программе
*
*  ert   04/07/2008   creating
*/ 
#ifndef __ABOUT_H
#define __ABOUT_H

//
// About Dialog
//
class CAboutDlg : public BaseDialog
{
public:
   CAboutDlg();
   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(CAboutDlg)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
};

#endif

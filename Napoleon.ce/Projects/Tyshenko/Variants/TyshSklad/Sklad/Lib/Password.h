/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог пароля
 *
 *  ert   14/10/2008   creating
 */ 

#ifndef __PWD_DLG_H
#define __PWD_DLG_H

#include <BaseDialog.h>

class Password : public BaseDialog//CStdSimpleDialog<IDD_ADMPWD, SHIDIF_SIZEDLG>
{
public:
   std::wstring password;

   Password() : BaseDialog(IDD_ADMPWD) {}
   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(Password)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
};

#endif

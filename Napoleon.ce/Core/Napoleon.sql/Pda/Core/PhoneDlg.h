/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Диалог позвонить/отправить SMS
 *
 *  ert   25/11/2008   creating
 */
#ifndef __PHONE_DLG
#define __PHONE_DLG

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <BaseDialog.h>

class PhoneDlg : public BaseDialog
{
public:
   PhoneDlg(const wchar_t *number);

   BEGIN_MSG_MAP(PhoneDlg)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)

      COMMAND_ID_HANDLER(IDC_CALL, Close)
      COMMAND_ID_HANDLER(IDC_SMS, Close)

      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   const wchar_t* Text() const { return text.c_str(); }

protected:
   const wchar_t *number;
   std::wstring text;

   LRESULT OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   LRESULT Close(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
};


#endif

/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic MainForm
*
*  ert   16/08/2021   creating
*/
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>

#include "Preference.h"
#include <Password.h>
#include <PrfDlg.h>
#include "FormEntries.h"

#include "AppBaseForm.h"

class LoginData : public IFormData
{
};

class LoginForm : public AppBaseForm, CMessageFilter
{
public:
	LoginForm() {}
	~LoginForm() { delete data; }

	virtual DWORD GetResourceID() const { return IDC_LOGIN_DLG; }
   virtual DWORD GetMenuBarID() const { return -1; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(LoginForm)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      COMMAND_ID_HANDLER(IDCANCEL, OnCancel)
      COMMAND_ID_HANDLER(IDC_BACK, OnCancel)
      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

	DECLARE_FORM(LoginForm, IDC_LOGIN_DLG);

   virtual bool SetData(IFormData *_data)
   {
      data = (LoginData *)_data;

		CButton b(GetDlgItem(IDOK));
		b.SetButtonStyle(BS_DEFPUSHBUTTON, FALSE);

		CEdit edit((CEdit)GetDlgItem(IDC_PWD));
		edit.LimitText(MAX_PASSWORD);	

		GetDlgItem(IDC_LOGIN).SetFocus();

		CMessageLoop *ml = _Module.GetMessageLoop();
		if( ml != NULL )
			ml->AddMessageFilter(this);
      return true;
   }

   virtual void Destroy()
	{
		CMessageLoop *ml = _Module.GetMessageLoop();
		if( ml != NULL )
			ml->RemoveMessageFilter(this);
		AppBaseForm::Destroy();
	}

	virtual void UpdateLayout(const RECT& bounds, bool forceRecalc) 
	{
		int offset = 2;
		DWORD hgh = bounds.bottom - bounds.top;
		DWORD wdh = bounds.right - bounds.left;

		CWindow w = GetDlgItem(IDOK);
		CRect rb;
		w.GetWindowRect(rb);
		ScreenToClient(rb);

		GetDlgItem(IDOK).MoveWindow(bounds.left, hgh - rb.Height() - offset, rb.Width(), rb.Height(), FALSE);

		rb.OffsetRect(rb.Width() + 5, 0);
		GetDlgItem(IDCANCEL).MoveWindow(rb.left, hgh - rb.Height() - offset, rb.Width(), rb.Height(), FALSE);
	}

	LRESULT OnCancel(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
		_Module.GetFrame()->Quit();
      return 0;
   }

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
	{
		Preference p;
		p.Load();

		wchar_t buf[MAX_PASSWORD];
		int len;
		len = GetDlgItemText(IDC_LOGIN, buf, MAX_PASSWORD);
		if(len == 0)
		{
			return 1;
		}
		wcstombs(p.login, buf, sizeof(p.login));

		len = GetDlgItemText(IDC_PWD, buf, MAX_PASSWORD);
		wcstombs(p.password, buf, sizeof(p.password));

		p.Save();

		LoggedIn = true;
		OpenMainForm();
		return 1;
	}

	virtual BOOL PreTranslateMessage(MSG* pMsg)
	{
		BOOL ret = FALSE;
		if( pMsg->message == WM_KEYDOWN )
		{
			WPARAM key = pMsg->wParam;
			switch(key)
			{
	      case VK_RETURN:
			case VK_UP:
			case VK_TAB:
			case VK_DOWN:
				ret = MoveToNext(key == VK_RETURN);
				break;
			}
		}

		return ret;
	}

	BOOL MoveToNext(bool byReturn)
	{
		bool isFirst = (GetFocus() == GetDlgItem(IDC_LOGIN));

		if(byReturn && !isFirst)
		{
			BOOL bh = TRUE;
			OnOK(0, 0, 0, bh);
			return TRUE;
		}

		int id = isFirst ? IDC_PWD : IDC_LOGIN;

		GetDlgItem(id).SetFocus();
		return TRUE;
	}

	LoginData *data;
};


IMPLEMENT_FORM(LoginForm);


void OpenLoginForm()
{
	_Module.GetFrame()->Load(IDC_LOGIN_DLG, new LoginData());
}
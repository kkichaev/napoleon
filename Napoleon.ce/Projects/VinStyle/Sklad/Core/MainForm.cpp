/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic MainForm
*
*  ert   03/09/2010   creating
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

class MainFormData : public IFormData
{
public:
protected:
};

class MainForm : public AppBaseForm, CMessageFilter
{
public:
   MainForm() {}
   ~MainForm() { delete data; }

   DECLARE_FORM(MainForm, IDR_MAIN_FRAME)

   virtual DWORD GetResourceID() const { return IDR_MAIN_FRAME; }
   virtual DWORD GetMenuBarID() const { return IDR_MAIN_FRAME; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(MainForm)
      COMMAND_ID_HANDLER(IDC_PREFERENCE, ChangePreferences)
      COMMAND_ID_HANDLER(IDD_ORDER_LIST, OpenOrders)
		COMMAND_ID_HANDLER(IDD_INVENT, DoOpenInvent)
		COMMAND_ID_HANDLER(IDD_CHK_RACK, DoChkRack)
      COMMAND_ID_HANDLER(IDC_QUIT, OnQuit)
      
      COMMAND_ID_HANDLER(IDC_SYNC, Sync)
      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);
   virtual void Destroy();
	virtual BOOL PreTranslateMessage(MSG* pMsg);

protected:
   LRESULT OpenOrders(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
	
   LRESULT DoChkRack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT DoOpenInvent(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnKeyDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

   BOOL MoveToNext(bool next);
   BOOL PressEnter();

protected:
   MainFormData *data;
   CMenuBarCtrl menuBar;
};

IMPLEMENT_FORM(MainForm)

bool MainForm::SetData(IFormData* _data)
{
   data = (MainFormData *)_data;
   //SetFontToChild(46, true);

   GetDlgItem(IDD_ORDER_LIST).SetFocus();

   CMessageLoop *ml = _Module.GetMessageLoop();
   if( ml != NULL )
      ml->AddMessageFilter(this);

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
      SetDlgItemText(IDC_VERSION, ver.c_str());
   return true;
}

BOOL MainForm::PreTranslateMessage(MSG* pMsg)
{
   BOOL ret = FALSE;
   if( pMsg->message == WM_KEYDOWN )
   {
      WPARAM key = pMsg->wParam;
      switch(key)
      {
      case VK_UP:
      case VK_DOWN:
         ret = MoveToNext(key == VK_DOWN);
         break;
      case VK_RETURN:
         ret = PressEnter();
         break;
      }
   }

   return ret;
}

LRESULT MainForm::OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
   _Module.GetFrame()->Quit();
   return 0;
}

static bool CheckClass(HWND hWnd, const wchar_t* className)
{
   bool res = false;
   wchar_t buf[100];
   if( GetClassName(hWnd, buf, sizeof(buf)/sizeof(buf[0])) > 0 )
      res = (wcsicmp(buf, className) == 0);

   return res;
}

BOOL MainForm::MoveToNext(bool next)
{
   HWND hWnd = GetFocus();
   if( hWnd == NULL || !CheckClass(hWnd, L"BUTTON") )
      return FALSE;

   BOOL ret = FALSE;
   hWnd = GetNextDlgTabItem(hWnd, (next) ? FALSE : TRUE);
   if( hWnd != NULL && CheckClass(hWnd, L"BUTTON") )
   {
      ::SetFocus(hWnd);
      ret = TRUE;
   }

   return ret;
}

BOOL MainForm::PressEnter()
{
   HWND hWnd = GetFocus();
   if( hWnd == NULL || !CheckClass(hWnd, L"BUTTON") )
      return FALSE;

   int id = ::GetWindowLong(hWnd, GWL_ID);
   PostMessage(WM_COMMAND, MAKELONG(LOWORD(id), BN_CLICKED), (LPARAM)hWnd);
   return TRUE;
}

void MainForm::Destroy()
{
   CMessageLoop *ml = _Module.GetMessageLoop();
   if( ml != NULL )
      ml->RemoveMessageFilter(this);
   AppBaseForm::Destroy();
}

LRESULT MainForm::ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();
   
   HWND focused = GetFocus();

   int adminPreference = CanLoadAdminPreference();
   if( adminPreference < 0 )
      return 0;

   PreferenceDialog pd(&p, (adminPreference == 1));
   if( pd.DoModal() == IDOK )
   {
   }

   ::SetFocus(focused);
   return 0;
}

LRESULT MainForm::OpenOrders(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenOrderList();
   return 0;
}

LRESULT MainForm::DoOpenInvent(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenInvent();
   return 0;
}

LRESULT MainForm::DoChkRack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenChkRack();
   return 0;
}

LRESULT MainForm::Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenSyncForm();
   return 0;
}

void OpenMainForm()
{
   _Module.GetFrame()->Load(IDR_MAIN_FRAME, new MainFormData());
}
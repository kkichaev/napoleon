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

bool LoggedIn = false;
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
      COMMAND_ID_HANDLER(IDD_ORDER_LIST, OpenDeliveries)
      COMMAND_ID_HANDLER(IDD_INCOME_LIST, OpenIncomes)
      COMMAND_ID_HANDLER(IDD_INVENT_LIST, OpenInvents)
		
      COMMAND_ID_HANDLER(IDC_SYNC, Sync)

      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);
   virtual void Destroy();
	virtual BOOL PreTranslateMessage(MSG* pMsg);

protected:
   LRESULT OpenDeliveries(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OpenIncomes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OpenInvents(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnKeyDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	
   BOOL MoveToNext(bool next);
   BOOL PressEnter();

protected:
   MainFormData *data;
   CMenuBarCtrl menuBar;
	CFont boldFont, refFont;
};

IMPLEMENT_FORM(MainForm)

bool MainForm::SetData(IFormData* _data)
{
   data = (MainFormData *)_data;
   //SetFontToChild(46, true);

	CButton chWnd(GetDlgItem(IDD_ORDER_LIST));
	chWnd.SetFocus();

   CMessageLoop *ml = _Module.GetMessageLoop();
   if( ml != NULL )
      ml->AddMessageFilter(this);

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
      SetDlgItemText(IDC_VERSION, ver.c_str());

	_Module.GetFrame()->LoadMenuBar(0, 0, SHCMBF_HIDESIPBUTTON);

	//LOGFONT lf;
	//refFont.Attach(chWnd.GetFont());
	//refFont.GetLogFont(&lf);
	//lf.lfHeight -= 2;
	//boldFont.CreateFontIndirect(&lf);
	//chWnd.SetFont(boldFont);

	chWnd.SetButtonStyle(BS_DEFPUSHBUTTON, FALSE);

	if(!LoggedIn)
		OpenLoginForm();

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
		case VK_ESCAPE:
			OnQuit(0, 0, 0, ret);
			ret = TRUE;
			break;
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
	CButton chWnd(GetFocus());
	if( chWnd.IsWindow() == FALSE || !CheckClass(chWnd.m_hWnd, L"BUTTON") )
      return FALSE;

   BOOL ret = FALSE;
	CButton hWnd(GetNextDlgTabItem(chWnd.m_hWnd, (next) ? FALSE : TRUE));
	if( hWnd.IsWindow() == TRUE && CheckClass(hWnd.m_hWnd, L"BUTTON") )
   {
		hWnd.SetFocus();
      ret = TRUE;
   }

	if(ret)
	{
		chWnd.SetButtonStyle((chWnd.GetButtonStyle() & (~BS_DEFPUSHBUTTON)), TRUE);
		hWnd.SetButtonStyle(BS_DEFPUSHBUTTON, TRUE);

		//chWnd.SetFont(refFont);
		//hWnd.SetFont(boldFont);
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

   //int adminPreference = CanLoadAdminPreference();
   //if( adminPreference < 0 )
   //   return 0;

   PreferenceDialog pd(&p, true); //(adminPreference == 1));
   if( pd.DoModal() == IDOK )
   {
   }

   ::SetFocus(focused);
   return 0;
}

LRESULT MainForm::OpenDeliveries(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenDocList(DtDeliveries);
   return 0;
}

LRESULT MainForm::OpenIncomes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenDocList(DtIncomes);
   return 0;
}

LRESULT MainForm::OpenInvents(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenDocList(DtInvent);
   return 0;
}

LRESULT MainForm::Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   bool clearBase = true;
   bool rcvData = true; //(IsDlgButtonChecked(IDC_RCV_PRICE) == BST_CHECKED);
   bool sndDocs = true; // (IsDlgButtonChecked(IDC_SEND) == BST_CHECKED);

   std::wstring answer;
   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   long ec = 0;

   if( sndDocs )
   {
      ec = _Module.ExportDocuments(&answer, &pw);
      if( ec )
      {
         if( ec == neNoDocuments )
            ec = 0;
         else
            _Module.ShowErrorBox(ec, answer.c_str(), IDS_ERROR_IN_TX);
		}
   }

   if( ec == 0 && clearBase)// && !rcvData )
   {
      _Module.BaseRemove();
   }

   if( ec == 0 && rcvData )
   {
      ec = _Module.ReceiveData(&answer, &pw, clearBase);
      if( ec )
         _Module.ShowErrorBox(ec, answer.c_str(), IDS_ERROR_IN_SYNC);
   }

   pw.DestroyWindow();
   //OpenSyncForm();
   return 0;
}

void OpenMainForm()
{
   _Module.GetFrame()->Load(IDR_MAIN_FRAME, new MainFormData());
}
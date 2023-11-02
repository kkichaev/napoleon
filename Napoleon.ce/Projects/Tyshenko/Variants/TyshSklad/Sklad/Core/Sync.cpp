/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* Синхронизация
* 
*  ert   21/07/2008   creating
*/ 
#include "stdafx.h"
#include <Module.h>
#include "MainFrame.h"
#include <map>

#include "Progress.h"
#include "BaseDialog.h"
#include "About.h"
#include "FormEntries.h"
#include <NapoleonRes.h>
#include "Progress.h"

#include <StdFuncs.h>

#include "TopApp.h"

#include <NplConfig.h>
#include "AppBaseForm.h"
#include "PrfDlg.h"

class SyncFormData : public IFormData
{
public:
protected:
};

class SyncForm : public AppBaseForm
{
public:
   SyncForm() {}
   ~SyncForm() { delete data; }

   DECLARE_FORM(SyncForm, IDD_SYNC)

   virtual DWORD GetResourceID() const { return IDD_SYNC; }
   virtual DWORD GetMenuBarID() const { return -1; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual void Destroy()
   {
      UnregisterHotKeys(m_hWnd, true);
      AppBaseForm::Destroy();
   }

   BEGIN_MSG_MAP(SyncForm)
      MESSAGE_HANDLER(WM_HOTKEY, OnHotKey)
      COMMAND_HANDLER(IDC_CLEAR_BASE, BN_CLICKED, OnClearClick)
      COMMAND_ID_HANDLER(IDC_PREFERENCE, OnPreference)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      COMMAND_ID_HANDLER(ID_SYNC_PART, OnPartSync)
      COMMAND_ID_HANDLER(IDCANCEL, OnCancel)
      COMMAND_ID_HANDLER(IDC_BACK, OnCancel)
      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data)
   {
      data = (SyncFormData *)_data;

		CButton b(GetDlgItem(IDOK));
		b.SetFocus();
		b.SetButtonStyle(BS_DEFPUSHBUTTON, FALSE);

      //SetFontToChild(26, false);

      //CheckDlgButton(IDC_RCV_PRICE, BST_CHECKED);
      //CheckDlgButton(IDC_SEND, BST_CHECKED);

      //RegisterHotKeys(m_hWnd, true);
      return true;
   }

   LRESULT OnHotKey(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      if(wParam == PK1 || wParam == PK2 )
         PostMessage(WM_COMMAND, (wParam == PK1) ? IDOK : IDCANCEL, 0);

      return 0;
   }

   LRESULT OnClearClick(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( IsDlgButtonChecked(IDC_CLEAR_BASE) == BST_CHECKED )
      {
         if( MessageBox(IDS_CLEAR_BASE_ALERT, IDS_WARNING, MB_YESNO|MB_ICONQUESTION) != IDYES )
         {
            CheckDlgButton(IDC_CLEAR_BASE, BST_UNCHECKED);
         }
      }
      return 0;
   }

   LRESULT OnCancel(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      OpenMainForm();
      return 0;
   }

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT OnPartSync(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   LRESULT OnPreference(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      Preference p;
      p.Load();

      UnregisterHotKeys(m_hWnd, true);

      //int adminPreference = CanLoadAdminPreference();
      //if( adminPreference < 0 )
      //   return 0;

      PreferenceDialog pd(&p, true); //(adminPreference == 1));
      pd.DoModal();

      RegisterHotKeys(m_hWnd, true);
      return 0;
   }

	void DoSync(bool fullExchange);

protected:
   SyncFormData *data;
};

IMPLEMENT_FORM(SyncForm)

LRESULT SyncForm::OnPartSync(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	DoSync(false);
   return 0;
}


LRESULT SyncForm::OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
	DoSync(true);
   return 0;
}

void SyncForm::DoSync(bool fullExchange)
{
   bool clearBase = (IsDlgButtonChecked(IDC_CLEAR_BASE) == BST_CHECKED);
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
   if( ec == 0 )
   {
      MessageBox(IDS_SYNC_ENDED, IDS_MESSAGE, MB_OK);
      OpenMainForm();
   }
}


void OpenSyncForm()
{
   _Module.GetFrame()->Load(IDD_SYNC, new SyncFormData());
}
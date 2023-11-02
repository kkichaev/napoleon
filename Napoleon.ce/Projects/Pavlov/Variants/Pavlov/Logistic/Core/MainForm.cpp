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
#include <ListForm.h>

#include "Preference.h"
#include <Password.h>
#include <PrfDlg.h>
#include "FormEntries.h"

class MainFormData : public IFormData
{
public:
   std::vector<AgentsImpl> agents;

   void Refresh();

protected:
   StringHolder sh;
};

class MainForm : public BaseForm
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
      COMMAND_ID_HANDLER(IDC_DOC_LIST, DocList)
      COMMAND_ID_HANDLER(IDC_COST, OpenCost)
      COMMAND_ID_HANDLER(IDC_REST, OpenRest)
      COMMAND_ID_HANDLER(IDC_SYNC, Sync)
      COMMAND_CODE_HANDLER(EN_SETFOCUS, OnSetFocus)
      COMMAND_CODE_HANDLER(EN_KILLFOCUS, OnKillFocus)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);

protected:
   LRESULT OnSetFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnKillFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OpenCost(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OpenRest(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT DocList(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   void Refresh(bool clearAgent);

   bool CheckLogin();

protected:
   MainFormData *data;
   CMenuBarCtrl menuBar;
};

IMPLEMENT_FORM(MainForm)

void MainFormData::Refresh()
{
   AgentsImpl a;
   SQLTable t(a.Name());

   agents.clear();
   sh.Clear();

   bool bdo = t.Select(&a, L"ORDER BY name");
   while( bdo )
   {
      UnbindingItem(&a, &sh);
      agents.push_back(a);
      bdo = t.SelectNext(&a);
   }
}

LRESULT MainForm::OnSetFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   SHSipPreference(m_hWnd, (id == IDC_PWD) ? SIP_UP : SIP_DOWN);
   return 0;
}

LRESULT MainForm::OnKillFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( id == IDC_PWD )
      SHSipPreference(m_hWnd, SIP_DOWN);
   return 0;
}

bool MainForm::SetData(IFormData* _data)
{
   data = (MainFormData *)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(),0,0));

   Refresh(false);

   return true;
}

LRESULT MainForm::ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   Password dlg;
   if( dlg.DoModal() == IDOK )
   {
      wchar_t pwd[MAX_PASSWORD];
      mbstowcs(pwd, p.password, MAX_PASSWORD);
      if( wcscmp(pwd, dlg.password.c_str()) == 0 )
      {
         PreferenceDialog pd(&p);
         pd.DoModal();
      }
   }
   return 0;
}

LRESULT MainForm::Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   std::wstring answer;
   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   long ec = _Module.Sync(&answer, &pw);
   pw.DestroyWindow();

   if( ec != 0 )
   {
      _Module.ShowErrorBox(ec, answer,  L"Ошибка при приеме:\n");
   } else
   {
      Refresh(true);
      MessageBox(L"Синхронизация завершена", L"Информация", MB_OK|MB_ICONINFORMATION);
   }

   return 0;
}

LRESULT MainForm::DocList(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( CheckLogin() )
      OpenDocList();
   return 0;
}

LRESULT MainForm::OpenCost(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( CheckLogin() )
   {
      OpenPartnerList();
   }
   return 0;
}

LRESULT MainForm::OpenRest(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( CheckLogin() )
   {
      OpenDoc(new WhDocImpl());
   }
   return 0;
}

void MainForm::Refresh(bool clearAgent)
{
   data->Refresh();

   if( clearAgent )
      _Module.ClearAgent();

   const AgentsImpl* a = _Module.Agent();
   std::wstring curID;
   if( a != NULL )
      curID = a->id;

   CComboBox agents(GetDlgItem(IDC_USER));
   agents.ResetContent();

   std::vector<AgentsImpl>::const_iterator i = data->agents.begin();
   for( ; i != data->agents.end(); i++ )
   {
      int index = agents.AddString(i->name);
      if( curID.compare(i->id) == 0 )
         agents.SetCurSel(index);
   }

   GetDlgItem(IDC_PWD).SetWindowText(L"");
}

bool CheckPassword(HWND hWnd, const AgentsImpl& a)
{
   bool res = false;

   CWindow pwd(GetDlgItem(hWnd, IDC_PWD));
   int len = pwd.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));

   pwd.GetWindowText(buf, len);
   pwd.SetWindowText(L"");

   res = (wcscmp(a.password, buf) == 0);
   if(!res)
   {
      MessageBox(hWnd, L"Введен неверный пароль", L"Ошибка", MB_OK | MB_ICONSTOP);
   }

   return res;
}

bool MainForm::CheckLogin()
{
   bool res = false;

   CComboBox agents(GetDlgItem(IDC_USER));
   int curSel = agents.GetCurSel();
   if( curSel >= 0 && (unsigned)curSel < data->agents.size() )
   {
      const AgentsImpl &a = data->agents.at(curSel);
      if( CheckPassword(m_hWnd, a) )
      {
         res = true;
         _Module.SetAgent(a);

         SHSipPreference(m_hWnd, SIP_DOWN);
      } 
   }

   return res;
}

void OpenMainForm()
{
   _Module.GetFrame()->Load(IDR_MAIN_FRAME, new MainFormData());
}
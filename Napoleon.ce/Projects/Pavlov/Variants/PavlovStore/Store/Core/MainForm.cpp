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
#include "MainFrame.h"

typedef BOOL (*pSysSetFxKeyState)(DWORD dwVKCode,BOOL dwEnableState);

class MainFormData : public IFormData
{
public:
protected:
};

class MainForm : public AppBaseForm
{
public:
   MainForm() {}
   ~MainForm() { delete data; }

   DECLARE_FORM(MainForm, IDR_MAIN_FRAME)

   virtual DWORD GetResourceID() const { return IDR_MAIN_FRAME; }
   virtual DWORD GetMenuBarID() const { return IDR_MAIN_FRAME; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(MainForm)
      COMMAND_HANDLER(IDC_AGENTS, CBN_SELCHANGE, ChangeAgent)
      COMMAND_HANDLER(IDC_SERVERS, CBN_SELCHANGE, ChangeServer)
      COMMAND_ID_HANDLER(IDOK, OnReturn)
      COMMAND_ID_HANDLER(IDD_ORDER_LIST, OpenOrders)
      COMMAND_ID_HANDLER(IDC_QUIT, OnQuit)
      COMMAND_ID_HANDLER(IDC_F4, OnF4)
      
      COMMAND_ID_HANDLER(IDC_SYNC, Sync)
      CHAIN_MSG_MAP(AppBaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);
   virtual void Destroy();

protected:
   LRESULT OpenOrders(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT ChangeServer(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      GetDlgItem(IDC_AGENTS).SetFocus();
      return 0;
   }
   LRESULT ChangeAgent(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnReturn(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnF4(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   //LRESULT ChangePreferences(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sync(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnKeyDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

   BOOL MoveToNext(bool next);

protected:
   MainFormData *data;
   CMenuBarCtrl menuBar;
   std::vector<Agents*> agents;
   std::vector<std::wstring*> ips;
   StringHolder holder;
};

IMPLEMENT_FORM(MainForm)


LRESULT MainForm::OnF4(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenSyncForm();
   return 0;
}

LRESULT MainForm::ChangeAgent(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   GetDlgItem(IDC_PASSWORD).SetFocus();
   return 0;
}

LRESULT MainForm::OnReturn(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( GetFocus() == GetDlgItem(IDC_PASSWORD) )
      OpenOrders(nCode, id, hWnd, bHandled);

   return 0;
}

bool MainForm::SetData(IFormData* _data)
{
   data = (MainFormData *)_data;

   ((MainFrame*)_Module.GetFrame())->SetCurAgent(L"", L"", L"");
   //SetFontToChild(46, true);

   CComboBox cbAgents(GetDlgItem(IDC_AGENTS));
   SQLTable t(AgentImpl().Name());
   Agents *agent = new Agents();
   bool bdo = t.Select(agent);
   while( bdo )
   {
      agents.push_back(agent);
      Agents* ref = agent;
      UnbindingItem(ref, &holder);

      int cp = cbAgents.AddString(ref->name);
      cbAgents.SetItemDataPtr(cp, ref);

      agent = new Agents();
      bdo = t.SelectNext(agent);
   }

   delete agent;

   CComboBox cbServers(GetDlgItem(IDC_SERVERS));
   ServerImpl serv;
   SQLTable st(serv.Name());
   bdo = st.Select(&serv, L"order by name");
   while( bdo )
   {
      std::wstring *tip = new std::wstring(serv.ip);
      RTrim(tip);
      ips.push_back(tip);
      int idx = cbServers.AddString(serv.name);
      cbServers.SetItemDataPtr(idx, (void*)tip->c_str());

      bdo = st.SelectNext(&serv);
   }
   cbServers.SetFocus();

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
      SetDlgItemText(IDC_VERSION, ver.c_str());

   if( agents.size() == 0 )
   {
      if( ::MessageBox(NULL, L"В программе нет агентов. Открыть окно синхронизации?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
         OpenSyncForm();
   }

   SetFKey(VK_F4, FALSE);
   return true;
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

void SetFKey(int vk, BOOL set)
{
   HMODULE hm = LoadLibrary(L"syslib.dll");
   pSysSetFxKeyState keyFn = (pSysSetFxKeyState)GetProcAddress(hm, L"SysSetFxKeyState");
   if( keyFn )
      keyFn(vk, set);
   FreeLibrary(hm);
}

void MainForm::Destroy()
{
   std::vector<Agents*>::iterator ai = agents.begin();
   for( ; ai != agents.end(); ai++ )
      delete (*ai);

   std::vector<std::wstring*>::iterator ii = ips.begin();
   for( ; ii != ips.end(); ii++ )
      delete (*ii);

   AppBaseForm::Destroy();

   SetFKey(VK_F4, TRUE);
}

LRESULT MainForm::OpenOrders(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CComboBox servs(GetDlgItem(IDC_SERVERS));
   int idx = servs.GetCurSel();
   if( idx < 0 )
   {
      ::MessageBox(NULL, L"Не выбран сервер.", L"Ошибка", MB_ICONSTOP | MB_OK);
      return 0;
   }
   const wchar_t *ip = (const wchar_t *)servs.GetItemDataPtr(idx);

   CComboBox agents(GetDlgItem(IDC_AGENTS));
   idx = agents.GetCurSel();
   if( idx < 0 )
   {
      ::MessageBox(NULL, L"Не выбран ревизор. Выберите ревизора, и введите пароль.", L"Ошибка", MB_ICONSTOP | MB_OK);
      return 0;
   }

   std::wstring str;
   GetString(&str, GetDlgItem(IDC_PASSWORD));
   AgentImpl* a = (AgentImpl*)agents.GetItemDataPtr(idx);
   if( wcscmp(a->password, str.c_str()) != 0 )
   {
      ::MessageBox(NULL, L"Неправильный пароль. Пожалуйста, введите правильный пароль.", L"Ошибка", MB_ICONSTOP | MB_OK);
      return 0;
   }

   ((MainFrame*)_Module.GetFrame())->SetCurAgent(a->login, a->password, ip);
   OpenOrderList();
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
/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Фрейм 
 * 
 *  ert   13/08/2007   creating
 */ 
#ifndef __MAIN_FRAME_H
#define __MAIN_FRAME_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <BaseForm.h>
#include <BaseFrame.h>
#include <NapoleonRes.h>
#include <ListForm.h>
#include <ZBCRLib.h>
#include "Exchange.h"

typedef CWinTraits< WS_CLIPCHILDREN | WS_CLIPSIBLINGS /*| WS_NONAVDONEBUTTON*/,0> MainFrameTraits;

struct BarcodeHandler
{
   virtual void HandleEvent() = 0;
};


class MainFrame : public BaseFrame<MainFrame, MainFrameTraits>, public CMessageFilter
{
public:
   typedef BaseFrame<MainFrame, MainFrameTraits> BaseClass;

   BEGIN_MSG_MAP(MainFrame)
      COMMAND_ID_HANDLER(IDC_QUIT, OnQuit)
      COMMAND_ID_HANDLER(IDC_ABOUT, About)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      MESSAGE_HANDLER(WM_ACTIVATE, OnActivate)
      MESSAGE_HANDLER(WM_SIZE, OnSize)
      MESSAGE_HANDLER(WM_LOAD_FORM, OnLoadForm)
      MESSAGE_HANDLER(WM_BCR_NOTIFY, OnBarcodeNotify)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   MainFrame();
   ~MainFrame();

   virtual BOOL PreTranslateMessage(MSG* pMsg);
   virtual HWND LoadMenuBar(DWORD barID, DWORD barV5 = 0, DWORD flags = SHCMBF_HIDESIPBUTTON);

   void RefreshCurrent() { if ( current ) current->Refresh(); }

   virtual void Quit();

   void SetBarcodeHandler(BarcodeHandler* newHandler) { bcHandler = newHandler; }

   void SetCurAgent(const wchar_t* login, const wchar_t* password, const wchar_t* ip);
   const wchar_t* GetCurLogin() const { return login.c_str(); }
   const wchar_t* GetCurPassword() const { return password.c_str(); }
   const wchar_t* GetIP() const { return ip.c_str(); }

   // message handlers
protected:
   LRESULT About(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT OnActivate(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);
   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT OnLoadForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT OnBarcodeNotify(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);   

   virtual LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   virtual void CameraActive(bool active) {}

   BarcodeHandler *bcHandler;

   std::wstring ip, login, password;
};



#endif

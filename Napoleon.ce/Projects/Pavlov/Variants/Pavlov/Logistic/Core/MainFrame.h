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

typedef CWinTraits< WS_CLIPCHILDREN | WS_CLIPSIBLINGS /*| WS_NONAVDONEBUTTON*/,0> MainFrameTraits;

class MainFrame : public BaseFrame<MainFrame, MainFrameTraits>
{
public:
   typedef BaseFrame<MainFrame, MainFrameTraits> BaseClass;

   BEGIN_MSG_MAP(MainFrame)
      COMMAND_ID_HANDLER(IDC_QUIT, OnQuit)
      COMMAND_ID_HANDLER(IDC_ABOUT, About)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      MESSAGE_HANDLER(WM_ACTIVATE, OnActivate)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   MainFrame(){}
   ~MainFrame();

   // message handlers
protected:
   LRESULT About(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT OnActivate(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);
   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

   virtual LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   virtual void CameraActive(bool active) {}

};



#endif

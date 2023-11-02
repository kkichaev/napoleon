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

const int WM_ACTIVATE_CAMERAVIEW = WM_USER + 100;

class MainFrame : public BaseFrame<MainFrame, MainFrameTraits>, IPreferenceChangeHandler
{
public:
   typedef BaseFrame<MainFrame, MainFrameTraits> BaseClass;

   BEGIN_MSG_MAP(MainFrame)
      COMMAND_ID_HANDLER(IDC_QUIT, OnQuit)
      COMMAND_ID_HANDLER(IDC_ABOUT, About)
      COMMAND_ID_HANDLER(IDC_SYNC, Sync)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      //COMMAND_ID_HANDLER(IDC_REMOVE_EMPTY_ORGS, RemoveEmptyOrgs)
      //COMMAND_ID_HANDLER(IDC_REGISTER, Register)
		MESSAGE_HANDLER(WM_COPYDATA, OnNewInstance)
      MESSAGE_HANDLER(WM_ACTIVATE, OnActivate)
      MESSAGE_HANDLER(WM_ACTIVATE_CAMERAVIEW, ActivateCamera)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   MainFrame() : cameraActive(false) {}
   ~MainFrame();

   // специфические функции
public:
   virtual void PreferenceChanged();

   virtual void CameraActive(bool active);

   static bool MakePhoto(HWND hWnd, std::wstring* photo);

   // message handlers
protected:
   LRESULT Register(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT About(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT Sync(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   //LRESULT RemoveEmptyOrgs(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
   LRESULT OnActivate(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT ActivateCamera(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);

   LRESULT OnQuit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

   virtual LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   LRESULT OnNewInstance(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);

   void StartTopApp();
   void StopTopApp();
   int RemoveEmptyOrgs();


   bool cameraActive;
};

#ifdef GPS_POS
struct ModuleStates;
void RefreshIco(const ModuleStates* newState); // in OrgFuncs
#endif

#endif

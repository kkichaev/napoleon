/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   24/08/2007   creating
 */ 
#ifndef __PREFERENCE_DLG_H
#define __PREFERENCE_DLG_H

#include "PropDialog.h"
#include "Preference.h"

class PreferenceDialog;
class PrefPage : public PropPage
{
public:
   PrefPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}

   virtual void Save(Preference *preference) = 0;
};

//
// ---------------------- Price Properties ------------------
//
class PriceProperties : public PrefPage
{
public:
   PriceProperties(bool _adminPreference);

   BEGIN_MSG_MAP(PriceProperties)
      COMMAND_HANDLER(IDC_COLUMN2, CBN_SELCHANGE, CheckColumn3)
      CHAIN_MSG_MAP(PrefPage)
   END_MSG_MAP()

   bool adminPreference;

   LRESULT CheckColumn3(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);

   virtual void Init();
   virtual void Save(Preference *preference);

#ifdef Provisia
   StringHolder sh;
#endif
};

//
// ---------------------- Network Properties ------------------
//
class NetworkProperties : public PrefPage
{
public:
   NetworkProperties();

   BEGIN_MSG_MAP(NetworkProperties)
      CHAIN_MSG_MAP(PropPage)
   END_MSG_MAP()

   virtual void Init();
   virtual void Save(Preference *preference);

protected:
   void LoadAccessPoint();
   void SaveAccessPoint();
};

class UpdateProperties : public PrefPage
{
public:
   UpdateProperties();

   virtual void Init();
   virtual void Save(Preference *preference);
};

#ifdef GPS_POS
class GPSProperties : public PrefPage
{
public:
   GPSProperties();

   BEGIN_MSG_MAP(GPSProperties)
      COMMAND_HANDLER(IDC_TIME_END, CBN_SELCHANGE, CheckButton)
      COMMAND_HANDLER(IDC_TIME_START, EN_SETFOCUS, CheckButton)
      COMMAND_ID_HANDLER(IDC_FIND, FindPort)
      CHAIN_MSG_MAP(PropPage)
   END_MSG_MAP()

   virtual void Init();
   virtual void Save(Preference *preference);

protected:
   LRESULT CheckButton(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);
   LRESULT FindPort(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);
};
#endif

#ifdef VISIT_DOC
class PhotoProperties : public PrefPage
{
public:
   PhotoProperties();

   BEGIN_MSG_MAP(PhotoProperties)
      COMMAND_HANDLER(IDC_PHOTO, CBN_SELCHANGE, OnSelChange)
      CHAIN_MSG_MAP(PropPage)
   END_MSG_MAP()

   virtual void Init();
   virtual void Save(Preference *preference);
   LRESULT OnSelChange(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);
};
#endif

#if defined(VAN_SELLING)
class PrintProperties : public PrefPage
{
public:
   PrintProperties();
   ~PrintProperties();

   BEGIN_MSG_MAP(PrintProperties)
      MESSAGE_HANDLER(WM_DESTROY, OnDestroy)
      COMMAND_ID_HANDLER(IDC_REFRESH, OnRefresh)
      CHAIN_MSG_MAP(PropPage)
   END_MSG_MAP()

   LRESULT OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT OnRefresh(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   virtual void Init();
   virtual void Save(Preference *preference);

protected:
   HINSTANCE hPrint;
};
#endif

#ifdef ORDER_ONLINE
class OnlineProperties : public PrefPage
{
public:
   OnlineProperties();

   virtual void Init();
   virtual void Save(Preference *preference);
};
#endif

class PreferenceDialog : public PropDialog
{
public:
   PreferenceDialog();
   ~PreferenceDialog();

   virtual bool OnOK();

   const Preference& GetPreference() const { return preference; }

protected:
   Preference preference;
};

#endif

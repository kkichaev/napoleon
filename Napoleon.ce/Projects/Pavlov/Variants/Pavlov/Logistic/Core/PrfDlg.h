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
};

class PreferenceDialog : public PropDialog
{
public:
   PreferenceDialog(Preference* p);
   ~PreferenceDialog();

   virtual bool OnOK();

   const Preference& GetPreference() const { return *preference; }

protected:
   Preference *preference;
};

#endif

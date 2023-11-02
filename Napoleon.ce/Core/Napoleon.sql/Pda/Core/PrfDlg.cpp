/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   24/08/2007   creating
 */ 
#include "stdafx.h"
#include "PrfDlg.h"
#include "NplConfig.h"
#include <Form.h>

#include <Password.h>

//
// ---------------------- PreferenceDialog ------------------
//
static bool CanLoadAdminPreference()
{
   NapoleonConfig cfg;
   std::wstring val;
   if( cfg.ReadValue(&val, ADMPWD) )
   {
      Password dlg;
      if( dlg.DoModal() == IDOK )
         return (val.compare(dlg.password) == 0);
      return false;
   }
   return true;
}

PreferenceDialog::PreferenceDialog()
{
   preference.Load();

   bool adminPreference = CanLoadAdminPreference();
   AddPage(new PriceProperties(adminPreference));

   if( adminPreference )
   {
      AddPage(new NetworkProperties());

      std::wstring fileName;
      _Module.MakeFileName(&fileName, L"NplUpdate.exe");
      if( IsFileExist(fileName) ) AddPage(new UpdateProperties());

#ifdef GPS_POS
      AddPage(new GPSProperties());
#endif
#ifdef ORDER_ONLINE
      AddPage(new OnlineProperties());
#endif
   }
#ifdef VISIT_DOC
   AddPage(new PhotoProperties());
#endif
#ifdef VAN_SELLING
   AddPage(new PrintProperties());
#endif
}

PreferenceDialog::~PreferenceDialog()
{
}

bool PreferenceDialog::OnOK()
{
   std::vector<PropPage*>::iterator i = pages.begin();
   for( ;i != pages.end(); i++ )
      ((PrefPage*)(*i))->Save(&preference);

   preference.Save();
   return true;
}

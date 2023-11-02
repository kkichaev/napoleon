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

//
// ---------------------- NetworkProperties ------------------
//
NetworkProperties::NetworkProperties() : PrefPage(IDC_NET_PREFERENCE_DIALOG, L"Сеть") 
{
}

void NetworkProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   wchar_t buf[MAX_LOGIN+5];
   mbstowcs(buf, preference.ip, sizeof(buf)/sizeof(buf[0]));
   SetDlgItemText(IDC_IP, buf);

   mbstowcs(buf, preference.ip2, sizeof(buf)/sizeof(buf[0]));
   SetDlgItemText(IDC_IP2, buf);

   SetDlgItemInt(IDC_PORT, preference.port, FALSE);

   ((CEdit)GetDlgItem(IDC_PORT)).SetLimitText(4);

   mbstowcs(buf, preference.login, sizeof(buf)/sizeof(buf[0]));
   SetDlgItemText(IDC_LOGIN, buf);

   mbstowcs(buf, preference.password, sizeof(buf)/sizeof(buf[0]));   
   SetDlgItemText(IDC_PWD, buf);


   CComboBox port(GetDlgItem(IDC_COM_PORT));
   for( int i=0; i<10; i++ )
   {
      wchar_t buf[10];
      wsprintf(buf, L"COM%d", i);

      port.AddString(buf);
   }
   port.SetCurSel(preference.scanPort);

}

void NetworkProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   wchar_t buf[MAX_LOGIN+5];
   GetDlgItemText(IDC_IP, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->ip, buf, sizeof(preference->ip));

   GetDlgItemText(IDC_IP2, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->ip2, buf, sizeof(preference->ip2));

   GetDlgItemText(IDC_LOGIN, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->login, buf, sizeof(preference->login));

   GetDlgItemText(IDC_PWD, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->password, buf, sizeof(preference->password));

   preference->port = GetDlgItemInt(IDC_PORT, NULL, FALSE);

   CComboBox port(GetDlgItem(IDC_COM_PORT));
   preference->scanPort = (WORD)(port.GetCurSel());
}

//
// ---------------------- PreferenceDialog ------------------
//
PreferenceDialog::PreferenceDialog(Preference* p) : preference(p)
{
   AddPage(new NetworkProperties());
}

PreferenceDialog::~PreferenceDialog()
{
}

bool PreferenceDialog::OnOK()
{
   std::vector<PropPage*>::iterator i = pages.begin();
   for( ;i != pages.end(); i++ )
      ((PrefPage*)(*i))->Save(preference);

   preference->Save();
   return true;
}

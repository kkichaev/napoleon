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
// ---------------------- NetworkProperties ------------------
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
};

NetworkProperties::NetworkProperties() : PrefPage(IDC_NET_PREFERENCE_DIALOG, IDS_NETWORK) 
{
}

void NetworkProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   //wchar_t buf[MAX_LOGIN+5];

	USES_CONVERSION;

   SetDlgItemText(IDC_IP, A2W(preference.ip));
   SetDlgItemText(IDC_IP2, A2W(preference.ip2));

   SetDlgItemInt(IDC_PORT, preference.port, FALSE);

   ((CEdit)GetDlgItem(IDC_PORT)).SetLimitText(5);

   //mbstowcs(buf, preference.login, sizeof(buf)/sizeof(buf[0]));
   //SetDlgItemText(IDC_LOGIN, buf);

   //mbstowcs(buf, preference.password, sizeof(buf)/sizeof(buf[0]));   
   //SetDlgItemText(IDC_PWD, buf);

}

#define _W2A(lpw) (\
	((_lpw = lpw) == NULL) ? NULL : (\
		(_convert = (lstrlenW(_lpw)+1), \
		(_convert>INT_MAX/2) ? NULL : \
		ATLW2AHELPER((LPSTR) alloca(_convert*sizeof(WCHAR)), _lpw, _convert*sizeof(WCHAR), _acp))))


void NetworkProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

	USES_CONVERSION;

   wchar_t buf[MAX_LOGIN+5];
   std::wstring val;
   if( GetString(&val, GetDlgItem(IDC_IP)) )
   {
		const char* _ip = _W2A(val.c_str());
		strncpy(preference->ip, _ip, sizeof(preference->ip));
   }

   if( GetString(&val, GetDlgItem(IDC_IP2)) )
   {
		strncpy(preference->ip2, _W2A(val.c_str()), sizeof(preference->ip2));
   }

   //GetDlgItemText(IDC_LOGIN, buf, sizeof(buf)/sizeof(buf[0]));
   //wcstombs(preference->login, buf, sizeof(preference->login));

   //GetDlgItemText(IDC_PWD, buf, sizeof(buf)/sizeof(buf[0]));
   //wcstombs(preference->password, buf, sizeof(preference->password));

   preference->port = GetDlgItemInt(IDC_PORT, NULL, FALSE);
}


int CanLoadAdminPreference()
{
   Password dlg;
   if( dlg.DoModal() == IDOK )
      return 1;
   return -1;
   //NapoleonConfig cfg;
   //std::wstring val;
   //if( cfg.ReadValue(&val, ADMPWD) )
   //{
   //   Password dlg;
   //   if( dlg.DoModal() == IDOK )
   //      return (val.compare(dlg.password) == 0) ? 1 : 0;
   //   return -1;
   //}
   //return 1;
}
//
// ---------------------- PreferenceDialog ------------------
//
PreferenceDialog::PreferenceDialog(Preference* p, bool isAdmin) : PropDialog(), preference(p)
{
   //AddPage(new MainProperties());
   //if( isAdmin )
   {
      AddPage(new NetworkProperties());
   }

   _Module.LoadString(&title, IDS_SETTINGS);
   m_psh.pszCaption = title.c_str();
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

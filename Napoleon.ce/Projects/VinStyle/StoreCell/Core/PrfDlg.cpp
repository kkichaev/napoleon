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

   wchar_t buf[MAX_LOGIN+5];

   ConfigImpl c;
   SetDlgItemText(IDC_IP, c.LoadIP(ConfigImpl::IP1));
   SetDlgItemText(IDC_IP2, c.LoadIP(ConfigImpl::IP2));

   SetDlgItemInt(IDC_PORT, preference.port, FALSE);

   ((CEdit)GetDlgItem(IDC_PORT)).SetLimitText(5);

   mbstowcs(buf, preference.login, sizeof(buf)/sizeof(buf[0]));
   SetDlgItemText(IDC_LOGIN, buf);

   mbstowcs(buf, preference.password, sizeof(buf)/sizeof(buf[0]));   
   SetDlgItemText(IDC_PWD, buf);

}

void NetworkProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   wchar_t buf[MAX_LOGIN+5];
   std::wstring val;
   if( GetString(&val, GetDlgItem(IDC_IP)) )
   {
      ConfigImpl cfg;
      cfg.key = (wchar_t*)ConfigImpl::IP1;
      cfg.value = (wchar_t*)val.c_str();
      cfg.Write();
   }

   if( GetString(&val, GetDlgItem(IDC_IP2)) )
   {
      ConfigImpl cfg;
      cfg.key = (wchar_t*)ConfigImpl::IP2;
      cfg.value = (wchar_t*)val.c_str();
      cfg.Write();
   }

   GetDlgItemText(IDC_LOGIN, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->login, buf, sizeof(preference->login));

   GetDlgItemText(IDC_PWD, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->password, buf, sizeof(preference->password));

   preference->port = GetDlgItemInt(IDC_PORT, NULL, FALSE);
}

//
// ---------------------- MainProperties ------------------
//
//struct LangData
//{
//   std::wstring fileName;
//
//   LangData() {}
//   LangData(const wchar_t* fn) : fileName(fn) {}
//};
//
//class MainProperties : public PrefPage
//{
//public:
//   MainProperties();
//
//   BEGIN_MSG_MAP(MainProperties)
//      CHAIN_MSG_MAP(PropPage)
//   END_MSG_MAP()
//
//   virtual void Init();
//   virtual void Save(Preference *preference);
//
//   void LoadLang(const Preference& p);
//
//protected:
//   std::vector<LangData> langs;
//};
//
//MainProperties::MainProperties() : PrefPage(IDC_MAIN_PREFERENCE_DIALOG, IDS_MAIN_SETTINGS) 
//{
//}
//
//void MainProperties::LoadLang(const Preference& p)
//{
//   CComboBox cb(GetDlgItem(IDC_LANGUAGE_ID));
//
//   int idx = cb.AddString(L"Русский");
//   langs.push_back(LangData());
//   if( *p.langResource == L'\0' )
//      cb.SetCurSel(idx);
//
//   std::wstring buf;
//   _Module.MakeFileName(&buf, L"*.dll");
//
//   WIN32_FIND_DATA data;
//   HANDLE h = FindFirstFile(buf.c_str(), &data);
//   if( h != INVALID_HANDLE_VALUE )
//   {
//      do
//      {
//         HINSTANCE hi = LoadLibrary(data.cFileName);
//         if( hi != NULL )
//         {
//            wchar_t buf[MAX_LANG_LENGTH * 2];
//            if( LoadString(hi, IDC_LANGUAGE_ID, buf, sizeof(buf)/sizeof(buf[0])) > 0 )
//            {
//               idx = cb.AddString(buf);
//               if( wcscmp(p.langResource, data.cFileName) ==  0 )
//                  cb.SetCurSel(idx);
//               langs.push_back(LangData(data.cFileName));
//            }
//            FreeLibrary(hi);
//         }
//      } while(FindNextFile(h, &data) == TRUE);
//      FindClose(h);
//   }
//}
//
//void MainProperties::Init()
//{
//   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();
//   LoadLang(preference);
//
//   CTrackBarCtrl fontSize(GetDlgItem(IDC_FONT_SIZE));
//   fontSize.SetRange(0, 3);
//   fontSize.SetPos(preference.fontSize);
//}
//
//void MainProperties::Save(Preference *preference)
//{
//   if( m_hWnd == NULL ) return;
//
//   CComboBox cb(GetDlgItem(IDC_LANGUAGE_ID));
//   int cs = cb.GetCurSel();
//   if( cs >= 0 )
//   {
//      const std::wstring& resFile = langs.at(cs).fileName;
//      if( resFile.size() < MAX_LANG_LENGTH )
//      {
//         wcscpy(preference->langResource, resFile.c_str());
//      }
//   }
//
//   CTrackBarCtrl fontSize(GetDlgItem(IDC_FONT_SIZE));
//   preference->fontSize = fontSize.GetPos();
//}

int CanLoadAdminPreference()
{
   NapoleonConfig cfg;
   std::wstring val;
   if( cfg.ReadValue(&val, ADMPWD) )
   {
      Password dlg;
      if( dlg.DoModal() == IDOK )
         return (val.compare(dlg.password) == 0) ? 1 : 0;
      return -1;
   }
   return 1;
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

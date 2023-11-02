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

#include <TopApp.h>

#include <ObjImpl.h>


NetworkProperties::NetworkProperties() : PrefPage(IDC_NET_PREFERENCE_ADD, L"Сеть") 
{
}

void NetworkProperties::LoadAccessPoint()
{
   CComboBox ap(GetDlgItem(IDC_GPRS_GATE));
   ap.AddString(L"internet.beeline.ru");
   ap.AddString(L"internet.mts.ru");
   ap.AddString(L"internet.nw");
   ap.AddString(L"internet");

   HKEY hk;
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, L"Drivers\\Unimodem\\Init", 0, 0, &hk) == ERROR_SUCCESS )
   {
      DWORD cb = 0;
      if( RegQueryValueEx(hk, L"3", NULL, NULL, NULL, &cb) == ERROR_SUCCESS )
      {
         wchar_t *value = (wchar_t*)alloca(cb + sizeof(wchar_t));
         RegQueryValueEx(hk, L"3", NULL, NULL, (BYTE*)value, &cb);

         // AT+CGDCONT=1,"IP","имя шлюза"<cr>
         wchar_t *ep = wcsrchr(value, L'\"');
         if( ep != NULL )
         {
            *ep = L'\0';
            wchar_t *sp = wcsrchr(value, L'\"');
            ap.SetWindowText(sp+1);
         }
      }
      RegCloseKey(hk);
   }
}

void NetworkProperties::SaveAccessPoint()
{
   CWindow ap(GetDlgItem(IDC_GPRS_GATE));
   int len = ap.GetWindowTextLength() + 1;

   if( len == 1 ) return;

   wchar_t *val = (wchar_t*)alloca(len * sizeof(wchar_t));

   ap.GetWindowText(val, len);
   std::wstring str(L"AT+CGDCONT=1,\"IP\",\"");
   str += val;
   str += L"\"<cr>";

   HKEY hk;
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, L"Drivers\\Unimodem\\Init", 0, 0, &hk) == ERROR_SUCCESS )
   {
      RegSetValueEx(hk, L"3", NULL, REG_SZ, (BYTE*)str.c_str(), (str.size()+1) * sizeof(wchar_t));
      RegCloseKey(hk);
   }
}

static void CreateComboBox(const std::wstring& ip, CWindow *owner, UINT id)
{
   CRect bounds, ownRect;
   CComboBox current;
   CWindow prev(owner->GetDlgItem(id));

   prev.GetWindowRect(bounds);
   prev.DestroyWindow();
   owner->ScreenToClient(bounds);
   owner->GetClientRect(ownRect);

   bounds.right = bounds.left + bounds.Width() * 3 / 2;
   bounds.bottom += 80;

   current.Create(owner->m_hWnd, bounds, NULL, WS_CHILD | WS_VISIBLE, 0, id);

   std::wstring::size_type off = 0, nextOff;
   while( true )
   {
      nextOff = ip.find(SEP_SYM, off);

      std::wstring val(ip.substr(off, (nextOff != std::wstring::npos) ? nextOff - off : std::wstring::npos));
      std::wstring::size_type div = val.find(L'\t');
      if( div >= 0 )
         val.replace(div, 1, 1, L'|');

      current.AddString(val.c_str());

      if( nextOff == std::wstring::npos )
         break;

      off = nextOff + 1;
   }
}

void NetworkProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   std::wstring ipv;
   NapoleonConfig cfg;
   if( cfg.ReadValue(&ipv, L"IP") )
   {
      CreateComboBox(ipv, this, IDC_IP);
      CreateComboBox(ipv, this, IDC_IP2);
   }

   wchar_t buf[MAX_LOGIN+5];
   ConfigImpl c;
   SetDlgItemText(IDC_IP, c.LoadIP(ConfigImpl::IP1, preference));
   SetDlgItemText(IDC_IP2, c.LoadIP(ConfigImpl::IP2, preference));

   //mbstowcs(buf, preference.ip, sizeof(buf)/sizeof(buf[0]));
   //SetDlgItemText(IDC_IP, buf);

   //mbstowcs(buf, preference.ip2, sizeof(buf)/sizeof(buf[0]));
   //SetDlgItemText(IDC_IP2, buf);

   SetDlgItemInt(IDC_PORT, preference.port, FALSE);

   CRect b;
   GetDlgItem(IDC_IP).GetWindowRect(b);
   ScreenToClient(b);

   ((CEdit)GetDlgItem(IDC_PORT)).SetLimitText(4);

   mbstowcs(buf, preference.login, sizeof(buf)/sizeof(buf[0]));
   SetDlgItemText(IDC_LOGIN, buf);

   mbstowcs(buf, preference.password, sizeof(buf)/sizeof(buf[0]));   
   SetDlgItemText(IDC_PWD, buf);

   if( preference.flags & apfShowSKU )
      CheckDlgButton(IDC_ENTER_SKU, BST_CHECKED);

   if( preference.addFlags & afHideRestQTY )
      CheckDlgButton(IDC_QTY, BST_CHECKED);

   LoadAccessPoint();
}

void NetworkProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   wchar_t buf[MAX_LOGIN+5];//, *div;

   //GetDlgItemText(IDC_IP, buf, sizeof(buf)/sizeof(buf[0]));
   //if( (div = wcschr(buf, L'|')) != NULL )
   //   *div = L'\0';
   //wcstombs(preference->ip, buf, sizeof(preference->ip));

   //GetDlgItemText(IDC_IP2, buf, sizeof(buf)/sizeof(buf[0]));
   //if( (div = wcschr(buf, L'|')) != NULL )
   //   *div = L'\0';
   //wcstombs(preference->ip2, buf, sizeof(preference->ip2));

   std::wstring val;
   if( GetString(&val, GetDlgItem(IDC_IP)) )
   {
      int pos = val.find(L'|');
      ConfigImpl cfg;
      std::wstring v;
      cfg.key = (wchar_t*)ConfigImpl::IP1;
      if( pos > 0 )
         v = val.substr(0, pos).c_str();
      else
         v = val.c_str();

      cfg.value = (wchar_t*)v.c_str();
      cfg.Write();
   }

   if( GetString(&val, GetDlgItem(IDC_IP2)) )
   {
      ConfigImpl cfg;
      int pos = val.find(L'|');
      cfg.key = (wchar_t*)ConfigImpl::IP2;
      std::wstring v;
      if( pos > 0 )
         v = val.substr(0, pos).c_str();
      else
         v = val.c_str();

      cfg.value = (wchar_t*)v.c_str();
      cfg.Write();
   }


   GetDlgItemText(IDC_LOGIN, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->login, buf, sizeof(preference->login));

   GetDlgItemText(IDC_PWD, buf, sizeof(buf)/sizeof(buf[0]));
   wcstombs(preference->password, buf, sizeof(preference->password));

   preference->port = GetDlgItemInt(IDC_PORT, NULL, FALSE);

   if( IsDlgButtonChecked(IDC_ENTER_SKU) == BST_CHECKED ) preference->flags |= apfShowSKU;
   else preference->flags &= (~apfShowSKU);

   if( IsDlgButtonChecked(IDC_QTY) == BST_CHECKED ) preference->addFlags |= afHideRestQTY;
   else preference->addFlags &= (~afHideRestQTY);

   SaveAccessPoint();
   preference->flags &= (~apfTopApp);
}

/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   24/08/2009   creating
 */ 
#include "stdafx.h"
#include "PrfDlg.h"
#include "NplConfig.h"
#include <Form.h>

#include <TopApp.h>

GPSProperties::GPSProperties() : PrefPage(IDC_GPS_PREFERENCE_DIALOG, L"GPS") 
{
}

DWORD Pow10(DWORD val)
{
   DWORD res = 1;
   while( val-- > 0 )
      res *= 10;

   return res;
}

DWORD Log10(DWORD val)
{
   DWORD step = 0;
   while( val >= 10 )
   {
      val /= 10;
      step++;
   }

   return step;
}

void GPSProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   CComboBox port(GetDlgItem(IDC_COM_PORT));

   port.AddString(L"нет");
   for( int i=0; i<10; i++ )
   {
      wchar_t buf[10];
      wsprintf(buf, L"COM%d", i);

      port.AddString(buf);
   }
   port.SetCurSel(preference.gpsPort);

   int interval = preference.gpsInterval;
   if( interval < 10 ) interval = 10;
   if( interval > 60 ) interval = 60;
   SetDlgItemInt(IDC_GPS_TIME, interval);

   //if( preference.gpsFlags & gpfGPSTrack )
   //   CheckDlgButton(IDC_ROUTE, BST_CHECKED);

   //if( preference.gpsFlags & gpfOrderGPS )
   //   CheckDlgButton(IDC_ORDER_GPS_POS, BST_CHECKED);

   //if( preference.gpsFlags & gpfUseGSM )
   //   CheckDlgButton(IDC_GSM_CELL, BST_CHECKED);

   CComboBox accuracy(GetDlgItem(IDC_ACCURACY));
   accuracy.AddString(L"1");
   accuracy.AddString(L"10");
   accuracy.AddString(L"100");
   accuracy.AddString(L"1000");
   accuracy.SetCurSel(Log10(preference.gpsAccuracy));

   //CComboBox dt(GetDlgItem(IDC_TIME_END));
   //dt.AddString(L"сек");
   //dt.AddString(L"мин");


   //WORD gpsInterval = preference.gpsInterval;

   //if( gpsInterval == 0 )
   //{
   //   CheckDlgButton(IDC_GPS_TRACK, BST_CHECKED);
   //   dt.SetCurSel(0);
   //}
   //else
   //{
   //   CheckDlgButton(IDC_GPS_TIME, BST_CHECKED);

   //   if( gpsInterval > 60 && (gpsInterval % 60 == 0) )
   //   {
   //      dt.SetCurSel(1);
   //      SetDlgItemInt(IDC_TIME_START, gpsInterval / 60, FALSE);
   //   } else
   //   {
   //      dt.SetCurSel(0);
   //      SetDlgItemInt(IDC_TIME_START, gpsInterval, FALSE);
   //   }
   //}

   SetDlgItemInt(IDC_DAY_INTERVAL, preference.gpsArchInterval, FALSE);

   //CRect rc;
   //int hgh;
   //dt.GetWindowRect(rc);
   //hgh = rc.Height();
   //CWindow ti(GetDlgItem(IDC_TIME_START));
   //ti.GetWindowRect(rc);
   //ScreenToClient(rc);
   //ti.MoveWindow(rc.left, rc.top, rc.Width(), hgh, FALSE);

}

void GPSProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   CComboBox port(GetDlgItem(IDC_COM_PORT));
   preference->gpsPort = (WORD)(port.GetCurSel());

   //WORD gpsInterval = (IsDlgButtonChecked(IDC_GPS_TIME) == BST_CHECKED) ? 
   //  GetDlgItemInt(IDC_TIME_START, NULL, FALSE) : 0;

   //if( ((CComboBox)GetDlgItem(IDC_TIME_END)).GetCurSel() == 1 )
   //   gpsInterval *= 60;

   preference->gpsAccuracy = (WORD)Pow10(((CComboBox)GetDlgItem(IDC_ACCURACY)).GetCurSel());

   int interval = GetDlgItemInt(IDC_GPS_TIME, NULL, FALSE);
   if( interval < 10 ) interval = 10;
   if( interval > 60 ) interval = 60;
   preference->gpsInterval = (WORD)interval;

   //if( IsDlgButtonChecked(IDC_ORDER_GPS_POS) == BST_CHECKED ) preference->gpsFlags |= gpfOrderGPS;
   //else preference->gpsFlags &= (~gpfOrderGPS);

   //preference->gpsFlags &= (~gpfUseGSM);

   //if( IsDlgButtonChecked(IDC_ROUTE) == BST_CHECKED ) preference->gpsFlags |= gpfGPSTrack;
   //else preference->gpsFlags &= (~gpfGPSTrack);

   preference->gpsArchInterval = GetDlgItemInt(IDC_DAY_INTERVAL, NULL, FALSE);
}

LRESULT GPSProperties::CheckButton(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
{
   //CheckDlgButton(IDC_GPS_TIME, BST_CHECKED);
   //CheckDlgButton(IDC_GPS_TRACK, BST_UNCHECKED);
   return 0;
}

//#include <Apps.h>
typedef short (*FindGPSPortT)();
LRESULT GPSProperties::FindPort(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
{
   HANDLE hApps = _Module.AppsIntance();
   if( hApps )
   {
      FindGPSPortT FindGPSPort = (FindGPSPortT)GetProcAddress((HMODULE)hApps, L"FindGPSPort");

      HCURSOR hCurs = GetCursor();
      SetCursor(LoadCursor(NULL, IDC_WAIT));

      short port = FindGPSPort();

      SetCursor(hCurs);

      if( port < 0 )
         MessageBox(L"Не могу найти порт", L"Ошибка", MB_OK | MB_ICONSTOP);
      else
      {
         CComboBox cport(GetDlgItem(IDC_COM_PORT));
         cport.SetCurSel(port+1);

         wchar_t buf[100];
         wsprintf(buf, L"GPS найден на COM%d", port);
         MessageBox(buf, L"Сообщние", MB_OK | MB_ICONINFORMATION);
      }
   } else
   {
      MessageBox(L"Отсутствует GPS модуль", L"Ошибка", MB_OK | MB_ICONSTOP);
   }
   return 0;
}

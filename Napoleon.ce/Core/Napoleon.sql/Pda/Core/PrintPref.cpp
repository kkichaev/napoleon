/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки печати
 *
 *  ert   25/06/2008   creating
 */
#include "stdafx.h"
#include "PrfDlg.h"
#include "NplConfig.h"
#include "DoPrint.h"
#include <Form.h>

typedef IConnect* (*TGetConnection)(int index);
typedef void (*TGetPrinter)(IPrinter **printer);

PrintProperties::PrintProperties() : PrefPage(IDC_PRINT_PREFERENCE, L"Печать"), hPrint(NULL)
{
}

PrintProperties::~PrintProperties()
{
   if( hPrint != NULL ) FreeLibrary(hPrint);
}

LRESULT PrintProperties::OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   HWND hList = GetDlgItem(IDC_DEVICE_LIST);
   int index = SendMessage(hList, LB_GETCOUNT, 0, 0);
   while( index > 0 )
   {
      index--;

      ConnectData *cd = (ConnectData*)SendMessage(hList, LB_GETITEMDATA, index, 0);
      delete cd->name;
      delete cd->addr;
      delete cd;
   }
   
   return 0;
}

LRESULT PrintProperties::OnRefresh(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   // load devices
   HCURSOR hCurs = GetCursor();
   SetCursor(LoadCursor(NULL, IDC_WAIT));

   TGetConnection tc = (TGetConnection)GetProcAddress(hPrint, L"GetConnection");
   IConnect *connect = tc(0);
   if( connect )
   {
      if( connect->LookupPrepare() )
      {
         CListBox list(GetDlgItem(IDC_DEVICE_LIST));
         list.ResetContent();

         ConnectData *cd;
         while( (cd = connect->LookupNext()) )
         {
            int index = list.AddString(cd->name);
            list.SetItemData(index, (LPARAM)cd);
         }
      } else
      {
         wchar_t buf[100];
         wsprintf( buf, L"Не могу найти устройств, ошибка %d", WSAGetLastError());
         ::MessageBox(GetActiveWindow(), buf, L"Ошибка", MB_OK|MB_ICONSTOP);
      }
      delete connect;
   }

   SetCursor(hCurs);
   return 0;
}

void PrintProperties::Init()
{
   hPrint = LoadLibrary(L".\\NPrinter.dll");
   if( hPrint == NULL ) return;
   
   wchar_t buf[100];
   ConnectConfig cfg;
   cfg.Load();

   // load printer models
   IPrinter *printer;
   int count;

   TGetPrinter tg = (TGetPrinter)GetProcAddress(hPrint, L"GetPrinter");

   tg(&printer);
   PrinterType *pt = printer->GetPrinterTypes(&count);
   CListBox types(GetDlgItem(IDC_MODEL_LIST));

   char** desc = printer->GetPrinterDesc(NULL);

   for( int i=0; i<count; i++ )
   {
      mbstowcs(buf, desc[i], strlen(desc[i])+1);
      int index = types.AddString(buf);

      types.SetItemData(index, (DWORD)pt[i]);

      if( cfg.type.compare(pt[i]) == 0 )
         types.SetCurSel(index);
   }

   SetDlgItemInt(IDC_COPY, cfg.copies, FALSE);

   if( cfg.data != NULL )
   {
      CListBox dev(GetDlgItem(IDC_DEVICE_LIST));
      int index = dev.AddString(cfg.data->name);
      dev.SetItemData(index, (LPARAM)cfg.data);
      dev.SetCurSel(index);
   }
 
   return;
}

void PrintProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   CListBox types(GetDlgItem(IDC_MODEL_LIST));
   CListBox dev(GetDlgItem(IDC_DEVICE_LIST));

   int tcs = types.GetCurSel();
   int dcs = dev.GetCurSel();

   ConnectConfig cfg;
#ifdef DEBUG
   if( tcs < 0 ) return;
#else
   if( tcs < 0 || dcs < 0 ) return;

   cfg.SetData((ConnectData*)dev.GetItemData(dcs));
#endif


   cfg.type = (char*)types.GetItemData(tcs);
   cfg.copies = GetDlgItemInt(IDC_COPY, NULL, FALSE);

   cfg.Save();
}

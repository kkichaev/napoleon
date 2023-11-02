/*
 * Copyright (C), 2009 - 2011, Денис Мосягин
 *
 * Конфигурация плагинов
 *
 * ert   08/04/2011   creating
 */

#include "stdafx.h"
#include <commctrl.h>
#include "dispatcher.h"
#include "resource.h"

using namespace GRServer;

static Dispatcher::PluginList* pluginList;
static int offset, btnWdh, listGap, btnHgh;
static void InitDialog(HWND hWnd, Dispatcher::PluginList* plugins)
{
   RECT rc;
   HWND hList = GetDlgItem(hWnd, IDC_PLUGINS);
   pluginList = plugins;

   ListView_SetExtendedListViewStyle(hList, LVS_EX_FULLROWSELECT);

   GetWindowRect(hList, &rc);
   listGap = rc.bottom - rc.top;
   ScreenToClient(hWnd, (LPPOINT)&rc);
   offset = rc.left;
   listGap += rc.top;

   GetWindowRect(GetDlgItem(hWnd, IDOK), &rc);
   btnWdh = rc.right - rc.left;
   btnHgh = rc.bottom - rc.top;
   ScreenToClient(hWnd, (LPPOINT)&rc);
   listGap = rc.top - listGap;

   GetClientRect(hList, &rc);
   LVCOLUMN c;
   c.mask = LVCF_FMT | LVCF_WIDTH | LVCF_TEXT;
   c.fmt = LVCFMT_LEFT;
   c.cx = rc.right - 200;

   c.pszText = L"Название";
   ListView_InsertColumn(hList, 1, &c);

   c.cx = 100;
   c.pszText = L"Версия";
   ListView_InsertColumn(hList, 2, &c);

   c.pszText = L"Состояние";
   ListView_InsertColumn(hList, 3, &c);

   int index = 0;
   Dispatcher::PluginList::const_iterator i = plugins->begin();
   for( ; i != plugins->end(); i++, index++ )
   {
      LVITEM item ;
      item.mask = LVIF_TEXT;

      item.iItem = index;

      item.iSubItem = 0;
      item.pszText = (LPWSTR)i->plugin->Name();
      ListView_InsertItem(hList, &item);

      item.iSubItem = 1;
      item.pszText = (LPWSTR)i->plugin->Version();
      ListView_SetItem(hList, &item);

      item.iSubItem = 2;
      item.pszText = (i->inited) ? L"работает" : L"не работает";
      ListView_SetItem(hList, &item);
   }
}

static void SetSize(HWND hDlg, WORD wdh, WORD hgh)
{
   HWND hwnd = GetDlgItem(hDlg, IDC_PLUGINS);
   SetWindowPos(hwnd, NULL, offset, offset, wdh - offset * 2, hgh - 2* offset - btnHgh - listGap, SWP_NOZORDER);

   hwnd = GetDlgItem(hDlg, IDOK);
   SetWindowPos(hwnd, NULL, wdh - offset - btnWdh, hgh - offset - btnHgh, btnWdh, btnHgh, SWP_NOZORDER);

   hwnd = GetDlgItem(hDlg, IDC_SETTINGS);
   SetWindowPos(hwnd, NULL, wdh - 2 * (offset + btnWdh), hgh - offset - btnHgh, btnWdh, btnHgh, SWP_NOZORDER);
}

static void OnNotify(HWND hWnd, NMHDR* hdr)
{
   if( hdr->idFrom == IDC_PLUGINS )
   {
      switch(hdr->code)
      {
      case LVN_ITEMCHANGED:
         {
            int index = ListView_GetNextItem(hdr->hwndFrom, -1, LVNI_SELECTED);
            if( index >= 0 && index < (int)pluginList->size() )
            {
               IPluginConfig* config = pluginList->at(index).plugin->GetConfig();
               HWND hSet = GetDlgItem(hWnd, IDC_SETTINGS);
               EnableWindow(hSet, (config != NULL) ? TRUE : FALSE);
               delete config;
            }
         }
         break;
      case NM_DBLCLK:
         {
            LPNMITEMACTIVATE lpnm = (LPNMITEMACTIVATE)hdr;
            int index = lpnm->iItem;
            if( index >= 0 && index < (int)pluginList->size() )
            {
               IPluginConfig* config = pluginList->at(index).plugin->GetConfig();
               if( config != NULL )
               {
                  config->Configure(gServer, hWnd);
                  delete config;
               }
            }
         }
         break;
      }
   }
}

static void ChangeSettings(HWND hWnd)
{
   HWND hList = GetDlgItem(hWnd, IDC_PLUGINS);
   int index = ListView_GetNextItem(hList, -1, LVNI_SELECTED);
   if( index >= 0 && index < (int)pluginList->size() )
   {
      IPluginConfig* config = pluginList->at(index).plugin->GetConfig();
      config->Configure(gServer, hWnd);
      delete config;
   }
}

static INT_PTR CALLBACK Plugins(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      InitDialog(hWnd, (Dispatcher::PluginList*)lParam);
      break;

   case WM_NOTIFY:
      OnNotify(hWnd, (NMHDR*)lParam);
      break;

   case WM_SIZE:
      SetSize(hWnd, LOWORD(lParam), HIWORD(lParam));
      return TRUE;

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDC_SETTINGS:
         ChangeSettings(hWnd);
         break;

      case IDCANCEL:
      case IDOK:
         EndDialog(hWnd, LOWORD(wParam));
         break; 
      }
   }
   return FALSE;
}

static bool ctrlInited = false;
void Dispatcher::PluginConfigure(HWND owner)
{
   if( !ctrlInited )
   {
      INITCOMMONCONTROLSEX data = {0};
      data.dwSize = sizeof(data);
      data.dwICC = ICC_WIN95_CLASSES;

      InitCommonControlsEx(&data);
      ctrlInited = true;
   }

   DialogBoxParam(NULL, MAKEINTRESOURCE(IDD_PLUGINS), NULL, Plugins, (LPARAM)&plugins);
}
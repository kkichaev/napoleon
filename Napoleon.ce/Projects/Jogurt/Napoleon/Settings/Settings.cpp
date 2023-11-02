// Settings.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "Settings.h"
#include <time.h>
#include <shlobj.h>

// Global Variables:
HINSTANCE hInst;

const char PROG_KEY[] = "SOFTWARE\\Ert\\Napoleon";
const char FOLDER[] = "ExchangeFolder";
const char SERVER_ID[] = "ServerID";
const char PORT[] = "ListenPort";
const char LOG_FILE[] = "LogFile";
const char SBIS_FOLDER[] = "SbisBaseFolder";

char exchangeBuf[MAX_PATH+2];

static int CALLBACK SetStartFolder(HWND hWnd, UINT iMsg, LPARAM , LPARAM lData)
{
   if( iMsg == BFFM_INITIALIZED )
      SendMessage(hWnd,BFFM_SETSELECTION, TRUE, lData);
   return 0;
}

static bool SetExhcangeFolder(char *folder, const char *title)
{
   LPMALLOC pMalloc;
   LPSTR lpBuf;
   BROWSEINFO bi;
   LPITEMIDLIST pidlBrowse;

   SHGetMalloc(&pMalloc);
   lpBuf = (LPSTR)pMalloc->Alloc(MAX_PATH);

   bi.hwndOwner = NULL;
   bi.pidlRoot = NULL;
   bi.pszDisplayName = lpBuf;
   bi.lpszTitle = title;
   bi.ulFlags = 0;

   if( *folder )
   {
      bi.lpfn = (BFFCALLBACK)SetStartFolder;
      bi.lParam = (LPARAM)folder;
   } else
   {
      bi.lpfn = NULL;
      bi.lParam = 0;
   }

   pidlBrowse = SHBrowseForFolder(&bi);
   bool retVal = false;
   if( pidlBrowse != NULL )
   {
      if( SHGetPathFromIDList(pidlBrowse, lpBuf) )
         strcpy(folder,lpBuf);
      pMalloc->Free(pidlBrowse);

      retVal = true;
   }

   pMalloc->Free(lpBuf);
   pMalloc->Release();

   return retVal;
}

static void LoadSettings(HWND hDlg)
{
   HKEY hKey;
   if( RegOpenKey(HKEY_LOCAL_MACHINE, PROG_KEY, &hKey) != ERROR_SUCCESS )
      return;

   HWND ctrl;
   DWORD val;
   char buf[MAX_PATH+2];

   // ExchangeFolder loading
   DWORD cbValue = sizeof(buf);
   if( RegQueryValueEx(hKey, FOLDER, NULL, NULL, (BYTE*)buf, &cbValue) == ERROR_SUCCESS )
   {
      ctrl = GetDlgItem(hDlg, IDC_EXCHANGE);
      SendMessage(ctrl, EM_SETLIMITTEXT, MAX_PATH, 0);
      SendMessage(ctrl, WM_SETTEXT, 0, (LPARAM)buf);
   }

   cbValue = sizeof(buf);
   if( RegQueryValueEx(hKey, SBIS_FOLDER, NULL, NULL, (BYTE*)buf, &cbValue) == ERROR_SUCCESS )
   {
      ctrl = GetDlgItem(hDlg, IDC_SBIS);
      SendMessage(ctrl, EM_SETLIMITTEXT, MAX_PATH, 0);
      SendMessage(ctrl, WM_SETTEXT, 0, (LPARAM)buf);
   }

   /*
   // LogFile loading
   cbValue = sizeof(logFile);
   if( RegQueryValueEx(hKey, LOG_FILE, NULL, NULL, (BYTE*)logFile, &cbValue) != ERROR_SUCCESS )
      *logFile = '\0';
   */

   cbValue = sizeof(val);
   if( RegQueryValueEx(hKey, PORT, NULL, NULL, (BYTE*)&val, &cbValue) != ERROR_SUCCESS )
      val = 8888;
   SetDlgItemInt(hDlg, IDC_PORT, val, FALSE);

   RegCloseKey(hKey);
}

static void SaveSettings(HWND hDlg)
{
   HKEY hKey;
   if( RegCreateKeyEx(HKEY_LOCAL_MACHINE, PROG_KEY, 0, NULL, 0, KEY_ALL_ACCESS, NULL, &hKey, NULL) != ERROR_SUCCESS )
      return;

   char buf[MAX_PATH+2];
   int len = GetDlgItemText(hDlg, IDC_EXCHANGE, buf, sizeof(buf));
   if( buf[len-1] == '\\' ) len--;
   RegSetValueEx(hKey, FOLDER, 0, REG_SZ, (const BYTE*)buf, len);

   len = GetDlgItemText(hDlg, IDC_SBIS, buf, sizeof(buf));
   if( buf[len-1] == '\\' ) len--;
   RegSetValueEx(hKey, SBIS_FOLDER, 0, REG_SZ, (const BYTE*)buf, len);

   DWORD val = GetDlgItemInt(hDlg, IDC_PORT, NULL, FALSE);
   RegSetValueEx(hKey, PORT, 0, REG_DWORD, (const BYTE*)&val, sizeof(val));

   RegCloseKey(hKey);
}

// Message handler for about box.
INT_PTR CALLBACK Settings(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	switch (message)
	{
	case WM_INITDIALOG:
      {
         HICON hIcon = LoadIcon(hInst, MAKEINTRESOURCE(IDI_SETTINGS));
         SendMessage(hDlg, WM_SETICON, ICON_BIG, (LPARAM)hIcon);

         LoadSettings(hDlg);
		   return (INT_PTR)TRUE;
      }

	case WM_COMMAND:
      switch(LOWORD(wParam))
      {
         case IDC_BROWSE:
         {
            char buf[MAX_PATH+2];
            GetDlgItemText(hDlg, IDC_EXCHANGE, buf, sizeof(buf));
            if( SetExhcangeFolder(buf, "Укажите каталог обмена") )
               SetDlgItemText(hDlg, IDC_EXCHANGE, buf);
            break;
         }
         case IDC_SBIS_BROWSE:
         {
            char buf[MAX_PATH+2];
            GetDlgItemText(hDlg, IDC_SBIS, buf, sizeof(buf));
            if( SetExhcangeFolder(buf, "Укажите каталог базы СБИС") )
               SetDlgItemText(hDlg, IDC_SBIS, buf);
            break;
         }
         case IDC_USERS:
         {
            int len = GetDlgItemText(hDlg, IDC_EXCHANGE, exchangeBuf, sizeof(exchangeBuf));
            exchangeBuf[len] = '\0';
            if( exchangeBuf[len-1] != '\\' )
               strcpy(exchangeBuf+len, "\\");
            ShowUsers(exchangeBuf, hDlg);
            break;
         }
         case IDOK:
         case IDCANCEL:
		   {
            if( LOWORD(wParam) == IDOK )
               SaveSettings(hDlg);

			   EndDialog(hDlg, LOWORD(wParam));
			   return (INT_PTR)TRUE;
		   }
      }
	   break;
	}
	return (INT_PTR)FALSE;
}

int APIENTRY _tWinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPTSTR    lpCmdLine,
                     int       nCmdShow)
{
   INITCOMMONCONTROLSEX icc;

   hInst = hInstance;
   icc.dwSize = sizeof(icc);
   icc.dwICC = ICC_LISTVIEW_CLASSES;

   InitCommonControlsEx(&icc);
   DialogBox(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, Settings);
	return 0;
}

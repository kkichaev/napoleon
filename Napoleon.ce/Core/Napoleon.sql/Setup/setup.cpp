/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Setup & New menu item
 * 
 *  ert   29/08/2007   creating
 */ 

#include "stdafx.h"
#include <ce_setup.h>

HINSTANCE hInst;

#include <Preference.h>
#include "ini.h"

#ifdef MARK_SYNCED
#include <DoSync.h>
#endif

#include <notify.h>


//
//----------------------------------- Install Section -------------------------------------
//
codeINSTALL_INIT Install_Init(HWND hwndParent, BOOL fFirstCall, BOOL fPreviouslyInstalled, LPCTSTR pszInstallDir)
{
   return codeINSTALL_INIT_CONTINUE;
}

codeINSTALL_EXIT Install_Exit(HWND hwndParent, LPCTSTR pszInstallDir, WORD cFailedDirs, WORD cFailedFiles, 
                              WORD cFailedRegKeys, WORD cFailedRegVals, WORD cFailedShortcuts)
{
   //InitCommonControls();
   
   //WritePreference();

#ifdef MARK_SYNCED
   std::wstring fn(pszInstallDir);
   fn += SYNC_STAMP;

   bool retVal = true;

   FILE *f = _wfopen(fn.c_str(), L"wb");
   if( f )
   {
      SYSTEMTIME st;
      FILETIME ft;
      GetSystemTime(&st);
      SystemTimeToFileTime(&st, &ft);
      __int64 val1 = (__int64)ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
      DWORD res = (DWORD)(val1 / ((__int64)10000000 * 3600 * 24));
      
      fwrite(&res, sizeof(res), 1, f);
      fclose(f);
   }
#endif

#ifdef Agama
   // выключение автоблокировки
   HKEY hk;
   if( RegOpenKeyEx(HKEY_CURRENT_USER, L"ControlPanel\\Keybd", 0, 0, &hk) == ERROR_SUCCESS )
   {
      DWORD val = 0;
      RegSetValueEx(hk, L"DeviceLockWhenSuspend", 0, REG_DWORD, (BYTE*)&val, sizeof(val));
      RegCloseKey(hk);
   }
#endif

//#ifdef NAPOLEON_APPS
//   if( MessageBox(hwndParent, L"Для завершения установки надо перезагрузить устройство.\nСделать это сейчас?", L"Вопрос",
//      MB_YESNO | MB_ICONQUESTION) == IDYES )
//   {
//      ExitWindowsEx(EWX_REBOOT, 0);
//   }
//#endif
   return codeINSTALL_EXIT_DONE;
}

wchar_t InstDir[MAX_PATH+2];
codeUNINSTALL_INIT Uninstall_Init(HWND hwndParent, LPCTSTR pszInstallDir)
{
   wcscpy(InstDir, pszInstallDir);

   wchar_t buf[MAX_PATH];
   wcscpy(buf, InstDir);
   wcscat(buf, L"\\Napoleon.exe");
   CeRunAppAtTime(buf, NULL);
   CeRunAppAtEvent(buf, NOTIFICATION_EVENT_NONE);

   return codeUNINSTALL_INIT_CONTINUE;
}

codeUNINSTALL_EXIT Uninstall_Exit(HWND hwndParent)
{
   if( MessageBox(hwndParent, L"Удалить базу данных?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      Preference pref;
      pref.Load();
      if( *pref.dbName != '\0' )
      {
         wchar_t buf[MAX_PATH];
         WIN32_FIND_DATA data;
 
         wcscpy(buf, InstDir);
         wcscat(buf, L"\\*.*");

         HANDLE fh = FindFirstFile(buf, &data);
         if( fh != INVALID_HANDLE_VALUE )
         {
            do
            {
               if( (data.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) == 0 )
               {
                  wcscpy(buf, InstDir);
                  wcscat(buf, L"\\");
                  wcscat(buf, data.cFileName);

                  DeleteFile(buf);
               }
            } while( FindNextFile(fh, &data) > 0 );
            FindClose(fh);
         }
      }
#ifdef Agama
      if( MessageBox(hwndParent, L"Удалить настройки программы?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
      {
         Preference pref;
         pref.Remove();
      }
#endif
   }
   return codeUNINSTALL_EXIT_DONE;
}

BOOL WINAPI DllMain( HANDLE hInstDll, ULONG ulReason, LPVOID lpReserved )
{
   switch( ulReason )
   {
      case DLL_PROCESS_ATTACH :
         hInst = (HINSTANCE)hInstDll;
         break;
      
      case DLL_PROCESS_DETACH:
         break;
         
      case DLL_THREAD_ATTACH:
         break;
         
      case DLL_THREAD_DETACH:
         break;
         
   }
   return TRUE;
}

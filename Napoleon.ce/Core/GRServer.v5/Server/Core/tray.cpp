/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Загрузчик сервера (служба или трей)
 *
 * ert   24/03/2009   creating
 */
#include "stdafx.h"
#include <tray.h>
#include <malloc.h>
#include <shlobj.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

const int WM_TRAY_NOTIFY = WM_USER+100;
const int STOP_WAIT = 30000; 
const int MAX_STOP_TRY = 10; // ждем 10 * STOP_WAIT

const TCHAR TrayLoader::trayRunArg[] = L"--run-tray";
TrayLoader* TrayLoader::loader;

//
//------------------------------ TrayLoader ----------------------------
//
DWORD ArgsToVector(LPCTSTR args, char*** argV);

TrayLoader::TrayLoader(GRServer::IRunnableModule *module, HINSTANCE hInst, int _idIcon, 
                       LPCTSTR args, IProgramNotify *notifier) : 
   IProgramLoader(module), hInstance(hInst), idIcon(_idIcon)
{
   argc = ArgsToVector(args, &argv);

   this->args = args;

   this->notifier = notifier;
}

TrayLoader::~TrayLoader()
{
   free((LPTSTR*)argv);
}

bool TrayLoader::IsInstalled(const IRunnableModule *module)
{
   TCHAR path[MAX_PATH];

   if( SHGetSpecialFolderPath(NULL, path, CSIDL_STARTUP, FALSE) == TRUE )
   {
      wcscat(path, L"\\");
      wcscat(path, module->Name());
      wcscat(path, L".lnk");

      WIN32_FIND_DATA data;
      HANDLE hFile = FindFirstFile(path, &data);
      if( hFile != INVALID_HANDLE_VALUE )
      {
         FindClose(hFile);
         return true;
      }
   }

   return false;
}

HRESULT CreateLink(LPCTSTR lpszPathObj, LPCTSTR args, LPCTSTR lpszPathLink, LPCTSTR lpszDesc) 
{ 
   HRESULT hres; 
   IShellLink* psl; 

   hres = CoCreateInstance(CLSID_ShellLink, NULL, CLSCTX_INPROC_SERVER,  IID_IShellLink, (LPVOID*)&psl); 
   if (SUCCEEDED(hres)) 
   { 
      IPersistFile* ppf; 

      psl->SetPath(lpszPathObj); 
      psl->SetArguments(args);
      psl->SetDescription(lpszDesc); 

      hres = psl->QueryInterface(IID_IPersistFile, (LPVOID*)&ppf); 
      if (SUCCEEDED(hres)) 
      {
         if( sizeof(TCHAR) == sizeof(char) )
         {
            WCHAR wsz[MAX_PATH]; 
            MultiByteToWideChar(CP_ACP, 0, (LPCSTR)lpszPathLink, -1, wsz, MAX_PATH); 
            hres = ppf->Save(wsz, TRUE);
         } else
            hres = ppf->Save(lpszPathLink, TRUE);

         ppf->Release(); 
      } 
      psl->Release(); 
   } 
   return hres;
}

bool TrayLoader::Install(const IRunnableModule *module, bool runAfterInstall, LPCTSTR args)
{
   TCHAR path[MAX_PATH];
   if( GetModuleFileName(NULL, path, sizeof(path)/sizeof(TCHAR)) == 0 )
      return false;

   if( args == NULL ) args = L"";

   TCHAR *argsStr = (TCHAR*)alloca((wcslen(trayRunArg) + 2 + wcslen(args)) * sizeof(TCHAR));

   wcscpy(argsStr, trayRunArg);
   wcscat(argsStr, L" ");
   wcscat(argsStr, args);

   TCHAR linkPath[MAX_PATH];
   SHGetSpecialFolderPath(NULL, linkPath, CSIDL_STARTUP, TRUE);
   wcscat(linkPath, L"\\");
   wcscat(linkPath, module->Name());
   wcscat(linkPath, L".lnk");

   CoInitialize(NULL);

   HRESULT hres = CreateLink(path, argsStr, linkPath, module->DisplayName());

   CoUninitialize();

   if( !SUCCEEDED(hres) )
      return false;

   if( runAfterInstall )
      ShellExecute(NULL, L"open", linkPath, NULL, NULL, SW_SHOW); 

   return true;
}

bool TrayLoader::Uninstall(const IRunnableModule *module)
{
   TCHAR path[MAX_PATH];
   SHGetSpecialFolderPath(NULL, path, CSIDL_STARTUP, TRUE);
   wcscat(path, L"\\");
   wcscat(path, module->Name());
   wcscat(path, L".lnk");

   return (DeleteFile(path) != FALSE);
}

bool TrayLoader::Startup()
{
   WNDCLASSEX wnd_class = { 0 };

   wnd_class.cbSize = sizeof(WNDCLASSEX);
   wnd_class.style = CS_DBLCLKS|CS_HREDRAW|CS_VREDRAW;
   wnd_class.lpfnWndProc = WindowProc;
   wnd_class.hInstance = hInstance;
   wnd_class.hbrBackground = HBRUSH(COLOR_APPWORKSPACE+1);
   wnd_class.lpszClassName = L"GRServerClass";

   if( !RegisterClassEx(&wnd_class) )
      return false;

   LPCTSTR name = module->DisplayName();
   if( name == NULL || *name == L'\0' )
      name = module->Name();

   hMainWnd = CreateWindowEx(WS_EX_APPWINDOW|WS_EX_OVERLAPPEDWINDOW, L"GRServerClass",
      name, WS_OVERLAPPEDWINDOW, 
      CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
      NULL, NULL, hInstance, NULL);

   if( hMainWnd == NULL )
      return false;

   ShowWindow(hMainWnd, SW_HIDE);

   NOTIFYICONDATA data;
   data.cbSize = sizeof(data);
   data.hWnd = hMainWnd;
   data.uID = 0;
   data.uFlags = NIF_ICON|NIF_TIP|NIF_MESSAGE;
   data.uCallbackMessage = WM_TRAY_NOTIFY;
   data.hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(idIcon));
   wcscpy(data.szTip, name);
   Shell_NotifyIcon(NIM_ADD, &data);

   return true;
}

LRESULT CALLBACK TrayLoader::WindowProc(HWND hWnd, UINT iMsg, WPARAM wParam, LPARAM lParam)
{
   if( iMsg == WM_TRAY_NOTIFY )
   {
      if( lParam == WM_RBUTTONDOWN || lParam == WM_CONTEXTMENU )
         loader->notifier->ProgramNotify(loader, hWnd);

      return TRUE;
   }
   return DefWindowProc(hWnd, iMsg, wParam, lParam);
}

void TrayLoader::ShowCriticalError(const wchar_t* msg)
{
   wchar_t buf[MAX_PATH];
   GetModuleFileName(NULL, buf, sizeof(buf)/sizeof(buf[0]));
   MessageBox(hMainWnd, msg, buf, MB_ICONSTOP | MB_OK);
}

bool TrayLoader::Run()
{
   loader = this;
   if( !Startup() )
      return false;

   if( !module->Init(argc, (const char**)argv, this) )
   {
      DeleteTrayIcon();
      return false;
   }

   HANDLE hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)RunModule, NULL, 0, NULL);
   if( hThread == INVALID_HANDLE_VALUE )
   {
      DeleteTrayIcon();
      return false;
   }

   threadID = GetCurrentThreadId();

   MSG msg;
   while( GetMessage(&msg, NULL, 0, 0) )
   {
      TranslateMessage(&msg);
      DispatchMessage(&msg);
   }

   Stoping();
   if( WaitForSingleObject(hThread, 20000) != WAIT_OBJECT_0 )
      TerminateThread(hThread, 0);

   CloseHandle(hThread);
   return true;
}

void TrayLoader::DeleteTrayIcon()
{
   NOTIFYICONDATA data;
   data.cbSize = sizeof(data);
   data.hWnd = hMainWnd;
   data.uID = 0;
   data.uFlags = 0;
   Shell_NotifyIcon(NIM_DELETE, &data);
}

void TrayLoader::Stoping()
{
   DeleteTrayIcon();

   HANDLE hTryStop = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryStop, NULL, 0, NULL);
   DWORD checkPoint = 1;
   do
   {
      if( WaitForSingleObject(hTryStop, STOP_WAIT) != WAIT_TIMEOUT )
         break;
   } while( checkPoint++ <= MAX_STOP_TRY );

   if( checkPoint > MAX_STOP_TRY )
   {
      TerminateThread(hTryStop, 0);
      module->Kill();
   }

   CloseHandle(hTryStop);
}

DWORD WINAPI TrayLoader::TryStop(LPVOID)
{
   loader->module->Stop();
   return 0;
}

DWORD WINAPI TrayLoader::RunModule(LPVOID)
{
   loader->module->Run();
   return 0;
}

const char* TrayLoader::ExecString(const IRunnableModule& module) const
{
   static char buf[10000];

   char prog[MAX_PATH];
   GetModuleFileNameA(NULL, prog, sizeof(prog));

   strcpy(buf, prog);
   strcat(buf, " ");

   if( sizeof(TCHAR) != sizeof(char) )
   {
      USES_CONVERSION;
      strcat(buf, W2A_CP(args, CP_UTF8));
   } else
      strcat(buf,(const char*)args);

   return buf;
}

void TrayLoader::Stopping(const IRunnableModule& module)
{
   PostThreadMessage(threadID, WM_QUIT, 0, 0);
}
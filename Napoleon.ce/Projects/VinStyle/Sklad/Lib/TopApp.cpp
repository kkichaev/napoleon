/*
* Copyright (C), 2007, Денис Мосягин
*
* Класс для создания единственного приложения на КПК
* 
*  ert   07/03/2008  creating
*/ 

#include "stdafx.h"
#include "TopApp.h"
#include <Module.h>
#include <StdFuncs.h>

WNDPROC TopApp::taskWndProc = NULL;
WNDPROC TopApp::mainWndProc = NULL;
HWND TopApp::taskHWnd = NULL;
HWND TopApp::mainWnd = NULL;
bool TopApp::enableDoneButton = false;
bool TopApp::hooked = false;
DWORD TopApp::saveOrientation = (DWORD)-1;
RECT TopApp::doneBounds;

void TopApp::MakeShortcutName(wchar_t *shortcut, wchar_t *fileName)
{
   *fileName = L'\"';
   GetModuleFileName(_Module.GetModuleInstance(), fileName+1, MAX_PATH-1);
   SHGetSpecialFolderPath(NULL, shortcut, CSIDL_STARTUP, TRUE);
   
   wchar_t *p = wcsrchr(fileName, L'\\');
   wcscat(shortcut, p);
   p = wcsrchr(shortcut, L'.');
   wcscpy(p+1, L"lnk");

   wcscat(fileName, L"\"");
}

void TopApp::MakeAutorun()
{
   wchar_t fileName[MAX_PATH], shortcut[MAX_PATH];

   MakeShortcutName(shortcut, fileName);
   SHCreateShortcut(shortcut, fileName);
}

void TopApp::RemoveAutorun()
{
   wchar_t fileName[MAX_PATH], shortcut[MAX_PATH];

   MakeShortcutName(shortcut, fileName);
   DeleteFile(shortcut);
}

void TopApp::Start(HWND hMain)
{
   if( hooked )
      return;

   taskHWnd = FindWindow(L"HHTaskBar", L"");

   if( taskHWnd == NULL )
      return ;

   hooked = true;

   taskWndProc = (WNDPROC)GetWindowLong(taskHWnd, GWL_WNDPROC);
   SetWindowLong(taskHWnd, GWL_WNDPROC, (long)TaskWndProc);

   mainWnd = hMain;
   if( hMain != NULL )
   {
      mainWndProc = (WNDPROC)GetWindowLong(mainWnd, GWL_WNDPROC);
      SetWindowLong(mainWnd, GWL_WNDPROC, (long)MainWndProc);
   }

   EnableDoneButton(false);
   //EnableHardwareKeyboard(FALSE);
}

void TopApp::Stop()
{
   if( !hooked )
      return;

   hooked = false;
   SetWindowLong(taskHWnd, GWL_WNDPROC, (long)taskWndProc);
   taskWndProc = NULL;

   if( mainWnd != NULL )
   {
      SetWindowLong(mainWnd, GWL_WNDPROC, (long)mainWndProc);
      mainWndProc = NULL;
   }
   //EnableHardwareKeyboard(TRUE);
}

void TopApp::EnableDoneButton(bool enable)
{
   if( !hooked )
      return;

   enableDoneButton = enable;
   if( enable )
   {
      GetWindowRect(taskHWnd, &doneBounds);
      doneBounds.left = doneBounds.right - (doneBounds.bottom - doneBounds.top);
   }
}

LRESULT CALLBACK TopApp::TaskWndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
   if( message == WM_LBUTTONDOWN )
   {
      if( enableDoneButton )
      {
         POINT pt;
         pt.x = LOWORD(lParam);
         pt.y = HIWORD(lParam);

         if( PtInRect(&doneBounds, pt) )
            return CallWindowProc(taskWndProc, hWnd, message, wParam, lParam);
      }
      return 0;
   }
   return CallWindowProc(taskWndProc, hWnd, message, wParam, lParam);
}

LRESULT CALLBACK TopApp::MainWndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
   if( message == WM_ACTIVATE )
   {
      if( LOWORD(wParam) == WA_INACTIVE )
      {
         if( lParam == NULL )
         {
            HWND fgWnd = GetForegroundWindow();
            DWORD thread = GetWindowThreadProcessId(fgWnd, NULL);
            SetForegroundWindow(hWnd);
            //PostThreadMessage(thread, WM_QUIT, 0, 0);
            //DestroyWindow(fgWnd);
         }
         return 0;
      }
   }
   return CallWindowProc(mainWndProc, hWnd, message, wParam, lParam);
}

void TopApp::ChangeOrientation(bool landscape)
{
   DEVMODE devMode = {0};

   devMode.dmSize = sizeof(devMode);
   devMode.dmFields = DM_DISPLAYORIENTATION;
   ChangeDisplaySettingsEx(NULL, &devMode, NULL, CDS_TEST, NULL);

   saveOrientation = devMode.dmDisplayOrientation;

   devMode.dmDisplayOrientation = (landscape) ? DMDO_90 : DMDO_0;

   if( saveOrientation != devMode.dmDisplayOrientation )
      ChangeDisplaySettingsEx(NULL, &devMode, NULL, 0, NULL);
}

void TopApp::RestoreOrientation()
{
   if( saveOrientation == -1 ) return;

   DEVMODE devMode = {0};

   devMode.dmSize = sizeof(devMode);
   devMode.dmFields = DM_DISPLAYORIENTATION;
   devMode.dmDisplayOrientation  = saveOrientation;

   ChangeDisplaySettingsEx(NULL, &devMode, NULL, 0, NULL); 
}

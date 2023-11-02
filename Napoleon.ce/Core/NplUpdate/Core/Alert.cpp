/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Update alert
*
*  ert   19/10/2009   creating
*/
#include "stdafx.h"
#include "NplUpdate.h"
#include <string>

const int IDC_CHOICE1 = 100;
const int IDC_CHOICE2 = 101;
const int NOTIFY_ID = 1000;

// {638DA527-BD59-441a-9B0C-216DF312B341}
static const GUID NOTIFY_GUID = 
{ 0x638da527, 0xbd59, 0x441a, { 0x9b, 0xc, 0x21, 0x6d, 0xf3, 0x12, 0xb3, 0x41 } };

class WaitChoiceWindow
{
public:
   WaitChoiceWindow(HINSTANCE hInst);

   bool Do(const wchar_t *message, DWORD id);

protected:
   HINSTANCE hInst;

protected:
   static HANDLE hStop;
   static WORD choice;
   static LRESULT WaitProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);
};

WORD WaitChoiceWindow::choice = 0;
HANDLE WaitChoiceWindow::hStop = INVALID_HANDLE_VALUE;

WaitChoiceWindow::WaitChoiceWindow(HINSTANCE hInst)
{
   WNDCLASS cls = {0};
   cls.hInstance = hInst;
   cls.lpfnWndProc = WaitProc;
   cls.lpszClassName = L"SYNCCLS";

   RegisterClass(&cls);

   this->hInst = hInst;
}

bool WaitChoiceWindow::Do(const wchar_t *message, DWORD id)
{
   SHNOTIFICATIONDATA sn = {0};

   hStop = CreateEvent(NULL, TRUE, FALSE, NULL);

   sn.hwndSink = CreateWindow(L"SYNCCLS", NULL, WS_POPUP, 0, 0, 0, 0, NULL, NULL, hInst, NULL);

   sn.dwID = NOTIFY_ID;
   sn.clsid = NOTIFY_GUID;
   sn.npPriority = SHNP_INFORM;
   sn.csDuration = -1;
   sn.hicon = LoadIcon(hInst, MAKEINTRESOURCE(id));
   sn.cbStruct = sizeof(SHNOTIFICATIONDATA);
   sn.grfFlags = SHNF_STRAIGHTTOTRAY;

   sn.pszHTML = message;
   sn.pszTitle = L"Обновление программы \"Наполеон\"";

   SOFTKEYCMD skc[2];
   skc[0].wpCmd = IDC_CHOICE1;
   skc[0].grfFlags = NOTIF_SOFTKEY_FLAGS_DISMISS;
   skc[1].wpCmd = IDC_CHOICE2;
   skc[1].grfFlags = NOTIF_SOFTKEY_FLAGS_DISMISS;

   sn.rgskn[0].pszTitle = L"Да";
   sn.rgskn[0].skc = skc[0];
   sn.rgskn[1].pszTitle = L"Нет";
   sn.rgskn[1].skc = skc[1];

   SHNotificationAdd(&sn);

   MSG msg;
   while( GetMessage(&msg, NULL, 0, 0) )
   {
      TranslateMessage(&msg);
      DispatchMessage(&msg);

      if( WaitForSingleObject(hStop, 0) == WAIT_OBJECT_0 )
         break;
   }

   DestroyWindow(sn.hwndSink);
   CloseHandle(hStop);

   return (choice == 1);
}

LRESULT WaitChoiceWindow::WaitProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
   if( message == WM_COMMAND )
   {
      switch(LOWORD(wParam))
      {
      case IDC_CHOICE1:
         choice = 1;
         if( hStop != INVALID_HANDLE_VALUE ) SetEvent(hStop);
         return 0;
      case IDC_CHOICE2:
         choice = 2;
         if( hStop != INVALID_HANDLE_VALUE ) SetEvent(hStop);
         return 0;
      default:
         break;
      }
   }
   return DefWindowProc(hWnd, message, wParam, lParam);
}

bool Application::Alert(const wchar_t* curVersion, DWORD id) const
{
   WaitChoiceWindow wcw(hInstance);
   return wcw.Do(curVersion, id);
}

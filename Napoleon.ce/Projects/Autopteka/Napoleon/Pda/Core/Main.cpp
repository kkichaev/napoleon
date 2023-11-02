/*
* Copyright (C), 2007, Денис Мосягин
*
* Главный модуль - для каждого проекта свой
*
*  ert   13/08/2007   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <MainFrame.h>
#include <StdFuncs.h>

#include <TopApp.h>
#include <Preference.h>

#include <Notify.h>
#include <DoSync.h>
#include <SQLTable.h>

NapoleonApp _Module;

void DataClose()
{
   SQLTable::CloseDB();
}

void DataInit(const char *dbName)
{
   if( *dbName == L'\0' ) dbName = DEFAULT_BASE;

   int len = strlen(dbName) + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   mbstowcs(buf, dbName, len);

   std::wstring fileName;
   _Module.MakeFileName(&fileName, buf);

   SQLTable::OpenDB(fileName.c_str());
   //if( SQLTable::OpenDB(fileName.c_str()) == false )
   //   MessageBox(NULL, L"Не могу открыть базу данных", L"Ошибка", MB_OK | MB_ICONSTOP);
}

static HRESULT SayToPreviousInstance(HINSTANCE hInstance, LPCTSTR  lpstrCmdLine)
{
	CFrameWndClassInfo& classInfo = MainFrame::GetWndClassInfo();
	ATLVERIFY(::LoadString(hInstance, classInfo.m_uCommonResourceID, classInfo.m_szAutoName, sizeof(classInfo.m_szAutoName)/sizeof(classInfo.m_szAutoName[0])) != 0);
	classInfo.m_wc.lpszClassName = classInfo.m_szAutoName;
	const TCHAR* pszClass = classInfo.m_wc.lpszClassName;

   if(NULL == pszClass || '\0' == *pszClass)
		return E_FAIL;

   HWND hwnd = FindWindow(pszClass, NULL);
   if( hwnd == NULL )
      return S_FALSE;

	// Transmit our params to previous instance
	if (lpstrCmdLine && *lpstrCmdLine)
	{
		COPYDATASTRUCT cd = { NULL, sizeof(TCHAR) * (wcslen(lpstrCmdLine) + 1), (PVOID)lpstrCmdLine };
		::SendMessage(hwnd, WM_COPYDATA, NULL, (LPARAM)&cd);
	}

   return S_OK;
}

int WINAPI _tWinMain(HINSTANCE hInstance, HINSTANCE /*hPrevInstance*/, LPTSTR lpstrCmdLine, int nCmdShow)
{
   if( wcscmp(lpstrCmdLine, APP_RUN_AT_TIME) == 0 || wcscmp(lpstrCmdLine, APP_RUN_AFTER_TIME_CHANGE) == 0 )
   {
      //MessageBox(NULL, L"!", L"!", MB_OK);
      //Log("Run app");

      if( wcscmp(lpstrCmdLine, APP_RUN_AFTER_TIME_CHANGE) == 0 )
         ClearSyncFile();
      HRESULT hRes = SayToPreviousInstance(hInstance, lpstrCmdLine);
      //if( hRes != S_OK )
      //   Log("Fail to say");
      return 0;
   }

   //if( *lpstrCmdLine == L'\0' )
   //{
   //   wchar_t buf[MAX_PATH];
   //   GetModuleFileName(hInstance, buf, sizeof(buf)/sizeof(wchar_t));
   //   CeRunAppAtEvent(buf, NOTIFICATION_EVENT_NONE);
   //   CeRunAppAtEvent(buf, NOTIFICATION_EVENT_TIME_CHANGE);
   //} else
   //{
   //   if( wcscmp(APP_RUN_AFTER_TIME_CHANGE, lpstrCmdLine) == 0 )
   //   {
   //      ClearSyncFile();
   //      return 0;
   //   }
   //}

   HANDLE hMutex;
   HRESULT hRes = MainFrame::ActivatePreviousInstance(hInstance, lpstrCmdLine, &hMutex);
   if(FAILED(hRes) || hRes == S_FALSE)
      return hRes;

   hRes = ::CoInitializeEx(NULL, COINIT_MULTITHREADED);
   ATLASSERT(SUCCEEDED(hRes));

   AtlInitCommonControls(ICC_WIN95_CLASSES|ICC_DATE_CLASSES/*|ICC_INTERNET_CLASSES*/);
   SHInitExtraControls();

   Preference prf;
   if( prf.Load() )
   {
      if( (prf.flags & apfLandscape) != 0 ) TopApp::ChangeOrientation(true);
      else if( (prf.flags & apfPortrait) != 0 ) TopApp::ChangeOrientation(false);
   }

   hRes = _Module.Init(NULL, hInstance);
   ATLASSERT(SUCCEEDED(hRes));

   if( !wcscmp(lpstrCmdLine, L"CreateOrder") )
      _Module.createOrder = true;

   //int nRet = 0;
   int nRet = MainFrame::AppRun(lpstrCmdLine, nCmdShow);

   _Module.Term();

   FreeSystemFont();
   DataClose();

   TopApp::RestoreOrientation();

   ::CoUninitialize();

   HINSTANCE hl;
   if( (hl = GetModuleHandle(L"aygshell.dll")) != NULL ) FreeLibrary(hl);
   if( (hl = GetModuleHandle(L"shellres.dll")) != NULL ) FreeLibrary(hl);
   if( (hl = GetModuleHandle(L"oleaut32.dll")) != NULL ) FreeLibrary(hl);

   if( hMutex != NULL )
   {
      ReleaseMutex(hMutex);
      CloseHandle(hMutex);
   }

   return nRet;
}
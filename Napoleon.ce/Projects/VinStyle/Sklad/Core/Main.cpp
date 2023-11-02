/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic
*
*  ert   02/09/2010   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <StdFuncs.h>
#include <MainFrame.h>
#include <SQLTable.h>

#include "Exchange.h"
#include "Reflector.h"

Application _Module;

int WINAPI _tWinMain(HINSTANCE hInstance, HINSTANCE /*hPrevInstance*/, LPTSTR lpstrCmdLine, int nCmdShow)
{
   HANDLE hMutex;
   HRESULT hRes = MainFrame::ActivatePreviousInstance(hInstance, lpstrCmdLine, &hMutex);
   if(FAILED(hRes) || hRes == S_FALSE)
      return hRes;

   _AtlModuleInstance = hInstance;
   ATL::_AtlBaseModule.m_hInst = hInstance;
   ATL::_AtlBaseModule.m_hInstResource = hInstance;

   hRes = ::CoInitializeEx(NULL, COINIT_MULTITHREADED);
   ATLASSERT(SUCCEEDED(hRes));

   AtlInitCommonControls(ICC_WIN95_CLASSES|ICC_DATE_CLASSES/*|ICC_INTERNET_CLASSES*/);
#ifdef ZEBEX_AYG
   SHInitExtraControls();
#endif

   hRes = _Module.Init(NULL, hInstance);
   ATLASSERT(SUCCEEDED(hRes));

   int nRet = MainFrame::AppRun(lpstrCmdLine, nCmdShow);

   _Module.DataClose();
   _Module.Term();

   FreeSystemFont();

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

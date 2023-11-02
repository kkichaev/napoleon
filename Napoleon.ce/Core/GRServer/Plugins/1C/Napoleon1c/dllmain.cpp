// dllmain.cpp: реализация DllMain.

#include "stdafx.h"
#include "resource.h"
#include "Napoleon1c_i.h"
#include "dllmain.h"

CNapoleon1cModule _AtlModule;

// Используется, чтобы определить, можно ли выгрузить DLL средствами OLE
STDAPI DllCanUnloadNow(void)
{
   return _AtlModule.DllCanUnloadNow();
}


// Возвращает фабрику класса для создания объекта требуемого типа
STDAPI DllGetClassObject(REFCLSID rclsid, REFIID riid, LPVOID* ppv)
{
   return _AtlModule.DllGetClassObject(rclsid, riid, ppv);
}


// DllRegisterServer - добавляет записи в системный реестр
STDAPI DllRegisterServer(void)
{
   // регистрирует объект, библиотеку типов и все интерфейсы в библиотеке типов
   HRESULT hr = _AtlModule.DllRegisterServer();
   return hr;
}


// DllUnregisterServer - удаляет записи из системного реестра
STDAPI DllUnregisterServer(void)
{
   HRESULT hr = _AtlModule.DllUnregisterServer();
   return hr;
}

// DllInstall - добавляет и удаляет записи системного реестра для каждого пользователя
//              и каждого компьютера.	
STDAPI DllInstall(BOOL bInstall, LPCWSTR pszCmdLine)
{
   HRESULT hr = E_FAIL;
   static const wchar_t szUserSwitch[] = _T("user");

   if (pszCmdLine != NULL)
   {
      if (_wcsnicmp(pszCmdLine, szUserSwitch, _countof(szUserSwitch)) == 0)
      {
         AtlSetPerUserRegistration(true);
      }
   }

   if (bInstall)
   {	
      hr = DllRegisterServer();
      if (FAILED(hr))
      {	
         DllUnregisterServer();
      }
   }
   else
   {
      hr = DllUnregisterServer();
   }

   return hr;
}

// Точка входа DLL
extern "C" BOOL WINAPI DllMain(HINSTANCE hInstance, DWORD dwReason, LPVOID lpReserved)
{
	hInstance;
	return _AtlModule.DllMain(dwReason, lpReserved); 
}


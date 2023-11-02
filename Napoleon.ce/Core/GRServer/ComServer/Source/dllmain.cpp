// dllmain.cpp: реализация DllMain.

#include "stdafx.h"
#include "resource.h"
#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include "dllmain.h"

CComGRServerModule _AtlModule;

// Точка входа DLL
extern "C" BOOL WINAPI DllMain(HINSTANCE hInstance, DWORD dwReason, LPVOID lpReserved)
{
	hInstance;
	return _AtlModule.DllMain(dwReason, lpReserved); 
}

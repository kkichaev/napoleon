// dllmain.cpp: определяет точку входа для приложения DLL.
#include "stdafx.h"
#include "postgre.h"
// #include "ODBCSource.h"

using namespace GRServer;

HINSTANCE hInstance;

static WORD ctr = 0;
extern "C" DECL_SPEC bool GetPlugin(IPlugin** plugin)
{
   if( ctr == 0 )
   {
      *plugin = new PostgrePlugin();
      ctr++;
      return true;
   }
   return false;
}

BOOL APIENTRY DllMain( HMODULE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved )
{
	switch (ul_reason_for_call)
	{
	case DLL_THREAD_ATTACH:
      hInstance = hModule;
      break;
	case DLL_THREAD_DETACH:
      break;
   case DLL_PROCESS_ATTACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}


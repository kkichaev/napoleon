/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* Работа с батарейкой
*
*  ert   12/04/2008   creating
*/

#include "stdafx.h"
#include <StdFuncs.h>
//#include <uniqueid.h>

typedef DWORD (*TSetSystemPowerState)(LPCWSTR pwsSystemState,
	DWORD   StateFlags, DWORD   Options);

#define POWER_STATE_ON           (DWORD)(0x00010000)        // on state

void PowerUp()
{
   HMODULE hLib = LoadLibrary(L"COREDLL.DLL");
   if( hLib )
   {
      TSetSystemPowerState ss = (TSetSystemPowerState)GetProcAddress(hLib, L"SetSystemPowerState");
      if( ss )
      {
         ss(NULL, POWER_STATE_ON, 0);
      }

      FreeLibrary(hLib);
   }
}

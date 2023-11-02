/*
 * Copyright (C), 2007 - 2009, Денис Мосягин
 *
 * GetVersion
 *
 *  ert   09/10/2009   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>
 
bool GetVersionStr(std::wstring* ver, HINSTANCE hInst)
{
   wchar_t filebuf[MAX_PATH];
   GetModuleFileName(hInst, filebuf, sizeof(filebuf)/sizeof(filebuf[0]));

   DWORD err = 0;
   DWORD handle = 0;
   DWORD size = GetFileVersionInfoSize(filebuf, &handle);
   if( size > 0 )
   {
      wchar_t* version;
      VOID *data = alloca(size);
      GetFileVersionInfo(filebuf, 0, size, data);
      if( VerQueryValue(data, L"\\StringFileInfo\\041904b0\\FileVersion",  (VOID**)&version, (UINT*)&size) == TRUE )
      {
         ver->assign(version);
         return true;
      }
   } else
   {
      err = GetLastError();
   }

   return false;
}
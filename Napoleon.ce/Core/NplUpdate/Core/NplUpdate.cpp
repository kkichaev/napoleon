/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* NplUpdate
*
*  ert   16/10/2009   creating
*/
#include "stdafx.h"
#include <ServerDefs.h>
#include "NplUpdate.h"

#include <string>

#define INT_STAT_FILE L"NplInt.state"

void GetIntStateFile(std::wstring* fileName, const wchar_t *category)
{
   wchar_t buf[MAX_PATH];

   GetModuleFileName(NULL, buf, MAX_PATH);
   wchar_t *p = wcsrchr(buf, L'\\');
   wcscpy(p+1, INT_STAT_FILE);
   if( category != NULL && *category != L'\0' )
   {
      wcscat(p, L".");
      wcscat(p, category);
   }

   *fileName = buf;
}

int WINAPI _tWinMain(HINSTANCE hInstance, HINSTANCE /*hPrevInstance*/, LPTSTR lpstrCmdLine, int nCmdShow)
{
   //std::wstring fn;
   //if( *lpstrCmdLine == L'\0' )
   //{
   //   GetIntStateFile(&fn);
   //   lpstrCmLine = fn.c_str();
   //}
   //const wchar_t testLine[] = L"\"\\Storage Card\\Debug\\Suchanov\\NplPdaUpdate.config\"";
   //const wchar_t testLine[] = L"\"\\Storage Card\\Debug\\Test\\NplPdaUpdate.config\"";
   if( !app.Start(hInstance, lpstrCmdLine) )
      return 0;

   //if( !app.Start(hInstance, lpstrCmdLine) )
   //   return 0;

   app.Do();

   return 0;
}

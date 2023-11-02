/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Позвонить по телефону
 *
 *  ert   24/09/2008   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

bool MakeCall(const wchar_t *phone)
{
   typedef LONG (*tapiRequestMakeCallT)(LPCTSTR lpszDestAddress, LPCTSTR lpszAppName, 
      LPCTSTR lpszCalledParty, LPCTSTR lpszComment);

   HINSTANCE hLib = LoadLibrary(L"CELLCORE.DLL");
   if( hLib == NULL )
      return false;

   tapiRequestMakeCallT makeCall = (tapiRequestMakeCallT)GetProcAddress(hLib, L"tapiRequestMakeCallW");
   
   bool retVal = false;
   if( makeCall != NULL )
      retVal = (makeCall(phone, NULL, NULL, NULL) == 0);

   FreeLibrary(hLib);
   return retVal;
}

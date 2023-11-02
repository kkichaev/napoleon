/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * IsFileExists
 *
 *  ert   25/08/2009   creating
 */ 
#include "stdafx.h"
#include <StdFuncs.h>

bool IsFileExist(const std::wstring& fileName)
{
   WIN32_FIND_DATA data;

   HANDLE h = FindFirstFile(fileName.c_str(), &data);
   bool bRet = (h != INVALID_HANDLE_VALUE);

   if( h != INVALID_HANDLE_VALUE ) CloseHandle(h);

   return bRet;
}
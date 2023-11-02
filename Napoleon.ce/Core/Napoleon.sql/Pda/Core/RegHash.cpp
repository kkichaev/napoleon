/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Функции для регистрации
 * 
 *  ert   01/09/2007   creating
 */ 
#include "stdafx.h"
#include "RegHash.h"

DWORD MakeAnswer(DWORD code)
{
   wchar_t buf[20], *num = buf;
   wsprintfW(buf, L"%010u", code);

   DWORD hash = 5381;
   while(*num)
   {
      hash = ((hash << 5) + hash) + *num;
      num++;
   }
   return ~hash;
}

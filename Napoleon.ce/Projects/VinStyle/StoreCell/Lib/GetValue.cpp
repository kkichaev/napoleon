/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Получить значение из строки
 *
 *  ert   20/05/2008   creating
 */
#include "stdafx.h"
#include "StdFuncs.h"

DWORD GetValue(const wchar_t *buf, DWORD scale)
{
   wchar_t decBuf[4], sepBuf[4];
   int cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_SDECIMAL, decBuf, sizeof(decBuf)/sizeof(decBuf[0]));
   decBuf[cch] = L'\0';
   cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_STHOUSAND, sepBuf, sizeof(sepBuf)/sizeof(sepBuf[0]));
   sepBuf[cch] = L'\0';

   DWORD val = 0;
   bool sign = false;
   if( *buf == L'-' ) { sign = true; buf++; }
   if( *buf == L'+' ) buf++;

   while( *buf != L'\0' && *buf != *decBuf && *buf != L'.' )
   {
      if( *buf != *sepBuf )
         val = val * 10 + *buf - L'0';
      buf++;
   }

   val *= scale;
   if( *buf == *decBuf || *buf == L'.' )
   {
      while( *(++buf) && scale > 1 )
      {
         scale /= 10;
         val += (*buf - L'0') * scale;
      }
   }
   /*
   DWORD val = _wtoi(buf) * scale;
   const wchar_t *p = wcschr(buf, L'.');
   if( p )
   {
      while( *(++p) )
      {
         scale /= 10;
         val += (*p - L'0') * scale;
      }
   }
   */

   if( sign ) val = -(int)val;
   return val;
}

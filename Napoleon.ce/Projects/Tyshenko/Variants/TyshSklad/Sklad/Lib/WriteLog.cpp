/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Log
 *
 *  ert   15/05/2010   creating
 */
#include "stdafx.h" 

#ifdef DEBUG
#include <Module.h>
#include <StdFuncs.h>
#include <stdarg.h>

void WriteToLog(wchar_t* msg, ...)
{
   va_list args;
   va_start( args, msg );

   std::wstring fn;
   _Module.MakeFileName(&fn, L"Log");
   FILE *f = _wfopen(fn.c_str(), L"at");
   if( f )
   {
      vfwprintf(f, msg, args);
      fputws(L"\n", f);
      fclose(f);
   }
}
#endif

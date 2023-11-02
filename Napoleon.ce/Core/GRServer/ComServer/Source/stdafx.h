// stdafx.h: включаемый файл дл€ стандартных системных включаемых файлов
// или включаемых файлов дл€ конкретного проекта, которые часто используютс€,
// но не часто измен€ютс€

#pragma once

#ifndef STRICT
#define STRICT
#endif

#define _CRT_SECURE_NO_WARNINGS

#include "targetver.h"

#define _ATL_APARTMENT_THREADED
#define _ATL_NO_AUTOMATIC_NAMESPACE

#define _ATL_CSTRING_EXPLICIT_CONSTRUCTORS	// некоторые конструкторы CString будут €вными
#define LOAD_LIBRARY_SEARCH_SYSTEM32 0
#define __RPC__inout_xcount(v) 
#define __RPC__in_xcount(v)

#include "resource.h"
#include <atlbase.h>
#include <atlcom.h>
#include <atlctl.h>
#include <atlstr.h>

using namespace ATL;

struct Aliases
{
   wchar_t* name;
   wchar_t* src;

   static const wchar_t* GetAlias(const wchar_t *name);
};

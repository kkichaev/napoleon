/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Обработка комнды GET
 *
 * ert   24/09/2009   creating
 */ 
#include "stdafx.h"
#include <ServerDefs.h>

#include <dbf.h>
#include "server.h"
#include "sessobj.h"
#include "session.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;


bool Session::HandleGet()
{
   const Object& o = Command();
   const Member *param = o[PARAM_MEMBER];
   if( param == NULL ) return false;

   const wstring &srcStr = (const std::wstring&)*param->str;
   wstring::size_type ep = -1;
   bool res = true;
   do
   {
      wstring::size_type pos = ep + 1;

      ep = srcStr.find(GET_PARAM_SEPARATOR, pos);
      wstring item(srcStr.substr(pos, ep - pos));

      WriteObject(item);
   } while( ep != wstring::npos );

   return res;
}

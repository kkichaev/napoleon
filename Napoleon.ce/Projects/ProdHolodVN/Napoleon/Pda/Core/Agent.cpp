/*
 * Copyright (C), 2007-2011, Денис Мосягин
 *
 * Агенты
 *
 *  ert   10/03/2011   creating
 */
#include "stdafx.h"
#include "Add.h"
#include <FormEntries.h>
#include <StdFuncs.h>
#include <EnterNumber.h>
#include <SAnchor.h>

#include <Preference.h>

BEGIN_TYPE_REFLECTION(AgentPrefix)
   REGISTER_STRING_MEMBER(AgentPrefix, id)
   REGISTER_STRING_MEMBER(AgentPrefix, login)
   REGISTER_STRING_MEMBER(AgentPrefix, password)
   REGISTER_STRING_MEMBER(AgentPrefix, prefix)
END_TYPE_REFLECTION(AgentPrefix)

bool AgentPrefixImpl::GetPrefix(std::wstring *prefix)
{
   bool res = false;

   Preference pref;
   pref.Load();

   AgentPrefixImpl a;
   SQLTable t(a.Name());
   bool bdo = t.Select(&a);

   wchar_t *lgn, *pwd;
   int len;

   len = strlen(pref.login) + 1;
   lgn = (wchar_t*)alloca(len * sizeof(wchar_t));
   mbstowcs(lgn, pref.login, len);

   len = strlen(pref.password) + 1;
   pwd = (wchar_t*)alloca(len * sizeof(wchar_t));
   mbstowcs(pwd, pref.password, len);

   while( bdo )
   {
      if( wcscmp(a.login, lgn) == 0 && wcscmp(a.password, pwd) == 0 )
      {
         prefix->assign(a.prefix);
         res = true;
         break;
      }
      bdo = t.SelectNext(&a);
   }

   return res;
}

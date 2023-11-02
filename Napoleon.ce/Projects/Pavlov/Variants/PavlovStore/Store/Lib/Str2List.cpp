/*
 * Copyright (C), 2007 - 2009, Денис Мосягин
 *
 * Строку в вектор
 *
 *  ert   16/06/2009   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

//
//-------------------------------------- StringToList---------------------------------------
//
void StringToList(std::vector<std::wstring> *fields, const wchar_t *fieldsStr, wchar_t delimiter, bool eatSpace)
{
   std::wstring field;
   const wchar_t *pos = fieldsStr;
   for( ; *pos != L'\0'; pos++ )
   {
      wchar_t sym = *pos;
      if( eatSpace && sym == L' ' ) continue;
      if( sym == delimiter )
      {
         fields->push_back(field);
         field.clear();
         continue;
      }
      field.append(1, sym);
   }
   if( !field.empty() ) fields->push_back(field);
}


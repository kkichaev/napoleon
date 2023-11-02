/*
* Copyright (C), 2007, Денис Мосягин
*
* Распределение памяти для строк
*
* ert   01/08/2007   creating
*/ 

#ifndef __STRING_HOLDER_H
#define __STRING_HOLDER_H

#include <vector>

class StringHolder
{
public:
   StringHolder() {}
   ~StringHolder()
   {
      Clear();
   }

   wchar_t* Add(const wchar_t *src)
   {
      if( *src == L'\0' ) return L"";

      wchar_t *dest = (wchar_t*)malloc((wcslen(src) + 1) * sizeof(wchar_t));
      wcscpy(dest, src);
      
      chunks.push_back(dest);
      return dest;
   }

   wchar_t* Add(const char *src, UINT cp = CP_ACP)
   {
      int len = MultiByteToWideChar(cp, 0, src, -1, NULL, 0);
      wchar_t* dest = (wchar_t*)malloc(len * sizeof(wchar_t));
      MultiByteToWideChar(cp, 0, src, -1, dest, len);
      
      chunks.push_back(dest);
      return dest;
   }

   void Clear()
   {
      std::vector<wchar_t*>::iterator i = chunks.begin();
      for( ;i != chunks.end(); i++ )
         delete (*i);
      chunks.clear();
   }

protected:
   std::vector<wchar_t*> chunks;
};

#endif
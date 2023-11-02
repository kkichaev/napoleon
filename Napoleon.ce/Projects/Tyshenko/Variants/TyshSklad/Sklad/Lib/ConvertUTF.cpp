#include "stdafx.h"
#include <StdFuncs.h>

static wchar_t cnvUtf[] = L"יצףךוםדרשחץתפûגאןנמכהז‎ÿקסלטעüב‏ÉÖÓÊÅÍÃØÙÇÕÚÔÛÂÀÏÐÎËÄÆÝ‗×ÑÌÈÒÜÁÞ¸¨¹`1234567890-=qwertyuiop[]\asdfghjkl;'zxcvbnm,./~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?";
static char    cnvAnsi[] = "יצףךוםדרשחץתפûגאןנמכהז‎ÿקסלטעüב‏ÉÖÓÊÅÍÃØÙÇÕÚÔÛÂÀÏÐÎËÄÆÝ‗×ÑÌÈÒÜÁÞ¸¨¹`1234567890-=qwertyuiop[]\asdfghjkl;'zxcvbnm,./~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL:\"ZXCVBNM<>?";

bool Convert(char* dest, const wchar_t* src, WORD len)
{
   int index = 0;
   const wchar_t *p = src;
   while( *p != L'\0' && (len < 0 || index < len) )
   {
      const wchar_t *sym = wcschr(cnvUtf, *p);
      if( sym != NULL )
      {
         int offset = (int)(sym - cnvUtf);
         *dest = *(cnvAnsi + offset);
      } else
      {
         char abuf[3];
         wchar_t buf[2];
         buf[0] = *p;
         *(buf+1) = L'\0';
         wcstombs(abuf, buf, 1);

         *dest = *abuf;
      }
      dest++;
      index++;
      p++;
   }

   return true;
}


bool Convert(wchar_t* dest, const char* src, WORD len)
{
   int index = 0;
   const char *p = src;
   while( *p != '\0' && (len < 0 || index < len) )
   {
      const char* sym = strchr(cnvAnsi, *p);
      if( sym != NULL )
      {
         int offset = (int)(sym - cnvAnsi);
         *dest = *(cnvUtf + offset);
      } else
      {
         char abuf[3];
         wchar_t buf[2];
         abuf[0] = *p;
         abuf[1] = '\0';
         mbstowcs(buf, abuf, 1);

         *dest = *buf;
      }
      dest++;
      index++;
      p++;
   }

   return true;
}
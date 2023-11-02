// datetill.cpp: определяет точку входа для консольного приложения.
//

#include "stdafx.h"


int _tmain(int argc, _TCHAR* argv[])
{
   if( argc != 2 )
   {
      _tprintf(_T("datetill 20.12.2005"));
      return 1;
   }

   SYSTEMTIME st = {0};
   __int64 ft;
   int d, m, y;
   if( _stscanf(argv[1], _T("%02d.%02d.%d"), &d, &m, &y) != 3 )
   {
      _tprintf(_T("param error"));
      return 1;
   }
   st.wDay = (WORD)d;
   st.wMonth = (WORD)m;
   st.wYear = (WORD)y;

   SystemTimeToFileTime(&st, (FILETIME*)&ft);
   ft /= (__int64)10000000;
   DWORD value = (DWORD)(ft / 3600);
   _tprintf(_T("%u"), value);
   return 0;
}


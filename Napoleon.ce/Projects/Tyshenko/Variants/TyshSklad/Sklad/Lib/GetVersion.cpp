/*
* Copyright (C), 2007, Денис Мосягин
*
* Получить версию системы
*
*  ert   04/12/2007   creating
*/

#include "stdafx.h"
#include <StdFuncs.h>

PlatformVersion GetPlatformVersion()
{
   BOOL rb;
   PlatformVersion iDevice = vUnknown;
   OSVERSIONINFO osvi;
   TCHAR szPlatform[MAX_PATH];

   osvi.dwOSVersionInfoSize = sizeof(osvi);
   rb = GetVersionEx(&osvi);
   if (rb == FALSE)
      return vUnknown;

   switch (osvi.dwPlatformId)
   {
   case VER_PLATFORM_WIN32_CE:
      rb = SystemParametersInfo(SPI_GETPLATFORMTYPE, MAX_PATH, szPlatform, 0);
      if (rb == FALSE)
         return vUnknown;

      if (lstrcmpi(szPlatform, L"Smartphone") == 0)  
      {
         switch( osvi.dwMajorVersion )
         {
            case 3:
               iDevice = vSP2002;
               break;
            case 4:
               iDevice = vSP2003;
               break;
            case 5:
               if (osvi.dwMinorVersion < 2 )
                  iDevice = vSP5;
               else
                  iDevice = vSP6;
               break;
         }
      } 
      else if (lstrcmp(szPlatform, L"PocketPC") == 0) 
      {
         switch( osvi.dwMajorVersion )
         {
            case 3:
            {
               if (osvi.dwMinorVersion == 0)
                  iDevice = vPPC2000;
               else if (osvi.dwMinorVersion == 1)
                  iDevice = vPPC2002;
               break;
            }
            case 4:
            {
               if( osvi.dwMinorVersion == 20 )
                  iDevice = vPPC2003;
               else
                  iDevice = vPPC2003SE;
               break;
            }
            case 5:
            {
               if (osvi.dwMinorVersion < 2 )
                  iDevice = vPPC5;
               else
                  iDevice = vPPC6;
               break;
            }
         }
      }
      break;
   default:
      break;
   }

   return iDevice;
}

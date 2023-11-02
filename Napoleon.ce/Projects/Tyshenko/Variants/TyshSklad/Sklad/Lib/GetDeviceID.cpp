/*
* Copyright (C), 2007, Денис Мосягин
*
* GetDeviceID
*
*  ert   06/12/2007   creating
*/

#include "stdafx.h"
#include <StdFuncs.h>

#ifdef WIN32_PLATFORM_PSPC

#include <uniqueid.h>

DWORD GetDeviceID()
{
   DWORD id = 0;
   DWORD dwOutBytes;
   const int nBuffSize = 4096;
   char devBuf[nBuffSize];
   BOOL bRes = ::KernelIoControl(IOCTL_HAL_GET_DEVICEID, 0, 0, devBuf, nBuffSize, &dwOutBytes);
   if (bRes)
   {
      if( ((DEVICE_ID*)devBuf)->dwPresetIDBytes )
      {
         char *src = devBuf + ((DEVICE_ID*)devBuf)->dwPresetIDOffset;
         for( DWORD i=0; i<((DEVICE_ID*)devBuf)->dwPresetIDBytes; i++ )
            id += *src++;
      }
   }

   return id;
}

#else
DWORD GetDeviceID()
{
   return 0;
}
#endif
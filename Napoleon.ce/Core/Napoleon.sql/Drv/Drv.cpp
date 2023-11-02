/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Virtual COM port
 *
 * ert   10/09/2010   creating
 */ 
#include "windows.h"

const DWORD DEVICE_CONTEXT = 1;
const DWORD OPEN_CONTEXT = 1;
const DWORD WAIT_DATA_TIMEOUT = 200;

const int DEFAULT_BUF_SIZE = 2048;

int bufSize = 0;
int curPos = 0;
char *dataBuf;

CRITICAL_SECTION cs;
HANDLE haveData;

static bool PushPopByte(char* data, bool push)
{
   bool res = false;
   EnterCriticalSection(&cs);

   if( push )
   {
      if( curPos < bufSize )
      {
         dataBuf[curPos++] = *data;
         res = true;

         SetEvent(haveData);
      }
   } else
   {
      if( curPos > 0 )
      {
         ResetEvent(haveData);

         res = true;

         *data = *dataBuf;
         if( --curPos > 0 )
            memcpy(dataBuf, dataBuf+1, curPos);
      }
   }

   LeaveCriticalSection(&cs);
   return res;
}

static bool GetByte(char* res, DWORD wait)
{
   bool bres = false;

   if( curPos == 0 )
      WaitForSingleObject(haveData, wait);

   if( curPos > 0 )
      bres = PushPopByte(res, false);

   return bres;
}

BOOL APIENTRY DllMain( HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved )
{
	switch ( ul_reason_for_call )
	{
	case DLL_PROCESS_ATTACH:
		break;
	case DLL_PROCESS_DETACH:
		break;
	case DLL_THREAD_ATTACH:
		break;
	case DLL_THREAD_DETACH:
		break;
	}
	return TRUE;
}

HANDLE COM_Init(ULONG Identifier)
{
   bufSize = DEFAULT_BUF_SIZE;
   dataBuf = (char*)malloc(bufSize);

   curPos = 0;

   InitializeCriticalSection(&cs);
   haveData = CreateEvent(NULL, TRUE, FALSE, NULL);
	
   return (HANDLE)DEVICE_CONTEXT;
}

BOOL COM_Deinit()
{
   free(dataBuf);
   dataBuf = NULL;

   CloseHandle(haveData);
   DeleteCriticalSection(&cs);
	return TRUE;
}

HANDLE COM_Open(HANDLE hDeviceContext, DWORD AccessCode,  DWORD ShareMode)
{
   return (HANDLE)OPEN_CONTEXT;
}

BOOL COM_Close(HANDLE hOpenContext)
{
	return TRUE;
}

BOOL COM_IOControl(HANDLE hOpenContext, DWORD dwCode, PBYTE pBufIn, DWORD dwLenIn, PBYTE pBufOut, DWORD dwLenOut, PDWORD pdwActualOut)
{
	return TRUE;
}

void COM_PowerUp(HANDLE hDeviceContext)
{
}

void COM_PowerDown(HANDLE hDeviceContext)
{
}

DWORD COM_Read(HANDLE hOpenContext, PBYTE pTargetBuffer, ULONG BufferLength, PULONG pBytesRead)
{
   if( dataBuf == NULL )
      return -1;

   ULONG readed = 0;
   while( readed < BufferLength )
   {
      if( GetByte((char*)pTargetBuffer++, WAIT_DATA_TIMEOUT) == false )
         break;
      readed++;
   }

   //if( pBytesRead )
   //   *pBytesRead = readed;

	return readed;
}

DWORD COM_Write(HANDLE hOpenContext, PBYTE pSourceBytes, ULONG NumberOfBytes)
{
   if( dataBuf == NULL )
      return -1;

   ULONG writed = 0;
   while( writed < NumberOfBytes )
   {
      if( PushPopByte((char*)pSourceBytes++, true) == false )
         break;
      writed++;
   }

	return writed;
}


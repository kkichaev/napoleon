/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Сканер
*
*  ert   05/09/2010   creating
*/
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>
#include <ListForm.h>
#include "FormEntries.h"

#include "Preference.h"
//
// ------------------------------- Scanner impl ---------------------------
//
typedef void (*ReadSymHandler)(char sym);

static HANDLE handle = INVALID_HANDLE_VALUE;
static HANDLE hStop = INVALID_HANDLE_VALUE;
static HANDLE hThread = INVALID_HANDLE_VALUE;
static ReadSymHandler ReadHandler = NULL;

static bool IsScannerOpen()
{
   return (handle != INVALID_HANDLE_VALUE);
}

static void CloseScanner()
{
   SetEvent(hStop);

   if( WaitForSingleObject(hThread, 1000) != WAIT_OBJECT_0 )
      TerminateThread(hThread, 0);

   CloseHandle(hThread);
   CloseHandle(hStop);

   hStop = INVALID_HANDLE_VALUE;
   hThread = INVALID_HANDLE_VALUE;

   ReadHandler = NULL;

   if( handle != INVALID_HANDLE_VALUE )
   {
      CloseHandle(handle);
      handle = INVALID_HANDLE_VALUE;
   }
}

static DWORD ReadProc(void *)
{
   char sym = 0;
   DWORD cb = 0;

   while( true )
   {
      if( WaitForSingleObject(hStop, 0) == WAIT_OBJECT_0 )
         break;

      if( ReadFile(handle, &sym, sizeof(sym), &cb, NULL) == TRUE && cb > 0 )
      {
         if( ReadHandler != NULL )
            ReadHandler(sym);
      }
   }

   return 0;
}

static bool OpenScanner(WORD port, DWORD baudRate, ReadSymHandler rsh)
{
   wchar_t buf[20];
   wsprintf(buf, L"COM%d:", port);
   handle = CreateFile(buf, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);

   if( handle == INVALID_HANDLE_VALUE )
      return false;

   SetupComm(handle, 4096, 4096 ) ;

   DCB dcb = { 0 };

   dcb.DCBlength = sizeof(dcb);
   GetCommState(handle, &dcb);

   dcb.BaudRate = baudRate;
   dcb.ByteSize = 8;
   dcb.StopBits = 2;
   dcb.XonLim = 2048;
   dcb.XoffLim = 512;
   dcb.fBinary = 1;
   SetCommState(handle, &dcb);

   COMMTIMEOUTS ct;
   GetCommTimeouts(handle, &ct);
   ct.ReadIntervalTimeout = baudRate/100;
   ct.ReadTotalTimeoutMultiplier = 0;
   SetCommTimeouts(handle, &ct);

   hStop = CreateEvent(NULL, TRUE, FALSE, NULL);
   hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)ReadProc, NULL, 0, 0);

   ReadHandler = rsh;
   return true;
}

//
// ------------------------------- Scanner impl ---------------------------
//
static UINT scanMsg;
static HWND scanWnd;
static std::string data;

void SymHandler(char sym)
{
   if( IsWindow(scanWnd) )
   {
      if( sym == '\r' || sym == '\n'  )
         PostMessage(scanWnd, scanMsg, 0, 0);
      else
         data.append(1, sym);
   }
}

void StartScan(HWND hWnd, UINT msg)
{
   Preference p;
   p.Load();

   OpenScanner(p.scanPort, 9600, SymHandler);

   scanMsg = msg;
   scanWnd = hWnd;
   data.clear();
}

void StopScan()
{
   scanWnd = NULL;
   data.clear();
   CloseScanner();
}

bool GetScan(std::wstring* rdata)
{
   int len = data.size() + 1;
   wchar_t *buf = (wchar_t*) alloca(len * sizeof(wchar_t));
   mbstowcs(buf, data.c_str(), len);

   *rdata = buf;
   data.clear();
   return !(rdata->empty());
}

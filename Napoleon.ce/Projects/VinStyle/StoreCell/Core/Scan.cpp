/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Сканер
*
*  ert   04/10/2010   creating
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

#ifdef Urovo

#include "UrovoDevice.h"

static bool needClose = false;
static HINSTANCE lib = NULL;
static HWND gHwnd;
static UINT gMsg = WM_SCAN_DATA;
static HANDLE hThread = INVALID_HANDLE_VALUE;
static HANDLE hEvent[2];
static HANDLE hNotify = 0;
static std::wstring scanBuf;

typedef int (*TSCA_GetPowerStatus)();
typedef DWORD (*TSCA_RegisterNotification)(HANDLE hMsgQ);
typedef BOOL (*TSCA_EnableModule)();
typedef BOOL (*TSCA_UnRegisterNotification)(DWORD dwID);
typedef BOOL (*TSCA_DisableModule)();

static DWORD DoScan(void *param)
{
	while (true)
	{
		DWORD evt = WaitForMultipleObjects(2, hEvent, FALSE, INFINITE);
		switch (evt)
		{
		case 0://return thread
			return 0;
		case 1://disable network 
			DWORD bytesRead;
			DWORD flags;

			char buf[256];

			if (ReadMsgQueue(hEvent[1], buf, sizeof(buf), &bytesRead, INFINITE, &flags))
			{
				USES_CONVERSION;
				scanBuf.assign(A2W(buf+2), bytesRead-2);
				PostMessage(gHwnd, gMsg, 0, 0);
			}
			break;
		}
	}

	return 0;
}

void StartScan(HWND hWnd)
{
	if(lib == NULL)
	{
		lib = LoadLibrary(L"Device.dll");
		if( lib == NULL )
			return;
	}

	TSCA_GetPowerStatus PwrS = (TSCA_GetPowerStatus)GetProcAddress(lib, L"SCA_GetPowerStatus");
	TSCA_EnableModule Enbl = (TSCA_EnableModule)GetProcAddress(lib, L"SCA_EnableModule");
	TSCA_RegisterNotification RegNtfy = (TSCA_RegisterNotification)GetProcAddress(lib, L"SCA_RegisterNotification");

	needClose = false;
	if(PwrS() == 0)
	{
		needClose = true;
		Enbl();
	}

	hThread = INVALID_HANDLE_VALUE;
	hEvent[0] = hEvent[1] = INVALID_HANDLE_VALUE;
	hNotify = 0;

	MSGQUEUEOPTIONS opt;
	opt.dwSize = sizeof(opt);
	opt.dwFlags = MSGQUEUE_ALLOW_BROKEN;
	opt.dwMaxMessages = 3;
	opt.cbMaxMessage = 252;
	opt.bReadAccess = TRUE;

	HANDLE hMsg = CreateMsgQueue(NULL, &opt);
	if( hMsg == NULL )
		return;

	hNotify = (HANDLE)RegNtfy(hMsg);
	if( hNotify == NULL )
		return;

	hEvent[0] = CreateEvent(0, FALSE, FALSE, NULL);
	hEvent[1] = hMsg;
	gHwnd = hWnd;

	hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoScan, NULL, 0, NULL);
}

bool GetScanData(std::wstring* data, LPARAM lParam)
{
	data->assign(scanBuf);
	return true;
}

void StopScan()
{
	if(lib == NULL)
		return;

	if(hEvent[0] == INVALID_HANDLE_VALUE)
		return;

	SetEvent(hEvent[0]);
	
	DWORD res = WaitForSingleObject(hThread, 3000);
	if( res != WAIT_OBJECT_0 )
		TerminateThread(hThread, 0);
	hEvent[0] = INVALID_HANDLE_VALUE;

	TSCA_UnRegisterNotification UnReg = (TSCA_UnRegisterNotification)GetProcAddress(lib, L"SCA_UnRegisterNotification");
	UnReg((DWORD)hNotify);
	CloseHandle(hEvent[1]);
	
	if(needClose)
	{
		TSCA_DisableModule Dis = (TSCA_DisableModule)GetProcAddress(lib, L"SCA_DisableModule");
		Dis();
	}
}

#else // Motorola

#include <scancapi.h>

static HANDLE hScanner = NULL;
static SCAN_BUFFER *buf = NULL;
static HINSTANCE lib = NULL;
static HWND gHwnd;
static UINT gMsg = WM_SCAN_DATA;

typedef DWORD (*TSCAN_FindFirst_W)(LPSCAN_FINDINFO_W lpScanFindInfo, LPHANDLE lphFindHandle);
typedef DWORD (*TSCAN_FindClose)(HANDLE hFindHandle);
typedef DWORD (*TSCAN_Open)(LPCTSTR lpszDeviceName, LPHANDLE lphScanner);
typedef DWORD (*TSCAN_Enable)(HANDLE hScanner);
typedef LPSCAN_BUFFER_W (*TSCAN_AllocateBuffer_W)(BOOL bText, DWORD dwSize);
typedef DWORD (*TSCAN_ReadLabelMsg_W)(HANDLE hScanner, LPSCAN_BUFFER_W lpScanBuffer, HWND hWnd, UINT uiMsgNo, DWORD dwTimeout, LPDWORD lpdwRequestID);
typedef DWORD (*TSCAN_Flush)(HANDLE hScanner);
typedef DWORD (*TSCAN_DeallocateBuffer_W)(LPSCAN_BUFFER_W lpScanBuffer);
typedef DWORD (*TSCAN_Disable)(HANDLE hScanner);
typedef DWORD (*TSCAN_Close)(HANDLE hScanner);
typedef DWORD (*TSCAN_CancelRead)(HANDLE hScanner, DWORD reqID);
typedef DWORD (*TSCAN_GetScanParameters_W)(HANDLE hScanner, LPSCAN_PARAMS_W lpScanParams);
typedef DWORD (*TSCAN_SetScanParameters_W)(HANDLE hScanner, LPSCAN_PARAMS_W lpScanParams);

inline void DO_SCAN()
{
	TSCAN_ReadLabelMsg_W RLM = (TSCAN_ReadLabelMsg_W)GetProcAddress(lib, L"SCAN_ReadLabelMsg_W");
	RLM(hScanner, buf, gHwnd, gMsg, 0, NULL);
}


void StartScan(HWND hWnd)
{
   TSCAN_FindFirst_W FF;
   TSCAN_FindClose FC;
   TSCAN_Open Open;
   TSCAN_Enable Enable;
   TSCAN_AllocateBuffer_W AB;
	TSCAN_GetScanParameters_W GetP;
	TSCAN_SetScanParameters_W SetP;

   if( lib == NULL )
   {
      lib = LoadLibrary(L"SCNAPI32.DLL");
      if( lib == NULL )
         return;

      FF = (TSCAN_FindFirst_W)GetProcAddress(lib, L"SCAN_FindFirst_W");
      FC = (TSCAN_FindClose)GetProcAddress(lib, L"SCAN_FindClose");
      Open = (TSCAN_Open)GetProcAddress(lib, L"SCAN_Open");
      Enable = (TSCAN_Enable)GetProcAddress(lib, L"SCAN_Enable");
      AB = (TSCAN_AllocateBuffer_W)GetProcAddress(lib, L"SCAN_AllocateBuffer_W");
		GetP = (TSCAN_GetScanParameters_W)GetProcAddress(lib, L"SCAN_GetScanParameters_W");
		SetP = (TSCAN_SetScanParameters_W)GetProcAddress(lib, L"SCAN_SetScanParameters_W");
   }

   DWORD res;
   if( hScanner == NULL )
   {
      SCAN_FINDINFO_W fi = { 0 };
      HANDLE fh;

      SI_INIT(&fi);
      res = FF(&fi, &fh);
      if( res == E_SCN_SUCCESS )
      {
         FC(fh);

         res = Open(fi.szDeviceName, &hScanner);
         if( res != E_SCN_SUCCESS ) hScanner = NULL;
         else
         {
				SCAN_PARAMS_W params;
				GetP(hScanner, &params);
				params.dwScanType = SCAN_TYPE_MONITOR;
				SetP(hScanner, &params);

            if( Enable(hScanner) != E_SCN_SUCCESS ) StopScan();
            else
            {
               buf = AB(TRUE, 2000);
               SI_INIT(buf);
               gHwnd = hWnd;

					DO_SCAN();
            }
         }
      }
   }
}

bool GetScanData(std::wstring* data, LPARAM lParam)
{
   bool ret = false;

   if(lib != NULL )
   {
      if( ((SCAN_BUFFER*)lParam)->dwStatus == E_SCN_SUCCESS)
      {
         data->assign((wchar_t*)SCNBUF_GETDATA((SCAN_BUFFER*)lParam), SCNBUF_GETLEN((SCAN_BUFFER*)lParam));
         ret = true;
      }

		DO_SCAN();
   }

   return ret;
}

HWND RestartScan(HWND newHWnd)
{
	HWND hRet = gHwnd;
	gHwnd = newHWnd;

	if( lib != 0)
	{
		if( hRet != 0 )
		{
			TSCAN_CancelRead CancelRead = (TSCAN_CancelRead)GetProcAddress(lib, L"SCAN_CancelRead");
			CancelRead(hScanner, SCNBUF_GETREQID(buf));
		}
		if( newHWnd != NULL )
		{
			DO_SCAN();
		}
	}

	return hRet;
}

void StopScan()
{
   if( lib == NULL )
      return;

   TSCAN_Flush Flush;
   TSCAN_DeallocateBuffer_W DB;
   TSCAN_Disable Disable;
   TSCAN_Close Close;

   Flush = (TSCAN_Flush)GetProcAddress(lib, L"SCAN_Flush");
   DB = (TSCAN_DeallocateBuffer_W)GetProcAddress(lib, L"SCAN_DeallocateBuffer_W");
   Disable = (TSCAN_Disable)GetProcAddress(lib, L"SCAN_Disable");
   Close = (TSCAN_Close)GetProcAddress(lib, L"SCAN_Close");

   if( buf != NULL )
   {
      Flush(hScanner);
      DB(buf);
      buf = NULL;
   }

   if( hScanner != NULL )
   {
      Disable(hScanner);
      Close(hScanner);

      hScanner = NULL;
   }

   FreeLibrary(lib);
   lib = NULL;
}

#endif // Urovo|Motorola
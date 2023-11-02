#define _CRT_SECURE_NO_WARNINGS

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <malloc.h>

#include <strsafe.h>

void ErrorExit(LPTSTR lpszFunction) 
{ 
    // Retrieve the system error message for the last-error code

    LPVOID lpMsgBuf;
    LPVOID lpDisplayBuf;
    DWORD dw = GetLastError(); 

    FormatMessage(
        FORMAT_MESSAGE_ALLOCATE_BUFFER | 
        FORMAT_MESSAGE_FROM_SYSTEM |
        FORMAT_MESSAGE_IGNORE_INSERTS,
        NULL,
        dw,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
        (LPTSTR) &lpMsgBuf,
        0, NULL );

    // Display the error message and exit the process

    lpDisplayBuf = (LPVOID)LocalAlloc(LMEM_ZEROINIT, 
        (lstrlen((LPCTSTR)lpMsgBuf) + lstrlen((LPCTSTR)lpszFunction) + 40) * sizeof(TCHAR)); 
    StringCchPrintf((LPTSTR)lpDisplayBuf, 
        LocalSize(lpDisplayBuf) / sizeof(TCHAR),
        TEXT("%s failed with error %d: %s"), 
        lpszFunction, dw, lpMsgBuf); 
    MessageBox(NULL, (LPCTSTR)lpDisplayBuf, TEXT("Error"), MB_OK); 

    LocalFree(lpMsgBuf);
    LocalFree(lpDisplayBuf);
    ExitProcess(dw); 
}

void CopyString(wchar_t *dest, const wchar_t *src)
{
   const wchar_t* ep = src + wcslen(src);
   if( *src == L'"' )
   {
      src++;
      ep--;
   }

   while( *src == L' ') src++;

   for( ; src < ep; src++ )
   {
      if( *src == L'\\' && *(++src) != '"' )
         *dest++ = '\\';
      *dest++ = *src;
   }
   *dest = L'\0';
}

int wmain(int argc, wchar_t* argv[])
{
   int i;
   STARTUPINFO si;
   PROCESS_INFORMATION pi;
   wchar_t *p = GetCommandLine();
   wchar_t *ep = wcsstr(p, argv[0]);
   if( ep )
   {
      ep += wcslen(argv[0]);
      if( *ep = L'"') ep++;
   } else
      ep = p;

   while( *ep == L' ') ep++;
   while( *ep != L' ' ) ep++; // skip -c
   while( *ep == L' ') ep++;

   memset(&si, 0, sizeof(si));
   si.cb = sizeof(si);

   wchar_t *var = _wgetenv(L"BIN_PATH");
   if( var )
   {
      wchar_t PATH[] = L"PATH=";
      wchar_t *pth = (wchar_t*)alloca(sizeof(PATH) + wcslen(var) * sizeof(wchar_t) + 10);
      wcscpy(pth, PATH);
      wcscat(pth, var);
      _wputenv(pth);
      //MessageBox(NULL, var, L"!", MB_OK);
   }
#ifdef _DEBUG
   //MessageBox(NULL, argv[2], L"!", MB_OK);
   var = _wgetenv(L"PATH");
   if( var )
      MessageBox(NULL, var, L"!", MB_OK);
#endif

   wchar_t* buf = (wchar_t*)alloca((wcslen(ep) + 1) * sizeof(wchar_t));
   CopyString(buf, ep);

#ifdef _DEBUG
   FILE *wr = fopen("out.txt", "wt");
   fputws(buf, wr);
   fclose(wr);
#endif

   if( CreateProcess(NULL, buf, NULL, NULL, FALSE/*TRUE*/, CREATE_DEFAULT_ERROR_MODE|CREATE_NEW_PROCESS_GROUP, 
		 NULL, NULL, &si, &pi) == FALSE )
   {
      i = GetLastError();
      ErrorExit(L"CreateProcess");
      return i;
   }

   WaitForSingleObject(pi.hProcess, INFINITE);

   GetExitCodeProcess(pi.hProcess, (DWORD*)&i);
   CloseHandle(pi.hThread);
   CloseHandle(pi.hProcess);
   return i;
}
/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Загрузчик сервера (служба или трей)
 *
 * ert   24/03/2009   creating
 */
#include "stdafx.h"
#include <service.h>
#include <malloc.h>
#include <sstream>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

//
//------------------------------ ServiceLoader ----------------------------
//

const int MAX_STOP_TRY = 10; // ждем 10 * waitHint

DWORD ServiceLoader::checkPoint = 0;
DWORD ServiceLoader::waitHint = 3000; // мс, не менее 100мс

ServiceLoader* ServiceLoader::loader = NULL;
SERVICE_STATUS_HANDLE ServiceLoader::ssh = NULL;

const TCHAR ServiceLoader::serviceRunArg[] = L"--run-service";

DWORD CountArgs(LPCTSTR args)
{
   bool quoted = false;
   bool prevIsSpace = true;
   LPCTSTR p = (LPTSTR)args;
   DWORD argN = 0;

   for( ; *p != L'\0'; p++ )
   {
      // считаем несколько пробелов за один 
      if( *p != L' ' && !quoted  && prevIsSpace )
         argN++;

      if( *p == L'"' )
      {
         prevIsSpace = false;
         quoted = !quoted;
         continue;
      }

      prevIsSpace = (!quoted && *p == L' ');
   }

   return argN;
}

// для освобождения памяти просто вызвать free((LPTSTR)argV);
DWORD ArgsToVector(LPCTSTR args, char*** argV)
{
   *argV = NULL;

   wchar_t bf[500];
   GetModuleFileName(NULL, bf, sizeof(bf) / sizeof(bf[0]));
   std::wstring argT(bf);
   argT.append(1, L' ').append(args);
   args = argT.c_str();

   bool quoted = false;
   bool prevIsSpace = true;

   DWORD argN = CountArgs(args) + 1;
   if( argN == 0 )
      return 0;

   char* p = (char*)malloc((wcslen(args) + 1) * sizeof(char) + argN * sizeof(char**));

   *argV = (char**)p;
   p += argN * sizeof(char**) / sizeof(char);
   if( sizeof(TCHAR) == sizeof(char) )
      strcpy(p, (const char*)args);
   else
   {
      USES_CONVERSION;
      strcpy(p, W2A_CP(args, CP_UTF8));
   }

   argN = 0;
   quoted = false;

   for( ; *p != '\0'; p++ )
   {
      if( *p != ' ' && !quoted && prevIsSpace )
         (*argV)[argN++] = p;

      prevIsSpace = false;
      if( *p == '"' )
      {
         quoted = !quoted;
         continue;
      }

      if( !quoted && *p == ' ' )
      {
         prevIsSpace = true;
         *p = '\0';
      }
   }

   return argN;
}

ServiceLoader::ServiceLoader(IRunnableModule *module, LPCTSTR args) : IProgramLoader(module)
{
   argc = ArgsToVector(args, &argv);
}

ServiceLoader::~ServiceLoader()
{
   free((LPTSTR*)argv);
}

bool ServiceLoader::IsInstalled(const IRunnableModule *module)
{
   SC_HANDLE schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
   if( schSCManager == NULL )
      return false;

   bool retVal;
   SC_HANDLE hService = OpenService(schSCManager, module->Name(), SERVICE_INTERROGATE);
   if( hService != NULL )
   {
      retVal = true;
      CloseServiceHandle(hService);
   } else
      retVal = false;

   CloseServiceHandle(schSCManager);

   return retVal;
}

bool ServiceLoader::Install(const IRunnableModule *module, bool runAfterInstall, LPCTSTR args)
{
   TCHAR path[MAX_PATH];
   if( GetModuleFileName(NULL, path, sizeof(path)/sizeof(TCHAR)) == 0 )
      return false;

   if( args == NULL ) args = L"";

   //quoted path
   size_t pathLen = wcslen(path) + 2;
   TCHAR *serviceStr = (TCHAR*)alloca((pathLen + 2 + wcslen(serviceRunArg) + 2 + wcslen(args)) * sizeof(TCHAR));

   *serviceStr = L'"';
   wcscpy(serviceStr+1, path);
   wcscat(serviceStr, L"\"");

   wcscat(serviceStr, L" ");
   wcscat(serviceStr, serviceRunArg);
   wcscat(serviceStr, L" ");

   wcscat(serviceStr, args);

   SC_HANDLE schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
   if( schSCManager == NULL )
      return false;

   SC_HANDLE schService = CreateService(schSCManager, module->Name(), module->DisplayName(),
            SERVICE_ALL_ACCESS, SERVICE_WIN32_OWN_PROCESS, SERVICE_AUTO_START, 
            SERVICE_ERROR_NORMAL, serviceStr, NULL, NULL, NULL, NULL, NULL);

   bool retVal = false;
   if( schService )
   {
      retVal = true;

      SERVICE_DESCRIPTION sd;
      sd.lpDescription = (LPTSTR)module->DisplayName();
      ChangeServiceConfig2(schService, SERVICE_CONFIG_DESCRIPTION, &sd);

      if( runAfterInstall )
      {
         char **argV;
         DWORD argN = ArgsToVector(serviceStr + pathLen + 1, &argV);
         StartService(schService, argN, (LPCTSTR*)argV);
         free((char*)argV);
      }

      CloseServiceHandle(schService);
   }

   CloseServiceHandle(schSCManager);
   return retVal;  
}

bool ServiceLoader::Uninstall(const IRunnableModule *module)
{
   SC_HANDLE schSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
   if( schSCManager == NULL )
      return false;

   bool retVal = false;

   SC_HANDLE schService = OpenService(schSCManager, module->Name(), SERVICE_ALL_ACCESS);
   if( schService != NULL )
   {
      SERVICE_STATUS ssStatus;
      if( ControlService( schService, SERVICE_CONTROL_STOP, &ssStatus ) )
      {
         Sleep(1000);
         while( QueryServiceStatus( schService, &ssStatus ) )
         {
            if( ssStatus.dwCurrentState != SERVICE_STOP_PENDING )
               break;
            Sleep(1000);
         }
      }

      retVal = (DeleteService(schService) != FALSE);
      if( !retVal )
      {
         DWORD lastErr = GetLastError();
         if( lastErr == ERROR_SERVICE_MARKED_FOR_DELETE ) retVal = true;
      }
   }

   CloseServiceHandle(schSCManager);
   return retVal;  
}

bool ServiceLoader::Run()
{
   SERVICE_TABLE_ENTRY dispatchTable[] =
   {
      { (LPTSTR)module->Name(), (LPSERVICE_MAIN_FUNCTION)ServiceMain },
      { NULL, NULL }
   };
   loader = this;

   return (StartServiceCtrlDispatcher(dispatchTable) != NULL);
}

void ServiceLoader::SetStatus(DWORD currentState)
{
   SERVICE_STATUS ss;

   // Disable control requests until the service is started.
   if (currentState == SERVICE_START_PENDING)
      ss.dwControlsAccepted = 0;
   else
      ss.dwControlsAccepted =
         SERVICE_ACCEPT_STOP|SERVICE_ACCEPT_SHUTDOWN;

   if( currentState == SERVICE_STOP_PENDING )
      checkPoint++;

   ss.dwServiceType             = SERVICE_WIN32_OWN_PROCESS;
   ss.dwServiceSpecificExitCode = 0;
   ss.dwCurrentState            = currentState;
   ss.dwWin32ExitCode           = 0;
   ss.dwCheckPoint              = checkPoint;
   ss.dwWaitHint                = waitHint;

   // Send status of the service to the Service Controller.
   if( !SetServiceStatus(ssh, &ss) ) 
      module->Stop();
}

void ServiceLoader::ShowCriticalError(const wchar_t* msg)
{
   HANDLE  hEventSource;

   const wchar_t* moduleName = module->Name();
   DWORD dwErr = GetLastError();

   hEventSource = RegisterEventSource(NULL, moduleName);
   if( hEventSource != NULL )
   {
      LPWSTR strings[2];
      std::wstringstream smsg;

      smsg << moduleName << L" код ошибки " << dwErr;
      std::wstring tstr(smsg.str());

      strings[0] = (LPWSTR)tstr.c_str();
      strings[1] = (LPWSTR)msg;

      ReportEvent(hEventSource, EVENTLOG_ERROR_TYPE, 0, 0, NULL, 2, 0,(LPCWSTR*)strings, NULL);
      DeregisterEventSource(hEventSource);
   } 
}

bool ServiceLoader::Init()
{
   bool ret = true;
   DWORD status = SERVICE_RUNNING;

   if( !module->Init(argc, (const char**)argv, this) )
   {
      ret = false;
      status = SERVICE_STOPPED;
   }

   loader->SetStatus(status);
   return ret;
}

void WINAPI ServiceLoader::ServiceMain(DWORD dwArgc, LPTSTR *lpszArgv)
{
   ssh = RegisterServiceCtrlHandler(loader->module->Name(), (LPHANDLER_FUNCTION)ServiceHandler);

   if( !loader->Init() )
      return;

   return loader->module->Run();
}

void WINAPI ServiceLoader::ServiceHandler(DWORD dwCtrlCode)
{
   DWORD dwState = SERVICE_RUNNING;

   switch(dwCtrlCode)
   {
      case SERVICE_CONTROL_STOP:
         dwState = SERVICE_STOP_PENDING;
         break;

      case SERVICE_CONTROL_SHUTDOWN:
         dwState = SERVICE_STOP_PENDING;
         break;

      case SERVICE_CONTROL_INTERROGATE:
         break;

      default:
         break;
   }

   // Set the status of the service.
   loader->SetStatus(dwState);

   if ((dwCtrlCode == SERVICE_CONTROL_STOP) || (dwCtrlCode == SERVICE_CONTROL_SHUTDOWN))
      CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)Stoping, NULL, 0, NULL);
}

DWORD WINAPI ServiceLoader::Stoping(LPVOID)
{
   loader->SetStatus(SERVICE_STOP_PENDING);
   HANDLE hTryStop = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryStop, NULL, 0, NULL);

   checkPoint = 0;

   do
   {
      if( WaitForSingleObject(hTryStop, waitHint - 50) != WAIT_TIMEOUT )
         break;

      loader->SetStatus(SERVICE_STOP_PENDING);
   } while( checkPoint <= MAX_STOP_TRY );

   if( checkPoint > MAX_STOP_TRY )
   {
      TerminateThread(hTryStop, 0);
      loader->module->Kill();
   }

   loader->SetStatus(SERVICE_STOPPED);
   CloseHandle(hTryStop);
   return 0;
}

DWORD WINAPI ServiceLoader::TryStop(LPVOID)
{
   loader->module->Stop();
   return 0;
}

const char* ServiceLoader::ExecString(const IRunnableModule& module) const
{
   static char buf[500];
   if( sizeof(TCHAR) != sizeof(char) )
   {
      USES_CONVERSION;

      wsprintfA(buf, "net start \"%s\"", W2A_CP(module.Name(), CP_UTF8));
   } else
   {
      wsprintfA(buf, "net start \"%s\"", (char*)module.Name());
   }

   return buf;
}

void ServiceLoader::Stopping(const IRunnableModule& module)
{
   char buf[500];
   if( sizeof(TCHAR) != sizeof(char) )
   {
      USES_CONVERSION;

      wsprintfA(buf, "net stop \"%s\"", W2A_CP(module.Name(), CP_UTF8));
   } else
   {
      wsprintfA(buf, "net stop \"%s\"", (char*)module.Name());
   }

   STARTUPINFOA si = {0};
   PROCESS_INFORMATION pi; 
   si.cb = sizeof(si);
   if( CreateProcessA(NULL, buf, NULL, NULL, FALSE, CREATE_NEW_PROCESS_GROUP, NULL, NULL, &si, &pi) )
   {
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);
   }
}

/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Загрузчик сервера (служба или трей)
 *
 * ert   24/03/2009   creating
 */
#ifndef __GRSERVER_SERVICE_H
#define __GRSERVER_SERVICE_H

#include <progloader.h>

namespace GRServer {

class ServiceLoader : public IProgramLoader
{
 public:
   ServiceLoader(IRunnableModule *module, LPCTSTR args);
   ~ServiceLoader();

   static bool IsInstalled(const IRunnableModule *module);
   static bool Install(const IRunnableModule *module, bool runAfterInstall = true, LPCTSTR args = L"");
   static bool Uninstall(const IRunnableModule *module);

   virtual bool Run();

   virtual void Stop() {}

   virtual void ShowCriticalError(const wchar_t* msg);

   // строка для запуски сервера
   virtual const char* ExecString(const IRunnableModule& module) const;

   // останавливаем сервер
   virtual void Stopping(const IRunnableModule& module);

   static bool HaveRunArg(LPCTSTR str)
   {
      return (wcsstr(str, serviceRunArg) == str);
   }
 protected:
   DWORD argc;
   char **argv;

   bool Init();

 protected:
   void SetStatus(DWORD currentState);

   static DWORD checkPoint;
   static DWORD waitHint;

   static ServiceLoader *loader;
   static SERVICE_STATUS_HANDLE ssh;

   static const TCHAR serviceRunArg[];

   static void WINAPI ServiceHandler(DWORD dwCtrlCode); 
   static void WINAPI ServiceMain(DWORD dwArgc, LPTSTR *lpszArgv); 

   static DWORD WINAPI Stoping(LPVOID);
   static DWORD WINAPI TryStop(LPVOID);
};

}; // namesapce GRServer

#endif

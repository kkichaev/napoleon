/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Загрузчик сервера (служба или трей)
 *
 * ert   24/03/2009   creating
 */
#ifndef __GRSERVER_TRAY_H
#define __GRSERVER_TRAY_H

#include <progloader.h>

namespace GRServer {

class TrayLoader : public IProgramLoader
{
 public:
   TrayLoader(IRunnableModule *module, HINSTANCE hInstance, int idIcon, LPCTSTR args, IProgramNotify *notifier);
   ~TrayLoader();

   static bool IsInstalled(const IRunnableModule *module);
   static bool Install(const IRunnableModule *module, bool runAfterInstall = true, LPCTSTR args = L"");
   static bool Uninstall(const IRunnableModule *module);

   virtual bool Run();

   virtual void Stop() { PostQuitMessage(0); }

   virtual void ShowCriticalError(const wchar_t* msg);

   // строка для запуски сервера
   virtual const char* ExecString(const IRunnableModule& module) const;

   // останавливаем сервер
   virtual void Stopping(const IRunnableModule& module);

   static bool HaveRunArg(LPCTSTR str)
   {
      return (wcsstr(str, trayRunArg) == str);
   }

 protected:
   DWORD argc;
   char **argv;

   LPCTSTR args;
   DWORD threadID;

   IProgramNotify *notifier;

   HINSTANCE hInstance;
   int idIcon;
   HWND hMainWnd;

   bool Startup();
   void Stoping();

   void DeleteTrayIcon();

   static const TCHAR trayRunArg[];
   static TrayLoader *loader;

   static LRESULT CALLBACK WindowProc(HWND hWnd, UINT iMsg, WPARAM wParam, LPARAM lParam);
   static DWORD WINAPI RunModule(LPVOID);

   static DWORD WINAPI TryStop(LPVOID);
};

} // namespace GRServer

#endif

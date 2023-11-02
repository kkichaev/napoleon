/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Класс диспетчера сервера
 *
 * ert   27/03/2009   creating
 */

#include "stdafx.h"
#include <malloc.h>
#include "dispatcher.h"
#include "srvutility.h"
#include "sessobj.h"
#include "parse.h"
#include "objdef.h"
#include "stdobjs.h"

#include "packet.h" // Decompress here

#include "folderset.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <signal.h>

using namespace GRServer;
using namespace std;


ServerRunMode Dispatcher::RunMode() const
{
   return srmTray;
}

bool Dispatcher::Install(ServerRunMode newMode, bool runAfterInstall) const
{
   return false;
}

extern "C" {
   extern BYTE _binary_stdObjects_xml_start;
   extern BYTE _binary_stdObjects_xml_end;
}

bool Dispatcher::LoadInternalObjects()
{
   int size = (&_binary_stdObjects_xml_end - &_binary_stdObjects_xml_start);
   Binary b;
   memcpy(b.Alloc(size), &_binary_stdObjects_xml_start, size);

   return ObjectDef::Load(b);
}

static void TermHandler(int sig, siginfo_t* si, void* )
{
   gServer->AddLog(IErrorLogger::Short, "TermHandler exit");

   ((Dispatcher*)gServer)->Stop();
   gServer->AddLog("Stopped");

   exit(0);
}

void WINAPI Dispatcher::Run()
{
   struct sigaction sa;
   sa.sa_flags = SA_SIGINFO;
   sigemptyset(&sa.sa_mask);
   sa.sa_sigaction = TermHandler;
   sigaction(SIGINT, &sa, NULL);

   while(true)
   {
      RequestHandler *rh = new RequestHandler();
      if( !rh->Accept(this, socket) )
      {
         delete rh;
         if( errno == EINTR )
         {
            AddLog(IErrorLogger::Short, "EINTR in Dispatcher::Run exiting");
            break;
         }

         AddError(false, "Ошибка при подключении клиента %d", errno);
         continue;
      }

      AddLog(IErrorLogger::Full, "Accept client");

      if( !Thread::Starting(rh, this) )
      {
         delete rh;
         AddLog("Can't starting client thread");
      }
   }

   Cleanup();
 }

void Dispatcher::Stop()
{
   ClosePlugins();
   Cleanup();
//   SetEvent(evStop);
//
//   HANDLE hStop = Thread::StopEvent();
//   if( hStop )
//      WaitForSingleObject(hStop, INFINITE);
}


void Dispatcher::LoadPlugins()
{
   std::string folder;
   //if( folder.empty() )
   FullFileName(&folder, dataCtrl.Config().pluginsFolder.c_str());
//
//   try
//   {
//      USES_CONVERSION;
//
//      WIN32_FIND_DATAA data;
//      std::string tfname = (folder + "*.dll");
//      const char* files = tfname.c_str();
//
//      AddLog("Search plugins %s", files);
//
//      HANDLE hFind = FindFirstFileA(files, &data);
//      if( hFind != INVALID_HANDLE_VALUE )
//      {
//         do
//         {
//            tfname = folder + data.cFileName;
//            const char *pluginName = tfname.c_str();
//
//            AddLog( "find plugin %s", pluginName);
//
//            HMODULE mod = LoadLibraryA(pluginName);
//            if( mod )
//            {
//               TGetPlugin GetPlg = (TGetPlugin)GetProcAddress(mod, "GetPlugin");
//               if( GetPlg != NULL )
//               {
//                  IPlugin *plugin;
//                  while( GetPlg(&plugin) )
//                  {
//                     PluginData pd;
//                     pd.plugin = plugin;
//                     pd.inited = plugin->Init(this);
//                     if( !pd.inited )
//                        AddError(false, "Ошибка при инициализации плагина %s в файле %s", W2A(plugin->Name()), data.cFileName);
//
//                     plugins.push_back(pd);
//                  }
//               } else
//               {
//                  FreeLibrary(mod);
//               }
//            }
//         } while( FindNextFileA(hFind, &data) );
//
//         FindClose(hFind);
//      }
//   } catch(...)
//   {
//      AddError(false, "Exception while loading plugins");
//   }
}

void Dispatcher::ProgramNotify(IProgramLoader *loader, HWND hWnd)
{
}


void Dispatcher::PluginConfigure(HWND owner)
{

}

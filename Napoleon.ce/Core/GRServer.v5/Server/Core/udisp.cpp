/*
 * Copyright (C), 2009, ����� �������
 *
 * ����� ���������� �������
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
   extern BYTE _binary___Server_Core_objects_stdObjects_xml_start;
   extern BYTE _binary___Server_Core_objects_stdObjects_xml_end;
}

bool Dispatcher::LoadInternalObjects()
{
   int size = (&_binary___Server_Core_objects_stdObjects_xml_end - &_binary___Server_Core_objects_stdObjects_xml_start);
   Binary b;
   memcpy(b.Alloc(size), &_binary___Server_Core_objects_stdObjects_xml_start, size);

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
   int rc = sigaction(SIGINT, &sa, NULL);
   rc = sigaction(SIGTERM, &sa, NULL);
   rc = sigaction(SIGQUIT, &sa, NULL);
   rc = sigaction(SIGHUP, &sa, NULL);

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

         AddError(false, "Can't accept client %d", errno);
         continue;
      }

      AddLog(IErrorLogger::Full, "Accept client");

      std::string error;
      if( !Thread::Starting(rh, this, MAX_THREADS, false, &error) )
      {
         delete rh;
         AddLog("Can't start thread %s", error.c_str());
      }
   }

   Cleanup();
 }

void Dispatcher::Stop()
{
   Cleanup();
}

#include <dlfcn.h>

typedef bool (*TGetPlugin)(IPlugin** plugin);
void Dispatcher::LoadPlugins()
{
   std::string folder(dataCtrl.Config().ProgFolder());

   std::string pname(folder + "python.so");
   void *pyhandle = dlopen(pname.c_str(), RTLD_LAZY | RTLD_GLOBAL); 
   if(pyhandle != NULL)
   {
      TGetPlugin getP =  (TGetPlugin)dlsym(pyhandle, "GetPlugin");
      if(getP != NULL)
      {
         IPlugin *plugin;
         bool havePlugins = false;
         while( getP(&plugin) )
         {
            havePlugins = true;
            AddLog(IErrorLogger::Full, "Init plugin plugin %s", pname.c_str());

            PluginData pd;
            pd.plugin = plugin;
            pd.inited = plugin->Init(this);

            AddLog(IErrorLogger::Full, "...inited");
            if( !pd.inited )
               AddError(false, "Error initing plugin %s", pname.c_str());

            plugins.push_back(pd);
         }
      }
   } else
   {
      const char* error = dlerror();
      AddLog(IErrorLogger::None, "Error while loading plugin %s", error);
   }
}

void Dispatcher::ProgramNotify(IProgramLoader *loader, HWND hWnd)
{
}


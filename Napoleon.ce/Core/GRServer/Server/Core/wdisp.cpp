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

#ifdef UNIX
#else
#include "service.h" // IsInstalled
#include "tray.h"    // IsInstalled
#endif

#include "packet.h" // Decompress here

#include "folderset.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef UNIX
#else
#include <resource.h>
#endif

using namespace GRServer;
using namespace std;

ServerRunMode Dispatcher::RunMode() const
{
   if( ServiceLoader::IsInstalled(this) )
      return srmService;
   else if( TrayLoader::IsInstalled(this) )
      return srmTray;
   return srmUndef;
}

bool Dispatcher::Install(ServerRunMode newMode, bool runAfterInstall) const
{
   std::string configFile(dataCtrl.Config().configFile);
   if( configFile.empty() ) return false;

   const wchar_t* args;
   char buf[500];
   std::string fileName;
   FullFileName(&fileName, configFile.c_str());
   wsprintfA((char*)buf, "--config-file \"%s\"", fileName.c_str());

   USES_CONVERSION;
   args = A2W(buf);

   bool ret = true;
   switch(newMode)
   {
   case srmUndef:
      if( ServiceLoader::IsInstalled(this) )
         ServiceLoader::Uninstall(this);
      if( TrayLoader::IsInstalled(this) )
         TrayLoader::Uninstall(this);
      break;
   case srmTray:
      if( ServiceLoader::IsInstalled(this) )
         ServiceLoader::Uninstall(this);
      ret = TrayLoader::Install(this, runAfterInstall, args);
      break;
   case srmService:
      if( TrayLoader::IsInstalled(this) )
         TrayLoader::Uninstall(this);
      ret = ServiceLoader::Install(this, runAfterInstall, args);
      break;
   }

   return ret;
}

bool Dispatcher::LoadInternalObjects()
{
   bool ret = false;

   HRSRC stdObj = FindResource(NULL, MAKEINTRESOURCE(IDR_STD_OBJECTS), MAKEINTRESOURCE(BIN_DATA));
   if( stdObj != NULL )
   {
      DWORD size = SizeofResource(NULL, stdObj);
      HGLOBAL glob = LoadResource(NULL, stdObj);

      void* v = LockResource(glob);

      Binary b, dest;
      memcpy(b.Alloc(size), v, size);

      FreeResource(glob);
      if( Decompress(&dest, b, size*10) )
         ret = ObjectDef::Load(dest);
   }
   if( !ret )
      AddError(true, "Ошибка загрузки стандартных объектов");

   return ret;
}

void WINAPI Dispatcher::Run()
{
   HANDLE waitHandles[2];
   waitHandles[0] = evStop;
   waitHandles[1] = evAccept;

   stopMutex = CreateMutexA(NULL, TRUE, SERVER_MUTEX);

   try
   {

      bool done = false;
      while( !done )
      {
         DWORD res = WaitForMultipleObjects(2, waitHandles, FALSE, INFINITE);
         switch( res )
         {
         case WAIT_OBJECT_0:
            // stop event;
            done = true;
            break;

         case WAIT_OBJECT_0 + 1:
         {
            // accept event
            WSAResetEvent(evAccept);
            RequestHandler *rh = new RequestHandler();

            try
            {
               if( !rh->Accept(this, socket) )
               {
                  delete rh;
                  AddError(false, "Ошибка при подключении клиента %d", WSAGetLastError());
                  continue;
               }

					//AddLog("Threads %d", Thread::ThreadCount());

					if (!Thread::Starting(rh, this, MAX_THREADS, ((ServerConfig&)GetConfig()).makeDumpOnException))
					{
						delete rh;
						AddLog("Can't starting client thread");
					}

            }
            catch(...)
            {
               AddError(false, "Exception starting client thread");
               try
               {
                  delete rh;
               } catch(...)
               {
               }
            }
				
				//Sleep(5000);
				//char *err = 0;
				//*err = 1;

				break;
         }

         default:
            AddError(false, "WaitForMultipleObjects %d, WSAGetLastError %d, GetLastError %d", res, WSAGetLastError(), GetLastError());
            done = true;
            break;
         }
      }
   }
   catch(...)
   {
      AddError(false, "Exception in socket" );
   }

   Cleanup();
 }

void Dispatcher::Stop()
{
   SetEvent(evStop);

   HANDLE hStop = Thread::StopEvent();
   if( hStop )
      WaitForSingleObject(hStop, INFINITE);
}


typedef DECL_SPEC bool (*TGetPlugin)(IPlugin** plugin);

void Dispatcher::LoadPlugins()
{
   std::string folder;
   //if( folder.empty() )
   FullFileName(&folder, dataCtrl.Config().pluginsFolder.c_str());

   try
   {
      USES_CONVERSION;

      WIN32_FIND_DATAA data;
      std::string tfname = (folder + "*.dll");
      const char* files = tfname.c_str();

      AddLog(IErrorLogger::Short, "Search plugins %s", files);

      HANDLE hFind = FindFirstFileA(files, &data);
      if( hFind != INVALID_HANDLE_VALUE )
      {
         do
         {
            tfname = folder + data.cFileName;
            const char *pluginName = tfname.c_str();

            AddLog(IErrorLogger::Short, "find plugin %s", pluginName);

            HMODULE mod = LoadLibraryA(pluginName);
            if( mod )
            {
               TGetPlugin GetPlg = (TGetPlugin)GetProcAddress(mod, "GetPlugin");
               if( GetPlg != NULL )
               {
                  AddLog(IErrorLogger::Full, "GetPlugin %s %X", pluginName, (size_t)GetPlg);
                  IPlugin *plugin;
                  
                  bool havePlugins = false;
                  while( GetPlg(&plugin) )
                  {
                     havePlugins = true;
                     AddLog(IErrorLogger::Full, "Init plugin plugin %s %X", pluginName, (size_t)plugin);

                     PluginData pd;
                     pd.plugin = plugin;
                     pd.inited = plugin->Init(this);

                     AddLog(IErrorLogger::Full, "...inited");
                     if( !pd.inited )
                        AddError(false, "Ошибка при инициализации плагина %s в файле %s", W2A(plugin->Name()), data.cFileName);

                     plugins.push_back(pd);
                  }
                  if( !havePlugins )
                     AddLog(IErrorLogger::Short, "No plugins in %s", pluginName);
               } else
               {
                  FreeLibrary(mod);
                  AddLog(IErrorLogger::Full, "Can't find GetPlugin in plugin %s", pluginName);
               }
            } else
            {
               AddLog(IErrorLogger::Full, "Can't load plugin %s error %d", pluginName, GetLastError());
            }
         } while( FindNextFileA(hFind, &data) );

         FindClose(hFind);
      }
   } catch(...)
   {
      AddError(false, "Exception while loading plugins");
   }
}

void Dispatcher::ProgramNotify(IProgramLoader *loader, HWND hWnd)
{
   POINT pt;
   GetCursorPos(&pt);
   HMENU hm = LoadMenu(NULL, MAKEINTRESOURCE(IDR_MAINMENU));

   SetForegroundWindow(hWnd);
   int cmd = TrackPopupMenu(GetSubMenu(hm,0), TPM_LEFTBUTTON | TPM_RETURNCMD, pt.x, pt.y, 0, hWnd, NULL);
   PostMessage(hWnd, WM_NULL, 0, 0);

   DestroyMenu(hm);

   switch(cmd)
   {
   case ID_EXIT:
      loader->Stop();
      break;
   case IDC_SETTINGS:
      dataCtrl.Config().Edit(NULL);
      break;
   }
}


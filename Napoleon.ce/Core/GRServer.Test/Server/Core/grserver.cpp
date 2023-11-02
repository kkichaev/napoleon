/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Main
 *
 * ert   26/09/2009   creating
 */

#include "stdafx.h"
#ifdef _DEBUG
//#include "vld.h"
#endif
#include <dispatcher.h>
#include <service.h>
#include <tray.h>
#include "resource.h"

#include "sessobj.h"

using namespace GRServer;

IServer *GRServer::gServer;

int APIENTRY wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow)
{
   Dispatcher dispatcher;
   gServer = &dispatcher;

   if( *lpCmdLine == L'\0' )
      lpCmdLine = L"--run-tray --config-file GRServer.ini";

   if( ServiceLoader::HaveRunArg(lpCmdLine) )
   {
      dispatcher.SetRunMode(srmService);
      ServiceLoader sl(&dispatcher, lpCmdLine);
      sl.Run();

      return 0;
   }

   if( TrayLoader::HaveRunArg(lpCmdLine) )
   {
      dispatcher.SetRunMode(srmTray);
      TrayLoader tl(&dispatcher, hInstance, IDI_MAINICON, lpCmdLine, &dispatcher);
      tl.Run();

      return 0;
   }

   ServerConfig config;
   config.Edit(hInstance);

   return 0;
}


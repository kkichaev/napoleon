/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Main
 *
 * ert   26/09/2009   creating
 */

#include "stdafx.h"
#include <dispatcher.h>

using namespace GRServer;

IServer *GRServer::gServer;

int main(int argc, const char** argv)
{
   Dispatcher dispatcher;
   gServer = &dispatcher;

   if( !dispatcher.Init(argc, argv, NULL) )
      return 1;


   dispatcher.Run();
   return 0;
}


/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Прием заявки
 *
 *  ert   01/03/2010   creating
 */ 
#include "stdafx.h"
#include <atldef.h>
#include <dbf.h>
#include <StringHolder.h>
#include <exchange.h>
#include <sync.h>
#include "Config.h"
#include <time.h>
#include <string>
#include <direct.h>

#include "Server.h"

struct OrdRCV : public ISyncCreator
{
   virtual bool CompressedData() const { return true; }
   virtual SyncFormat* Format(const char *userID) const { user = userID; return new SyncOrder(userID); }

   mutable std::string user;
};

static void StartProcess(char *cmd)
{
   PROCESS_INFORMATION pi;
   STARTUPINFO si;
   
   memset(&si, 0, sizeof(si));
   si.cb = sizeof(si);
   si.lpDesktop = "WinSta0\\Default";
   
   CreateProcess(NULL, cmd, NULL, NULL, TRUE, CREATE_NEW_CONSOLE, NULL, NULL, &si, &pi);
}

bool ReceiveOrder(SOCKET sock, const char *param, const char *exchangeFolder)
{
   OrdRCV orcv;
   bool res = false;
   if( ReceiveData(sock, param, exchangeFolder, orcv) )
   {
      char orderName[_MAX_DIR];
      sprintf(orderName, "%s\\order.bat", ExchangeFolder());
      if( GetFileAttributes(orderName) != INVALID_FILE_ATTRIBUTES )
      {
         char cmdbuf[_MAX_DIR*2];
         sprintf(cmdbuf, "\"%s\" \"%s\"", orderName, orcv.user.c_str());
         StartProcess(cmdbuf);
      }

      res = true;
   }

   return res;
}

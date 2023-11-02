/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Обработка нестандартных команд
 *
 *  ert   28/03/2008   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include <exchange.h>
#include <sync.h>
#include "Server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>
#include "Sync.h"
#include <VConvert.h>

struct SORCreator : public ISyncCreator
{
   virtual SyncFormat* Format(const char *userID) const { return new SyncOrgRemnants(userID); }
};

static bool ReceiveRemnants(SOCKET sock, const char *param, const char *exchangeFolder)
{
   return ReceiveData(sock, param, exchangeFolder, SORCreator());
}

bool HandleCustomCommand(const char *command, SOCKET socket, const char *exchangeFolder)
{
   if( !strncmp(command, SND_ORG_RMNTS, sizeof(SND_ORG_RMNTS)-1) )
      return ReceiveRemnants(socket, command + sizeof(SND_ORG_RMNTS)-1, exchangeFolder);
   return false;
}

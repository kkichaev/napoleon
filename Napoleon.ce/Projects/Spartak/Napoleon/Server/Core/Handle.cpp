/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Обработка нестандартных команд
 *
 *  ert   28/03/2008   creating
 */ 
#include "stdafx.h"
#include "Server.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>
#include <set>

#include <fcntl.h>

#include <algorithm>

#include <Exchange.h>
#include <Sync.h>
#include "Server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace std;

struct OCreator : public ISyncCreator
{
   virtual SyncFormat* Format(const char *userID) const { return new SyncOrgRemnants(userID); }
};

bool HandleCustomCommand(const char *command, SOCKET socket, const char *exchangeFolder)
{
   if( !strncmp(command, SND_ORG_RMNTS, sizeof(SND_ORG_RMNTS)-1) )
      return ReceiveData(socket, command + sizeof(SND_ORG_RMNTS)-1, exchangeFolder, OCreator());

   return false;
}

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

#include <Exchange.h>
#include <Sync.h>
#include "Server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>
#include "Sync.h"
#include <VConvert.h>

SyncOrgRest::SyncOrgRest(const char *user) : SyncFormat(user)
{
}

const char* SyncOrgRest::FileName() const
{
   static char buf[50];
   wsprintf(buf, "VT%s.DBF", userID);
   return buf;
}

DBRec* SyncOrgRest::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",  'C',50,0},
      {"FLAG", 'N',1,0},
      {"DATE",'D', 8, 0},
      {"ID_I",'C',50,0},  
   };

   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
} 

bool SyncOrgRest::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   USES_CONVERSION;

   const OrgRestSend& data = (const OrgRestSend&)_data;
   SYSTEMTIME st;
   FileTimeToSystemTime(&data.date, &st);


   char buf[20];
   wsprintf(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
   db->Fill("DATE", buf);
   db->Fill("ID", W2A_CP(data.id, CP_OEMCP));

   int count = data.items.size();
   char *flag = "1";
   if( count )
   {
      int itemCount = 1;
      for( int i=0; i<count; i++ )
      {
         const RestItem &item = data.items[i];
         const char *id = W2A_CP(item.id, CP_OEMCP);

         if( *id == '\0' )
         {
            flag = "0";
            continue;
         }

         db->Fill("ID_I", id);
         db->Fill("FLAG", flag);
         db->Append();
      }
   }

   return true;
}
 
struct SORCreator : public ISyncCreator
{
   virtual SyncFormat* Format(const char *userID) const { return new SyncOrgRest(userID); }
};

bool HandleCustomCommand(const char *command, SOCKET socket, const char *exchangeFolder)
{
   if( !strncmp(command, SND_ORG_REST, sizeof(SND_ORG_REST)-1) )
      return ReceiveData(socket, command + sizeof(SND_ORG_REST)-1, exchangeFolder, SORCreator());
   return false;
}

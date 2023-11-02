/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Выгрузка остатков организации
 *
 *  ert   03/04/2008   creating
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
#include <Sync.h>

SyncOrgRemnants::SyncOrgRemnants(const char *user) : SyncFormat(user)
{
}

bool SyncOrgRemnants::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   return false;
}

bool SyncOrgRemnants::SetToDB(DataForm *db, const IReflectableData &data) const
{
   USES_CONVERSION;

   char buf[200];
   const OrgRemnants &remnants = (const OrgRemnants &)data;
   SYSTEMTIME st;

   FileTimeToSystemTime(&remnants.date, &st);
   wsprintf(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   db->Fill("DATE", buf);

   db->Fill("ID", W2A_CP(remnants.id, CP_OEMCP));

   if( remnants.items.size() > 0 )
   {
      vector_t<OrgRemnantsItem>::const_iterator i = remnants.items.begin();
      for( ; i != remnants.items.end(); i++ )
      {
         db->Fill("ID_I", W2A_CP(i->id, CP_OEMCP));
         db->Fill("QTY", ((double)i->qty)/QTY_SCALE);
         db->Fill("FLAGS", (i == remnants.items.begin()) ? "1" : "");
         db->Append();
      }
   }

   return true;
}

const char* SyncOrgRemnants::FileName() const
{
   static char buf[50];
   wsprintf(buf, "OS%s.DBF", userID);
   return buf;
}

DBRec* SyncOrgRemnants::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",'C',MAX_ORG_ID,0},
      {"DATE",'D',8,0},
      {"FLAGS", 'C', 50, 0},
      {"ID_I",'C',MAX_ITEM_ID,0},
      {"QTY",'N',10,2},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}

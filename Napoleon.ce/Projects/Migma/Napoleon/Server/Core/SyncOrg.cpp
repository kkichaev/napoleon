/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Синхронизация организаций
 *
 *  ert   06/02/2008  creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include "Server.h"

#include <exchange.h>
#include <sync.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

//
//------------------------------------ Sync Org -------------------------------------
//
SyncOrg::SyncOrg(const char *userID) : SyncFormat(userID)
{
}

const char* SyncOrg::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "O%s.DBF", userID);
   return buf; 
}

bool SyncOrg::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   ((Org*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Org*)data)->id = sh->Add(Trunc(db["ID"]), CP_OEMCP);

   const char *p = db["COSTTYPE"];
   if( p )   ((Org*)data)->dcost = sh->Add(Trunc(p), CP_OEMCP);
   else ((Org*)data)->dcost = L"";

#ifdef ORG_COST_TYPE
   p = db["COSTYPE"];
   ((Org*)data)->costype = (p==NULL) ? 0 : atoi(p);
#endif

   return true;
}

bool SyncOrg::SetToDB(DataForm *db, const IReflectableData &data) const
{
   USES_CONVERSION;

   db->Fill("NAME", W2A_CP(((const Org&)data).name, CP_OEMCP));
   db->Fill("ID", W2A_CP(((const Org&)data).id, CP_OEMCP));
   return true;
}

DBRec* SyncOrg::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",'C',MAX_ORG_ID,0},
      {"NAME",'C',MAX_ORG_NAME,0},
#ifdef ORG_COST_TYPE
      {"COSTYPE",'N',2,0},
#endif
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Синхронизация торговых точек
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

#include <Exchange.h>
#include <Sync.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#if defined(ORG_INFO) || defined(ORG_UNITS) || defined(ORG_UNITS_STR)

using namespace std;

//
//------------------------------------ Sync Org -------------------------------------
//
bool SyncOrg::LoadUnits(const char *exchangeFolder, const char *userID)
{
   char buf[40];
   wsprintf(buf, "UN%s.DBF", userID);
   std::string fileName(exchangeFolder);
   OemToChar(buf, buf);
   fileName += buf;

   DataForm base;
   bool opened = base.Open(fileName.c_str());
   if( !opened )
   {
      fileName = exchangeFolder;
      fileName += "UNITS.DBF";

      opened = base.Open(fileName.c_str());
   }

   if( !opened )
      return false;

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      string id = Trunc(base["IDO"]);
      map<string, vector<OrgUnit> >::iterator fnd = units.find(id);
      if( fnd == units.end() )
      {
         vector<OrgUnit> ary;
         fnd = units.insert(map<string, vector<OrgUnit> >::value_type(id, ary)).first;
      }

      OrgUnit ou;
      ou.id = holder.Add(Trunc(base["NAME"]), CP_OEMCP);
      ou.name = L"";

      fnd->second.push_back(ou);
   }

   return true;
}

void SyncOrg::SetUnits(Org *org, const char *id, StringHolder *holder) const
{ 
   map<string, vector<OrgUnit> >::const_iterator fnd = units.find(id);
   if( fnd == units.end() )
      return;

   vector<OrgUnit>::const_iterator i = fnd->second.begin();
   for( ; i != fnd->second.end(); i++ )
   {
      OrgUnit ou;

      ou.id = holder->Add(i->id);
      ou.name = L"";

      org->units.push_back(ou);
   }
}

#endif //ORG_INFO

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

#include <Exchange.h>
#include <Sync.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef ORG_INFO
void SyncOrg::LoadContacts(const char *exchangeFolder, const char*userID)
{
   char buf[50];
   wsprintf(buf, "C%s.DBF", userID);
   std::string fileName(exchangeFolder);
   OemToChar(buf, buf);
   fileName += buf;

   DataForm base;
   if( !base.Open(fileName.c_str()) )
   {
      fileName = exchangeFolder;
      fileName += "CONTACTS.DBF";

      if( !base.Open(fileName.c_str()) )
         return;
   }

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      std::string code = Trunc(base["ID"]);

      Contact ct;
      ct.name = holder.Add(Trunc(base["FIO"]), CP_OEMCP);
      ct.phone = holder.Add(Trunc(base["PHONE"]), CP_OEMCP);

      contacts[code].push_back(ct);
   }
}

#endif

void SyncOrg::LoadDogovors(const char *exchangeFolder, const char *userID)
{
   char buf[50];
   wsprintf(buf, "D%s.DBF", userID);
   std::string fileName(exchangeFolder);
   OemToChar(buf, buf);
   fileName += buf;

   DataForm base;
   if( !base.Open(fileName.c_str()) )
      return;

   for( long rc=0; base.ReadRec(rc); rc++ )
   {
      std::string code = Trunc(base["ID"]);

      Dogovor dog;

      dog.number = holder.Add(Trunc(base["NUMBER"]), CP_OEMCP);
      dog.name = holder.Add(Trunc(base["NAME"]), CP_OEMCP);
      const char *p = Trunc(base["COSTTYPE"]);
      dog.costType = (*p == '\0') ? L"" : holder.Add(p, CP_OEMCP);

      DateToFileTime(&dog.from, base["FROM"]);
      DateToFileTime(&dog.till, base["TILL"]);

      dog.firm = holder.Add(Trunc(base["FIRM"]), CP_OEMCP);

      dogovors[code].push_back(dog);
   }
}

void SyncOrg::LoadMatrix(const char *exchangeFolder)
{
   char buf[50];
   wsprintf(buf, "MATRIX.DBF", userID);
   std::string fileName(exchangeFolder);
   fileName += buf;

   DataForm base;
   if( !base.Open(fileName.c_str()) )
      return;

   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      const char *p = base["IDO"];
      if( p == NULL ) continue;

      std::string code = Trunc(p);
      if( code.empty() ) continue;

      MatrixItem mi;
      mi.id = holder.Add(Trunc(base["ID"]), CP_OEMCP);

      matrix[code].push_back(mi);
   }
}

//
//------------------------------------ Sync Org -------------------------------------
//
SyncOrg::SyncOrg(const char *userID) : SyncFormat(userID)
{
#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   LoadUnits(ExchangeFolder(), userID);
#endif

#ifdef ORG_INFO
   LoadContacts(ExchangeFolder(), userID);
#endif

   LoadDogovors(ExchangeFolder(), userID);
   LoadMatrix(ExchangeFolder());
}

const char* SyncOrg::FileName() const
{ 
   static char buf[50];
   wsprintf(buf, "O%s.DBF", userID);
   return buf; 
}

bool SyncOrg::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   std::string code = Trunc(db["ID"]);
   ((Org*)data)->name = sh->Add(Trunc(db["NAME"]), CP_OEMCP);
   ((Org*)data)->id = sh->Add(code.c_str(), CP_OEMCP);

#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   SetUnits((Org*)data, code.c_str(), sh);
#endif

   const char *p;

#ifdef ORG_INFO
   p = db["ADDRESS"];
   ((Org*)data)->address = (p==NULL) ? L"" : sh->Add(Trunc(db["ADDRESS"]), CP_OEMCP);

   std::map<std::string, std::vector<Contact> >::const_iterator fnd = contacts.find(code);
   if( fnd != contacts.end() )
   {
      const std::vector<Contact>& cvect = fnd->second;
      std::vector<Contact>::const_iterator i = cvect.begin();
      for( ;i != cvect.end(); i++ )
         ((Org*)data)->contacts.push_back(*i);
   }
#endif

   p = db["COLOR"];
   if( p != NULL )
      ((Org*)data)->color = strtol(p, (char**)&p, 16);
   else
      ((Org*)data)->color = 0;


#ifdef STOP_LIST
   ((Org*)data)->flags = 0;
   p = db["STOP"];
   if( p != NULL && atoi(p) == 1 )
      ((Org*)data)->flags |= ofStopList;
#endif

   std::map<std::string, vector_t<Dogovor> >::const_iterator dog = dogovors.find(code);
   if( dog != dogovors.end() )
      ((Org*)data)->dogovors = dog->second;

   std::map<std::string, vector_t<MatrixItem> >::const_iterator mi = matrix.find(code);
   if( mi != matrix.end() )
      ((Org*)data)->matrix = mi->second;

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

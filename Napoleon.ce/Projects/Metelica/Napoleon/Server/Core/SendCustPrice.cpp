/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Отправка прайса
 *
 *  ert   29/03/2008   creating
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

#include "Server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <set>
#include <VConvert.h>

SyncRest::SyncRest(const char *userName) : SyncFormat(userName)
{
}

const char *SyncRest::FileName() const
{
   static char buf[50];

   wsprintf(buf, "RST%s.DBF", userID);
   return buf;
}

struct RestData
{
   std::string name;
   std::string id;

   bool operator < (const RestData& item) const { return (name.compare(item.name)<0); }
};

struct PriceData
{
   std::string name;
   std::string id;

   bool operator < (const PriceData& item) const { return (id.compare(item.id)<0); }
};

typedef std::set<PriceData> PriceSet;
typedef std::set<RestData> RestSet;
void LoadPrice(PriceSet *ps, const char *user)
{
   SyncPrice priceFmt(user);
   DataForm base;
   std::string fileName(ExchangeFolder());
   fileName += priceFmt.FileName();

   if( !base.Open(fileName.c_str()) )
   {
      fileName = ExchangeFolder();
      fileName += priceFmt.AltFileName();

      if( base.Open(fileName.c_str()) == false )
         return ;
   }

   const DataReflector &reflector = GetTypeReflector(priceFmt.TypeName());
   for( long rc=0; base.ReadRec(rc); rc++ )
   {
      PriceData data;
      data.id = Trunc(base["ID"]);
      data.name = Trunc(base["NAME"]);

      ps->insert(data);
   }
}

bool SyncRest::SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const
{
   PriceSet ps;
   RestSend &rest = *(RestSend*)data;

   RestSet rs;

   LoadPrice(&ps, userID);

   long curRec = db.GetRecNo();
   rest.items.clear();
   do
   {
      PriceData pd;
      pd.id = Trunc(db["ID"]);

      PriceSet::const_iterator fnd = ps.find(pd);
      if( fnd == ps.end() ) continue;

      RestData rd;
      rd.id = pd.id;
      rd.name = fnd->name;

      rs.insert(rd);
   } while( db.ReadRec(++curRec) );
   db.ReadRec(curRec-1);

   RestSet::const_iterator i = rs.begin();
   for( ; i!=rs.end(); i++ )
   {
      RestItem item;
      item.id = sh->Add(i->id.c_str(), CP_OEMCP);
      rest.items.push_back(item);
   }


   return true;
}

DBRec* SyncRest::BaseHeader(int *count) const
{
   static DBRec dbrec[] = {
      {"ID",'C',20,0},
   };
   *count = sizeof(dbrec)/sizeof(dbrec[0]);
   return dbrec;
}

struct SendRest : public ISendData
{
   SendRest(const char *userID) : format(userID) {}

   virtual bool FailOnError() const { return false; }

   virtual const wchar_t *ErrorMessage() const { return L": отсутствуют остатки"; }
   virtual const wchar_t *CMD() const { return SND_REST; }
   virtual const SyncFormat& Format() const { return format; }

   virtual bool CheckKey() const { return false; }

   SyncRest format;
};

bool SendCustomPrice(SOCKET sock, WORD dbVer, const char *userName)
{
   if( SendData(sock, dbVer, SendRest(userName)) )
      WaitResponse(sock);

   return true;
}

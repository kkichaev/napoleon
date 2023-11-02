/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Add on - дополнения для разных клиентов
 *
 * ert   16/06/2010   creating
 */ 
#include "stdafx.h"
#include <vector>
#include <map>
#include "server.h"
#include "servobj.h"
#include "objdef.h"
#include "parse.h"
#include "datasource.h"
#include "session.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class GetFolderID : public IFunction, public ISession::IHandler
{
public:
   virtual void SessionClosed(ISession* s)
   {
      SessionData::iterator fnd = data.find(s);
      if( fnd != data.end() )
         data.erase(fnd);
   }

   virtual const wchar_t* Name() const { return L"GetFolderID"; }
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

   typedef std::map<std::wstring, std::wstring> PriceData;
   typedef std::map<ISession*, PriceData> SessionData;
   SessionData data;

   SessionData::iterator LoadData(Session* session);
};

GetFolderID getFolderID;

GetFolderID::SessionData::iterator GetFolderID::LoadData(Session* session)
{
   ISessionObject *iso = session->GetObject(L"FID", NULL);
   ServObject* so = iso->Self();
   if( so->size() == 0 )
      iso->Reading();

   int pidi,fidi;
   pidi = so->format->FindMember(L"pid");
   fidi = so->format->FindMember(L"fid");

   if( fidi < 0 || pidi < 0 )
      return data.end();

   PriceData prcData;
   ServObject::const_iterator i = so->begin();
   for( ; i != so->end(); i++ )
   {
      const Object& o = *(*i);
      prcData[(const std::wstring&)*o.at(pidi).str] = (const std::wstring&)*o.at(fidi).str;
   }
   data[session] = prcData;
   session->AddHandler(this);
   return data.find(session);
}

bool GetFolderID::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   std::wstring val;

   SessionData::iterator fnd = data.find(session);
   if( fnd == data.end() )
      fnd = LoadData(session);
   if( fnd != data.end() )
   {
      const PriceData& pd = fnd->second;
      if( params.size() == 1 )
      {
         const Token& p = params[0];
         if( p.type == Token::ttString )
         {
            PriceData::const_iterator pfnd = pd.find(*p.value.str);
            if( pfnd != pd.end() )
               val = pfnd->second;
         }
      }
   }
   *result = val;
   return true;
}

bool GRServer::AddOnInit()
{
   AddFunction(&getFolderID);
   return true;
}


/*
 * Copyright (C), 2009-2011, Денис Мосягин
 *
 * Add on - дополнения для разных клиентов
 *
 * ert   08/06/2011   creating
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

#include "creators.h"
#include "objects.h"
#include "dbf.h"
#include "StdConsts.h"

#include <srvutility.h>

using namespace GRServer;
using namespace std;

//
// три параметра - имя файла, бинарное поле, тэг
//
class PhotoSave : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"PhotoSave"; }
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
} photoSave;

class OrgName : public IFunction, public Session::IHandler
{
public:
   virtual ~OrgName()
   {
      SessionMap::iterator i = orgMaps.begin();
      for( ; i != orgMaps.end(); i++ )
         delete i->second;
   }

   virtual void SessionClosed(ISession* s)
   {
      SessionMap::iterator i = orgMaps.find((Session*)s);
      if( i != orgMaps.end())
      {
         delete i->second;
         orgMaps.erase(i);
      }
   }

   virtual const wchar_t* Name() const { return L"OrgName"; }
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

protected:
   typedef std::map<std::wstring, std::wstring> OrgMap;
   typedef std::map<Session*, OrgMap*> SessionMap;
   SessionMap orgMaps;

   const OrgMap* LoadOrgs(Session* s);
} orgName;

static void StripQuotas(std::wstring* str)
{
   std::wstring::iterator i = str->begin();
   for( ; i != str->end(); )
   {
      wchar_t sym = (*i);
      if( sym == L'\'' || sym == L'"' ) 
         i = str->erase(i);
      else
         i++;
   }
}

bool PhotoSave::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   bool res = false;
   std::wstring wFileName, tag;
   MemoryBinary *src = NULL;

   if( params.size() == 3 )
   {
      int ctr = 0;
      Token* p = (Token*)&params[ctr];
      if( p->type == Token::ttString )
      {
         wFileName = *p->value.str;
         StripQuotas(&wFileName);
         ctr++;
      }
      p = (Token*)&params[ctr];
      if( p->type == Token::ttBinary )
      {
         src = p->value.binary;
         ctr++;
      }

      p = (Token*)&params[ctr];
      if( p->type == Token::ttString )
      {
         tag = *p->value.str;
         ctr++;
      }

      if( ctr == 3 && src != NULL )
      {
         USES_CONVERSION;
         std::string folder, name;
         int off = wFileName.find_last_of(L'\\');
         if( off >= 0 )
         {
            folder = W2A(wFileName.substr(0, off).c_str());
            name = W2A(wFileName.substr(off+1).c_str());
         } else
            name = W2A(wFileName.c_str());

         res = true;
         const char *f = folder.c_str();
         if( f[1] != ':' && f[0] != '\\' )
         {
            folder.insert(0, session->Config().ExchangeFolder());

            CreateDirectoryA(folder.c_str(), NULL);

            int ctr = 1;
            std::string fn;

            while( ctr < 1000 )
            {
               char buf[50];
               wsprintfA(buf, "%s%04d.jpg", name.c_str(), ctr++);
               fn = folder;
               fn += buf;
               if( !IsFileExists(fn.c_str()) )
                  break;
            }

            FILE *f = fopen(fn.c_str(), "wb");
            if( f )
            {
               USES_CONVERSION;

               fwrite(src->Bytes(), src->Size(), sizeof(BYTE), f);
               fclose(f);
               JPEGAddComment(fn.c_str(), W2A(tag.c_str()));
            }
         }
      }
   }

   return res;
}

bool OrgName::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   bool res = false;

   if( params.size() == 1 && params.front().type == Token::ttString )
   {
      const OrgMap* om = LoadOrgs(session);
      if( om != NULL )
      {
         const std::wstring& orgID = *params.front().value.str;
         OrgMap::const_iterator fnd = om->find(orgID);
         if( fnd != om->end() )
         {
            (*result) = fnd->second;
         } else
         {
            std::wstring name(L"Код ");
            name += orgID;
            name += L"_";
            (*result) = name;
         }
         res = true;
      }
   }

   return res;
}

const OrgName::OrgMap* OrgName::LoadOrgs(Session* s)
{
   OrgMap* res = NULL;

   SessionMap::const_iterator fnd = orgMaps.find(s);
   if( fnd != orgMaps.end() )
   {
      res = fnd->second;
   } else
   {
      ISessionObject* iso = s->LoadObject(L"Org", NULL);
      if( iso != NULL )
      {
         int iid, iname;
         SessionObject *so = (SessionObject *)iso->Self();
         iid = so->format->FindMember(L"id");
         iname = so->format->FindMember(L"name");

         res = new OrgMap();

         SessionObject::const_iterator i = so->begin();
         for( ; i != so->end(); i++ )
         {
            const Object& o = *(*i);
            res->insert(OrgMap::value_type((const std::wstring&)*o.at(iid).str, (const std::wstring&)*o.at(iname).str));
         }

         orgMaps.insert(SessionMap::value_type(s, res));
      }
   }
   return res;
}

bool GRServer::AddOnInit()
{
   AddFunction(&photoSave);
   AddFunction(&orgName);
   return true;
}


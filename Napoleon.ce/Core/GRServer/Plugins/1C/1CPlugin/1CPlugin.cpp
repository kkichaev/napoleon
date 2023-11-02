/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * 1C plugin
 *
 * ert   19/08/2010   creating
 */

#include "stdafx.h"
#include "1CPlugin.h"

#include <iserver.h>
#include <isessobj.h>
#include <ithread.h>
#include <gservices.h>
#include <stdobjs.h>
#include <ServerDefs.h>
#include <idatasource.h>

using namespace GRServer;

const DWORD WAIT_RESULT_TIMEOUT = 240000; // 4 min

class GRServer::ThreadWorker : public IThreadWorker
{
public:
   ThreadWorker(Plugin* owner)
   {
      this->owner = owner;
   }

   virtual DWORD Execute();

   void ClearOwner() { owner = NULL; }

protected:
   Plugin *owner;
};

class ObjSource : public IDataSource::IObjSource
{
public:
   ObjSource(Plugin* p);
   virtual ~ObjSource();

   virtual ExchangeList* Do(ISessionObject *object, const std::wstring& action, IFormatHolder* f, BSTR* msg);
   virtual void Close();

protected:
   Plugin *p;
};

class DataSourceCreator : public IDataSource::ICreator
{
public:
   DataSourceCreator(Plugin* p) { this->p = p; }

   virtual const wchar_t* Name() const { return SOURCE_NAME; }

   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IObjSource* CreateObjSource(const ParamList& parameters, const ISessionObject& object) const
   {
      return new ObjSource(p);
   }

protected:
   Plugin *p;
};

ObjSource::ObjSource(Plugin* p)
{
   this->p = p;
}

ObjSource::~ObjSource()
{
}

ExchangeList* ObjSource::Do(ISessionObject *object, const std::wstring& action, IFormatHolder* f, wchar_t** msg)
{
   return p->Do(*object->Self(), action, f, msg);
}

void ObjSource::Close()
{
}

//
// ----------------------------- Plugin ---------------------------
//
Plugin::Plugin() : server(NULL), worker(NULL), result(NULL)
{
   evStop = CreateEvent(NULL, TRUE, FALSE, NULL);
   evResult = CreateEvent(NULL, TRUE, FALSE, NULL);

   objCreator.GetFormatList()->AddFormat(new ServerCommandFormat(), true);
}

Plugin::~Plugin()
{
   CloseHandle(evStop);
   CloseHandle(evResult);

   delete result;
   result = NULL;
}

bool Plugin::Init(IServer* server)
{
   this->server = server;

   const IServerConfig& config = server->GetConfig();
   IObjectDef* od = (IObjectDef*)server->GetService(OBJDEF_SERVICE);

   IDataSourceRegister* sr = (IDataSourceRegister*)server->GetService(SOURCE_SERVICE);
   DataSourceCreator *creator = new DataSourceCreator(this);
   sr->AddSource(creator);
   sr->SetDefaultObjSource(creator);

   std::string fileName(config.PluginsFolder());
   fileName += PLUGIN_DEF_FILE;

   if(GetFileAttributesA(fileName.c_str()) != 0xFFFFFFFF)
      return od->Load(fileName.c_str());

   return true;
}

bool Plugin::Connect(Socket *src, const wchar_t* password)
{
   CloseConnection();

   src->CopyTo(&socket);
   SendAnswer(&socket, true, password);
   ResetEvent(evStop);

   worker = new ThreadWorker(this);
   return server->Execute(worker);
}

void Plugin::LostConnection()
{
   worker = NULL;
   server->PluginClosed(this);

   socket.Close();
}

void Plugin::CloseConnection()
{
   if( worker )
   {
      worker->ClearOwner();
      worker = NULL;
      SetEvent(evStop);
   }

   socket.Close();
}

void Plugin::Close()
{
   SendCommand(&socket, PLUGIN_CLOSED, L"");
   CloseConnection();
}

ExchangeList* Plugin::Do(const ServObject& object, const std::wstring& action, IFormatHolder* f, BSTR* msg)
{
   if( socket.IsConnected() == false )
   {
      *msg = SysAllocString(L"Нет связи с 1С");
      return NULL;
   }

   Member m;
   FormatList* fl = objCreator.GetFormatList();
   fl->SetHolder(f);

   ExchangeList objList(fl);

   ServObject *cmd = objCreator.Create(ServerCommandFormat::Name());
   Object *o = cmd->AddObject();
   m.str = new CString(DO_OBJ_CMD);
   o->Assign(m, COMMAND_MEMBER);
   m.str = new CString(action);
   o->Assign(m, PARAM_MEMBER);

   objList.push_back(cmd);
   objList.push_back((ServObject*)&object);

   ResetEvent(evResult);
   bool res = objList.Write(&socket);
   objList[1] = NULL; // prevent delete object 

   fl->SetHolder(NULL);

   ExchangeList* retVal = NULL;
   if( res )
   {
      HANDLE hh[2];
      hh[0] = evResult;
      hh[1] = evStop;

      DWORD wr = WaitForMultipleObjects(2, hh, FALSE, WAIT_RESULT_TIMEOUT);
      if( wr == WAIT_OBJECT_0 )
      {
         retVal = result;
         result = NULL;
      }
      else
         *msg = SysAllocString(L"Нет ответа от 1С");
   }

   return retVal;
}

void Plugin::MakeResult(ExchangeList* rcvd, int index)
{
   delete result;
   result = NULL;

   if( rcvd != NULL && (int)rcvd->size() > index )
   {
      result = new ExchangeList(rcvd->GetFormatList());
      for( ; index < (int)rcvd->size(); index++ )
      {
         ServObject *src = rcvd->at(index);
         ServObject *so = new ServObject(src->format);
         result->push_back(so);

         while( src->size() )
         {
            Object *o = so->AddObject();
            src->front()->MoveTo(o);
            src->erase(src->begin());
         }
      }
   }

   SetEvent(evResult);
}

void Plugin::HandleObject(ExchangeList* object)
{
   const ServObject* curObj = object->front();
   if( curObj->size() )
   {
      const Object* obj = curObj->front();
      if( curObj->Name().compare(SERVER_COMMAND) == 0 )
      {
         const Member *m = (*obj)[COMMAND_MEMBER];
         if( m )
         {
            if( m->str->compare(KEEP_ALIVE) == 0 )
            {
               SendAnswer(&socket, true, L"");
            }
         }
      } else if( curObj->Name().compare(SERVER_ANSWER) == 0 )
      {
         const Member *m = (*obj)[MESSAGE_MEMBER];
         if( m )
         {
            if( m->str->compare(DO_OBJ_CMD) == 0 )
            {
               MakeResult(object, 1);
            }
         }
      }
   }
}

bool Plugin::Read()
{
   Binary buf;
   ExchangeList obj(objCreator.GetFormatList());

   bool res = obj.Read(&buf, &socket, INFINITE, evStop, &objCreator);
   if( res )
      HandleObject(&obj);

   return res;
}

//
// ----------------------------- ThreadWorker ---------------------------
//
DWORD ThreadWorker::Execute()
{
   while( true )
   {
      if( !owner->Read() || !owner )
         break;
   }

   if( owner )
      owner->LostConnection();

   return 0;
}

static WORD ctr = 0;
extern "C" DECL_SPEC bool GetPlugin(IPlugin** plugin)
{
   if( ctr == 0 )
   {
      *plugin = new Plugin();
      ctr++;

      return true;
   }
   return false;
}

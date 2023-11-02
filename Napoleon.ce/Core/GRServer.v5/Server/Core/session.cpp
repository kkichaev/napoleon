/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * Session
 *
 * ert   26/09/2009   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "objdef.h"
#include "objects.h"
#include "session.h"
#include "ServerDefs.h"
#include "datactrl.h"
#include "srvdata.h"
#include "dispatcher.h"

#include "srvutility.h"
#include "server.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

static const int WAIT_CLIENT_TIMEOUT = 30 * 1000;

static const __int64 UPLOAD_TIMEOUT = 30 * 10000000; // 30 seconds

static const std::string JSON_NAME_TAG("name");
static const std::string JSON_DATA_TAG("data");
static const std::string JSON_WHERE_TAG("where");

static const std::wstring JSON_FORMAT_NAME(L"JsonParam");

static const std::wstring UPLOAD_OBJECT_NAME(L"UploadFileRequests");
static const std::wstring UPLOAD_CODE_FIELD(L"code");
static const std::wstring UPLOAD_DATA_FIELD(L"object");
static const std::wstring UPLOAD_OBJNAME_FIELD(L"objName");
static const std::wstring UPLOAD_FLDNAME_FIELD(L"fldName");

//
//------------------------------------------ Session ----------------------------------------------------
//
Session::Session(Dispatcher* _d) :
   dispatcher(_d), config(_d->Controller().Config()), curUser(NULL), ack(&formats),
   response(&formats), trash(&formats), answer(NULL), socket(NULL), curMemory(0), agents(NULL)
   ,jsonWriter(NULL), evStop(INVALID_HANDLE_VALUE), jsonResult(new JSONArray())
{
	memoryLimit = _d->SessionMemoryLimit();
	totalMemoryLimit = _d->TotalMemoryLimit();
   user = NULL;
}

Session::~Session()
{
   Clear();
}

void Session::Clear()
{
   HandlerList::iterator i = handlers.begin();
   for( ; i != handlers.end(); i++ )
      (*i)->SessionClosed(this);

	curMemory = 0;

   ack.clear();
   response.clear();
	trash.clear();

   delete answer;
   answer = NULL;

   outStream.Clear();

   delete user;
   user = NULL;

   delete curUser;
   curUser = NULL;

	delete agents;
	agents = NULL;

	formats.clear();
}

AgentsObject::AgentsObject(Session* s) : SessionObject(ObjectDef::Get(L"Agents"), s)
{
	if (!CreateReader())
	{
		gServer->AddError(false, "Can't load Agents GetLastError %d", GetLastError());
	}
	Load(NULL);
	CloseReader();
}

SessionObject* Session::Agents() const
{
	if (agents == NULL)
	{
		agents = new AgentsObject(const_cast<Session*>(this));
	}

	return agents;
}

void Session::AddToTemp(SessionObject *so)
{
	trash.push_back(so);
}

SessionObject* Session::FindInTemp(const std::wstring& name)
{
	ExchangeList::iterator i = trash.begin();
	for ( ;i != trash.end(); i++)
	{
		if ((*i)->Name().compare(name) == 0)
			return (SessionObject*)((ServObject*)(*i));
	}

	return NULL;
}

const StrSet& Session::AllowedUID() const { return user->AllowedUID(); }

bool Session::HandleCommand(const wchar_t* command, const Member* param)
{ 
	return dispatcher->HandleCommand(command, param, this);
}

bool Session::CanAddObject(const Object& o)
{
	if (memoryLimit == 0) return true;
	if (curMemory / (1024 * 1024) > memoryLimit) return false;

	size_t cb = o.Size();
	curMemory += cb;

	if (curMemory / (1024*1024) <= memoryLimit)
		return true;

	USES_CONVERSION;
	gServer->AddLog("Memory limit exceeded (%d) while reading object %s, limit=%d", socket->GetSocket(), 
      W2A_CP(o.format.name.c_str(), CP_UTF8), memoryLimit);
	return false;
}

ISessionObject* Session::CreateObject(const std::wstring& objName, bool addToResponse)
{
   // can't get filter to agents in python
	// if (objName.compare(L"Agents") == 0)
	// {
	// 	return Agents();
	// }

	SessionObject* so = Build(objName, true);
   if( addToResponse )
      response.push_back(so);

   return so;
}

const ObjectDef* Session::GetObjDef(const std::wstring& name) const
{
#ifdef ADS_COMPATIBILITY
   const ObjectDef* od = NULL;
   if( user != NULL )
   {
      const wchar_t* v = user->Version();
      if( (v == NULL || *v == L'\0') && (name.compare(L"Order") == 0 || name.compare(L"PDAOrder") == 0 || name.compare(L"Client") == 0) )
      {
         std::wstring nname(name);
         nname += L"Prev";
         od = ObjectDef::Get(nname);
      }
   }
   if( od == NULL )
      od = ObjectDef::Get(name);
#else
   const ObjectDef* od = ObjectDef::Get(name);
#endif
   if( od == NULL )
   {
      ObjectDef srch;
      srch.name = name;

      std::set<ObjectDef>::const_iterator fnd = localObjDef.find(srch);
      if( fnd != localObjDef.end() )
         od = &(*fnd);
   }

   return od;
}

SessionObject* Session::Build(const std::wstring &name, bool createAlways)
{
   const ObjectDef* od = GetObjDef(name);

   SessionObject *so = NULL;
   if( od == NULL )
   {
      if( createAlways )
         so = new SessionObject(name, this);
   } else
   {
      so = new SessionObject(od, this);
   }

   return so;
}

void Session::PostObject(ISessionObject* object)
{
	postObjects.push_back(object);
}

void Session::PostObjects()
{
   if( answer != NULL && answer->size() )
   {
      answer->ToString(&outStream, GetFormatList());
      answer->clear();
   }

	USES_CONVERSION;

	int objCtr = 0;
	DWORD sendLimit = 0;
	std::vector<ISessionObject*>::iterator si = postObjects.begin();
	for( ; si != postObjects.end(); si++, objCtr++ )
	{
		SessionObject* so = (SessionObject*)(*si);
		const char *objA = W2A_CP(so->Name().c_str(), CP_UTF8);

		DWORD cb = 0, recs = 0;
		bool done = WriteObjectToStream(so, &cb, &recs);

		dispatcher->AddLog(IErrorLogger::Full, "post size = %u, recs = %u %s ", cb, recs, objA);

		if (!done)
			break;
	}

	postObjects.clear();
}

bool Session::SendStreamPart(SessionObject* curObject)
{
	SessionObject *strCont = Build(L"StreamContinue", false);
	if( strCont == NULL )
		return false;

	Object *o = strCont->AddObject();
	Member m;
	m.number = 1;
	o->Assign(m, L"continue");
	WriteToStream(*strCont, true);
	delete strCont;

	if (!SendStream(curObject))
		return false;

	ExchangeList tck(&formats);
	Binary buf;

	if (tck.Read(&buf, socket, WAIT_CLIENT_TIMEOUT, evStop, this) && tck.size() && tck.front()->Name().compare(SERVER_COMMAND) == 0)
	{
		const Object& cmdObject = (*tck.front())[0];
		const Member *cmd = cmdObject[COMMAND_MEMBER];

		if (cmd == NULL) return false;
		return (cmd->str->compare(DONE_COMMAND) == 0);
	}

	return false;
	//return (SendStream(curObject) && ReadAck(WAIT_CLIENT_TIMEOUT) && AckIs(SERVER_COMMAND) && CommandIs(DONE_COMMAND));
}

void Session::AddToAnswer(ServObject *object)
{
   if (jsonWriter != NULL)
   {
      JSONArray* r = GetResultObject(object->format->name);

      ToJSON(r, *object);
      return;
   }

   FormatList *fl = GetFormatList();
   if( answer != NULL && answer->size() )
   {
      answer->ToString(&outStream, fl);
      answer->clear();
   }

   object->ToString(&outStream, fl);
}

void Session::SetObjDef(ObjectDef* od, Format* f, FormatList *fl)
{
   od->name = f->name;

   GRServer::Format::iterator fi = f->begin();
   for( ; fi != f->end(); fi++ )
   {
      ObjectDef::Field fld;
      fld.format = *fi;
      fld.data = fi->name;
      fld.width = 0;
      fld.flags = 0;
      fld.pass = 0;

      size_t pos = fi->name.find('@');
      if( pos != std::wstring::npos )
      {
         fld.data.erase(0, pos+1);
         fi->name.erase(pos);
         fld.format.name.erase(pos);
      }

      if( fi->type == MemberFormat::mtObject )
      {
         ObjectDef child;
         child.name = f->name;
         child.name += L'$';
         child.name += fi->name;

         child.parent = od->name;
         fld.data = child.name;
      
         Format* chF = fl->GetFormat(child.name);
         if( chF != NULL )
         {
            SetObjDef(&child, chF, fl);
         }
      }
      od->fields.insert(fld);
   }

   localObjDef.insert(*od);
}

Format* Session::RegisterType(const std::wstring& type, bool registerObjDef)
{
   FormatList *fl = GetFormatList();
   Format *f = fl->NewFormat(L"");

#ifdef UNIX   
   USES_WCONVERSION;
   const unsigned short* start = (const unsigned short*)W32_16(type.c_str());
   const unsigned short* end = start + type.size();

   ParseStreamU ps(start, end);
#else
   const wchar_t *start = type.c_str();
   const wchar_t *end = start + type.size();
   ParseStreamU ps(start, end);
#endif

   bool res = f->Read(ps, fl);
   if( res )
      fl->AddFormat(f, false);
   else
   {
      delete f;
      f = NULL;
   }

   if( res && registerObjDef && GetObjDef(f->name) == NULL )
   {
      ObjectDef od;
      SetObjDef(&od, f, fl);
   }

   return f;
}

ISessionObject* Session::GetObject(const std::wstring& objName, const ISessionObject* thisObject)
{
   SessionObject* so = (SessionObject*)FindObject(objName, thisObject);
   if( so != NULL )
      return so;

   so = Build(objName, false);
   if( so == NULL )
      return NULL;

   response.push_back(so);
   return so;
}

ISessionObject* Session::LoadObject(const std::wstring& objName, const ISessionObject* thisObject, const wchar_t* filter)
{
   ISessionObject* so = GetObject(objName, thisObject);
   if( so == NULL )
      return NULL;

	if (so->Reading(filter))
	{
		if (curUser != NULL)
		{
			((SessionObject*)so)->SetUserid(curUser->ID());
		}
		return so;
	}

   return NULL;
}

JSONArray* Session::GetResultObject(const std::wstring& name)
{
   USES_CONVERSION;
   const char* aname = W2A_CP(name.c_str(), CP_UTF8);
   for (JSONArray::iterator i = jsonResult.value.array->begin(); i != jsonResult.value.array->end(); i++)
   {
      JSONValue& res = (*i)->get(JSON_NAME_TAG);

      // impossible
      if (!res.IsString()) continue;
      if (res.value.string->compare(aname) == 0)
      {
         return (*i)->get(JSON_DATA_TAG).value.array;
      }
   }

   JSONArray* res = new JSONArray();
   JSONObject* obj = new JSONObject();
   obj->Put(JSON_NAME_TAG, aname);
   obj->Put(JSON_DATA_TAG, new JSONValue(res));

   jsonResult.value.array->push_back(new JSONValue(obj));
   return res;
}


DWORD Session::WriteToStream(SessionObject& so, bool format)
{
   if (jsonWriter != NULL)
   {
      so.WriteTo(*GetResultObject(so.format->name));
      return 0;
   }
   
   DWORD prevSize = outStream.Size();
	WriteAnswerToOutStream();
	
   if( format )
      so.WriteFormat(&outStream);
   so.WriteTo(&outStream);

	return outStream.Size() - prevSize;
}

bool Session::WriteObjectToStream(SessionObject* so, DWORD *cb, DWORD *count)
{
	bool ret = true;
   DWORD sendLimit = so->SendLimit();
	DWORD recs = 0, sended = 0;

   bool addFormat = true;
   while( so->MoveNext() )
   {
		//if (recs < 100)
		sended += WriteToStream(*so, addFormat);
      addFormat = false;
		recs++;

      if (jsonWriter != NULL)
         continue;

		DWORD curSize = outStream.Size();
      if( sendLimit != 0 && curSize > sendLimit )
      {
         // ���� ��� ������ ���������� ��� ������
			if (!so->MoveNext())
				break;
     
			if (!SendStreamPart(so))
			{
				ret = false;
				break;
			}

			// ���������� ������ ������� ������� ����� ���������
         sended += WriteToStream(*so, true);
			recs++;
      }
	}

	if (cb != NULL)
		*cb = sended;
	if (count != NULL)
		*count = recs;
	return ret;
}

const SessionObject* Session::WriteObject(const std::wstring& objName, const wchar_t* filter)
{
   USES_CONVERSION;
   const char *userA = W2A_CP(user->UserName(), CP_UTF8);
   const char *objA = W2A_CP(objName.c_str(), CP_UTF8);

	dispatcher->AddLog(IErrorLogger::Full, "get object (%d) %s:%s", (int)socket->GetSocket(), objA, (filter) ? W2A_CP(filter, CP_UTF8) : "");

   if( !user->ObjectAllowed(objName, User::oaRead) )
   {
      gServer->AddError(false, "������������ '%s' ��������� �������� ������ '%s'",  userA, objA);
      return NULL;
   }

   try
   {
      SessionObject* so = (SessionObject*)FindObject(objName, NULL);
		if (so == NULL && ObjectDef::HaveEvent(Event::ResolveObjects))
		{
			const ObjectDef* rdef = ObjectDef::Get(L"%ResolveInfo");
			SessionObject *uso = new SessionObject(rdef, this);
			int idx = uso->format->FindMember(L"name");
			if (idx >= 0)
			{
				Object *obj = uso->AddObject();
				obj->at(idx).str->assign(objName);
				response.push_back(uso);

				ObjectDef::Fire(Event::ResolveObjects, this);
			}
			so = (SessionObject*)FindObject(objName, NULL);
		}

		if ( so == NULL )
		{
         so = Build(objName, false);
         if( so == NULL )
         {
            //gServer->AddError(false, "�� ���� ������� ������ '%s'", objA);
            return NULL;
         }
         response.push_back(so);
      }

		// Dispatcher::RequestSemahore();

		DWORD start = GetTickCount();
		if (so->CreateReader(filter))
      {
			DWORD cb = 0, recs = 0;
			bool writed = WriteObjectToStream(so, &cb, &recs);
			// Dispatcher::ReleaseSemaphore();

			if (!writed)
			{
				dispatcher->AddLog(IErrorLogger::Full, "fail to send (%d) %s", (int)socket->GetSocket(), objA);
				return NULL;
			}

			so->CloseReader();

			DWORD finish = GetTickCount();
			dispatcher->AddLog(IErrorLogger::Full, "done (%d) time = %u, size = %u, recs = %u %s", (int)socket->GetSocket(), finish - start, cb, recs, objA);
			return so;
		}
		else
		{
			// Dispatcher::ReleaseSemaphore();
		}
		//else
      //   gServer->AddError(false, "�� ���� ������� Reader ��� ������� '%s'", objA);
   } catch(...)
   {
		gServer->AddError(false, "Exception while reading object (%d) '%s'", (int)socket->GetSocket(), objA);
   }
	dispatcher->AddLog(IErrorLogger::Full, "no object %s", objA);
	return NULL;
}

bool Session::GetObjectFormat(const Member* param)
{
   std::wstring objName((const std::wstring&)*param->str);
   SessionObject* so = (SessionObject*)FindObject(objName, NULL);
   if( so == NULL )
   {
      so = Build(objName, false);
      if( so != NULL )
      {
			WriteAnswerToOutStream();
			so->WriteFormat(&outStream);
         delete so;
      }
   } else
   {
		WriteAnswerToOutStream();
		so->WriteFormat(&outStream);
   }

   return true;
}

void Session::WriteAnswerToOutStream()
{
	if (answer != NULL && answer->size())
	{
		answer->ToString(&outStream, GetFormatList());
		answer->clear();
	}
}

static void AddFormatMember(GRServer::Format* dest, JSONObject::const_iterator& src, FormatList& fmts)
{
   USES_CONVERSION;

   MemberFormat mf;
   mf.name = A2W_CP(src->first.c_str(), CP_UTF8);

   if (src->second->IsString())
   {
      mf.type = MemberFormat::mtString;
      dest->push_back(mf);
   }
   else if (src->second->IsInt())
   {
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      dest->push_back(mf);
   }
   else if (src->second->IsDouble())
   {
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 8;
      dest->push_back(mf);
   }
   else if (src->second->IsArray())
   {
      const JSONArray& asrc = *src->second->value.array;
      const JSONValue* aval;
      if (asrc.size() > 0 && (aval = asrc.at(0))->IsObject())
      {
         std::wstring name(dest->name + L'$' + mf.name);
         mf.type = MemberFormat::mtObject;
         dest->push_back(mf);

         GRServer::Format* ff = new GRServer::Format(name);
         fmts.AddFormat(ff, true);

         for (JSONObject::const_iterator fi = aval->value.object->begin(); fi != aval->value.object->end(); fi++)
         {
            AddFormatMember(ff, fi, fmts);
         }
      }
   }
}

void Session::PushToAck(const JSONObject& src)
{
   GRServer::Format* fmt = new GRServer::Format(JSON_FORMAT_NAME);
   formats.AddFormat(fmt, true);

   for (JSONObject::const_iterator fi = src.begin(); fi != src.end(); fi++)
   {
      AddFormatMember(fmt, fi, formats);
   }

   ServObject* so = new ServObject(fmt);
   GRServer::Object *o = so->AddObject();
   SetFields(o, src, &formats);

   ack.push_back(so);
}

bool Session::PutObjects(const JSONValue& src, bool deleteBefore)
{
   if (!src.IsArray())
      return false;

   USES_CONVERSION;

   for (JSONArray::const_iterator i = src.value.array->begin(); i != src.value.array->end(); i++)
   {
      if (!(*i)->IsObject()) continue;

      std::string name;
      JSONValue& dsrc = (*i)->get(JSON_DATA_TAG);

      if (!(*i)->read(&name, JSON_NAME_TAG) || !dsrc.IsArray()) continue;

      const wchar_t* oname = A2W_CP(name.c_str(), CP_UTF8);
      SessionObject* so = Build(oname, false);
      if (so == NULL)
      {
         AddAnswer(false, oname);
         gServer->AddLog(IErrorLogger::Full, "Socket (%d) no post object named %s", socket->GetSocket(), name.c_str());
         continue;
      }

      if (so->ReadFrom(*dsrc.value.array))
      {
         if (deleteBefore)
         {
            std::string tf;
            JSONValue& wsrc = (*i)->get(JSON_WHERE_TAG);
            if (wsrc.IsString())
            {
               tf = *wsrc.value.string;
            }
            so->Removing(A2W_CP(tf.c_str(), CP_UTF8));
            // AddAnswer();
         }

         Writing(so, true);
      }

      delete so;
   }
   return true;
}

bool Session::Writing(SessionObject* so, bool updateExecutable)
{
   USES_CONVERSION;

   const char* objA = W2A_CP(so->format->name.c_str(), CP_UTF8);
   bool res = false;
   std::wstring response(so->Name());
   try
   {
      User* u = (curUser == NULL) ? user : curUser;
      if (u->ObjectAllowed(so->format->name, User::oaWrite))
      {
         ObjectDef* od = (ObjectDef*)so->GetObjectDef();
         if (od != NULL && !(od)->events.Fire(IEvent::BeforePut, this, so))
         {
            res = false;
            response = L"Write object prohibit "; response += so->Name();
         }
         else
         {
            // Dispatcher::RequestSemahore();
            res = (so->CreateWriter(NULL, updateExecutable ? stCommon : stInternal) && so->Write(updateExecutable, NULL));
            // Dispatcher::ReleaseSemaphore();
         }
      }
      else
         gServer->AddError(false, "User '%s' can't write object '%s'", W2A_CP(user->Name().c_str(), CP_UTF8), objA);
   }
   catch (...)
   {
      gServer->AddError(false, "Exception while writing object '%s'", objA);
   }
   try
   {
      so->CloseWriter();
   }
   catch (...)
   {
      gServer->AddError(false, "Exception on CloseWriter of the object '%s'", objA);
   }

   AddAnswer(res, response);
   return res;
}

bool Session::StoreAckObjects(bool retIDS, bool updateExecutable)
{
   if( ack.size() < 1 ) return false;

   USES_CONVERSION;

   ExchangeList::iterator i = ack.begin();
   for( ; i != ack.end(); i++ )
   {
      SessionObject* so = (SessionObject*)((ServObject*)(*i));
      const std::wstring& name = so->Name();
      if( name.compare(SERVER_COMMAND) == 0 )
         break;

      Writing(so, updateExecutable);
   }

   ack.RemoveTo(i);
   return true;
}

void Session::WriteStdObjects()
{
   //std::vector<std::wstring> names;
   CVector<CString> *names;
   ObjectDef::GetObjectsName(&names, IObjectDef::SendAlways);

   //std::vector<std::wstring>::const_iterator i = names.begin();
   CVector<CString>::const_iterator i = names->begin();
   for( ; i != names->end(); i++ )
   {
      SessionObject* so = (SessionObject*)FindObject((const std::wstring&)(*i), NULL);
      if( so == NULL )
         WriteObject((const std::wstring&)(*i));
   }
   delete names;
}

bool Session::CommandIs(const wchar_t* command) const
{
   const Object& cmdObject = Command();
   const Member *cmd = cmdObject[COMMAND_MEMBER];

   if( cmd == NULL ) return false;
   return (cmd->str->compare(command) == 0);
}

bool Session::PopAck()
{
   if( ack.size() != 0 )
   {
      ServObject* so = ack.front();
      if( so->size() > 1 && so->Name().compare(SERVER_COMMAND) == 0 )
      {
         Object* o = so->front();
         delete o;
         so->front() = NULL;
         so->erase(so->begin());
      }
      else
      {
         delete so;
         ack.front() = NULL;
         ack.erase(ack.begin());
      }
   }

   return (ack.size() != 0);
}

void Session::Commit()
{
   ExchangeList::iterator i = response.begin();
   for( ; i != response.end(); i++ )
   {
      SessionObject* so = (SessionObject*)((ServObject*)(*i));
      const IObjectData* od = so->GetObjectDef();
      if( od != NULL && (od->flags & IObjectDef::RemoveOnCommit) != 0 )
         so->RemoveSource();
   }
	response.clear();
	curMemory = 0;
}

bool Session::Selecting(const Member* param)
{
   if( param != NULL )
   {
      size_t pos = param->str->find(L':');
		if( pos != std::wstring::npos )
         WriteObject(param->str->substr(0, pos), param->str->substr(pos+1).c_str());

      return true;
   }
   return false;
}

bool Session::Removing(const Member* param)
{
   bool ret = false;
   try
   {
      if( param != NULL )
      {
         size_t pos = param->str->find(L':');
         if( pos != std::wstring::npos )
         {
            const std::wstring& objName = param->str->substr(0, pos);
				User* u = (curUser == NULL) ? user : curUser;
				if (u->ObjectAllowed(objName, User::oaRemove))
            {
               SessionObject* so = Build(objName, false);
               if( so != NULL )
               {
                  ret = so->Removing(param->str->substr(pos+1).c_str());
                  AddAnswer(ret, objName.c_str());
                  delete so;
               }
            }
         }
      }
   }
   catch(...)
   {
      ret = false;
   }
   return ret;
}

void Session::AddHandler(IHandler* handler)
{
   HandlerList::iterator i = handlers.begin();
   for( ; i != handlers.end(); i++ )
      if( (*i) == handler )
         return;

   handlers.push_back(handler);
}

void Session::RemoveHandler(IHandler* handler)
{
   HandlerList::iterator i = handlers.begin();
   for( ; i != handlers.end(); )
      if( (*i) == handler )
      {
         handlers.erase(i);
         break;
      }
}

Session::AckReturn Session::ReadAck(DWORD timeout)
{
   if( socket == NULL ) return arFail;

   ack.clear();

   Binary buf;
	if (ack.Read(&buf, socket, timeout, evStop, this))
		return (ack.size()) ? arAck : arFail;

   return arFail;
}

#ifdef UNIX
#include <sys/sysinfo.h>
#include <fstream>

using namespace std;
void Session::MemoryStat(std::string* out, bool printLimits) const
{
   std::ifstream stat("/proc/self/statm",std::ios_base::in); 
   unsigned size, resident, shared, text, lib, data, dirty;
   stat >> size >> resident >> shared >> text >> lib >> data >> dirty;
   unsigned pgSize = sysconf(_SC_PAGE_SIZE);


	std::stringstream str;
	if (printLimits)
		str << "Mem limit " << memoryLimit << "/" << totalMemoryLimit;
	else
		str << "Mem";
	str << " cur " << (size * pgSize) / (1024 * 1024);
	str << " data " << (data * pgSize) / (1024 * 1024) << " ws " << (resident * pgSize) / (1024 * 1024);
	
	out->append(str.str());
}

#else
#include <Psapi.h>
void Session::MemoryStat(std::string* out, bool printLimits) const
{

	// getrusage on linux
	HANDLE h = GetCurrentProcess();
	PROCESS_MEMORY_COUNTERS mem;
	GetProcessMemoryInfo(h, (PPROCESS_MEMORY_COUNTERS)&mem, sizeof(mem));



	std::stringstream str;
	if (printLimits)
		str << "Mem limit " << memoryLimit << "/" << totalMemoryLimit;
	else
		str << "Mem";
	str << " cur " << curMemory / (1024 * 1024);
	str << " ws " << mem.WorkingSetSize / (1024 * 1024) << " peak ws " << mem.PeakWorkingSetSize / (1024 * 1024);
	
	out->append(str.str());
}

#endif

//#define _NO_IFDED 1

bool Session::SendStream(SessionObject* runninObject)
{
   if( socket == NULL ) return false;

	//dispatcher->AddLog(IErrorLogger::Full, "Sending stream %d", (int)socket->GetSocket());

	bool retVal = true;
   if( answer->size() )
   {
		//dispatcher->AddLog(IErrorLogger::Full, "Writing answer %d %d", (int)socket->GetSocket(), answer->size());
		
		OutStream s;
      answer->ToString(&s, GetFormatList());
      answer->clear();
      outStream.InsertToFront(s);
	}

	// remove all response, but RemoveOnCommit
	ExchangeList::iterator i = response.begin();
	for (; i != response.end(); )
	{
		SessionObject* so = (SessionObject*)((ServObject*)(*i));
		if (so == runninObject)
		{
			i++;
			continue;
		}

		bool candelete = true;
		ExchangeList::const_iterator fi = trash.begin();
		for (; fi != trash.end(); fi++)
		{
			if (*fi == so)
			{
				candelete = false;
				break;
			}
		}

		const IObjectData* od = so->GetObjectDef();
		if( od == NULL || (od->flags & IObjectDef::RemoveOnCommit) == 0)
		{

			*i = NULL;
			if(candelete)
				delete so;
			i = response.erase(i);
		}
		else
		{
			i++;
		}
	}

	// ��������� ������� ��� �������� ����� �������
	if(runninObject == NULL)
		ack.clear();

	trash.clear();

   if( outStream.Size() )
   {
#ifdef _MAKE_SEND_LOG
      std::string fname = gServer->GetConfig().ExchangeFolder();
      fname += "log.txt";
      FILE *wr= fopen(fname.c_str(), "ab");
      if( wr != NULL )
      {
      const std::wstring& sout = outStream.ToString();
      fwrite(sout.c_str(), sizeof(wchar_t), sout.size(), wr);
      fclose(wr);
      }
#endif
		//dispatcher->AddLog(IErrorLogger::Full, "Making packet %d %d", (int)socket->GetSocket(), outStream.Size());

		Packet* pkt = Packet::MakePacket(outStream, (outStream.IsNeedCompress()) ? GZIP_OPT : L"");
		
		//dispatcher->AddLog(IErrorLogger::Full, "Made packet %d %d", (int)socket->GetSocket(), pkt->data->Size());
		retVal = (pkt != NULL &&  socket->Write(*pkt));
      delete pkt;
		
		//dispatcher->AddLog(IErrorLogger::Full, "Write packet %d", (int)socket->GetSocket());
		outStream.Clear();

		//dispatcher->AddLog(IErrorLogger::Full, "Clear stream %d", (int)socket->GetSocket());
	}

   return retVal;
}

const sockaddr_in& Session::Address() const
{
   return socket->Address();
}

void Session::GetIPAddress(std::wstring* ip) const
{
	wchar_t buf[30];
	const sockaddr_in& addr = socket->Address();
#ifdef UNIX
	wsprintf(buf, L"%u.%u.%u.%u", (addr.sin_addr.s_addr & 0xFF000000) >> 24
      ,(addr.sin_addr.s_addr & 0xFF0000) >> 16, (addr.sin_addr.s_addr & 0xFF00) >> 8, (addr.sin_addr.s_addr & 0xFF));
#else
	wsprintf(buf, L"%u.%u.%u.%u", addr.sin_addr.s_net, addr.sin_addr.s_host, addr.sin_addr.s_lh, addr.sin_addr.s_impno);
#endif
	ip->assign(buf);
}

bool Session::Execute(const wchar_t* stmt)
{
   return internalDataSource->Execute(stmt, this);
}

ISessionObject* Session::Query(const wchar_t* stmt, const wchar_t* name, const wchar_t* groupExpr)
{
	ISessionObject* so = internalDataSource->Query(stmt, name, groupExpr, this);
	if(so != NULL)
		trash.push_back(so->Self());
	return so;
}

void Session::WriteLicenseRequest(const CString& mgrLog)
{
// 	SessionObject *so = Build(L"ReqServerData", false);
// 	if (so != NULL)
// 	{      
// 		Object* o = so->AddObject();
// 		Member* m = (*o)[L"name"];
// 		m->str->assign(PROJECT_NAME);

// 		m = (*o)[L"data"];
// 		m->str->assign(mgrLog.c_str());
// #if UNIX
//       tzset();
// 		m = (*o)[L"tz"];
// 		m->number = timezone / 60;
// #else
// 		TIME_ZONE_INFORMATION st;
// 		GetTimeZoneInformation(&st);

// 		m = (*o)[L"tz"];
// 		m->number = st.Bias;
// #endif
// 		WriteToStream(*so, true);
// 		delete so;
// 	}
}

void Session::FlushJSONWriter(std::string* buf)
{
   if (jsonWriter != NULL)
   {
      jsonWriter->Write(buf, jsonResult);
   }
}

bool Session::RequestUpload(std::wstring* url, const std::wstring& objName, const std::wstring& objData)
{
   // check object 
   // gen new utl
   // put to UploadFileRequests

   const ObjectDef* od = GetObjDef(objName);
   if (od == NULL)
   {
      AddAnswer(false, L"Can't find object for upload");
      return false;
   }

   std::wstring binaryField;

   ObjectDef::Fields::const_iterator i = od->fields.begin();
   for (; i != od->fields.end(); i++)
   {
      const MemberFormat& mf = i->format;
      if (mf.type == MemberFormat::mtBinary && (i->flags & ObjectDef::Field::File) != 0 && !i->src.empty())
      {
         const IObjectData::Field* field = od->FindField(i->src);
         // if (field != NULL && (field->format.flags & MemberFormat::ExecOnPut))
         if (field != NULL)
         {
            binaryField = i->format.name;
            break;
         }
      }
   }

   if (binaryField.empty())
   {
      AddAnswer(false, L"Can't find binary field with calculable src");
      return false;
   }

   GenerateID(url);

   SessionObject* so = Build(UPLOAD_OBJECT_NAME, false);
   GRServer::Object* o = so->AddObject();

   (*o)[UPLOAD_CODE_FIELD]->str->assign(*url);
   (*o)[UPLOAD_OBJNAME_FIELD]->str->assign(objName);
   (*o)[UPLOAD_DATA_FIELD]->str->assign(objData);
   (*o)[UPLOAD_FLDNAME_FIELD]->str->assign(binaryField);

   so->CreateWriter(NULL, stInternal);
   so->Write(true);
   so->CloseWriter();

   delete so;

   return true;
}

bool Session::SaveFile(const std::wstring& url, const std::string& fileContent)
{
   USES_CONVERSION;

   std::wstring filter;
   filter.append(L"\"").append(UPLOAD_CODE_FIELD).append(L"\"='").append(url).append(L"'");

   ISessionObject* so = LoadObject(UPLOAD_OBJECT_NAME, NULL, filter.c_str());
   if (so == NULL || so->Self()->size() == 0)
   {
      std::wstring err(L"Can't find code ");
      AddAnswer(false, err + url);

      return false;
   }

   const GRServer::Object& src = *so->Self()->at(0);

   JSONReader r;
   JSONValue* v = r.Parse(W2A_CP(src[UPLOAD_DATA_FIELD]->str->c_str(), CP_UTF8));
   if (v== NULL || !v->IsObject())
   {
      delete v;
      AddAnswer(false, L"Can't read JSON");
      return false;
   }

   SessionObject* dest = Build((const std::wstring&)*(src[UPLOAD_OBJNAME_FIELD]->str), false);

   GRServer::Object* dobj = dest->AddObject();
   SetFields(dobj, *v->value.object, &formats);
   delete v;

   int idx = dest->format->FindMember(src[UPLOAD_FLDNAME_FIELD]->str->c_str());

   dobj->at(idx).binary = new MemoryBinary(new Binary(fileContent));

   dest->CreateWriter(NULL, stInternal);
   bool ret = dest->Write(true);
   dest->CloseWriter();

   FILETIME ft;
   SYSTEMTIME st;
   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &ft);

   wchar_t buf[20];
   *(__int64*)&ft -= UPLOAD_TIMEOUT;

   wsprintf(buf, L"%d%09u", (int)((*(__int64*)&ft) / 1000000000), (unsigned)((*(__int64*)&ft) % 1000000000));


   std::wstring rmvFilter(L"\"code\"='");
   rmvFilter.append(url).append(L"' or \"sended\" <= ").append(buf);

   ((SessionObject*)so)->CreateRemover(NULL, stInternal, NULL);
   so->Removing(rmvFilter.c_str());
   ((SessionObject*)so)->CloseRemover();
   
   AddAnswer(ret, dest->format->name.c_str());

   delete dest;
   return ret;
}

//
//----------------------------------------------- ServerAnswer ---------------------------------------
//
ServerAnswer::ServerAnswer(Session *session) : SessionObject(session)
{
   InitObject(ObjectDef::Get(L"ServerAnswer"));
}

void ServerAnswer::Add(bool response, const std::wstring& message)
{
   Object *o = Object::Create(*format);
   push_back(o);

   (*o)[RESPONSE_MEMBER]->number = (response) ? 1 : 0;
   (*o)[MESSAGE_MEMBER]->str->assign(message);
}


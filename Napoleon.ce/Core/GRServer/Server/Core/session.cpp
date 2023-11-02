/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Сессия
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

#include "server.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

static const int WAIT_CLIENT_TIMEOUT = 30 * 1000;

//
//------------------------------------------ Session ----------------------------------------------------
//
Session::Session(Dispatcher* _d) :
   dispatcher(_d), config(_d->Controller().Config()), curUser(NULL), ack(&formats),
   response(&formats), trash(&formats), answer(NULL), socket(NULL), curMemory(0), agents(NULL)
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
	gServer->AddLog("Memory limit exceeded (%d) while reading object %s, limit=%d", socket->GetSocket(), W2A(o.format.name.c_str()), memoryLimit);
	return false;
}

ISessionObject* Session::CreateObject(const std::wstring& objName, bool addToResponse)
{
	if (objName.compare(L"Agents") == 0)
	{
		return Agents();
	}

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
		const char *objA = W2A(so->Name().c_str());

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

	if (tck.Read(socket, WAIT_CLIENT_TIMEOUT, evStop, this) && tck.size() && tck.front()->Name().compare(SERVER_COMMAND) == 0)
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
   
   const wchar_t *start = type.c_str();
   const wchar_t *end = start + type.size();
   ParseStreamU ps(start, end);

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

DWORD Session::WriteToStream(SessionObject& so, bool format)
{
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

		DWORD curSize = outStream.Size();
      if( sendLimit != 0 && curSize > sendLimit )
      {
         // если нет данных отправляем как обычно
			if (!so->MoveNext())
				break;
     
			if (!SendStreamPart(so))
			{
				ret = false;
				break;
			}

			// записываем данные которые считали перед отправкой
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
   const char *userA = W2A(user->UserName());
   const char *objA = W2A(objName.c_str());

	dispatcher->AddLog(IErrorLogger::Full, "get object (%d) %s:%s", (int)socket->GetSocket(), objA, (filter) ? W2A(filter) : "");

   if( !user->ObjectAllowed(objName, User::oaRead) )
   {
      gServer->AddError(false, "Пользователю '%s' запрещено смотреть объект '%s'",  userA, objA);
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
            //gServer->AddError(false, "Не могу создать объект '%s'", objA);
            return NULL;
         }
         response.push_back(so);
      }

		Dispatcher::RequestSemahore();

		DWORD start = GetTickCount();
		if (so->CreateReader(filter))
      {
			DWORD cb = 0, recs = 0;
			bool writed = WriteObjectToStream(so, &cb, &recs);
			Dispatcher::ReleaseSemaphore();

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
			Dispatcher::ReleaseSemaphore();
		}
		//else
      //   gServer->AddError(false, "Не могу создать Reader для объекта '%s'", objA);
   } catch(...)
   {
		gServer->AddError(false, "Exception при чтении объекта (%d) '%s'", (int)socket->GetSocket(), objA);
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

bool Session::DoObjCommand(const Member* param)
{
   if( ack.size() < 1 ) return false;

	WriteAnswerToOutStream();
   if( ack.size() > 1 )
   {
      ExchangeList::iterator i = ack.begin();
      i++;
      for( ; i != ack.end(); i++ )
      {
         SessionObject* so = (SessionObject*)((ServObject*)(*i));
         const std::wstring& name = so->Name();
         if( name.compare(SERVER_COMMAND) == 0 )
            break;

         so->DoObjCommand((const std::wstring&)*param->str, &outStream);
      }
      ack.RemoveTo(i);
   }

   return true;
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

      RID_LIST ids;

      const char* objA = W2A(name.c_str());
      bool res = false;
		std::wstring response(so->Name());
      try
      {
			User* u = (curUser == NULL) ? user : curUser;
			if (u->ObjectAllowed(name, User::oaWrite))
         {
				ObjectDef* od = (ObjectDef*)so->GetObjectDef();
				if( od != NULL && !(od)->events.Fire(IEvent::BeforePut, this, so) )
				{
					res = false;
					response = L"Запрет записи "; response += so->Name();
				}
				else
				{
					Dispatcher::RequestSemahore();
					res = (so->CreateWriter(NULL, updateExecutable ? stCommon : stInternal) && so->Write(updateExecutable, (retIDS) ? &ids : NULL));
					Dispatcher::ReleaseSemaphore();
				}
         } else
            gServer->AddError(false, "Пользователю '%s' запрещена запись объекта '%s'", W2A(user->Name().c_str()), objA);
      } catch(...)
      {
         gServer->AddError(false, "Exception при записи объекта '%s'", objA);
      }
      try
      {
         so->CloseWriter();
      } catch(...)
      {
         gServer->AddError(false, "Exception on CloseWriter объекта '%s'", objA);
      }

      AddAnswer(res, response);
      if( res && retIDS && ids.size() > 0 )
      {
         // add ids
			WriteAnswerToOutStream();
         outStream.Append(L"RIDS[id:n]");
         RID_LIST::const_iterator i = ids.begin();
         for( ; i != ids.end(); i++ )
         {
            outStream.Append(L'[');
            outStream.Append((unsigned long)(*i), 0);
            outStream.Append(L']');
         }
      }
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
				if (u->ObjectAllowed(objName, User::oaWrite))
            {
               SessionObject* so = Build(objName, false);
               if( so != NULL )
               {
                  ret = so->Removing(param->str->substr(pos+1).c_str());
                  AddAnswer(ret, objName.c_str());
                  delete so;
               }
            }
            else 
            {
               USES_CONVERSION;
               gServer->AddLog("Not allowed remove %s ", W2A(param->str->c_str()));
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

#ifdef UNIX
Session::AckReturn Session::ReadPreviousVersion(Binary* packet)
{
   return arFail;
}
#endif

Session::AckReturn Session::ReadAck(DWORD timeout)
{
   if( socket == NULL ) return arFail;

   ack.clear();

	if (ack.Read(socket, timeout, evStop, this))
		return (ack.size()) ? arAck : arFail;

   AckReturn retVal;
   try
   {
      retVal = arFail;
   } catch(...)
   {
      gServer->AddError(false, "Exception при работе в совместимом режиме");
      retVal = arFail;
   }

   return retVal;
}

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

	// сохраняем запросы при отправки части объекта
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

		Packet* pkt = Packet::MakePacket(outStream, (outStream.IsNeedCompress()) ? GZIP_OPT : L"", formats.cryptData);
		
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
	wsprintf(buf, L"%u.%u.%u.%u", addr.sin_addr.s_net, addr.sin_addr.s_host, addr.sin_addr.s_lh, addr.sin_addr.s_impno);
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
	SessionObject *so = Build(L"ReqServerData", false);
	if (so != NULL)
	{
		TIME_ZONE_INFORMATION st;
		GetTimeZoneInformation(&st);

		Object* o = so->AddObject();
		Member* m = (*o)[L"name"];
		m->str->assign(PROJECT_NAME);

		m = (*o)[L"data"];
		m->str->assign(mgrLog.c_str());

		m = (*o)[L"tz"];
		m->number = st.Bias;

		WriteToStream(*so, true);
		delete so;
	}
}

//
//----------------------------------------------- ServerAnswer ---------------------------------------
//
ServerAnswer::ServerAnswer(Session *session) : SessionObject(session)
{
   InitObject(ObjectDef::Get(L"ServerAnswer"));
}

void ServerAnswer::Add(bool response, const std::wstring& message, const wchar_t* kind)
{
   Object *o = Object::Create(*format);
   push_back(o);

   (*o)[RESPONSE_MEMBER]->number = (response) ? 1 : 0;
   (*o)[MESSAGE_MEMBER]->str->assign(message);
   if(kind != NULL && *kind != 0)
      (*o)[KIND_MEMBER]->str->assign(kind);
}


/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Источник сервера
 *
 * ert   21/03/2012   creating
 */ 
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "objects.h"
#include "serversrc.h"
#include "dispatcher.h"

using namespace GRServer;

class ServerReader : public IDataSource::IReader
{
public:
   ServerReader(Format* format, const Session& session);

   virtual bool MoveNext(Object *parentObject)
   { 
      if( readed )
         return false;

      readed = true;
      return true;
   }

   virtual bool Get(Object* o) const;
   virtual void Remove() {}
   virtual void Close() {}

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
   bool readed;
   int iCurDate, iUserID;
   std::wstring userid;
};

class ActiveUsersReader : public IDataSource::IReader
{
public:
	ActiveUsersReader(Format* format);

	virtual bool MoveNext(Object *parentObject) { return iDuration >= 0 && iUserID >= 0 && iIP >= 0 && iExcl >= 0 && current != users.end(); }

	virtual bool Get(Object* o) const;
	virtual void Remove() {}
	virtual void Close() {}

	virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
	virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
	std::vector<ActiveUsersData> users;
	mutable std::vector<ActiveUsersData>::const_iterator current;

	int iUserID, iDuration, iIP, iExcl;
};

ServerReader::ServerReader(Format* format, const Session& session) : readed(false)
{
   iCurDate = format->FindMember(L"curdate");
   iUserID = format->FindMember(L"userid");

   userid = session.GetUser().ID();
}

bool ServerReader::Get(Object* o) const
{
   if( iCurDate >= 0 && iUserID >= 0 )
   {
      SYSTEMTIME st;
      FILETIME ft;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft);

      o->at(iCurDate).datetime = ft;
      o->at(iUserID).str->assign(userid);

      return true;
   }
   return false;
}

ActiveUsersReader::ActiveUsersReader(Format* format)
{
	((Dispatcher*)gServer)->GetActiveUsers(&users);

	current = users.begin();

	iDuration = format->FindMember(L"duration");
	iUserID = format->FindMember(L"userid");
	iIP = format->FindMember(L"ip");
	iExcl = format->FindMember(L"isExclusive");
}

bool ActiveUsersReader::Get(Object* o) const
{
	if (current != users.end())
	{
		o->at(iDuration).number= current->duration;
		o->at(iUserID).str->assign(current->userid);
		o->at(iIP).str->assign(current->ip);
		o->at(iExcl).number = current->isExclusive;

		current++;
		return true;
	}
	return false;
}

IDataSource::IReader* ServerSourceCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   return new ServerReader(((SessionObject*)object.Self())->format, (Session&)object.GetSession());
}


IDataSource::IReader* ActiveUsersCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
	return new ActiveUsersReader(((SessionObject*)object.Self())->format);
}

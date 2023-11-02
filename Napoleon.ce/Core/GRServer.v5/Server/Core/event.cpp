/*
 * Copyright (C), 2009-2010, ����� �������
 *
 * Event & Action impl
 *
 * ert   10/03/2010   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "event.h"
#include "parse.h"
#include "server.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

Event::Event(Type t, const std::vector<Data> &d) : type(t), data(d)
{
}

Event::~Event()
{
}

bool Event::Fire(Session *session, SessionObject *object)
{
	mutex.Init();

	// if (object->NeedDebug())
	//	gServer->AddLog(IErrorLogger::Full, "fired.size = %d", fired.size());

	std::set<Session *>::const_iterator fnd = fired.find(session);
	if (fnd != fired.end())
	{
		// if (object->NeedDebug())
		//	gServer->AddLog(IErrorLogger::Full, "find event");
		return true;
	}

	if (mutex.Acquire(1000))
	{
		fired.insert(session);
		mutex.Release();
	}

	bool ret = true;

	try
	{
		std::vector<Data>::iterator i = data.begin();
		// for (; ret && i != data.end(); i++)
		for (; i != data.end(); i++)
		{
			if (i->action.empty())
			{
				Token ret;
				session->Parse(&ret, i->param, object);
				continue;
			}

			std::vector<Token> params;

			ParamResolver pr(&params, session, object);
			if (pr.Do(i->param))
			{
				Action *a = Action::Get(i->action);
				if (a == NULL)
				{
					USES_CONVERSION;
					gServer->AddError(false, "��� action %s", W2A_CP(i->action.c_str(), CP_UTF8));
				}
				else
				{
					if (!a->Do(session, object, params))
						ret = false;
					if (object->NeedDebug())
						gServer->AddLog(IErrorLogger::Full, "do action (%s)", ret ? "true" : "false");
				}
			}
			else
			{
				ret = false;
				if (object->NeedDebug())
				{
					USES_CONVERSION;
					gServer->AddLog(IErrorLogger::Full, "action error param resolve %s", W2A_CP(i->param.c_str(), CP_UTF8));
				}
			}
		}
	}
	catch (...)
	{
	}

	if (mutex.Acquire(1000))
	{
		fnd = fired.find(session);
		if (fnd != fired.end())
			fired.erase(fnd);
		mutex.Release();
	}

	// if (object->NeedDebug())
	//	gServer->AddLog(IErrorLogger::Full, "leave event (%d)", fired.size());
	return ret;
}

void Event::AddData(const std::vector<Data> &src)
{
	std::vector<Data>::const_iterator i = src.begin();
	for (; i != src.end(); i++)
		data.push_back(*i);
}

Event::Type Event::EventTypeFromString(const std::wstring &type)
{
	if (type.compare(L"login") == 0)
		return Login;
	if (type.compare(L"failLogin") == 0)
		return FailLogin;
	if (type.compare(L"error") == 0)
		return Error;
	if (type.compare(L"warning") == 0)
		return Warning;
	if (type.compare(L"get") == 0)
		return Get;
	if (type.compare(L"remove") == 0)
		return Remove;
	if (type.compare(L"userChange") == 0)
		return Impersonate;
	if (type.compare(L"writeCommit") == 0)
		return WriteCommit;
	if (type.compare(L"beforePut") == 0)
		return BeforePut;
	if (type.compare(L"readCommit") == 0)
		return ReadCommit;
	if (type.compare(L"resolveObjects") == 0)
		return ResolveObjects;
	if (type.compare(L"beforeRead") == 0)
		return BeforeRead;
	if (type.compare(L"onLoad") == 0)
		return OnLoad;

	return Put;
}

EventList::EventList()
{
}

bool EventList::Fire(Event::Type type, Session *session, SessionObject *object)
{
	iterator i = begin();

	for (; i != end(); i++)
	{
		if (i->GetType() == type)
		{
			return i->Fire(session, object);
		}
	}

	return true;
}

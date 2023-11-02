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
#include <mutex_t.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class PriceDataHolder : public ISession::IHandler
{
public:
	struct PriceData
	{
		int folderID;
		int order;
		std::wstring fid;
	};
	typedef std::map<std::wstring, PriceData> PriceDataMap;
	typedef std::map<ISession*, PriceDataMap*> SessionData;

	PriceDataHolder()
	{
		mutex.Init();
	}

	virtual void SessionClosed(ISession *s)
	{
		if (mutex.Acquire(1000))
		{
			SessionData::iterator fnd = data.find(s);
			if (fnd != data.end())
			{
				delete fnd->second;
				data.erase(fnd);
			}
			mutex.Release();
		}
	}

	PriceDataMap* GetData(ISession* s);


private:
	SessionData data;
	Mutex mutex;
};
static PriceDataHolder prcHolder;

class GetFolderID : public IFunction
{
public:
	virtual const wchar_t* Name() const { return L"GetPriceFolderID"; }
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

};

class GetFolderFID : public IFunction
{
public:
	virtual const wchar_t* Name() const { return L"GetPriceFolderFID"; }
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

};

class GetPriceOrder : public IFunction
{
public:
	virtual const wchar_t* Name() const { return L"GetPriceOrder"; }
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

};

static GetFolderID getFolderID;
static GetFolderFID getFolderFID;
static GetPriceOrder getPriceOrder;

bool GetFolderID::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
	int res = 0;
	if (params.size() > 0)
	{
		const Token& p = params[0];
		PriceDataHolder::PriceDataMap* pdm = prcHolder.GetData(session);
		if (p.type == Token::ttString)
		{
			PriceDataHolder::PriceDataMap::const_iterator pfnd = pdm->find(*p.value.str);
			if (pfnd != pdm->end())
				res = pfnd->second.folderID;
		}
	}

	*result = res;
	return true;
}

bool GetFolderFID::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
	std::wstring res;
	if (params.size() > 0)
	{
		const Token& p = params[0];
		PriceDataHolder::PriceDataMap* pdm = prcHolder.GetData(session);
		if (p.type == Token::ttString)
		{
			PriceDataHolder::PriceDataMap::const_iterator pfnd = pdm->find(*p.value.str);
			if (pfnd != pdm->end())
				res = pfnd->second.fid;
		}
	}

	*result = res;
	return true;
}

bool GetPriceOrder::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
	int res = -1;
	if (params.size() > 0)
	{
		const Token& p = params[0];
		PriceDataHolder::PriceDataMap* pdm = prcHolder.GetData(session);
		if (p.type == Token::ttString)
		{
			PriceDataHolder::PriceDataMap::const_iterator pfnd = pdm->find(*p.value.str);
			if (pfnd != pdm->end())
				res = pfnd->second.order;
		}
	}

	*result = res;
	return true;
}

PriceDataHolder::PriceDataMap* PriceDataHolder::GetData(ISession *s)
{
	SessionData::iterator fnd = data.find(s);
	if (fnd == data.end())
	{
		s->AddHandler(this);
		ISessionObject* so = s->LoadObject(L"PriceFolderOrder", NULL);
		if (so != NULL)
		{
			PriceDataMap* pd = new PriceDataMap();

			ServObject* obj = so->Self();
			GRServer::Format* fmt = obj->format;
			int ididx = fmt->FindMember(L"id");
			int fididx = fmt->FindMember(L"folderID");
			int ordidx = fmt->FindMember(L"ord");
			int fldidx = fmt->FindMember(L"fid");

			if (ididx >= 0 && fididx >= 0 && ordidx >= 0 && fldidx >= 0)
			{
				ServObject::const_iterator i = obj->begin();
				for (; i != obj->end(); i++)
				{
					const Object& src = *(*i);

					PriceData pdata;
					pdata.folderID = (int)(src.at(fididx).number + 0.05);
					pdata.order = (int)(src.at(ordidx).number + 0.05);
					pdata.fid.assign((const std::wstring&)*src.at(fldidx).str);

					(*pd)[(const std::wstring&)*src.at(ididx).str] = pdata;
				}
			}
			fnd = data.insert(SessionData::value_type(s, pd)).first;
		}
	}

	return fnd->second;
}

bool GRServer::AddOnInit()
{
	AddFunction(&getFolderID);
	AddFunction(&getFolderFID);
	AddFunction(&getPriceOrder);
   return true;
}


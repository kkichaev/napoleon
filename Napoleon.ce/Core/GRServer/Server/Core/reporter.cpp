/*
* Copyright (C), 2009 - 2010, Денис Мосягин
*
*
* ert   30/03/2010   creating
*/
#include "stdafx.h"
#include "reporter.h"
#include "session.h"

using namespace GRServer;


class ReportReader : public IDataSource::IReader
{
public:
	ReportReader(const std::wstring& reportName, SessionObject* so);

	virtual bool MoveNext(Object *parentObject);
	virtual bool Get(Object* o) const { return false; }

	virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) {
		this->filter = filter;
		return true;
	}

	virtual void Remove() {}
	virtual void Close() {}

	virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
	virtual const Member* Value(const wchar_t* name) const { return NULL; }

private:
	std::wstring reportName;
	std::wstring filter;

	SessionObject* obj;
	bool inited;
};

ReportReader::ReportReader(const std::wstring& _reportName, SessionObject* so) :
	reportName(_reportName),
	obj(so)
{
	inited = false;
}

bool ReportReader::MoveNext(Object *parentObject)
{
	if (inited)
		return false;

	inited = true;
	Session& session = (Session&)obj->GetSession();
	SessionObject* so = session.Build(L"%ReportSourceParam", true);
	int cndi = so->format->FindMember(L"condition");
	int obji = so->format->FindMember(L"objectName");

	Object* o = so->AddObject();
	if (cndi >= 0)
		o->at(cndi).str->assign(filter);
	if (obji >= 0)
		o->at(obji).str->assign(obj->Name());

	// мы не можем удалить весь коммандный объект, т.к. он может содрежать много команд
	ExchangeList* ack = session.Ack();
	// первый объект удаляется в Reporter (это параметр)
	ack->insert(ack->begin(), so);
	// второй - фейковый объект
	ack->insert(ack->begin(), so);

	Member m;
	m.str = new CString(reportName);

	session.HandleCommand(GET_REPORT, &m);

	// удаляем фековый объект
	ack->front() = NULL;
	ack->erase(ack->begin());

	return obj->size() == 0 ? false: obj->MoveNext();
}

IDataSource::IReader* ReporterCreater::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
	std::wstring report;
	const Parameter *prm = parameters.Find(L"report", 0);
	if (prm == NULL)
		return NULL;

	Session& s = (Session&)iobject.GetSession();
	SessionObject* object = (SessionObject*)iobject.Self();
	s.Parse(&report, prm->value, object);

	if (report.empty())
		return NULL;

	return new ReportReader(report, object);
}
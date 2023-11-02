#include "stdafx.h"
#include "postgre.h"

#include <server/catalog/pg_type_d.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;


//
//-------------------------------------- KeyMember ----------------------------------------------
//
class StringMember : public KeyMember
{
public:
	StringMember(int index) : KeyMember(index) {}

	virtual KeyMember* Clone() const { return new StringMember(index); }
	virtual void Load(const Object& src) { value.assign(*src.at(index).str); }
	virtual bool IsEqual(const KeyMember& _src) const { return (value.compare(((const StringMember&)_src).value) == 0); }

protected:
	CString value;
};

class NumberMember : public KeyMember
{
public:
	NumberMember(int index) : KeyMember(index) {}

	virtual KeyMember* Clone() const { return new NumberMember(index); }
	virtual void Load(const Object& src) { value = src.at(index).number; }
	virtual bool IsEqual(const KeyMember& _src) const { return (value == ((const NumberMember&)_src).value); }

protected:
	double value;
};

class DateTimeMember : public KeyMember
{
public:
	DateTimeMember(int index) : KeyMember(index) {}

	virtual KeyMember* Clone() const { return new DateTimeMember(index); }
	virtual void Load(const Object& src) { value = src.at(index).datetime; }
	virtual bool IsEqual(const KeyMember& _src) const { return (CompareFileTime(&value, &((const DateTimeMember&)_src).value) == 0); }

protected:
	FILETIME value;
};

KeyMember* KeyMember::Create(const std::wstring& name, Format* format)
{
	KeyMember* ret = NULL;
	int idx = format->FindMember(name.c_str());
	if (idx >= 0)
	{
		MemberFormat& mf = format->at(idx);
		switch (mf.type)
		{
		case MemberFormat::mtDateTime:
			ret = new DateTimeMember(idx);
			break;
		case MemberFormat::mtNumber:
			ret = new NumberMember(idx);
			break;
		case MemberFormat::mtString:
			ret = new StringMember(idx);
			break;
		}
	}
	return ret;
}

//
//-------------------------------------- KeyHolder ----------------------------------------------
//
KeyHolder::KeyHolder(const std::wstring& keyFields, const ISessionObject& object)
{
	Format* format = object.Self()->format;

	wstring::const_iterator si = keyFields.begin(), ei = keyFields.end();
	if (*si == L'"') si++;

	wstring f;
	for (; si != ei; si++)
	{
		wchar_t sym = *si;

		if (sym == L'"') break;
		if (sym == L',')
		{
			if (!f.empty())
			{
				size_t start = f.find_first_not_of(L' ');
				size_t end = f.find_last_not_of(L' ');
				KeyMember* km = KeyMember::Create(f.substr(start, end - start + 1), format);
				if (km)
					keys.push_back(km);
			}
			f.clear();
		}
		else
			f.append(1, sym);
	}
	if (!f.empty())
	{
		size_t start = f.find_first_not_of(L' ');
		size_t end = f.find_last_not_of(L' ');
		KeyMember* km = KeyMember::Create(f.substr(start, end - start + 1), format);
		if (km)
			keys.push_back(km);
	}
}

KeyHolder::KeyHolder(const KeyHolder& src)
{
	vector<KeyMember*>::const_iterator i = src.keys.begin();
	for (; i != src.keys.end(); i++)
		keys.push_back((*i)->Clone());
}

KeyHolder::~KeyHolder()
{
	vector<KeyMember*>::iterator i = keys.begin();
	for (; i != keys.end(); i++)
		delete (*i);
}

void KeyHolder::Load(const Object& object)
{
	vector<KeyMember*>::iterator i = keys.begin();
	for (; i != keys.end(); i++)
		(*i)->Load(object);
}

bool KeyHolder::operator != (const KeyHolder& src) const
{
	bool ret = true;

	vector<KeyMember*>::const_iterator si = keys.begin(), di = src.keys.begin();
	for (; ret && si != keys.end(); si++, di++)
		ret = (*si)->IsEqual(*(*di));

	return !ret;
}

//
//-------------------------------------- QueryChildReader ----------------------------------------------
//
QueryChildReader::QueryChildReader(const CString& keyFields, const ISessionObject& object, const ISessionObject& _parent) :
	QueryReader(object), keyHolder((const std::wstring&)keyFields, _parent), parent(NULL), keyLoaded(false), parentHaveNextObject(false)
{
	ObjectSource* os = _parent.GetSource();
	if (os != NULL && os->reader && os->readerName.compare(QuerySourceCreator().Name()) == 0)
		parent = (QueryReader*)os->reader;

	if (parent != NULL)
		parent->AddChildObject(&object);
}

QueryChildReader::~QueryChildReader()
{
}

bool QueryChildReader::MoveNext(Object* parentObject)
{
	if (parentObject == NULL)
		return false;

	if (parentHaveNextObject)
	{
		parentHaveNextObject = false;
		keyLoaded = false;
		return false;
	}

	bool ret = false;
	if (!keyLoaded)
	{
		keyHolder.Load(*parentObject);
		ret = true;
	}
	else
	{
		if (this->nextObject != NULL)
			return true;

		Object* nextObject = parent->GetNext();
		if (nextObject != NULL)
		{
			KeyHolder nextKey(keyHolder);
			nextKey.Load(*nextObject);

			ret = (keyHolder == nextKey);
		}
	}
	keyLoaded = ret;
	return ret;
}

Object* QueryChildReader::GetNext()
{
	if (parent == NULL)
		return NULL;

	Object* no = parent->GetNext();
	if (no != NULL)
	{
		KeyHolder nextKey(keyHolder);
		nextKey.Load(*no);

		if (keyHolder == nextKey)
		{
			if (nextObject == NULL)
				nextObject = Create(*object.Self()->format);
			parent->Get(nextObject);
			return nextObject;
		}
		else
		{
			parentHaveNextObject = true;
			delete nextObject;
			nextObject = NULL;
			//keyLoaded = false;
		}
	}
	return NULL;
}

bool QueryChildReader::Get(Object* o) const
{
	if (nextObject != NULL && &nextObject->format == &o->format)
	{
		nextObject->MoveTo(o);
		delete nextObject;
		nextObject = NULL;
		return true;
	}
	return parent->Get(o);
}


QueryBinder::QueryBinder()
{

}

static FieldBinder* CreateBinder(const wchar_t* name, int bindPos, const std::vector<const ISessionObject*>& objects, const MemberFormat** elFormat)
{
	std::vector<const ISessionObject*>::const_iterator obj = objects.begin();
	for (; obj != objects.end(); obj++)
	{
		const IObjectData* od = (*obj)->GetObjectDef();
		if (od == NULL)
			continue;

		IObjectData::Fields::const_iterator fi = od->fields.begin();
		for (; fi != od->fields.end(); fi++)
		{
			if (_wcsicmp(name, fi->data.c_str()) == 0)
			{
				GRServer::Format* format = (*obj)->Self()->format;
				int fldIndex = format->FindMember(fi->format.name.c_str());
				if (fldIndex >= 0)
				{
					const MemberFormat& mf = format->at(fldIndex);
					*elFormat = &mf;
					return FieldBinder::Create(*fi, fldIndex, bindPos);
				}
			}
		}
	}
	return NULL;
}

//static inline bool IsInt(Oid val) { return val == BOOLOID || val == INT8OID || val == INT2OID || val == INT4OID; }
//static inline bool IsText(Oid val) { return val == CHAROID || val == TEXTOID || val == VARCHAROID; }
//static inline bool IsReal(Oid val) { return val == FLOAT4OID || val == FLOAT8OID; }

bool QueryBinder::Prepare(PGresult* res, const std::vector<const ISessionObject*>& objects)
{
	USES_CONVERSION;

	int nColumns = PQnfields(res);
	for (int i = 0; i < nColumns; i++)
	{
		const wchar_t* name = A2W_CP(PQfname(res, i), CP_UTF8);

		const MemberFormat* format = NULL;
		FieldBinder* ret = CreateBinder(name, i, objects, &format);
		//if (ret == NULL)
		//{
		//	Oid tp = PQftype(res, i);
		//	IObjectData::Field field;
		//	field.data = name;
		//	field.flags = 0;
		//	field.width = 0;
		//	field.format.name = name;
		//	field.format.format.fraction = 0;

		//	if (IsInt(tp))
		//		field.format.type = MemberFormat::mtNumber;
		//	else if (IsReal(tp))
		//	{
		//		field.format.type = MemberFormat::mtNumber;
		//		field.format.format.fraction = 8;
		//	}
		//	else if (IsText(tp))
		//	{
		//		field.format.type = MemberFormat::mtString;
		//		field.width = DEFAULT_STRING_LENGTH;
		//	}
		//	else
		//	{
		//		continue;
		//	}
		//	ret = FieldBinder::Create(field, -1, i);
		//	if (ret != NULL)
		//	{
		//		customFormats.push_back(field.format);
		//		format = &(customFormats.back());
		//	}
		//}

		if (ret != NULL)
		{
			fields.push_back(ret);
			formats.push_back(format);
		}
	}

	return fields.size() > 0;
}

bool QueryBinder::Read(Object* o, const PGresult* res, int curRow) const
{
	bool ret = true;

	//
	// При чтении надо проверять форматы чтобы записать в корректный объект. fields - содержит связи со всеми child объектами тоже
	//
	const Format& fmt = o->GetFormat();
	Format::const_iterator dfi = fmt.begin();
	for (; dfi != fmt.end(); dfi++)
	{
		const MemberFormat* elFormat = &(*dfi);
		FieldBinder* fb = GetBinder(elFormat);
		if (fb)
			fb->Read(&o->at(fb->fieldIndex), res, curRow);
	}

	// файлы читаем те, которые соответствуют формату
	const ObjFiles::const_iterator fnd = objFiles.find((Format*)&fmt);
	if (fnd != objFiles.end())
	{
		std::vector<FileField*>::const_iterator fi = fnd->second.begin();
		for (; fi != fnd->second.end(); fi++)
			if (!(*fi)->ReadFile(o))
				gServer->AddError(false, "Error while reading file");
	}
	return ret;
}

FieldBinder* QueryBinder::GetBinder(const MemberFormat* format) const
{
	std::vector<FieldBinder*>::const_iterator i = fields.begin();
	std::vector<const MemberFormat*>::const_iterator sfi = formats.begin();
	for (; i != fields.end(); i++, sfi++)
	{
		if (format == (*sfi))
		{
			return (*i);
		}
	}

	return NULL;
}

QueryReader::QueryReader(PGconn* conn, const CString& _stmt
	, const ISessionObject& _object, bool _debug, int _rowCount, ParamHelper* defaults) :
	object(_object)
	,connection(conn)
	,params(defaults)
	,rowCount(_rowCount)
	,curRow(-1)
	,nextObject(NULL)
	,result(NULL)
	,stmt(_stmt)
{
}

QueryReader::QueryReader(const ISessionObject& _object) : 
	object(_object)
	,nextObject(NULL)
	,rowCount(0)
	,curRow(-1)
	,params(NULL)
	,connection(NULL)
	,result(NULL)
{

}

void QueryReader::Close()
{
	delete nextObject;
	nextObject = NULL;

	if(result != NULL)
		PQclear(result);
	result = NULL;
}

bool QueryReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
	DWORD cbParams = sizeof(L"PARAMS:") - sizeof(wchar_t);
	if (filter != NULL && memcmp(filter, L"PARAMS:", cbParams) == 0)
	{
		filter = (const wchar_t*)(filter + cbParams / sizeof(wchar_t));
	}
	params.Read((filter == NULL) ? L"" : filter, &object.GetSession(), &object, gServer);
	return true;
}


bool QueryReader::Open()
{
	USES_CONVERSION;

	std::vector<const ISessionObject*> objects(childs);
	objects.insert(objects.begin(), &object);

	CString* sres = params.Substitute(stmt.c_str());
	stmt.clear();
	object.PrepareFilterStr(&stmt, (const std::wstring&)*sres);
	delete sres;

	std::string query = W2A_CP(stmt.c_str(), CP_UTF8);

	if (debug)
	{
		USES_CONVERSION;
		gServer->AddLog(IErrorLogger::Full, "Do Query: %s", query.c_str());
	}

	result = PQexecParams(connection, query.c_str(), 0, NULL, NULL, NULL, NULL, 1);
	if (PQresultStatus(result) != PGRES_TUPLES_OK)
	{
		AddErrorToLog("", result);
		gServer->AddError(false, "stmt %s", query.c_str());
	}

	if (PQntuples(result) == 0)
		return false;

	return binder.Prepare(result, objects);
}

bool QueryReader::MoveNext(Object* parentObject)
{
	if (result == NULL)
	{
		if (!Open())
			return false;
	}

	if (nextObject != NULL)
		return true;

	if (rowCount > 0 && curRow >= rowCount - 1)
		return false;

	return ++curRow < PQntuples(result);
}

bool QueryReader::Get(Object* o) const
{
	if (nextObject != NULL && &nextObject->format == &o->format)
	{
		nextObject->MoveTo(o);
		delete nextObject;
		nextObject = NULL;
		return true;
	}
	return binder.Read(o, result, curRow);
}

Object* QueryReader::GetNext()
{
	if ((curRow + 1) >= PQntuples(result))
	{
		delete nextObject;
		nextObject = NULL;
		return NULL;
	}
	curRow++;

	if (nextObject == NULL)
		nextObject = Create(*object.Self()->format);
	binder.Read(nextObject, result, curRow);
	return nextObject;
}


IDataSource::IReader* QuerySourceCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   PGconn* conn = GetConnection(object);
   if (conn == NULL)
      return NULL;

	IDataSource::IReader* ret = NULL;

	if (object.Parent() != NULL)
	{
		const Parameter* p = parameters.Find(L"keyFields", -1);
		if (p == NULL)
		{
			gServer->AddError(false, "SQLQuery нет параметра keyFields");
			return NULL;
		}
		CString* keyFields = NULL;
		if (!object.GetSession().Parse(&keyFields, p->value, &object))
		{
			gServer->AddError(false, "SQLQuery не правильный параметр keyFields");
			delete keyFields;
			return NULL;
		}

		ret = new QueryChildReader(*keyFields, object, *object.Parent());
		delete keyFields;
	}
	else
	{
		const Parameter* p = parameters.Find(L"stmt", -1);
		if (p == NULL)
		{
			gServer->AddError(false, "SQLQuery нет параметра stmt");
			return NULL;
		}
		CString* stmt = NULL;
		if (!object.GetSession().Parse(&stmt, p->value, &object))
		{
			gServer->AddError(false, "SQLQuery не правильный параметр stmt");
			delete stmt;
			return NULL;
		}

		p = parameters.Find(L"debug", -1);
		bool debug = (p != NULL);

		int rowCount = 0;
		p = parameters.Find(L"rowCount", -1);
		if (p != NULL)
		{
			CString* rc = NULL;
			if (object.GetSession().Parse(&rc, p->value, &object))
				rowCount = _wtoi(rc->c_str());

			delete rc;
		}

		ParamHelper* defaults = new ParamHelper(NULL);
		defaults->Read(parameters, &object.GetSession(), &object, gServer);
		ret = new QueryReader(conn, *stmt, object, debug, rowCount, defaults);
		delete stmt;
	}

	return ret;
}
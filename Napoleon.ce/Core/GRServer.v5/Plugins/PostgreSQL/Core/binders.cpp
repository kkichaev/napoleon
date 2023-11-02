#include "stdafx.h"
#include "postgre.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

#include <server/catalog/pg_type_d.h>


static void NumberFromInt(Member* m, const char* ptr, int len)
{
	m->number = (len == 2) ? (short)ntohs(*(short*)ptr) :
		(len == 4) ? (int)ntohl(*(int*)ptr) :
		(double)ntohll(*(__int64*)ptr);
}

static double inline MakeFloat(const char* ptr, int len)
{
	if (len == 4)
	{
		int val = (int)ntohl(*(int*)ptr);
		return *(float*)&val;
	}
	else
	{
		__int64 val = ntohll(*(__int64*)ptr);
		return *(double*)&val;
	}
}

static void NumberFromFloat(Member* m, const char* ptr, int len)
{
	m->number = MakeFloat(ptr, len);
}

static void NumberFromString(Member* m, const char* ptr, int len)
{
	char* ep;
	m->number = std::strtod(ptr, &ep);
}

static void FiletimeFromInt(Member* m, const char* ptr, int len)
{
	*(__int64*)&m->datetime= (len == 8) ? ntohll(*(__int64*)ptr) : 0;
}

static void EmptyConvertor(Member* m, const char* ptr, int len)
{
}

static void StringFromInt(Member* m, const char* ptr, int len)
{
	__int64 val = (len == 2) ? (short)ntohs(*(short*)ptr) :
		(len == 4) ? (int)ntohl(*(int*)ptr) :
		ntohll(*(__int64*)ptr);

	m->str->assign(to_wstring(val));
}

static void StringFromFloat(Member* m, const char* ptr, int len)
{
	double val = MakeFloat(ptr, len);
	m->str->assign(to_wstring(val));
}

static void StringFromString(Member* m, const char* ptr, int len)
{
	USES_CONVERSION;
	m->str->assign(A2W_CP(ptr, CP_UTF8));
}



class IntBinder : public FieldBinder
{
public:
	IntBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
		FieldBinder(src, _fieldIndex, _columnIndex) {}

	virtual bool Read(Member* m, const PGresult* res, int curRow) const
	{
		if (convertor == NULL)
		{
			convertor = EmptyConvertor;

			switch (PQftype(res, columnIndex))
			{
			case INT8OID:
			case INT2OID:
			case INT4OID:
				convertor = NumberFromInt;
				break;
			case FLOAT8OID:
			case FLOAT4OID:
				convertor = NumberFromFloat;
				break;
			case CHAROID:
			case TEXTOID:
			case VARCHAROID:
				convertor = NumberFromString;
				break;
			}
		}

		int len = PQgetlength(res, curRow, columnIndex);
		const char* ptr = PQgetvalue(res, curRow, columnIndex);
		if (ptr == NULL)
			m->number = 0;
		else
		{
			convertor(m, ptr, len);
		}

		return true;
	}

	virtual Oid Type() const { return INT8OID; }
	//virtual const char* TypeHint() const { return "::bigint"; }
	virtual FieldBinder::Value ParamValue(const Member& m) const
	{
		buffer = htonll((__int64)m.number);
		return Value((const char*)&buffer, sizeof(buffer), 1);
	}

private:
	mutable __int64 buffer;
};


class FloatBinder : public FieldBinder
{
public:
	FloatBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
		FieldBinder(src, _fieldIndex, _columnIndex) {}

	virtual bool Read(Member* m, const PGresult* res, int curRow) const
	{
		if (convertor == NULL)
		{
			convertor = EmptyConvertor;

			switch (PQftype(res, columnIndex))
			{
			case INT8OID:
			case INT2OID:
			case INT4OID:
				convertor = NumberFromInt;
				break;
			case FLOAT8OID:
			case FLOAT4OID:
				convertor = NumberFromFloat;
				break;
			case CHAROID:
			case TEXTOID:
			case VARCHAROID:
				convertor = NumberFromString;
				break;
			}
		}

		int len = PQgetlength(res, curRow, columnIndex);
		const char* ptr = PQgetvalue(res, curRow, columnIndex);
		if (ptr == NULL)
			m->number = 0;
		else
		{
			convertor(m, ptr, len);
		}

		return true;
	}

	virtual Oid Type() const { return FLOAT8OID; }
	//virtual const char* TypeHint() const { return "::double precision"; }
	virtual FieldBinder::Value ParamValue(const Member& m) const
	{
		buffer = htonll(*(__int64*)&m.number);
		return Value((const char*)&buffer, sizeof(buffer), 1);
	}

private:
	mutable __int64 buffer;
};

class DateBinder : public FieldBinder
{
public:
	DateBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
		FieldBinder(src, _fieldIndex, _columnIndex) {}

	virtual bool Read(Member* m, const PGresult* res, int curRow) const
	{
		switch (PQftype(res, columnIndex))
		{
		case INT8OID:
			convertor = FiletimeFromInt;
			break;
		}

		int len = PQgetlength(res, curRow, columnIndex);
		const char* ptr = PQgetvalue(res, curRow, columnIndex);
		if (ptr == NULL)
			*(__int64*)&m->datetime = 0;
		else
		{
			convertor(m, ptr, len);

			// use local timezone
			tzset();
			*(__int64*)&m.datetime += (__int64)timezone * 10000000;
		}

		return true;
	}
	virtual Oid Type() const { return INT8OID; }
	virtual const char* TypeHint() const { return "::bigint"; }
	virtual FieldBinder::Value ParamValue(const Member& m) const
	{
		// move to local time zone
		__int64 val = *(__int64*)&m.datetime;
		tzset();
		val -= (sqlite3_int64)timezone * 10000000;
		buffer = htonll(val);

		return Value((const char*)&buffer, sizeof(buffer), 1);
	}

private:
	mutable __int64 buffer;
};

class BinaryBinder : public FieldBinder
{
public:
	BinaryBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
		FieldBinder(src, _fieldIndex, _columnIndex) {}

	virtual bool Read(Member* m, const PGresult* res, int curRow) const
	{
		int len = PQgetlength(res, curRow, columnIndex);
		const char* ptr = PQgetvalue(res, curRow, columnIndex);
		m->binary = new MemoryBinary();

		if (ptr != NULL && len > 0)
		{
			Binary* b = new Binary();
			BYTE* pb = b->Alloc(len);
			memcpy(pb, ptr, len);
			m->binary->Assign(b);
		}

		return true;
	}

	virtual Oid Type() const { return BYTEAOID; }
	//virtual const char* TypeHint() const { return "::bytea"; }
	virtual FieldBinder::Value ParamValue(const Member& m) const
	{
		DWORD size = m.binary == NULL ? 0 : m.binary->Size();

		return Value((size > 0) ? (const char*)m.binary->Bytes() : NULL, size, 1);
	}
};


class StringBinder : public FieldBinder
{
public:
	StringBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
		FieldBinder(src, _fieldIndex, _columnIndex) {}

	virtual bool Read(Member* m, const PGresult* res, int curRow) const
	{
		switch (PQftype(res, columnIndex))
		{
		case INT8OID:
		case INT2OID:
		case INT4OID:
			convertor = StringFromInt;
			break;
		case FLOAT8OID:
		case FLOAT4OID:
			convertor = StringFromFloat;
			break;
		case CHAROID:
		case TEXTOID:
		case VARCHAROID:
			convertor = StringFromString;
			break;
		}

		int len = PQgetlength(res, curRow, columnIndex);
		const char* ptr = PQgetvalue(res, curRow, columnIndex);
		if (ptr != NULL && len > 0)
		{
			convertor(m, ptr, len);
		}

		return true;
	}

	virtual Oid Type() const { return VARCHAROID; }
	//virtual const char* TypeHint() const { return ""; }
	virtual FieldBinder::Value ParamValue(const Member & m) const
	{
		USES_CONVERSION;
		value = W2A_CP(m.str->c_str(), CP_UTF8);

		return Value(value.c_str(), (int)value.size(), 0);
	}
private:
	mutable std::string value;
};

static IObjectData::Field ordField;
class OrderFieldBinder : public IntBinder
{
public:
	OrderFieldBinder(int _columnIndex, int *rouwCount);

	virtual bool Read(Member* m, const PGresult* res, int curRow) const { return true; }
	virtual FieldBinder::Value ParamValue(const Member& m) const;

private:
	int* rowCount;
};

OrderFieldBinder::OrderFieldBinder(int _columnIndex, int* _rowCount) :
	IntBinder(ordField, 0, _columnIndex)
	,rowCount(_rowCount)
{
	if (ordField.format.name.empty())
	{
		ordField.format.name = ORDERED_FIELD;
		ordField.format.type = MemberFormat::mtNumber;
		ordField.format.format.fraction = 0;
	}
}

FieldBinder::Value OrderFieldBinder::ParamValue(const Member& m) const
{
	Member mt;
	mt.number = *rowCount;
	return IntBinder::ParamValue(mt);
}

FieldBinder* FieldBinder::Create(const IObjectData::Field& src, size_t fieldIndex, int columnIndex)
{
	switch (src.format.type)
	{
	case MemberFormat::mtNumber:
		return src.format.format.fraction == 0 ? (FieldBinder*)new IntBinder(src, fieldIndex, columnIndex) :
			(FieldBinder*)new FloatBinder(src, fieldIndex, columnIndex);

	case MemberFormat::mtDateTime:
		return new DateBinder(src, fieldIndex, columnIndex);

	case MemberFormat::mtBinary:
		return new BinaryBinder(src, fieldIndex, columnIndex);

	case MemberFormat::mtString:
		return new StringBinder(src, fieldIndex, columnIndex);
	}

	return NULL;
}

FieldBinder* FieldBinder::CreateOrderBinder(int columnIndex, int* rowCount)
{
	return new OrderFieldBinder(columnIndex, rowCount);
}

FieldBinder::FieldBinder(const IObjectData::Field& src, size_t _fieldIndex, int _columnIndex) :
	format(src.format)
	, fieldIndex(_fieldIndex)
	, columnIndex(_columnIndex)
	, convertor(NULL)
{

}

void ReadBinder::Close()
{
	BinderList::iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		delete (*i);
	}
	fields.clear();
}

static void PrepareFields(std::vector<FieldBinder*>& fields, std::string* fieldsStr, 
	const ISessionObject& src, int addCount, int *rowCount)
{
	const IObjectData* od = src.GetObjectDef();
	if (od == NULL)
		return;

	GRServer::Format* format = src.Self()->format;

	USES_CONVERSION;
	IObjectData::Fields::const_iterator fi = od->fields.begin();
	for (; fi != od->fields.end(); fi++)
	{
		int fldIndex = format->FindMember(fi->format.name.c_str());
		if (fldIndex < 0)
			continue;

		if (fi->CanCreate())
		{
			FieldBinder* fb = FieldBinder::Create(*fi, fldIndex, (int)(fields.size() + addCount));
			if (fb != NULL)
			{
				fields.push_back(fb);
				fieldsStr->append(1, '"').append(W2A_CP(fi->format.name.c_str(), CP_UTF8)).append("\",");
			}
		}
	}

	if (od->IsOrderedSource() && rowCount != NULL)
	{
		FieldBinder* fb = FieldBinder::CreateOrderBinder((int)(fields.size() + addCount), rowCount);
		fields.push_back(fb);
		fieldsStr->append(1, '"').append(W2A_CP(ORDERED_FIELD, CP_UTF8)).append("\",");
	}

	fieldsStr->erase(fieldsStr->size() - 1, 1);
}

bool ReadBinder::Prepare(std::string* fieldsStr, const ISessionObject& src)
{
	PrepareFields(fields, fieldsStr, src, 0, NULL);
	return fields.size() > 0;
}

bool ReadBinder::ReadTo(Object* o, const PGresult* res, int curRow) const
{
	bool ret = true;
	BinderList::const_iterator i = fields.begin();
	for (; ret && i != fields.end(); i++)
	{
		Member* out = &o->at((*i)->fieldIndex);
		ret = (*i)->Read(out, res, curRow);
	}

	return ret;
}

const MemberFormat* ReadBinder::Type(const wchar_t* name) const
{
	MemberFormat* mf = NULL;

	BinderList::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		if ((*i)->format.name.compare(name) == 0)
		{
			mf = &(*i)->format;
		}
	}

	return mf;
}

const Member* ReadBinder::Value(const wchar_t* name, const PGresult* res, int curRow) const
{
	Member* m = NULL;

	BinderList::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		if ((*i)->format.name.compare(name) == 0)
		{
			cache.str = &strValue;
			(*i)->Read(&cache, res, curRow);
			m = (Member*)&cache;
		}
	}

	return m;
}

ParamsBinder::ParamsBinder() :
	values(NULL)
	, lengths(NULL)
	, types(NULL)
	, formats(NULL)
	, rowCount(0)
{

}

ParamsBinder::~ParamsBinder()
{
	BinderList::iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		delete (*i);
	}

	free(values);
}

bool ParamsBinder::PrepareFK(std::string* fieldStr, std::string* andParams, std::string* valueParams, const ISessionObject& src)
{
	CVector<IObjectData::Field>* fkFields = NULL;
	const IObjectData* od = src.GetObjectDef();
	ISessionObject* parent = src.Parent();
	if (parent == NULL || !od->LoadFK(NULL, &fkFields) || fkFields->size() == 0)
	{
		delete fkFields;
		return false;
	}

	USES_CONVERSION;

	char buf[100];

	Format* format = parent->Self()->format;
	std::string parentObj;
	parentObj.append(1,'"').append(W2A_CP(parent->GetObjectDef()->tableName.c_str(), CP_UTF8));

	vector<IObjectData::Field>::const_iterator fi = fkFields->begin();
	for (; fi != fkFields->end(); fi++)
	{
		size_t pos = fi->format.name.find_last_of(L'$');
		const std::wstring& pname = fi->format.name.substr(pos + 1);
		int fldIndex = format->FindMember(pname.c_str());
		if (fldIndex < 0)
			continue;

		FieldBinder* fb = FieldBinder::Create(*fi, fldIndex, (int)(parentFields.size() + fields.size()));
		if (fb == NULL)
			continue;

		parentFields.push_back(fb);

		std::string fname;
		fname.append(parentObj).append(1, '$').append(W2A_CP(pname.c_str(), CP_UTF8)).append(1, '"');

		_itoa(fb->columnIndex + 1, buf, 10);
		std::string param;
		param.append(1, '$').append(buf);// .append(fb->TypeHint());

		if (fieldStr != NULL)
		{
			if (!fieldStr->empty())
				fieldStr->append(1, ',');
			fieldStr->append(fname);
		}

		if (andParams != NULL)
		{
			if (!andParams->empty())
				andParams->append(" AND ");
			andParams->append(fname).append(" = ").append(param);
		}

		if (valueParams != NULL)
		{
			if (!valueParams->empty())
				valueParams->append(1, ',');
			valueParams->append(param);
		}
	}
	
	delete fkFields;
	return true;
}

bool ParamsBinder::Prepare(std::string* fieldsStr, std::string* params, const ISessionObject& src)
{
	PrepareFields(fields, fieldsStr, src, (int)parentFields.size(), &rowCount);
	if (params != NULL)
	{
		char buf[100];
		BinderList::const_iterator fi = fields.begin();
		for (; fi != fields.end(); fi++)
		{
			if (!params->empty())
				params->append(",");

			_itoa((*fi)->columnIndex + 1, buf, 10);
			std::string val("$");
			val.append(buf);// .append((*fi)->TypeHint());

			params->append(val);
		}
	}
	return (fields.size() > 0);
}

const Oid* ParamsBinder::Types() const
{
	if (Count() == 0)
		return NULL;

	AllocValues();

	BinderList::const_iterator fi = fields.begin();
	for (; fi != fields.end(); fi++)
	{
		int i = (*fi)->columnIndex;
		types[i] = (*fi)->Type();
	}

	fi = parentFields.begin();
	for (; fi != parentFields.end(); fi++)
	{
		int i = (*fi)->columnIndex;
		types[i] = (*fi)->Type();
	}
	return types;
}

void ParamsBinder::AllocValues() const
{
	if (values != NULL)
		return;

	values = (const char**)malloc((sizeof(char*) + 2 * sizeof(int) + sizeof(Oid)) * Count());

	char* p = (char*)values + sizeof(char*) * Count();
	lengths = (int*)p;

	p += sizeof(int) * Count();
	types = (Oid*)p;

	p += sizeof(Oid) * Count();
	formats = (int*)p;
}

// call values first, then lengths returns correct values
const char* const* ParamsBinder::Values(const Object* src, const Object* parent) const
{
	if (Count() == 0)
		return NULL;

	AllocValues();

	BinderList::const_iterator fi;
	if (src != NULL)
	{
		fi = fields.begin();
		for (; fi != fields.end(); fi++)
		{
			int i = (*fi)->columnIndex;
			FieldBinder::Value v = (*fi)->ParamValue(src->at((*fi)->fieldIndex));
			values[i] = v.data;
			lengths[i] = v.length;
			formats[i] = v.format;
		}
	}

	if (parent != NULL)
	{
		fi = parentFields.begin();
		for (; fi != parentFields.end(); fi++)
		{
			int i = (*fi)->columnIndex;
			FieldBinder::Value v = (*fi)->ParamValue(parent->at((*fi)->fieldIndex));
			values[i] = v.data;
			formats[i] = v.format;
			lengths[i] = v.length;
		}
	}

	rowCount++;
	return values;
}

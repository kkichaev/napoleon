#include "stdafx.h"
#include "postgre.h"
#include <sstream>

#include <ServerDefs.h>

#include <regex>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

const wchar_t* GRServer::SENDED_FIELDS = L"$_objSended";
const wchar_t* GRServer::ORDERED_FIELD = L"_objOrdered";


struct MemberFormatDB : public MemberFormat
{
	WORD fieldWidth;
};

struct MFCmp
{
	bool operator()(const MemberFormat& _Left, const MemberFormat& _Right) const
	{	// apply operator< to operands
		return (_Left.name.compare(_Right.name) < 0);
	}
};

typedef std::set<MemberFormatDB, MFCmp> FieldSet;

void GRServer::AddErrorToLog(const std::string& msg, PGresult* res)
{
	if (!msg.empty())
		gServer->AddLog(msg.c_str());
	gServer->AddLog(IErrorLogger::Full, PQresultErrorMessage(res));
}

bool GRServer::Execute(PGconn* conn, const char* stmt)
{
	PGresult* res = PQexec(conn, stmt);
	ExecStatusType st = PQresultStatus(res);
	bool ret = (st == PGRES_TUPLES_OK || st == PGRES_COMMAND_OK);

	if (!ret)
	{
		std::string msg("Error while execute stmt ");
		msg += stmt;

		AddErrorToLog(msg, res);
	}

	PQclear(res);

	return true;
}

InternalSource::InternalSource(SQTable* _src) :
	src(_src)
{
}

IBinary* InternalSource::GetServerData(int id)
{
	PGconn* pc = connection.GetConnection();
	if (pc == NULL)
		return NULL;

	stringstream ss;
	ss << "SELECT \"data\" FROM \"ServerData\" WHERE \"id\"=" << id;

	PGresult* res = PQexecParams(pc, ss.str().c_str(), 0, NULL, NULL, NULL, NULL, 1);
	if (res == NULL)
		return NULL;

	ExecStatusType st = PQresultStatus(res);
	if (st != PGRES_TUPLES_OK || PQntuples(res) == 0)
	{
		PQclear(res);
		return NULL;
	}

	Binary* bret = NULL;
	int len = PQgetlength(res, 0, 0);
	if (len > 0)
	{
		bret = new Binary();
		BYTE* pb = bret->Alloc(len);
		memcpy(pb, PQgetvalue(res, 0, 0), len);
	}

	PQclear(res);

	return bret == NULL ? NULL : new MemoryBinary(bret);
}

bool InternalSource::PutServerData(int id, const Binary& b)
{
	PGconn* pc = connection.GetConnection();
	if (pc == NULL)
		return false;

	const char* stmt = 
		"INSERT INTO \"ServerData\" (\"id\", \"data\") VALUES ($1::bigint, $2::bytea) "
		"ON CONFLICT (\"id\") DO UPDATE SET \"data\"=EXCLUDED.\"data\"";

	const BYTE* data = b;
	const __int64 val = htonll(id);
	const char* const paramValues[] = { (const char*)&val, (const char*)data };
	int formats[] = { 1, 1 };
	int length[] = { sizeof(val), (int)b.Size() };


	PGresult* res = PQexecParams(pc, stmt, 2, NULL, paramValues, length, formats, 0);
	bool ret = PQresultStatus(res) == PGRES_COMMAND_OK;
	PQclear(res);

	return ret;
}

bool InternalSource::Execute(const wchar_t* stmt, ISession* session)
{
	PGconn* conn = connection.GetConnection();
	if (conn == NULL)
		return false;

	USES_CONVERSION;
	return ::Execute(conn, W2A_CP(stmt, CP_UTF8));
}

static void InitReader(ISessionObject* so, const wchar_t* expr, wchar_t* _key, PGconn* conn)
{
	QuerySourceCreator qs;
	ObjectSource* src = so->GetSource();
	ISessionObject* parent = so->Parent();
	if (parent == NULL)
	{
		std::wstring stmt(expr);
		src->reader = new QueryReader(conn, stmt, *so, false, 0, NULL);
		src->readerName.assign(qs.Name());
	}
	else
	{
		wchar_t* p = wcschr(_key, L';');
		if (p != NULL)
			*p = L'\0';

		std::wstring key(_key);
		src->reader = new QueryChildReader(key, *so, *parent);
		src->readerName.assign(qs.Name());

		_key = (wchar_t*)((p != NULL) ? p + 1 : L"");
	}

	ServObject* obj = so->Self();
	GRServer::Format::const_iterator fi = obj->format->begin();
	for (; fi != obj->format->end(); fi++)
	{
		if (fi->type == MemberFormat::mtObject)
		{
			InitReader(so->GetChild(fi->name), L"", _key, conn);
			break;
		}
	}
}

ISessionObject* InternalSource::Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session)
{
	GRServer::Format* fmt = session->RegisterType(typeDef, true);
	if (fmt == NULL)
		return NULL;

	wchar_t* grp = _wcsdup(groupExpr);

	ISessionObject* so = session->CreateObject(fmt->name, true);

	PGconn* conn = GetConnection(*so);
	if (conn == NULL)
	{
		return NULL;
	}


	CString dest, src;
	src.assign(stmt);
	so->PrepareFilterStr(&dest, src);

	InitReader(so, dest.c_str(), grp, conn);
	free(grp);

	so->Reading(L"", false);
	return so;
}


void InternalSource::Close()
{
	connection.Close();
}

static bool IsTableExists(PGconn* conn, const char* tableName)
{
	std::string stmt(
		"select count(*) from information_schema.tables "
		"where table_type like 'BASE TABLE' and table_schema = 'public' and table_name = '");
	stmt.append(tableName).append("'");

	PGresult* res = PQexec(conn, stmt.c_str());
	if (!res)
		return false;

	bool ret = false;
	if (PQresultStatus(res) == PGRES_TUPLES_OK && PQntuples(res) > 0)
	{
		ret = *PQgetvalue(res, 0, 0) != '0';
	}
	PQclear(res);
	return ret;
}

const char* GRServer::QuoteString(std::string* dest, const std::string& src)
{
	char symW = '"';
	if (*src.begin() != symW) dest->append(1, symW);
	dest->append(src);
	if (*dest->rbegin() != symW) dest->append(1, symW);
	return dest->c_str();
}

void GRServer::PKToList(std::vector<std::string>* fields, const std::wstring& _str, bool quoting, wchar_t divSymbol)
{
	USES_CONVERSION;

	const std::wstring& str = (*_str.begin() == L'"') ? _str.substr(1, _str.size() - 2) : _str;

	wstring::size_type lastPos = str.find_first_not_of(divSymbol, 0);
	wstring::size_type pos = str.find_first_of(divSymbol, lastPos);

	while (string::npos != pos || string::npos != lastPos)
	{
		wstring::size_type size = pos - lastPos;
		wstring::size_type start = lastPos;

		const std::wstring& vstr = str.substr(start, size);
		start = vstr.find_first_not_of(L' ');
		size = vstr.find_last_not_of(L' ');
		if (size >= start)
		{
			if (quoting)
			{
				string tstr;
				QuoteString(&tstr, W2A_CP(vstr.substr(start, size - start + 1).c_str(), CP_UTF8));
				fields->push_back(tstr);
			}
			else
				fields->push_back(W2A_CP(vstr.substr(start, size - start + 1).c_str(), CP_UTF8));
		}

		lastPos = str.find_first_not_of(divSymbol, pos);
		pos = str.find_first_of(divSymbol, lastPos);
	}
}

static void AddOrderedField(vector<IObjectData::Field>& fields, const IObjectData& objDef)
{
	if (objDef.IsOrderedSource())
	{
		IObjectData::Field f;
		MemberFormat& mf = f.format;
		mf.name = ORDERED_FIELD;
		mf.type = MemberFormat::mtNumber;
		mf.format.fraction = 0;
		f.width = 0;
		fields.push_back(f);
	}
}

bool GRServer::AddOrderedField(IObjectData::Fields& fields, const IObjectData& objDef)
{
	bool ret = false;
	if (objDef.IsOrderedSource())
	{
		IObjectData::Field f;
		MemberFormat& mf = f.format;
		mf.name = ORDERED_FIELD;
		mf.type = MemberFormat::mtNumber;
		mf.format.fraction = 0;
		f.width = 0;
		fields.insert(f);

		ret = true;
	}
	return ret;
}

static void UpdateFields(std::vector<IObjectData::Field>* fields, const CVector<IObjectData::Field>& keyFields)
{
	std::vector<IObjectData::Field>::const_iterator i = keyFields.begin();
	for (; i != keyFields.end(); i++)
	{
		bool finded = false;
		std::vector<IObjectData::Field>::iterator j = fields->begin();
		for (; j != fields->end(); j++)
		{
			if (j->format.name.compare(i->format.name) == 0)
			{
				finded = true;
				break;
			}
		}

		if (!finded)
			fields->push_back(*i);
	}
}

static const char* TypeToString(std::string* buf, const IObjectData::Field& format, int defaultLength)
{
	char wbuf[100];
	switch (format.format.type)
	{
	case MemberFormat::mtString:
		sprintf(wbuf, "VARCHAR(%d)", (format.width == 0) ? defaultLength : format.width);
		buf->assign(wbuf);
		break;
	case MemberFormat::mtNumber:
		buf->assign((format.format.format.fraction == 0) ? "BIGINT" : "FLOAT");
		break;
	case MemberFormat::mtDateTime:
		buf->assign("BIGINT");
		break;
	case MemberFormat::mtBinary:
		buf->assign("BYTEA");
		break;
	}
	return buf->c_str();
}


static bool CreateTable(PGconn* conn, const IObjectData& objDef, const char* tableName)
{
	vector<string> keyFields;
	IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
	if (keyI != objDef.members.end())
		PKToList(&keyFields, keyI->second, false);

	stringstream ss;
	ss << "CREATE TABLE \"" << tableName << '" (';

	vector<IObjectData::Field> fields;
	IObjectData::Fields::const_iterator fi = objDef.fields.begin();
	for (; fi != objDef.fields.end(); fi++)
	{
		if (!fi->CanCreate())
			continue;
		fields.push_back(*fi);
	}

	AddOrderedField(fields, objDef);

	CVector<IObjectData::Field>* fkFields = NULL;
	CVector<MemberFormat>* fkMFields = NULL;
	objDef.LoadFK(&fkMFields, &fkFields);

	//fields.insert(fields.end(), fkFields->begin(), fkFields->end());
	UpdateFields(&fields, *fkFields);

	if ((objDef.flags & IObjectDef::RemoveOnCommit) != 0)
	{
		IObjectData::Field f;
		MemberFormat& mf = f.format;
		mf.name = SENDED_FIELDS;
		mf.type = MemberFormat::mtNumber;
		mf.format.fraction = 0;
		f.width = 0;
		fields.push_back(f);
	}

	USES_CONVERSION;

	bool assigned = false;
	std::string buf;
	vector<IObjectData::Field>::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		const char* pType = TypeToString(&buf, *i, DEFAULT_STRING_LENGTH);
		if (*pType == L'\0') continue;

		if (assigned) ss << ",";
		else assigned = true;

		ss << '"' << W2A_CP(i->format.name.c_str(), CP_UTF8) << "\" " << pType << " ";
	}

	if (keyFields.size() > 0)
	{
		ss << ", CONSTRAINT pk_" << tableName << " PRIMARY KEY (";
		vector<string>::const_iterator ki = keyFields.begin();
		while (ki != keyFields.end())
		{
			if (ki != keyFields.begin())
				ss  << ",";
			ss << '"' << (*ki) << '"';
			ki++;
		}
		ss <<  ")";
	}

	CString* indexText = NULL;
	if (fkFields->size() > 0)
	{
		CString* fkText;
		objDef.CreateFKConstraint(&fkText, &indexText, *fkMFields);

		ss << L", " << W2A_CP(fkText->c_str(), CP_UTF8);
		delete fkText;
	}

	ss << ")";
	bool ret = ::Execute(conn, ss.str().c_str());
	if (ret && indexText != NULL && !indexText->empty())
	{
		ret = ::Execute(conn, W2A_CP(indexText->c_str(), CP_UTF8));
	}

	delete fkFields;
	delete fkMFields;
	delete indexText;

	return ret;
}

static void DoReplace(std::wstring* str, wchar_t src, wchar_t rpl)
{
	std::wstring::size_type sp = 0;
	std::wstring::size_type fnd = str->find(src, sp);
	while (fnd != std::wstring::npos)
	{
		str->replace(fnd, 1, 1, rpl);
		sp = fnd + 1;
		fnd = str->find(src, sp);
	}
}

bool CreateIndex(PGconn* conn, const std::wstring& tableName, const std::wstring& fields, bool unique)
{
	USES_CONVERSION;

	bool quoted = (*fields.begin() == L'"');
	std::wstring indexName((quoted) ? fields.substr(1, fields.size() - 2) : fields);
	DoReplace(&indexName, L',', L'_');
	indexName = tableName + ((unique) ? L"_U_" : L"_") + indexName;

	vector<string> indexFields;
	PKToList(&indexFields, fields, true);

	stringstream ss;
	ss <<  "CREATE ";
	if (unique)
		ss << "UNIQUE ";
	ss << "INDEX \"" << W2A_CP(indexName.c_str(), CP_UTF8) << "\" ON \"" << W2A_CP(tableName.c_str(), CP_UTF8) << "\" (";

	vector<string>::const_iterator i = indexFields.begin();
	for (; i != indexFields.end(); i++)
	{
		if (i != indexFields.begin())
			ss << ",";

		ss << (*i);
	}
	ss << ")";
	return ::Execute(conn, ss.str().c_str());
}

void MakeAlterStmt(std::string* stmt, const char* tableName, const std::vector<IObjectData::Field>& added)
{
	stmt->assign("ALTER TABLE \"").append(tableName).append("\" ADD ");

	USES_CONVERSION;

	string tbuf;
	vector<IObjectData::Field>::const_iterator fi = added.begin();
	for (; fi != added.end(); fi++)
	{
		if (fi != added.begin())
			stmt->append(",");

		stmt->append(1, '"').append(W2A_CP(fi->format.name.c_str(), CP_UTF8)).append("\" ");
		(*stmt) += TypeToString(&tbuf, *fi, DEFAULT_STRING_LENGTH);
	}
}

bool AlterTable(PGconn* conn, const char* tableName, const std::vector<IObjectData::Field>& added)
{
	if (gServer->GetConfig().Debug() == IErrorLogger::Full)
	{
		USES_CONVERSION;
		gServer->AddLog(IErrorLogger::Full, "Alter table %s", tableName);
	}

	bool ret = true;
	vector<IObjectData::Field>::const_iterator fi = added.begin();
	for (; fi != added.end() && ret; fi++)
	{
		std::vector<IObjectData::Field> vec;
		vec.push_back(*fi);

		string stmt;
		MakeAlterStmt(&stmt, tableName, vec);
		ret = ::Execute(conn, stmt.c_str());
	}

	return ret;
}

static void GetTableFields(PGconn* conn, FieldSet* fields, const char* tableName)
{
	std::regex charType("(^char[^v]*(varying)?)|(^varchar)", std::regex_constants::ECMAScript | std::regex_constants::icase);
	std::regex intType("((big)|(small))?(int|serial)", std::regex_constants::ECMAScript | std::regex_constants::icase);
	std::regex realType("numeric|real|double|decimal", std::regex_constants::ECMAScript | std::regex_constants::icase);
	std::regex binaryType("bytea", std::regex_constants::ECMAScript | std::regex_constants::icase);

	std::string stmt("SELECT column_name, data_type, character_maximum_length FROM information_schema.columns WHERE table_name = '");
	stmt.append(tableName).append(1,'\'');

	USES_CONVERSION;

	PGresult* res = PQexec(conn, stmt.c_str());
	if (PQresultStatus(res) == PGRES_TUPLES_OK)
	{
		int rows = PQntuples(res);
		for (int i = 0; i < rows; i++)
		{
			const char* clmn = PQgetvalue(res, i, 0);
			const char* p = PQgetvalue(res, i, 1);

			MemberFormatDB mf;

			std::cmatch match;
			if (std::regex_search(p, match, charType))
			{
				//bool isVarchar = (match[2].length() > 0 || match[3].length() > 0);
				mf.fieldWidth = atoi(PQgetvalue(res, i, 2));
				mf.type = MemberFormat::mtString;				
			} else if (std::regex_search(p, match, intType))
			{
				//bool isBig = match[2].length() > 0;
				//bool isSmall = match[3].length() > 0;
				mf.type = MemberFormat::mtNumber;
				mf.format.fraction = 0;
			} else if (std::regex_search(p, match, realType))
			{
				mf.type = MemberFormat::mtNumber;
				mf.format.fraction = 8;
			} else if (std::regex_search(p, match, binaryType))
			{
				mf.type = MemberFormat::mtBinary;
			}
			else {
				continue;
			}
			mf.name = A2W_CP(clmn, CP_UTF8);
			mf.flags = 0;
			fields->insert(mf);
		}
	}

	PQclear(res);
}

bool CheckTable(PGconn* conn, FieldSet* fields, const IObjectData& objDef, const char* tn)
{
	bool res = true;

	GetTableFields(conn, fields, tn);

	std::vector<IObjectData::Field> added;

	IObjectData::Fields odfields(objDef.fields);

	AddOrderedField(odfields, objDef);

	CVector<IObjectData::Field>* fkFields = NULL;
	objDef.LoadFK(NULL, &fkFields);

	CVector<IObjectData::Field>::const_iterator fki = fkFields->begin();
	for (; fki != fkFields->end(); fki++)
		odfields.insert(*fki);
	delete fkFields;

	IObjectData::Fields::const_iterator fi = odfields.begin();
	for (; fi != odfields.end(); fi++)
	{
		if (!fi->CanCreate())
			continue;

		MemberFormatDB mf;
		*(MemberFormat*)&mf = fi->format;
		mf.fieldWidth = 0;
		FieldSet::iterator fnd = fields->find(mf);
		if (fnd == fields->end())
			added.push_back(*fi);
		else
		{
			if (mf.type == MemberFormat::mtString && fnd->fieldWidth < fi->width)
			{
				USES_CONVERSION;

				gServer->AddError(false, "Table [%s] field [%s] width %d smaller %d",
					W2A_CP(objDef.tableName.c_str(), CP_UTF8),
					W2A_CP(mf.name.c_str(), CP_UTF8),
					fnd->fieldWidth,
					fi->width
				);
				abort();
				return false;
			}
		}
	}

	if ((objDef.flags & IObjectDef::RemoveOnCommit) != 0)
	{
		IObjectData::Field f;
		f.format.name = SENDED_FIELDS;
		f.format.type = MemberFormat::mtNumber;
		f.format.format.fraction = 0;
		f.width = 0;

		MemberFormatDB mf;
		*(MemberFormat*)&mf = f.format;
		if (fields->find(mf) == fields->end())
			added.push_back(f);
	}

	if (added.size() > 0)
		res = AlterTable(conn, tn, added);

	if (res)
	{
		IObjectData::MemberArray::const_iterator keyI = objDef.memberArray.find(INDEX_KEY_STR);
		if (keyI != objDef.memberArray.end())
		{
			IObjectData::ValueList::const_iterator valI = keyI->second.begin();
			for (; valI != keyI->second.end(); valI++)
				CreateIndex(conn, objDef.tableName, (*valI), false);
		}

		keyI = objDef.memberArray.find(UNIQUE_INDEX_KEY_STR);
		if (keyI != objDef.memberArray.end())
		{
			IObjectData::ValueList::const_iterator valI = keyI->second.begin();
			for (; valI != keyI->second.end(); valI++)
				CreateIndex(conn, objDef.tableName, (*valI), true);
		}
	}

	return res;
}


bool InternalSource::Init(GRServer::IObjectDef* objDefs, const GRServer::ServerConfig& config)
{
	PGconn* conn = connection.GetConnection();
	if (conn == NULL)
		return false;

	const char* stmt = "CREATE TABLE IF NOT EXISTS \"ServerData\" (\"id\" BIGINT PRIMARY KEY, \"data\" BYTEA)";
	if (!::Execute(conn, stmt))
		return false;

	CVector<CString>* names = NULL;
	objDefs->GetObjectsName(&names, IObjectDef::Internal);

	USES_CONVERSION;

	bool res = true;
	CVector<CString>::const_iterator i;
	for (int pass = 0; res && pass < 2; pass++)
	{
		int idx = 0;
		i = names->begin();
		for (; res && i != names->end(); i++)
		{
			idx++;
			const std::wstring& tname = (const std::wstring&)*i;
			bool isChildTable = (tname.find(L'$') != std::wstring::npos);
			if (pass == 0 && isChildTable || pass == 1 && !isChildTable)
				continue;

			FieldSet fields;
			const IObjectData* odef = objDefs->Get(tname);
			gServer->AddLog(IErrorLogger::Full, "Check table %s", W2A_CP(odef->tableName.c_str(), CP_UTF8));

			const char* tn = W2A_CP(odef->tableName.c_str(), CP_UTF8);
			if (!IsTableExists(conn, tn))
			{
				gServer->AddLog(IErrorLogger::Full, "Create table %s", tn);
				res = CreateTable(conn, *odef, tn);
			}
			else
			{
				res = CheckTable(conn, &fields, *odef, tn);
			}
		}
	}
	delete names;
	return res;
}

//
//ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

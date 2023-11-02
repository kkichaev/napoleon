/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"

#include "Reader.h"
#include "QuerySource.h"

//
// ------------------------------------ Child Binder ----------------------------------------------------
//
ChildBinder::ChildBinder() : executed(false)
{
}

bool ChildBinder::ExecuteReader(const std::wstring& stmt)
{ 
   SQLRETURN rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
	BindParams();
	return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

bool ChildBinder::PrepareFKStmt(std::wstring* paramStmt, const ISessionObject& obj, ODBCFlavor* flavor)
{
   CVector<IObjectData::Field>* fkFields = NULL;
   const IObjectData* od = obj.GetObjectDef();
   ISessionObject* parent = obj.Parent();
   if( parent == NULL || !od->LoadFK(NULL, &fkFields) )
   {
      delete fkFields;
      return false;
   }

   Format* format = parent->Self()->format;
   paramStmt->clear();
   
   vector<IObjectData::Field>::const_iterator fi = fkFields->begin();
   for( ; fi != fkFields->end(); fi++ )
   {
      size_t pos = fi->format.name.find_last_of(L'$');
      const std::wstring& pname = fi->format.name.substr(pos+1);
      int fldIndex = format->FindMember(pname.c_str());
      if( fldIndex < 0 )
         continue;

      FieldBinder* fb = flavor->GetBinder(*fi, DEFAULT_STRING_LENGTH, fldIndex, (int)(params.size() + 1));
      if( fb == NULL )
         continue;

      AddParam(paramStmt, fb);
   }
   delete fkFields;
   return true;
}

bool ChildBinder::PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   wstring fkFilter;

   if( !PrepareFKStmt(&fkFilter, obj, flavor) )
      return false;

   return ReadBinder::PrepareRead(stmt, obj, fkFilter, hDbc, flavor);
}

bool ChildBinder::MoveNext(Object *parentObject)
{
   if( parentObject == NULL )
      return false;
   SQLRETURN rc = SQL_SUCCESS;

   if( !executed )
   {
      SQLCloseCursor(hstmt);
      WriteParams(*parentObject);
      rc = SQLExecute(hstmt);
      //if( rc != SQL_SUCCESS )
      //{
      //   if( gServer->GetConfig().Debug() == IErrorLogger::Full )
      //      AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
      //}
   }
   
   executed = (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO) && ((rc = SQLFetch(hstmt)) == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
   return executed;
}

//
// ------------------------------------ Reader ----------------------------------------------------
//
Reader::Reader(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor, const std::vector<wstring>& _filters, 
	bool debug, ParamHelper* defaults, const CString* whereFilter, const CString* stmt) :
   obj(object),
	isRoot(false),
   binder(NULL),
	params(defaults),
	filters(_filters)
{
   this->hDbc = hDbc;
   this->flavor = flavor;
	this->debug = debug;

	if (stmt != NULL)
	{
		this->stmt = stmt->c_str();
	}
	else 
	{
		if (whereFilter)
		{
			this->whereFilter = whereFilter->c_str();
		}
	}
}

Reader::~Reader()
{
   Close();
}

void Reader::Close()
{
   if( binder )
   {
      binder->Close();
      delete binder;
      binder = NULL;
	
		//if (isRoot)
		//{
		//	gServer->AddLog(IErrorLogger::Full, "Reader::Close ReleaseSTMT %s", tableName.c_str());
		//	ReleaseSTMT();
		//}
	}
}

void Reader::Remove()
{
   const IObjectData* od = obj.GetObjectDef();
   wstring tableName;
   QuoteString(&tableName, od->tableName);

   wstring stmt(L"DELETE FROM "); stmt += tableName;
   if( parsedFilter.empty() == false )
   {
      stmt += L" WHERE ";
      stmt += (const std::wstring&)parsedFilter;
   }
   Execute(hDbc, stmt);
}

const MemberFormat* Reader::Type(const wchar_t* name) const
{
   return (binder) ? binder->FieldType(name) : NULL;
}

const Member* Reader::Value(const wchar_t* name) const
{
   return (binder) ? binder->Value(name) : NULL;
}

bool Reader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
	DWORD cbParams = sizeof(L"PARAMS:") - sizeof(wchar_t);
	if (memcmp(filter, L"PARAMS:", cbParams) == 0)
	{
		params.Read((const wchar_t*)(filter + cbParams / sizeof(wchar_t)), &object.GetSession(), &object, (IErrorLogger*)gServer);
	}
	else
	{
		wstring dest;
		// mark filter no parse
		dest.append(1, L'\x1').append(filter);
		this->filters.insert(this->filters.begin(), dest);
	}
	return true;
}

void Reader::PrepareBinder(const std::wstring& _filter)
{
	const IObjectData* od = obj.GetObjectDef();

	CString filter(_filter);

	if (stmt.empty())
	{
		if (!whereFilter.empty())
		{
			CString tfilter;

			if (!filter.empty())
			{
				tfilter.append(L"(").append(filter).append(L") AND (").append(whereFilter).append(L")");
			}
			else
			{
				tfilter = whereFilter;
			}

			filter = tfilter;
		}

		if (!filter.empty())
		{
			CString* f = params.Substitute(filter.c_str());
			filter.assign(*f);
			delete f;
		}
		obj.PrepareFilterStr(&parsedFilter, filter);
		if ((od->flags & IObjectDef::RemoveOnCommit) != 0)
		{
			wstring tableName;
			wstring objSendField;
			QuoteString(&tableName, od->tableName);
			QuoteString(&objSendField, SENDED_FIELDS);
			objSendField += L"= 1";

			wstring stmt(L"UPDATE "); stmt += tableName; stmt += L" SET "; stmt += objSendField;
			if (parsedFilter.empty() == false)
			{
				stmt += L" WHERE ";
				stmt += (const std::wstring&)parsedFilter;
			}

			Execute(hDbc, stmt);
			if (!parsedFilter.empty())
				parsedFilter.append(L" AND ");
			parsedFilter.append(objSendField);
		}

		binder = CreateBinder();
		//std::wstring stmt;
		binder->PrepareRead(&stmt, obj, (const std::wstring&)parsedFilter, hDbc, flavor);

	}
	else
	{
		CString tstr;
		obj.PrepareFilterStr(&tstr, stmt);

		CString* f = params.Substitute(tstr.c_str());
		stmt.assign(f->c_str());
		delete f;

		CString dest;
		obj.PrepareFilterStr(&dest, stmt.c_str());
		stmt = dest.c_str();

		binder = new QueryBinder();
		binder->PrepareRead(&stmt, obj, L"", hDbc, flavor);
	}

	if (debug)
	{
		USES_CONVERSION;
		gServer->AddLog(IErrorLogger::None, "run stmt %s", W2A(stmt.c_str()));
	}
}

bool Reader::MoveNext(Object *parentObject)
{
   if( !binder )
   {
		isRoot = (parentObject == NULL);

		if (filters.size() > 0)
		{
			const ISession& session = obj.GetSession();

			vector<wstring>::const_iterator i = filters.begin();
			for (; i != filters.end(); i++)
			{
				CString dest;
				const wchar_t* filter = i->c_str();

				if (*filter != L'\x1')
				{
					Token tres;
					if (session.Parse(&tres, *i, &obj) && tres.type == Token::Type::ttString)
					{
						obj.PrepareFilterStr(&dest, *tres.value.str);
						filter = dest.c_str();
					}
					PrepareBinder(filter);
				}
				else
				{
					PrepareBinder(filter + 1);
				}

				return binder->MoveNext(parentObject);
				//if (binder->MoveNext(parentObject))
				//{
				//	return true;
				//}

				//binder->Close();
				//delete binder;
				//binder = NULL;
				//stmt.clear();
				//parsedFilter.clear();
			}

			return false;
		}
		else
		{
			PrepareBinder(L"");
		}

	}
   return binder->MoveNext(parentObject);
}

bool Reader::Get(Object* o) const
{
   return (binder) ? binder->Read(o) : false;
}

//
// ------------------------------------ Entry points ----------------------------------------------------
//
IDataSource::IReader* GRServer::CreateReader(const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return NULL;

   if( object.Parent() != NULL )
      return new ChildReader(object, hDbc, flavor);

	std::vector<wstring> filters;
	parameters.Load(&filters, L"readFilter", object);
	
	//CString *filter = NULL;
 //  const Parameter *filterP = parameters.Find(L"readFilter", 0);
 //  if( filterP != NULL )
 //     object.GetSession().Parse(&filter, filterP->value, &object);

	const Parameter* filterP = parameters.Find(L"debug", -1);

	ParamHelper *defaults = new ParamHelper(NULL);
	defaults->Read(parameters, &object.GetSession(), &object, gServer);
	
	CString* whereFilter = NULL;
	const Parameter *whF = parameters.Find(L"whereFilter", -1);
	if (whF != NULL)
	{
		bool needAssign = true;
		ISession& s = ((ISession&)object.GetSession());
		const Parameter *whC = parameters.Find(L"whereCondition", -1);
		if (whC)
		{
			needAssign = s.CheckCondition(whC->value, &object);
		}
		if (needAssign)
		{
			if (!s.Parse(&whereFilter, whF->value, &object))
			{
				USES_CONVERSION;
				gServer->AddLog("Error while parse whereFilter of %s", W2A(object.Self()->Name().c_str()));
			}
		}
	}

	CString *stmt = NULL;
	const Parameter* p = parameters.Find(L"stmt", -1);
	if (p != NULL)
	{
		if (!object.GetSession().Parse(&stmt, p->value, &object))
		{
			gServer->AddError(false, "SQTable не правильный параметр stmt");
			delete stmt;
			return NULL;
		}
	}

	IDataSource::IReader* ret = new Reader(object, hDbc, flavor, filters, (filterP != NULL), defaults, whereFilter, stmt);
   
	delete whereFilter;
	delete stmt;

   return ret;
}

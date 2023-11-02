/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB QuerySource
 *
 * ert   01/11/2012   creating
 */
#include "stdafx.h"
#include "ODBCSource.h"
#include "QuerySource.h"

using namespace std;

//
//-------------------------------------- QueryBinder ----------------------------------------------
//
static FieldBinder* CreateBinder(const wchar_t* name, int bindPos, const std::vector<const ISessionObject*>& objects, const MemberFormat** elFormat, ODBCFlavor *flavor)
{
   _wsetlocale( LC_ALL, L"" );

   std::vector<const ISessionObject*>::const_iterator obj = objects.begin();
   for( ; obj != objects.end(); obj++ )
   {
      const IObjectData* od = (*obj)->GetObjectDef();
      if( od == NULL )
         continue;

      IObjectData::Fields::const_iterator fi = od->fields.begin();
      for( ; fi != od->fields.end(); fi++ )
      {
         if(_wcsicmp(name, fi->data.c_str()) == 0)
         {
            GRServer::Format *format = (*obj)->Self()->format;
            int fldIndex = format->FindMember(fi->format.name.c_str());
            if( fldIndex >= 0 )
            {
               const MemberFormat& mf = format->at(fldIndex);
               *elFormat = &mf;
               return flavor->GetBinder(*fi, DEFAULT_STRING_LENGTH, fldIndex, bindPos);
            }
         }
      }
   }
   return NULL;
}

QueryBinder::QueryBinder() //: prepared(false)
{

}

void QueryBinder::Close()
{
	//if (prepared)
	//{
	//	AddToLog(IErrorLogger::Full, "QueryBinder::Close ReleaseSTMT");
	//	ReleaseSTMT();
	//}
	Binder::Close();
}

bool QueryBinder::PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor)
{
	std::vector<const ISessionObject*> objs;
	objs.push_back(&obj);

	return Prepare(*stmt, objs, hDbc, flavor);
}

bool QueryBinder::Prepare(const std::wstring& stmt, const std::vector<const ISessionObject*>& objects, SQLHDBC hDbc, ODBCFlavor* flavor)
{
	//AddToLog(IErrorLogger::Full, "QueryBinder::Prepare ReqSTMT");
	//RequestSTMT();
	//prepared = true;

   SQLRETURN rc;
   rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   rc = SQLExecDirect(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
   if( rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO )
   {
		AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
      SQLFreeHandle(SQL_HANDLE_STMT, hstmt);
      hstmt = NULL;
      return false;
   }

   SQLSMALLINT nColumn = 0;
   SQLNumResultCols(hstmt, &nColumn);

   this->flavor = flavor;

   wchar_t name[1000];
   for( SQLSMALLINT i=1; i<=nColumn; i++ )
   {
      SQLSMALLINT len = 0;
      SQLLEN num = 0;
      SQLColAttribute(hstmt, i, SQL_DESC_NAME, name, sizeof(name), &len, &num);

      const MemberFormat* format = NULL;
      FieldBinder* ret = CreateBinder(name, i, objects, &format, flavor);
		if (ret == NULL)
		{
			SQLLEN type, prec;
			SQLColAttribute(hstmt, i, SQL_DESC_TYPE, NULL, 0, &len, &type);

			MemberFormat::MemberType mt = flavor->ToMemberType((SQLSMALLINT)type);
			if (mt != MemberFormat::mtNone)
			{
				IObjectData::Field field;
				field.format.type = mt;
				field.data = name;
				field.flags = 0;
				field.width = 0;
				field.format.name = name;
				field.format.flags = 0;

				if (mt == MemberFormat::mtNumber)
				{
					SQLColAttribute(hstmt, i, SQL_DESC_PRECISION, NULL, 0, &len, &prec);
					if (prec > 8)
						prec = 8;
					field.format.format.fraction = (WORD)prec;
				}
				else if (mt == MemberFormat::mtDateTime)
					field.format.format.dateFormat = MemberFormat::Stamp;

				ret = flavor->GetBinder(field, DEFAULT_STRING_LENGTH, 0, i);
				if (ret != NULL)
				{
					customFormats.push_back(field.format);
					format = &(customFormats.back());
				}
			}
		}
      if( ret != NULL )
      {
         fields.push_back(ret);
         formats.push_back(format);
		}
   }

   return BindFields();
}


bool QueryBinder::Read(Object* o) const
{
   if( hstmt == NULL )
      return false;

   bool ret = true;

   //
   // При чтении надо проверять форматы чтобы записать в корректный объект. fields - содержит связи со всеми child объектами тоже
   //
   const Format& fmt = o->GetFormat();
   Format::const_iterator dfi = fmt.begin();
   for( ; dfi != fmt.end(); dfi++ )
   {
      const MemberFormat* elFormat = &(*dfi);
		FieldBinder* fb = GetBinder(elFormat);
		if (fb)
			fb->Read(o);

      //std::vector<FieldBinder*>::const_iterator i = fields.begin();
      //std::vector<const MemberFormat*>::const_iterator sfi = formats.begin();
      //for( ; ret && i != fields.end(); i++, sfi++ )
      //{
      //   if( elFormat == (*sfi) )
      //   {
      //      (*i)->Read(o);
      //   }
      //}
   }

   // файлы читаем те, которые соответствуют формату
   const ObjFiles::const_iterator fnd = objFiles.find((Format*)&fmt);
   if( fnd != objFiles.end() )
   {
      std::vector<FileField*>::const_iterator fi = fnd->second.begin();
      for( ; fi != fnd->second.end(); fi++ )
         if( !(*fi)->ReadFile(o) )
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


//
//-------------------------------------- QueryReader ----------------------------------------------
//
QueryReader::QueryReader(const CString& stmt, const ISessionObject& _object, SQLHDBC hDbc, ODBCFlavor* flavor, bool debug, int rowCount, ParamHelper *defaults) :
	object(_object), nextObject(NULL),params(defaults)
{
   this->stmt = stmt;
   this->hDbc = hDbc;
   this->flavor = flavor;
   this->debug = debug;
	this->rowCount = rowCount;
	this->curRow = 0;
}

QueryReader::QueryReader(const ISessionObject& _object) : object(_object), nextObject(NULL), rowCount(0), curRow(0), params(NULL)
{
}

QueryReader::~QueryReader()
{
   delete nextObject;
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

bool QueryReader::MoveNext(Object *parentObject)
{
   if( !binder.IsOpened() )
   {
      std::vector<const ISessionObject*> objects(childs);
      objects.insert(objects.begin(), &object);

		CString* sres = params.Substitute(stmt.c_str());
		stmt.clear();
		object.PrepareFilterStr(&stmt, (const std::wstring&)*sres);
		delete sres;

		//std::wstring str((const std::wstring&)stmt);
		//stmt.clear();
		//object.PrepareFilterStr(&stmt, str);
		//CString* sres = params.Substitute(stmt.c_str(), defaults);
		//stmt.assign(sres->c_str());
		//delete sres;

		if (debug)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "Do Query: %s", W2A(stmt.c_str()));
		}

      bool res = binder.Prepare((const std::wstring&)stmt, objects, hDbc, flavor);
      if( !res  )
      {
         if( AddErrorsToLog(false, SQL_HANDLE_STMT, binder.GetHSTMT(), IErrorLogger::Full) )
			{
				USES_CONVERSION;
				gServer->AddError(false, "stmt %s", W2A(stmt.c_str()));
			}
         return false;
      } else if( debug )
      {
         gServer->AddLog(IErrorLogger::Full, "Query executed");
      }
   }

	if (rowCount > 0 && curRow++ >= rowCount)
		return false;
	
	if (nextObject != NULL)
      return true;

   return binder.MoveNext(parentObject);
}

bool QueryReader::Get(Object* o) const
{
   if( nextObject != NULL && &nextObject->format == &o->format)
   {
      nextObject->MoveTo(o);
      delete nextObject;
      nextObject = NULL;
      return true;
   }
   return binder.Read(o);
}

Object* QueryReader::GetNext()
{
   if( !binder.MoveNext(NULL) )
   {
      delete nextObject;
      nextObject = NULL;
      return NULL;
   }

   if( nextObject == NULL )
      nextObject = Create(*object.Self()->format);
   binder.Read(nextObject);
   return nextObject;
}

void QueryReader::Close()
{
   binder.Close();
}

//
//-------------------------------------- KeyMember ----------------------------------------------
//
class StringMember : public KeyMember
{
public:
   StringMember(int index) : KeyMember(index) {}

   virtual KeyMember* Clone() const { return new StringMember(index); }
   virtual void Load(const Object& src) { value.assign(*src.at(index).str); }
   virtual bool IsEqual (const KeyMember& _src) const { return (value.compare( ((const StringMember&)_src).value) == 0); }

protected:
   CString value;
};

class NumberMember : public KeyMember
{
public:
   NumberMember(int index) : KeyMember(index) {}

   virtual KeyMember* Clone() const { return new NumberMember(index); }
   virtual void Load(const Object& src) { value = src.at(index).number; }
   virtual bool IsEqual (const KeyMember& _src) const { return (value == ((const NumberMember&)_src).value); }

protected:
   double value;
};

class DateTimeMember : public KeyMember
{
public:
   DateTimeMember(int index) : KeyMember(index) {}

   virtual KeyMember* Clone() const { return new DateTimeMember(index); }
   virtual void Load(const Object& src) { value = src.at(index).datetime; }
   virtual bool IsEqual (const KeyMember& _src) const { return (CompareFileTime(&value, &((const DateTimeMember&)_src).value) == 0); }

protected:
   FILETIME value;
};

KeyMember* KeyMember::Create(const std::wstring& name, Format* format)
{
   KeyMember *ret = NULL;
   int idx = format->FindMember(name.c_str());
   if( idx >= 0 )
   {
      MemberFormat& mf = format->at(idx);
      switch( mf.type )
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
   Format *format = object.Self()->format;

   wstring::const_iterator si = keyFields.begin(), ei = keyFields.end();
   if( *si == L'"' ) si++;

   wstring f;
   for( ; si != ei; si++ )
   {
      wchar_t sym = *si;

      if( sym == L'"' ) break;
      if( sym == L',' )
      {
         if( !f.empty() )
         {
            size_t start = f.find_first_not_of(L' ');
            size_t end = f.find_last_not_of(L' ');
            KeyMember* km = KeyMember::Create(f.substr(start, end - start + 1), format);
            if( km )
               keys.push_back(km);
         }
         f.clear();
      } else
         f.append(1, sym);
   }
   if( !f.empty() )
   {
      size_t start = f.find_first_not_of(L' ');
      size_t end = f.find_last_not_of(L' ');
      KeyMember* km = KeyMember::Create(f.substr(start, end - start + 1), format);
      if( km )
         keys.push_back(km);
   }
}

KeyHolder::KeyHolder(const KeyHolder& src)
{
   vector<KeyMember*>::const_iterator i = src.keys.begin();
   for( ; i != src.keys.end(); i++ )
      keys.push_back((*i)->Clone());
}

KeyHolder::~KeyHolder()
{
   vector<KeyMember*>::iterator i = keys.begin();
   for( ; i != keys.end(); i++ )
      delete (*i);
}

void KeyHolder::Load(const Object& object)
{
   vector<KeyMember*>::iterator i = keys.begin();
   for( ; i != keys.end(); i++ )
      (*i)->Load(object);
}

bool KeyHolder::operator != (const KeyHolder& src) const
{
   bool ret = true;

   vector<KeyMember*>::const_iterator si = keys.begin(), di = src.keys.begin();
   for( ; ret && si != keys.end(); si++, di++ )
      ret = (*si)->IsEqual(*(*di));

   return !ret;
}

//
//-------------------------------------- QueryChildReader ----------------------------------------------
//
QueryChildReader::QueryChildReader(const CString& keyFields, const CString* checkField, const ISessionObject& object, const ISessionObject& _parent) :
	QueryReader(object), keyHolder((const std::wstring&)keyFields, _parent), parent(NULL), keyLoaded(false), parentHaveNextObject(false)
{
   ObjectSource *os = _parent.GetSource();
   if( os != NULL && os->reader && os->readerName.compare(QuerySourceCreator().Name()) == 0 )
      parent = (QueryReader*)os->reader;

   if( parent != NULL )
      parent->AddChildObject(&object);

	checkNull = NULL;
	if (checkField && !checkField->empty())
	{
		Format *fmt = object.Self()->format;
		int idx = fmt->FindMember(checkField->c_str());
		if (idx >= 0)
			checkNull = &fmt->at(idx);
	}
}

QueryChildReader::~QueryChildReader()
{
}

bool QueryChildReader::MoveNext(Object *parentObject)
{
   if( parentObject == NULL )
      return false;

	if (parentHaveNextObject)
	{
		parentHaveNextObject = false;
		keyLoaded = false;
		return false;
	}

	bool ret = false;
   if( !keyLoaded )
   {
		if (checkNull)
		{
			FieldBinder* fb = GetBinder(checkNull);
			if (fb && fb->IsNull())
				return false;
		}
      keyHolder.Load(*parentObject);
      ret = true;
   } else
   {
		if (this->nextObject != NULL)
			return true;

      Object* nextObject = parent->GetNext();
      if( nextObject != NULL )
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

//
//-------------------------------------- QuerySourceCreator ----------------------------------------------
//
IDataSource::IReader* QuerySourceCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
   {
      gServer->AddError(false, "ODBC не соединен");
      return NULL;
   }

   QueryReader* ret = NULL;
   ISessionObject* parent = object.Parent();
   if( parent != NULL )
   {
      const Parameter* p = parameters.Find(L"keyFields", -1);
      if( p == NULL )
      {
         gServer->AddError(false, "SQLQuery нет параметра keyFields");
         return NULL;
      }
      CString *keyFields = NULL;
      if( !object.GetSession().Parse(&keyFields, p->value, &object) )
      {
         gServer->AddError(false, "SQLQuery не правильный параметр keyFields");
         delete keyFields;
         return NULL;
      }

		CString *checkField = NULL;
		p = parameters.Find(L"checkNullField", -1);
		if (p != NULL)
			object.GetSession().Parse(&checkField, p->value, &object);

		ret = new QueryChildReader(*keyFields, checkField, object, *parent);

      delete keyFields;
		delete checkField;
   } else
   {
      const Parameter* p = parameters.Find(L"stmt", -1);
      if( p == NULL )
      {
         gServer->AddError(false, "SQLQuery нет параметра stmt");
         return NULL;
      }
      CString *stmt = NULL;
      if( !object.GetSession().Parse(&stmt, p->value, &object) )
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
			CString *rc = NULL;
			if (object.GetSession().Parse(&rc, p->value, &object))
				rowCount = _wtoi(rc->c_str());

			delete rc;
		}
		
		ParamHelper *defaults = new ParamHelper(NULL);
		defaults->Read(parameters, &object.GetSession(), &object, gServer);
		ret = new QueryReader(*stmt, object, GetHDBC(), flavor, debug, rowCount, defaults);
      delete stmt;
   }
   return ret;
}

#include <token.h>

class ChildQueryBinder : public ParamBinder
{
public:
	ChildQueryBinder() {}
	bool Execute()
	{
		BindParams();

		SQLRETURN rc = SQLExecute(hstmt);
		if (rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO)
		{
			AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
			return false;
		}
		return true;
	}

	virtual bool PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor)
	{
		SQLRETURN rc;
		rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
		rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt->c_str(), SQL_NTS);
		if (rc != SQL_SUCCESS)
		{
			AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
			SQLFreeHandle(SQL_HANDLE_STMT, hstmt);
			hstmt = NULL;
			return false;
		}
		//BindParams();

		GRServer::Format *format = obj.Self()->format;
		const IObjectData* objDef = obj.GetObjectDef();

		wchar_t name[1000];
		for (SQLSMALLINT i = 1; true; i++)
		{
			SQLSMALLINT dt, dp, nlb, len = sizeof(name);
			SQLULEN size;
			rc = SQLDescribeCol(hstmt, i, name, sizeof(name), &len, &dt, &size, &dp, &nlb);
			if (rc != SQL_SUCCESS)
			{
				//AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
				break;
			}

			int idx = format->FindMember(name);
			if (idx < 0)
				continue;

			const IObjectData::Field* odf = objDef->FindField(format->at(idx).name);
			if (odf == NULL)
				continue;

			FieldBinder* ret = flavor->GetBinder(*odf, DEFAULT_STRING_LENGTH, idx, (int)(fields.size() + 1));

			if (ret != NULL)
				fields.push_back(ret);
		}
		return BindFields();
	}

};

class SQLChildQuery : public IDataSource::IReader
{
public:
	SQLChildQuery(const CString& _stmt, const ISessionObject& _object,
		SQLHDBC hDbc, ODBCFlavor* _flavor, bool debug, int rowCount, const ParamList& parameters);
	~SQLChildQuery();

	virtual bool MoveNext(Object *parentObject);

	// подставляем только объекты созданные методом Create
	// иначе будут проблемы при удалении объекта созданного в DLL
	virtual bool Get(Object* o) const;

	virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }
	virtual void Remove() {}
	virtual void Close();

	virtual const MemberFormat* Type(const wchar_t* name) const { return binder.FieldType(name); }
	virtual const Member* Value(const wchar_t* name) const { return binder.Value(name); }

protected:
	bool inited, requery;
	SQLHDBC hDbc;
	ODBCFlavor* flavor;
	bool debug;
	int rowCount, curRow;
	unsigned objIndex;

	const ISessionObject& object;
	CString stmt;

	ChildQueryBinder binder;
	std::vector<std::wstring> params;
	std::vector<Token*> values;
	std::vector<FieldBinder*> binders;

	void FreeTokens();
};

SQLChildQuery::SQLChildQuery(const CString& _stmt, const ISessionObject& _object,
	SQLHDBC _hDbc, ODBCFlavor* _flavor, bool debug, int rowCount, const ParamList& parameters) : 
	object(_object), stmt(_stmt), hDbc(_hDbc), flavor(_flavor), inited(false), requery(true)
{
	this->debug = debug;
	this->rowCount = rowCount;
	this->curRow = 0;

	ParamList::const_iterator i = parameters.begin();
	for (; i != parameters.end(); i++)
	{
		if (i != parameters.begin())
		{
			params.push_back(i->value);
		}
	}
}

void SQLChildQuery::FreeTokens()
{
	std::vector<Token*>::iterator i = values.begin();
	for (; i != values.end(); i++)
		delete (*i);

	values.clear();
}

SQLChildQuery::~SQLChildQuery()
{
}

void SQLChildQuery::Close()
{
	FreeTokens();
}

static bool InitFromToken(IObjectData::Field *f, const Token& src)
{
	bool ret = false;
	switch (src.type)
	{
	case Token::ttNumber:
		f->format.type = MemberFormat::mtNumber;
		f->format.format.fraction = 8;
		ret = true;
		break;
	case Token::ttDateTime:
		f->format.type = MemberFormat::mtDateTime;
		ret = true;
		break;
	case Token::ttString:
		f->format.type = MemberFormat::mtString;
		f->width = DEFAULT_STRING_LENGTH;
		ret = true;
		break;
	}

	return ret;
}

bool SQLChildQuery::MoveNext(Object *parentObject)
{
	if (requery)
	{
		FreeTokens();

		int i = 0;
		std::vector<std::wstring>::iterator pi = params.begin();
		for (; pi != params.end(); pi++)
		{
			Token* var = new Token();

			if (!object.GetSession().Parse(var, *pi, &object))
			{
				USES_CONVERSION;
				gServer->AddLog("SQLChildQuery parse error %s", W2A(pi->c_str()));
				
				delete var;
				return false;
			}

			values.push_back(var);
			FieldBinder* fb;
			if (!inited)
			{
				IObjectData::Field f;
				InitFromToken(&f, *var);
				
				fb = flavor->GetBinder(f, DEFAULT_STRING_LENGTH, 0, i + 1);
				if (fb == NULL)
				{
					USES_CONVERSION;
					gServer->AddLog("SQLChildQuery can't create binder %s", W2A(pi->c_str()));

					delete var;
					return false;
				}
				binders.push_back(fb);
				binder.AddParam(NULL, fb);
			}
			else
			{
				fb = binders.at(i);
			}

			fb->WriteFrom(*var);

			i++;
		}

		if (!inited)
		{
			binder.PrepareRead((std::wstring*)&stmt, object, L"", hDbc, flavor);
			
			if (debug)
			{
				USES_CONVERSION;
				gServer->AddLog(IErrorLogger::None, "Prepare stmt %s", W2A(stmt.c_str()));
			}
			inited = true;
		}

		if(!binder.Execute())
		{
			return false;
		}
	}

	requery = !binder.MoveNext(parentObject);
	return !requery;
}

bool SQLChildQuery::Get(Object* o) const
{
	return binder.Read(o);
}


//
//-------------------------------------- SQLChildQueryCreator ----------------------------------------------
//
IDataSource::IReader* SQLChildQueryCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
	ODBCFlavor* flavor = GetFlavor();
	if (flavor == NULL)
	{
		gServer->AddError(false, "ODBC не соединен");
		return NULL;
	}

	ISessionObject* parent = object.Parent();
	if (parent == NULL)
	{
		gServer->AddError(false, "No parent for SQLChildQuery");
		return NULL;
	}

	const Parameter* p = parameters.Find(L"stmt", -1);
	if (p == NULL)
	{
		gServer->AddError(false, "SQLChildQuery нет параметра stmt");
		return NULL;
	}

	CString *stmt = NULL;
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
		CString *rc = NULL;
		if (object.GetSession().Parse(&rc, p->value, &object))
			rowCount = _wtoi(rc->c_str());

		delete rc;
	}


	SQLChildQuery *ret = new SQLChildQuery(*stmt, object, GetHDBC(), flavor, debug, rowCount, parameters);
	delete stmt;
	return ret;
}


//
//-------------------------------------- QuerySourceCreator ----------------------------------------------
//

IDataSource::IReader* SQLSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
      return NULL;
   return GRServer::CreateReader(parameters, object, GetHDBC(), flavor);
}

IDataSource::IWriter* SQLSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
      return NULL;
   return GRServer::CreateWriter(parent, parameters, object, GetHDBC(false), flavor);
}

IDataSource::IRemover* SQLSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
      return NULL;
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, GetHDBC());
}

IDataSource::ISelector* SQLSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
      return NULL;
   return GRServer::CreateSelector(object, GetHDBC(), flavor);
}

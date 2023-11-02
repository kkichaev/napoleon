/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * MySQLDB plugin
 *
 * ert   06/02/2013   creating
 */
#include "stdafx.h"
#include "Binder.h"
#include "QuerySource.h"

using namespace std;

//
//-------------------------------------- QueryBinder ----------------------------------------------
//
struct FieldBindData
{
   int ordinal;
   FieldBinder* field;
};

static FieldBinder* CreateBinder(const MYSQL_FIELD& field, const std::vector<const ISessionObject*>& objects, const MemberFormat** elFormat)
{
   _wsetlocale( LC_ALL, L"" );

	USES_CONVERSION;
   std::vector<const ISessionObject*>::const_iterator obj = objects.begin();
   for( ; obj != objects.end(); obj++ )
   {
      const IObjectData* od = (*obj)->GetObjectDef();
      if( od == NULL )
         continue;

      IObjectData::Fields::const_iterator fi = od->fields.begin();
      for( ; fi != od->fields.end(); fi++ )
      {
			if(strcmp(field.name, W2U(fi->data.c_str())) == 0)
         {
            GRServer::Format *format = (*obj)->Self()->format;
            int fldIndex = format->FindMember(fi->format.name.c_str());
            if( fldIndex >= 0 )
            {
               const MemberFormat& mf = format->at(fldIndex);
               *elFormat = &mf;
               return FieldBinder::Create(*fi, fldIndex);
            }
         }
      }
   }
   return NULL;
}

bool QueryBinder::Prepare(MYSQL *db, const std::string& sql, const std::vector<const ISessionObject*>& objects)
{
   stmt = mysql_stmt_init(db);
   if( stmt == NULL || mysql_stmt_prepare(stmt, sql.c_str(), sql.size()) != 0 )
   {
      AddErrorsToLog(false, db, IErrorLogger::Short);
      return false;
   }

	MYSQL_RES* meta = mysql_stmt_result_metadata(stmt);
	if( meta == NULL )
	{
		AddErrorsToLog(false, db, IErrorLogger::Short);
		return false;
	}
	MYSQL_FIELD* field;
	int i=0;
	while((field = mysql_fetch_field(meta)) != NULL)
	{
      const MemberFormat* format;
      FieldBinder* ret = CreateBinder(*field, objects, &format);
      if( ret != NULL )
      {
         FieldBindData fbd;
         fbd.ordinal = i+1;
         fbd.field = ret;
         
         fields.push_back(ret);
         formats.push_back(format);
      }
		i++;
	}

	mysql_free_result(meta);

	if (!BindFields())
		return false;

	if (mysql_stmt_execute(this->stmt) != 0)
		return false;

	mysql_stmt_store_result(this->stmt);
	return true;
}

bool QueryBinder::Read(Object* o) const
{
   if( bind == NULL )
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
	   int index = 0;
      std::vector<FieldBinder*>::const_iterator i = fields.begin();
      std::vector<const MemberFormat*>::const_iterator sfi = formats.begin();
      for( ; ret && i != fields.end(); i++, sfi++, index++ )
      {
         if( elFormat == (*sfi) )
         {
				if( (*i)->Read(o, stmt, index) == false )
               ret = false;
         }
      }
   }

   return ret;
}

//
//-------------------------------------- QueryReader ----------------------------------------------
//
QueryReader::QueryReader(MYSQL *db, const std::wstring& stmt, const ISessionObject& _object, bool debug) : object(_object), nextObject(NULL)
{
	USES_CONVERSION;
	this->stmt = W2U(stmt.c_str());
	this->connection = db;
	this->debug = debug;
}

QueryReader::QueryReader(const ISessionObject& _object) : object(_object), nextObject(NULL), connection(NULL)
{
}

QueryReader::~QueryReader()
{
   delete nextObject;
}

bool QueryReader::MoveNext(Object *parentObject)
{
   if( !binder.IsOpened() )
   {
      std::vector<const ISessionObject*> objects(childs);
      objects.insert(objects.begin(), &object);
      if( !binder.Prepare(connection, stmt, objects) )
      {
         AddErrorsToLog(false, connection);
         return false;
      }else if( debug )
      {
         gServer->AddLog(IErrorLogger::Full, "Query executed");
      }
   }
   if( nextObject != NULL )
      return true;

   return binder.MoveNext(parentObject);
}

bool QueryReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   if( filter == NULL || *filter == L'\0' )
      return true;

   std::wstring str((const std::wstring&)stmt);
   std::vector<std::wstring> params;
   std::vector<std::wstring>::const_iterator i;
   int cnt = 1;

   PKToList(&params, filter, false);
   for( i = params.begin(); i != params.end(); i++, cnt++)
   {
      bool finded = false;
      wchar_t buf[10];
      wsprintf(buf, L"$%02d", cnt);

      while(true)
      {
         size_t find = str.find(buf);
         if( find == std::wstring::npos )
            break;
         str.replace(find, 3, (*i));
         finded = true;
      }
      if( !finded )
         break;
   }

	USES_CONVERSION;
	stmt.assign(W2U(str.c_str()));

   if( debug )
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Do Query: %s", stmt.c_str());
   }
   return true;
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
      return false;
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
   if( idx > 0 )
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
            long start = f.find_first_not_of(L' ');
            long end = f.find_last_not_of(L' ');
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
      long start = f.find_first_not_of(L' ');
      long end = f.find_last_not_of(L' ');
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
QueryChildReader::QueryChildReader(const CString& keyFields, const ISessionObject& object, const ISessionObject& _parent) : 
   QueryReader(object), keyHolder((const std::wstring&)keyFields, _parent), parent(NULL), keyLoaded(false)
{
   ObjectSource *os = _parent.GetSource();
   if( os != NULL && os->reader && os->readerName.compare(QuerySourceCreator().Name()) == 0 )
      parent = (QueryReader*)os->reader;

   if( parent != NULL )
      parent->AddChildObject(&object);
}

QueryChildReader::~QueryChildReader()
{
}

bool QueryChildReader::MoveNext(Object *parentObject)
{
   if( parentObject == NULL )
      return false;

   bool ret = false;
   if( !keyLoaded )
   {
      keyHolder.Load(*parentObject);
      ret = true;
   } else
   {
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

bool QueryChildReader::Get(Object* o) const
{
   return parent->Get(o);
}

//
//-------------------------------------- QuerySourceCreator ----------------------------------------------
//
IDataSource::IReader* QuerySourceCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   MYSQL* connection = GetConnection();
   if( connection == NULL )
   {
      gServer->AddError(false, "OLEDB не соединен");
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

      ret = new QueryChildReader(*keyFields, object, *parent);
      delete keyFields;
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

      ret = new QueryReader(connection, (const std::wstring&)*stmt, object, debug);
      delete stmt;
   }
   return ret;
}

//
//-------------------------------------- QuerySourceCreator ----------------------------------------------
//

IDataSource::IReader* SQLSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   MYSQL* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   return GRServer::CreateReader(parameters, object, connection);
}

IDataSource::IWriter* SQLSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   MYSQL* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   return GRServer::CreateWriter(parent, parameters, object, connection);
}

IDataSource::IRemover* SQLSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   MYSQL* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, connection);
}

IDataSource::ISelector* SQLSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;

   //MYSQL* connection = GetConnection();
   //if( connection == NULL )
   //   return NULL;
   //return GRServer::CreateSelector(object, connection);
}

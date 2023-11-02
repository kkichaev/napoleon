/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB QuerySource
 *
 * ert   01/11/2012   creating
 */
#include "stdafx.h"
#include "OleDB.h"
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

static FieldBinder* CreateBinder(const DBCOLUMNINFO& column, const std::vector<const ISessionObject*>& objects, const MemberFormat** elFormat)
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
         if(_wcsicmp(column.pwszName, fi->data.c_str()) == 0)
         {
            GRServer::Format *format = (*obj)->Self()->format;
            int fldIndex = format->FindMember(fi->format.name.c_str());
            if( fldIndex >= 0 )
            {
               const MemberFormat& mf = format->at(fldIndex);
               *elFormat = &mf;
               return FieldBinder::Create(mf, fldIndex);
            }
         }
      }
   }
   return NULL;
}

HRESULT QueryBinder::Prepare(CSession& session, const std::wstring& stmt, const std::vector<const ISessionObject*>& objects)
{
   HRESULT hr = reader.Open(session, stmt.c_str(), NULL, NULL, DBGUID_DEFAULT, false);
   if( !SUCCEEDED(hr) )
      return hr;

   DWORD bufSize = 0;

   DBORDINAL nColumns;
   DBCOLUMNINFO* columns;
   LPOLESTR str;
   hr = reader.GetColumnInfo(&nColumns, &columns, &str);
   if( !SUCCEEDED(hr) )
      return false;

   std::vector<FieldBindData> bindData;
   for( DBORDINAL i=0; i<nColumns; i++ )
   {
      const MemberFormat* format;
      FieldBinder* ret = CreateBinder(columns[i], objects, &format);
      if( ret != NULL )
      {
         FieldBindData fbd;
         fbd.ordinal = i+1;
         fbd.field = ret;
         
         bufSize += ret->FieldLength();
         bindData.push_back(fbd);
         fields.push_back(ret);
         formats.push_back(format);
      }
   }

   data = (BindData*)(new BYTE [bufSize]);
   hr = reader.CreateAccessor(fields.size(), data, bufSize);
   if( !SUCCEEDED(hr) )
   {
      delete data;
      data = NULL;
      return hr;
   }
   
   BindData* cd = data;
   std::vector<FieldBindData>::iterator fi = bindData.begin();
   for( ; fi != bindData.end(); fi++ )
   {
      fi->field->Bind(cd, reader, fi->ordinal);
      cd = (BindData*)((BYTE*)cd + fi->field->FieldLength());
   }

   if( nColumns )
   {
      IMalloc* mem;
      CoGetMalloc(1, &mem);

      mem->Free(columns);
      mem->Free(str);

      mem->Release();
   }

   if( !CreateAccessor(bufSize) )
      return false;

   return reader.Bind();
}

bool QueryBinder::Read(Object* o) const
{
   if( data == NULL )
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
      std::vector<FieldBinder*>::const_iterator i = fields.begin();
      std::vector<const MemberFormat*>::const_iterator sfi = formats.begin();
      for( ; ret && i != fields.end(); i++, sfi++ )
      {
         if( elFormat == (*sfi) )
         {
            if( (*i)->Read(o) == false )
               ret = false;
         }
      }
   }

   return ret;
}

//
//-------------------------------------- QueryReader ----------------------------------------------
//
QueryReader::QueryReader(CDataConnection& c, const CString& stmt, const ISessionObject& _object) : connection(&c), object(_object), nextObject(NULL)
{
   this->stmt = stmt;
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
      HRESULT res = binder.Prepare(connection->m_session, (const std::wstring&)stmt, objects);
      if( !SUCCEEDED(res)  )
      {
         AddErrorsToLog(false, res);
         return false;
      }
   }
   if( nextObject != NULL )
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
   CDataConnection* connection = GetConnection();
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

      ret = new QueryReader(*connection, *stmt, object);
      delete stmt;
   }
   return ret;
}

//
//-------------------------------------- QuerySourceCreator ----------------------------------------------
//

IDataSource::IReader* SQLSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   CDataConnection* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   return GRServer::CreateReader(parameters, object, *connection);
}

IDataSource::IWriter* SQLSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   CDataConnection* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   return GRServer::CreateWriter(parent, parameters, object, *connection);
}

IDataSource::IRemover* SQLSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   CDataConnection* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, *connection);
}

IDataSource::ISelector* SQLSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   CDataConnection* connection = GetConnection();
   if( connection == NULL )
      return NULL;
   return GRServer::CreateSelector(object, *connection);
}

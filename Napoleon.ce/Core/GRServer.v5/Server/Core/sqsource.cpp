/*
 * Copyright (C), 2009 - 2010, ����� �������
 *
 * SQLite source
 *
 * ��������������, ��� ��� �������� ������� ���� ������ ���������� � ������������ � objects.xml
 * � � ���������� ��� �������� ������ ������������ ObjectDef (�� ������ ���� �������������� � TableDef)
 * ��� ������� - ��� ��� �������
 *
 * ert   25/02/2010   creating
 */
#include "stdafx.h"
#include "creators.h"

//#define SQLITE_THREADSAFE 2
#include "sqlite/sqlite3.h"

#include "server.h"
#include "sessobj.h"
#include "objdef.h"
#include "srvdata.h"
#include "session.h"
#include "srvutility.h"
#include <ServerDefs.h>
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

static const wchar_t* SENDED_FIELDS = L"$_objSended";
static const wchar_t* ORDERED_FIELD = L"$_objOrdered";

//struct MustBeFields
//{
//   wchar_t *name;
//   wchar_t *type;
//} MUST_BE_FIELDS[] =
//{
//   //{ L"user_id", L"TEXT" },
//   { NULL, NULL }
//};

bool IsTableExists(const std::wstring& name);
bool Execute(const char *sql);
bool Execute(const CString& sql);

class SQLiteInternal : public IInternalDataSource
{
public:
   SQLiteInternal() {}
   ~SQLiteInternal() {}

   virtual const wchar_t* Name() const { return L"SQTableInternal"; }

   virtual IBinary* GetServerData(int id);
   virtual bool    PutServerData(int id, const Binary& b);

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config);
   virtual void    Close();

   virtual IDataSource::IReader*   CreateReader(const ParamList& parameters, const ISessionObject& object) const
   {
      return creator.CreateReader(parameters, object);
   }

   virtual IDataSource::IWriter*   CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
   {
      return creator.CreateWriter(parent, parameters, object);
   }

   virtual IDataSource::IRemover*  CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
   {
      return creator.CreateRemover(parent, parameters, object);
   }

   virtual bool Execute(const wchar_t* stmt, ISession* session)
   {
      USES_CONVERSION;
      return ::Execute(W2A_CP(stmt, CP_UTF8));
   }

   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

	virtual bool BackupBase(const char* backupFileName);

protected:
   SQLiteSourceCreator creator;
};

class FieldBinder
{
public:
   static FieldBinder* Create(const std::wstring& name, const GRServer::Format& objFormat);
   static FieldBinder* Create(int idx, const GRServer::Format& objFormat);

   virtual ~FieldBinder() {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index) = 0;
   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const = 0;

   virtual void Type(MemberFormat *type) const = 0;
   virtual void Value(Member* value, sqlite3_stmt *stmt, int index) const = 0;

   const std::wstring& Name() const { return name; }
   std::wstring& Name() { return name; }

protected:
   FieldBinder(int objectIndex, const std::wstring& name)
   {
      this->objectIndex = objectIndex;
      this->name = name;
   }

   std::wstring name;
   int objectIndex;
};

class EmptyBinder : public FieldBinder
{
public:
   EmptyBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index) { return true; }
   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const { return true; }
   virtual void Type(MemberFormat *type) const { }
	virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const {}
};

class StringBinder : public FieldBinder
{
public:
   StringBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

#ifdef UNIX
   std::string wrbuffer;
   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      USES_CONVERSION;
      const Member& m = o.at(objectIndex);
      wrbuffer.assign(W2A_CP(m.str->c_str(), CP_UTF8));
      return (sqlite3_bind_text(stmt, index, wrbuffer.c_str(), wrbuffer.size(), SQLITE_STATIC) == SQLITE_OK);
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      USES_CONVERSION;
      const wchar_t *value = A2W_CP((const char*)sqlite3_column_text(stmt, index), CP_UTF8);
      const Member& m = o->at(objectIndex);
      m.str->assign((value == NULL) ? L"" : value);

      return true;
   }
   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      USES_CONVERSION;
      value.assign(A2W_CP((const char*)sqlite3_column_text(stmt, index), CP_UTF8));
      m->str = (CString*)&value;
   }
#else
   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      const Member& m = o.at(objectIndex);
      return (sqlite3_bind_text16(stmt, index, m.str->c_str(), -1, SQLITE_STATIC) == SQLITE_OK);
   }
   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      const wchar_t *value = (const wchar_t*)sqlite3_column_text16(stmt, index);
      const Member& m = o->at(objectIndex);
      m.str->assign((value == NULL) ? L"" : value);

      return true;
   }
   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      value.assign((const wchar_t*)sqlite3_column_text16(stmt, index));
      m->str = (CString*)&value;
   }
#endif

   virtual void Type(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtString;
   }

protected:
   mutable CString value;
};

class OrderIndexBinder : public FieldBinder
{
public:
   OrderIndexBinder() : FieldBinder(-1, ORDERED_FIELD), index(0) {}

   int index;

   virtual bool Write(sqlite3_stmt* stmt, const Object& o, int index)
   {
      return (sqlite3_bind_int(stmt, index, this->index) == SQLITE_OK);
   }
   
   virtual bool Read(Object* o, sqlite3_stmt* stmt, int index) const { return true; }
   virtual void Type(MemberFormat* type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = 0;
   }

   virtual void Value(Member* m, sqlite3_stmt* stmt, int index) const
   {
      m->number = (double)sqlite3_column_int64(stmt, index);
   }
};

class IntegerBinder : public FieldBinder
{
public:
   IntegerBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      const Member& m = o.at(objectIndex);
      return (sqlite3_bind_int64(stmt, index, (__int64)m.number) == SQLITE_OK);
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      Member& m = o->at(objectIndex);
      m.number = (double)sqlite3_column_int64(stmt, index);
      return true;
   }

   virtual void Type(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = 0;
   }

   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      m->number = (double)sqlite3_column_int64(stmt, index);
   }
};

class DoubleBinder : public FieldBinder
{
public:
   DoubleBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      const Member& m = o.at(objectIndex);
      return (sqlite3_bind_double(stmt, index, m.number) == SQLITE_OK);
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      Member& m = o->at(objectIndex);
      m.number = sqlite3_column_double(stmt, index);
      return true;
   }
   virtual void Type(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = 8;
   }

   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      m->number = sqlite3_column_double(stmt, index);
   }
};

class DateBinder : public FieldBinder
{
public:
   DateBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      const Member& m = o.at(objectIndex);
      sqlite3_int64 val = m.datetime.dwLowDateTime | (((sqlite3_int64)m.datetime.dwHighDateTime) << 32);

      // move to local time
      tzset();
      val -= (sqlite3_int64)timezone * 10000000;
      return (sqlite3_bind_int64(stmt, index, val) == SQLITE_OK);
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      Member& m = o->at(objectIndex);
      sqlite_int64 value = sqlite3_column_int64(stmt, index);

      // move to utc time
      tzset();
      value += (sqlite3_int64)timezone * 10000000;

      m.datetime.dwLowDateTime = (DWORD)(value & 0xFFFFFFFF);
      m.datetime.dwHighDateTime = (DWORD)(value >> 32);

      return true;
   }

   virtual void Type(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtDateTime;
      type->format.dateFormat = MemberFormat::Stamp;
   }

   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      sqlite_int64 value = sqlite3_column_int64(stmt, index);

      m->datetime.dwLowDateTime = (DWORD)(value & 0xFFFFFFFF);
      m->datetime.dwHighDateTime = (DWORD)(value >> 32);
   }
};

class BinaryBinder : public FieldBinder
{
public:
   BinaryBinder(int objectIndex, const std::wstring& name) :
      FieldBinder(objectIndex, name) {}

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      bool ret = true;
      const Member& m = o.at(objectIndex);
      IBinary *b = m.binary;
      if( b != NULL && b->Size() > 0 )
         ret = (sqlite3_bind_blob(stmt, index, b->Bytes(), b->Size(), SQLITE_STATIC) == SQLITE_OK);
		else
			ret = (sqlite3_bind_blob(stmt, index, NULL, 0, SQLITE_STATIC) == SQLITE_OK);

      return ret;
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      Member& m = o->at(objectIndex);
      DWORD size = sqlite3_column_bytes(stmt, index);
      if( size > 0 )
      {
         Binary *b = new Binary();
         BYTE *pb = b->Alloc(size);

         memcpy(pb, sqlite3_column_blob(stmt, index), size);
         if( m.binary == NULL )
            m.binary = new MemoryBinary();
         m.binary->Assign(b);
      }
      return true;
   }

   virtual void Type(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtBinary;
   }

   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
      DWORD size = sqlite3_column_bytes(stmt, index);
      if( size > 0 )
      {
         Binary *b = new Binary();
         BYTE *pb = b->Alloc(size);
         memcpy(pb, sqlite3_column_blob(stmt, index), size);
         mb.Assign(b);

         m->binary = &mb;
      } else
         m->binary = NULL;
   }

   mutable MemoryBinary mb;
};

class RowIDParam : public FieldBinder
{
public:
   RowIDParam() : FieldBinder(-1, L"ROWID") {}

   void Set(const RowID& rid) { rowID = rid; }

   virtual bool Write(sqlite3_stmt *stmt, const Object& o, int index)
   {
      return  (rowID != NO_ROWID) ? (sqlite3_bind_int64(stmt, index, rowID) == SQLITE_OK) : false;
   }

   virtual bool Read(Object* o, sqlite3_stmt *stmt, int index) const
   {
      return false;
   }

   virtual void Type(MemberFormat *type) const
   {
   }

   virtual void Value(Member* m, sqlite3_stmt *stmt, int index) const
   {
   }

private:
   RowID rowID;
};

class Binder
{
public:
   Binder() : orderBinder(NULL) {}
   ~Binder() { Clear(); }

   void Clear();

   bool Prepare(const SessionObject& object);
	bool Prepare(sqlite3_stmt* stmt, const IObjectData* od, const GRServer::Format& format);

   bool PrepareForeignKey(std::wstring* whereStr, const SessionObject& object);

   bool Write(sqlite3_stmt *stmt, const Object& object, int startWith = 1);
   bool Read(Object* o, sqlite3_stmt *stmt) const;

   //(col1 [, coli]) VALUES (?1 [, ?i])
   void MakeInsertParams(std::wstring* params) const;
   void MakeInsertParams(std::wstring* columns, std::wstring* values, int startWith = 1) const;

   // col1 [,coli]
   void MakeSelectParams(std::wstring* params) const;

   const MemberFormat* FieldType(const wchar_t* name) const;
   const Member* Value(sqlite3_stmt* stmt, const wchar_t* name) const;

   void Add(FieldBinder* fb) { fields.push_back(fb); }

   int Count() const { return (int)fields.size(); }

   void SetParentID(const RowID& id) { ridParam.Set(id); }

   void ClearOrderIndex()
   {
      if (orderBinder != NULL)
         orderBinder->index = 0;
   }

   const std::vector<FieldBinder*>& Fields() const { return fields; }

protected:
   std::vector<FieldBinder*> fields;
   std::vector<FileField*> files;

   RowIDParam ridParam;

   mutable MemberFormat format;
   mutable Member value;
   OrderIndexBinder* orderBinder;
};

class SQLiteReader : public IDataSource::IReader
{
public:
	SQLiteReader(const SessionObject &object, const std::wstring& tableName, ParamHelper *defaults, const CString* whereFilter, 
      const vector<wstring> &filters, const CString* stmtStr = NULL, bool debug = false);
   virtual ~SQLiteReader();

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual const ParamHelper* GetParamHelper() const { return &params; }

protected:
   //SQLiteReader() : stmt(NULL) {}
   bool Prepare(const SessionObject& object, const wchar_t* whereStmt = L"");

   const SessionObject& sessionObject;
   vector<wstring> filters;

   std::wstring sqlStr, tableName;
   Binder binder;
   sqlite3_stmt *stmt;
	bool debug;

	DWORD commitId;
	ParamHelper params;
	std::wstring whereFilter;
};

class SQLiteChildReader : public SQLiteReader
{
public:
   SQLiteChildReader(const SessionObject& object, const std::wstring& tableName);

   virtual bool MoveNext(Object *parentObject);
   //virtual bool Get(Object* o) const;

protected:
   GRServer::Format* format;
   Binder parent;

   bool childPrepared;
   int childIndex;
};

class SQLiteQuery : public IDataSource::IReader
{
public:
	SQLiteQuery(const CString& query, const SessionObject& object, bool debug, int rowCount, ParamHelper *defaults);
   ~SQLiteQuery();

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return binder.FieldType(name); }
   virtual const Member* Value(const wchar_t* name) const { return (stmt != NULL) ? binder.Value(stmt, name) : NULL; }

   virtual void AddChild(const std::wstring& childName, IReader* reader) {}

   virtual const ParamHelper* GetParamHelper() const { return NULL; }


   void AddChildObject(const ISessionObject* object) { childs.push_back(object); }

   Object* GetNext();

	sqlite3_stmt *GetStmt() { return stmt; }

protected:
	SQLiteQuery(const ISessionObject& _object) : stmt(NULL), object(*(const SessionObject*)_object.Self()), nextObject(NULL), bof(false),params(NULL) { }
	
	bool Prepare(const SessionObject& object);

	bool debug, bof;

   const SessionObject& object;
   std::vector<const ISessionObject*> childs;
   CString query;

   Binder binder;
   sqlite3_stmt *stmt;

	mutable Object* nextObject;
	int rowCount, curRow;

	ParamHelper params;
};

class KeyMember
{
public:
	static KeyMember* Create(const std::wstring& name, GRServer::Format* format);

	KeyMember(int _index) : index(_index) {}
	virtual ~KeyMember() {}

	virtual KeyMember* Clone() const = 0;
	virtual void Load(const Object& src) = 0;
	virtual bool IsEqual(const KeyMember& _src) const = 0;

protected:
	int index;
};

class KeyHolder
{
public:
	KeyHolder(const std::wstring& keyFields, const ISessionObject& object);
	KeyHolder(const KeyHolder& src);
	~KeyHolder();

	void Load(const Object& object);
	bool operator != (const KeyHolder& src) const;
	bool operator == (const KeyHolder& src) const { return !(this->operator != (src)); }

protected:
	std::vector<KeyMember*> keys;
};

class QueryChildReader : public SQLiteQuery
{
public:
	QueryChildReader(const CString& keyFields, const ISessionObject& object, const ISessionObject& _parent);
	~QueryChildReader();

	virtual bool MoveNext(Object *parentObject);
	virtual bool Get(Object* o) const;

protected:
	SQLiteQuery* parent;
	KeyHolder keyHolder;
	bool keyLoaded;
};

//#include "mutex_t.h"
//static Mutex wrMutex;

const int MAX_DO_COUNT = 1000;
class SQLiteWriter : public IDataSource::IWriter
{
public:
   SQLiteWriter(const std::wstring& tableName);
   virtual ~SQLiteWriter();

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   std::wstring tableName;
   Binder binder;
   sqlite3_stmt *stmt;
   int doCount;
};

class SQLiteChildWriter : public SQLiteWriter
{
public:
   SQLiteChildWriter(const SessionObject& object, const std::wstring& tableName);

   // prpared in constructor
   virtual bool Prepare(const ISessionObject& object) { return true; }
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   Binder parent;
   sqlite3_stmt *rmvStmt;
   int childIndex;
};

class SQLiteRemover : public IDataSource::IRemover
{
public:
   SQLiteRemover(const SessionObject& object, const std::wstring& tableName);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() { }

protected:
   const SessionObject& object;
   std::wstring sql;
};

struct FieldDef
{
   enum Type { ftNull, ftInteger, ftText, ftBlob, ftReal };

   std::wstring name;
   Type type;
};

class TableDef : public std::vector<FieldDef>
{
public:
   TableDef(const std::wstring& tableName) : name(tableName) {}

   bool AlterTable(const ObjectDef& objDef);
   bool GetFieldsDef();

protected:
   const_iterator FindField(const std::wstring& name);
   bool AddFields(const std::vector<ObjectDef::Field>& fields);

   std::wstring name;
};

static sqlite3 *db = NULL;

bool Execute(const char *sql)
{
   char *errMsg = NULL;
   int res = sqlite3_exec(db, sql, NULL, NULL, &errMsg);
   sqlite3_free(errMsg);
   return (res == SQLITE_OK);
}

bool Execute(const CString& sql)
{
   sqlite3_stmt *stmt;
#ifdef UNIX
   USES_CONVERSION;
   int rc = sqlite3_prepare_v2(db,  W2A_CP(sql.c_str(), CP_UTF8), -1, &stmt, NULL);
#else
   int rc = sqlite3_prepare16_v2(db,  sql.c_str(), -1, &stmt, NULL);
#endif   

   if( rc == SQLITE_OK )
      rc = sqlite3_step(stmt);

	//if (rc != SQLITE_DONE)
	//{
	//	USES_CONVERSION;
	//	gServer->AddError(false, "stmt %s", W2A(sql.c_str()));
	//	gServer->AddError(false, "sqlite3 error %s", sqlite3_errmsg(db));
	//}
   sqlite3_finalize(stmt);

   return (rc == SQLITE_DONE);
}

bool IsTableExists(const std::wstring& name)
{
   if( db == NULL ) return false;

   std::wstring str(L"SELECT name FROM SQLITE_MASTER WHERE type='table' AND name='");
   str.append(name);
   str.append(L"'");

#ifdef UNIX
   USES_CONVERSION;
   sqlite3_stmt *stmt = NULL;
   int rc = sqlite3_prepare_v2(db, W2A_CP(str.c_str(), CP_UTF8), (int)(str.size() * sizeof(unsigned short)), &stmt, NULL);
#else
   sqlite3_stmt *stmt = NULL;
   int rc = sqlite3_prepare16_v2(db, str.c_str(), (int)(str.size() * sizeof(unsigned short)), &stmt, NULL);
#endif
   if( rc == SQLITE_OK )
      rc = sqlite3_step(stmt);
   sqlite3_finalize(stmt);

   return (rc == SQLITE_ROW);
}

static FieldDef::Type StringToFieldType(const wchar_t *field)
{
   if( _wcsicmp(field, L"INTEGER") ) return FieldDef::ftInteger;
   if( _wcsicmp(field, L"TEXT") ) return FieldDef::ftText;
   if( _wcsicmp(field, L"BLOB") ) return FieldDef::ftBlob;
   if( _wcsicmp(field, L"REAL") ) return FieldDef::ftReal;
   return FieldDef::ftNull;
}

static void PKToList(std::vector<std::wstring>* fields, const std::wstring& str, wchar_t sepSym = L',', bool noQuotes = false)
{
	bool haveQuotes = false;
   wstring::const_iterator si = str.begin(), ei = str.end();
	if (!noQuotes && *si == L'"')
	{
		si++;
		haveQuotes = true;
	}

   wstring f;
   for( ; si != ei; si++ )
   {
      wchar_t sym = *si;

      if( haveQuotes && sym == L'"' ) break;
      if( sym == sepSym )
      {
         if( !f.empty() )
         {
            //f.insert(f.begin(), L'\"');
            //f.append(L"\"");
            size_t start = f.find_first_not_of(L' ');
            fields->push_back(f.substr(start, f.size() - start));
         }
         f.clear();
      } else
         f.append(1, sym);
   }
   if( !f.empty() )
   {
      size_t start = f.find_first_not_of(' ');
      fields->push_back(f.substr(start, f.size() - start));
   }
}

static const wchar_t* TypeToString(const MemberFormat& format)
{
   switch(format.type)
   {
      case MemberFormat::mtString:
         return L" TEXT ";

      case MemberFormat::mtDateTime:
         return L" INTEGER ";

      case MemberFormat::mtNumber:
         return ( format.format.fraction == 0 ) ? L" INTEGER " : L" REAL ";

      case MemberFormat::mtBinary:
         return L" BLOB ";

      default: break;
   }
   return L"";
}

//struct MBFComparer
//{
//   bool operator()(const MustBeFields& _Left, const MustBeFields& _Right) const
//   {
//      return (wcscmp(_Left.name, _Right.name) < 0);
//   }
//};

static void UpdateFields(std::vector<MemberFormat>* fields, const CVector<MemberFormat>& keyFields)
{
	std::vector<MemberFormat>::const_iterator i = keyFields.begin();
	for (; i != keyFields.end(); i++)
	{
		bool finded = false;
		std::vector<MemberFormat>::iterator j = fields->begin();
		for (; j != fields->end(); j++)
		{
			if (j->name.compare(i->name) == 0)
			{
				finded = true;
				break;
			}
		}

		if (!finded)
			fields->push_back(*i);
	}
}

static bool CreateTable(const ObjectDef& objDef)
{
   if( db == NULL ) return false;

   vector<wstring> keyFields;
   ObjectDef::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second);

   std::wstring text(L"CREATE TABLE \"");
   text += objDef.tableName;
   text += L"\" (";

   //std::set<MustBeFields, MBFComparer> mbf;
   //std::set<MustBeFields, MBFComparer>::iterator mbFind;
   //for( int mi=0; MUST_BE_FIELDS[mi].name; mi++ )
   //   mbf.insert(MUST_BE_FIELDS[mi]);

   vector<MemberFormat> fields;
   ObjectDef::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      fields.push_back(fi->format);
   }

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      MemberFormat mf;
      mf.name = SENDED_FIELDS;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      fields.push_back(mf);
   }

   if (objDef.IsOrderedSource())
   {      
      MemberFormat mf;
      mf.name = ORDERED_FIELD;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      fields.push_back(mf);
   }

	CVector<MemberFormat>* fkFields = NULL;
   objDef.LoadFK(&fkFields);

	UpdateFields(&fields, *fkFields);

   bool assigned = false;
   vector<MemberFormat>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const MemberFormat& format = (*i);

      const wchar_t *pType = TypeToString(format);
      if( *pType == L'\0' ) continue;

      if( assigned ) text += L",";
      else assigned = true;

      text += L"\"";
      text += format.name;
      text += L"\" ";
      text += pType;
      text += L" ";

      //MustBeFields mf;
      //mf.name = (wchar_t*)format.name.c_str();
      //mbFind = mbf.find(mf);
      //if( mbFind != mbf.end() )
      //   mbf.erase(mbFind);
   }

   //for( mbFind = mbf.begin(); mbFind != mbf.end(); mbFind++ )
   //{
   //   text += L",\"";
   //   text += mbFind->name;
   //   text += L"\" ";
   //   text += mbFind->type;
   //   text += L" ";
   //}

   if( keyFields.size() > 0 )
   {
      text += L", CONSTRAINT pk_";
      text += objDef.tableName;
      text += L" PRIMARY KEY (";
      vector<wstring>::const_iterator ki = keyFields.begin();
      while( ki != keyFields.end() )
      {
         if( ki != keyFields.begin() )
            text += L",";
         text += L"\"";
         text += (*ki);
         text += L"\"";
         ki++;
      }
      text += L")";
   }

   CString *indexText = NULL;
   if( fkFields->size() > 0 )
   {
      CString *fkText;
      objDef.CreateFKConstraint(&fkText, &indexText, *fkFields);

      text += L", ";
      text += (const std::wstring&)(*fkText);
      delete fkText;
   }

   text += L")";

   bool ret = Execute(text);

   if( indexText != NULL && !indexText->empty() )
      Execute(*indexText);

   delete fkFields;
   delete indexText;
   return ret;
}

bool TableDef::AddFields(const std::vector<ObjectDef::Field>& fields)
{
   if( db == NULL ) return false;

   std::vector<ObjectDef::Field>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const wchar_t *pType = TypeToString(i->format);
      if( *pType == L'\0' ) continue;

      std::wstring text(L"ALTER TABLE \"");
      text += name;
      text += L"\" ADD COLUMN \"";
      text += i->format.name;
      text += L"\" ";
      text += pType;
      text += L" ";

      sqlite3_stmt *stmt;
#ifdef UNIX
      USES_CONVERSION;
      int rc = sqlite3_prepare_v2(db,  W2A_CP(text.c_str(), CP_UTF8), -1, &stmt, NULL);
#else
      int rc = sqlite3_prepare16_v2(db,  text.c_str(), -1, &stmt, NULL);
#endif
      if( rc == SQLITE_OK )
         sqlite3_step(stmt);
      sqlite3_finalize(stmt);

      if( rc != SQLITE_OK )
      {
         USES_CONVERSION;
         gServer->AddError(true, "Error alter table %s: %s", W2A_CP(name.c_str(), CP_UTF8), sqlite3_errmsg(db));
         break;
      }
   }

   return (i == fields.end());
}

bool TableDef::GetFieldsDef()
{
   if( db == NULL ) return false;

   std::wstring str(L"PRAGMA TABLE_INFO('");
   str += name;
   str += L"')";

   sqlite3_stmt *stmt = NULL;
#ifdef UNIX
   USES_CONVERSION;
   int rc = sqlite3_prepare_v2(db, W2A_CP(str.c_str(), CP_UTF8), (int)(str.size() * sizeof(unsigned short)), &stmt, NULL);
#else
   int rc = sqlite3_prepare16_v2(db, str.c_str(), (int)(str.size() * sizeof(unsigned short)), &stmt, NULL);
#endif
   if( rc != SQLITE_OK ) return false;

   while( sqlite3_step(stmt) == SQLITE_ROW )
   {
      const wchar_t *colName = W16_32((const wchar_t *)sqlite3_column_text16(stmt, 1));
      const wchar_t *colType = W16_32((const wchar_t *)sqlite3_column_text16(stmt, 2));

      FieldDef fd;
      fd.name = colName;
      fd.type = StringToFieldType(colType);

      push_back(fd);
   }
   sqlite3_finalize(stmt);
   return true;
}

TableDef::const_iterator TableDef::FindField(const std::wstring& name)
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( i->name.compare(name) == 0 )
         break;
   }

   return i;
}

bool TableDef::AlterTable(const ObjectDef& objDef)
{
   if( !GetFieldsDef() ) return false;

   std::vector<ObjectDef::Field> added;

   ObjectDef::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      if( FindField(fi->format.name) == end() )
         added.push_back(*fi);
   }

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      IObjectData::Field f;
      MemberFormat& mf = f.format;
      mf.name = SENDED_FIELDS;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      f.width = 0;
      if( FindField(SENDED_FIELDS) == end() )
         added.push_back(f);
   }

   if (objDef.IsOrderedSource())
   {
      if (FindField(ORDERED_FIELD) == end())
      {
         IObjectData::Field f;
         MemberFormat& mf = f.format;
         mf.name = ORDERED_FIELD;
         mf.type = MemberFormat::mtNumber;
         mf.format.fraction = 0;
         added.push_back(f);
      }
   }

	bool ret = true;
   if( added.size() > 0 )
      ret = AddFields(added);

   return ret;
}

static void DoReplace(std::wstring *str, wchar_t src, wchar_t rpl)
{
	std::wstring::size_type sp = 0;
	std::wstring::size_type fnd = str->find(src, sp);
	while( fnd != std::wstring::npos )
	{
		str->replace(fnd, 1, 1, rpl);
		sp = fnd + 1;
		fnd = str->find(src, sp);
	}
}

static bool CreateIndex(const std::wstring& tableName, const std::wstring& fields, bool unique)
{
	bool quoted = (*fields.begin() == L'"');
	std::wstring indexName( (quoted) ? fields.substr(1, fields.size() - 2) : fields);
	DoReplace(&indexName, L',', L'_');

   vector<wstring> indexFields;
	PKToList(&indexFields, fields);

	indexName = tableName + ((unique) ? L"_U_" : L"_") + indexName;
	std::wstring sql(L"CREATE ");
	if( unique )
		sql += L"UNIQUE ";
	sql += L"INDEX \""; sql += indexName + L"\" ON \""; sql += tableName; sql += L"\" (";

   vector<wstring>::const_iterator i = indexFields.begin();
   for( ; i != indexFields.end(); i++ )
   {
      if( i != indexFields.begin() )
			sql += L",";

      sql += L"\""; sql += (*i); sql += L"\"";
	}
	sql += L")";
	return Execute(sql);
}

static bool CheckDBFormat()
{
   bool res = true;
   //std::vector<std::wstring> names;
   CVector<CString> *names = NULL;
   ObjectDef::GetObjectsName(&names, IObjectDef::Internal);

   USES_CONVERSION;

   //std::vector<std::wstring>::const_iterator i = names.begin();
   CVector<CString>::const_iterator i = names->begin();
   for( ; res && i != names->end(); i++ )
   {
      const ObjectDef* objDef = ObjectDef::Get((const std::wstring&)(*i));
      if( !IsTableExists(objDef->tableName) )
         res = CreateTable(*objDef);
      else
      {
         TableDef tableDef(objDef->tableName);
         res =  tableDef.AlterTable(*objDef);
      }

      if( !res )
         gServer->AddError(false, "SQLite error while creating %s", W2A_CP(i->c_str(), CP_UTF8));
		else
		{
			ObjectDef::MemberArray::const_iterator keyI = objDef->memberArray.find(INDEX_KEY_STR);
			if( keyI != objDef->memberArray.end() )
			{
				ObjectDef::ValueList::const_iterator valI = keyI->second.begin();
				for( ; valI != keyI->second.end(); valI++)
					CreateIndex(objDef->tableName, (*valI), false);
			}

			keyI = objDef->memberArray.find(UNIQUE_INDEX_KEY_STR);
			if( keyI != objDef->memberArray.end() )
			{
				ObjectDef::ValueList::const_iterator valI = keyI->second.begin();
				for( ; valI != keyI->second.end(); valI++)
					CreateIndex(objDef->tableName, (*valI), true);
			}
		}
	}
   delete names;

   Execute("COMMIT;");
   return res;
}

bool SQLiteInternal::Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config)
{
	//MessageBox(NULL, L"", L"", MB_OK);

   bool res = false;
   if( !config.serverBase.empty() )
   {
      std::string fileName;
      MakeFullFileName(&fileName, config.serverBase.c_str(), config.ConfigFolder());

      USES_CONVERSION;
      const wchar_t *fn = A2W_CP(fileName.c_str(), CP_UTF8);

		if (sqlite3_open16(W32_16(fn), &db) == SQLITE_OK)
      {
			sqlite3_exec(db, "PRAGMA journal_mode=WAL;", NULL, NULL, NULL);

			if (config.noCheckFormat) res = true;
			else
			{
				if (CheckDBFormat())
				{
					sqlite3_config(SQLITE_CONFIG_SERIALIZED);
					::Execute("PRAGMA foreign_keys = ON");
					res = true;
				}
			}

         if( res && !IsTableExists(L"ServerData") )
         {
            char *errMsg = NULL;
            char sql[] = "CREATE TABLE ServerData(id INTEGER, data BLOB, CONSTRAINT pkServerData PRIMARY KEY (id) )";
            res = (sqlite3_exec(db, sql, NULL, NULL, &errMsg) == SQLITE_OK);
            sqlite3_free(errMsg);
         }
      }
      gServer->AddLog(IErrorLogger::Full, "DB created");
   }

   if( !res )
   {
      gServer->AddError(true, "Can't create DB '%s'", config.serverBase.c_str());
   } else 
   {
      gServer->AddLog("Slite Thread Safe Mode %d", sqlite3_threadsafe());
   }
   return res;
}

IBinary* SQLiteInternal::GetServerData(int id)
{
   Binary *dest = NULL;
   sqlite3_stmt *stmt;

   char buf[100];
   wsprintfA(buf, "SELECT data FROM ServerData WHERE id=%d", id);

   int rc = sqlite3_prepare_v2(db,  buf, -1, &stmt, NULL);
   if( rc == SQLITE_OK )
   {
      rc = sqlite3_step(stmt);
      if( rc == SQLITE_ROW )
      {
         DWORD size = sqlite3_column_bytes(stmt, 0);
         if( size > 0 )
         {
            dest = new Binary();
            memcpy(dest->Alloc(size), sqlite3_column_blob(stmt, 0), size);
         }
      }
   }
   sqlite3_finalize(stmt);
   return new MemoryBinary(dest);
}

bool SQLiteInternal::PutServerData(int id, const Binary& data)
{
   bool res = false;
   char buf[100];
   wsprintfA(buf, "INSERT OR REPLACE INTO ServerData (id, data) VALUES (%d, ?1)", id);

   sqlite3_stmt *stmt;
   int rc = sqlite3_prepare_v2(db,  buf, -1, &stmt, NULL);
   if( rc == SQLITE_OK )
   {
      sqlite3_bind_blob(stmt, 1, (const BYTE*)data, data.Size(), SQLITE_STATIC);
      res = (sqlite3_step(stmt) == SQLITE_DONE);
   }
   sqlite3_finalize(stmt);

   return res;
}

void SQLiteInternal::Close()
{
   if( db != NULL )
   {
      sqlite3_close(db);
      db = NULL;
   }
}

IInternalDataSource* GRServer::CreateSQLiteSource()
{
   return new SQLiteInternal();
}

class GroupHolder
{
public:
   GroupHolder() {}
   ~GroupHolder()
   {
      std::vector<GroupKey>::iterator i = fields.begin();
      for( ; i != fields.end(); i++ )
         delete i->key;

   }

   bool Prepare(const wchar_t* groupExpr, const std::vector<std::wstring>& columns, sqlite3_stmt *stmt);

   bool IsNew(sqlite3_stmt *stmt)
   {
      if( fields.size() == 0 )
         return true;

      bool ret = false;
      std::vector<GroupKey>::iterator i = fields.begin();
      for( ; i != fields.end(); i++ )
      {
         if( i->IsNew(stmt) )
            ret = true;
      }

      return ret;
   }

   struct Key
   {
      Key(int _index) : index(_index) {}
      virtual ~Key() {}

      // ���� ����� - �� ����� ���������
      virtual bool IsNew(sqlite3_stmt *stmt) = 0;

      int index;
   };

   struct GroupKey
   {
      Key* key;

      bool IsNew(sqlite3_stmt *stmt) { return key->IsNew(stmt); }
   };

   struct KeyInt : public Key
   {
      KeyInt(int index) : Key(index), inited (false) {}

      virtual bool IsNew(sqlite3_stmt *stmt)
      {
         __int64 v = sqlite3_column_int64(stmt, index);

         bool ret = (!inited || (v != value));
         inited = true;
         value = v;

         return ret;
      }

      __int64 value;
      bool inited;
   };

   struct KeyDbl : public Key
   {
      KeyDbl(int index) : Key(index), inited (false) {}

      virtual bool IsNew(sqlite3_stmt *stmt)
      {
         double v = sqlite3_column_double(stmt, index);

         bool ret = (!inited || (v != value));
         inited = true;
         value = v;

         return ret;
      }

      double value;
      bool inited;
   };

   struct KeyText : public Key
   {
      KeyText(int index) : Key(index) {}

      virtual bool IsNew(sqlite3_stmt *stmt)
      {
         const char* v = (const char*)sqlite3_column_text(stmt, index);
         bool ret = (value.empty() || value.compare(v) != 0);
         value = v;
         return ret;
      }

      std::string value;
   };

private:
   std::vector<GroupKey> fields;
};

bool GroupHolder::Prepare(const wchar_t* groupExpr, const std::vector<std::wstring>& columns, sqlite3_stmt *stmt)
{
   std::wstring::size_type sp = 0;
   std::wstring grp(groupExpr);
   while(true)
   {
      std::wstring::size_type ep = grp.find_first_of(L',', sp);
      std::wstring tval = grp.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

      for( unsigned i=0; i<columns.size(); i++)
      {
         if( _wcsicmp(tval.c_str(), columns.at(i).c_str()) == 0 )
         {
            GroupKey gk;
            gk.key = NULL;
            int type = sqlite3_column_type(stmt, i);
            switch( type )
            {
            case SQLITE_INTEGER:
               gk.key = new KeyInt(i);
               break;
            case SQLITE_FLOAT:
               gk.key = new KeyDbl(i);
               break;
            case SQLITE_BLOB:
               break;
            //case SQLITE3_TEXT:
            default:
               gk.key = new KeyText(i);
               break;
            }

            if( gk.key != NULL )
               fields.push_back(gk);
         }
      }

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }

   return true;
}

class RecBinder
{
public:
   RecBinder() {}

   ~RecBinder()
   {
      std::vector<Field>::iterator i = fields.begin();
      for( ; i != fields.end(); i++ )
         delete i->field;
   }

   bool Prepare(const std::vector<std::wstring>& columns, GRServer::Format* format);
   
   bool Read(Object* o, sqlite3_stmt *stmt) const
   {
      bool ret = true;
      std::vector<Field>::const_iterator i = fields.begin();
      for( ; i != fields.end(); i++ )
         if( !i->field->Read(o, stmt,i->index) )
            ret = false;

      return ret;
   }

   struct Field
   {
      FieldBinder* field;
      int index;
   };

private:
   std::vector<Field> fields;
};

bool RecBinder::Prepare(const std::vector<std::wstring>& columns, GRServer::Format* format)
{
   int fidx = 0;
   GRServer::Format::iterator fi = format->begin();
   for( ; fi != format->end(); fi++, fidx++ )
   {
      std::wstring column(fi->name);
      size_t pos = column.find('@');
      if( pos != std::wstring::npos )
      {
         column = column.substr(pos+1);
         fi->name.erase(pos);
      }

      for( unsigned i = 0; i<columns.size(); i++ )
      {
         if( _wcsicmp(column.c_str(), columns[i].c_str()) == 0 )
         {
            Field f;
            f.field = FieldBinder::Create(fidx, *format);
				if (f.field != NULL)
				{
					f.index = i;
					fields.push_back(f);
				}
            break;
         }
      }
   }

   //int index = 0;
   //std::vector<std::wstring>::const_iterator ci = columns.begin();
   //for( ; ci != columns.end(); ci++, index++ )
   //{
   //   int fi = format.FindMember(ci->c_str());
   //   if( fi >= 0 )
   //   {
   //      Field f;
   //      f.field = FieldBinder::Create(fi, format);
   //      f.index = index;

   //      fields.push_back(f);
   //   }
   //}

   return true;
}


class RecWriter
{
public:
   RecWriter() { current = NULL; child = NULL; }

   bool Prepare(const std::vector<std::wstring>& columns, GRServer::Format* fmt, const wchar_t* groupExpr, sqlite3_stmt* stmt)
   {
      bool res = true; 
      if( !binder.Prepare(columns, fmt) )
         res = false;

      if( !group.Prepare(groupExpr, columns, stmt) )
         res = false;

      return res;
   }

   ~RecWriter() { delete child; }

   void SetChild(RecWriter* ch, int index, const std::wstring& name, ISession* session)
   {
      child = ch;
      childIndex = index;
      childName = name;
      this->session = session;
   }

   bool HaveChild() const { return (child != NULL); }

   void Do(sqlite3_stmt *stmt, bool forceNew, ServObject* obj);

private:
   GroupHolder group;
   RecBinder binder;
   Object* current;

   RecWriter* child;
   int childIndex;
   std::wstring childName;
   ISession* session;
};

void RecWriter::Do(sqlite3_stmt *stmt, bool forceNew, ServObject* obj)
{
   if( group.IsNew(stmt) )
      forceNew = true;

   if( forceNew )
   {
      current = obj->AddObject();
      binder.Read(current, stmt);

      if( HaveChild() )
         current->at(childIndex).object = session->CreateObject(childName, false)->Self();
   }

   if( HaveChild() )
   {
      child->Do(stmt, forceNew, current->at(childIndex).object);
   }
}

static void ReadColumns(std::vector<std::wstring> *columns, sqlite3_stmt *stmt)
{
   USES_CONVERSION;
   // create objects
   int nCol = sqlite3_column_count(stmt);
   for( int i=0; i<nCol; i++ )
   {
      const wchar_t* name = A2W_CP(sqlite3_column_name(stmt, i), CP_UTF8);
      //SQLITE_INTEGER, SQLITE_FLOAT, SQLITE_BLOB, SQLITE3_TEXT
      //int colType = sqlite3_column_type(stmt, i);
      columns->push_back(name);
   }
}

static bool PrepareRecWriter(RecWriter* mainWriter, const std::vector<std::wstring> &columns, GRServer::Format* format, 
                             ISession *session, wchar_t* groupExpr, sqlite3_stmt* stmt)
{
   bool ret = true;
   wchar_t* p = wcschr(groupExpr, L';');
   if( p != NULL )
      *p = L'\0';

   mainWriter->Prepare(columns, format, groupExpr, stmt);

   int index = 0;
   GRServer::Format::const_iterator fi = format->begin();
   for( ; fi != format->end(); fi++, index++ )
   {
      if( fi->type == MemberFormat::mtObject )
      {
         if( *groupExpr == L'\0' || mainWriter->HaveChild() )
         {
            ret = false;
            break;
         }

         RecWriter *child = new RecWriter();
         std::wstring name = format->name;
         name += L"$";
         name += fi->name;
         mainWriter->SetChild(child, index, name, session);

         GRServer::Format *chf = session->GetFormatList()->GetFormat(name);
         if( chf == NULL )
         {
            ret = false;
            break;
         }
         ret = PrepareRecWriter(child, columns, chf, session, (p != NULL) ? (wchar_t*)p+1 :(wchar_t*) L"", stmt);
      }
   }

   return ret;
}

ISessionObject* SQLiteInternal::Query(const wchar_t* wstmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session)
{
   GRServer::Format *format = session->RegisterType(typeDef, false);
   if( format == NULL )
      return NULL;

   ISessionObject* so = NULL;
	so = session->CreateObject(format->name, false);
	CString dest, src;
	src.assign(wstmt);
	so->PrepareFilterStr(&dest, src);
	sqlite3_stmt *stmt;

#ifdef UNIX
	USES_CONVERSION;
   int rc = sqlite3_prepare_v2(db, W2A_CP(dest.c_str(), CP_UTF8), -1, &stmt, NULL);
#else
   int rc = sqlite3_prepare16_v2(db, dest.c_str(), -1, &stmt, NULL);
#endif

   if( rc == SQLITE_OK )
   {
      std::vector<std::wstring> columns;
      ReadColumns(&columns, stmt);

      wchar_t* grp = _wcsdup(groupExpr);
      RecWriter writer;
      bool res = PrepareRecWriter(&writer, columns, format, session, grp, stmt);
      free(grp);

      if( res )
      {
         while( (rc = sqlite3_step(stmt)) == SQLITE_ROW )
         {
            writer.Do(stmt, false, so->Self());
         }
      }
   }
	else
	{
		USES_CONVERSION;
		gServer->AddError(false, "Query error %s", sqlite3_errmsg(db));
		gServer->AddError(false, "Query is %s", W2A_CP(dest.c_str(), CP_UTF8));
	}

   sqlite3_finalize(stmt);
   return so;
}

static void GetTableNameW(std::wstring* tableName, const ParamList& parameters, const SessionObject& object)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
      tableName->assign(od->tableName);
   else
      tableName->assign(object.Name());
}

IDataSource::IReader* SQLiteSourceCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::wstring tableName;
   GetTableNameW(&tableName, parameters, object);

   if( object.Parent() != NULL )
      return new SQLiteChildReader(object, tableName);

   std::vector<std::wstring> filters;
   parameters.Load(&filters, L"readFilter", iobject);

	const Parameter *dbg = parameters.Find(L"debug", -1);
	ParamHelper *defaults = new ParamHelper(NULL);
	defaults->Read(parameters, &object.GetSession(), &object, gServer);

	CString* whereFilter = NULL;
	const Parameter *whF = parameters.Find(L"whereFilter", -1);
	if (whF != NULL)
	{
		bool needAssign = true;
		Session& s = ((Session&)iobject.GetSession());
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
				gServer->AddLog("Error while parse whereFilter of %s", W2A_CP(object.Name().c_str(), CP_UTF8));
			}
		}
	}

	CString *stmt = NULL;
	const Parameter* p = parameters.Find(L"stmt", -1);
	if (p != NULL)
	{
		if (!object.GetSession().Parse(&stmt, p->value, &object))
		{
			gServer->AddError(false, "SQTable error parsing stmt");
			delete stmt;
			return NULL;
		}
	}

	IDataSource::IReader* ret = new SQLiteReader(object, tableName, defaults, whereFilter, filters, stmt, (dbg != NULL));

	delete whereFilter;
	delete stmt;
	return ret;
}

IDataSource::IWriter* SQLiteSourceCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::wstring tableName;
   GetTableNameW(&tableName, parameters, object);

   if( parent != NULL )
      return new SQLiteChildWriter(object, tableName);

   return new SQLiteWriter(tableName);
}

IDataSource::IRemover* SQLiteSourceCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::wstring tableName;
   GetTableNameW(&tableName, parameters, object);

   if( parent != NULL )
      return NULL; // foreign key constraint

   return new SQLiteRemover(object, tableName);
}

//
//------------------------------------------ Binder ----------------------------------------------------
//
FieldBinder* FieldBinder::Create(int oi, const GRServer::Format& objFormat)
{
   const std::wstring& name = objFormat.at(oi).name;
   return Create(name, objFormat);
}

FieldBinder* FieldBinder::Create(const std::wstring& name, const GRServer::Format& objFormat)
{
   FieldBinder *ret = NULL;

   int oi = objFormat.FindMember(name.c_str());
   if( oi >= 0 )
   {
      const MemberFormat& mf = objFormat.at(oi);
      switch( mf.type )
      {
      case MemberFormat::mtString:
         ret = new StringBinder(oi, name);
         break;
      case MemberFormat::mtNumber:
         ret = (mf.format.fraction == 0) ?
            (FieldBinder*)new IntegerBinder(oi, name) :
            (FieldBinder*)new DoubleBinder(oi, name);
         break;
      case MemberFormat::mtDateTime:
         ret = new DateBinder(oi, name);
         break;
      case MemberFormat::mtBinary:
         ret = new BinaryBinder(oi, name);
         break;
      default: break;
      }
   }
   return ret;
}

void Binder::Clear()
{
   value.str = NULL;

   std::vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      if( (*i) != &ridParam )
         delete (*i);
   }
   fields.clear();

   std::vector<FileField*>::iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      delete (*fi);
   files.clear();
}

bool Binder::Prepare(sqlite3_stmt* stmt, const IObjectData* od, const GRServer::Format& format)
{
   USES_CONVERSION;

	Clear();
	for( int i=0; i<sqlite3_column_count(stmt); i++ )
	{
		FieldBinder* fb = NULL;

#ifdef UNIX
		const wchar_t *colName = A2W_CP((const char *)sqlite3_column_name(stmt, i), CP_UTF8);
#else
		const wchar_t *colName = (const wchar_t *)sqlite3_column_name16(stmt, i);
#endif

      IObjectData::Fields::const_iterator fi = od->fields.begin();
      for( ; fi != od->fields.end(); fi++ )
      {
         if(_wcsicmp(colName, fi->data.c_str()) == 0)
         {
				int idx = format.FindMember(fi->format.name.c_str());
				if( idx >= 0 )
					fb = FieldBinder::Create(idx, format);
				break;
			}
		}

		if( fb == NULL )
			fb = new EmptyBinder(0, colName);
		fields.push_back(fb);
	}

	return true;
}

bool Binder::Prepare(const SessionObject& object)
{
   Clear();

   const IObjectData* od = object.GetObjectDef();
   if( od == NULL ) return false;

   ObjectDef::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      if( fi->CanCreate() )
      {
         FieldBinder* fb = FieldBinder::Create((*fi).format.name, *object.format);
         if( fb != NULL )
            fields.push_back(fb);
			else
				fields.push_back(new EmptyBinder(0, (*fi).format.name));
      } else if( (fi->flags & ObjectDef::Field::File) != 0 && !fi->src.empty() )
      {
         int srcidx = object.format->FindMember(fi->src.c_str());
         int meidx = object.format->FindMember(fi->format.name.c_str());
         if( srcidx >= 0 && meidx >= 0 && object.format->at(srcidx).type == MemberFormat::mtString )
         {
            files.push_back(new FileField(srcidx, meidx, gServer->GetConfig().ImageFolder(), gServer));
         }
      }
   }

   if (od->IsOrderedSource())
   {
      orderBinder = new OrderIndexBinder();
      fields.push_back(orderBinder);
   }

   return true;
}

bool Binder::PrepareForeignKey(std::wstring* whereStr, const SessionObject& object)
{
   CVector<MemberFormat>* fkFields = NULL;
   const IObjectData* od = object.GetObjectDef();
   const ISessionObject* parentObjI = object.Parent();
   const SessionObject* parentObj = (parentObjI ==NULL) ? NULL : (const SessionObject*)parentObjI->Self();

   if( parentObj == NULL || !od->LoadFK(&fkFields) )
   {
      delete fkFields;
      return false;
   }

   //whereStr->append(L" WHERE (");

   wchar_t buf[50];
   int index = 1;
   vector<MemberFormat>::const_iterator i = fkFields->begin();
   for( ; i != fkFields->end(); i++ )
   {
      size_t pos = i->name.find_last_of(L'$');
      const std::wstring& pname = i->name.substr(pos+1);

      FieldBinder *fb = FieldBinder::Create(pname, *parentObj->format);
      if( fb == NULL && fkFields->size() == 1 && (*i).type == MemberFormat::mtNumber )
      {
         ridParam.Name() = pname;
         fb = &ridParam;
      }

      if( fb != NULL )
      {
         if( index > 1 ) whereStr->append(L" AND ");
         whereStr->append(i->name);
         wsprintfW(buf, L" = ?%d", index++);
         whereStr->append(buf);

         fb->Name() = i->name;
         Add(fb);
      }
   }
   //whereStr->append(L")");

   delete fkFields;
   return true;
}

void Binder::MakeInsertParams(std::wstring* param) const
{
   wstring clmns, vls;
   MakeInsertParams(&clmns, &vls, 1);

   std::wstring addStr;
   param->append(L"(");
   param->append(clmns);
   param->append(L") VALUES (");
   param->append(vls);
   param->append(L")");
}

void Binder::MakeInsertParams(std::wstring* columns, std::wstring* values, int startWith) const
{
   wchar_t buf[20];
   int ctr = startWith;

   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      if( ctr != startWith ) { columns->append(L","); values->append(L","); }

      columns->append(1, L'"').append((*i)->Name()).append(1, L'"');

      wsprintf(buf, L"?%d", ctr++);
      values->append(buf);
   }
}

void Binder::MakeSelectParams(std::wstring* param) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      if( i != fields.begin() ) param->append(L",");
      param->append(L"[");
      param->append((*i)->Name());
      param->append(L"]");
   }

}

bool Binder::Write(sqlite3_stmt *stmt, const Object& object, int startWith)
{
   bool ret = true;

   if (orderBinder)
      orderBinder->index++;

   int index = startWith;
   std::vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++, index++ )
   {
      if( (*i)->Write(stmt, object, index) == false )
      {
         ret = false;
         break;
      }
   }

   if( ret )
   {
      std::vector<FileField*>::iterator fi = files.begin();
      for( ; fi != files.end(); fi++ )
			if (!(*fi)->WriteFile(object))
			{
				ret = false;
				gServer->AddError(false, "Error while writing file");
			}
   }

   return ret;
}

bool Binder::Read(Object* o, sqlite3_stmt *stmt) const
{
   bool ret = true;

   int index = 0;
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++, index++ )
   {
      if( (*i)->Read(o, stmt, index) == false )
      {
         ret = false;
         break;
      }
   }

   //if( ret )
   //{
   //   std::vector<FileField*>::const_iterator fi = files.begin();
   //   for( ; fi != files.end(); fi++ )
   //      if( !(*fi)->ReadFile(o) )
   //         gServer->AddError(false, "Error while reading file");
   //}
   return ret;
}

const MemberFormat* Binder::FieldType(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->Type(&format);
         return &format;
      }
   }

   return NULL;
}

const Member* Binder::Value(sqlite3_stmt* stmt, const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   int index = 0;
   for( ; i != fields.end(); i++, index++ )
   {
      const FieldBinder* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->Value(&value, stmt, index);
         return &value;
      }
   }

   return NULL;
}

//
//------------------------------------------ SQLiteWriter ----------------------------------------------------
//
SQLiteWriter::SQLiteWriter(const std::wstring& tableName) : stmt(NULL), doCount(0)
{
   this->tableName = tableName;
}

SQLiteWriter::~SQLiteWriter()
{
   Close();
}

void MakeOnConflict(std::wstring* stmt, const std::vector<std::wstring>& keyFields, const vector<FieldBinder*> &fields)
{
   if (keyFields.size() == 0)
      return;
   std::set<std::wstring> kf;
   std::vector<std::wstring>::const_iterator i = keyFields.begin();
   stmt->append(L" ON CONFLICT (");

   for (; i != keyFields.end(); i++)
   {
      kf.insert(*i);
      stmt->append(1, L'"').append(*i).append(1, L'"').append(L",");
   }

   stmt->erase(stmt->size() - 1);
   if (keyFields.size() == fields.size())
      stmt->append(L") DO NOTHING");
   else
   {
      stmt->append(L") DO UPDATE SET ");

      vector<FieldBinder*>::const_iterator fi = fields.begin();
      for (; fi != fields.end(); fi++)
      {
         const std::wstring& name = (*fi)->Name();
         if (std::find(kf.begin(), kf.end(), name) != kf.end())
            continue;

         stmt->append(1, L'"').append(name).append(1, L'"').append(L"=EXCLUDED.")
            .append(1, L'"').append(name).append(1, L'"').append(L",");
      }
      stmt->erase(stmt->size() - 1);
   }
}

// INSERT OR REPLACE INTO tableName(col1 [, coli]) VALUES (?1 [, ?i])
bool SQLiteWriter::Prepare(const ISessionObject &iobject)
{
	const SessionObject& object = *(const SessionObject*)iobject.Self();
   const IObjectData* od = object.GetObjectDef();
   if(od == NULL || !IsTableExists(od->tableName)) return false;

   vector<wstring> keyFields;
   ObjectDef::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
   if (keyI != od->members.end())
      PKToList(&keyFields, keyI->second);

   USES_CONVERSION;
   bool ret = false;

   if( binder.Prepare(object) )
   {
      //std::wstring sql(L"INSERT OR REPLACE INTO \"");
      std::wstring sql(L"INSERT INTO \"");
      sql += tableName;
      sql += L"\" ";
      binder.MakeInsertParams(&sql);
      MakeOnConflict(&sql, keyFields, binder.Fields());

      doCount = 0;

      if( stmt != NULL ) sqlite3_finalize(stmt);
#ifdef UNIX
      ret = (sqlite3_prepare_v2(db, W2A_CP(sql.c_str(), CP_UTF8), (int)(sql.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#else
      ret = (sqlite3_prepare16_v2(db, sql.c_str(), (int)(sql.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#endif      
		if (ret)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "Run stmt %s", W2A_CP(sql.c_str(), CP_UTF8));

			Execute("BEGIN;");
		}
      else
      {
         USES_CONVERSION;
         gServer->AddError(false, "sqlite3_prepare error %s: %s stmt %s", 
            W2A_CP(object.Name().c_str(), CP_UTF8),
            sqlite3_errmsg(db),
            W2A_CP(sql.c_str(), CP_UTF8)
         );
      }
   }

   return ret;
}

bool SQLiteWriter::Write(const Object &o, RowID *rid)
{
   bool ret = false;
   if( stmt != NULL && binder.Write(stmt, o) )
   {
      ret = (sqlite3_step(stmt) == SQLITE_DONE);

      if( ret )
      {
         RowID liRid = sqlite3_last_insert_rowid(db);
         if( rid )
            *rid = liRid;

         WriterList::iterator ci = childs.begin();
         for( ; ret && ci != childs.end(); ci++ )
            ret = (*ci)->Write(o, &liRid);
		}
		else 
		{
			USES_CONVERSION;
			gServer->AddError(false, "write sqlite3_step %s: %s", W2A_CP(o.format.name.c_str(), CP_UTF8), sqlite3_errmsg(db));
		}

      sqlite3_reset(stmt);

      if( doCount++ > MAX_DO_COUNT )
      {
         doCount = 0;
         Execute("COMMIT;");
         Execute("BEGIN;");
      }
   }

   return ret;
}

void SQLiteWriter::Close()
{
   if( stmt != NULL )
   {
      Execute("COMMIT;");
      sqlite3_finalize(stmt);
      stmt = NULL;
   }
   binder.Clear();
}

//
//------------------------------------------ SQLiteChildWriter ----------------------------------------------------
//
SQLiteChildWriter::SQLiteChildWriter(const SessionObject& object, const std::wstring& tableName) : 
   SQLiteWriter(tableName), rmvStmt(NULL), childIndex(-1)
{
   const ISessionObject* parentObjI = object.Parent();
   const SessionObject* parentObj = (parentObjI == NULL) ? NULL : (const SessionObject*)parentObjI->Self();
   if( parentObj != NULL )
   {
      ObjectSource* os = parentObj->GetSource();
      if( os != NULL && os->writerName.compare(SQLiteSourceCreator().Name()) == 0 )
      {
//         const IObjectData* od = object.GetObjectDef();
         std::wstring rmvSql(L"DELETE FROM \"");

         rmvSql += tableName;
         rmvSql += L"\" WHERE (";
         parent.PrepareForeignKey(&rmvSql, object);
			rmvSql += L" )";

         binder.Prepare(object);

         const wstring& oname = object.Name();
         size_t off = oname.find_last_of(L'$');
         childIndex = parentObj->format->FindMember(oname.substr(off+1).c_str());

         std::wstring clmns, vls;
         binder.MakeInsertParams(&clmns, &vls, 1);
         clmns += L",";
         vls += L",";
         parent.MakeInsertParams(&clmns, &vls, binder.Count()+1);

         std::wstring sql(L"INSERT OR REPLACE INTO \"");
         sql += tableName;
         sql += L"\" (";
         sql += clmns;
         sql += L") VALUES (";
         sql += vls;
         sql += L")";

#ifdef UNIX
         USES_CONVERSION;
         sqlite3_prepare_v2(db, W2A_CP(sql.c_str(), CP_UTF8), (int)(sql.size() * sizeof(unsigned short)), &stmt, NULL);
         sqlite3_prepare_v2(db, W2A_CP(rmvSql.c_str(), CP_UTF8), (int)(rmvSql.size() * sizeof(unsigned short)), &rmvStmt, NULL);
#else
         sqlite3_prepare16_v2(db, sql.c_str(), (int)(sql.size() * sizeof(unsigned short)), &stmt, NULL);
         sqlite3_prepare16_v2(db, rmvSql.c_str(), (int)(rmvSql.size() * sizeof(unsigned short)), &rmvStmt, NULL);
#endif
      }
   }
}

void SQLiteChildWriter::Close()
{
   if( rmvStmt != NULL )
   {
      sqlite3_finalize(rmvStmt);
      rmvStmt = NULL;
   }
   parent.Clear();
   SQLiteWriter::Close();
}

bool SQLiteChildWriter::Write(const Object& o, RowID *rid)
{
   bool res = true;
   if( stmt != NULL  )
   {
      const Member& m = o.at(childIndex);
      if( m.object != NULL )
      {
         parent.SetParentID((rid != NULL) ? *rid : NO_ROWID);

         parent.Write(rmvStmt, o, 1);
         sqlite3_step(rmvStmt);
         sqlite3_reset(rmvStmt);

         //res = true;
         binder.ClearOrderIndex();

         ServObject::const_iterator i = m.object->begin();
         for( ; res && i != m.object->end(); i++ )
         {
            parent.Write(stmt, o, binder.Count() + 1);
            res = SQLiteWriter::Write(*(*i), NULL);
         }
      }
   }

   return res;
}

//
//------------------------------------------ SQLiteReader ----------------------------------------------------
//
SQLiteReader::SQLiteReader(const SessionObject &object, const std::wstring& tableName, ParamHelper *defaults, 
   const CString* whereFilter, const vector<wstring>& _filters, const CString* stmtStr, bool debug) :
   sessionObject(object), stmt(NULL), params(defaults), filters(_filters)
{
	this->debug = debug;
   this->tableName = tableName;

   if (stmtStr != NULL)
	{
		this->sqlStr = stmtStr->c_str();
	}
	else
	{
		if (whereFilter)
		{
			this->whereFilter = whereFilter->c_str();
		}
	}
}

bool SQLiteReader::Prepare(const SessionObject& object, const wchar_t* whereStmt)
{
   std::wstring parsedFilter;

   bool ret = false;
	if (!sqlStr.empty()) 
	{
#ifdef UNIX
		USES_CONVERSION;
		ret = (sqlite3_prepare_v2(db, W2A_CP(sqlStr.c_str(), CP_UTF8), (int)(sqlStr.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#else
		ret = (sqlite3_prepare16_v2(db, sqlStr.c_str(), (int)(sqlStr.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#endif      
		ret = (ret && binder.Prepare(stmt, object.GetObjectDef(), *object.format));
	} else if( binder.Prepare(object) )
   {
		sqlStr = L"SELECT ";

		binder.MakeSelectParams(&sqlStr);
		sqlStr += L" FROM \"";
		sqlStr += tableName;
		sqlStr += L"\" ";
		if (*whereStmt || !whereFilter.empty())
		{
			if (*whereStmt)
			{
				CString* res = params.Substitute(whereStmt);
             
				parsedFilter += L"(";
				parsedFilter += res->c_str();
				delete res;
				parsedFilter += L" )";
			}
			if (!whereFilter.empty())
			{
				CString dest;
				object.PrepareFilterStr(&dest, whereFilter);

				if (!parsedFilter.empty())
					parsedFilter += L" AND ";
				parsedFilter += L"(";
				parsedFilter += (const std::wstring&)dest;
				parsedFilter += L")";
			}

			sqlStr += L" WHERE ";
         sqlStr += parsedFilter;
		}

      const IObjectData* od = object.GetObjectDef();
      if (od != NULL && od->IsOrderedSource())
      {
         sqlStr += L" ORDER BY \"";
         sqlStr += ORDERED_FIELD;
         sqlStr += L"\"";
      }

#ifdef UNIX
		USES_CONVERSION;
		ret = (sqlite3_prepare_v2(db, W2A_CP(sqlStr.c_str(), CP_UTF8), (int)(sqlStr.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#else
		ret = (sqlite3_prepare16_v2(db, sqlStr.c_str(), (int)(sqlStr.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#endif

      if( (od != NULL && (od->flags & IObjectDef::RemoveOnCommit) != 0) )
      {
         USES_CONVERSION;
         wchar_t buf[30];
         commitId = GetTickCount();
         wsprintf(buf, L"%u", commitId);
         wstring stmt(L"UPDATE \""); stmt += tableName; stmt += L"\" SET \""; stmt += SENDED_FIELDS; stmt += L"\" = "; stmt += buf;
         if( !parsedFilter.empty() )
         {
            stmt += L" WHERE ";
            stmt += parsedFilter;
         }

         char *errMsg = NULL;
         sqlite3_exec(db, W2A_CP(stmt.c_str(), CP_UTF8), NULL, NULL, &errMsg);
         sqlite3_free(errMsg);

         // gServer->AddLog(IErrorLogger::Full, "Remove stmt %s", W2A_CP(stmt.c_str(), CP_UTF8));                  
   }

		if (debug)
		{
			USES_CONVERSION;
			gServer->AddLog(IErrorLogger::Full, "preparing stmt %s", W2A_CP(sqlStr.c_str(), CP_UTF8));
			if (!ret)
			{
				gServer->AddLog(IErrorLogger::Full, "error stmt %s", sqlite3_errmsg(db));
			}
		}
	}

   return ret;
}


bool SQLiteReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   if( stmt != NULL )
   {
      sqlite3_finalize(stmt);
      stmt = NULL;
   }

	bool haveParams = false;
	if (*filter != L'\0')
	{
		DWORD cbParams = sizeof(L"PARAMS:") - sizeof(wchar_t);
		if (memcmp(filter, L"PARAMS:", cbParams) == 0)
		{
			params.Read((const wchar_t*)(filter + cbParams / sizeof(wchar_t)), &object.GetSession(), &object, (IErrorLogger*)gServer);
			haveParams = true;
		}
	}

	std::wstring dst;
	if (!sqlStr.empty())
	{
		if (haveParams)
		{
			CString* f = params.Substitute(sqlStr.c_str());
			sqlStr.assign(f->c_str());
			delete f;
		}
		CString dest;
		object.PrepareFilterStr(&dest, sqlStr.c_str());
		sqlStr = dest.c_str();
	}
	else
	{
		if (*filter != L'\0')
		{
			if (haveParams)
			{
            return true;
				//CString* f = params.Substitute(rdFilter.c_str(), defaults);
				//dst = f->c_str();
				//delete f;
			}
			else
			{
				dst = filter;
			}

			CString tfilter;
			if (!object.PrepareFilterStr(&tfilter, dst))
				return false;

			dst.assign((const std::wstring&)tfilter);
		}
	}
	return Prepare(sessionObject, dst.c_str());
}

SQLiteReader::~SQLiteReader()
{
   Close();
}

bool SQLiteReader::MoveNext(Object *parentObject)
{
   if( stmt == NULL )
   {
      if (filters.size() > 0)
      {
         const ISession& session = sessionObject.GetSession();

         std::vector<wstring>::const_iterator i = filters.begin();
         for (; i != filters.end(); i++)
         {
            const wchar_t* filter = i->c_str();

            CString dest;
            Token tres;
            if (session.Parse(&tres, *i, &sessionObject) && tres.type == Token::Type::ttString)
            {
               sessionObject.PrepareFilterStr(&dest, *tres.value.str);
               filter = dest.c_str();
            }

            bool res = Prepare(sessionObject, filter);

            if (res && sqlite3_step(stmt) == SQLITE_ROW)
            {
               return true;
            }

            sqlStr.clear();
            sqlite3_finalize(stmt);
            stmt = NULL;
            binder.Clear();
         }
         return false;
      }
      else
      {
         if (!Prepare(sessionObject, L""))
            return false;
      }      
   }
   return (sqlite3_step(stmt) == SQLITE_ROW);
}

bool SQLiteReader::Get(Object* o) const
{
   if( stmt == NULL ) return false;
   return binder.Read(o, stmt);
}

void SQLiteReader::Close()
{
   if( stmt != NULL )
   {
      sqlite3_finalize(stmt);
      stmt = NULL;
   }
   binder.Clear();
}

const MemberFormat* SQLiteReader::Type(const wchar_t* name) const
{
   return binder.FieldType(name);
}

const Member* SQLiteReader::Value(const wchar_t* name) const
{
   return (stmt != NULL) ? binder.Value(stmt, name) : NULL;
}

void SQLiteReader::Remove()
{
   const IObjectData* od = sessionObject.GetObjectDef();

	USES_CONVERSION;
	wchar_t buf[30];

	wsprintf(buf, L"%u", commitId);
   wstring stmt(L"DELETE FROM \""); stmt += od->tableName; stmt += L"\" WHERE \""; stmt += SENDED_FIELDS; stmt += L"\" = "; stmt += buf;

   char *errMsg = NULL;
	const char* pstmt = W2A_CP(stmt.c_str(), CP_UTF8);
	sqlite3_exec(db, pstmt, NULL, NULL, &errMsg);

	gServer->AddLog(IErrorLogger::Full, "Run stmt %s", pstmt);
	if (errMsg)
		gServer->AddLog(IErrorLogger::Full, " error %s", errMsg);

	sqlite3_free(errMsg);
}

//
//------------------------------------------ SQLiteChildReader ----------------------------------------------------
//
SQLiteChildReader::SQLiteChildReader(const SessionObject& object, const std::wstring& tableName ) :
   SQLiteReader(object, tableName, NULL, NULL, vector<wstring>()), childPrepared(false), childIndex(0)
{
   const ISessionObject* parentObjI = object.Parent();
   const SessionObject* parentObj = (parentObjI == NULL) ? NULL : (const SessionObject*)parentObjI->Self();
   if( parentObj != NULL )
   {
      std::wstring whereStr;
      parent.PrepareForeignKey(&whereStr, object);

      Prepare(object, whereStr.c_str());

      childPrepared = false;
   }
}

bool SQLiteChildReader::MoveNext(Object *parentObject)
{
   if( stmt == NULL || parentObject == NULL ) return false;
   if( !childPrepared )
   {
      sqlite3_reset(stmt);
      parent.Write(stmt, *parentObject);
   }

   childPrepared = (sqlite3_step(stmt) == SQLITE_ROW);
   return childPrepared;
}

//
//------------------------------------------ SQLiteRemover ----------------------------------------------------
//
SQLiteRemover::SQLiteRemover(const SessionObject& _object, const std::wstring& tableName) :
   object(_object)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
   {
      sql = L"DELETE FROM \"";
      sql += tableName;
      sql += L"\" ";
   }
}

bool SQLiteRemover::Remove(const wchar_t* filter)
{
   if( sql.empty() ) return false;

   std::wstring dst(sql);
   if( filter != NULL && *filter != L'\0' )
   {
      dst.append( L" WHERE ");
      CString pFilter, src(filter);
      if( !object.PrepareFilterStr(&pFilter, src) )
         return false;
      dst.append((const std::wstring&)pFilter);
   }

   bool ret = true;
   RemoverList::iterator ci = childs.begin();
   for( ; ret && ci != childs.end(); ci++ )
      ret = (*ci)->Remove(filter);

   if( ret )
   {
      sqlite3_stmt *stmt = NULL;

      USES_CONVERSION;
      if( sqlite3_prepare16_v2(db, W32_16(dst.c_str()), (int)(dst.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK )
         ret = (sqlite3_step(stmt) == SQLITE_DONE);

		gServer->AddLog(IErrorLogger::Full, "Run stmt %s", W2A_CP(dst.c_str(), CP_UTF8));

      if( stmt != NULL )
         sqlite3_finalize(stmt);
   }
   return ret;
}

//
//------------------------------------------ SQLiteQuery ----------------------------------------------------
//
IDataSource::IReader* SQLiteQueryCreator::CreateReader(const GRServer::ParamList &parameters, const GRServer::ISessionObject &iobject) const
{
	IDataSource::IReader *ret = NULL;

   const SessionObject& object = *(const SessionObject*)iobject.Self();
   if( object.Parent() != NULL )
	{
		const Parameter* p = parameters.Find(L"keyFields", -1);
		if (p == NULL)
		{
			gServer->AddError(false, "SQLQuery ��� ��������� keyFields");
			return NULL;
		}
		CString *keyFields = NULL;
		if (!object.GetSession().Parse(&keyFields, p->value, &object))
		{
			gServer->AddError(false, "SQLQuery �� ���������� �������� keyFields");
			delete keyFields;
			return NULL;
		}

		ret = new QueryChildReader(*keyFields, object, *object.Parent());
		delete keyFields;
	}
	else
	{
      const Parameter* p = parameters.Find(L"stmt", -1);
      if( p == NULL )
      {
         gServer->AddError(false, "SQLQuery ��� ��������� stmt");
         return NULL;
      }
      CString *stmt = NULL;
      if( !object.GetSession().Parse(&stmt, p->value, &object) )
      {
         gServer->AddError(false, "SQLQuery �� ���������� �������� stmt");
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
		ret = new SQLiteQuery(*stmt, object, debug, rowCount, defaults);
      delete stmt;
	}

	return ret;
}

SQLiteQuery::SQLiteQuery(const CString& query, const SessionObject& _object, bool debug, int rowCount, ParamHelper *defaults) :
	stmt(NULL), object(_object), nextObject(NULL), bof(false), params(defaults)
{
	this->debug = debug;
	this->query = query;
	this->rowCount = rowCount;
	this->curRow = 0;
}

SQLiteQuery::~SQLiteQuery()
{
	delete nextObject;
}

bool SQLiteQuery::MoveNext(Object *parentObject)
{
	if (bof)
		return false;

   if( stmt == NULL )
   {
      if( !Prepare(object) )
         return false;
   }
	if (rowCount > 0 && curRow++ >= rowCount)
		return false;

	if (nextObject != NULL)
		return true;
	
	return (sqlite3_step(stmt) == SQLITE_ROW);
}

bool SQLiteQuery::Get(Object* o) const
{
   if( stmt == NULL || bof) return false;

	if (nextObject != NULL && &nextObject->format == &o->format)
	{
		nextObject->MoveTo(o);
		delete nextObject;
		nextObject = NULL;
		return true;
	}
	return binder.Read(o, stmt);
}

bool SQLiteQuery::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   DWORD cbParams = sizeof(L"PARAMS:") - sizeof(wchar_t);
   if (filter != NULL && memcmp(filter, L"PARAMS:", cbParams) == 0)
   {
      filter = (const wchar_t*)(filter + cbParams / sizeof(wchar_t));
   }
	params.Read((filter == NULL) ? L"" : filter, &object.GetSession(), &object, gServer);
	return true;
}

void SQLiteQuery::Close()
{
   if( stmt != NULL )
   {
      sqlite3_finalize(stmt);
      stmt = NULL;
   }
   binder.Clear();
}

Object* SQLiteQuery::GetNext()
{
	if (bof)
		return NULL;

	if (sqlite3_step(stmt) != SQLITE_ROW)
	{
		bof = true;
		delete nextObject;
		nextObject = NULL;
		return NULL;
	}

	if (nextObject == NULL)
		nextObject = Create(*object.Self()->format);
	binder.Read(nextObject, stmt);
	return nextObject;
}

bool SQLiteQuery::Prepare(const SessionObject& object)
{
   bool ret = false;
	USES_CONVERSION;

	CString* res = params.Substitute(query.c_str());
	query.clear();
	CString tstr;
	ret = object.PrepareFilterStr(&tstr, *res);
	if (!ret)
	{
		gServer->AddLog(IErrorLogger::None, "Error parse todate in query %s", W2A_CP(res->c_str(), CP_UTF8));
		delete res;
		return false;
	}
	delete res;
	query.assign(tstr);

	if (debug)
	{
		gServer->AddLog(IErrorLogger::Full, "Do Query (%d): %s", ((const Session&)object.GetSession()).GetSocket(), 
         W2A_CP(query.c_str(), CP_UTF8));
	}
#ifdef UNIX
	ret = (sqlite3_prepare_v2(db, W2A_CP(query.c_str(), CP_UTF8), (int)(query.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#else
	ret = (sqlite3_prepare16_v2(db, query.c_str(), (int)(query.size() * sizeof(unsigned short)), &stmt, NULL) == SQLITE_OK);
#endif   
	if( !ret )
	{
		gServer->AddLog(IErrorLogger::Short, "Eror while preparing query (%d) %s: %s", ((const Session&)object.GetSession()).GetSocket(), 
         W2A_CP(query.c_str(), CP_UTF8), sqlite3_errmsg(db));
	} else
	{
		ret = binder.Prepare(stmt, object.GetObjectDef(), *object.format);
	}

	return ret;
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

KeyMember* KeyMember::Create(const std::wstring& name, GRServer::Format* format)
{
	KeyMember *ret = NULL;
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
	GRServer::Format *format = object.Self()->format;

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
	SQLiteQuery(object), keyHolder((const std::wstring&)keyFields, _parent), parent(NULL), keyLoaded(false)
{
	ObjectSource *os = _parent.GetSource();
	if (os != NULL && os->reader && os->readerName.compare(SQLiteQueryCreator().Name()) == 0)
	{
		parent = (SQLiteQuery*)os->reader;
	}

	if (parent != NULL)
		parent->AddChildObject(&object);
}

QueryChildReader::~QueryChildReader()
{
}

bool QueryChildReader::MoveNext(Object *parentObject)
{
	if (parentObject == NULL)
		return false;


	bool ret = false;
	if (!keyLoaded)
	{
		stmt = parent->GetStmt();
		binder.Prepare(stmt, object.GetObjectDef(), *object.format);

		keyHolder.Load(*parentObject);
		ret = true;
	}
	else
	{
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

bool QueryChildReader::Get(Object* o) const
{
	return binder.Read(o, stmt);
}

bool SQLiteInternal::BackupBase(const char* backupFileName)
{
	bool ret = true;
	int rc;                     /* Function return code */
	sqlite3 *pFile;             /* Database connection opened on zFilename */
	sqlite3_backup *pBackup;    /* Backup handle used to copy data */

	/* Open the database file identified by zFilename. */
	rc = sqlite3_open(backupFileName, &pFile);
	if (rc == SQLITE_OK)
	{
		pBackup = sqlite3_backup_init(pFile, "main", db, "main");
		if (pBackup)
		{
			do 
			{
				rc = sqlite3_backup_step(pBackup, 25);
				//
				//void(*xProgress)(int, int)
				//xProgress(sqlite3_backup_remaining(pBackup),sqlite3_backup_pagecount(pBackup));
				//
				if (rc == SQLITE_OK || rc == SQLITE_BUSY || rc == SQLITE_LOCKED)
				{
					sqlite3_sleep(10);
				}
			} while (rc == SQLITE_OK || rc == SQLITE_BUSY || rc == SQLITE_LOCKED);

			(void)sqlite3_backup_finish(pBackup);
		}
		rc = sqlite3_errcode(pFile);
		if(rc != SQLITE_OK)
		{
			gServer->AddLog(IErrorLogger::Full, "Backup error %d: %s", rc, sqlite3_errmsg(pFile));
			ret = false;
		}
	}

	(void)sqlite3_close(pFile);

	return ret;
}
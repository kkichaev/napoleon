#pragma once

#include <libpq-fe.h>

#define DEFINE_PLUGIN
#include <vector>
#include <iplugin.h>
#include <iserver.h>
#include <idatasource.h>
#include <isessobj.h>

namespace GRServer {

const int DEFAULT_STRING_LENGTH = 300;

class Config
{
public:
   Config();

   bool Load(const std::string& fileName);
   bool Save(const std::string& fileName);

   void GetConnectionString(std::string* out) const;

private:
   std::string user;
   std::string password;
   std::string host;
   std::string database;

   std::map<std::string, std::string> params;
};

class PostgrePlugin : public IPlugin
{
public:
   PostgrePlugin();
   ~PostgrePlugin();

   virtual const wchar_t* Name() const { return L"ƒрайвер PostgreSQL"; }
   virtual const wchar_t* Version() const { return L"1.0.0.1"; }

   virtual bool Init(IServer* server);
   virtual bool Connect(Socket* socket, const wchar_t* password);
   virtual void Close();

   // этот метод сервер вызывает много раз (определ€€ можно ли конфигурировать plugin
   // конструктор должен быть "легкий"
   virtual IPluginConfig* GetConfig() const;
};

class PGConnection
{
public:
   PGConnection();
   ~PGConnection();

   PGconn* GetConnection();

   void StartTransaction();
   void FinishTransaction(bool commit);

   void Close();

private:
   bool transactionStarted;
   PGconn* conn;
};

class FieldBinder
{
public:
   typedef void (*ConvertValue)(Member* m, const char* p, int cb);

   struct Value
   {
      const char* data;
      int length;
      int format;

      Value(const char* p, int l, int f) { data = p; length = l; format = f; }
   };

   FieldBinder(const IObjectData::Field& src, size_t fieldIndex, int columnIndex);
   virtual ~FieldBinder() {}

   virtual bool Read(Member* m, const PGresult* res, int curRow) const = 0;
   
   //virtual const char* TypeHint() const = 0;
   virtual Value ParamValue(const Member& m) const = 0;
   virtual Oid Type() const = 0;

   static FieldBinder* Create(const IObjectData::Field& src, size_t fieldIndex, int columnIndex);
   static FieldBinder* CreateOrderBinder(int columnIndex, int* rowCount);

   size_t fieldIndex;
   MemberFormat format;
   int columnIndex;

   mutable ConvertValue convertor;
};

typedef std::vector<FieldBinder*> BinderList;
class ReadBinder
{
public:
   virtual ~ReadBinder() { Close(); }

   void Close();

   bool Prepare(std::string* fields, const ISessionObject& src);

   bool ReadTo(Object* o, const PGresult* res, int curRow) const;

   const MemberFormat* Type(const wchar_t* name) const;
   const Member* Value(const wchar_t* name, const PGresult* res, int curRow) const;

   size_t Count() const { return fields.size(); }

protected:
   BinderList fields;
   mutable Member cache;
   mutable CString strValue;
};

class ParamsBinder
{
public:
   ParamsBinder();
   ~ParamsBinder();

   //
   // "f1","f2" ...  $1::bigint,$2::bigint
   //
   bool Prepare(std::string* fields, std::string* params, const ISessionObject& src);
   //
   // "f1","f2" ... "f1"=$1::bigint and "f2"=$2::bigint ... $1::bigint,$2::bigint
   //
   bool PrepareFK(std::string* fields, std::string* andParams, std::string* params, const ISessionObject& src);
   
   const BinderList& Fields() const { return fields; }

   const char* const* Values(const Object* src, const Object* parent) const;
   const int* Lengths() const { return lengths; }
   const int* Formats() const { return formats; }
   const Oid* Types() const;

   size_t Count() const { return fields.size() + parentFields.size(); }

protected:
   mutable int rowCount;

   BinderList fields;
   BinderList parentFields;
   
   mutable const char** values;
   mutable int* lengths;
   mutable int* formats;
   mutable Oid* types;

   void AllocValues() const;
};

class SQTable : public IDataSource::ICreator
{
public:
   SQTable() {}
   ~SQTable() {}

   virtual const wchar_t* Name() const { return L"SQTable"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class QuerySourceCreator : public IDataSource::ICreator
{
public:
   QuerySourceCreator() {}
   ~QuerySourceCreator() {}

   virtual const wchar_t* Name() const { return L"SQLQuery"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

class InternalSource : public IInternalDataSource
{
public:
   InternalSource(SQTable* src);

   virtual ~InternalSource() {}

   virtual const wchar_t* Name() const { return L"PGSourceInternal"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return src->CreateReader(parameters, object); }
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return src->CreateWriter(parent, parameters, object); }
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return src->CreateRemover(parent, parameters, object); }

   virtual IBinary* GetServerData(int id);
   virtual bool    PutServerData(int id, const Binary& b);

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config);
   virtual void    Close();

   virtual bool    Execute(const wchar_t* stmt, ISession* session);
   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

private:
   SQTable* src;
   PGConnection connection;
};

class QueryBinder : public ReadBinder
{
public:
   QueryBinder();
   ~QueryBinder() { Close(); }

   bool Prepare(PGresult* res, const std::vector<const ISessionObject*>& objects);

   bool Read(Object* o, const PGresult* res, int curRow) const;

   FieldBinder* GetBinder(const MemberFormat* format) const;

protected:
   typedef std::map<GRServer::Format*, std::vector<FileField*>> ObjFiles;
   ObjFiles objFiles;
   std::vector<const MemberFormat*> formats;
   //std::vector<MemberFormat> customFormats;
};

class QueryReader : public IDataSource::IReader
{
public:
   QueryReader(PGconn* conn, const CString& stmt, const ISessionObject& object, bool debug, int rowCount, ParamHelper* defaults);
   ~QueryReader() { Close(); }

   virtual bool MoveNext(Object* parentObject);

   // подставл€ем только объекты созданные методом Create
   // иначе будут проблемы при удалении объекта созданного в DLL
   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return binder.Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return binder.Value(name, result, curRow); }

   virtual void AddChild(const std::wstring& childName, IReader* reader) {}

   virtual void AddChildObject(const ISessionObject* object) { childs.push_back(object); }

   virtual Object* GetNext();

   virtual FieldBinder* GetBinder(const MemberFormat* format) const { return binder.GetBinder(format); }

   virtual const ParamHelper* GetParamHelper() const { return NULL; }

protected:
   QueryReader(const ISessionObject& object);

   bool Open();

protected:
   PGconn* connection;
   PGresult* result;

   bool debug;
   int rowCount, curRow;

   const ISessionObject& object;
   std::vector<const ISessionObject*> childs;
   CString stmt;

   ParamHelper params;
   QueryBinder binder;
   mutable Object* nextObject;
};

class KeyMember
{
public:
   static KeyMember* Create(const std::wstring& name, Format* format);

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

class QueryChildReader : public QueryReader
{
public:
   QueryChildReader(const CString& keyFields, const ISessionObject& object, const ISessionObject& _parent);
   ~QueryChildReader();

   virtual bool MoveNext(Object* parentObject);
   virtual bool Get(Object* o) const;

   virtual void AddChildObject(const ISessionObject* object) {
      if (parent != NULL)
         parent->AddChildObject(object);
   }

   virtual FieldBinder* GetBinder(const MemberFormat* format) const { return parent ? parent->GetBinder(format) : NULL; }

   virtual Object* GetNext();
protected:
   QueryReader* parent;
   KeyHolder keyHolder;
   bool keyLoaded, parentHaveNextObject;
};


//class SQLCostSource : public IDataSource::ICreator
//{
//public:
//   SQLCostSource() {}
//   ~SQLCostSource() {}
//
//   virtual const wchar_t* Name() const { return L"SQLCostReader"; }
//   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
//   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
//};

bool Execute(PGconn* conn, const char* stmt);

PGconn* GetConnection(const ISessionObject& object, PGConnection** connection = NULL);
void AddErrorToLog(const std::string& msg, PGresult* res);
void PKToList(std::vector<std::string>* fields, const std::wstring& _str, bool quoting = true, wchar_t divSymbol = L',');
const char* QuoteString(std::string* dest, const std::string& src);
bool AddOrderedField(IObjectData::Fields& fields, const IObjectData& objDef);

extern const wchar_t* SENDED_FIELDS;
extern const wchar_t* ORDERED_FIELD;

} // namespace GRServer

#if __BIG_ENDIAN__
# define htonll(x) (x)
# define ntohll(x) (x)
#else
# define htonll(x) (((__int64)htonl((x) & 0xFFFFFFFF) << 32) | htonl((int32_t)((__int64)(x) >> 32)))
# define ntohll(x) (((__int64)ntohl((x) & 0xFFFFFFFF) << 32) | ntohl((int32_t)((__int64)(x) >> 32)))
#endif


extern GRServer::IServer* gServer;

#include "stdafx.h"
#include "postgre.h"

#include <ServerDefs.h>

using namespace GRServer;
using namespace std;

const char NO_PARSE_MARK = '\x1';
const int MAX_DO_COUNT = 1000;

class TableReader : public IDataSource::IReader
{
public:
   TableReader(const ISessionObject& object, PGconn *connection, ParamHelper* defaults, const std::vector<std::wstring>& filters, bool debug);

   virtual ~TableReader() { Close(); }
   virtual bool MoveNext(Object* parentObject);

   virtual bool Get(Object* o) const { return binder.ReadTo(o, result, curRow); }

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return binder.Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return binder.Value(name, result, curRow); }

   virtual const ParamHelper* GetParamHelper() const { return &params; }

protected:
   virtual bool Prepare();
   bool Probe(const std::string& stmt);

protected:
   ParamHelper params;
   std::vector<std::string> filters;

   const ISessionObject& obj;

   ReadBinder binder;

   PGconn* connection;
   PGresult* result;
   int curRow;
   bool debug, prepared;
};

class ChildReader : public TableReader
{
public:
   ChildReader(const ISessionObject& _object, PGconn* conn);
   ~ChildReader();
   
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }
   virtual bool MoveNext(Object* parentObject);

protected:
   virtual bool Prepare();

private:
   ParamsBinder params;
   std::string stmtName;
};

class TableWriter : public IDataSource::IWriter
{
public:
   TableWriter(PGconn* conn, PGConnection* connection);
   ~TableWriter() { Close(); }

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID* rid);
   virtual void Close();

   bool WriteInt(const Object* o, const Object* parent);

protected:
   PGconn* connection;
   PGConnection* pgc;

   bool failed;
   std::string stmtName;

   ParamsBinder params;
   int writeCount;
};

class ChildWriter : public TableWriter
{
public:
   ChildWriter(const ISessionObject& _object, PGconn* conn);

   virtual bool Prepare(const ISessionObject& object) { return true; }
   virtual bool Write(const Object& o, RowID* rid);
   virtual void Close();

private:
   std::string rmvName;

   ParamsBinder rmvParams;
   int childIndex;
};

class Remover : public IDataSource::IRemover
{
public:
   Remover(const ISessionObject& object, PGconn* conn);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() {}

protected:
   const ISessionObject& object;
   std::string stmt;
   PGconn* connection;
};

Remover::Remover(const ISessionObject& _object, PGconn* conn) :
   object(_object)
   ,connection(conn)
{
   USES_CONVERSION;

   stmt = "DELETE FROM \"";
   stmt += W2A_CP(_object.GetObjectDef()->tableName.c_str(), CP_UTF8);
   stmt += "\"";
}

bool Remover::Remove(const wchar_t* filter)
{
   std::string tstr(stmt);

   if (filter != NULL && *filter != L'\0')
   {
      USES_CONVERSION;

      CString parsedFilter, src(filter);
      object.PrepareFilterStr(&parsedFilter, src);

      tstr += " WHERE "; tstr.append(W2A_CP(parsedFilter.c_str(), CP_UTF8));
   }

   return ::Execute(connection, tstr.c_str());
}

ChildWriter::ChildWriter(const ISessionObject& _object, PGconn* conn) :
   TableWriter(conn, NULL)
   ,childIndex(-1)
{
   USES_CONVERSION;

   std::string fieldStr, paramStr, fkString;

   params.Prepare(&fieldStr, &paramStr, _object);
   params.PrepareFK(&fieldStr, NULL, &paramStr, _object);

   rmvParams.PrepareFK(NULL, &fkString, NULL, _object);

   std::string tableName(W2A_CP(_object.GetObjectDef()->tableName.c_str(), CP_UTF8));

   std::string sql;
   sql.append("INSERT INTO \"").append(tableName).append("\" (")
      .append(fieldStr).append(") VALUES (").append(paramStr).append(1, ')');

   const wstring& oname = _object.Self()->Name();
   size_t off = oname.find_last_of(L'$');

   childIndex = _object.Parent()->Self()->format->FindMember(oname.substr(off + 1).c_str());

   stmtName = tableName + "Insert";

   PGresult* res = PQprepare(connection, stmtName.c_str(), sql.c_str(), (int)params.Count(), params.Types());
   PQclear(res);

   std::string rmvSql;
   rmvSql.append("DELETE FROM \"").append(tableName).append("\" WHERE ").append(fkString);

   rmvName = tableName + "Remove";
   res = PQprepare(connection, rmvName.c_str(), rmvSql.c_str(), (int)rmvParams.Count(), rmvParams.Types());
   PQclear(res);
}

void ChildWriter::Close()
{ 
   string stmt("DEALLOCATE \"");
   if(!rmvName.empty())
      ::Execute(connection, (stmt + rmvName + "\"").c_str());
   if(!stmtName.empty())
      ::Execute(connection, (stmt + stmtName + "\"").c_str());
   
   stmtName.clear();
   rmvName.clear();
}

bool ChildWriter::Write(const Object& o, RowID* rid)
{
   bool bres = true;
   const char* const* values = rmvParams.Values(NULL, &o);
   const int* lengths = rmvParams.Lengths();
   const int* formats = rmvParams.Formats();

   PGresult* result = PQexecPrepared(connection, rmvName.c_str(), (int)rmvParams.Count(), values, lengths, formats, 1);
   bres = (PQresultStatus(result) == PGRES_COMMAND_OK);
   PQclear(result);

   const Member& m = o.at(childIndex);
   if (m.object != NULL)
   {
      ServObject::const_iterator i = m.object->begin();
      for (; bres && i != m.object->end(); i++)
      {
         bres = WriteInt(*i, &o);
      }
   }

   return bres;
}

TableWriter::TableWriter(PGconn* conn, PGConnection* _pgc) :
   connection(conn)
   ,pgc(_pgc)
   ,writeCount(0)
   ,failed(false)
{
}

bool TableWriter::Prepare(const ISessionObject& object)
{
   std::vector<std::string> keyFields;

   const IObjectData* od = object.GetObjectDef();
   Format* format = object.Self()->format;
   CVector<IObjectData::Field>* sfkFields = NULL;

   IObjectData::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
   std::string fields, paramStr, kfString;
   if (keyI != od->members.end())
   {
      PKToList(&keyFields, keyI->second, true);
      std::vector<std::string>::const_iterator fi = keyFields.begin();
      for( ; fi != keyFields.end(); fi++)
      {
         if (fi != keyFields.begin())
            kfString += ",";
         kfString += *fi;
      }
   }

   params.Prepare(&fields, &paramStr, object);

   USES_CONVERSION;
   std::string tableName(W2A_CP(od->tableName.c_str(), CP_UTF8));

   std::string stmt("INSERT INTO \"");
   stmt.append(tableName).append("\" (").append(fields).append(") VALUES ( ").append(paramStr)
      .append(") ON CONFLICT (").append(kfString).append(") ");

   if (keyFields.size() == params.Count())
   {
      stmt.append("DO NOTHING");
   }
   else
   {
      stmt.append(" DO UPDATE SET ");

      bool started = true;
      for (BinderList::const_iterator fi = params.Fields().begin(); fi != params.Fields().end(); fi++)
      {
         std::string name;
         name.append(1,'"').append(W2A_CP((*fi)->format.name.c_str(), CP_UTF8)).append(1,'"');

         if (std::find(keyFields.begin(), keyFields.end(), name) != keyFields.end())
            continue;

         if (!started)
            stmt.append(",");

         stmt.append(name).append("=EXCLUDED.").append(name);
         started = false;
      }
   }

   stmtName = tableName + "Insert";

   if (pgc != NULL)
      pgc->StartTransaction();

   PGresult* res = PQprepare(connection, stmtName.c_str(), stmt.c_str(), (int)params.Count(), params.Types());
   bool bres = PQresultStatus(res) == PGRES_COMMAND_OK;
   if (!bres)
   {
      failed = true;
      AddErrorToLog("Fail to prepare insert", res);
   }

   PQclear(res);

   return bres;
}

bool TableWriter::WriteInt(const Object* o, const Object* parent)
{
   const char* const* values = params.Values(o, parent);
   const int* lengths = params.Lengths();
   const int* formats = params.Formats();

   PGresult* result = PQexecPrepared(connection, stmtName.c_str(), (int)params.Count(), values, lengths, formats, 1);
   bool bres = (PQresultStatus(result) == PGRES_COMMAND_OK);

   if (bres)
   {
      WriterList::iterator ci = childs.begin();
      for (; bres && ci != childs.end(); ci++)
         bres = (*ci)->Write(*o, NULL);
   }
   if(!bres)
   {
      AddErrorToLog("Fail exec insert", result);
      failed = true;
   }

   PQclear(result);

   if (bres && pgc != NULL)
   {
      if (writeCount++ > MAX_DO_COUNT)
      {
         writeCount = 0;
         pgc->FinishTransaction(true);
      }
   }
   return bres;
}

bool TableWriter::Write(const Object& o, RowID* rid)
{
   return WriteInt(&o, NULL);
}

void TableWriter::Close()
{
   string stmt("DEALLOCATE \"");
   if (!stmtName.empty())
   {
      ::Execute(connection, (stmt + stmtName + "\"").c_str());
      stmtName.clear();
   }

   if (pgc != NULL && !failed)
      pgc->FinishTransaction(!failed);
}

ChildReader::ChildReader(const ISessionObject& _object, PGconn* conn) :
   TableReader(_object, conn, NULL, vector<wstring>(), false)
{
}

ChildReader::~ChildReader()
{
   string stmt("DEALLOCATE \"");
   if (!stmtName.empty())
      ::Execute(connection, (stmt + stmtName + "\"").c_str());
}

bool ChildReader::Prepare()
{
   string fields, paramStmt;
   prepared = true;

   if (!binder.Prepare(&fields, obj) || binder.Count() == 0)
      return false;

   params.PrepareFK(NULL, &paramStmt, NULL, obj);

   USES_CONVERSION;
   const IObjectData* od = obj.GetObjectDef();
   stmtName = W2A_CP(od->tableName.c_str(), CP_UTF8);
   
   std::string stmt;

   stmt.append("SELECT ").append(fields).append(" FROM \"").append(stmtName).append("\" WHERE ").append(paramStmt);
   if (od->IsOrderedSource())
   {
      USES_CONVERSION;
      stmt.append(" ORDER BY \"").append(W2A_CP(ORDERED_FIELD, CP_UTF8)).append(1,'"');
   }

   PGresult* res = PQprepare(connection, stmtName.c_str(), stmt.c_str(), (int)params.Count(), params.Types());
   bool bres = PQresultStatus(res) == PGRES_COMMAND_OK;
   if (!bres)
   {
      AddErrorToLog("Error preparing", res);
   }
   PQclear(res);

   return bres;
}

bool ChildReader::MoveNext(Object* parentObject)
{
   if (parentObject == NULL) return false;
   if (!prepared && !Prepare())
      return false;

   if (curRow == -1)
   {
      const char* const* values = params.Values(NULL, parentObject);
      const int* lengths = params.Lengths();
      const int* formats = params.Formats();

      result = PQexecPrepared(connection, stmtName.c_str(), (int)params.Count(), values, lengths, formats, 1);
      if (PQresultStatus(result) != PGRES_TUPLES_OK)
         return false;
   }
   
   if (++curRow < PQntuples(result))
      return true;

   curRow = -1;
   return false;
}

TableReader::TableReader(const ISessionObject& _object, PGconn* _connection, ParamHelper* defaults, const std::vector<std::wstring>& filters, bool _debug) :
   curRow(-1)
   ,result(NULL)
   ,params(defaults)
   ,obj(_object)
   ,connection(_connection)
   ,debug(_debug)
   ,prepared(false)
{
   USES_CONVERSION;

   vector<wstring>::const_iterator i = filters.begin();
   for (; i != filters.end(); i++)
   {
      this->filters.push_back(W2A_CP(i->c_str(), CP_UTF8));
   }
}

void TableReader::Close()
{
   if (result != NULL)
   {
      PQclear(result);
      result = NULL;
   }
}

bool TableReader::Prepare()
{
   string fields;
   prepared = true;

   if (!binder.Prepare(&fields, obj) || binder.Count() == 0)
      return false;

   const IObjectData* od = obj.GetObjectDef();

   USES_CONVERSION;

   string stmt("SELECT ");
   stmt.append(fields).append(" FROM \"").append(W2A_CP(od->tableName.c_str(), CP_UTF8)).append(1, '"');

   if (filters.size() == 0)
      return Probe(stmt);

   stmt += " WHERE ";

   const ISession& session = obj.GetSession();
   vector<string>::const_iterator fi = filters.begin();
   for (; fi != filters.end(); fi++)
   {
      const char* sp = fi->c_str();
      bool filterMark = *fi->begin() == NO_PARSE_MARK;
      if (filterMark)
      {
         sp++;
      }
      const wchar_t* flt = A2W_CP(sp, CP_UTF8), *fcp = NULL;
      CString dest;
      Token tres;

      if (filterMark) 
         fcp = flt;
      else if (session.Parse(&tres, flt, &obj) && tres.type == Token::Type::ttString)
         fcp = tres.value.str->c_str();

      if (fcp != NULL)
      {
         obj.PrepareFilterStr(&dest, fcp);
         if (Probe(stmt + W2A_CP(dest.c_str(), CP_UTF8)))
            break;
      }
   }

   return fi != filters.end();
}

bool TableReader::Probe(const std::string& stmt)
{
   result = PQexecParams(connection, stmt.c_str(), 0, NULL, NULL, NULL, NULL, 1);
   ExecStatusType st = PQresultStatus(result);
   if (st != PGRES_TUPLES_OK || PQntuples(result) == 0)
   {
      if (st != PGRES_TUPLES_OK)
      {
         std::string msg("Error exec query ");
         msg += stmt;
         AddErrorToLog(msg, result);
      }
      PQclear(result);
      result = NULL;
      return false;
   }

   return true;
}

bool TableReader::MoveNext(Object* parentObject)
{
   if (!prepared && !Prepare())
      return false;

   return ++curRow < PQntuples(result);
}

bool TableReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   DWORD cbParams = sizeof(L"PARAMS:") - sizeof(wchar_t);
   if (memcmp(filter, L"PARAMS:", cbParams) == 0)
   {
      params.Read((const wchar_t*)(filter + cbParams / sizeof(wchar_t)), &object.GetSession(), &object, (IErrorLogger*)gServer);
   }
   else
   {
      USES_CONVERSION;

      string dest;
      // mark filter no parse
      this->filters.clear();
      dest.append(1, NO_PARSE_MARK).append(W2A_CP(filter, CP_UTF8));
      this->filters.push_back(dest);
   }
   return true;
}

IDataSource::IReader* SQTable::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   PGconn* conn = GetConnection(object);
   if (conn == NULL)
      return NULL;

   if (object.Parent() != NULL)
      return new ChildReader(object, conn);

   std::vector<wstring> filters;
   parameters.Load(&filters, L"readFilter", object);
   const Parameter* debug = parameters.Find(L"debug", -1);

   ParamHelper* defaults = new ParamHelper(NULL);
   defaults->Read(parameters, &object.GetSession(), &object, gServer);

   return new TableReader(object, conn, defaults, filters, (debug != NULL));
}

IDataSource::IWriter* SQTable::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   PGConnection* pgc;
   PGconn* conn = GetConnection(object, &pgc);
   if (conn == NULL)
      return NULL;

   if (parent == NULL)
      return new TableWriter(conn, pgc);

   return new ChildWriter(object, conn);
}

IDataSource::IRemover* SQTable::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   PGconn* conn = GetConnection(object);
   if (conn == NULL)
      return NULL;

   return new Remover(object, conn);
}
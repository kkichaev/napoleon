/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Работа с SQLite на WinCE
 *
 *  ert   12/06/2009   creating
 */ 
#include "stdafx.h"
#include "SQLTable.h"
#include "sqlitedll.h"
#include <StdFuncs.h>
#include <set>

using namespace std;

sqlite3* db = NULL;
int SQLTable::pulsCount = 0;
int SQLTable::doCount = 0;

BEGIN_TYPE_REFLECTION(RowID)
   REGISTER_INT64_MEMBER(RowID, rowid)
END_TYPE_REFLECTION(RowID)

//
//------------------------------------- IBinder ----------------------------------------
//
struct RIDParam : public SQLTable::IBinder
{
   RIDParam(ROWID *rid, int index) : IBinder(index) { this->rid = rid; }

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      sql_bind_int64(stmt, index, *rid);
   }

   ROWID *rid;
};

struct TextParam : public SQLTable::IBinder
{
   TextParam(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;

      const wchar_t *value = *(const wchar_t**)member.GetValue(*data);
      sql_bind_text16(stmt, index, value, -1, SQLITE_STATIC);
   }

   const MemberType& member;
};

struct DateTimeParam : public SQLTable::IBinder
{
   DateTimeParam(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;

      FILETIME *value = (FILETIME*)member.GetValue(*data);
      sqlite3_int64 val = value->dwLowDateTime | (((sqlite3_int64)value->dwHighDateTime) << 32);
      sql_bind_int64(stmt, index, val);
   }

   const MemberType& member;
};

struct Int64Param : public SQLTable::IBinder
{
   Int64Param(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;
      sql_bind_int64(stmt, index, *(__int64*)member.GetValue(*data));
   }

   const MemberType& member;
};

template <class INT_TYPE> struct IntParam : public SQLTable::IBinder
{
   IntParam(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;

      INT_TYPE *value = (INT_TYPE*)member.GetValue(*data);
      sql_bind_int(stmt, index, *value);
   }

   const MemberType& member;
};

template <class NUM_TYPE> struct NumParam : public SQLTable::IBinder
{
   NumParam(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;

      NUM_TYPE *value = (NUM_TYPE*)member.GetValue(*data);
      sql_bind_double(stmt, index, *value);
   }

   const MemberType& member;
};

struct BinaryParam : public SQLTable::IBinder
{
   BinaryParam(const MemberType &mt, int index) : member(mt), IBinder(index) {}

   virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt)
   {
      if( data == NULL ) return;

      StreamWriter sw;
      member.Serialize(&sw, *data);

      int count = sw.Size();
      BYTE *bytes = (BYTE*)malloc(count);
      sw.ToBytes(bytes);
      sql_bind_blob(stmt, index, bytes, count, FreeBytes);
   }

   static void FreeBytes(void *value)
   {
      free(value);
   }

   const MemberType& member;
};
//
//------------------------------------- OBinder ----------------------------------------
//
struct RIDResult : public SQLTable::OBinder
{
   RIDResult(ROWID *rid, int index) : OBinder(index) { this->rid = rid; }

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      *rid = sql_column_int64(stmt, index);
   }

   ROWID *rid;
};

struct TextResult : public SQLTable::OBinder
{
   TextResult(const MemberType &mt, StringHolder &_holder, int index) : member(mt), OBinder(index), holder(_holder) {}

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      const wchar_t *value = (const wchar_t*)sql_column_text16(stmt, index);
      value = (value != NULL) ? holder.Add(value) : L"";
      member.SetValue(data, &value);
   }

   const MemberType& member;
   StringHolder &holder;
};

struct DateTimeResult : public SQLTable::OBinder
{
   DateTimeResult(const MemberType &mt, int index) : member(mt), OBinder(index) {}

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      FILETIME ft;
      sqlite_int64 value = sql_column_int64(stmt, index);
      ft.dwLowDateTime = (DWORD)(value & 0xFFFFFFFF);
      ft.dwHighDateTime = (DWORD)(value >> 32);

      member.SetValue(data, &ft);
   }

   const MemberType& member;
};

struct Int64Result : public SQLTable::OBinder
{
   Int64Result(const MemberType &mt, int index) : member(mt), OBinder(index) {}

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      sqlite_int64 value = sql_column_int64(stmt, index);
      member.SetValue(data, &value);
   }

   const MemberType& member;
};

template <class INT_TYPE> struct IntResult : public SQLTable::OBinder
{
   IntResult(const MemberType &mt, int index) : member(mt), OBinder(index) {}

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      INT_TYPE value = (INT_TYPE)sql_column_int(stmt, index);
      member.SetValue(data, &value);
   }

   const MemberType& member;
};

template <class NUM_TYPE> struct NumResult : public SQLTable::OBinder
{
   NumResult(const MemberType &mt, int index) : member(mt), OBinder(index) {}

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      NUM_TYPE value = (NUM_TYPE)sql_column_double(stmt, index);
      member.SetValue(data, &value);
   }

   const MemberType& member;
};

struct BinaryResult : public SQLTable::OBinder
{
   BinaryResult(const MemberType &mt, int index) : member(mt), OBinder(index), value(NULL) {}
   ~BinaryResult() { free(value); }

   virtual void Write(IReflectableData* data, sqlite3_stmt *stmt)
   {
      free(value);
      value = NULL;

      DWORD size = sql_column_bytes(stmt, index);
      if( size > 0 )
      {
         value = malloc(size);
         memcpy(value, sql_column_blob(stmt, index), size);
         StreamReader sr((const BYTE*)value, size);
         member.Deserialize(data, sr);
      }
   }

   const MemberType& member;

protected:
   void *value;
};
//
//------------------------------------- ParamBinder ----------------------------------------
//
template<class Vec> void Clear(Vec *vec)
{
   Vec::iterator i = vec->begin();
   while( i != vec->end() )
   {
      delete (*i);
      i++;
   }

   vec->clear();
}

SQLTable::IBinder* SQLTable::ParamBinder::CreateBinder(const MemberType &mt, int pos)
{
   IBinder *ibinder = NULL;

   switch( mt.type )
   {
   case MemberType::String:
      ibinder = new TextParam(mt, pos);
      break;

   case MemberType::DateTime:
      ibinder = new DateTimeParam(mt, pos);
      break;

   case MemberType::Short:
      ibinder = new IntParam<short>(mt, pos);
      break;

   case MemberType::UShort:
      ibinder = new IntParam<unsigned short>(mt, pos);
      break;

   case MemberType::Integer:
      ibinder = new IntParam<int>(mt, pos);
      break;

   case MemberType::Unsigned:
      ibinder = new IntParam<unsigned>(mt, pos);
      break;

   case MemberType::Long:
      ibinder = new IntParam<long>(mt, pos);
      break;

   case MemberType::Int64:
      ibinder = new Int64Param(mt, pos);
      break;

   case MemberType::ULong:
      ibinder = new IntParam<unsigned long>(mt, pos);
      break;

   case MemberType::Double:
      ibinder = new NumParam<double>(mt, pos);
      break;

   case MemberType::Float:
      ibinder = new NumParam<float>(mt, pos);
      break;

   case MemberType::Collection:
   case MemberType::UserType:
      ibinder = new BinaryParam(mt, pos);
      break;
   }

   return ibinder;
}

void SQLTable::ParamBinder::Prepare(const std::vector<MemberType*>& members)
{
   Clear(this);

   int pos = 1;
   std::vector<MemberType*>::const_iterator i = members.begin();
   for( ; i != members.end(); i++ )
   {
      IBinder *ibinder = CreateBinder(*(*i), pos);
      if( ibinder != NULL )
      {
         pos++;
         push_back(ibinder);
      }
   }
}

void SQLTable::ParamBinder::Prepare(const IReflectableData* data, std::vector<std::wstring> *tableFields, bool addRID)
{
   Clear(this);

   if( data != NULL )
   {
      const DataReflector &r = data->GetType();
      vector<wstring>::iterator i = tableFields->begin();
      int pos = 1;
      while( i != tableFields->end() )
      {
         int fld;
         const std::wstring& field = (*i);
         if( field.at(0) == L'\"' ) fld = r.Find(field.substr(1, field.size()-2).c_str());
         else fld = r.Find((*i).c_str());

         if(fld < 0)
         {
            i = tableFields->erase(i);
            continue;
         }
         const MemberType &mt = r.Type(fld);
         IBinder *ibinder = CreateBinder(mt, pos);
         if( ibinder == NULL )
         {
            i = tableFields->erase(i);
            continue;
         }

         push_back(ibinder);
         i++;
         pos++;
      }
   } else
      tableFields->clear();
   
   if( addRID )
   {
      tableFields->push_back(L"ROWID");
      push_back(new RIDParam(&rid, tableFields->size()));
   }
}

void SQLTable::ParamBinder::Read(const IReflectableData* data, sqlite3_stmt *stmt)
{
   iterator i = begin();
   while( i != end() )
   {
      (*i)->Read(data, stmt);
      i++;
   }
}
//
//------------------------------------- ResultBinder ----------------------------------------
//
SQLTable::OBinder* SQLTable::ResultBinder::CreateBinder(const MemberType &mt, int pos)
{
   OBinder *obinder = NULL;

   switch( mt.type )
   {
   case MemberType::String:
      obinder = new TextResult(mt, holder, pos);
      break;

   case MemberType::DateTime:
      obinder = new DateTimeResult(mt, pos);
      break;

   case MemberType::Short:
      obinder = new IntResult<short>(mt, pos);
      break;

   case MemberType::UShort:
      obinder = new IntResult<unsigned short>(mt, pos);
      break;

   case MemberType::Integer:
      obinder = new IntResult<int>(mt, pos);
      break;

   case MemberType::Unsigned:
      obinder = new IntResult<unsigned>(mt, pos);
      break;

   case MemberType::Long:
      obinder = new IntResult<long>(mt, pos);
      break;

   case MemberType::ULong:
      obinder = new IntResult<unsigned long>(mt, pos);
      break;

   case MemberType::Int64:
      obinder = new Int64Result(mt, pos);
      break;

   case MemberType::Double:
      obinder = new NumResult<double>(mt, pos);
      break;

   case MemberType::Float:
      obinder = new NumResult<float>(mt, pos);
      break;

   case MemberType::Collection:
   case MemberType::UserType:
      obinder = new BinaryResult(mt, pos);
      break;
   }
   return obinder;
}

void SQLTable::ResultBinder::Prepare(const IReflectableData* data, std::vector<std::wstring> *tableFields, bool addRID)
{
   Clear(this);

   if( data != NULL )
   {
      const DataReflector &r = data->GetType();
      vector<wstring>::iterator i = tableFields->begin();
      int pos = 0;
      while( i != tableFields->end() )
      {
         int fld;
         const std::wstring& field = (*i);
         if( field.at(0) == L'\"' ) fld = r.Find(field.substr(1, field.size()-2).c_str());
         else fld = r.Find((*i).c_str());

         if(fld < 0)
         {
            i = tableFields->erase(i);
            continue;
         }
         const MemberType &mt = r.Type(fld);
         OBinder *obinder = CreateBinder(mt, pos);
         if( obinder == NULL )
         {
            i = tableFields->erase(i);
            continue;
         }

         push_back(obinder);
         i++;
         pos++;
      }
   } else
      tableFields->clear();
   
   if( addRID )
   {
      tableFields->push_back(L"ROWID");
      push_back(new RIDResult(&rid, tableFields->size()-1));
   }
}

ROWID SQLTable::ResultBinder::Write(IReflectableData* data, sqlite3_stmt *stmt)
{
   holder.Clear();

   iterator i = begin();
   while( i != end() )
   {
      (*i)->Write(data, stmt);
      i++;
   }

   return rid;
}

int RussCollate(void *NotUsed, int nKey1, const void *pKey1, int nKey2, const void *pKey2)
{
   return wcsncmp((const wchar_t*)pKey1, (const wchar_t*)pKey2, (nKey1<nKey2) ? nKey1 : nKey2);
}

int RussNoCaseCollate(void *NotUsed, int nKey1, const void *pKey1, int nKey2, const void *pKey2)
{
   return _wcsnicmp((const wchar_t*)pKey1, (const wchar_t*)pKey2, (nKey1<nKey2) ? nKey1 : nKey2);
}

void AssignValue(sqlite3_context* context, const MemberType& mt, const IReflectableData& data)
{
   void *value = mt.GetValue(data);
   switch( mt.type )
   {
   case MemberType::String:
      sql_result_text16(context, *(wchar_t**)value, -1, SQLITE_STATIC);
      break;
   case MemberType::Short:
      sql_result_int(context, *(short*)value);
      break;
   case MemberType::UShort:
      sql_result_int(context, *(unsigned short*)value);
      break;
   case MemberType::Integer:
   case MemberType::Unsigned:
   case MemberType::Long:
   case MemberType::ULong:
      sql_result_int(context, *(long*)value);
      break;
   case MemberType::Double:
      sql_result_double(context, *(double*)value);
      break;
   case MemberType::Float:
      sql_result_double(context, *(float*)value);
      break;
   case MemberType::Int64:
      sql_result_int64(context, *(__int64*)value);
      break;
   case MemberType::DateTime:
   {
      sqlite3_int64 val = ((FILETIME*)value)->dwLowDateTime | (((sqlite3_int64)((FILETIME*)value)->dwHighDateTime) << 32);
      sql_result_int64(context, val);
      break;
   }
   default:
      sql_result_null(context);
      break;
   }
}

/*
   PriceImpl p;
   Test data;
   SQLTable t(p.Name());
   t.Select(L"SELECT 'test' FROM price WHERE id = '001154' and collectionValue(cost, 'CostItem', 0, 'cost') = 16400", &data);

*/
void GetCollectionValue(sqlite3_context* context, int argc, sqlite3_value **argv)
{
   if( argc != 4 )
      sql_result_error16(context, L"args mismatch", -1);
   else
   {
      const BYTE* pBytes = (const BYTE*)sql_value_blob(argv[0]);
      int n = sql_value_bytes(argv[0]);

      const wchar_t* type = (const wchar_t*)sql_value_text16(argv[1]);
      int row = sql_value_int(argv[2]);
      const wchar_t* member = (const wchar_t*)sql_value_text16(argv[3]);

      bool done = false;
      const DataReflector* tr = FindTypeReflector(type);
      if( tr != NULL )
      {
         int index = tr->Find(member);
         if( index >= 0 )
         {
            const MemberType& mt = tr->Type(index);
            
            StreamReader sr(pBytes, n);
            short count;
            sr.Read(&count, sizeof(count));

            if( row >= 0 && row < count )
            {
               IReflectableData *data = tr->Create();

               do
               {
                  if( !tr->Deserialize(data, sr) )
                     break;
                  row--;
               } while(row >= 0);

               if( row < 0 )
               {
                  AssignValue(context, mt, *data);
                  done = true;
               }

               delete data;
            }
         }
      }

      if( !done )
         sql_result_null(context);
   }
}

void InitDB(sqlite3 *db)
{
   sql_create_collation16(db, L"RUSS", SQLITE_UTF16_ALIGNED, NULL, RussCollate);
   sql_create_collation16(db, L"RUSS_NOCASE", SQLITE_UTF16_ALIGNED, NULL, RussNoCaseCollate);
   sql_create_function16(db, L"collectionValue", 4, SQLITE_ANY, NULL, GetCollectionValue, NULL, NULL);
}

// поле, тип, индекс, член типа
//collectionValue("qty", "Qty", 1, "qty")

//
//------------------------------------- SQLTable ----------------------------------------
//
bool SQLTable::OpenDB(const char *dbName)
{
   if( db != NULL )
      return true;

   if( sql_open(dbName, &db) != SQLITE_OK ) return false;

   InitDB(db);
   return true;
}

bool SQLTable::OpenDB(const wchar_t *dbName)
{
   if( db != NULL )
      return true;

   if( sql_open16(dbName, &db) != SQLITE_OK ) return false;

   InitDB(db);
   return true;
}

void SQLTable::CloseDB()
{
   if( db != NULL )
   {
      sql_close(db);
      db = NULL;
   }
}

bool SQLTable::Execute(const wchar_t *sql)
{
   if( db == NULL ) return false;

   int size = wcslen(sql) + 1;

   int mbSize = size * 2;
   char *mb = (char*)alloca(mbSize);
   mbSize = WideCharToMultiByte(CP_UTF8, 0, sql, size-1, mb, mbSize, NULL, NULL);
   mb[mbSize] = '\0';

   return Execute(mb);
}


bool SQLTable::Execute(const char *sql)
{
   char *errMsg = NULL;
   int res = sql_exec(db, sql, NULL, NULL, &errMsg);
   sql_free(errMsg);

   return (res == SQLITE_OK);
}

bool SQLTable::StartTransaction(int pc)
{
   if( db == NULL ) return false;

   pulsCount = pc;
   doCount = 0;

   return Execute("BEGIN;");
}

bool SQLTable::EndTransaction()
{
   if( db == NULL ) return false;

   pulsCount = 0;
   doCount = 0;

   return Execute("END;");
}

void SQLTable::DoCommand()
{
   if( pulsCount == 0 ) return;

   if( doCount++ >= pulsCount )
   {
      doCount = 0;
      Execute("END;");
      Execute("BEGIN;");
   }
}

bool SQLTable::DropTable(const wchar_t *tableName)
{
   std::wstring sql(L"DROP TABLE '");
   sql += tableName;
   sql +=L"'";

   return Execute(sql.c_str());
}

SQLTable::SQLTable(const std::wstring &tn) : lastOp(opNone), opStmt(NULL), dataTag(0), tableName(tn)
{
}

SQLTable::~SQLTable()
{
   Clear(&params);
   Clear(&result);

   if( opStmt != NULL )
   {
      sql_finalize(opStmt);
      opStmt = NULL;
   }
}

bool SQLTable::IsTableExist(const wchar_t *tableName)
{
   if( db == NULL ) return false;

   std::wstring str(L"SELECT name FROM SQLITE_MASTER WHERE type='table' AND name='");
   str.append(tableName);
   str.append(L"'");

   sqlite3_stmt *stmt = NULL;
   int rc = sql_prepare16_v2(db, str.c_str(), str.size() * sizeof(wchar_t), &stmt, NULL);
   if( rc == SQLITE_OK )
      rc = sql_step(stmt);
   sql_finalize(stmt);

   return (rc == SQLITE_ROW);
}

SQLTable::FieldType SQLTable::MemberTypeToFieldType(MemberType::DataTypes type)
{
   switch(type)
   {
   case MemberType::String:
      return ftText;

   case MemberType::Short:
   case MemberType::UShort:
   case MemberType::Integer:
   case MemberType::Unsigned:
   case MemberType::Long:
   case MemberType::ULong:
   case MemberType::DateTime:
      return ftInteger;

   case MemberType::Double:
   case MemberType::Float:
      return ftReal;

   case MemberType::Collection:
   case MemberType::UserType:
      return ftBlob;
   }
   return ftNull;
}

bool SQLTable::AddDBFields(const std::vector<FieldDef>& fields)
{
   if( db == NULL ) return false;

   std::vector<FieldDef>::const_iterator i = fields.begin();
   for(  ;i != fields.end(); i++ )
   {
      std::wstring text(L"ALTER TABLE '");
      text += tableName;
      text += L"' ADD COLUMN ";
      text += i->name;

      switch(i->type)
      {
      case ftText:
         text += L" TEXT ";
         break;
      case ftInteger:
         text += L" INTEGER ";
         break;
      case ftReal:
         text += L" REAL ";   
         break;
      case ftBlob:
         text += L" BLOB ";
         break;
      }

      sqlite3_stmt *stmt;
      int rc = sql_prepare16_v2(db,  text.c_str(), -1, &stmt, NULL);
      if( rc == SQLITE_OK )
         sql_step(stmt);
      sql_finalize(stmt);

      if( rc != SQLITE_OK ) break;
   }
   return (i == fields.end());
}

bool SQLTable::Create(const DataReflector& reflector, const wchar_t *keyFieldStr)
{
   if( db == NULL ) return false;

   vector<wstring> keyFields;
   StringToList(&keyFields, keyFieldStr);

   std::wstring text(L"CREATE TABLE '");
   text += tableName;
   text += L"' (";

   int i=0, count = reflector.Count();
   for( ; i<count; i++ )
   {
      const MemberType& member = reflector.Type(i);
      if( member.type == MemberType::Parent ) continue;

      if( i > 0 ) text += L",";
      text += L"'";
      text += member.name;
      text += L"'";

      switch(MemberTypeToFieldType((MemberType::DataTypes)member.type))
      {
      case ftText:
         text += L" TEXT ";
         break;
      case ftInteger:
         text += L" INTEGER ";
         break;
      case ftReal:
         text += L" REAL ";   
         break;
      case ftBlob:
         text += L" BLOB ";
         break;
      }
   }

   if( keyFields.size() > 0 )
   {
      text += L", CONSTRAINT pk_";
      text += tableName;
      text += L" PRIMARY KEY (";
      vector<wstring>::const_iterator ki = keyFields.begin();
      while( ki != keyFields.end() )
      {
         if( ki != keyFields.begin() )
            text += L",";
         text += L"'";
         text += (*ki);
         text += L"'";
         ki++;
      }
      text += L")";
   }
   text += L")";

   sqlite3_stmt *stmt;

   int rc = sql_prepare16_v2(db,  text.c_str(), -1, &stmt, NULL);
   if( rc == SQLITE_OK )
      rc = sql_step(stmt);
   sql_finalize(stmt);

   this->tableName = tableName;
   return (rc == SQLITE_DONE);
}

// CREATE INDEX indexName ON tableName (col1 [, col2])
bool SQLTable::CreateIndex(const wchar_t *indexFields)
{
   if( indexFields == NULL || *indexFields == L'\0' )
      return true;

   vector<wstring> fields;
   StringToList(&fields, indexFields);
   if( fields.size() == 0 )
      return false;

   wstring indexName(indexFields);
   wstring::iterator ii = indexName.begin();
   while( ii != indexName.end() )
   {
      if( *ii == L',' ) *ii = L'_';
      ii++;
   }

   wstring sql(L"CREATE INDEX ");
   sql += indexName; sql += L"_" ; sql += tableName; sql += L" ON '"; sql += tableName; sql += L"' (";

   vector<wstring>::const_iterator i = fields.begin();
   while( i != fields.end() )
   {
      if( i != fields.begin() )
         sql += L",";
      sql += (*i);

      i++;
   }
   sql += L")";

   return Execute(sql.c_str());
}

bool SQLTable::GetFields(std::vector<std::wstring> *fields)
{
   if( db == NULL ) return false;

   std::wstring str(L"PRAGMA TABLE_INFO('");
   str += tableName;
   str += L"')";

   sqlite3_stmt *stmt = NULL;
   int rc = sql_prepare16_v2(db, str.c_str(), str.size() * sizeof(wchar_t), &stmt, NULL);
   if( rc != SQLITE_OK ) return false;

   while( sql_step(stmt) == SQLITE_ROW )
   {
      const wchar_t *colName = (const wchar_t *)sql_column_text16(stmt, 1);
      std::wstring col(L"\"");
      col += colName;
      col += L"\"";
      fields->push_back(col);
   }
   sql_finalize(stmt);
   return true;
}

// SELECT f1[, fi] FROM tableName where (param1 = ?1 [AND parami = ?i])
void SQLTable::MakeSelectQuery(std::wstring *sql, const std::vector<std::wstring>& fields, const std::vector<std::wstring>& params)
{
   ATLASSERT(fields.size() > 0 && params.size() > 0);

   sql->assign(L"SELECT ");
   vector<wstring>::const_iterator i = fields.begin();
   while( i != fields.end() )
   {
      if( i != fields.begin() ) sql->append(L",");
      std::wstring cname(*i);
      if( *cname.begin() == L'"' )
      {
         *cname.begin() = L'[';
         *cname.rbegin() = L']';
      }
      sql->append(cname);
      //sql->append((*i));
      i++;
   }

   sql->append(L" FROM '");
   sql->append(tableName);
   sql->append(L"' WHERE (");

   int ctr = 1;
   i = params.begin();
   wchar_t buf[20];
   while( i != params.end() )
   {
      if( i != params.begin() ) sql->append(L" AND ");

      _itow(ctr++, buf, 10);
      sql->append(L"\"");
      sql->append(*i);
      sql->append(L"\"");
      sql->append(L" = ?");
      sql->append(buf);

      i++;
   }

   sql->append(L")");
}

// ridInParams == false : INSERT OR REPLACE INTO tableName(col1 [, coli]) VALUES (?1 [, ?i])
// ridInParams == true  : UPDATE tableName SET col1 = ?1 [, col2 = ?i] WHERE ROWID = ?n
void SQLTable::MakeInsertQuery(std::wstring *sql, const std::vector<std::wstring>& fields)
{
   ATLASSERT(fields.size() > 0);

   sql->assign(L"INSERT OR REPLACE INTO '");
   sql->append(tableName);
   sql->append(L"' (");

   std::wstring val(L" ) VALUES (");

   wchar_t buf[20];
   int ctr = 1;
   vector<wstring>::const_iterator i = fields.begin();
   while( i != fields.end() )
   {
      if( i != fields.begin() ) { sql->append(L","); val.append(L","); }

      sql->append(*i);

      _itow(ctr++, buf, 10);
      val.append(L"?");
      val.append(buf);

      i++;
   }

   sql->append(val);
   sql->append(L")");
}

// fields.size() == 0 : DELETE FROM tableName WHERE ROWID = ?1
// fields.size() != 0 : DELETE FROM tableName WHERE (col1 = ?1 [ and coli = ?i ])
void SQLTable::MakeRemoveQuery(std::wstring *sql, const std::vector<std::wstring>& fields)
{
   sql->assign(L"DELETE FROM '");
   sql->append(tableName);
   sql->append(L"' WHERE ");
   if( fields.size() == 0 )
   {
      sql->append(L"ROWID = ?1");
   } else
   {
      sql->append(L"(");

      wchar_t buf[20];
      int ctr = 1;
      vector<wstring>::const_iterator i = fields.begin();
      while( i != fields.end() )
      {
         if( i != fields.begin() ) sql->append(L" AND ");
         sql->append(*i);

         _itow(ctr++, buf, 10);
         sql->append(L" = ?");
         sql->append(buf);

         i++;
      }

      sql->append(L")");
   }
}

// SELECT [DISTINCT] col1 [, col2] FROM tableName [WHERE whereStr]
void SQLTable::MakeSelectQuery(std::wstring *sql, const std::vector<std::wstring>& fields, bool distinct, const wchar_t *addStr)
{
   sql->assign(L"SELECT ");
   if( distinct )
      sql->append(L"DISTINCT ");

   vector<wstring>::const_iterator i = fields.begin();
   while( i != fields.end() )
   {
      if( i != fields.begin() ) sql->append(L",");
      std::wstring cname(*i);
      if( *cname.begin() == L'"' )
      {
         *cname.begin() = L'[';
         *cname.rbegin() = L']';
      }
      sql->append(cname);
      //sql->append(*i);
      i++;
   }

   sql->append(L" FROM '");
   sql->append(tableName);
   sql->append(L"'");

   if( *addStr != L'\0' )
   {
      sql->append(L" ");
      sql->append(addStr);
   }
}

void SQLTable::MakeUpdateQuery(std::wstring *sql, const std::vector<std::wstring>& fields)
{
   sql->assign(L"UPDATE '");
   sql->append(tableName);
   sql->append(L"' SET ");

   wchar_t buf[20];
   int ctr = 1;
   vector<wstring>::const_iterator i = fields.begin();
   for( ;i != fields.end(); i++ )
   {
      if( !wcsicmp((*i).c_str(), L"ROWID") ) continue;

      if( i != fields.begin() ) sql->append(L",");

      sql->append(*i);

      _itow(ctr++, buf, 10);
      sql->append(L" = ?");
      sql->append(buf);
   }

   _itow(ctr, buf, 10);
   sql->append(L" WHERE ROWID = ?");
   sql->append(buf);
}

bool SQLTable::PrepareRead(const IReflectableData *rdata, const IReflectableData *pdata, 
                           const wchar_t *paramStr, bool ridInResult, bool ridInParams)
{
   vector<wstring> fields, pfields;
   if( !GetFields(&fields) ) return false;
   result.Prepare(rdata, &fields, ridInResult);
   
   StringToList(&pfields, paramStr);
   params.Prepare(pdata, &pfields, ridInParams);

   wstring sql;
   MakeSelectQuery(&sql, fields, pfields);

   return PrepareStmt(sql, opRead, (DWORD)&rdata->GetType(), paramStr);
}

bool SQLTable::PrepareWrite(const IReflectableData &data, bool ridInParams, const wchar_t *excluded)
{
   vector<wstring> fields;
   if( !GetFields(&fields) ) return false;

   if( excluded != NULL && *excluded != L'\0' ) // remove excluded fields
   {
      std::vector<wstring>::iterator i = fields.begin();
      while( i != fields.end() )
      {
         if( wcsstr(excluded, (*i).c_str()) != NULL )
            i = fields.erase(i);
         else
            i++;
      }
   }
   params.Prepare(&data, &fields, ridInParams);

   // remove ROWID field from vector (set ROWID in where)
   if( ridInParams ) fields.pop_back();
   
   wstring sql;
   if( ridInParams ) MakeUpdateQuery(&sql, fields);
   else MakeInsertQuery(&sql, fields);

   return PrepareStmt(sql, opWrite, (DWORD)&data.GetType(), (ridInParams) ? L"ROWID" : excluded);
}

bool SQLTable::PrepareRemove(const IReflectableData *data, const wchar_t *paramStr)
{
   vector<wstring> fields;
   if( *paramStr != L'\0' )
      StringToList(&fields, paramStr);

   bool ridInParam = (data == NULL);
   params.Prepare(data, &fields, ridInParam);

   wstring sql;
   MakeRemoveQuery(&sql, fields);

   return PrepareStmt(sql, opRemove, (ridInParam) ? 0 : (DWORD)&data->GetType(), (ridInParam) ? L"ROWID" : paramStr);
}

bool SQLTable::PrepareSelect(const IReflectableData &data, const wchar_t *stmt)
{
   vector<wstring> fields;

   const DataReflector &r = data.GetType();
   int count = r.Count();
   for( int i = 0; i<count; i++ )
      fields.push_back(r.Type(i).name);

   result.Prepare(&data, &fields, false);

   return PrepareStmt(stmt, opSelect, (DWORD)&data.GetType(), stmt);
}

bool SQLTable::PrepareSelect(const IReflectableData &data, bool distinct, const wchar_t *addStr)
{
   vector<wstring> fields;
   if( !GetFields(&fields) ) return false;

   // special case
   if( data.GetType().Find(L"rowid") >= 0 )
      fields.push_back(L"rowid");

   result.Prepare(&data, &fields, false);

   wstring sql;
   MakeSelectQuery(&sql, fields, distinct, addStr);

   return PrepareStmt(sql, opSelect, (DWORD)&data.GetType(), addStr);
}

bool SQLTable::PrepareUpdate(const IReflectableData &data, const wchar_t *paramFields)
{
   vector<wstring> fields;
   if( *paramFields != L'\0' )
      StringToList(&fields, paramFields);

   params.Prepare(&data, &fields, true);

   wstring sql;
   MakeUpdateQuery(&sql, fields);

   return PrepareStmt(sql, opUpdate, (DWORD)&data.GetType(), paramFields);
}

bool SQLTable::PrepareStmt(const std::wstring &sql, Operations op, DWORD dt, const wchar_t *ot)
{
   if( opStmt != NULL )
   {
      lastOp = opNone;
      sql_finalize(opStmt);
      opStmt = NULL;
   }

   int rc = sql_prepare16_v2(db, sql.c_str(), sql.size() * sizeof(wchar_t), &opStmt, NULL);
   if( rc == SQLITE_OK )
   {
      lastOp = op;
      dataTag = dt;
      opTag = ot;
   }
   return (rc == SQLITE_OK);
}

ROWID SQLTable::Read(IReflectableData* data, const wchar_t *keyMembers)
{
   if( db == NULL ) return NO_ROWID;

   if( !CheckStatement(data, opRead, keyMembers) && !PrepareRead(data, data, keyMembers, true, false) )
      return NO_ROWID;

   params.Read(data, opStmt);
   ROWID rid = NO_ROWID;
   if( sql_step(opStmt) == SQLITE_ROW )
      rid = result.Write(data, opStmt);

   sql_reset(opStmt);
   return rid;
}

bool  SQLTable::Read(IReflectableData* data, const ROWID& id)
{
   if( db == NULL ) return false;

   if( !CheckStatement(data, opRead, L"ROWID") && !PrepareRead(data, NULL, L"ROWID", false, true) )
      return false;

   params.RID(id);
   // *data - dummy param
   // see second param in PrepareRead 
   params.Read(data, opStmt); 
   int rc = sql_step(opStmt);
   if( rc == SQLITE_ROW )
      result.Write(data, opStmt);

   sql_reset(opStmt);
   return (rc == SQLITE_ROW); 
}

ROWID SQLTable::Write(const IReflectableData& data, const wchar_t *exclude)
{
   ROWID rid = NO_ROWID;
   if( db == NULL ) return rid;

   if( !CheckStatement(&data, opWrite, exclude) && !PrepareWrite(data, false, exclude) )
      return rid;


   params.Read(&data, opStmt);
   int rc = sql_step(opStmt);
   if( rc == SQLITE_DONE )
      rid = sql_last_insert_rowid(db);

   DoCommand();
   sql_reset(opStmt);
   return rid;
}

bool  SQLTable::Write(const IReflectableData& data, const ROWID& id)
{
   if( db == NULL ) return false;

   if( !CheckStatement(&data, opWrite, L"ROWID") && !PrepareWrite(data, true, L"") )
      return false;

   params.RID(id);
   params.Read(&data, opStmt);

   int rc = sql_step(opStmt);
   DoCommand();
   sql_reset(opStmt);
   return (rc == SQLITE_DONE);
}

bool SQLTable::Remove(const ROWID& id)
{
   if( db == NULL ) return false;

   if( !CheckStatement(NULL, opRemove, L"ROWID") && !PrepareRemove(NULL, L"") )
      return false;

   params.RID(id);
   params.Read(NULL, opStmt);

   int rc = sql_step(opStmt);
   DoCommand();
   sql_reset(opStmt);
   return (rc == SQLITE_DONE);
}

bool SQLTable::Remove(const IReflectableData& data, const wchar_t *keyMembers)
{
   if( db == NULL ) return false;

   if( !CheckStatement(&data, opRemove, keyMembers) && !PrepareRemove(&data, keyMembers) )
      return false;

   params.Read(&data, opStmt);

   int rc = sql_step(opStmt);

   DoCommand();
   sql_reset(opStmt);
   return (rc == SQLITE_DONE);
}

bool SQLTable::Select(const wchar_t *stmt, IReflectableData *data)
{
   if( db == NULL ) return false;

   if( !CheckStatement(data, opSelect, stmt) )
   {
      if( !PrepareSelect(*data, stmt) )
         return false;
   } else
   {
      sql_reset(opStmt);
   }

   int rc = sql_step(opStmt);
   if( rc == SQLITE_ROW )
      result.Write(data, opStmt);

   return (rc == SQLITE_ROW);
}

bool SQLTable::Select(IReflectableData *data, const wchar_t *addStr, bool distinct)
{
   if( db == NULL ) return false;

   if( !CheckStatement(data, opSelect, addStr) )
   {
      if( !PrepareSelect(*data, distinct, addStr) )
         return false;
   } else
   {
      sql_reset(opStmt);
   }

   int rc = sql_step(opStmt);
   if( rc == SQLITE_ROW )
      result.Write(data, opStmt);

   return (rc == SQLITE_ROW);
}

bool SQLTable::Update(const IReflectableData& data, const wchar_t *fields, const ROWID& id)
{
   if( db == NULL ) return false;

   if( !CheckStatement(&data, opUpdate, fields) && !PrepareUpdate(data, fields) )
      return false;

   params.RID(id);
   params.Read(&data, opStmt);
   int rc = sql_step(opStmt);

   DoCommand();
   sql_reset(opStmt);
   return (rc == SQLITE_DONE);
}

bool SQLTable::SelectNext(IReflectableData *data)
{
   int rc = sql_step(opStmt);
   if( rc == SQLITE_ROW )
      result.Write(data, opStmt);

   return (rc == SQLITE_ROW);
}

int SQLTable::Count()
{
   std::wstring sql(L"SELECT COUNT(*) FROM '");
   sql += tableName;
   sql += L"'";

   int count = 0;
   sqlite3_stmt *stmt;
   int rc = sql_prepare16_v2(db, sql.c_str(), sql.size() * sizeof(wchar_t), &stmt, NULL);
   if( rc == SQLITE_OK )
   {
      if( sql_step(stmt) == SQLITE_ROW )
         count = sql_column_int(stmt, 0);
      sql_finalize(stmt);
   }

   return count;
}

void SQLTable::RIDList(std::vector<ROWID> *rids, const wchar_t *addStr)
{
   std::wstring sql(L"SELECT ROWID FROM '");
   sql += tableName;
   sql += L"'";
   if( *addStr != L'\0' )
   {
      sql += L" ";
      sql += addStr;
   }

   __int64 rid = 0;
   sqlite3_stmt *stmt = NULL;
   int rc = sql_prepare16_v2(db, sql.c_str(), sql.size() * sizeof(wchar_t), &stmt, NULL);
   if( rc == SQLITE_OK )
   {
      while( sql_step(stmt) == SQLITE_ROW )
      {
         rid = sql_column_int64(stmt, 0);
         rids->push_back(rid);
      }
   }

   sql_finalize(stmt);

   return;
}

static SQLTable::FieldType StringToFieldType(const wchar_t *field)
{
   if( wcsicmp(field, L"INTEGER") ) return SQLTable::ftInteger;
   if( wcsicmp(field, L"TEXT") ) return SQLTable::ftText;
   if( wcsicmp(field, L"BLOB") ) return SQLTable::ftBlob;
   if( wcsicmp(field, L"REAL") ) return SQLTable::ftReal;
   return SQLTable::ftNull;
}

bool SQLTable::GetFieldsDef(std::vector<FieldDef>* fields)
{
   if( db == NULL ) return false;

   std::wstring str(L"PRAGMA TABLE_INFO('");
   str += tableName;
   str += L"')";

   sqlite3_stmt *stmt = NULL;
   int rc = sql_prepare16_v2(db, str.c_str(), str.size() * sizeof(wchar_t), &stmt, NULL);
   if( rc != SQLITE_OK ) return false;

   while( sql_step(stmt) == SQLITE_ROW )
   {
      const wchar_t *colName = (const wchar_t *)sql_column_text16(stmt, 1);
      const wchar_t *colType = (const wchar_t *)sql_column_text16(stmt, 2);

      FieldDef fd;
      fd.name = colName;
      fd.type = StringToFieldType(colType);

      fields->push_back(fd);
   }
   sql_finalize(stmt);
   return true;
}

bool SQLTable::PrepareCommand(const std::wstring& stmt, const std::vector<MemberType*>& params)
{
   this->params.Prepare(params);
   if( !PrepareStmt(stmt, opExecCommand, 0, stmt.c_str()) )
      return false;

   return true;
}

bool SQLTable::ExecCommand(const IReflectableData &data)
{
   params.Read(&data, opStmt);
   int rc = sql_step(opStmt);
   DoCommand();
   sql_reset(opStmt);
   return (rc == SQLITE_DONE);
}

struct FDCompare
{
   bool operator()(const SQLTable::FieldDef& _Left, const SQLTable::FieldDef& _Right) const
   {
      return (_Left.name.compare(_Right.name) < 0);
   }
};

static void GetTypeFields(std::set<SQLTable::FieldDef, FDCompare> *fields, const DataReflector& dr)
{
   int count = dr.Count();
   for( int i=0; i<count; i++ )
   {
      SQLTable::FieldDef fd;
      const MemberType& mt = dr.Type(i);

      fd.name = mt.name;
      fd.type = SQLTable::MemberTypeToFieldType((MemberType::DataTypes)mt.type);

      fields->insert(fd);
   }
}

bool SQLTable::CheckDBFormat(const DataReflector& dr)
{
   std::vector<SQLTable::FieldDef> dbFields;
   std::set<SQLTable::FieldDef, FDCompare> docFields;

   if( GetFieldsDef(&dbFields) )
   {
      GetTypeFields(&docFields, dr);

      std::vector<SQLTable::FieldDef>::const_iterator i = dbFields.begin();
      for( ; i != dbFields.end(); i++ )
      {
         std::set<SQLTable::FieldDef, FDCompare>::iterator fnd = docFields.find(*i);
         if( fnd != docFields.end() ) docFields.erase(fnd);
      }

      if( docFields.size() > 0 )
      {
         dbFields.clear();
         std::set<SQLTable::FieldDef, FDCompare>::iterator fi = docFields.begin();
         for( ; fi != docFields.end(); fi++ )
            dbFields.push_back(*fi);

         AddDBFields(dbFields);
      }
   }

   return true;
}

//
//------------------------------------- FTSTable ----------------------------------------
//
#include <sstream>
FTSTable::FTSTable(const std::wstring &name) : SQLTable(name)
{
}

bool FTSTable::Searching(Result *result, const std::wstring &text, const std::wstring &field, const std::wstring *whereStr)
{
   wstringstream sql;
   sql << L"SELECT rowid  FROM '" << tableName 
      << L"' WHERE " << field << L" LIKE '%" << text << L"%'";
   if( whereStr != NULL && !whereStr->empty() )
      sql << L" AND (" << (*whereStr) << L")";

   sql << L" ORDER BY " << field;

   sqlite3_stmt *stmt;
   int rc = sql_prepare16_v2(db, sql.str().c_str(), -1, &stmt, NULL);
   if( rc == SQLITE_OK )
   {
      while( sql_step(stmt) == SQLITE_ROW )
      {
         ROWID rid = sql_column_int64(stmt, 0);
         result->push_back(rid);
      }
   }
   sql_finalize(stmt);

   return (rc == SQLITE_OK);
}

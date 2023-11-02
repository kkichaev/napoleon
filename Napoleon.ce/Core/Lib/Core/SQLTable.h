/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Работа с SQLite на WinCE
 *
 *  ert   12/06/2009   creating
 */ 
#ifndef __SQLITE_TABLE_H
#define __SQLITE_TABLE_H

#include <Reflection.h>
#include <TypeHolder.h>

struct sqlite3_stmt;

struct RowID : public IReflectableData
{
   ROWID rowid;

   DECLARE_TYPE_REFLECTION(RowID);
};

class SQLTable
{
public:
   struct IBinder
   {
      IBinder(int index) { this->index = index; }

      virtual ~IBinder() {}
      virtual void Read(const IReflectableData* data, sqlite3_stmt *stmt) = 0;

      int index;
   };

   struct OBinder
   {
      OBinder(int index) { this->index = index; }

      virtual ~OBinder() {}
      virtual void Write(IReflectableData* data, sqlite3_stmt *stmt) = 0;

      int index;
   };

   class ParamBinder : public std::vector<IBinder*>
   {
   public:
      // prepare remove tableField what not in data members
      // bining in tableFields order
      void Prepare(const IReflectableData* data, std::vector<std::wstring> *tableFields, bool addRID);
      void Prepare(const std::vector<MemberType*>& members);

      void Read(const IReflectableData* data, sqlite3_stmt *stmt);
      void RID(const ROWID& rid) { this->rid = rid; }

   protected:
      IBinder* CreateBinder(const MemberType &mt, int pos);
      ROWID rid;
   };

   class ResultBinder : public std::vector<OBinder*>
   {
   public:
      // prepare remove tableField what not in data members
      // bining in tableFields order
      void Prepare(const IReflectableData* data, std::vector<std::wstring> *tableFields, bool addRID);
      ROWID Write(IReflectableData* data, sqlite3_stmt *stmt);

   protected:
      OBinder* CreateBinder(const MemberType &mt, int pos);
      ROWID rid;
      StringHolder holder;
   };

   enum Operations { opNone, opRead, opWrite, opRemove, opSelect, opUpdate, opExecCommand };
   enum FieldType { ftNull, ftInteger, ftReal, ftText, ftBlob };

   typedef struct _ft
   {
      FieldType type;
      std::wstring name;
   } FieldDef;

   static bool OpenDB(const char *dbName);
   static bool OpenDB(const wchar_t *dbName);
   static void CloseDB();

   static bool IsTableExist(const wchar_t *tableName);
   static bool DropTable(const wchar_t *tableName);

   static bool Execute(const wchar_t *sql);
   static bool Execute(const char *sql);

   static FieldType MemberTypeToFieldType(MemberType::DataTypes type);

   // pulsCount - set count after that end transaction invoked
   static bool StartTransaction(int pulsCount);
   // explicit call reset pulsCount
   static bool EndTransaction();

   SQLTable(const std::wstring &tableName);
   ~SQLTable();

   const wchar_t* TableName() const { return tableName.c_str(); }

   bool Create(const DataReflector& reflector, const wchar_t *keyField);
   bool CreateIndex(const wchar_t *indexFields);

   bool PrepareCommand(const std::wstring& stmt, const std::vector<MemberType*>& params);
   bool ExecCommand(const IReflectableData &data);

   // keyMember - перечисленные через запятую ключевые поля по которым ищется запись
   ROWID Read(IReflectableData* data, const wchar_t *keyMembers);
   bool  Read(IReflectableData* data, const ROWID& id);

   // use INSERT OR REPLACE
   // insert all fields< what db contains
   ROWID Write(const IReflectableData& data) { return Write(data, L""); }
   ROWID Write(const IReflectableData& data, const wchar_t *exclude);
   bool  Write(const IReflectableData& data, const ROWID& id);

   bool Update(const IReflectableData& data, const wchar_t *fields, const ROWID& id);

   bool Remove(const ROWID& id);
   bool Remove(const IReflectableData& data, const wchar_t *keyMembers);

   bool Select(const wchar_t *stmt, IReflectableData *data);
   bool Select(IReflectableData *data, const wchar_t *addStr = L"", bool distinct = false);
   bool SelectNext(IReflectableData *data);

   void RIDList(std::vector<ROWID> *rids, const wchar_t *addStr = L"");

   int Count();

   bool GetFieldsDef(std::vector<FieldDef>* fields);
   bool AddDBFields(const std::vector<FieldDef>& fields);

   bool CheckDBFormat(const DataReflector& dr);

protected:
   // call every write or remove for count operations in transaction
   static void DoCommand();
   static int pulsCount, doCount;

protected:

   bool PrepareRead(const IReflectableData *result, const IReflectableData *params, 
      const wchar_t *paramStr, bool ridInResult, bool ridInParams);
   bool PrepareWrite(const IReflectableData &params, bool ridInParams, const wchar_t *exclude);
   bool PrepareRemove(const IReflectableData *params, const wchar_t *param);
   bool PrepareSelect(const IReflectableData &params, bool distinct, const wchar_t *addStr);
   bool PrepareSelect(const IReflectableData &params, const wchar_t *stmt);
   bool PrepareUpdate(const IReflectableData &params, const wchar_t *fields);

   bool PrepareStmt(const std::wstring &sql, Operations op, DWORD dataTag, const wchar_t *opTag);

   void MakeSelectQuery(std::wstring *sql, const std::vector<std::wstring>& fields, const std::vector<std::wstring>& params);
   void MakeInsertQuery(std::wstring *sql, const std::vector<std::wstring>& fields);
   void MakeRemoveQuery(std::wstring *sql, const std::vector<std::wstring>& fields);
   void MakeSelectQuery(std::wstring *sql, const std::vector<std::wstring>& fields, bool distinct, const wchar_t *addStr);
   void MakeUpdateQuery(std::wstring *sql, const std::vector<std::wstring>& fields);

   bool GetFields(std::vector<std::wstring> *fields);

   bool CheckStatement(const IReflectableData* data, Operations op, const wchar_t *tag)
   {
      DWORD dtag = (data != NULL ) ? (DWORD)&data->GetType() : 0;
      return (lastOp == op && dataTag == dtag && opTag.compare(tag) == 0);
   }

   std::wstring tableName;

   ParamBinder params;
   ResultBinder result;

   Operations lastOp;
   std::wstring opTag;
   DWORD dataTag;
   sqlite3_stmt *opStmt;
};

/*
 Full Text Search
*/
class FTSTable : public SQLTable
{
public:
   typedef std::vector<ROWID> Result;

   FTSTable(const std::wstring &name);

   // строки хранятся в нижнем регистре
   bool Searching(Result *result, const std::wstring &text, const std::wstring &field, const std::wstring *whereStr);
};

#endif

/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * Интерфесы объекта
 *
 * ert   01/02/2013   creating
 */

#ifndef __BINDER_H
#define __BINDER_H

#include "MySQLDrv.h"
#include <isessobj.h>

using namespace GRServer;

class FieldBinder
{  
public:
   static FieldBinder* Create(const IObjectData::Field& field, int index);
   
   FieldBinder(const MemberFormat& format, int _index);
   virtual ~FieldBinder() {}
   
   virtual bool Bind(MYSQL_BIND *bind) = 0;

   virtual bool Read(Object* m, MYSQL_STMT *stmt, int column) = 0;
   virtual bool Write(const Object& o) { return false; }

   virtual void GetType(MemberFormat *type) const = 0;
   virtual void GetValue(Member* value) const = 0;

   const std::wstring& Name() const { return name; }

protected:
   std::wstring name;
   int index;

   my_bool isNull;
   my_bool isError;
   DWORD length;
};

class Binder
{
public:
   Binder();
   virtual ~Binder() { Close(); }

   virtual bool PrepareRead(const ISessionObject& obj, const std::string& filter, MYSQL* db);

   virtual bool MoveNext(Object *parentObject)
   { 
      if( stmt == NULL )
         return false;

      int res = mysql_stmt_fetch(stmt);
		if (res == 1)
			AddErrorsToLog(stmt, IErrorLogger::Full);
      return (res == 0 || res == MYSQL_DATA_TRUNCATED);
   }

   virtual bool Read(Object* o) const;

   const MemberFormat* FieldType(const wchar_t* name) const;
   const Member* Value(const wchar_t* name) const;

   virtual void Close();

protected:
   mutable MemberFormat format;
   mutable Member value;

   std::vector<FieldBinder*> fields;
   std::vector<FileField*> files;

   MYSQL_STMT *stmt;
   MYSQL_BIND *bind;

   virtual bool PrepareStmt(MYSQL* db, const std::string& stmt);
	bool BindFields();
};

class ParamBinder : public Binder
{
public:
   ParamBinder();

   bool WriteParams(const Object& obj);
   virtual void Close();
   
   void AddParam(std::string* paramStmt, const IObjectData::Field& field, int fldIndex);
   
protected:
   std::vector<FieldBinder*> params;
   MYSQL_BIND *paramBind;

   virtual bool PrepareStmt(MYSQL* db, const std::string& stmt);
};


class ChildBinder : public ParamBinder
{
public:
   ChildBinder() : executed(false) {}
   
   virtual bool PrepareRead(const ISessionObject& obj, const std::string& filter, MYSQL* db)
   {
		std::string paramStmt;
      if( !PrepareFKStmt(&paramStmt, obj) )
         return false;

      return ParamBinder::PrepareRead(obj, paramStmt, db);
   }

   virtual bool MoveNext(Object *parentObject)
   {
      if( parentObject == NULL || stmt == NULL )
         return false;

      if( !executed )
      {
         WriteParams(*parentObject);
         mysql_stmt_execute(stmt);
			mysql_stmt_store_result(this->stmt);
		}
      
      executed = Binder::MoveNext(NULL);
		return executed;
   }

protected:
   bool executed;

   bool PrepareFKStmt(std::string* paramStmt, const ISessionObject& obj);
};

class MYSQLReader : public IDataSource::IReader
{
public:
   MYSQLReader(const ISessionObject &object, MYSQL *connection, const CString* filter);
   virtual ~MYSQLReader() { Close(); }

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object)
   {
      this->filter.assign(filter);
      return true; 
   }

   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual Binder* CreateBinder() const { return new Binder(); }

protected:
	bool PrepareBinder(const std::string& parsedFilter);

protected:
   const ISessionObject& obj;
   std::wstring filter;
	std::string parsedFilter;
   Binder *binder;
   MYSQL *db;
};

class WriteBinder : public ParamBinder
{
public:
   WriteBinder() : fkIndex(-1) {}

   virtual bool PrepareWrite(const ISessionObject& object, MYSQL* db);
   bool Write(const Object& o, const Object* parent);

protected:
	bool CreateInsertStmt(const ISessionObject& object, const std::vector<std::wstring>& keyFields, MYSQL* db);

	DWORD fkIndex;
};

class RemoveFKBinder : public ParamBinder
{
public:
   bool PrepareFKRemove(const ISessionObject& object, MYSQL* db);
   bool Remove(const Object& parentObj);
};

class MYSQLWriter : public IDataSource::IWriter
{
public:
   MYSQLWriter(const ISessionObject& object, MYSQL* connection);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid) { return Write(o, NULL, rid); }
   virtual void Close();

   bool Write(const Object& o, const Object* parent, RowID *rid);

protected:
   int doCount;
   MYSQL* connection;
   WriteBinder* writer;
};

#endif
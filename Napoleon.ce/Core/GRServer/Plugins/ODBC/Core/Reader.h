/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   17/11/2012   creating
 */

#ifndef __OLE_READER_H
#define __OLE_READER_H

#include "Source.h"
#include "Binder.h"

using namespace GRServer;
using namespace std;

class ChildBinder : public ParamBinder
{
public:
   ChildBinder();
   
   virtual bool ExecuteReader(const std::wstring& stmt);
	virtual bool PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor);
   virtual bool MoveNext(Object *parentObject);

protected:
   bool executed;

protected:
   bool PrepareFKStmt(std::wstring* stmt, const ISessionObject& obj, ODBCFlavor* flavor);
};

class Reader : public IDataSource::IReader
{
public:
	Reader(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor, const std::vector<wstring> &filters, 
      bool debug, ParamHelper* defaults, const CString* whereFilter = NULL, const CString* stmt = NULL);
   virtual ~Reader();

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* o) const;

	virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual ReadBinder* CreateBinder() const { return new ReadBinder(); }

   virtual const ParamHelper* GetParamHelper() const { return &params; }

protected:
   vector<wstring> filters;
   CString parsedFilter;

   ReadBinder* binder;
   const ISessionObject& obj;
   SQLHDBC hDbc;
	bool debug;
   ODBCFlavor* flavor;

	bool isRoot;
	ParamHelper params;
	std::wstring whereFilter;
	std::wstring stmt;
	//std::string tableName;

   void PrepareBinder(const std::wstring& filter);
};

class ChildReader : public Reader
{
public:
   ChildReader(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor) : 
      Reader(object, hDbc, flavor, vector<wstring>(), false, NULL) {}
   virtual ~ChildReader() {} 

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }

   virtual ReadBinder* CreateBinder() const { return new ChildBinder(); }
};

#endif
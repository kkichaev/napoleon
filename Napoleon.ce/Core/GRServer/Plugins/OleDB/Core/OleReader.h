/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   17/11/2012   creating
 */

#ifndef __OLE_READER_H
#define __OLE_READER_H

#include "OleSource.h"
#include "Binder.h"

using namespace GRServer;
using namespace std;

class ChildBinder : public ParamBinder
{
public:
   ChildBinder();
   
   virtual HRESULT OpenReader(CSession& session, const std::wstring& stmt);
   virtual bool PrepareRead(const ISessionObject& obj, const std::wstring& filter, CSession& session);
   virtual bool MoveNext(Object *parentObject);

protected:
   bool executed;

protected:
   bool PrepareFKStmt(std::wstring* stmt, const ISessionObject& obj);
};

class OleReader : public IDataSource::IReader
{
public:
   OleReader(const ISessionObject& object, CDataConnection& connection, const CString* filter);
   virtual ~OleReader();

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { this->filter.assign(filter); return true; }
   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual Binder* CreateBinder() const { return new Binder(); }
protected:
   CString filter, parsedFilter;
   Binder* binder;
   const ISessionObject& obj;
   CSession& session;
};

class OleChildReader : public OleReader
{
public:
   OleChildReader(const ISessionObject& object, CDataConnection& connection) : OleReader(object, connection, NULL) {}
   virtual ~OleChildReader() {} 

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }

   virtual Binder* CreateBinder() const { return new ChildBinder(); }
};

#endif
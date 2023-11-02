/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB QuerySource
 *
 * ert   01/11/2012   creating
 */
#ifndef __QUERY_SOURCE_H
#define __QUERY_SOURCE_H

using namespace GRServer;

#include "MySQLDrv.h"

class SQLSource : public IDataSource::ICreator
{
public:
   SQLSource() {}
   ~SQLSource() {}

   virtual const wchar_t* Name() const { return L"MySQLSource"; }

   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const;
};

class SQTable : public SQLSource
{
public:
   SQTable() {}
   ~SQTable() {}

   virtual const wchar_t* Name() const { return L"SQTable"; }
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

class SQLFolderCreator : public IDataSource::ICreator
{
public:
   SQLFolderCreator() {}
   ~SQLFolderCreator() {}

   virtual const wchar_t* Name() const { return L"SQLFolder"; }
   virtual IDataSource::IReader*  CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class QueryBinder : public Binder
{
public:
   bool Prepare(MYSQL *db, const std::string& stmt, const std::vector<const ISessionObject*>& objects);
   bool IsOpened() const { return (bind != NULL); }

   virtual bool Read(Object* o) const;

protected:
   std::vector<const MemberFormat*> formats;
};

class QueryReader : public IDataSource::IReader
{
public:
   QueryReader(MYSQL *db, const std::wstring& stmt, const ISessionObject& object, bool debug);
   ~QueryReader();

   virtual bool MoveNext(Object *parentObject);

   // подставляем только объекты созданные методом Create
   // иначе будут проблемы при удалении объекта созданного в DLL
   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return binder.FieldType(name); }
   virtual const Member* Value(const wchar_t* name) const { return binder.Value(name); }

   virtual void AddChild(const std::wstring& childName, IReader* reader) {}

   void AddChildObject(const ISessionObject* object) { childs.push_back(object); }

   Object* GetNext();

protected:
   QueryReader(const ISessionObject& object);

   bool Open();

protected:   
   MYSQL* connection;
   const ISessionObject& object;
   std::vector<const ISessionObject*> childs;
	std::string stmt;

   QueryBinder binder;
   mutable Object* nextObject;
	bool debug;
};

class KeyMember
{
public:
   static KeyMember* Create(const std::wstring& name, Format* format);

   KeyMember(int _index) : index(_index) {}
   virtual ~KeyMember() {}

   virtual KeyMember* Clone() const = 0;
   virtual void Load(const Object& src) = 0;
   virtual bool IsEqual (const KeyMember& _src) const = 0;

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

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

protected:
   QueryReader* parent;
   KeyHolder keyHolder;
   bool keyLoaded;
};

#endif
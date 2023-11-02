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

#include "Source.h"

//class SQLSource : public IDataSource::ICreator
//{
//public:
//   SQLSource() {}
//   ~SQLSource() {}
//
//   virtual const wchar_t* Name() const { return L"SQLSource"; }
//
//   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const;
//   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
//   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
//};

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

//class SQLChildQueryCreator : public IDataSource::ICreator
//{
//public:
//	SQLChildQueryCreator() {}
//	~SQLChildQueryCreator() {}
//
//	virtual const wchar_t* Name() const { return L"SQLChildQuery"; }
//	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
//	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
//};

#include "Binder.h"

class QueryBinder : public ReadBinder
{
public:
	QueryBinder();

   bool Prepare(const std::wstring& stmt, const std::vector<const ISessionObject*>& objects, SQLHDBC hDbc, ODBCFlavor* flavor);
   bool IsOpened() const { return (hstmt != NULL); }

   virtual bool Read(Object* o) const;
	virtual void Close();

	FieldBinder* GetBinder(const MemberFormat* format) const;

	virtual bool PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor);

protected:
   typedef std::map<GRServer::Format*, std::vector<FileField*>> ObjFiles;
   ObjFiles objFiles;
   std::vector<const MemberFormat*> formats;
	std::vector<MemberFormat> customFormats;
   ODBCFlavor* flavor;
	//bool prepared;
};

class QueryReader : public IDataSource::IReader
{
public:
	QueryReader(const CString& stmt, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor, bool debug, int rowCount, ParamHelper *defaults);
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

   virtual void AddChildObject(const ISessionObject* object) { childs.push_back(object); }

   virtual Object* GetNext();

	virtual FieldBinder* GetBinder(const MemberFormat* format) const { return binder.GetBinder(format); }

   virtual const ParamHelper* GetParamHelper() const { return NULL; }

protected:
   QueryReader(const ISessionObject& object);

   bool Open();

protected:
   SQLHDBC hDbc;
   ODBCFlavor* flavor;
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
   QueryChildReader(const CString& keyFields, const CString* checkField, const ISessionObject& object, const ISessionObject& _parent);
   ~QueryChildReader();

   virtual bool MoveNext(Object *parentObject);
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
	MemberFormat *checkNull;
};

class SQLCostSource : public IDataSource::ICreator
{
public:
	SQLCostSource() {}
	~SQLCostSource() {}

	virtual const wchar_t* Name() const { return L"SQLCostReader"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

#endif
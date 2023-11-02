/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Папки товара
 *
 * ert   24/05/2010   creating
 */ 
#ifndef __FOLDER_SET_H
#define __FOLDER_SET_H

#include "parse.h"
#include "member.h"
#include "datasource.h"
#include "dbftblset.h"
#include <folderholder.h>

namespace GRServer {

struct DBFFolderSet : public DBFTableSet
{
   virtual const wchar_t* Name() const { return L"DBFFolderSet"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;

   IDataSource::IWriter* CreateWriter(const ParamList& parameters, const ISessionObject& object, const std::string& baseFolder) const;
};

class DBFFolderSetReader : public DBFTableSetReader
{
public:
   DBFFolderSetReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param);
   virtual void Close();
   virtual bool Get(Object* o) const;

protected:
   int keyIndex, valueIndex;
};

class FolderConstructorCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"FolderConstructor"; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

class DBFPriceTable : public DBFTableSet
{
   virtual const wchar_t* Name() const { return L"DBFPriceTable"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
};

struct FolderID : public IFunction
{
   virtual const wchar_t* Name() const;
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object);
};

extern FolderID folderID;
extern GRServer::FolderIDHolder folderHolder;

class TreeReader
{
public:
	TreeReader() { levelIndex = -1; doSort = false; }
	~TreeReader() { Clear(); }

	void SetData(int li, int id, int ri, int ni, bool doSort)
	{
		levelIndex = li; childIndex = id; rowidIndex = ri; curRec = 0; nameIndex = ni; this->doSort = doSort;
	}

	void Add(const std::wstring& parent, Object* obj)
	{
		if (childIndex >= 0)
		{
			//USES_CONVERSION;

			//std::string id = W2A_CP(obj->at(childIndex).str->c_str(), DBF_CODE_PAGE);
			data[parent].push_back(ObjectData(obj, (const std::wstring&)*obj->at(childIndex).str, (const std::wstring&)*obj->at(nameIndex).str));
		}
	}

	bool MoveNext();
	bool Get(Object* o) const;

	void Clear();

protected:
	int levelIndex;
	int childIndex;
	int rowidIndex;
	int nameIndex;
	DWORD curRec;
	bool doSort;

	struct ObjectData
	{
		Object* object;
		std::wstring id;
		std::wstring name;

		ObjectData(Object* o, const std::wstring& id, const std::wstring& name)
		{
			object = o;
			this->id = id;
			this->name = name;
		}

		bool operator < (const ObjectData& src) const
		{
			int cmp = _wcsicmp(name.c_str(), src.name.c_str());
			return (cmp < 0);
		}
	};

	class ObjectList : public std::vector<ObjectData>
	{
	public:
		ObjectList() {}
		~ObjectList()
		{
			iterator i = begin();
			for (; i != end(); i++)
				delete i->object;
		}

		void pop_front() { erase(begin()); }
	};

	typedef std::map<std::wstring, ObjectList> TreeData;
	TreeData data;
	typedef std::vector<TreeData::iterator> PathList;
	PathList path;
};

} // namespace GRServer

#endif

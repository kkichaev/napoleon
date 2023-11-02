/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * DBFTableSet
 *
 * ert   29/04/2010   creating
 */ 
#ifndef __DBF_TABLE_SET_H
#define __DBF_TABLE_SET_H

#include "member.h"
#include "datasource.h"
#include "sources.h"
#include "session.h"

namespace GRServer {

struct TableSetParam;
struct DBFTableSet : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"DBFTableSet"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;

   IDataSource::IWriter* CreateWriter(const TableSetParam& param, const ISessionObject& object, const std::string& baseFolder) const;
   IDataSource::IWriter* CreateWriter(const ParamList& parameters, const ISessionObject& object, const std::string& baseFolder) const;

};

struct DBFShadowReader : public IDataSource::ICreator
{
	virtual const wchar_t* Name() const { return L"DBFShadowReader"; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

/*
GET                       - база текущего пользователя
SELECT
 без фильтра              - база текущего пользователя
 user is null             - commonTable (users.size() == 0)
 user = 'code'            - базы соответствующих пользователей (Intersect with Session::AllowedUserID)
 user in ('code', 'code')
*/
struct TableSetParam
{
   TableSetParam() : userField(L"id"), removeAfterReading(false){ filter.filter = NULL; filter.holder = NULL; }
   ~TableSetParam();

   std::string userTable;
   std::string commonTable;
   std::wstring userField;
   FilterReader::Data filter;
   //FilterData filter;

   std::wstring catalog;
   std::wstring catalogObject;
   bool ordered;
	bool removeAfterReading;

   virtual bool Read(const SessionObject& object, const ParamList& parameters);
   bool ReadCatalog(const std::wstring& val);
};

class UserFieldMap : public std::map<std::string, std::string>
{
public:
   void Load(const std::wstring& field, Session* s);
   const char* Get(const std::string& userid) const;
};

class DBFUserSet
{
public:
   DBFUserSet(const TableSetParam& param, const ISessionObject& object);
   bool SetFilter(const wchar_t* filter, const ISessionObject& object);
	const char* Next(std::wstring* userid, bool canUseCommonTable = true) const;

   void BaseFolderChanged(const std::string& baseFolder,  const ISessionObject& object);

protected:
   std::string userTable;
   std::string commonTable;

   std::vector<std::string> users;
   // если в параметрах userField != id то используется map userId->userField наличие преобразования определяем по размеру userFieldMap
   UserFieldMap userFieldMap;
   mutable int userIndex;
   mutable std::string buf;
	bool isNullUser;
};

class DBFChildTableSetReader;
class DBFTableSetReader : public DBFReader
{
public:
   DBFTableSetReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param);

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual void AddChild(const std::wstring& childName, IReader* reader);

   virtual void SetBaseFolder(const std::string& baseFolder);

	bool OpenNextBase();
	virtual void Close();

protected:
   //IFilterInSet *filter;
   DBFUserSet users;
   std::wstring userID;
   int userIDIndex;
   bool needSetFilter;

   std::string baseFolder;

   const SessionObject& object;

   typedef std::vector<DBFChildTableSetReader*> ChildList;
   ChildList childs;

	bool removeAfterReading;

protected:
   void UpdateChilds(const wchar_t* userid);
};

class DBFChildTableSetReader : public ChildDBFReader
{
public:
   DBFChildTableSetReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param);

   void OpenNewFile(const wchar_t* user);

   virtual void SetBaseFolder(const std::string& baseFolder);
   virtual void AddChild(const std::wstring& childName, IReader* reader);

protected:
   std::string userTable;
   std::string commonTable;

   std::string baseFolder;
   UserFieldMap userFieldMap;

   typedef std::vector<DBFChildTableSetReader*> ChildList;
   ChildList childs;
   void UpdateChilds(const wchar_t* userid);

   //FilterData filter;
   //IFilterInSet *filter;

   //const SessionObject& object;
};

class TableRemover : public IDataSource::IRemover
{
public:
   TableRemover(const TableSetParam& param, const SessionObject& _object);
   virtual ~TableRemover();

   virtual bool Remove(const wchar_t* filter);
   virtual void Close();

   virtual void SetBaseFolder(const std::string& baseFolder);

protected:
   virtual bool CanRemoveTable(const char* table) { return true; }

   std::string baseFolder;

   const SessionObject& object;
   DBFUserSet users;
};


} // namespace GRServer

#endif

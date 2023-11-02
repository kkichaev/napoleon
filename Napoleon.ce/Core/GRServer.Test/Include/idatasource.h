/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * DataSource decl
 *
 * ert   12/03/2010   creating
 */ 
#ifndef __GR_SERVER_I_DATA_SOURCE_H
#define __GR_SERVER_I_DATA_SOURCE_H

#include <Binary.h>
#include "srctype.h"
#include <servobj.h>
#include <gservices.h>

namespace GRServer {

class Object;
class ServerConfig;
//class SessionObject;
class ParamList;
class SourceDefList;
struct IFormatHolder;
class Format;

struct IDataSource
{
   struct IReader
   {
      virtual ~IReader() {}
      virtual bool MoveNext(Object *parentObject) = 0;

      // для Get надо использовать объекты созданные этой функцией
      // иначе не будет работать в DLL
      virtual Object* Create(const Format& format) { return Object::Create(format); }

      // подставляем только объекты созданные методом Create
      // иначе будут проблемы при удалении объекта созданного в DLL
      virtual bool Get(Object* o) const = 0;

      virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return false; }
      virtual void Remove() = 0;
      virtual void Close() = 0;

      virtual const MemberFormat* Type(const wchar_t* name) const = 0;
      virtual const Member* Value(const wchar_t* name) const = 0;
   
      virtual void SetBaseFolder(const std::string& baseFolder) {}
      virtual void AddChild(const std::wstring& childName, IReader* reader) {}
   };

   struct IWriter
   {
      virtual ~IWriter() {}
      virtual bool Prepare(const ISessionObject& object) = 0;
      virtual bool Write(const Object& o, RowID *rid) = 0;
      virtual void Close() = 0;

      virtual void AddChild(IWriter* writer, const std::wstring& typeName) { childs.push_back(writer); }

      virtual void SetBaseFolder(const std::string& baseFolder) {}

      std::vector<IWriter*> childs;
   };

   struct IRemover
   {
      virtual ~IRemover() {}
      virtual bool Remove(const wchar_t* filter) = 0;
      virtual void Close() = 0;
      virtual void AddChild(IRemover* remover, const std::wstring& typeName) { childs.push_back(remover); }

      virtual void SetBaseFolder(const std::string& baseFolder) {}

      std::vector<IRemover*> childs;
   };

   struct IObjSource
   {
      virtual ~IObjSource() {}
      virtual ExchangeList* Do(ISessionObject *object, const std::wstring& action, IFormatHolder* f, BSTR* msg) = 0;
      virtual void Close() = 0;
   };

   struct ISelector
   {
      virtual ~ISelector() {}
      virtual bool Select(Object* selObject, const Object* whereObject, 
         const std::wstring& selStr, const std::wstring& whereStr, const std::wstring* orderStr,
         const Format& selFmt, const Format& whereFormat, 
         const std::vector<int>& selIdx, const std::vector<int>& whereIdx) = 0;

      virtual bool SelectNext(Object* selObject, const Object* whereObject) = 0;
   };

   virtual ~IDataSource() {}

public:
   // static methods
   struct ICreator
   {
      virtual ~ICreator() {}

      virtual const wchar_t* Name() const = 0;
      virtual IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const = 0;
      virtual IWriter*    CreateWriter(IWriter* parent, const ParamList& parameters, const ISessionObject& object) const = 0;
      virtual IRemover*   CreateRemover(IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
      virtual IObjSource* CreateObjSource(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
      virtual ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   };
};

struct IInternalDataSource : public IDataSource::ICreator
{
   virtual ~IInternalDataSource() {}

   virtual IBinary* GetServerData(int id) = 0;
   virtual bool    PutServerData(int id, const Binary& b) = 0;

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config) = 0;
   virtual void    Close() = 0;

   virtual bool    Execute(const wchar_t* stmt, ISession* session) = 0;
   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session) = 0;
	
	virtual bool BackupBase(const char* backupFileName) { return false; }
};

typedef std::vector<IDataSource::IWriter*> WriterList;
typedef std::vector<IDataSource::IRemover*> RemoverList;

struct ObjectSource
{
   ObjectSource() : reader(NULL), writer(NULL), remover(NULL) {}
   virtual ~ObjectSource()
   {
      delete reader;
      delete writer;
      delete remover;
   }

   SourceType type;

   CString readerName;
   IDataSource::IReader* reader;

   CString writerName;
   IDataSource::IWriter* writer;

   CString removerName;
   IDataSource::IRemover* remover;
};

#define SOURCE_SERVICE L"DataSource"
struct IDataSourceRegister
{
   virtual void AddSource(IDataSource::ICreator* creator) = 0;
   virtual void SetDefaultObjSource(IDataSource::ICreator* creator) = 0;
   virtual void RegisterInternalSource(IInternalDataSource* internalSource) = 0;
};

} // namespace GRServer

#endif


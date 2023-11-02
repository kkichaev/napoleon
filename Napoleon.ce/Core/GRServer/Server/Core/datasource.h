/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * DataSource decl
 *
 * ert   12/03/2010   creating
 */ 
#ifndef __GR_SERVER_DATA_SOURCE_H
#define __GR_SERVER_DATA_SOURCE_H

#include <idatasource.h>

namespace GRServer {

class Object;
class ServerConfig;
class SessionObject;
class ParamList;
class SourceDefList;
class Dispatcher;

struct DataSource : public IDataSource
{
   static IReader* CreateReader(const SourceDefList& list, const SessionObject& object, std::wstring *name, SourceType* stype);
   static IWriter* CreateWriter(IWriter* parent, const SourceDefList& list, const SessionObject& object, 
      std::wstring *name, SourceType* srcType);
   static IRemover* CreateRemover(IRemover* parent, const SourceDefList& list, const SessionObject& object, 
      std::wstring *name, SourceType* srcType);
   static IObjSource* CreateObjSource(const SourceDefList& list, const SessionObject& object);
   static ISelector* CreateSelector(const SourceDefList& list, const SessionObject& object);

   static void AddCreator(const ICreator* creator);
   static bool Init(const ServerConfig& config, Dispatcher* dispatcher);
   static void Cleanup();
   static void RegisterInternalSource(IInternalDataSource* internalSource);

   static IDataSourceRegister* GetService();
};

} // namespace GRServer

#endif


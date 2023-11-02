/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Создатели источников данных
 *
 * ert   25/02/2010   creating
 */ 
#ifndef __SOURCE_CREATORS_H
#define __SOURCE_CREATORS_H

#include "sessobj.h"

#include "filter.h"

namespace GRServer {

struct FilterData;
struct DBFCreatorBase : public IDataSource::ICreator
{
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object, 
      FilterReader::Data& filter, const ParamList& parameters) const = 0;
   //virtual IDataSource::IReader* Create(const std::string& fileName, const SessionObject& object, 
   //   const FilterData& filter, const ParamList& parameters) const = 0;
};

struct DBFSourceCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"DBFTable"; }

   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object,
      FilterReader::Data& filter, const ParamList& parameters) const;
   //virtual IDataSource::IReader* Create(const std::string& fileName, const SessionObject& object,
   //   const FilterData& filter, const ParamList& parameters) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;

	// просто удаляем таблицу
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct DBFMaskReaderCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"DBFMaskReader"; }

   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object,
      FilterReader::Data& filter, const ParamList& parameters) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const { return NULL; }
};

struct SequenceSC : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"Sequence"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const { return NULL; }
};

struct KeyValueSC : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"KeyValueTable"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;

   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct KeyValueDBFSC : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"KeyValueDBFTable"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;

   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct UnionSC : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"ObjectUnion"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const { return NULL; }

   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
   { return NULL; }
};

struct TEHCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"DBFCatalogTable"; }

   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object,
      FilterReader::Data& filter, const ParamList& parameters) const;
   //virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object,
   //   const FilterData& filter, const ParamList& parameters) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;
};

struct TRHCreator : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"DBFCatalogItemTable"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;
};

struct MessageCreator : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"MessageTable"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const;

   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct CostCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"DBFCostReader"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;

};

struct CSVCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"CSVTable"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

IInternalDataSource* CreateSQLiteSource();

struct SQLiteSourceCreator : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"SQTable"; }

   virtual IDataSource::IReader*   CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*   CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover*  CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::ISelector* CreateSelector(const ParamList& parameters, const ISessionObject& object) const;
};

struct SQLiteQueryCreator : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"SQLQuery"; }

   virtual IDataSource::IReader*   CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter*   CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IRemover*  CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::ISelector* CreateSelector(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

struct XMLCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"XMLTable"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct XMLUserTableCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"XMLUserTable"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
	{
		return NULL; 
	}
};

struct XMLFolderCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"XMLFolder"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct SQLFolderCreator : public IDataSource::ICreator
{
	virtual const wchar_t* Name() const { return L"SQLFolder"; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

struct SQLExecutorCreator : public IDataSource::ICreator
{
	IInternalDataSource* dataSource;
	SQLExecutorCreator(IInternalDataSource* ds) { this->dataSource = ds; }

	virtual const wchar_t* Name() const { return L"SQLExecutor"; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};


//bool SQLiteInitializer(const GRServer::ServerConfig& config);
//void SQLiteCleanup();

} // namespace GRServer

#endif
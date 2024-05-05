/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 *
 * ert   30/03/2010   creating
 */ 
#include "stdafx.h"
#include "sources.h"
#include "dbftblset.h"
#include "folderset.h"
#include "serversrc.h"
#include "srvdata.h"
#include "reporter.h"

#include <set>
using namespace GRServer;

struct CreatorComparer
{
   bool operator() (const IDataSource::ICreator* _Left, const IDataSource::ICreator* _Right) const
   {
      return (wcscmp(_Left->Name(), _Right->Name()) < 0);
   } 
};

class SourceSet : public std::set<const IDataSource::ICreator*, CreatorComparer>
{
public:
   SourceSet() {}

   ~SourceSet()
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         delete (*i);
   }
};

struct SourceFinder : public IDataSource::ICreator
{
   SourceFinder(const std::wstring &n) : name(n) {}

   virtual const wchar_t* Name() const { return name.c_str(); }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const { return NULL; }

protected:
   const std::wstring& name;
};

static SourceSet sources;
static IDataSource::ICreator* defaultObjCreator = NULL;
IInternalDataSource* GRServer::internalDataSource = NULL;

//
//------------------------------- SourceDefList ------------------------------------
//
void* SourceDefList::CreateSource(SourceKind kind, void* parent, const ISessionObject& object, std::wstring *name, SourceType* srcType) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( srcType != NULL && *srcType != stCommon && i->type != *srcType && i->type != stAny )
         continue;

      const IDataSource::ICreator* creator = NULL;

      //if( i->type == stInternal )
      //   creator = internalDataSource;
      //else
      {
         SourceFinder sf(i->name);
         SourceSet::iterator fnd = sources.find(&sf);
         if( fnd != sources.end() )
            creator = (*fnd);
      }

      if( creator != NULL )
      {
         void *src = NULL;

         if( kind == skReader )
            src = creator->CreateReader(i->parameters, object);
         else if( kind == skWriter )
            src = creator->CreateWriter((IDataSource::IWriter*)parent, i->parameters, object);
         else if( kind == skRemover )
            src = creator->CreateRemover((IDataSource::IRemover*)parent, i->parameters, object);
         else if( kind == skSelector )
            src = creator->CreateSelector(i->parameters, object);
         else if( kind == skObjSource )
         {
            src = creator->CreateObjSource(i->parameters, object);
            if( src == NULL && defaultObjCreator )
               src = defaultObjCreator->CreateObjSource(i->parameters, object);
         }

         if( src != NULL )
         {
            if( name )
               name->assign(i->name);

            if( srcType != NULL )
               *srcType = i->type;

            return src;
         }
      }
   }

   return NULL;
}

//
//------------------------------- IDataSource ------------------------------------
//
IDataSource::IReader* DataSource::CreateReader(const SourceDefList& srcList, const SessionObject& object, std::wstring *name, SourceType* stype)
{
   SourceType ts = stInternal;
   IReader* reader = (IReader*)srcList.CreateSource(SourceDefList::skReader, NULL, object, name, &ts);
	if (reader != NULL)
	{
		*stype = ts;
		return reader;
	}

   return (IReader*)srcList.CreateSource(SourceDefList::skReader, NULL, object, name, stype);
}

IDataSource::IWriter* DataSource::CreateWriter(IWriter* parent, const SourceDefList& srcList, const SessionObject& object, 
                                                std::wstring *name, SourceType* srcType)
{
   return (IWriter*)srcList.CreateSource(SourceDefList::skWriter, parent, object, name, srcType);
}

IDataSource::IRemover* DataSource::CreateRemover(IRemover* parent, const SourceDefList& srcList, const SessionObject& object, 
                                                std::wstring *name, SourceType* srcType)
{
   return (IRemover*)srcList.CreateSource(SourceDefList::skRemover, parent, object, name, srcType);
}

IDataSource::IObjSource* DataSource::CreateObjSource(const SourceDefList& srcList, const SessionObject& object)
{
   return (IObjSource*)srcList.CreateSource(SourceDefList::skObjSource, NULL, object, NULL, NULL);
}

IDataSource::ISelector* DataSource::CreateSelector(const SourceDefList& srcList, const SessionObject& object)
{
   SourceType t = stInternal;
   ISelector* sel = (ISelector*)srcList.CreateSource(SourceDefList::skSelector, NULL, object, NULL, &t);
   if( sel != NULL )
      return sel;

   return (ISelector*)srcList.CreateSource(SourceDefList::skSelector, NULL, object, NULL, NULL);
}


void DataSource::AddCreator(const IDataSource::ICreator* creator)
{
   SourceSet::iterator fnd = sources.find(creator);
	if (fnd != sources.end()) 
	{
		delete (*fnd);
		sources.erase(fnd);
	}

   sources.insert(creator);
}

void DataSource::RegisterInternalSource(IInternalDataSource* internalSource)
{
   internalDataSource = internalSource;
}

class SourceRegister : public IDataSourceRegister
{
public:
   virtual void AddSource(IDataSource::ICreator* creator)
   {
      DataSource::AddCreator(creator);
   }

   virtual void SetDefaultObjSource(IDataSource::ICreator* creator)
   {
      defaultObjCreator = creator;
   }

   virtual void RegisterInternalSource(IInternalDataSource* internalSource)
   {
      DataSource::RegisterInternalSource(internalSource);
   }
};

class ServerInfo : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"ServerInfo"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return new Reader(); }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }

	class Reader : public IDataSource::IReader
	{
		mutable bool readed;

	public:
		Reader() : readed(false) {}

		virtual bool MoveNext(Object *parentObject) { return !readed; }
		virtual void Remove() {}
		virtual void Close() {}

		virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
		virtual const Member* Value(const wchar_t* name) const { return NULL; }

		virtual bool Get(Object* o) const;
	};
};

bool ServerInfo::Reader::Get(Object *o) const
{
	readed = true;

	int idx = o->format.FindMember(L"name");
	if (idx >= 0) {
		o->at(idx).str->assign(PROJECT_NAME);
		return true;
	}
	return false;
}

static SourceRegister sr;
IDataSourceRegister* DataSource::GetService()
{
   return &sr;
}

bool DataSource::Init(const ServerConfig& config, Dispatcher* dispatcher)
{
   AddCreator(new DBFSourceCreator());
   AddCreator(new DBFMaskReaderCreator());
   AddCreator(new SequenceSC());
   AddCreator(new KeyValueSC());
   AddCreator(new KeyValueDBFSC());
   AddCreator(new UnionSC());
   AddCreator(new TEHCreator());
   AddCreator(new TRHCreator());
   AddCreator(new MessageCreator());
   AddCreator(new DBFTableSet());
   AddCreator(new DBFFolderSet());
   AddCreator(new DBFPriceTable());
   AddCreator(new CostCreator());
   AddCreator(new ServerSourceCreator());
	AddCreator(new ActiveUsersCreator());
	AddCreator(new CSVCreator());
   AddCreator(new XMLCreator());
	AddCreator(new XMLUserTableCreator());
	AddCreator(new XMLFolderCreator());
	AddCreator(new ReporterCreater());
	AddCreator(new FolderConstructorCreator());
	AddCreator(new ServerInfo());
	AddCreator(new DBFShadowReader());

   if( internalDataSource == NULL )
   {
      internalDataSource = CreateSQLiteSource();

      IDataSource::ICreator *ic = new SQLiteSourceCreator();
      SourceFinder sf(ic->Name());
      SourceSet::iterator fnd = sources.find(&sf);
      if( fnd != sources.end() )
         delete ic;
      else
         AddCreator(ic);
		AddCreator(new SQLiteQueryCreator());
		AddCreator(new SQLFolderCreator());
	}
   if( !internalDataSource->Init(ObjectDef::GetService(), config) )
      return false;

	AddCreator(new SQLExecutorCreator(internalDataSource));
   AddCreator(new ManagerRightsCreator(internalDataSource));
	return ServerData::Init(dispatcher);
}

void DataSource::Cleanup()
{
	UserActivityHolder::Close();

   internalDataSource->Close();
   delete internalDataSource;
   internalDataSource = NULL;
}

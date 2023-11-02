/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
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

#include "folderset.h"
class SQLFolderReader : public IDataSource::IReader
{
public:
   SQLFolderReader(const ISessionObject& object, const ParamList& parameters);

   ~SQLFolderReader() { delete reader; }

   virtual bool MoveNext(Object* parentObject);
   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) {
      if (reader) reader->SetFilter(filter, object);
      return true;
   }
   virtual void Remove() {}
   virtual void Close() { reader->Close(); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return reader->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return reader->Value(name); }

   virtual const ParamHelper* GetParamHelper() const { return reader != NULL ? reader->GetParamHelper() : NULL; }

protected:
   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder* folderHolder;
   ISession* session;
   const ISessionObject& object;

   const GRServer::Format* format;
   bool readed;
   int parentIndex;
   TreeReader treeReader;

   IDataSource::IReader* reader;
};

static void LoadFieldName(std::wstring* out, const wchar_t* name , const ParamList& parameters
   , const ISessionObject& object)
{
   const Parameter* pf;
   CString* tstr = NULL;
   if ((pf = parameters.Find(name, -1)) != NULL)
   {
      if (object.GetSession().Parse(&tstr, pf->value, &object))
      {
         out->assign((const std::wstring&)*tstr);
      }
   }
   delete tstr;
}

SQLFolderReader::SQLFolderReader(const ISessionObject& _object, const ParamList& parameters) :
   readed(false), object(_object), parentIndex(-1),reader(NULL)
{
   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);
   session = &object.GetSession();

   format = object.Self()->format;
   keyIndex = format->FindMember(L"fid");
   valueIndex = format->FindMember(L"id");
   if (valueIndex >= 0)
   {
      const MemberFormat& mf = format->at(valueIndex);
      if (mf.type != MemberFormat::mtNumber)
         valueIndex = -1;
   }

   std::wstring parentField(L"parent");
   std::wstring childField(L"fid");
   std::wstring ridField(L"id");

   LoadFieldName(&parentField, L"parentField", parameters, object);
   LoadFieldName(&childField, L"childField", parameters, object);
   LoadFieldName(&ridField, L"rowidField", parameters, object);

   parentIndex = format->FindMember(parentField.c_str());

   int ridIndex = ridField.empty() ? -1 : format->FindMember(ridField.c_str());
   if (ridIndex >= 0)
   {
      const MemberFormat& mf = format->at(ridIndex);
      if (mf.type != MemberFormat::mtNumber || (mf.flags & MemberFormat::ExecOnGet) != 0)
         ridIndex = -1;
   }
   
   treeReader.SetData(format->FindMember(L"level")
      , format->FindMember(childField.c_str())
      , ridIndex
      , format->FindMember(L"name")
      , true);

   std::wstring readerName = (parameters.Find(L"stmt", -1) == NULL) ? L"SQTable" : L"SQLQuery";
   SourceFinder sf(readerName);
   SourceSet::iterator fnd = sources.find(&sf);
   if (fnd != sources.end())
   {
      reader = (*fnd)->CreateReader(parameters, _object);
   }
   else
   {
      gServer->AddLog("Can't find SQLForeder reader");
   }
}

bool SQLFolderReader::MoveNext(Object* parentObject)
{
   if (!readed)
   {
      readed = true;

      treeReader.Clear();
      while (reader->MoveNext(parentObject))
      {
         Object* o = Create(*format);
         reader->Get(o);

         const Member& mf = o->at(parentIndex);
         treeReader.Add((const std::wstring&)*mf.str, o);
      }
   }

   return treeReader.MoveNext();
}

bool SQLFolderReader::Get(Object* o) const
{
   treeReader.Get(o);

   if (folderHolder && keyIndex >= 0 && valueIndex >= 0)
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   return true;
}

IDataSource::IReader* SQLFolderCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();

   IDataSource::IReader* ret = NULL;

   return new SQLFolderReader(iobject, parameters);
}

IDataSource::IWriter* SQLFolderCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   return internalDataSource != NULL ? internalDataSource->CreateWriter(parent, parameters, iobject) : NULL;
}

IDataSource::IRemover* SQLFolderCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   return internalDataSource != NULL ? internalDataSource->CreateRemover(parent, parameters, iobject) : NULL;
}


bool DataSource::Init(const ServerConfig& config, Dispatcher* dispatcher)
{
   AddCreator(new DBFSourceCreator());
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
#ifdef UNIX
#else
	AddCreator(new CSVCreator());
   AddCreator(new XMLCreator());
	AddCreator(new XMLUserTableCreator());
	AddCreator(new XMLFolderCreator());
	AddCreator(new DBFShadowReader());
   //AddCreator(new DBFMaskReaderCreator());
#endif
	AddCreator(new ReporterCreater());
	AddCreator(new FolderConstructorCreator());
	AddCreator(new ServerInfo());
   AddCreator(new SQLFolderCreator());

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
		//AddCreator(new SQLFolderCreator());
	}
   if( !internalDataSource->Init(ObjectDef::GetService(), config) )
      return false;

	return ServerData::Init(dispatcher);
}

void DataSource::Cleanup()
{
	UserActivityHolder::Close();

   internalDataSource->Close();
   delete internalDataSource;
   internalDataSource = NULL;
}

/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Add on - дополнения для разных клиентов
 *
 * ert   16/06/2010   creating
 */ 
#include "stdafx.h"
#include <vector>
#include <map>
#include <set>
#include "server.h"
#include <idatasource.h>
#include <sessobj.h>
#include <folderset.h>
#include <cwriter.h>
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

const char FOLDER_KEY_PREFIX[] = "Folder";

using namespace GRServer;
static std::vector<std::string> folders;

class DBFMixCreator : public DBFTableSet
{
public:
   DBFMixCreator() {}

   virtual const wchar_t* Name() const { return L"DBFMixer"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
};

class DBFFolderCombineCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"DBFFolderCombine"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class DBFPriceCombineCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"DBFPriceCombine"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

class DBFOrderExtractorCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"DBFOrderExtractor"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class DBFMixBase : public IDataSource::IReader
{
public:
   DBFMixBase(IDataSource::ICreator *creator);
   ~DBFMixBase();

   virtual bool Prepare(const ParamList& parameters, const SessionObject& object);

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove() {}
   virtual void Close() { source->Close(); }

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual const MemberFormat* Type(const wchar_t* name) const { return source->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return source->Value(name); }

protected:
   std::wstring filter;

   ParamList parameters;
   const SessionObject* object;
   IDataSource::ICreator *creator;

   int curFolder;
   IDataSource::IReader* source;

protected:
   bool CreateSource();
   virtual void PrepareSource(IDataSource::IReader* newSource) {}
};

class DBFMixReader : public DBFMixBase
{
public:
   DBFMixReader();

   virtual void AddChild(const std::wstring& childName, IDataSource::IReader* reader);

protected:

   typedef std::vector<IDataSource::IReader*> ChildList;
   ChildList childs;

   virtual void PrepareSource(IDataSource::IReader* newSource);
};

class DBFFolderCombine : public DBFMixBase
{
public:
   DBFFolderCombine();

   virtual bool Prepare(const ParamList& parameters, const SessionObject& object);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

protected:
   mutable Object* saveObject;
   mutable bool startNewReader;
   mutable int curID;

   int fidIndex, idIndex, levelIndex, nameIndex, uidIndex;
   int startLevel, levelShift;
   int keyIndex, valueIndex;

   virtual void PrepareSource(IDataSource::IReader* newSource);
   Object* MakeRootObject();
   void UpdateObject(Object* o) const;
   Session* session;
};

class DBFFolderCombineRemover : public IDataSource::IRemover
{
public:
   DBFFolderCombineRemover(const ParamList& parameters, const SessionObject& object);
   ~DBFFolderCombineRemover();

   virtual bool Remove(const wchar_t* filter);
   virtual void Close();

protected:
   typedef std::vector<IDataSource::IRemover*> RemoverList;
   RemoverList removers;
};

class DBFFolderCombineWriter : public IDataSource::IWriter
{
public:
   DBFFolderCombineWriter(const ParamList& parameters, const SessionObject& object);
   ~DBFFolderCombineWriter();

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   int idIndex;

   typedef std::vector<IDataSource::IWriter*> WriterList;
   WriterList writers;
};

class DBFPriceCombine : public DBFMixBase
{
public:
   DBFPriceCombine();

   virtual bool Prepare(const ParamList& parameters, const SessionObject& object);
   virtual bool Get(Object* o) const;

protected:
   int fidIndex;
   int idIndex;
};

class DBFOrderExtractor : public IDataSource::IWriter
{
public:
   DBFOrderExtractor();
   ~DBFOrderExtractor();

   bool Prepare(const ParamList& parameters, const SessionObject& object);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

   virtual void AddChild(IWriter* writer, const std::wstring& typeName);

protected:
   int itemsIndex, idIndex, ordFlagIndex;

   typedef std::vector<CatalogWriter*> WriterList;
   WriterList writers;
};
//CatalogList
//---------------------------------------- DBFMixCreator ---------------------------------------------
//
DBFMixBase::DBFMixBase(IDataSource::ICreator *creator) : source(NULL), object(NULL)
{
   curFolder = -1;
   this->creator = creator;
}

DBFMixBase::~DBFMixBase()
{
   delete source;
   delete creator;
}

bool DBFMixBase::Prepare(const ParamList& parameters, const SessionObject& object)
{
   this->parameters = parameters;
   this->object = &object;

   if( curFolder >= (int) folders.size() )
   {
      source = creator->CreateReader(parameters, object);
      return (source != NULL);
   }
   return CreateSource();
}

bool DBFMixBase::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   this->filter = filter;
   if( source )
      return source->SetFilter(filter, object);
   return true;
}

bool DBFMixBase::CreateSource()
{
   if( curFolder >= (int) folders.size() - 1 )
      return false;

   if( source != NULL )
      delete source;

   bool ret = false;
   source = creator->CreateReader(parameters, *object);
   if( source )
   {
      ret = true;
      curFolder++;

      PrepareSource(source);

      source->SetBaseFolder(folders[curFolder]);
      if( !filter.empty() )
         ret = source->SetFilter(filter.c_str(), *object);
   }
   return ret;
}

bool DBFMixBase::MoveNext(Object *parentObject)
{
   if( source->MoveNext(parentObject) )
      return true;

   do
   {
      if( !CreateSource() )
         return false;
   } while( source->MoveNext(parentObject) == false );

   return true;
}

bool DBFMixBase::Get(Object* o) const
{
   return source->Get(o);
}

//
//---------------------------------------- DBFMixCreator ---------------------------------------------
//
IDataSource::IReader* DBFMixCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFMixReader* res = new DBFMixReader();
   if( res->Prepare(parameters, object) == false )
   {
      delete res;
      res = NULL;
   }
   return res;
}

DBFMixReader::DBFMixReader() : DBFMixBase(new DBFTableSet())
{
}

void DBFMixReader::AddChild(const std::wstring& childName, IDataSource::IReader* reader)
{
   if( childName.compare(DBFTableSet().Name()) == 0 )
   {
      if( curFolder >= 0 && curFolder < (int) folders.size() )
         reader->SetBaseFolder(folders[curFolder]);

      childs.push_back(reader);
      if( source )
         source->AddChild(childName, reader);
   }
}

void DBFMixReader::PrepareSource(IDataSource::IReader* newSource)
{
   std::wstring name(DBFTableSet().Name());
   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      newSource->AddChild(name, (*i));
}

//
//---------------------------------------- DBFFolderCombineCreator ---------------------------------------------
//
IDataSource::IReader* DBFFolderCombineCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFFolderCombine* res = new DBFFolderCombine();
   if( res->Prepare(parameters, object) == false )
   {
      delete res;
      res = NULL;
   }
   return res;
}

IDataSource::IRemover* DBFFolderCombineCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   return new DBFFolderCombineRemover(parameters, object);
}

IDataSource::IWriter* DBFFolderCombineCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   return new DBFFolderCombineWriter(parameters, object);
}

DBFFolderCombine::DBFFolderCombine() : DBFMixBase(new DBFFolderSet())
{
   curID = 0;

   fidIndex = -1;
   idIndex = -1;
   levelIndex = -1;
   nameIndex = -1;

   startLevel = -1;
   levelShift = 0;

   saveObject = NULL;
   startNewReader = true;
}

bool DBFFolderCombine::Prepare(const ParamList& parameters, const SessionObject& object)
{
   GRServer::Format *format = object.format;
   fidIndex = format->FindMember(L"fid");
   if( fidIndex < 0 )
      fidIndex = format->FindMember(L"id");
   else
      idIndex = format->FindMember(L"id");

   levelIndex = format->FindMember(L"level");
   nameIndex = format->FindMember(L"name");
   uidIndex = format->FindMember(L"userid");

   keyIndex = format->FindMember(L"fid");
   valueIndex = format->FindMember(L"id");
   if( keyIndex >= 0 && valueIndex >= 0 )
   {
      if( format->at(keyIndex).type != MemberFormat::mtString )
         keyIndex = -1;
      if( format->at(valueIndex).type != MemberFormat::mtNumber )
         valueIndex = -1;
   }
   session = (Session*)&object.GetSession();

   return (DBFMixBase::Prepare(parameters, object) && (fidIndex >= 0) && (levelIndex >= 0) && (nameIndex >= 0));
}

bool DBFFolderCombine::MoveNext(Object* parentObject)
{
   if( saveObject )
      return true;
   return DBFMixBase::MoveNext(parentObject);
}

void DBFFolderCombine::PrepareSource(IDataSource::IReader* newSource)
{
   startNewReader = true;
}

Object* DBFFolderCombine::MakeRootObject()
{
   delete saveObject;
   saveObject = source->Create(*object->format);
   DBFMixBase::Get(saveObject);

   Object* rootObject = source->Create(*object->format);

   Member& m = saveObject->at(levelIndex);
   if( startLevel < 0 )
      startLevel = (int)m.number;
   levelShift = startLevel - (int)m.number + 1;

   wchar_t buf[100];
   wsprintf(buf, L"Склад %d", curFolder + 1);
   rootObject->at(nameIndex).str->assign(buf);

   wsprintf(buf, L"%d\t\t", curFolder);
   rootObject->at(fidIndex).str->assign(buf);
   rootObject->at(levelIndex).number = startLevel;

   if( idIndex >= 0 )
      rootObject->at(idIndex).number = ++curID;

   if( uidIndex >= 0 )
      rootObject->at(uidIndex).str->assign(*saveObject->at(uidIndex).str);

   return rootObject;
}

void DBFFolderCombine::UpdateObject(Object* o) const
{
   if( idIndex >= 0 )
      o->at(idIndex).number = ++curID;

   o->at(levelIndex).number += levelShift;

   wchar_t buf[30];
   wsprintf(buf, L"%d\t", curFolder);
   Member &m = o->at(fidIndex);
   std::wstring str = (const std::wstring&)*m.str;
   str.insert(0, buf);
   m.str->assign(str);

   if( keyIndex >= 0 && valueIndex >= 0 )
      folderHolder.SetValue(session, *o, keyIndex, valueIndex );
}

bool DBFFolderCombine::Get(Object* o) const
{
   if( startNewReader )
   {
      startNewReader = false;

      Object *root = const_cast<DBFFolderCombine*>(this)->MakeRootObject();
      root->MoveTo(o);
      delete root;

      return true;
   }

   if( saveObject )
   {
      saveObject->MoveTo(o);
      delete saveObject;
      saveObject = NULL;
   } else
   {
      DBFMixBase::Get(o);
   }

   UpdateObject(o);
   return true;
}

DBFFolderCombineRemover::DBFFolderCombineRemover(const ParamList& parameters, const SessionObject& object)
{
   DBFFolderSet creator;

   std::vector<std::string>::const_iterator fi = folders.begin();
   for( ; fi != folders.end(); fi++ )
   {
      IDataSource::IRemover* r = creator.CreateRemover(NULL, parameters, object);
      if( r )
      {
         r->SetBaseFolder((*fi));
         removers.push_back(r);
      }
   }
}

DBFFolderCombineRemover::~DBFFolderCombineRemover()
{
   RemoverList::iterator i = removers.begin();
   for( ; i != removers.end(); i++ )
      delete (*i);
}

bool DBFFolderCombineRemover::Remove(const wchar_t* filter)
{
   bool res = true;
   RemoverList::iterator i = removers.begin();
   for( ; i != removers.end(); i++ )
      if( !(*i)->Remove(filter) )
         res = false;

   return res;
}

void DBFFolderCombineRemover::Close()
{
   RemoverList::iterator i = removers.begin();
   for( ; i != removers.end(); i++ )
      (*i)->Close();
}

DBFFolderCombineWriter::DBFFolderCombineWriter(const ParamList& parameters, const SessionObject& object)
{
   DBFFolderSet creator;

   std::vector<std::string>::const_iterator fi = folders.begin();
   for( ; fi != folders.end(); fi++ )
   {
      IDataSource::IWriter* w = creator.CreateWriter(parameters, object, (*fi));
      writers.push_back(w);
   }
}

DBFFolderCombineWriter::~DBFFolderCombineWriter()
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      delete (*i);
}

bool DBFFolderCombineWriter::Prepare(const ISessionObject& iobject)
{
   bool ret = true;
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      if( (*i) && !(*i)->Prepare(iobject) )
         ret = false;

   const SessionObject& object = *(const SessionObject*)iobject.Self();
   idIndex = object.format->FindMember(L"fid");
   if( idIndex < 0 )
      idIndex = object.format->FindMember(L"id");
      
   return ret;
}

bool DBFFolderCombineWriter::Write(const Object& o, RowID *rid)
{
   const Member& idM = o.at(idIndex);

   int wrIndex = 0;
   std::wstring idStr((const std::wstring&)*idM.str);
   int idDiv = idStr.find(L'\t');
   if( idDiv >= 0 )
   {
      wrIndex = _wtoi(idStr.substr(0, idDiv).c_str());
      idStr = idStr.substr(idDiv+1);
   }

   Object *dest = Object::Create(o.GetFormat());
   o.Copy(dest);
   dest->at(idIndex).str->assign(idStr);

   if( wrIndex < (int)writers.size() )
   {
      IDataSource::IWriter* w = writers[wrIndex];
      if( w )
         w->Write(*dest, rid);
   }

   delete dest;
   return true;
}

void DBFFolderCombineWriter::Close()
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
   {
      if( (*i) )
         (*i)->Close();
   }
}

//
//---------------------------------------- DBFPriceCombineCreator ---------------------------------------------
//
IDataSource::IReader* DBFPriceCombineCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFPriceCombine* res = new DBFPriceCombine();
   if( res->Prepare(parameters, object) == false )
   {
      delete res;
      res = NULL;
   }
   return res;
}

DBFPriceCombine::DBFPriceCombine() : DBFMixBase(new DBFPriceTable())
{
   fidIndex = -1;
   idIndex = -1;
}

bool DBFPriceCombine::Prepare(const ParamList& parameters, const SessionObject& object)
{
   fidIndex = object.format->FindMember(L"fid");
   idIndex = object.format->FindMember(L"id");
   return (DBFMixBase::Prepare(parameters, object) && (fidIndex >= 0) && (idIndex >= 0));
}

bool DBFPriceCombine::Get(Object* o) const
{
   if( !DBFMixBase::Get(o) )
      return false;

   wchar_t buf[30];
   wsprintf(buf, L"%d\t", curFolder);

   Member &m = o->at(fidIndex);
   std::wstring str = (const std::wstring&)*m.str;
   str.insert(0, buf);
   m.str->assign(str);
   //o->at(fidIndex).str->insert(0, buf);
   
   Member &m1 = o->at(idIndex);
   str = (const std::wstring&)*m1.str;
   str.insert(0, buf);
   m1.str->assign(str);
   //o->at(idIndex).str->insert(0, buf);

   return true;
}

//
//---------------------------------------- DBFPriceCombineCreator ---------------------------------------------
//
IDataSource::IWriter* DBFOrderExtractorCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFOrderExtractor* ret = new DBFOrderExtractor();
   if( ret->Prepare(parameters, object) == false )
   {
      delete ret;
      ret = NULL;
   }

   return ret;
}

DBFOrderExtractor::DBFOrderExtractor()
{
}

DBFOrderExtractor::~DBFOrderExtractor()
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      delete (*i);
}

void DBFOrderExtractor::AddChild(IWriter* writer, const std::wstring& typeName)
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      (*i)->AddChild(writer, typeName);
}

bool DBFOrderExtractor::Prepare(const ParamList& parameters, const SessionObject& object)
{
   if( folders.size() == 0 )
      return false;

   Token tableName;
   const Parameter *tname = parameters.Find(L"tableName", 0);
   if( tname == NULL || !((Session&)object.GetSession()).Parse(&tableName, tname->value, &object) || tableName.type != Token::ttString )
      return false;

   USES_CONVERSION;
   const char* tName = W2A(tableName.value.str->c_str());

   std::vector<std::string>::const_iterator fi = folders.begin();
   for( ; fi != folders.end(); fi++ )
   {
      std::string fileName((*fi) + tName);
      CatalogWriter* wr = new CatalogWriter(*object.format, object.GetObjectDef(), fileName);
      writers.push_back(wr);
   }

   return true;
}

bool DBFOrderExtractor::Prepare(const ISessionObject& iobject)
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      if( !(*i)->Prepare(iobject) )
         return false;

   const SessionObject& object = *(const SessionObject*)iobject.Self();
   itemsIndex = object.format->FindMember(L"items");
   if( itemsIndex >= 0 )
   {
      const ISessionObject *soI = object.GetChild(L"items");
      const SessionObject *so = (soI == NULL) ? NULL : (const SessionObject *)soI->Self();
      if( so )
      {
         idIndex = so->format->FindMember(L"id");
         ordFlagIndex = so->format->FindMember(L"ordflag");
      }
   }
   return (itemsIndex >= 0) && (idIndex >= 0);
}

bool DBFOrderExtractor::Write(const Object& o, RowID *rid)
{
   int count = writers.size();
   Object** src = new Object* [count];
   memset(src, NULL, sizeof(Object*) * count);

   ServObject* so = o.at(itemsIndex).object;
   if( so != NULL )
   {
      ServObject::const_iterator i = so->begin();
      for( ; i != so->end(); i++ )
      {
         int wrIndex = 0;
         const Member& idM = (*i)->at(idIndex);

         std::wstring idStr((const std::wstring&)*idM.str);
         int idDiv = idStr.find(L'\t');
         if( idDiv >= 0 )
         {
            wrIndex = _wtoi(idStr.substr(0, idDiv).c_str());
            idStr = idStr.substr(idDiv+1);
         }

         if( wrIndex < count )
         {
            bool markFirstId = false;
            ServObject* dest;
            if( src[wrIndex] == NULL )
            {
               src[wrIndex] = Object::Create(o.GetFormat());
               o.Copy(src[wrIndex]);
               dest = src[wrIndex]->at(itemsIndex).object;
               dest->clear();
               markFirstId = true;
            } else
            {
               dest = src[wrIndex]->at(itemsIndex).object;
            }

            Object* itemObj = Object::Create((*i)->GetFormat());
            (*i)->Copy(itemObj);
            itemObj->at(idIndex).str->assign(idStr);
            if( ordFlagIndex >= 0 )
               itemObj->at(ordFlagIndex).number = (markFirstId) ? 1 : 0;

            dest->push_back(itemObj);
         }
      }
   }

   for( int obji = 0; obji < count; obji++ )
   {
      Object* wrObj = src[obji];
      if( wrObj )
      {
         writers[obji]->Write(*wrObj, rid);
         delete wrObj;
      }
   }

   delete[] src;
   return true;
}

void DBFOrderExtractor::Close()
{
   WriterList::iterator i = writers.begin();
   for( ; i != writers.end(); i++ )
      (*i)->Close();
}

static bool LoadFolders()
{
   const IServerConfig& c = gServer->GetConfig();
   char buf[30];
   std::set<std::string> loaded;

   std::string baseFolder(c.ExchangeFolder());
   if( *baseFolder.rbegin() != '\\' )
      baseFolder.append("\\");

   for( int i=1; ; i++ )
   {
      wsprintfA(buf, "%s%d", FOLDER_KEY_PREFIX, i);
      const char *cf = c.Option(buf);
      if( *cf == '\0' )
         break;

      std::string f(baseFolder);
      f.append(cf);
      if( *f.rbegin() != '\\' )
         f.append("\\");

      if( loaded.find(f) == loaded.end() )
      {
         folders.push_back(f);
         loaded.insert(f);
      }
   }

   return true;
}

bool GRServer::AddOnInit()
{
   if( !LoadFolders() )
      return false;

   IDataSourceRegister *rs = (IDataSourceRegister*)gServer->GetService(SOURCE_SERVICE);
   if( rs )
   {
      rs->AddSource(new DBFMixCreator());
      rs->AddSource(new DBFFolderCombineCreator());
      rs->AddSource(new DBFPriceCombineCreator());
      rs->AddSource(new DBFOrderExtractorCreator());
   }
   return true;
}


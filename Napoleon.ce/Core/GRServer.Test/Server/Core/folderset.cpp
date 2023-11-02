/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Папки товара
 *
 * ert   24/05/2010   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "parse.h"
#include "folderset.h"
#include "server.h"
#include "objects.h"
#include "objdef.h"

#include <folderholder.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>
#include <algorithm>

using namespace GRServer;

struct FolderSetParam : TableSetParam
{
   std::wstring parentField;
   std::wstring childField;
   std::wstring rowidField;
   bool doSort;

   FolderSetParam() : doSort(false) {}

   virtual bool Read(const SessionObject& object, const ParamList& parameters)
   {
      bool ret = TableSetParam::Read(object, parameters);
      if( ret )
      {
         std::wstring val;
         const Session& session = (const Session&)object.GetSession();
         const Parameter* pf = parameters.Find(L"parentField", 2);
         if( pf != NULL && session.Parse(&val, pf->value, &object) )
            parentField = val.c_str();

         pf = parameters.Find(L"childField", -1);
         if( pf != NULL && session.Parse(&val, pf->value, &object) )
            childField = val.c_str();

         pf = parameters.Find(L"rowidField", -1);
         if( pf != NULL && session.Parse(&val, pf->value, &object) )
            rowidField = val.c_str();

         Token tval;
         pf = parameters.Find(L"sort", -1);
         if( pf != NULL && session.Parse(&tval, pf->value, &object) && tval.type == Token::ttNumber )
            doSort = (tval.value.number > 0);
      }

      return ret;
   }
};

class DBFFolderTreeReader : public DBFFolderSetReader
{
public:
   DBFFolderTreeReader(const GRServer::Format& fmt, const SessionObject& object, const FolderSetParam& param);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

protected:
   std::string parentField;
   TreeReader treeReader;
};


class FolderWriter : public DBFWriter
{
public:
   FolderWriter(const FolderSetParam& params, const GRServer::Format& format, const IObjectData* objDef, const std::string& fileName);

   virtual void AddFields(std::vector<DBRec>* dbFields);
   virtual bool Prepare(const ISessionObject& object);

protected:
   virtual void BeforeWrite(const Object& o);

protected:
   std::wstring parentField;
   std::wstring childField;

   std::vector<std::string> ids;
   int curLevel;
   int idIndex, levelIndex;
   int width, offset;
};

class FolderTableRemover : public TableRemover
{
public:
   FolderTableRemover(const TableSetParam& param, const SessionObject& _object) :
      TableRemover(param, _object)
      {
         commonTable = param.commonTable;
      }

protected:
   virtual bool CanRemoveTable(const char* table)
#if defined(Parfum) || defined(Deka)
   { return true; }
#else
   { return strcmp(commonTable.c_str(), table) != 0; }
#endif

   std::string commonTable;
};

class FolderConstructor : public IDataSource::IReader
{
public:
	FolderConstructor(const SessionObject& object, const std::wstring &sourceName, const std::wstring& hiddenSource);
	~FolderConstructor();

	virtual bool MoveNext(Object *parentObject);
	virtual bool Get(Object* o) const;
	virtual void Remove();
	virtual void Close();
	virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { this->filter = filter; return true; }

	virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
	virtual const Member* Value(const wchar_t* name) const { return NULL; }

private:
	void Build();

protected:
	const SessionObject& object;
	std::wstring sourceName;
	std::wstring hiddenSource;
	bool inited;
	std::wstring filter;

	const ServObject* source;
	Session* session;
	std::set<std::wstring> hidden;
	int hiddenLevel, curPos;
	int idIdx, nameIdx, levelIdx, fidIdx;
	int srcIdIdx, srcNameIdx, srcLevelIdx, srcFidIdx;
};

GRServer::FolderIDHolder GRServer::folderHolder;

//
//------------------------------- FolderIDHolder ------------------------------------
//
bool FolderIDHolder::Get(ISession *s, DWORD *val, const std::wstring& key) const
{
   const_iterator f = find(s);
   bool retVal = false;

   if( f != end() )
   {
      std::map<std::wstring, DWORD>::const_iterator fnd = f->second.find(key);
      if( fnd != f->second.end() )
      {
         *val = fnd->second;
         retVal = true;
      }
   }
   return retVal;
}

void FolderIDHolder::SetValue(ISession* s, const Object& o, int keyIndex, int valueIndex)
{
   if( keyIndex >= 0 && valueIndex >= 0 )
   {
		if (mutex.Acquire(1000))
		{
			iterator f = find(s);
			if (f == end())
				s->AddHandler(this);

			const Member& km = o.at(keyIndex);
			const Member& vm = o.at(valueIndex);

//#ifdef Zakroma
//			USES_CONVERSION;
//			gServer->AddLog(IErrorLogger::None, "FolderSet put fid '%s', row=%d",W2A(km.str->c_str()), (DWORD)vm.number);
//#endif
			(*this)[s][(const std::wstring&)*km.str] = (DWORD)vm.number;
			mutex.Release();
		}
   }
}

void FolderIDHolder::SessionClosed(ISession* s)
{
	if (mutex.Acquire(1000))
	{
		iterator f = find(s);
		if (f != end())
			erase(f);
		mutex.Release();
	}
}

void FolderIDHolder::Clear(ISession* s)
{
	if (mutex.Acquire(1000))
	{
		iterator f = find(s);
		if (f != end())
			f->second.clear();
		mutex.Release();
	}
}


//
//------------------------------- DBFFolderSetReader ------------------------------------
//
DBFFolderSetReader::DBFFolderSetReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param) :
      DBFTableSetReader(fmt, object, param)
{
   GRServer::Format* f = object.format;
   keyIndex = f->FindMember(L"fid");
   valueIndex = f->FindMember(L"id");

//#ifdef Zakroma
//	USES_CONVERSION;
//	gServer->AddLog(IErrorLogger::None, "FolderSet user '%s' KeyIndex=%d, ValueIndex=%d", W2A(((const Session&)object.GetSession()).GetUser().ID()), keyIndex, valueIndex);
//#endif
}

void DBFFolderSetReader::Close()
{
   DBFTableSetReader::Close();
}

bool DBFFolderSetReader::Get(Object* o) const
{
	//gServer->AddLog(IErrorLogger::None, "FolderSet try read");
	if (!DBFTableSetReader::Get(o))
      return false;

	//gServer->AddLog(IErrorLogger::None, "FolderSet try readed");
	folderHolder.SetValue((Session*)&object.GetSession(), *o, keyIndex, valueIndex);
   return true;
}

//void DBFFolderSetReader::UpdateFolderHolder(Object* o) const
//{
//   if( keyIndex >= 0 && valueIndex >= 0 )
//   {
//      const Member& km = o->at(keyIndex);
//      const Member& vm = o->at(valueIndex);
//      folderHolder.SetValue((Session*)&object.GetSession(), *km.str, (DWORD)vm.number );
//   }
//}
//
//------------------------------- DBFFolderSet ------------------------------------
//
IDataSource::IReader* DBFFolderSet::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IReader* reader = NULL;
   FolderSetParam param;
   if( param.Read(object, parameters) )
   {
      if( param.parentField.empty() )
         reader = new DBFFolderSetReader(*object.format, object, param);
      else
         reader = new DBFFolderTreeReader(*object.format, object, param);
   }
   return reader;
}

IDataSource::IWriter* DBFFolderSet::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   return CreateWriter(parameters, iobject, iobject.GetSession().Config().ExchangeFolder());
}

IDataSource::IWriter* DBFFolderSet::CreateWriter(const ParamList& parameters, const ISessionObject& iobject, const std::string& baseFolder) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IWriter* writer = NULL;
   FolderSetParam param;
   if( param.Read(object, parameters) )
   {
      if( param.parentField.empty() )
         writer = DBFTableSet::CreateWriter(param, object, baseFolder);
      else
      {
         USES_CONVERSION;

         std::string fileName(baseFolder);

         const User& u = ((Session&)object.GetSession()).GetUser();
         if( u.ImpersonateAsNull() )
         {
            fileName += param.commonTable;
         } else
         {
            fileName += param.userTable;
            fileName += W2A(u.ID());
         }

         writer = new FolderWriter(param, *object.format, object.GetObjectDef(), fileName);
      }
   }

   return writer;
}

IDataSource::IRemover* DBFFolderSet::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IRemover* remover = NULL;
   TableSetParam param;
   if( param.Read(object, parameters) )
   {
      remover = new FolderTableRemover(param, object);
   }
   return remover;
}

//
//------------------------------- TreeReader ------------------------------------
//
bool TreeReader::MoveNext()
{
   if( levelIndex < 0 || data.size() == 0 )
      return false;

   bool ret = false;
   std::wstring key;
   if( path.size() == 0 )
   {
      TreeData::iterator fnd = data.find(key);
      if( fnd != data.end() )
      {
         path.insert(path.begin(), fnd);
         if( doSort )
            std::sort(fnd->second.begin(), fnd->second.end());
         ret = true;
      }
   } else
   {
      PathList::iterator i = path.begin();
      if( (*i)->second.size() )
      {
//         USES_CONVERSION;

         ObjectData* src = &(*((*i)->second).begin());

         TreeData::iterator fnd = data.find(src->id);
         if( fnd != data.end() )
         {
            path.insert(path.begin(), fnd);
            if( doSort )
               std::sort(fnd->second.begin(), fnd->second.end());

            ret = true;
         } else
         {
            delete src->object;
            ((*i)->second).pop_front();

            while( ((*i)->second).size() == 0 )
            {
               path.erase(path.begin());
               if( path.size() == 0 )
                  break;

               i = path.begin();

               src = &(*((*i)->second).begin());
               delete src->object;
               ((*i)->second).pop_front();
            }

            if( path.size() == 0 )
               ret = false;
            else
               ret = (((*i)->second).size() != 0);
         }
      }
   }

   curRec++;
   return ret;
}

bool TreeReader::Get(Object* o) const
{
   TreeData::iterator i = *path.begin();
   ObjectList &ol = i->second;

   if( ol.size() > 0 )
   {
      ObjectData& src = ol.front();
      src.object->MoveTo(o);

      o->at(levelIndex).number = (double)path.size();
      if( rowidIndex >= 0 )
         o->at(rowidIndex).number = curRec;
   }

   return true;
}

void TreeReader::Clear()
{
   path.clear();
   data.clear();
}

//
//------------------------------- DBFFolderTreeReader ------------------------------------
//
DBFFolderTreeReader::DBFFolderTreeReader(const GRServer::Format& fmt, const SessionObject& object, const FolderSetParam& param) :
   DBFFolderSetReader(fmt, object, param)
{
   USES_CONVERSION;
   this->parentField = W2A_CP(param.parentField.c_str(), DBF_CODE_PAGE);

   treeReader.SetData(fmt.FindMember(L"level"), fmt.FindMember(param.childField.c_str()),
      fmt.FindMember(param.rowidField.c_str()), fmt.FindMember(L"name"), param.doSort);
}

bool DBFFolderTreeReader::MoveNext(Object *parentObject)
{
   if( treeReader.MoveNext() ) return true;

   while( true )
   {
      if( !OpenNextBase() )
         return false;

      int pi = base.Field(parentField.c_str());
      if( pi < 0 )
         continue;

      DBField* pf = base.GetFieldBase() + pi;
      int parentOffset = pf->offset;
      int parentWidth = pf->width;

      treeReader.Clear();

		USES_CONVERSION;
      while( DBFReader::MoveNext(parentObject) )
      {
         Object* o = Object::Create(*object.format);
         DBFReader::Get(o);

         std::string parentID;

         const char* p = base.GetRec() + parentOffset;
         const char* ep = p + parentWidth;
         while( *p == ' ' && p < ep ) p++;
         if( p < ep && *p )
         {
            while( ep > p && ep[-1] == ' ' ) ep--;
            if( p < ep )
               parentID.assign(p, ep - p);
         }

			treeReader.Add(A2W_CP(parentID.c_str(), DBF_CODE_PAGE), o);
      }
      break;
   }

   return treeReader.MoveNext();
}

bool DBFFolderTreeReader::Get(Object* o) const
{
   treeReader.Get(o);
   folderHolder.SetValue((Session*)&object.GetSession(), *o, keyIndex, valueIndex );
   //UpdateFolderHolder(o);

   return true;
}

//
//------------------------------- FolderWriter ------------------------------------
//
FolderWriter::FolderWriter(const FolderSetParam& params, const GRServer::Format& format, const IObjectData* objDef, const std::string& fileName) :
   DBFWriter(format, objDef, fileName)
{
   parentField = params.parentField;
   childField = params.childField;
}

bool FolderWriter::Prepare(const ISessionObject& object)
{
   if( !DBFWriter::Prepare(object) )
      return false;

   const GRServer::Format& format = *object.Self()->format;
   idIndex = format.FindMember(childField.c_str());
   levelIndex = format.FindMember(L"level");

   char pname[20];
   MakeDBFName(pname, parentField);
   DBField *pf = base.GetFieldRef(pname);

   if( pf == NULL )
      return false;

   width = pf->width;
   offset = pf->offset;
   curLevel = -1;

   return true;
}

void FolderWriter::BeforeWrite(const Object& o)
{
   USES_CONVERSION;

   int level = (int)o.at(levelIndex).number;
   std::string id(W2A_CP(o.at(idIndex).str->c_str(), DBF_CODE_PAGE));

   std::string parentID;
   while( level <= curLevel && ids.size() )
   {
      ids.pop_back();
      curLevel--;
   }
   if( ids.size() )
      parentID = ids.back();

   char *p = (char*)base.GetRec() + offset;
   memset(p, ' ', width);
   memcpy(p, parentID.c_str(), min(parentID.size(), (unsigned)width));
   ids.push_back(id);
   curLevel = level;
}

void FolderWriter::AddFields(std::vector<DBRec>* dbFields)
{
   char pname[20], cname[20];

   MakeDBFName(pname, parentField);
   MakeDBFName(cname, childField);
   std::vector<DBRec>::const_iterator i = dbFields->begin();

   for( ; i != dbFields->end(); i++ )
   {
      if( strncmp((const char*)i->name, pname, sizeof(((DBRec*)NULL)->name)) == 0 )
         return;
      if( strncmp((const char*)i->name, cname, sizeof(((DBRec*)NULL)->name)) == 0 )
         width = i->width;
   }

   if( width > 0 )
   {
      DBRec dbField;
      strncpy((char*)dbField.name, pname, sizeof(((DBRec*)NULL)->name));
      dbField.prec = 0;
      dbField.type = 'C';
      dbField.width = width;

      dbFields->push_back(dbField);
   }
}

//
//------------------------------- FolderID ------------------------------------
//
const wchar_t* FolderID::Name() const
{
   return L"FolderID";
}

bool FolderID::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
{
   bool ret = false;

   if( params.size() == 1 )
   {
      std::wstring val;
      const Token& p = params.front();
      if( p.type == Token::ttNumber )
      {
         wchar_t buf[50];
         _swprintf(buf, L"%f", p.value.number + 0.000005);
         val = buf;
      } else if( p.type == Token::ttString )
         val = *p.value.str;

      if( val.empty() == false )
      {
         DWORD dval;

         ret = true;
         if( !folderHolder.ContainsData(session) )
            session->LoadObject(L"Folder", NULL);

         if( folderHolder.Get(session, &dval, val) )
            (*result) = (double)dval;
         else
            (*result) = (double)0;
      }
   }
   return ret;
}

//
// ------------------------------ FolderConstructor --------------------------------
//
FolderConstructor::FolderConstructor(const SessionObject& _object, const std::wstring &_sourceName, const std::wstring& _hiddenSource) :
	inited(false), object(_object), sourceName(_sourceName), hiddenSource(_hiddenSource)
{
	hiddenLevel = -1;
	curPos = -1;
}

void FolderConstructor::Build()
{
	const GRServer::Format *format = object.format;
	idIdx = format->FindMember(L"id");
	nameIdx = format->FindMember(L"name");
	levelIdx = format->FindMember(L"level");
	fidIdx = format->FindMember(L"fid");

	// case ManageerFolder
	if (format->at(idIdx).type == MemberFormat::mtString)
		fidIdx = -1;

	session = &(Session&)object.GetSession();
	ISessionObject* so = session->LoadObject(sourceName, NULL, filter.c_str());
	if (so == NULL)
		source = NULL;
	else
	{
		source = so->Self();
		const GRServer::Format* srcFmt = source->format;

		srcIdIdx = srcFmt->FindMember(L"id");
		srcNameIdx = srcFmt->FindMember(L"name");
		srcLevelIdx = srcFmt->FindMember(L"level");
		srcFidIdx = srcFmt->FindMember(L"fid");
	}

	so = session->LoadObject(hiddenSource, NULL, filter.c_str());
	if (so != NULL)
	{
		ServObject* ho = so->Self();
		int id = ho->format->FindMember(L"id");
		if (id >= 0)
		{
			ServObject::const_iterator i = ho->begin();
			for (; i != ho->end(); i++)
			{
				const Object* ob = (*i);
				hidden.insert((const std::wstring&)(*ob->at(id).str));
			}
		}
	}

	if (idIdx >= 0 && fidIdx >= 0)
	{
		folderHolder.Clear(session);
	}
	inited = true;
}

FolderConstructor::~FolderConstructor()
{

}

bool FolderConstructor::MoveNext(Object *parentObject)
{
	if (!inited)
		Build();

	if (source == NULL || srcIdIdx < 0 || idIdx < 0 || nameIdx < 0 || srcNameIdx < 0 || levelIdx < 0 || srcLevelIdx < 0 || srcFidIdx < 0)
		return false;

	hiddenLevel = -1;
	while (++curPos < (int)source->size())
	{
		const Object* o = source->at(curPos);
		int srcLevel = (int)(o->at(srcLevelIdx).number + 0.5);
		if (hiddenLevel >= 0 && srcLevel > hiddenLevel)
			continue;

		if (hidden.find((const std::wstring&)(*o->at(srcFidIdx).str)) == hidden.end())
			break;
		hiddenLevel = srcLevel;
	}
	return curPos < (int)source->size();
}

bool FolderConstructor::Get(Object* o) const
{
	const Object* src = source->at(curPos);
	o->at(levelIdx).number = src->at(srcLevelIdx).number;
	o->at(nameIdx).str->assign(*src->at(srcNameIdx).str);
	if (fidIdx < 0)
	{
		o->at(idIdx).str->assign(*src->at(srcFidIdx).str);
	}
	else
	{
		o->at(idIdx).number = src->at(srcIdIdx).number;
		o->at(fidIdx).str->assign(*src->at(srcFidIdx).str);

		folderHolder.SetValue(session, *o, fidIdx, idIdx);
	}

	return true;
}

void FolderConstructor::Remove()
{
}

void FolderConstructor::Close()
{

}


IDataSource::IReader* FolderConstructorCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
	std::wstring folderSrc, hiddenSrc;
	const Session& session = (const Session&)object.GetSession();
	const Parameter* pf = parameters.Find(L"commonFolders", -1);
	if (pf == NULL || !session.Parse(&folderSrc, pf->value, &object))
	{
		gServer->AddLog("FolderConstructor no (or error in) commonFolders");
		return NULL;
	}

	pf = parameters.Find(L"hiddenFolders", -1);
	if (pf == NULL || !session.Parse(&hiddenSrc, pf->value, &object))
	{
		gServer->AddLog("FolderConstructor no (or error in) hiddenFolders");
		return NULL;
	}

	return new FolderConstructor((const SessionObject&)(*object.Self()), folderSrc, hiddenSrc);
}

FolderID GRServer::folderID;


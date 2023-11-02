/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * DBFTableSet
 *
 * ert   29/04/2010   creating
 */
#include "stdafx.h"
#include "dbftblset.h"
#include "objdef.h"
#include "server.h"
#include "objects.h"
#include "srvutility.h"
#include "cwriter.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class Catalog
{
public:
   Catalog();

   void Set(const SessionObject& object, const std::wstring& catalog);
   bool Open(const DataForm& base);
   const std::string& Read(const DataForm& base) const;

protected:
   struct BaseData
   {
      WORD offset, width;
      std::string name;
   };

   typedef std::vector<BaseData> DataList;
   DataList data;
   mutable std::string buffer;
};

class ChildObject
{
public:
   ChildObject();

    // return childIndex
   int Set(const SessionObject& object, const std::wstring& items, const GRServer::Format** format);
   bool Open(const DataForm& base);
   Object* Read(const DataForm& base) const;

protected:
   ObjectReader reader;
   const SessionObject* object;
};

class DBFOrderedCatalogTableReader : public DBFReader
{
public:
   DBFOrderedCatalogTableReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param);
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual void SetBaseFolder(const std::string& baseFolder);
	virtual void Close();
	virtual bool OpenNextBase();

protected:
   //const SessionObject& object;
   DBFUserSet users;
   std::wstring userID;
   int userIDIndex;
	SessionObject* child;

   Catalog catalog;
   ChildObject childObject;
   int objIndex;
   const GRServer::Format* childFormat;

   std::string baseFolder;
	bool needRestoreUser;
	bool removeAfterCommit;

protected:
   bool Open(const std::string& tableName);

   ServObject* CreateChild(Object* o) const;
};

class DBFCatalogTableReader : public DBFOrderedCatalogTableReader
{
public:
   DBFCatalogTableReader(const GRServer::Format& fmt, const SessionObject& object, const TableSetParam& param);
   ~DBFCatalogTableReader();

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;
   virtual void Close();
	virtual bool OpenNextBase();

protected:
   mutable std::map<std::string, Object*> items;
   mutable std::map<std::string, Object*>::iterator curItem;

protected:
   void ClearItems();
};

class TableSetCatalogWriter : public CatalogWriter
{
public:
   TableSetCatalogWriter(const TableSetParam& param, const GRServer::Format& fmt, const IObjectData* _objDef, const std::string& _fileName);
   ~TableSetCatalogWriter() { delete childWriter; }

   virtual bool Prepare(const ISessionObject& object);

protected:
   std::wstring childObject;
};

//
//------------------------------- CatalogParam ------------------------------------
//
TableSetParam::~TableSetParam()
{
   delete filter.filter;
   delete filter.holder;
}

bool TableSetParam::Read(const SessionObject& object, const ParamList& parameters)
{
   std::wstring val, val1;
   const Parameter* prm = parameters.Find(L"filter", 2);
   const Session& session = (const Session&)object.GetSession();

   if( prm != NULL && session.Parse(&val, prm->value, &object) )
      FilterReader::Parse(&filter, val, object);
      //filter.Parse(val);

   prm = parameters.Find(L"catalog", 3);
   if( prm != NULL && session.Parse(&val, prm->value, &object) )
      ReadCatalog(val);

   prm = parameters.Find(L"userFieldName", -1);
   if( prm != NULL )
      session.Parse(&userField, prm->value, &object);

   const Parameter* put = parameters.Find(L"userTable", 0);
   const Parameter* pct = parameters.Find(L"commonTable", 1);

	removeAfterReading = (parameters.Find(L"removeAfterReading", -1) != NULL);

   if( put != NULL && pct != NULL &&
      session.Parse(&val, put->value, &object) &&
      session.Parse(&val1, pct->value, &object) )
   {
      USES_CONVERSION;

      userTable = W2A_CP(val.c_str(), CP_UTF8);
      commonTable = W2A_CP(val1.c_str(), CP_UTF8);

      return true;
   }
   return false;
}

bool TableSetParam::ReadCatalog(const std::wstring &str)
{
   size_t pos = 0;
   size_t ep = str.find(L';', pos);
   if( ep == std::wstring::npos )
      return false;
   catalog.assign(str.substr(pos, ep));

   pos = ep + 1;
   ep = str.find(L';', pos);
   if( ep == std::wstring::npos )
   {
      catalog.clear();
      return false;
   }
   catalogObject.assign(str.substr(pos, ep - pos));

   ordered = (_wtoi(str.substr(ep+1).c_str()) == 1) ? true : false;
   return true;
}

//
//------------------------------- TableRemover ------------------------------------
//
TableRemover::TableRemover(const TableSetParam& param, const SessionObject& _object) :
   object(_object), users(param, _object)
{
   baseFolder = _object.GetSession().Config().ExchangeFolder();
}

TableRemover::~TableRemover()
{
}

void TableRemover::SetBaseFolder(const std::string& baseFolder)
{
   this->baseFolder = baseFolder;
   users.BaseFolderChanged(baseFolder, object);
}

bool TableRemover::Remove(const wchar_t* filter)
{
   bool ret = true;
   RemoverList::iterator ci = childs.begin();
   for( ; ret && ci != childs.end(); ci++ )
      ret = (*ci)->Remove(filter);

   if( ret && users.SetFilter(filter, object) )
   {
      while( ret )
      {
         std::wstring uid;
         std::string fileName(baseFolder);

         const char* table = users.Next(&uid, false);
         if( *table == '\0' )
            break;

         if( !CanRemoveTable(table) )
            continue;

         fileName += table;
         fileName += ".DBF";

         if( IsFileExists(fileName) && DeleteFileA(fileName.c_str()) == 0 )
         {
            gServer->AddError(false, "Не могу удалить файл '%s' ошибка %d", fileName.c_str()), GetLastError();
            ret = false;
         }
      }
   }

   return ret;
}

void TableRemover::Close()
{
}

//
//------------------------------- DBFSetReader ------------------------------------
//
IDataSource::IReader* DBFShadowReader::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
	DBFTableSet *cr = new DBFTableSet();
	IDataSource::IReader*reader = cr->CreateReader(parameters, iobject);
	SessionObject* so = (SessionObject*)iobject.Self();
	if (so->CreateWriter(NULL, SourceType::stInternal))
	{
		so->GetSource()->reader = reader;

		so->Load(NULL, false);
		so->Write(true);
		so->CloseWriter();
		so->clear();
	}

	delete cr;
	reader->Close();
	delete reader;

	return NULL;

	//const SessionObject& object = *(const SessionObject*)iobject.Self();
	//IDataSource::IReader* reader = NULL;
	//TableSetParam param;
	//if (param.Read(object, parameters))
	//{
	//	if (object.Parent() != NULL)
	//	{
	//		if (param.filter.holder != NULL)
	//			reader = new DBFChildTableSetReader(*object.format, object, param);
	//	}
	//	else
	//	{
	//		if (!param.catalog.empty() && !param.catalogObject.empty())
	//		{
	//			DBFOrderedCatalogTableReader *r;
	//			if (param.ordered)
	//				r = new DBFOrderedCatalogTableReader(*object.format, object, param);
	//			else
	//				r = new DBFCatalogTableReader(*object.format, object, param);
	//			if (!r->OpenNextBase())
	//			{
	//				delete r;
	//				r = NULL;
	//			}
	//			reader = r;
	//		}
	//		else
	//		{
	//			DBFTableSetReader *r = new DBFTableSetReader(*object.format, object, param);
	//			if (!r->OpenNextBase())
	//			{
	//				delete r;
	//				r = NULL;
	//			}
	//			reader = r;
	//		}
	//	}
	//}
	//return reader;
}
//
//------------------------------- DBFTableSet ------------------------------------
//
IDataSource::IReader* DBFTableSet::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IReader* reader = NULL;
   TableSetParam param;
   if( param.Read(object, parameters) )
   {
      if( object.Parent() != NULL )
      {
         if( param.filter.holder != NULL )
            reader = new DBFChildTableSetReader(*object.format, object, param);
      } else
      {
         if( !param.catalog.empty() && !param.catalogObject.empty() )
         {
            if( param.ordered )
               reader = new DBFOrderedCatalogTableReader(*object.format, object, param);
            else
               reader = new DBFCatalogTableReader(*object.format, object, param);
         }
         else
            reader = new DBFTableSetReader(*object.format, object, param);
      }
   }
   return reader;
}

IDataSource::IWriter* DBFTableSet::CreateWriter(const TableSetParam& param, const ISessionObject& iobject, const std::string& baseFolder) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IWriter* writer = NULL;
   USES_CONVERSION;

   std::string fileName(baseFolder);

   const User& u = ((Session&)object.GetSession()).GetUser();
   if( u.ImpersonateAsNull() )
   {
      fileName += param.commonTable;
   } else
   {
      fileName += param.userTable;
      fileName += W2A_CP(u.ID(), CP_UTF8);
   }

   if( !param.catalog.empty() && !param.catalogObject.empty() )
   {
      writer = new TableSetCatalogWriter(param, *object.format, object.GetObjectDef(), fileName);
   } else
   {
      writer = new DBFWriter(*object.format, object.GetObjectDef(), fileName);
   }

   return writer;
}

IDataSource::IWriter* DBFTableSet::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return CreateWriter(parameters, object, object.GetSession().Config().ExchangeFolder());
}

IDataSource::IWriter* DBFTableSet::CreateWriter(const ParamList& parameters, const ISessionObject& iobject, const std::string& baseFolder) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IWriter* writer = NULL;
   TableSetParam param;
   if( param.Read(object, parameters) )
      writer = CreateWriter(param, iobject, baseFolder);

   return writer;
}

IDataSource::IRemover* DBFTableSet::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   IDataSource::IRemover* remover = NULL;
   TableSetParam param;
   if( param.Read(object, parameters) )
   {
      remover = new TableRemover(param, object);
   }
   return remover;
}

//
//------------------------------- DBFUserSet ------------------------------------
class IFieldReader
{
public:
   IFieldReader(int _index) : index(_index) {}
   virtual ~IFieldReader() {}
   virtual void Read(std::string* dest, const Object& src) const = 0;
protected:
   int index;
};

class StrReader : public IFieldReader
{
public:
   StrReader(int index) : IFieldReader(index) {}
   virtual void Read(std::string* dest, const Object& src) const
   {
      USES_CONVERSION;
      dest->assign(W2A_CP(src.at(index).str->c_str(), CP_UTF8));
   }
};

class IntReader : public IFieldReader
{
public:
   IntReader(int index) : IFieldReader(index) {}
   virtual void Read(std::string* dest, const Object& src) const
   {
      char buf[50];
      wsprintfA(buf, "%d", (int)(src.at(index).number + 0.000005) );
      dest->assign(buf);
   }
};

static void Trunc(std::string *str)
{
   std::string::iterator i = str->begin();
   while( i != str->end() && (*i) == ' ' )
      i++;

   if( i != str->end() )
   {
      std::string::iterator ei = str->end();
      do
         ei--;
      while( ei > i && (*ei) == ' ' );

      if( ei > i )
         str->assign(i, ++ei);
      else
         str->clear();
   } else
      str->clear();
}

DBFUserSet::DBFUserSet(const TableSetParam& param, const ISessionObject& object) : userIndex(0)
{
   this->userTable = param.userTable;
   this->commonTable = param.commonTable;
	this->isNullUser = false;

   if( param.userField.compare(L"id") != 0 )
      userFieldMap.Load(param.userField, &(Session&)object.GetSession());

   BaseFolderChanged(object.GetSession().Config().ExchangeFolder(), object);
}

void UserFieldMap::Load(const std::wstring& field, Session* s)
{
   SessionObject agents(ObjectDef::Get(L"Agents"), s);
   if( agents.CreateReader() )
   {
      agents.Load(NULL);
      agents.CloseReader();

      int idi = agents.format->FindMember(L"id");
      int idx = agents.format->FindMember(field.c_str());
      if( idi >= 0 && idx >= 0 )
      {
         MemberFormat& mf = agents.format->at(idx);
         IFieldReader *reader = (mf.type == MemberFormat::mtString) ? (IFieldReader*)new StrReader(idx) :
            (mf.type == MemberFormat::mtNumber) ? (IFieldReader*)new IntReader(idx) :
            NULL;

         if( reader != NULL )
         {
            USES_CONVERSION;
            SessionObject::const_iterator i = agents.begin();
            for( ; i != agents.end(); i++ )
            {
               std::string id = W2A_CP((*i)->at(idi).str->c_str(), CP_UTF8);
               std::string val;
               reader->Read(&val, *(*i));
               Trunc(&val);
               (*this)[id] = val;
            }
            delete reader;
         }
      }
   }
}

const char* UserFieldMap::Get(const std::string& userid) const
{
   std::map<std::string, std::string>::const_iterator fnd = find(userid);
   return (fnd != end() ) ? fnd->second.c_str() : "";
}

void DBFUserSet::BaseFolderChanged(const std::string& baseFolder,  const ISessionObject& object)
{
   users.clear();

   USES_CONVERSION;
   const char* userA = W2A_CP(((Session&)object.GetSession()).GetUser().ID(), CP_UTF8);
   std::string userId(userA);
   if( userFieldMap.size() > 0 )
      userId = userFieldMap.Get(userA);

   if( IsFileExists(baseFolder + userTable + userId + ".DBF") )
   {
      users.push_back(userA);
   }
}

bool DBFUserSet::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   bool ret = true;

   users.clear();
   ret = ParseUserFilter(&users, filter);
   if( !ret ) users.clear();
   else
   {
      USES_CONVERSION;

      const User &u = ((Session&)object.GetSession()).GetUser();
      StrSet aui = u.AllowedUID();
      std::vector<std::string>::iterator i = users.begin();
      for( ; i != users.end(); )
      {
			const char *cpUser = (*i).c_str();
			if (strcmp(cpUser, NULL_USER) == 0) {
				isNullUser = true;
				users.erase(i);
				break;
			}
         if( aui.find(A2W(cpUser)) == aui.end() && (wcscmp(u.ID(), COM_ID) != 0) )
            i = users.erase(i);
         else
            i++;
      }
   }

   return ret;
}

const char* DBFUserSet::Next(std::wstring* userid, bool canUseCommonTable) const
{
   const char *uid = "";
   userid->clear();

   if( users.size() == 0 )
   {
      if( userIndex == 0  && (canUseCommonTable || isNullUser) )
      {
         uid = commonTable.c_str();
         userIndex++;
      }
   } else
   {
      if( userIndex < (int)users.size() )
      {
         USES_CONVERSION;
         const std::string& uidx = users[userIndex];

         userid->assign(A2W(uidx.c_str()));
         if( userFieldMap.size() > 0 )
            buf = userTable + userFieldMap.Get(uidx);
         else
            buf = userTable + uidx;

         uid = buf.c_str();
         userIndex++;
      }
   }
   return uid;
}

//
//------------------------------- DBFTableSetReader ------------------------------------
//
DBFTableSetReader::DBFTableSetReader(const GRServer::Format &fmt, const SessionObject& obj, const TableSetParam& param) :
   DBFReader(fmt), users(param, obj), object(obj)
{
   filter = const_cast<FilterReader::Data&>(param.filter).GetFilter();
   userIDIndex = obj.format->FindMember(L"userid");
   needSetFilter = false;

   baseFolder = obj.GetSession().Config().ExchangeFolder();
	removeAfterReading = param.removeAfterReading;
}

void DBFTableSetReader::AddChild(const std::wstring& childName, IDataSource::IReader* reader)
{
   if( childName.compare(DBFTableSet().Name()) == 0 )
      childs.push_back((DBFChildTableSetReader*)reader);
}

void DBFTableSetReader::SetBaseFolder(const std::string& baseFolder)
{
   this->baseFolder = baseFolder;
   users.BaseFolderChanged(baseFolder, object);

   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i)->SetBaseFolder(baseFolder);
}

bool DBFTableSetReader::SetFilter(const wchar_t *filter, const GRServer::ISessionObject& object)
{
   wchar_t *buf = _wcsdup(filter);
   CharUpper(buf);
   bool isQtyFilter = (wcsstr(buf, L"SETQTYFILTER") != NULL);
   free(buf);

   if( isQtyFilter )
      return true;

   needSetFilter = true;
   if( this->filter ) this->filter->SetUserFilter(L"");
   return users.SetFilter(filter, object);
}

bool DBFTableSetReader::MoveNext(Object* parent)
{
   if( !base.Opened() )
   {
      if( !OpenNextBase() )
         return false;
   }

   bool ret = DBFReader::MoveNext(parent);
   if( ret == false )
   {
      Close();
		while (true)
		{
			ret = OpenNextBase();
			if (!ret)
				break;
			ret = DBFReader::MoveNext(parent);
			if (ret)
				break;
			Close();
		}
   }

   return ret;
}

void DBFTableSetReader::Close()
{
	DBFReader::Close();
	if (removeAfterReading)
		base.Delete();
}

void DBFTableSetReader::UpdateChilds(const wchar_t* userid)
{
   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i)->OpenNewFile(userid);
}

bool DBFTableSetReader::OpenNextBase()
{
   bool ret = false;

   while( true )
   {
      const char *table = users.Next(&userID);
      if( *table == '\0' )
         break;

      std::string folder = baseFolder;
      FilterReader::Data fData;
		if( filter )
         fData.filter = filter;
         //fData.filter = filter->Clone();

      if( needSetFilter && filter )
      {
         fData.filter->SetUserFilter((userID.empty()) ? L"userid is null" : L"userid = '" + userID + L"'");
      }

		ret = Open(folder + table, object, fData);
      //delete fData.filter;

      if( ret )
      {
         UpdateChilds((userID.empty()) ?
            ((Session&)object.GetSession()).GetUser().ID() : userID.c_str());
         break;
      }
   }

   return ret;
}

bool DBFTableSetReader::Get(Object* o) const
{
   if( !DBFReader::Get(o) ) return false;

   if( userIDIndex >= 0 && !userID.empty() )
   {
      Member& m = o->at(userIDIndex);
      m.str->assign(userID);
   }

   return true;
}

//
//------------------------------- DBFChildTableSetReader ------------------------------------
//
DBFChildTableSetReader::DBFChildTableSetReader(const GRServer::Format& fmt, const SessionObject& _object,
                                               const TableSetParam& param) : ChildDBFReader(fmt)
{
   this->userTable = param.userTable;
   this->commonTable = param.commonTable;

   baseFolder = _object.GetSession().Config().ExchangeFolder();

   thisObject = &_object;

   filter = const_cast<FilterReader::Data&>(param.filter).GetFilter();
   holder = const_cast<FilterReader::Data&>(param.filter).GetHolder();

   if( param.userField.compare(L"id") != 0 )
      userFieldMap.Load(param.userField, &(Session&)thisObject->GetSession());

   const ISessionObject *parent = _object.Parent();
   if( parent != NULL )
   {
      ObjectSource *os = ((const SessionObject*)parent->Self())->GetSource();
      if( os != NULL && os->reader )
      {
         os->reader->AddChild(DBFTableSet().Name(), this);
      }
      //if( os != NULL && os->readerName.compare(DBFTableSet().Name()) == 0 )
      //{
      //   DBFTableSetReader* pts = (DBFTableSetReader*)os->reader;
      //   pts->AddChildReader(this);
      //}
   }
}

void DBFChildTableSetReader::SetBaseFolder(const std::string& baseFolder)
{
   this->baseFolder = baseFolder;

   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i)->SetBaseFolder(baseFolder);
}

void DBFChildTableSetReader::UpdateChilds(const wchar_t* userid)
{
   ChildList::const_iterator i = childs.begin();
   for( ; i != childs.end(); i++ )
      (*i)->OpenNewFile(userid);
}

void DBFChildTableSetReader::AddChild(const std::wstring& childName, IReader* reader)
{
   if( childName.compare(DBFTableSet().Name()) == 0 )
      childs.push_back((DBFChildTableSetReader*)reader);
}

void DBFChildTableSetReader::OpenNewFile(const wchar_t* userW)
{
   if( base.Opened() )
      Close();

   USES_CONVERSION;
   const char* user = W2A_CP(userW, CP_UTF8);
   if( userFieldMap.size() > 0 )
      user = userFieldMap.Get(user);

   std::string folder = baseFolder;

   FilterReader::Data data;
   data.holder = holder;
   //if( filter )
   //   data.filter = filter->Clone();

   bool opened = Open(folder + userTable + user, *thisObject, data);
   if( opened == false )
      opened = Open(folder + commonTable, *thisObject, data);

   UpdateChilds(userW);

   //delete data.holder;
   //delete data.filter;
}

//
//------------------------------- DBFCatalogTableReader ------------------------------------
//
DBFOrderedCatalogTableReader::DBFOrderedCatalogTableReader(const GRServer::Format &fmt, const SessionObject &obj, const TableSetParam &param) :
   DBFReader(fmt), users(param, obj), objIndex(-1), childFormat(NULL)
{
	needRestoreUser = false;
   thisObject = &obj;
   filter = const_cast<FilterReader::Data&>(param.filter).GetFilter();
   catalog.Set(*thisObject, param.catalog);
   objIndex = childObject.Set(*thisObject, param.catalogObject, &childFormat);
	child = (SessionObject*)obj.GetChild(param.catalogObject);

   userIDIndex = obj.format->FindMember(L"userid");

   baseFolder = obj.GetSession().Config().ExchangeFolder();
	removeAfterCommit = param.removeAfterReading;
}

void DBFOrderedCatalogTableReader::SetBaseFolder(const std::string& baseFolder)
{
   this->baseFolder = baseFolder;
   users.BaseFolderChanged(baseFolder, *thisObject);
}

bool DBFOrderedCatalogTableReader::SetFilter(const wchar_t *filter, const ISessionObject &object)
{
   return users.SetFilter(filter, object);
}

bool DBFOrderedCatalogTableReader::MoveNext(Object *parentObject)
{
	if (!base.Opened() && !OpenNextBase())
	{
		if (needRestoreUser)
		{
			thisObject->GetSession().RestoreUser();
			needRestoreUser = false;
		}
		return false;
	}

   bool ret = DBFReader::MoveNext(parentObject);
	
	if (needRestoreUser)
	{
		thisObject->GetSession().RestoreUser();
		needRestoreUser = false;
	}
	if (!ret)
   {
      Close();
      ret = OpenNextBase();
   }

   return ret;
}

bool DBFOrderedCatalogTableReader::OpenNextBase()
{
   bool ret = false;
   do
   {
      const char *table = users.Next(&userID);
      if( *table == '\0' )
         break;
      ret = Open(table);
   } while( !ret );
   return ret;
}

ServObject* DBFOrderedCatalogTableReader::CreateChild(Object* o) const
{
   Member& m = o->at(objIndex);
   ServObject* obj = m.object;
   if( obj == NULL )
   {
      obj = new ServObject(const_cast<GRServer::Format*>(childFormat));
      m.object = obj;

		// Если вдруг был reader его надо убрать, чтоб не мешал загрузке в SessionObject::Load
		if (child != NULL)
			child->CloseReader();
   }

   return obj;
}

bool DBFOrderedCatalogTableReader::Get(Object* o) const
{
   if( !DBFReader::Get(o) ) return false;
   if( userIDIndex >= 0 )
   {
      Member& m = o->at(userIDIndex);
      m.str->assign(userID);
   }

   if( objIndex < 0 ) return true;

   const_cast<DBFOrderedCatalogTableReader*>(this)->rc--; //MoveNext moves rc

   ServObject* obj = CreateChild(o);
   std::string code = catalog.Read(base);
   do
   {
      Object *co = childObject.Read(base);
      obj->push_back(co);

      const_cast<DBFOrderedCatalogTableReader*>(this)->rc++;
      if( !base.ReadRec(rc) )
         break;

      const std::string& ccode = catalog.Read(base);
      if( code.compare(ccode) != 0 )
      {
         code = ccode;
         //const_cast<DBFOrderedCatalogTableReader*>(this)->rc--;
         break;
      }
   } while(true);

   if( userIDIndex >= 0 )
   {
      Member& m = o->at(userIDIndex);
      m.str->assign(userID);
   }

   return true;
}

bool DBFOrderedCatalogTableReader::Open(const std::string& fileName)
{
   FilterReader::Data fData;
	if (filter)
		fData.filter = filter;

	if (userID.empty() == false && !needRestoreUser)
		needRestoreUser = thisObject->GetSession().Impresonate(userID.c_str(), false);

	bool ret = DBFReader::Open(baseFolder + fileName, *thisObject, fData);

	//fData.filter = filter->Clone();
   if( ret )
      return (catalog.Open(base) && childObject.Open(base));

	//delete fData.filter;
   return false;
}

void DBFOrderedCatalogTableReader::Close()
{
	if (needRestoreUser)
	{
		thisObject->GetSession().RestoreUser();
		needRestoreUser = false;
	}
	DBFReader::Close();

	if (removeAfterCommit)
		base.Delete();
}

//
//------------------------------- DBFOrderedCatalogTableReader ------------------------------------
//
DBFCatalogTableReader::DBFCatalogTableReader(const GRServer::Format &fmt, const GRServer::SessionObject &object,
   const TableSetParam &param) : DBFOrderedCatalogTableReader(fmt, object, param)
{
}

DBFCatalogTableReader::~DBFCatalogTableReader()
{
   Close();
}

void DBFCatalogTableReader::Close()
{
   ClearItems();
   DBFOrderedCatalogTableReader::Close();
}

bool DBFCatalogTableReader::MoveNext(Object *parentObject)
{
   if( !base.Opened() && !OpenNextBase() ) return false;

   bool ret = (curItem != items.end());
   if( !ret )
   {
      Close();
      ret = OpenNextBase();
   }
   return ret;
}

bool DBFCatalogTableReader::Get(Object* o) const
{
   curItem->second->MoveTo(o);
   delete curItem->second;

   items.erase(curItem++);

   if( userIDIndex >= 0 )
   {
      Member& m = o->at(userIDIndex);
      m.str->assign(userID);
   }
   return true;
}

bool DBFCatalogTableReader::OpenNextBase()
{
   do
   {
      if( !DBFOrderedCatalogTableReader::OpenNextBase() )
         return false;
      if( base.NumRec() > 0 )
         break;
      Close();
   } while( true );

   if( objIndex >= 0 )
   {
      for( long rci = 0; base.ReadRec(rci); rci++ )
      {
         if( filter != NULL && filter->InSet(base, *thisObject) == false )
            continue;

         Object *o;
         const std::string& code = catalog.Read(base);
         std::map<std::string, Object*>::const_iterator fnd = items.find(code);
         if( fnd == items.end() )
         {
            o = Object::Create(*thisObject->format);
            DBFReader::Get(o);
            items[code] = o;
         } else
            o = fnd->second;

         ServObject* obj = CreateChild(o);
         obj->push_back(childObject.Read(base));
      }
   }
   curItem = items.begin();
   return (curItem != items.end());
}

void DBFCatalogTableReader::ClearItems()
{
   std::map<std::string, Object*>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      delete i->second;
}

//
//------------------------------- ChildObject ------------------------------------
//
Catalog::Catalog()
{
}

void Catalog::Set(const SessionObject& object, const std::wstring& catalog)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
   {
      std::wstring::size_type sp = 0;
      while(true)
      {
         std::wstring::size_type ep = catalog.find_first_of(L',', sp);
         std::wstring tval = catalog.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

         const ObjectDef::Field *f = od->FindField(tval);
         if( f != NULL )
         {
            USES_CONVERSION;
            BaseData bd;

            bd.name = W2A_CP(f->data.c_str(), DBF_CODE_PAGE);
            bd.offset = 0;
            bd.width = 0;
            data.push_back(bd);
         }

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
   }
}

bool Catalog::Open(const DataForm& base)
{
   int ctr = 0;
   DataList::iterator i = data.begin();
   for( ; i != data.end(); i++ )
   {
      if( !i->name.empty() )
      {
         DBField* f = base.GetFieldRef(i->name.c_str());
         if( f != NULL )
         {
            i->offset = f->offset;
            i->width = f->width;
            ctr++;
         }
      }
   }
   return (data.size() > 0 && (unsigned)ctr == data.size());
}

const std::string& Catalog::Read(const DataForm& base) const
{
   buffer.clear();

   DataList::const_iterator i = data.begin();
   for( ; i != data.end(); i++ )
   {
      if( i->width != 0 )
      {
         const char *sp = base.GetRec() + i->offset;
         std::string val(sp, i->width);
         buffer.append(val);
      }
   }
   return buffer;
}

//
//------------------------------- ChildObject ------------------------------------
//
ChildObject::ChildObject()
{
}

int ChildObject::Set(const SessionObject& parObject, const std::wstring& items, const GRServer::Format** format)
{
   const ISessionObject *so = parObject.GetChild(items);
   object = (so == NULL) ? NULL : (const SessionObject*)so->Self();
   int index = -1;
   if( object != NULL )
   {
      index = parObject.format->FindMember(items.c_str());
      *format = object->format;
   }

   return index;
}

bool ChildObject::Open(const DataForm& base)
{
   if( object == NULL ) return false;
   return reader.Create(*object, base);
}

Object* ChildObject::Read(const DataForm& base) const
{
   if( object == NULL ) return NULL;

   Object* o = Object::Create(*object->format);
   reader.Read(o, base);
   if( object != NULL )
      const_cast<SessionObject*>(object)->UpdateExecutableFields(o, false);

   return o;
}

//
//------------------------------- TableSetCatalogWriter ------------------------------------
//
TableSetCatalogWriter::TableSetCatalogWriter(const TableSetParam& param, const GRServer::Format &fmt,
                                             const IObjectData *_objDef, const std::string &_fileName) :
   CatalogWriter(fmt, _objDef, _fileName)
{
   childObject = param.catalogObject;
}

bool TableSetCatalogWriter::Prepare(const ISessionObject& object)
{
   if( childWriter == NULL )
   {
      const ISessionObject* ch = object.GetChild(childObject);
      if( ch != NULL )
         childWriter = new CatalogItemsWriter(*(const SessionObject*)ch->Self());
   }

   return CatalogWriter::Prepare(object);
}

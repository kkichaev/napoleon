/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   17/11/2012   creating
 */
#include "stdafx.h"

#include "Reader.h"

#include "QuerySource.h"
#include "Writer.h"
#include <folderholder.h>

#include <algorithm>

class FolderReader : public IDataSource::IReader
{
public:
   FolderReader(const ISessionObject& object, IDataSource::IReader* child, bool autoID, bool debug);
   ~FolderReader() { Close(); }

   virtual void Remove() {}
   virtual void Close();
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return child->SetFilter(filter, object); }

   virtual bool MoveNext(Object *parentObject) { return child->MoveNext(parentObject); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return child->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return child->Value(name); }
   virtual void AddChild(const std::wstring& childName, IReader* reader) { child->AddChild(childName, reader); }

protected:
   IDataSource::IReader* child;

   bool autoID;
   mutable int curIndex;

   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder *folderHolder;
   ISession* session;
	bool debug;
};

class TreeReader
{
public:
	TreeReader() { levelIndex = -1; doSort = false; buildedTree = false; }
   ~TreeReader() { Clear(); }

   void SetData(int li, int id, int ri, int ni, bool doSort)
   {
      levelIndex = li; childIndex = id; rowidIndex = ri; curRec = 0; nameIndex = ni; this->doSort = doSort;
   }

   void Add(const CString& parent, Object* obj)
   {
      if( childIndex >= 0 )
      {
         bool test = false;
         const CString* id = obj->at(childIndex).str;
			if (id->empty() == false && parent.compare(*id) != 0)
			{
				if (used.find(id->c_str()) != used.end())
				{
					delete obj;
				}
				else
				{
					used.insert(id->c_str());
					data[(const std::wstring&)parent].push_back(ObjectData(obj, *id, *obj->at(nameIndex).str));
				}
			}
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
	bool buildedTree;
	std::set<std::wstring> used;

	struct ObjectData
   {
      Object* object;
      CString id;
      CString name;

      ObjectData(Object* o, const CString& id, const CString& name)
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
         for(; i != end(); i++ )
            delete i->object;
      }

      void pop_front() { erase(begin()); }
   };

	struct ObjectTree
	{
		Object* object;
		int level;
		DWORD curRec;
	};

   typedef std::map<std::wstring, ObjectList> TreeData;
   TreeData data;
   //typedef std::vector<TreeData::iterator> PathList;
   //PathList path;

	std::vector<ObjectTree> tree;
	mutable std::vector<ObjectTree>::iterator treeI;

	DWORD MakeTree(const std::wstring& parent, int level, DWORD curRec);
};

class FolderTreeReader : public IDataSource::IReader
{
public:
   FolderTreeReader(const ISessionObject& object, const CString& parentF, const CString& childF, 
      const CString* ridF, const CString* stmt, const vector<wstring>& filters, SQLHDBC hDbc, ODBCFlavor* flavor, bool debug);
   ~FolderTreeReader();

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { reader->SetFilter(filter, object); return true; }
   virtual void Remove() {}
   virtual void Close() { reader->Close(); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return reader->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return reader->Value(name); }

   virtual const ParamHelper* GetParamHelper() const { return reader != NULL ? reader->GetParamHelper() : NULL; }

protected:
   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder *folderHolder;
   ISession* session;
   const ISessionObject& object;

   const Format* format;
   bool readed;
   int parentIndex;
   TreeReader treeReader;

   IDataSource::IReader *reader;
};

class FolderTreeWriter : public Writer
{
public:
   FolderTreeWriter(const ISessionObject& object, const CString& parentF, const CString& childF, SQLHDBC hDbc, ODBCFlavor* flavor);

   virtual bool Write(const Object& o, RowID *rid);

private:
   std::vector<std::wstring> ids;
   int curLevel;
   int childIndex, levelIndex, parentIndex, idIndex;
};

//
//-------------------------------------- FolderReader ----------------------------------------------
//
FolderReader::FolderReader(const ISessionObject& object, IDataSource::IReader* _child, bool autoID, bool debug) :
   child(_child), curIndex(1)
{
   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);

   Format* f = object.Self()->format;
   keyIndex = f->FindMember(L"fid");
   valueIndex = f->FindMember(L"id");

   session = &object.GetSession();

   this->autoID = autoID;
	this->debug = debug;
}

void FolderReader::Close()
{
   if( child != NULL )
   {
      delete child;
      child = NULL;
   }
}

bool FolderReader::Get(Object* o) const
{
   if( !child->Get(o) )
      return false;

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
   {
      if( autoID )
      {
         o->at(valueIndex).number = curIndex++;
      }
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   }
   return true;
}

//
//-------------------------------------- FolderTreeWriter ----------------------------------------------
//
FolderTreeWriter::FolderTreeWriter(const ISessionObject& object, const CString& parentF, const CString& childF, SQLHDBC hDbc, ODBCFlavor* flavor) :
   Writer(object, hDbc, flavor)
{
   //GRServer::Format *format = object.Self()->format;
   //parentIndex = format->FindMember(parentF.c_str());
   //childIndex = format->FindMember(childF.c_str());
   ////levelIndex = format->FindMember(L"level");
   //idIndex = format->FindMember(L"id");

   //curLevel = -1;
}

bool FolderTreeWriter::Write(const Object& o, RowID *rid)
{
   //if (levelIndex >= 0)
   //{
   //   int level = (int)(o.at(levelIndex).number + 0.005);

   //   std::wstring parentID;
   //   while (level <= curLevel && ids.size())
   //   {
   //      ids.pop_back();
   //      curLevel--;
   //   }

   //   if (ids.size())
   //      parentID = ids.back();
   //   ids.push_back(o.at(idIndex).str->c_str());
   //   o.at(parentIndex).str->assign(parentID);

   //   curLevel = level;
   //}

   return Writer::Write(o, rid);
}

//
//-------------------------------------- FolderTreeReader ----------------------------------------------
//
FolderTreeReader::FolderTreeReader(const ISessionObject& _object, const CString& parentF, const CString& childF,
                                   const CString* ridF, const CString* stmt, const vector<wstring>& _filters,
                                   SQLHDBC hDbc, ODBCFlavor* flavor, bool debug) :
   readed(false), object(_object)
{
   if( stmt == NULL )
      reader = new Reader(object, hDbc, flavor, _filters, debug, NULL);
   else
      reader = new QueryReader(*stmt, object, hDbc, flavor, false, 0, NULL);

   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);

   format = object.Self()->format;
   keyIndex = format->FindMember(L"fid");
   valueIndex = format->FindMember(L"id");
	if (valueIndex >= 0)
	{
		const MemberFormat &mf = format->at(valueIndex);
		if (mf.type != MemberFormat::mtNumber)
			valueIndex = -1;
	}

   session = &object.GetSession();

   parentIndex = format->FindMember(parentF.c_str());
   
   int ridIndex = (ridF == NULL) ? -1 : format->FindMember(ridF->c_str());
   if( ridIndex >= 0 )
   {
      const MemberFormat &mf = format->at(ridIndex);
      if( mf.type != MemberFormat::mtNumber || (mf.flags & MemberFormat::ExecOnGet) != 0 )
         ridIndex = -1;
   }
   treeReader.SetData(format->FindMember(L"level"), format->FindMember(childF.c_str()), ridIndex, format->FindMember(L"name"), true);
}

FolderTreeReader::~FolderTreeReader()
{
   delete reader;
}

bool FolderTreeReader::MoveNext(Object *parentObject)
{
   if( !readed )
   {
      readed = true;

      treeReader.Clear();

      while( reader->MoveNext(parentObject) )
      {
         Object *o = Create(*format);
         reader->Get(o);

         const Member& mf = o->at(parentIndex);
         treeReader.Add(*mf.str, o);
      }
   }

   return treeReader.MoveNext();
}

bool FolderTreeReader::Get(Object* o) const
{
	if (!treeReader.Get(o))
		return false;

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   return true;
}

//
//------------------------------- TreeReader ------------------------------------
//
DWORD TreeReader::MakeTree(const std::wstring& parent, int level, DWORD curRec)
{
	TreeData::iterator fnd = data.find(parent);
	if (fnd != data.end())
	{
		if( doSort )
			std::sort(fnd->second.begin(), fnd->second.end());
		ObjectList::iterator i = fnd->second.begin();
		for (; i != fnd->second.end(); i++)
		{
			ObjectTree ot;
			ot.object = i->object;
			ot.curRec = curRec++;
			ot.level = level;
			tree.push_back(ot);

			curRec = MakeTree((const std::wstring&)i->id, level + 1, curRec);
		}
	}
	return curRec;
}

bool TreeReader::MoveNext()
{
   if( levelIndex < 0 )
      return false;
   
	if (!buildedTree)
	{
		buildedTree = true;
		MakeTree(L"", 1, 1);

		treeI = tree.begin();
	}
	return treeI != tree.end();
}

bool TreeReader::Get(Object* o) const
{
	if (treeI == tree.end())
	{
		return false;
	}
	
	treeI->object->MoveTo(o);
	o->at(levelIndex).number = treeI->level;
	if (rowidIndex >= 0)
		o->at(rowidIndex).number = treeI->curRec;

	treeI++;
	return true;
}

void TreeReader::Clear()
{
   data.clear();
}

//
//-------------------------------------- SQLFolderCreator ----------------------------------------------
//
IDataSource::IReader* SQLFolderCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
   {
      gServer->AddError(false, "ODBC не соединен");
      return NULL;
   }

   IDataSource::IReader* ret = NULL;

   const Parameter *pf = parameters.Find(L"parentField", -1);
   const Parameter *pdebug = parameters.Find(L"debug", -1);
   if( pf != NULL )
   {
      std::vector<wstring> filters;
      parameters.Load(&filters, L"readFilter", object);

      const Parameter *cf = parameters.Find(L"childField", -1);
      const Parameter *ridf = parameters.Find(L"rowidField", -1);
      const Parameter *stmtf = parameters.Find(L"stmt", -1);
      if( !cf  )
         gServer->AddError(false, "Ошибка создания SQLFolder - не все параметры");
      else
      {
         CString *spf = NULL, *scf = NULL, *sridf = NULL, *stmt = NULL;
         ISession& session = object.GetSession();
         
         if( session.Parse(&spf, pf->value, &object) &&
             session.Parse(&scf, cf->value, &object) &&
             (ridf == NULL || session.Parse(&sridf, ridf->value, &object)) &&
             (stmtf == NULL || session.Parse(&stmt, stmtf->value, &object))
            )
         {
            ret = new FolderTreeReader(object, *spf, *scf, sridf, stmt, filters, GetHDBC(), flavor, (pdebug != NULL));
         }
         delete spf;
         delete scf;
         delete sridf;
         delete stmt;
      }
   } else
   {
      IDataSource::IReader* child;
      const Parameter* p = parameters.Find(L"stmt", -1);
      const Parameter* aid = parameters.Find(L"autoid", -1);
      if( p != NULL )
      {
         CString *stmt = NULL;
         if( !object.GetSession().Parse(&stmt, p->value, &object) )
         {
            gServer->AddError(false, "SQLFolder не правильный параметр stmt");
            delete stmt;
            return NULL;
         }
         child = new QueryReader(*stmt, object, GetHDBC(), flavor, false, 0, NULL);
         delete stmt;
      } else
      {
         child = new Reader(object, GetHDBC(), flavor, vector<wstring>(), (pdebug == NULL), NULL);
      }
      ret = new FolderReader(object, child, (aid != NULL), (pdebug == NULL));
   }
   return ret;
}

IDataSource::IWriter* SQLFolderCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
   {
      gServer->AddError(false, "ODBC не соединен");
      return NULL;
   }
   const Parameter *pf = parameters.Find(L"parentField", -1);
   const Parameter *cf = parameters.Find(L"childField", -1);
   if( pf == NULL || cf == NULL )
      return NULL;

   IDataSource::IWriter *ret = NULL;

   CString *spf = NULL, *scf = NULL;
   ISession& session = object.GetSession();

   if( session.Parse(&spf, pf->value, &object) &&
       session.Parse(&scf, cf->value, &object))
   {
      ret = new FolderTreeWriter(object, *spf, *scf, GetHDBC(), flavor);
   }
   delete spf;
   delete scf;

   return ret;
}

IDataSource::IRemover* SQLFolderCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;

   ODBCFlavor* flavor = GetFlavor();
   if( flavor == NULL )
   {
      gServer->AddError(false, "OLEDB не соединен");
      return NULL;
   }
   return GRServer::CreateRemover(object, GetHDBC());
}
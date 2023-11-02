/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   17/11/2012   creating
 */
#include "stdafx.h"

#include "OleReader.h"

#include "QuerySource.h"
#include <folderholder.h>

#include <algorithm>

class FolderReader : public OleReader
{
public:
   FolderReader(CDataConnection& c, const ISessionObject& object);
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);

protected:
   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder *folderHolder;
   ISession* session;
};

class TreeReader
{
public:
   TreeReader() { levelIndex = -1; doSort = false; }
   ~TreeReader() { Clear(); }

   void SetData(int li, int id, int ri, int ni, bool doSort)
   {
      levelIndex = li; childIndex = id; rowidIndex = ri; curRec = 0; nameIndex = ni; this->doSort = doSort;
   }

   void Add(const CString& parent, Object* obj)
   {
      if( childIndex >= 0 )
      {
         data[(const std::wstring&)parent].push_back(ObjectData(obj, *obj->at(childIndex).str, *obj->at(nameIndex).str));
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

   typedef std::map<std::wstring, ObjectList> TreeData;
   TreeData data;
   typedef std::vector<TreeData::iterator> PathList;
   PathList path;
};

class FolderTreeReader : public OleReader
{
public:
   FolderTreeReader(CDataConnection& c, const ISessionObject& object, const CString& parentF, const CString& childF, const CString& ridF);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

protected:
   int keyIndex;
   int valueIndex;
   mutable FolderIDHolder *folderHolder;
   ISession* session;

   const Format* format;
   bool readed;
   int parentIndex;
   TreeReader treeReader;
};

//
//-------------------------------------- FolderReader ----------------------------------------------
//
FolderReader::FolderReader(CDataConnection& c, const ISessionObject& object) :
   OleReader(object, c, NULL)
{
   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);

   Format* f = object.Self()->format;
   keyIndex = f->FindMember(L"fid");
   valueIndex = f->FindMember(L"id");

   session = &object.GetSession();
}

bool FolderReader::Get(Object* o) const
{
   if( !OleReader::Get(o) )
      return false;

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   return true;
}

bool FolderReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   this->filter.assign(filter);
   return true;
}

//
//-------------------------------------- FolderTreeReader ----------------------------------------------
//
FolderTreeReader::FolderTreeReader(CDataConnection& c, const ISessionObject& object, const CString& parentF, const CString& childF, const CString& ridF) :
   OleReader(object, c, NULL), readed(false)
{
   folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);

   format = object.Self()->format;
   keyIndex = format->FindMember(L"fid");
   valueIndex = format->FindMember(L"id");

   session = &object.GetSession();

   parentIndex = format->FindMember(parentF.c_str());
   
   int ridIndex = format->FindMember(ridF.c_str());
   if( ridIndex >= 0 )
   {
      const MemberFormat &mf = format->at(ridIndex);
      if( mf.type != MemberFormat::mtNumber || (mf.flags & MemberFormat::ExecOnGet) != 0 )
         ridIndex = -1;
   }
   treeReader.SetData(format->FindMember(L"level"), format->FindMember(childF.c_str()), ridIndex, format->FindMember(L"name"), true);
}

bool FolderTreeReader::MoveNext(Object *parentObject)
{
   if( !readed )
   {
      readed = true;

      treeReader.Clear();
      while( OleReader::MoveNext(parentObject) )
      {
         Object *o = Create(*format);
         OleReader::Get(o);

         const Member& mf = o->at(parentIndex);
         treeReader.Add(*mf.str, o);
      }
   }

   return treeReader.MoveNext();
}

bool FolderTreeReader::Get(Object* o) const
{
   treeReader.Get(o);

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   return true;
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
         ObjectData* src = &(*((*i)->second).begin());

         TreeData::iterator fnd = data.find((const std::wstring&)src->id);
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

      o->at(levelIndex).number = path.size();
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
//-------------------------------------- SQLFolderCreator ----------------------------------------------
//
IDataSource::IReader* SQLFolderCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   CDataConnection* connection = GetConnection();
   if( connection == NULL )
   {
      gServer->AddError(false, "OLEDB не соединен");
      return NULL;
   }

   IDataSource::IReader* ret = NULL;

   const Parameter *pf = parameters.Find(L"parentField", -1);
   if( pf != NULL )
   {
      const Parameter *cf = parameters.Find(L"childField", -1);
      const Parameter *ridf = parameters.Find(L"rowidField", -1);
      if( !cf || !ridf )
         gServer->AddError(false, "Ошибка создания SQLFolder - не все параметры");
      else
      {
         CString *spf = NULL, *scf = NULL, *sridf = NULL;
         ISession& session = object.GetSession();
         
         if( session.Parse(&spf, pf->value, &object) &&
            session.Parse(&scf, cf->value, &object) &&
            session.Parse(&sridf, ridf->value, &object) )
         {
            ret = new FolderTreeReader(*connection, object, *spf, *scf, *sridf);
         }
         delete spf;
         delete scf;
         delete sridf;
      }
   } else
   {
      ret = new FolderReader(*connection, object);
   }
   return ret;
}

IDataSource::IWriter* SQLFolderCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;
}

IDataSource::IRemover* SQLFolderCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;

   CDataConnection* connection = GetConnection();
   if( connection == NULL )
   {
      gServer->AddError(false, "OLEDB не соединен");
      return NULL;
   }
   return GRServer::CreateRemover(object, *connection);
}
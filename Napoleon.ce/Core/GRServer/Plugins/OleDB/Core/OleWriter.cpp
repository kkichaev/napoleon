/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"
#include "OleSource.h"
#include "Binder.h"
#include <ServerDefs.h>

using namespace GRServer;
using namespace std;

class WriteBinder : public ParamBinder
{
public:
   WriteBinder() : selectStmt(true), fkIndex(0xFFFFFFFF) {}

   virtual bool PrepareWrite(const ISessionObject& object, CSession& session);
   bool Write(const Object& o, const Object* parent);

   virtual HRESULT OpenReader(CSession& session, const std::wstring& stmt) { return reader.Create(session, stmt.c_str()); }

protected:
   bool selectStmt;
   DWORD fkIndex;

protected:
   bool WriteParams(const Object& obj, const Object* parent);
   void WriteData(const Object& o, const Object* parent);
   
   bool CreateInsertStmt(const ISessionObject& object, CSession& session);
   bool CreateSelectStmt(const ISessionObject& object, CSession& session, const vector<wstring>& keyFields);
};

class OleWriter : public IDataSource::IWriter
{
public:
   OleWriter(const ISessionObject& object, CDataConnection& connection);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid) { return Write(o, NULL, rid); }
   virtual void Close();

   virtual WriteBinder* CreateWriter() const { return new WriteBinder(); }

   bool Write(const Object& o, const Object* parent, RowID *rid);

protected:
   int doCount;
   bool rootObject;
   CSession& session;
   WriteBinder* writer;

#ifdef DEBUG
   int totalCount;
#endif
};

class RemoveFKBinder : public ParamBinder
{
public:
   bool PrepareFKRemove(const ISessionObject& object, CSession& session);
   bool Remove(const Object& parentObj);
};

class ChildWriteBinder : public WriteBinder
{
public:
   ChildWriteBinder() {}
   virtual bool PrepareWrite(const ISessionObject& object, CSession& session) { return CreateInsertStmt(object, session); }
protected:
};

class OleChildWriter : public OleWriter
{
public:
   OleChildWriter(const ISessionObject& object, CDataConnection& connection);

   virtual WriteBinder* CreateWriter() const { return new ChildWriteBinder(); }
   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);

protected:
   int childIndex;
   RemoveFKBinder remover;
};

class OleRemover : public IDataSource::IRemover
{
public:
   OleRemover(const ISessionObject& object, CDataConnection& connection);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() {}

protected:
   std::wstring tableName;
   CSession& session;
   CCommand<CNoAccessor> remover;
};

//
// -------------------------------------------- WriteBinder --------------------------------------
//
bool WriteBinder::PrepareWrite(const ISessionObject& object, CSession& session)
{
   std::wstring keyFilter;
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return false;

   IObjectData::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
   if( keyI == od->members.end() )
      return CreateInsertStmt(object, session);

   vector<wstring> keyFields;
   PKToList(&keyFields, keyI->second, false);
   return CreateSelectStmt(object, session, keyFields);
}

bool WriteBinder::CreateSelectStmt(const ISessionObject& object, CSession& session, const vector<wstring>& keyFields)
{
   const IObjectData* od = object.GetObjectDef();
   Format* format = object.Self()->format;

   std::wstring keyFilter;
   vector<wstring>::const_iterator fi = keyFields.begin();
   for( ; fi != keyFields.end(); fi++ )
   {
      int idx = format->FindMember(fi->c_str());
      if( idx < 0 )
         continue;
      MemberFormat mf(format->at(idx));
      QuoteString(&mf.name);
      AddParam(&keyFilter, mf, idx);
   }
   if( !BindParams() )
      return false;

   return PrepareRead(object, keyFilter, session);
}

bool WriteBinder::CreateInsertStmt(const ISessionObject& object, CSession& session)
{
   // insert into ...
   const IObjectData* od = object.GetObjectDef();
   Format *format = object.Self()->format;
   Format *parentFormat = NULL; 

   std::wstring tableName;
   std::wstring stmt(L"INSERT INTO "), values(L") VALUES (");
   
   selectStmt = false;

   QuoteString(&tableName, od->tableName);
   stmt += tableName; stmt += L" (";
   bool begin = false;
   vector<MemberFormat> fields;

   IObjectData::Fields::const_iterator ofi = od->fields.begin();
   for( ; ofi != od->fields.end(); ofi++ )
      fields.push_back(ofi->format);

   CVector<MemberFormat>* sfkFields = NULL;
   if( od->LoadFK(&sfkFields) )
   {
      fkIndex = fields.size();
      parentFormat = object.Parent()->Self()->format;
      CVector<MemberFormat>::const_iterator fki = sfkFields->begin();
      for( ; fki != sfkFields->end(); fki++ )
         fields.push_back(*fki);
   }
   delete sfkFields;

   DWORD fidx = 0;
   vector<MemberFormat>::const_iterator fi = fields.begin();
   for( ; fi != fields.end(); fi++, fidx++ )
   {
      int idx = -1;
      if( fidx < fkIndex )
      {
         idx = format->FindMember(fi->name.c_str());
      } else
      {
         int fp = fi->name.find_last_of(L'$');
         idx = parentFormat->FindMember(fi->name.substr(fp+1).c_str());
      }
      if( idx < 0 )
      {
         if( fidx < fkIndex )
            fkIndex--;
         continue;
      }

      MemberFormat mf(*fi);
      QuoteString(&mf.name);

      if( !begin )
         begin = true;
      else
      {
         stmt += L",";
         values += L",";
      }

      stmt += mf.name;
      values += L"?";

      AddParam(NULL, mf, idx);
   }
   stmt += values; stmt += L")";
   if( !BindParams() )
      return false;

   return (reader.Create(session, stmt.c_str()) == S_OK);
}

bool WriteBinder::WriteParams(const Object& o, const Object* parent)
{
   bool ret = true;
   DWORD idx = 0;
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++, idx++ )
      (*i)->Write((idx<fkIndex) ? o : *parent);

   return ret;
}

void WriteBinder::WriteData(const Object& o, const Object* parent)
{
   DWORD idx = 0;
   vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++, idx++ )
      (*i)->Write((idx<fkIndex) ? o : *parent);
}

bool WriteBinder::Write(const Object& o, const Object* parent)
{
   HRESULT hr;

   WriteParams(o, parent);
   if( !selectStmt )
   {
      hr = reader.Open();
      return (hr == S_OK);
   }

   CDBPropSet ps(DBPROPSET_ROWSET);
   ps.AddProperty(DBPROP_IRowsetChange, true);
   ps.AddProperty(DBPROP_UPDATABILITY, DBPROPVAL_UP_CHANGE | DBPROPVAL_UP_INSERT);
   hr = reader.Open(&ps, NULL, true, 1);
   if( hr != S_OK )
      return false;

   if( reader.MoveFirst() == S_OK )
   {
      WriteData(o, parent);
      hr = reader.SetData();
   } else
   {
      WriteData(o, parent);
      hr = reader.Insert();
   }

   if( hr == S_OK )
   {
      std::vector<FileField*>::iterator fi = files.begin();
      for( ; fi != files.end(); fi++ )
         (*fi)->WriteFile(o);
   }

   reader.Close();
   return (hr == S_OK);
}

//
// -------------------------------------------- RemoveFKBinder --------------------------------------
//
bool RemoveFKBinder::PrepareFKRemove(const ISessionObject& object, CSession& session)
{
   const IObjectData* od = object.GetObjectDef();
   const ISessionObject* parent = object.Parent();
   if( od == NULL || parent == NULL )
      return false;
   CVector<MemberFormat>* fkFields = NULL;
   bool ret = od->LoadFK(&fkFields);

   if( ret )
   {
      wstring paramStmt;
      Format *pf = parent->Self()->format;

      CVector<MemberFormat>::const_iterator fi = fkFields->begin();
      for( ; fi != fkFields->end(); fi++ )
      {
         int pos = fi->name.find_last_of(L'$');
         const std::wstring& pname = fi->name.substr(pos+1);
         int fldIndex = pf->FindMember(pname.c_str());
         if( fldIndex < 0 )
            continue;

         AddParam(&paramStmt, (*fi), fldIndex);
      }

      ret = BindParams();
      if( ret )
      {
         wstring tn;
         QuoteString(&tn, od->tableName);
         wstring stmt(L"DELETE FROM "); stmt += tn; stmt += L" WHERE "; stmt += paramStmt;

         HRESULT hr = reader.Create(session, stmt.c_str());
         ret = (hr == S_OK);
      }
   }

   delete fkFields;
   return ret;
}

bool RemoveFKBinder::Remove(const Object& parentObj)
{
   if( !WriteParams(parentObj) )
      return false;
   HRESULT hr = reader.Open();
   return (hr == S_OK);
}

//
// -------------------------------------------- OleWriter --------------------------------------
//
OleWriter::OleWriter(const ISessionObject& object, CDataConnection& connection) : 
   doCount(0),
#ifdef DEBUG
   totalCount(0),
#endif
   session(connection.m_session),
   writer(NULL)
{
}

bool OleWriter::Prepare(const ISessionObject& object)
{
   writer = CreateWriter();
   if( !writer->PrepareWrite(object, session) )
      return false;

   rootObject = (object.Parent() == NULL);
   if( rootObject )
      session.StartTransaction();
   return true;
}

bool OleWriter::Write(const Object& o, const Object* parent, RowID *rid)
{
   if( !writer )
      return false;

   bool ret = writer->Write(o, parent);
   if( ret )
   {
      WriterList::iterator ci = childs.begin();
      for( ; ret && ci != childs.end(); ci++ )
         ret = (*ci)->Write(o, NULL);
   }
#ifdef DEBUG
   if( rootObject )
      totalCount++;
#endif
   if( rootObject && doCount++ > MAX_DO_COUNT )
   {
      doCount = 0;
      session.Commit(TRUE);
   }

   return ret;
}

void OleWriter::Close()
{
   if( writer != NULL )
   {
      writer->Close();
      delete writer;
      writer = NULL;
   }
   if( rootObject )
      session.Commit();
}

//
// -------------------------------------------- OleChildWriter --------------------------------------
//
OleChildWriter::OleChildWriter(const ISessionObject& object, CDataConnection& connection) :
   OleWriter(object, connection),
   childIndex(-1)
{
}

bool OleChildWriter::Prepare(const ISessionObject& object)
{
   //ISessionObject* parent = object.Parent();
   //if( parent == NULL )
   //   return false;
   //const IObjectData* parentOD = parent->GetObjectDef();
   //if( parentOD == NULL )
   //   return false;
   //
   if( !remover.PrepareFKRemove(object, session) )
      return false;

   const IObjectData* od = object.GetObjectDef();
   int off = od->tableName.find_last_of(L'$');
   childIndex = object.Parent()->Self()->format->FindMember(od->tableName.substr(off+1).c_str());

   return (childIndex < 0 ) ? false : OleWriter::Prepare(object);
}

bool OleChildWriter::Write(const Object& o, RowID *rid)
{
   if( childIndex < 0 )
      return false;

   const Member& m = o.at(childIndex);
   if( !remover.Remove(o) )
      return false;

   bool res = true;
   if( m.object != NULL )
   {
      ServObject::const_iterator i = m.object->begin();
      for( ; res && i != m.object->end(); i++ )
         res = OleWriter::Write(*(*i), &o, NULL);
   }

   return res;
}

//
// -------------------------------------------- OleRemover --------------------------------------
//
OleRemover::OleRemover(const ISessionObject& object, CDataConnection& connection) :
   session(connection.m_session)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
      QuoteString(&tableName, od->tableName);
}

bool OleRemover::Remove(const wchar_t* filter)
{
   if( tableName.empty() )
      return false;

   std::wstring stmt(L"DELETE FROM "); stmt += tableName;
   if( filter != NULL && *filter != L'\0' )
   {
      stmt += L" WHERE "; stmt += filter;
   }
   HRESULT hr = remover.Open(session, stmt.c_str());
   return (hr == S_OK);
}

IDataSource::IWriter* GRServer::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, CDataConnection& connection)
{
   if( parent != NULL )
      return new OleChildWriter(object, connection);
   
   return new OleWriter(object, connection);
}

IDataSource::IRemover* GRServer::CreateRemover(const ISessionObject& object, CDataConnection& connection)
{
   return new OleRemover(object, connection);
}

IDataSource::ISelector* GRServer::CreateSelector(const ISessionObject& object, CDataConnection& connection)
{
   return NULL;
}
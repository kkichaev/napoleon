/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include "OleSource.h"
#include <atldbsch.h>
#include <ServerDefs.h>

#include "QuerySource.h"
#include "isessobj.h"

using namespace GRServer;
using namespace std;

const wchar_t* GRServer::SENDED_FIELDS = L"$_objSended";

#define SSPROP_INIT_MARSCONNECTION               16
extern const GUID OLEDBDECLSPEC DBPROPSET_SQLSERVERDBINIT        = {0x5cf4ca10,0xef21,0x11d0,{0x97,0xe7,0x0,0xc0,0x4f,0xc2,0xad,0x98}};

class OleDataSource : public IInternalDataSource
{
public:
   OleDataSource();
   ~OleDataSource();

   virtual IBinary* GetServerData(int id);
   virtual bool    PutServerData(int id, const Binary& b);

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config);
   virtual void    Close();

   virtual const wchar_t* Name() const { return L"OleSourceInternal"; }

   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const;

   virtual bool Execute(const wchar_t* stmt, ISession* session);

   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

   CDataConnection* GetConnection() const { return (opened) ? (CDataConnection*)(&connection) : NULL; }
   bool TryConnect();

protected:
   bool opened;
   CDataConnection connection;
   std::wstring database;

protected:
   bool IsTableExists(const std::wstring& table);

   bool CheckDBFormat(GRServer::IObjectDef* objDef);
   HRESULT CreateTable(const IObjectData& objDef);
   HRESULT CheckTable(const IObjectData& objDef);

   // Все идентификаторы (таблиы и колонок) должны быть в кавычках - это используется в коде
   HRESULT CreateTable(const std::wstring& tableName, const vector<MemberFormat>& fields, const vector<wstring>& keyFields, const vector<MemberFormat>* fkFields = NULL);
   HRESULT CreateIndex(const std::wstring& tableName, const vector<MemberFormat>& fkFields);

   struct MFCmp
   {
	bool operator()(const MemberFormat& _Left, const MemberFormat& _Right) const
		{	// apply operator< to operands
         return (_Left.name.compare(_Right.name) < 0);
		}
   };

   typedef std::set<MemberFormat, MFCmp> FieldSet;

   void GetTableFields(FieldSet *fields, const std::wstring table);
   HRESULT AlterTable(const IObjectData& objDef, const std::vector<MemberFormat>& added);
};

class BDataAccessor
{
public:
	ISequentialStream* data;
   ULONG length;
   ULONG status;

BEGIN_COLUMN_MAP(BDataAccessor)
	BLOB_ENTRY_LENGTH_STATUS(1, IID_ISequentialStream, STGM_READ, data, length, status)
END_COLUMN_MAP()
};

class BDataWriteAccessor
{
public:
	ISequentialStream* data;
   ULONG id;
   ULONG length;
   ULONG status;

BEGIN_COLUMN_MAP(BDataWriteAccessor)
	BLOB_ENTRY_LENGTH_STATUS(1, IID_ISequentialStream, STGM_READ, data, length, status)
	COLUMN_ENTRY(2, id)
END_COLUMN_MAP()
};


class BinaryWrapper : public IBinary
{
public:
   BinaryWrapper(const Binary& b)
   {
      size = b.Size();
      bytes = b;
   }

   virtual void Assign(Binary* _b) {}

   virtual DWORD Size() const { return size; }
   virtual const BYTE* Bytes() const { return bytes; }

   virtual void Close() {}

protected:
   const BYTE* bytes;
   DWORD size;
};

OleDataSource* gvDataSource;
std::wstring providerName;
const int DEFAULT_STRING_LENGTH = 300;

inline bool IsOracle() { return (wcsstr(providerName.c_str(), L"OraOLEDB") != NULL); }

void GRServer::QuoteString(std::wstring* dest)
{
   if( *dest->begin() != L'\"') dest->insert(0, L"\"");
   if( *dest->rbegin() != L'\"') dest->append(L"\"");
}

IInternalDataSource* GRServer::CreateInternalDS()
{
   if( gvDataSource == NULL )
      gvDataSource = new OleDataSource();
   return gvDataSource;
}

bool GRServer::Execute(CSession& session, const std::wstring& sql)
{
   CCommand<CNoAccessor, CNoRowset> cmd;
   HRESULT hr = cmd.Open(session, sql.c_str());
   cmd.Close();

   if( gServer->GetConfig().Debug() == IErrorLogger::Full )
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Execute stmt %s", W2A(sql.c_str()));

      if( hr != S_OK )
         AddErrorsToLog(false, hr);
   }

   return (hr == S_OK);
}

bool OleDataSource::Execute(const wchar_t* stmt, ISession* session)
{
   std::wstring wstmt(stmt);
   return GRServer::Execute(connection, wstmt);
}

static void InitReader(ISessionObject* so, const wchar_t* expr, wchar_t *_key, CDataConnection& connection)
{
   QuerySourceCreator qs;
   ObjectSource* src = so->GetSource();
   ISessionObject* parent = so->Parent();
   if( parent == NULL )
   {
      std::wstring stmt(expr);
      src->reader = new QueryReader(connection, stmt, *so);
      src->readerName.assign(qs.Name());
   } else
   {
      wchar_t *p = wcschr(_key, L',');
      if( p != NULL )
         *p = L'\0';

      std::wstring key(_key);
      src->reader = new QueryChildReader(key, *so, *parent);
      src->readerName.assign(qs.Name());

      _key = (p!=NULL) ? p+1 : L"";
   }

   ServObject* obj = so->Self();
   GRServer::Format::const_iterator fi = obj->format->begin();
   ServObject::iterator si = obj->begin();
   for( ; fi != obj->format->end(); fi++, si++ )
   {
      if( fi->type == MemberFormat::mtObject )
      {
         InitReader(so->GetChild(fi->name), L"", _key, connection);
         break;
      }
   }
}

ISessionObject* OleDataSource::Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session)
{
   GRServer::Format* fmt = session->RegisterType(typeDef);
   if( fmt == NULL )
      return NULL;

   wchar_t* grp = _wcsdup(groupExpr);

   ISessionObject *so = session->CreateObject(fmt->name, false);
   InitReader(so, stmt, grp, connection);
   free(grp);

   so->Reading(L"", false);
   return so;
}

CDataConnection* GRServer::GetConnection()
{
   if( gvDataSource == NULL )
   {
      gvDataSource = new OleDataSource();
      gvDataSource->TryConnect();
   }
   return gvDataSource->GetConnection();
}

OleDataSource::OleDataSource() : opened(false)
{
}

OleDataSource::~OleDataSource()
{
}

IBinary* OleDataSource::GetServerData(int id)
{
   wchar_t buf[300];
   wsprintf(buf, L"SELECT \"data\" FROM \"ServerData\" WHERE \"id\" = %d", id);
   CCommand<CAccessor<BDataAccessor>> rs;
   HRESULT hr = rs.Open(connection, buf);
   if( FAILED(hr) )
      return NULL;

   hr = rs.MoveFirst();
   if( FAILED(hr) || rs.status != DBSTATUS_S_OK )
      return NULL;

   ULONG rb;
   Binary *ret = new Binary();
   BYTE *pb = ret->Alloc(rs.length);
   rs.data->Read(pb, rs.length, &rb);
	rs.FreeRecordMemory();
   rs.Close();
   
   return new MemoryBinary(ret);
}

bool OleDataSource::PutServerData(int id, const Binary& b)
{
   wchar_t buf[300];
   wsprintf(buf, L"SELECT \"data\", \"id\" FROM \"ServerData\" WHERE \"id\" = %d", id);

   CDBPropSet ps(DBPROPSET_ROWSET);
   CCommand<CAccessor<BDataWriteAccessor>> rs;

   ps.AddProperty(DBPROP_IRowsetChange, true);
   ps.AddProperty(DBPROP_UPDATABILITY, DBPROPVAL_UP_CHANGE | DBPROPVAL_UP_INSERT);
   HRESULT hr = rs.Open(connection, buf, &ps, NULL, DBGUID_DEFAULT, true, 1);
   if( FAILED(hr) )
      return false;

   BinaryWrapper bw(b);
   StreamWriter sw(bw);
   hr = rs.MoveFirst();
   if( hr == S_OK )
   {
      if( rs.status == DBSTATUS_S_OK )
         rs.data->Release();
      rs.length = b.Size();
      rs.status = DBSTATUS_S_OK;
      rs.data = &sw;
      hr = rs.SetData();
   } else
   {
      rs.length = b.Size();
      rs.status = DBSTATUS_S_OK;
      rs.data = &sw;
      rs.id = id;
      hr = rs.Insert();
   }
   rs.Close();

   return (hr == S_OK);
}

bool OleDataSource::TryConnect()
{
   USES_CONVERSION;

   std::string err("Ошибка инициализации OLEDB: ");
   Config c;
   if( !c.Load(configFile) )
   {
      err += "не могу загрузить настройки из файла [";
      err += W2A(configFile.c_str());
      err += "]";
      gServer->AddError(true, err.c_str());
      return false;
   }

   std::wstring cstr;
   if( !c.MakeConnectionString(&cstr) )
   {
      err += "ошибка создания строки подключения";
      gServer->AddError(true, err.c_str());
      return false;
   }

   //cstr += L"MARS Connection=yes;";
   database = c.initialBase;
   providerName = c.provider;

   HRESULT hr;
   hr = connection.Open(cstr.c_str());
   if( !SUCCEEDED(hr) )
   {
      connection.CloseDataSource();
      if( hr == DB_SEC_E_AUTH_FAILED )
      {
         err += "ошибка аутентификации";
         gServer->AddError(true, err.c_str());
         return false;
      } else
      {
         // check and create base if can
         wstring tbase = c.initialBase;
         wstring newCstr;
         c.initialBase.clear();
         c.MakeConnectionString(&newCstr);
         c.initialBase = tbase;
         
         if( SUCCEEDED(connection.Open(newCstr.c_str())) )
         {
            wstring sql(L"CREATE DATABASE ");
            sql += database;
            if( !GRServer::Execute(connection, sql) )
            {
               err += "ошибка при создании БД ";
               err += W2A(database.c_str());
               gServer->AddError(true, err.c_str(), hr);
               return false;
            }
            connection.CloseDataSource();
            hr = connection.Open(cstr.c_str());
         }
         if( !SUCCEEDED(hr) )
         {
            err += "ошибка подключения с кодом %X";
            gServer->AddError(true, err.c_str(), hr);
            return false;
         }
      }
   }

   opened = true;
   return true;
}

bool OleDataSource::IsTableExists(const std::wstring& table)
{
   CTables tbl;
   tbl.Open(connection, database.c_str(), NULL, table.c_str());
   return (tbl.MoveFirst() == S_OK);
}

void GRServer::PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting)
{
   const std::wstring& str = (*_str.begin() == L'"') ? _str.substr(1, _str.size()-2) : _str;

   wstring::size_type lastPos = str.find_first_not_of(L",", 0);
   wstring::size_type pos     = str.find_first_of(L",", lastPos);

   while (string::npos != pos || string::npos != lastPos)
   {
      wstring::size_type size = pos - lastPos;
      wstring::size_type start = lastPos;

      const std::wstring& vstr = str.substr(start, size);
      start = vstr.find_first_not_of(L' ');
      size = vstr.find_last_not_of(L' ');
      if( size >= start )
      {
         if( quoting )
         {
            wstring tstr;
            QuoteString(&tstr, vstr.substr(start, size - start + 1));
            fields->push_back(tstr);
         } else
            fields->push_back(vstr.substr(start, size - start + 1));
      }
      
      lastPos = str.find_first_not_of(L",", pos);
      pos = str.find_first_of(L",", lastPos);
   }
}

static DBTYPE TypeToDBType(const MemberFormat& format)
{
   switch(format.type)
   {
      case MemberFormat::mtString:
         return DBTYPE_WSTR;

      case MemberFormat::mtDateTime:
         return DBTYPE_I8;

      case MemberFormat::mtNumber:
         return ( format.format.fraction == 0 ) ? DBTYPE_I4 : DBTYPE_R8;   

      case MemberFormat::mtBinary:
         return DBTYPE_BYTES;
   }
   return DBTYPE_NULL;
}

HRESULT OleDataSource::CreateIndex(const std::wstring& tableName, const vector<MemberFormat>& fkFields)
{
   HRESULT hr;
   CComPtr<IIndexDefinition> idxCreate;
   hr = connection.m_session.m_spOpenRowset.QueryInterface(&idxCreate);
   if( !SUCCEEDED(hr) )
      return hr;

   //std::wstring indexName(L"\"fki_");
   //indexName += tableName;
   //indexName += L"\"";

   DBID table;
   DBID *index = NULL;
   table.eKind = DBKIND_NAME;
   table.uName.pwszName = (wchar_t*)tableName.c_str();

   //index.eKind = DBKIND_NAME;
   //index.uName.pwszName = (wchar_t*)indexName.c_str();

   DBID *fields = new DBID[fkFields.size()];
   DBINDEXCOLUMNDESC* clmns = new DBINDEXCOLUMNDESC[fkFields.size()];

   int idx = 0;
   vector<MemberFormat>::const_iterator i = fkFields.begin();
   for( ; i != fkFields.end(); i++, idx++ )
   {
      DBID *cf = fields + idx;
      cf->eKind = DBKIND_NAME;
      cf->uName.pwszName = (wchar_t*)i->name.c_str();

      DBINDEXCOLUMNDESC *cc = clmns + idx;
      cc->pColumnID = cf;
      cc->eIndexColOrder = DBINDEX_COL_ORDER_ASC;
   }

   //hr = idxCreate->CreateIndex(&table, &index, fkFields.size(), clmns, 0, NULL, NULL);
   hr = idxCreate->CreateIndex(&table, NULL, fkFields.size(), clmns, 0, NULL, &index);

   CoTaskMemFree(index);
   delete[] fields;
   delete[] clmns;

   return hr;
}

static void MakePKConstraint(DBCONSTRAINTDESC* cc, DBID *pkList, const vector<wstring>& keyFields)
{
   int idx = 0;
   vector<wstring>::const_iterator i = keyFields.begin();
   for( ; i != keyFields.end(); i++, idx++ )
   {
      DBID *cid = pkList + idx;
      cid->eKind = DBKIND_NAME;
      cid->uName.pwszName = (wchar_t*)i->c_str();
   }
   cc->pConstraintID = NULL;
   cc->ConstraintType = DBCONSTRAINTTYPE_PRIMARYKEY;
   cc->cColumns = keyFields.size();
   cc->rgColumnList = pkList;
   cc->Deferrability = 0;  
   cc->pReferencedTableID = NULL;
   cc->cForeignKeyColumns = 0;
   cc->rgForeignKeyColumnList = NULL;
   cc->pwszConstraintText = NULL;
   cc->UpdateRule = DBUPDELRULE_NOACTION;
   cc->DeleteRule = DBUPDELRULE_NOACTION;
   cc->MatchType = DBMATCHTYPE_NONE;
}

static std::vector<std::wstring>* MakeFKConstraint(DBCONSTRAINTDESC* cc, DBID *fkList, DBID *fkfList, DBID *fTable, const vector<MemberFormat>& fkFields)
{
   // первая строка таблица - остальные поля
   vector<wstring> *str = new vector<wstring> (fkFields.size() + 1);
   
   int idx = 0, pos;
   vector<MemberFormat>::const_iterator i = fkFields.begin();
   for( ; i != fkFields.end(); i++, idx++ )
   {
      std::wstring tn;
      if( idx == 0 ) // get table name
      {
         pos = i->name.find_last_of(L'$');
         QuoteString(&tn, i->name.substr(0, pos));
         str->at(0) = tn;
         fTable->eKind = DBKIND_NAME;
         fTable->uName.pwszName = (wchar_t*)str->at(0).c_str();
      }
      DBID *cid = fkfList + idx;
      cid->eKind = DBKIND_NAME;
      cid->uName.pwszName = (wchar_t*)i->name.c_str();

      cid = fkList + idx;
      pos = i->name.find_last_of(L'$');
      QuoteString(&tn, i->name.substr(pos+1));
      str->at(idx+1) = tn;
      cid->eKind = DBKIND_NAME;
      cid->uName.pwszName = (wchar_t*)str->at(idx+1).c_str();
   }

   cc->pConstraintID = NULL;
   cc->ConstraintType = DBCONSTRAINTTYPE_FOREIGNKEY;
   cc->cColumns = fkFields.size();
   cc->rgColumnList = fkList;
   cc->Deferrability = 0;  
   cc->pReferencedTableID = fTable;
   cc->cForeignKeyColumns = fkFields.size();
   cc->rgForeignKeyColumnList = fkfList;
   cc->pwszConstraintText = NULL;
   cc->UpdateRule = DBUPDELRULE_NOACTION;
   cc->DeleteRule = DBUPDELRULE_CASCADE;
   cc->MatchType = DBMATCHTYPE_NONE;

   return str;
}

static void SetDBColumn(DBCOLUMNDESC *column, const MemberFormat& format, const CDBPropSet& varChar)
{
   column->pwszTypeName = NULL;
   column->pTypeInfo = NULL;
   column->pclsid = NULL;
   if( format.type == MemberFormat::mtString || format.type == MemberFormat::mtBinary )
   {
      column->ulColumnSize = (format.type == MemberFormat::mtString) ? MAX_STRING_LENGTH : 10240 * 1024;
      column->cPropertySets = 1;
      column->rgPropertySets = (DBPROPSET*)&varChar;
   } else
   {
      column->cPropertySets = 0;
      column->rgPropertySets = NULL;
   }
   column->dbcid.eKind = DBKIND_NAME;
   column->dbcid.uName.pwszName = (wchar_t*)format.name.c_str();
   if( format.type == MemberFormat::mtNumber )
      column->bPrecision = (BYTE)format.format.fraction;
   column->bScale = 0;

   column->wType = TypeToDBType(format);
}

HRESULT OleDataSource::CreateTable(const std::wstring& tableName, const vector<MemberFormat>& fields, const vector<wstring>& keyFields, const vector<MemberFormat>* fkFields)
{
   HRESULT hr;
   CComPtr<ITableDefinitionWithConstraints> tblCreate;
   hr = connection.m_session.m_spOpenRowset.QueryInterface(&tblCreate);
   if( !SUCCEEDED(hr) )
      return hr;

   DBID table;
   int constraintCount = 0;
   DBCONSTRAINTDESC constraints[2] = {0}; // pk & fk
   vector<wstring> *fkStrings = NULL;
   DBCOLUMNDESC *columns;
   DBID fTable;
   DBID *pkList = NULL;
   DBID *fkList = NULL;
   DBID *fkfList = NULL;

   table.eKind = DBKIND_NAME;
   table.uName.pwszName = (wchar_t*)tableName.c_str();
   
   // load columns
   columns = new DBCOLUMNDESC[fields.size()];
   CDBPropSet varChar(DBPROPSET_COLUMN);
   varChar.AddProperty(DBPROP_COL_FIXEDLENGTH, false);

   memset(columns, 0, sizeof(DBCOLUMNDESC) * fields.size());
   
   int idx = 0;
   vector<MemberFormat>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++, idx++ )
      SetDBColumn(columns + idx, *i, varChar);

   // load pk
   std::vector<std::wstring> kf;
   if( keyFields.size() > 0 )
   {
      vector<wstring>::const_iterator fi = keyFields.begin();
      for( ; fi != keyFields.end(); fi++ )
      {
         std::wstring name;
         QuoteString(&name, *fi);
         kf.push_back(name);
      }

      DBCONSTRAINTDESC *cc = constraints + constraintCount;      
      pkList = new DBID[kf.size()];

      MakePKConstraint(cc, pkList, kf);

      constraintCount++;
   }

   // load fk
   if( fkFields != NULL && fkFields->size() > 0 )
   {
      DBCONSTRAINTDESC *cc = constraints + constraintCount;
      fkList = new DBID[fkFields->size()];
      fkfList = new DBID[fkFields->size()];

      fkStrings = MakeFKConstraint(cc, fkList, fkfList, &fTable, *fkFields);

      constraintCount++;
   }

   hr = tblCreate->CreateTableWithConstraints(NULL, &table, fields.size(), columns, constraintCount, constraints, IID_NULL, 0, NULL, NULL, NULL); 

   delete[] columns;
   delete[] pkList;
   delete[] fkList;
   delete[] fkfList;
   delete fkStrings;

   if( SUCCEEDED(hr) && fkFields != NULL && fkFields->size() > 0 )
      hr = CreateIndex(tableName, *fkFields);

   return hr;
}

static const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format)
{
   wchar_t wbuf[100];
   switch(format.format.type)
   {
   case MemberFormat::mtString:
      wsprintf(wbuf, L"VARCHAR2(%d)", (format.width == 0) ? DEFAULT_STRING_LENGTH : format.width);
      buf->assign(wbuf);
      break;
   case MemberFormat::mtNumber:
      wsprintf(wbuf, L"NUMBER(*, %d)", format.format.format.fraction);
      buf->assign(wbuf);
      break;
   case MemberFormat::mtDateTime:
      buf->assign(L"NUMBER(*)");
      break;
   case MemberFormat::mtBinary:
      buf->assign(L"BLOB");
      break;
   }
   return buf->c_str();
}

static HRESULT CreateOracleTable(CSession& session, const IObjectData& objDef)
{
   vector<wstring> keyFields;
   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second, false);

   std::wstring text(L"CREATE TABLE \"");
   text += objDef.tableName;
   text += L"\" (";

   vector<IObjectData::Field> fields;
   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      fields.push_back(*fi);
   }
   CVector<IObjectData::Field>* fkFields = NULL;
   CVector<MemberFormat>* fkMFields = NULL;
   objDef.LoadFK(&fkMFields, &fkFields);
   //LoadFK(&fkFields, objDef);

   fields.insert(fields.end(), fkFields->begin(), fkFields->end());

   bool assigned = false;
   std::wstring buf;
   vector<IObjectData::Field>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const wchar_t *pType = TypeToString(&buf, *i);
      if( *pType == L'\0' ) continue;

      if( assigned ) text += L",";
      else assigned = true;

      text += L"\"";
      text += i->format.name;
      text += L"\" ";
      text += pType;
      text += L" ";
   }

   if( keyFields.size() > 0 )
   {
      text += L", CONSTRAINT pk_";
      text += objDef.tableName;
      text += L" PRIMARY KEY (";
      vector<wstring>::const_iterator ki = keyFields.begin();
      while( ki != keyFields.end() )
      {
         if( ki != keyFields.begin() )
            text += L",";
         text += L"\"";
         text += (*ki);
         text += L"\"";
         ki++;
      }
      text += L")";
   }

   CString *indexText = NULL;
   if( fkFields->size() > 0 )
   {
      CString *fkText;
      objDef.CreateFKConstraint(&fkText, &indexText, *fkMFields);

      text += L", ";
      text += (const std::wstring&)(*fkText);
      delete fkText;
   }
   //AddFKConstraint(&text, &indexText, objDef.tableName, fkFields);

   text += L")";

   CCommand<CNoAccessor, CNoRowset> cmd;
   HRESULT hr = cmd.Open(session, text.c_str());
   cmd.Close();

   if( indexText != NULL && !indexText->empty() )
   {
      hr = cmd.Open(session, indexText->c_str());
      cmd.Close();
   }

   delete fkFields;
   delete fkMFields;
   delete indexText;

   return hr;
}

HRESULT OleDataSource::CreateTable(const IObjectData& objDef)
{
   if( IsOracle() )
   {
      return CreateOracleTable(connection.m_session, objDef);
   }
   vector<wstring> keyFields;
   vector<MemberFormat> fields;
   vector<MemberFormat> fkFields;

   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second, false);

   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      MemberFormat mf(fi->format);
      QuoteString(&mf.name);
      fields.push_back(mf);
   }

   CVector<MemberFormat>* sfkFields = NULL;
   objDef.LoadFK(&sfkFields);
   CVector<MemberFormat>::const_iterator sfi = sfkFields->begin();
   for( ; sfi != sfkFields->end(); sfi++ )
   {
      MemberFormat mf(*sfi);
      QuoteString(&mf.name);
      fields.push_back(mf);
      fkFields.push_back(mf);
   }
   delete sfkFields;

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      MemberFormat mf;
      QuoteString(&mf.name, SENDED_FIELDS);
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      fields.push_back(mf);
   }

   wstring tableName;
   QuoteString(&tableName, objDef.tableName);
   return CreateTable(tableName, fields, keyFields, &fkFields);
}

static MemberFormat::MemberType ToMemberType(USHORT type)
{
   switch(type)
   {
   case DBTYPE_I2:
   case DBTYPE_I4:
   case DBTYPE_R4:
   case DBTYPE_R8:
   case DBTYPE_CY:
   case DBTYPE_I8:
   case DBTYPE_UI8:
   case DBTYPE_NUMERIC:
   case DBTYPE_VARNUMERIC:
      return MemberFormat::mtNumber;

   case DBTYPE_STR:
   case DBTYPE_WSTR:
      return MemberFormat::mtString;

   //case DBTYPE_DBTIMESTAMP:
   //   return MemberFormat::mtDateTime;

   case DBTYPE_BYTES:
      return MemberFormat::mtBinary;
   default:
      return MemberFormat::mtNone;
   }
}

void OleDataSource::GetTableFields(OleDataSource::FieldSet *fields, const std::wstring table)
{
   HRESULT hr;
   CColumns clmns;

   hr = clmns.Open(connection, NULL, NULL, table.c_str());
   hr = clmns.MoveFirst();
   while( hr == S_OK )
   {
      MemberFormat::MemberType type = ToMemberType(clmns.m_nDataType);
      if( type != MemberFormat::mtNone )
      {
         MemberFormat mf;
         mf.name = clmns.m_szColumnName;
         mf.type = type;
         mf.flags = 0;
         if( type == MemberFormat::mtNumber )
            mf.format.fraction = clmns.m_nNumericPrecision;
         else if( type == MemberFormat::mtDateTime )
            mf.format.dateFormat = MemberFormat::Stamp;

         fields->insert(mf);
      }
      hr = clmns.MoveNext();
   }
}

HRESULT OleDataSource::AlterTable(const IObjectData& objDef, const std::vector<MemberFormat>& added)
{
   if( gServer->GetConfig().Debug() == IErrorLogger::Full )
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "ALter table %s", W2A(objDef.tableName.c_str()));
   }

   CComPtr<ITableDefinition> tblAlter;
   HRESULT hr = connection.m_session.m_spOpenRowset.QueryInterface(&tblAlter);
   if( !SUCCEEDED(hr) )
      return hr;

   std::wstring tableName;
   DBID table;
   QuoteString(&tableName, objDef.tableName);

   table.eKind = DBKIND_NAME;
   table.uName.pwszName = (wchar_t*)tableName.c_str();

   bool ret = true;
   CDBPropSet varChar(DBPROPSET_COLUMN);
   varChar.AddProperty(DBPROP_COL_FIXEDLENGTH, false);

   vector<MemberFormat>::const_iterator i = added.begin();
   for( ; (SUCCEEDED(hr) != 0) && i != added.end(); i++ )
   {
      DBCOLUMNDESC desc;

      SetDBColumn(&desc, *i, varChar);
      hr = tblAlter->AddColumn(&table, &desc, NULL);
   }

   return hr;
}

HRESULT OleDataSource::CheckTable(const IObjectData& objDef)
{
   HRESULT res = S_OK;

   FieldSet fields;
   GetTableFields(&fields, objDef.tableName);

   std::vector<MemberFormat> added;

   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      if( fields.find(fi->format) == fields.end() )
      {
         MemberFormat mf(fi->format);
         QuoteString(&mf.name);
         added.push_back(mf);
      }
   }

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      MemberFormat mf;
      mf.name = SENDED_FIELDS;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      if( fields.find(mf) == fields.end() )
      {
         QuoteString(&mf.name);
         added.push_back(mf);
      }
   }

   if( added.size() > 0 )
      res = AlterTable(objDef, added);

   return res;
}

void GRServer::AddErrorsToLog(bool isCritical, HRESULT err)
{
   USES_CONVERSION;
   HRESULT hr;
   LCID lcid = GetUserDefaultLCID();

   wstring errStr(L"[OleDB]");

   if( IsOracle() )
   {
      const CLSID CLSID_OraOLEDBErrorLookup = {0x3FC8E6E4,0x53FF,0x11D2,{0xBB,0x7D,0x00,0xC0,0x4F,0xA3,0x00,0x80}};
      CComPtr<IErrorLookup> pIErrorLookup;

      hr = CoCreateInstance(CLSID_OraOLEDBErrorLookup, NULL, CLSCTX_INPROC_SERVER, IID_IErrorLookup, (void **)&pIErrorLookup);
      if( !SUCCEEDED(hr) )
         return;

      BSTR src = NULL, desc = NULL;
      hr = pIErrorLookup->GetErrorDescription(err, 0, NULL, lcid, &src, &desc);
      if( SUCCEEDED(hr) )
         errStr += desc;

      SysFreeString(src);
      SysFreeString(desc);
   } else
   {
      ULONG recs;
      CDBErrorInfo cerr;

      hr = cerr.GetErrorRecords(&recs);
      if( !SUCCEEDED(hr) )
         return;

      while(recs-- > 0)
      {
         BSTR desc;
         if( SUCCEEDED(cerr.GetAllErrorInfo(recs, lcid, &desc)) )
         {
            errStr += L" ";
            errStr += desc;
            SysFreeString(desc);
         }
      }
   }

   gServer->AddError(isCritical, W2A(errStr.c_str()));

}

bool OleDataSource::CheckDBFormat(GRServer::IObjectDef* objDefs)
{
   HRESULT res = S_OK;
   //vector<wstring> names;
   CVector<CString> *names = NULL;
   objDefs->GetObjectsName(&names, IObjectDef::Internal);

   bool logging = ( gServer->GetConfig().Debug() == IErrorLogger::Full );
   USES_CONVERSION;

   CVector<CString>::const_iterator i = names->begin();
   for( ; res == S_OK && i != names->end(); i++ )
   {
      const IObjectData* odef = objDefs->Get((const std::wstring&)*i);
      if( logging )
         gServer->AddLog(IErrorLogger::Full, "Check table %s", W2A(odef->tableName.c_str()));

      if( !IsTableExists(odef->tableName) )
      {
         if( logging )
            gServer->AddLog(IErrorLogger::Full, "Create table %s", W2A(odef->tableName.c_str()));
         res = CreateTable(*odef);
      } else
      {
         res = CheckTable(*odef);
      }

      if( !SUCCEEDED(res) )
         AddErrorsToLog(false, res);
   }

   delete names;
   return (res == S_OK);
}

bool OleDataSource::Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config)
{
   if( !TryConnect() )
      return false;

   bool res;

   res = CheckDBFormat(objDef);

   if( res && !IsTableExists(L"ServerData") )
   {
      std::wstring stmt;
      if( IsOracle() )
      {
         stmt = L"CREATE TABLE \"ServerData\"(\"id\" NUMBER, \"data\" BLOB, CONSTRAINT pkServerData PRIMARY KEY (\"id\") )";
      } else
      {
         stmt = L"CREATE TABLE \"ServerData\"(\"id\" INTEGER, \"data\" IMAGE, CONSTRAINT pkServerData PRIMARY KEY (\"id\") )";
      }
      res = GRServer::Execute(connection.m_session, stmt);
   }

   return res;
}

void OleDataSource::Close()
{
   if( opened )
   {
      connection.CloseDataSource();
      opened = false;
   }
}

IDataSource::IReader* OleDataSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{   
   return GRServer::CreateReader(parameters, object, (CDataConnection&)(connection));
}

IDataSource::IWriter* OleDataSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return GRServer::CreateWriter(parent, parameters, object, (CDataConnection&)(connection));
}

IDataSource::IRemover* OleDataSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, (CDataConnection&)(connection));
}

IDataSource::ISelector* OleDataSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   return GRServer::CreateSelector(object, (CDataConnection&)(connection));
}

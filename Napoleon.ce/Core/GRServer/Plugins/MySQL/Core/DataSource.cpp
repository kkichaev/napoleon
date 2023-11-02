/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * MySQLDB plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include "MySQLDrv.h"
#include <ServerDefs.h>
#include "Binder.h"
#include "QuerySource.h"

using namespace GRServer;
using namespace std;

const wchar_t* GRServer::SENDED_FIELDS = L"$_objSended";

class MySQLDataSource : public IInternalDataSource
{
public:
   MySQLDataSource();
   ~MySQLDataSource();

   virtual IBinary* GetServerData(int id);
   virtual bool    PutServerData(int id, const Binary& b);

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config);
   virtual void    Close();

   virtual const wchar_t* Name() const { return L"MySQLSourceInternal"; }

   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const;

	virtual bool Execute(const wchar_t* stmt, ISession* session);
   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

   bool TryConnect();

   MYSQL* GetConnection() { return connection; }

protected:
   bool opened;
   MYSQL* connection;
   std::string database;
   
   WORD stringLength;

protected:
   bool IsTableExists(const std::wstring& table);

   bool CheckDBFormat(GRServer::IObjectDef* objDef);
   bool CreateTable(const IObjectData& objDef);
   bool CheckTable(const IObjectData& objDef);

   struct MFCmp
   {
	bool operator()(const IObjectData::Field& _Left, const IObjectData::Field& _Right) const
		{	// apply operator< to operands
         return (_Left.format.name.compare(_Right.format.name) < 0);
		}
   };

   typedef std::set<IObjectData::Field, MFCmp> FieldSet;

   void GetTableFields(FieldSet *fields, const std::wstring table);
   bool AlterTable(const IObjectData& objDef, const std::vector<IObjectData::Field>& added);
};

MySQLDataSource* gvDataSource;

MYSQL* GRServer::GetConnection()
{
   return (gvDataSource == NULL) ? NULL : gvDataSource->GetConnection();
}

const wchar_t* GRServer::QuoteString(std::wstring* dest, char sym)
{
   wchar_t symW = (wchar_t)sym;
   if( *dest->begin() != symW) dest->insert(dest->begin(), symW);
   if( *dest->rbegin() != symW) dest->append(1, symW);
   return dest->c_str();
}

const char* GRServer::QuoteString(std::string* dest, char sym)
{
   if( *dest->begin() != sym) dest->insert(dest->begin(), sym);
   if( *dest->rbegin() != sym) dest->append(1, sym);
   return dest->c_str();
}

const char* GRServer::QuoteString(std::string* dest, const std::wstring& src, char sym)
{
   USES_CONVERSION;
   const char* srcA = W2U(src.c_str());
   
   if( *srcA != sym )
      dest->append(1, sym);
   dest->append(srcA);
   if( *dest->rbegin() != sym) 
      dest->append(1, sym);
   return dest->c_str();
}

const wchar_t* GRServer::QuoteString(std::wstring* dest, const std::wstring& src, char sym)
{
   wchar_t symW = (wchar_t)sym;
   if( *src.begin() != symW) dest->append(1, symW);
   dest->append(src);
   if( *dest->rbegin() != symW) dest->append(1, symW);
   return dest->c_str();
}

const char* GRServer::QuoteString(std::string* dest, const std::string& src, char sym)
{
   if( *src.begin() != sym) dest->append(1, sym);
   dest->append(src);
   if( *dest->rbegin() != sym) dest->append(1, sym);
   return dest->c_str();
}

IInternalDataSource* GRServer::CreateInternalDS()
{
   if( gvDataSource == NULL )
      gvDataSource = new MySQLDataSource();
   return gvDataSource;
}

bool GRServer::Execute(MYSQL *mysql, const std::string& sql)
{
   int res = mysql_query(mysql, sql.c_str());
   return (res == 0);
}

MySQLDataSource::MySQLDataSource() : opened(false)
{
}

MySQLDataSource::~MySQLDataSource()
{
}

bool MySQLDataSource::Execute(const wchar_t* stmt, ISession* session)
{
	USES_CONVERSION;
	return (!opened) ? false : GRServer::Execute(connection, W2U(stmt));
}

IBinary* MySQLDataSource::GetServerData(int id)
{
   char buf[300];
   sprintf(buf, "SELECT data FROM ServerData WHERE id = %d", id);
   if( mysql_query(connection, buf) != 0 )
      return NULL;
   
   MYSQL_RES *result = mysql_store_result(connection);
   if( result == NULL )
      return NULL;

   Binary *ret = NULL;
   MYSQL_ROW row = mysql_fetch_row(result);
   if( row != NULL )
   {
      unsigned long* length = mysql_fetch_lengths(result);
      ret = new Binary();
      BYTE *pb = ret->Alloc(*length);
      memcpy(pb, row[0], *length);
   }

   mysql_free_result(result);
   return (ret != NULL) ? new MemoryBinary(ret) : NULL;
}

bool MySQLDataSource::PutServerData(int id, const Binary& b)
{
   bool res = false;
   char buf[300];
   sprintf(buf, "INSERT INTO ServerData (id, data) VALUES(%d, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)", id);
   
   MYSQL_STMT    *stmt;
   MYSQL_BIND    bind;

   stmt = mysql_stmt_init(connection);
   if( mysql_stmt_prepare(stmt, buf, strlen(buf)) != 0 )
   {
      AddErrorsToLog(true, connection);
      return false;
   }
   
   DWORD len = b.Size();
   memset(&bind, 0, sizeof(bind));
   bind.buffer_type = MYSQL_TYPE_BLOB;
   bind.buffer = (void*)((const char*)b);
   bind.buffer_length = len;
   bind.is_null = NULL;
   bind.length= &len;

   res = (mysql_stmt_bind_param(stmt, &bind) == 0) && (mysql_stmt_execute(stmt) == 0);

   mysql_stmt_close(stmt);

   return res;
}

bool MySQLDataSource::TryConnect()
{
   Config c;
   if( !c.Load(configFile) )
   {
      std::string err("Ошибка инициализации MySQL: ");
      err += "не могу загрузить настройки из файла [";
      err += configFile.c_str();
      err += "]";
      gServer->AddError(true, err.c_str());
      return false;
   }
   stringLength = c.defaultStringLength;

   connection = mysql_init(NULL);
   if( connection == NULL )
   {
      AddErrorsToLog(true, connection);
      return false;
   }

   database = c.base;
   if( mysql_real_connect(connection, c.host.c_str(), c.login.c_str(), c.password.c_str(), NULL, c.port, NULL, 0) == NULL )
   {
      AddErrorsToLog(true, connection);
      return false;
   }
   
   if( mysql_select_db(connection, database.c_str()) != 0 )
   {
      char buf[1000];
      sprintf(buf, "CREATE DATABASE %s DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci", database.c_str());
		if( !GRServer::Execute(connection, buf) || mysql_select_db(connection, database.c_str()) != 0 )
      {
         AddErrorsToLog(true, connection);
         return false;
      }
   }

   opened = GRServer::Execute(connection, "SET NAMES 'utf8'");
	if (opened)
		GRServer::Execute(connection, "set SESSION sql_mode = 'ANSI_QUOTES'");
   return true;
}

bool MySQLDataSource::IsTableExists(const std::wstring& table)
{
   std::string sql("SHOW TABLES LIKE ");
   QuoteString(&sql, table, '\'');

   bool ret = false;
   if( mysql_query(connection, sql.c_str()) == 0 )
   {
      MYSQL_RES *res = mysql_store_result(connection);
      ret = (res != NULL && mysql_num_rows(res) > 0);
      mysql_free_result(res);
   }
   return ret;
}

void GRServer::PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting)
{
   const std::wstring& str = (*_str.begin() == L'`') ? _str.substr(1, _str.size()-2) : _str;

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

//static DBTYPE TypeToDBType(const MemberFormat& format)
//{
//   switch(format.type)
//   {
//      case MemberFormat::mtString:
//         return DBTYPE_WSTR;
//
//      case MemberFormat::mtDateTime:
//         return DBTYPE_I8;
//
//      case MemberFormat::mtNumber:
//         return ( format.format.fraction == 0 ) ? DBTYPE_I4 : DBTYPE_R8;   
//
//      case MemberFormat::mtBinary:
//         return DBTYPE_BYTES;
//   }
//   return DBTYPE_NULL;
//}

static void PKToList(std::vector<std::wstring>* fields, const std::wstring& str)
{
   wstring::const_iterator si = str.begin(), ei = str.end();
   if( *si == L'"' ) si++;

   wstring f;
   for( ; si != ei; si++ )
   {
      wchar_t sym = *si;

      if( sym == L'"' ) break;
      if( sym == L',' )
      {
         if( !f.empty() )
         {
            long start = f.find_first_not_of(L' ');
            fields->push_back(f.substr(start, f.size() - start));
         }
         f.clear();
      } else
         f.append(1, sym);
   }
   if( !f.empty() )
   {
      long start = f.find_first_not_of(' ');
      fields->push_back(f.substr(start, f.size() - start));
   }
}

static const char* TypeToString(const MemberFormat& format, char *buf, WORD length)
{
   switch(format.type)
   {
      case MemberFormat::mtString:
      {
         sprintf(buf, " VARCHAR(%d) ", length);
         return buf;
      }

      case MemberFormat::mtDateTime:
         return " BIGINT ";

      case MemberFormat::mtNumber:
         return ( format.format.fraction == 0 ) ? " INTEGER " : " REAL ";

      case MemberFormat::mtBinary:
         return " BLOB ";

      default: break;
   }
   return "";
}

bool MySQLDataSource::CreateTable(const IObjectData& objDef)
{
   if( !opened ) return false;

   vector<wstring> keyFields;
   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second);

   USES_CONVERSION;
   const char *tn = W2U(objDef.tableName.c_str());
   std::string text("CREATE TABLE ");
   QuoteString(&text, tn);
   text += " (";

   vector<IObjectData::Field> fields;
   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      fields.push_back(*fi);
   }
   CVector<IObjectData::Field>* fkFields = NULL;
   objDef.LoadFK(NULL, &fkFields);

   fields.insert(fields.end(), fkFields->begin(), fkFields->end());

   bool assigned = false;
   char buf[200];
   vector<IObjectData::Field>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const MemberFormat& format = i->format;
      WORD stringLength = i->width;
      if(stringLength == 0)
         stringLength = this->stringLength;

      const char *pType = TypeToString(format, buf, stringLength);
      if( *pType == '\0' ) continue;

      if( assigned ) text += ",";
      else assigned = true;

      QuoteString(&text, format.name);
      text += pType;
      text += " ";
   }

   if( keyFields.size() > 0 )
   {
      text += ", CONSTRAINT pk_";
      text += tn;
      text += " PRIMARY KEY (";
      vector<wstring>::const_iterator ki = keyFields.begin();
      while( ki != keyFields.end() )
      {
         if( ki != keyFields.begin() )
            text += ",";
         QuoteString(&text, *ki);

         ki++;
      }
      text += ")";
   }

   CString *indexText = NULL;
   CString *fkText = NULL;
   if( fkFields->size() > 0 )
   {
      CVector<MemberFormat> fkFormats;
      for( i=fkFields->begin(); i != fkFields->end(); i++ )
         fkFormats.push_back(i->format);

      objDef.CreateFKConstraint(&fkText, &indexText, fkFormats, '`');
   }

   text += ")";

   bool ret = GRServer::Execute(connection, text);

   if( ret && indexText != NULL && !indexText->empty() )
   {
      GRServer::Execute(connection, W2U(indexText->c_str()));
      
      text = "ALTER TABLE "; QuoteString(&text, tn); text += " ADD ";
      text += W2U(fkText->c_str());
      ret = GRServer::Execute(connection, text);
   }

   delete fkFields;
   delete indexText;
   delete fkText;
   return ret;
}

static void SetFieldType(IObjectData::Field *f, const char* type)
{
   f->width = 0;
   MemberFormat& mf = f->format;
   mf.flags = 0;
   if( _strnicmp(type, "varchar", sizeof("varchar")-1) == 0 )
   {
      mf.type = MemberFormat::mtString;
      sscanf(type + sizeof("varchar")-1, "(%d", &f->width);
   } else if(_strnicmp(type, "char", sizeof("char")-1) == 0 )
   {
      mf.type = MemberFormat::mtString;
      sscanf(type + sizeof("char")-1, "(%d", &f->width);
   } else if( _strnicmp(type, "int", sizeof("int")-1) == 0 || 
        _strnicmp(type, "bigint", sizeof("bigint")-1) == 0
      )
   {
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
   } else if( _strnicmp(type, "double", sizeof("int")-1) == 0 ||
        _strnicmp(type, "real", sizeof("real")-1) == 0
      )
   {
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 8;
   } else if( _strnicmp(type, "blob", sizeof("blob")-1) == 0 )
   {
      mf.type = MemberFormat::mtBinary;
   } else
      mf.type = MemberFormat::mtNone;
}

void MySQLDataSource::GetTableFields(MySQLDataSource::FieldSet *fields, const std::wstring table)
{
   std::string stmt("SHOW COLUMNS IN ");
   QuoteString(&stmt, table);

   if( mysql_query(connection, stmt.c_str()) != NULL )
   {
      return;
   }

   MYSQL_RES* res = mysql_store_result(connection);
   if( res != NULL )
   {
      USES_CONVERSION;
      MYSQL_ROW row;
      while( (row = mysql_fetch_row(res)) != NULL )
      {
         IObjectData::Field f;
         MemberFormat& mf = f.format;
         mf.name = U2W(row[0]);
         SetFieldType(&f, row[1]);

         fields->insert(f);
      }
   }
   mysql_free_result(res);
}

bool MySQLDataSource::AlterTable(const IObjectData& objDef, const std::vector<IObjectData::Field>& added)
{
   std::string stmt("ALTER TABLE ");
   QuoteString(&stmt, objDef.tableName);

   char buf[200];
   std::vector<IObjectData::Field>::const_iterator i = added.begin();
   for( ; i != added.end(); i++ )
   {
      WORD stringLength = i->width;
      if(stringLength == 0)
         stringLength = this->stringLength;

      if( i != added.begin() )
         stmt += ", ";
      stmt += "ADD COLUMN ";
      QuoteString(&stmt, i->format.name);
      stmt += " ";
      stmt += TypeToString(i->format, buf, stringLength);
   }

   return GRServer::Execute(connection, stmt.c_str());
}

bool MySQLDataSource::CheckTable(const IObjectData& objDef)
{
   bool res = true;

   FieldSet fields;
   GetTableFields(&fields, objDef.tableName);

   std::vector<IObjectData::Field> added;

   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      if( fields.find(*fi) == fields.end() )
      {
         IObjectData::Field f(*fi);
         QuoteString(&f.format.name);
         added.push_back(f);
      }
   }

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      IObjectData::Field f;
      MemberFormat& mf = f.format;
      mf.name = SENDED_FIELDS;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      f.width = 0;
      if( fields.find(f) == fields.end() )
      {
         QuoteString(&mf.name);
         added.push_back(f);
      }
   }

   if( added.size() > 0 )
      res = AlterTable(objDef, added);

   return res;
}

void GRServer::AddErrorsToLog(bool isCritical, MYSQL* conn, IErrorLogger::DebugLevel level)
{
   if( level == IErrorLogger::None )
      gServer->AddError(isCritical, "Ошибка %u: %s ", mysql_errno(conn), mysql_error(conn));
   else
      gServer->AddLog(level, "Ошибка %u: %s ", mysql_errno(conn), mysql_error(conn));
}

void GRServer::AddErrorsToLog(MYSQL_STMT* conn, IErrorLogger::DebugLevel level)
{
	if (level == IErrorLogger::None)
		gServer->AddError(false, "Ошибка %u: %s ", mysql_stmt_errno(conn), mysql_stmt_error(conn));
	else
		gServer->AddLog(level, "Ошибка %u: %s ", mysql_stmt_errno(conn), mysql_stmt_error(conn));
}

bool MySQLDataSource::CheckDBFormat(GRServer::IObjectDef* objDefs)
{
   bool res = true;
   //vector<wstring> names;
   CVector<CString> *names = NULL;
   objDefs->GetObjectsName(&names, IObjectDef::Internal);

   CVector<CString>::const_iterator i = names->begin();
   for( ; res && i != names->end(); i++ )
   {
      const IObjectData* odef = objDefs->Get((const std::wstring&)*i);
      if( !IsTableExists(odef->tableName) )
         res = CreateTable(*odef);
      else
         res = CheckTable(*odef);
   
      if( !res )
         AddErrorsToLog(false, connection);
   }

   delete names;
   return res;
}

class ServerDataDef : public IObjectData
{
public:
   ServerDataDef();

   virtual const Field* FindField(const std::wstring& name) const { return NULL; }
   virtual bool LoadFK(CVector<MemberFormat>** formats, CVector<Field> **fields = NULL) const
   { 
      if( formats != NULL )
         *formats = new CVector<MemberFormat>();
      if( fields != NULL )
         *fields = new CVector<Field>();
      return false;
   }
   virtual void CreateFKConstraint(CString** fktext, CString** indexText, const CVector<MemberFormat>& fields, wchar_t escape = L'"') const {}
};

ServerDataDef::ServerDataDef()
{
   name = tableName = L"ServerData";

   members[PRIMARY_KEY_STR] = L"id";
   
   Field mf;
   mf.format.name = L"id";
   mf.data = L"id";
   mf.format.type = MemberFormat::mtNumber;
   mf.format.format.fraction = 0;
   mf.format.flags = 0;
   fields.insert(mf);

   mf.format.name = L"data";
   mf.data = L"data";
   mf.format.type = MemberFormat::mtBinary;
   fields.insert(mf);
}

bool MySQLDataSource::Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config)
{
   if( !TryConnect() )
      return false;

   bool res;

   res = CheckDBFormat(objDef);
   if( res && !IsTableExists(L"ServerData") )
   {
      ServerDataDef sdd;
      res = CreateTable(sdd);
   }

   return res;
}

void MySQLDataSource::Close()
{
   if( opened )
   {
      mysql_close(connection);
      opened = false;
   }
}

static void InitReader(ISessionObject* so, const wchar_t* expr, wchar_t *_key, MYSQL *db)
{
   QuerySourceCreator qs;
   ObjectSource* src = so->GetSource();
   ISessionObject* parent = so->Parent();
   if( parent == NULL )
   {
      std::wstring stmt(expr);
      src->reader = new QueryReader(db, stmt, *so, false);
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
   for( ; fi != obj->format->end(); fi++ )
   {
      if( fi->type == MemberFormat::mtObject )
      {
         InitReader(so->GetChild(fi->name), L"", _key, db);
         break;
      }
   }
}

ISessionObject* MySQLDataSource::Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session)
{
   GRServer::Format* fmt = session->RegisterType(typeDef, true);
   if( fmt == NULL )
      return NULL;

   wchar_t* grp = _wcsdup(groupExpr);

   ISessionObject *so = session->CreateObject(fmt->name, true);
   InitReader(so, stmt, grp, connection);
   free(grp);

   so->Reading(L"", false);
   return so;
}

IDataSource::IReader* MySQLDataSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{ 
   return GRServer::CreateReader(parameters, object, connection);
}

IDataSource::IWriter* MySQLDataSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return GRServer::CreateWriter(parent, parameters, object, connection);
}

IDataSource::IRemover* MySQLDataSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, connection);
}

IDataSource::ISelector* MySQLDataSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;
   //return GRServer::CreateSelector(object, const_cast<CDataConnection&>(connection));
}

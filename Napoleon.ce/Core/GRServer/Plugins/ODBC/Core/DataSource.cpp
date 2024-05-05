/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include "Source.h"
#include <atldbsch.h>
#include <ServerDefs.h>

#include "QuerySource.h"
#include "Binder.h"
#include "isessobj.h"

using namespace GRServer;
using namespace std;

const wchar_t* GRServer::SENDED_FIELDS = L"$_objSended";
const wchar_t* GRServer::ORDERED_FIELD = L"_objOrdered";

#define SSPROP_INIT_MARSCONNECTION               16
extern const GUID OLEDBDECLSPEC DBPROPSET_SQLSERVERDBINIT        = {0x5cf4ca10,0xef21,0x11d0,{0x97,0xe7,0x0,0xc0,0x4f,0xc2,0xad,0x98}};

class ODBCDataSource : public IInternalDataSource
{
public:
	ODBCDataSource(bool noCheckFormat);
   ~ODBCDataSource();

   virtual IBinary* GetServerData(int id);
   virtual bool    PutServerData(int id, const Binary& b);

   virtual bool    Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config);
   virtual void    Close();

   virtual const wchar_t* Name() const { return L"ODBCSourceInternal"; }

   virtual IDataSource::IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter*    CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IRemover*   CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const;

   virtual bool Execute(const wchar_t* stmt, ISession* session);

   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session);

   ODBCFlavor* GetFlavor() const { return flavor; }
   SQLHDBC GetHDBC() const;
	SQLHDBC GetHDBC_Wr() const;

   bool TryConnect();
   bool Reconnect();
   bool InitConnection();

   bool DoConnect(GRServer::IObjectDef* objDef);

protected:
   mutable bool opened;
   bool connecting;
	bool noCheckFormat;
   SQLHENV hEnv;
   SQLHDBC hDbc;
	
	bool connectionForWrite;
	SQLHDBC hDbcWr;
	
	ODBCFlavor* flavor;
   std::wstring connectStr;

protected:
   bool IsTableExists(const std::wstring& table);
   bool IsProcExists(const std::wstring& procName);
	bool IsIndexExists(const std::wstring& index);

   bool CreateTable(const IObjectData& objDef);

   bool CheckDBFormat(GRServer::IObjectDef* objDef);
   
   void GetTableFields(FieldSet *fields, const std::wstring table);
   bool AlterTable(const IObjectData& objDef, const std::vector<IObjectData::Field>& added);
	bool CreateIndex(const std::wstring& tableName, const std::wstring& fields, bool unique);

	bool CheckTable(FieldSet *fields, const IObjectData& objDef);
	bool MakeUpsertProc(const FieldSet &fields, const IObjectData& objDef);
};

static void AddOrderedField(vector<IObjectData::Field>& fields, const IObjectData& objDef)
{
   if (objDef.IsOrderedSource())
   {
      IObjectData::Field f;
      MemberFormat& mf = f.format;
      mf.name = ORDERED_FIELD;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      f.width = 0;
      fields.push_back(f);
   }
}

bool GRServer::AddOrderedField(IObjectData::Fields& fields, const IObjectData& objDef)
{
   bool ret = false;
   if (objDef.IsOrderedSource())
   {
      IObjectData::Field f;
      MemberFormat& mf = f.format;
      mf.name = ORDERED_FIELD;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      f.width = 0;
      fields.insert(f);

      ret = true;
   }
   return ret;
}
//static HANDLE hStmtSem = NULL;
//static DWORD reqStmtCount = 0;
//void GRServer::InitSTMTSemaphore(int count)
//{
//	if (count > 0)
//		hStmtSem = CreateSemaphore(NULL, count, count, NULL);
//}
//
//void GRServer::RequestSTMT()
//{
//	if (hStmtSem == NULL)
//		return;
//
//	InterlockedIncrement((LONG*)&reqStmtCount);
//	gServer->AddLog(IErrorLogger::Full, "req_stmt %d", reqStmtCount);
//
//	WaitForSingleObject(hStmtSem, INFINITE);
//}
//
//void GRServer::ReleaseSTMT()
//{
//	if (hStmtSem == NULL)
//		return;
//
//	InterlockedDecrement((LONG*)&reqStmtCount);
//	LONG prev;
//	ReleaseSemaphore(hStmtSem, 1, &prev);
//	gServer->AddLog(IErrorLogger::Full, "dec_stmt %d", prev);
//}

#ifndef SQL_COPT_SS_MARS_ENABLED
#define SQL_COPT_SS_MARS_ENABLED 1224
#endif

#ifndef SQL_MARS_ENABLED_YES
#define SQL_MARS_ENABLED_YES (SQLPOINTER)1
#endif

class MSSQLFlavor : public ODBCFlavor
{
public:
   MSSQLFlavor() {}
   ~MSSQLFlavor() {}

   virtual void SetDBCAttribites(SQLHDBC hDbc)
   {
      int rc = SQLSetConnectAttr(hDbc, SQL_COPT_SS_MARS_ENABLED, SQL_MARS_ENABLED_YES, SQL_IS_UINTEGER);
      if( rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO )
      {
         AddErrorsToLog(true, SQL_HANDLE_DBC, hDbc);
      }
		//rc = SQLSetConnectAttr(hDbc, SQL_ATTR_TXN_ISOLATION, (SQLPOINTER)SQL_TXN_SERIALIZABLE, SQL_IS_UINTEGER);
		//if (rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO)
		//{
		//	AddErrorsToLog(true, SQL_HANDLE_DBC, hDbc);
		//}
	}

   virtual const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength);
	virtual void MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists);
	virtual bool GrantExecuteToUpsertProc(std::wstring* stmt, const std::wstring& procName);
	virtual void ChangeNumberType(HDBC hDbc, const std::wstring& tableName, const std::vector<IObjectData::Field>& fields);
};

class FireBirdFlavor : public ODBCFlavor
{
public:
	FireBirdFlavor() {}
	~FireBirdFlavor() {}

	virtual void MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists) {}
   virtual const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength);
	virtual const wchar_t* UpsertSTMT(bool haveFK) const { return haveFK ? L"INSERT INTO" : L"UPDATE OR INSERT INTO"; }
	virtual bool QuoteTableName() const { return false; } //true; }
	virtual bool CanAlterManyColumns() const { return false; }
	virtual bool FetchInExecute() const { return true; }
};

class PostgresFlavor : public ODBCFlavor
{
public:
	PostgresFlavor() {}
	~PostgresFlavor() {}

	virtual void MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists) {}
	virtual const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength);
	virtual const wchar_t* UpsertSTMT(bool haveFK) const { return L"INSERT INTO"; }
	virtual bool QuoteTableName() const { return false; } //true; }
	virtual bool CanAlterManyColumns() const { return false; }
	virtual bool FetchInExecute() const { return true; }
	virtual void UpsertOnConflict(bool haveFK, std::wstring* stmt, const std::vector<std::wstring> &keyFields, std::vector<std::wstring> &allFields);
};

ODBCDataSource* gvDataSource;

std::wstring providerName;

ODBCFlavor* GRServer::GetFlavor()
{
   return (gvDataSource == NULL) ? NULL : gvDataSource->GetFlavor();
}

SQLHDBC GRServer::GetHDBC(bool forRead)
{
	if (gvDataSource == NULL)
		return NULL;

   return (forRead) ? gvDataSource->GetHDBC() : gvDataSource->GetHDBC_Wr();
}


MemberFormat::MemberType ODBCFlavor::ToMemberType(SQLSMALLINT sqlDataType)
{
   MemberFormat::MemberType type = MemberFormat::mtNone;
   switch(sqlDataType)
   {
   case SQL_CHAR:
   case SQL_VARCHAR:
   case SQL_LONGVARCHAR:
   case SQL_WCHAR:
   case SQL_WVARCHAR:
   case SQL_WLONGVARCHAR:
      type = MemberFormat::mtString;
      break;

   case SQL_DECIMAL:
   case SQL_NUMERIC:
   case SQL_SMALLINT:
   case SQL_INTEGER:
   case SQL_REAL:
   case SQL_FLOAT:
   case SQL_DOUBLE:
   case SQL_TINYINT:
   case SQL_BIGINT:
      type = MemberFormat::mtNumber;
      break;

   case SQL_BINARY:
   case SQL_VARBINARY:
   case SQL_LONGVARBINARY:
      type = MemberFormat::mtBinary;
      break;
   }

   return type;
}

IInternalDataSource* GRServer::CreateInternalDS(bool noCheckFormat)
{
   if( gvDataSource == NULL )
      gvDataSource = new ODBCDataSource(noCheckFormat);
   return gvDataSource;
}

bool GRServer::AddErrorsToLog(bool isCritical, SQLSMALLINT ht, SQLHANDLE handle, IErrorLogger::DebugLevel level)
{
   char state[10], err[10000];
   SQLSMALLINT cn;
   SQLINTEGER errCode;

   bool ret = false;

   int idx = 1;
   while( true )
   {
      SQLRETURN rc = SQLGetDiagRecA(ht, handle, idx++, (SQLCHAR*)state, &errCode, (SQLCHAR*)err, sizeof(err)/sizeof(err[0]),&cn);
      if( rc != SQL_SUCCESS )
         break;
      ret = true;
      if( isCritical )
         gServer->AddError(isCritical, "ODBC error [%s]: %s", state, err);
      else
         gServer->AddLog(level, "ODBC error [%s]: %s", state, err);

      isCritical = false;
   }
   return ret;
}

void GRServer::AddToLog(IErrorLogger::DebugLevel level, const char* message)
{
   gServer->AddLog(level, "ODBC: %s", message);
}

ODBCDataSource::ODBCDataSource(bool noCheckFormat) : opened(false), flavor(NULL), connecting(false)
{
   hEnv = NULL;
   hDbc = NULL;
	hDbcWr = NULL;

	connectionForWrite = false;

	this->noCheckFormat = noCheckFormat;
}

ODBCDataSource::~ODBCDataSource()
{
   delete flavor;

	if (hDbcWr != NULL)
		SQLFreeHandle(SQL_HANDLE_DBC, hDbcWr);
	
	SQLFreeHandle(SQL_HANDLE_DBC, hDbc);
   SQLFreeHandle(SQL_HANDLE_ENV, hEnv);

	//CloseHandle(hStmtSem);
	//hStmtSem = NULL;
}

struct ServerDataDef : public IObjectData
{
   ServerDataDef()
   {
      flags = 0;
      name = L"ServerData";
      tableName = L"ServerData";

      members[PRIMARY_KEY_STR] = L"id";
   
      Field f;
      f.flags = 0;
      f.pass = 0;
      f.width = 0;

      MemberFormat& mf = f.format;
      mf.name = L"id";
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      mf.flags = 0;
      
      f.data = mf.name;
      fields.insert(f);

      mf.name = L"data";
      mf.type = MemberFormat::mtBinary;
      f.data = mf.name;
      fields.insert(f);
   }

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
   virtual const ParamList* InternalSourceParams() const { return NULL; }
   virtual bool IsOrderedSource() const { return false;  }
};

struct DoConnectParam
{
   ODBCDataSource* source;
   GRServer::IObjectDef* objDef;
};

static DWORD DoFirstConnect(DoConnectParam* param)
{
   param->source->DoConnect(param->objDef);
   delete param;
   return 1;
}

bool ODBCDataSource::DoConnect(GRServer::IObjectDef* objDef)
{
   while( !TryConnect() )
   {
      connecting = false;
      Sleep(1000);
   }

   bool res = noCheckFormat ? true : CheckDBFormat(objDef);
   ServerDataDef sd;
   if( res && !IsTableExists(sd.tableName) )
   {
      res = CreateTable(sd);
   }
	if( res && flavor->UpsertSTMT(false) == NULL )
   {
      std::wstring stmt;
      std::wstring procName;
      GetUpsertProcName(&procName, sd);

      flavor->MakeUpsertProc(NULL, &stmt, procName, sd, IsProcExists(procName));
      res = GRServer::Execute(hDbc, stmt);
   }

   opened = true;
   connecting = false;
   return true;
}

bool ODBCDataSource::Init(GRServer::IObjectDef* objDef, const GRServer::ServerConfig& config)
{
   if( !InitConnection() )
      return false;

   DoConnectParam *param = new DoConnectParam();
   param->source = this;
   param->objDef = objDef;

   HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoFirstConnect, param, 0, 0);
   CloseHandle(ht);
   return true;
}

static DWORD DoReconnect(ODBCDataSource *source)
{
   source->Reconnect();
   return 0;
};

SQLHDBC ODBCDataSource::GetHDBC_Wr() const
{
	if (hDbcWr == NULL)
		return GetHDBC();
	return hDbcWr;
}

SQLHDBC ODBCDataSource::GetHDBC() const
{
   if( !opened )
      return NULL;

   bool checked = true;
   SQLINTEGER isDead = SQL_CD_FALSE, length = 0;
   SQLGetConnectAttr(hDbc, SQL_ATTR_CONNECTION_DEAD, &isDead, sizeof(SQLINTEGER), &length);
   checked = (isDead == SQL_CD_FALSE);
   if( !checked )
   {
      AddToLog(IErrorLogger::Full, "connection is dead, reconnect");

      opened = false;
      HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReconnect, (ODBCDataSource*)this, 0, 0);
      CloseHandle(ht);
      return NULL;
   }
   return hDbc;
}

const wchar_t* GRServer::QuoteString(std::wstring* dest)
{
   wchar_t symW = L'"';
   if( *dest->begin() != symW) dest->insert(dest->begin(), symW);
   if( *dest->rbegin() != symW) dest->append(1, symW);
   return dest->c_str();
}

const wchar_t* GRServer::QuoteString(std::wstring* dest, const std::wstring& src)
{
   wchar_t symW = L'"';
   if( *src.begin() != symW) dest->append(1, symW);
   dest->append(src);
   if( *dest->rbegin() != symW) dest->append(1, symW);
   return dest->c_str();
}

bool GRServer::Execute(SQLHDBC hdbc, const std::wstring& sql, bool fetchResult)
{
   SQLHSTMT hstmt;
   SQLRETURN rc;

   rc = SQLAllocHandle(SQL_HANDLE_STMT, hdbc, &hstmt);
   rc = SQLExecDirect(hstmt, (wchar_t*)sql.c_str(), SQL_NTS);

   if( gServer->GetConfig().Debug() == IErrorLogger::Full )
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Execute stmt %s", W2A(sql.c_str()));

      if( rc != SQL_SUCCESS )
         AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
   }

	if( rc == SQL_SUCCESS && fetchResult )
	{
		// Это для Firebird - он не коммитит транзакцию, пока не выбрал данные
		int rc1;
		do {
			rc1 = SQLFetch(hstmt);
		} while( rc1 == SQL_SUCCESS );
	}

   SQLFreeHandle(SQL_HANDLE_STMT, hstmt);
   return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

bool ODBCDataSource::IsProcExists(const std::wstring& procName)
{
   SQLHSTMT hstmt;
   SQLRETURN rc;
   rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   std::wstring tbuf;
   wstring buf;
   wchar_t* tn = (flavor->QuoteTableName()) ? (wchar_t*)QuoteString(&buf, procName) : (wchar_t*)procName.c_str();
   rc = SQLProcedures(hstmt, NULL, 0, NULL, 0, tn, SQL_NTS);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
      rc = SQLFetch(hstmt);

   while( SQLMoreResults(hstmt) == SQL_SUCCESS )
      ;

   SQLFreeHandle(SQL_HANDLE_STMT, &hstmt);
   return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

bool ODBCDataSource::IsTableExists(const std::wstring& table)
{
   SQLHSTMT hstmt;
   SQLRETURN rc;
   rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   std::wstring tbuf;
   wstring buf;

   wchar_t* tn = (flavor->QuoteTableName()) ? (wchar_t*)QuoteString(&buf, table) : (wchar_t*)table.c_str();
   rc = SQLTables(hstmt, NULL, 0, NULL, 0, tn, SQL_NTS, NULL, 0);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
      rc = SQLFetch(hstmt);

   while( SQLMoreResults(hstmt) == SQL_SUCCESS )
      ;

   SQLFreeHandle(SQL_HANDLE_STMT, &hstmt);
   return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

bool ODBCDataSource::IsIndexExists(const std::wstring& index)
{
	bool ret = false;

   SQLHSTMT hstmt;
   SQLRETURN rc;
   rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   std::wstring tbuf;
   wstring buf;

   wchar_t* tn = (flavor->QuoteTableName()) ? (wchar_t*)QuoteString(&buf, index) : (wchar_t*)index.c_str();
   rc = SQLStatistics(hstmt, NULL, 0, NULL, 0, tn, SQL_NTS, SQL_INDEX_ALL, SQL_QUICK);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
	{
      const int STR_LEN = 128 + 1;

      wchar_t szName[STR_LEN];
      SQLLEN cbName;

      SQLBindCol(hstmt, 6, SQL_C_WCHAR, szName, STR_LEN, &cbName);
		while( (rc = SQLFetch(hstmt)) == SQL_SUCCESS )
      {
			if( !ret && !wcscmp(szName, index.c_str()) )
				ret = true;
		}
	}
   while( SQLMoreResults(hstmt) == SQL_SUCCESS )
      ;

   SQLFreeHandle(SQL_HANDLE_STMT, &hstmt);
   return ret;
}

bool ODBCDataSource::InitConnection()
{
   USES_CONVERSION;

   if( connecting )
      return true;

   Config c;
   std::string err("Ошибка инициализации ODBC: ");
   if( connectStr.empty() )
   {
      if( !c.Load(configFile) )
      {
         err += "не могу загрузить настройки из файла [";
         err += W2A(configFile.c_str());
         err += "]";
         gServer->AddError(true, err.c_str());
         return false;
      }

      if( !c.MakeConnectionString(&connectStr) )
      {
         err += "ошибка создания строки подключения";
         gServer->AddError(true, err.c_str());
         return false;
      }
		//InitSTMTSemaphore(c.concurentStmtCount);
   }

   // Если есть, используем это для Reconnect
   if( flavor == NULL )
   {
      //if( c.provider.find(L"SQL") != std::wstring::npos )
      //   flavor = new MSSQLFlavor();
      //else 
      if( c.provider.find(L"Oracle") != std::wstring::npos )
         flavor = new OracleFlavor();
		else if( c.provider.find(L"Firebird") != std::wstring::npos )
			flavor = new FireBirdFlavor();
		else if (c.provider.find(L"PostgreSQL") != std::wstring::npos)
			flavor = new PostgresFlavor();
		else
		{
			flavor = new MSSQLFlavor();
			connectionForWrite = true;
		}
   }

   SQLRETURN rc;

   if( hEnv != NULL )
      SQLFreeHandle(SQL_HANDLE_ENV, hEnv);
   if( hDbc != NULL )
      SQLFreeHandle(SQL_HANDLE_DBC, hDbc);
	if (hDbcWr != NULL)
		SQLFreeHandle(SQL_HANDLE_DBC, hDbcWr);

   rc = SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &hEnv);
   if( rc != SQL_SUCCESS )
   {
      gServer->AddError(true, "Ошибка инициализации ODBC(HENV)");
      return false;
   }

   SQLSetEnvAttr(hEnv, SQL_ATTR_ODBC_VERSION, (SQLPOINTER)SQL_OV_ODBC3, 0);
   rc = SQLAllocHandle(SQL_HANDLE_DBC, hEnv, &hDbc);
   if( rc != SQL_SUCCESS )
   {
      AddErrorsToLog(true, SQL_HANDLE_ENV, hEnv);
      return false;
   }
	if (connectionForWrite)
	{
		rc = SQLAllocHandle(SQL_HANDLE_DBC, hEnv, &hDbcWr);
		if (rc != SQL_SUCCESS)
		{
			AddErrorsToLog(true, SQL_HANDLE_ENV, hEnv);
			return false;
		}
	}

	return true;
}

bool ODBCDataSource::TryConnect()
{
   if( connecting )
      return false;
   if( opened )
      return true;

   connecting = true;

   AddToLog(IErrorLogger::Full, "try connect");
   flavor->SetDBCAttribites(hDbc);
   SQLRETURN rc = SQLDriverConnect(hDbc, NULL, (wchar_t*)connectStr.c_str(), SQL_NTS, NULL, 0, NULL, SQL_DRIVER_NOPROMPT);
   bool ret = (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
   if( !ret )
   {
      // дадим возможность переподключиться позже
      AddErrorsToLog(false, SQL_HANDLE_DBC, hDbc);
	}
	else if (hDbcWr != NULL)
	{
		flavor->SetDBCAttribites(hDbcWr);
		rc = SQLDriverConnect(hDbcWr, NULL, (wchar_t*)connectStr.c_str(), SQL_NTS, NULL, 0, NULL, SQL_DRIVER_NOPROMPT);
		if (!(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO))
		{
			ret = false;
			AddErrorsToLog(false, SQL_HANDLE_DBC, hDbcWr);
		}
		SQLSetConnectAttr(hDbc, SQL_ATTR_TXN_ISOLATION, (SQLPOINTER*)SQL_TXN_READ_UNCOMMITTED, SQL_IS_UINTEGER);
		SQLSetConnectAttr(hDbcWr, SQL_ATTR_TXN_ISOLATION, (SQLPOINTER*)SQL_TXN_READ_UNCOMMITTED, SQL_IS_UINTEGER);
	}

   return ret;
}

bool ODBCDataSource::Reconnect()
{
   opened = false;
   if( !InitConnection() )
      return false;

   while( !TryConnect() )
   {
      connecting = false;
      Sleep(1000);
   }

   opened = true;
   connecting = false;
   return true;
}

const wchar_t* FireBirdFlavor::TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength)
{
   wchar_t wbuf[100];
   switch(format.format.type)
   {
   case MemberFormat::mtString:
      wsprintf(wbuf, L"VARCHAR(%d)", (format.width == 0) ? defaultLength : format.width);
      buf->assign(wbuf);
      break;
   case MemberFormat::mtNumber:
      buf->assign(( format.format.format.fraction == 0 ) ? L"BIGINT" : L"FLOAT");
      break;
   case MemberFormat::mtDateTime:
      buf->assign(L"BIGINT");
      break;
   case MemberFormat::mtBinary:
      buf->assign(L"BLOB");
      break;
   }
   return buf->c_str();
}

void PostgresFlavor::UpsertOnConflict(bool haveFK, std::wstring* stmt, const std::vector<std::wstring> &keyFields, std::vector<std::wstring> &allFields)
{
	if (haveFK || keyFields.size() == 0)
		return;

	std::set<std::wstring> kf;
	std::vector<std::wstring>::const_iterator i = keyFields.begin();
	stmt->append(L" ON CONFLICT (");

	for (; i != keyFields.end(); i++)
	{
		kf.insert(*i);
		stmt->append(1, L'"').append(*i).append(1, L'"').append(L",");
	}

	stmt->erase(stmt->size() - 1);
	if (keyFields.size() == allFields.size())
		stmt->append(L") DO NOTHING");
	else
	{
		stmt->append(L") DO UPDATE SET ");

		i = allFields.begin();
		for (; i != allFields.end(); i++)
		{
			if (kf.find(*i) != kf.end())
				continue;

			stmt->append(1, L'"').append(*i).append(1, L'"').append(L"=EXCLUDED.").append(1, L'"').append(*i).append(1, L'"').append(L",");
		}
		stmt->erase(stmt->size() - 1);
	}
}

const wchar_t* PostgresFlavor::TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength)
{
	wchar_t wbuf[100];
	switch (format.format.type)
	{
	case MemberFormat::mtString:
		wsprintf(wbuf, L"VARCHAR(%d)", (format.width == 0) ? defaultLength : format.width);
		buf->assign(wbuf);
		break;
	case MemberFormat::mtNumber:
		buf->assign((format.format.format.fraction == 0) ? L"BIGINT" : L"FLOAT");
		break;
	case MemberFormat::mtDateTime:
		buf->assign(L"BIGINT");
		break;
	case MemberFormat::mtBinary:
		buf->assign(L"BYTEA");
		break;
	}
	return buf->c_str();
}

const wchar_t* MSSQLFlavor::TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength)
{
   wchar_t wbuf[100];
   switch(format.format.type)
   {
   case MemberFormat::mtString:
      wsprintf(wbuf, L"NVARCHAR(%d)", (format.width == 0) ? defaultLength : format.width);
      buf->assign(wbuf);
      break;
   case MemberFormat::mtNumber:
		buf->assign((format.format.format.fraction == 0) ? L"BIGINT": L"FLOAT");
		//buf->assign((format.format.format.fraction == 0) ?
		//	((format.width != 0 && format.width < 11) ? L"INT" : L"BIGINT") :
		//	L"FLOAT");
      break;
   case MemberFormat::mtDateTime:
      buf->assign(L"BIGINT");
      break;
   case MemberFormat::mtBinary:
      buf->assign(L"VARBINARY(MAX)");
      break;
   }
   return buf->c_str();
}

static bool IsKeyField(const wstring& name, const std::vector<std::wstring>& keyFields)
{
   vector<wstring>::const_iterator fi = keyFields.begin();
   for( ; fi != keyFields.end(); fi++ )
      if( fi->compare(name) == 0 )
         return true;

   return false;
}

static bool HaveField(const IObjectData::Fields& fields, const IObjectData::Field& fld)
{
	IObjectData::Fields::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
		if (i->format.name.compare(fld.format.name) == 0)
			return true;

	return false;
}

void MSSQLFlavor::MakeUpsertProc(FieldSet* fields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists)
{
   vector<wstring> keyFields;

   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second, false);

   std::wstring tbuf;
   std::wstring allFields, updateValues, updateKeys, insertFields, insertValues;

   IObjectData::Fields src(objDef.fields);
   AddOrderedField(src, objDef);

   IObjectData::Fields::const_iterator fi = src.begin();
   for( ; fi != src.end(); fi++ )
   {
      if( fi->CanCreate() == false )
         continue;


		const wstring& name = fi->format.name;
      std::wstring qName, updTok;
      std::wstring atName(L"@");
      atName += name;
      QuoteString(&qName, name);
      updTok = qName; updTok += L" = "; 
		{
			updTok += atName;
		}

      if( !allFields.empty() ) allFields += L", ";
      allFields += atName; allFields += L" "; 
		
		{
			allFields += TypeToString(&tbuf, *fi, DEFAULT_STRING_LENGTH);
		}

      if( !insertValues.empty() ) insertValues += L", ";
		{
			insertValues += atName;
		}

      if( !insertFields.empty() ) insertFields += L", ";
      insertFields += qName;

      if( IsKeyField(name, keyFields) )
      {
         if( !updateKeys.empty() ) updateKeys += L" AND ";
         updateKeys += updTok;
      } else
      {
         if( !updateValues.empty() ) updateValues += L", ";
         updateValues += updTok;
      }
   }

   bool isChildTable = false;
   CVector<IObjectData::Field> *fk = NULL;
   if( objDef.LoadFK(NULL, &fk) && fk != NULL && fk->size() > 0 )
   {
      updateKeys.clear();

      CVector<IObjectData::Field>::const_iterator fki = fk->begin();
      for( ; fki != fk->end(); fki++ )
      {
			if (HaveField(objDef.fields, *fki))
				continue;

         const wstring& name = fki->format.name;
         std::wstring qName, updTok;
         std::wstring atName(L"@");
         atName += name;
         QuoteString(&qName, name);
         updTok = qName; updTok += L" = "; updTok += atName;

         if( !allFields.empty() ) allFields += L", ";
         allFields += atName; allFields += L" "; allFields += TypeToString(&tbuf, *fki, DEFAULT_STRING_LENGTH);

         if( !updateKeys.empty() ) updateKeys += L" AND ";
         updateKeys += updTok;

         if( !insertValues.empty() ) insertValues += L", ";
         insertValues += atName;

         if( !insertFields.empty() ) insertFields += L", ";
         insertFields += qName;
      }

      isChildTable = true;
   }
   delete fk;

   stmt->clear();
   if( isProcExists )
      stmt->append(L"alter procedure "); 
   else
      stmt->append(L"create procedure ");

   stmt->append(procName); stmt->append(L"("); stmt->append(allFields); stmt->append(L")\nas\nbegin tran\n");
   if( !isChildTable )
   {
      if( keyFields.size() > 0 )
      {
         if( !updateValues.empty() )
         {
            stmt->append(L"update \""); stmt->append(objDef.tableName); stmt->append(L"\" with (serializable) set "); stmt->append(updateValues); stmt->append(L" where "); stmt->append(updateKeys); stmt->append(L"\n");
            stmt->append(L"if @@rowcount = 0\n");
         } else
         {
            stmt->append(L"if not exists (select * from \""); stmt->append(objDef.tableName); stmt->append(L"\" with (updlock,serializable) where "); stmt->append(updateKeys); stmt->append(L")\n");
         }
         stmt->append(L"begin\n");
      }
   }
   stmt->append(L" insert into \""); stmt->append(objDef.tableName); stmt->append(L"\" ("); stmt->append(insertFields); stmt->append(L") values ("); stmt->append(insertValues); stmt->append(L")\n");
   if( !isChildTable && keyFields.size() > 0 )
      stmt->append(L"end;\n");
   stmt->append(L"commit tran");

   //$procName, $tableName
   //// @id int, @data varbinary(max) 
   //$allFields, 
   //// \"data\" = @data 
   //$updateValues
   //// \"id\" = @id
   //$updateKeys
   ////\"id\", \"data\"
   //$insertFields
   ////@id, @data
   //$insertValues
//L"create procedure $procName($allFields)\n"
//L"as\n"
//L"begin tran\n"
//L"if not exists (select * from \"$tableName\" with (updlock,serializable) where $updateKeys)\n"
//L"update \"$tableName\" with (serializable) set $updateValues where $updateKeys\n"
//L"if @@rowcount = 0\n"
//L"begin\n"
//L"	insert into \"$tableName\" ($insertFields) values ($insertFields)\n"
//L"end;\n"
//L"commit tran"

// child case
//L"create procedure $procName($allFields)\n"
//L"as\n"
//L"begin tran\n"
//L"delete from \"$tableName\" where $updateKeys\n"
//Linsert into \"$tableName\" ($insertFields) values ($insertValues)\n"
//L"commit tran"
}

bool MSSQLFlavor::GrantExecuteToUpsertProc(std::wstring* stmt, const std::wstring& procName)
{
	stmt->clear();
	stmt->append(L"grant execute on "); stmt->append(procName); stmt->append(L" to public;");
	return true;
}

void MSSQLFlavor::ChangeNumberType(HDBC hDbc, const std::wstring& tableName, const std::vector<IObjectData::Field>& fields)
{
	std::vector<IObjectData::Field>::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
	{
		std::wstring tstr(L"alter table [");
		tstr.append(tableName).append(L"] alter column [").append(i->format.name).append(L"] bigint");
		Execute(hDbc, tstr, false);
	}
}

const wchar_t* OracleFlavor::TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength)
{
   wchar_t wbuf[100];
   switch(format.format.type)
   {
   case MemberFormat::mtString:
      wsprintf(wbuf, L"VARCHAR2(%d)", (format.width == 0) ? defaultLength : format.width);
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

void OracleFlavor::MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& _procName, const IObjectData& objDef, bool isProcExists)
{
   std::wstring procName;
   vector<wstring> keyFields;

   QuoteString(&procName, _procName);

   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second, false);

	std::set<std::wstring> usedFields;
   std::wstring tbuf;
   std::wstring allFields, updateValues, updateKeys, insertFields, insertValues;
   IObjectData::Fields src(objDef.fields);
   AddOrderedField(src, objDef);

   IObjectData::Fields::const_iterator fi = src.begin();
   for( ; fi != src.end(); fi++ )
   {
      if( fi->CanCreate() == false )
         continue;

      const wstring& name = fi->format.name;
      std::wstring qName, updTok;
      std::wstring atName(L"@");
      atName += name;
		if (usedFields.find(atName) != usedFields.end())
			continue;

		usedFields.insert(atName);
      QuoteString(&qName, name);
      QuoteString(&atName);
      updTok = qName; updTok += L" = "; updTok += atName;

      TypeToString(&tbuf, *fi, DEFAULT_STRING_LENGTH);
      size_t cp = tbuf.find(L'(');
      if( cp != std::wstring::npos )
         tbuf.erase(cp);
      if( !allFields.empty() ) allFields += L", ";
      allFields += atName; allFields += L" "; allFields += tbuf;

      if( !insertValues.empty() ) insertValues += L", ";
      insertValues += atName;

      if( !insertFields.empty() ) insertFields += L", ";
      insertFields += qName;

      if( IsKeyField(name, keyFields) )
      {
         if( !updateKeys.empty() ) updateKeys += L" AND ";
         updateKeys += updTok;
      } else
      {
         if( !updateValues.empty() ) updateValues += L", ";
         updateValues += updTok;
      }
   }

   bool isChildTable = false;
   CVector<IObjectData::Field> *fk = NULL;
   if( objDef.LoadFK(NULL, &fk) && fk != NULL && fk->size() > 0 )
   {
      updateKeys.clear();

      CVector<IObjectData::Field>::const_iterator fki = fk->begin();
      for( ; fki != fk->end(); fki++ )
      {
         const wstring& name = fki->format.name;
         std::wstring qName, updTok;
         std::wstring atName(L"@");
         atName += name;
			if (usedFields.find(atName) != usedFields.end())
				continue;

			usedFields.insert(atName);
			QuoteString(&qName, name);
         QuoteString(&atName);
         updTok = qName; updTok += L" = "; updTok += atName;

         TypeToString(&tbuf, *fki, DEFAULT_STRING_LENGTH);
         size_t cp = tbuf.find(L'(');
         if( cp != std::wstring::npos )
            tbuf.erase(cp);
         if( !allFields.empty() ) allFields += L", ";
         allFields += atName; allFields += L" "; allFields += tbuf;

         if( !updateKeys.empty() ) updateKeys += L" AND ";
         updateKeys += updTok;

         if( !insertValues.empty() ) insertValues += L", ";
         insertValues += atName;

         if( !insertFields.empty() ) insertFields += L", ";
         insertFields += qName;
      }

      isChildTable = true;
   }
   delete fk;

   stmt->clear();
   stmt->append(L"create or replace procedure "); 

   stmt->append(procName); stmt->append(L"("); stmt->append(allFields); stmt->append(L")\nas\nbegin\n");
   std::wstring insStmt;
   insStmt.append(L"("); insStmt.append(insertFields); insStmt.append(L") values ("); insStmt.append(insertValues); insStmt.append(L")\n");
   if( isChildTable || keyFields.size() == 0 )
   {
      stmt->append(L"insert into \""); stmt->append(objDef.tableName); stmt->append(L"\" "); stmt->append(insStmt); stmt->append(L";\n");
   } else
   {
      stmt->append(L" merge into \""); stmt->append(objDef.tableName); stmt->append(L"\" m using dual on ("); stmt->append(updateKeys); stmt->append(L")\n");
      stmt->append(L"  when not matched then insert "); stmt->append(insStmt);
      if( !updateValues.empty() )
      {
         stmt->append(L" when matched then update set "); stmt->append(updateValues);
      }
      stmt->append(L";\n");
   }
   stmt->append(L"end "); stmt->append(procName); stmt->append(L";\n");


   //L"create or replace procedure $procName($allFields)\n"
//L"as\n"
//L"begin\n"
//L"    merge into \"$tableName\" m using dual on ($updateKeys)\n"
//L"         when not matched then insert ($insertFields) values ($insertvalues)\n"
//L"             when matched then update set $updateValues;\n"
//L"end ups;\n"

// child case
//L"create procedure $procName($allFields)\n"
//L"as\n"
//L"begin tran\n"
//L"delete from \"$tableName\" where $updateKeys\n"
//Linsert into \"$tableName\" ($insertFields) values ($insertValues)\n"
//L"commit tran"
}

static void UpdateFields(std::vector<IObjectData::Field>* fields, const CVector<IObjectData::Field>& keyFields)
{
	std::vector<IObjectData::Field>::const_iterator i = keyFields.begin();
	for (; i != keyFields.end(); i++)
	{
		bool finded = false;
		std::vector<IObjectData::Field>::iterator j = fields->begin();
		for (; j != fields->end(); j++)
		{
			if (j->format.name.compare(i->format.name) == 0)
			{
				finded = true;
				break;
			}
		}

		if (!finded)
			fields->push_back(*i);
	}
}

bool ODBCDataSource::CreateTable(const IObjectData& objDef)
{
   vector<wstring> keyFields;
   IObjectData::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
      PKToList(&keyFields, keyI->second, false);

   std::wstring text(L"CREATE TABLE ");
   QuoteString(&text, objDef.tableName);
   text += L" (";

   vector<IObjectData::Field> fields;
   IObjectData::Fields::const_iterator fi = objDef.fields.begin();
   for( ; fi != objDef.fields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;
      fields.push_back(*fi);
   }

   AddOrderedField(fields, objDef);

   CVector<IObjectData::Field>* fkFields = NULL;
   CVector<MemberFormat>* fkMFields = NULL;
   objDef.LoadFK(&fkMFields, &fkFields);

   //fields.insert(fields.end(), fkFields->begin(), fkFields->end());
	UpdateFields(&fields, *fkFields);

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      IObjectData::Field f;
      MemberFormat& mf = f.format;
      mf.name = SENDED_FIELDS;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      f.width = 0;
      fields.push_back(f);
   }

   bool assigned = false;
   std::wstring buf;
   vector<IObjectData::Field>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const wchar_t *pType = flavor->TypeToString(&buf, *i, DEFAULT_STRING_LENGTH);
      if( *pType == L'\0' ) continue;

      if( assigned ) text += L",";
      else assigned = true;

      QuoteString(&text, i->format.name);
      text += L" ";
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
         QuoteString(&text, (*ki));
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

   text += L")";
   bool ret = GRServer::Execute(hDbc, text);
   if( ret && indexText != NULL && !indexText->empty() )
   {
      ret = GRServer::Execute(hDbc, indexText->c_str());
   }

   delete fkFields;
   delete fkMFields;
   delete indexText;

   return ret;
}

bool ODBCDataSource::CheckDBFormat(GRServer::IObjectDef* objDefs)
{
   bool res = true;
   //vector<wstring> names;
   CVector<CString> *names = NULL;
   objDefs->GetObjectsName(&names, IObjectDef::Internal);

   bool logging = ( gServer->GetConfig().Debug() == IErrorLogger::Full );
   USES_CONVERSION;

   CVector<CString>::const_iterator i;
   for( int pass=0; res && pass < 2; pass++ )
   {
		int idx = 0;
      i = names->begin();
      for( ; res && i != names->end(); i++ )
      {
			idx++;
         const std::wstring& tname = (const std::wstring&)*i;
         bool isChildTable = (tname.find(L'$') != std::wstring::npos);
         if( pass == 0 && isChildTable || pass == 1 && !isChildTable )
            continue;

			FieldSet fields;
			const IObjectData* odef = objDefs->Get(tname);
         if( logging )
            gServer->AddLog(IErrorLogger::Full, "Check table %s", W2A(odef->tableName.c_str()));

         if( !IsTableExists(odef->tableName) )
         {
            if( logging )
               gServer->AddLog(IErrorLogger::Full, "Create table %s", W2A(odef->tableName.c_str()));
            res = CreateTable(*odef);
         } else
         {
            res = CheckTable(&fields, *odef);
         }
         if( res )
            res = MakeUpsertProc(fields, *odef);
      }
   }

   delete names;
   return res;
}

void ODBCDataSource::Close()
{
   if( opened )
   {
		if (hDbcWr != NULL)
			SQLFreeHandle(SQL_HANDLE_DBC, hDbcWr);		
		if (hDbc != NULL)
         SQLFreeHandle(SQL_HANDLE_DBC, hDbc);
      if( hEnv != NULL )
         SQLFreeHandle(SQL_HANDLE_ENV, hEnv);
      opened = false;
   }
}

static void DoReplace(std::wstring *str, wchar_t src, wchar_t rpl)
{
	std::wstring::size_type sp = 0;
	std::wstring::size_type fnd = str->find(src, sp);
	while( fnd != std::wstring::npos )
	{
		str->replace(fnd, 1, 1, rpl);
		sp = fnd + 1;
		fnd = str->find(src, sp);
	}
}

bool ODBCDataSource::CreateIndex(const std::wstring& tableName, const std::wstring& fields, bool unique)
{
	bool quoted = (*fields.begin() == L'"');
	std::wstring indexName( (quoted) ? fields.substr(1, fields.size() - 2) : fields);
	DoReplace(&indexName, L',', L'_');
	indexName = tableName + ((unique) ? L"_U_" : L"_") + indexName;

	if( IsIndexExists(indexName) )
		return true;

	wstring tName;
   vector<wstring> indexFields;
	PKToList(&indexFields, fields, true);

   QuoteString(&indexName);
   QuoteString(&tName, tableName);

	std::wstring sql(L"CREATE ");
	if( unique )
		sql += L"UNIQUE ";
	sql += L"INDEX "; sql += indexName + L" ON "; sql += tName; sql += L" (";

   vector<wstring>::const_iterator i = indexFields.begin();
   for( ; i != indexFields.end(); i++ )
   {
      if( i != indexFields.begin() )
			sql += L",";

      sql += (*i);
	}
	sql += L")";
	return GRServer::Execute(hDbc, sql.c_str());
}

bool ODBCDataSource::CheckTable(FieldSet *fields, const IObjectData& objDef)
{
   bool res = true;

   GetTableFields(fields, objDef.tableName);

   std::vector<IObjectData::Field> added;
	std::vector<IObjectData::Field> changedFields;

   IObjectData::Fields odfields(objDef.fields);

   AddOrderedField(odfields, objDef);

   CVector<IObjectData::Field>* fkFields = NULL;
   objDef.LoadFK(NULL, &fkFields);

   CVector<IObjectData::Field>::const_iterator fki = fkFields->begin();
   for( ; fki != fkFields->end(); fki++ )
      odfields.insert(*fki);
   delete fkFields;

   IObjectData::Fields::const_iterator fi = odfields.begin();
   for( ; fi != odfields.end(); fi++ )
   {
      if( !fi->CanCreate() )
         continue;

		MemberFormatDB mf;
		*(MemberFormat*)&mf = fi->format;
		mf.fieldWidth = 0;
		FieldSet::iterator fnd = fields->find(mf);
      if( fnd == fields->end() )
         added.push_back(*fi);
      else
      {
         if (mf.type == MemberFormat::mtNumber && fnd->fieldWidth < 11)
         {
            changedFields.push_back(*fi);
         }

         if (mf.type == MemberFormat::mtString && fnd->fieldWidth < fi->width)
         {
            USES_CONVERSION;

            gServer->AddError(true, "Table [%s] field [%s] width %d smaller %d",
               W2A(objDef.tableName.c_str()),
               W2A(mf.name.c_str()),
               fnd->fieldWidth,
               fi->width
            );
            Sleep(1000);
//            abort();
            return false;
         }
 		}
   }

   if( (objDef.flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      IObjectData::Field f;
		f.format.name = SENDED_FIELDS;
		f.format.type = MemberFormat::mtNumber;
		f.format.format.fraction = 0;
		f.width = 0;

		MemberFormatDB mf;
		*(MemberFormat*)&mf = f.format;
      if( fields->find(mf) == fields->end() )
         added.push_back(f);
   }

   if( added.size() > 0 )
      res = AlterTable(objDef, added);

	if (res && changedFields.size() > 0)
		flavor->ChangeNumberType(hDbc,  objDef.tableName, changedFields);

	if( res )
	{
		IObjectData::MemberArray::const_iterator keyI = objDef.memberArray.find(INDEX_KEY_STR);
		if( keyI != objDef.memberArray.end() )
		{
			IObjectData::ValueList::const_iterator valI = keyI->second.begin();
			for( ; valI != keyI->second.end(); valI++)
				CreateIndex(objDef.tableName, (*valI), false);
		}

		keyI = objDef.memberArray.find(UNIQUE_INDEX_KEY_STR);
		if( keyI != objDef.memberArray.end() )
		{
			IObjectData::ValueList::const_iterator valI = keyI->second.begin();
			for( ; valI != keyI->second.end(); valI++)
				CreateIndex(objDef.tableName, (*valI), true);
		}
	}

   return res;
}


void ODBCDataSource::GetTableFields(FieldSet *fields, const std::wstring table)
{
   SQLHSTMT hstmt;
   SQLRETURN rc;
   rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);

   wstring buf;
   wchar_t* tn = (flavor->QuoteTableName()) ? (wchar_t*)QuoteString(&buf, table) : (wchar_t*)table.c_str();
   rc = SQLColumns(hstmt, NULL, 0, NULL, 0, tn, SQL_NTS, NULL, 0);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
   {
      const int STR_LEN = 128 + 1;

      wchar_t szColumnName[STR_LEN];
      SQLLEN cbColumnName = 0, cbDataType = 0, cbDecimalDigits = 0, cbColumnSize = 0, ColumnSize = 0;
      SQLSMALLINT DataType, DecimalDigits;

      SQLBindCol(hstmt, 4, SQL_C_WCHAR, szColumnName, STR_LEN, &cbColumnName);
      SQLBindCol(hstmt, 5, SQL_C_SSHORT, &DataType, 0, &cbDataType);
      SQLBindCol(hstmt, 7, SQL_C_SLONG, &ColumnSize, 0, &cbColumnSize);
      SQLBindCol(hstmt, 9, SQL_C_SSHORT, &DecimalDigits, 0, &cbDecimalDigits);

      while( (rc = SQLFetch(hstmt)) == SQL_SUCCESS )
      {
         MemberFormat::MemberType type = flavor->ToMemberType(DataType);
         if( type != MemberFormat::mtNone )
         {
				MemberFormatDB mf;
            mf.name = szColumnName;
            mf.type = type;
            mf.flags = 0;
            mf.fieldWidth = (WORD)ColumnSize;
            if( type == MemberFormat::mtNumber )
            {
					if (ColumnSize > 10 && DecimalDigits == 0) // timestamp
               {
                  //type = MemberFormat::mtDateTime;
                  //mf.format.dateFormat = MemberFormat::Stamp;
               } else
               {
                  mf.format.fraction = DecimalDigits;
               }
            }

            fields->insert(mf);
         }
      }
   } else 
   {
      AddErrorsToLog(true, SQL_HANDLE_STMT, hstmt);
   }

	SQLFreeHandle(SQL_HANDLE_STMT, &hstmt);
}

void MakeAlterStmt(std::wstring *stmt, ODBCFlavor* flavor, const IObjectData& objDef, const std::vector<IObjectData::Field>& added)
{
   (*stmt) = L"ALTER TABLE ";
   wstring tbuf;
   QuoteString(stmt, objDef.tableName);
   (*stmt) += L" ADD ";
   if( flavor->AlterTableUseBrackets() )
      (*stmt) += L"( ";

   vector<IObjectData::Field>::const_iterator fi = added.begin();
   for( ; fi != added.end() ; fi++ )
   {
      if( fi != added.begin() )
         (*stmt) += L",";

      QuoteString(stmt, fi->format.name);
      (*stmt) += L" ";
      (*stmt) += flavor->TypeToString(&tbuf, *fi, DEFAULT_STRING_LENGTH);
   }

   if( flavor->AlterTableUseBrackets() )
      (*stmt) += L" )";
}

bool ODBCDataSource::AlterTable(const IObjectData& objDef, const std::vector<IObjectData::Field>& added)
{
   if( gServer->GetConfig().Debug() == IErrorLogger::Full )
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Alter table %s", W2A(objDef.tableName.c_str()));
   }
	
	bool ret = true;
	if( !flavor->CanAlterManyColumns() )
	{
		vector<IObjectData::Field>::const_iterator fi = added.begin();
		for( ; fi != added.end() && ret ; fi++ )
		{
			std::vector<IObjectData::Field> vec;
			vec.push_back(*fi);

			wstring stmt;
			MakeAlterStmt(&stmt, flavor, objDef, vec);
			ret = GRServer::Execute(hDbc, stmt);
		}
	} else
	{
		wstring stmt;
		MakeAlterStmt(&stmt, flavor, objDef, added);
		ret = GRServer::Execute(hDbc, stmt);
	}

	return ret;
}

bool ODBCDataSource::Execute(const wchar_t* stmt, ISession* session)
{
   std::wstring wstmt(stmt);
   return GRServer::Execute(hDbc, wstmt, flavor->FetchInExecute());
}

static void InitReader(ISessionObject* so, const wchar_t* expr, wchar_t *_key, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   QuerySourceCreator qs;
   ObjectSource* src = so->GetSource();
   ISessionObject* parent = so->Parent();
   if( parent == NULL )
   {
      std::wstring stmt(expr);
      src->reader = new QueryReader(stmt, *so, hDbc, flavor, false, 0, NULL);
      src->readerName.assign(qs.Name());
   } else
   {
      wchar_t *p = wcschr(_key, L';');
      if( p != NULL )
         *p = L'\0';

      std::wstring key(_key);
      src->reader = new QueryChildReader(key, NULL, *so, *parent);
      src->readerName.assign(qs.Name());

      _key = (p!=NULL) ? p+1 : L"";
   }

   ServObject* obj = so->Self();
   GRServer::Format::const_iterator fi = obj->format->begin();
   for( ; fi != obj->format->end(); fi++ )
   {
      if( fi->type == MemberFormat::mtObject )
      {
         InitReader(so->GetChild(fi->name), L"", _key, hDbc, flavor);
         break;
      }
   }
}

ISessionObject* ODBCDataSource::Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr, ISession* session)
{
   GRServer::Format* fmt = session->RegisterType(typeDef, true);
   if( fmt == NULL )
      return NULL;

   wchar_t* grp = _wcsdup(groupExpr);

   ISessionObject *so = session->CreateObject(fmt->name, false);
	CString dest, src;
	src.assign(stmt);
	so->PrepareFilterStr(&dest, src);

   InitReader(so, dest.c_str(), grp, hDbc, flavor);
   free(grp);

   so->Reading(L"", false);
   return so;
}

void GRServer::PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting, wchar_t divSymbol)
{
   const std::wstring& str = (*_str.begin() == L'"') ? _str.substr(1, _str.size()-2) : _str;

	wstring::size_type lastPos = str.find_first_not_of(divSymbol, 0);
	wstring::size_type pos = str.find_first_of(divSymbol, lastPos);

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
      
		lastPos = str.find_first_not_of(divSymbol, pos);
		pos = str.find_first_of(divSymbol, lastPos);
   }
}

IBinary* ODBCDataSource::GetServerData(int id)
{
   // ждем открытия
   while( !opened )
   {
      Sleep(1000);
   }

   wchar_t buf[300];
   wsprintf(buf, L"SELECT \"data\" FROM \"ServerData\" WHERE \"id\" = %d", id);
   SQLHSTMT hstmt;
   SQLRETURN rc;

   Binary *ret = NULL;
   SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   rc = SQLExecDirect(hstmt, buf, SQL_NTS);
   if( rc == SQL_SUCCESS )
   {
      rc = SQLFetch(hstmt);
      if (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO)
      {
         BYTE b[1];
         SQLLEN cb;
         rc = SQLGetData(hstmt, 1, SQL_C_BINARY, b, 0, &cb);
         if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
         {
            ret = new Binary();
            BYTE *pb = ret->Alloc((DWORD)cb);
            rc = SQLGetData(hstmt, 1, SQL_C_BINARY, pb, cb, &cb);
         }
      }
   } else
   {
      bool checked = true;
      SQLINTEGER isDead = SQL_CD_FALSE, length = 0;
      SQLGetConnectAttr(hDbc, SQL_ATTR_CONNECTION_DEAD, &isDead, sizeof(SQLINTEGER), &length);
      checked = (isDead == SQL_CD_FALSE);
      if( !checked )
      {
         AddToLog(IErrorLogger::Full, "connection is dead, reconnect");

         opened = false;
         HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReconnect, (ODBCDataSource*)this, 0, 0);
         CloseHandle(ht);
         return NULL;
      }
   }

   SQLFreeHandle(SQL_HANDLE_STMT, hstmt);
   return (ret != NULL) ? new MemoryBinary(ret) : NULL;
}

const wchar_t* GRServer::GetUpsertProcName(std::wstring *procName, const IObjectData& objDef)
{
   procName->assign(L"Ins$");
   procName->append(objDef.name);

   return procName->c_str();
}

bool ODBCDataSource::MakeUpsertProc(const FieldSet &fields, const IObjectData& objDef)
{
	if( flavor->UpsertSTMT(false) != NULL )
		return true;

   std::wstring stmt, procName;
   GetUpsertProcName(&procName, objDef);

	flavor->MakeUpsertProc(const_cast<FieldSet*>(&fields), &stmt, procName, objDef, IsProcExists(procName));
	if (!GRServer::Execute(hDbc, stmt))
		return false;

	if (flavor->GrantExecuteToUpsertProc(&stmt, procName))
		return GRServer::Execute(hDbc, stmt);
   return true;
}

bool ODBCDataSource::PutServerData(int id, const Binary& b)
{
   SQLHSTMT hstmt;
   SQLRETURN rc;

   bool update = false;

   std::wstring procName;
   ServerDataDef sd;
   
	std::wstring stmt;
	const wchar_t* upsert = flavor->UpsertSTMT(false);
	if( upsert == NULL )
	{
		GetUpsertProcName(&procName, sd);
		if( flavor->QuoteTableName() )
			QuoteString(&procName);
		stmt = L"{ call "; stmt += procName; stmt += L"(?,?) }";
	} else
	{
		std::vector<std::wstring> kf;
		std::vector<std::wstring> af;

		kf.push_back(L"id");
		af.push_back(L"id");
		af.push_back(L"data");

		procName = sd.tableName;
		QuoteString(&procName);
		stmt = upsert; stmt += L" "; stmt += procName; stmt += L" (\"data\",\"id\") VALUES (?,?)";
		flavor->UpsertOnConflict(false, &stmt, kf, af);
	}

   SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   SQLLEN cb = b.Size(), cbi = 0;

   rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);

	SQLLEN ind = SQL_DATA_AT_EXEC;
   rc = SQLBindParameter(hstmt, 1, SQL_PARAM_INPUT, SQL_C_BINARY, SQL_VARBINARY, 0, 0, (SQLPOINTER)(const BYTE*)b, 0, &ind);
   rc = SQLBindParameter(hstmt, 2, SQL_PARAM_INPUT, SQL_C_SSHORT, SQL_INTEGER, 0, 0, (SQLPOINTER)&id, 0, &cbi);

   //SQLHDESC hIpd;
   //SQLGetStmtAttr(hstmt, SQL_ATTR_IMP_PARAM_DESC, &hIpd, 0, 0);
   //SQLSetDescField(hIpd, 1, SQL_DESC_NAME, "@data", SQL_NTS);
   //SQLSetDescField(hIpd, 2, SQL_DESC_NAME, "@key", SQL_NTS);

   rc = SQLExecute(hstmt);
	if (rc == SQL_NEED_DATA)
	{
		SQLPOINTER data;
		SQLINTEGER idata;
		data = (SQLPOINTER)(const BYTE*)b;
		idata = b.Size();

		FieldBinder* param;
		while ((rc = SQLParamData(hstmt, (SQLPOINTER*)&param)) == SQL_NEED_DATA)
		{
			SQLPutData(hstmt, data, idata);
			idata = SQL_NULL_DATA;
		}
	}

   if( rc != SQL_SUCCESS && rc != SQL_NO_DATA )
      AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);

	SQLFreeHandle(SQL_HANDLE_STMT, hstmt);

   return (rc == SQL_SUCCESS || rc == SQL_NO_DATA);
}

IDataSource::IReader* ODBCDataSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{   
   return GRServer::CreateReader(parameters, object, hDbc, flavor);
}

IDataSource::IWriter* ODBCDataSource::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return GRServer::CreateWriter(parent, parameters, object, (hDbcWr != NULL) ? hDbcWr : hDbc, flavor);
}

IDataSource::IRemover* ODBCDataSource::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;
   return GRServer::CreateRemover(object, (hDbcWr != NULL) ? hDbcWr : hDbc);
}

IDataSource::ISelector* ODBCDataSource::CreateSelector(const ParamList& parameters, const ISessionObject& object) const
{
   return GRServer::CreateSelector(object, hDbc, flavor);
}

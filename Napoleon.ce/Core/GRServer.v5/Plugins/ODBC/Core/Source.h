/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */

#pragma once

#include <atldbcli.h>
#include "ODBCSource.h"
#include <isessobj.h>

namespace GRServer {

const ULONG MAX_DO_COUNT = 1000;
const int DEFAULT_STRING_LENGTH = 300;

class StreamWriter : public ISequentialStream
{
public:
   StreamWriter(const IBinary& _src) : src(&_src), cRef(0), cp(0) {}
   StreamWriter() : src(NULL), cRef(0), cp(0) {}
   virtual ~StreamWriter() {}

   void SetSrc(const IBinary* _src)
   {
      src = _src;
      cp = 0;
   }

	// ISequentialStream interface implementation.
   STDMETHODIMP_(ULONG)	AddRef(void) { return ++cRef; }
   STDMETHODIMP_(ULONG)	Release(void);

	STDMETHODIMP QueryInterface(REFIID riid, LPVOID *ppv);
   STDMETHODIMP Read(void __RPC_FAR *pv, ULONG cb, ULONG __RPC_FAR *pcbRead);
   STDMETHODIMP Write(const void __RPC_FAR *pv, ULONG cb, ULONG __RPC_FAR *pcbWritten);

protected:
   const IBinary* src;
   DWORD cp;
   ULONG cRef;
};

class FieldBinder;
struct MemberFormatDB : public MemberFormat
{
	WORD fieldWidth;
};

struct MFCmp
{
	bool operator()(const MemberFormat& _Left, const MemberFormat& _Right) const
	{	// apply operator< to operands
		return (_Left.name.compare(_Right.name) < 0);
	}
};

typedef std::set<MemberFormatDB, MFCmp> FieldSet;

class ODBCFlavor
{
public:
   virtual ~ODBCFlavor() {}

   virtual void SetDBCAttribites(SQLHDBC hDbc) {}
   virtual const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength) = 0;
   virtual MemberFormat::MemberType ToMemberType(SQLSMALLINT sqlDataType);

   virtual bool AlterTableUseBrackets() const { return false; }
	virtual bool CanAlterManyColumns() const { return true; }
   
   // заключать ли в кавычки имя таблицы в IsTableExists & GetTableFields
   virtual bool QuoteTableName() const { return false; }

	virtual void MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists) = 0;
	virtual bool GrantExecuteToUpsertProc(std::wstring* stmt, const std::wstring& procName) { return false; }

   virtual FieldBinder* GetBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos);
	
	virtual const wchar_t* UpsertSTMT(bool haveFK) const { return NULL; }
	virtual void UpsertOnConflict(bool haveFK, std::wstring* stmt, const std::vector<std::wstring> &keyFields, std::vector<std::wstring> &allFields) {}

	virtual bool FetchInExecute() const { return false; }
	virtual void ChangeNumberType(HDBC hDbc, const std::wstring& tableName, const std::vector<IObjectData::Field>& fields) {}
};

class OracleFlavor : public ODBCFlavor
{
public:
	OracleFlavor() {}
	~OracleFlavor() {}
	virtual const wchar_t* TypeToString(std::wstring *buf, const IObjectData::Field& format, int defaultLength);
	virtual bool AlterTableUseBrackets() const { return true; }
	virtual bool QuoteTableName() const { return true; }
	virtual void MakeUpsertProc(FieldSet* dbFields, std::wstring* stmt, const std::wstring& procName, const IObjectData& objDef, bool isProcExists);
	virtual FieldBinder* GetBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos);
};


ODBCFlavor* GetFlavor();
SQLHDBC GetHDBC(bool forRead = true);

bool AddErrorsToLog(bool isCritical, SQLSMALLINT ht, SQLHANDLE handle, IErrorLogger::DebugLevel level = IErrorLogger::None);
void AddToLog(IErrorLogger::DebugLevel level, const char* message);
const wchar_t* QuoteString(std::wstring* dest, const std::wstring& src);
const wchar_t* QuoteString(std::wstring* dest);

bool Execute(SQLHDBC hdbc, const std::wstring& stmt, bool fetchResult = false);
const wchar_t* GetUpsertProcName(std::wstring *procName, const IObjectData& objDef);

void PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting = true, wchar_t divSymbol=L',');


IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);
IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);
IDataSource::IRemover* CreateRemover(const ISessionObject& object, SQLHDBC hDbc);

bool AddOrderedField(IObjectData::Fields& fields, const IObjectData& objDef);

extern const wchar_t* SENDED_FIELDS;
extern const wchar_t* ORDERED_FIELD;
} // namespace GRServer
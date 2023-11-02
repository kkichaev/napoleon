/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */

#pragma once

#include <atldbcli.h>
#include "OleDB.h"
#include <isessobj.h>

namespace GRServer {

const ULONG MAX_STRING_LENGTH = 1500;
const ULONG MAX_DO_COUNT = 1000;

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

bool Execute(CSession& session, const std::wstring& stmt);
inline bool Execute(CDataConnection& connection, const std::wstring& stmt) { return Execute(connection.m_session, stmt); }

bool ConvertToDate(std::wstring* dst, const wchar_t *src);
void PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting = true);
void QuoteString(std::wstring* dest);
inline void QuoteString(std::wstring* dest, const std::wstring& src)
{
   dest->assign(src);
   QuoteString(dest);
}

void AddErrorsToLog(bool isCritical, HRESULT err);

IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object, CDataConnection& connection);
IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, CDataConnection& connection);
IDataSource::IRemover* CreateRemover(const ISessionObject& object, CDataConnection& connection);
IDataSource::ISelector* CreateSelector(const ISessionObject& object, CDataConnection& connection);

CDataConnection* GetConnection();

extern const wchar_t* SENDED_FIELDS;
} // namespace GRServer
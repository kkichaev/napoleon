/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"

#include "OleReader.h"

//
// ------------------------------------ Child Binder ----------------------------------------------------
//
ChildBinder::ChildBinder() : executed(false)
{
}

HRESULT ChildBinder::OpenReader(CSession& session, const std::wstring& stmt)
{
   HRESULT hr = reader.Create(session, stmt.c_str());
   if( !SUCCEEDED(hr) )
      return hr;
   //return hr;
   return reader.Prepare();
}

bool ChildBinder::PrepareFKStmt(std::wstring* paramStmt, const ISessionObject& obj)
{
   CVector<MemberFormat>* fkFields = NULL;
   const IObjectData* od = obj.GetObjectDef();
   ISessionObject* parent = obj.Parent();
   if( parent == NULL || !od->LoadFK(&fkFields) )
   {
      delete fkFields;
      return false;
   }

   Format* format = parent->Self()->format;
   paramStmt->clear();
   
   vector<MemberFormat>::const_iterator fi = fkFields->begin();
   for( ; fi != fkFields->end(); fi++ )
   {
      int pos = fi->name.find_last_of(L'$');
      const std::wstring& pname = fi->name.substr(pos+1);
      int fldIndex = format->FindMember(pname.c_str());
      if( fldIndex < 0 )
         continue;

      AddParam(paramStmt, (*fi), fldIndex);
   }
   delete fkFields;

   return BindParams();
}

bool ChildBinder::PrepareRead(const ISessionObject& obj, const std::wstring& filter, CSession& session)
{
   wstring fkFilter;

   if( !PrepareFKStmt(&fkFilter, obj) )
      return false;

   if(!Binder::PrepareRead(obj, fkFilter, session) )
      return false;

   HRESULT hr = reader.BindColumns(reader.m_spCommand);
   return (hr==S_OK);
}

bool ChildBinder::MoveNext(Object *parentObject)
{
   if( data == NULL || parentObject == NULL )
      return false;
   if( !executed )
   {
      //reader.Close();
      if( reader.m_spRowset != NULL )
      {
         reader.m_pAccessor->FreeRecordMemory(reader.m_spRowset);
         reader.ReleaseRows();
         reader.m_spRowset.Release();
      }

      WriteParams(*parentObject);

		//DBPARAMS    params;
		//DBPARAMS    *pParams;
		//reader.BindParameters(&reader.m_hParameterAccessor, reader.m_spCommand, &params.pData);
		//// Setup the DBPARAMS structure
		//params.cParamSets = 1;
		//params.hAccessor = reader.m_hParameterAccessor;
		//pParams = &params;

      //reader.m_spCommand->Execute(NULL, reader.GetIID(), pParams, NULL ,(IUnknown**)reader.GetInterfacePtr());
      reader.Open(NULL, NULL, false, 0);
   }
   
   //return false;
   executed = (reader.m_spRowset != NULL && reader.m_pAccessor != NULL && reader.MoveNext() == S_OK);
   return executed;
}

//
// ------------------------------------ OleReader ----------------------------------------------------
//
OleReader::OleReader(const ISessionObject& object, CDataConnection& connection, const CString* filter) : 
   obj(object),
   session(connection.m_session),
   binder(NULL)
{
   if( filter != NULL )
      this->filter = *filter;
}

OleReader::~OleReader()
{
   Close();
}

void OleReader::Close()
{
   if( binder )
   {
      binder->Close();
      delete binder;
      binder = NULL;
   }
}

void OleReader::Remove()
{
   const IObjectData* od = obj.GetObjectDef();
   wstring tableName;
   QuoteString(&tableName, od->tableName);

   wstring stmt(L"DELETE FROM "); stmt += tableName;
   if( parsedFilter.empty() == false )
   {
      stmt += L" WHERE ";
      stmt += (const std::wstring&)parsedFilter;
   }
   Execute(session, stmt);
}

const MemberFormat* OleReader::Type(const wchar_t* name) const
{
   return (binder) ? binder->FieldType(name) : NULL;
}

const Member* OleReader::Value(const wchar_t* name) const
{
   return (binder) ? binder->Value(name) : NULL;
}


static const wchar_t* ReadDigit(WORD* dest, const wchar_t* src, int len, const wchar_t* sep)
{
   wchar_t dig[5];
   int pos = 0;

   while( *src && (wcschr(sep, *src) == 0) && pos < len )
   {
      if( !iswdigit(*src) )
         return NULL;
      dig[pos++] = *src;
      src++;
   }
   dig[pos] = L'\0';

   *dest = _wtoi(dig);
   return src;
}

// функция даты вида ToDate('20.04.2010') ToDate('20-04-2010 10:20:15') ToDate('20/04/2010 10:20:15')
static const wchar_t* AddDate(std::wstring* dest, const wchar_t *src)
{
   SYSTEMTIME st = {0};

   while( *src != L'(' ) if( *src++ == L'\0' ) return NULL;
   while( *src != L'\'' ) if( *src++ == L'\0' ) return NULL;

   src = ReadDigit(&st.wDay, src+1, 2, L"./-");
   if( st.wDay == 0 || st.wDay > 31 ) return NULL;

   src = ReadDigit(&st.wMonth, src+1, 2, L"./-");
   if( st.wMonth == 0 || st.wMonth > 12 ) return NULL;

   src = ReadDigit(&st.wYear, src+1, 4, L" '");
   if( st.wYear == 0 ) return NULL;

   if( *src == L' ' )
   {
      src = ReadDigit(&st.wHour, src+1, 2, L":");
      if( st.wHour >= 24 ) return NULL;

      src = ReadDigit(&st.wMinute, src+1, 2, L":");
      if( st.wMinute >= 60 ) return NULL;

      src = ReadDigit(&st.wSecond, src+1, 2, L"'");
      if( st.wSecond >= 60 ) return NULL;
   }

   if( *src != L'\'' ) return NULL;
   while( *src != ')' && *src != L'\0' ) src++;
   if( *src == L'\0' ) return NULL;

   wchar_t buf[50];
   FILETIME ft;
   SystemTimeToFileTime(&st, &ft);
   wsprintf(buf, L"%d%09d", (int)((*(__int64*)&ft) / 1000000000), (int)((*(__int64*)&ft) % 1000000000));
   dest->append(buf);

   return src+1;
}

bool GRServer::ConvertToDate(std::wstring* dst, const wchar_t *src)
{
   wchar_t* buf = (wchar_t*)alloca((wcslen(src) + 1) * sizeof(wchar_t));
   wcscpy(buf, src);
   CharUpper(buf);

   bool ret = true;
   const wchar_t *sp = buf, *sp1 = src;
   while( true )
   {
      const wchar_t* p = wcsstr(sp, L"TODATE");
      if( p == NULL )
      {
         dst->append(sp1);
         break;
      }
      dst->append(sp1, p - sp);
      sp = AddDate(dst, p + sizeof(L"TODATE")/sizeof(wchar_t) - 1);
      if( sp == NULL )
      {
         ret = false;
         break;
      }

      sp1 = src + (sp - buf);
   }

   return ret;
}

bool OleReader::MoveNext(Object *parentObject)
{
   if( !binder )
   {
      ConvertToDate((std::wstring*)&parsedFilter, filter.c_str());

      const IObjectData* od = obj.GetObjectDef();
      if( (od->flags & IObjectDef::RemoveOnCommit) != 0 )
      {
         wstring tableName;
         wstring objSendField;
         QuoteString(&tableName, od->tableName);
         QuoteString(&objSendField, SENDED_FIELDS);
         
         wstring stmt(L"UPDATE "); stmt += tableName; stmt += L" SET "; stmt += objSendField; stmt += L" = 1";
         if( parsedFilter.empty() == false )
         {
            stmt += L" WHERE ";
            stmt += (const std::wstring&)parsedFilter;
         }
         Execute(session, stmt);
         parsedFilter = objSendField; parsedFilter.append(L" = 1");
      }

      binder = CreateBinder();
      binder->PrepareRead(obj, (const std::wstring&)parsedFilter, session);
   }
   return binder->MoveNext(parentObject);
}

bool OleReader::Get(Object* o) const
{
   return (binder) ? binder->Read(o) : false;
}

//
// ------------------------------------ Entry points ----------------------------------------------------
//
IDataSource::IReader* GRServer::CreateReader(const ParamList& parameters, const ISessionObject& object, CDataConnection& connection)
{
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return NULL;

   if( object.Parent() != NULL )
      return new OleChildReader(object, connection);

   CString *filter = NULL;
   const Parameter *filterP = parameters.Find(L"readFilter", 0);
   if( filterP != NULL )
      object.GetSession().Parse(&filter, filterP->value, &object);

   IDataSource::IReader* ret = new OleReader(object, connection, filter);
   delete filter;
   return ret;
}

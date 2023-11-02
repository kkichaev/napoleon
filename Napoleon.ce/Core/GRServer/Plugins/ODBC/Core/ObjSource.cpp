#include "stdafx.h"
#include "ODBCSource.h"
#include <atldbcli.h>
#include "QuerySource.h"
#include "Reader.h"
#include <ServerDefs.h>
#include <stdobjs.h>

const wchar_t OBJ_WRITER_PARAM[] = L"objWriter";
const wchar_t RESULT_FIELD_NAME[] = L"result";
const wchar_t RESPONSE_FIELD_NAME[] = L"response";

using namespace GRServer;

class ExecProcBinder : public ParamBinder
{
public:
   struct Result
   {
      double result;
      std::wstring response;
   };

   ExecProcBinder() {}
   ~ExecProcBinder() { Close(); }

   bool Prepare(const ISessionObject& object, const std::wstring& procName, const std::vector<std::wstring>& keyFields, SQLHDBC hDbc, ODBCFlavor* flavor);
   bool Do(const Object& o, Result* res);

protected:
   void LoadResult(Result* res);
};

class ResultReader : public ParamBinder
{
public:
   ResultReader() : loader(NULL) {}

   virtual void Close()
   {
      if( loader != NULL )
         loader->LoaderClose();
      ParamBinder::Close();

      delete object->Self();
   }

   bool Prepare(const ISessionObject& src, const std::vector<std::wstring>& keyFields, SQLHDBC hDbc, ODBCFlavor* flavor);
   bool Read(Object* dest, const Object& src, const ExecProcBinder::Result& res);

   virtual bool ExecuteReader(const std::wstring& stmt);

protected:
   ISessionObject* object;
   IObjectLoader* loader;

   int responseIdx;
   int resultIdx;
};

class ObjSource : public IDataSource::IObjSource
{
public:
   ObjSource() {}
   bool Prepare(const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);

   virtual ExchangeList* Do(ISessionObject *object, const std::wstring& action, IFormatHolder* f, BSTR* msg);
   virtual void Close();

private:
   ObjCreator objCreator;

   ExecProcBinder binder;
   ResultReader reader;

   std::wstring errStr;
};

static void PrepareParams(std::wstring* stmt, std::vector<FieldBinder*> *params, const std::vector<std::wstring>& keyFields, 
                          ODBCFlavor* flavor, const IObjectData::Fields& fields, const GRServer::Format& format, bool whereStyle)
{
   int started = (int)params->size();

   std::vector<std::wstring>::const_iterator ki = keyFields.begin();
   for( ; ki != keyFields.end(); ki++ )
   {
      int idx = format.FindMember(ki->c_str());
      if( idx < 0 )
         continue;

      FieldBinder* fb = NULL;
      IObjectData::Fields::const_iterator ofi = fields.begin();
      for( ; ofi != fields.end(); ofi++ )
         if( ofi->format.name.compare(*ki) == 0 )
         {
            fb = flavor->GetBinder(*ofi, DEFAULT_STRING_LENGTH, idx, (int)(params->size() + 1));
            break;
         }

      if( fb != NULL )
      {
         if( whereStyle )
         {
            if( params->size() != started )
               stmt->append(L" AND ");
            QuoteString(stmt, *ki);
            stmt->append(L" = ?");
         } else
         {
            if( params->size() != started )
               stmt->append(L",");
            stmt->append(L"?");
         }
         params->push_back(fb);
      }
   }
}

//
//----------------------------------------- ExecProcBinder ----------------------------------------
//
bool ExecProcBinder::Prepare(const ISessionObject& object, const std::wstring& _procName,  const std::vector<std::wstring>& keyFields, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   GRServer::Format *format = object.Self()->format;

   const IObjectData *od = object.GetObjectDef();

   wstring procName(_procName);
   if( flavor->QuoteTableName() )
      QuoteString(&procName);

   //
   // первые два параметра
   // result out number, response out varchar2
   //
   std::wstring stmt(L"{ call "); stmt += procName; stmt += L"(?,?,";

   FieldBinder *fb;
   IObjectData::Field ff;

   ff.flags = 0;
   ff.width = 0;
   ff.format.flags = 0;
   ff.format.format.fraction = 0;

   ff.data = RESULT_FIELD_NAME;
   ff.format.name = RESULT_FIELD_NAME;
   ff.format.type = MemberFormat::mtNumber;
   fb = flavor->GetBinder(ff, DEFAULT_STRING_LENGTH, -1, 1);
   fb->SetIOType(SQL_PARAM_OUTPUT);
   params.push_back(fb);

   ff.data = RESPONSE_FIELD_NAME;
   ff.format.name = RESPONSE_FIELD_NAME;
   ff.format.type = MemberFormat::mtString;
   fb = flavor->GetBinder(ff, 4000 /* DEFAULT_STRING_LENGTH */, -1, 2);
   fb->SetIOType(SQL_PARAM_OUTPUT);
   params.push_back(fb);

   PrepareParams(&stmt, &params, keyFields, flavor, od->fields, *format, false);
   
   if( params.size() != keyFields.size() + 2 )
      return false;

   stmt += L") }";

   SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   SQLRETURN rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
	BindParams();
	return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

void ExecProcBinder::LoadResult(ExecProcBinder::Result* res)
{
   for( int i=0; i<2; i++ )
   {
      FieldBinder* fb = params.at(i);

      value.str = &strValue;
      fb->GetValue(&value);

      fb->GetType(&format);
      if( format.type == MemberFormat::mtString )
         res->response.assign((const std::wstring&)strValue);
      else
         res->result = value.number;
   }
}

bool ExecProcBinder::Do(const Object& o, Result* res)
{
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( (*i)->ObjPos() >= 0 )
         (*i)->Write(o);
   }

   SQLRETURN rc = SQLExecute(hstmt);
   if( rc == SQL_NEED_DATA )
   {
      FieldBinder* param;
      while( (rc = SQLParamData(hstmt, (SQLPOINTER*)&param)) == SQL_NEED_DATA )
      {
         param->PutData(hstmt, o);
      }
   }

   if( rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO && rc != SQL_NO_DATA )
   {
      if( gServer->GetConfig().Debug() != IErrorLogger::None )
         AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
   }

   res->result = -1;
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
      LoadResult(res);

   while( SQLMoreResults(hstmt) == SQL_SUCCESS )
      ;

   return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO || rc == SQL_NO_DATA);
}

//
//----------------------------------------- ResultReader ----------------------------------------
//
bool ResultReader::Prepare(const ISessionObject& src, const std::vector<std::wstring>& keyFields, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   // читать будем в копию объекта - чтобы src не сбивался
   object = src.GetSession().CreateObject(src.GetObjectDef()->name, false);
   loader = object->GetObjectLoader();

   if( loader == NULL )
      return false;

   GRServer::Format *format = object->Self()->format;
   responseIdx = format->FindMember(SERV_RESPONSE);
   resultIdx = format->FindMember(SERV_RESULT);

   const IObjectData *od = object->GetObjectDef();
   
   std::wstring whereStr;
   PrepareParams(&whereStr, &params, keyFields, flavor, od->fields, *format, true);
   if( params.size() != keyFields.size() )
      return false;

	std::wstring stmt;
   return PrepareRead(&stmt, *object, whereStr, hDbc, flavor);
}

bool ResultReader::ExecuteReader(const std::wstring &stmt)
{
   SQLRETURN rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
	BindParams();
	return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

bool ResultReader::Read(Object* dest, const Object& src, const ExecProcBinder::Result& res)
{
   if( loader == NULL )
      return false;

   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
      (*i)->Write(src);

   SQLRETURN rc = SQLExecute(hstmt);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
   {
      if( MoveNext(NULL) && ReadBinder::Read(dest) )
         loader->LoadObject(dest);
   }

   if( responseIdx >= 0 )
      dest->at(responseIdx).str->assign(res.response);

   if( resultIdx >= 0 )
      dest->at(resultIdx).number = res.result;

   return true;
}

//
//----------------------------------------- ObjSource ----------------------------------------
//
bool ObjSource::Prepare(const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   std::wstring writeProc;
   std::vector<std::wstring> keyFields;

   const Parameter* p = parameters.Find(OBJ_WRITER_PARAM, -1);
   if( p != NULL )
   {
      CString *res = NULL;
      if( object.GetSession().Parse(&res, p->value, &object) )
         writeProc = (const std::wstring&)*res;
      delete res;
   }

   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
   {
      IObjectData::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
      if( keyI != od->members.end() )
         PKToList(&keyFields, keyI->second, false);
   }

   if( writeProc.empty() )
   {
      errStr = L"Нет параметра objWriter в описании";
      return false;
   }

   if( keyFields.size() == 0 )
   {
      errStr = L"Не определен primaryKey у объекта";
      return false;
   }

   bool res = binder.Prepare(object, writeProc, keyFields, hDbc, flavor);
   res = reader.Prepare(object, keyFields, hDbc, flavor);

   return res;
}

ExchangeList* ObjSource::Do(ISessionObject *src, const std::wstring& action, IFormatHolder* f, BSTR* msg)
{
   if( action.compare(WRITE_OBJECTS) != 0 )
   {
      *msg = SysAllocString(L"Поддерживается только команад записи");
      return NULL;
   }
   if( !errStr.empty() )
   {
      *msg = SysAllocString(errStr.c_str());
      return NULL;
   }

   USES_CONVERSION;
   char buf[300], objName[100];
	strcpy(objName, W2A(src->Self()->Name().c_str()));
   sprintf(buf, "start obj writing %s", objName);
   AddToLog(IErrorLogger::Full, buf);

   FormatList* fl = objCreator.GetFormatList();
   fl->SetHolder(f);
   ExchangeList* ret = new ExchangeList(fl);
   if( src->Writing() )
   {
      ServObject* so = src->Self();

      ServObject* resObj = new ServObject(so->format);
      ret->push_back(resObj);

      ServObject::const_iterator si = so->begin();
      for( ; si != so->end(); si++ )
      {
         const Object& o = *(*si);
         ExecProcBinder::Result res;
         if( !binder.Do(o, &res) )
            continue;

         Object* obj = resObj->AddObject();
         reader.Read(obj, o, res);

			std::string msg = "response ";
			msg += W2A(res.response.c_str());
         sprintf(buf, " (%d)", (int)res.result);
			msg += buf;
			AddToLog(IErrorLogger::Full, msg.c_str());
      }
   }
   sprintf(buf, "end obj writing %s", objName);
   AddToLog(IErrorLogger::Full, buf);

   return ret;
}

void ObjSource::Close()
{
   binder.Close();
   reader.Close();
}

IDataSource::IObjSource* SQLSource::CreateObjSource(const ParamList& parameters, const ISessionObject& object) const
{
   ObjSource *os = new ObjSource();
   if(! os->Prepare(parameters, object, GetHDBC(), GetFlavor()) )
   {
      delete os;
      os = NULL;
   }
   return os;
}

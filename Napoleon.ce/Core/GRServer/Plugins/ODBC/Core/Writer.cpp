/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"
#include "Writer.h"
#include <ServerDefs.h>

using namespace GRServer;
using namespace std;

class RemoveFKBinder : public ParamBinder
{
public:
   bool PrepareFKRemove(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);
   bool Remove(const Object& parentObj);
};

class ChildWriter : public Writer
{
public:
   ChildWriter(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);
   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close() { remover.Close(); Writer::Close(); }

protected:
   int childIndex;
   RemoveFKBinder remover;
};

class Remover : public IDataSource::IRemover
{
public:
   Remover(const ISessionObject& object, SQLHDBC hDbc);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() { SQLFreeHandle(SQL_HANDLE_STMT, hstmt); }

protected:
   const ISessionObject& object;
   std::wstring tableName;
   SQLHSTMT hstmt;
};

//
// -------------------------------------------- WriteBinder --------------------------------------
//
WriteBinder::~WriteBinder()
{
   std::vector<FileField*>::iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      delete (*fi);
   files.clear();
}

bool WriteBinder::PrepareWrite(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   std::wstring keyFilter;
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return false;

   return CreateStmt(object, hDbc, flavor);
}

static bool HaveField(const IObjectData::Fields& fields, const IObjectData::Field& fld)
{
	IObjectData::Fields::const_iterator i = fields.begin();
	for (; i != fields.end(); i++)
		if (i->format.name.compare(fld.format.name) == 0)
			return true;

	return false;
}

bool WriteBinder::CreateStmt(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   //   "{ call Ins$Proc(?,?) }";
	std::vector<std::wstring> keyFields;
	std::vector<std::wstring> allFields;

	const IObjectData* od = object.GetObjectDef();
   Format *format = object.Self()->format;
   CVector<IObjectData::Field>* sfkFields = NULL;

   wstring procName;
	std::wstring stmt, values;
	
	bool haveFK = (od->LoadFK(NULL, &sfkFields) && (object.Parent() != NULL));

	IObjectData::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
	if (keyI != od->members.end())
		PKToList(&keyFields, keyI->second, false);

	const wchar_t* upsert = flavor->UpsertSTMT(haveFK);
	if( upsert != NULL )
	{
		procName = od->tableName;
		QuoteString(&procName);
		stmt = upsert; stmt += L" "; stmt += procName; stmt += L" (";
		values = L" ) VALUES (";
	} else 
	{
		GetUpsertProcName(&procName, *od);
		if( flavor->QuoteTableName() )
			QuoteString(&procName);
		stmt = L"{ call "; stmt += procName; stmt += L" (";
	}

   IObjectData::Fields src(od->fields);
   bool added = AddOrderedField(src, *od);

   IObjectData::Fields::const_iterator ofi = src.begin();
   for( ; ofi != src.end(); ofi++ )
   {
      FieldBinder* fb = NULL;
      if (added && ofi->format.name.compare(ORDERED_FIELD) == 0)
      {
         fb = FieldBinder::OrderBinder(&orderIndex, (int)(params.size() + 1));
      }
      else
      {
         int idx = format->FindMember(ofi->format.name.c_str());
         if (ofi->CanCreate() == false)
         {
            if ((ofi->flags & IObjectData::Field::File) != 0 && !ofi->src.empty())
            {
               int srcidx = format->FindMember(ofi->src.c_str());
               if (srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString)
               {
                  std::string folder;
                  SetFileFieldBaseFolder(&folder, *ofi, gServer->GetConfig());
                  files.push_back(new FileField(srcidx, idx, folder.c_str(), gServer));
               }
            }
            continue;
         }
         if (idx >= 0)
            fb = flavor->GetBinder(*ofi, DEFAULT_STRING_LENGTH, idx, (int)(params.size() + 1));
      }

		if( upsert != NULL )
		{
			if( fb != NULL )
			{
				wstring qname;
				QuoteString(&qname, ofi->format.name);
				stmt += qname; stmt += L",";
				values += L"?,";
				params.push_back(fb);

				allFields.push_back(ofi->format.name);
			}
		} else
		{
			if( fb == NULL )
				stmt += L"null,";
			else
			{
				stmt += L"?,";
				params.push_back(fb);
			}
		}
   }

   if( haveFK )
   {
      fkIndex = (DWORD)params.size();

      Format *parentFormat = object.Parent()->Self()->format;
      CVector<IObjectData::Field>::const_iterator fki = sfkFields->begin();
      for( ; fki != sfkFields->end(); fki++ )
      {
			if (HaveField(od->fields, *fki))
				continue;

         size_t pos = fki->format.name.find_last_of(L'$');
         const std::wstring& pname = fki->format.name.substr(pos+1);

         FieldBinder* fb = NULL;
         int idx = parentFormat->FindMember(pname.c_str());
         if( idx >= 0 )
            fb = flavor->GetBinder(*fki, DEFAULT_STRING_LENGTH, idx, (int)(params.size() + 1));

			if( upsert != NULL )
			{
				if( fb != NULL )
				{
					wstring qname;
					QuoteString(&qname, fki->format.name);
					stmt += qname; stmt += L",";
					values += L"?,";
					params.push_back(fb);
				}
			} else
			{
				if( fb == NULL )
					stmt += L"null,";
				else
				{
					stmt += L"?,";
					params.push_back(fb);
				}
			}
      }
   }
   delete sfkFields;

   stmt.erase(stmt.size()-1);
	if( upsert != NULL )
	{
		values.erase(values.size() - 1);
		stmt += values; stmt += L")";
	} else
		stmt += L") }";

	flavor->UpsertOnConflict(haveFK, &stmt, keyFields, allFields);

	SQLRETURN rc = SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
	if (!(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO))
	{
		if (gServer->GetConfig().Debug() == IErrorLogger::Full)
			AddErrorsToLog(false, SQL_HANDLE_DBC, hDbc);
	}
	else
	{
		rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
		if (!(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO))
		{
			if (gServer->GetConfig().Debug() == IErrorLogger::Full)
				AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
		}
		else
		{
			BindParams();
		}
	}
	return (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
}

static void DumpObject(const Object& o)
{
#ifdef DEBUG
	for (unsigned i = 0; i < o.format.size(); i++)
	{
		const MemberFormat& mf = o.format.at(i);
		const Member& m = o.at(i);

		std::wstring val = mf.name; val += L":";

		switch (mf.type)
		{
		case MemberFormat::mtString:
			val += L"'";  val += m.str->c_str(); val += L"'";
			break;
		case MemberFormat::mtNumber:
		{
			wchar_t buf[50];
			swprintf(buf, 50, L"%f", m.number);
			val += buf;
			break;
		}
		case MemberFormat::mtDateTime:
		{
			wchar_t buf[50];
			swprintf(buf, 50, L"%lld", m.datetime);
			val += buf;
			break;
		}
		}
		USES_CONVERSION;
		AddToLog(IErrorLogger::Full, W2A(val.c_str()));
	}
#endif
}

bool WriteBinder::Write(const Object& o, const Object* parent)
{
	//return false;
	DWORD idx = 0;

   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++, idx++ )
      (*i)->Write((idx<fkIndex) ? o : *parent);

   SQLRETURN rc = SQLExecute(hstmt);

   if( rc == SQL_NEED_DATA )
   {
      FieldBinder* param;
      while( (rc = SQLParamData(hstmt, (SQLPOINTER*)&param)) == SQL_NEED_DATA )
      {
         param->PutData(hstmt, o);
      }
      //rc = SQLExecute(hstmt);
   }

   if( rc != SQL_SUCCESS && rc != SQL_NO_DATA )
   {
		if (gServer->GetConfig().Debug() == IErrorLogger::Full)
		{
			AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
			DumpObject(o);
		}
   }
   
   while( SQLMoreResults(hstmt) == SQL_SUCCESS )
      ;

	bool ret = false;
   if(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO || rc == SQL_NO_DATA)
   {
		ret = true;
      std::vector<FileField*>::iterator fi = files.begin();
      for( ; fi != files.end(); fi++ )
			if (!(*fi)->WriteFile(o))
			{
				gServer->AddError(false, "Error while writing file");
				ret = false;
			}
   }
	
	return ret;
}


//
// -------------------------------------------- Writer --------------------------------------
//
Writer::Writer(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor) : 
   doCount(0),
#ifdef DEBUG
   totalCount(0),
#endif
   writer(NULL)
{
   this->hDbc = hDbc;
   this->flavor = flavor;

	USES_CONVERSION;
	char msg[1000];
	sprintf(msg, "Write obj %s", W2A(object.Self()->format->name.c_str()));
	AddToLog(IErrorLogger::Full, msg);
}

Writer::~Writer()
{
	Close();
}

bool Writer::Prepare(const ISessionObject& object)
{
	rootObject = (object.Parent() == NULL);
	if (rootObject) {
		const IObjectData* od = object.GetObjectDef();
		
		//USES_CONVERSION;
		//tableName = W2A(od->tableName.c_str());
		//gServer->AddLog(IErrorLogger::Full, "Writer::Prepare ReqSTMT %s", tableName.c_str());
		//RequestSTMT();
	}

	writer = new WriteBinder();
   if( !writer->PrepareWrite(object, hDbc, flavor) )
      return false;

   if( rootObject )
      SQLSetConnectAttr(hDbc, SQL_ATTR_AUTOCOMMIT, (SQLPOINTER)SQL_AUTOCOMMIT_OFF, 0);

   writer->orderIndex = 0;
   //AddToLog(IErrorLogger::Full, "Preparing write");
   return true;
}

bool Writer::Write(const Object& o, const Object* parent, RowID *rid)
{
   if( !writer )
      return false;

	//AddToLog(IErrorLogger::Full, "Writing....");
   writer->orderIndex++;
   bool ret = writer->Write(o, parent);
   if( ret )
   {
      WriterList::iterator ci = childs.begin();
      for( ; ret && ci != childs.end(); ci++ )
         ret = (*ci)->Write(o, NULL);
   
		char msg[200];
		sprintf(msg, "Writed %d", ret ? 1 : 0);
		//AddToLog(IErrorLogger::Full, msg);
	}
#ifdef DEBUG
   if( rootObject )
      totalCount++;
#endif
   if( rootObject && doCount++ > MAX_DO_COUNT )
   {
      doCount = 0;
      SQLEndTran(SQL_HANDLE_DBC, hDbc, SQL_COMMIT);
   }

   return ret;
}

void Writer::Close()
{

	//AddToLog(IErrorLogger::Full, "Close writer");
   if( writer != NULL )
   {
      writer->Close();
      delete writer;
      writer = NULL;
   }
   if( rootObject )
   {
      SQLEndTran(SQL_HANDLE_DBC, hDbc, SQL_COMMIT);
      SQLSetConnectAttr(hDbc, SQL_ATTR_AUTOCOMMIT, (SQLPOINTER)SQL_AUTOCOMMIT_ON, 0);
		//gServer->AddLog(IErrorLogger::Full, "Writer::Close ReleaseSTMT %s", tableName.c_str());
		//ReleaseSTMT();
	}
}

//
// -------------------------------------------- RemoveFKBinder --------------------------------------
//
bool RemoveFKBinder::PrepareFKRemove(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   const IObjectData* od = object.GetObjectDef();
   const ISessionObject* parent = object.Parent();
   if( od == NULL || parent == NULL )
      return false;
   CVector<IObjectData::Field>* fkFields = NULL;
   bool ret = od->LoadFK(NULL, &fkFields);

   if( ret )
   {
      wstring paramStmt;
      Format *pf = parent->Self()->format;

      CVector<IObjectData::Field>::const_iterator fi = fkFields->begin();
      for( ; fi != fkFields->end(); fi++ )
      {
         size_t pos = fi->format.name.find_last_of(L'$');
         const std::wstring& pname = fi->format.name.substr(pos+1);
         int fldIndex = pf->FindMember(pname.c_str());
         if( fldIndex < 0 )
            continue;

         FieldBinder* fb = flavor->GetBinder(*fi, DEFAULT_STRING_LENGTH, fldIndex, (int)(params.size() + 1));
         if( fb != NULL )
            AddParam(&paramStmt, fb);
      }

      SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
      if( ret )
      {
         wstring tn;
         wstring stmt(L"DELETE FROM "); stmt += QuoteString(&tn, od->tableName); stmt += L" WHERE "; stmt += paramStmt;

         SQLRETURN rc = SQLPrepare(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
         ret = (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
      }
		if (ret)
			ret = BindParams();
	}

   delete fkFields;
   return ret;
}

bool RemoveFKBinder::Remove(const Object& parentObj)
{
   if( !WriteParams(parentObj) )
      return false;

   SQLRETURN rc = SQLExecute(hstmt);
   return (rc == SQL_SUCCESS || rc == SQL_NO_DATA || rc == SQL_SUCCESS_WITH_INFO);
}

//
// -------------------------------------------- OleChildWriter --------------------------------------
//
ChildWriter::ChildWriter(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor) :
   Writer(object, hDbc, flavor),
   childIndex(-1)
{
}

bool ChildWriter::Prepare(const ISessionObject& object)
{
   if( !remover.PrepareFKRemove(object, hDbc, flavor) )
      return false;

   const IObjectData* od = object.GetObjectDef();
   //int off = od->tableName.find_last_of(L'$');
   //childIndex = object.Parent()->Self()->format->FindMember(od->tableName.substr(off+1).c_str());
   size_t off = od->name.find_last_of(L'$');
   childIndex = object.Parent()->Self()->format->FindMember(od->name.substr(off+1).c_str());

   return (childIndex < 0 ) ? false : Writer::Prepare(object);
}

bool ChildWriter::Write(const Object& o, RowID *rid)
{
   if( childIndex < 0 )
      return false;

   if( !remover.Remove(o) )
      return false;

   bool res = true;
   const Member& m = o.at(childIndex);
   if( m.object != NULL )
   {
      writer->orderIndex = 0;
      ServObject::const_iterator i = m.object->begin();
      for (; res && i != m.object->end(); i++)
      {
         res = Writer::Write(*(*i), &o, NULL);
      }
   }

   return res;
}

//
// -------------------------------------------- OleRemover --------------------------------------
//
Remover::Remover(const ISessionObject& _object, SQLHDBC hDbc) :
   object(_object)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
      QuoteString(&tableName, od->tableName);
   SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
}

bool Remover::Remove(const wchar_t* filter)
{
   if( tableName.empty() )
      return false;

   std::wstring stmt(L"DELETE FROM "); stmt += tableName;
   if( filter != NULL && *filter != L'\0' )
   {
      CString parsedFilter, src(filter);
      object.PrepareFilterStr(&parsedFilter, src);
      stmt += L" WHERE "; stmt.append((const std::wstring&)parsedFilter);
   }

   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Remove stmt %s", W2A(stmt.c_str()));
   }
   SQLRETURN rc = SQLExecDirect(hstmt, (SQLWCHAR*)stmt.c_str(), SQL_NTS);
   bool ret = (rc == SQL_SUCCESS || rc == SQL_NO_DATA);
   if (!ret)
   {
      USES_CONVERSION;
      gServer->AddLog(IErrorLogger::Full, "Error stmt %s", W2A(stmt.c_str()));
      AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
   }
   //gServer->AddLog(IErrorLogger::Full, "Remove done %X", (int)this);
   //else 
   //{
   //   USES_CONVERSION;
   //   gServer->AddLog(IErrorLogger::Full, "Remove stmt %s", W2A(stmt.c_str()));
   //}
   return ret;
}

IDataSource::IWriter* GRServer::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   if( parent != NULL )
      return new ChildWriter(object, hDbc, flavor);
   
   return new Writer(object, hDbc, flavor);
}

IDataSource::IRemover* GRServer::CreateRemover(const ISessionObject& object, SQLHDBC hDbc)
{
   return new Remover(object, hDbc);
}

IDataSource::ISelector* GRServer::CreateSelector(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   return NULL;
}
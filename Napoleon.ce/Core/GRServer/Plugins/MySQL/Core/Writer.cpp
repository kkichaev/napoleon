/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * MySQLDB plugin
 *
 * ert   05/02/2013   creating
 */
#include "stdafx.h"
#include "Binder.h"
#include <ServerDefs.h>

using namespace std;
using namespace GRServer;

const ULONG MAX_DO_COUNT = 1000;


class MYSQLChildWriter : public MYSQLWriter
{
public:
   MYSQLChildWriter(const ISessionObject& object, MYSQL* connection);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);

protected:
   int childIndex;
   RemoveFKBinder remover;
};

class MYSQLRemover : public IDataSource::IRemover
{
public:
   MYSQLRemover(const ISessionObject& object, MYSQL* connection);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() {}

protected:
   std::string tableName;
   MYSQL* connection;
   const ISessionObject& object;
};

//
// -------------------------------------------- WriteBinder --------------------------------------
//
bool WriteBinder::PrepareWrite(const ISessionObject& object, MYSQL* db)
{
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return false;

   vector<wstring> keyFields;

   IObjectData::Members::const_iterator keyI = od->members.find(PRIMARY_KEY_STR);
   if( keyI != od->members.end() )
      PKToList(&keyFields, keyI->second, false);

   return CreateInsertStmt(object, keyFields, db);
}

bool WriteBinder::Write(const Object& o, const Object* parent)
{
	DWORD idx = 0;
	std::vector<FieldBinder*>::iterator i = params.begin();
	for (; i != params.end(); i++, idx++)
	{
		(*i)->Write((idx < fkIndex) ? o : *parent);
	}
	
	return (mysql_stmt_execute(stmt) == 0);
}

// INSERT INTO `table` (`f1`,...) VALUES (?,...) ON DUPLICATE KEY UPDATE `f1`=VALUES(`f1`), ...
bool WriteBinder::CreateInsertStmt(const ISessionObject& object, const vector<wstring>& keyFields, MYSQL* db)
{
   const IObjectData* od = object.GetObjectDef();
   Format *format = object.Self()->format;
	CVector<IObjectData::Field>* sfkFields = NULL;

   std::string dupStr;
   std::string valStr(") VALUES (");
   std::string sql("INSERT INTO ");
   QuoteString(&sql, od->tableName);
   sql += " (";

	bool haveFK = od->LoadFK(NULL, &sfkFields);

   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
		if (fi->CanCreate() == false)
			continue;

      int fldIndex = format->FindMember(fi->format.name.c_str());
      if( fldIndex < 0 )
         continue;

      FieldBinder* fb = FieldBinder::Create((*fi), fldIndex);
      if( fb == NULL )
         continue;

      params.push_back(fb);

      std::string fldName;
      QuoteString(&fldName, fi->format.name);
      sql += fldName; sql += ",";
      valStr += "?,";

      bool found = false;
      vector<wstring>::const_iterator ki = keyFields.begin();
      for( ; ki != keyFields.end(); ki++ )
      {
         if( ki->compare(fi->format.name) == 0 )
         {
            found = true;
            break;
         }
      }

      if( !found )
      {
         dupStr += fldName; 
         dupStr += "=VALUES("; 
         dupStr += fldName; 
         dupStr += "),";
      }
   }

	if (haveFK)
	{
		fkIndex = (DWORD)params.size();

		Format *parentFormat = object.Parent()->Self()->format;
		CVector<IObjectData::Field>::const_iterator fki = sfkFields->begin();
		for (; fki != sfkFields->end(); fki++)
		{
			size_t pos = fki->format.name.find_last_of(L'$');
			const std::wstring& pname = fki->format.name.substr(pos + 1);
			int idx = parentFormat->FindMember(pname.c_str());
			if (idx < 0)
				continue;

			FieldBinder* fb = FieldBinder::Create((*fki), idx);
			if (fb != NULL)
			{
				std::string fldName;
				QuoteString(&fldName, fki->format.name);
				sql += fldName; sql += ",";
				valStr += "?,";

				dupStr += fldName;
				dupStr += "=VALUES(";
				dupStr += fldName;
				dupStr += "),";

				params.push_back(fb);
			}
		}
	}
	delete sfkFields;


   sql.erase(sql.size() - 1);
   valStr.erase(valStr.size() - 1);
   sql += valStr; sql += ")";
   if( keyFields.size() > 0 && dupStr.size() > 0 )
   {
      sql += " ON DUPLICATE KEY UPDATE ";
      dupStr.erase(dupStr.size() - 1);
      sql += dupStr;
   }

   return PrepareStmt(db, sql);
}

//
// -------------------------------------------- RemoveFKBinder --------------------------------------
//
bool RemoveFKBinder::PrepareFKRemove(const ISessionObject& object, MYSQL *db)
{
   const IObjectData* od = object.GetObjectDef();
   const ISessionObject* parent = object.Parent();
   if( od == NULL || parent == NULL )
      return false;
   CVector<IObjectData::Field>* fkFields = NULL;
   bool ret = od->LoadFK(NULL, &fkFields);

   if( ret )
   {
      string paramStmt;
      Format *pf = parent->Self()->format;

      CVector<IObjectData::Field>::const_iterator fi = fkFields->begin();
      for( ; fi != fkFields->end(); fi++ )
      {
         int pos = fi->format.name.find_last_of(L'$');
         const std::wstring& pname = fi->format.name.substr(pos+1);
         int fldIndex = pf->FindMember(pname.c_str());
         if( fldIndex < 0 )
            continue;

         AddParam(&paramStmt, (*fi), fldIndex);
      }

      string stmt("DELETE FROM "); 
      QuoteString(&stmt, od->tableName); stmt += " WHERE "; stmt += paramStmt;

      ret = PrepareStmt(db, stmt);
   }

   delete fkFields;
   return ret;
}

bool RemoveFKBinder::Remove(const Object& parentObj)
{
   if( !WriteParams(parentObj) )
      return false;
   return (mysql_stmt_execute(stmt) == 0);
}

//
// -------------------------------------------- MYSQLWriter --------------------------------------
//
MYSQLWriter::MYSQLWriter(const ISessionObject& object, MYSQL* _connection) : 
   doCount(0),
   connection(_connection),
   writer(NULL)
{
}

bool MYSQLWriter::Prepare(const ISessionObject& object)
{
   writer = new WriteBinder();
   if( !writer->PrepareWrite(object, connection) )
      return false;

   Execute(connection, "BEGIN");
   return true;
}

bool MYSQLWriter::Write(const Object& o, const Object* parent, RowID *rid)
{
   if( !writer )
      return false;

   bool ret = writer->Write(o, parent);
   if( ret )
   {
      WriterList::iterator ci = childs.begin();
      for( ; ret && ci != childs.end(); ci++ )
         ret = (*ci)->Write(o, NULL);
   } else
   {
      AddErrorsToLog(false, connection, IErrorLogger::Short);
   }
   if( doCount++ > MAX_DO_COUNT )
   {
      doCount = 0;
      Execute(connection, "COMMIT");
   }

   return ret;
}

void MYSQLWriter::Close()
{
   if( writer != NULL )
   {
      writer->Close();
      delete writer;
      writer = NULL;
   }
   Execute(connection, "COMMIT");
}

//
// -------------------------------------------- MYSQLChildWriter --------------------------------------
//
MYSQLChildWriter::MYSQLChildWriter(const ISessionObject& object, MYSQL* connection) :
   MYSQLWriter(object, connection),
   childIndex(-1)
{
}

bool MYSQLChildWriter::Prepare(const ISessionObject& object)
{
   //ISessionObject* parent = object.Parent();
   //if( parent == NULL )
   //   return false;
   //const IObjectData* parentOD = parent->GetObjectDef();
   //if( parentOD == NULL )
   //   return false;
   //
   if( !remover.PrepareFKRemove(object, connection) )
      return false;

   const IObjectData* od = object.GetObjectDef();
   int off = od->tableName.find_last_of(L'$');
   childIndex = object.Parent()->Self()->format->FindMember(od->tableName.substr(off+1).c_str());

   return (childIndex < 0 ) ? false : MYSQLWriter::Prepare(object);
}

bool MYSQLChildWriter::Write(const Object& o, RowID *rid)
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
         res = MYSQLWriter::Write(*(*i), &o, NULL);
   }

   return res;
}

//
// -------------------------------------------- MYSQLRemover --------------------------------------
//
MYSQLRemover::MYSQLRemover(const ISessionObject& _object, MYSQL* _connection) :
   connection(_connection),
	object(_object)
{
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL )
      QuoteString(&tableName, od->tableName);
}

bool MYSQLRemover::Remove(const wchar_t* filter)
{
   if( tableName.empty() )
      return false;

   std::string stmt("DELETE FROM "); stmt += tableName;
   if( filter != NULL && *filter != '\0' )
   {
      USES_CONVERSION;
      stmt += " WHERE ";
      CString parsedFilter, src(filter);
      object.PrepareFilterStr(&parsedFilter, src);
		stmt += W2U(parsedFilter.c_str());
   }
   return Execute(connection, stmt);
}

IDataSource::IWriter* GRServer::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, MYSQL* connection)
{
   if( parent != NULL )
      return new MYSQLChildWriter(object, connection);
   
   return new MYSQLWriter(object, connection);
}

IDataSource::IRemover* GRServer::CreateRemover(const ISessionObject& object, MYSQL* connection)
{
   return new MYSQLRemover(object, connection);
}
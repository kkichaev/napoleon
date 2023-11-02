/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * MySQLDB plugin
 *
 * ert   01/02/2013   creating
 */
#include "stdafx.h"
#include "Binder.h"

using namespace std;
using namespace GRServer;

class MYSQLChildReader : public MYSQLReader
{
public:
   MYSQLChildReader(const ISessionObject& object, MYSQL* connection) : MYSQLReader(object, connection, NULL)
	{
		//PrepareBinder("");
	}

   virtual ~MYSQLChildReader() {} 

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }

   virtual Binder* CreateBinder() const { return new ChildBinder(); }
};

bool ChildBinder::PrepareFKStmt(std::string* paramStmt, const ISessionObject& obj)
{
   CVector<IObjectData::Field>* fkFields = NULL;
   const IObjectData* od = obj.GetObjectDef();
   ISessionObject* parent = obj.Parent();
   if( parent == NULL || !od->LoadFK(NULL, &fkFields) )
   {
      delete fkFields;
      return false;
   }

   Format* format = parent->Self()->format;
   paramStmt->clear();
   
   vector<IObjectData::Field>::const_iterator fi = fkFields->begin();
   for( ; fi != fkFields->end(); fi++ )
   {
      const MemberFormat &mf = fi->format;

      int pos = mf.name.find_last_of(L'$');
      const std::wstring& pname = mf.name.substr(pos+1);
      int fldIndex = format->FindMember(pname.c_str());
      if( fldIndex < 0 )
         continue;

      AddParam(paramStmt, (*fi), fldIndex);
   }
   delete fkFields;

   return true;
}

MYSQLReader::MYSQLReader(const ISessionObject &object, MYSQL *connection, const CString* filter) :
   obj(object),
   db(connection),
   binder(NULL)
{
   if( filter != NULL )
   {
      this->filter = filter->c_str();
   }
}

void MYSQLReader::Close()
{
   if( binder )
   {
      binder->Close();
      delete binder;
      binder = NULL;
   }
}

void MYSQLReader::Remove()
{
   USES_CONVERSION;
   const IObjectData* od = obj.GetObjectDef();
   wstring tableName;
   QuoteString(&tableName, od->tableName);

   string stmt("DELETE FROM "); stmt += W2U(tableName.c_str());
   if( parsedFilter.empty() == false )
   {
      stmt += " WHERE ";
      stmt += parsedFilter;
   }
   Execute(db, stmt);
}

const MemberFormat* MYSQLReader::Type(const wchar_t* name) const
{
   return (binder) ? binder->FieldType(name) : NULL;
}

const Member* MYSQLReader::Value(const wchar_t* name) const
{
   return (binder) ? binder->Value(name) : NULL;
}

bool MYSQLReader::PrepareBinder(const std::string& parsedFilter)
{
	binder = CreateBinder();
	return binder->PrepareRead(obj, (const std::string&)parsedFilter, db);
}

bool MYSQLReader::MoveNext(Object *parentObject)
{
   if( !binder )
   {
		USES_CONVERSION;

		CString tStr;
		obj.PrepareFilterStr(&tStr, filter);
		parsedFilter = W2U(tStr.c_str());

		const IObjectData* od = obj.GetObjectDef();
		if ((od->flags & IObjectDef::RemoveOnCommit) != 0)
		{
			wstring tableName;
			string objSendField;
			QuoteString(&tableName, od->tableName);
			objSendField = W2U(SENDED_FIELDS);
			QuoteString(&objSendField);

			string stmt("UPDATE "); stmt += W2U(tableName.c_str()); stmt += " SET "; stmt += objSendField; stmt += " = 1";
			if (parsedFilter.empty() == false)
			{
				stmt += " WHERE ";
				stmt += parsedFilter;
			}
			Execute(db, stmt);
			parsedFilter = objSendField; parsedFilter.append(" = 1");
		}

		if (!PrepareBinder(parsedFilter))
			return false;
	}
   return binder->MoveNext(parentObject);
}

bool MYSQLReader::Get(Object* o) const
{
   return (binder) ? binder->Read(o) : false;
}

IDataSource::IReader* GRServer::CreateReader(const ParamList& parameters, const ISessionObject& object, MYSQL* connection)
{
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return NULL;

   if( object.Parent() != NULL )
      return new MYSQLChildReader(object, connection);

   CString *filter = NULL;
   const Parameter *filterP = parameters.Find(L"readFilter", 0);
   if( filterP != NULL )
      object.GetSession().Parse(&filter, filterP->value, &object);

   IDataSource::IReader* ret = new MYSQLReader(object, connection, filter);
   delete filter;
   return ret;
}
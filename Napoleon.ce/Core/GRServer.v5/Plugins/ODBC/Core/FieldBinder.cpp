/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"
#include "Source.h"
#include "Binder.h"

using namespace GRServer;
using namespace std;

class StringBinder : public FieldBinder
{
public:
   StringBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos, bool rebindOnExecute);
   ~StringBinder() { free(buf); }

   virtual void Read(Object* obj);
   virtual void Write(const Object& obj);

	virtual void WriteFrom(const Token& src)
	{
		if (src.type == Token::ttString)
		{
			wcsncpy(buf, src.value.str->c_str(), length);
			*(buf + length) = L'\0';
			ind = SQL_NTS;
			if (rebindOnExecute)
				SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_WCHAR, SQL_VARCHAR, length, 0, buf, length * sizeof(wchar_t), &ind);
		}
	}

   virtual bool BindRead(HSTMT stmt)
   {
      return (SQLBindCol(stmt, bindPos, SQL_C_WCHAR, buf, length * sizeof(wchar_t), &ind) == SQL_SUCCESS);
   }

   virtual bool BindWrite(HSTMT stmt)
   {
		this->stmt = stmt;
		return (SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_WCHAR, SQL_VARCHAR, length, 0, buf, length * sizeof(wchar_t), &ind) == SQL_SUCCESS);
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtString;
   }

   virtual void GetValue(Member* value) const
   {
      std::wstring tb;
      if( ind > 0 )
         tb.assign(buf, ind/2);

      value->str->assign(tb);
   }

   virtual void GetData(SQLHSTMT hstmt, Member* value)
   {
      if( SQLGetData(hstmt, bindPos, SQL_C_WCHAR, buf, length + 1, &ind) == SQL_SUCCESS )
         value->str->assign(buf);
   }

   virtual void PutData(SQLHSTMT hstmt, const Object& obj) {}

private:
   wchar_t *buf;
   int length;
	HSTMT stmt;
	bool rebindOnExecute;
};

class HexBinder : public FieldBinder
{
public:
	HexBinder(const IObjectData::Field& format, int objPos, int bindPos) :
		FieldBinder(format, objPos, bindPos)
	{
	}

	virtual void Read(Object* obj)
	{
		if (ind == SQL_NULL_DATA) *buf = '\0';
		
		char* ep;
		obj->at(objPos).number = strtol(buf, (char**)&ep, 16);
	}

	virtual void WriteFrom(const Token& src)
	{
		if (src.type == Token::ttNumber)
		{
			ind = 0;
			sprintf(buf, "%X", (unsigned)(src.value.number + 0.5));
		}
	}

	virtual void Write(const Object& obj)
	{
		ind = 0;
		sprintf(buf, "%X", (unsigned)(obj.at(objPos).number + 0.5));
	}

	virtual bool BindRead(HSTMT stmt)
	{
		return (SQLBindCol(stmt, bindPos, SQL_C_CHAR, buf, sizeof(buf), &ind) == SQL_SUCCESS);
	}

	virtual bool BindWrite(HSTMT stmt)
	{
		return (SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_CHAR, SQL_VARCHAR, sizeof(buf), 0, buf, sizeof(buf), &ind) == SQL_SUCCESS);
	}

	virtual void GetType(MemberFormat *type) const
	{
		type->name = name;
		type->type = MemberFormat::mtNumber;
		type->format.fraction = 0;
	}

	virtual void GetValue(Member* value) const
	{
		if (ind != SQL_NULL_DATA)
		{
			char* ep;
			value->number = strtol(buf, (char**)&ep, 16);
		}
		else
			value->number = 0;
	}

	virtual void GetData(SQLHSTMT hstmt, Member* value)
	{
		SQLRETURN ret = SQLGetData(hstmt, bindPos, SQL_C_CHAR, buf, sizeof(buf), &ind);
		if (ret != SQL_SUCCESS)
			AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
		if (ind == SQL_NULL_DATA || (ret != SQL_SUCCESS && ret != SQL_SUCCESS_WITH_INFO))
			value->number = 0;
		else
		{
			char* ep;
			value->number = strtol(buf, (char**)&ep, 16);
		}
	}

	virtual void PutData(SQLHSTMT hstmt, const Object& obj) {}

private:
	char buf[20];
};

class NumberBinder : public FieldBinder
{
public:
   NumberBinder(const IObjectData::Field& format, int objPos, int bindPos) : 
      FieldBinder(format, objPos, bindPos)
   {
      prec = format.format.format.fraction;
		//bindAsDouble = true;
   }

   virtual void Read(Object* obj)
   {
      if( ind == SQL_NULL_DATA ) buf = 0;
      obj->at(objPos).number = buf;
   }

	virtual void WriteFrom(const Token& src)
	{
		if (src.type == Token::ttNumber)
		{
			ind = 0;
			buf = src.value.number;
		}
	}

   virtual void Write(const Object& obj)
   {
      ind = 0;
		buf = obj.at(objPos).number;
   }

   virtual bool BindRead(HSTMT stmt)
   {
      return (SQLBindCol(stmt, bindPos, SQL_C_DOUBLE, &buf, sizeof(buf), &ind) == SQL_SUCCESS);
   }

   virtual bool BindWrite(HSTMT stmt)
   {
		return (SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_DOUBLE, SQL_FLOAT, 0, prec, &buf, 0, &ind) == SQL_SUCCESS);
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = prec;
   }

   virtual void GetValue(Member* value) const
   {
      if( ind != SQL_NULL_DATA )
         value->number = buf;
      else
         value->number = 0;
   }

   virtual void GetData(SQLHSTMT hstmt, Member* value)
   {
      SQLRETURN ret = SQLGetData(hstmt, bindPos, SQL_C_DOUBLE, &value->number, sizeof(value->number), &ind);
		if (ret != SQL_SUCCESS)
			AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt, IErrorLogger::Full);
      if( ind == SQL_NULL_DATA || (ret != SQL_SUCCESS && ret != SQL_SUCCESS_WITH_INFO))
         value->number = 0;
   }

   virtual void PutData(SQLHSTMT hstmt, const Object& obj) {}

protected:
   double buf;
	//int ibuf;
	//bool bindAsDouble;
   int prec;
};

class OrderIndexBinder : public NumberBinder
{
public:
   OrderIndexBinder(const IObjectData::Field& format, DWORD* idx, int bindPos) :
      NumberBinder(format, 0, bindPos) 
   {
      orderIndex = idx;
   }

   virtual void Read(Object* obj)
   {
      //if (ind == SQL_NULL_DATA) buf = 0;
      //obj->at(objPos).number = buf;
   }


   virtual void Write(const Object& obj)
   {
      ind = 0;
      buf = *orderIndex;
   }

private:
   DWORD* orderIndex;
};


class DateTimeBinder : public FieldBinder
{
public:
   DateTimeBinder(const IObjectData::Field& format, int objPos, int bindPos) : 
      FieldBinder(format, objPos, bindPos)
   {
   }

   virtual void Read(Object* obj)
   {
      if( ind == SQL_NULL_DATA )
      {
         // 1970-01-01
         *(__int64*)&buf = (__int64)116444736000000000;
      }
      obj->at(objPos).datetime = buf;
   }

	virtual void WriteFrom(const Token& src)
	{
		if (src.type == Token::ttDateTime)
		{
			ind = 0;
			buf = src.value.datetime; 
		}
	}

   virtual void Write(const Object& obj)
   {
      ind = 0;
      buf = obj.at(objPos).datetime;
   }

   virtual bool BindRead(HSTMT stmt)
   {
      return (SQLBindCol(stmt, bindPos, SQL_C_SBIGINT, &buf, sizeof(buf), &ind) == SQL_SUCCESS);
   }

   virtual bool BindWrite(HSTMT stmt)
   {
      return (SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_SBIGINT, SQL_BIGINT, 0, 0, &buf, sizeof(buf), &ind) == SQL_SUCCESS);
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtDateTime;
      type->format.dateFormat = MemberFormat::Stamp;
   }

   virtual void GetValue(Member* value) const
   {
      if( ind != SQL_NULL_DATA )
         value->datetime = buf;
      else
      {
         // 1970-01-01
         *(__int64*)&buf = (__int64)116444736000000000;
      }
   }

   virtual void GetData(SQLHSTMT hstmt, Member* value)
   {
      SQLGetData(hstmt, bindPos, SQL_C_SBIGINT, &value->datetime, sizeof(value->datetime), &ind);
      if( ind == SQL_NULL_DATA )
         *(__int64*)&value->datetime = (__int64)116444736000000000;
   }

   virtual void PutData(SQLHSTMT hstmt, const Object& obj) {}

private:
   FILETIME buf;
};

BinaryBinder::BinaryBinder(const IObjectData::Field& format, int objPos, int bindPos) : 
      FieldBinder(format, objPos, bindPos)
{
   length = 0;
}

void BinaryBinder::Read(Object* obj)
{
   Member& m = obj->at(objPos); 
   delete m.binary;
   m.binary = NULL;

   SQLRETURN rc = SQLGetData(hstmt, bindPos, SQL_C_BINARY, buf, 0, &ind);
	if (rc == -1)
		AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
	if (ind > 0)
   {
      Binary* ret = new Binary();
      BYTE *pb = ret->Alloc((DWORD)ind);
      rc = SQLGetData(hstmt, bindPos, SQL_C_BINARY, pb, ind, &ind);
      
      if( rc == -1 )
         AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);

      m.binary = new MemoryBinary();
      m.binary->Assign(ret);
   }
}

void BinaryBinder::PutData(SQLHSTMT hstmt, const Object& obj) 
{
   SQLPOINTER data;
   SQLINTEGER idata;
   const Member& m = obj.at(objPos); 
   if( m.binary == NULL )
   {
      idata = SQL_NULL_DATA;
      data = buf;
   }
   else
   {
      data = (SQLPOINTER)m.binary->Bytes();
      idata = m.binary->Size();
   }
   SQLPutData(hstmt, data, idata); 
}

bool BinaryBinder::BindWrite(HSTMT stmt)
{
   ind = SQL_DATA_AT_EXEC;
   hstmt = stmt;
   return (SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_BINARY, SQL_VARBINARY, 0, 0, this, 0, &ind) == SQL_SUCCESS);
}

void BinaryBinder::GetType(MemberFormat *type) const
{
   type->name = name;
   type->type = MemberFormat::mtBinary;
}

StringBinder::StringBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos, bool rebindOnExecute) :
   FieldBinder(format, objPos, bindPos)
{
   length = (format.width == 0) ? defaultLength : format.width;
   buf = (wchar_t*)malloc((length + 1) * sizeof(wchar_t));
	this->rebindOnExecute = rebindOnExecute;
}

void StringBinder::Read(Object* obj)
{
   std::wstring src;
   if( ind <= 0 )
      src.clear();
   else
      src.assign(buf, ind/2);

   obj->at(objPos).str->assign(src);
}

void StringBinder::Write(const Object& obj)
{
   const CString& src = *obj.at(objPos).str;
   wcsncpy(buf, src.c_str(), length);
   *(buf + length) = L'\0';
   ind = SQL_NTS;
	if (rebindOnExecute)
		SQLBindParameter(stmt, bindPos, inputOutputType, SQL_C_WCHAR, SQL_VARCHAR, length, 0, buf, length * sizeof(wchar_t), &ind);
}

FieldBinder::FieldBinder(const IObjectData::Field& format, int objPos, int bindPos)
{
   inputOutputType = SQL_PARAM_INPUT;
   this->ind = 0;
   this->objPos = objPos;
   this->bindPos = bindPos;
   this->name = format.format.name;
}

static IObjectData::Field orderField;
FieldBinder* FieldBinder::OrderBinder(DWORD* index, int bindPos)
{
   if (orderField.format.name.empty())
   {
      MemberFormat& mf = orderField.format;
      mf.name = ORDERED_FIELD;
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = 0;
      orderField.width = 0;
   }

   return new OrderIndexBinder(orderField, index, bindPos);
}

class ORADateTimeBinder : public FieldBinder
{
public:
	ORADateTimeBinder(const IObjectData::Field& format, int objPos, int bindPos) :
		FieldBinder(format, objPos, bindPos)
	{
	}

	virtual void Read(Object* obj)
	{
		if (ind == SQL_NULL_DATA)
		{
			// 1970-01-01
			strcpy(buf, "116444736000000000");
		}
		else
		{
			ConvertValue(buf);
		}
		__int64 val = _atoi64(buf);
		*(__int64*)&(obj->at(objPos).datetime) = val;
	}

	void ConvertValue(char *buf)
	{
		char *src = buf, *dest = buf;
		while (*src)
		{
			char sym = *src;
			if ((int)sym > 0 && isdigit(sym))
			{
				*dest = sym;
				dest++;
			}
			if (sym == 'E' || sym == 'e')
				break;
			src++;
		}
		while ((dest - buf) < 18)
			*dest++ = '0';
		*dest = '\0';
	}

	virtual void WriteFrom(const Token& src) {}
	virtual void Write(const Object& obj)
	{
		ind = SQL_NTS;
		_i64toa(*(__int64*)&(obj.at(objPos).datetime), buf, 10);
	}

	virtual bool BindRead(HSTMT stmt)
	{
		bool ret = (SQLBindCol(stmt, bindPos, SQL_C_CHAR, buf, sizeof(buf), &ind) == SQL_SUCCESS);
		if (!ret)
			GRServer::AddErrorsToLog(false, SQL_HANDLE_STMT, stmt);
		return ret;
	}

	virtual bool BindWrite(HSTMT stmt)
	{
		return (SQLBindParameter(stmt, bindPos, SQL_PARAM_INPUT, SQL_C_CHAR, SQL_VARCHAR, 0, 0, buf, 0, &ind) == SQL_SUCCESS);
	}

	virtual void GetType(MemberFormat *type) const
	{
		type->name = name;
		type->type = MemberFormat::mtDateTime;
		type->format.dateFormat = MemberFormat::Stamp;
	}

	virtual void GetValue(Member* value) const
	{
		if (ind == SQL_NULL_DATA)
		{
			// 1970-01-01
			strcpy(buf, "116444736000000000");
		}
		__int64 val = _atoi64(buf);
		*(__int64*)&(value->datetime) = val;
	}

	virtual void GetData(SQLHSTMT hstmt, Member* value)
	{
		SQLGetData(hstmt, SQL_C_DOUBLE, SQL_C_CHAR, buf, sizeof(buf), &ind);
		if (ind == SQL_NULL_DATA)
			*(__int64*)&value->datetime = (__int64)116444736000000000;
		else
		{
			__int64 val = _atoi64(buf);
			*(__int64*)&(value->datetime) = val;
		}
	}

	virtual void PutData(SQLHSTMT hstmt, const Object& obj) {}

private:
	mutable char buf[30];
};

class OraBinaryBinder : public BinaryBinder
{
public:
	virtual bool BindRead(HSTMT stmt)
	{
		hstmt = stmt;
		return (SQLBindCol(stmt, bindPos, SQL_C_BINARY, this, 0, &ind) == SQL_SUCCESS);
	}

	virtual void Read(Object* obj)
	{
		Member& m = obj->at(objPos);
		delete m.binary;
		m.binary = NULL;
		if (ind > 0)
		{
			Binary* ret = new Binary();
			BYTE *pb = ret->Alloc((DWORD)ind);
			SQLRETURN rc = SQLGetData(hstmt, bindPos, SQL_C_BINARY, pb, ind, &ind);

			if (rc == -1)
				AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);

			m.binary = new MemoryBinary();
			m.binary->Assign(ret);
		}
	}
};

FieldBinder* OracleFlavor::GetBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos)
{
	if (format.format.type == MemberFormat::mtDateTime)
		return new ORADateTimeBinder(format, objPos, bindPos);
	if (format.format.type == MemberFormat::mtString)
		return new StringBinder(format, defaultLength, objPos, bindPos, true);
	return ODBCFlavor::GetBinder(format, defaultLength, objPos, bindPos);
}


FieldBinder* ODBCFlavor::GetBinder(const IObjectData::Field& format, int defaultLength, int objPos, int bindPos)
{
   FieldBinder* ret = NULL;
   switch(format.format.type)
   {
   case MemberFormat::mtString:
      ret = new StringBinder(format, defaultLength, objPos, bindPos, false);
      break;
   case MemberFormat::mtNumber:
		if((format.flags & IObjectData::Field::Hex) != 0)
			ret = new HexBinder(format, objPos, bindPos);
		else
			ret = new NumberBinder(format, objPos, bindPos);
      break;
   case MemberFormat::mtDateTime:
      ret = new DateTimeBinder(format, objPos, bindPos);
      break;
   case MemberFormat::mtBinary:
      ret = new BinaryBinder(format, objPos, bindPos);
      break;
   }

   return ret;
}

//
// ------------------------------------ Binder ----------------------------------------------------
//
Binder::Binder() : hstmt(NULL)
{
	//recCount = 0;
}

bool Binder::MoveNext(Object *parentObject)
{
	//SQLRETURN rc;
	//return (hstmt == NULL) ? false : ((rc = SQLFetch(hstmt)) == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
	if (hstmt == NULL)
		return false;

	SQLRETURN rc = SQLFetch(hstmt);


	if (rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO)
	{
		//recCount++;
		return true;
	}

	if (rc == SQL_ERROR) // filter for SQL_NO_DATA
	{
		//gServer->AddLog(IErrorLogger::Full, "rc = %d, count = %d", rc, recCount);
		AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt);
	}

	return false;
}

void Binder::Close()
{
   std::vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      delete (*i);
   fields.clear();

   //std::vector<FileField*>::iterator fi = files.begin();
   //for( ; fi != files.end(); fi++ )
   //   delete (*fi);
   //files.clear();

   if( hstmt != NULL )
   {
      SQLFreeHandle(SQL_HANDLE_STMT, hstmt);
      hstmt = NULL;
   }
}

bool Binder::Read(Object* o) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      (*i)->Read(o);

   //std::vector<FileField*>::const_iterator fi = files.begin();
   //for( ; fi != files.end(); fi++ )
   //   if( !(*fi)->ReadFile(o) )
   //      gServer->AddError(false, "Error while reading file");

   return true;
}

const MemberFormat* Binder::FieldType(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if(_wcsicmp(fb->Name().c_str(), name) == 0 )
      {
         fb->GetType(&format);
         return &format;
      }
   }

   return NULL;
}

const Member* Binder::Value(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if(_wcsicmp(fb->Name().c_str(), name) == 0 )
      {
         value.str = &strValue;
         fb->GetValue(&value);
         return &value;
      }
   }

   return NULL;
}

bool Binder::BindFields()
{
   bool ret = true;
   vector<FieldBinder*>::iterator i = fields.begin();
   for( ; ret && i != fields.end(); i++ )
      ret = (*i)->BindRead(hstmt);

   return ret;
}

//
// ------------------------------------ ReadBinder ----------------------------------------------------
//
ReadBinder::ReadBinder()
{
}

bool ReadBinder::PrepareRead(std::wstring* stmt, const ISessionObject& obj, const std::wstring& filter, SQLHDBC hDbc, ODBCFlavor* flavor)
{
   wstring fields;
   if( !PrepareReadFields(&fields, obj, flavor) || fields.size() == 0 )
      return false;

	if (stmt->empty())
	{
		stmt->assign(L"SELECT ");

		wstring tname;
		stmt->append(fields); stmt->append(L" FROM "); stmt->append(QuoteString(&tname, obj.GetObjectDef()->tableName));
		if (filter.size() > 0)
		{
			stmt->append(L" WHERE ");
			stmt->append(filter);
		}

      const IObjectData* od = obj.GetObjectDef();
      if (od != NULL && od->IsOrderedSource())
      {
         stmt->append(L" ORDER BY \"").append(ORDERED_FIELD).append(L"\"");
      }
	}

   bool ret = OpenReader(hDbc, *stmt);
   //if( obj.Self()->format->name.compare(L"Agents") == 0 )
   //{
   //   USES_CONVERSION;
   //   gServer->AddError(false, "Open stmt %s", W2A(stmt.c_str()));
   //}
   return ret;
}

bool ReadBinder::PrepareReadFields(std::wstring* stmt, const ISessionObject& obj, ODBCFlavor* flavor)
{
   const IObjectData* od = obj.GetObjectDef();
   if( od == NULL )
      return false;

   GRServer::Format *format = obj.Self()->format;

	for (int pass = 0; pass < 2; pass++)
	{
		IObjectData::Fields::const_iterator fi = od->fields.begin();
		for (; fi != od->fields.end(); fi++)
		{
			int fldIndex = format->FindMember(fi->format.name.c_str());
			if (fldIndex < 0)
				continue;

			if (fi->CanCreate())
			{
				if ((pass == 0 && fi->format.type != MemberFormat::mtBinary) || (pass == 1 && fi->format.type == MemberFormat::mtBinary))
				{
					FieldBinder* fb = flavor->GetBinder(*fi, DEFAULT_STRING_LENGTH, fldIndex, (int)(fields.size() + 1));
					if (fb != NULL)
					{
						fields.push_back(fb);
						wstring name;
						stmt->append(QuoteString(&name, fi->format.name));
						stmt->append(L",");
					}
				}
			}
		}
	}

   stmt->erase(stmt->size() - 1, 1);
   return true;
}

bool ReadBinder::OpenReader(SQLHDBC hDbc, const std::wstring& stmt)
{
   bool res = false;
   SQLAllocHandle(SQL_HANDLE_STMT, hDbc, &hstmt);
   if( BindFields() && ExecuteReader(stmt) )
      res = true;

   if( !res )
   {
      if( GRServer::AddErrorsToLog(false, SQL_HANDLE_STMT, hstmt) )
      {
         USES_CONVERSION;
         gServer->AddError(false, "stmt %s", W2A_CP(stmt.c_str(), CP_UTF8));
      }
   }
   return res;
}

//
// ------------------------------------ ParamBinder ----------------------------------------------------
//
ParamBinder::ParamBinder()
{
}

bool ParamBinder::WriteParams(const Object& obj)
{
   bool ret = true;
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      (*i)->Write(obj);
   }

   return ret;
}

bool ParamBinder::BindParams()
{
   vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
      (*i)->BindWrite(hstmt);

   return true;
}

void ParamBinder::AddParam(std::wstring* paramStmt, FieldBinder* param)
{
   params.push_back(param);
   if( paramStmt != NULL )
   {
      wstring tstr;
      if( !paramStmt->empty() )
         paramStmt->append(L" AND ");
      
      paramStmt->append(QuoteString(&tstr, param->Name()));
      paramStmt->append(L" = ?");
   }
}

void ParamBinder::Close()
{
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
      delete (*i);
   params.clear();

   ReadBinder::Close();
}

FieldBinderOle* FieldBinderOle::Create(const MemberFormat& format, int index)
{
   return NULL;
}


//
// ------------------------------------ BinderBase ----------------------------------------------------
//
BinderBase::BinderBase() : data(NULL)
{
}

void BinderBase::Close()
{
   if( data == NULL )
      return;

   value.str = NULL;

   std::vector<FieldBinderOle*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
         delete (*i);
   fields.clear();

   reader.FreeRecordMemory();
   reader.Close();

   delete data;
   data = NULL;
}

bool BinderBase::CreateAccessor(ULONG bufSize)
{
   HRESULT hr;
   data = (BindData*)(new BYTE [bufSize]);
   hr = reader.CreateAccessor((int)fields.size(), data, bufSize);
   if( !SUCCEEDED(hr) )
   {
      delete data;
      data = NULL;
      return false;
   }
   
   BindData* cd = data;
   vector<FieldBinderOle*>::iterator i = fields.begin();
   int ord = 1;
   for( ; i != fields.end(); i++ )
   {
      (*i)->Bind(cd, reader, ord++);
      cd = (BindData*)((BYTE*)cd + (*i)->FieldLength());
   }

   return true;
}

const MemberFormat* BinderBase::FieldType(const wchar_t* name) const
{
   std::vector<FieldBinderOle*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinderOle* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->GetType(&format);
         return &format;
      }
   }

   return NULL;
}

const Member* BinderBase::Value(const wchar_t* name) const
{
   std::vector<FieldBinderOle*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinderOle* fb = (*i);
      if( fb->Name().compare(name) == 0 )
      {
         fb->GetValue(&value);
         return &value;
      }
   }

   return NULL;
}

bool BinderBase::Read(Object* o) const
{
   if( data == NULL )
      return false;

   bool ret = true;

   std::vector<FieldBinderOle*>::const_iterator i = fields.begin();
   for( ; ret && i != fields.end(); i++ )
   {
      if( (*i)->Read(o) == false )
         ret = false;
   }

   return ret;
}
//
// ------------------------------------ Binder ----------------------------------------------------
//
BinderOle::BinderOle()
{
}

bool BinderOle::PrepareReadStmt(ULONG* dataSize, std::wstring* stmt, const ISessionObject& obj)
{
   const IObjectData* od = obj.GetObjectDef();
   if( od == NULL )
      return false;

   GRServer::Format *format = obj.Self()->format;

   wstring name;
   *dataSize = 0;
   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      int fldIndex = format->FindMember(fi->format.name.c_str());
      if( fldIndex < 0 )
         continue;

      if( fi->CanCreate() )
      {
         const MemberFormat& mf = format->at(fldIndex);
         FieldBinderOle* fb = FieldBinderOle::Create(mf, fldIndex);
         if( fb != NULL )
         {
            fields.push_back(fb);
            (*dataSize) += fb->FieldLength();
            QuoteString(&name, mf.name);
            stmt->append(name);
            stmt->append(L",");
         }
      } else if( (fi->flags & IObjectData::Field::File) != 0 && !fi->src.empty() )
      {
         int srcidx = format->FindMember(fi->src.c_str());
         if( srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString )
         {
            std::string folder;
            SetFileFieldBaseFolder(&folder, *fi, gServer->GetConfig());
            files.push_back(new FileField(srcidx, fldIndex, folder.c_str(), gServer));
         }
      }
   }

   stmt->erase(stmt->size() - 1, 1);
   return true;
}

bool BinderOle::CreateReader(ULONG bufSize, const std::wstring& stmt, CSession& session)
{
   if( !CreateAccessor(bufSize) )
      return false;
   
   HRESULT hr = OpenReader(session, stmt);
   return (SUCCEEDED(hr) != 0);
}

bool BinderOle::PrepareRead(const ISessionObject& obj, const std::wstring& filter, CSession& session)
{
   wstring fields;
   ULONG bufSize = 0;

   if( !PrepareReadStmt(&bufSize, &fields, obj) || fields.size() == 0 )
      return false;

   wstring stmt(L"SELECT "), tname;
   stmt += fields; stmt += L" FROM ";
   QuoteString(&tname, obj.GetObjectDef()->tableName);
   stmt += tname;
   if( filter.size() > 0 )
   {
      stmt += L" WHERE ";
      stmt += filter;
   }

   return CreateReader(bufSize, stmt, session);
}

bool BinderOle::Read(Object* o) const
{
   if( !BinderBase::Read(o) )
      return false;

   std::vector<FileField*>::const_iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      if( !(*fi)->ReadFile(o) )
         gServer->AddError(false, "Error while reading file");

   return true;
}

void BinderOle::Close()
{
   BinderBase::Close();

   std::vector<FileField*>::iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      delete (*fi);
   files.clear();
}

//
// ------------------------------------ ParamBinderOle ----------------------------------------------------
//
ParamBinderOle::ParamBinderOle() : paramData(NULL), dataSize(0)
{
}

bool ParamBinderOle::WriteParams(const Object& obj)
{
   bool ret = true;
   std::vector<FieldBinderOle*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( !(*i)->Write(obj) )
         ret = false;
   }

   return ret;
}

bool ParamBinderOle::BindParams()
{
   HRESULT hr;
   paramData = (BindData*)(new BYTE [dataSize]);
   hr = reader.CreateParameterAccessor((int)params.size(), paramData, dataSize);
   if( !SUCCEEDED(hr) )
      return false;

   BindData* cd = paramData;
   vector<FieldBinderOle*>::iterator i = params.begin();
   int ord = 1;
   for( ; i != params.end(); i++ )
   {
      (*i)->BindParam(cd, reader, ord++);
      cd = (BindData*)((BYTE*)cd + (*i)->FieldLength());
   }

   return true;
}

void ParamBinderOle::AddParam(std::wstring* paramStmt, const MemberFormat& mf, int fldIndex)
{
   FieldBinderOle* fb = FieldBinderOle::Create(mf, fldIndex);
   if( fb == NULL )
      return;

   params.push_back(fb);
   dataSize += fb->FieldLength();

   if( paramStmt != NULL )
   {
      wstring tstr;
      QuoteString(&tstr, mf.name);
      if( !paramStmt->empty() )
         paramStmt->append(L" AND ");
      
      paramStmt->append(tstr);
      paramStmt->append(L" = ?");
   }
}

void ParamBinderOle::Close()
{
   //if( paramData != NULL )
   //{
   //   std::vector<FieldBinderOle*>::iterator i = params.begin();
   //   for( ; i != params.end(); i++ )
   //      delete (*i);
   //   params.clear();
   //   
   //   delete paramData;
   //   paramData = NULL;
   //}

   //Binder::Close();
}

//
// ------------------------------------ StreamWriter ----------------------------------------------------
//

ULONG	StreamWriter::Release(void)
{
   return --cRef;
}

HRESULT StreamWriter::QueryInterface( REFIID riid, void** ppv )
{
	*ppv = NULL;
	if ( riid == IID_IUnknown )			 *ppv = this;
	if ( riid == IID_ISequentialStream ) *ppv = this;
	if ( *ppv )
	{
		( (IUnknown*) *ppv )->AddRef();
		return S_OK;
	}
	return E_NOINTERFACE;
}

HRESULT StreamWriter::Read(void *pv, ULONG cb, ULONG* pcbRead)
{
	if ( pcbRead ) *pcbRead = 0;
	if ( 0 == cb ) return S_OK; 

   DWORD left = (src == NULL) ? 0 : src->Size() - cp;
   ULONG readed = cb > left ? left : cb;

	if ( 0 == left ) return S_FALSE;

   memcpy( pv, (void*)(src->Bytes() + cp), readed );
	cp += readed;

   if ( pcbRead ) *pcbRead = readed;
	if ( cb != readed )
      return S_FALSE; 
   return S_OK;
}

HRESULT StreamWriter::Write( const void *pv, ULONG cb, ULONG* pcbWritten )
{
   return S_OK;
}

inline bool IsLocalName(const char *fileName)
{
#ifdef UNIX
   return !( *fileName != '\0' && (*fileName == '/' || *fileName == '~') );
#else
   return !( *fileName != '\0' && fileName[1] != '\0' && ((*fileName == '\\' && fileName[1] == '\\') || fileName[1] == ':') );
#endif
}

void GRServer::SetFileFieldBaseFolder(std::string* dest, const IObjectData::Field& src, const IServerConfig& config)
{
	const char* imgFolder = config.ImageFolder();
	if (IsLocalName(imgFolder))
		dest->assign(config.ExchangeFolder()).append(imgFolder);
	else
		dest->assign(imgFolder);

	//dest->assign(exchangeFolder);
	//if( !src.baseFolder.empty() )
	//{
	//   USES_CONVERSION;
	//   const char* bf = W2A(src.baseFolder.c_str());
	//   if( IsLocalName(bf) )
	//      dest->append(bf);
	//   else
	//      dest->assign(bf);
	//}
}
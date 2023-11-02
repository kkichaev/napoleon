/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * MySQLDB plugin
 *
 * ert   01/02/2013   creating
 */
#include "stdafx.h"
#include "MySQLDrv.h"
#include "Binder.h"

using namespace std;
using namespace GRServer;

class StringBinder : public FieldBinder
{
public:
   StringBinder(const MemberFormat& format, int index) : 
      FieldBinder(format, index), curLength(250), fieldType(MYSQL_TYPE_STRING)
   {
      value = (char*)malloc(curLength);
   }

   StringBinder(const MemberFormat& format, int index, DWORD length, enum_field_types type) : 
      FieldBinder(format, index),
      fieldType(type)
   {
      curLength = length;
      value = (char*)malloc(curLength);
   }

   ~StringBinder() { free(value); }

   virtual bool Bind(MYSQL_BIND *bind)
   {
      bind->buffer_type = fieldType;
      bind->buffer = value;
      bind->buffer_length = curLength;

      bind->is_null= &isNull;
      bind->error= &isError;
      bind->length = &length;

      this->bind = bind;

      return true;
   }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
		USES_CONVERSION;
		const char* cp = W2U(m.str->c_str());
		length = strlen(cp);
		if (curLength < length)
      {
         free(value);
         value = (char*)malloc(length+1);
         curLength = length;
      }
      strcpy(value, cp);

      bind->buffer = value;
      bind->buffer_length = length;
      return true;
   }

   virtual bool Read(Object* m, MYSQL_STMT *stmt, int column)
   {
      if( isNull )
      {
         m->at(index).str->clear();
         return true;
      }

      if( isError )
      {
         if( curLength < length)
         {
            value = (char*)realloc(value, length);
            if( value == NULL )
            {
               curLength = 0;
               return false;
            }

            bind->buffer = value + curLength;
            bind->buffer_length = length - curLength;
            mysql_stmt_fetch_column(stmt, bind, column, curLength);

            curLength = length;
         } else
         {
            return false;
         }
      }

      bind->buffer = value;
      bind->buffer_length = curLength;

      WriteValue(m->at(index));
      return true;
   }

   virtual void WriteValue(Member& m)
   {
      USES_CONVERSION;
      m.str->assign(U2W(value));
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtString;
   }

   virtual void GetValue(Member* value) const
   {
      USES_CONVERSION;
      value->str->assign(U2W(this->value));
   }

   char* value;
   DWORD curLength;
   MYSQL_BIND *bind;
   enum enum_field_types fieldType;
};

class NumberBinder : public FieldBinder
{
public:
   NumberBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtNumber;
      type->format.fraction = 8;
   }

   virtual bool Bind(MYSQL_BIND *bind)
   {
      bind->buffer_type = MYSQL_TYPE_DOUBLE;
      bind->buffer = &value;
      bind->buffer_length = sizeof(value);

      bind->is_null= &isNull;
      bind->error= &isError;
      bind->length = &length;

      return true;
   }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      value = m.number;
      return true;
   }

   virtual bool Read(Object* m, MYSQL_STMT *stmt, int column)
   {
      m->at(index).number = (isNull) ? 0 : value;
      return true;
   }

   virtual void GetValue(Member* value) const
   {
      value->number = this->value;
   }

   double value;
};

class DateTimeBinder : public FieldBinder
{
public:
   DateTimeBinder(const MemberFormat& format, int index) : FieldBinder(format, index) {}

   virtual bool Bind(MYSQL_BIND *bind)
   {
      bind->buffer_type = MYSQL_TYPE_LONGLONG;
      bind->buffer = &value;
      bind->buffer_length = sizeof(value);

      bind->is_null= &isNull;
      bind->error= &isError;
      bind->length = &length;

      return true;
   }

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      *(__int64*)&value = *(__int64*)&m.datetime;
      return true;
   }

   virtual bool Read(Object* m, MYSQL_STMT *stmt, int column)
   {
      if( isNull )
      {
         SYSTEMTIME st = {0};
         st.wYear = 1970;
         st.wDay = 1;
         st.wMonth = 1;
         SystemTimeToFileTime(&st, &value);
      } 
      *(__int64*)&(m->at(index).datetime) = *(__int64*)&value;
      return true;
   }

   virtual void GetType(MemberFormat *type) const
   {
      type->name = name;
      type->type = MemberFormat::mtDateTime;
      type->format.dateFormat = MemberFormat::Stamp;
   }

   virtual void GetValue(Member* value) const
   {
      *(__int64*)&value->datetime = *(__int64*)&(this->value);
   }

   FILETIME value;
};


class BinaryBinder : public StringBinder
{
public:
   BinaryBinder(const MemberFormat& format, int index) : StringBinder(format, index, 1000, MYSQL_TYPE_BLOB) {}

   virtual bool Write(const Object& o)
   {
      const Member& m = o.at(index);
      if( m.binary == NULL || m.binary->Size() == 0 )
      {
         isNull = true;
      } else
      {
         isNull = false;
         length = m.binary->Size();
         bind->buffer = (void*)(m.binary->Bytes());
         bind->buffer_length = length;
      }
      return true;
   }

   virtual void WriteValue(Member& m)
   {
      Binary *b = new Binary();
      BYTE *pb = b->Alloc(length);
      memcpy(pb, value, length);

      if( m.binary == NULL )
         m.binary = new MemoryBinary();
      m.binary->Assign(b);
   }
};

FieldBinder* FieldBinder::Create(const IObjectData::Field& field, int index)
{
   const MemberFormat& format = field.format;
   switch(format.type)
   {
   case MemberFormat::mtString:
      return new StringBinder(format, index);
   case MemberFormat::mtNumber:
      return new NumberBinder(format, index);
   case MemberFormat::mtDateTime:
      return new DateTimeBinder(format, index);
   case MemberFormat::mtBinary:
      return new BinaryBinder(format, index);
   default:
      return NULL;
   }
}

FieldBinder::FieldBinder(const MemberFormat& format, int _index) : 
   name(format.name),
   index(_index), isNull(false), isError(false)
{
}

Binder::Binder()
{
   stmt = NULL;
   bind = NULL;
}

void Binder::Close()
{
   if( stmt != NULL )
   {
      mysql_stmt_close(stmt);
      stmt = NULL;
   }

   if( bind != NULL )
   {
      free(bind);
      bind = NULL;
   }

   std::vector<FieldBinder*>::iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
      delete (*i);
   fields.clear();

   std::vector<FileField*>::iterator fi = files.begin();
   for( ; fi != files.end(); fi++ )
      delete (*fi);
   files.clear();
}


const MemberFormat* Binder::FieldType(const wchar_t* name) const
{
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; i != fields.end(); i++ )
   {
      const FieldBinder* fb = (*i);
      if( fb->Name().compare(name) == 0 )
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
      if( fb->Name().compare(name) == 0 )
      {
         fb->GetValue(&value);
         return &value;
      }
   }

   return NULL;
}

bool Binder::Read(Object* o) const
{
   if( stmt == NULL || bind == NULL )
      return false;

   bool ret = true;

   int index = 0;
   std::vector<FieldBinder*>::const_iterator i = fields.begin();
   for( ; ret && i != fields.end(); i++, index++ )
   {
      if( (*i)->Read(o, stmt, index) == false )
         ret = false;
   }

   if( ret )
   {
      std::vector<FileField*>::const_iterator fi = files.begin();
      for( ; fi != files.end(); fi++ )
         if( !(*fi)->ReadFile(o) )
            gServer->AddError(false, "Error while reading file");
   }

   return ret;
}

bool Binder::PrepareRead(const ISessionObject& obj, const std::string& filter, MYSQL* db)
{
   std::string stmt("SELECT ");

   const IObjectData* od = obj.GetObjectDef();
   if( od == NULL )
      return false;

   GRServer::Format *format = obj.Self()->format;
   string tbuf;

   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      int fldIndex = format->FindMember(fi->format.name.c_str());
      if( fldIndex < 0 )
         continue;

      if( fi->CanCreate() )
      {
         const MemberFormat& mf = format->at(fldIndex);
         FieldBinder* fb = FieldBinder::Create(*fi, fldIndex);
         if( fb != NULL )
         {
            fields.push_back(fb);

            QuoteString(&stmt, mf.name);
            stmt.append(",");
         }
      } else if( (fi->flags & IObjectData::Field::File) != 0 && !fi->src.empty() )
      {
         int srcidx = format->FindMember(fi->src.c_str());
         if( srcidx >= 0 && format->at(srcidx).type == MemberFormat::mtString )
            files.push_back(new FileField(srcidx, fldIndex, gServer->GetConfig().ExchangeFolder(), gServer));
      }
   }

   stmt.erase(stmt.size() - 1, 1);
   stmt += " FROM "; 
   QuoteString(&stmt, obj.GetObjectDef()->tableName);
   if( !filter.empty() )
   {
      stmt += " WHERE ";
      stmt += filter;
   }

   if(!PrepareStmt(db, stmt) )
      return false;

	if (mysql_stmt_execute(this->stmt) != 0)
		return false;

	mysql_stmt_store_result(this->stmt);
	return true;
}

bool Binder::PrepareStmt(MYSQL* db, const std::string& sql)
{
   stmt = mysql_stmt_init(db);
   if( stmt == NULL || mysql_stmt_prepare(stmt, sql.c_str(), sql.size()) != 0 )
   {
		if (stmt != NULL)
			AddErrorsToLog(stmt, IErrorLogger::Short);
		else
			AddErrorsToLog(false, db, IErrorLogger::Short);
      return false;
   }

	return BindFields();
}

bool Binder::BindFields()
{
   if( fields.size() > 0 )
   {
      bind = (MYSQL_BIND*)malloc(sizeof(MYSQL_BIND) * fields.size());
      memset(bind, 0, sizeof(MYSQL_BIND) * fields.size());

      MYSQL_BIND *cb = bind;
      std::vector<FieldBinder*>::const_iterator i = fields.begin();
      for( ; i != fields.end(); i++, cb++ )
         (*i)->Bind(cb);

      mysql_stmt_bind_result(stmt, bind);
   }
   return true;
}

ParamBinder::ParamBinder() : paramBind(NULL)
{
}

bool ParamBinder::WriteParams(const Object& obj)
{
   bool ret = true;
   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( !(*i)->Write(obj) )
         ret = false;
   }

   return ret;
}

void ParamBinder::Close()
{
   free(paramBind);
   paramBind = NULL;

   std::vector<FieldBinder*>::iterator i = params.begin();
   for( ; i != params.end(); i++ )
      delete (*i);
   params.clear();

   Binder::Close();
}

void ParamBinder::AddParam(std::string* paramStmt, const IObjectData::Field& field, int fldIndex)
{
   FieldBinder* fb = FieldBinder::Create(field, fldIndex);
   if( fb == NULL )
      return;

   params.push_back(fb);

   if( paramStmt != NULL )
   {
      string tstr;
      QuoteString(&tstr, field.format.name);
      if( !paramStmt->empty() )
         paramStmt->append(" AND ");
      
      paramStmt->append(tstr);
      paramStmt->append(" = ?");
   }
}

bool ParamBinder::PrepareStmt(MYSQL* db, const std::string& sql)
{
   if( !Binder::PrepareStmt(db, sql) )
      return false;

   if( params.size() > 0 )
   {
      paramBind = (MYSQL_BIND*)malloc(sizeof(MYSQL_BIND) * params.size());
      memset(paramBind, 0, sizeof(MYSQL_BIND)*params.size());

      MYSQL_BIND *cb = paramBind;
      std::vector<FieldBinder*>::const_iterator i = params.begin();
      for( ; i != params.end(); i++, cb++ )
         (*i)->Bind(cb);

      mysql_stmt_bind_param(stmt, paramBind);
   }
   return true;
}

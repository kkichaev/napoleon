/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * DBF writer
 *
 * ert   28/09/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include <set>
#include <algorithm>

#include <ServerDefs.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class TimeStampWriter : public IFieldWriter
{
public:
   TimeStampWriter(int mi, char *name) : IFieldWriter(mi, name) {}
   virtual bool Write(const Object& o, DataForm& base)
   {
      SYSTEMTIME st;
      FileTimeToSystemTime(&o.at(index).datetime, &st);

      char buf[30];
      wsprintfA(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
      base.Fill(name, buf);

      return true;
   }
};

class DateWriter : public IFieldWriter
{
public:
   DateWriter(int mi, char *name) : IFieldWriter(mi, name) {}
   virtual bool Write(const Object& o, DataForm& base)
   {
      SYSTEMTIME st;
      FileTimeToSystemTime(&o.at(index).datetime, &st);

      char buf[30];
      wsprintfA(buf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
      base.Fill(name, buf);

      return true;
   }
};

class TimeWriter : public IFieldWriter
{
public:
   TimeWriter(int mi, char *name) : IFieldWriter(mi, name) {}
   virtual bool Write(const Object& o, DataForm& base)
   {
      SYSTEMTIME st;
      FileTimeToSystemTime(&o.at(index).datetime, &st);

      char buf[30];
      wsprintfA(buf, "%02d:%02d", st.wHour, st.wMinute);
      base.Fill(name, buf);

      return true;
   }
};

class NumberWriter : public IFieldWriter
{
public:
   NumberWriter(int mi, char *name) : IFieldWriter(mi, name) {}
   virtual bool Write(const Object& o, DataForm& base)
   {
      base.Fill(name, o.at(index).number);
      return true;
   }
};

class StringWriter : public IFieldWriter
{
public:
   StringWriter(int mi, char *name) : IFieldWriter(mi, name) {}
   virtual bool Write(const Object& o, DataForm& base)
   {
      USES_CONVERSION;
      base.Fill(name, W2A_CP(o.at(index).str->c_str(), DBF_CODE_PAGE));
      return true;
   }
};

void DBFWriter::MakeDBFName(char* dest, const std::wstring& name)
{
   USES_CONVERSION;
   char* svDest = dest;

   const char *src = W2A_CP(name.c_str(), DBF_CODE_PAGE);
   int len = 0;
   while( *src && len++ < 10 )
   {
      *dest++ = ((int)(*src) > 0) ? toupper(*src) : *src;
      src++;
   }
   *dest = '\0';
}

void AddFieldWriter(FieldWriter *writer, int mi, const ObjectDef::Field& src)
{
   if( src.data.empty() )
      return;

   std::wstring::size_type pos = 0;
   while( true )
   {
      std::wstring::size_type epos = src.data.find(L',', pos);
      std::wstring fName = src.data.substr(pos, (epos != std::wstring::npos) ? epos - pos : -1);

      char name[15];
      DBFWriter::MakeDBFName(name, fName);

      IFieldWriter *fw = NULL;

      const MemberFormat& fmt = src.format;
      switch(fmt.type)
      {
      case MemberFormat::mtDateTime:
         switch( fmt.format.dateFormat )
         {
         case MemberFormat::Stamp:
            fw = new TimeStampWriter(mi, name);
            break;
         case MemberFormat::Date:
            fw = new DateWriter(mi, name);
            break;
         case MemberFormat::Time:
            fw = new TimeWriter(mi, name);
            break;
         }
         break;
      case MemberFormat::mtNumber:
         fw = new NumberWriter(mi, name);
         break;
      case MemberFormat::mtString:
         fw = new StringWriter(mi, name);
         break;
      default:
         break;
      }

      if( fw != NULL ) writer->push_back(fw);

      if( epos == std::wstring::npos ) break;
      pos = epos + 1;
   }
}

bool FieldWriter::AddFields(const SessionObject& object)
{
   bool ret = false;
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL ) return ret;

   ObjectDef::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      if( (fi->format.flags & MemberFormat::HiddenPut) != 0 )
         continue;

      int mi = object.format->FindMember(fi->format.name.c_str());
      if( mi < 0 ) continue;

      AddFieldWriter(this, mi, (*fi));
   }

   return true;
}

DBFWriter::DBFWriter(const GRServer::Format& fmt, const IObjectData* _objDef, const std::string& _fileName, bool append) :
   format(fmt), objDef(_objDef), fileName(_fileName), objIndex(-1)
{
   appendMode = append;
}

bool DBFWriter::CreateBase(const SessionObject& object)
{
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL ) return false;

   bool ret = false;
   std::vector<DBRec> dbFields;

   ObjectDef::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
      SetDBField(&dbFields, (*fi));

   AddFields(&dbFields);

   if( dbFields.size() > 0 )
   {
      DBRec *flds = new DBRec[dbFields.size()];
      for( WORD i=0; i<dbFields.size(); i++ )
         flds[i] = dbFields[i];

      ret = base.Create(fileName.c_str(), (int)dbFields.size(), flds);
      delete flds;
   }
   return ret;
}

bool DBFWriter::Prepare(const ISessionObject& iobject)
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   if( !base.Open(fileName.c_str()) )
   {
      if( !CreateBase(object) )
      {
         gServer->AddError(false, "Не могу открыть таблицу '%s' для записи ошибка %d", fileName.c_str(), GetLastError());
         return false;
      }
   }
	gServer->AddLog(IErrorLogger::Full, "write file '%s'", fileName.c_str());

   const ISessionObject* parentObj = object.Parent();
   if( parentObj != NULL )
   {
      const std::wstring& oname = object.Name();
      size_t off = oname.find_last_of(L'$');
      objIndex = ((const SessionObject*)parentObj->Self())->format->FindMember(oname.substr(off+1).c_str());
   }

   if( !appendMode )
      keyRec.Setup(base, *objDef);
   return writer.AddFields(object);
}

bool DBFWriter::Write(const Object& o, RowID *rid)
{
   if( objIndex < 0 )
      return WriteObject(o);

   bool ret = true;
   const Member& m = o.at(objIndex);
   ServObject *so = m.object;
   if( so != NULL )
   {
      ServObject::const_iterator i = so->begin();
      for( ; ret && i != so->end(); i++ )
      {
         ret = WriteObject(*(*i));
      }
   }

   return ret;
}

bool DBFWriter::WriteObject(const Object& o)
{
   //base.ResetRec();
   base.MarkDelete(false);
	bool ret = true;

   if( !writer.Write(o, base) )
		return false;
   else
   {
      BeforeWrite(o);

      WriterList::iterator i = childs.begin();
      for( ; i != childs.end(); i++ )
         (*i)->Write(o, NULL);

      int rec = keyRec.GetRec(base);
		if (rec < 0)
		{
			ret = base.Append();
			if (ret)
				keyRec.PutRec(base, base.GetRecNo() - 1);
		}
      else
      {
         //криво, но не знаю как быть :(
         base.ReadRec(rec);
         writer.Write(o, base);
			ret = base.WriteRec(rec);
      }
   }

   if( !ret )
		gServer->AddLog(IErrorLogger::Full, "can't write object in file '%s' error code %d", base.GetName(), GetLastError());

	return ret;
}

void DBFWriter::Close()
{
   keyRec.Close(&base);
   writer.clear();
   base.Close();
}

DBFWriter::KeyToRec::KeyToRec()
{
}

void DBFWriter::KeyToRec::LoadKeyFields(const DataForm& base, const IObjectData& objDef)
{
   ObjectDef::Members::const_iterator keyI = objDef.members.find(PRIMARY_KEY_STR);
   if( keyI != objDef.members.end() )
   {
      std::wstring::const_iterator si = keyI->second.begin(), ei = keyI->second.end();
      if( *si == L'"' ) si++;

      char fieldName[15];
      std::wstring f;
      for( ; true ; si++ )
      {
         wchar_t sym = (si == ei) ? L'\0' : *si;

         if( sym == L'\0' || sym == L'"' || sym == L',' )
         {
            if( !f.empty() )
            {
               MakeDBFName(fieldName, f);
               DBField* dbfield = base.GetFieldRef(fieldName);
               if( dbfield != NULL )
               {
                  FieldData fd;
                  fd.offset = dbfield->offset;
                  fd.width = dbfield->width;

                  keyFields.push_back(fd);
               }
            }

            if( sym == L'\0' || sym == L'"' ) break;
            f.clear();
         }
         f.append(1, sym);
      }
   }
}

void DBFWriter::KeyToRec::Setup(const DataForm& base, const IObjectData& objDef)
{
   LoadKeyFields(base, objDef);

   if( keyFields.size() > 0 )
   {
      for( int i=0; base.ReadRec(i); i++ )
      {
         std::string value;
         LoadValue(&value, base);

         Data& d = data[value];
         d.used = false;
         d.recs.push_back(i);
      }
   }
}

void DBFWriter::KeyToRec::PutRec(const DataForm& base, int rec)
{
	if (keyFields.size() > 0)
	{
		std::string value;
		LoadValue(&value, base);

		Data& d = data[value];
		d.used = false;
		d.recs.push_back(rec);
	}
}

int DBFWriter::KeyToRec::GetRec(const DataForm& base)
{
   int rec = -1;
   if( data.size() > 0 )
   {
      std::string value;
      LoadValue(&value, base);

      if( !value.empty() )
      {
         KeyData::iterator i = data.find(value);
         if( i != data.end() )
         {
            Data& rd = i->second;
            std::vector<int>::iterator ri = rd.recs.begin();

            rec = (*ri);

            rd.recs.erase(ri);
            if( rd.recs.size() > 0 ) rd.used = true;
            else data.erase(i);
         }
      }
   }

   return rec;
}

void DBFWriter::KeyToRec::Close(DataForm* base)
{
   if( !base->Opened() )
      return;

   KeyData::iterator i = data.begin();
   for( ; i != data.end(); i++ )
   {
      Data& rd = i->second;
      if( rd.used )
      {
         std::vector<int>::iterator ri = rd.recs.begin();
         for( ; ri != rd.recs.end(); ri++ )
         {
            base->ReadRec((*ri));
            base->DeleteRec();
         }
      }
   }
}

void DBFWriter::KeyToRec::LoadValue(std::string* value, const DataForm& base)
{
   value->clear();

   std::vector<FieldData>::const_iterator i = keyFields.begin();
   for( ; i != keyFields.end(); i++ )
   {
      const char *p = base.GetRec() + i->offset;
      const char *ep = p + i->width - 1;

      while( ep >= p && *ep == ' ' ) ep--;
      ep++;

      value->append(p, (int)(ep-p));
      value->append("\x1"); // разелитель полей
   }
}

bool GRServer::SetDBField(std::vector<DBRec>* fields, const ObjectDef::Field& src)
{
   if( src.data.empty() )
      return false;

   DBRec field = {0};
   const MemberFormat& fmt = src.format;

   if( (fmt.flags & MemberFormat::HiddenPut) != 0 )
      return false;

   switch(fmt.type)
   {
   case MemberFormat::mtDateTime:
      switch( fmt.format.dateFormat )
      {
      case MemberFormat::Stamp:
         field.type = 'C';
         field.width = 15;
         break;
      case MemberFormat::Date:
         field.type = 'D';
         field.width = 8;
         break;
      case MemberFormat::Time:
         field.type = 'C';
         field.width = 5;
         break;
      }
      break;
   case MemberFormat::mtNumber:
      field.type = 'N';
      field.prec = (BYTE)fmt.format.fraction;
      field.width = (BYTE)src.width;
      break;
   case MemberFormat::mtString:
      field.width = (src.width > 0 ) ? src.width : 250;
      field.type = 'C';
      break;

   default: return false;
   }

   std::wstring::size_type pos = 0;
   while( true )
   {
      std::wstring::size_type epos = src.data.find(L',', pos);
      std::wstring fName = src.data.substr(pos, (epos != std::wstring::npos) ? epos - pos : -1);

      DBFWriter::MakeDBFName((char*)field.name, fName);

      if( _strnicmp((const char*)field.name, DBFReader::ROWID, sizeof(field.name)) != 0 )
         fields->push_back(field);

      if( epos == std::wstring::npos ) break;
      pos = epos + 1;
   }

   return true;
}


IDataSource::IWriter* DBFSourceCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   std::string fileName;
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   if( !GetTableName(&fileName, parameters, object) ) return NULL;

   bool append = false;
   const Parameter *p = parameters.Find(L"writeMode", -1);
   if( p != NULL )
      append = (p->value.compare(L"append") == 0);
   return new DBFWriter(*object.format, object.GetObjectDef(), fileName, append);
}

class DBFTableRemover : public IDataSource::IRemover
{
public:
	DBFTableRemover(const std::string& fileName)
	{
		this->fileName = fileName;
		const char* p = strrchr(fileName.c_str(), '.');
		if( (p == NULL) || (_strnicmp(p+1, "dbf", 3) != 0) )
			this->fileName.append(".DBF");
	}

   virtual bool Remove(const wchar_t* filter)
	{
		_unlink(fileName.c_str());

		std::vector<IRemover*>::iterator i = childs.begin();
		for( ; i != childs.end(); i++ )
			(*i)->Remove(L"");

		return true;
	}

	virtual void Close() {}

private:
	std::string fileName;
};

IDataSource::IRemover* DBFSourceCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   std::string fileName;
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   if( !GetTableName(&fileName, parameters, object) ) return NULL;

	return new DBFTableRemover(fileName);
}

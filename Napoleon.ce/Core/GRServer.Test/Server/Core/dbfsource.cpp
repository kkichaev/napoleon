/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * DBF источник данных
 *
 * ert   28/09/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include "srvutility.h"
#include "session.h"
#include "token.h"
#include <set>
#include <algorithm>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef UNIX
#include <stdio.h>
#endif

using namespace GRServer;

//
//------------------------------- ObjectReader ------------------------------------
//
class BaseReader : public FieldReader
{
public:
   BaseReader(int _member, const DBField& field) : member(_member)
   {
      width = field.width;
      offset = field.offset;
   }
protected:
   int member;

   WORD width;
   WORD offset;
};

class StringReader : public BaseReader
{
public:
   StringReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      USES_CONVERSION;

      Member& m = object->at(member);

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      std::string sbuf;
      m.str->assign(A2W_CP(Trunc(buf, &sbuf), DBF_CODE_PAGE));
   }
};

class RowidReader : public FieldReader
{
public:
   RowidReader(int _member) : member(_member) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);
      m.number = base.GetRecNo() + 1;
   }

protected:
   int member;
};

class NumberReader : public BaseReader
{
public:
   NumberReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

		//char *ptr = strchr(buf, ',');
		//if (ptr != NULL)
		//	*ptr = '.';
		//m.number = atof(buf);

		m.number = _atof_l(buf, ServObject::GetLocale());
   }
};

class HexReader : public BaseReader
{
public:
   HexReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      m.number = strtol(buf, (char**)&buf, 16);
   }
};

class LogicReader : public BaseReader
{
public:
   LogicReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);

      char sym = *(base.GetRec() + offset);
      m.number = (sym == 'T' || sym == '1') ? 1.0 : 0.0;
   }
};

FILETIME GRServer::ReadFileTime(MemberFormat::DateFormat format, const char* str)
{
   SYSTEMTIME st = {1970,1,1,1,0,0,0,0};
   FILETIME ft;

   if( (signed char)*str > 0 && isdigit(*str) )
   {
      switch(format)
      {
      case MemberFormat::Stamp:
         sscanf(str, "%4d%2d%2d%2d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay, (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
         break;
      case MemberFormat::Date:
         sscanf(str, "%4d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay);
         break;
      case MemberFormat::Time:
         sscanf(str, "%2d:%2d:%2d", (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
         break;
      }
   }

   SystemTimeToFileTime(&st, &ft);
   return ft;
}

class DateReader : public BaseReader
{
public:
   DateReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);

      SYSTEMTIME st = {0};

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      m.datetime = ReadFileTime(MemberFormat::Date, buf);
      SystemTimeToFileTime(&st, &m.datetime);
   }
};

class TimeStampReader : public BaseReader
{
public:
   TimeStampReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);
//      SYSTEMTIME st = {0};

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      m.datetime = ReadFileTime(MemberFormat::Stamp, buf);
   }
};

class TimeReader : public BaseReader
{
public:
   TimeReader(int member, const DBField& field) : BaseReader(member, field) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      Member& m = object->at(member);
//      SYSTEMTIME st = {0};

      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      m.datetime = ReadFileTime(MemberFormat::Time, buf);
   }
};

class FileReader : public BaseReader
{
public:
   class FileBinary : public IBinary
   {
   public:
      FileBinary(const char* _fileName) : fileName(_fileName), bytes(NULL)
      {
      }
      ~FileBinary() { Close(); }

      virtual void Assign(Binary* b);

      virtual DWORD Size() const;
      virtual const BYTE* Bytes() const;

      virtual void Close();

   protected:
      void Load() const;

      std::string fileName;
      mutable Binary *bytes;
   };

   FileReader(int member, const DBField& field, const std::string& path) : BaseReader(member, field), basePath(path) {}

   virtual void Read(Object* object, const DataForm& base) const
   {
      char* buf = (char*)alloca(width+1);
      strncpy(buf, base.GetRec() + offset, width);
      buf[width] = '\0';

      OemToCharA(buf, buf);
      std::string fileName;
      Trunc(buf, &fileName);
      if( fileName.empty() == false )
      {
#ifdef UNIX
         if( fileName.at(0) != '/' && fileName.at(0) != '~' )
            fileName = basePath + fileName;
#else
         if( fileName.at(0) != '\\' && fileName.at(1) != ':' )
            fileName = basePath + fileName;
#endif
         object->at(member).binary = new FileBinary(fileName.c_str());
      }
   }

protected:
   std::string basePath;
};

void FileReader::FileBinary::Assign(Binary* b)
{
   FILE *f = fopen(fileName.c_str(), "wb");
   if( f )
   {
      fwrite((const BYTE*)*b, b->Size(), sizeof(BYTE), f);
      fclose(f);
   }
   delete b;
}

DWORD FileReader::FileBinary::Size() const
{
   if( bytes == NULL ) Load();
   return bytes->Size();
}

const BYTE* FileReader::FileBinary::Bytes() const
{
   if( bytes == NULL ) Load();
   return *bytes;
}

void FileReader::FileBinary::Close()
{
   if( bytes != NULL )
   {
      delete bytes;
      bytes = NULL;
   }
}

void FileReader::FileBinary::Load() const
{
   if( bytes != NULL ) delete bytes;
   bytes = new Binary();

   const char* str = fileName.c_str();
   FILE* f = fopen(str, "rb");
   if( f != NULL )
   {
      DWORD size = _filelength(_fileno(f));
      if( size > 0 )
      {
         BYTE *b = bytes->Alloc(size);
         fread(b, size, sizeof(BYTE), f);
      }
      fclose(f);
   } else
   {
      gServer->AddError(false, "Не могу открыть файл '%s' errno=%d (%s)", str, errno, strerror(errno));
   }
}

void ObjectReader::Read(ServObject* so, const DataForm& base) const
{
   Object* object = so->AddObject();
   Read(object, base);
}

void ObjectReader::Read(Object* object, const DataForm& base) const
{
   const_iterator i = begin();
   for( ; i != end(); i++ )
      (*i)->Read(object, base);
}

const char* DBFReader::ROWID = "rowid";

bool ObjectReader::Create(const SessionObject& object, const DataForm& base)
{
   clear();

   std::string exchangeFolder(object.GetSession().Config().ExchangeFolder());
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL ) return false;

   USES_CONVERSION;
   GRServer::ObjectDef::Fields::const_iterator i = od->fields.begin();
   for( ; i != od->fields.end(); i++ )
   {
      const char* aname = W2A_CP(i->data.c_str(), DBF_CODE_PAGE);
      int findex = base.Field(aname);
      int dindex = object.format->FindMember(i->format.name.c_str());

      if( findex < 0 || dindex < 0)
      {
         if( dindex >= 0 && _stricmp(aname, DBFReader::ROWID) == 0 )
         {
            const MemberFormat& destFormat = (*object.format)[dindex];
            if( destFormat.type == MemberFormat::mtNumber )
               push_back(new RowidReader(dindex));
         }
         continue;
      }

      FieldReader *r = NULL;
      DBField& field = *(base.GetFieldBase() + findex);
      const MemberFormat& destFormat = (*object.format)[dindex];

      switch( destFormat.type )
      {
      case MemberFormat::mtString:
         r = new StringReader(dindex, field);
         break;
      case MemberFormat::mtNumber:
         r = (field.type =='L') ?
            (FieldReader*)new LogicReader(dindex, field) : ((i->flags & ObjectDef::Field::Hex) == 0 ) ?
            (FieldReader*)new NumberReader(dindex, field) :
            (FieldReader*)new HexReader(dindex, field);
         break;
      case MemberFormat::mtBinary:
         if( (i->flags & ObjectDef::Field::File) != 0 )
         {
            if( !i->src.empty() )
            {
               const char* srcname = W2A_CP(i->src.c_str(), DBF_CODE_PAGE);
               int srcindex = base.Field(srcname);
               if( srcindex >= 0 )
                  field = *(base.GetFieldBase() + srcindex);
            }
            r = new FileReader(dindex, field, exchangeFolder);
         }
         break;
      case MemberFormat::mtDateTime:
         switch( destFormat.format.dateFormat )
         {
         case MemberFormat::Stamp:
            if( field.type == 'D' ) r = new DateReader(dindex, field);
            else if( field.type == 'C' ) r = new TimeStampReader(dindex, field);
            break;
         case MemberFormat::Date:
            if( field.type == 'D' ) r = new DateReader(dindex, field);
            break;
         case MemberFormat::Time:
            if( field.type == 'C' ) r = new TimeReader(dindex, field);
            break;
         }
         break;

      default:
         break;
      }

      if( r != NULL ) push_back(r);
   }

   return true;
}

//
//------------------------------- DBFSource ------------------------------------
//
IDataSource::IReader* DBFCreatorBase::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   FilterReader::Data fd;
   const Session& session = (const Session&)iobject.GetSession();
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   const Parameter *filter = parameters.Find(L"filter", 1);

   if( filter != NULL )
   {
      Token ft;
      if( session.Parse(&ft, filter->value, &object) && ft.type == Token::ttString )
         FilterReader::Parse(&fd, *ft.value.str, object);
   }

   std::string fileName;
   IDataSource::IReader* reader = NULL;
   if( GetTableName(&fileName, parameters, object) )
      reader = Create(fileName, object, fd, parameters);

   delete fd.holder;
   delete fd.filter;

   return reader;
}

IDataSource::IReader* DBFSourceCreator::Create(const std::string& fileName, const ISessionObject& iobject,
                                               FilterReader::Data& filter, const ParamList& parameters) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFReader *reader = (filter.holder != NULL) ? new ChildDBFReader(*object.format) : new DBFReader(*object.format);
   if( !reader->Open(fileName, object, filter) )
   {
      delete reader;
      reader = NULL;
   }
   return reader;
}


class DBFMaskReader : public DBFReader
{
public:
   DBFMaskReader(const GRServer::Format& fmt) : DBFReader(fmt) { hFind = INVALID_HANDLE_VALUE; }

   virtual bool Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter);
   virtual void Close();
   virtual bool MoveNext(Object *parentObject);

protected:
   HANDLE hFind;
   std::string folderBase;
};

bool DBFMaskReader::Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter)
{
   std::string tName(fileName);
   size_t pos1 = tName.find_last_of('\\');
   size_t pos2 = tName.find_last_of('/');
	if( pos1 == std::string::npos )
		pos1 = 0;
	if( pos2 == std::string::npos )
		pos2 = 0;

   const char* p = strrchr(tName.c_str(), '.');
   if( (p == NULL) || (_strnicmp(p+1, "dbf", 3) != 0) )
      tName.append(".DBF");

   folderBase = tName.substr(0, max(pos1, pos2) + 1);
   WIN32_FIND_DATAA fnd;
   hFind = FindFirstFileA(tName.c_str(), &fnd);
   if( hFind == INVALID_HANDLE_VALUE )
      return false;

   return DBFReader::Open(folderBase+fnd.cFileName, object, filter);
}

bool DBFMaskReader::MoveNext(Object *parentObject)
{
   while( !DBFReader::MoveNext(parentObject) )
   {
      WIN32_FIND_DATAA fnd;
      if( FindNextFileA(hFind, &fnd) == 0 )
         return false;

      base.Close();

      std::string fn (folderBase + fnd.cFileName);
      if( !base.Open(fn.c_str(), false) )
         return false;
      rc = 0;
   }

   return true;
}

void DBFMaskReader::Close()
{
   if( hFind != INVALID_HANDLE_VALUE )
   {
      FindClose(hFind);
      hFind = INVALID_HANDLE_VALUE;
   }

   DBFReader::Close();
}

IDataSource::IReader* DBFMaskReaderCreator::Create(const std::string& fileName, const ISessionObject& iobject,
                                               FilterReader::Data& filter, const ParamList& parameters) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFReader *reader = new DBFMaskReader(*object.format);
   if( !reader->Open(fileName, object, filter) )
   {
      delete reader;
      reader = NULL;
   }
   return reader;
}

//
//------------------------------- DBFReader ------------------------------------
//
enum FilterParseStates
{
   fpsDestField = 1,
   fpsOp = 2,
   fpsSource = 3,
   fpsSourceField = 4,
   fpsSpace = 0x10,
};

bool FilterData::Parse(const std::wstring& str)
{
   WORD state = fpsSpace;
   bool res = false;
   std::wstring::const_iterator startI(str.begin()), endI(str.end());
   std::wstring opStr;

   destIsObjectField = true;
   for( ; startI != endI; startI++ )
   {
      wchar_t sym = (*startI);
      if( sym == '"' ) continue;
      if( sym == L'.' && (state & 0xF) == fpsSource )
      {
         state |= fpsSpace;
         continue;
      }
      if( sym == L' ' )
      {
         state |= fpsSpace;
         continue;
      }

      if( (state & fpsSpace ) )
      {
         state &= (~fpsSpace);
         state ++;
      }
      if( state > fpsSourceField ) break;

      switch( state & 0xF )
      {
      case fpsDestField:
         if( sym == L'[' ) destIsObjectField = false;
         else if( sym != L']' ) destField.append(1, sym);
         break;
      case fpsOp:
         opStr.append(1, sym);
         break;
      case fpsSource:
         if( !source.empty() || sym != L'$' ) source.append(1, sym);
         break;
      case fpsSourceField:
         sourceField.append(1, sym);
         break;
      }
   }

   if( (state & 0xF) >= fpsSourceField ) res = true;

   if( _wcsicmp(opStr.c_str(), L"=") == 0 ) op = Equal;
   else if( _wcsicmp(opStr.c_str(), L"in") == 0 ) op = InSet;
   else res = false;

   return res;
}

class NumberSet : public DBFReader::Filter::Set
{
public:
   NumberSet() {}
   virtual ~NumberSet() {}
   virtual void Add(const Member& m) { values.insert(m.number); }
   virtual bool InSet(const char* value)
   {
		//std::set<double>::const_iterator fnd = values.find(atof(value));
		std::set<double>::const_iterator fnd = values.find(_atof_l(value, ServObject::GetLocale()));
      return (fnd != values.end());
   }

   std::set<double> values;
};

class StringSet : public DBFReader::Filter::Set
{
public:
   StringSet() {}
   virtual ~StringSet() {}
   virtual void Add(const Member& m)
   {
      USES_CONVERSION;
      values.insert(W2A_CP(m.str->c_str(), DBF_CODE_PAGE));
   }

   virtual bool InSet(const char* value)
   {
      std::string sbuf;
      std::set<std::string>::const_iterator fnd = values.find(Trunc(value, &sbuf));
      return (fnd != values.end());
   }

   std::set<std::string> values;
};

class DataTimeSet : public DBFReader::Filter::Set
{
public:
   DataTimeSet(MemberFormat::DateFormat fmt) : format(fmt) {}
   virtual ~DataTimeSet() {}
   virtual void Add(const Member& m) { values.insert(m.datetime);  }

   virtual bool InSet(const char* value)
   {
      std::set<FILETIME, CmpFileTime>::const_iterator fnd = values.find(ReadFileTime(format, value));
      return (fnd != values.end());
   }

   std::set<FILETIME, CmpFileTime> values;
   MemberFormat::DateFormat format;
};

bool FilterData::SetDBData(FilterData::DBData* data, const DataForm &base, const SessionObject &object, bool addData) const
{
   USES_CONVERSION;

   bool retval = false;
   std::string field;

   if( destIsObjectField )
   {
      const IObjectData* od = object.GetObjectDef();
      if( od != NULL )
      {
         ObjectDef::Field f;
         f.format.name = destField.c_str();
         ObjectDef::Fields::const_iterator fnd = od->fields.find(f);
         if( fnd != od->fields.end() )
            field = W2A_CP(fnd->data.c_str(), DBF_CODE_PAGE);
      }
   } else
      field = W2A_CP(destField.c_str(), DBF_CODE_PAGE);

   if( !field.empty() )
   {
      DBField *f = base.GetFieldRef(field.c_str());
      if( f != NULL )
      {
         data->offset = f->offset;
         data->length = f->width;

         Session& session = (Session&)object.GetSession();
         const ISessionObject *srcO = (addData) ?
            session.LoadObject(source, &object, userFilter.c_str()):
            session.FindObject(source, &object);
         data->src = (srcO != NULL) ? (SessionObject*)srcO->Self() : NULL;

         if( data->src != NULL )
         {
            data->srcIndex = data->src->format->FindMember(sourceField.c_str());
            if( data->srcIndex >= 0 ) retval = true;
         }
      }
   }

   return retval;
}

DBFReader::Filter::Filter(const DataForm &base, const FilterData &filter, const SessionObject &object) : set(NULL)
{
   FilterData::DBData data;
   if( filter.SetDBData(&data, base, object, true) )
   {
      offset = data.offset;
      length = data.length;

      MemberFormat &mf = data.src->format->at(data.srcIndex);
      if( mf.type == MemberFormat::mtNumber ) set = new NumberSet();
      else if( mf.type == MemberFormat::mtString ) set = new StringSet();
      else if( mf.type == MemberFormat::mtDateTime ) set = new DataTimeSet(mf.format.dateFormat);

      if( set != NULL )
      {
         SessionObject::const_iterator i = data.src->begin();
         for( ; i != data.src->end(); i++ )
            set->Add((*i)->at(data.srcIndex));
      }
   }
}

DBFReader::Filter::~Filter()
{
   delete set;
}

bool DBFReader::Filter::InSet(const DataForm& base)
{
   if( set == NULL ) return false;

   char *buf = (char*)alloca(length+1);
   strncpy(buf, base.GetRec() + offset, length);
   buf[length] = '\0';

   return set->InSet(buf);
}

DBFReader::DBFReader(const GRServer::Format& fmt) : objectFormat(fmt)
{
   mv.str = &value;
   filter = NULL;

	//doLoging = false;
}

DBFReader::~DBFReader()
{
   delete filter;
}

void DBFReader::Remove()
{
   Close();
   _unlink(base.GetName());
}

bool DBFReader::Open(const std::string& _fileName, const SessionObject& object, FilterReader::Data& filter)
{
   thisObject = &object;

   const IObjectData* od = object.GetObjectDef();
   if( od == NULL ) return false;

   std::string fileName(_fileName);
   const char* p = strrchr(fileName.c_str(), '.');
   if( (p == NULL) || (_strnicmp(p+1, "dbf", 3) != 0) )
      fileName.append(".DBF");

#ifdef UNIX
   GetFolderFileName(&fileName);
#endif
   if( (od->flags & IObjectDef::RemoveOnCommit) != 0 )
   {
		//doLoging = true;

      std::string src(fileName);

      fileName.append(".TMP");
      if( !IsFileExists(fileName) && IsFileExists(src.c_str()) )
         MoveFileA(src.c_str(), fileName.c_str());
	}

	if (!base.Open(fileName.c_str(), false))
		return false;
	rc = 0;

   IFilterInSet *f = filter.GetFilter();
   if( this->filter != NULL && this->filter != f )
      delete this->filter;
   this->filter = f;

	return items.Create(object, base);
}

void DBFReader::Close()
{
	//if (doLoging)
	//	gServer->AddLog("DBFReader::Close enter");
	
	base.Close();
}

bool DBFReader::MoveNext(Object *parentObject)
{
	//if (doLoging)
	//	gServer->AddLog("DBFReader::MoveNext enter");

   if( base.Opened() )
   {
      while( base.ReadRec(rc) )
      {
			//if (doLoging)
			//	gServer->AddLog("DBFReader::MoveNext ReadRec");
			
			rc++;
			if (filter == NULL || filter->InSet(base, *thisObject))
			{
				//if (doLoging)
				//	gServer->AddLog("DBFReader::MoveNext ret");
				return true;
			}
      }
   }
	//if (doLoging)
	//	gServer->AddLog("DBFReader::MoveNext ret false");
	return false;
}

bool DBFReader::Get(Object* o) const
{
	//if (doLoging)
	//	gServer->AddLog("DBFReader::Get enter");

	if (!base.Opened()) return false;

   items.Read(o, base);
   return true;
}

const MemberFormat* DBFReader::Type(const wchar_t* name) const
{
   if( !base.Opened() ) return NULL;

   USES_CONVERSION;
   DBField* field = base.GetFieldRef(W2A_CP(name, DBF_CODE_PAGE));
   if( field == NULL ) return NULL;

   switch( field->type )
   {
   case 'C':
      mf.type = MemberFormat::mtString;
      break;
   case 'N':
   case 'F':
   case 'L':
      mf.type = MemberFormat::mtNumber;
      mf.format.fraction = (field->type == 'L') ? 0 : field->prec;
      break;
   case 'D':
      mf.type = MemberFormat::mtDateTime;
      mf.format.dateFormat = MemberFormat::Date;
      break;
   default:
      return NULL;
   }

   return &mf;
}
static void ValueToFileTime(const char *value, FILETIME* ft)
{
   SYSTEMTIME st = {0};

   sscanf(value, "%4d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay);
   SystemTimeToFileTime(&st, ft);
}

const Member* DBFReader::Value(const wchar_t* name) const
{
   USES_CONVERSION;
   int index = base.Field(W2A_CP(name, DBF_CODE_PAGE));
   if( index < 0 ) return NULL;

   return (SetValue(&mv, index)) ? &mv : NULL;
}

bool DBFReader::SetValue(Member* member, int index) const
{
   if( !base.Opened() || index < 0 ) return false;

   USES_CONVERSION;
   DBField* field = base.GetFieldBase() + index;
   char *value = (char*)alloca(field->width + 1);
   strncpy(value, base.GetField(index), field->width);
   value[field->width] = '\0';
   std::string sbuf;

   switch( field->type )
   {
   case 'C':
      member->str->assign(A2W_CP(Trunc(value, &sbuf), DBF_CODE_PAGE));
      break;
   case 'N':
   case 'F':
	case 'L':
	{
		//char *ptr = strchr(value, ',');
		//if (ptr != NULL)
		//	*ptr = '.';
		//member->number = (field->type == 'L') ? ((*value == '1' || *value == 'T') ? 1.0 : 0.0) : atof(value);
		member->number = (field->type == 'L') ? ((*value == '1' || *value == 'T') ? 1.0 : 0.0) : _atof_l(value, ServObject::GetLocale());
		break;
	}
   case 'D':
      ValueToFileTime(value, &member->datetime);
      break;
   default:
      return false;
   }

   return true;
}

//
//------------------------------- ChildDBFReader ------------------------------------
//
StringItemsHolder::StringItemsHolder(const FilterData::DBData& data) :
   ChildItemsHolderBase(data.srcIndex, data.offset, data.length)
{
}

void StringItemsHolder::Add(DataForm& base, Object* object)
{
   USES_CONVERSION;

   char *buf = (char*)alloca(length+1);
   strncpy(buf, base.GetRec() + offset, length);
   buf[length] = '\0';
   std::string sbuf;
   const wchar_t* p = A2W_CP(Trunc(buf, &sbuf), DBF_CODE_PAGE);
   items[p].push_back(object);
}

void StringItemsHolder::Add(Object* parentObject, Object* object)
{
   if( parentIndex >= 0 )
   {
      Member& m = parentObject->at(parentIndex);
      items[(std::wstring&)*m.str].push_back(object);
   }
}

bool StringItemsHolder::MoveNext(const wchar_t* field)
{
   current = items.find(field);
   return ( current != items.end() && current->second.size() > 0 );
}

NumberItemsHolder::NumberItemsHolder(const FilterData::DBData& data):
   ChildItemsHolderBase(data.srcIndex, data.offset, data.length)
{
}

void NumberItemsHolder::Add(DataForm& base, Object* object)
{
   char *buf = (char*)alloca(length+1);
   strncpy(buf, base.GetRec() + offset, length);
   buf[length] = '\0';
	
	//char *ptr = strchr(buf, ',');
	//if (ptr != NULL)
	//	*ptr = '.';
	//double val = atof(buf);
	double val = _atof_l(buf, ServObject::GetLocale());
   items[val].push_back(object);
}

void NumberItemsHolder::Add(Object* parent, Object* object)
{
   Member& m = parent->at(parentIndex);
   items[m.number].push_back(object);
}

DateTimeItemsHolder::DateTimeItemsHolder(const FilterData::DBData& data, MemberFormat::DateFormat _format):
   ChildItemsHolderBase(data.srcIndex, data.offset, data.length), format(_format)
{
}

void DateTimeItemsHolder::Add(DataForm& base, Object* object)
{
   char *buf = (char*)alloca(length+1);
   strncpy(buf, base.GetRec() + offset, length);
   buf[length] = '\0';

   SYSTEMTIME st = {0};
   if( format == MemberFormat::Date )
   {
      sscanf(buf, "%4d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay);
   } else if( format == MemberFormat::Stamp )
   {
      sscanf(buf, "%4d%2d%2d%2d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay, (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
   } else
   {
      sscanf(buf, "%2d:%2d:%2d", (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
   }

   FILETIME val;
   SystemTimeToFileTime(&st, &val);
   items[val].push_back(object);
}

void DateTimeItemsHolder::Add(Object* parent, Object* object)
{
   Member& m = parent->at(parentIndex);
   items[m.datetime].push_back(object);
}


bool ChildDBFReader::Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter)
{
   bool retval = false;

   DataForm base;
   if( base.Open(fileName.c_str()) )
   {
      thisObject = &object;

      holder = filter.GetHolder();
      holder->Load(base, object, NULL);

      retval = true;
      base.Close();
   }

   return retval;
}

bool ChildDBFReader::MoveNext(Object *parentObject)
{
   return (holder && parentObject) ? holder->Next(*parentObject) : false;
}

bool ChildDBFReader::Get(Object* object) const
{
   return (holder) ? holder->Get(object) : false;
}

void ChildDBFReader::Close()
{
   delete holder;
   holder = NULL;
}

bool GRServer::GetTableName(std::string* fileName, const ParamList& parameters, const SessionObject& object)
{
   Token tableName;
   const Parameter *tname = parameters.Find(L"tableName", 0);
   Session& session = (Session&)object.GetSession();
   if( tname == NULL || !session.Parse(&tableName, tname->value, &object) || tableName.type != Token::ttString ) return false;

   USES_CONVERSION;
   const ServerConfig &config = (const ServerConfig&)session.Config();
   fileName->assign(config.ExchangeFolder());
	fileName->append(W2A(tableName.value.str->c_str()));
#ifdef UNIX
   ConvertPath(*fileName, fileName);
#endif
   return true;
}

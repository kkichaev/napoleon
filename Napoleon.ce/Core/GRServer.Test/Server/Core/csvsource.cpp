/*
 * Copyright (C), 2009-2012, Денис Мосягин
 *
 * Класс CSV таблицы (пока только ANSI кодировка)
 *
 * ert   03/07/2012   creating
 */

#include "stdafx.h"
#include "creators.h"
#include <iserver.h>
#include <pstream.h>
#include <srvutility.h>
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>
#include <stdio.h>

using namespace GRServer;
using namespace std;

const wchar_t FILE_NAME[] = L"fileName";
const wchar_t SEPARATOR[] = L"separator";

struct CSVParams
{
   std::string tableName;
   std::string separator;

   bool Read(const ParamList& parameters, const ISessionObject& object);
};

class RdStream : public ParseStreamW
{
public:
   RdStream(CPChar start, CPChar end) : ParseStreamW(start, end) {}

   bool GetToken(std::wstring* token, const std::wstring& separator);
};

class WrStream : public OutStream
{
public:
};

class Field
{
public:
   static Field* Create(const MemberFormat& mf, int index);

   virtual ~Field() {}

   virtual bool Read(Object* dest, const std::wstring& src) const = 0;
   virtual bool Write(WrStream* dest, const Object& src) const = 0;

protected:
   Field(int index)
   {
      this->index = index;
   }

protected:
   int index;
};

class StringField : public Field
{
public:
   StringField(int index) : Field(index) {}

   virtual bool Read(Object* dest, const std::wstring& src) const;
   virtual bool Write(WrStream* dest, const Object& src) const;
};

class NumberField : public Field
{
public:
   NumberField(WORD prec, int index) : Field(index)
   {
      this->prec = prec;
   }

   virtual bool Read(Object* dest, const std::wstring& src) const;
   virtual bool Write(WrStream* dest, const Object& src) const;

protected:
   WORD prec;
};

class DateField : public Field
{
public:
   DateField(MemberFormat::DateFormat dateFormat, int index) : Field(index)
   {
      this->dateFormat = dateFormat;
   }

   virtual bool Read(Object* dest, const std::wstring& src) const;
   virtual bool Write(WrStream* dest, const Object& src) const;

protected:
   MemberFormat::DateFormat dateFormat;
};

class CSVBinder
{
public:
   CSVBinder() {}
   virtual ~CSVBinder() { Close(); }

   void Close();
   bool Prepare(const ISessionObject& object, const std::string& sepSym);

   bool Read(Object* dest, const std::string& src) const;
   bool Write(std::string* dest, const Object& src) const;

protected:
   std::string separator;

   typedef std::vector<Field*> Fields;
   Fields fields;
};

class CSVReader : public IDataSource::IReader
{
public:
   CSVReader();

   bool Prepare(const ParamList& parameters, const ISessionObject& object);

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* o) const;

   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
   CSVBinder binder;

   CSVParams params;
   FILE *file;
   std::string curLine;
};

class CSVWriter : public IDataSource::IWriter
{
public:
   CSVWriter();

   bool Prepare(const ParamList& parameters, const ISessionObject& object);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   CSVBinder binder;

   CSVParams params;
   FILE *file;
};

//
//-------------------------------------------- Field --------------------------------------------------
//
Field* Field::Create(const MemberFormat& mf, int index)
{
   switch(mf.type)
   {
   case MemberFormat::mtString:
      return new StringField(index);
   case MemberFormat::mtNumber:
      return new NumberField(mf.format.fraction, index);
   case MemberFormat::mtDateTime:
      return new DateField(mf.format.dateFormat, index);
   default:
      return NULL;
   }
}

bool StringField::Read(Object* dest, const std::wstring& src) const
{
   if( index >= 0 )
   {
      Member& m = dest->at(index);
      int offset = 0;
      size_t size = src.size();
      if( *src.begin() == L'"' )
      {
         offset++;
         size--;
      }
      if( *src.rbegin() == L'"')
         size--;
      m.str->assign(src.substr(offset, size));
   }

   return true;
}

bool StringField::Write(WrStream* dest, const Object& src) const
{
   if( index >= 0 )
   {
      const Member& m = src.at(index);
      dest->Append(L'"');
      dest->Append(m.str->c_str());
      dest->Append(L'"');
   }

   return true;
}

bool NumberField::Read(Object* dest, const std::wstring& src) const
{
   if( index >= 0 )
   {
      Member& m = dest->at(index);
      m.number = _wtof(src.c_str());
   }

   return true;
}

bool NumberField::Write(WrStream* dest, const Object& src) const
{
   if( index >= 0 )
   {
      wchar_t buf[100];
      const Member& m = src.at(index);
      if( prec == 0 )
         wsprintf(buf, L"%d", (int)m.number);
      else
         _swprintf(buf, L"%.*f", (int)prec, m.number);
      dest->Append(buf);
   }

   return true;
}

bool DateField::Read(Object* dest, const std::wstring& src) const
{
   if( index >= 0 )
   {
      Member& m = dest->at(index);
      SYSTEMTIME st = {0};
      switch(dateFormat)
      {
      case MemberFormat::Stamp:
         swscanf(src.c_str(), L"%d/%02d/%02d %02d:%02d:%02d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay, (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
         break;
      case MemberFormat::Date:
         swscanf(src.c_str(), L"%d/%02d/%02d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay);
         break;
      case MemberFormat::Time:
         swscanf(src.c_str(), L"%02d:%02d:%02d", (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
         break;
      }

      SystemTimeToFileTime(&st, &m.datetime);
   }

   return true;
}

bool DateField::Write(WrStream* dest, const Object& src) const
{
   if( index >= 0 )
   {
      wchar_t buf[100];
      const Member& m = src.at(index);
      SYSTEMTIME st = {0};
      FileTimeToSystemTime(&m.datetime, &st);

      *buf = L'\0';

      switch(dateFormat)
      {
      case MemberFormat::Stamp:
         wsprintf(buf, L"%d/%02d/%02d %02d:%02d:%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
         break;
      case MemberFormat::Date:
         wsprintf(buf, L"%d/%02d/%02d", st.wYear, st.wMonth, st.wDay);
         break;
      case MemberFormat::Time:
         wsprintf(buf, L"%02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);
         break;
      }

      dest->Append(buf);
   }

   return true;
}

//
//-------------------------------------------- CSVParams --------------------------------------------------
//
bool CSVParams::Read(const GRServer::ParamList &parameters, const ISessionObject& object)
{
   ISession &session = object.GetSession();
   const Parameter* param = parameters.Find(FILE_NAME, -1);
   if( param == NULL )
      return false;

   USES_CONVERSION;
   CString *cs = NULL;
   bool ret = session.Parse(&cs, param->value, &object);
   if( ret )
   {
      tableName = W2A(cs->c_str());
   }
   delete cs;
   if( !ret )
      return false;

   param = parameters.Find(SEPARATOR, -1);
   if( param == NULL )
      return false;

   cs = NULL;
   ret = session.Parse(&cs, param->value, &object);
   if( ret )
   {
      separator = W2A(cs->c_str());
   }
   delete cs;

   return ret;
}

bool RdStream::GetToken(std::wstring* token, const std::wstring& separator)
{
   if( EOS() || endI - startI < (int)separator.size() || separator.size() == 0 )
      return false;

   bool readString = false;
   CPChar p = startI;
   CPChar checkSP = NULL;
   std::wstring::const_iterator spI = separator.begin();
   while( p < endI && spI != separator.end() )
   {
      wchar_t sym = *p;
      if( checkSP != NULL )
      {
         if( sym == *spI )
            spI++;
         else
            checkSP = NULL;
      }
      if( checkSP == NULL )
      {
         if( readString )
         {
            if( sym == L'"' )
               readString = (*(p-1) == L'\\');
         } else
         {
            if( sym == L'"' )
               readString = true;
            else if( sym == *spI )
            {
               checkSP = p;
               spI++;
            }
         }
      }
      p++;
   }

   if( checkSP == NULL )
      token->assign(startI);
   else
      token->assign(startI, 0, checkSP - startI);

   startI = p;
   return true;
}

//
//-------------------------------------------- Binder --------------------------------------------------
//
void CSVBinder::Close()
{
   Fields::iterator fi = fields.begin();
   for( ; fi != fields.end(); fi++ )
      delete (*fi);
}

bool CSVBinder::Prepare(const ISessionObject& object, const std::string& sepSym)
{
   separator = sepSym;
   const IObjectData* od = object.GetObjectDef();
   if( od == NULL )
      return false;

   GRServer::Format* format = object.Self()->format;
   IObjectData::Fields::const_iterator fi = od->fields.begin();
   for( ; fi != od->fields.end(); fi++ )
   {
      int idx = format->FindMember(fi->format.name.c_str());
      Field* f = Field::Create(fi->format, idx);
      if( f == NULL )
         continue;

      fields.push_back(f);
   }

   return true;
}

bool CSVBinder::Read(Object* dest, const std::string& src) const
{
   USES_CONVERSION;
   Fields::const_iterator fi = fields.begin();
   const wchar_t* pstr = A2W(src.c_str());
   RdStream ss(pstr, pstr + src.size());
   std::wstring wsep(A2W(separator.c_str()));

   bool ret = true;
   for( ; ret && fi != fields.end(); fi++ )
   {
      std::wstring str;
      if( ss.EOS() || !ss.GetToken(&str, wsep) )
      {
         ret = false;
         break;
      }
      ret = (*fi)->Read(dest, str);
   }

   return ret;
}

bool CSVBinder::Write(std::string* dest, const Object& src) const
{
   USES_CONVERSION;

   WrStream stream;
   std::wstring wsep(A2W(separator.c_str()));

   bool ret = true;
   Fields::const_iterator fi = fields.begin();
   for( ; ret && fi != fields.end(); fi++ )
   {
      ret = (*fi)->Write(&stream, src);
      if( ret )
         stream.Append(wsep.c_str());
   }
   if( ret && stream.Size() / sizeof(wchar_t) > wsep.size() )
   {
      const wchar_t* pstr = stream.ToString().c_str();
      dest->assign(W2A(pstr), 0, stream.Size() / sizeof(wchar_t) - wsep.size());
   }

   return ret;
}

//
//-------------------------------------------- CSVWriter --------------------------------------------------
//
CSVReader::CSVReader() : file(NULL)
{
}

bool CSVReader::Prepare(const ParamList& parameters, const ISessionObject& object)
{
   if( !params.Read(parameters, object) )
      return false;

   const IServerConfig &config = object.GetSession().Config();
   std::string fileName(config.ExchangeFolder());
   fileName += params.tableName;

   file = fopen(fileName.c_str(), "rt");
   if(file == NULL)
      return false;

   return binder.Prepare(object, params.separator);
}

bool CSVReader::MoveNext(Object *parentObject)
{
   if( file == NULL || feof(file) != 0 )
      return false;

   return ReadLine(&curLine, file);
}

bool CSVReader::Get(Object* o) const
{
   return binder.Read(o, curLine);
}

void CSVReader::Remove()
{
}

void CSVReader::Close()
{
   if( file != NULL )
   {
      fclose(file);
      file = NULL;
   }
}

//
//-------------------------------------------- CSVWriter --------------------------------------------------
//
CSVWriter::CSVWriter() : file(NULL)
{
}

bool CSVWriter::Prepare(const ParamList& parameters, const ISessionObject& object)
{
   if( !params.Read(parameters, object) )
      return false;

   return true;
}

bool CSVWriter::Prepare(const ISessionObject& object)
{
   if( !binder.Prepare(object, params.separator) )
      return false;

   const IServerConfig &config = object.GetSession().Config();
   std::string fileName(config.ExchangeFolder());
   fileName += params.tableName;

   file = fopen(fileName.c_str(), "at");
   return (file != NULL);
}

bool CSVWriter::Write(const Object& o, RowID *rid)
{
   if( file == NULL )
      return false;

   std::string str;
   if( !binder.Write(&str, o) )
      return false;

   fputs(str.c_str(), file);
   fputs("\n", file);
   return true;

}

void CSVWriter::Close()
{
   if( file != NULL )
   {
      fclose(file);
      file = NULL;
   }
}


IDataSource::IReader* CSVCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   CSVReader* rdr = new CSVReader();
   if( !rdr->Prepare(parameters, object) )
   {
      delete rdr;
      rdr = NULL;
   }
   return rdr;
}

IDataSource::IWriter* CSVCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent != NULL )
      return NULL;

   CSVWriter* wr = new CSVWriter();
   if( !wr->Prepare(parameters, object) )
   {
      delete wr;
      wr = NULL;
   }
   return wr;
}

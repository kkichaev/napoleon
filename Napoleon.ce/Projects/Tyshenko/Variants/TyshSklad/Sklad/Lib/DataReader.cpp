/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Чтение из потока в IReflectableData
 * 
 *  ert   25/09/2009   creating
 */
#include "stdafx.h"
#include "DataReader.h"
#include "DBImpl.h"

struct BaseReader : public IReaderElement
{
   BaseReader(const MemberType* member) { this->member = member; }
   bool CanWrite() const { return (member != NULL); }

protected:
   const MemberType* member;
};

struct StringReader : public BaseReader
{
   StringReader(const MemberType* member) : BaseReader(member)
   {
      if( member == NULL || member->type != MemberType::String)
         this->member = NULL; 
   }

   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const;

protected:
   mutable std::wstring buffer;
};

struct BinaryReader : public BaseReader
{
   BinaryReader(IBinaryWriter *_writer) : BaseReader(NULL), writer(_writer) {}
   ~BinaryReader() {}

   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const;

   IBinaryWriter *writer;
};

struct NumericReader : public BaseReader
{
   NumericReader(const MemberType* member, ReceivedStream* stream);
   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const;
protected:
   void WriteDouble(IReflectableData* data, const wchar_t* p) const;
   void WriteInt(IReflectableData* data, const wchar_t* p) const;

   bool readDouble;
   int scale;
};

struct DateTimeReader : public BaseReader
{
   DateTimeReader(const MemberType* member, wchar_t type, ReceivedStream* stream);
   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const;
protected:
   enum Flags { ReadDate = 1, ReadTime = 2 };
   WORD flags;
};

struct CollectionReader : public BaseReader
{
   CollectionReader(const MemberType* member, const DataReflector& type, ReceivedStream* stream);
   ~CollectionReader();

   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const;

protected:
   IReflectableData* childData;
   DataReader* reader;
   mutable StringHolder sh;
};

struct EmptyReader : public IReaderElement
{
   EmptyReader(ReceivedStream *stream)
   {
      stream->SkipObject();
   }

   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const
   {
      while( true )
      {
         wchar_t sym = stream->Get();
         if( sym != L'[' ) break;
         stream->Unget(sym);
         stream->SkipObject();
      }
      return true;
   }
};
//
//--------------------------------------- Element Readers --------------------------------------------------
//
bool StringReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   buffer.clear();
   wchar_t sym = stream->Get();
   if( sym != L'"' ) return false;

   while( true )
   {
      if( stream->EOS() ) return false;
      sym = stream->Get();
      if( sym == L'"' )
      {
         stream->Get();
         break;
      }

      if( sym == L'\\' )
      {
         wchar_t sym1 = stream->Get();
         switch( sym1 )
         {
         case L'\\': break;
         case L'/': sym = sym1; break;
         case L'"': sym = sym1; break;
         case L'b': sym = L'\b'; break;
         case L'f': sym = L'\f'; break;
         case L'n': sym = L'\n'; break;
         case L'r': sym = L'\r'; break;
         case L't': sym = L'\t'; break;
         default:
            buffer.append(1, sym);
            sym = sym1;
            break;
         }
      } 
      buffer.append(1, sym);
   }

   if( CanWrite() )
   {
      const wchar_t *pstr = buffer.c_str();
      member->SetValue(data, &pstr);
   }
   return true;
}

bool BinaryReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   std::wstring ssize;
   if( !stream->CopyUntill(&ssize, L':') ) return false;
   DWORD len = _wtoi(ssize.c_str());

   bool retVal = true;
   if( writer != NULL )
   {
      writer->Write(data, stream, len);
   } else
   {
      if( (len % 2) != 0 ) len++;
      len /= sizeof(wchar_t);
      while( len-- > 0 )
      {
         stream->Get();
         if( stream->EOS() ) break;
      }
   }

   return retVal;
}

static int CheckScale(const MemberType* member)
{
   OutStream os;
   member->ToStream(&os);

   const std::wstring& str = os.ToString();
   std::wstring::size_type pos = str.find(L'(');

   int scale = 0;
   if( pos != std::wstring::npos )
      scale = _wtoi(str.substr(pos+1).c_str());

   return scale;
}

NumericReader::NumericReader(const MemberType* member, ReceivedStream* stream) : BaseReader(member)
{
   wchar_t sym = stream->Get();
   if( sym == L'(' ) stream->CopyUntill(NULL, L')');
   else stream->Unget(sym);

   readDouble = false;
   scale = 0;
   if( member != NULL )
   {
      switch(member->type)
      {
      case MemberType::Float:
      case MemberType::Double:
         readDouble = true;
         break;
      case MemberType::Int64:
         break;
      case MemberType::Short:
      case MemberType::UShort:
      case MemberType::Integer:
      case MemberType::Unsigned:
      case MemberType::Long:
      case MemberType::ULong:
         scale = CheckScale(member);
         break;
      default:
         this->member = NULL;
         break;
      }
   }
}

void NumericReader::WriteDouble(IReflectableData* data, const wchar_t* p) const
{
   wchar_t *ep;
   double value = wcstod(p, &ep);
   if( member->size == sizeof(double) )
      member->SetValue(data, &value);
   else
   {
      float v = (float)value;
      member->SetValue(data, &v);
   }
}

void NumericReader::WriteInt(IReflectableData* data, const wchar_t* p) const
{
   if( member->size == sizeof(__int64) )
   {
      __int64 value = _wtoi64(p);
      member->SetValue(data, &value);
   } else
   {
      DWORD value = 0;

      bool sign = false;
      int sc = scale;
      bool rest = false;
      if( *p == L'-' )
      {
         sign = true;
         p++;
      }
      while( *p && (!rest || sc-- > 0) )
      {
         if( *p == L'.' ) rest = true;
         else value = value * 10 + *p - L'0';
         p++;
      }
      while( sc-- > 0 ) value *= 10;
      if( sign )
         value = (DWORD)(-(int)value);

      if( member->size == sizeof(DWORD) )
         member->SetValue(data, &value);
      else
      {
         WORD w = (WORD)value;
         member->SetValue(data, &value);
      }
   }
}

bool NumericReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   std::wstring svalue;
   while( true )
   {
      if( stream->EOS() ) return false;
      wchar_t sym = stream->Get();
      if( sym == L',' || sym == L']' )
         break;
      svalue.append(1, sym);
   }

   if( CanWrite() )
   {
      const wchar_t *p = svalue.c_str();
      if( readDouble ) WriteDouble(data, p);
      else WriteInt(data, p);
   }

   return !svalue.empty();
}

DateTimeReader::DateTimeReader(const MemberType* member, wchar_t type, ReceivedStream* stream) : BaseReader(member)
{
   if( member == NULL || member->type != MemberType::DateTime )
      this->member = NULL;

   flags = 0;
   if( type == L'd' )
   {
      flags |= ReadDate;
      type = stream->Get();
      if( type != L't' ) stream->Unget(type);
   }
   if( type == L't' ) flags |= ReadTime;
}

bool DateTimeReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   wchar_t buf[30]; // 2009-09-09 15:20:30
   wchar_t *p = buf, *ep = buf + sizeof(buf)/sizeof(buf[0]) - 1;

   SYSTEMTIME st;
   GetLocalTime(&st);

   while( !stream->EOS() && p != ep )
   {
      wchar_t sym = stream->Get();
      if( sym == L',' || sym == ']' ) break;
      *p++ = sym;
   }
   *p = L'\0';
   p = buf;

   bool error = (flags & ReadDate);
   if( flags & ReadDate )
   {
      st.wYear = (WORD)wcstol(p, &p, 10);
      if( *p != L'\0' )
      {
         st.wMonth = (WORD)wcstol(p+1, &p, 10);
         if( *p != L'\0' )
         {
            st.wDay = (WORD)wcstol(p+1, &p, 10);
            error = false;
         }
      }
   }

   if( !error && flags & ReadTime )
   {
      error = true;
      st.wHour = (WORD)wcstol(p, &p, 10);
      if( *p != L'\0' )
      {
         st.wMinute = (WORD)wcstol(p+1, &p, 10);
         if( *p != L'\0' )
         {
            st.wSecond = (WORD)wcstol(p+1, &p, 10);
            error = false;
         }
      }
   }

   if( !error && CanWrite() )
   {
      FILETIME ft;
      st.wMilliseconds = 0;
      SystemTimeToFileTime(&st, &ft);
      member->SetValue(data, &ft);
   }

   return !error;
}

CollectionReader::CollectionReader(const MemberType* member, const DataReflector& type, ReceivedStream* stream) : BaseReader(member)
{
   if( member != NULL && member->type != MemberType::Collection )
      this->member = NULL;

   childData = type.Create();
   reader = DataReader::CreateReader(type, stream);
}

CollectionReader::~CollectionReader()
{
   delete childData;
   delete reader;
}

bool CollectionReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   if( reader == NULL ) return false;

   IDataCollection* collection = NULL;
   if( CanWrite() )
   {
      collection = (IDataCollection*)member->GetValue(*data);
      collection->Clear();
   }

   ClearMembers(childData);
   while( reader->Read(childData, stream) ) 
   {
      if( collection != NULL )
      {
         UnbindingItem(childData, &sh);
         collection->Add(*childData, 0); // 0 - это фикция добаиться все равно в конец
         ClearMembers(childData);
      }
   }
   if( !stream->EOS() ) stream->Get(); // read ','
   return true;
}

//
//----------------------------------------------------- DataReader ---------------------------------------------
//
DataReader::DataReader()
{
}

DataReader::~DataReader()
{
   iterator i = begin();
   for( ; i != end(); i++ )
      delete (*i);
}

static bool CreateElement(IReaderElement** element, const MemberType *member, ReceivedStream *stream, const wchar_t* name, GetBinaryWriter gbw)
{
   wchar_t sym = stream->Get();
   switch( sym )
   {
   case L's':
      *element = new StringReader(member);
      break;
   case L'b':
      *element = new BinaryReader(gbw(name));
      break;
   case L'n':
      *element = new NumericReader(member, stream);
      break;
   case L'd':
   case L't':
      *element = new DateTimeReader(member, sym, stream);
      break;
   default:
      return false;
   }

   return true;
}

DataReader* DataReader::CreateReader(const DataReflector& type, ReceivedStream *stream, GetBinaryWriter gbw)
{
   DataReader* reader = new DataReader();

   std::wstring name;
   bool error = false;
   IReaderElement *element = NULL;
   while( !error && !stream->EOS() )
   {
      wchar_t sym = stream->Get();

      if( sym == L',' || sym == L']' )
      {
         if( element != NULL ) reader->push_back(element);
         element = NULL;
         name.clear();
         if( sym == L',' ) continue;
         break;
      }

      if( sym != L':' && sym != L'[' ) name.append(1, sym);
      else
      {
         int index = type.Find(name.c_str());

         if( sym == L':' )
         {
            error = !CreateElement(&element, ((index<0) ? (const MemberType*)NULL : &type.Type(index)),
               stream, name.c_str(), gbw);
         } else
         {
            if( index < 0 )
            {
               stream->Unget(sym);
               element = new EmptyReader(stream);
            } else
            {
               IReflectableData* pdata = type.Create();
               const MemberType& member = type.Type(index);
               IDataCollection* collection = (IDataCollection*)member.GetValue(*pdata);

               const DataReflector& chType = collection->DataType();
               element = new CollectionReader(&member, chType, stream);

               delete pdata;
            }
         }
      }
   }

   if( element != NULL || !name.empty() )
   {
      error = true;
      delete element;
   }

   if( error )
   {
      delete reader;
      reader = NULL;
   }
   return reader;
}

bool DataReader::Read(IReflectableData* data, ReceivedStream* stream) const
{
   if( stream->EOS() ) return false;

   wchar_t sym = stream->Get();
   if( sym != L'[' )
   {
      stream->Unget(sym);
      return false;
   }

   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( !(*i)->Read(data, stream) )
         return false;
   }

   return true;
}
//
//--------------------------------------- BinaryFileWriter --------------------------------------------------
//
BinaryFileWriter::BinaryFileWriter()
{
}

BinaryFileWriter::~BinaryFileWriter()
{
}

bool BinaryFileWriter::Write(IReflectableData* data, ReceivedStream* stream, DWORD size)
{
   if( size != 0 )
   {
      std::wstring fileName;

      GetFileName(&fileName);
      FILE *wr = _wfopen(fileName.c_str(), L"wb");
      if( wr )
      {
         while( size > 0 )
         {
            wchar_t sym = stream->Get();
            if( size == 1 )
            {
               fputc((char)sym, wr);
               break;
            } else
               fputwc(sym, wr);

            size -= sizeof(wchar_t);
         }

         fclose(wr);
      }

      AfterWrite(data, fileName);
   }
   stream->Get();

   return true;
}

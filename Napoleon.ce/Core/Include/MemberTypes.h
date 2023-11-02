/*
* Copyright (C), 2007, Денис Мосягин
*
* Простые типы
*
* ert   10/07/2007   creating
*/ 

#ifndef _MEMBER_TYPES_H
#define _MEMBER_TYPES_H

#include <Reflection.h>

#include <malloc.h>

//
// хранит указатель на строку wchar_t* память под строку не выделяется и не освобождается в типе. 
// Может хранить указатель на неудаляемую память
// для выделения памяти использовать внешние функции
//
struct StringType : public MemberType
{
   typedef WORD tsize;

   StringType(const wchar_t *name, short offset) : 
      MemberType(MemberType::String, name, sizeof(wchar_t*), offset)
   {
   }

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wcsncpy(buf, *(wchar_t**)GetValue(data), cch);
      buf[cch-1] = L'\0';
   }   

   virtual void ToStream(OutStream* stream) const
   {
      stream->Append(name);
      stream->Append(L":s");
   }

   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const
   {
      wchar_t* p = *(wchar_t**)GetValue(data);
      stream->AppendQuoted((p != NULL) ? p : L"");
   }

   // загоняем в поток
   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const
   {
      wchar_t *p = *(wchar_t**)GetValue(data);
      tsize len = (tsize)((wcslen(p)+1)*sizeof(wchar_t));

      long cp = streamer->CurrentPos();
      if( cp & 1 )
      {
         BYTE val = 0;
         streamer->Write(&val, sizeof(val));
      }

      if( streamer->Write(&len, sizeof(len)) == false )
         return false;

      if( len > 0 )
         return streamer->Write(p, len);

      return true;
   }

   // загружаем из потока - используется указатель из потока
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const
   {
      long cpl = (long)streamer.CurrentPos();
      if( cpl & 1 )
      {
         BYTE val = 0;
         streamer.Read(&val, sizeof(val));
      }

      tsize len;
      if( streamer.Read(&len, sizeof(len)) == false )
         return false;

      const wchar_t* cp;
      if( len )
         cp = streamer.GetString(len);
      else
         cp = L"";

      SetValue(data, &cp);

      return true;
   }
};

//
// предок всех числовых типов
//
struct NumericType : public MemberType
{
   NumericType(MemberType::DataTypes type, const wchar_t *name, short size, short offset) :
      MemberType(type, name, size, offset)
   {
   }

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wchar_t *digit = (wchar_t*)alloca(cch * sizeof(wchar_t));
      ValueToString(data, digit, cch);
      Formatting(digit, buf, cch);
   }

   virtual void ToStream(OutStream* stream) const
   {
      stream->Append(name);
      stream->Append(L":n");
   }

   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const
   {
      wchar_t buf[50];
      ValueToString(data, buf, sizeof(buf)/sizeof(buf[0]));
      stream->Append(buf);
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const = 0;
   virtual void Formatting(const wchar_t *src, wchar_t *buf, int cch) const
   {
      GetNumberFormatW(LOCALE_USER_DEFAULT, LOCALE_NOUSEROVERRIDE, src, NULL, buf, cch);
   }
};

//
// short
//
struct ShortType : public NumericType
{
   ShortType(const wchar_t *name, int offset) : 
      NumericType(MemberType::Short, name, sizeof(short), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%d", (int)(*(short*)GetValue(data)));
   }
};

//
// unsigned short
//
struct UShortType : public NumericType
{
   UShortType(const wchar_t *name, int offset) : 
      NumericType(MemberType::UShort, name, sizeof(unsigned short), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%u", (unsigned)(*(unsigned short*)GetValue(data)));
   }
};

//
// int
//
struct IntegerType : public NumericType
{
   IntegerType(const wchar_t *name, int offset) : 
      NumericType(MemberType::Integer, name, sizeof(int), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%d", *(int*)GetValue(data));
   }
};

//
// unsigned
//
struct UnsignedType : public NumericType
{
   UnsignedType(const wchar_t *name, int offset) : 
      NumericType(MemberType::Unsigned, name, sizeof(unsigned), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%u", *(unsigned*)GetValue(data));
   }
};

//
// long
//
struct LongType : public NumericType
{
   LongType(const wchar_t *name, int offset) : 
      NumericType(MemberType::Long, name, sizeof(long), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%l", *(long*)GetValue(data));
   }
};

//
// unsigned long
//
struct ULongType : public NumericType
{
   ULongType(const wchar_t *name, int offset) : 
      NumericType(MemberType::ULong, name, sizeof(unsigned long), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%lu", *(unsigned long*)GetValue(data));
   }
};

//
// __int64
//
struct Int64Type : public NumericType
{
   Int64Type(const wchar_t *name, int offset) : 
      NumericType(MemberType::Int64, name, sizeof(__int64), offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      __int64 value = *(__int64*)GetValue(data);
      wchar_t templ[] = L"-%d%09d";
      wchar_t *pt = templ+1;

      if( value < 0 )
      {
         pt = templ;
         value = -value;
      }
      wsprintfW(buf, pt, value / 1000000000, value % 1000000000);
   }
};

//
// unsigned array
//
struct ULongArrayType : public NumericType
{
   ULongArrayType(const wchar_t *name, int size, int offset) : 
      NumericType(MemberType::ULong, name, size, offset)
   {
   }

protected:
   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      wsprintfW(buf, L"%lu", *(unsigned long*)GetValue(data));
   }
};
//
// double
//
struct DoubleType : public NumericType
{
   DoubleType(const wchar_t *name, int offset) : 
      NumericType(MemberType::Double, name, sizeof(double), offset)
   {
      format.prec = 2;
   }

   struct DoubleFormat : public TypeFormat
   {
      short prec;
   } format;

   virtual void SetFormat(const TypeFormat &format) { this->format = (const DoubleFormat&)format; }

   virtual void ToStream(OutStream* stream) const
   {
      wchar_t buf[10];
      swprintf(buf, L"(%d)", format.prec);

      NumericType::ToStream(stream);
      stream->Append(buf);
   }

protected:
   DoubleType(MemberType::DataTypes type, const wchar_t *name, short size, short offset) : 
      NumericType(type, name, size, offset)
   {
      format.prec = 2;
   }

   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      swprintf(buf, L"%.*f", (int)format.prec, *(double*)GetValue(data));
   }

   virtual void Formatting(const wchar_t *src, wchar_t *buf, int cch) const
   {
      nformat.NumDigits = format.prec;
      NUMBERFMTW *pfmt = &nformat;
      GetNumberFormatW(LOCALE_USER_DEFAULT, LOCALE_NOUSEROVERRIDE, src, pfmt, buf, cch);
   }

public:
   struct NumberFormat : public NUMBERFMTW
   {
      NumberFormat();
      wchar_t sepbuf[4], thubuf[4];
   };

   static NumberFormat nformat;
};

//
// FILETIME
//
struct DateTimeType : public MemberType
{
   struct DateTimeFormat : public TypeFormat
   {
      enum Apperance { DateTime, DateOnly, TimeOnly } appearance;
   };

   DateTimeType(const wchar_t *name, int offset) : 
      MemberType(MemberType::DateTime, name, sizeof(FILETIME), offset)
   {
      format.appearance = DateTimeFormat::DateOnly;
   }

   DateTimeType(const wchar_t *name, int offset, DateTimeType::DateTimeFormat::Apperance fmt) : 
      MemberType(MemberType::DateTime, name, sizeof(FILETIME), offset)
   {
      format.appearance = fmt;
   }

   DateTimeFormat format;

   virtual void SetFormat(const TypeFormat &format) { this->format = (const DateTimeFormat&)format; }

   virtual void ToStream(OutStream* stream) const
   {
      stream->Append(name);
      stream->Append(L':');
      
      switch( format.appearance )
      {
      case DateTimeFormat::DateOnly:
         stream->Append(L'd');
         break;
      case DateTimeFormat::TimeOnly:
         stream->Append(L't');
         break;
      default:
         stream->Append(L"dt");
         break;
      }
   }

   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const
   {
      wchar_t buf[50];
      SYSTEMTIME st;
      FileTimeToSystemTime((FILETIME*)GetValue(data), &st);

      if( format.appearance != DateTimeFormat::TimeOnly )
      {
         wsprintfW(buf, L"%d-%02d-%02d", st.wYear, st.wMonth, st.wDay);
         stream->Append(buf);
      }

      if( format.appearance != DateTimeFormat::DateOnly )
      {
         if( format.appearance != DateTimeFormat::TimeOnly ) stream->Append(L' ');
         wsprintfW(buf, L"%02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);
         stream->Append(buf);
      }
   }

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      SYSTEMTIME st;
      FileTimeToSystemTime((FILETIME*)GetValue(data), &st);

      if( format.appearance == DateTimeFormat::DateOnly )
         GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, cch);
      else if( format.appearance == DateTimeFormat::TimeOnly )
         GetTimeFormatW(LOCALE_USER_DEFAULT, TIME_NOSECONDS, &st, NULL, buf, cch);
      else
      {
         int wch = GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, cch);
         buf[wch-1] = L' ';
         GetTimeFormatW(LOCALE_USER_DEFAULT, TIME_NOSECONDS, &st, NULL, buf + wch, cch - wch - 1);
      }
   }
};

//
// Тип которым описывается поле коллекция vector_t<T> collection type
//
template <typename T> struct vector_t : public IDataCollection, std::vector<T>
{
   virtual const DataReflector& DataType() const { T a; return a.GetType(); }

   virtual int Count() const { return (int)size(); }

   // ------ методы коллекции (получить, добавить, удалить, обновить) -------
   virtual bool Get(IReflectableData* data, int index) const
   {      
      if( index >= (int)size() ) return false;

      *(T*)data = at(index);
      return true;
   }

   virtual IReflectableData* GetItem(int index)
   {
      if( index >= (int)size() ) return NULL;
      return &at(index);
   }

   virtual bool Add(const IReflectableData& data, int index)
   {
      push_back((const T&)data);
      index = (int)size()-1;
      return true;
   }

   virtual bool Remove(int index)
   {
      if( index >= (int)size() ) return false;

      iterator i = begin();
      erase(i+index);
      return true;
   }

   virtual bool Update(const IReflectableData& data, int index)
   {
      if( index >= (int)size() ) return false;
      at(index) = (const T&)data;
      return true;
   }

   virtual void Clear()
   {
      clear();
   }
};

//
// хранит vector_t
//
template <typename T> struct CollectionType : public MemberType
{
   typedef short tsize;

   CollectionType(const wchar_t *name, int offset) : 
      MemberType(MemberType::Collection, name, sizeof(vector_t<T>), offset)
   {
   }

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
   }

   // для элемента типа Collection src это IDataCollection
   virtual void SetValue(IReflectableData *data, const void *_src) const
   { 
      vector_t<T> *dest = (vector_t<T>*)((BYTE*)data + offset);
      const IDataCollection *src = (const IDataCollection *)_src;

      ATLASSERT(dest->DataType() == src->DataType());

      dest->clear();
      IReflectableData* element = dest->DataType().Create();
      int count = src->Count();
      for( int i=0; i<count; i++ )
      {
         src->Get(element, i);
         dest->Add(*element, i);
      }

      delete element;
   }

   // для элемент типа Collection возвращает IDataCollection*
   virtual void* GetValue(const IReflectableData &data) const
   { 
      vector_t<T> *cd = (vector_t<T>*)((BYTE*)&data + offset);
      return (IDataCollection*)cd;
   }

   // загоняем в поток
   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const
   {
      vector_t<T> *src = (vector_t<T>*)((BYTE*)&data + offset);
      tsize size = (int)src->size();

      if( streamer->Write(&size, sizeof(size) ) == false )
         return false;

      const DataReflector &type = src->DataType();      
      std::vector<T>::const_iterator i = src->begin();
      for( ;i!=src->end(); i++ )
         if( type.Serialize(streamer, (*i)) == false )
            return false;

      return true;
   }

   // загружаем из потока - используется указатель из потока
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const
   {
      vector_t<T> *src = (vector_t<T>*)((BYTE*)data + offset);
      tsize size;

      if( streamer.Read(&size, sizeof(size)) == false )
         return false;

      const DataReflector &type = src->DataType();      
      IReflectableData* element = src->DataType().Create();
      src->clear();
      while( size-- > 0 )
      {
         if( type.Deserialize(element, streamer) == false )
         {
            delete element;
            return false;
         }

         src->Add(*element, (int)src->size());
      }

      delete element;
      return true;
   }

   virtual void ToStream(OutStream* stream) const
   {
      const DataReflector &type = T().GetType();
      type.ToStream(stream, name);
   }

   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const
   {
      const DataReflector &type = T().GetType();
      vector_t<T> *src = (vector_t<T>*)((BYTE*)&data + offset);

      if( src->size() == 0 )
         stream->Append(L"[]");
      else
      {
         std::vector<T>::const_iterator i = src->begin();
         for( ;i!=src->end(); i++ )
            type.DataToStream(stream, (*i));
      }
   }
};

//
// Тип для поддержки наследования
//
struct ParentType : public MemberType
{
   ParentType(const wchar_t *name) : 
      MemberType(MemberType::Parent, name, 0, 0)
   {
   }

   virtual void  SetValue(IReflectableData *data, const void *src) const {}

   // для элемент типа Collection возвращает IDataCollection*
   virtual void* GetValue(const IReflectableData &data) const { return (void*)&data; }

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const {}   

   // загоняем в поток
   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const
   {
      const DataReflector& reflector = GetTypeReflector(name);
      return reflector.Serialize(streamer, data);
   }

   // загружаем из потока - используется указатель из потока
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const
   {
      const DataReflector& reflector = GetTypeReflector(name);
      return reflector.Deserialize(data, streamer);
   }
};

inline void ConvertScaling(wchar_t *buf, DWORD value, DWORD scale)
{
   DWORD count = 1, base = 10;
   while( base < scale ) { count++; base = base * 10; }

#ifdef UNDER_CE
   wsprintfW(buf, L"%lu.%0*lu", value / scale, count, value % scale);
#else
   swprintf(buf, L"%lu.%0*lu", value / scale, count, value % scale);
#endif
}

inline void ConvertScaling(wchar_t *buf, long value, DWORD scale)
{
   DWORD count = 1, base = 10;
   while( base < scale ) { count++; base = base * 10; }

   bool blw0 = false;
   wchar_t *templ = L"-%ld.%0*ld";
   wchar_t *pt = templ+1;

   if( value < 0 )
   {
      blw0 = true;
      value = -value;
      pt = templ;
   }
   
#ifdef UNDER_CE
   wsprintfW(buf, pt, value / (long)scale, count, value % scale);
#else
   swprintf(buf, pt, value / (long)scale, count, value % scale);
#endif
}

void FormatScaling(const wchar_t *src, wchar_t *buf, int cch, DWORD rest, DWORD scale, bool hideRest);

//
// unsigned long
//
struct ULongScaleType : public NumericType
{
   ULongScaleType(const wchar_t *name, int offset, int scale, bool hideRest = false) :
      NumericType(MemberType::ULong, name, sizeof(unsigned long), offset)
   {
      format.scale = scale;
      format.hideRest = hideRest;
   }

   struct ScaleFormat : public TypeFormat
   {
      enum FLAGS { SF_ROUND = 1, SF_SIGNED = 2 };

      bool hideRest;
      unsigned scale;
      const wchar_t *rest;
      DWORD flag;

      ScaleFormat() { rest = L""; flag = 0; }
   } format;

   virtual void SetFormat(const TypeFormat &format)
   {
      bool sign = ((this->format.flag & ScaleFormat::SF_SIGNED) != 0);
      this->format = (const ScaleFormat&)format;
      if( sign )
         this->format.flag |= ScaleFormat::SF_SIGNED;
   }

   virtual void ToStream(OutStream* stream) const
   {
      wchar_t buf[20];
      DWORD count = 1, base = 10;
      while( base < format.scale ) { count++; base = base * 10; }

      wsprintfW(buf, L"(%d)", count);
      NumericType::ToStream(stream);
      stream->Append(buf);
   }

protected:
   ULongScaleType(MemberType::DataTypes type, const wchar_t *name, int size, int offset, int scale, bool hideRest) : 
      NumericType(type, name, size, offset)
   {
      format.scale = scale;
      format.hideRest = hideRest;
   }

   virtual void ValueToString(const IReflectableData &data, wchar_t *buf, int cch) const
   {
      DWORD value = DValue(data);

      if( format.flag & ScaleFormat::SF_SIGNED )
         rest = abs((int)value) % format.scale;
      else
         rest = value % format.scale;

      if( format.flag & ScaleFormat::SF_ROUND && rest != 0 )
      {
         DWORD n = value / format.scale;
         value = n * format.scale;
         rest = 0;
      }

      if( format.flag & ScaleFormat::SF_SIGNED )
         ConvertScaling(buf, (long)value, format.scale);
      else
         ConvertScaling(buf, value, format.scale);
   }
   
   virtual void Formatting(const wchar_t *src, wchar_t *buf, int cch) const
   {
      FormatScaling(src, buf, cch, rest, format.scale, format.hideRest);

      if( *format.rest != L'\0' )
         wcscat(buf, format.rest);
   }

   virtual DWORD DValue(const IReflectableData &data) const
   {
      return *(DWORD*)GetValue(data);
   }

   mutable DWORD rest;
};

struct LongScaleType : public ULongScaleType
{
public:
   LongScaleType(const wchar_t *name, int offset, int scale, bool hideRest = false) :
      ULongScaleType(MemberType::Long, name, sizeof(long), offset, scale, hideRest)
      {
         format.flag |= ScaleFormat::SF_SIGNED;
      }

protected:
   virtual DWORD DValue(const IReflectableData &data) const
   {
      return *(long*)GetValue(data);
   }
};

struct ShortScaleType : public ULongScaleType
{
public:
   ShortScaleType(const wchar_t *name, int offset, int scale, bool hideRest = false) :
      ULongScaleType(MemberType::Short, name, sizeof(short), offset, scale, hideRest)
      {
         format.flag |= ScaleFormat::SF_SIGNED;
      }

protected:
   virtual DWORD DValue(const IReflectableData &data) const
   {
      return *(short*)GetValue(data);
   }
};

struct UShortScaleType : public ULongScaleType
{
public:
   UShortScaleType(const wchar_t *name, int offset, int scale, bool hideRest = false) :
      ULongScaleType(MemberType::UShort, name, sizeof(unsigned short), offset, scale, hideRest)
      {
      }

protected:
   virtual DWORD DValue(const IReflectableData &data) const
   {
      return *(unsigned short*)GetValue(data);
   }
};

#endif


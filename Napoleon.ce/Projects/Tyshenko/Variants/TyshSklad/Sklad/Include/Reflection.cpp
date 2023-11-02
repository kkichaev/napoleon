/*
* Copyright (C), 2007, Денис Мосягин
*
* Все объекты для описания типов
*
* ert   10/07/2007   creating
*/ 

#include "stdafx.h"
#include <atldef.h>
#include <TypeHolder.h>
#include <set>

struct THComparer
{
   bool operator()(const DataReflector* _Left, const DataReflector* _Right) const
   {
      return (*_Left) < (*_Right);
   }
};

class TypeHolder : public std::set<DataReflector*, THComparer>
{
public:
   TypeHolder() {}

   ~TypeHolder()
   {
      iterator i = begin();
      while( i != end() )
      {
         delete (*i);
         i++;
      }
   }

   DataReflector& GetType(const wchar_t *typeName)
   { 
      DataReflector *r = FindType(typeName);
      ATLASSERT(r != NULL);
      return *r; 
   }

   DataReflector* FindType(const wchar_t *typeName)
   { 
      DataReflector dr(NULL, typeName);
      iterator fnd = find(&dr);
      return (fnd != end()) ? (*fnd) : NULL;
   }

   void RegisterType(DataReflector *type)
   {
      ATLASSERT(find(type) == end());
      insert(type);
   }

   void RemoveType(const wchar_t *typeName)
   {
      DataReflector dr(NULL, typeName);
      iterator fnd = find(&dr);
      if( fnd != end() )
      {
         delete (*fnd);
         erase(fnd);
      }
   }
};

DoubleType::NumberFormat DoubleType::nformat;
DoubleType::NumberFormat::NumberFormat()
{
   int cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_SDECIMAL, sepbuf, 4);
   sepbuf[cch] = L'\0';
   cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_STHOUSAND, thubuf, 4);
   thubuf[cch] = L'\0';

   NumDigits = 2;
   LeadingZero = 1;
   Grouping = 3;
   lpDecimalSep  = sepbuf;
   lpThousandSep = thubuf;
   NegativeOrder = 1;
}

#pragma warning(disable : 4073)
#pragma init_seg(lib)
static TypeHolder typeHolder;

const DataReflector& GetTypeReflector(const wchar_t *typeName)
{
   return typeHolder.GetType(typeName);
}

const DataReflector* FindTypeReflector(const wchar_t *typeName)
{
   return typeHolder.FindType(typeName);
}

void RemoveTypeReflector(const wchar_t *typeName)
{
   typeHolder.RemoveType(typeName);
}

void RegisterTypeReflector(DataReflector *type)
{
   typeHolder.RegisterType(type);
}

void FormatScaling(const wchar_t *src, wchar_t *buf, int cch, DWORD rest, DWORD scale, bool hideRest)
{
   DWORD tval = 10;
   WORD len = 1;
   if( scale > 20 )
      while( tval < scale ) { len++; tval *= 10; }

   if( hideRest && rest == 0 )
      DoubleType::nformat.NumDigits = 0;
   else
      DoubleType::nformat.NumDigits = len;

   NUMBERFMTW *pfmt = &DoubleType::nformat;
   GetNumberFormatW(LOCALE_SYSTEM_DEFAULT, 0, src, pfmt, buf, cch);
}

DataReflector::~DataReflector()
{
   iterator i = begin();
   while( i != end() )
   {
      delete (*i);
      i++;
   }

   std::vector<std::wstring*>::iterator si = allocatedStrings.begin();
   while( si != allocatedStrings.end() )
   {
      delete (*si);
      si++;
   }
}

bool DataReflector::operator == (const DataReflector &src) const
{
   int i = Count(); 
   if( i != src.Count() ) return false;
   while( i-- > 0 )
   {
      if( Type(i) != src.Type(i) )
         return false;
   }
   return true;
}

int DataReflector::Count() const 
{ 
   int count = size();
   const_iterator i = begin();
   if( (*i)->type == MemberType::Parent )
   {
      const DataReflector &reflector = GetTypeReflector((*i)->name);
      count += reflector.Count() - 1;
   }
   return count; 
}

bool DataReflector::Serialize(StreamWriter *streamer, const IReflectableData &data) const
{
   const_iterator i = begin();
   for( ;i!=end(); i++ )
   {
      if( !(*i)->Serialize(streamer, data) )
         return false;
   }

   return true;
}

bool DataReflector::Deserialize(IReflectableData *data, const StreamReader &streamer) const
{
   const_iterator i = begin();
   for( ;i!=end(); i++ )
   {
      if( !(*i)->Deserialize(data, streamer) )
         return false;
   }

   return true;
}

void DataReflector::ToStream(OutStream* stream, const wchar_t* typeName) const
{
   int count = Count();

   stream->Append((typeName != NULL) ? typeName : name);
   stream->Append(L'[');
   for( int i=0; i<count; i++ )
   {
      if( i != 0 ) stream->Append(L',');
      Type(i).ToStream(stream);
   }
   stream->Append(L']');
}

void DataReflector::DataToStream(OutStream* stream, const IReflectableData& data) const
{
   int count = Count();
   stream->Append(L'[');
   for( int i=0; i<count; i++ )
   {
      if( i != 0 ) stream->Append(L',');
      Type(i).DataToStream(stream, data);
   }
   stream->Append(L']');
}

const MemberType& DataReflector::Type(int index) const
{
   //ATLASSERT(index < Count()); 
   const_iterator i = begin();      
   if( (*i)->type == MemberType::Parent )
   {
      const DataReflector &reflector = GetTypeReflector((*i)->name);
      int count = reflector.Count()-1;

      if( index <= count )
         return reflector.Type(index);

      index -= count;
   }
   return *operator[](index); 
}

const MemberType& DataReflector::Type(const wchar_t *field) const
{
   for( int i=Count()-1; i >=0; i-- )
   {
      const MemberType &type = Type(i);
      if( !wcscmp(type.name, field) )
         return type;
   }

   ATLASSERT(false);
   return *(*begin());
}

int DataReflector::Find(const wchar_t *field, bool ignoreCase) const 
{
   for( int i=Count()-1; i>=0; i-- )
   {
      const MemberType& type = Type(i);
      if(ignoreCase)
		{
			if(!wcsicmp(type.name, field) )
				return i;
		} else
		{
			if( !wcscmp(type.name, field) )
			  return i;
		}
   }

   return -1;
}

void DataReflector::AddMember(MemberType *mt)
{ 
   if( mt->type == MemberType::Parent && size() )
   {
      push_back(front());
      front() = mt;
   } else
      push_back(mt); 
}

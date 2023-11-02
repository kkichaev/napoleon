/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Secuence source
 *
 * ert   16/09/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include "session.h"
#include "dbf.h"
#include "token.h"
#include <algorithm>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class SequenceReader : public IDataSource::IReader
{
public:
   SequenceReader(const SessionObject& object, IDataSource::IReader* parent);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual const MemberFormat* Type(const wchar_t* name) const
   {
      std::vector<MemberFormatAdd>::const_iterator fi = formats.begin();
      for( ; fi != formats.end(); fi++ )
         if( fi->name.compare(name) == 0 )
            return &(*fi);

      return NULL;
   }

   virtual const Member* Value(const wchar_t* name) const
   {
      std::vector<MemberFormatAdd>::const_iterator fi = formats.begin();
      std::vector<Member>::const_iterator mi = values.begin();
      for( ; fi != formats.end() && mi != values.end(); fi++, mi++ )
         if( fi->name.compare(name) == 0 )
            return &(*mi);
      return NULL;
   }

   virtual void Close()
   {
   }

   virtual void Remove() {}

   const GRServer::Format* objectFormat;

   IDataSource::IReader* source;
   int startIndex, curIndex;

   struct MemberFormatAdd : public MemberFormat
   {
      std::wstring data;
   };

   std::vector<MemberFormatAdd> formats;
   std::vector<Member> values;

protected:
   void SetDestFormat();

   const IObjectData* objDef;
   const SessionObject& object;
};

IDataSource::IReader* SequenceSC::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const Parameter *psource = parameters.Find(L"source", 0);
   if( psource == NULL ) return NULL;

   const SessionObject& object = *(const SessionObject*)iobject.Self();
   const Session& session = (const Session&)object.GetSession();
   Token src;
   if( !session.Parse(&src, psource->value, &object) || src.type != Token::ttSource ) return NULL;

   Token sit;
   SequenceReader* sr = new SequenceReader(object, src.value.source->reader);
   const Parameter *si = parameters.Find(L"startIndex", -1);
   if( si != NULL  && session.Parse(&sit, si->value, &object) )
      sr->startIndex = (int)(sit.value.number + 0.05);

   return sr;
}

SequenceReader::SequenceReader(const SessionObject& _object, IDataSource::IReader *p) :
   source(p), startIndex(1), object(_object)
{
   objectFormat = object.format;
   objDef = object.GetObjectDef();
}


inline bool AcceptedFormat(MemberFormat::MemberType t)
{
   switch(t)
   {
   case MemberFormat::mtString:
   case MemberFormat::mtNumber:
   case MemberFormat::mtDateTime:
      return true;
   default: break;
   }

   return false;
}

void SequenceReader::SetDestFormat()
{
   if( objDef != NULL )
   {
      wchar_t num[10];
      wsprintf(num, L"%d", startIndex);

      GRServer::ObjectDef::Fields::const_iterator i = objDef->fields.begin();
      for( ; i != objDef->fields.end(); i++ )
      {
         int dindex = object.format->FindMember(i->format.name.c_str());
         if( dindex < 0 )
            continue;

         const MemberFormat& destFormat = (*object.format)[dindex];
         if( !AcceptedFormat(destFormat.type) )
            continue;

         std::wstring tfield = i->data + num;
         const MemberFormat* checkfmt = source->Type(tfield.c_str());
         // special case for startIndex == 0
         if( checkfmt == NULL && startIndex == 0 )
            checkfmt = source->Type(i->data.c_str());

         if( checkfmt != NULL && checkfmt->type == destFormat.type )
         {
            MemberFormatAdd mfa;
            *(MemberFormat*)(&mfa) = destFormat;
            mfa.data = i->data;
            formats.push_back(mfa);
         }
      }

      curIndex = startIndex;
      objDef = NULL;
   }
}

bool SequenceReader::MoveNext(Object *parentObject)
{
   SetDestFormat();

   if( formats.size() == 0 )
      return false;

   wchar_t num[10];
   wsprintf(num, L"%d", curIndex);

   values.clear();
   std::vector<MemberFormatAdd>::const_iterator fi = formats.begin();
   for( ; fi != formats.end(); fi++ )
   {
      std::wstring tfield((*fi).data + num);
      const Member* pm = source->Value(tfield.c_str());
      // special case for curIndex == 0
      if( pm == NULL && curIndex == 0 )
         pm = source->Value((*fi).data.c_str());

      if( pm == NULL )
      {
         curIndex = startIndex;
         return false;
      }

      Member m;
      switch( (*fi).type )
      {
      case MemberFormat::mtString:
         m.str = new CString(*pm->str);
         break;
      case MemberFormat::mtNumber:
         m.number = pm->number;
         break;
      case MemberFormat::mtDateTime:
         m.datetime = pm->datetime;
         break;
      default: break;
      }

      values.push_back(m);
   }
   curIndex++;
   return true;
}

bool SequenceReader::Get(Object* o) const
{
   if( formats.size() == 0 ) return false;

   std::vector<MemberFormatAdd>::const_iterator fi = formats.begin();
   std::vector<Member>::const_iterator mi = values.begin();
   for( ; fi != formats.end() && mi != values.end(); fi++, mi++ )
      o->Assign((*mi), fi->name.c_str());

   return true;
}

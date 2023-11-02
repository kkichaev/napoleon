/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * PriceTable
 *
 * ert   31/07/2010   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "parse.h"
#include "folderset.h"
#include "dbftblset.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class DBFPrice : public IDataSource::IReader
{
public:
   DBFPrice(const SessionObject& object, IDataSource::IReader* reader);
   ~DBFPrice();

   virtual bool MoveNext(Object *parentObject);
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);

   virtual bool Get(Object* o) const;
   virtual void Remove() { reader->Remove(); }
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return reader->Type(name); }
   virtual const Member* Value(const wchar_t* name) const { return reader->Value(name); }

   virtual void AddChild(const std::wstring& childName, IReader* chrdr)
   {
      if( reader )
         reader->AddChild(childName, chrdr);
   }

   virtual void SetBaseFolder(const std::string& baseFolder)
   {
      if( reader )
         reader->SetBaseFolder(baseFolder);
   }

protected:
   const SessionObject& object;
   Object *curObject;

   int qtyIndex;
   bool filterOn;
   IDataSource::IReader* reader;
};

//
//------------------------------- DBFPriceTable ------------------------------------
//
DBFPrice::DBFPrice(const SessionObject& obj, IDataSource::IReader* reader) : object(obj)
{
   filterOn = true;
   this->reader = reader;

   qtyIndex = object.format->FindMember(L"qty");
   if( qtyIndex >= 0 )
   {
      if( object.format->at(qtyIndex).type != MemberFormat::mtNumber )
         qtyIndex = -1;
   }
   curObject = NULL;
}

DBFPrice::~DBFPrice()
{
   Close();
   delete reader;
}

bool DBFPrice::MoveNext(Object *parentObject)
{
   bool ret = reader->MoveNext(parentObject);
   if( filterOn && qtyIndex >= 0 )
   {
      if( curObject ) delete curObject;
      curObject = Object::Create(*object.format);
      Member &v = curObject->at(qtyIndex);

      do
      {
         reader->Get(curObject);
         if( v.number > 0.0 )
            break;
         ret = reader->MoveNext(parentObject);
      } while( ret );
      
   }

   return ret;
}

bool DBFPrice::Get(Object* o) const
{
   if( filterOn && qtyIndex >= 0 )
   {
      if( curObject )
      {
         curObject->MoveTo(o);
         return true;
      }

      return false;
   }

   return reader->Get(o);
}

void DBFPrice::Close()
{
   if( curObject )
   {
      delete curObject;
      curObject = NULL;
   }

   reader->Close();
}

static bool CheckFilter(const wchar_t *p, bool &bValue, const wchar_t **ep)
{
   std::wstring value;
   while( *p != L'(' && *p ) p++;
   if( *p ) p++;
   while( *p && *p == L' ' ) p++;

   while( *p && *p != L' ' && *p != L')' )
   {
      value.append(1, *p);
      p++;
   }

   *ep = (*p) ? p + 1 : p;

   bool ret = false;
   if( !value.empty() )
   {
      ret = true;
      if( wcscmp(value.c_str(), L"FALSE") == 0 ) bValue = false;
      else if( wcscmp(value.c_str(), L"TRUE") == 0 ) bValue = true;
      else ret = false;
   }

   return ret;
}

bool DBFPrice::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   bool ret = true;
   wchar_t *buf = _wcsdup(filter);
   CharUpper(buf);
   wchar_t *p = wcsstr(buf, L"SETQTYFILTER");
   if( p != NULL )
   {
      const wchar_t *ep;
      ret = CheckFilter(p + sizeof(L"SETQTYFILTER")/sizeof(wchar_t) -1 , filterOn, &ep);
      if( ret )
      {
         if( *ep ) wcscpy(p, ep);
         else *p = L'\0';
      }
   }

   if( ret && *buf ) ret = reader->SetFilter(buf, object);
   free(buf);

   return ret;
}

//
//------------------------------- DBFPriceTable ------------------------------------
//
IDataSource::IReader* DBFPriceTable::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   IDataSource::IReader* reader = DBFTableSet::CreateReader(parameters, object);

   if( reader != NULL )
      reader = new DBFPrice(*(SessionObject*)object.Self(), reader);

   return reader;
}


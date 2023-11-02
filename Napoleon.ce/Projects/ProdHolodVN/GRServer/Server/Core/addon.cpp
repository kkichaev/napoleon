/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Add on - дополнения для разных клиентов
 *
 * ert   16/06/2010   creating
 */ 
#include "stdafx.h"
#include <vector>
#include <map>
#include "server.h"
#include "servobj.h"
#include "objdef.h"
#include "parse.h"
#include "datasource.h"
#include "session.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include "creators.h"
#include "objects.h"
#include "sources.h"
#include "dbf.h"
#include "StdConsts.h"

using namespace GRServer;
using namespace std;

struct DiscountSourceCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"DiscountTable"; }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object, 
      FilterReader::Data& filter, const ParamList& parameters) const;
};

class DiscountReader : public DBFReader
{
public:
   DiscountReader(const GRServer::Format& fmt) : DBFReader(fmt), data(NULL) {}
   ~DiscountReader();

   virtual bool Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter);
   virtual bool MoveNext(GRServer::Object *parent);
   virtual bool Get(GRServer::Object *object) const;

protected:
   bool inited;
   ServObject *data;
   mutable ServObject::const_iterator current;
};

struct DiscountKey
{
   std::wstring id; // IDO
   std::wstring num; //NUM

   bool operator< (const DiscountKey& key) const;
   void Load(const DataForm& base);
};

struct DiscountPriceKey
{
   int hash;
   double discount;
   double sum;
   double qty;

   bool operator< (const DiscountPriceKey& key) const;
   void Load(Object *curObj, const DataForm& base);
};

class DiscountObjReader
{
public:
   DiscountObjReader(Session& s, ServObject* dest, const SessionObject* oItems);
   ~DiscountObjReader();

   void Read(const DataForm& base);

protected:
   int PriceToIndex(const DataForm& base) const;

   ServObject* dest;

   std::map<std::wstring, WORD> priceMap;
   typedef std::map<DiscountKey, GRServer::Object*> ObjMap;
   mutable ObjMap objects;

   typedef std::map<DiscountPriceKey, GRServer::Object*> ObjPrcMap;
   mutable ObjPrcMap objectsPrc;

   int iId, iDog, iDsc, iQty, iSum, iItem, iItemIdx, iPrcItem;
   GRServer::Format *itemFormat;
   GRServer::Format *itemPriceFormat;
};

IDataSource::IReader* DiscountSourceCreator::Create(const std::string& fileName, const ISessionObject& iobject, 
      FilterReader::Data& filter, const ParamList& parameters) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   DBFReader *reader = new DiscountReader(*object.format);
   if( !reader->Open(fileName, object, filter) )
   {
      delete reader;
      reader = NULL;
   }
   return reader;
}

bool DiscountKey::operator <(const DiscountKey& key) const
{
   double val = id.compare(key.id);
   if( val < 0 ) return true;
   if( val > 0 ) return false;

   val = num.compare(key.num);
   if( val < 0 ) return true;
   //if( val > 0 ) return false;

   //val = discount - key.discount;
   //if( val < 0 ) return true;
   //if( val > 0 ) return false;

   //val = sum - key.sum;
   //if( val < 0 ) return true;
   //if( val > 0 ) return false;

   //val = qty - key.qty;
   //if( val < 0 ) return true;
   return false;
}

void DiscountKey::Load(const DataForm& base)
{
   std::string buf;

   USES_CONVERSION;

   Trunc(base["IDO"], &buf);
   id = A2W_CP(buf.c_str(), CP_OEMCP);

   //discount = atof(base["DSC"]);
   //sum = atof(base["MAX_SUM"]);
   //qty = atof(base["MAX_QTY"]);

   Trunc(base["NUM"], &buf);
   if( !buf.empty() )
      num = A2W_CP(buf.c_str(), CP_OEMCP);
}

bool DiscountPriceKey::operator <(const DiscountPriceKey& key) const
{
   double val;
   val = hash - key.hash;
   if( val < 0 ) return true;
   if( val > 0 ) return false;

   val = discount - key.discount;
   if( val < 0 ) return true;
   if( val > 0 ) return false;

   val = sum - key.sum;
   if( val < 0 ) return true;
   if( val > 0 ) return false;

   val = qty - key.qty;
   if( val < 0 ) return true;
   return false;
}

void DiscountPriceKey::Load(Object *curObj, const DataForm& base)
{
   hash = (int)curObj;
   discount = atof(base["DSC"]);
   sum = atof(base["MAX_SUM"]);
   qty = atof(base["MAX_QTY"]);
}

DiscountObjReader::DiscountObjReader(Session& s, ServObject* dest, const SessionObject* oItems)
{
   this->dest = dest;
   this->itemFormat = oItems->format;

   const SessionObject *oprc = (const SessionObject *)oItems->GetChild(L"items")->Self();
   itemPriceFormat = oprc->format;

   const SessionObject* price = s.FindObject(L"Price", NULL);
   if( price == NULL )
   {
      const ISessionObject *pI = s.LoadObject(L"Price", NULL, L"SetQtyFilter(False)");
      price = (pI == NULL) ? NULL : (const SessionObject*)pI->Self();
   }

   if( price != NULL )
   {
      const GRServer::Format* f = dest->format;

      iId = f->FindMember(L"id");
      iDog = f->FindMember(L"dogovor");
      iItem = f->FindMember(L"items");

      iDsc = itemFormat->FindMember(L"discount");
      iQty = itemFormat->FindMember(L"qty");
      iSum = itemFormat->FindMember(L"sum");
      iPrcItem = itemFormat->FindMember(L"items");

      iItemIdx = itemPriceFormat->FindMember(L"index");

      int idFld = price->format->FindMember(L"id");
      if( idFld >= 0 )
      {
         WORD index = 0;

         SessionObject::const_iterator cp = price->begin();
         for( ; cp != price->end(); cp++ )
            priceMap[*(*cp)->at(idFld).str] = index++;
      }
   }
}

DiscountObjReader::~DiscountObjReader()
{
}

int DiscountObjReader::PriceToIndex(const DataForm& base) const
{
   std::string buf;
   Trunc(base["ID"], &buf);
   
   USES_CONVERSION;
   std::wstring id(A2W_CP(buf.c_str(), CP_OEMCP));

   std::map<std::wstring, WORD>::const_iterator fnd = priceMap.find(id);
   int index = (fnd == priceMap.end()) ? -1 : (int)fnd->second;

   return index;
}

void DiscountObjReader::Read(const DataForm& base)
{
   for( long rc = 0; base.ReadRec(rc); rc++ )
   {
      int index = PriceToIndex(base);
      if( index < 0 )
         continue;

      DiscountKey key;
      key.Load(base);

      Object *curObj;
      ObjMap::iterator fnd = objects.find(key);
      if( fnd == objects.end() )
      {
         curObj = dest->AddObject();

         curObj->at(iId).str->assign(key.id);
         curObj->at(iDog).str->assign(key.num);
         curObj->at(iItem).object = new ServObject(itemFormat);

         objects[key] = curObj;
      } else
      {
         curObj = fnd->second;
      }

      DiscountPriceKey pKey;
      pKey.Load(curObj, base);
      ObjPrcMap::iterator pfnd = objectsPrc.find(pKey);
      if( pfnd == objectsPrc.end() )
      {
         ServObject* prcObj = curObj->at(iItem).object;
         curObj = prcObj->AddObject();

         curObj->at(iDsc).number = pKey.discount;
         curObj->at(iQty).number = pKey.qty;
         curObj->at(iSum).number = pKey.sum;

         curObj->at(iPrcItem).object = new ServObject(itemPriceFormat);

         objectsPrc[pKey] = curObj;
      } else
      {
         curObj = pfnd->second;
      }

      ServObject* itemSObj = curObj->at(iPrcItem).object;
      Object *itemO = itemSObj->AddObject();
      itemO->at(iItemIdx).number = index;
   }
}

DiscountReader::~DiscountReader()
{
   delete data;
}

bool DiscountReader::MoveNext(GRServer::Object *parent)
{
   if( data == NULL || current == data->end() )
      return false;

   if( inited )
   {
      inited = false;
      return true;
   }
   current++;
   return (current != data->end());
}

bool DiscountReader::Get(GRServer::Object *object) const
{
   (*current)->Copy(object);
   return true;
}

bool DiscountReader::Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter)
{
   bool retval = false;

   DataForm base;
   if( base.Open(fileName.c_str()) )
   {
      thisObject = &object;

      data = new ServObject(object.format);

      const SessionObject* oItem = (const SessionObject*)object.GetChild(L"items")->Self();
      DiscountObjReader dr((Session&)object.GetSession(), data, oItem);
      dr.Read(base);

      retval = true;
      base.Close();

      current = data->begin();
      inited = true;
   }

   return retval;
}

bool GRServer::AddOnInit()
{
   DataSource::AddCreator(new DiscountSourceCreator());
   return true;
}


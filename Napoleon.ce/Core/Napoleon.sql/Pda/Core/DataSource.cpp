/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация источнико данных
 *
 *  ert   21/06/2008   creating
 */
#include "stdafx.h"
#include "DoPrint.h"
#include "NplConfig.h"

CollectionSource::CollectionSource(const IDataCollection &_data, ReflectableSource *parent) : data(_data), index(0)
{
   const DataReflector& reflector = data.DataType();

   item = reflector.Create();
   ritem = new ReflectableSource(item);

   data.Get(item, 0);

   this->parent = parent;
}

CollectionSource::~CollectionSource()
{
   delete item;
   delete ritem;
}

IDataSource *CollectionSource::GetObject(const wchar_t *name)
{
   return ritem->GetObject(name);
}

void CollectionSource::PrintData()
{
   if( parent != NULL )
      parent->NextCollectionItem(*item);
}

bool CollectionSource::GetValue(std::wstring *value, const wchar_t *name)
{
   return ritem->GetValue(value, name);
}

bool CollectionSource::MoveNext()
{
   if( index >= data.Count() - 1 ) return false;

   index++;
   return data.Get(item, index);
}

ReflectableSource::ReflectableSource(IReflectableData *_data) : data(_data)
{
}

ReflectableSource::~ReflectableSource()
{
   std::vector<CollectionSource*>::const_iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
   {
      delete (*i);
   }
}

IDataSource *ReflectableSource::GetObject(const wchar_t *name)
{
   const DataReflector &reflector = data->GetType();
   int index = reflector.Find(name);

   if( index < 0 ) return NULL;
   const MemberType &mt = reflector.Type(index);
   if( mt.type != MemberType::Collection ) return NULL;

   IDataCollection *list = (IDataCollection*)mt.GetValue(*data);
   CollectionSource *cs = new CollectionSource(*list, this);

   objects.push_back(cs);
   return cs;
}

bool ReflectableSource::GetValue(std::wstring *value, const wchar_t *name)
{
   const DataReflector &reflector = data->GetType();
   int index = reflector.Find(name);

   NapoleonConfig cfg;
   if( index < 0 )
   {
      return cfg.ReadValue(value, name);
   }

   const MemberType &mt = reflector.Type(index);
   if( mt.type == MemberType::Collection )
   {
      return cfg.ReadValue(value, name);
   }

   wchar_t buf[500];
   mt.ToString(*data, buf, sizeof(buf)/sizeof(buf[0]));

   *value = buf;
   return true;
}


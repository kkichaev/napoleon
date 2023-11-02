/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Filter
 *
 * ert   20/10/2010   creating
 */
#ifndef __DBF_FILTER_H
#define __DBF_FILTER_H

#include <sessobj.h>
#include "parse.h"
#include <dbf.h>

namespace GRServer {

class IFilterInSet
{
public:
   virtual ~IFilterInSet() {}

   virtual bool InSet(const DataForm& srcBase, const SessionObject& thisObject) = 0;

   virtual void SetUserFilter(const std::wstring& userFilter) = 0;

   virtual IFilterInSet* Clone() const = 0;
};

struct IObjectReader;
class IFilterObjHolder
{
public:
   virtual ~IFilterObjHolder() {}

   virtual void Load(const DataForm& base, const SessionObject& thisObject, IObjectReader* reader) = 0; // load My objects from DB

   virtual bool Next(const Object& parent) = 0;
   virtual bool Get(Object* dest) const = 0;
   virtual IFilterObjHolder* Clone() const = 0;
};

struct FilterReader
{
public:
   /*
      Чтобы избехать утечки памяти holder & filter удаляются (см DBFCreatorBase::CreateReader).
      Для работы, копируем указатель, а в этой структуре устанавлеваем его в NULL
   */
   struct Data
   {
      Data() : holder(NULL), filter(NULL) {}

      IFilterObjHolder *holder;
      IFilterInSet *filter;

      IFilterInSet* GetFilter() { IFilterInSet* f = filter; filter = NULL; return f; }
      IFilterObjHolder* GetHolder() { IFilterObjHolder* f = holder; holder = NULL; return f; }
   };

   static bool Parse(Data* data, StringStream& stream, const SessionObject& thisObject);
   static bool Parse(Data* data, const std::wstring& str, const SessionObject& thisObject)
   {
      StringStream ss(str);
      return Parse(data, ss, thisObject);
   }
};

} // namespace GRServer

#endif

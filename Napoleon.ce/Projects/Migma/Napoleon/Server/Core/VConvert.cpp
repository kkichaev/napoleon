/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Коневертор версий
 * 
 *  ert   15/09/2007   creating
 */ 
#include "stdafx.h"
#include <atldef.h>
#include "VConvert.h"
#include "Exchange.h"

/*
  v4    - v0105 Order add podRemark
  v0105 - v0106 add GPS_POS
*/

struct OrderV105 : public IReflectableData
{
   FILETIME created;
   FILETIME date;
   wchar_t*   id;
   DWORD    params;
   WORD     supplyer;
   WORD     sumType;
   wchar_t *remark;
   vector_t<OrderItem> items;

   short    discount;

   DECLARE_TYPE_REFLECTION(OrderV105);
};


BEGIN_TYPE_REFLECTION(OrderV105)
   REGISTER_FILETIME_MEMBER(OrderV105, created)
   REGISTER_FILETIME_MEMBER(OrderV105, date)
   REGISTER_STRING_MEMBER(OrderV105, id)
   REGISTER_ULONG_MEMBER(OrderV105, params)
   REGISTER_USHORT_MEMBER(OrderV105, supplyer)
   REGISTER_USHORT_MEMBER(OrderV105, sumType)
   REGISTER_STRING_MEMBER(OrderV105, remark)
   REGISTER_COLLECTION_MEMBER(OrderV105, items, OrderItem)
   REGISTER_SHORT_MEMBER(OrderV105, discount)
END_TYPE_REFLECTION(OrderV105)


struct OrderConvertV4 : public IConvertor
{
   enum { VERSION = 0x105 };

   OrderConvertV4()
   {
      versionManager.AddConvertor(L"Order", *this, VERSION);
   }
   ~OrderConvertV4() {}

   virtual IReflectableData *CreatePrevious() const { return new OrderV105(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      CopyData(current, prev);

      ((Order*)current)->latitude = 0;
      ((Order*)current)->longitude = 0;

      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      CopyData(prev, current);
      return true;
   }
};


OrderConvertV4 ocv4;

#pragma warning(disable : 4073)
#pragma init_seg(lib)
VersionManager versionManager;

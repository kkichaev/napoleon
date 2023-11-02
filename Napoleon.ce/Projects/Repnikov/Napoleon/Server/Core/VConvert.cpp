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
#include "exchange.h"
#include "Sync.h"

struct PriceV105 : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *photo;
   DWORD    folderID;

   vector_t<CostItem> cost;

   int      qty;         // QTY_SCALE
   DWORD    qtyInPack;   // QTY_SCALE
   WORD     flags;
   WORD     tax1;
   DWORD    weight;  // WEIGHT_SCALE
   DWORD    color;

   DECLARE_TYPE_REFLECTION(PriceV105);
};

BEGIN_TYPE_REFLECTION(PriceV105)
   REGISTER_STRING_MEMBER(PriceV105, id)
   REGISTER_STRING_MEMBER(PriceV105, name)
   REGISTER_STRING_MEMBER(PriceV105, photo)
   REGISTER_ULONG_MEMBER(PriceV105, folderID)   

   REGISTER_COLLECTION_MEMBER(PriceV105, cost, CostItem)

   REGISTER_LONG_SCALE_MEMBER(PriceV105, qty, QTY_SCALE)
   REGISTER_ULONG_MEMBER(PriceV105, qtyInPack)
   REGISTER_USHORT_MEMBER(PriceV105, flags)
   REGISTER_USHORT_MEMBER(PriceV105, tax1)
   REGISTER_ULONG_MEMBER(PriceV105, weight)
   REGISTER_ULONG_MEMBER(PriceV105, color)

END_TYPE_REFLECTION(PriceV105)


struct PriceConvertV105 : public IConvertor
{
   enum { VERSION = 0x0105 };

   PriceConvertV105()
   {
      versionManager.AddConvertor(PRICE_OBJ, *this, VERSION);
   }
   ~PriceConvertV105() {}

   virtual IReflectableData *CreatePrevious() const { return new PriceV105(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      ((Price*)current)->id = ((const PriceV105&)prev).id;
      ((Price*)current)->name = ((const PriceV105&)prev).name;
      ((Price*)current)->folderID = ((const PriceV105&)prev).folderID;

      ((Price*)current)->cost.clear();
      vector_t<CostItem>::const_iterator i = ((const PriceV105&)prev).cost.begin();
      while( i != ((const PriceV105&)prev).cost.end() )
      {
         ((Price*)current)->cost.push_back((*i));
         i++;
      }

      QtyItem q;
      q.qty = ((const PriceV105&)prev).qty;
      ((Price*)current)->qty.push_back(q);
      ((Price*)current)->qtyInPack = ((const PriceV105&)prev).qtyInPack;
      ((Price*)current)->flags = ((const PriceV105&)prev).flags;
      ((Price*)current)->tax1 = ((const PriceV105&)prev).tax1;
      ((Price*)current)->photo = ((const PriceV105&)prev).photo;

      ((Price*)current)->weight = ((const PriceV105&)prev).weight;
      ((Price*)current)->color = ((const PriceV105&)prev).color;

      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      ((PriceV105*)prev)->id = ((const Price&)current).id;
      ((PriceV105*)prev)->name = ((const Price&)current).name;
      ((PriceV105*)prev)->photo = ((const Price&)current).photo;
      ((PriceV105*)prev)->folderID = ((const Price&)current).folderID;

      ((PriceV105*)prev)->cost.clear();
      vector_t<CostItem>::const_iterator i = ((const Price&)current).cost.begin();
      while( i != ((const Price&)current).cost.end() )
      {
         ((PriceV105*)prev)->cost.push_back((*i));
         i++;
      }

      ((PriceV105*)prev)->qty = (((const Price&)current).qty.size() > 0) ? ((const Price&)current).qty[0].qty : 0;
      ((PriceV105*)prev)->qtyInPack = ((const Price&)current).qtyInPack;
      ((PriceV105*)prev)->flags = ((const Price&)current).flags;
      ((PriceV105*)prev)->tax1 = ((const Price&)current).tax1;
      ((PriceV105*)prev)->weight = ((const Price&)current).weight;
      ((PriceV105*)prev)->color = ((const Price&)current).color;

      return true;
   }
};

struct OrderV105 : public IReflectableData
{
   FILETIME created;
   FILETIME date;
   wchar_t  *id;
   DWORD    params;
   WORD     supplyer;
   WORD     sumType;
   wchar_t  *remark;
   vector_t<OrderItem> items;

#ifdef ORG_UNITS_STR
   wchar_t *unitCode;
#endif

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
#ifdef ORG_UNITS_STR
   REGISTER_STRING_MEMBER(OrderV105, unitCode)
#endif
END_TYPE_REFLECTION(OrderV105)

struct OrderConvertV105 : public IConvertor
{
   enum { VERSION = 0x0105 };

   OrderConvertV105()
   {
      versionManager.AddConvertor(L"Order", *this, VERSION);
   }
   ~OrderConvertV105() {}

   virtual IReflectableData *CreatePrevious() const { return new OrderV105(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      CopyData(current, prev);

      ((Order*)current)->warehouseCode = L"";
      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      CopyData(prev, current);
      return true;
   }
} ordv1;

struct OrgV105 : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   vector_t<OrgUnit> units;
#endif
   DECLARE_TYPE_REFLECTION(OrgV105);
};

struct OrgConvertV105 : public IConvertor
{
   enum { VERSION = 0x0105 };

   OrgConvertV105()
   {
      versionManager.AddConvertor(L"Org", *this, VERSION);
   }
   ~OrgConvertV105() {}

   virtual IReflectableData *CreatePrevious() const { return new OrgV105(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      CopyData(current, prev);
      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      CopyData(prev, current);
      return true;
   }
} orgv1;

BEGIN_TYPE_REFLECTION(OrgV105)
   REGISTER_STRING_MEMBER(OrgV105, id)
   REGISTER_STRING_MEMBER(OrgV105, name)
   REGISTER_COLLECTION_MEMBER(OrgV105, units, OrgUnit)
END_TYPE_REFLECTION(OrgV105)

PriceConvertV105 pcv1;

#pragma warning(disable : 4073)
#pragma init_seg(lib)
VersionManager versionManager;

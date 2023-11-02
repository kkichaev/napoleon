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

struct OrderP : public IReflectableData
{
   FILETIME created;
   FILETIME date;
   wchar_t  *id;
   DWORD    params;
   WORD     supplyer;
   WORD     sumType;
   wchar_t  *remark;
   vector_t<OrderItem> items;

#ifdef ORD_DLV_BIND
   wchar_t  *number;    // номер заказа из учетной системы
#endif

   DWORD    collectSum; // инкассация
   wchar_t *collectNum;

   wchar_t *logistic;
   wchar_t *fcontrol;

   DECLARE_TYPE_REFLECTION(OrderP);
};

BEGIN_TYPE_REFLECTION(OrderP)
   REGISTER_FILETIME_MEMBER(OrderP, created)
   REGISTER_FILETIME_MEMBER(OrderP, date)
   REGISTER_STRING_MEMBER(OrderP, id)
   REGISTER_ULONG_MEMBER(OrderP, params)
   REGISTER_USHORT_MEMBER(OrderP, supplyer)
   REGISTER_USHORT_MEMBER(OrderP, sumType)
   REGISTER_STRING_MEMBER(OrderP, remark)
   REGISTER_COLLECTION_MEMBER(OrderP, items, OrderItem)
#ifdef ORD_DLV_BIND
   REGISTER_STRING_MEMBER(OrderP, number)
#endif
   REGISTER_ULONG_MEMBER(Order, collectSum) // инкассация
   REGISTER_STRING_MEMBER(Order, collectNum)
   REGISTER_STRING_MEMBER(Order, logistic)
   REGISTER_STRING_MEMBER(Order, fcontrol)
END_TYPE_REFLECTION(OrderP)

const int VERSION = 0x1005;

struct OrderCnv : public IConvertor
{
   OrderCnv()
   {
      versionManager.AddConvertor(L"Order", *this, VERSION);
   }
   ~OrderCnv() {}

   virtual IReflectableData *CreatePrevious() const { return new OrderP(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      CopyData(current, prev);

      ((Order*)current)->podRemark = L"";

      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      CopyData(prev, current);
      return true;
   }
} ocv;

//struct FolderP : public IReflectableData
//{
//   wchar_t *name;
//   DWORD    id;
//   WORD     size;
//   WORD     level;
//   DWORD    sort;
//
//   DWORD    firstID; // первый элемент прайс-листа
//
//   DECLARE_TYPE_REFLECTION(FolderP);
//};
//
//BEGIN_TYPE_REFLECTION(FolderP)
//   REGISTER_STRING_MEMBER(FolderP, name)
//   REGISTER_ULONG_MEMBER(FolderP, id)
//   REGISTER_USHORT_MEMBER(FolderP, size)
//   REGISTER_USHORT_MEMBER(FolderP, level)
//   REGISTER_ULONG_MEMBER(FolderP, sort)
//   REGISTER_ULONG_MEMBER(FolderP, firstID)
//END_TYPE_REFLECTION(FolderP)
//
//struct FolderConvert : public IConvertor
//{
//   FolderConvert() { versionManager.AddConvertor(L"Folder", *this, VERSION); }
//   ~FolderConvert() {}
//
//   virtual IReflectableData *CreatePrevious() const { return new FolderP(); }
//
//   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
//   {
//      CopyData(current, prev);
//      return true;
//   }
//
//   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
//   {
//      CopyData(prev, current);
//      ((FolderP*)prev)->firstID = -1;
//      return true;
//   }
//} fcv;


#pragma warning(disable : 4073)
#pragma init_seg(lib)
VersionManager versionManager;
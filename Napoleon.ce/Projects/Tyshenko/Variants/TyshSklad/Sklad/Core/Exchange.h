/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Форматы для синхронизации (тест)
 *
 *  ert   01/12/2006   creating
 */ 
#ifndef __EXCHANGE_TYPE_H
#define __EXCHANGE_TYPE_H

#include <TypeHolder.h>
#include <StdConsts.h>

#define ADMPWD          L"ADMPWD"

//struct CostItem : public IReflectableData
//{
//   DWORD cost;
//
//   operator DWORD&() { return cost; }
//   operator const DWORD() const { return cost; }
//
//   CostItem() { cost = 0; }
//   CostItem(DWORD val) { cost = val; }
//   CostItem& operator = (DWORD val) { cost = val; return *this; }
//
//   DECLARE_TYPE_REFLECTION(CostItem)
//};
//

struct Price : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *packName;

	wchar_t *barcode;
	wchar_t *boxBarcode;
	DWORD qtyInPack;

   DECLARE_TYPE_REFLECTION(Price);
};

struct WHDocItem : public IReflectableData
{
   wchar_t*	id;
	WORD		isBaseUnit;
	DWORD		packCoef;
   DWORD		qty;       //QTY_SCALE поле с первоначальным количеством

   DECLARE_TYPE_REFLECTION(WHDocItem);
};

struct WHDocs : public IReflectableData
{
   wchar_t  *type;
   wchar_t  *id;
   wchar_t  *name;
   wchar_t  *number;
	FILETIME date;

   vector_t<WHDocItem> items;

   DECLARE_TYPE_REFLECTION(WHDocs);
};

struct ReqDocParam : public IReflectableData
{
	wchar_t *type;
	wchar_t *id;
	wchar_t *number;
	FILETIME date;

	WORD status;

   DECLARE_TYPE_REFLECTION(ReqDocParam);
};

struct ReqDocAnswer : public IReflectableData
{
	wchar_t *message;
	WORD status;

   DECLARE_TYPE_REFLECTION(ReqDocAnswer);
};

struct WhOutDocItem : public IReflectableData
{
   wchar_t *id;
   DWORD    qty; // QTY_SCALE
   DWORD    inputQty; // QTY_SCALE
	WORD		isBaseUnit;
	DWORD		packCoef;

   DECLARE_TYPE_REFLECTION(WhOutDocItem)
};

struct WhOutDoc : public IReflectableData
{
   wchar_t  *type;
   wchar_t  *id;
   wchar_t  *number;
   wchar_t  *name;
   FILETIME date;
	FILETIME created;

   WORD flags;

   vector_t<WhOutDocItem> items;

   DECLARE_TYPE_REFLECTION(WhOutDoc)
};

#endif

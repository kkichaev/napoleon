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
	wchar_t *barcode;

   DWORD inPack;

   DECLARE_TYPE_REFLECTION(Price);
};

struct SkMarks : public IReflectableData
{
	wchar_t *id;
	wchar_t *markBegin;
	wchar_t *markEnd;

	DECLARE_TYPE_REFLECTION(SkMarks);
};

struct DocTypes: public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   int controlDoc;
   int controlRack;
   int controlItem;
   int controlQty;
   int isRouteList;
	int isMovement;

   DECLARE_TYPE_REFLECTION(DocTypes);
};

struct WhAgents : public IReflectableData
{
   wchar_t* id;
   wchar_t *userid;

   WORD canMixInput;
   WORD canInputQty;
   WORD canInputInPack;
   WORD inputRack;

   DECLARE_TYPE_REFLECTION(WhAgents);
};

struct Config : public IReflectableData
{
   wchar_t *key;
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(Config);
};

struct OrderItem : public IReflectableData
{
   wchar_t *id;
   wchar_t *rack;
   wchar_t *rackDest;
	wchar_t *mark;
	wchar_t *barcode;
	wchar_t *palletBarcode;

	// При добавлении поля надо его инициальзировать во всех файлах

   DWORD   qty;       //QTY_SCALE поле с первоначальным количеством
   WORD    flags;

   DECLARE_TYPE_REFLECTION(OrderItem);
};

enum OrderFlags
{
   ofExported    = 0x00001,
   ofCash        = 0x00002,
   ofProceeded   = 0x00004,
   ofUndelivered = 0x00100,
};

//
//-------------------------------------------------------------------------------- Order
//
struct WhCellOrder : public IReflectableData
{
   wchar_t  *id;

   FILETIME created;

   DWORD    params;

   vector_t<OrderItem> items;

   DECLARE_TYPE_REFLECTION(WhCellOrder);
};

//
//-------------------------------------------------------------------------------- OrderProceeded
//
struct ControlDoc : public IReflectableData
{
   wchar_t  *id;
   vector_t<OrderItem> items;

   DECLARE_TYPE_REFLECTION(ControlDoc);
};

//
//-------------------------------------------------------------------------------- OrderProceeded
//
struct OrderProceeded : public IReflectableData
{
   FILETIME created;

   wchar_t *remark;
   wchar_t *type;

   DECLARE_TYPE_REFLECTION(OrderProceeded)
};

//
//-------------------------------------------------------------------------------- Pallets
//
struct PalletItem : public IReflectableData
{
	wchar_t* id;
	wchar_t* barcode;
	DWORD qty;

   DECLARE_TYPE_REFLECTION(PalletItem)
};

struct Pallets : public IReflectableData
{
	wchar_t* barcode;
	vector_t<PalletItem> items;

   DECLARE_TYPE_REFLECTION(Pallets)
};

#endif

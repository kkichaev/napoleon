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

struct Agents : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *login;
   wchar_t *password;

   DECLARE_TYPE_REFLECTION(Agents);
};

struct Server : public IReflectableData
{
   wchar_t *ip;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(Server);
};

struct Price : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DWORD cost;
   wchar_t *barcode;

   DECLARE_TYPE_REFLECTION(Price);
};

struct PriceItem : public IReflectableData
{
   wchar_t *barcode;
   DECLARE_TYPE_REFLECTION(PriceItem);
};

struct PriceRcv : public Price
{
   vector_t<PriceItem> items;
   DECLARE_TYPE_REFLECTION(PriceRcv);
};

struct DocItem : public IReflectableData
{
   wchar_t *id;
   DWORD   qty;       //QTY_SCALE поле с первоначальным количеством
   DWORD   order;

   DECLARE_TYPE_REFLECTION(DocItem);
};

enum OrderFlags
{
   ofExported    = 0x00001,
   ofCash        = 0x00002,
   ofProceeded   = 0x00004,
   ofUndelivered = 0x00100,
};

struct ControlDoc : public IReflectableData
{
   FILETIME created;
   DWORD params;
   vector_t<DocItem> items;

   DECLARE_TYPE_REFLECTION(ControlDoc);
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

   DECLARE_TYPE_REFLECTION(DocTypes);
};

struct WhAgents : public IReflectableData
{
   wchar_t* id;
   wchar_t *userid;

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

#endif

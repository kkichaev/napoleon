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

struct Agents : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *login;
   wchar_t *password;

   DECLARE_TYPE_REFLECTION(Agents);
};

struct Partner : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(Partner);
};

struct Boards : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(Boards);
};

struct Sklad : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   int      qty;         // QTY_SCALE
   DWORD    cost;        // SUM_SCALE

   wchar_t *unit;

   DECLARE_TYPE_REFLECTION(Sklad);
};

struct WHDocItem : public IReflectableData
{
   wchar_t *id;
   wchar_t *board;
   int      qty;         // QTY_SCALE

   DECLARE_TYPE_REFLECTION(WHDocItem);
};

enum DocFlags { ofExported = 1 };

struct WHDoc : public IReflectableData
{
   FILETIME created;
   wchar_t *remark;
   wchar_t *user;
   DWORD    params;

   vector_t<WHDocItem> items;

   DECLARE_TYPE_REFLECTION(WHDoc);
};


#endif

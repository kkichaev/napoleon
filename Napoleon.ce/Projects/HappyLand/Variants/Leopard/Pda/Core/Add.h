/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Дополнения Леопард
 *
 *  ert   21/12/2010   creating
 */
#ifndef __ADD_LPRD_H
#define __ADD_LPRD_H

#include "ObjImpl.h"

struct Sklad : public IReflectableData
{
   enum Flags { CheckPack = 1, };

   wchar_t *id;
   wchar_t *name;

   DWORD flags;

   DECLARE_TYPE_REFLECTION(Sklad)
};

class SkladImpl : public DBImpl<Sklad>
{
public:
   SkladImpl() : DBImpl(L"Sklads") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

DWORD CountQTY(const Price& price, const wchar_t* sklad, const wchar_t* pack = L"");
DWORD GetInPack(const Price& price, const wchar_t* sklad, const wchar_t* pack);

#endif

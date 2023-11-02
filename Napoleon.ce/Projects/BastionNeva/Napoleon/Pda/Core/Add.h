/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Восход дополнения
 *
 *  ert   02/08/2010   creating
 */
#ifndef __ADD_VOSHOD_H
#define __ADD_VOSHOD_H

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocImpl.h>
#include <PriceForm.h>

extern wchar_t dtPlans[];

// в планах даты "с" и "по" с временем 0 (ResetTime) дата "по" не включается в план
// начало следующего плана совпадает с концом предыдущего
struct Plan : public IReflectableData
{
   wchar_t* name;
   wchar_t* plan;
   wchar_t* fact;
   wchar_t* procent;

   DECLARE_TYPE_REFLECTION(Plan)
};

class PlanImpl : public DBImpl<Plan>
{
public:
   PlanImpl() : DBImpl(L"Plans") {}

   virtual const wchar_t*  KeyFields() const { return L"name"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   // ------------------------------- Instance functions -----------------------------------
protected:
};

struct Pays : public IReflectableData
{
   FILETIME date;
   wchar_t *id;
   wchar_t *number;

   DWORD    sum;
   DECLARE_TYPE_REFLECTION(Pays)
};

class PaysImpl : public DBImpl<Pays>, public IDocument
{
public:
   PaysImpl() : DBImpl(L"pays") {}

   virtual const wchar_t*  KeyFields() const { return L"id,number"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return number; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   virtual ICreatableDocument* Creatable() { return NULL; }
   virtual void EditDocument(UINT retForm) {}
};
extern wchar_t dtPays[];
void OpenPlans(const wchar_t *svDocType);

#endif
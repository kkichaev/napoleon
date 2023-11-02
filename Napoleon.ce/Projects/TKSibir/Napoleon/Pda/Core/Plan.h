/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Инкассация
 *
 *  ert   17/12/2010   creating
 */
#ifndef __PLAN_DOC_H
#define __PLAN_DOC_H

#include "DocImpl.h"

struct Plan : public IReflectableData
{
   FILETIME date; // дата создания
   DWORD  sum; // SUM_SCALE
   DWORD flags;

   DECLARE_TYPE_REFLECTION(Plan)
};

class PlanImpl : public DBImpl<Plan>, public IDocument, public ICreatableDocument
{
public:
   PlanImpl() : DBImpl(L"Plans"), svDocType(L"") {}

   virtual const wchar_t*  KeyFields() const { return L"date"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return L""; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return this; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   //virtual const char* CMD() const { return SND_Incass; }
   virtual const wchar_t* SendText(int count) const { return L"Передача планов"; }

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   void SetSVDocType(const wchar_t* dt) { svDocType = dt; }
protected:
   const wchar_t* svDocType;
};

void OpenPlans(const wchar_t *svDocType);

extern wchar_t dtPlans[];

#endif

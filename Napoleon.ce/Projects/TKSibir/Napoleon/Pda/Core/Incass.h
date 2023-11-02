/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Инкассация
 *
 *  ert   17/12/2010   creating
 */
#ifndef __INCASS_DOC_H
#define __INCASS_DOC_H

#include "DocImpl.h"

struct Incass : public IReflectableData
{
   wchar_t *id;

   FILETIME date;
   DWORD sum; // SUM_SCALE

   wchar_t *remark;
   DWORD flags;

   FILETIME payDate;

   DECLARE_TYPE_REFLECTION(Incass)
};

class IncassImpl : public DBImpl<Incass>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   IncassImpl() : DBImpl(L"Incass") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

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
   virtual const wchar_t* SendText(int count) const { return L"Передача доверенностей"; }

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   
};

extern wchar_t dtIncass[];

struct IncassType : public DocType
{
   IncassType();
};

#endif

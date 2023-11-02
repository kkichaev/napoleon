/*
 * Copyright (C), 2007-2012, Денис Мосягин
 *
 * Холодильники
 *
 *  ert   25/04/2012   creating
 *
 */
#ifndef __REFR_DOC_H
#define __REFR_DOC_H

#include <Exchange.h>
#include <DBImpl.h>
#include <DocImpl.h>

struct RfrDocItem : public IReflectableData
{
   wchar_t* id;
   wchar_t* name;
   wchar_t* text;
   DWORD flags;

   DECLARE_TYPE_REFLECTION(RfrDocItem)
};

struct RfrDoc : public IReflectableData
{
   wchar_t *id;

   FILETIME created;

   wchar_t *remark;
   DWORD flags;

   vector_t<RfrDocItem> items;

   DECLARE_TYPE_REFLECTION(RfrDoc)
};

class DocRfrgImpl : public DBImpl<RfrDoc>, public IDocument, public ICreatableDocument
{
public:
   DocRfrgImpl() : DBImpl(L"rfrg") {}

   virtual const wchar_t*  KeyFields() const { return L"id,created"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { NULL }; return index; }

   // ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return created; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual void EditDocument(UINT retForm);
   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }


   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   //virtual const char* CMD() const { return SND_PROXY; }
   virtual const wchar_t* SendText(int count) const { return L"Передача документов"; }

   virtual bool IsProceeded() const { return (flags & ofProceeded) != 0; }
   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return created; }
};



extern wchar_t dtRfrDoc[];


#endif
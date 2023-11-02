/*
* Copyright (C), 2007-2012, Денис Мосягин
*
* Остатки
*
*  ert   28/04/2012   creating
*/
#ifndef _REST_OUT_H
#define _REST_OUT_H

#include <DocImpl.h>

struct RestInItem : public IReflectableData
{
   wchar_t* id;
   int plan;

   DECLARE_TYPE_REFLECTION(RestInItem);
};

struct RestIn : public IReflectableData
{
   wchar_t* id;
   vector_t<RestInItem> items;

   DECLARE_TYPE_REFLECTION(RestIn);
};

class RestInImpl : public DBImpl<RestIn>
{
public:
   RestInImpl() : DBImpl(L"RestIn") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

struct RestOutItem : public IReflectableData
{
   wchar_t* id;
   int plan;
   int qty;
   int order;

   DECLARE_TYPE_REFLECTION(RestOutItem);
};

struct RestOut : public IReflectableData
{
   wchar_t* id;
   FILETIME created;
   FILETIME date;
   DWORD    flags;

   vector_t<RestOutItem> items;

   DECLARE_TYPE_REFLECTION(RestOut);
};

class RestOutImpl : public DBImpl<RestOut>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   RestOutImpl() : DBImpl(L"RestOut") {}

   virtual const wchar_t*  KeyFields() const { return L"created"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return created; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const { return true; }
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual bool IsProceeded() const { return false; }

   virtual const FILETIME& UID() const { return created; }

};

extern wchar_t dtRestOut[];

#endif
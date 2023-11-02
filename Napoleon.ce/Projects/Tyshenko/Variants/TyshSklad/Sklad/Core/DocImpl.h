/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * реализация работы заявки с БД
 *
 *  ert   17/06/2009   creating
 */ 
#ifndef __DOC_IMPL_H
#define __DOC_IMPL_H

#include "DBImpl.h"
#include <Document.h>
#include "Exchange.h"

#include <set>

#include <Apps.h>

extern wchar_t dtWHDoc[];

class WHDocsImpl : public DBImpl<WHDocs>, public IDocument 
{
public:
	WHDocsImpl() : DBImpl(L"documents") { }

   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }

	virtual const wchar_t*  KeyFields() const { return L"type,id,number"; }
	virtual const wchar_t** Indexes() const { return NULL; }

	virtual DWORD Sum() const { return 0; }
	virtual IReflectableData* Data() { return this; }
	virtual const IDBData* DBData() const { return this; }

	virtual void EditDocument(UINT retForm);

	virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }
	virtual const ROWID& RowID() const { return rid; }

	virtual ICreatableDocument* Creatable() { return NULL; }
};

enum WhDocFlags { WFSended = 1 };

struct WhOutDocImpl : public DBImpl<WhOutDoc>, public IDocument, public ICreatableDocument
{
   WhOutDocImpl() : DBImpl(L"whoutdoc") {}

   virtual const wchar_t*  KeyFields() const { return L"created"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
   virtual const wchar_t* ID() const { return id; } // id контрагента
   virtual const FILETIME& Date() const { return created; }
   virtual const wchar_t* Description() const;

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual void EditDocument(UINT retForm);

   // return null if is not creatable
   virtual ICreatableDocument* Creatable(){ return (ICreatableDocument*)this; }

	// see Create(const WHDocs& src)
	virtual bool CreateDocument(const ROWID &orgID) { return false; }
	virtual bool Init(const ROWID &orgID) { return false; }

	virtual IDocument* Copy() { return NULL; }

   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); }
   virtual bool WriteDocument() { return Write(); }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // for sending
   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }
   virtual bool IsDirty() const { return ((flags & WFSended) == 0); }
   virtual bool ClearDirty(SQLTable *table, bool reverse);

   virtual const FILETIME& UID() const { return created; }

   //------------------- Instance -----------------
	bool Create(const WHDocs& src);
	void InitInvent();

	static WhOutDocImpl* Find(const WHDocs& src);
};


#endif // __DOC_IMPL_H
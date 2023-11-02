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
extern Location gCurrentGPSPos;

extern wchar_t dtOrder[];

struct QTYData;
class ControlDocImpl : public DBImpl<ControlDoc>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   ControlDocImpl() : DBImpl(L"ctrDoc") { docType = dtOrder; params = 0; }

   virtual const wchar_t*  KeyFields() const { return L"created"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return L""; }
   virtual const FILETIME& Date() const { return created; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const;
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const { return ((params & ofExported) == 0); }
   //virtual bool IsExported() const { return ((params & ofExported) != 0); }
   //virtual bool IsProceeded() const { return ((params & ofProceeded) != 0); }

   // если table == NULL тогда надо просто изменить флаги у самого документа
   virtual bool ClearDirty(SQLTable *table, bool reverse);

   bool Send();

   std::vector<DocItem>::iterator FindItem(const wchar_t *id) const;
   void RemoveItems(const wchar_t* id);
   bool Update(const wchar_t* id, int qty);

   bool Remove();

   //void RemoveOrdersTill(const SYSTEMTIME &check);

   const wchar_t* DocType() const { return docType; }
   int TotalQty(const wchar_t* id) const;

   DWORD Qty() const;

protected:
   const wchar_t* docType;
};

#endif // __DOC_IMPL_H
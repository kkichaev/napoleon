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
class OrderImpl : public DBImpl<WhCellOrder>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   OrderImpl() : DBImpl(L"orders") { docType = dtOrder; params = 0; }

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
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

   std::vector<OrderItem>::iterator FindItem(const wchar_t *id) const;

   void UpdateOrder(std::vector<OrderItem>::iterator item, const QTYData &qd);

   bool Remove();

   //void RemoveOrdersTill(const SYSTEMTIME &check);

   const wchar_t* DocType() const { return docType; }
   const OrderItem* FindItem(const wchar_t* rack, const wchar_t* id) const;

   DWORD Qty() const;

protected:
   const wchar_t* docType;
};

#endif // __DOC_IMPL_H
/*
 * Copyright (C), 2006-2009, ƒенис ћос€гин
 *
 * »нтерфейсы документов
 * ƒокумент св€зан с организацией, содержит дату, и описание (номер и т.п.)
 * имеет уникальый ключ
 * может иметь сумму
 * пол€ id & date ќЅя«ј“≈Ћ№Ќќ есть в документе (нужно дл€ выборки документов)
 *
 *  ert   17/06/2009   creating
 */
#ifndef __I_DOCUMENT_H
#define __I_DOCUMENT_H

class SQLTable;
struct IDBData;
struct IReflectableData;
struct IDocument;

/*
 документ, который может быть создан в системе
*/
struct ICreatableDocument
{
   // for sending
   virtual const ROWID& Serialize(StreamWriter* writer) const = 0;

   //virtual const FILETIME& UID() const = 0;

   virtual bool IsDirty() const = 0;
   virtual bool WriteDocument() = 0;

   // если table == NULL тогда надо просто изменить флаги у самого документа
   virtual bool ClearDirty(SQLTable *table, bool reverse) = 0;
};

struct IDocument
{
   virtual const wchar_t* ID() const = 0; // id контрагента
   virtual const FILETIME& Date() const = 0;

   virtual DWORD Sum() const = 0;

   virtual IReflectableData* Data() = 0;

   virtual void EditDocument(UINT retForm) = 0;

   // methods for db read|write
   virtual const IDBData* DBData() const = 0;
   virtual bool ReadDocument(const ROWID& rid) = 0;

   virtual const ROWID& RowID() const = 0;

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() = 0;
};

/*
фабрика документов статическа€ - одна на один тип. ее мы не удал€ем 
*/
struct IDocFactory
{
   virtual IDocument* Create() const = 0;
   virtual void Free(IDocument* document) const = 0;
};

struct OrderProceeded;
struct IProceededHandler
{
   virtual ~IProceededHandler() {}

   virtual const wchar_t* Name() const = 0;
   virtual SQLTable* Prepare(const OrderProceeded& data) = 0;
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) = 0;
};

#endif // __DOCUMENTS_H
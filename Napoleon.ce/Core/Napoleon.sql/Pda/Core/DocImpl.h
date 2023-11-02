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
#include "DocType.h"
#include "Exchange.h"

#include <set>

#ifdef GPS_POS
#include <Apps.h>
extern Location gCurrentGPSPos;
#endif

struct QTYData;
class OrderImpl : public DBImpl<Order>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   OrderImpl() : DBImpl(L"orders") { instanceFlags = 0; docType = dtOrder; }

   virtual const wchar_t*  KeyFields() const { return L"created"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const;
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy();

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

#ifdef SHOW_OFF_TAKE
   virtual bool HideRemnants() const;
#endif
   virtual bool CheckQty() const { return true; }

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

#ifdef Autopteka_van
   //virtual const char* CMD() const { return SND_VAN_ORD; }
   //virtual const wchar_t* SendText(int count) const { return L"Передача продаж"; }
#else
   //virtual const char* CMD() const { return SND_ORDER_W; }
   //virtual const wchar_t* SendText(int count) const { return (count == 1) ? L"Передача заказа" : L"Передача заказов"; }
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   short WarehouseIndex();
#endif

   virtual bool IsDirty() const { return (params & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual bool IsProceeded() const { return ((params & ofProceeded) != 0); }

   virtual const FILETIME& UID() const { return created; }

   // ------------------------------- Instance functions -----------------------------------

   DWORD Weight() const;

   bool Send();

   void AssignRemark(const wchar_t *r) { remark = holder.Add(r); }

   void LoadItemSales(std::set<ItemSales, ItemSaleDateCompare> *sales, const wchar_t *itemID);

   virtual bool EditDetail();
   void AddFromPriceList();

   std::vector<OrderItem>::iterator FindItem(const wchar_t *id) const;

   void UpdateOrder(std::vector<OrderItem>::iterator item, const QTYData &qd);
   // changes - изменение кол-ва товара в заявке
   void ItemQtyChanged(const wchar_t *id, int changes);

   bool Remove();
   bool RemoveByKey(const wchar_t *key);

   void RemoveOrdersTill(const SYSTEMTIME &check);

   void OrderImpl::ChangeSumType(WORD newSumType);

   const wchar_t* DocType() const { return docType; }

#ifdef ORD_SURVAY
   const wchar_t *GetSurvay(DWORD folder) const;

   // folder == "" || folder == NULL - clear
   void SetSurvay(DWORD folder, const wchar_t* fid, const wchar_t *choice);
#endif

#ifdef ORD_ADD_TO_PACK
   void AddToFullPack();
#endif

#if defined(Autopteka) || defined(Autopteka_van)
   void ChangeCost(DWORD newType);
#endif

protected:
   OrderImpl(const wchar_t *tn, const wchar_t* docType) : DBImpl(tn) { this->docType = docType; }

   void AfterRemove();

   enum InstanceFlags { ifNoUpdatePrice = 1, };

   DWORD instanceFlags;
   const wchar_t* docType;
};

class DeliveryImpl : public DBImpl<Delivery>, public IDocument
{
public:
   //DeliveryImpl(const ROWID &orgID);

// ------------------------------- DBImpl -----------------------------------
   DeliveryImpl() : DBImpl(L"delivery") {}

#if defined(Voshod) || defined(Kolbiko)
   virtual const wchar_t*  KeyFields() const { return L"number,date"; }
#else
   virtual const wchar_t*  KeyFields() const { return L"number"; }
#endif
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }

// ------------------------------- IDocument functions -----------------------------------
   
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return number; }

   virtual IReflectableData* Data() { return this; }

   virtual void EditDocument(UINT retForm);

   virtual DWORD Sum() const;
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return NULL; }

// ------------------------------- Instance functions -----------------------------------
   void LoadItemSales(std::set<ItemSales, ItemSaleDateCompare> *sales, const wchar_t *itemID);
};

class PaymentImpl : public DBImpl<Payment>, public IDocument
#ifdef VAN_SELLING
   , public ICreatableDocument
#endif
{
public:
   //PaymentImpl(const ROWID &orgID);

// ------------------------------- DBImpl -----------------------------------

   PaymentImpl() : DBImpl(L"payments") {}

#ifdef Voshod
   virtual const wchar_t*  KeyFields() const { return L"id,number,dogId,fiscal"; }
#else
   virtual const wchar_t*  KeyFields() const { return L"id,number"; }
#endif
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }

// ------------------------------- IDocument functions -----------------------------------
   
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return number; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
#ifdef VAN_SELLING
   virtual IDocument* Copy();

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

   virtual const wchar_t* SendText(int count) const { return L"Передача оплат"; }

   virtual bool IsDirty() const { return (params & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
#else
   virtual ICreatableDocument* Creatable() { return NULL; }
   virtual void EditDocument(UINT retForm) {}
#endif
};


#ifdef PROXY_DOC
class ProxyImpl : public DBImpl<Proxy>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   ProxyImpl() : DBImpl(L"proxy") {}

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

   //virtual const char* CMD() const { return SND_PROXY; }
   virtual const wchar_t* SendText(int count) const { return L"Передача доверенностей"; }

   virtual bool IsDirty() const { return (flags & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   
protected:
   ProxyImpl(const wchar_t* table) : DBImpl(table) {}
};
#endif //PROXY_DOC

#endif // __DOC_IMPL_H
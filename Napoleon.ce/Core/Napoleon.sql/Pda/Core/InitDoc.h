/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Загрузчик типов докуменов
 *
 *  ert   22/03/2008   creating
 */
#ifndef _INIT_DOC_H
#define _INIT_DOC_H

#include <map>
#include "FormEntries.h"

struct OrderType : public DocType
{
   OrderType();
};

struct DeliveryType : public DocType
{
   DeliveryType();
};

class BalanceDoc : public IDocument
{
public:
   BalanceDoc() : isDelivery(true) {}

   bool isDelivery;

   virtual const wchar_t* ID() const {  return (isDelivery) ? d.ID() : p.ID(); }
   virtual const FILETIME& Date() const { return (isDelivery) ? d.Date() : p.Date(); }
   virtual const wchar_t* Description() const { return (isDelivery) ? d.Description() : p.Description(); }

#ifdef MAKE_BALANCE
   virtual DWORD Sum() const { return (isDelivery) ? d.Sum() : p.sum; }
#else
   virtual DWORD Sum() const { return (isDelivery) ? d.sumD : p.sum; }
#endif

   virtual const ROWID& RowID() const { return (isDelivery) ? d.rid : p.rid; }

   virtual IReflectableData* Data() { return (isDelivery) ? (IReflectableData*)&d : (IReflectableData*)&p; }

   virtual void EditDocument(UINT retForm)
   { 
      if(isDelivery)
      {
         DeliveryImpl *di = new DeliveryImpl(d);
         OpenDelivery(di, dtBalance);
      }
   }

   // methods for db read|write
   virtual const IDBData* DBData() const { return (isDelivery) ? d.DBData() : p.DBData(); }

   virtual bool ReadDocument(const ROWID& rid) { return (isDelivery) ? d.ReadDocument(rid) : p.ReadDocument(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return NULL; }

protected:
   DeliveryImpl d;
   PaymentImpl p;
};

struct BalanceType : public DocType
{
   BalanceType();

   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
      const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const;
};

#ifdef VISIT_DOC
struct VisitType : public DocType
{
   VisitType();
};
#endif

#ifdef PROXY_DOC
struct ProxyType : public DocType
{
   ProxyType();
};
#endif

#ifdef ORG_STOCK
struct OrgStock : public DocType
{
   OrgStock();
   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const;
   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, const wchar_t *whereStr, const wchar_t *orderStr) const;
};
#endif

#ifdef ORG_TASK
struct TaskDoc : public DocType
{
   TaskDoc();
   virtual bool ShowInDocumentList() const { return false; }
   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const {}

#ifdef Enoteka
   virtual const wchar_t* SendTypeName() const { return L"Task"; }
#else
   virtual const wchar_t* SendTypeName() const { return L"TaskSend"; }
#endif
};
#endif

#ifdef ORG_REMNANTS
struct RemnantsType : public DocType
{
   RemnantsType();
};
#endif

#ifdef SCRIPT_DOC
struct ScriptType : public DocType
{
   ScriptType();
   virtual bool ShowInDocumentList() const { return true; }
};
#endif

#ifdef VAN_SELLING
extern wchar_t dtPayment[];
#endif


#endif

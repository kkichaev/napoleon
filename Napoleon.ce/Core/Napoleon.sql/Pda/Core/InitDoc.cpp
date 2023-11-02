/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Загрузчик типов докуменов
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <DocType.h>
#include <Module.h>
#include <InitDoc.h>

#if defined(Autopteka_van) || defined(VAN_SELLING)
wchar_t dtOrder[] = L"Продажи";
#else
wchar_t dtOrder[] = L"Заявки";
#endif
wchar_t dtDelivery[] = L"Отгрузки";
wchar_t dtBalance[] = L"Долги";

struct OrderFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new OrderImpl(); }
   virtual void Free(IDocument* document) const { delete (OrderImpl*)document; }
} orderFactory;

class OrderPHandler : public IProceededHandler
{
public:
   OrderPHandler() {}

   virtual const wchar_t* Name() const { return L"Order"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

#ifdef VAN_SELLING
wchar_t dtPayment[] = L"Оплаты";

struct PaymentFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PaymentImpl(); }
   virtual void Free(IDocument* document) const { delete (PaymentImpl*)document; }
} payFactory;

struct PaymentType : public DocType
{
   PaymentType() : DocType(dtPayment, &payFactory, dtHaveSum)
   {
   }

   virtual bool ShowInDocumentList() const { return true; }
};
#endif

struct DeliveryFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new DeliveryImpl(); }
   virtual void Free(IDocument* document) const { delete (DeliveryImpl*)document; }
} deliveryFactory;

struct BalanceFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new BalanceDoc(); }
   virtual void Free(IDocument* document) const { delete (BalanceDoc*)document; }
} balanceFactory;

class BalanceDocList : public DocumentList
{
public:
   BalanceDocList(const std::vector<ROWID> &rids, const std::vector<bool> &t) : 
      DocumentList(balanceFactory, rids), types(t) {}

   virtual IDocument* Get(unsigned index)
   {
      if( index >= rids.size() )
         return NULL;

      if( document == NULL )
         document = factory.Create();

      ((BalanceDoc*)document)->isDelivery = types[index];
      return ( document->ReadDocument(rids[index])) ? document : NULL;
   }

protected:
   std::vector<bool> types;
};

OrderType::OrderType() : 
      DocType(dtOrder, &orderFactory, dtHaveSum)
{
}

DeliveryType::DeliveryType() : 
   DocType(dtDelivery, &deliveryFactory, dtHaveSum)
{
}

BalanceType::BalanceType() : DocType(dtBalance, &balanceFactory, dtHaveSum)
{
}

struct DocRid : public IReflectableData
{
   FILETIME date;
   __int64  rowid;
   
   DECLARE_TYPE_REFLECTION(DocRid)
};

BEGIN_TYPE_REFLECTION(DocRid)
   REGISTER_FILETIME_MEMBER(DocRid, date)
   REGISTER_INT64_MEMBER(DocRid, rowid)
END_TYPE_REFLECTION(DocRid)

struct BalanceData : public IReflectableData
{
   FILETIME date;
   __int64  rowid;
   wchar_t *number;

   bool isDelivery;
   bool operator< (const BalanceData &_item) const 
   {
      int cmp = CompareFileTime(&date, &_item.date);
      if( cmp < 0 ) return true;
      if( cmp > 0 ) return false;

      cmp = wcscmp(number, _item.number);
      return (cmp < 0) ? true : (cmp > 0) ? false : 
         ((int)!isDelivery) < ((int)!_item.isDelivery);
   }

   DECLARE_TYPE_REFLECTION(BalanceData)
};

BEGIN_TYPE_REFLECTION(BalanceData)
   REGISTER_FILETIME_MEMBER(BalanceData, date)
   REGISTER_STRING_MEMBER(BalanceData, number)
   REGISTER_INT64_MEMBER(BalanceData, rowid)
END_TYPE_REFLECTION(BalanceData)

bool BalanceType::GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, const wchar_t *whereStr, const wchar_t *orderStr ) const
{
   std::wstring sql;
   
   if( *orgid != L'\0' )
   {
      sql.assign(L" WHERE id='"); sql.append(orgid); sql.append(L"'");

      if( *whereStr != L'\0' )
      {
         sql.append(L" AND (");
         sql.append(whereStr);
         sql.append(L")");
       }

      //if( *orderStr != L'\0' )
      //{
         //sql.append(L" ORDER BY date");
         //sql.append(orderStr);
      //}
   }
   sql.append(L" ORDER BY date");

   BalanceData dr;
   StringHolder sh;
   std::set<BalanceData> bd;
   bool bdo;

#ifdef Provisia // в баланс выбираем только оплаты
#else
   SQLTable dt(DeliveryImpl().Name());
   bdo = dt.Select(&dr, sql.c_str());
   while( bdo )
   {
      dr.isDelivery = true;
      dr.number = sh.Add(dr.number);
      bd.insert(dr);
      bdo = dt.SelectNext(&dr);
   }
#endif

   SQLTable pt(PaymentImpl().Name());
   bdo = pt.Select(&dr, sql.c_str());
   while( bdo )
   {
      dr.isDelivery = false;
      dr.number = sh.Add(dr.number);
      bd.insert(dr);
      bdo = pt.SelectNext(&dr);
   }

   std::vector<ROWID> rids;
   std::vector<bool> types;
   std::set<BalanceData>::const_iterator i = bd.begin();
   for( ; i != bd.end(); i++ )
   {
      rids.push_back(i->rowid);
      types.push_back(i->isDelivery);
   }

   *orgDocs = new BalanceDocList(rids, types);
   return true;
}

void InitDocTypeSet()
{
   docTypeManager.Init();

#ifdef SCRIPT_DOC
   docTypeManager.insert(new ScriptType());
#endif

   docTypeManager.insert(new OrderType());
#ifdef VAN_SELLING
   docTypeManager.insert(new PaymentType());
#else
   docTypeManager.insert(new DeliveryType());
   docTypeManager.insert(new BalanceType());
#endif

#ifdef VISIT_DOC
   docTypeManager.insert(new VisitType());
#endif

#ifdef PROXY_DOC
   docTypeManager.insert(new ProxyType());
#endif

#ifdef ORG_STOCK
   docTypeManager.insert(new OrgStock());
#endif

#ifdef ORG_TASK
   docTypeManager.insert(new TaskDoc());
#endif

#ifdef ORG_REMNANTS
   docTypeManager.insert(new RemnantsType());
#endif

   AddProceededHandler(new OrderPHandler());

   InitCustomDocTypeSet();
}


typedef std::map<std::wstring, IProceededHandler*> PHList;
static PHList pHandlers;

void AddProceededHandler(IProceededHandler* handler)
{
   PHList::iterator fnd = pHandlers.find(handler->Name());
   if( fnd != pHandlers.end() )
   {
      delete fnd->second;
      pHandlers.erase(fnd);
   }
   pHandlers[handler->Name()] = handler;
}

IProceededHandler* GetProceededHandler(const wchar_t* name)
{
   PHList::iterator fnd = pHandlers.find(name);
   return ( fnd == pHandlers.end() ) ? NULL : fnd->second;
}

void DestroyDocTypeSet()
{
   PHList::iterator i = pHandlers.begin();
   for( ; i != pHandlers.end(); i++ )
      delete i->second;

   pHandlers.clear();
}

SQLTable* OrderPHandler::Prepare(const OrderProceeded& data)
{
   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;

   std::wstring tname(OrderImpl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET params = params | ";
   sql += buf;

#ifdef POD_COMMENT   
   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);
#endif
   sql += L" WHERE created = ?";

   MemberType *idT = (MemberType*)&type.Type(L"created");
   params.push_back(idT);

   if( !table->PrepareCommand(sql, params) )
   {
      delete table;
      table = NULL;
   }

   return table;
}

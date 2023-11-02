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
#include "InitDoc.h"
#include "DocImpl.h"

wchar_t dtOrder[] = L"Заказы";
wchar_t dtDKA1[] = L"ДКА1";
wchar_t dtDKA2[] = L"ДКА2";
wchar_t dtScanDoc[] = L"ШК";

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

OrderType::OrderType() : 
      DocType(dtOrder, &orderFactory, dtHaveSum)
{
}

struct DKA1Factory : public IDocFactory
{
   virtual IDocument* Create() const { return new DKA1Impl(); }
   virtual void Free(IDocument* document) const { delete (DKA1Impl*)document; }
} dka1Factory;

class DKA1PHandler : public IProceededHandler
{
public:
   DKA1PHandler() {}

   virtual const wchar_t* Name() const { return L"DKA1"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

DKA1Type::DKA1Type() : 
      DocType(dtDKA1, &dka1Factory, dtHaveSum)
{
}

struct DKA2Factory : public IDocFactory
{
   virtual IDocument* Create() const { return new DKA2Impl(); }
   virtual void Free(IDocument* document) const { delete (DKA2Impl*)document; }
} dka2Factory;

class DKA2PHandler : public IProceededHandler
{
public:
   DKA2PHandler() {}

   virtual const wchar_t* Name() const { return L"DKA2"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

DKA2Type::DKA2Type() : 
      DocType(dtDKA2, &dka2Factory, dtHaveSum)
{
}

struct ScanDocFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new ScanDocImpl(); }
   virtual void Free(IDocument* document) const { delete (ScanDocImpl*)document; }
} scanDocFactory;

class ScanDocPHandler : public IProceededHandler
{
public:
   ScanDocPHandler() {}

   virtual const wchar_t* Name() const { return L"ScanDoc"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

ScanDocType::ScanDocType() : 
      DocType(dtScanDoc, &scanDocFactory, 0)
{
}

void InitDocTypeSet()
{
   docTypeManager.Init();

   docTypeManager.insert(new OrderType());
   docTypeManager.insert(new DKA1Type());
   docTypeManager.insert(new DKA2Type());
   docTypeManager.insert(new ScanDocType());

   AddProceededHandler(new OrderPHandler());
   AddProceededHandler(new DKA1PHandler());
   AddProceededHandler(new DKA2PHandler());
   AddProceededHandler(new ScanDocPHandler());
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

   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

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

SQLTable* DKA1PHandler::Prepare(const OrderProceeded& data)
{
   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;

   std::wstring tname(DKA1Impl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET params = params | ";
   sql += buf;

   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

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

SQLTable* DKA2PHandler::Prepare(const OrderProceeded& data)
{
   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;

   std::wstring tname(DKA1Impl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET params = params | ";
   sql += buf;

   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

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

SQLTable* ScanDocPHandler::Prepare(const OrderProceeded& data)
{
   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;

   std::wstring tname(DKA1Impl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET params = params | ";
   sql += buf;

   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

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
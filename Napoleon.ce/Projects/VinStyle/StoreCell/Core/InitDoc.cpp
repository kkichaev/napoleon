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

void InitDocTypeSet()
{
   docTypeManager.Init();

   docTypeManager.insert(new OrderType());

   AddProceededHandler(new OrderPHandler());
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

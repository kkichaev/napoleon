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

wchar_t dtWHDoc[] = L"Документы";
wchar_t dtOutWHDoc[] = L"ИспДокументы";

struct OrderProceeded : public IReflectableData
{
   FILETIME created;

   wchar_t *type;

   DECLARE_TYPE_REFLECTION(OrderProceeded)
};

BEGIN_TYPE_REFLECTION(OrderProceeded)
   REGISTER_FILETIME_MEMBER(OrderProceeded, created)
   REGISTER_STRING_MEMBER(OrderProceeded, type)
END_TYPE_REFLECTION(OrderProceeded)

struct WHDocFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new WHDocsImpl(); }
   virtual void Free(IDocument* document) const { delete (WHDocsImpl*)document; }
} WHDocFactory;

struct OrderFactory : public IDocFactory
{
	virtual IDocument* Create() const { return new WhOutDocImpl(); }
   virtual void Free(IDocument* document) const { delete (WhOutDocImpl*)document; }
} orderFactory;

class OrderPHandler : public IProceededHandler
{
public:
   OrderPHandler() {}

   virtual const wchar_t* Name() const { return L"WhOutDoc"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

WHDocType::WHDocType() : 
      DocType(dtWHDoc, &WHDocFactory, 0)
{
}

WhOutDocType::WhOutDocType() :
      DocType(dtOutWHDoc, &orderFactory, 0)
{
}

void InitDocTypeSet()
{
   docTypeManager.Init();
   docTypeManager.insert(new WHDocType());
   docTypeManager.insert(new WhOutDocType());

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

	std::wstring tname(WHDocsImpl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(WFSended, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET flags = flags | ";
   sql += buf;

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

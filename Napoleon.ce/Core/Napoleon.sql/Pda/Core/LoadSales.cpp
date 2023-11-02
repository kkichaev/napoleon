/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Последние продажи
 *
 *  ert   31/03/2008   creating
 */
#include "stdafx.h"

#include <FormEntries.h>
#include "DocType.h"

#ifdef SHOW_OFF_TAKE

#include "OrgRmnts.h"
#include <StdFuncs.h>
#include "OffTake.h"

#include <NplConfig.h>

OffTakeHolder offTakeHolder;

DWORD CalcQty(const ItemSales& is, DWORD coef)
{
   DWORD val = (DWORD)(((__int64)is.offTake * coef) / SUM_SCALE) - is.rest;
   if( val % QTY_SCALE )
      val = (val / QTY_SCALE + 1) * QTY_SCALE;

   return ((int)val < 0) ? 0 : val;
}

void OffTakeHolder::UpdateLastRest(std::vector<ItemSales> *sales, const wchar_t* itemID, DWORD newRest, DWORD newRet)
{
   if( sales->size() )
   {
      int index = size() - 2;
      ItemSales& is = sales->front();
      is.rest = newRest;
#ifdef Kolbiko
      if( index >= 0 && sales->size() > 1 )
      {
         ItemSales ist = sales->at(1);
         DWORD prevRest = at(index).items[itemID].rest;
         DWORD qty = at(index + 1).items[itemID].qty;
         is.offTake = prevRest + qty - newRest;
         if( (int)is.offTake < 0 ) is.offTake = 0;
         ist.rest = newRest;
         is.qty = CalcQty(ist, GetOffTakeCoef(itemID));
      }
#else
      if( index >= 0 )
      {
         DWORD prevRest = at(index).items[itemID].rest;
         DWORD qty = at(index + 1).items[itemID].qty;
         is.offTake = prevRest + qty - newRest;
         if( (int)is.offTake < 0 ) is.offTake = 0;
         is.qty = CalcQty(is, GetOffTakeCoef(itemID));
      }
#endif
   } 
   //else
   //{
   //   ItemSales is;
   //   is.qty = 0;
   //   is.rest = newRest;
   //   is.offTake = 0;

   //   sales->push_back(is);
   //}

   //if( size() )
   //{
   //   back().items[itemID].rest = newRest;
   //}
}

void OffTakeHolder::Load(std::vector<ItemSales> *sales, bool fromOrders, const wchar_t* orgID, const wchar_t* itemID)
{
   LoadData(orgID, fromOrders);

   if( size() )
   {
      DWORD prevRest = 0;

      iterator i = begin();
      unsigned ctr = 1;
      for( ; i != end(); i++, ctr++ )
      {
         ItemSales is;
         is.date = i->date;

         DWORD qty = 0, rest = 0;
         OffTakeData::PriceSales::const_iterator pfnd = i->items.find(itemID);
         if( pfnd != i->items.end() )
         {
            qty = pfnd->second.qty;
            rest = pfnd->second.rest;
         }
         is.offTake = prevRest + qty - rest;
         if( (int)is.offTake < 0 ) is.offTake = 0;
         is.rest = rest;
         if( ctr < size() )
            is.qty = at(ctr).items[itemID].qty;
         else
         {
#ifdef Kolbiko
            if( sales->size() > 0 )
            {
               ItemSales ist = sales->front();
               ist.rest = is.rest;
               is.qty = CalcQty(ist, GetOffTakeCoef(itemID));
            } else
               is.qty = 0;
#else
            is.qty = CalcQty(is, GetOffTakeCoef(itemID));
#endif
         }

         prevRest = is.rest;
         sales->insert(sales->begin(), is);
      }
   }
}

OffTakeData* OffTakeHolder::Find(const FILETIME& date)
{
   iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( CompareFileTime(&date, &i->date) < 0 )
         return &(*i);
   }

   return NULL;
}

void OffTakeHolder::LoadData(const wchar_t* orgID, bool fromOrders)
{
   //if( curOrg.compare(orgID) == 0 )
   //   return;

   curOrg = orgID;
   clear();

   LoadRemnants();
   if( fromOrders ) LoadOrders();
   else LoadDelivery();
}

void OffTakeHolder::LoadRets()
{
}

void OffTakeHolder::LoadDelivery()
{
   const ::DocType* dt = docTypeManager.GetDocType(dtDelivery);
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(curOrg.c_str(), &orgDocs, L"", L"date") )
   {
      for( unsigned i=0; i<orgDocs->Count(); i++ )
      {
         IDocument *d = orgDocs->Get(i);
         if( d != NULL )
         {
            DeliveryImpl *doc = (DeliveryImpl*)d->Data();
            OffTakeData* data = Find(doc->date);

            if( data )
            {
               vector_t<DeliveryItem>::const_iterator item = doc->items.begin();
               for( ; item != doc->items.end(); item++ )
                  data->items[item->id].qty += item->qty;
            }
         }
      }
   }
   delete orgDocs;
}

void OffTakeHolder::LoadOrders()
{
   const ::DocType* dt = docTypeManager.GetDocType(dtOrder);
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(curOrg.c_str(), &orgDocs, L"", L"created") )
   {
      for( unsigned i=0; i<orgDocs->Count(); i++ )
      {
         IDocument *d = orgDocs->Get(i);
         if( d != NULL )
         {
            OrderImpl *doc = (OrderImpl*)d->Data();
            OffTakeData* data = Find(doc->created);

            if( data )
            {
               vector_t<OrderItem>::const_iterator item = doc->items.begin();
               for( ; item != doc->items.end(); item++ )
                  data->items[item->id].qty += item->qty;
            }
         }
      }
   }
   delete orgDocs;
}

void OffTakeHolder::LoadRemnants()
{
   const ::DocType* dt = docTypeManager.GetDocType(dtRemnants);
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(curOrg.c_str(), &orgDocs, L"", L"date") )
   {
      for( unsigned i=0; i<orgDocs->Count(); i++ )
      {
         IDocument *d = orgDocs->Get(i);
         if( d != NULL )
         {
            OrgRemnantsImpl *ri = (OrgRemnantsImpl*)d->Data();

            OffTakeData od;
            SYSTEMTIME st;

            FileTimeToSystemTime(&ri->date, &st);
            ResetTime(&st);
            SystemTimeToFileTime(&st, &od.date);

            vector_t<OrgRemnantsItem>::const_iterator oi = ri->items.begin();
            for( ; oi != ri->items.end(); oi++ )
            {
               DWORD qty = oi->qty;
               if( qty == 0 )
                  qty = 1;

               od.items[oi->id].rest += qty;
            }

            push_back(od);
         }
      }
   }
   delete orgDocs;
}

void LoadItemSales(std::vector<ItemSales> *sales, bool fromOrders, const wchar_t* orgID, const wchar_t* itemID, const ROWID& ignoredDocument)
{
   offTakeHolder.Load(sales, fromOrders, orgID, itemID);
}

#else

void LoadItemSales(std::vector<ItemSales> *sales, bool fromOrders, const wchar_t* orgID, const wchar_t* itemID, const ROWID& ignoredDocument)
{
   const DocType *doctype = docTypeManager.GetDocType( (fromOrders) ? dtOrder : dtDelivery );

   DocumentList *dl;
   if( !doctype->GetDocuments(orgID, &dl) ) return;

   std::set<ItemSales, ItemSaleDateCompare> items;
   int size = dl->Count();
   for( int i=0; i<size; i++ )
   {
      if( fromOrders )
      {
         OrderImpl* doc = (OrderImpl*)dl->Get(i);
         if( doc->RID() == ignoredDocument ) continue;
         doc->LoadItemSales(&items, itemID);
      } else
      {
         DeliveryImpl* doc = (DeliveryImpl*)dl->Get(i);
         if( doc->RID() == ignoredDocument ) continue;
         doc->LoadItemSales(&items, itemID);
      }
   }

   delete dl;
   /*
   std::vector<ROWID>::const_iterator i = od.documents.begin();
   for( ; i != od.documents.end(); i ++ )
   {
      if( (*i) == ignoredDocument ) continue;
      if( fromOrders )
      {
         if( !os.Read((*i)) ) continue;
         os.LoadItemSales(&items, itemID);
      }
   }
*/
   std::set<ItemSales, ItemSaleDateCompare>::const_iterator ti = items.begin();
   for( ; ti!= items.end(); ti++ )
      sales->push_back(*ti);
}

#endif //SHOW_OFF_TAKE

void LoadLastSales(std::set<std::wstring> *itms, const OrderImpl& order, bool fromOrders)
{
   // find last order
   DocumentList *dl;
   const DocType *doctype = docTypeManager.GetDocType((fromOrders) ? dtOrder : dtDelivery);
   if( !doctype->GetDocuments(order.id, &dl) )
      return;

   if( fromOrders )
   {
      int size = dl->Count();
      for( int i=size-1; i>=0; i-- )
      {
         OrderImpl* doc = (OrderImpl*)dl->Get(i);
         if( doc->RID() == order.RID() ) continue;

         std::vector<OrderItem>::const_iterator di = doc->items.begin();
         for( ; di != doc->items.end(); di++ )
            itms->insert(di->id);

         break;
      }
   } 
   else
   {
      int size = dl->Count();
      for( int i=size-1; i>=0; i-- )
      {
         DeliveryImpl* doc = (DeliveryImpl*)dl->Get(i);

         std::vector<DeliveryItem>::const_iterator di = doc->items.begin();
         for( ; di != doc->items.end(); di++ )
            itms->insert(di->id);

         break;
      }
   }

   delete dl;
}


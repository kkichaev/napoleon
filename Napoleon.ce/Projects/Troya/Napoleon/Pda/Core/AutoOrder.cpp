/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Автозаказ
 *
 *  ert   20/03/2008   creating
 */
#include "stdafx.h"
#include "AutoOrder.h"
#include <Table.h>
#include <DocType.h>
#include <Exchange.h>
#include <CEInt.h>
#include <Sync.h>

#include "OrgRmnts.h"

#include "FormEntries.h"
#include "Preference.h"

struct ItemQty
{
   CEOID id;
   DWORD qty; // QTY_SCALE
   WORD  count;
   FILETIME date;

   bool operator < (const ItemQty &ref) const { return id < ref.id; }
};

static bool LoadDocumentsItems(std::set<ItemQty> *items, CEOID orgID, bool onlyLast)
{
   OrgDocs od;
   const DocType *dt = docTypeManager.GetDocType(dtDelivery);
   if( dt == NULL || dt->GetDocuments(orgID, &od) == false ) return false;

   Delivery d;
   SyncDelivery sd;
   CEDBFormat format(sd);
   CETable table(format);
   if( table.Open(sd.FileName()) == false ) return false;

   SYSTEMTIME st;
   FILETIME ft;
   GetLocalTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &ft);

   // вычтем 15 дней
   // BC9 7C32 4000
   ft.dwHighDateTime -= 0xBC9;
   if( ft.dwLowDateTime < 0x7C324000l )
      ft.dwHighDateTime--;
   ft.dwLowDateTime -= 0x7C324000l;

   FileTimeToSystemTime(&ft, &st);

   std::vector<CEOID>::const_iterator docI = od.documents.begin();
   for( ; docI != od.documents.end(); docI++ )
   {
      table.Seek((*docI));
      table.GetCurrent(&d);

      if( CompareFileTime(&ft, &d.date) > 0 )
         continue;

      std::vector<DeliveryItem>::const_iterator i = d.items.begin();
      for( ; i != d.items.end(); i++ )
      {
         if( (int)i->qty <= 0 ) continue;

         ItemQty iq;
         iq.id = i->id;
         iq.qty = i->qty;
         iq.count = 1;
         iq.date = d.date;

         std::set<ItemQty>::iterator fnd = items->find(iq);
         if( fnd == items->end() ) items->insert(iq);
         else
         {
            if( onlyLast )
            {
               if( CompareFileTime(&fnd->date, &d.date) < 0 )
                  (*fnd) = iq;
            } else
            {
               fnd->qty += i->qty;
               fnd->count++;
            }
         }
      }
   }
   return true;
}

static OrderImpl *MakeOrder(CEOID orgID, const std::set<ItemQty> &items)
{
   Preference pref;
   Price p;
   SyncPrice sp;
   CEDBFormat format(sp);
   CETable table(format);

   SYSTEMTIME st;
   FILETIME ft;
   GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &ft);

   OrderImpl *o = new OrderImpl(orgID);
   o->date = ft;
   o->created = ft;
   o->remark = L"Автозаказ";

   table.Open(sp.FileName());
   pref.Load();

   std::set<ItemQty>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      table.Seek(i->id);
      table.GetCurrent(&p);

      DWORD qty = i->qty;
      int check = p.qty;
      if( pref.flags & opfNoBelowZero )
      {
         if( (int)qty > check )
         {
            if( check <= 0 ) continue;
            else qty = check;
         }
      }
      
      QTYData qd;
      qd.id = i->id;
      qd.qty = qty;
      qd.flags = 0;
      qd.cost = p.cost[0];
      qd.sum = ItemSum(qd.cost, qd.qty);

      o->UpdateOrder(o->items.end(), qd);
   }
   return o;
}

void MakeAutoOrder(CEOID orgID, bool makeFromRemnants)
{
   std::set<ItemQty> items;
   if( !LoadDocumentsItems(&items, orgID, makeFromRemnants) )
   {
      MessageBox(GetActiveWindow(), L"К сожалению, для работы автозаказа не хватает данных", 
                 L"Ошибка", MB_OK|MB_ICONSTOP);
      return;
   }


   if( makeFromRemnants )
   {
      OrgRemnantsImpl rmnts(orgID);
      rmnts.Read();

      vector_t<OrgRemnantsItem>::const_iterator i = rmnts.items.begin();
      for( ; i != rmnts.items.end(); i++ )
      {
         ItemQty iq;
         iq.id = i->id;

         std::set<ItemQty>::iterator fnd = items.find(iq);
         if( fnd == items.end() ) continue;

         DWORD qty = fnd->qty - i->qty;
         if( (int)qty <= 0 )
         {
            items.erase(fnd);
            continue;
         }
         fnd->qty = qty;
      }

      std::set<ItemQty>::iterator itemI = items.begin();
      for( ; itemI != items.end(); itemI++ )
      {
         DWORD halfQty = itemI->qty / 2;
         halfQty = (halfQty / QTY_SCALE) * QTY_SCALE;
         itemI->qty = itemI->qty + halfQty;
      }
   } else
   {
      std::set<ItemQty>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
      {
         if( i->count > 1 )
         {
            DWORD qty = i->qty / i->count;
            qty = (qty / QTY_SCALE) * QTY_SCALE;
            i->qty = qty;
         }
      }
   }

   OrderImpl *o = MakeOrder(orgID, items);
   if( o!= NULL )
      OpenInvoice(o, true);
}

/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
 *  ert   17/06/2008   modifying (SQL impl)
 */
#include "stdafx.h"

#include "DocImpl.h"
#include <StdFuncs.h>
#include "FormEntries.h"

DWORD DeliveryImpl::Sum() const
{
   DWORD sum = 0;
   vector_t<DeliveryItem>::const_iterator i = items.begin();
   for( ;i != items.end(); i++ )
      sum += i->sum;

   return sum;
}

void DeliveryImpl::EditDocument(UINT retForm)
{
   OpenDelivery(this, dtDelivery);
}

void DeliveryImpl::LoadItemSales(std::set<ItemSales, ItemSaleDateCompare> *sales, const wchar_t *itemID)
{
   std::vector<DeliveryItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, itemID) != 0 ) continue;

      ItemSales item;
      item.date = date;
      item.qty = i->qty;

      std::set<ItemSales, ItemSaleDateCompare>::iterator fnd = sales->find(item);
      if( fnd != sales->end() )
         fnd->qty += item.qty;
      else
         sales->insert(item);
   }
}
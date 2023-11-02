/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
 */
#include "stdafx.h"

#include <Exchange.h>
#include <CEInt.h>
#include <Table.h>
#include <Sync.h>

#include "FormEntries.h"


bool UpdatePrice(OrderImpl *order, CEOID oid, int qty)
{
   if( qty == 0 ) return true;

   SyncPrice sp;
   CEDBFormat priceFormat(sp);
   CETable price(priceFormat);
   if( price.Open(sp.FileName()) == false )
      return false;

   if( price.Seek(oid) == false ) return false;

   Price *data = (Price*)price.DataType().Create();
   
   price.GetCurrent(data);

   if( order->whIndex == 0 || order->whIndex > data->qtys.size() )
      data->qty += qty;
   else
      data->qtys[order->whIndex-1].qty += qty;

   price.WriteRecord(*data, oid, false);
   delete data;

   return true;
}


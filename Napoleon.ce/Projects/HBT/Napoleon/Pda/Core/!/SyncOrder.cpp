/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Форматы для синхронизации
 *
 *  ert   10/08/2007   creating
 */ 
#include "stdafx.h"
#include <exchange.h>
#include <ceint.h>
#include <sync.h>
#include <Table.h>

//
// --------------------- Sync Order ------------------------------
//
bool SyncOrder::Serialize(StreamWriter *writer, const IReflectableData &data) const
{   
   CEDBFormat orgFormat(ORG_OBJ, ORG_KEY), priceFormat(PRICE_OBJ, PRICE_KEY);
   CETable orgTable(orgFormat), priceTable(priceFormat);
   if(orgTable.Open(ORG_DB) == false || priceTable.Open(PRICE_DB) == false)
      return false;

   OrderSend os;
   StringHolder sh;
   const Order &src = (const Order &)data;
   if( orgTable.Seek(src.id) == true )
   {
      Org org;
      orgTable.GetCurrent(&org);      

      os.created = src.created;
      os.date = src.date;
      os.id = sh.Add(org.id);
      os.params = src.params;
      os.remark = src.remark;
      os.sumType = src.sumType;
      os.supplyer = src.supplyer;

      os.discount = src.discount;

#ifdef GPS_POS
      os.latitude = src.latitude;
      os.longitude = src.longitude;
#endif

#ifdef PAY_DELAY
      os.delay = src.delay;
#endif
      for( unsigned i=0; i<src.items.size(); i++ )
      {
         OrderItemSend osi;
         const OrderItem& item = src.items[i];
         if( priceTable.Seek(item.id) )
         {
            Price price;
            priceTable.GetCurrent(&price);

            osi.id = sh.Add(price.id);
            osi.cost = item.cost;
            osi.flags = item.flags;
            osi.qty = item.qty;
            os.items.push_back(osi);
         }
      }
      os.GetType().Serialize(writer, os);
   } else
      return false;
   return true;
}

bool SyncOrder::Deserialize(IReflectableData *data, const StreamReader &reader) const
{
   return false;
}


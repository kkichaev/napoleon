/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   01/09/2010   creating
 */
#include "stdafx.h"
#include "ObjImpl.h"
#include <DocImpl.h>

void OrderImpl::AfterRemove()
{
   if( !IsExported() && ((instanceFlags & ifNoUpdatePrice) == 0) )
   {
      PriceImpl p;
      std::vector<OrderItem>::const_iterator i = items.begin();
      for( ; i!= items.end(); i++ )
      {
         p.id = i->id;
         if( p.Read() )
         {
#ifdef MULTI_WH
            int index = WarehouseIndex();
            if( index < (short)p.qty.size() )
            {
               p.qty[index] += i->qty;
               p.Write();
            }
#elif FIRMS_REST
            int index = WarehouseIndex();
            if( index == 0 ) p.qty += i->qty;
            else if( index <= (short)p.firmQty.size() )
               p.firmQty[index-1] += i->qty;
            p.Write();
#elif WH_QTY
            int index = WarehouseIndex();
            if( index == 0 ) p.qty += i->qty;
            else if( index <= (short)p.whQty.size() )
               p.whQty[index-1] += i->qty;
            p.Write();
#else
            p.qty += i->qty;
            p.Write();
#endif
         }
      }
   }
}


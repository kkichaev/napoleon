/*
 * Copyright (C), 2007, Денис Мосягин
 *
 *  Процедура открытия прайс-листа
 *
 *  ert   06/02/2008   creating
 */
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "PriceForm.h"
#include "FormEntries.h"

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order)
   {
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder) : PriceFormData(_order, upFolder)
   {
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const
   {
      if( order == NULL )
         return (price.cost.size() > 1) ? price.cost[1] : price.cost[0];

      std::vector<OrderItem>::const_iterator fnd = order->FindItem(priceItem.id);
      return (fnd != order->items.end()) ? fnd->cost : 
         (price.cost.size() > ct) ? price.cost[ct] : price.cost[0];
   }

   virtual int ItemDiscount(const Price &price) const
   {
      return 0;
      //int discount = 0;
      //if( order )
      //{
      //   discount = ((int)(price.cost[order->sumType] * DISCOUNT_SCALE * 100 / price.cost[0] - DISCOUNT_SCALE * 100));
      //}
      //return discount;
   }
};

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}

void SelectPriceItem(IPriceSelect *selector, OrderImpl *o)
{
   PriceFormData *pfd = new PriceFormData(o, selector);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}
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
   std::map<int, int> discounts;

   PriceFormDataAdd(OrderImpl* _order, IPriceSelect* selector = NULL) : PriceFormData(_order, selector)
   {
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID& upFolder, IPriceSelect* selector = NULL) :  PriceFormData(_order, upFolder, selector)
   {
   }

   virtual PriceBaseData* Clone()
   {
      PriceBaseData *pfd = new PriceFormDataAdd(UnbindOrder(), UpFolder());
      return pfd;
   }

   virtual int ItemDiscount(const Price &price) const
   {
      if( order != NULL )
      {
         std::vector<OrderItem>::iterator fnd = order->FindItem(price.id);
         if( fnd != order->items.end() )
            return fnd->discount;
      }
      return 0;
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const
   {
      DWORD cost = PriceFormData::ItemCost(price, ct);
      std::map<int, int>::const_iterator fnd = discounts.find(price.folderID);
      if( fnd != discounts.end() )
      {
         int dsc = fnd->second;
			int sign = (dsc < 0) ? -1 : 1;
			cost += (int)(((__int64)cost * dsc  + sign * SUM_SCALE * SUM_SCALE / 2) / (SUM_SCALE * SUM_SCALE));

      }
      return cost;
   }

   virtual void Init()
   {
      PriceFormData::Init();

      if( order != NULL )
      {
         OrgImpl org;
         org.id = order->id;
         org.Read();
         vector_t<OrgDiscount>::const_iterator i = org.discounts.begin();
         for( ; i != org.discounts.end(); i++ )
            discounts[i->id] = i->discount;
      }
   }

};

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}

void SelectPriceItem(IPriceSelect *selector, OrderImpl *o)
{
   PriceFormData *pfd = new PriceFormDataAdd(o, selector);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}
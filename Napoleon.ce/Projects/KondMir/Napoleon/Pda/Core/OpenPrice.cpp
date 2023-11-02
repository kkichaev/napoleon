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
#include "Costs.h"

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect *selector = NULL) : PriceFormData(_order, selector)
   {
      discount = 0;
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect *selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
      discount = 0;
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual void Init() // вызываем в SetData для работы с виртуальными функциями
   {
      PriceFormData::Init();

      if( order != NULL )
      {
         OrgImpl oi;
         oi.id = order->id;
         oi.Read();
         discount = -oi.discount;
      }
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const;

   int discount;
};

DWORD PriceFormDataAdd::ItemCost(const Price &price, WORD ct) const
{ 
   DWORD cost = PriceFormData::ItemCost(price, ct);

   if( discount != 0 )
      cost = (((int)cost * 2 + 1) * SUM_SCALE * SUM_SCALE + ((int)cost * discount * 2)) / (2 * SUM_SCALE * SUM_SCALE);

   return cost;
}

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
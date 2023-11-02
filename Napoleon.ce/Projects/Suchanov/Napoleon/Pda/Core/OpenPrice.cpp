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

#include <Table.h>
#include <Costs.h>

#define PlusItem 2

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect *selector = NULL) : PriceFormData(_order, selector)
   {
      Load(_order);
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect *selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
      Load(_order);
   }

   void Load(OrderImpl* _order)
   {
      index1 = (DWORD)-1;
      index2 = (DWORD)-1;
      if( _order != NULL )
      {
         OrgImpl o;
         o.id = _order->id;
         o.Read();

         if( *o.type1 != L'\0' )
            index1 = CostManager::CostIndex(o.type1);
         if( *o.type2 != L'\0' )
            index2 = CostManager::CostIndex(o.type2);
      } 
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual COLORREF GetItemColor(int index) const
   {
      COLORREF clr = PriceFormData::GetItemColor(index);
      if( clr == textColor && (int)(index - folders.size()) >= 0 )
      {
         if( priceItem.flags & PlusItem )
            return RGB(0, 192, 192);
      }
      return clr;
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const
   {
      DWORD cost = 0;

      if( price.type == 1 && index1 != (DWORD)-1 )
         cost = CostManager::GetCost(price.id, index1);
      if( price.type == 2 && index2 != (DWORD)-1 )
         cost = CostManager::GetCost(price.id, index2);

      if( cost == 0 && price.cost.size() > 0 )
         cost = price.cost[0];

      return cost;
   }

   DWORD index1, index2;
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

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
#include "Add.h"

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect *selector = NULL) : PriceFormData(_order, selector)
   {
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect *selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const;

   virtual int ItemDiscount(const Price &price) const;

   int GetDiscount(int index, const OrderItem* item, DWORD *maxSum) const;

   bool CanDiscount(int index, const DiscountItem& discount, const OrderItem* item) const;

   virtual void LoadMatrix();
};

DiscountImpl dsc1, dsc2;

void PriceFormDataAdd::LoadMatrix()
{
   PriceFormData::LoadMatrix();

   //std::vector<SKUData> plan;
   //GetSKUPlans(&plan);

   //std::vector<SKUData>::const_iterator i = plan.begin();
   //for( ; i != plan.end(); i++ )
   //{
   //   Matrix m;
   //   m.name = L"<"; m.name += i->name; m.name += L">";
   //   m.items = i->rids;

   //   matrixes.push_back(m);
   //}
}

void RefreshDiscount(OrderImpl* order)
{
   OrgImpl o;
   o.id = order->id;
   o.Read();

   dsc1.id = o.ido;
   dsc1.dogovor = L"";
   dsc1.Read();

   if( *order->dogovor != L'\0' )
   {
      dsc2.id = o.ido;
      dsc2.dogovor = order->dogovor;
      dsc2.Read();
   }
}

bool PriceFormDataAdd::CanDiscount(int index, const DiscountItem& discount, const OrderItem* item) const
{
   vector_t<DiscountPriceItem>::const_iterator i = discount.items.begin();
   for( ; i != discount.items.end(); i++ )
      if( i->index == index )
         break;

   if( (i == discount.items.end()) || (item != NULL && discount.qty > item->qty) ) 
      return false;

   return true;
}

int PriceFormDataAdd::GetDiscount(int index, const OrderItem* item, DWORD *maxSum) const
{
   int dsc = 0;
   //return 0;

   if( order != NULL )
   {
      DiscountImpl *d[] = { &dsc1, &dsc2 };
      for( int idx = 0; idx < 2; idx ++ )
      {
         const DiscountImpl& dImpl = *d[idx];
         if( dImpl.rid == NO_ROWID ) continue;

         vector_t<DiscountItem>::const_iterator di = dImpl.items.begin();
         for( ; di != dImpl.items.end(); di++ )
         {
            if( !CanDiscount(index, (*di), item) ) continue;

            if( dsc == 0 )
            {
               dsc = -(di->discount);
               *maxSum = di->sum;
            }
            else
            {
               // для скидок i->discount > 0 выбираем максимальную, для наценок i->discount < 0 - выбираем наименьшую
               if( di->discount > -dsc )
               {
                  *maxSum = di->sum;
                  dsc = -(di->discount);
               }
            }
         }
      }
   }

   return dsc;
}

int PriceFormDataAdd::ItemDiscount(const Price &price) const
{
   if( order == NULL ) return 0;

   std::vector<OrderItem>::const_iterator fnd = order->FindItem(price.id);
   const OrderItem *oi = (fnd == order->items.end()) ? NULL : &(*fnd);

   //check discount;
   DWORD maxSum;
   return GetDiscount(PriceToIndex(price), oi, &maxSum) * DISCOUNT_SCALE / SUM_SCALE;
}

DWORD PriceFormDataAdd::ItemCost(const Price &price, WORD ct) const
{ 
   if( order == NULL && ct < price.cost.size() )
      return price.cost[ct];

   DWORD cost = PriceFormData::ItemCost(price, ct);

//   if( order != NULL )
//   {
//      std::vector<OrderItem>::const_iterator fnd = order->FindItem(price.id);
//      const OrderItem *oi = (fnd == order->items.end()) ? NULL : &(*fnd);
//
//      //check discount;
//      DWORD maxSum;
//      int discount = GetDiscount(PriceToIndex(price), oi, &maxSum);
//      if( discount != 0 )
//      {
//         int val = (cost * discount) / (SUM_SCALE * 100);
//         //if( maxSum > 0 )
//         //{
//         //   if( val > 0 )
//         //   {
//         //      if( val > (int)maxSum ) val = maxSum;
//         //   } else
//         //   {
//         //      if( -val > (int)maxSum ) val = -(int)maxSum;
//         //   }
//         //}
//         cost += val;
//      }
//   }
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
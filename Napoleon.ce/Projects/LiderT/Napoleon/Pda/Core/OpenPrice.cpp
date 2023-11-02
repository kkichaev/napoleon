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
      Load(_order);
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect *selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
      Load(_order);
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   void Load(OrderImpl* _order)
   {
      specCostIndex = (DWORD)-1;

      if( _order != NULL )
      {
         OrgImpl org;
         org.id = _order->id;
         org.Read();

         vector_t<Card>::const_iterator ci = org.cards.begin();
         for( ; ci != org.cards.end(); ci++ )
         {
            if( wcscmp(_order->card, ci->id) == 0 && *ci->costype != L'\0' )
            {
               specCostIndex = CostManager::CostIndex(ci->costype);
               break;
            }
         }

         DiscountImpl di;
         di.id = _order->card;
         if( di.Read() )
         {
            vector_t<DiscountItem>::const_iterator i = di.items.begin();
            for( ; i != di.items.end(); i++ )
            {
               folder.id = i->id;
               folder.Read();
               discounts[folder.rid] = -i->discount;
            }
         }
      }
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const;
   
   mutable FolderImpl folder;
   DWORD specCostIndex;
   std::map<ROWID, short> discounts;
};

DWORD PriceFormDataAdd::ItemCost(const Price &price, WORD ct) const
{
   DWORD stdCost = PriceFormData::ItemCost(price, -1); // get price cost
   if( order != NULL )
   {
      if( specCostIndex != (DWORD)-1 )
      {
         DWORD cost = CostManager::GetCost(price.id, specCostIndex);
         if( cost != 0 )
            return cost;
      }

      short dsc = 0;

      folder.id = price.folderID;
      folder.Read();
      TreeNode* fnode = root.Find(folder.rid);
      while( fnode != NULL )
      {
         std::map<ROWID, short>::const_iterator fnd = discounts.find(fnode->id);
         if( fnd != discounts.end() )
         {
            dsc = fnd->second;
            break;
         }
         fnode = fnode->parent;
      }
      if( dsc != 0 )
         stdCost += (((__int64)stdCost * dsc) / SUM_SCALE + DISCOUNT_SCALE / 2) / DISCOUNT_SCALE ;
   }

   return stdCost;
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
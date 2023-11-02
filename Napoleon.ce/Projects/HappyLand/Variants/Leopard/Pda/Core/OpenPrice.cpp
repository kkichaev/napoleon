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
#include <ObjImpl.h>
#include "Add.h"

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl *_order) : PriceFormData(_order) {}

   virtual DWORD PriceQty(const Price &price) const;
};

DWORD CountQTY(const Price& price, const wchar_t* sklad, const wchar_t* pack)
{
   DWORD qty = 0;

   SkladImpl s;
   s.id = (wchar_t*)sklad;
   s.Read();

   bool whInPacks = ((s.flags & Sklad::CheckPack) != 0);
   vector_t<PackItem>::const_iterator i = price.packs.begin();
   for( ; i != price.packs.end(); i++ )
   {
      if( wcscmp(i->warehouse, sklad) == 0 )
      {
         if( pack && *pack )
         {
            //if( wcscmp(i->pack, pack) == 0 )
            if( (whInPacks && wcscmp(i->pack, pack) == 0) || (!whInPacks && ((i->flags & PackItem::Main) != 0)) )
            {
               qty = i->qty;
               break;
            }
         } else
         {
            qty += i->qty;
         }
      }
   }

   return qty;
}

DWORD GetInPack(const Price& price, const wchar_t* sklad, const wchar_t* pack)
{
   DWORD qty = 0;

   vector_t<PackItem>::const_iterator i = price.packs.begin();
   for( ; i != price.packs.end(); i++ )
   {
      if( wcscmp(i->warehouse, sklad) == 0 && wcscmp(i->pack, pack) == 0 )
      {
         qty = i->inPack;
         break;
      }
   }

   return qty;
}

DWORD PriceFormDataAdd::PriceQty(const Price &price) const
{
   std::wstring whCode; 
   if( order == NULL )
   {
      SkladImpl s;
      SQLTable t(s.Name());
      if( t.Select(&s) )
         whCode = s.id;
   } else
      whCode = order->whCode;
   return CountQTY(price, whCode.c_str());
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}


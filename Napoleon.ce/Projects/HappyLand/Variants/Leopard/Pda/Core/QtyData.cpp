/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Методы относящиеся к QTYData
 *
 *  ert   01/12/2009   creating
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
#include "Invoice.h"

#include <PicWindow.h>
#include <StdFuncs.h>

#include "Add.h"

#ifdef SAVE_IN_PACK
WORD saveInPack=SAVE_IN_PACK;
#endif

QTYData::QTYData()
{
   id = L"";
   qty = 0;
   flags = 0;
   cost = 0;
   sum = 0;
   canChange = false;

#if defined(Autopteka) || defined(Autopteka_van)
   costType = 0;
#endif

#ifdef Gudkova
   costType = 0;
#endif

#ifdef Alians
   remnants = 0;
   showRemnants = false;
   enterPacket = false;
#endif

#ifdef PRICE_MOVER
   mover = NULL;
   sumLabel = NULL;
#endif

#ifdef MULTI_WH
   whIndex = 0;
#endif

#ifdef ORD_ITEM_DISCOUNT
   discount = 0;
   itemCost = 0;
#endif
}

#ifdef PRICE_MOVER
QTYData::~QTYData()
{
   delete mover;
}
#endif

void PriceFormData::SetQTYDataFromOrder(QTYData *qd, const Price& p, std::vector<OrderItem>::iterator item)
{
#ifdef ORD_ITEM_DISCOUNT
   qd->itemCost = ItemCost(p, order->sumType);
#endif

   if( item == order->items.end() )
   {
#ifdef SAVE_IN_PACK
      qd->flags = saveInPack;
#endif
      qd->cost = ItemCost(p, order->sumType);
#ifdef ORDER_DISCOUNT
      qd->cost += ((int)qd->cost * order->discount / DISCOUNT_SCALE) / SUM_SCALE;
#endif
   } else
   {
      qd->qty =  item->qty;
      qd->flags = item->flags;
      qd->sum = ItemSum(item->cost, item->qty);
      qd->cost = item->cost;
#ifdef ORD_ITEM_DISCOUNT
      qd->discount = item->discount;
#endif
      qd->pack = item->pack;
   }

#if defined(Autopteka) || defined(Autopteka_van)
   qd->costType = order->sumType;
   qd->orgID = order->id;
#endif

#ifdef Gudkova
   qd->costType = order->sumType;
#endif

#ifdef MULTI_WH
   qd->whIndex = order->WarehouseIndex();
#endif

#ifdef SHOW_OFF_TAKE
   qd->remnants = remnants.GetItemQty(qd->id.c_str());
#endif

   qd->whCode = order->whCode;
   LoadItemSales(&qd->sales, SALES_FROM_ORDERS, order->id, qd->id.c_str(), 0);
}

std::vector<OrderItem>::iterator PriceFormData::InitQTYData(QTYData* qd, const Price& p)
{
   qd->canChange = true;
   qd->id = p.id;
   qd->cost = ItemCost(p, 0);

#ifdef ORD_ITEM_DISCOUNT
   qd->itemCost = qd->cost;
#endif

#ifdef PRICE_MOVER
   qd->mover = new PMover(order, this, index);
   qd->sumLabel = sumLabel;
#endif

#ifdef MULTI_WH
   qd->whIndex = currentWh;
#endif

   std::vector<OrderItem>::iterator fnd;
   if( order != NULL )
   {
      fnd = order->FindItem(p.id);
      SetQTYDataFromOrder(qd, p, fnd);
   }
   else
   {
#ifdef SHOW_OFF_TAKE
      qd->flags |= oiHideRemnants;
#endif
      SkladImpl s;
      SQLTable t(s.Name());
      if( t.Select(&s) )
         qd->whCode = s.id;
   }

   return fnd;
}

bool PriceFormData::SelectLeaf(int index)
{
   if( index >= (int)leafs.size() )
      return false;

   priceItem.Read(leafs[index]);
   if( selector != NULL )
   {
      selector->Select(priceItem.id);
      return false;
   }

   QTYData qd;
   std::vector<OrderItem>::iterator fnd;
   fnd = InitQTYData(&qd, priceItem);

   bool retVal = SetQTY(&qd);

#ifdef SAVE_IN_PACK
   saveInPack = qd.flags;
#endif

   if( retVal )
   {
      priceItem.ClearCache();
      if( order != NULL )
      {
#ifdef PRICE_MOVER
         fnd = order->FindItem(qd.id.c_str());
#endif
         UpdateOrder(qd, fnd);

#ifdef SHOW_OFF_TAKE
         remnants.Update(qd.id.c_str(), qd.remnants, false);
#endif
      }
   }

   return retVal;
}

static bool UpdatePrice(OrderImpl *order, const wchar_t *id, const wchar_t* pack, int qty)
{
   if( qty == 0 ) return true;

   PriceImpl p;
   p.id = (wchar_t*)id;
   if( !p.Read() ) return false;

   SkladImpl s;
   s.id = order->whCode;
   s.Read();

   bool whInPacks = ((s.flags & Sklad::CheckPack) != 0);
   vector_t<PackItem>::iterator i = p.packs.begin();
   for( ; i != p.packs.end(); i++ )
      if( wcscmp(i->warehouse, order->whCode) == 0 )
      {
         if( (whInPacks && wcscmp(i->pack, pack) == 0) || (!whInPacks && ((i->flags & PackItem::Main) != 0)) )
         {
            i->qty += qty;
            break;
         }
      }
   return p.Update(L"packs");
}

void OrderImpl::UpdateOrder(std::vector<OrderItem>::iterator item, const QTYData &qd)
{
   int priceUpdate = 0;

#ifdef SHOW_OFF_TAKE
   int qtyDiff = 0;
   std::vector<ItemSales>::const_iterator qi = qd.sales.begin();
   if( qi != qd.sales.end() )
   {
      qtyDiff = (int)qd.qty - (int)qi->qty;
   }
#endif

   WORD flags = qd.flags;
#ifdef ORDER_ONLINE
   flags |= oiDirtyItem;
#endif
   if( item == items.end() )
   { // insert
      if( qd.qty == 0 ) return;

      OrderItem oi;
      oi.id = holder.Add(qd.id.c_str());
      oi.cost = qd.cost;
      oi.flags = flags;
      oi.qty = qd.qty;
#ifdef ORD_ITEM_DISCOUNT
      oi.discount = qd.discount;
#endif

#ifdef SHOW_OFF_TAKE
      oi.offTakeDiff = qtyDiff;
#endif
      oi.pack = holder.Add(qd.pack.c_str());

      items.push_back(oi);
      
      priceUpdate = -(int)qd.qty;
   } else
   {
#ifdef ORD_ITEM_DISCOUNT
      item->discount = qd.discount;
#endif
      priceUpdate = item->qty;
      item->flags = flags;
      if( qd.qty != 0 )
      { 
         // change
         item->qty = qd.qty;
         item->cost = qd.cost;
#ifdef SHOW_OFF_TAKE
         item->offTakeDiff = qtyDiff;
#endif
         priceUpdate -= (int)qd.qty;

         item->pack = holder.Add(qd.pack.c_str());
      } else
      { 
         items.erase(item);
      }
   }

   bool res;
   if( items.size() ) res = Write();
   else res = Remove();

   if( res )
   {
      ItemQtyChanged(qd.id.c_str(), -priceUpdate);

      if( (instanceFlags & ifNoUpdatePrice) == 0 )
         UpdatePrice(this, qd.id.c_str(), qd.pack.c_str(), priceUpdate);
      docTypeManager.SumChanged(dtOrder, id);
   }
}

bool InvoiceData::Selecting(int index)
{
   QTYData qd;

   std::vector<OrderItem>::iterator i = order->items.begin() + index;
   qd.id = i->id;
   qd.qty = i->qty;
   qd.sum = ItemSum(i->cost, i->qty);
   qd.flags = i->flags;
   qd.canChange = !order->IsExported();

#ifdef Autopteka
   qd.costType = order->sumType;
   qd.orgID = order->id;
   qd.cost = i->cost;
#elif Autopteka_van
   qd.costType = order->sumType;
   qd.orgID = order->id;
   qd.cost = i->cost;
#elif Gudkova
   qd.costType = order->sumType;
   qd.cost = i->cost;
#else
   qd.cost = i->cost;
#endif
   
#ifdef ORD_ITEM_DISCOUNT
   PriceImpl pi;
   pi.id = i->id;
   pi.Read();
   int sumType = ((int)order->sumType < 0) ? 0 : order->sumType;
   qd.itemCost = (pi.cost.size() < sumType ) ? pi.cost[sumType] : pi.cost.back();
   qd.discount = i->discount;
#endif

#ifdef PRICE_MOVER
   qd.mover = new IMover(order);
   qd.sumLabel = sumLabel;
#endif

#ifdef MULTI_WH
   qd.whIndex = order->WarehouseIndex();
#endif

#ifdef SHOW_OFF_TAKE
   qd.remnants = remnants.GetItemQty(i->id);
#endif

   qd.pack = i->pack;
   qd.whCode = order->whCode;

   LoadItemSales(&qd.sales, SALES_FROM_ORDERS, order->id, i->id, 0);

#ifdef PRICE_MOVER
#else
   ((DocumentForm*)owner)->LoadMenuBar(false);
#endif

   BeforeSetQty(&qd);
   bool retVal = SetQTY(&qd);

   ((DocumentForm*)owner)->LoadMenuBar(true);

   if( retVal )
   {
      AfterSetQty(qd);
#ifdef PRICE_MOVER
      i = order->FindItem(qd.id.c_str());
#endif
      order->UpdateOrder(i, qd);

#ifdef SHOW_OFF_TAKE
      remnants.Update(qd.id.c_str(), qd.remnants, false);
#endif
   }
   return retVal;
}
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

#include <StdFuncs.h>

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
   orderCreated.dwHighDateTime = 0;
#endif

#ifdef PRICE_MOVER
   mover = NULL;
   sumLabel = NULL;
#endif

#ifdef MULTI_WH
   whIndex = 0;
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
   }

#if defined(Autopteka) || defined(Autopteka_van)
   qd->costType = order->sumType;
   qd->orgID = order->id;
   qd->orderCreated = order->created;
#endif

#ifdef MULTI_WH
   qd->whIndex = order->WarehouseIndex();
#endif

   LoadItemSales(&qd->sales, SALES_FROM_ORDERS, order->id, p.id, 0);
}

std::vector<OrderItem>::iterator PriceFormData::InitQTYData(QTYData* qd, const Price& p, int index)
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
#ifdef SHOW_OFF_TAKE
   else
      qd->flags |= oiHideRemnants;
#endif

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
   fnd = InitQTYData(&qd, priceItem, index);

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

static bool UpdatePrice(OrderImpl *order, const wchar_t *id, int qty)
{
   if( qty == 0 ) return true;

   PriceImpl p;
   p.id = (wchar_t*)id;
   if( !p.Read() ) return false;

#ifdef MULTI_WH
   short index = order->WarehouseIndex();
   if( index < (short)p.qty.size() )
      p.qty[index] += qty;
#else
   p.qty += qty;
#endif
   return p.Update(L"qty");
}

void OrderImpl::UpdateOrder(std::vector<OrderItem>::iterator item, const QTYData &qd)
{
   int priceUpdate = 0;

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
      oi.flags = qd.flags;
      oi.qty = qd.qty;
#ifdef ORD_ITEM_DISCOUNT
      oi.discount = qd.discount;
#endif

      items.push_back(oi);
      
      priceUpdate = -(int)qd.qty;
   } else
   {
#ifdef ORD_ITEM_DISCOUNT
      item->discount = qd.discount;
#endif
      priceUpdate = item->qty;
      item->flags = qd.flags;
      if( qd.qty != 0 )
      { 
         // change
         item->qty = qd.qty;
         item->cost = qd.cost;
         priceUpdate -= (int)qd.qty;
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
         UpdatePrice(this, qd.id.c_str(), priceUpdate);
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

#if defined(Autopteka) || defined(Autopteka_van)
   qd.costType = order->sumType;
   qd.orgID = order->id;
   qd.cost = i->cost;
   qd.orderCreated = order->created;
#endif
   
#ifdef ORD_ITEM_DISCOUNT
   PriceImpl pi;
   pi.id = i->id;
   pi.Read();
   qd.itemCost = (pi.cost.size() < order->sumType ) ? pi.cost[order->sumType] : pi.cost.back();
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
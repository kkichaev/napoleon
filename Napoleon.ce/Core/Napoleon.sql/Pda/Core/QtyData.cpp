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

#ifdef QTY_DATA_COST_TYPE
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

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   whIndex = 0;
#endif

#ifdef ORD_ITEM_DISCOUNT
   discount = 0;
   itemCost = 0;
#endif

#ifdef Kolbiko
   retQty = 0;
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
#ifdef ORD_ITEM_DISCOUNT
      if( qd->discount != 0 )
         qd->cost += ((int)qd->cost * qd->discount / DISCOUNT_SCALE) / SUM_SCALE;
#endif
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
   }

#ifdef QTY_DATA_COST_TYPE
   qd->costType = order->sumType;
#endif

#if defined(Autopteka) || defined(Autopteka_van)
   qd->orgID = order->id;
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   qd->whIndex = order->WarehouseIndex();
#endif

#ifdef SHOW_OFF_TAKE
   if( order->HideRemnants() ) 
      qd->flags |= oiHideRemnants;

   qd->remnants = remnants.GetItemQty(qd->id.c_str());
#endif

   if( order->CheckQty() == false )
      qd->flags |= oiNoCheckWHQty;

   LoadItemSales(&qd->sales, SALES_FROM_ORDERS, order->id, qd->id.c_str(), 0);
}

std::vector<OrderItem>::iterator PriceFormData::InitQTYData(QTYData* qd, const Price& p, int index)
{
   qd->canChange = true;
   qd->id = p.id;
   qd->cost = ItemCost(p, 0);

#ifdef ORD_ITEM_DISCOUNT
   qd->itemCost = qd->cost;
   qd->discount = ItemDiscount(p);
   if( qd->discount != 0 )
      qd->cost += ((int)qd->cost * qd->discount / DISCOUNT_SCALE) / SUM_SCALE;
#endif

#ifdef PRICE_MOVER
   qd->mover = new PMover(order, this, index);
   qd->sumLabel = sumLabel;
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
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
   if( selector != NULL && selector->CanSelect() )
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
#elif FIRMS_REST
   short index = order->WarehouseIndex();
   if( index <= 0 ) p.qty += qty;
   else
   {
      if( index <= (short)p.firmQty.size() )
         p.firmQty[index-1] += qty;
   }
#elif WH_QTY
   short index = order->WarehouseIndex();
   if( index <= 0 ) p.qty += qty;
   else
   {
      if( index <= (short)p.whQty.size() )
         p.whQty[index-1] += qty;
   }
#else
   p.qty += qty;
#endif
   return p.Update(L"qty");
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
#ifdef SHOW_OFF_TAKE
      oi.offTakeDiff = qtyDiff;
#endif
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
      docTypeManager.SumChanged(docType, id);
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

#ifdef QTY_DATA_COST_TYPE
   qd.costType = order->sumType;
#endif

#ifdef Autopteka
   qd.orgID = order->id;
#elif Autopteka_van
   qd.orgID = order->id;
#endif
   qd.cost = i->cost;
   
#ifdef ORD_ITEM_DISCOUNT
   PriceImpl pi;
   pi.id = i->id;
   pi.Read();
   int sumType = ((short)order->sumType < 0) ? 0 : order->sumType;
   qd.itemCost = ((int)pi.cost.size() > sumType ) ? pi.cost[sumType] : pi.cost.back();
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
   if( order->HideRemnants() ) 
      qd.flags |= oiHideRemnants;

   qd.remnants = remnants.GetItemQty(i->id);
#endif

   if( order->CheckQty() == false )
      qd.flags |= oiNoCheckWHQty;

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
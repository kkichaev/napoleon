/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
 *  ert   17/06/2008   modifying (SQL impl)
 */
#include "stdafx.h"

#include "ObjImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <NapoleonRes.h>

DWORD OrderImpl::Sum() const
{
   DWORD sum = 0;
   vector_t<OrderItem>::const_iterator i = items.begin();
   for( ;i != items.end(); i++ )
      sum += ItemSum(i->cost, i->qty);

   return sum;
}

#ifdef VAN_SELLING
const wchar_t* OrderImpl::Description() const
{
   return docNum;
}
#else
const wchar_t* OrderImpl::Description() const
{
#ifdef ORD_DLV_BIND
   if( *number != L'\0' ) return number;
#endif
#ifdef POD_COMMENT
      if( params & ofProceeded )
         return (*podRemark == L'\0') ?  L"в обработке" : podRemark;

      return (params & ofExported) ? L"отправлен" : L"";
#else
   return (params & ofProceeded) ? L"в обработке" : (params & ofExported) ? L"отправлен" : L"";
#endif
}
#endif

DWORD OrderImpl::Weight() const
{
#ifndef Suchanov
   PriceImpl price;
   DWORD weight = 0;
   vector_t<OrderItem>::const_iterator i = items.begin();

   for( ;i != items.end(); i++ )
   {
      price.id = i->id;
      if( price.Read() )
         weight += ItemWeight(price.weight, i->qty);
   }

   return weight;
#else
   return 0;
#endif
}

bool OrderImpl::EditDetail()
{
   return EditOrderDetail(this);
}

bool OrderImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

#if defined(ORD_DLV_BIND) && !defined(ORDER_ONLINE)
   if( *number != L'\0' ) return false;
#endif

   const wchar_t *updStr = L"params";
   if( reverse )
   {
      if( params & ofExported ) params &= (~ofExported);
      else params |= ofExported;
   } else
   {
      params |= ofExported;

#ifdef ORDER_ONLINE
      vector_t<OrderItem>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
         i->flags &= (~oiDirtyItem);

      updStr = L"items,params";
#endif
   }

   return (updateTable == NULL) ? true : updateTable->Update(*this, updStr, rid);
}

bool OrderImpl::CanRemove() const
{
   bool needDelete = false;
   if( IsExported() )
   {
      int id = MessageBox(GetActiveWindow(), L"Удалить заказ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
      if( id == IDYES )
         needDelete = true;
   } else
   {
      int id = MessageBox(GetActiveWindow(), L"ВНИМАНИЕ!\nЗаказ не передан на компьютер\nУдалить заказ?", 
         L"Подтверждение", MB_YESNO|MB_ICONQUESTION);

      if( id == IDYES )
         needDelete = true;
   }

   return needDelete;
}

void OrderImpl::EditDocument(UINT retForm)
{
   if( items.size() == 0 )
      OpenPriceList(this);
   else
      OpenInvoice(this, (retForm != IDD_ORDER_LIST));
}

void OrderImpl::LoadItemSales(std::set<ItemSales, ItemSaleDateCompare> *sales, const wchar_t *itemID)
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->id, itemID) != 0 ) continue;

      ItemSales item;
      item.date = date;
      item.qty = i->qty;

      std::set<ItemSales, ItemSaleDateCompare>::iterator fnd = sales->find(item);
      if( fnd != sales->end() )
         fnd->qty += item.qty;
      else
         sales->insert(item);
   }
}
#ifdef Zakroma
IDocument* OrderImpl::Copy() { return NULL; }
#else
IDocument* OrderImpl::Copy()
{
   if( rid == NO_ROWID ) return NULL;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   OrderImpl *o = new OrderImpl();

   o->Read(rid);
   SystemTimeToFileTime(&st, &o->created);
   o->params = 0;
   o->rid = NO_ROWID;
#ifdef ORD_DLV_BIND
   o->number = L"";
#endif
   o->Write();
   return o;
}
#endif

void OrderImpl::AddFromPriceList()
{
   OpenPriceList(this);
}

std::vector<OrderItem>::iterator OrderImpl::FindItem(const wchar_t *id) const
{
   std::vector<OrderItem>::iterator i = ((vector_t<OrderItem>&)items).begin();

   for( ; i!=items.end(); i++ )
      if( wcscmp(i->id, id) == 0 ) return i;

   return ((vector_t<OrderItem>&)items).end();
}

#ifdef MULTI_WH
#include <NplConfig.h>

#ifdef Agama
short OrderImpl::WarehouseIndex() { return whIndex; }
#else
short OrderImpl::WarehouseIndex()
{
   int index = 0;
   if( *warehouseCode != L'\0' )
   {
      NapoleonConfig config;
      std::wstring tvalue;
      if( config.ReadValue(&tvalue, WAREHOUSES) )
      {
         int off = 0, nextOff, codePos;
         while( true )
         {
            nextOff = tvalue.find(SEP_SYM, off);
            std::wstring value = tvalue.substr(off, (nextOff != std::wstring::npos) ? 
               nextOff - off : std::wstring::npos);

            codePos = value.find(L'\t');
            if( codePos != std::wstring::npos && value.substr(codePos+1).compare(warehouseCode) == 0 )
               break;
            if( nextOff == std::wstring::npos )
               break;
            off = nextOff + 1;
            index++;
         }
      }
   }
   return index;
}
#endif // Agama
#elif FIRMS_REST
short OrderImpl::WarehouseIndex() { return supplyer; }
#elif WH_QTY
short OrderImpl::WarehouseIndex() { return whIndex; }
#endif // Multi_wh

bool OrderImpl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   AfterRemove();
   rid = NO_ROWID;
   return true;
}

bool OrderImpl::RemoveByKey(const wchar_t *key)
{
   if( table.Remove(*this, key) )
   {
      AfterRemove();
      return true;
   }
   return false;
}

void OrderImpl::RemoveOrdersTill(const SYSTEMTIME &check)
{
   FILETIME ft;
   SystemTimeToFileTime(&check, &ft);

   wchar_t buf[200];
   __int64 val = ft.dwLowDateTime | (((__int64)ft.dwHighDateTime) << 32);
   wsprintf(buf,  L"DELETE FROM %s WHERE date <= %d%09d", Name(), (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));

   SQLTable::Execute(buf);
   docTypeManager.Refresh(dtOrder);
}

void OrderImpl::ChangeSumType(WORD newSumType)
{
   PriceImpl p;

   std::vector<OrderItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      p.id = i->id;
      p.Read();
      i->cost = p.cost[newSumType];
   }
   this->sumType = newSumType;
   if( Write() )
      docTypeManager.Refresh(dtOrder);
}

#ifdef ORD_SURVAY
const wchar_t *OrderImpl::GetSurvay(DWORD folder) const
{
   vector_t<Survay>::const_iterator i = survay.begin();
   for( ; i != survay.end(); i++ )
   {
      if( i->folder == folder ) return i->choice;
   }

   return L"";
}

void OrderImpl::SetSurvay(DWORD folder, const wchar_t* fid, const wchar_t *choice)
{
   vector_t<Survay>::iterator i = survay.begin();
   for( ; i != survay.end(); i++ )
   {
      if( i->folder == folder )
      {
         if( choice == NULL || *choice == L'\0' )
         {
            survay.erase(i);
            return;
         }
         i->choice = holder.Add(choice);
         i->fid = holder.Add(fid);
      }
   }

   Survay gf;
   gf.folder = folder;
   gf.choice = holder.Add(choice);

   survay.push_back(gf);

   Write();
}
#endif

#ifdef SHOW_OFF_TAKE

bool OrderImpl::HideRemnants() const
{
   return false;
}

#endif

#ifdef ORD_ADD_TO_PACK
void OrderImpl::AddToFullPack()
{
   PriceImpl price;

   bool changed = false;

   vector_t<OrderItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      price.id = i->id;
      price.Read();

      if( price.qtyInPack != 0 )
      {
         int rest = i->qty % price.qtyInPack;
         if( rest != 0 )
         {
            int changes = (price.qtyInPack - rest);
            i->qty += changes;

            ItemQtyChanged(i->id, changes);
            changed = true;
         }
      }
   }

   if( changed )
      docTypeManager.SumChanged(dtOrder, id);
}
#endif

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
#include "DocType.h"

#include <Add.h>

#include <algorithm>

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect* selector = NULL) : PriceFormData(_order, selector)
   {
      openMatrix = false;
      coef = GetCoef(_order);
      
      retDoc = NULL;
      if( _order != NULL && !order->HideRemnants() )
         retDoc = ReturnImpl::GetAssociated(*_order);
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID& upFolder, IPriceSelect* selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
      openMatrix = false;
      coef = GetCoef(_order);
      retDoc = (_order == NULL) ? NULL : ReturnImpl::GetAssociated(*_order);
   }

   virtual ~PriceFormDataAdd()
   {
      if( retDoc != NULL )
      {
         if( retDoc->items.size() == 0 )
            retDoc->Remove();
         delete retDoc;
      }
   }

   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual DWORD ItemCost(const Price &price, WORD ct) const
   {
      DWORD cost = PriceFormData::ItemCost(price, ct);
      return (coef == SUM_SCALE) ? cost : (int)((__int64)cost * coef / SUM_SCALE);
   }

   WORD GetCoef(OrderImpl* _order);

   virtual void SetDataDone()
   {
      if( openMatrix )
      {
         SetMatrix(1);
         owner->Refresh();
      }
   }

   virtual COLORREF GetItemColor(int index) const
   {
      COLORREF ret = PriceFormData::GetItemColor(index);

      index -= folders.size();
      if( index >= 0 && index < (int)leafs.size() && ret == textColor )
      {
         if( retDoc != NULL && retDoc->FindItem(priceItem.id) != retDoc->items.end() )
            ret = RGB(192,192,192);
      }
      return ret;
   }

   virtual void SetQTYDataFromOrder(QTYData *qd, const Price& p, std::vector<OrderItem>::iterator item);
   virtual void UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item);
   virtual void LoadMatrix();
   WORD coef;
   bool openMatrix;

   ReturnImpl* retDoc;
};

static void LoadDocuments(const wchar_t* id, std::set<std::wstring>* items)
{
   FILETIME ft;
   _Module.GetLocalTime(&ft);

     wchar_t buf[200];
   __int64 val = *(__int64*)&ft - (__int64)28 * 24 * 3600 * 10000000;
   wsprintf(buf,  L"date >= %d%09d", (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));

   DocumentList *dl;
   const DocType *dt = docTypeManager.GetDocType(dtOrder);
   if( dt->GetDocuments(id, &dl, buf) )
   {
      int count = dl->Count();
      for( int i=0; i<count; i++ )
      {
         OrderImpl& d = *(OrderImpl*)(dl->Get(i)->Data());
         vector_t<OrderItem>::const_iterator di = d.items.begin();
         for( ; di != d.items.end(); di++ )
            items->insert(di->id);
      }
   }
   delete dl;
}

struct OSItem
{
   std::wstring name;
   ROWID id;
};

bool SItemCmp(const OSItem& el1, const OSItem& el2)
{
   return (el1.name.compare(el2.name) < 0);
}

static void LoadItems(std::vector<OSItem> *dest, const std::set<std::wstring>& src)
{
   PriceImpl pi;
   std::set<std::wstring>::const_iterator i = src.begin();
   for( ; i != src.end(); i++ )
   {
      pi.id = (wchar_t*)((*i).c_str());
      pi.Read();

      OSItem si;
      si.name = pi.name;
      si.id = pi.RID();
      dest->push_back(si);
   }

   sort(dest->begin(), dest->end(), SItemCmp);
}

void PriceFormDataAdd::SetQTYDataFromOrder(QTYData *qd, const Price& p, std::vector<OrderItem>::iterator item)
{
   PriceFormData::SetQTYDataFromOrder(qd, p, item);
   if( retDoc != NULL )
   {
      std::vector<OrderItem>::iterator fnd = retDoc->FindItem(qd->id.c_str());
      if( fnd != retDoc->items.end() )
         qd->retQty = fnd->qty;
   }
}

void PriceFormDataAdd::UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item)
{
   PriceFormData::UpdateOrder(qd, item);
   if( retDoc != NULL )
   {
      QTYData qdr = qd;
      qdr.qty = qd.retQty;

      retDoc->UpdateOrder(retDoc->FindItem(qd.id.c_str()), qdr);
   }
}

void PriceFormDataAdd::LoadMatrix()
{
   PriceFormData::LoadMatrix();

   if( order == NULL )
      return;

   OrgImpl o;
   o.id = order->id;
   o.Read();

   if( o.matrix.size() > 0 )
   {
      Matrix m;
      m.name = L"<Ассортимент>";

      PriceImpl p;
      vector_t<MatrixItem>::const_iterator i = o.matrix.begin();
      for( ; i != o.matrix.end(); i++ )
      {
         p.id = i->id;
         if( p.Read() )
            m.items.push_back(p.rid);
      }

      matrixes.insert(matrixes.begin()+1, m);
      openMatrix = true;
   }

   //std::set<std::wstring> items;
   //LoadDocuments(order->id, &items);

   //if( items.size() )
   //{
   //   std::vector<OSItem> dest;
   //   LoadItems(&dest, items);

   //   Matrix m;
   //   m.name = L"<Ассортимент>";

   //   std::vector<OSItem>::const_iterator i = dest.begin();
   //   for( ; i != dest.end(); i++ )
   //      m.items.push_back(i->id);

   //   matrixes.insert(matrixes.begin()+1, m);
   //   openMatrix = true;
   //}
}

WORD PriceFormDataAdd::GetCoef(OrderImpl* _order)
{
   WORD c = SUM_SCALE;
   if( _order != NULL )
   {
      OrgImpl oi;
      oi.id = _order->id;
      oi.Read();

      c = oi.coef;
      if( c == 0 )
         c = SUM_SCALE;
   }

   return c;
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
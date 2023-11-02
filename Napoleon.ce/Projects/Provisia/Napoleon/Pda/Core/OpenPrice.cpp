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

#include "Add.h"
#include <StdFuncs.h>

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect *selector = NULL) : PriceFormData(_order, selector)
   {
      openMatrix = false;
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect *selector = NULL) : PriceFormData(_order, upFolder, selector)
   {
      openMatrix = false;
   }

   virtual bool Get(IReflectableData* data, int index) const;

   mutable std::wstring name;

   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual void SetDataDone()
   {
      if( openMatrix )
      {
         SetMatrix(1);
         owner->Refresh();
      }
   }

   virtual void LoadMatrix();
   bool openMatrix;
};

bool PriceFormDataAdd::Get(IReflectableData* data, int index) const
{
   if( PriceFormData::Get(data,index) == false ) return false;

   if( index >= (int)folders.size() && priceItem.qty <= 0 )
   {
      name = L"* ";
      name += ((PriceFormItem*)data)->name;
      ((PriceFormItem*)data)->name = name.c_str();
   }
   return true;
}

void PriceFormDataAdd::LoadMatrix()
{
   PriceFormData::LoadMatrix();

   if( order != NULL )
   {
      std::set<std::wstring> items;
      FocusedItemsImpl fi;
      SQLTable t(fi.Name());
      bool bdo = t.Select(&fi);
      while( bdo )
      {
         items.insert(fi.id);
         bdo = t.SelectNext(&fi);
      }

      SYSTEMTIME st;
      __int64 val;
      GetLocalTime(&st);
      st.wDay = 1;
      ResetTime(&st);
      SystemTimeToFileTime(&st, (FILETIME*)&val);

      std::wstring whereStr;
      wchar_t buf[200];
      wsprintf(buf,  L"date >= %d%09d", (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));
      whereStr += buf;
      
      st.wMonth++;
      SystemTimeToFileTime(&st, (FILETIME*)&val);
      wsprintf(buf,  L" AND date < %d%09d", (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));
      whereStr += buf;

      const ::DocType* dt = docTypeManager.GetDocType(dtOrder);
      DocumentList *orgDocs = NULL;
      if( dt->GetDocuments(order->id, &orgDocs, whereStr.c_str()) && orgDocs->Count() >= 1 )
      {
         for( unsigned i=0; i<orgDocs->Count() && items.size() > 0; i++ )
         {
            IDocument *d = orgDocs->Get(i);
            if( d != NULL )
            {
               OrderImpl *doc = (OrderImpl*)d->Data();
               vector_t<OrderItem>::const_iterator item = doc->items.begin();
               for( ; item != doc->items.end() && items.size() > 0; item++ )
               {
                  std::set<std::wstring>::iterator fnd = items.find(item->id);
                  if( fnd != items.end() )
                     items.erase(fnd);
               }
            }
         }
      }
      delete orgDocs;


      if( items.size() > 0 )
      {
         Matrix m;
         m.name = L"<Фокусный товар>";

         PriceImpl p;
         std::set<std::wstring>::const_iterator i = items.begin();
         for( ; i != items.end(); i++ )
         {
            p.id = (wchar_t*)(*i).c_str();
            p.Read();
            m.items.push_back(p.RID());
         }

         matrixes.insert(matrixes.begin()+1, m);
         //openMatrix = true;
      }
   }
}


void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST, pfd);
}


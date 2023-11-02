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

void PriceFormDataAdd::LoadMatrix()
{
   PriceFormData::LoadMatrix();

   if( order != NULL )
   {
      OrgImpl o;
      o.id = order->id;
      o.Read();

      if( o.matrix.size() > 0 )
      {
         Matrix m;
         m.name = L"<Матрица контрагента>";

         PriceImpl p;
         vector_t<MatrixItem>::const_iterator i = o.matrix.begin();
         for( ; i != o.matrix.end(); i++ )
         {
            p.id = i->id;
            p.Read();

            m.items.push_back(p.rid);
         }

         matrixes.insert(matrixes.begin()+1, m);
         openMatrix = true;
      }
   }
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
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
#include "NplConfig.h"
#include "Preference.h"
#include <DocImpl.h>

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order);
   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder);
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual COLORREF GetItemColor(int index) const;
   COLORREF GetItemBkColor(int index) const;

   virtual void SetDataDone()
   {
      if( matrixIndex >= 0 )
      {
         SetMatrix(matrixIndex);
         owner->Refresh();
      }
   }

   virtual void LoadMatrix();
   int matrixIndex;
};

class PriceFormAdd : public PriceForm
{
 public:
   PriceFormAdd() {}

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   BEGIN_MSG_MAP(PriceFormAdd)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)
   virtual DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
   {
      NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
      DWORD item = lvcd->nmcd.dwItemSpec;
      lvcd->clrText = ((PriceBaseData*)data)->GetItemColor(item);
      lvcd->clrTextBk = ((PriceFormDataAdd*)data)->GetItemBkColor(item);

      return CDRF_NOTIFYITEMDRAW;
   }
};

IMPLEMENT_FORM(PriceFormAdd);

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order)
{
   matrixIndex = -1;
}

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder) : PriceFormData(_order, upFolder)
{
   matrixIndex = -1;
}

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
         std::wstring matrix = o.matrix.front().name;
         std::vector<Matrix>::const_iterator mi = matrixes.begin();
         int idx = 0;
         for( ; mi != matrixes.end(); mi++, idx++ )
         {
            if( matrix.compare(mi->name) == 0 )
            {
               matrixIndex = idx;
               break;
            }
         }
      }
   }
}

COLORREF PriceFormDataAdd::GetItemColor(int index) const
{
   index -= folders.size();
  
   if( index >= 0 && index < (int)leafs.size() )
   {
      priceItem.Read(leafs[index]);

      if( IsItemMarked(priceItem.id) )
         return selectColor;

      if( lastSaledItems.find(priceItem.id) != lastSaledItems.end() )
        return lastColor;
   }
   return textColor;
}

COLORREF PriceFormDataAdd::GetItemBkColor(int index) const
{
   index -= folders.size();
  
   COLORREF bk = RGB(255,255,255);

   if( index >= 0 && index < (int)leafs.size() )
   {
      priceItem.Read(leafs[index]);

      return (priceItem.color != 0) ? priceItem.color : bk;
   }
   return bk;
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


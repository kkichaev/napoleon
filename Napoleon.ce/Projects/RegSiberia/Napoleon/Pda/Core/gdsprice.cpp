/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * Регион Сибирь add-in
 *
 *  ert   17/03/2011   creating
 */ 
#include "stdafx.h"
#include <Exchange.h>
#include <ObjImpl.h>
#include <Module.h>

#include "Add.h"
#include <NapoleonRes.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <PriceForm.h>
#include <FormEntries.h>

struct GoodsPriceData : public PriceFormData
{
   GoodsPriceData(GoodsRestImpl* doc, bool retToDocList);
   GoodsPriceData(GoodsRestImpl* doc, const ROWID& upFolder, bool retToDocList);

   ~GoodsPriceData();

   virtual PriceBaseData* Clone() { return new GoodsPriceData(UnbindGoods(), UpFolder(), retToDocList); }

   virtual COLORREF GetItemColor(int index) const;

   GoodsRestImpl* UnbindGoods() { GoodsRestImpl *d = doc; doc = NULL; return d; }
   virtual bool SelectLeaf(int index);

   bool retToDocList;

protected:
   GoodsRestImpl* doc;
};

class GoodsPrice : public PriceForm
{
public:
   GoodsPrice() {}

   DECLARE_FORM(GoodsPrice, IDD_GOODS_PRICE)

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }

   virtual LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

IMPLEMENT_FORM(GoodsPrice)

GoodsPriceData::GoodsPriceData(GoodsRestImpl* doc, bool retToDocList) : PriceFormData(NULL)
{
   this->doc = doc;
   this->retToDocList = retToDocList;
}

GoodsPriceData::GoodsPriceData(GoodsRestImpl* doc, const ROWID& upFolder, bool retToDocList) : PriceFormData(NULL, upFolder)
{
   this->doc = doc;
   this->retToDocList = retToDocList;
}

GoodsPriceData::~GoodsPriceData()
{
   delete doc;
}

COLORREF GoodsPriceData::GetItemColor(int index) const
{
   int i1 = index - folders.size();
   if( i1 >= 0 && i1 < (int)leafs.size() )
   {
      priceItem.Read(leafs[index]);
      if( doc->HaveItem(priceItem.id) )
         return RGB(0, 255, 0);
   }
   return PriceFormData::GetItemColor(index);
}

bool GoodsPriceData::SelectLeaf(int index)
{
   if( index >= (int)leafs.size() )
      return false;

   PriceImpl p;
   p.Read(leafs[index]);

   return doc->EditItemData(p.id);
}

LRESULT GoodsPrice::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   GoodsRestImpl *doc = ((GoodsPriceData*)data)->UnbindGoods();

   if( doc != NULL )
      OpenGoods(doc, ((GoodsPriceData*)data)->retToDocList);
   else
      PriceForm::Closing(nCode, id, hWnd, bHandled);

   return 0;
}

void OpenGoodsPrice(GoodsRestImpl* doc, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_GOODS_PRICE, new GoodsPriceData(doc, retToDocList));
}
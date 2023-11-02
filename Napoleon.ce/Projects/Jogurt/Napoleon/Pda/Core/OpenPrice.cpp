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

#include "Price.h"
#include "PriceForm.h"
#include "FormEntries.h"

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order) {}
   PriceFormDataAdd(OrderImpl* _order, CEOID upFolder) : PriceFormData(_order, upFolder) {}

   virtual PriceFormData* Clone()
   {
      OrderImpl *order = UnbindOrder();
      CEOID upFolder = UpFolder();

      PriceFormData *pfd = new PriceFormDataAdd(order, upFolder);
      return pfd;
   }

   DWORD Flags(int index);

 protected:
};


class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd() {}

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }

protected:
   virtual LRESULT SetCellInfo(LPNMHDR hdr);
};

IMPLEMENT_FORM(PriceFormAdd)

DWORD PriceFormDataAdd::Flags(int index)
{
   if( index < (int)folders.size() ) return 0;

   index -= folders.size();
   if( index >= (int)leafs.size() )
      return 0;

   CETable priceTable(priceFormat);
   priceTable.Open(syncPrice.FileName());
   priceTable.Seek(leafs[index]);
   priceTable.GetCurrent(pItem);

   return pItem->flags;
}

LRESULT PriceFormAdd::SetCellInfo(LPNMHDR hdr)
{
   if( PriceForm::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
   {
      int index = di->item.iItem;
      DWORD flags = ((PriceFormDataAdd*)data)->Flags(index);
      if( (flags & 0x4) != 0 )
         di->item.iImage = 5;
      else if( (flags & 0x8) )
         di->item.iImage = 6;
   }

   return TRUE;
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


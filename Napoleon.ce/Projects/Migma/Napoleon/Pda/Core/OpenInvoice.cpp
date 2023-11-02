/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   16/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "FormEntries.h"
#include "Invoice.h"

struct InvoiceDataAdd : public InvoiceData
{
   InvoiceDataAdd(OrderImpl *_order, bool retToDocList) : InvoiceData(_order, retToDocList) {}

   void GetOrderText(std::wstring *val)
   {
      int qty = 0;
      int i = order->items.size();

      while( i-- > 0 ) qty += (order->items[i].qty);
      wchar_t buf[30];
/*
      int discount = order->discount;
      char sign = '+';
      if( discount < 0 )
      {
         discount = -discount;
         sign = '-';
      }

      wsprintf(buf, L"%d%c%02d.%d", order->sumType + 1, sign, discount/DISCOUNT_SCALE, 
         discount % DISCOUNT_SCALE);
 */
      wsprintf(buf, L"%d шт", qty/QTY_SCALE);
      *val = buf;
   }
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd();

   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }

protected:
   virtual void SetDocumentInfoText();
};

IMPLEMENT_FORM(InvoiceAdd)

InvoiceAdd::InvoiceAdd()
{
}

void InvoiceAdd::SetDocumentInfoText()
{
   sumLabel.SetSum(((DocumentData*)data)->Sum());
   std::wstring tval;

   ((InvoiceDataAdd*)data)->GetOrderText(&tval);
   sumLabel.SetInfoText(tval.c_str());
}

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceDataAdd(order, retToDocList));
}

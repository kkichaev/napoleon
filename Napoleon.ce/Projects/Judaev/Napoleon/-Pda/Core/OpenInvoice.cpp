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
   InvoiceDataAdd(OrderImpl *_order, bool retToDocList) : InvoiceData(_order, retToDocList)
   {
   }

   void SetCloseDate()
   {
      if( CompareFileTime(&order->invoiceClose, &order->created) == 0 )
      {
         SYSTEMTIME st;
         GetLocalTime(&st);
         st.wMilliseconds = 0;

         SystemTimeToFileTime(&st, &order->invoiceClose);
         order->Update(L"invoiceClose");
      }
   }
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd() {}

   virtual DWORD GetResourceID() const { return IDD_INVOICE; }
   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }

   BEGIN_MSG_MAP(InvoiceAdd)
     COMMAND_ID_HANDLER(IDC_BACK, Backing)
     COMMAND_ID_HANDLER(IDC_SEND, SendOrder)
     CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

   LRESULT SendOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ((InvoiceDataAdd*)data)->SetCloseDate();
      return Invoice::SendOrder(nCode, id, hWnd, bHandled);
   }

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ((InvoiceDataAdd*)data)->SetCloseDate();
      return Invoice::Backing(nCode, id, hWnd, bHandled);
   }

   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)
};

IMPLEMENT_FORM(InvoiceAdd)

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceDataAdd(order, retToDocList));
}

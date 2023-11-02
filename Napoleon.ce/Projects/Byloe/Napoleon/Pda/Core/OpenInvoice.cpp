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
#include "Add.h"

struct InvoiceDataAdd : public InvoiceData
{
   InvoiceDataAdd(OrderImpl *order, bool retToDocList) : InvoiceData(order, retToDocList)
   {
      SetCurrentWH(order);
   }
};

#ifdef ORD_DLV_BIND
struct InvoiceDlvDataAdd : public InvoiceDlvData
{
   InvoiceDlvDataAdd(OrderImpl *order, bool retToDocList) : InvoiceDlvData(order, retToDocList)
   {
      SetCurrentWH(order);
   }
};
#endif

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
#ifdef ORD_DLV_BIND
   _Module.GetFrame()->Load(IDD_INVOICE, new InvoiceDlvDataAdd(order, retToDocList));
#else
   _Module.GetFrame()->Load(IDD_INVOICE, new InvoiceDataAdd(order, retToDocList));
#endif
}

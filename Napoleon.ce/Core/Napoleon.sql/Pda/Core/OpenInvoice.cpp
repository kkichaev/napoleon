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

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
#ifdef ORD_DLV_BIND
   _Module.GetFrame()->Load(IDD_INVOICE, new InvoiceDlvData(order, retToDocList));
#else
   _Module.GetFrame()->Load(IDD_INVOICE, new InvoiceData(order, retToDocList));
#endif
}

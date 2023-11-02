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

class PriceFormAdd : public PriceForm
{
 public:
   PriceFormAdd() {}

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST_ADD; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)
};

IMPLEMENT_FORM(PriceFormAdd);

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormData(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


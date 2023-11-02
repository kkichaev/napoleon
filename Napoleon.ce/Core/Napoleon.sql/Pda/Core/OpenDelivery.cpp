/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   06/02/2009   creating
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

//
//--------------------------- Globals -------------------------------------
//
void OpenDelivery(DeliveryImpl *dlv, const wchar_t *type)
{
   _Module.GetFrame()->Load(IDD_DELIVERY, new DeliveryData(dlv, type));
}

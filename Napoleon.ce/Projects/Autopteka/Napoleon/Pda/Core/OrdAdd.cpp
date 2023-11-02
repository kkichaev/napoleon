/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Дополнения к заявке
 * 
 *  ert   06/06/2009   creating
 */ 
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <Exchange.h>

#include "FormEntries.h"
#include "Add.h"
#include <ObjImpl.h>

void OrderImpl::ChangeCost(DWORD newType)
{
   vector_t<OrderItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      i->cost = CostManager::GetCost(i->id, newType);
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Создание заказа
 *
 *  ert   20/08/2007   creating
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <exchange.h>
#include <ceint.h>
#include <Table.h>
#include <Sync.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include <DocType.h>

#include "BaseDialog.h"

const DWORD DEMO_LIMIT = 3600 * 24 * 2; // демо период 2 дня
#include "RegHash.h"
#include "Preference.h"
#include "OrgRmnts.h"

bool OrderImpl::Create(CEOID id, SyncFormat *format, DocumentTypes dt)
{
   Preference p;
   p.Load();
   
   if( p.answer != MakeAnswer(p.code) )
   {
      if( p.wokred > DEMO_LIMIT )
      {
         MessageBox(GetActiveWindow(), L"К сожаления, демо период закончился,\nдля продложения работы необходимо зарегистрировать программу",
            L"Информация", MB_OK|MB_ICONINFORMATION);

         return false;
      }
   }

   OrderImpl *order = new OrderImpl(id, format, dt);
   OpenOrgRemnantsForm(order);
   return true;
}

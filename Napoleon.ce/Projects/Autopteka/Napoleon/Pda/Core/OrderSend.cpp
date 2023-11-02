/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
 *  ert   17/06/2008   modifying (SQL impl)
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>
#include "Progress.h"

#include "DBImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>

#include <Preference.h>

DWORD sz = sizeof(Order);

bool OrderImpl::Send()
{
   if( rid == NO_ROWID )
      return false;

   Preference pref;
   HWND activeWindow = GetActiveWindow();
   pref.Load();
   if( IsExported() )
   {
#ifdef CANT_SEND_SENDED
      MessageBox(activeWindow, L"Заказ отправлен\nДля отправки скопируйте заказ", L"Предупреждение", MB_OK|MB_ICONSTOP);
      return false;
#else
      int res = MessageBox(activeWindow, L"Заказ отправлен\nповторить передачу?", L"Предупреждение", MB_YESNO|MB_ICONQUESTION);
      if( res == IDNO )
         return false;
#endif
   } else if( pref.flags & npfConfirmOrderSend )
   {
      int res = MessageBox(activeWindow, L"Отправить заказ?", L"Вопрос", MB_YESNO|MB_ICONQUESTION);
      if( res == IDNO )
         return false;
   }

   if( SendDocument(this, docTypeManager.GetDocType(dtOrder), NULL, true) )
      params |= ofExported;
   return true;
}

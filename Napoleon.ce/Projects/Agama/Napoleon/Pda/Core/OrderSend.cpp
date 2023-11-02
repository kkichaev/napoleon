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
      if( pref.flags & npfConfirmOrderSend )
      {
         MessageBox(activeWindow, L"Документ отправлен\nДля отправки скопируйте документ", L"Предупреждение", MB_OK|MB_ICONSTOP);
         return false;
      }
#else
      int res = MessageBox(activeWindow, L"Документ отправлен\nповторить передачу?", L"Предупреждение", MB_YESNO|MB_ICONQUESTION);
      if( res == IDNO )
         return false;
#endif
   }
#ifdef CANT_SEND_SENDED
#else
   else if( pref.flags & npfConfirmOrderSend )
   {
      int res = MessageBox(activeWindow, L"Отправить документ?", L"Вопрос", MB_YESNO|MB_ICONQUESTION);
      if( res == IDNO )
         return false;
   }
#endif

   if( SendDocument(this, docTypeManager.GetDocType(docType)) )
   {
      params |= ofExported;
   }

   /*
   ProgressWindow pw;
   pw.CreateSTDWindow(activeWindow);
   pw.SetText(L"Подготовка");

   std::wstring answer;
   long ec = _Module.SendDocument(this, docTypeManager.GetDocType(dtOrder), &answer, &pw);
   pw.DestroyWindow();

   if( rid == NO_ROWID ) // order was destroyed
      return true;

   if( ec )
   {
      _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
      return false;
   } else
   {
      params |= ofExported;
      Write();

      MessageBox(activeWindow, answer.c_str(), L"Подтверждение", MB_OK|MB_ICONINFORMATION);
   }
   */
   return true;
}

/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Компонент обработка ввода чисел
 *
 *  ert   17/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <NapoleonRes.h>

#include "NumInput.h"

LRESULT NumInput::OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   CWindow parent(CWindow(hWndCtl).GetParent());
   CEdit qty(parent.GetDlgItem(ctrlID));
   qty.SetFocus();

   wchar_t buf[2];
   buf[1] = L'\0';
   int len = qty.GetWindowTextLength();
   wchar_t *textBuf = (wchar_t*)alloca((len+1) * sizeof(textBuf[0]));
   qty.GetWindowText(textBuf, len+1);

   if( wID == IDC_DIG_BS )
   {
      int start, end;
      qty.GetSel(start, end);
      if( start != end )
         qty.ReplaceSel(L"");
      else
      {
         if( len > 0 )
         {
            wcscpy(textBuf+len-1, textBuf+len);
            qty.SetWindowText(textBuf);
            qty.SetSel(len,len);
         }
      }
      return 0;
   } else if( wID == IDC_MINUS )
   {
      if( *textBuf == L'-' )
         qty.SetWindowText(textBuf+1);
      else
      {
         *buf = L'-';
         qty.SetSel(0,0);
         qty.ReplaceSel(buf);
         qty.SetSel(len,len);
      }
      return 0;
   } else if( wID == IDC_DIG_PT )
   {
      if( wcschr(textBuf, L'.') == NULL )
      {
         *buf = L'.';
         qty.ReplaceSel(buf);
      }
      return 0;
   }
   
   switch(wID)
   {
   case IDC_DIG_0:
      *buf = L'0';
      break;
   case IDC_DIG_1:
      *buf = L'1';
      break;
   case IDC_DIG_2:
      *buf = L'2';
      break;
   case IDC_DIG_3:
      *buf = L'3';
      break;
   case IDC_DIG_4:
      *buf = L'4';
      break;
   case IDC_DIG_5:
      *buf = L'5';
      break;
   case IDC_DIG_6:
      *buf = L'6';
      break;
   case IDC_DIG_7:
      *buf = L'7';
      break;
   case IDC_DIG_8:
      *buf = L'8';
      break;
   case IDC_DIG_9:
      *buf = L'9';
      break;
   }
   qty.ReplaceSel(buf);
   return 0;
}

void NumInput::Show(CWindow& parent, UINT show)
{
   int ctrls[] = { IDC_DIG_0, IDC_DIG_1, IDC_DIG_2, IDC_DIG_3, IDC_DIG_4, 
      IDC_DIG_5, IDC_DIG_6, IDC_DIG_7, IDC_DIG_8, IDC_DIG_9, IDC_DIG_PT, IDC_DIG_BS };

   for( int i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
      parent.GetDlgItem(ctrls[i]).ShowWindow(show);
}
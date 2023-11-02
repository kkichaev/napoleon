/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества, custom
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

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include "Add.h"
#include <NplConfig.h>

int curWhIndex = 0;

int SetCurrentWH(OrderImpl* o)
{
   curWhIndex = 0;

   if( o != NULL && o->whCode != L'\0' ) 
   {
      std::wstring value;
      NapoleonConfig cfg;

      if( cfg.ReadValue(&value, L"Склады") )
      {
         std::wstring::size_type off = 0, nextOff = 0;
         int ctr = 0;
         while( true )
         {
            nextOff = value.find(SEP_SYM, off);
            std::wstring tval = value.substr(off, (nextOff != std::wstring::npos) ? 
                  nextOff - off : std::wstring::npos);

            std::wstring::size_type amp = tval.find(L'\t');
            if( amp != std::wstring::npos )
            {
               if( wcscmp(tval.substr(amp+1).c_str(), o->whCode) == 0 )
               {
                  curWhIndex = ctr;
                  break;
               }
            }

            if( nextOff == std::wstring::npos )
               break;

            off = nextOff + 1;
            ctr++;
         }
      }
   }

   return curWhIndex;
}

class QTYAdd : public CQTYDialog
{
public:
   QTYAdd(QTYData *data) : CQTYDialog(data) {}

   virtual DWORD PriceQty(const Price &price) const
   {
      if( curWhIndex == 0 || curWhIndex > (int)price.qtys.size() ) return price.qty;
      return price.qtys[curWhIndex-1].qty;
   }
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYAdd dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

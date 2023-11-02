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

#include <MainFrame.h>
#include <Table.h>

class QTYAdd : public CQTYDialog
{
public:
   QTYAdd(QTYData *data) : CQTYDialog(data)
   {
      mult = 0;
   }

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      if( mult == 0 )
      {
         PriceImpl p;
         p.id = (wchar_t*)data->id.c_str();
         p.Read();
         mult = p.mult;
         if( mult == 0 ) mult = QTY_SCALE;
      }
      SetScalingValue(IDC_MULT_QTY, mult, QTY_SCALE, true);      
   }

   virtual void MoveControls(WORD wdh, WORD hgh)
   {
      CRect b1, b2;
      GetDlgItemRect(b1, IDC_INPACK_LABEL);
      GetDlgItemRect(b2, IDC_MULT_QTY_LABEL);

      int w = b2.Width();
      b2.left = b1.left;
      GetDlgItem(IDC_MULT_QTY_LABEL).MoveWindow(b2.left, b2.top, w, b2.Height());

      GetDlgItemRect(b1, IDC_INPACK);
      GetDlgItemRect(b2, IDC_MULT_QTY);
      w = b2.Width();
      b2.left = b1.right - w;
      GetDlgItem(IDC_MULT_QTY).MoveWindow(b2.left, b2.top, w, b2.Height());
   }
   
   virtual bool CheckQty()
   {
      if( mult > QTY_SCALE && (data->qty % mult) != 0 )
      {
         wchar_t buf[100];
         wsprintf(buf, L"Количество должно быть кратно %d", mult / QTY_SCALE);
         MessageBox(buf, L"Предупреждение", MB_OK | MB_ICONSTOP);
         return false;
      }
      return CQTYDialog::CheckQty();
   }

protected:
   DWORD mult;
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYAdd dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

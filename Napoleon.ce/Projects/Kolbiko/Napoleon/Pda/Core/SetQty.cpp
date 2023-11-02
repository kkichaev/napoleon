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

#include "Add.h"

bool qtyInited = false;
class QTYDialog : public CQTYDialog
{
public:
   BEGIN_MSG_MAP(QTYDialog)
      COMMAND_HANDLER(IDC_ADD_QTY, EN_CHANGE, UpdateRemnants)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

   QTYDialog(QTYData *data) : CQTYDialog(data) {qtyInited = false;}

   virtual DWORD GetStartQty(bool inPack, int qip)
   {
      if( data->sales.size() > 1 )
         return 0;
      else
         return CQTYDialog::GetStartQty(inPack, qip); 
   }

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      SetDlgItemText(IDC_UNIT_TEXT, price.unitName);
      SetScalingValue(IDC_MULT_QTY, price.avgWeight, WEIGHT_SCALE, false);

      SetScalingValue(IDC_ADD_QTY, data->retQty, QTY_SCALE, true);

      CEdit qty(GetDlgItem(IDC_ADD_QTY));
      qty.SetLimitText(6);
      qty.SetSelAll();

      if( (data->flags & oiHideRemnants) != 0 )
      {
         GetDlgItem(IDC_ADD_QTY).ShowWindow(SW_HIDE);
         GetDlgItem(IDC_TEXT).ShowWindow(SW_HIDE);
      }

      if( data->sales.size() > 0 && data->qty == 0 && data->canChange )
         SetScalingValue(IDC_QTY, data->sales.front().qty, QTY_SCALE, true);
      qtyInited = data->canChange;
   }


   virtual void SaveData()
   {
      wchar_t buf[20];
      GetDlgItemText(IDC_ADD_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      data->retQty = GetValue(buf, QTY_SCALE);
   }

   virtual void MoveControls(WORD wdh, WORD hgh)
   {
      CRect rc1, rc2;

      GetDlgItemRect(rc1, IDC_INPACK_LABEL);

      GetDlgItemRect(rc2, IDC_UNIT_TEXT_LABEL);
      GetDlgItem(IDC_UNIT_TEXT_LABEL).MoveWindow(rc1.left, rc2.top, rc2.right - rc1.left, rc2.Height());

      GetDlgItemRect(rc2, IDC_MULT_QTY_LABEL);
      GetDlgItem(IDC_MULT_QTY_LABEL).MoveWindow(rc1.left, rc2.top, rc2.right - rc1.left, rc2.Height());

      GetDlgItemRect(rc1, IDC_INPACK);

      GetDlgItemRect(rc2, IDC_UNIT_TEXT);
      GetDlgItem(IDC_UNIT_TEXT).MoveWindow(rc1.right - rc2.Width(), rc2.top, rc1.right, rc2.Height());

      GetDlgItemRect(rc2, IDC_MULT_QTY);
      GetDlgItem(IDC_MULT_QTY).MoveWindow(rc1.right - rc2.Width(), rc2.top, rc1.right, rc2.Height());

      GetDlgItemRect(rc1, IDC_REMNANTS_LABEL);
      GetDlgItemRect(rc2, IDC_TEXT);
      GetDlgItem(IDC_TEXT).MoveWindow(rc1.right - rc2.Width(), rc2.top, rc1.right, rc2.Height());

      GetDlgItemRect(rc1, IDD_REMNANTS_QTY);
      GetDlgItemRect(rc2, IDC_ADD_QTY);
      GetDlgItem(IDC_ADD_QTY).MoveWindow(rc1.left, rc2.top, rc1.Width(), rc2.Height());
   }
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

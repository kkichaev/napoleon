/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Диалог количества
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

#include "FormEntries.h"
#include "Preference.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

#include <MainFrame.h>
#include "SAnchor.h"
#include "Qty.h"
#include <EnterNumber.h>

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *_data) : CQTYDialog(_data, IDD_QTY)
   {
   }

   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(QTYDialog)
      //MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_COST, STN_CLICKED, SetCost)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

protected:
   StaticAnchor costText;


   LRESULT SetCost(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[20];
      GetDlgItemText(IDC_COST, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD dcost = GetValue(buf, SUM_SCALE);

      EnterNumber dlg;
      dlg.value = dcost;
      dlg.title = L"Цена";

      if( dlg.DoModal() == IDOK )
      {
         SetScalingValue(IDC_COST, dlg.value, SUM_SCALE, false);
         data->cost = dlg.value;
         SetSum(code, id, hWnd, bHandled);
      }

      return 0;
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      costText.SubclassWindow(GetDlgItem(IDC_COST));

      return FALSE;
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


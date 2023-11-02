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
#include "FormEntries.h"

#include <MainFrame.h>
#include <NplConfig.h>

void MoveDown(CWindow* parent, int *ctrls, int qty, UINT id1, UINT id2); // from Qty.cpp

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *_data) : CQTYDialog(_data)
   {
      flags &= (~ShowSIP);
   }

   BEGIN_MSG_MAP(QTYDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_COST_TYPE, CBN_SELCHANGE, SetCostType)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()
   
   LRESULT SetCostType(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      CComboBox cb(GetDlgItem(IDC_COST_TYPE));

      int newcs = cb.GetCurSel();
      PriceImpl p;
      p.id = (wchar_t*)data->id.c_str();
      if( p.Read() && (int)p.cost.size() > newcs )
      {
         data->cost = p.cost[newcs];
         SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);
         SetSum(0, 0, hWnd, bHandled);
      }
      return 0;
   }

   LRESULT OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      int ctrls[] = { IDC_PREV_QTY, IDC_SALES_HIST, IDC_COST_TEXT, IDC_COST_TYPE };
      MoveDown(this, ctrls, sizeof(ctrls)/sizeof(ctrls[0]), IDC_COST_LABEL, IDC_DISCOUNT);

      NapoleonConfig config;
      std::wstring val;
      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, data->costType);

      bHandled = FALSE;
      return 0;
   }

   void UpdateWinPos(UINT id, UINT ref)
   {
      CRect r1, r2;
      CWindow w1(GetDlgItem(id));

      GetDlgItem(ref).GetWindowRect(r1);
      ScreenToClient(r1);
      w1.GetWindowRect(r2);
      ScreenToClient(r2);
      int offset = r2.left - r1.left;
      if( offset != 0 )
      {
         r2.left -= offset;
         r2.right -= offset;
         w1.MoveWindow(r2);
      }
   }

   LRESULT OnSizeChanged(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      CQTYDialog::OnSizeChanged(uMsg, wParam, lParam, bHandled);

      UpdateWinPos(IDC_COST_TEXT, IDC_COST_LABEL);
      UpdateWinPos(IDC_COST_TYPE, IDC_COST);

      return 0;
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

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

const int IS_WEIGHT_ITEM = 0x1000;

class CostDialog : public CSimpleDialog<IDC_SUM, TRUE> 
{
public:
   CostDialog(DWORD cost, DWORD stopCost, WORD scale = SUM_SCALE) : numInput(IDC_COST)
   { this->cost = cost; this->stopCost = stopCost; this->scale = scale; }

   typedef CSimpleDialog<IDC_SUM, TRUE> BaseClass;

   BEGIN_MSG_MAP(CostDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()
 
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;
      SetScalingValue(IDC_COST, cost, scale, false);
      SetScalingValue(IDC_COST1, stopCost, scale, false);
      return 0;
   }

   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[50];

      GetDlgItem(IDC_COST).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
      cost = GetValue(buf, scale);

      bHandled = FALSE;
      return 0;
   }

   NumInput numInput;
   DWORD cost, stopCost;
   WORD scale;

   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, scale);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
      SetDlgItemText(id, buf);
   }
};


class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *data) : CQTYDialog(data) {}

   BEGIN_MSG_MAP(QTYDialog)
      COMMAND_HANDLER(IDC_COST, STN_CLICKED, ChangeCost)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

   virtual void SetData(const PriceImpl& price)
   {
      data->itemCost = price.cost[0];
      if( data->itemCost == 0 ) data->itemCost = data->cost;
      if( data->discount == 0 && data->cost != data->itemCost )
         data->discount = ((int)(data->cost * DISCOUNT_SCALE * 100 / data->itemCost - DISCOUNT_SCALE * 100));

      CQTYDialog::SetData(price);

      costAnchor.SubclassWindow(GetDlgItem(IDC_COST));

      minNac = price.minNac * DISCOUNT_SCALE / SUM_SCALE;
      isWeightItem = ((price.flags & IS_WEIGHT_ITEM) != 0);
   }

   void ShowWarning()
   {
      wchar_t buf[300];
      wsprintf(buf, L"Наценка меньше минимальной (%d.%01d%%)!", minNac / DISCOUNT_SCALE, minNac % DISCOUNT_SCALE);
      MessageBox( buf, L"Ошибка", MB_OK | MB_ICONSTOP);
   }

   virtual bool CheckQty()
   {
      if( isWeightItem )
      {
         if( (data->qty % qtyInPack) != 0 )
         {
            MessageBox(L"Весовой товар выбивается кратно упаковки!", L"Ошибка", MB_OK | MB_ICONSTOP);
            return false;
         }
         return true;
      } else
         return CQTYDialog::CheckQty();
   }

   virtual bool CanSetDiscount(int dsc)
   { 
      if( dsc < (int)minNac )
      {
         ShowWarning();
         return false;
      }
      return true; 
   }

   LRESULT ChangeCost(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      CostDialog dlg(data->cost, data->itemCost);
      if( dlg.DoModal() == IDOK )
      {
         int disc = ((int)(dlg.cost * DISCOUNT_SCALE * 100 / data->itemCost - DISCOUNT_SCALE * 100));
         if( disc < (int)minNac )
         {
            ShowWarning();
            return 0;
         }
         data->cost = dlg.cost;
         data->discount = ((int)(data->cost * DISCOUNT_SCALE * 100 / data->itemCost - DISCOUNT_SCALE * 100));
         SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);
         SetScalingValue(IDC_DISCOUNT_VALUE, data->discount, DISCOUNT_SCALE, false);
         SetSum(0, 0, hWnd, bHandled);
      }
      return 0;
   }

private:
   StaticAnchor costAnchor;
   DWORD minNac;
   bool isWeightItem;
};

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

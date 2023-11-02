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

#include <SAnchor.h>
#include <NumInput.h>
#include <PictButton.h>
#include "Add.h"
#include "OrgRmnts.h"

class QTYDialog : public CQTYDialog
{
public:
   QTYDialog(QTYData *_data) : CQTYDialog(_data),
      calc((GetSystemMetrics(SM_CXICON)>16) ? IDC_CALC32 : IDC_CALC)
   {
      flags &= (~ShowSIP);
   }

   BEGIN_MSG_MAP(QTYDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_COST, STN_CLICKED, ChangeCost)
      COMMAND_HANDLER(IDC_DISCOUNT_VALUE, STN_CLICKED, ChangeNac)
      COMMAND_ID_HANDLER(IDC_CALC, Calc)
      COMMAND_ID_HANDLER(IDOK, SaveRmnts)

      CHAIN_MSG_MAP(CQTYDialog)

      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT ChangeCost(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT ChangeNac(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT Calc(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT SaveRmnts(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   StaticAnchor cost, nac;
   PictButton calc;


   OrgRemnantsImpl rmnts;
   PriceImpl price;
   StringHolder sh;
};

class CostDialog : public CSimpleDialog<IDD_ENTER_COST, TRUE> 
{
public:
   CostDialog(DWORD cost, DWORD minCost, DWORD stopCost, WORD scale = SUM_SCALE) : numInput(IDC_COST)
   { this->cost = cost; this->minCost = minCost; this->stopCost = stopCost; this->scale = scale; }

   typedef CSimpleDialog<IDD_ENTER_COST, TRUE> BaseClass;

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

      if( minCost != 0 )
      {
         SetScalingValue(IDC_COST1, stopCost, scale, false);
         SetScalingValue(IDC_COST_DSC, minCost, scale, false);
      }
      else
      {
         GetDlgItem(IDC_COST1).ShowWindow(SW_HIDE);
         GetDlgItem(IDC_COST_LABEL).ShowWindow(SW_HIDE);

         GetDlgItem(IDC_COST_DSC).ShowWindow(SW_HIDE);
         GetDlgItem(IDC_COST_DSC_LABEL).ShowWindow(SW_HIDE);
      }
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
   DWORD cost, minCost, stopCost;
   WORD scale;

   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, scale);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
      SetDlgItemText(id, buf);
   }
 };

LRESULT QTYDialog::Calc(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   ItemSales last = {0};
   std::vector<ItemSales> sales;
   
   LoadItemSales(&sales, false, data->orgID.c_str(), data->id.c_str(), NO_ROWID);
   if( sales.size() != 0 )
   {
      std::vector<ItemSales>::const_iterator i = sales.begin();
      last = (*i);
      for( ; i != sales.end(); i++ )
      {
         if( CompareFileTime(&i->date, &last.date) > 0 ) // берем первые данные с датой меньше текущей
         {
            last = (*i);
            break;
         }
      }
   }

   DWORD qty = (DWORD)((__int64)last.qty * (QTY_SCALE + QTY_SCALE/10)) / QTY_SCALE;
   //if( (pData.flags & pfNeedRest) )
   //{
   //   DWORD day;
   //   OrgRemnantsImpl::GetItemData(&qty, &day, data->orgID, data->id);

   //   qty = (qty * 6) / 5;
   //} else
   //{
   //   qty = (last.qty * 16) / 60;
   //   DWORD rest = qty % QTY_SCALE;
   //   if( rest != 0 )
   //      qty += (QTY_SCALE - rest);
   //}

   if( qty == 0 )
   {
      MessageBox(L"Нет данных для расчета", L"Ошибка", MB_OK|MB_ICONSTOP);
      return 0;
   }

   wchar_t buf[100];
   wsprintf(buf, L"рек.заказ %d\n Установить?", qty / QTY_SCALE);

   if( MessageBox(buf, L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      SetScalingValue(IDC_QTY, qty, QTY_SCALE, true);
      data->qty = qty;
      ((CButton)GetDlgItem(IDC_PACK)).SetCheck(BST_UNCHECKED);
      SetSum(0,0,0,bHandled);
   }

   GetDlgItem(IDC_QTY).SetFocus();
   return 0;
}

LRESULT QTYDialog::ChangeCost(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   DWORD stopCost = (price.cost.size()>2) ? price.cost[2] : price.cost[0];
   CostDialog dlg(data->cost, price.cost[1], stopCost);
   if( dlg.DoModal() == IDOK )
   {
      if( dlg.cost < stopCost )
      //if( dlg.cost < pData.cost[1] )
      {
         MessageBox(L"Цена меньше минимальной на товар!", L"Ошибка", MB_OK|MB_ICONSTOP);
      } else
      {
         DWORD checkCost = CostManager::GetCost(price.id, data->costType);
         if( dlg.cost != checkCost )
            data->flags |= oiCustomCost;
         else
            data->flags &= (~oiCustomCost);

         data->cost = dlg.cost;
         SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);
         SetSum(0,0,0,bHandled);
         
         int nac = DivideInPack(data->cost, price.cost[0], 1000) - 1000;
         SetScalingValue(IDC_DISCOUNT_VALUE, nac, 10, false);
      }
   }
   return 0;
}

LRESULT QTYDialog::ChangeNac(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   if( data->cost != 0 )
   {
      int nac = DivideInPack(data->cost, price.cost[0], 1000) - 1000;
      CostDialog dlg(nac, 0, 0, 10);
      if( dlg.DoModal() == IDOK )
      {
         DWORD cost = price.cost[0];
         data->cost = cost + (DWORD)((__int64)cost * (dlg.cost * 10 + 5) / 10000); // чтобы шли ошибки округления

         DWORD checkCost = CostManager::GetCost(price.id, data->costType);
         if( data->cost != checkCost )
            data->flags |= oiCustomCost;
         else
            data->flags &= (~oiCustomCost);

         SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);
         SetSum(0,0,0,bHandled);
         SetScalingValue(IDC_DISCOUNT_VALUE, dlg.cost, 10, false);
      }
   }
   return 0;
}

LRESULT QTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   cost.SubclassWindow(GetDlgItem(IDC_COST));
   nac.SubclassWindow(GetDlgItem(IDC_DISCOUNT_VALUE));
   calc.SubclassWindow(GetDlgItem(IDC_CALC));

   price.id = (wchar_t*)data->id.c_str();
   price.Read();

   wchar_t buf[30];
   wsprintf(buf, L"упак x%d", price.qtyInPack/QTY_SCALE);
   GetDlgItem(IDC_PACK).SetWindowText(buf);

   int nac = (data->cost != 0) ? DivideInPack(data->cost, price.cost[0], 1000) - 1000 : 0;
   SetScalingValue(IDC_DISCOUNT_VALUE, nac, 10, false);

   OrgImpl org;
   org.id = (wchar_t*)data->orgID.c_str();
   org.Read();
   if( data->orderCreated.dwHighDateTime == 0 || (org.flags & ofCheckRest) == 0 )
   {
      GetDlgItem(IDC_REST_LABEL2).EnableWindow(FALSE);
      GetDlgItem(IDC_REST2).EnableWindow(FALSE);
   }

   if( data->orderCreated.dwHighDateTime != 0 && rmnts.Load(data->orgID.c_str(), data->orderCreated) )
   {
      OrgRemnantsItem* oi = rmnts.FindItem(data->id.c_str());
      if( oi != NULL )
      {
         SetScalingValue(IDC_REST2, oi->qty, QTY_SCALE, true);
      }
   }
   return 0;
}

LRESULT QTYDialog::SaveRmnts(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   bHandled = FALSE;
   wchar_t buf[50];

   GetDlgItem(IDC_REST2).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD qty = GetValue(buf, QTY_SCALE);

   rmnts.Update(data->id.c_str(), qty, false);

   return 0;
}

LRESULT QTYDialog::OnSizeChanged(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   CQTYDialog::OnSizeChanged(uMsg, wParam, lParam, bHandled);

   CRect rc, bounds;
   GetDlgItem(IDC_QTY).GetWindowRect(rc);
   ScreenToClient(rc);

   GetDlgItemRect(bounds, IDC_REST2);
   GetDlgItem(IDC_REST2).MoveWindow(rc.left, bounds.top, bounds.Width(), bounds.Height(), FALSE);

   calc.GetWindowRect(bounds);
   calc.MoveWindow(rc.right + offset, rc.top-1, bounds.Width(), bounds.Height(), FALSE);

   GetDlgItemRect(bounds, IDC_INPACK_LABEL);
   GetDlgItem(IDC_DISCOUNT).MoveWindow(bounds);

   GetDlgItemRect(bounds, IDC_INPACK);
   GetDlgItem(IDC_DISCOUNT_VALUE).MoveWindow(bounds);

   GetDlgItemRect(rc, IDC_QTY_LABEL);
   GetDlgItemRect(bounds, IDC_REST_LABEL2);
   GetDlgItem(IDC_REST_LABEL2).MoveWindow(rc.right - bounds.Width(), bounds.top, bounds.Width(), bounds.Height(), FALSE);

   return 0;
}

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

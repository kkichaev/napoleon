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
      static int ac[] = { IDC_MIN_LOAD, NULL };
      static int al[] = { IDC_MIN_LOAD_LABEL, NULL };

      addedCtrls = ac;
      addedLabels = al;
   }

   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(QTYDialog)
      //MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_HANDLER(IDC_COST_DSC, STN_CLICKED, SetCost)
      COMMAND_HANDLER(IDC_QTY, EN_CHANGE, SetSum)
      COMMAND_HANDLER(IDC_PACK, BN_CLICKED, SetSum)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(CQTYDialog)
   END_MSG_MAP()

protected:
   virtual DWORD GetStartQty(bool inPack, int qip) { return 0; }
   //virtual DWORD GetStartQty() { return 0 * QTY_SCALE; }

protected:
   DWORD    minCost;
   StaticAnchor discText;

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( wID == IDOK )
      {
         Preference p;
         p.Load();

         DWORD flags = p.flags;

         wchar_t buf[20];
         GetDlgItemText(IDC_QTY, buf, sizeof(buf)/sizeof(buf[0]));

         if(((CButton)GetDlgItem(IDC_PACK)).GetCheck() == BST_CHECKED)
            data->flags |= oiInPack;
         else
            data->flags &= (~oiInPack);

         DWORD val = GetValue(buf, QTY_SCALE);
         if( (data->flags & oiInPack) != 0 ) 
            val = (val * qtyInPack) / QTY_SCALE;
         
         data->qty = val;

         GetDlgItemText(IDC_COST_DSC, buf, sizeof(buf)/sizeof(buf[0]));
         data->cost = GetValue(buf, SUM_SCALE);

         if( flags & opfNoBelowZero )
         {
            int check = priceQty + prevQty;
            if( (int)data->qty > check )
            {
               if( priceQty < 0 ) data->qty = 0;
               else data->qty = check;

               if( flags & opfAlertBelowZero )
                  MessageBox(L"Количество в заказе уменьшено", L"Предупреждение", MB_OK);
            }
         }

         data->sum = data->cost * data->qty;
      }

      bHandled = FALSE;
      return 0;
   }

   LRESULT SetCost(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[20];
      GetDlgItemText(IDC_COST_DSC, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD dcost = GetValue(buf, SUM_SCALE);

      EnterNumber dlg;
      dlg.value = dcost;
      dlg.title = L"Цена";

      if( dlg.DoModal() == IDOK )
      {
         if( dlg.value < minCost )
         {
            if( MessageBox(L"Цена ниже минимальной! Изменить цену?", L"Вопрос", 
               MB_YESNO|MB_ICONQUESTION) != IDYES )
            {
               dlg.value = dcost;
            }
         }

         SetScalingValue(IDC_COST_DSC, dlg.value, SUM_SCALE, false);

         SetSum(code, id, hWnd, bHandled);
      }

      return 0;
   }

   LRESULT SetSum(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[20];
      GetDlgItemText(IDC_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      
      DWORD val = GetValue(buf, QTY_SCALE);
      if( (((CButton)GetDlgItem(IDC_PACK)).GetCheck() == BST_CHECKED) )
         val = (val * qtyInPack) / QTY_SCALE;

      GetDlgItemText(IDC_COST_DSC, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD dcost = GetValue(buf, SUM_SCALE);
      SetScalingValue(IDC_SUM, ItemSum(dcost, val), SUM_SCALE, false);
      return 0;
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      PriceImpl p;
      p.id = (wchar_t*)data->id.c_str();
      p.Read();

      SetScalingValue(IDC_REST, p.qty, QTY_SCALE, true);

      minCost = p.cost[1];
      SetScalingValue(IDC_MIN_LOAD, p.minPart, QTY_SCALE, false);

      int qip = p.qtyInPack;
      SetDlgItemInt(IDC_PLACE, DivideInPack(p.qty, qip, QTY_SCALE) / QTY_SCALE);
      SetDlgItemInt(IDC_ADD_QTY, ((qip!=0) ? (p.qty % qip) : p.qty) / QTY_SCALE);
      SetScalingValue(IDC_WEIGHT, p.weight, WEIGHT_SCALE, false);

      DWORD dcost;
      if( data->dcost == 0 )
         dcost = data->cost - (data->cost * data->discount / DISCOUNT_SCALE) / 100;
      else
         dcost = data->dcost;
      SetScalingValue(IDC_COST_DSC, dcost, SUM_SCALE, false);

      if( data->sum == 0 )
         SetSum(0, 0, 0, bHandled);
      else
         SetScalingValue(IDC_SUM, data->sum, SUM_SCALE, false);


      discText.SubclassWindow(GetDlgItem(IDC_COST_DSC));

      return FALSE;
   }

   //LRESULT OnSizeChanged(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   //{
   //   CQTYDialog::OnSizeChanged(uMsg, wParam, lParam, bHandled);

   //   CRect bounds;
   //   // определяем границу для контролов по кнопке 1
   //   GetDlgItemRect(bounds, IDC_DIG_1);
   //   int shift = bounds.left;

   //   // выравниваем контролы по границе кнопки 1
   //   int ctrls[] = { IDC_MIN_LOAD }; 
   //   for( i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
   //      ShiftXDlgItem(shift, ctrls[i], false);

   //   // теперь выравниваем метки по правой границе
   //   int lblCtrls[] = { IDC_MIN_LOAD_LABEL }; 
   //   for( i=0; i<sizeof(lblCtrls)/sizeof(lblCtrls[0]); i++ )
   //   {
   //      CWindow wnd(GetDlgItem(lblCtrls[i]));
   //      wnd.GetWindowRect(bounds);
   //      ScreenToClient(bounds);
   //      bounds.OffsetRect(shift - 4 - bounds.right, 0);
   //      wnd.MoveWindow(bounds);
   //   }

   //   return 0;

   //   //WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   //   //CRect bounds;
   //   //GetDlgItemRect(bounds, IDC_QTY);      

   //   //CRect nameBounds;
   //   //GetDlgItemRect(nameBounds, IDC_ITEM_NAME);
   //   //GetDlgItem(IDC_ITEM_NAME).MoveWindow(offset, nameBounds.top, wdh-2*offset,  bounds.top-offset-nameBounds.top, FALSE);
   //   //GetDlgItemRect(bounds, IDC_QTY_LABEL);
   //   //
   //   //int shift = wdh/2 - bounds.left;
   //   //int lpshift = offset - nameBounds.left;

   //   //int shiftCtrls[] = { IDC_REST_LABEL, IDC_COST_LABEL, IDC_INPACK_LABEL, IDC_COST_DSC_LABEL, 
   //   //   IDC_SALES_HIST, IDC_PLACE_LABEL, IDC_WEIGHT_LABEL };
   //   //for( int i=0; i<sizeof(shiftCtrls)/sizeof(shiftCtrls[0]); i++ )
   //   //   ShiftXDlgItem(offset, shiftCtrls[i], false);

   //   //int shiftCtrls2[] = { IDC_REST, IDC_COST, IDC_INPACK, IDC_COST_DSC, IDC_PLACE, IDC_ADD_LABEL, 
   //   //   IDC_ADD_QTY, IDC_WEIGHT };
   //   //for( int i=0; i<sizeof(shiftCtrls2)/sizeof(shiftCtrls2[0]); i++ )
   //   //   ShiftXDlgItem(lpshift, shiftCtrls2[i]);

   //   //int ctrls[] = { IDC_QTY_LABEL, IDC_QTY, IDC_SUM_LABEL, IDC_SUM, IDC_PACK, IDC_MIN_LOAD_LABEL,
   //   //   IDC_MIN_LOAD };
   //   //for( int i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
   //   //   ShiftXDlgItem(shift, ctrls[i]);

   //   //int digBorder;
   //   //if( IsSquareScreen() )
   //   //{
   //   //   GetDlgItem(IDC_DIG_1).GetWindowRect(bounds);
   //   //   ScreenToClient(bounds);
   //   //   digBorder = bounds.left - offset;
   //   //} else
   //   //   digBorder = wdh;

   //   //qtyText.GetWindowRect(bounds);
   //   //ScreenToClient(bounds);
   //   //bounds.right = digBorder;
   //   //qtyText.MoveWindow(bounds);

   //   //MoveButtons(wdh, hgh);
   //   //return 0;
   //}
};


bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDialog dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}


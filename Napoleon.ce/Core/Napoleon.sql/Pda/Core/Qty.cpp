/*
 * Copyright (C), 2006-2008, Денис Мосягин
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

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include <MainFrame.h>
#include <Table.h>

#ifdef ORD_ITEM_DISCOUNT
class DiscountDialog : public CSimpleDialog<IDD_ENTER_COST, TRUE> 
{
public:
   DiscountDialog(int cost, DWORD minCost, WORD scale = SUM_SCALE) : numInput(IDC_COST)
   { this->cost = cost; this->minCost = minCost; this->scale = scale; }

   typedef CSimpleDialog<IDD_ENTER_COST, TRUE> BaseClass;

   BEGIN_MSG_MAP(DiscountDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()
 
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      CEdit ed(GetDlgItem(IDC_COST));
      ed.LimitText(4);
      ed.SetFocus();

      CComboBox cb(GetDlgItem(IDC_DISCOUNT));
      cb.AddString(L"скид.");
      cb.AddString(L"нац.");

      if( cost <= 0 )
      {
         cost = -cost;
         cb.SetCurSel(0);
      } else
         cb.SetCurSel(1);

#ifdef Alcom
      cb.EnableWindow(FALSE);
#endif

      SetScalingValue(IDC_COST, cost, scale, false);

      if( minCost != 0 )
         SetScalingValue(IDC_COST1, minCost, SUM_SCALE, false);
      else
      {
         GetDlgItem(IDC_COST1).ShowWindow(SW_HIDE);
         GetDlgItem(IDC_COST_LABEL).ShowWindow(SW_HIDE);
      }
      return TRUE;
   }

   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[50];

      GetDlgItem(IDC_COST).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
      cost = GetValue(buf, scale);

      CComboBox cb(GetDlgItem(IDC_DISCOUNT));
      if( cb.GetCurSel() == 0 ) cost = -cost;

      bHandled = FALSE;
      return 0;
   }

   NumInput numInput;
   int cost;
   DWORD minCost;
   WORD scale;

   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, scale);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
      SetDlgItemText(id, buf);
   }
 };
#endif

#ifdef Kirov_Pavel
class HistoryDialog : public BaseDialog
{
public:
   HistoryDialog(const std::vector<ItemSales> &_sales) : BaseDialog(IDD_HIST, SHIDIF_DONEBUTTON),
      sales(_sales) 
      {
      }

   typedef BaseDialog BaseClass;   

   BEGIN_MSG_MAP(HistoryDalog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT SetCellInfo(LPNMHDR hdr)
   {
      NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
      if( !(di->item.mask & LVIF_TEXT) )
         return TRUE;

      int index = di->item.iItem;
      if( index < (int)sales.size() )
      {
         if( di->item.iSubItem == 0 )
         {
            SYSTEMTIME st;
            FileTimeToSystemTime(&sales[index].date, &st);
            wsprintf(di->item.pszText, L"%02d.%02d.%d", st.wDay, st.wMonth, st.wYear);
         } else
         {
            wchar_t buf[20];
            ConvertScaling(buf, sales[index].qty, QTY_SCALE);
            FormatScaling(buf, di->item.pszText, di->item.cchTextMax, 
               sales[index].qty % QTY_SCALE, QTY_SCALE, true);
         }
      }
      return TRUE;
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      CListViewCtrl lv(GetDlgItem(IDC_HIST_LIST));

      LVCOLUMN lvc = {0};
      lvc.mask = LVCF_FMT | LVCF_WIDTH | LVCF_TEXT | LVCF_SUBITEM;
      lvc.fmt = LVCFMT_LEFT;
      lvc.pszText = L"Дата";
      lvc.cx = 100;
      lvc.iSubItem = 0;
      lv.InsertColumn(0, &lvc);

      lvc.pszText = L"Кол-во";
      lvc.cx = 100;
      lvc.iSubItem = 1;
      lv.InsertColumn(1, &lvc);

      lv.SetItemCount(sales.size());
      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      CRect bounds;
      CListViewCtrl lv(GetDlgItem(IDC_HIST_LIST));
      lv.GetWindowRect(bounds);
      ScreenToClient(bounds);

      bounds.left = 0;
      bounds.right = wdh;
      bounds.bottom = hgh;

      wdh = bounds.Width() - GetSystemMetrics(SM_CXVSCROLL) - 2;
      lv.MoveWindow(bounds);
      lv.SetColumnWidth(0, wdh/2);
      lv.SetColumnWidth(1, wdh/2);

      return 0;
   }

   const std::vector<ItemSales> &sales;
};

#endif

#ifdef SHOW_OFF_TAKE
const DWORD QTY_FLAGS = SHIDIF_FULLSCREENNOMENUBAR;
#else
const DWORD QTY_FLAGS = DEFAULT_FLAGS;
#endif

CQTYDialog::CQTYDialog(QTYData *_data) : BaseDialog(IDD_QTY, QTY_FLAGS), data(_data), inHistory(false), numInput(IDC_QTY)
#ifdef PRICE_MOVER
      ,next((GetSystemMetrics(SM_CXSMICON)>16) ? IDC_NEXT32 : IDC_NEXT) 
      ,prev((GetSystemMetrics(SM_CXSMICON)>16) ? IDC_PREV32 : IDC_PREV)
#endif
{
   flags &= (~ShowSIP);

   addedCtrls = NULL;
   addedLabels = NULL;
}

CQTYDialog::CQTYDialog(QTYData *_data, int formID) : BaseDialog(formID, QTY_FLAGS), data(_data), inHistory(false), numInput(IDC_QTY)
#ifdef PRICE_MOVER
      ,next((GetSystemMetrics(SM_CXSMICON)>16) ? IDC_NEXT32 : IDC_NEXT) 
      ,prev((GetSystemMetrics(SM_CXSMICON)>16) ? IDC_PREV32 : IDC_PREV)
#endif
{
   flags &= (~ShowSIP);

   addedCtrls = NULL;
   addedLabels = NULL;
}

LRESULT CQTYDialog::OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   numInput.SetTargetControl(wID);
   return 0;
}

bool CQTYDialog::CheckQty()
{
   Preference p;
   p.Load();
   DWORD flags = p.flags;

   int check = priceQty + prevQty;
   if( p.flags & opfNoBelowZero )
   {
      if( (int)data->qty > check )
      {
         if( priceQty < 0 ) data->qty = 0;
         else data->qty = check;

         if( flags & opfAlertBelowZero )
            MessageBox(L"Количество в заказе уменьшено", L"Предупреждение", MB_OK);
      }
   } else
   {
      if( (int)data->qty > check )
      {
         if( flags & opfAlertBelowZero )
            MessageBox(L"Остаток товара на складе меньше нуля!", L"Предупреждение", MB_OK);
      }
   }

   return true;
}

LRESULT CQTYDialog::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   if( wID == IDOK )
   {
      if( !UpdateQTY() )
         return 0;

      SaveData();
   }

   bHandled = FALSE;
   return 0;
}

LRESULT CQTYDialog::ShowHistory(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   inHistory = true;
#ifdef Kirov_Pavel
   HistoryDialog dlg(data->sales);
   dlg.DoModal();
#endif
   inHistory = true;
   return 0;
}

LRESULT CQTYDialog::SetSum(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   if( code == BN_CLICKED )
      numInput.SetTargetControl(IDC_QTY);

   wchar_t buf[20];
   GetDlgItemText(IDC_QTY, buf, sizeof(buf));
   
   DWORD val = GetValue(buf, QTY_SCALE);
   if( (((CButton)GetDlgItem(IDC_PACK)).GetCheck() == BST_CHECKED) )
      val = MulInPack(val, qtyInPack, QTY_SCALE);

   SetScalingValue(IDC_SUM, ItemSum(data->cost, val), SUM_SCALE, false);
   if( code == BN_CLICKED )
      GetDlgItem(IDC_QTY).SetFocus();
   return 0;
}

bool CQTYDialog::UpdateQTY()
{
   bool res = true;
   wchar_t buf[20];
   GetDlgItemText(IDC_QTY, buf, sizeof(buf)/sizeof(buf[0]));

   if(((CButton)GetDlgItem(IDC_PACK)).GetCheck() == BST_CHECKED)
      data->flags |= oiInPack;
   else
      data->flags &= (~oiInPack);

   DWORD val = GetValue(buf, QTY_SCALE);
   if( (data->flags & oiInPack) ) 
      val = MulInPack(val, qtyInPack, QTY_SCALE);
   
   data->qty = val;

#ifdef Alians
   if( data->showRemnants )
   {
      GetDlgItemText(IDC_REMNANTS_ENTER, buf, sizeof(buf)/sizeof(buf[0]));
      data->remnants = GetValue(buf, QTY_SCALE);
   }
#endif

   if( (data->flags & oiNoCheckWHQty) == 0 )
      res = CheckQty();

   data->sum = data->cost * data->qty;

#ifdef SHOW_OFF_TAKE
   if( (data->flags & oiHideRemnants) == 0 )
   {
      GetDlgItemText(IDD_REMNANTS_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      data->remnants = GetValue(buf, QTY_SCALE);
   } 
#endif

   return res;
}

void CQTYDialog::SetData(const PriceImpl& priceItem)
{
   prevQty = data->qty;
   qtyInPack = QTYInPack(priceItem);

   priceQty = PriceQty(priceItem);


#if !defined(Alians) && !defined(PRICE_MOVER)
   if( data->qty == 0 )
   {
#ifdef SHOW_OFF_TAKE
      //if( data->sales.size() > 1 )
      //   data->qty = data->sales[0].qty;
      //else
      data->qty = GetStartQty(((data->flags & oiInPack) != 0), qtyInPack);
#else
      data->qty = GetStartQty(((data->flags & oiInPack) != 0), qtyInPack);
#endif
   }
#endif

   if( data->sum == 0 )
      data->sum = ItemSum(data->cost, data->qty);

   if( (data->flags & oiInPack) != 0 )
      data->qty = DivideInPack(data->qty, qtyInPack, QTY_SCALE);

   CButton button(GetDlgItem(IDC_PACK));
   button.SetCheck(((data->flags & oiInPack) != 0) ? BST_CHECKED : BST_UNCHECKED);

#ifdef Agama
   std::wstring name(priceItem.id);
   name += L' ';
   name += priceItem.name;
   SetDlgItemText(IDC_ITEM_NAME, name.c_str());
#else
   SetDlgItemText(IDC_ITEM_NAME, priceItem.name);
#endif

#ifdef Alians
   if( !data->showRemnants )
   {
      GetDlgItem(IDC_REMNANTS_ENTER).ShowWindow(SW_HIDE);
      GetDlgItem(IDC_REMNANTS_LABEL).ShowWindow(SW_HIDE);
      GetDlgItem(IDC_ADD_REMNANTS).ShowWindow(SW_HIDE);
      GetDlgItem(IDC_DEL_REMNANTS).ShowWindow(SW_HIDE);
   } else
   {
      SetScalingValue(IDC_REMNANTS_ENTER, data->remnants, QTY_SCALE, true);
      GetDlgItem(IDC_REMNANTS_ENTER).SetFocus();
   }
#endif

#ifdef ORD_ITEM_DISCOUNT
   int discount = data->discount;
   if( discount < 0 )
      GetDlgItem(IDC_DISCOUNT).SetWindowText(L"скид.,%");
   else if( discount > 0 )
      GetDlgItem(IDC_DISCOUNT).SetWindowText(L"нац.,%");
   SetScalingValue(IDC_DISCOUNT_VALUE, abs(discount), DISCOUNT_SCALE, false);
#endif

   SetScalingValue(IDC_INPACK, qtyInPack, QTY_SCALE, true);
   SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);
   
   Preference pref;
   pref.Load();

   int pQty = PriceQty(priceItem);

   if( pref.flags & ppfBoxQty )
   {
      wchar_t buf[20], src[20];

      long val;
      val = DivideInPack(pQty, qtyInPack, QTY_SCALE);

      DWORD rest = abs(val) % QTY_SCALE;
      if( rest != 0 )
      {
         int n = val / QTY_SCALE;
         val = n * QTY_SCALE;
      }

      ConvertScaling(src, val, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), 0, QTY_SCALE, true);
      wcscat(buf, L"у");

      SetDlgItemText(IDC_REST, buf);
   } else
      SetScalingValue(IDC_REST, pQty, QTY_SCALE, true);

#ifdef SHOW_OFF_TAKE
   if( pref.addFlags & afHideRestQTY )
   {
      GetDlgItem(IDC_REMNANTS_LABEL).ShowWindow(SW_HIDE);
      GetDlgItem(IDD_REMNANTS_QTY).ShowWindow(SW_HIDE);
   }
   if( data->qty != 0 )
#endif
   SetScalingValue(IDC_QTY, data->qty, QTY_SCALE, true);
   SetScalingValue(IDC_SUM, data->sum, SUM_SCALE, false);

   SetSalesInfo();
}

void CQTYDialog::SetData(bool setQtyFocus)
{
   PriceImpl pData;
   pData.id = (wchar_t*)data->id.c_str();
   if( !pData.Read() ) return;

   SetData(pData);

   if( data->canChange == false )
      DisableChilds();
   else
   {
      CEdit qty(GetDlgItem(IDC_QTY));
      qty.SetLimitText(6);

      qty.SetSelAll();
      if( setQtyFocus ) qty.SetFocus();
   }
}

//void CQTYDialog::OnPaint(HDC)
//{
//   PAINTSTRUCT pPaint;
//   HDC dc = BeginPaint(&pPaint);
//
//   int wdh = 24; //GetSystemMetrics(SM_CXSMICON);
//   HIMAGELIST il = ImageList_LoadImage(_Module.GetModuleInstance(), 
//      MAKEINTRESOURCE(IDB_QTY), wdh, 1, CLR_NONE, IMAGE_BITMAP, 0);
//
//   CRect rc, rc2;
//   GetDlgItem(IDOK).GetWindowRect(rc);
//   GetDlgItem(IDC_DIG_PT).GetWindowRect(rc2);
//   int gap = rc2.left - rc.right;
//   ScreenToClient(rc);
//
//   ImageList_Draw(il, 0, dc, rc.right + (gap - wdh) / 2, rc.top + (rc.Height()-wdh) / 2, ILD_NORMAL);
//   ImageList_Destroy(il);
//   EndPaint(&pPaint);
//}

#ifdef ORD_ITEM_DISCOUNT
void MoveDown(CWindow* parent, int *ctrls, int qty, UINT id1, UINT id2)
{
   CRect r;
   int offset = 0;

   parent->GetDlgItem(id1).GetWindowRect(r);
   offset = r.top;
   parent->GetDlgItem(id2).GetWindowRect(r);
   offset = abs(r.top - offset);

   for( int i=0; i<qty; i++ )
   {
      CWindow c(parent->GetDlgItem(ctrls[i]));
      c.GetWindowRect(r);

      r.top += offset;
      r.bottom += offset;
      parent->ScreenToClient(r);
      c.MoveWindow(r);
   }
}

LRESULT CQTYDialog::ChangeNac(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   DiscountDialog dlg(data->discount, 0, DISCOUNT_SCALE);

   if( dlg.DoModal() == IDOK && CanSetDiscount(dlg.cost))
   {
      int discount = dlg.cost;

      data->discount = discount;

      wchar_t *text = ( discount <= 0 ) ? L"скид.,%" : L"нац.,%";
      GetDlgItem(IDC_DISCOUNT).SetWindowText(text);

      //DWORD dc = data->itemCost + (int)((__int64)data->itemCost * discount / DISCOUNT_SCALE) / SUM_SCALE;
		int sign = (discount < 0) ? -1 : 1;
		DWORD dc = data->itemCost + (int)(((__int64)data->itemCost * discount + sign * DISCOUNT_SCALE * SUM_SCALE / 2) / (DISCOUNT_SCALE * SUM_SCALE));
      data->cost = dc;

      SetScalingValue(IDC_DISCOUNT_VALUE, abs(discount), DISCOUNT_SCALE, false);
      SetScalingValue(IDC_COST, data->cost, SUM_SCALE, false);

      SetSum(0, 0, hWnd, bHandled);
   }

   return 0;
}
#endif

LRESULT CQTYDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

#ifdef SHOW_OFF_TAKE
   if( (data->flags & oiHideRemnants) != 0 )
   {
      GetDlgItem(IDC_REMNANTS_LABEL).ShowWindow(SW_HIDE);
      GetDlgItem(IDD_REMNANTS_QTY).ShowWindow(SW_HIDE);
      GetDlgItem(IDC_PREV_QTY).ShowWindow(SW_HIDE);
      GetDlgItem(IDC_SALES_HIST).ShowWindow(SW_HIDE);
   } else
      SetScalingValue(IDD_REMNANTS_QTY, data->remnants, QTY_SCALE, true);
#endif

   qtyText.SubclassWindow(GetDlgItem(IDC_PREV_QTY));
   qtyText.SetBkColor(GetSysColor(COLOR_BTNFACE));

#ifdef Kirov_Pavel
   histText.SubclassWindow(GetDlgItem(IDC_SALES_HIST));
#endif

#ifdef ORD_ITEM_DISCOUNT
   int ctrls[] = { IDC_INPACK_LABEL, IDC_INPACK, IDC_PREV_QTY, IDC_SALES_HIST };
   MoveDown(this, ctrls, sizeof(ctrls)/sizeof(ctrls[0]), IDC_DISCOUNT, IDC_COST_LABEL);
   nac.SubclassWindow(GetDlgItem(IDC_DISCOUNT_VALUE));
#endif

   SetData(false);

#ifdef PRICE_MOVER
   flags &= (~ShowSIP);

   CWindow nw(GetDlgItem(IDC_NEXT)), pw(GetDlgItem(IDC_PREV));
   if( data->mover != NULL )
   {
      next.SubclassWindow(nw);
      prev.SubclassWindow(pw);
   } else
   {
      nw.ShowWindow(SW_HIDE);
      pw.ShowWindow(SW_HIDE);
   }
#endif

   //if( SHOW_QTY_KEYBOARD == false && GetSystemMetrics(SM_CXSCREEN) < GetSystemMetrics(SM_CYSCREEN) )
   //   numInput.Show(*this, SW_HIDE);

   return TRUE;
}

void CQTYDialog::RightAlign(int rightPos, int ctrl)
{
   CRect bounds;
   CWindow wnd(GetDlgItem(ctrl));

   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);
   bounds.OffsetRect(rightPos - bounds.right, 0);
   wnd.MoveWindow(bounds);
}

LRESULT CQTYDialog::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   if( inHistory )
      return 0;

   CRect bounds;
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);
   GetDlgItemRect(bounds, IDC_REST);      

   CRect nameBounds;
   GetDlgItemRect(nameBounds, IDC_ITEM_NAME);
   GetDlgItem(IDC_ITEM_NAME).SetWindowPos(HWND_BOTTOM, offset, nameBounds.top, wdh-2*offset, bounds.top - 2*offset - nameBounds.top, 0);

   //GetDlgItem(IDC_ITEM_NAME).MoveWindow(offset, nameBounds.top, wdh-2*offset,  bounds.top-2*offset-nameBounds.top, FALSE);
   
   // Move right-side controls
#ifdef SHOW_OFF_TAKE
   int i;
   // сдвиг определим по растоянию кнопки 0 от границы экрана
   GetDlgItemRect(bounds, IDC_DIG_0);
   int shift = wdh - offset - bounds.right;

   //сдвигаем цифровую клавиатуру
   int digCtrls[] = { IDC_DIG_0,  IDC_DIG_1, IDC_DIG_2, IDC_DIG_3, IDC_DIG_4, IDC_DIG_5, IDC_DIG_6, IDC_DIG_7, IDC_DIG_8, IDC_DIG_9,
      IDC_DIG_PT, IDC_DIG_BS  };
   for( i=0; i<sizeof(digCtrls)/sizeof(digCtrls[0]); i++ )
      ShiftXDlgItem(shift, digCtrls[i]);

   // определяем границу для контролов по кнопке 1
   GetDlgItemRect(bounds, IDC_DIG_1);
   shift = bounds.left;

   // выравниваем контролы по границе кнопки 1
   int ctrls[] = { IDC_QTY, IDC_SUM, IDC_PACK, IDD_REMNANTS_QTY }; 
   for( i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
      ShiftXDlgItem(shift, ctrls[i], false);

   if( addedCtrls )
   {
      for( i=0; addedCtrls[i]; i++ )
         ShiftXDlgItem(shift, addedCtrls[i], false);
   }

   // теперь выравниваем метки по правой границе
   shift -= 4;
   int lblCtrls[] = { IDC_QTY_LABEL, IDC_SUM_LABEL, IDC_REMNANTS_LABEL }; 
   for( i=0; i<sizeof(lblCtrls)/sizeof(lblCtrls[0]); i++ )
      RightAlign(shift, lblCtrls[i]);

   if( addedLabels )
   {
      for( i=0; addedLabels[i]; i++ )
         RightAlign(shift, addedLabels[i]);
   }


   CWindow histLabel(GetDlgItem(IDC_SALES_HIST));
   if( GetSystemMetrics(SM_CXSCREEN) < GetSystemMetrics(SM_CYSCREEN) ) // portrait
   {
      GetDlgItemRect(bounds, IDC_DIG_7);
      int refY = bounds.bottom + 3;

      qtyText.GetWindowRect(bounds);
      ScreenToClient(bounds);
      int hgh = bounds.Height();
      bounds.left = offset;
      bounds.right = wdh - offset;
      bounds.top = refY;
      bounds.bottom = hgh + bounds.top;
      qtyText.MoveWindow(bounds);

      histLabel.GetWindowRect(bounds);
      ScreenToClient(bounds);
      bounds.MoveToY(refY - bounds.Height() - offset);
      histLabel.MoveWindow(bounds);
   } else // landscape & square
   {
      CRect qbounds;
      GetDlgItemRect(bounds, IDC_DIG_1);
      qtyText.GetWindowRect(qbounds);
      ScreenToClient(qbounds);

      int hgh = qbounds.Height();
      qbounds.left = offset;
      qbounds.right = bounds.left - offset;
      qbounds.top = bounds.top;
      qbounds.bottom = qbounds.top + hgh;
      qtyText.MoveWindow(qbounds);

      histLabel.GetWindowRect(qbounds);
      ScreenToClient(qbounds);
      qbounds.MoveToY(bounds.top - qbounds.Height() - offset);
      histLabel.MoveWindow(qbounds);
   }

#else
   GetDlgItemRect(bounds, IDC_QTY_LABEL);
#ifdef Fusion
   int shift = wdh/2 - bounds.left - 10;
#else
   int shift = wdh/2 - bounds.left;
#endif

   int ctrls[] = { IDC_QTY_LABEL, IDC_QTY, IDC_SUM_LABEL, IDC_SUM, IDC_PACK, IDC_DIG_0, 
      IDC_DIG_1, IDC_DIG_2, IDC_DIG_3, IDC_DIG_4, IDC_DIG_5, IDC_DIG_6, IDC_DIG_7, IDC_DIG_8, IDC_DIG_9,
      IDC_DIG_PT, IDC_DIG_BS  };

   for( int i=0; i<sizeof(ctrls)/sizeof(ctrls[0]); i++ )
      ShiftXDlgItem(shift, ctrls[i]);

   int digBorder;
   CWindow dig1(GetDlgItem(IDC_DIG_1));
   //if( GetSystemMetrics(SM_CXSCREEN) >= GetSystemMetrics(SM_CYSCREEN)/*dig1.IsWindowVisible() == TRUE*/ )
   if( (dig1.GetStyle() & WS_VISIBLE) != 0 )
   {
      dig1.GetWindowRect(bounds);
      ScreenToClient(bounds);
      digBorder = bounds.left - offset;
   } else
      digBorder = wdh;

   qtyText.GetWindowRect(bounds);
   ScreenToClient(bounds);
   bounds.right = digBorder;
   qtyText.MoveWindow(bounds);
#endif

   MoveButtons(wdh, hgh);

#ifdef PRICE_MOVER
   CRect b2;

   GetDlgItemRect(bounds, IDOK);      
   GetDlgItemRect(b2, IDCANCEL);      
   int woffset = bounds.right + 2;//bounds.left - b2.left - b2.Width();

   CWindow pb(GetDlgItem(IDC_PREV));
   pb.GetWindowRect(b2);
   ScreenToClient(b2);
   //int cx = bounds.left + bounds.Width() + woffset;
   pb.MoveWindow(woffset, bounds.top, b2.Width(), bounds.Height());

   //cx += b2.Width() + woffset;
   GetDlgItem(IDC_NEXT).MoveWindow(woffset + b2.Width(), bounds.top, b2.Width(), bounds.Height());
#endif

   MoveControls(wdh, hgh);

   return 0;
}

#ifdef PRICE_MOVER
LRESULT CQTYDialog::ChangeItem(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->mover != NULL )
   {
      if( UpdateQTY() )
      {
         if( data->mover->Move(data, (id == IDC_NEXT)) )
            SetData();
         else
            GetDlgItem(IDC_QTY).SetFocus();
      }
   }
   return 0;
}
#endif

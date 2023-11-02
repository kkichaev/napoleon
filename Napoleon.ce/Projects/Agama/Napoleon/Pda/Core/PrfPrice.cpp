/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   20/06/2008   creating
 */ 
#include "stdafx.h"
#include "PrfDlg.h"
#include "NplConfig.h"
#include <Form.h>

//
// ---------------------- Price Properties ------------------
//
PriceProperties::PriceProperties(bool _adminPreference) : 
   PrefPage(IDC_PRICE_PREFERENCE_ADD, L"Прайс-лист"), 
   adminPreference(_adminPreference)
{
}

LRESULT PriceProperties::CheckColumn3(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
{
   int curSel = ((CComboBox)GetDlgItem(IDC_COLUMN2)).GetCurSel();
   GetDlgItem(IDC_COLUMN3).EnableWindow((curSel != pcfNone));
   return 0;
}

void PriceProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   CComboBox column2(GetDlgItem(IDC_COLUMN2));
   CComboBox column3(GetDlgItem(IDC_COLUMN3));

   for( int i=0; i<PRICE_COLUMN_FIELDS; i++ )
   {
      column2.InsertString(i, priceFieldTitle[i]);
      column3.InsertString(i, priceFieldTitle[i]);
   }

   column2.SetCurSel(preference.priceColumn2);
   column3.SetCurSel(preference.priceColumn3);

   column3.EnableWindow(column2.GetCurSel() != pcfNone);

   if( preference.flags & ppfBoxQty )
      CheckDlgButton(IDC_BOX_QTY, BST_CHECKED);
   if( preference.flags & opfNoBelowZero )
      CheckDlgButton(IDC_BLW_ZERO, BST_CHECKED);
   if( preference.flags & opfAlertBelowZero )
      CheckDlgButton(IDC_ALERT_BLW, BST_CHECKED);
   if( preference.flags & apfSortNoCase )
      CheckDlgButton(IDC_SORT_NO_CASE, BST_CHECKED);

   if( preference.flags & ppfSelectLastOrder )
      CheckDlgButton(IDC_SEL_LAST_ORDER, BST_CHECKED);

   if( preference.addFlags & afAutoVisit )
      CheckDlgButton(IDD_VISIT, BST_CHECKED);

   CComboBox onLoad(GetDlgItem(IDC_ORIENTATION));
   onLoad.AddString(L"ничего не делать");
   onLoad.AddString(L"развернуть вертикально");
   onLoad.AddString(L"развернуть горизонтально");

   if( (preference.flags & apfPortrait) != 0 ) onLoad.SetCurSel(1);
   else if( (preference.flags & apfLandscape) != 0 ) onLoad.SetCurSel(2);
   else onLoad.SetCurSel(0);

   NapoleonConfig cfg;
   std::wstring val;
   if( !cfg.ReadValue(&val, WH_QTY) ) return;

   CComboBox wh(GetDlgItem(IDC_WAREHOUS));
   int ctr = 0;      
   std::wstring::size_type off = 0, nextOff;
   while( true )
   {
      nextOff = val.find(SEP_SYM, off);
      int pos = wh.AddString(val.substr(off, (nextOff != std::wstring::npos) ? 
                                        nextOff - off : std::wstring::npos).c_str());
      if( preference.whDefault == ctr )
         wh.SetCurSel(pos);

      wh.SetItemData(pos, ctr++);
      if( nextOff == std::wstring::npos )
         break;
      off = nextOff + 1;
   }
}

void PriceProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   if( IsDlgButtonChecked(IDC_BLW_ZERO) == BST_CHECKED ) preference->flags |= opfNoBelowZero;
   else preference->flags &= (~opfNoBelowZero);

   if( IsDlgButtonChecked(IDC_ALERT_BLW) == BST_CHECKED ) preference->flags |= opfAlertBelowZero;
   else preference->flags &= (~opfAlertBelowZero);

   if( IsDlgButtonChecked(IDC_SEL_LAST_ORDER) == BST_CHECKED ) preference->flags |= ppfSelectLastOrder;
   else preference->flags &= (~ppfSelectLastOrder);

   if( IsDlgButtonChecked(IDC_BOX_QTY) == BST_CHECKED ) preference->flags |= ppfBoxQty;
   else preference->flags &= (~ppfBoxQty);

   if( IsDlgButtonChecked(IDC_SORT_NO_CASE) == BST_CHECKED ) preference->flags |= apfSortNoCase;
   else preference->flags &= (~apfSortNoCase);

   if( IsDlgButtonChecked(IDD_VISIT) == BST_CHECKED ) preference->addFlags |= afAutoVisit;
   else preference->addFlags &= (~afAutoVisit);

   CComboBox column2(GetDlgItem(IDC_COLUMN2));
   CComboBox column3(GetDlgItem(IDC_COLUMN3));

   preference->priceColumn2 = (PriceColumnField)column2.GetCurSel();
   preference->priceColumn3 = (PriceColumnField)column3.GetCurSel();

   CComboBox wh(GetDlgItem(IDC_WAREHOUS));
   int cs = wh.GetCurSel();
   if( cs >= 0 )
      preference->whDefault = wh.GetItemData(cs);

   CComboBox onLoad(GetDlgItem(IDC_ORIENTATION));
   cs = onLoad.GetCurSel();
   
   preference->flags &= (~(apfPortrait | apfLandscape));
   if( cs == 1 ) preference->flags |= apfPortrait;
   else if( cs == 2 ) preference->flags |= apfLandscape;
}



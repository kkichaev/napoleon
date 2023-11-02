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
   PrefPage(IDC_PRICE_PREFERENCE, L"Прайс-лист"), 
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

#ifdef FIRM_DEFAULT
   CComboBox firms(GetDlgItem(IDC_FIRMS));
   NapoleonConfig config;
   std::wstring val;
   config.ReadValue(&val, SUPPL_TYPE);

   std::wstring::size_type sp = 0;
   for( int i=0; ; i++ )
   {
      std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
      if( ep == std::wstring::npos )
         firms.InsertString(i, val.substr(sp, ep).c_str());
      else
         firms.InsertString(i, val.substr(sp, ep - sp).c_str());

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }

   if( preference.defaultFirm >= 0 )
      firms.SetCurSel(preference.defaultFirm);
#endif

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

   CComboBox column2(GetDlgItem(IDC_COLUMN2));
   CComboBox column3(GetDlgItem(IDC_COLUMN3));

   preference->priceColumn2 = (PriceColumnField)column2.GetCurSel();
   preference->priceColumn3 = (PriceColumnField)column3.GetCurSel();

#ifdef FIRM_DEFAULT
   CComboBox firms(GetDlgItem(IDC_FIRMS));
   int cs = firms.GetCurSel();
   if( cs >= 0 )
      preference->defaultFirm = cs;
#endif
}



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

   if( preference.flags & ppfSelectLastOrder )
      CheckDlgButton(IDC_SEL_LAST_ORDER, BST_CHECKED);

   SetDlgItemInt(IDC_PRICE_NUM, preference.priceLines, FALSE);

   NapoleonConfig config;
   std::wstring val;

   DWORD offTakeCoef = 150;
   if( config.ReadValue(&val, OFFTAKE_COEF) )
      offTakeCoef = _wtoi(val.c_str());
   SetScalingValue(IDC_COEF, offTakeCoef, SUM_SCALE, false);

   if( config.ReadValue(&val, SUPPL_TYPE) )
   {
      CComboBox firms(GetDlgItem(IDC_FIRMS));

      std::wstring::size_type sp = 0;
      for( int i=0; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

         std::wstring::size_type sepSym = tval.find(L'\t');

         int index = firms.AddString(tval.substr(0, sepSym).c_str());
        
         if( sepSym != std::wstring::npos )
         {
            const wchar_t *code = sh.Add(tval.substr(sepSym + 1).c_str());
            firms.SetItemData(index, (DWORD)code);
            if( wcscmp(preference.defaultFirm, code) == 0 )
               firms.SetCurSel(index);
         }

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
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

   CComboBox column2(GetDlgItem(IDC_COLUMN2));
   CComboBox column3(GetDlgItem(IDC_COLUMN3));

   preference->priceColumn2 = (PriceColumnField)column2.GetCurSel();
   preference->priceColumn3 = (PriceColumnField)column3.GetCurSel();

   preference->priceLines = GetDlgItemInt(IDC_PRICE_NUM, NULL, FALSE);

   wchar_t buf[50];
   GetDlgItem(IDC_COEF).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD offTakeCoef = (WORD)GetValue(buf, SUM_SCALE); // масштабируем и записываем
   wsprintf(buf, L"%d", offTakeCoef);
   NapoleonConfig config;
   config.WriteValue(OFFTAKE_COEF, buf);

   CComboBox firms(GetDlgItem(IDC_FIRMS));
   int index = firms.GetCurSel();
   if( index >= 0 )
      wcscpy(preference->defaultFirm, (const wchar_t*)firms.GetItemData(index));

}



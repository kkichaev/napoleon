/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Диалог настроек обновлений
 *
 *  ert   17/10/2009   creating
 */ 
#include "stdafx.h"
#include "PrfDlg.h"

UpdateProperties::UpdateProperties() : PrefPage(IDC_UPDATE_DIALOG, L"Обновл.")
{
}

void UpdateProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();
   CComboBox update(GetDlgItem(IDC_UPDATE));

   update.AddString(L"не проводить");
   update.AddString(L"сообщать о новой");
   update.AddString(L"устанавливать");

   update.SetCurSel(((preference.flags & ufMask) >> ufShift) & 0x3);
}

void UpdateProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   CComboBox update(GetDlgItem(IDC_UPDATE));
   int cs = update.GetCurSel();
   if( cs >= 0 )
   {
      preference->flags &= (~ufMask);
      preference->flags |= ((cs & 0x3) << ufShift);
   }
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки системы
 * 
 *  ert   04/08/2007   creating
 */ 
#include "stdafx.h"
#include "Preference.h"

const wchar_t *Preference::subKey = L"\\Software\\Ert\\Napoleon";
#ifdef PREFERENCE_NAME
const wchar_t *Preference::valueName = PREFERENCE_NAME;
#else
const wchar_t *Preference::valueName = L"Preference";
#endif

const wchar_t *priceFieldTitle[] = 
{
   L"Нет", L"Кол-во", L"Кол-во в заказе", L"Кол-во прайс/заказ", L"Цена", L"Цена/Сумма в заказе"
};

bool Preference::Load()
{
   HKEY hk;
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, 0, &hk) != ERROR_SUCCESS )
      return false;

   DWORD type, cb = 0;
   RegQueryValueEx(hk, valueName, 0, &type, NULL, &cb);
   //if( cb != sizeof(*this) )
   //{
   //}

   if( cb > sizeof(*this) )
      cb = sizeof(*this);

   RegQueryValueEx(hk, valueName, 0, &type, (LPBYTE)this, &cb);
   RegCloseKey(hk);

   if( priceScale >= MAX_SCALE_ROW )
      priceScale = 0;

   if( orgScale >= MAX_SCALE_ROW )
      orgScale = 0;

   return true;
}

bool Preference::Save() const
{
   HKEY hk;
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, 0, &hk) != ERROR_SUCCESS &&
       RegCreateKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, NULL, REG_OPTION_NON_VOLATILE, NULL, NULL, &hk, NULL) != ERROR_SUCCESS )
   {
      return false;
   }

   RegSetValueEx(hk, valueName, 0, REG_BINARY, (const BYTE*)this, sizeof(*this));
   RegCloseKey(hk);
   return true;
}

bool Preference::Remove()
{
   return (RegDeleteKey(HKEY_LOCAL_MACHINE, subKey) == ERROR_SUCCESS);
}
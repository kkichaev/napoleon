/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Настройки системы. Не создавать виртуальных функций! 
 *
 * В классе DATA обязательно дожен быть определен 
 *
 *  static const wchar_t *subKey;
 * 
 *  ert   03/09/2010   creating
 */ 
#ifndef __PREFERENCE_BASE_CE_H
#define __PREFERENCE_BASE_CE_H

#define PREF_REG_NAME L"Preference"

template <class DATA> class PreferenceBase : public DATA
{
public:
   bool Remove()
   {
      return (RegDeleteKey(HKEY_LOCAL_MACHINE, subKey) == ERROR_SUCCESS);
   }

   bool Load()
   {
      HKEY hk;
      if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, 0, &hk) != ERROR_SUCCESS )
         return false;

      DWORD type, cb = 0;
      RegQueryValueEx(hk, PREF_REG_NAME, 0, &type, NULL, &cb);

      if( cb > sizeof(DATA) )
         cb = sizeof(DATA);

      RegQueryValueEx(hk, PREF_REG_NAME, 0, &type, (LPBYTE)this, &cb);
      RegCloseKey(hk);

      return true;
   }

   bool Save() const
   {
      HKEY hk;
      if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, 0, &hk) != ERROR_SUCCESS &&
          RegCreateKeyEx(HKEY_LOCAL_MACHINE, subKey, 0, NULL, REG_OPTION_NON_VOLATILE, NULL, NULL, &hk, NULL) != ERROR_SUCCESS )
      {
         return false;
      }

      RegSetValueEx(hk, PREF_REG_NAME, 0, REG_BINARY, (const BYTE*)this, sizeof(*this));
      RegCloseKey(hk);
      return true;
   }
};

#endif

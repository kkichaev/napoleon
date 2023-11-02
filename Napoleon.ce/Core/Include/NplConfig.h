/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Чтение/Запись конфигурации в реестр
 *
 *  ert   09/09/2007   creating
 */
#ifndef __CONFIG_IMPL_H
#define __CONFIG_IMPL_H

#include <string>

class NapoleonConfig
{
public:
   NapoleonConfig();
   ~NapoleonConfig();

   bool ReadValue(std::wstring *value, const wchar_t *key);
   bool WriteValue(const wchar_t *value, const wchar_t *key);
   bool Remove(const wchar_t *key);

   bool GetStringItem(std::wstring *value, const wchar_t *key, int item);
};

#endif

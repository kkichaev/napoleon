/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Настройки системы. Не создавать виртуальных функций! 
 * 
 *  ert   03/09/2010   creating
 */ 
#include "stdafx.h"
#include "Preference.h"

const wchar_t *PrefData::subKey = L"\\Software\\Ert\\NapoleonLogistic";

PrefData::PrefData()
{ 
   strcpy(ip, "127.0.0.1");
   strcpy(ip2, "169.254.2.2");

   port = 8888;

   strcpy(password, "1");
   strcpy(login, "login");

   scanPort = 0;
}

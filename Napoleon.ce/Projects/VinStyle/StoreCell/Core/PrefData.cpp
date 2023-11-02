/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Настройки системы. Не создавать виртуальных функций! 
 * 
 *  ert   03/09/2010   creating
 */ 
#include "stdafx.h"
#include "Preference.h"

const wchar_t *PrefData::subKey = L"\\Software\\Ert\\NapoleonSkladVV";

PrefData::PrefData()
{ 
   port = 8888;

   strcpy(password, "1");
   strcpy(login, "login");

   priceColumn2 = pcfQty;
   priceColumn3 = pcfCost;

   priceScale = 1;
   orgScale = 0;
}

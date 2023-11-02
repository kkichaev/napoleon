/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Настройки системы. Не создавать виртуальных функций! 
 * 
 *  ert   03/09/2010   creating
 */ 
#ifndef __PREFERENCE_CE_H
#define __PREFERENCE_CE_H

#include <PrefBase.h>
#include <StdConsts.h>

#include <bt_api.h>

#define PREF_REG_NAME L"Preference"
#define MAX_LANG_LENGTH 50

#define MAX_SCALE_ROW 3

enum PriceColumnField { pcfNone, pcfQty, pcfOrderQty, pcfPriceOrderQty, pcfCost, pcfCostSum };

struct PrefData
{
   WORD port;

   char  login[MAX_LOGIN];
   char  password[MAX_PASSWORD];

   PriceColumnField priceColumn2;
   PriceColumnField priceColumn3;

   WORD priceScale; // 0 ... MAX_SCALE_ROW-1
   WORD orgScale;   // 0 ... MAX_SCALE_ROW-1

	wchar_t goodItem[MAX_PATH];
	wchar_t badItem[MAX_PATH];

   PrefData();

   static const wchar_t *subKey;
};

typedef PreferenceBase<PrefData> Preference;

#endif

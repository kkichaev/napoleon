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

#define PREF_REG_NAME L"Preference"

struct PrefData
{
   char ip[16];
   char ip2[16];

   WORD port;

   char  login[MAX_LOGIN];
   char  password[MAX_PASSWORD];

   WORD scanPort;

   PrefData();

   static const wchar_t *subKey;
};

typedef PreferenceBase<PrefData> Preference;

#endif

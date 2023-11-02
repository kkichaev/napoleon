/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки системы
 * 
 *  ert   04/08/2007   creating
 */ 
#include "stdafx.h"
#include "Preference.h"

Preference::Preference()
{ 
   strcpy(ip, "127.0.0.1");
   strcpy(ip2, "169.254.2.2");

   port = 8888;
   baseVersion = BASE_VERSION;
   int updFlag = (ufCheck << ufShift);
   flags = (opfNoBelowZero|opfAlertBelowZero|ppfSelectLastOrder|updFlag);
   *login = '\0';
   *password = '\0';
   worked = 0;

   photoInMainMemory = true;
   *photoFolder = L'\0';

   priceColumn2 = pcfQty;
   priceColumn3 = pcfCost;

#ifdef Migma
   memset(&lastPrice, 0, sizeof(lastPrice));
#endif

#ifdef Alians
   orderNumber = 1;
#endif

   strcpy(dbName, DEFAULT_BASE);

#ifdef GPS_POS
   gpsPort = 6;
   //gpsInterval = 0;
   //gpsFlags = 0;
   gpsArchInterval = 7;
   gpsAccuracy = 1;
#endif

   priceScale = 2;
   orgScale = 2;

#ifdef VISIT_DOC
   photoQuality = 0;
#endif

   *defaultFirm = L'\0';

#ifdef SHOW_OFF_TAKE
   offTakeCoef = 150;  // SUM_SCALE 1.5
#endif

   addFlags = 0;
}

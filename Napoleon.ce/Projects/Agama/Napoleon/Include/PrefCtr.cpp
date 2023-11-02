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
   strcpy(ip, "81.211.42.69");
   strcpy(ip2, "169.254.2.2");

   port = 8811;
   baseVersion = BASE_VERSION;
   int updFlag = (ufCheck << ufShift);
   flags = (apfTopApp|ppfBoxQty|opfAlertBelowZero|apfPortrait);
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
   gpsInterval = 60;
   //gpsFlags = 0;
   gpsArchInterval = 0;
   gpsAccuracy = 1;
#endif

   priceScale = 0;
   orgScale = 0;

#ifdef VISIT_DOC
   photoQuality = 0;
#endif

#ifdef SHOW_OFF_TAKE
   offTakeCoef = 150;  // SUM_SCALE 1.5
#endif

   whDefault = 0; // код склада по умолчанию

   addFlags = 0; //afAutoVisit;
}

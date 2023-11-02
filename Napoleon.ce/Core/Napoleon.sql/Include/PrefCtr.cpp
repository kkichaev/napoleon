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
   baseVersion = BASE_VERSION;
   int updFlag = (ufCheck << ufShift);

#ifdef Pavlov
   strcpy(ip, "mail.cvk-trade.ru");
   strcpy(ip2, "napoleon_pavlov.cvk.trade");
#elif VisStyle
   strcpy(ip, "192.168.0.3");
   strcpy(ip2, "89.235.129.98");

   flags = (apfSortNoCase|ppfSelectLastOrder|opfNoBelowZero|opfAlertBelowZero|updFlag);

   priceColumn2 = pcfCost;
   priceColumn3 = pcfQty;
#else
   strcpy(ip, "127.0.0.1");
   strcpy(ip2, "169.254.2.2");

   flags = (opfNoBelowZero|opfAlertBelowZero|ppfSelectLastOrder|updFlag);

   priceColumn2 = pcfQty;
   priceColumn3 = pcfCost;
#endif

   port = 8888;
   *login = '\0';
   *password = '\0';
   worked = 0;

   photoInMainMemory = true;
   *photoFolder = L'\0';

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

   priceScale = 2;
   orgScale = 2;

#ifdef VISIT_DOC
   photoQuality = 0;
   picWidth = 0;
   picHeight = 0;
#endif

#ifdef SHOW_OFF_TAKE
   offTakeCoef = 150;  // SUM_SCALE 1.5
#endif

   addFlags = 0;

#ifdef FIRM_DEFAULT
   defaultFirm = -1;
#endif
}

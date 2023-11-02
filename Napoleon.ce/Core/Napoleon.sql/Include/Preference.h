/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки системы. Не создавать виртуальных функций! 
 * 
 *  ert   04/08/2007   creating
 */ 
#ifndef __PREFERENCE_CE_H
#define __PREFERENCE_CE_H

#include <Exchange.h>

#ifdef Agama
#define WH_QTY L"Склад"
#endif

#define DEFAULT_BASE "NapoleonDB.sdb"

enum PreferenceFlags
{
   opfNoBelowZero      = 0x0001,
   opfAlertBelowZero   = 0x0002,
   ppfSelectLastOrder  = 0x0004,
   npfConfirmOrderSend = 0x0008,
   ppfBoxQty           = 0x0010,
   apfTopApp           = 0x0020,
   apfSortNoCase       = 0x0040,

   opfSendOnLine       = 0x0080,
   opfOnLineUseIP1     = 0x0100,
   //ppfPriceRow2        = 0x0080,
   //ppfOrgRow2          = 0x0100,

   opfCalendarView     = 0x0200,

   apfPortrait         = 0x0400, // Agama
   apfLandscape        = 0x0800, // Agama

   apfShowSKU          = 0x1000, // Provisia

   ufMask              = 0x6000, // маска флагов для Update
};

#ifdef Agama
enum AddFlags
{
   afAutoVisit = 0x0001, // создавать посещение с заявкой
};
#else
// Provisia
enum AddFlags
{
   afHideRestQTY = 0x0001, // скрыть окно ввода остатков в диалоге кол-ва
};
#endif

const int ufShift = 13;
enum UpdateFlags { ufNone = 0, ufCheck, ufLoad };

//#ifdef GPS_POS
//enum GPSFlags
//{
//   gpfOrderGPS         = 0x0001, // set order coordinats
//   gpfUseGSM           = 0x0002,
//   gpfGPSTrack         = 0x0004,
//};
//#endif

inline DWORD MakeCode(DWORD id, const FILETIME &ft)
{
   return (id != 0) ? id : id ^ ft.dwLowDateTime + ft.dwHighDateTime;
}

#define PRICE_COLUMN_FIELDS 6
enum PriceColumnField { pcfNone, pcfQty, pcfOrderQty, pcfPriceOrderQty, pcfCost, pcfCostSum };
extern const wchar_t *priceFieldTitle[];

#define MAX_DB_NAME 50
#define MAX_SCALE_ROW 3

struct Preference
{
   char ip[16];
   WORD port;
   DWORD baseVersion;
   char  login[MAX_LOGIN];
   char  password[MAX_PASSWORD];
   WORD  flags;

   DWORD notUsed1;
   DWORD notUsed2;
   DWORD notUsed3;
   DWORD worked;

   bool  photoInMainMemory;
   char  photoFolder[MAX_PATH];

   PriceColumnField priceColumn2;
   PriceColumnField priceColumn3;

#ifdef Alians
   DWORD orderNumber;
#endif

#ifdef Migma
   FILETIME lastPrice;
#endif

   char ip2[16];

#ifdef Agama
   int whDefault;
#endif

#ifdef Kirov_Pavel
   WORD defaultCostType;
#endif

#ifdef Provisia
   WORD priceLines;
   wchar_t defaultFirm[15]; 
#endif

   char dbName[MAX_DB_NAME];

#ifdef GPS_POS
   WORD gpsInterval; //gpsFlags
   WORD gpsPort;
   WORD gpsNotUsed2; //gpsInterval
   WORD gpsArchInterval; // in days
   WORD gpsAccuracy;     // in meters (1, 10, 100, 1000)
#endif

#ifdef VISIT_DOC
   WORD photoQuality;
#endif

   WORD priceScale; // 0 ... MAX_SCALE_ROW-1
   WORD orgScale;   // 0 ... MAX_SCALE_ROW-1

#ifdef Voshod
   wchar_t defaultFirm[15]; 
#endif

#ifdef SHOW_OFF_TAKE
   WORD offTakeCoef;  // SUM_SCALE
#endif

   DWORD addFlags;

#ifdef VISIT_DOC
   WORD picWidth;
   WORD picHeight;
#endif

#ifdef FIRM_DEFAULT
   short defaultFirm;
#endif

   Preference();

#ifdef UNDER_CE
   bool Remove();
   bool Load();
   bool Save() const;

   static const wchar_t *subKey;
   static const wchar_t *valueName;
#endif
};

#endif

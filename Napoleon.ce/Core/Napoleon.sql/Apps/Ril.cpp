/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Napoleon Apps RIL
 * 
 *  ert   10/09/2010   creating
 */ 
#include "stdafx.h"
#include "AppsModule.h"
//
// -------------------- from ril.h -----------------------------
//
#define MAXLENGTH_BCCH                              (48)      // @constdefine 48
#define MAXLENGTH_NMR                               (16)      // @constdefine 16
 
typedef HANDLE HRIL, *LPHRIL;

typedef void (CALLBACK *RILRESULTCALLBACK)(
    DWORD dwCode,           // @parm result code
    HRESULT hrCmdID,        // @parm ID returned by the command that originated this response
    const void* lpData,     // @parm data associated with the notification
    DWORD cbData,           // @parm size of the strcuture pointed to lpData
    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
);

typedef void (CALLBACK *RILNOTIFYCALLBACK)(
    DWORD dwCode,           // @parm notification code
    const void* lpData,     // @parm data associated with the notification
    DWORD cbData,           // @parm size of the strcuture pointed to lpData
    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
);
 
typedef HRESULT (*RIL_InitializeT)(
    DWORD dwIndex,                      // @parm index of the RIL port to use (e.g., 1 for RIL1:)
    RILRESULTCALLBACK pfnResult,        // @parm function result callback
    RILNOTIFYCALLBACK pfnNotify,        // @parm notification callback
    DWORD dwNotificationClasses,        // @parm classes of notifications to be enabled for this client
    DWORD dwParam,                      // @parm custom parameter passed to result and notififcation callbacks
    HRIL* lphRil                        // @parm returned handle to RIL instance
);

typedef HRESULT (*RIL_DeinitializeT)(
    HRIL hRil                           // @parm handle to an RIL instance returned by <f RIL_Initialize>
);

typedef HRESULT (*RIL_GetCellTowerInfoT)(
    HRIL hRil                           // @parm handle to RIL instance returned by <f RIL_Initialize>
);

typedef HRESULT (*RIL_GetCurrentOperatorT)(
  HRIL hRil,
  DWORD dwFormat
);

typedef HRESULT(*RIL_GetSystemTimeT)(HRIL hRil);

typedef struct rilcelltowerinfo_tag {
    DWORD cbSize;                       // @field structure size in bytes
    DWORD dwParams;                     // @field indicates valid parameters
    DWORD dwMobileCountryCode;          // @field TBD
    DWORD dwMobileNetworkCode;          // @field TBD
    DWORD dwLocationAreaCode;           // @field TBD
    DWORD dwCellID;                     // @field TBD
    DWORD dwBaseStationID;              // @field TBD
    DWORD dwBroadcastControlChannel;    // @field TBD
    DWORD dwRxLevel;                    // @field Value from 0-63 (see GSM 05.08, 8.1.4)
    DWORD dwRxLevelFull;                // @field Value from 0-63 (see GSM 05.08, 8.1.4)
    DWORD dwRxLevelSub;                 // @field Value from 0-63 (see GSM 05.08, 8.1.4)
    DWORD dwRxQuality;                  // @field Value from 0-7  (see GSM 05.08, 8.2.4)
    DWORD dwRxQualityFull;              // @field Value from 0-7  (see GSM 05.08, 8.2.4)
    DWORD dwRxQualitySub;               // @field Value from 0-7  (see GSM 05.08, 8.2.4)
    DWORD dwIdleTimeSlot;               // @field TBD
    DWORD dwTimingAdvance;              // @field TBD
    DWORD dwGPRSCellID;                 // @field TBD
    DWORD dwGPRSBaseStationID;          // @field TBD
    DWORD dwNumBCCH;                    // @field TBD
    BYTE rgbBCCH[MAXLENGTH_BCCH];       // @field TBD
    BYTE rgbNMR[MAXLENGTH_NMR];         // @field TBD
} RILCELLTOWERINFO, *LPRILCELLTOWERINFO;

#define MAXLENGTH_OPERATOR_LONG                     (32)      // @constdefine 32
#define MAXLENGTH_OPERATOR_SHORT                    (16)      // @constdefine 16
#define MAXLENGTH_OPERATOR_NUMERIC                  (16)      // @constdefine 16
#define MAXLENGTH_OPERATOR_COUNTRY_CODE             (8)       // @constdefine 8

#define RIL_PARAM_ON_LONGNAME                       (0x00000001) // @paramdefine
#define RIL_PARAM_ON_SHORTNAME                      (0x00000002) // @paramdefine
#define RIL_PARAM_ON_NUMNAME                        (0x00000004) // @paramdefine
#define RIL_PARAM_ON_COUNTRY_CODE                   (0x00000008) // @paramdefine
#define RIL_PARAM_ON_ALL                            (0x0000000f) // @paramdefine

typedef struct riloperatornames_tag {
    DWORD cbSize;                           // @field structure size in bytes
    DWORD dwParams;                         // @field indicates valid parameters
    char szLongName[MAXLENGTH_OPERATOR_LONG];   // @field long representation (max 16 characters)
    char szShortName[MAXLENGTH_OPERATOR_SHORT]; // @field short representation (max 8 characters)
    char szNumName[MAXLENGTH_OPERATOR_NUMERIC]; // @field numeric representation (3 digit country code & 2 digit network code)
    char szCountryCode[MAXLENGTH_OPERATOR_COUNTRY_CODE]; // @field 2 character ISO 3166 country repesentation of the MCC
} RILOPERATORNAMES, *LPRILOPERATORNAMES;
 
struct BaseParam
{
   BaseParam(bool gt, HANDLE _hLib) : getTower(gt), hLib(_hLib) {}

   bool getTower;
   HANDLE evDone;
   HANDLE hLib;
};

struct TowerData : public BaseParam
{
   TowerData(HANDLE _hLib) : BaseParam(true, _hLib) {}

   DWORD lac;
   DWORD cellID;
};

struct CountryData : public BaseParam
{
   CountryData(HANDLE _hLib) : BaseParam(false, _hLib) {}

   DWORD mcc;
   DWORD mnc;
};

bool GSMModule::RILInit()
{
   if( hLib == NULL )
      hLib = LoadLibrary(L"ril.dll");

   return (hLib != NULL);
}

void GSMModule::RILClose()
{
   if( hLib != NULL )
   {
      FreeLibrary(hLib);
      hLib = NULL;
   }
}

static void CALLBACK GetData(DWORD dwCode, HRESULT hrCmdID, const void* lpData, DWORD cbData, DWORD dwParam)
{
   if( ((BaseParam*)dwParam)->getTower )
   {
      ((TowerData*)dwParam)->lac = ((RILCELLTOWERINFO*)lpData)->dwLocationAreaCode;
      ((TowerData*)dwParam)->cellID = ((RILCELLTOWERINFO*)lpData)->dwCellID;
   } else
   {
      char * p = ((RILOPERATORNAMES*)lpData)->szNumName;
      DWORD val = 0, i =0;
      for( ; i < 3; i++ )
      {
         val *= 10;
         val += *p++ - '0';
      }
      ((CountryData*)dwParam)->mcc = val;
      ((CountryData*)dwParam)->mnc = atoi(p);
   }

   SetEvent(((BaseParam*)dwParam)->evDone);
}

static bool Get(BaseParam* data)
{
   bool res = false;
   if( data->hLib == NULL )
      return res;

   RIL_InitializeT init = (RIL_InitializeT)GetProcAddress((HMODULE)data->hLib, L"RIL_Initialize");
   RIL_DeinitializeT deinit = (RIL_DeinitializeT)GetProcAddress((HMODULE)data->hLib, L"RIL_Deinitialize");

   if( init != NULL && deinit != NULL )
   {
      data->evDone = CreateEvent(NULL, TRUE, FALSE, NULL);

      HRIL hRil = 0;
      HRESULT hres = init(1, GetData, NULL, 0, (DWORD)data, &hRil);

      if( hres == S_OK )
      {
         if( data->getTower )
         {
            RIL_GetCellTowerInfoT cti = (RIL_DeinitializeT)GetProcAddress((HMODULE)data->hLib, L"RIL_GetCellTowerInfo");
            if( cti )
               hres = cti(hRil);
         } else
         {
            RIL_GetCurrentOperatorT gop = (RIL_GetCurrentOperatorT)GetProcAddress((HMODULE)data->hLib, L"RIL_GetCurrentOperator");
            if( gop )
               hres = gop(hRil, RIL_PARAM_ON_LONGNAME | RIL_PARAM_ON_SHORTNAME/* | RIL_PARAM_ON_NUMNAME*/);
         }

         if( WaitForSingleObject(data->evDone, 1000) == WAIT_OBJECT_0 )
            res = true;
         else
            Log("%s RilError: %x", (data->getTower) ? "GetTower" : "GetCurOp", hres);
      } else
         Log("RilInit RilError: %x", hres);

      deinit(hRil);
      CloseHandle(data->evDone);
   }

   return res;
}

bool GSMModule::GetMCC_MNC(DWORD *country, DWORD *op)
{
   CountryData cd(hLib);
   
   if( !Get(&cd) )
      return false;

   *country = cd.mcc;
   *op = cd.mnc;

   return true;
}

bool GSMModule::GetLAC_CellID(DWORD *lac, DWORD *cell)
{
   TowerData td(hLib);

   td.lac = 0;
   td.cellID = 0;

   //Log("Enter GetLAC_CellID");

   if( !Get(&td) )
      return false;

   *lac = td.lac;
   *cell = td.cellID;

   //Log("Cell(%d, %d)", td.lac, td.cellID);

   return true;
}
/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Вкл/выкл телефонной линии
*
*  ert   10/07/2009   creating
*/

#include "stdafx.h"

#include <StdFuncs.h>

#ifdef WIN32_PLATFORM_PSPC

#include <tapi.h>

#include <extapi.h>
#include <tsp.h>

#include <regext.h>
#include <snapi.h>


#define TAPI_API_LOW_VERSION    0x00020000
#define TAPI_API_HIGH_VERSION   0x00020000

typedef LONG (*lineGetEquipmentStateT)(HLINE hLine,  LPDWORD lpdwState, LPDWORD lpdwRadioSupport);
typedef LONG (*lineSetEquipmentStateT)(HLINE hLine, DWORD dwState);
typedef LONG (*lineRegisterT)(HLINE hLine, DWORD dwRegisterMode, LPCTSTR lpszOperator, DWORD dwOperatorFormat);


class PhoneLine
{
public:
   PhoneLine() : lineApp(NULL), line(NULL) {}
   ~PhoneLine() { Close(); }

   bool OpenLine();
   bool CloseLine();

protected:
   bool Init();
   DWORD LineID(DWORD numDevice);

   void Close();

   HLINEAPP lineApp;
   HLINE line;
};

bool PhoneLine::Init()
{
   if( lineApp ) return true;

   LINEINITIALIZEEXPARAMS liep;
   liep.dwTotalSize = sizeof(liep);
   liep.dwOptions = LINEINITIALIZEEXOPTION_USEEVENT;

   DWORD numDevices, api;
   if( lineInitializeEx(&lineApp, 0, 0, 0, &numDevices, &api, &liep) != 0 ) return false;

   DWORD lineID = LineID(numDevices);
   if( lineID == 0xffffffff ) return false;

   if( lineOpen(lineApp, lineID,  &line, TAPI_API_HIGH_VERSION, 0, 0, 
                LINECALLPRIVILEGE_OWNER, LINEMEDIAMODE_DATAMODEM | LINEMEDIAMODE_INTERACTIVEVOICE, 0) != 0 )
   {
      return false;
   }

   return true;
}

bool PhoneLine::OpenLine()
{
   if( !Init() ) return false;

   HINSTANCE lib = LoadLibrary(L"CELLCORE.DLL");
   if( lib == NULL ) return false;

   DWORD state, radio;
   lineGetEquipmentStateT gs = (lineGetEquipmentStateT)GetProcAddress(lib, L"lineGetEquipmentState");

   bool retVal = false;
   if( gs != NULL )
   {
      retVal = true;
      gs(line, &state, &radio);

      if( state != LINEEQUIPSTATE_FULL )
      {
         lineSetEquipmentStateT ss = (lineSetEquipmentStateT)GetProcAddress(lib, L"lineSetEquipmentState");
         lineRegisterT reg = (lineRegisterT)GetProcAddress(lib, L"lineRegister");
         if( ss && reg )
         {
            ss(line, LINEEQUIPSTATE_FULL);
            reg(line, LINEREGMODE_AUTOMATIC, NULL, LINEOPFORMAT_NONE);
         }
      }
   }
   FreeLibrary(lib);
   return retVal;
}

bool PhoneLine::CloseLine()
{
   if( !Init() ) return false;

   HINSTANCE lib = LoadLibrary(L"CELLCORE.DLL");
   if( lib == NULL ) return false;

   DWORD state, radio;
   lineGetEquipmentStateT gs = (lineGetEquipmentStateT)GetProcAddress(lib, L"lineGetEquipmentState");

   bool retVal = false;
   if( gs != NULL )
   {
      retVal = true;
      gs(line, &state, &radio);

      if( state != LINEEQUIPSTATE_MINIMUM )
      {
         lineSetEquipmentStateT ss = (lineSetEquipmentStateT)GetProcAddress(lib, L"lineSetEquipmentState");
         if( ss )
            ss(line, LINEEQUIPSTATE_MINIMUM);
      }
   }
   FreeLibrary(lib);
   return retVal;
}

DWORD PhoneLine::LineID(DWORD numDevice)
{
   DWORD dwReturn = 0xffffffff;
   for(DWORD dwCurrentDevID = 0 ; dwCurrentDevID < numDevice ; dwCurrentDevID++)
   {
      DWORD dwAPIVersion;
      LINEEXTENSIONID LineExtensionID;
      if( lineNegotiateAPIVersion(lineApp, dwCurrentDevID, TAPI_API_LOW_VERSION, TAPI_API_HIGH_VERSION,  &dwAPIVersion, &LineExtensionID) == 0 ) 
      {
         LINEDEVCAPS LineDevCaps;
         LineDevCaps.dwTotalSize = sizeof(LineDevCaps);
         if( lineGetDevCaps(lineApp, dwCurrentDevID,  dwAPIVersion, 0, &LineDevCaps) == 0 ) 
         {
            BYTE* pLineDevCapsBytes = new BYTE[LineDevCaps.dwNeededSize];
            if( pLineDevCapsBytes != NULL ) 
            {
               LINEDEVCAPS* pLineDevCaps = (LINEDEVCAPS*)pLineDevCapsBytes;
               pLineDevCaps->dwTotalSize = LineDevCaps.dwNeededSize;
               if( lineGetDevCaps(lineApp, dwCurrentDevID,  dwAPIVersion, 0, pLineDevCaps) == 0 )
               {
                  const wchar_t *lineName = (const wchar_t*)((char*)pLineDevCaps+pLineDevCaps->dwLineNameOffset);
                  if( wcscmp(lineName, CELLTSP_LINENAME_STRING) == 0 ) 
                  {
                     dwReturn = dwCurrentDevID;
                  }
               }

               delete[]  pLineDevCapsBytes;
            }
         }
      }
   }
   return dwReturn;
}

void PhoneLine::Close()
{
   if (line)
   {
      lineClose(line);
      line = NULL;
   }
   if (lineApp)
   {
      lineShutdown(lineApp);
      line = NULL;
   }
}

static void NotifyFunc(HREGNOTIFY hNotify, DWORD dwUserData, const PBYTE pData, const UINT cbData)
{
   ((NotifyCallback)dwUserData)();
   RegistryCloseNotification(hNotify);
}

bool OpenPhoneLine(NotifyCallback callback)
{
   PhoneLine ph;
   if( !ph.OpenLine() )
      return false;

   if( callback )
   {
      NOTIFICATIONCONDITION nc;
      nc.dwMask = SN_PHONEHOMESERVICE_BITMASK | SN_PHONEROAMING_BITMASK;
      nc.ctComparisonType = REG_CT_GREATER;
      nc.TargetValue.dw = 0;

      HREGNOTIFY hNotify;
      RegistryNotifyCallback(SN_PHONECALLONHOLD_ROOT, SN_PHONECALLONHOLD_PATH,
                             SN_PHONECALLONHOLD_VALUE, NotifyFunc, (DWORD)callback, 
                             &nc, &hNotify);
   }
   return true;
}

bool ClosePhoneLine(NotifyCallback callback)
{
   PhoneLine ph;
   if( !ph.CloseLine() )
      return false;

   if( callback )
   {
      NOTIFICATIONCONDITION nc;
      nc.dwMask = SN_PHONERADIOOFF_BITMASK;
      nc.ctComparisonType = REG_CT_GREATER;
      nc.TargetValue.dw = 0;

      HREGNOTIFY hNotify;
      RegistryNotifyCallback(SN_PHONECALLONHOLD_ROOT, SN_PHONECALLONHOLD_PATH,
                             SN_PHONECALLONHOLD_VALUE, NotifyFunc, (DWORD)callback, 
                             &nc, &hNotify);
   }
   return true;
}

bool IsPhoneOn()
{
   HKEY hKey;
   if( RegOpenKeyEx(SN_PHONERADIOOFF_ROOT, SN_PHONERADIOOFF_PATH, 0, 0, &hKey) != ERROR_SUCCESS )
      return false;

   bool phoneIsOn = false;

   DWORD value, size;
   size = sizeof(value);
   if( RegQueryValueEx(hKey, SN_PHONERADIOOFF_VALUE, NULL, NULL, (LPBYTE)&value, &size) == ERROR_SUCCESS )
      phoneIsOn = ((value & (SN_PHONEHOMESERVICE_BITMASK | SN_PHONEROAMING_BITMASK)) != 0);

   RegCloseKey(hKey);

   return phoneIsOn;
}

#else
bool OpenPhoneLine(NotifyCallback callback)
{
   return false;
}

bool ClosePhoneLine(NotifyCallback callback)
{
   return false;
}

bool IsPhoneOn()
{
   return false;
}

#endif

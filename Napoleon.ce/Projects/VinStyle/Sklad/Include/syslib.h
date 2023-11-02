#ifndef _SYSCTL_H
#define _SYSCTL_H

#ifdef LIB_INTERNAL

#define SYSLIB
#else
      #ifdef __cplusplus
          #define SYSLIB extern "C"	
      #else
          #define SYSLIB extern
      #endif 

#endif

#define   WM_SYS_NOTIFY  (WM_APP + 198) 

#define   SYS_GPRS_EVENT_OPEN_PORT      0xAA00

#define   SYS_GPRS_EVENT_AUTHENTICATED  0xAA01

#define   SYS_GPRS_EVENT_CONNECTED      0xAA02

#define   SYS_GPRS_EVENT_DISCONNECTED   0xAA03

#define   SYS_GPRS_EVENT_CONNECT_FAILED 0xAA04


//======= Settings for keypad ========
#define KBD_NUMERAL_MODE  0

#define KBD_CAPITAL_CHAR_MODE   (KBD_NUMERAL_MODE+1)

#define KBD_LOWERCASE_CHAR_MODE (KBD_NUMERAL_MODE+2)

#define KBD_FUNCTION_MODE       (KBD_NUMERAL_MODE+3)

#define SET_KEYPAD_KEYS 0     

#define VK_POWER           0x88 

#define VK_SCAN            0xEA

#define FVK_FN             0xE3

#define FVK_COMBOKEY       0xE4

//======= Settings for function key  =======

#define SET_HOT_KEYS 0

#define HOT_KEY_BACKLIGHT           TEXT("FnBacklight")

#define HOT_KEY_DEVICE_POWER        TEXT("FnPower")

#define HOT_KEY_SIP                 TEXT("FnSIP")

#define HOT_KEY_CALIBRATION         TEXT("FnCalibration")

#define HOT_KEY_WLAN                TEXT("FnWLAN")

#define HOT_KEY_BLUETOOTH           TEXT("FnBluetooth")

#define HOT_KEY_CUT                 TEXT("FnCUT")

#define HOT_KEY_COPY                TEXT("FnCOPY")

#define HOT_KEY_PASTE               TEXT("FnPASTE")

#define HOT_KEY_VOLUME_UP           TEXT("FnVolume+")

#define HOT_KEY_VOLUME_DOWN         TEXT("FnVolume-")

#define HOT_KEY_BACKLIGHT_INCREASE  TEXT("FnBacklight+")

#define HOT_KEY_BACKLIGHT_DECREASE  TEXT("FnBacklight-")


//======= Settings for backlight ========

//Settings for backlight mode
#define BKL_OFF          0

#define BKL_ALWAYS_ON    1

#define BKL_ENABLE_TIMER 2

//Settings for backlight timer
#define BKL_TIMER_15_SEC   15

#define BKL_TIMER_30_SEC   30

#define BKL_TIMER_45_SEC   45

#define BKL_TIMER_1_MIN    60

#define BKL_TIMER_3_MIN   180

#define BKL_TIMER_5_MIN   300

#define BKL_TIMER_10_MIN  600

#define BKL_TIMER_15_MIN  900


//======= Settings for system ========

#define SYSTEM_BATTERY_HIGH              0x01

#define SYSTEM_BATTERY_LOW               0x02

#define SYSTEM_BATTERY_CRITICAL          0x04

#define SYSTEM_BATTERY_STATE_UNKNOWN     0xff


//======= Settings for power schemes ==========

#define AC_POWER_SCHEME_1_MIN    60

#define AC_POWER_SCHEME_2_MIN    120

#define AC_POWER_SCHEME_5_MIN    300

#define AC_POWER_SCHEME_10_MIN   600

#define AC_POWER_SCHEME_15_MIN   900

#define AC_POWER_SCHEME_30_MIN   1800

#define AC_POWER_SCHEME_NEVER    0



#define BATTERY_POWER_SCHEME_1_MIN    60

#define BATTERY_POWER_SCHEME_2_MIN    120

#define BATTERY_POWER_SCHEME_3_MIN    180

#define BATTERY_POWER_SCHEME_4_MIN    240

#define BATTERY_POWER_SCHEME_5_MIN    300

#define BATTERY_POWER_SCHEME_10_MIN   600

#define BATTERY_POWER_SCHEME_15_MIN   900

#define BATTERY_POWER_SCHEME_30_MIN   1800

#define BATTERY_POWER_SCHEME_NEVER    0

//======= Settings for CPL programs ==========

#define  CPL_PC Connection    0  
#define  CPL_Dialing          1
#define  CPL_Keyboard         2
#define  CPL_Password         3
#define  CPL_Owner            4
#define  CPL_Power            5
#define  CPL_System           6
#define  CPL_Display          7
#define  CPL_Mouse            8
#define  CPL_Stylus           9
#define  CPL_Volume_Sounds    10
#define  CPL_InputPanel       11 
#define  CPL_Remove_Programs  12             
#define  CPL_Date_Time        13  
#define  CPL_Certificates     14  
#define  CPL_Bluetooth        15
#define  CPL_Net_Connections  16

//=====================================
//=         Device Information        =
//=====================================

SYSLIB  BOOL SysGetModelName(LPCTSTR lpszModelName);

SYSLIB  BOOL SysGetFirmwareVersion (LPCTSTR lpszFWVersion);

SYSLIB  BOOL SysGetSerialNumber(LPTSTR lpszSerial);

SYSLIB  BOOL SysGetLibraryVersion(LPTSTR lpLibraryVersion);


//=====================================
//=         Keypad Controls           =
//=====================================

SYSLIB BOOL SysGetKBDInputMode(PBYTE lpdwInputMode);

SYSLIB BOOL SysSetKBDInputMode(BYTE lwInputMode);

SYSLIB BOOL SysGetKeypadState(DWORD dwVKCode, PBOOL lpdwLockState);

SYSLIB BOOL SysSetKeypadState(DWORD dwVKCode, BOOL dwLockState);

//=====================================
//=         Function Key Controls     =
//=====================================

SYSLIB BOOL SysGetFxKeyPrograms(DWORD dwVKCode,LPCTSTR  lpFileName ,LPCTSTR lpParameters);

SYSLIB BOOL SysSetFxKeyPrograms(DWORD dwVKCode,LPCTSTR  lpFileName ,LPCTSTR lpParameters);

SYSLIB BOOL SysGetFxKeyState(DWORD dwVKCode,PBOOL lpdwEnableState);

SYSLIB BOOL SysSetFxKeyState(DWORD dwVKCode,BOOL dwEnableState);


//=====================================
//=         Backlight Controls        =
//=====================================

SYSLIB BOOL SysGetBacklightMode(PBYTE lpdwMode);

SYSLIB BOOL SysSetBacklightMode(BYTE dwMode);


SYSLIB BOOL SysGetBacklight(PBYTE lpdwPercentage);

SYSLIB BOOL SysSetBacklight(BYTE dwPercentage);


SYSLIB BOOL SysGetBacklightTimer(PDWORD lpdwTimeCount);

SYSLIB BOOL SysSetBacklightTimer(DWORD dwTimeCount);


SYSLIB BOOL SysGetKBDBacklight(PBYTE lpdwMode);

SYSLIB BOOL SysSetKBDBacklight(BYTE dwMode);

//=====================================
//=       WLAN Controls               =
//=====================================

SYSLIB BOOL SysSetWLANPower(BOOL dwPowerStatus);

SYSLIB BOOL SysGetWLANPower(PBOOL lpdwPowerStatus);

SYSLIB BOOL SysGetWLANPHYAddress(LPTSTR  lpszPhyAddr);

//=====================================
//=       Bluetooth Controls          =
//=====================================

SYSLIB BOOL SysSetBluetoothPower(BOOL dwPowerStatus);

SYSLIB BOOL SysGetBluetoothPower(PBOOL lpdwPowerStatus);

SYSLIB BOOL SysGetBluetoothPHYAddress(LPTSTR lpszPhyAddr);

//=====================================
//=       GPS Controls               =
//=====================================

SYSLIB BOOL SysSetGPSPower(BOOL dwPowerStatus);

SYSLIB BOOL SysGetGPSPower(PBOOL lpdwPowerStatus);

SYSLIB BOOL SysSetGPSPowerOffState(BOOL dwKeepPowerState);

SYSLIB BOOL SysGetGPSPowerOffState(PBOOL lpKeepPowerState);

//=====================================
//=       GPRS Controls               =
//=====================================

SYSLIB BOOL SysGPRSDialUp(LPTSTR szConnection);

SYSLIB BOOL SysGPRSHangUp(LPTSTR szConnection);


//=====================================
//=       Touch Panel Controls        =
//=====================================

SYSLIB BOOL SysSetTouchPanelState(BOOL dwEnableStatus);

SYSLIB BOOL SysGetTouchPanelState(PBOOL lpdwEnableStatus);

//=====================================
//=       System Controls             =
//=====================================

SYSLIB BOOL SysGetACPowerScheme(PDWORD lpUerIdleTime,PDWORD lpSystemIdleTime,PDWORD lpSuspendTime);

SYSLIB BOOL SysSetACPowerScheme(DWORD dwUerIdleTime,DWORD dwSystemIdleTime,DWORD dwSuspendTime);

SYSLIB BOOL SysGetBatteryPowerScheme(PDWORD lpUerIdleTime,PDWORD lpSystemIdleTime,PDWORD lpSuspendTime);

SYSLIB BOOL SysSetBatteryPowerScheme(DWORD dwUerIdleTime,DWORD dwSystemIdleTime,DWORD dwSuspendTime);

SYSLIB void SysWarmReset(void);

SYSLIB void SysPowerDown(void);

SYSLIB void SysStandby(void);

SYSLIB BOOL SysGetACLineState(PBOOL lpACState);

SYSLIB BOOL SysGetMainBatteryState(PBYTE lpPowerLevel,PBOOL lpChargeState);

SYSLIB BOOL SysGetBackupBatteryState(PBYTE lpPowerLevel,PBOOL lpChargeState);

SYSLIB BOOL SysGetSIPState(PBOOL lpSIPState);

SYSLIB BOOL SysSetSIPState(BOOL dwSIPState);

SYSLIB BOOL SysStylusCalibration(void);

SYSLIB BOOL SysLaunchCPLProgram(BYTE dwCPLProgram);

SYSLIB BOOL SysStartActiveSync(void);

SYSLIB int  SysGetPreloadNumbers(void);

SYSLIB BOOL SysGetPreloadProgram(int dwLoadPriority,LPCTSTR szProgramFile);

SYSLIB BOOL SysSetPreloadProgram(int dwLoadPriority,LPCTSTR szProgramFile);

SYSLIB BOOL SysRemovePreloadProgram(void);

SYSLIB BOOL SysGetLastNotifyEvent(PDWORD lpNotifyEvent);


#endif
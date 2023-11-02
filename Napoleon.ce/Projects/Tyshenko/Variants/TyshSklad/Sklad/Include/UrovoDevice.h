// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the DEVICE_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// DEVICE_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.


#ifdef LIB
#define DEVICE_API
namespace device

#else

#ifdef DEVICE_EXPORTS
#define DEVICE_API __declspec(dllexport)
#else
#define DEVICE_API __declspec(dllimport)
#endif

extern "C"

#endif
{	

	typedef enum _AUTH_MODE
	{
		Open,
		Shared,
		WPA,
		WPAPSK,
		WPANone,
		WPA2,
		WPA2PSK
	} AUTH_MODE;


	typedef enum _ENCRYPT_MODE
	{
		Disabled,
		WEP,
		TKIP,
		AES
	} ENCRYPT_MODE;


	typedef enum _EAP_TYPE
	{
		TLS,
		PEAP,
		MD5
	} EAP_TYPE;

	typedef struct _WLAN_INFO
	{
		UCHAR MacAddress[6];
		ULONG SsidLength;
		UCHAR Ssid [32];
		LONG  Rssi;
	} WLAN_INFO, *PWLAN_INFO;


	typedef struct _WLAN_INFO_LIST
	{
		ULONG NumberOfItems;
		PWLAN_INFO Configs;
	} WLAN_INFO_LIST, *PWLAN_INFO_LIST;


	typedef enum _MSG_TYPE
	{
		MSG_RECVSMS	=	0x10,
		MSG_RECVSIG	=	0x11,
		MSG_NETSTATE =	0x12	
	} MSG_TYPE;


	//************************************
	// Method:    EnableGsmModule
	// Returns:    BOOL
	// Qualifier: power on the gsm module
	//************************************
	DEVICE_API BOOL EnableGsmModule();


	//************************************
	// Method:    DisableGsmModule
	// Returns:    BOOL
	// Qualifier: power off the gsm module
	//************************************
	DEVICE_API BOOL DisableGsmModule();


	//************************************
	// Method:    GetGsmPowerStatus
	// Returns:    BOOL
	// Qualifier: get the power status of the gsm mudule
	//************************************
	DEVICE_API int GetGsmPowerStatus();


	//************************************
	// Method:    Enable3GModule
	// FullName:  Enable3GModule
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL Enable3GModule();
	
	//************************************
	// Method:    Disable3GModule
	// FullName:  Disable3GModule
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL Disable3GModule();


	//************************************
	// Method:    Get3GPowerStatus
	// FullName:  Get3GPowerStatus
	// Access:    public 
	// Returns:   DEVICE_API int
	// Qualifier:
	//************************************
	DEVICE_API int Get3GPowerStatus();
	
	//************************************
	// Method:    GetGsmSignalStrength
	// Returns:    int,between 0 & 31 ,-1 is GSM disable.
	// Qualifier: check the gsm signal strength
	//************************************
	DEVICE_API int GetGsmSignalStrength();




	//************************************
	// Method:    Get3GSignalStrength
	// FullName:  Get3GSignalStrength
	// Access:    public 
	// Returns:   DEVICE_API int
	// Qualifier: 
	//************************************
	DEVICE_API int Get3GSignalStrength();

	//************************************
	// Method:    GetSimIMSI
	// FullName:  GetSimIMSI
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: LPTSTR lpszIMSI
	// Parameter: DWORD cch
	//************************************
	DEVICE_API BOOL GetSimIMSI(LPTSTR lpszIMSI, DWORD cch);

	//************************************
	// Method:    EnableWlanModule
	// Returns:    BOOL
	// Qualifier: power on the wireless lan module
	//************************************
	DEVICE_API BOOL EnableWlanModule();


	//************************************
	// Method:    DisableWlanModule
	// Returns:    BOOL
	// Qualifier: power off the wifi module
	//************************************
	DEVICE_API BOOL DisableWlanModule();


	//************************************
	// Method:    GetWlanPowerStatus
	// Returns:    BOOL
	// Qualifier: get the power status of the wifi module
	//************************************
	DEVICE_API int GetWlanPowerStatus();


	//************************************
	// Method:    GetWlanSignalStrength
	// Returns:    int, between -92 & 0  ,1:Wlan is disable ,2: Wlan is Disconnected 
	// Qualifier: check the wifi signal strength.
	//************************************
	DEVICE_API int  GetWlanSignalStrength();


	//************************************
	// Method:    EnableBthModule
	// Returns:    BOOL
	// Qualifier: power on the Bluetooth module
	//************************************
	DEVICE_API BOOL EnableBthModule();


	//************************************
	// Method:    DisableBthModule
	// Returns:    BOOL
	// Qualifier: power off the Bluetooth module
	//************************************
	DEVICE_API BOOL DisableBthModule();


	//************************************
	// Method:    GetBthPowerStatus
	// Returns:    BOOL
	// Qualifier: get the power status of the Bluetooth module
	//************************************
	DEVICE_API int GetBthPowerStatus();


	//************************************
	// Method:    EnableGpsModule
	// Returns:    BOOL
	// Qualifier: power on the Gps module
	//************************************
	DEVICE_API BOOL EnableGpsModule();


	//************************************
	// Method:    DisableGpsModule
	// Returns:    BOOL
	// Qualifier: power off the Gps module
	//************************************
	DEVICE_API BOOL DisableGpsModule();


	//************************************
	// Method:    GetGpsPowerStatus
	// Returns:    BOOL
	// Qualifier: get the power status of the Gps module
	//************************************
	DEVICE_API int GetGpsPowerStatus();


	//************************************
	// Method:    EnableVibrateModule
	// FullName:  EnableVibrateModule
	// Returns:   BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL EnableVibrateModule();


	//************************************
	// Method:    DisableVibrateModule
	// FullName:  DisableVibrateModule
	// Returns:   BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL DisableVibrateModule();


	//************************************
	// Method:    SetBackLightLevel
	// Returns:    BOOL
	// Qualifier: set the backlight value.
	// Parameter: int level, between 1-20
	//************************************
	DEVICE_API BOOL SetBackLightLevel(int level);


	//************************************
	// Method:    GetBackLightLevel
	// Returns:    Returns the backlight value. between 1-20.-1 is failed.
	//************************************
	DEVICE_API int GetBackLightLevel();


	//************************************
	// Method:    CheckNetworkStat
	// Returns:    BOOL
	// Qualifier: Check that the device is connected to the gateway 
	//************************************
	DEVICE_API BOOL CheckNetworkStat();

	

	//************************************
	// Method:    ConnectGprs
	// Returns:    BOOL
	// Qualifier: establishes a gprs connection.
	// Parameter: LPCTSTR lpszConnName, the entry name in "ControlPanel\network and dial connection\"
	//			  LPDWORD lpErrorCode, point to the ras errorcode
	//			  DWORD dwTimeout, timeout
	DEVICE_API BOOL ConnectGprs(LPCTSTR lpszConnName, DWORD dwTimeout, LPDWORD lpErrorCode);
	

	//************************************
	// Method:    GetGprsStatus
	// Returns:    BOOL
	// Qualifier: get the gprs connection status.
	// Parameter: LPCTSTR lpszConnName, the entry name in "ControlPanel\network and dial connection\"
	//************************************
	DEVICE_API BOOL GetGprsStatus(LPCTSTR lpszConnName);
	
	
	//************************************
	// Method:    DisConnectGprs
	// Returns:    void
	// Qualifier: terminate the gprs connection.
	// Parameter: LPCTSTR lpszConnName, the entry name in "ControlPanel\network and dial connection\"
	//************************************
	DEVICE_API void DisConnectGprs(LPCTSTR lpszConnName);

	//************************************
	// Method:    CreateGprsEntry
	// Returns:   DEVICE_API BOOL
	// Qualifier: creates a new phone-book entry for gprs
	// Parameter: LPCTSTR lpszConnName, a null-terminated string that contains an phone entry name
	// Parameter: LPCTSTR szApn, a null-terminated string that contains anacess point name
	// Parameter: LPCTSTR szPhoneNumber, a null-terminated string that contains a telephone number 
	// Parameter: LPCTSTR szUserName, a null-terminated string that contains the user's user name. This string is used to authenticate the user's access to the remote access server.
	// Parameter: LPCTSTR szPassword, a null-terminated string that contains the user's password. This string is used to authenticate the user's access to the remote access server. 
	// Parameter: LPCTSTR szDomain, a null-terminated string that contains the domain on which authentication is to occur. An empty string ("") specifies the domain in which the remote access server is a member. An asterisk specifies the domain stored in the phone book for the entry. 
	//************************************
	DEVICE_API BOOL CreateGprsEntry(LPCTSTR lpszConnName, LPCTSTR szApn, LPCTSTR szPhoneNumber, LPCTSTR szUserName, LPCTSTR szPassword, LPCTSTR szDomain);



	//************************************
	// Method:    CreateGprsEntry
	// Returns:   DEVICE_API BOOL
	// Qualifier: creates a new phone-book entry for gprs
	// Parameter: LPCTSTR lpszConnName, a null-terminated string that contains an phone entry name
	// Parameter: LPCTSTR szApn, a null-terminated string that contains anacess point name
	// Parameter: LPCTSTR szPhoneNumber, a null-terminated string that contains a telephone number 
	// Parameter: LPCTSTR szUserName, a null-terminated string that contains the user's user name. This string is used to authenticate the user's access to the remote access server.
	// Parameter: LPCTSTR szPassword, a null-terminated string that contains the user's password. This string is used to authenticate the user's access to the remote access server. 
	// Parameter: LPCTSTR szDomain, a null-terminated string that contains the domain on which authentication is to occur. An empty string ("") specifies the domain in which the remote access server is a member. An asterisk specifies the domain stored in the phone book for the entry. 
	// Parameter: int nCID, PDP Context Identifier.
	//************************************
	DEVICE_API BOOL CreateGprsEntryEx(LPCTSTR lpszConnName, LPCTSTR szApn, LPCTSTR szPhoneNumber, LPCTSTR szUserName, LPCTSTR szPassword, LPCTSTR szDomain, int nCID);


	//************************************
	// Method:    QueryWlanInformation
	// FullName:  QueryWlanInformation
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: PBYTE pAssociated
	// Parameter: PWLAN_CONFIG_LIST pAvailableList
	// Parameter: PWLAN_CONFIG_LIST pPreferredList
	//************************************
	DEVICE_API BOOL QueryWlanInformation(PBYTE pAssociated, PWLAN_INFO_LIST pAvailableList, PWLAN_INFO_LIST pPreferredList);


	//************************************
	// Method:    FreeWlanInformation
	// FullName:  FreeWlanInformation
	// Access:    private 
	// Returns:   DEVICE_API void
	// Qualifier:
	//************************************
	DEVICE_API void FreeWlanInformation();


	//************************************
	// Method:    AddToWlanPreferredList
	// Returns:   BOOL
	// Qualifier: Add a SSID to the "preferred wireless network list"
	// Parameter: LPCTSTR szSSID
	//				the name of wireless network to connect
	// Parameter: AUTH_MODE authMode
	//				802.1x authentication mode value
	// Parameter: ENCRYPT_MODE encryptMode
	//				encryption  mode
	// Parameter: LPCTSTR szKey`
	//				for WEP-key, use '#/<key-value>',
	//				'#' is key-index (1-4), '<key-value>' is WEP key value (40-bit or 104-bit).
	//				40-bit is either '10-digit hexa numbers' (ex: 0x1234567890) or '5-char ASCII string' (ex: zxcvb)
	//		        104-bit is either '26-digit hexa numbers' (ex: 0x12345678901234567890123) or '13-char ASCII string' (ex: abcdefghijklm)
	//		        for TKIP-key, use '<key-value>' form. (no key index)
	//		        TKIP-key can be 8-63 char ASCII string (ex: asdfghjk)
	// Parameter: EAP_TYPE eapType
	//				this is for 802.1X (EAP). both AP and STA will get keys automatically after the successful EAP. the szKey must be NULL.
	//		        UI dialogs will popup and ask user credentials (like certificate or user-name/password).
	// Parameter: BOOL bAdhoc
	//				true for connecting to an adhoc net. false for connecting to an AP
	//************************************
	DEVICE_API BOOL AddToWlanPreferredList(LPCTSTR szSSID, AUTH_MODE authMode, ENCRYPT_MODE encryptMode, LPCTSTR szKey, EAP_TYPE eapType, BOOL bAdhoc);


	//************************************
	// Method:    ResetWlanPreferredList
	// Returns:   BOOL
	// Qualifier: Clear the "preferred wireless networks list". Wireless card will disconnect if it was connected
	//************************************
	DEVICE_API BOOL ResetWlanPreferredList();


	//************************************
	// Method:    RefreshWlanPreferredList
	// Returns:   BOOL
	// Qualifier: Forces the wireless card to reconnect "preferred wireless networks list"
	//************************************
	DEVICE_API BOOL RefreshWlanPreferredList();


	//************************************
	// Method:    GetDeviceID
	// Returns:    void
	// Qualifier: get the sn of the device
	// Parameter: LPTSTR lpszDvcID
	//************************************
	DEVICE_API BOOL GetDeviceID(LPTSTR lpszDvcID, DWORD cch);


	//************************************
	// Method:    GetDeviceState
	// Returns:   BOOL
	// Parameter: uiAction:   
	//            This parameter can be one of the following values:
	//            1: Boot State  Retrieves state about boot. 
	//               0,cold boot, 1,hot boot
	//			  pvParam:
	//			  Depends on the parameter being queried
	// Returns:   
	//************************************
	DEVICE_API BOOL GetDeviceInfo(UINT uiAction, PVOID pvParam);


	//************************************
	// Method:    SetKeyboardMode
	// Returns:   BOOL
	// Parameter: 0,numeric mode 1,lowercase mode 2,uppercase mode
	// Returns:   
	//************************************
	DEVICE_API BOOL SetKeyboardMode(UINT uiMode);


	//************************************
	// Method:    GetKeyboardMode
	// FullName:  GetKeyboardMode
	// Access:    public 
	// Returns:   0,numeric mode 1,lowercase mode 2,uppercase mode
	// Qualifier:
	//************************************
	DEVICE_API int GetKeyboardMode();


	//************************************
	// Method:    DisplayCursor
	// FullName:  DisplayCursor
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL DisplayCursor();


	//************************************
	// Method:    HideCursor
	// FullName:  HideCursor
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	//************************************
	DEVICE_API BOOL HideCursor();



	//************************************
	// Method:    AlphaBlend2
	// FullName:  AlphaBlend2
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: HDC dcDest
	// Parameter: int x
	// Parameter: int y
	// Parameter: int cx
	// Parameter: int cy
	// Parameter: HDC dcSrc
	// Parameter: int sx
	// Parameter: int sy
	//************************************
	DEVICE_API BOOL AlphaBlend2(HDC dcDest, int x, int y, int cx, int cy, HDC dcSrc, int sx, int sy);


#ifndef LIB

	//************************************
	// Method:    StartScreenLock
	// Returns:    BOOL
	// Qualifier: After call this method, the device will auto lockscreen every dwInterval if there is not user inputs. user can unlock by Func+5
	// Parameter: DWORD dwInterval, in seconds.
	//************************************
	DEVICE_API BOOL StartScreenLock(DWORD dwInterval = 300);

	//************************************
	// Method:    SetScreenLockHotkey
	// FullName:  SetScreenLockHotkey
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: UINT uKey
	// Parameter: LPCTSTR lpszKeyName
	//************************************
	DEVICE_API BOOL SetScreenLockHotkey(UINT uKey, LPCTSTR lpszKeyName);


	//************************************
	// Method:    SetScreenLockEvent
	// FullName:  SetScreenLockEvent
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: HANDLE hLockEvent
	//************************************
	DEVICE_API BOOL SetScreenLockEvent(HANDLE hLockEvent);

	//************************************
	// Method:    ScreenLockTimerReset
	// FullName:  ScreenLockTimerReset
	// Access:    private 
	// Returns:   DEVICE_API void
	// Qualifier:
	//************************************
	DEVICE_API void ScreenLockTimerReset();


	//************************************
	// Method:    StopScreenLock
	// Returns:    void
	// Qualifier: stop the screenlock function.
	//************************************
	DEVICE_API void StopScreenLock();


	//************************************
	// Method:    IsScreenLocked
	// Returns:    BOOL
	// Qualifier: Get state of screenlock.
	//************************************
	DEVICE_API BOOL IsScreenLocked();


#endif


	//************************************
	// Method:    SCA_Enable
	// Returns:    BOOL
	// Qualifier: power on the Scanner module
	//************************************
	DEVICE_API BOOL SCA_EnableModule();


	//************************************
	// Method:    SCA_Disable
	// Returns:    BOOL
	// Qualifier: power off the Scanner module
	//************************************
	DEVICE_API BOOL SCA_DisableModule();


	//************************************
	// Method:    SCA_GetPowerStatus
	// Returns:    BOOL
	// Qualifier: get the power status of the Scanner module
	//************************************
	DEVICE_API int SCA_GetPowerStatus();


	//************************************
	// Method:    SCA_RegisterMessage
	// Returns:   void
	// Qualifier: Register a message that is receive code data from the scanner
	// Parameter: hWnd : Handle to the caller's window
	//			  uiMessage:The message to be register
	//************************************
	DEVICE_API BOOL SCA_RegisterMessage(HWND hWnd,UINT uiMessage);

	//************************************
	// Method:    SCA_UnRegisterMessage
	// Returns:   void
	// Qualifier: UnRegister a message that is receive code data from the scanner

	//************************************
	DEVICE_API BOOL SCA_UnRegisterMessage();


	//************************************
	// Method:    SCA_RegisterNotification
	// FullName:  SCA_RegisterNotification
	// Access:    private 
	// Returns:   DEVICE_API DWORD
	// Qualifier:
	// Parameter: HANDLE hMsgQ
	//************************************
	DEVICE_API DWORD SCA_RegisterNotification(HANDLE hMsgQ);


	//************************************
	// Method:    SCA_UnRegisterNotification
	// FullName:  SCA_UnRegisterNotification
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: DWORD dwID
	//************************************
	DEVICE_API BOOL SCA_UnRegisterNotification(DWORD dwID);


	//************************************
	// Method:    SCA_SetTriggerState
	// FullName:  SCA_SetTriggerState
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: BOOL bState
	//************************************
	DEVICE_API BOOL SCA_SetTriggerState(BOOL bState);


	//************************************
	// Method:    SCA_LockTrigger
	// FullName:  SCA_LockTrigger
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL SCA_LockTrigger();


	//************************************
	// Method:    SCA_UnlockTrigger
	// FullName:  SCA_UnlockTrigger
	// Access:    public 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL SCA_UnlockTrigger();


	//************************************
	// Method:    SCA_SetTriggerMode
	// Returns:   BOOL
	// Qualifier: Set the scanner's TriggerMode
	// Parameter: nTriggerMode:   0   Host 
	//							  1   Continuous
	//							  2   Pluse
	//************************************
	DEVICE_API BOOL SCA_SetTriggerMode(int nTriggerMode);


	
	//************************************
	// Method:    SCA_SendParam
	// Returns:   BOOL
	// Qualifier: Set the scanner's params
	// Parameter: pParam: Pointer to the buffer that set param.
	//			  dwSize: Number of bytes to the buffer that set param.
	//			  bPermanent: Change Type:  FALSE: temporary change
	//										TRUE: nonvolatile memorizing change
	//************************************
	DEVICE_API BOOL SCA_SendParam(BYTE* pParam, DWORD dwSize, BOOL bPermanent );


	//************************************
	// Method:    SCA_RequestParam
	// Returns:   BOOL
	// Qualifier: Request the scanner's params
	// Parameter: 
	//************************************
	DEVICE_API BOOL SCA_RequestParam(BYTE* pParamNum, DWORD dwSize, BYTE* pParamVal, DWORD* lpdwValSize);


	//************************************
	// Method:    SCA_ResetScannerParams
	// Returns:   BOOL
	// Qualifier: Set the scanner's params to default
	//************************************
	DEVICE_API BOOL SCA_ResetScannerParams();


	//************************************
	// Method:    SMS_Open
	// FullName:  SMS_Open
	// Access:    private 
	// Returns:   DEVICE_API int
	// Qualifier:
	//************************************
	DEVICE_API int SMS_Open();

	//************************************
	// Method:    SMS_Close
	// FullName:  SMS_Close
	// Access:    private 
	// Returns:   DEVICE_API void
	// Qualifier:
	//************************************
	DEVICE_API void SMS_Close();
	
	//************************************
	// Method:    SMS_IsOpened
	// FullName:  SMS_IsOpened
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL SMS_IsOpened();

	//************************************
	// Method:    SMS_RegisterNotification
	// FullName:  SMS_RegisterNotification
	// Access:    private 
	// Returns:   DEVICE_API DWORD
	// Qualifier:
	// Parameter: HANDLE hMsgQ
	//************************************
	DEVICE_API DWORD SMS_RegisterNotification(HANDLE hMsgQ);

	//************************************
	// Method:    SMS_UnRegisterNotification
	// FullName:  SMS_UnRegisterNotification
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: DWORD dwID
	//************************************
	DEVICE_API BOOL  SMS_UnRegisterNotification(DWORD dwID);

	//************************************
	// Method:    SMS_SendSMS
	// FullName:  SMS_SendSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: LPCTSTR szRecipient
	// Parameter: LPCTSTR szSmcc
	// Parameter: LPCTSTR szMsg
	//************************************
	DEVICE_API BOOL SMS_SendSMS(LPCTSTR szRecipient, LPCTSTR szSmcc, LPCTSTR szMsg);

	//************************************
	// Method:    SMS_SendSMS
	// FullName:  SMS_SendSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: LPCTSTR szRecipient
	// Parameter: LPCTSTR szSmcc
	// Parameter: LPCTSTR szMsg
	//************************************
	DEVICE_API BOOL SMS_SendSMSEx(LPCTSTR szRecipient, LPCTSTR szSmcc, LPCTSTR szMsg, WORD wID, BYTE byTotalNum, BYTE byCurrentNum);

	//************************************
	// Method:    SMS_ReadSMS
	// FullName:  SMS_ReadSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: int iIndex
	// Parameter: LPTSTR szRecipient
	// Parameter: DWORD dwRecpLen
	// Parameter: LPTSTR szMsg
	// Parameter: DWORD dwMsgLen
	// Parameter: LPTSTR szTime
	// Parameter: DWORD dwTimeLen
	//************************************
	DEVICE_API BOOL SMS_ReadSMS(int iIndex, LPTSTR szRecipient, DWORD dwRecpLen, LPTSTR szMsg, DWORD dwMsgLen, LPTSTR szTime, DWORD dwTimeLen);


	//************************************
	// Method:    SMS_ReadSMS
	// FullName:  SMS_ReadSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: int iIndex
	// Parameter: LPTSTR szRecipient
	// Parameter: DWORD dwRecpLen
	// Parameter: LPTSTR szMsg
	// Parameter: DWORD dwMsgLen
	// Parameter: LPTSTR szTime
	// Parameter: DWORD dwTimeLen
	//************************************
	DEVICE_API BOOL SMS_ReadSMSEx(int iIndex, LPTSTR szRecipient, DWORD dwRecpLen, LPTSTR szMsg, DWORD dwMsgLen, LPTSTR szTime, DWORD dwTimeLen, WORD *pwID, BYTE *pbyTotalNum, BYTE *pbyCurrentNum);


	//************************************
	// Method:    SMS_DeleteSMS
	// FullName:  SMS_DeleteSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	// Parameter: int iIndex
	//************************************
	DEVICE_API BOOL SMS_DeleteSMS(int iIndex);

	//************************************
	// Method:    SMS_ListSMS
	// FullName:  SMS_ListSMS
	// Access:    private 
	// Returns:   DEVICE_API BOOL
	// Qualifier:
	//************************************
	DEVICE_API BOOL SMS_ListSMS();

	//************************************
	// Method:    SMS_GetSignalStrength
	// FullName:  SMS_GetSignalStrength
	// Access:    private 
	// Returns:   DEVICE_API int
	// Qualifier:
	//************************************
	DEVICE_API int SMS_GetSignalStrength();

	//************************************
	// Method:    SMS_GetRegistrationState
	// FullName:  SMS_GetRegistrationState
	// Access:    private 
	// Returns:   DEVICE_API int
	// Qualifier:
	//************************************
	DEVICE_API int SMS_GetRegistrationState();
}

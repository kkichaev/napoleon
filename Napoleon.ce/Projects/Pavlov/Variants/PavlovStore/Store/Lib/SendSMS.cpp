/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Послать СМС
 *
 *  ert   24/09/2008   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

#ifdef WIN32_PLATFORM_PSPC
#include <sms.h>

bool SendSMS(const wchar_t *phone, const wchar_t *text)
{
   typedef HRESULT (*SmsOpenT)(
      const LPCTSTR ptsMessageProtocol,
      const DWORD dwMessageModes,
      SMS_HANDLE* const psmshHandle,
      HANDLE* const phMessageAvailableEvent);

   typedef HRESULT (*SmsSendMessageT)(
      const SMS_HANDLE smshHandle,
      const SMS_ADDRESS * const psmsaSMSCAddress,
      const SMS_ADDRESS * const psmsaDestinationAddress,
      const SYSTEMTIME * const pstValidityPeriod,
      const BYTE * const pbData,
      const DWORD dwDataSize,
      const BYTE * const pbProviderSpecificData,
      const DWORD dwProviderSpecificDataSize,
      const SMS_DATA_ENCODING smsdeDataEncoding,
      const DWORD dwOptions,
      SMS_MESSAGE_ID * psmsmidMessageID);

   typedef HRESULT (*SmsCloseT)(
      const SMS_HANDLE smshHandle);


   HINSTANCE hLib = LoadLibrary(L"SMS.DLL");
   if( hLib == NULL ) return false;

   SmsOpenT smsOpen = (SmsOpenT)GetProcAddress(hLib, L"SmsOpen");
   SmsSendMessageT smsSendMessage = (SmsSendMessageT)GetProcAddress(hLib, L"SmsSendMessage");
   SmsCloseT smsClose = (SmsCloseT)GetProcAddress(hLib, L"SmsClose");

   if( smsOpen == NULL || smsSendMessage == NULL || smsClose == NULL )
   {
      FreeLibrary(hLib);
      return false;
   }

   SMS_HANDLE smshHandle;
   if( FAILED(smsOpen(SMS_MSGTYPE_TEXT, SMS_MODE_SEND, &smshHandle, NULL)) )
   {
      FreeLibrary(hLib);
      return false;
   }

	SMS_ADDRESS smsaDestination;
	TEXT_PROVIDER_SPECIFIC_DATA tpsd;
	SMS_MESSAGE_ID smsmidMessageID;

   smsaDestination.smsatAddressType = SMSAT_INTERNATIONAL;
	_tcsncpy(smsaDestination.ptsAddress, phone, SMS_MAX_ADDRESS_LENGTH);


   memset(&tpsd, 0, sizeof(tpsd));
   tpsd.dwMessageOptions = /*bSendConfirmation ? PS_MESSAGE_OPTION_STATUSREPORT : */ PS_MESSAGE_OPTION_NONE;
   tpsd.psMessageClass = PS_MESSAGE_CLASS1;
   tpsd.psReplaceOption = PSRO_NONE;
   tpsd.dwHeaderDataSize = 0;

	// Send the message, indicating success or failure
	smsSendMessage(smshHandle, NULL, 
						 &smsaDestination, NULL, (PBYTE)text, 
						 wcslen(text) * sizeof(WCHAR), (PBYTE) &tpsd, 
						 sizeof(TEXT_PROVIDER_SPECIFIC_DATA), SMSDE_OPTIMAL, 
						 SMS_OPTION_DELIVERY_NONE, &smsmidMessageID);

	// clean up
	smsClose(smshHandle);

   FreeLibrary(hLib);
   return true;
}
#else
bool SendSMS(const wchar_t *phone, const wchar_t *text)
{
   return false;
}
#endif

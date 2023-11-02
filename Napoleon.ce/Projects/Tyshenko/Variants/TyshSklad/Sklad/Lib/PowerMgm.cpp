/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Power Managment
 *
 *  ert   15/09/2010   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>
#include <Pm.h>
#include <Pmpolicy.h>

HANDLE hMQ = INVALID_HANDLE_VALUE;
HANDLE hPN = INVALID_HANDLE_VALUE;

const DWORD MSG_SIZE = sizeof(POWER_BROADCAST) + (MAX_PATH * sizeof(TCHAR));

HANDLE CreatePowerEvent()
{
   MSGQUEUEOPTIONS mqo;
   mqo.dwSize = sizeof(MSGQUEUEOPTIONS);
   mqo.dwFlags = MSGQUEUE_NOPRECOMMIT;
   mqo.dwMaxMessages = 4;
   mqo.cbMaxMessage = MSG_SIZE;
   mqo.bReadAccess = TRUE;

   PowerPolicyNotify(PPN_UNATTENDEDMODE, TRUE);
   hMQ = CreateMsgQueue(NULL, &mqo);
   hPN = RequestPowerNotifications(hMQ, PBT_TRANSITION);

   return hMQ;
}

void HandlePowerEvent()
{
   if( hMQ != INVALID_HANDLE_VALUE )
   {
      ResetEvent(hPN);

      BYTE buf[MSG_SIZE];
      DWORD cbReaded, dwFlags;
      while(ReadMsgQueue(hMQ, (POWER_BROADCAST*)buf, MSG_SIZE, &cbReaded, 0, &dwFlags))
      {
         if( ((POWER_BROADCAST*)buf)->Message == PBT_TRANSITION && !wcsicmp(((POWER_BROADCAST*)buf)->SystemPowerState, L"unattended" ) )
            SystemIdleTimerReset(); 
      }
   }
}

void FreePowerEvent()
{
   if( hPN != INVALID_HANDLE_VALUE )
   {
      StopPowerNotifications(hPN);
      CloseHandle(hPN);
      hPN = INVALID_HANDLE_VALUE;
   }

   if( hMQ != INVALID_HANDLE_VALUE )
   {
      CloseMsgQueue(hMQ);
      hMQ = INVALID_HANDLE_VALUE;
   }
   PowerPolicyNotify(PPN_UNATTENDEDMODE, FALSE);
}


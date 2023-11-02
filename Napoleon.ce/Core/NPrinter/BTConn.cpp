/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Соединение через Bluetooth
 *
 *  ert   09/05/2008   creating
 */ 
#include "stdafx.h"
#include "NPrinter.h"

#include <bt_api.h>
#include <initguid.h>
#include <bt_sdp.h>
#include <bthutil.h>

//#define DEBUG_FANTOM 1

//
//--------------------------------------- BTConnection ------------------------------
//

BTConnection::BTConnection() : sock(INVALID_SOCKET), hLookup(0)
{
   WSADATA wsaData;
   WSAStartup(MAKEWORD(2,2), &wsaData);

   BthGetMode(&mode);

#ifndef DEBUG_FANTOM
   if( mode == BTH_POWER_OFF )
      BthSetMode(BTH_CONNECTABLE);
#endif
}

BTConnection::~BTConnection()
{
   BthSetMode(mode);

   Close();
   WSACleanup();
}

void BTConnection::Close()
{
   if( hLookup != 0 )
   {
      WSALookupServiceEnd(hLookup);
      hLookup = 0;
   }

   if( sock != INVALID_SOCKET )
   {
      closesocket(sock);
      sock = INVALID_SOCKET;
   }
}

bool BTConnection::Connect(const ConnectData& data)
{
#ifndef DEBUG_FANTOM
   if( data.addrLen != sizeof(SOCKADDR_BTH) || sock != INVALID_SOCKET )
      return false;

   sock = socket (AF_BTH, SOCK_STREAM, BTHPROTO_RFCOMM);
   if( sock == INVALID_SOCKET )
      return false;

   if( connect(sock, data.addr, data.addrLen) )
      return false;
#endif
   return true;
}

bool BTConnection::LookupPrepare()
{
   WSAQUERYSET wq = {0};

   wq.dwSize = sizeof(wq);
   wq.dwNameSpace = NS_BTH;
   wq.lpcsaBuffer = NULL;

   return (WSALookupServiceBegin(&wq, LUP_CONTAINERS, &hLookup) == 0);
}

ConnectData* BTConnection::LookupNext()
{
   if( hLookup == 0 ) return NULL;

   union {
      char buf[5000];
      SOCKADDR_BTH	__unused;
   };

   LPWSAQUERYSET result;
   DWORD size;

   result = (LPWSAQUERYSET)buf;

   size = sizeof(buf);
   memset(result, 0, size);

   result->dwSize = sizeof(WSAQUERYSET);
   result->dwNameSpace = NS_BTH;
   result->lpBlob = NULL;
   
   int test;
   int ires = WSALookupServiceNext(hLookup, LUP_RETURN_NAME | LUP_RETURN_ADDR, &size, result);
   if( ires != 0 )
   {
      test = WSAGetLastError();
      return false;
   }

   ConnectData *cd = new ConnectData();
   cd->name = _wcsdup(result->lpszServiceInstanceName);
   cd->addr = (struct sockaddr*)new SOCKADDR_BTH;
   cd->addrLen = sizeof(SOCKADDR_BTH);
   cd->connectID = BTConnectID;

   memset(cd->addr, 0, sizeof(SOCKADDR_BTH));
   ((SOCKADDR_BTH*)cd->addr)->addressFamily = AF_BTH;
   ((SOCKADDR_BTH*)cd->addr)->serviceClassId = SerialPortServiceClass_UUID;
   ((SOCKADDR_BTH*)cd->addr)->btAddr = ((SOCKADDR_BTH*)result->lpcsaBuffer->RemoteAddr.lpSockaddr)->btAddr;

   return cd;
}

bool BTConnection::Read(BYTE *buf, DWORD *len)
{
   if( sock == INVALID_SOCKET ) return false;

   int cbread = recv(sock, (char*)buf, *len, 0);
   if( cbread == SOCKET_ERROR ) return false;

   *len = cbread;
   return true;
}

bool BTConnection::Write(const BYTE *buf, DWORD *len)
{
#ifdef DEBUG_FANTOM
   *len = 0;
   return true;
#else
   if( sock == INVALID_SOCKET ) return false;

   int cbsend = send(sock, (char*)buf, *len, 0);
   *len -= cbsend;
   return (*len == 0);
#endif
}

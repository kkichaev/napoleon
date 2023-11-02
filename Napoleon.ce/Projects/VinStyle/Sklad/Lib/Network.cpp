/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки системы
 * 
 *  ert   07/08/2007   creating
 */ 
#include "stdafx.h"
#include <winsock2.h>
#include <ws2tcpip.h>

#include "Network.h"
#include <Reflection.h>

#include <initguid.h>
#if defined WIN32_PLATFORM_PSPC && !defined(NATIVE_CE)
#include <connmgr.h>
#endif

#include <StdFuncs.h>

HANDLE Network::hConnection = 0;

Network::Network(WORD to) : socket(INVALID_SOCKET), lastError(0), timeout(to)
{
   WSADATA wsaData;
   WSAStartup(MAKEWORD(2,2), &wsaData);
}

Network::~Network()
{
   if( socket != INVALID_SOCKET )
      Close();

   WSACleanup();
}

void Network::ReleaseConnection(DWORD cache)
{
#if defined WIN32_PLATFORM_PSPC && !defined(NATIVE_CE)
   if( hConnection != 0 )
   {
      ConnMgrReleaseConnection(hConnection, cache);
      hConnection = 0;
   }
#endif
}

void Network::CopyConnection(Network *rcvr)
{
   rcvr->socket = socket;
   rcvr->lastError = lastError;
   rcvr->hConnection = hConnection;
   rcvr->timeout = timeout;

   socket = INVALID_SOCKET;
   lastError = 0;
   hConnection = INVALID_HANDLE_VALUE;
}

const int CONNECT_TIMEOUT = 60 * 1000; // 1 минута
bool Network::TryEstablishConnect()
{
#if defined WIN32_PLATFORM_PSPC && !defined(NATIVE_CE)
   DWORD dwStatus = 1;
   if( hConnection != 0 )
   {
      ConnMgrConnectionStatus(hConnection, &dwStatus);
      if( dwStatus == CONNMGR_STATUS_CONNECTED )
         return true;

      ConnMgrReleaseConnection(hConnection, 0);
      hConnection = 0;
   }

   GUID guidNetwork;

   CONNMGR_CONNECTIONINFO ci = { 0 };
   ci.cbSize           = sizeof(ci);
   ci.dwParams         = CONNMGR_PARAM_GUIDDESTNET;
   ci.bDisabled        = FALSE;
   ci.dwPriority       = CONNMGR_PRIORITY_USERINTERACTIVE;

   lastError = 0;

   ConnMgrMapURL(L"http://grsoft.ru", &guidNetwork, NULL);
   ci.dwFlags = CONNMGR_FLAG_PROXY_HTTP;

   ci.guidDestNet = guidNetwork;

   ConnMgrEstablishConnectionSync(&ci, &hConnection, CONNECT_TIMEOUT, &dwStatus);

   if( dwStatus == CONNMGR_STATUS_CONNECTED )
      return true;

   lastError = dwStatus;
   return false;
#else
   return true;
#endif
}

void Network::CloseConnection()
{
   //if( hConnection != INVALID_HANDLE_VALUE )
   //   ConnMgrReleaseConnection(hConnection, 1);
}

static bool GetAddr(sockaddr_in* adr, const char *name)
{
   bool res = false;

   addrinfo *addrI;
   if( getaddrinfo(name, NULL, NULL, &addrI) == 0 )
   {
      addrinfo *cur = addrI;
      while( cur )
      {
         if( cur->ai_addr->sa_family == AF_INET )
         {
            *adr = *(sockaddr_in*)cur->ai_addr;
            res = true;
            break;
         }
         cur = cur->ai_next;
      }
      freeaddrinfo(addrI);
   }

   return res;
}

bool Network::ConnectByName(const char *name, WORD port, bool establishConnect, bool tcpConnect)
{
   bool res = false;

   if( establishConnect && !TryEstablishConnect() )
      return res;

   sockaddr_in adr;
   if( GetAddr(&adr, name) )
   {
      adr.sin_port = htons(port);
      res = ConnectInt(adr, false, tcpConnect);
   }

   return res;
}

bool Network::Connect(const char *ip, WORD port, bool establishConnect, bool tcpConnect)
{
   DWORD addr = inet_addr(ip);
   if( addr != INADDR_NONE )
   {
      sockaddr_in adr;
      memset(&adr, 0, sizeof(adr));
      adr.sin_addr.S_un.S_addr = addr;
      adr.sin_family = AF_INET;
      adr.sin_port = htons(port);

      return ConnectInt(adr, establishConnect, tcpConnect);
   }
   return ConnectByName(ip, port, establishConnect, tcpConnect);
}

bool Network::ConnectInt(const sockaddr_in& adr, bool establishConnect, bool tcpConnect)
{
   if( tcpConnect )
      socket = ::socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
   else
      socket = ::socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);

   if( socket == INVALID_SOCKET )
   {
      lastError = WSAGetLastError();
      return false;
   }

   if( connect(socket, (const sockaddr*)&adr, sizeof(adr)) )
   {
      // call Close in Network::~Network();
      if( socket == INVALID_SOCKET )
      {
         return false;
      }

      bool error = true;
      if( establishConnect )
      {
         closesocket(socket);
         socket = INVALID_SOCKET;

         if( TryEstablishConnect() == false )
         {
            Close();
            return false;
         }

         socket = ::socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
         if( connect(socket, (const sockaddr*)&adr, sizeof(adr)) == 0 )
            error = false;
      } 

      if( error )
      {
         lastError = WSAGetLastError();
         Close();
         return false;
      }
   }

   return true;
}

void Network::Close()
{
   if( socket != INVALID_SOCKET )
   {
      SOCKET s = socket;
      socket = INVALID_SOCKET;
      closesocket(s);
   }
   CloseConnection();
}

bool Network::Send(const BYTE *buf, DWORD len, IProgressIndicator *pf)
{
   if( socket == INVALID_SOCKET )
      return false;

   DWORD sent = 0;

   if( pf ) pf->SetMax(len);
   while( sent < len )
   {
      int curSent = send(socket, (const char*)buf + sent, len - sent, 0);
      if( curSent == SOCKET_ERROR )
      {
         lastError = WSAGetLastError();
         return false;
      }
      sent += curSent;

      if( pf ) pf->SetPos(sent);
   }
   return true;
}

bool Network::Send(FILE *file, IProgressIndicator *pf)
{
   if( socket == INVALID_SOCKET )
      return false;

   BYTE *buf = (BYTE*)malloc(DEF_BUF_SIZE);
   bool retVal = true;

   fseek(file, 0, SEEK_END);
   long size = ftell(file), cp = 0;
   if( pf ) pf->SetMax(size);
   fseek(file, 0, SEEK_SET);

   while( !feof(file) )
   {
      int cbReaded = fread(buf, 1, DEF_BUF_SIZE, file);
      if( cbReaded <= 0 )
         break;

      if( Send(buf, cbReaded, NULL) == false )
      {
         retVal = false;
         break;
      }
      cp += cbReaded;
      if( pf ) pf->SetPos(cp);
   }
   
   free(buf);
   return retVal;
}

bool Network::WaitData()
{
   timeval tv;
   fd_set readfds;

   FD_ZERO(&readfds);
   tv.tv_sec = timeout;
   tv.tv_usec = 0;
   FD_SET(socket, &readfds);

   int resval = select(0, &readfds, NULL, NULL, &tv);
   return (resval != SOCKET_ERROR && resval != 0);
}

bool Network::Receive(BYTE *buf, DWORD *len, IProgressIndicator *pf)
{
   DWORD count = 0;
   if( pf ) pf->SetPos(0);

   while( count < *len )
   {
      DWORD cb = AvailBytes();
      if( cb == 0 )
      {
         if( !WaitData() || (cb = AvailBytes()) == 0 )
         {
            *len = count;
            lastError = WSAGetLastError();
            break;
         }
      }

      if( cb > *len - count ) cb = *len - count;
      int resval = recv(socket, (char*)buf, cb, 0);
      if( resval == SOCKET_ERROR )
      {
         lastError = WSAGetLastError();
         break;
      }
      if( !resval )
         break;

      count += resval;
      buf += resval;
      if( pf ) pf->SetPos(count);
   }
   *len = count;
   return (!count) ? false : true; 
}

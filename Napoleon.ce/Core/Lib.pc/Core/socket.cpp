/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Sockets.
 *
 * ert   20/04/2009   creating
 */ 
#include "stdafx.h"
#include <socket.h>

#ifndef UNIX
#include <Ws2tcpip.h>
#endif

#define _WINSOCK_DEPRECATED_NO_WARNINGS

using namespace GRServer;

Socket::~Socket()
{
   Close();
}

void Socket::Close()
{
   if( socket == INVALID_SOCKET )
      return;

   SOCKET sock = socket;
   socket = INVALID_SOCKET;

   if( shutdown(sock,/*SD_SEND=1*/2/*SD_BOTH*/) == SOCKET_ERROR )
      return;
   
   char buf[100];
   while( recv(sock, buf, sizeof(buf), 0) > 0 )
      ;

   closesocket(sock); 
}

bool Socket::Accept(SOCKET srvSock)
{
   int lenfrom = sizeof(address);
   socket = accept(srvSock, (sockaddr*)&address, &lenfrom);
 
   return (socket != INVALID_SOCKET);
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

bool Socket::Connect(const char *ip, WORD port)
{
   bool ret = false;
	if (socket == INVALID_SOCKET)
		socket = ::socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

   if( socket != INVALID_SOCKET )
   {
      sockaddr_in adr = { 0 };
      
      DWORD addr = inet_addr(ip);
      if( addr == INADDR_NONE )
      {
         if( !GetAddr(&adr, ip) )
            return ret;
      } else
      {
         adr.sin_addr.S_un.S_addr = addr;
         adr.sin_family = AF_INET;
      }
      adr.sin_port = htons(port);

      ret = (connect(socket, (const sockaddr*)&adr, sizeof(adr)) == 0);
		if (ret)
		{
			address = adr;
		}
   }

   return ret;
}

bool Socket::WaitData(DWORD timeout, HANDLE evStop)
{
   if( socket == INVALID_SOCKET )
      return false;

	DWORD endTime = GetTickCount() + timeout;
	DWORD ct;

	do 
	{
		DWORD cb = 0;
		ioctlsocket(socket, FIONREAD, &cb);
		if (cb != 0)
			return true;

		DWORD res = WaitForSingleObject(evStop, 10);
		if (res == WAIT_OBJECT_0)
		{
			readState = StopEvent;
			break;
		}
		ct = GetTickCount();
	} while (ct < endTime);

	readState = WaitTimeout;
	return false;

   //HANDLE evRead = WSACreateEvent();
   //WSAEventSelect(socket, evRead, FD_READ);

   //int ctr = 1;
   //HANDLE hh[2];
   //hh[0] = evRead;
   //if( evStop != 0 && evStop != INVALID_HANDLE_VALUE )
   //{
   //   hh[1] = evStop;
   //   ctr++;
   //}

   //DWORD res = WaitForMultipleObjects(ctr, hh, FALSE, timeout);
   //if( res == WAIT_OBJECT_0 + 1 )
   //   readState = StopEvent;
   //else if( res == WAIT_TIMEOUT )
   //   readState = WaitTimeout;

   //WSACloseEvent(evRead);
   //return (res == WAIT_OBJECT_0);
}

bool Socket::ReadBuf(BYTE *buffer, DWORD bufSize, DWORD timeout, HANDLE evStop, int flags)
{
   int res;

   readState = Other;
   while( bufSize > 0 )
   {
      if( !WaitData(timeout, evStop) )
         return false;

      DWORD cb;
      res = ioctlsocket(socket, FIONREAD, &cb);
      if( cb == 0 ) 
         return false;

      if( cb > bufSize ) cb = bufSize;
      res = recv(socket, (char*)buffer, cb, flags);
      if( res == SOCKET_ERROR || res == 0 )
         return false;

      buffer += cb;
      bufSize -= cb;
   }

   readState = Readed;
   return true;
}

bool Socket::Read(Binary *buffer, DWORD timeout, HANDLE evStop)
{
   readState = Other;

   buffer->Clear();

   if( !WaitData(timeout, evStop) )
      return false;

   DWORD cb;
   ioctlsocket(socket, FIONREAD, &cb);
   if( cb == 0 ) 
      return false;

   BYTE *buf = buffer->Alloc(cb);
   int res = recv(socket, (char*)buf, cb, 0);
   if( res == SOCKET_ERROR || res == 0 )
   {
      buffer->Clear();
      return false;
   }

   readState = Readed;
   return true;
}

bool Socket::Write(const BYTE *data, DWORD len)
{
   if( socket == INVALID_SOCKET )
      return false;

   DWORD sent = 0;
   while( sent < len )
   {
      int result = send(socket, (const char*)data + sent, len - sent, 0);

      if( result == SOCKET_ERROR )
         return false;

      sent += result;
   }
   return true;
}

bool Socket::Write(const Packet& packet)
{
   if( Write((BYTE*)packet.head.c_str(), (DWORD)(packet.head.size() * sizeof(wchar_t))) && Write(*packet.data) )
      return true;

   return false;
}

void Socket::CopyTo(Socket *dest)
{
   dest->socket = socket;
   dest->address = address;

   socket = INVALID_SOCKET;
}

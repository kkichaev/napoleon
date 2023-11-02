/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * Sockets.
 *
 * ert   02/01/2013   creating
 */
#include "stdafx.h"
#include <socket.h>
#include <unistd.h>
#include <sys/ioctl.h>

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

   if( shutdown(sock,SHUT_RDWR) == SOCKET_ERROR )
      return;

   char buf[100];
   while( recv(sock, buf, sizeof(buf), 0) > 0 )
      ;

   close(sock);
}

bool Socket::Accept(SOCKET srvSock)
{
   socklen_t lenfrom = sizeof(address);
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
   socket = ::socket(AF_INET, SOCK_STREAM, 0);

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
         adr.sin_addr.s_addr = addr;
         adr.sin_family = AF_INET;
      }
      adr.sin_port = htons(port);

      ret = (connect(socket, (const sockaddr*)&adr, sizeof(adr)) == 0);
   }

   return ret;
}

bool Socket::WaitData(DWORD timeout, HANDLE evStop)
{
   if( socket == INVALID_SOCKET )
      return false;

    timeval to;
    to.tv_sec = timeout / 1000;
    to.tv_usec = timeout % 1000;

    fd_set fd;
    FD_ZERO(&fd);
    FD_SET(socket, &fd);

    int ready = select(socket+1, &fd, NULL, NULL, &to);

    return (ready > 0 && FD_ISSET(socket, &fd) != 0);
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
      res = ioctl(socket, FIONREAD, &cb);
      if( cb == 0 )
         return false;

      if( cb > bufSize ) cb = bufSize;
      res = (int)recv(socket, (char*)buffer, cb, flags);
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
   ioctl(socket, FIONREAD, &cb);
   if( cb == 0 )
      return false;

   BYTE *buf = buffer->Alloc(cb);
   int res = (int)recv(socket, (char*)buf, cb, 0);
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
      int result = (int)send(socket, (const char*)data + sent, len - sent, 0);

      if( result == SOCKET_ERROR )
         return false;

      sent += result;
   }
   return true;
}

bool Socket::Write(const Packet& packet)
{
#ifdef UNIX
   size_t cb = packet.head.size();
   size_t db = (cb) * sizeof(unsigned short);
   char16_t *p = (char16_t*)alloca(db + sizeof(char16_t));
   ConvHelper((const char*)packet.head.c_str(), (char*)p, cb * sizeof(wchar_t), db + sizeof(char16_t), "UTF32", "UTF16");

   if( Write((BYTE*)p, (DWORD)db) && Write(*packet.data) )
      return true;
#else
   if( Write((BYTE*)packet.head.c_str(), packet.head.size() * sizeof(wchar_t)) && Write(*packet.data) )
      return true;
#endif
   return false;
}

void Socket::CopyTo(Socket *dest)
{
   dest->socket = socket;
   dest->address = address;

   socket = INVALID_SOCKET;
}

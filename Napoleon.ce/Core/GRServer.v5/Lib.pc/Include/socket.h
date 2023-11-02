/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * Sockets.
 *
 * ert   20/04/2009   creating
 */ 
#ifndef __GR_SOCKET_H
#define __GR_SOCKET_H

#include <Binary.h>
#include <OutStream.h>

namespace GRServer {

class Socket
{
public:
   enum ReadState { Other, Readed, WaitTimeout, StopEvent };

   Socket() : socket(INVALID_SOCKET), readState(Other) {}
   ~Socket();

   bool Accept(SOCKET srvSock);
   bool Connect(const char* ip, WORD port);

   void Close();

   bool WaitData(DWORD timeout, HANDLE evStop);

   bool Read(Binary *buffer, DWORD timeout, HANDLE evStop);
	bool ReadBuf(BYTE *buffer, DWORD bufSize, DWORD timeout, HANDLE evStop, int flags = 0);

	bool PeekData(BYTE *buffer, DWORD bufSize, DWORD timeout, HANDLE evStop) { 
		return ReadBuf(buffer, bufSize, timeout, evStop, MSG_PEEK);
	}

   bool Write(const Binary &buffer) { return Write(buffer, buffer.Size()); }
   bool Write(const BYTE *data, DWORD len);
   bool Write(const Packet& packet);

   void CopyTo(Socket* dest);

   const sockaddr_in& Address() const { return address; }

   ReadState GetReadState() const { return readState; }

   bool IsConnected() const { return (socket != INVALID_SOCKET); }

   SOCKET GetSocket() const { return socket; }

protected:
   SOCKET socket;
   sockaddr_in address;
   ReadState readState;
};

} // namespace GRServer

#endif

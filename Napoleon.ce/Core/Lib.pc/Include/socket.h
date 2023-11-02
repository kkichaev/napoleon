/*
 * Copyright (C), 2009, Денис Мосягин
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

// сокет для чтения
//
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
   bool Write(const std::wstring& data) { return Write((const BYTE*)data.c_str(), (DWORD)data.size() * sizeof(wchar_t)); }

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

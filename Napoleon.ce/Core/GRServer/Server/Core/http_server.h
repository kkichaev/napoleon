#ifndef __HTTP_SERVER_H
#define __HTTP_SERVER_H

#include <socket.h>
#include "dispatcher.h"


extern "C" DWORD HandleHTTP(GRServer::Socket& socket, GRServer::Dispatcher* dispatcher);
extern "C" DWORD DoHTTPRequest(GRServer::Socket & socket, GRServer::Dispatcher * dispatcher);

namespace GRServer {

class HttpSockStream	
{
public:
	HttpSockStream(Socket& _socket, HANDLE evStop) : socket(_socket), cp(0)
	{
		this->evStop = evStop;
	}

	bool ReadLine(std::string* dest, DWORD timeout);
	bool ReadBody(std::string* out, DWORD timeout, size_t length);

	SOCKET GetSocket() const { return socket.GetSocket(); }

private:
	Socket& socket;
	Binary buffer;
	DWORD cp;
	HANDLE evStop;
};

const DWORD HTTP_READ_TIMEOUT = 10 * 1000; // 10 sec
void URLDecode(std::string* dest, const std::string& src);

}; // namespace GRServer

#endif
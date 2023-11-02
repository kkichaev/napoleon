#ifndef __HTTP_SERVER_H
#define __HTTP_SERVER_H

#include <socket.h>
#include "dispatcher.h"


extern "C" DWORD HandleHTTP(GRServer::Socket& socket, GRServer::Dispatcher* dispatcher);
extern "C" DWORD DoHTTPRequest(GRServer::Socket & socket, GRServer::Dispatcher * dispatcher);

#ifdef UNIX
void StartWebSocket(GRServer::Dispatcher* dispatcher);
void StopWebSocket();
#endif

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

enum class HttpMethod
{
	Undef,
	Get,
	Post,
	Put,
	Delete,
	Head,
};

enum class ContentEncoding
{
	None,
	Gzip,
};

class HttpBaseHeader
{
public:
	HttpBaseHeader();

	std::string version;

	ContentEncoding encoding;

	size_t length;

	bool keepAlive;

	std::map<std::string, std::string> headers;
};


class HttpRcvHeader : public HttpBaseHeader
{
public:
	HttpRcvHeader();

	bool Read(HttpSockStream& ss);

	std::string login;
	std::string password;

	std::string path;
	std::string query;
	HttpMethod method;

	ContentEncoding accepting;

private:
	bool starting;
	bool ParseRequest(const std::string& str);
	
	void LoadCredentials(const std::string& value);
};

struct ResponseCode
{
	int code;
	const char* status;

	enum Code
	{
		OK = 0,				// 200
		Created,				// 201
		NoContent,			// 204
		BadRequest,			// 400
		Unauthorized,		// 401
		Forbidden,			// 403
		NotFound,			// 404
		MethodNotAllowed,	// 405
		NotAcceptable,		// 406
		PayloadTooLarge,	// 413
		UnsuportedMediaType,	// 415
		TooManyRequest,	// 429
		InternalError,		// 500
		NotImplemented,	// 501
	};

	static const ResponseCode* Get(Code code);
};

class HttpResponse
{
public:
	HttpResponse(const HttpRcvHeader& src, const ResponseCode* code, const std::string* body, const std::map<std::string, std::string>& addHeaders);
	~HttpResponse();

	bool Send(GRServer::Socket& socket);

	HttpBaseHeader header;

private:
	const std::string* body;
	std::string response;
};

const DWORD HTTP_READ_TIMEOUT = 10 * 1000; // 10 sec
void URLDecode(std::string* dest, const std::string& src);
extern const char CRLF[];
bool Send(Socket& socket, const std::string& targetURL, GRServer::Dispatcher* dispatcher, bool onlyHeaders, bool keepAlive);

}; // namespace GRServer

#endif
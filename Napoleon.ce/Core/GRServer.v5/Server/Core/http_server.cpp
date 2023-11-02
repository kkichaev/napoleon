/*
* Copyright (C), 2009 - 2022, Denis Mosiagin
*
* HTTP implementation
*
* ert   18/10/2018   creating
*/
#include "stdafx.h"
#include "http_server.h"
#include "dispatcher.h"
#include "srvutility.h"

#include <map>
#include <sys/stat.h>
#include <time.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

const char JPEG_TAG[] = "\xff\xd8";
const char PNG_TAG[] = "\x89\x50\x4E\x47";

const char HTTP_SERVER_VERSION[] = "GRServer 1.0";

const char RFC1123FMT[] = "%a, %d %b %Y %H:%M:%S GMT";

const char JPEG_MIME[] = "image/jpeg";
const char PNG_MIME[] = "image/png";

const char GET_METHOD[] = "GET";
const char HEAD_METHOD[] = "HEAD";
const char HTTP_VERSION[] = "HTTP/1.1";

const char HEADER_CONTENT_LENGTH[] = "Content-Length";
const char HEADER_CONNECTION[] = "Connection";
const char HEADER_MIME[] = "Content-Type";
const char HEADER_SERVER[] = "Server";

const int STATUS_OK = 200;

const int ERR_BAD_REQUEST = 400;
const int ERR_FORBIDDEN = 403;
const int ERR_NOT_FOUND = 404;
const int ERR_METH_NOT_ALLOWED = 405;
const int ERR_UNSUPPORT_MIME = 415;

const int ERR_INT_SERVER = 500;

const char GRServer::CRLF[] = "\r\n";

using namespace GRServer;

class Headers : public std::map<std::string, std::string>
{
public:
	const char* GetHeader(const char* header) const
	{
		Headers::const_iterator fnd = find(header);
		return (fnd == end()) ? "" : fnd->second.c_str();
	}

	bool Read(HttpSockStream& ss);
	void PutHeaders(std::string* res) const;

	void AddHeader(const char* header, const char *value)
	{
		insert(value_type(header, value));
	}

	void AddHeader(const std::string& header, const std::string& value)
	{
		insert(value_type(header, value));
	}

	void AddHeader(const char* header, DWORD value)
	{
		char buf[20];
		sprintf(buf, "%u", value);
		insert(value_type(header, buf));
	}

	void AddHeader(const char *header, time_t time)
	{
		struct tm* res = gmtime(&time);
		char buf[200];
		strftime(buf, sizeof(buf), RFC1123FMT, res);
		insert(value_type(header, buf));
	}
};

bool HttpSockStream::ReadBody(std::string* out, DWORD timeout, size_t length)
{
	if (length == 0)
		return true;

	if (cp < buffer.Size())
	{
		DWORD restSize = buffer.Size() - cp;
		if (restSize > length)
			restSize = (DWORD)length;

		out->append((const char*)((const BYTE*)buffer) + cp, restSize);
		length -= restSize;
		cp += restSize;
	}

	if (length > 0)
	{
		char* buf = (char*)malloc(length);
		if (buf == NULL)
		{
			return false;
		}

		if (!socket.ReadBuf((BYTE*)buf, (DWORD)length, timeout, evStop))
		{
			free(buf);
			return false;
		}
		out->append(buf, length);
		free(buf);
	}

	return true;
}

bool HttpSockStream::ReadLine(std::string* dest, DWORD timeout)
{
	dest->clear();
	while (true)
	{
		if (cp >= buffer.Size())
		{
			if (!socket.Read(&buffer, timeout, evStop))
				return false;
			cp = 0;
			if (cp >= buffer.Size())
				return true;
		}

		const char* sp = (const char*)((const BYTE*)buffer) + cp;
		while (cp < buffer.Size())
		{
			char curChar = *sp;
			if (curChar == '\r' || curChar == '\n')
			{
				if (curChar == '\r' && *(sp + 1) == '\n')
					cp++;
				cp++;
				return true;
			}
			dest->append(1, curChar);
			cp++;
			sp++;
		}
	}
}

inline char FromHEX(char ch)
{
	if (ch <= '9' && ch >= '0')
		ch -= '0';
	else if (ch <= 'f' && ch >= 'a')
		ch -= 'a' - 10;
	else if (ch <= 'F' && ch >= 'A')
		ch -= 'A' - 10;
	else
		ch = 0;
	return ch;
}

void GRServer::URLDecode(std::string* dest, const std::string& src)
{
	std::string::const_iterator i = src.begin();
	while (i != src.end())
	{
		char sym = *i;
		if (sym == '%')
		{
			i++;
			char ch = FromHEX(*i);
			i++;
			char ch1 = FromHEX(*i);
			sym = (ch << 4) + ch1;

		}
		dest->append(1, sym);

		i++;
	}
}

static bool ParseReqLine(const std::string &reqLine, std::string* method, std::string* reqTarget, std::string* version)
{
	std::string::const_iterator i = reqLine.begin();
	std::string* dest = method;
	while (i != reqLine.end())
	{
		char sym = (*i);
		if (sym == ' ')
		{
			if (dest == method)
				dest = reqTarget;
			else if (dest == reqTarget)
				dest = version;
			else
				return false;
		}
		else
			dest->append(1, sym);
		i++;
	}

	return true;
}

static void MakeStatusLine(std::string* stLine, int code, const std::string& msg)
{
	stLine->assign(HTTP_VERSION).append(1, ' ').append(std::to_string(code)).append(1, ' ').append(msg).append(CRLF);
}

void Headers::PutHeaders(std::string* res) const
{
	Headers::const_iterator i = begin();
	for (; i != end(); i++)
	{
		res->append(i->first).append(": ").append(i->second).append(CRLF);
	}
	res->append(CRLF);
}

static bool SendErrorResponse(Socket& socket, int error, const std::string& msg, GRServer::Dispatcher* dispatcher, bool keepAlive)
{
	std::string stline;
	Headers headers;

	headers.AddHeader(HEADER_CONNECTION, keepAlive ? "keep-alive" : "close");
	headers.AddHeader(HEADER_SERVER, HTTP_SERVER_VERSION);
	headers.AddHeader(HEADER_CONTENT_LENGTH, (DWORD)0);
	MakeStatusLine(&stline, error, msg);
	headers.PutHeaders(&stline);

	return socket.Write((const BYTE*)stline.c_str(), (DWORD)stline.size());
}

bool GRServer::Send(Socket& socket, const std::string& targetURL, GRServer::Dispatcher* dispatcher, bool onlyHeaders, bool keepAlive)
{
	const char *imgf = dispatcher->GetConfig().ImageFolder();
	std::string folder;
	std::string target;
	URLDecode(&target, targetURL);
	if (IsLocalName(imgf))
	{
		folder = dispatcher->GetConfig().ExchangeFolder();
		folder += imgf;
	} else
		folder = imgf;

	if (target.find("..") != std::string::npos)
	{
		SendErrorResponse(socket, ERR_FORBIDDEN, "Not found", dispatcher, keepAlive);
		return true;
	}

	USES_CONVERSION;
	const wchar_t* wfn = A2W_CP(target.c_str(), CP_UTF8);
	const char *fn = W2A_CP(wfn, CP_ACP);
	if (*fn == '/')
		folder += (fn + 1);
	else
		folder += fn;

	fn = folder.c_str();
	size_t fileLength = 0;
	FILE *f = NULL;

#ifdef UNIX	
	struct stat st;
	if (stat(fn, &st) == 0)
	{
		f = fopen(fn, "rb");
		fileLength = st.st_size;
	}	
#else
	struct _stat st;
	if (_stat(fn, &st) == 0)
	{
		f = fopen(fn, "rb");
		fileLength = st.st_size;
	}	
#endif	
	if (f == NULL)
	{
		SendErrorResponse(socket, ERR_NOT_FOUND, "Not found", dispatcher, keepAlive);
		return true;
	}

	Binary b;
	BYTE* cp = b.Alloc((DWORD)fileLength);
	if (cp == NULL)
	{
		SendErrorResponse(socket, ERR_INT_SERVER, "No memory", dispatcher, keepAlive);
	}
	else
	{
		std::string mime;
		fileLength = fread(cp, 1, fileLength, f);
		fclose(f);
		f = NULL;

		if (memcmp(cp, JPEG_TAG, sizeof(JPEG_TAG) - 1) == 0)
		{
			mime = JPEG_MIME;
		}
		else if (memcmp(cp, PNG_TAG, sizeof(PNG_TAG) - 1) == 0)
		{
			mime = PNG_MIME;
		}
		else
		{
			SendErrorResponse(socket, ERR_UNSUPPORT_MIME, "Unsupported media type", dispatcher, keepAlive);
		}

		if (mime.empty() == false)
		{
			Headers h;
			time_t now = time(NULL);

			std::string msg;
			Headers headers;

			MakeStatusLine(&msg, STATUS_OK, "OK");
			headers.AddHeader("Date", now);
			headers.AddHeader(HEADER_MIME, mime);
			headers.AddHeader(HEADER_CONTENT_LENGTH, (DWORD)fileLength);
			headers.AddHeader(HEADER_CONNECTION, keepAlive ? "keep-alive" : "close");
			headers.AddHeader(HEADER_SERVER, HTTP_SERVER_VERSION);

			headers.PutHeaders(&msg);
			socket.Write((const BYTE*)msg.c_str(), (DWORD)msg.size());

			if (!onlyHeaders)
				socket.Write(cp, (DWORD)fileLength);
		}
	}

	if (f != NULL)
		fclose(f);

	return true;
}

bool Headers::Read(HttpSockStream& ss)
{
	bool ret = true;
	std::string line;
	while (ss.ReadLine(&line, HTTP_READ_TIMEOUT))
	{
		if (line.empty())
			break;

		// RFC 7231 if header starts with space skip line
		if (*line.begin() == ' ')
			continue;
		
		size_t pos = line.find(':');
		if (pos == std::string::npos)
			continue;

		size_t cp = pos + 1, count = line.size() - pos - 1;
		std::string::iterator i = line.begin() + pos + 1;
		while (i != line.end() && *i == ' ')
		{
			i++;
			cp++;
		}

		if (i == line.end())
			continue;
		std::string::iterator ei = line.end() - 1;
		while (ei > i && *ei == ' ')
		{
			ei--;
			count--;
		}

		std::string key(line.substr(0, pos));
		
		AddHeader(line.substr(0, pos), line.substr(cp, count));
	}
	return ret;
}

extern "C" DWORD HandleHTTP(Socket& socket, GRServer::Dispatcher* dispatcher)
{ 
	dispatcher->AddLog(IErrorLogger::Full, "open http %d", socket.GetSocket());

	bool keepAlive = true;
	while (keepAlive)
	{
		std::string reqLine;
		std::string method, reqTarget, version;
		Headers headers;

		HttpSockStream ss(socket, dispatcher->evStop);

		if (!ss.ReadLine(&reqLine, HTTP_READ_TIMEOUT))
		{
			break;
		}

		if (!ParseReqLine(reqLine, &method, &reqTarget, &version))
		{
			SendErrorResponse(socket, ERR_BAD_REQUEST, "Bad request", dispatcher, keepAlive);
			continue;
		}

		if (!headers.Read(ss))
		{
			SendErrorResponse(socket, ERR_BAD_REQUEST, "Bad headers", dispatcher, keepAlive);
			continue;
		}
		DWORD cl = atol(headers.GetHeader(HEADER_CONTENT_LENGTH));
		if (cl > 0)
		{
			keepAlive = false;
			SendErrorResponse(socket, ERR_BAD_REQUEST, "Bad request", dispatcher, keepAlive);
			break;
		}

		const char* ka = headers.GetHeader(HEADER_CONNECTION);
		keepAlive = (*ka == '\0' || _stricmp(ka, "keep-alive") == 0);

		if (method.compare(GET_METHOD) == 0)
		{
			Send(socket, reqTarget, dispatcher, false, keepAlive);
			dispatcher->AddLog(IErrorLogger::Full, "get %d url %s", socket.GetSocket(), reqTarget.c_str());
		}
		else if (method.compare(HEAD_METHOD) == 0)
			Send(socket, reqTarget, dispatcher, true, keepAlive);
		else
			SendErrorResponse(socket, ERR_METH_NOT_ALLOWED, "Not allowed", dispatcher, keepAlive);

	}

	dispatcher->AddLog(IErrorLogger::Full, "close http %d", socket.GetSocket());
	return 0;
}

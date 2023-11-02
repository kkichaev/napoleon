/*
* Copyright (C), 2009 - 2018, Денис Мосягин
*
* HTTP implementation
*
* ert   18/10/2018   creating
*/
#include "stdafx.h"
#include "http_server.h"
#include "server.h"

#include <zlib.h>

using namespace std;
using namespace GRServer;

static const char* _ws = " \t\n\r\f\v";

// trim from end of string (right)
inline std::string& rtrim(std::string& s, const char* t = _ws)
{
	s.erase(s.find_last_not_of(t) + 1);
	return s;
}

// trim from beginning of string (left)
inline std::string& ltrim(std::string& s, const char* t = _ws)
{
	s.erase(0, s.find_first_not_of(t));
	return s;
}

// trim from both ends of string (right then left)
inline std::string& trim(std::string& s, const char* t = _ws)
{
	return ltrim(rtrim(s, t), t);
}

enum class HttpMethod 
{
	Undef,
	Get,
	Post,
	Put,
	Delete,
};

enum class ContentEncoding
{
	None,
	Gzip,
};

static const std::string cntEncoding("Content-Encoding");
static const std::string cntLength("Content-Length");
static const std::string cntType("Content-*Type");

class Header
{
public:
	Header();

	bool Read(HttpSockStream& ss);

	string path;
	string query;

	string version;

	HttpMethod method;
	ContentEncoding encoding;

	size_t length;

	map<string, string> headers;

private:
	bool starting;

	bool ParseRequest(const std::string& str);
};

Header::Header() : encoding(ContentEncoding::None), method(HttpMethod::Undef), length(0), starting(true)
{

}

bool Header::ParseRequest(const std::string& str)
{
	size_t pos = str.find_first_of(' ');
	if (pos == string::npos)
	{
		return false;
	}

	const string& tmth = str.substr(0, pos+1);
	const char* mth = tmth.c_str();
	if (_stricmp(mth, "get") == 0) method == HttpMethod::Get;
	else if (_stricmp(mth, "put") == 0) method == HttpMethod::Put;
	else if (_stricmp(mth, "post") == 0) method == HttpMethod::Post;
	else if (_stricmp(mth, "delete") == 0) method == HttpMethod::Delete;
	else
	{
		return false;
	}

	// read request
	pos = str.find_first_not_of(' ', pos);
	size_t ep = str.find_first_of(' ', pos);
	if (ep == string::npos)
	{
		return false;
	}

	string tstr;
	URLDecode(&tstr, str.substr(pos, (ep - pos)));
	pos = tstr.find('?');
	path = tstr.substr(0, pos + 1);
	if (pos != string::npos) query = tstr.substr(pos + 1);

	pos = str.find_first_not_of(' ', ep);
	if (pos == string::npos)
	{
		return false;
	}

	version = str.substr(pos);

	return true;
}

bool Header::Read(HttpSockStream& ss)
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

		if (starting)
		{
			starting = false;
			if (!ParseRequest(line))
			{
				gServer->AddLog("Socket (%d) wrong http request %s", ss.GetSocket(), line.c_str());
				return false;
			}
		}

		size_t pos = line.find(':');
		if (pos == std::string::npos)
			continue;

		std::string& key = rtrim(line.substr(0, pos));
		std::string& value = trim(line.substr(pos + 1));

		if (key.compare(cntLength) == 0)
		{
			length = atoi(value.c_str());
		}
		else if (key.compare(cntEncoding) == 0)
		{
			if (_stricmp(value.c_str(), "gzip") == 0)
				encoding = ContentEncoding::Gzip;
		}
		else
		{
			headers[key] = value;
		}
	}

	return true;
}

struct GZipFlag
{
	WORD text : 1;
	WORD crc : 1;
	WORD extra : 1;
	WORD name : 1;
	WORD comment : 1;
};

static const char* GZipDataPtr(const string& src)
{
	const char* p = src.c_str(), *ep = p + src.size();
	GZipFlag* flg = (GZipFlag*)(p + 3);

	p += 10;
	if(flg->extra != 0) 
	{
		WORD sz = *(WORD*)p;
		p += (sz + 2);
	}

	if (flg->name != 0)
	{
		while (*p++);
	}
	if (p >= ep)
		return NULL;

	if(flg->text != 0)
	{
		while (*p++);
	}
	if (p >= ep)
		return NULL;

	if (flg->crc != 0)
		p += 2;

	return p;
}

static bool UnGZip(string* out, const string& src)
{
	const char* ep = src.c_str() + src.size() - 8;
	const char* ptr = GZipDataPtr(src);
	if (ptr == NULL || ptr >= ep - 8)
		return false;

	DWORD crc = *((DWORD*)ep);
	DWORD len = *((DWORD*)ep + 1);

	z_stream stream;

	stream.zalloc = NULL;
	stream.zfree = NULL;
	stream.opaque = NULL;

	inflateInit(&stream);

	stream.avail_in = ep - ptr;
	stream.next_in = (BYTE*)ptr;

	size_t bsize = 10 * 1024;
	char* buf = (char*)malloc(bsize);

	bool retVal = false;
	while (true)
	{
		stream.avail_out = bsize;
		stream.next_out = (BYTE*)buf;

		int rc = inflate(&stream, Z_NO_FLUSH);
		if (rc == Z_STREAM_END || rc == Z_OK)
		{
			size_t writed = bsize - stream.avail_out;
			out->append(buf, writed);
			if (stream.avail_out > 0)
			{
				retVal = true;
				break;
			}
		}
		else
		{
			retVal = false;
		}
	}

	DWORD crcCmp = stream.adler;
	inflateEnd(&stream);
	free(buf);

	return crcCmp == crc;
}

static bool ParseRequest(Header& header, string& body, GRServer::Socket& socket, GRServer::Dispatcher* dispatcher)
{
	HttpSockStream ss(socket, dispatcher->evStop);

	if (!header.Read(ss))
	{
		return false;
	}
	if (header.length > 0)
	{
		string tstr;
		string* ptr = (header.encoding == ContentEncoding::Gzip) ? &tstr : &body;
		if (!ss.ReadBody(ptr, HTTP_READ_TIMEOUT, header.length))
		{
			gServer->AddLog(IErrorLogger::Full, "Socket (%d) can't read body %d bytes", socket.GetSocket(), header.length);
			return false;
		}
		if (header.encoding == ContentEncoding::Gzip)
		{
			if (!UnGZip(&body, tstr))
			{
				gServer->AddLog(IErrorLogger::Full, "Socket (%d) can't unzip body", socket.GetSocket());
				return false;
			}
		}
	}

	return true;
}

DWORD DoHTTPRequest(GRServer::Socket& socket, GRServer::Dispatcher* dispatcher)
{
	Header header;
	string body;

	if (!ParseRequest(header, body, socket, dispatcher))
	{
		return 1;
	}

	// have header & body;
	return 0;
}
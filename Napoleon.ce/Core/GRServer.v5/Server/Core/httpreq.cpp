/*
* Copyright (C), 2009 - 2022, Denis Mosiagin
*
* HTTP implementation
*
* ert   18/10/2018   creating
*/
#include "stdafx.h"
#include "http_server.h"
#include "server.h"

#include <zlib.h>
#include <sstream>

#include "srvutility.h"

#include "session.h"
#include "sessobj.h"

#include "json.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace std;
using namespace GRServer;

// trim from end of string (right)

const size_t MIN_COMPRESSION_SIZE = 100; // min bytes for start compression

static const std::string cntEncoding("content-encoding");
static const std::string acceptEncoding("accept-encoding");
static const std::string cntLength("content-length");
static const std::string cntType("content-type");
static const std::string authtTag("authorization");
static const std::string connectionTag("connection");
static const std::string jsonType("application/json; charset=utf-8");
static const std::string authBasic("basic");
static const std::string authBearer("bearer");
static const std::string wwwAuth("www-authenticate");
static const char START_OBJECT_TAG[] = "/object/";
static const std::string uploadPath("/upload/");


static ResponseCode codes[] =
{
	{200, "OK"},
	{201, "Created"},
	{204, "No Content"},
	{400, "Bad Request"},
	{401, "Unauthorized"},
	{403, "Forbidden"},
	{404, "Not Found"},
	{405, "Method Not Allowed"},
	{406, "Not Acceptable"},
	{413, "Payload Too Large"},
	{415, "Unsuported Media Type"},
	{429, "Too Many Request"},
	{500, "Internal Error"},
	{501, "Not Implemented"},
};

const ResponseCode* ResponseCode::Get(Code code)
{
	if ((int)code < sizeof(codes) / sizeof(codes[0]))
		return &codes[(int)code];
	
	return NULL;
}


HttpBaseHeader::HttpBaseHeader() :
	encoding(ContentEncoding::None), length(0), keepAlive(true)
{

}

HttpRcvHeader::HttpRcvHeader() :
	accepting(ContentEncoding::None),
	method(HttpMethod::Undef), starting(true)

{

}

bool HttpRcvHeader::ParseRequest(const std::string& str)
{
	size_t pos = str.find_first_of(' ');
	if (pos == string::npos)
	{
		return false;
	}

	const string& tmth = str.substr(0, pos);
	const char* mth = tmth.c_str();
	if (_stricmp(mth, "get") == 0) method = HttpMethod::Get;
	else if (_stricmp(mth, "put") == 0) method = HttpMethod::Put;
	else if (_stricmp(mth, "post") == 0) method = HttpMethod::Post;
	else if (_stricmp(mth, "delete") == 0) method = HttpMethod::Delete;
	else if (_stricmp(mth, "head") == 0) method = HttpMethod::Head;
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

	path = tstr.substr(0, pos);
	if (pos != string::npos)
	{
		query = tstr.substr(pos + 1);
	}

	pos = str.find_first_not_of(' ', ep);
	if (pos == string::npos)
	{
		return false;
	}

	version = str.substr(pos);

	return true;
}

inline ContentEncoding ParseEncoding(const std::string& value)
{
	std::string tstr;
	if( to_upper(tstr, value).find("GZIP") != string::npos)
		return ContentEncoding::Gzip;

	return ContentEncoding::None;
}

void HttpRcvHeader::LoadCredentials(const std::string& value)
{
	size_t pos = value.find_first_not_of(WhiteSpaces);
	if (pos == string::npos)
		return;

	const std::string& auth = value.substr(pos);
	if (is_same_text(auth, authBasic))
	{
		if ((pos = auth.find_first_not_of(WhiteSpaces, authBasic.size())) != string::npos)
		{
			std::string cred = base64_decode(auth.substr(pos));
			pos = cred.find(':');
			if (pos != string::npos)
			{
				login = cred.substr(0, pos);
				password = cred.substr(pos + 1);
			}
		}
	}
	else if (is_same_text(auth, authBearer))
	{
		if ((pos = auth.find_first_not_of(WhiteSpaces, authBearer.size())) != string::npos)
		{
			USES_CONVERSION;
			login = W2A_CP(COM_LOGIN, CP_UTF8);;
			password = auth.substr(pos);
		}
	}
}

bool HttpRcvHeader::Read(HttpSockStream& ss)
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
			continue;
		}

		size_t pos = line.find(':');
		if (pos == std::string::npos)
			continue;

		std::string key(line.substr(0, pos));
		rtrim(key);
		for (std::string::iterator ki = key.begin(); ki != key.end(); ki++) {
			*ki = tolower(*ki);
		}

		std::string value(line.substr(pos + 1));
		trim(value);

		if (key.compare(cntLength) == 0)
		{
			length = atoi(value.c_str());
		}
		else if (key.compare(cntEncoding) == 0)
		{
			encoding = ParseEncoding(value);
		}
		else if (key.compare(acceptEncoding) == 0)
		{
			accepting = ParseEncoding(value);
		}
		else if (key.compare(connectionTag) == 0)
		{
			if (_stricmp(value.c_str(), "keep-alive") != 0)
				keepAlive = false;
		}
		else if (key.compare(authtTag) == 0)
		{
			LoadCredentials(value);
		}
		else
		{
			headers[key] = value;
		}
	}

	return true;
}

static bool UnGZip(string* out, const string& src)
{
	const char* ep = src.c_str() + src.size() - 8;
	//const char* ptr = GZipDataPtr(src);
	const char* ptr = src.c_str();
	if (ptr == NULL || ptr >= ep - 8)
		return false;

	DWORD crc = *((DWORD*)ep);
	DWORD len = *((DWORD*)ep + 1);

	z_stream stream;

	stream.zalloc = NULL;
	stream.zfree = NULL;
	stream.opaque = NULL;

	inflateInit2(&stream, 31);

	stream.avail_in = (uInt)(ep - ptr);
	stream.next_in = (BYTE*)ptr;

	size_t bsize = 10 * 1024;
	char* buf = (char*)malloc(bsize);

	bool retVal = false;
	while (true)
	{
		stream.avail_out = (uInt)bsize;
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
			break;
		}
	}

	DWORD crcCmp = stream.adler;
	inflateEnd(&stream);
	free(buf);

	return crcCmp == crc;
}


static std::string* CompressBody(const std::string& src)
{
	z_stream stream;

	stream.zalloc = NULL;
	stream.zfree = NULL;
	stream.opaque = NULL;


	deflateInit(&stream, Z_DEFAULT_COMPRESSION);
	stream.next_in = (BYTE*)src.c_str();
	stream.avail_in = (uInt)src.size();

	stream.next_out = (BYTE*)src.c_str();
	stream.avail_out = (uInt)src.size();

	deflateInit2(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED, 31, 8, Z_DEFAULT_STRATEGY);

	std::string* out = new string();

	size_t bsize = 10 * 1024;
	char* buf = (char*)malloc(bsize);
	stream.avail_out = (uInt)bsize;
	stream.next_out = (BYTE*)buf;

	bool ret = true;
	int flag = Z_NO_FLUSH;
	do
	{
		int rc = deflate(&stream, flag);
		if (rc != Z_OK && rc != Z_STREAM_END)
		{
			ret = false;
			break;
		}

		if (stream.avail_out != 0)
		{
			if (flag == Z_NO_FLUSH)
				flag = Z_FINISH;
		}

		out->append(buf, bsize - stream.avail_out);
		stream.avail_out = (uInt)bsize;
		stream.next_out = (BYTE*)buf;

		if (rc == Z_STREAM_END)
			break;
	} while (true);

	free(buf);
	if (!ret)
	{
		delete out;
		out = NULL;
	}
	return out;
}



static bool ParseRequest(HttpRcvHeader& header, string& body, GRServer::Socket& socket, GRServer::Dispatcher* dispatcher)
{
	HttpSockStream ss(socket, dispatcher->evStop);

	bool ret = true;

	try 
	{
		ret = header.Read(ss);
	}
	catch (...)
	{
		ret = false;
	}
	if (!ret)
	{
		return ret;
	}

	if (header.length > 0)
	{
		string tstr;
		string* ptr = (header.encoding == ContentEncoding::Gzip) ? &tstr : &body;
		if (!ss.ReadBody(ptr, HTTP_READ_TIMEOUT, header.length))
		{
			gServer->AddLog(IErrorLogger::Full, "Socket (%d) can't read body %d bytes"
				, socket.GetSocket()
				, header.length
			);
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

HttpResponse::HttpResponse(const HttpRcvHeader& src, const ResponseCode* rc, const std::string* _body,
	const std::map<std::string, std::string>& addHeaders) : body(NULL)
{
	std::stringstream ss;
	if (rc == NULL)
		rc = ResponseCode::Get(ResponseCode::Code::InternalError);

	// ss << src.version << ' ' << rc->code << ' ' << rc->status;
	ss << "Status: " << rc->code << ' ' << rc->status;
	response = ss.str();

	header.version = src.version;
	header.keepAlive = src.keepAlive;

	if (src.accepting == ContentEncoding::Gzip && _body != NULL && _body->size() >= MIN_COMPRESSION_SIZE)
	{
		header.encoding = ContentEncoding::Gzip;
		body = CompressBody(*_body);

		delete _body;
	}
	else
	{
		body = _body;
	}

	header.length = (body != NULL) ? body->length() : 0;

	for (map<string, string>::const_iterator i = addHeaders.begin(); i != addHeaders.end(); i++)
		header.headers[i->first] = i->second;
}

static const char* months[] = {
	"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
};

static const char* wdays[] = {
	"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
};

static const std::string MakeDate()
{
	stringstream ss;
	SYSTEMTIME st;
	GetLocalTime(&st);

	ss << wdays[st.wDayOfWeek] << ", " << st.wDay << " " << months[st.wMonth - 1] << " " << st.wYear
		<< " " << st.wHour << ":" << st.wMinute << ":" << st.wSecond << " GMT";

	return ss.str();
}

bool HttpResponse::Send(GRServer::Socket& socket)
{
	std::stringstream ss;
	ss << response << CRLF;
	ss << "Date: " << MakeDate() << CRLF;

	if (header.length > 0)
	{
		ss << cntType << ": " << jsonType << CRLF;
		ss << cntLength << ": " << header.length << CRLF;
		if(header.encoding == ContentEncoding::Gzip)
			ss << cntEncoding  << ": gzip" << CRLF;
	}

	for (map<string, string>::const_iterator i = header.headers.begin(); i != header.headers.end(); i++)
	{
		ss << i->first << ": " << i->second << CRLF;
	}
	ss << CRLF;

	const string& out = ss.str();
	socket.Write((const BYTE*)out.c_str(), (DWORD)out.size());
	if (header.length > 0)
	{
		socket.Write((const BYTE*)body->c_str(), (DWORD)body->size());
	}

	return true;
}

HttpResponse::~HttpResponse()
{
	delete body;
}

//
//  [{"name":"ServerAnswer", "data"[{"response":<ReponseVal>, "message":<Text>}]}]
//
static std::string* MakeErrorResponse(const ResponseCode** response, const std::string& message
	, ResponseCode::Code code = ResponseCode::BadRequest)
{
	*response = ResponseCode::Get(code);

	JSONObject *sa = new JSONObject();
	sa->Put("response", 0);
	sa->Put("message", message);

	JSONArray* saa = new JSONArray();
	saa->push_back(new JSONValue(sa));

	JSONObject* res = new JSONObject();
	res->Put("name", "ServerAnswer");
	res->Put("data", saa);

	JSONArray out;
	out.push_back(new JSONValue(res));

	stringstream ss;
	out.dump(&ss);

	return new string(ss.str());
}

static bool RetriveCommandParam(Member* out, const HttpRcvHeader& header)
{
	stringstream ss(header.path.substr(1)); // remove trail /
	string str, obj;

	if (!(getline(ss, str, '/') && getline(ss, obj, '/')))
	{
		return false;
	}

	string query(obj + ":" + header.query);

	USES_CONVERSION;
	out->str->assign(A2W_CP(query.c_str(), CP_UTF8));
	return true;
}

static std::string* HandleGetRequest(const ResponseCode** response, const HttpRcvHeader& header
	, GRServer::Dispatcher* dispatcher
	, Socket* socket)
{
	CString buf;
	Member command;
	command.str = &buf;

	if(!RetriveCommandParam(&command, header))
	{
		return MakeErrorResponse(response, "Bad request");
	}

	std::string* out = NULL;
	JSONWriter w;

	Session* session = new Session(dispatcher);

	session->AssignSocket(socket, dispatcher->evStop);
	session->SetWriter(&w);

	if (!session->Auth(header.login, header.password))
	{
		out = MakeErrorResponse(response, "No user", ResponseCode::NotFound);
	}
	else
	{
		session->Selecting(&command);

		out = new string();
		session->FlushJSONWriter(out);

		*response = ResponseCode::Get(ResponseCode::OK);
	}

	delete session;

	return out;
}

static std::string* HandleDeleteRequest(const ResponseCode** response, const HttpRcvHeader& header
	, GRServer::Dispatcher* dispatcher
	, Socket* socket)
{
	CString buf;
	Member command;
	command.str = &buf;

	if (!RetriveCommandParam(&command, header))
	{
		return MakeErrorResponse(response, "Bad request");
	}

	std::string* out = NULL;
	JSONWriter w;

	Session* session = new Session(dispatcher);

	session->AssignSocket(socket, dispatcher->evStop);
	session->SetWriter(&w);

	if (!session->Auth(header.login, header.password))
	{
		out = MakeErrorResponse(response, "No user", ResponseCode::NotFound);
	}
	else
	{
		session->Removing(&command);

		out = new string();
		session->FlushJSONWriter(out);

		*response = ResponseCode::Get(ResponseCode::OK);
	}

	delete session;

	return out;
}

static std::string* HandlePostRequest(const ResponseCode** response, const HttpRcvHeader& header
	, GRServer::Dispatcher* dispatcher
	, Socket* socket
	, const std::string& body
	, bool deleteBefore)
{
	stringstream ss(header.path.substr(1));
	string path, objName;

	if (!getline(ss, path, '/'))
	{
		return MakeErrorResponse(response, "Bad request");
	}
	getline(ss, objName, '/');

	string* out = NULL;

	JSONReader r;
	JSONValue* data = NULL;
	USES_CONVERSION;

	if(path.compare("set_blocked") == 0)
	{
		// only put request
		if(deleteBefore)
		{
			data = r.Parse(body);
			if(data != NULL && data->IsArray())
			{
				std::set<std::wstring> src;
				USES_CONVERSION;

				JSONArray::const_iterator ci = data->value.array->begin();
				for( ; ci != data->value.array->end(); ci++)
				{
					if((*ci)->IsString())
					{
						src.insert(A2W_CP((*ci)->value.string->c_str(), CP_UTF8));
					}
				}
				dispatcher->UpdateBlocked(src);
			}
		}
		return NULL;
	}

	bool upload2 = (uploadPath.find(path, 1) == 1);
	if (!upload2)
	{
		data = r.Parse(body);
		if (data == NULL)
		{
			std::string err;
			r.GetError(&err);
			gServer->AddLog(IErrorLogger::Full, "Socket (%d) bad post body %s", socket->GetSocket(), err.c_str());
			return MakeErrorResponse(response, "Bad request");
		}
	}

	JSONWriter w;
	Session* session = new Session(dispatcher);

	session->AssignSocket(socket, dispatcher->evStop);
	session->SetWriter(&w);

	if (!session->Auth(header.login, header.password))
	{
		out = MakeErrorResponse(response, "No user", ResponseCode::NotFound);
	} 
	else if (_stricmp(path.c_str(), "object") == 0)
	{
		if (!data->IsArray())
		{
			out = MakeErrorResponse(response, "Params must be a array");
		}
		else
		{
			session->PutObjects(*data, deleteBefore);

			*response = ResponseCode::Get(ResponseCode::OK);
		}
	}
	else if (_stricmp(path.c_str(), "file") == 0)
	{
		std::wstring url;

		gServer->AddLog(IErrorLogger::Full, "Socket(%d) upload file request %s", 
			socket->GetSocket(),
			objName.c_str());

		if (session->RequestUpload(&url, A2W_CP(objName.c_str(), CP_UTF8), A2W_CP(body.c_str(), CP_UTF8)))
		{
			// sends url as body add upload tag
			out = new std::string(uploadPath);
			out->append(W2A_CP(url.c_str(), CP_UTF8));
			*response = ResponseCode::Get(ResponseCode::OK);
		}
		else
		{
			*response = ResponseCode::Get(ResponseCode::BadRequest);
		}
	}
	else if(_stricmp(path.c_str(), "query") == 0) 
	{
		if(!data->IsArray())
		{
			return MakeErrorResponse(response, "Params must be a array");
		}

		CString buf;
		Member command;
		command.str = &buf;

		std::string* out = new string();
		JSONWriter w;

		session->SetWriter(&w);
		USES_CONVERSION;
		const JSONArray& src = *data->value.array;
		for (JSONArray::const_iterator i = src.begin(); i != src.end(); i++)
		{
			if (!(*i)->IsObject()) continue;
			std::string objName, filter;
			if(!(*i)->read(&objName, "name"))
			{
				continue;
			}
			objName.append(1, ':');
			(*i)->read(&filter, "filter");
			if(!filter.empty())
				objName.append(filter);
			
			command.str->assign(A2W_CP(objName.c_str(), CP_UTF8));
			session->Selecting(&command);
			session->FlushJSONWriter(out);
			*response = ResponseCode::Get(ResponseCode::OK);
		}

	}
	else if (upload2)
	{
		if((body.size() / 1024) > dispatcher->GetConfig().UploadLimit()) 
		{
			out = MakeErrorResponse(response, "Upload limit exceeded", ResponseCode::PayloadTooLarge);
		} else
		{
			if (session->SaveFile(A2W_CP(objName.c_str(), CP_UTF8), body))
			{
				*response = ResponseCode::Get(ResponseCode::OK);
			}
			else
			{
				*response = ResponseCode::Get(ResponseCode::BadRequest);
			}
		}
	}
	else
	{
		if (!data->IsObject())
		{
			out = MakeErrorResponse(response, "Bad request");
		}
		else
		{
			CString buf;
			Member rname;
			rname.str = &buf;
			buf.assign(A2W_CP(objName.c_str(), CP_UTF8));

			JSONObject repCmd;
			repCmd.Put("command", "Get Report");
			session->PushToAck(repCmd); // fake object
			session->PushToAck(*data->value.object);
			if(session->HandleCommand(GET_REPORT, &rname)) {
				session->PostObjects();
			}
		}
	}

	if (out == NULL)
	{
		out = new string();
		session->FlushJSONWriter(out);
	}

	delete session;
	delete data;

	return out;
}

DWORD DoHTTPRequest(GRServer::Socket& socket, GRServer::Dispatcher* dispatcher)
{
	HttpRcvHeader header;
	string body;

	HttpResponse* response = NULL;
	const ResponseCode* code = NULL;
	std::string* rbody = NULL;
	std::map <std::string, std::string> headersAdd;

	if (!ParseRequest(header, body, socket, dispatcher))
	{
		rbody = MakeErrorResponse(&code, "server error", ResponseCode::Code::InternalError);
	}
	else
	{
		gServer->AddLog(IErrorLogger::Full, "Got http %d request with path (%s), query(%s)"
			, (int)header.method
			, header.path.c_str()
			, header.query.c_str()
		);
		// have header & body;
		
		if (header.login.empty())
		{
			code = ResponseCode::Get(ResponseCode::Code::Unauthorized);
			headersAdd[wwwAuth] = "Basic realm=\"User Visible Realm\", charset = \"UTF-8\"";
		}
		else
		{
			if (header.method == HttpMethod::Get)
			{
				if (_stricmp(header.path.substr(0, sizeof(START_OBJECT_TAG) - 1).c_str(), START_OBJECT_TAG) != 0)
				{
					// get image
					return Send(socket, header.path, dispatcher, false, header.keepAlive);
				} else
					rbody = HandleGetRequest(&code, header, dispatcher, &socket);
			} else if (header.method == HttpMethod::Delete)
			{
				rbody = HandleDeleteRequest(&code, header, dispatcher, &socket);
			}
			else if (header.method == HttpMethod::Post || header.method == HttpMethod::Put)
			{
				rbody = HandlePostRequest(&code, header, dispatcher, &socket, body, (header.method == HttpMethod::Put));
			}
			else if (header.method == HttpMethod::Head)
			{
				return Send(socket, header.path, dispatcher, true, header.keepAlive);
			}
			else
			{
				rbody = MakeErrorResponse(&code, "not implemented yet", ResponseCode::Code::NotImplemented);
			}
		}
	}

	response = new HttpResponse(header, code, rbody, headersAdd);
	response->Send(socket);
	delete response;

	return 0;
}

#ifdef UNIX
#include <sys/un.h>

struct WSParams
{
	pthread_t handle;
	std::string sock;
	GRServer::Dispatcher* disp;
};

class HTTPWorker : public IThreadWorker
{
public:
	HTTPWorker(int socket, GRServer::Dispatcher* _disp) : disp(_disp)
	{
		s.Accept(socket);
	}
	~HTTPWorker() {}

   virtual DWORD Execute();

private:
	GRServer::Dispatcher* disp;
	Socket s;
};

DWORD HTTPWorker::Execute()
{
	DoHTTPRequest(s, disp);
	return 0;
}


static int wsSock;
static bool exiting = false;
void* WSLoop(WSParams* params)
{
	if(params->sock.size() >= sizeof(((sockaddr_un*)0)->sun_path) )
	{
		params->sock = params->sock.substr(0, sizeof(((sockaddr_un*)0)->sun_path) - 1);
	}

	const char *sn = params->sock.c_str();
	unlink(sn);

	wsSock = socket(AF_UNIX, SOCK_STREAM, 0);
	
	sockaddr_un address;
	address.sun_family = AF_UNIX;
	strcpy(address.sun_path, sn);

	if(bind(wsSock, (sockaddr*)&address, sizeof(address)) < 0 ||
		listen(wsSock, 10) < 0)
	{
		gServer->AddLog("Error start http %d", errno);		
	}

	chmod(sn, S_IRUSR|S_IRGRP|S_IROTH|S_IWUSR|S_IWGRP|S_IWOTH);
	gServer->AddLog(IErrorLogger::Full, "Starting http listener on %s", address.sun_path);

	while(!exiting)
	{
		fd_set rfds;
		struct timeval tv;
		int retval;

		FD_ZERO(&rfds);
		FD_SET(wsSock, &rfds);

		tv.tv_sec = 0;
		tv.tv_usec = 1000;

		retval = select(wsSock + 1, &rfds, NULL, NULL, &tv);
		
		if(retval < 0 || exiting)
		{
			if(!exiting)
				gServer->AddLog(IErrorLogger::Full, "retval %d", retval);
			break;
		}
		if(retval == 0) continue;

		gServer->AddLog(IErrorLogger::Full, "Accept http");
		HTTPWorker *hw = new HTTPWorker(wsSock, params->disp);
		std::string error;
		if(!Thread::Starting(hw, params->disp, 100, false, &error))
		{
			delete hw;
		}
	}

	gServer->AddLog(IErrorLogger::Full, "Exit http loop");
	delete params;
	return NULL;
}

void StartWebSocket(GRServer::Dispatcher* dispatcher)
{
	const GRServer::ServerConfig& cfg = (const GRServer::ServerConfig&)dispatcher->GetConfig();
	if(cfg.webSocket.empty())
		return;

	WSParams* p = new WSParams();
	p->sock = cfg.webSocket;
	p->disp = dispatcher;

    pthread_create(&p->handle, NULL, (void *(*) (void *))WSLoop, p);
}

void StopWebSocket()
{
	exiting = true;
	shutdown(wsSock, SHUT_RDWR);
	close(wsSock);
}

#endif

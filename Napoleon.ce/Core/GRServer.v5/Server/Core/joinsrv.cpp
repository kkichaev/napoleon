/*
* Copyright (C), 2009-2022, Denis Mosiagin
*
* Join Server
*
* ert   08/05/2020   creating
*/

#include "stdafx.h"
#include "joinsrv.h"
#include "server.h"
#include <socket.h>

#include "curl_service.h"
#include "json.h"

#include <ws2tcpip.h>
#include <string>

#include <time.h>


using namespace std;
using namespace GRServer;

const DWORD TEST_TIMEOUT = 60 * 1000;

const unsigned JS_PACKET_TAG = 'GRJS';
const unsigned SERVER_CONNECT_CMD = 'CNCT';
const unsigned CLIENT_CONNECT_CMD = 'CLTC';
const unsigned OK_CMD = 'OKCM';
const unsigned REJECT_CMD = 'RJCT';

const unsigned DATA_CMD = 'DATA';
const unsigned CLOSE_CMD = 'CLOS';
const unsigned PING_CMD = 'PING';

const DWORD MAX_PACKET_LEN = 13000;

static JoinServer::Status status = JoinServer::None;
static HANDLE hJS;
static HANDLE evStop;
static DWORD serverID;
static std::string jsAddr;
static WORD jsPort;
static std::string jsError;
static JoinServerStatusHandler *jsHandler;

struct Command
{
	DWORD command;
	DWORD id;
	DWORD dataLen;
	unsigned char *data;

	Command();
	~Command();

	bool Read(Socket& sock, DWORD timeout, HANDLE evStop);

	bool Read(SOCKET socket);

	static bool Send(SOCKET socket, DWORD command, DWORD id, unsigned char* data, DWORD dataLen);
	static bool Send(Socket& socket, DWORD command, DWORD id, const std::string& message)
	{
		return Send(socket.GetSocket(), command, id, (unsigned char*)message.c_str(), (DWORD)message.size());
	}
	static bool Send(SOCKET socket, DWORD command, DWORD id, const std::string& message)
	{
		return Send(socket, command, id, (unsigned char*)message.c_str(), (DWORD)message.size());
	}
	static bool Send(Socket& socket, DWORD command, DWORD id, unsigned char* data, size_t dataLen)
	{
		return Send(socket.GetSocket(), command, id, data, (DWORD)dataLen);
	}
};

Command::Command() : command(0), dataLen(0), data(NULL), id(0)
{
}

Command::~Command()
{
	free(data);
}

bool Command::Read(SOCKET socket)
{
	DWORD head[4];
	int rc = recv(socket, (char*)head, sizeof(head), 0);
	if (rc < sizeof(head))
		return false;
	if ( ntohl(*head) != JS_PACKET_TAG)
		return false;

	command = ntohl(head[1]);
	id = ntohl(head[2]);
	dataLen = ntohl(head[3]);

	bool ret = true;
	if (dataLen > 0)
	{
		data = (unsigned char*)malloc(dataLen);
		rc = recv(socket, (char*)data, dataLen, 0);
		ret = (rc == dataLen);
	}
	return ret;
}

bool Command::Read(Socket& socket, DWORD timeout, HANDLE evStop)
{
	DWORD head[4];
	if (!socket.ReadBuf((BYTE*)&head, sizeof(head), timeout, evStop))
		return false;

	if (ntohl(*head) != JS_PACKET_TAG)
		return false;

	command = ntohl(head[1]);
	id = ntohl(head[2]);
	dataLen = ntohl(head[3]);

	if (dataLen > 0)
	{
		data = (unsigned char*)malloc(dataLen);
		return socket.ReadBuf(data, dataLen, timeout, evStop);
	}

	return true;
}

bool Command::Send(SOCKET socket, DWORD command, DWORD id, unsigned char* data, DWORD dataLen)
{
	DWORD cb = dataLen + sizeof(DWORD) * 4;
	char* buf = (char*)malloc(cb);

	*((DWORD*)buf) = htonl(JS_PACKET_TAG);
	*((DWORD*)buf + 1) = htonl(command);
	*((DWORD*)buf + 2) = htonl(id);
	*((DWORD*)buf + 3) = htonl(dataLen);

	if (dataLen > 0)
		memcpy(buf + sizeof(DWORD) * 4, data, dataLen);

	send(socket, buf, cb, 0);
	return true;
}

static void SetServerStatus(JoinServer::Status newStatus)
{
	if (status != newStatus)
	{
		status = newStatus;
		if (jsHandler != NULL)
		{
			jsHandler->OnStatusChange(status);
		}
	}
}

struct JSThreadParam
{
	std::string login;
	std::string password;
	std::string project;
	WORD port;
	bool checkOnly;
};

class ClientHandler
{
public:
	SOCKET joinSocket, grSocket;
	DWORD id;

	ClientHandler(DWORD id, SOCKET joinSocket, SOCKET grSocket);
	~ClientHandler();

	// read GRServer socket, sends command to JS
	bool Read(SOCKET s);

	bool IsClosed() const { return isClosed; }

private:
	bool isClosed;
};

ClientHandler::ClientHandler(DWORD _id, SOCKET _joinSocket, SOCKET _grSocket) : isClosed(false), joinSocket(_joinSocket), grSocket(_grSocket)
{
	this->id = _id;
}

ClientHandler::~ClientHandler()
{
	closesocket(joinSocket);
	closesocket(grSocket);
}

bool ClientHandler::Read(SOCKET s)
{
	u_long value;
	ioctlsocket(s, FIONREAD, &value);
	if (value == 0)
	{
		isClosed = true;
		return false;
	}

	char *buf = (char*)malloc(value);
	DWORD cb = recv(s, buf, value, 0);
	if ((int)cb > 0)
	{
		SOCKET dest = (s == grSocket) ? joinSocket : grSocket;
		send(dest, buf, cb, 0);
	}
	else
	{
		isClosed = true;
	}
	free(buf);

	return !isClosed;
}

static void FreeClients(std::vector<ClientHandler*> &clients)
{
	std::vector<ClientHandler*>::iterator i = clients.begin();
	for (; i != clients.end(); i++)
	{
		delete (*i);
	}
	clients.clear();
}


static fd_set masterSet;
static SOCKET maxSock = 0;

static void RemoveConnection(SOCKET socket)
{
	if (FD_ISSET(socket, &masterSet) != 0)
	{
		FD_CLR(socket, &masterSet);
		if (socket == maxSock)
		{
			while (FD_ISSET(maxSock, &masterSet) == 0)
				maxSock--;
		}
	}
}

static bool Connect(Socket& mainSock, DWORD serverId, const char*ip, WORD port, HANDLE hStop)
{
	if (!mainSock.Connect(ip, port))
	{
		gServer->AddLog("GRJS can't connect %s:%d", ip, port);
		return false;
	}

	if (!Command::Send(mainSock, SERVER_CONNECT_CMD, serverId, NULL, 0))
	{
		return false;
	}

	Command c;
	if (!c.Read(mainSock, TEST_TIMEOUT, hStop))
	{
		return false;
	}
	if (c.command == REJECT_CMD)
	{
		std::string msg((const char*)c.data, c.dataLen);
		gServer->AddLog("GRJS reject connect %s", msg.c_str());
		return false;
	}

	gServer->AddLog("GRJS connected");
	return true;
}

static DWORD MakeNewID(const std::vector<ClientHandler*> &clients)
{
	DWORD ret = 0;
	std::vector<ClientHandler*>::const_iterator i = clients.begin();
	for (; i != clients.end(); i++)
	{
		if (ret < (*i)->id)
			ret = (*i)->id;
	}

	return ret + 1;
}

static void RemoveClient(std::vector<ClientHandler*>& clients, std::vector<ClientHandler*>::iterator ci)
{
	ClientHandler* ch = (*ci);
	clients.erase(ci);

	RemoveConnection(ch->joinSocket);
	RemoveConnection(ch->grSocket);
	delete ch;
}

static time_t MAX_PING_WAITS = 10; // 10 secs
static time_t MAX_SILENCE_INTERVAL = 90; // 90 secs

bool Run(Socket &mainSock, HANDLE hStop, WORD port)
{
	bool ret = true;

	sockaddr_in grServerAddr;
	grServerAddr.sin_port = htons(port);
	grServerAddr.sin_family = AF_INET;
	grServerAddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");

	std::vector<ClientHandler*> clients;

	fd_set workingSet;
	struct timeval timeout;

	maxSock = mainSock.GetSocket();
	FD_ZERO(&masterSet);
	FD_SET(maxSock, &masterSet);

	time_t lastSend = time(NULL);
	time_t lastPing = 0;

	bool closed = false;
	do
	{
		workingSet = masterSet;
		timeout.tv_sec = 0;
		timeout.tv_usec = 100000; // 100 ms
		int rc = select((int)maxSock + 1, &workingSet, NULL, NULL, &timeout);

		if (WaitForSingleObject(hStop, 0) == WAIT_OBJECT_0)
		{
			// need exit;
			closed = true;
			ret = false;
			break;
		}
		if (rc < 0)
		{
			gServer->AddLog("GRJS select error %d", WSAGetLastError());
			break;
		}

		time_t curTime = time(NULL);
		if (curTime - lastSend > MAX_SILENCE_INTERVAL)
		{
			if (lastPing == 0)
			{
				lastPing = time(NULL);
				Command::Send(mainSock, PING_CMD, 0, NULL, 0);
				gServer->AddLog(IErrorLogger::Full, "GRJS send ping");
			}
			else if (curTime - lastPing > MAX_PING_WAITS)
			{
				gServer->AddLog(IErrorLogger::Full, "GRJS no answer closing");
				closed = true;
				break;
			}
		}

		int dscs = rc;
		for (SOCKET i = 0; i <= maxSock && dscs > 0; i++)
		{
			if (FD_ISSET(i, &workingSet))
			{
				dscs--;

				if (i == mainSock.GetSocket())
				{
					Command cmd;
					if (cmd.Read(i))
					{
						lastSend = time(NULL);
						lastPing = 0;

						if (cmd.command == CLIENT_CONNECT_CMD)
						{
							SOCKET joinSock = ::socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
							SOCKET grSock = ::socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
							
							sockaddr_in adr = mainSock.Address();
							adr.sin_port = htons((unsigned short)cmd.id);

							if ((connect(joinSock, (const sockaddr*)&adr, sizeof(adr)) == 0) && 
								(connect(grSock, (const sockaddr*)&grServerAddr, sizeof(grServerAddr)) == 0))
							{
								DWORD id = MakeNewID(clients);
								ClientHandler *ch = new ClientHandler(id, joinSock, grSock);
								clients.push_back(ch);
							
								Command::Send(joinSock, OK_CMD, id, NULL, 0);

								FD_SET(joinSock, &masterSet);
								FD_SET(grSock, &masterSet);
								if (maxSock < joinSock)
									maxSock = joinSock;
								if (maxSock < grSock)
									maxSock = grSock;
							}
						}
						else if (cmd.command == CLOSE_CMD)
						{
							std::vector<ClientHandler*>::iterator ci = clients.begin();
							for (; ci != clients.end(); ci++)
							{
								if ((*ci)->id == cmd.id)
								{
									RemoveClient(clients, ci);
									break;
								}
							}
						}
						else if (cmd.command == PING_CMD)
						{
							gServer->AddLog(IErrorLogger::Full, "GRJS got ping");
						}
					}
					else
					{
						// JS closed
						closed = true;
						break;
					}
				}
				else
				{
					bool handled = false;
					std::vector<ClientHandler*>::iterator ci = clients.begin();
					for (; ci != clients.end(); ci++)
					{
						if ((*ci)->joinSocket == i || (*ci)->grSocket == i)
						{
							ClientHandler* ch = (*ci);
							if (!ch->Read(i))
							{
								RemoveClient(clients, ci);
							}
							handled = true;
							break;
						}
					}

					if (!handled)
					{
						gServer->AddLog("GRJS close socket %d", i);
						RemoveConnection(i);
						closesocket(i);
					}
				}
			}
		}
	} while (!closed);

	// close connections
	FreeClients(clients);
	return ret;
}

DWORD JoinServer::Register(std::string* error, const std::string& login, const std::string& pwd, const std::string& jsProject)
{
	DWORD res = 0;

	char *ubuf = (char*)malloc(200 + login.size() + pwd.size() + jsProject.size());
	wsprintfA(ubuf, "https://grsoft.ru/grjs/grjs.php?register&login=%s&password=%s&project=%s",
		login.c_str(), pwd.c_str(), jsProject.c_str());

	CurlService* cs = (CurlService*)gServer->GetService(CURL_SERVICE);
	ICurlHandler* h = cs->CreateHandler();

	h->SetUrl(ubuf);
	h->Preform();
	free(ubuf);

	if (h->GetResultCode() == 200)
	{
		std::string msgBuf;
		h->GetOutput(&msgBuf);

		JSONReader r;

		JSONValue* resObj = r.Parse(msgBuf);
		if (resObj != NULL)
		{
			if ((*resObj)["res"] == 1)
			{
				const JSONValue& serverId = (*resObj)["id"];
				res = (DWORD)(serverId.value.longValue);
			}
			else 
			{
				const JSONValue& err = (*resObj)["error"];
				if (err.IsString())
					error->assign(*err.value.string);
			}
		}
	}

	delete h;
	return res;
}

static bool GetAddress(std::string* serverAddr, WORD* serverPort, DWORD *id, const std::string& login, const std::string& pwd, const std::string& project)
{
	bool ret = false;
	char *ubuf = (char*)malloc(200 + login.size() + pwd.size());
	wsprintfA(ubuf, "https://grsoft.ru/grjs/grjs.php?open&login=%s&password=%s&project=%s",
		login.c_str(), pwd.c_str(), project.c_str());

	CurlService* cs = (CurlService*)gServer->GetService(CURL_SERVICE);
	ICurlHandler* h = cs->CreateHandler();
				
	h->SetUrl(ubuf);
	h->Preform();
	free(ubuf);

	if (h->GetResultCode() == 200)
	{
		std::string msgBuf;
		h->GetOutput(&msgBuf);

		JSONReader r;

		JSONValue* resObj = r.Parse(msgBuf);
		if (resObj != NULL)
		{
			if ((*resObj)["res"] == 1) 
			{
				const JSONValue& addr = (*resObj)["addr"];
				const JSONValue& port = (*resObj)["port"];
				const JSONValue& serverId = (*resObj)["id"];

#ifdef GRJS_TEST
				*serverAddr = "127.0.0.1";
#else
				*serverAddr = *addr.value.string;
#endif

				*serverPort = (WORD)(port.value.longValue);
				if (serverId.IsDouble())
					*id = (DWORD)(serverId.value.doblueValue);
				else if(serverId.IsInt())
					*id = (DWORD)(serverId.value.longValue);
				else if (serverId.IsString())
					sscanf(serverId.value.string->c_str(), "%u", id);
				ret = true;
			}
			else
			{
				resObj->read(&jsError, "error");
			}
		}
	}
	
	delete h;

	return ret;
}

static DWORD JSThread(JSThreadParam* _param)
{
	std::string login = _param->login;
	std::string pwd = _param->password;
	std::string project = _param->project;
	bool checkOnly = _param->checkOnly;

	//DWORD id = _param->id;
	WORD port = _param->port;
	delete _param;

	Socket mainSock;
	bool running = true;
	SetServerStatus(JoinServer::None);
	while (running)
	{
		DWORD waitConnectTime = 1 * 1000;

		SetServerStatus(JoinServer::Connecting);
		while (!GetAddress(&jsAddr, &jsPort, &serverID, login, pwd, project) || !Connect(mainSock, serverID, jsAddr.c_str(), jsPort, evStop))
		{
			mainSock.Close();
			SetServerStatus(JoinServer::Error);
			if (checkOnly)
			{
				CloseHandle(evStop);
				CloseHandle(hJS);
				evStop = NULL;
				hJS = NULL;
				return 0;
			}

			if (WaitForSingleObject(evStop, waitConnectTime) == WAIT_OBJECT_0)
			{
				SetServerStatus(JoinServer::None);
				return 0;
			}
			if (waitConnectTime < 100 * 1000)
				waitConnectTime *= 2;
		}

		status = JoinServer::Working;
		if (jsHandler != NULL)
			jsHandler->OnStatusChange(status);

		running = Run(mainSock, evStop, port);
		mainSock.Close();
	}

	SetServerStatus(JoinServer::None);
	return 0;
}

bool JoinServer::Start(const std::string& jsLogin, const std::string& jsPwd, const std::string& jsProject, WORD serverPort, bool checkOnly)
{
	if (evStop != 0)
	{
		Stop();
	}

	evStop = CreateEvent(NULL, TRUE, FALSE, NULL);

	JSThreadParam *param = new JSThreadParam();
	param->login = jsLogin;
	param->password = jsPwd;
	param->port = serverPort;
	param->project = jsProject;
	param->checkOnly = checkOnly;

	hJS = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)JSThread, param, 0, NULL);
	return true;
}

void JoinServer::Stop()
{
	if (evStop != 0)
	{
		serverID = 0;
		SetEvent(evStop);
		WaitForSingleObject(hJS, INFINITE);

		CloseHandle(evStop);
		CloseHandle(hJS);
	}

	evStop = 0;
	hJS = 0;
}

DWORD JoinServer::GetID()
{
	return serverID;
}

JoinServer::Status JoinServer::GetStatus()
{
	return status;
}

const std::string& JoinServer::GetError()
{
	return jsError;
}

void JoinServer::SetHandler(JoinServerStatusHandler* h)
{
	jsHandler = h;
}
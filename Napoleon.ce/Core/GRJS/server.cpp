#include "grjs.h"
#include "common.h"

#include <sstream>

using namespace std;

/*
Packet

Tag - GRJS
Command - 4 bytes
ID - 4 bytes hex
DataLen - 4 bytes hex
Data

GRJSCMND0123456700000000

*/

const unsigned PACKET_TAG = 'GRJS';
const unsigned SERVER_CONNECT_CMD = 'CNCT';
const unsigned CLIENT_CONNECT_CMD = 'CLTC';
const unsigned OK_CMD = 'OKCM';
const unsigned REJECT_CMD = 'RJCT';

const unsigned PING_CMD = 'PING';

const unsigned DATA_CMD = 'DATA';
const unsigned CLOSE_CMD = 'CLOS';

const DWORD MAX_PACKET_LEN = 5000;

const char NO_SERVER_AVAIL[] = "Сервер не доступен";
const char SERVER_EXISTS[] = "Сервер с таким ID уже запущен";
const char WRONG_SERVER_ID[] = "Не верный ID сервера";
const char NO_SOCKETS[] = "Нет свободных подключений";

Logger logger;

inline bool IsClientID(DWORD id) { return ((id & 0x80000000) == 0); }

#ifdef WIN32
#include <varargs.h>
#else
#include <stdarg.h>
typedef int64_t __int64;
#endif

#ifndef WIN32
void TmToSystemTime(const tm& tme, SYSTEMTIME* st)
{
	st->wMilliseconds = 0;
	st->wDay = tme.tm_mday;
	st->wDayOfWeek = tme.tm_wday;
	st->wHour = tme.tm_hour;
	st->wMinute = tme.tm_min;
	st->wMonth = tme.tm_mon + 1;
	st->wYear = tme.tm_year + 1900;
	st->wSecond = tme.tm_sec;
}

void GetLocalTime(SYSTEMTIME *st)
{
	time_t t = time(NULL);
	tm tme;
	localtime_r(&t, &tme);

	TmToSystemTime(tme, st);
}

BOOL SystemTimeToFileTime(const SYSTEMTIME *st, LPFILETIME ft)
{
	tm tme;
	tme.tm_mday = st->wDay;
	tme.tm_hour = st->wHour;
	tme.tm_min = st->wMinute;
	tme.tm_mon = st->wMonth - 1;
	tme.tm_year = st->wYear - 1900;
	tme.tm_sec = st->wSecond;

	time_t t = mktime(&tme);
	__int64 tm = (__int64)t * 10000000 + 116444736000000000;

	ft->dwLowDateTime = (DWORD)tm;
	ft->dwHighDateTime = tm >> 32;

	return TRUE;
}

BOOL FileTimeToSystemTime(const FILETIME* ft, LPSYSTEMTIME st)
{
	time_t t = (time_t)((*(__int64*)ft - 116444736000000000) / 10000000);
	tm tme;
	localtime_r(&t, &tme);
	TmToSystemTime(tme, st);

	return TRUE;
}
#endif
static FILE *hLog = stdout;
void PutLog(const char *str, ...)
{
	if (hLog == NULL)
		return;

	SYSTEMTIME st;
	GetLocalTime(&st);

	va_list args;
	va_start(args, str);
	fprintf(hLog, "%02d/%02d/%d %02d:%02d:%02d.%03d ", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
	vfprintf(hLog, str, args);
	fprintf(hLog, "\n");
	va_end(args);

	fflush(hLog);
}

Command::Command() : command(0), dataLen(0), data(NULL), id(0), isHttp(false)
{
}

Command::~Command()
{
	free(data);
}

bool Command::ReadHttp(SOCKET sock)
{
	// read header at once;

	bool ret = false;
	int value;
#ifdef WIN32
	ioctlsocket(sock, FIONREAD, (u_long*)&value);
#else
	ioctl(sock, FIONREAD, &value);
#endif

	unsigned char *buf = (unsigned char*)malloc(value);
	int rc = recv(sock, (char*)buf, value, 0);
	if (rc > 0)
	{
		string val((const char*)buf, rc);
		free(buf);

		isHttp = true;

		bool starting = false;
		istringstream iss(val);
		while (std::getline(iss, val, '\n'))
		{
			size_t pos;
			if (!starting)
			{
				starting = true;
				istringstream urlver(val);
				urlver >> url >> httpVersion;
				pos = url.find('/', 1);
				if (pos == string::npos)
				{
					ret = false;
					break;
				}
				char *ep;
				id = strtoul(url.substr(1, pos - 1).c_str(), &ep, 10);
				url = url.substr(pos);
				ret = true;
				continue;
			}

			if (val.empty()) break; // end header

			if (*val.begin() == ' ') continue; // RFC 7231 if header starts with space skip line
			
			pos = val.find(':');   // make header
			if (pos == string::npos) continue;

			unsigned cp = pos + 1, count = val.size() - pos - 1;
			std::string::iterator i = val.begin() + pos + 1;
			while (i != val.end() && isspace(*i))
			{
				i++;
				cp++;
				count--;
			}

			if (i == val.end())
				continue;
			std::string::iterator ei = val.end() - 1;
			while (ei > i && isspace(*ei))
			{
				ei--;
				count--;
			}

			const string& key = val.substr(0, pos);
			headers[key] = val.substr(cp, count);
		}
	}
	else
	{
		free(buf);
	}
	return ret;
}

bool Command::Read(SOCKET socket)
{
	DWORD head[4];
	int rc = recv(socket, (char*)head, sizeof(head[0]), 0);
	if (rc < sizeof(head[0]))
		return false;
	if (ntohl(*head) != PACKET_TAG)
	{
		return ReadHttp(socket);
	}

	rc = recv(socket, (char*)(head + 1), sizeof(head) - sizeof(head[0]), 0);

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

bool Command::Send(SOCKET socket, DWORD command, DWORD id, unsigned char* data, DWORD dataLen)
{
	DWORD cb = dataLen + sizeof(DWORD) * 4;
	char* buf = (char*)malloc(cb);

	*((DWORD*)buf) = htonl(PACKET_TAG);
	*((DWORD*)buf + 1) = htonl(command);
	*((DWORD*)buf + 2) = htonl(id);
	*((DWORD*)buf + 3) = htonl(dataLen);

	if (dataLen > 0)
		memcpy(buf + sizeof(DWORD) * 4, data, dataLen);

	send(socket, buf, cb, 0);
	free(buf);
	return true;
}


ClientHandler::ClientHandler(const PendingClient& data, SOCKET serverSocket) : Handler(0, data.clientSocket)
{
	this->server = data.sh;
	this->serverSocket = serverSocket;
	traficClient = 0;
	traficServer = 0;
	start = data.started;

	this->server->Add(this);
}

DWORD ClientHandler::ServerID() const 
{ 
	return server->id; 
}


bool ClientHandler::TransmittData(SOCKET sock)
{
	SOCKET destS = (sock == socket) ? serverSocket : socket;
	// Передаем только то что влезает в один пакет.
	bool ret = true;

	int value;
#ifdef WIN32
	ioctlsocket(sock, FIONREAD, (u_long*)&value);
#else
	ioctl(sock, FIONREAD, &value);
#endif
	//PutLog("Socket %d, rcv %d", sock, value);
	//
	if (value == 0)
	{
		Command::Send(server->socket, CLOSE_CMD, id, NULL, 0);
		return false;
	}

	unsigned char *buf = (unsigned char*)malloc(value);
	int rc = recv(sock, (char*)buf, value, 0);
	if (rc <= 0)
	{
		free(buf);
		ret = false;
		Command::Send(server->socket, CLOSE_CMD, id, NULL, 0);
	}
	else
	{
		SendData(destS, buf, rc);

		if (sock == socket)
			traficClient += rc;
		else
			traficServer += rc;
	}

	return ret;
}

void ClientHandler::SendData(SOCKET sock, const unsigned char* data, DWORD cb)
{
	Sender::Send(sock, data, cb);
}

void ClientHandler::ClientAccepted()
{
	Command::Send(socket, OK_CMD, id, NULL, 0);
}

bool ClientHandler::Handle(SOCKET sock)
{
	if (id == 0)
	{
		Command c;
		if (sock == serverSocket  && c.Read(sock) && c.command == OK_CMD)
		{
			id = c.id;
			ClientAccepted();

			PutLog("Socket %d client %d accept server %d", socket, id, sock);
			SetBlock(socket);
			SetBlock(serverSocket);
		}
		else
		{
			PutLog("Socket %d wrong from socket %d", socket, sock);
		}
		return (id != 0);
	}

	return TransmittData(sock);
}

void ClientHandler::ServerClosing()
{
	server = NULL;
}

void ClientHandler::Close(ServerData* server)
{
	Handler::Close(server);
	logger.Add(*this);
	if (this->server != NULL)
	{
		this->server->ClientClosing(this);
	}
}

HttpClientHandler::HttpClientHandler(const PendingClient& data, SOCKET serverSocket) :
	ClientHandler(data, serverSocket), 
	headers(data.headers), url(data.url), httpVersion(data.httpVersion)
{
	
}

void HttpClientHandler::SendData(SOCKET sock, const unsigned char* data, DWORD cb)
{
	if (sock == serverSocket && strnicmp((const char*)data, "get ", 4) == 0)
	{
		const char *p = strchr((const char*)data + 5, '/'); // remove server id from get
		int dataLen = cb - (p - (const char*)data);
		unsigned char *newData = (unsigned char *)malloc(dataLen + 4);
		memcpy(newData, "GET ", 4);
		memcpy(newData + 4, p, dataLen);
		free((void*)data);
		data = newData;
		cb = dataLen;
	}

	Sender::Send(sock, data, cb);
}


void HttpClientHandler::ClientAccepted()
{
	stringstream ss;
	ss << "GET " << url << " " << httpVersion << "\r\n";
	map<string, string>::const_iterator i = headers.begin();
	
	for (; i != headers.end(); i++)
	{
		ss << i->first << ": " << i->second << "\r\n";
	}
	ss << "\r\n";

	const string &data = ss.str();
	Sender::Send(serverSocket, (unsigned char*)strdup(data.c_str()), data.size());
}

ServerHandler::ServerHandler(DWORD id, SOCKET socket, const std::string& addr) : Handler(id, socket)
{
	address = addr;
	logger.Add(*this);
}

void ServerHandler::Remove(ClientHandler* c)
{
	ClientList::iterator i = clients.begin();
	for (; i != clients.end(); i++)
	{
		if ((*i) == c)
		{
			clients.erase(i);
			break;
		}
	}
}

bool ServerHandler::Handle(SOCKET sock)
{
	Command cmd;
	if (!cmd.Read(socket))
	{
		//PutLog("Socket %d can't read command", socket);
		return false;
	}

	bool ret = true;
	if (cmd.command == PING_CMD)
	{
		Command::Send(socket, PING_CMD, 0, "");
		PutLog("Socket %d ping", socket);
	} else if (cmd.command == DATA_CMD && cmd.dataLen > 0)
	{
		ClientHandler* cli = FindClient(cmd.id);
		if (cli != NULL)
		{
			send(cli->socket, (const char*)cmd.data, cmd.dataLen, 0);
		}
	}
	
	return ret;
}

ClientHandler* ServerHandler::FindClient(DWORD id)
{
	ClientList::iterator i = clients.begin();
	for (; i != clients.end(); i++)
	{
		if ((*i)->id == id)
		{
			return (*i);
		}
	}

	return NULL;
}

void ServerHandler::Close(ServerData* server)
{
	Handler::Close(server);
	logger.Add(*this);

	ClientList::iterator i = clients.begin();
	for (; i != clients.end(); i++)
	{
		(*i)->ServerClosing();
		server->CloseDevice((*i)->socket);
	}

	clients.clear();
}

void ServerHandler::ClientClosing(ClientHandler *cli)
{ 
	Command::Send(socket, CLOSE_CMD, cli->id, NULL, 0);
	Remove(cli); 
}


//void PutMySQLError(MYSQL *conn)
//{
//	PutLog("Error %u %s", mysql_errno(conn), mysql_error(conn));
//}
//
//void PutMySQLError(MYSQL_STMT *conn)
//{
//	PutLog("Error %u %s", mysql_stmt_errno(conn), mysql_stmt_error(conn));
//}

ServerData::ServerData() : socket(-1)
{
}

ServerData::~ServerData()
{
	std::map<SOCKET, Handler*>::iterator i = devices.begin();
	for (; i != devices.end(); i++)
		delete i->second;
	devices.clear();
}

bool ServerData::Start()
{
	config.Read();
	socket = AllocListenSocket(config.port);
	return (socket != 0);
}

bool ServerData::CheckServerID(DWORD id)
{
	if (IsClientID(id))
		return false;

	return true;
}

DWORD ServerData::FindFreePort()
{
	DWORD newPort = config.minClientPort;
	std::map<SOCKET, PendingClient>::const_iterator i = pendingClients.begin();
	for (; i != pendingClients.end(); i++)
	{
		if (i->second.port > newPort)
			newPort = i->second.port;
	}
	newPort++;
	if (newPort > config.maxClientPort)
		return 0;
	return newPort;
}

SOCKET ServerData::AllocListenSocket(DWORD newPort)
{
	SOCKET newSock = ::socket(AF_INET, SOCK_STREAM, 0);

	int on = 1;
	int rc = setsockopt(newSock, SOL_SOCKET, SO_REUSEADDR, (char *)&on, sizeof(on));

	SetNonBlock(newSock);

	sockaddr_in sockaddr;
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_addr.s_addr = INADDR_ANY;
	sockaddr.sin_port = htons((unsigned short)newPort);

	if (bind(newSock, (struct sockaddr*)&sockaddr, sizeof(sockaddr)) == SOCKET_ERROR)
	{
		closesocket(newSock);
		PutLog("Cant bind to %d", newPort);
		return 0;
	}

	if (listen(newSock, 1) == SOCKET_ERROR)
	{
		closesocket(newSock);
		PutLog("Cant bind to listen on new socket");
		return 0;
	}
	return newSock;
}

bool ServerData::AddrFromSocket(std::string* outAddr, SOCKET sock) const
{
	std::map<SOCKET, sockaddr_in>::const_iterator fnd = address.find(sock);
	if (fnd == address.end())
		return false;

	outAddr->assign(inet_ntoa(fnd->second.sin_addr));

	char pbuf[20];
	DWORD port = htons(fnd->second.sin_port);
	_itoa(port, pbuf, 10);
	outAddr->append(1, ':').append(pbuf);

	return true;
}

static bool IsEqualIP(const std::string& addr1, const std::string& addr2)
{
	size_t pos1 = addr1.find(':');
	size_t pos2 = addr2.find(':');

	return (addr1.substr(0, pos1).compare(addr2.substr(0, pos2)) == 0);
}

bool ServerData::AssignClient(SOCKET socket, const Command &cmd)
{
	bool ret = false;
	std::string addr;
	AddrFromSocket(&addr, socket);

	HandlerMap::iterator server = Find(cmd.id);
	if (server != devices.end())
	{
		ServerHandler* sh = (ServerHandler*)server->second;
		DWORD newPort = FindFreePort();
		if (newPort == 0)
		{
			PutLog("Can't fine free port");
			if (!cmd.isHttp)
				Command::Send(socket, REJECT_CMD, 0, (unsigned char*)NO_SOCKETS, sizeof(NO_SOCKETS) - 1);
			else
				send(socket, NO_SOCKETS, sizeof(NO_SOCKETS) - 1, 0);
		}
		else
		{
			SOCKET serverSock = AllocListenSocket(newPort);

			PendingClient pc;
			pc.started = time(NULL);
			pc.port = newPort;
			pc.clientSocket = socket;
			pc.sh = sh;
			pc.url = cmd.url;
			pc.headers = cmd.headers;
			pc.httpVersion = cmd.httpVersion;

			pendingClients[serverSock] = pc;
			AddNewSocketToFD(serverSock);
			Command::Send(sh->socket, CLIENT_CONNECT_CMD, newPort, NULL, 0);

			PutLog("Socket %d (%s) %s request for server %X", socket, addr.c_str(), (cmd.isHttp ? "HTTP" : ""), cmd.id);
			ret = true;
		}
	}
	else
	{
		PutLog("Socket %d client %s no server %X", socket, addr.c_str(), cmd.id);
		if (!cmd.isHttp)
			Command::Send(socket, REJECT_CMD, 0, NO_SERVER_AVAIL);
		else
			send(socket, NO_SERVER_AVAIL, sizeof(NO_SERVER_AVAIL) - 1, 0);
	}

	return ret;
}

bool ServerData::HandleClient(SOCKET socket)
{
	Handler *dd = NULL;
	std::map<SOCKET, Handler*>::iterator di = devices.begin();
	for (; di != devices.end(); di++)
	{
		if (di->first == socket || di->second->ServerSocket() == socket)
		{
			return di->second->Handle(socket);
		}
	}

	// if something from pending socket - drop it.
	std::map<SOCKET, PendingClient>::iterator i = pendingClients.begin();
	for (; i != pendingClients.end(); i++)
	{
		if (i->second.clientSocket == socket)
		{
			// remove listen socket
			pendingClients.erase(i);
			RemoveConnection(i->first);
			closesocket(i->first);

			PutLog("Socket %d wrong requset from pending socket", socket);
			return false; // says to close current socket
		}
	}

	Command cmd;
	if (!cmd.Read(socket))
	{
		PutLog("Socket %d not a command", socket);
		return false;
	}
	
	if (cmd.isHttp)
	{
		return AssignClient(socket, cmd);
	}

	std::string addr;
	bool ret = false;
	if (cmd.command == SERVER_CONNECT_CMD)
	{
		if (!CheckServerID(cmd.id))
		{
			PutLog("Socket %d wrong server id %X", socket, cmd.id);
			Command::Send(socket, REJECT_CMD, 0, WRONG_SERVER_ID);
		}
		else
		{
			AddrFromSocket(&addr, socket);
			HandlerMap::iterator server = Find(cmd.id);
			if (server != devices.end())
			{
				if (IsEqualIP(addr, ((ServerHandler*)server->second)->address))
				{
					PutLog("Socket %d reconnect exists server %s %X", socket, addr.c_str(), cmd.id);
					CloseDevice(server->first);

					ServerHandler *sh = new ServerHandler(cmd.id, socket, addr);
					devices[socket] = sh;
					ret = true;
					Command::Send(socket, OK_CMD, cmd.id, NULL, 0);
				}
				else
				{
					PutLog("Socket %d fail connect exists server %s %X curreint addr %s", socket, addr.c_str(), cmd.id, ((ServerHandler*)server->second)->address.c_str());
					Command::Send(socket, REJECT_CMD, 0, SERVER_EXISTS);
				}
			}
			else
			{
				PutLog("Socket %d conect new server %s %X", socket, addr.c_str(), cmd.id);

				ServerHandler *sh = new ServerHandler(cmd.id, socket, addr);
				devices[socket] = sh;
				ret = true;
				Command::Send(socket, OK_CMD, cmd.id, NULL, 0);
			}
		}
	}
	else if (cmd.command == CLIENT_CONNECT_CMD)
	{
		ret = AssignClient(socket, cmd);
	}
	else
	{
		PutLog("Socket %d unhandled command %x", socket, cmd.command);
	}
	return ret;
}

// call only in MainLoop thread
void ServerData::RemoveConnection(SOCKET socket)
{
	if (FD_ISSET(socket, &masterSet) != 0)
	{
		FD_CLR(socket, &masterSet);
		if (socket == maxD)
		{
			while (FD_ISSET(maxD, &masterSet) == 0)
				maxD--;
		}
	}
}

Handler* ServerData::RemoveDevice(SOCKET socket)
{
	Handler* ret = NULL;

	std::map<SOCKET, Handler*>::iterator i = devices.begin();
	for (; i != devices.end(); i++)
	{
		Handler* h = i->second;
		if (h->socket == socket || h->ServerSocket() == socket)
		{
			ret = h;
			devices.erase(i->first);
			break;
		}
	}

	return ret;
}

ServerData::HandlerMap::iterator ServerData::Find(DWORD id)
{
	HandlerMap::iterator i = devices.begin();
	for (; i != devices.end(); i++)
	{
		if (i->second->id == id)
			break;
	}
	return i;
}

void ServerData::Close()
{
	if (socket > 0)
	{
		closesocket(socket);
		socket = -1;
	}
}

void ServerData::CloseDevice(SOCKET socket)
{
	Handler* dd = RemoveDevice(socket);
	RemoveConnection(socket);
	
	Sender::ForceClose(socket);
	//closesocket(socket);

	if (dd != NULL)
	{
		if (dd->ServerSocket() == socket)
		{
			RemoveConnection(dd->socket);
			Sender::ForceClose(dd->socket);
			//closesocket(dd->socket);
			
			PutLog("Close client socket %d", dd->socket);
		} else if (dd->ServerSocket() != 0)
		{
			RemoveConnection(dd->ServerSocket());
			Sender::ForceClose(dd->ServerSocket());
			//closesocket(dd->ServerSocket());

			PutLog("Socket %d close server socket %d", socket, dd->ServerSocket());
		}

		dd->Close(this);
		delete dd;
	}

	PutLog("Socket %d closed", socket);
}

void ServerData::AddNewSocketToFD(SOCKET newSocket)
{
	FD_SET(newSocket, &masterSet);
	if (newSocket > (SOCKET)maxD)
		maxD = newSocket;
}

void ServerData::MainLoop()
{
	fd_set workingSet;

	bool finishServer = false;
	struct timeval timeout;

	maxD = socket;
	FD_ZERO(&masterSet);
	FD_SET(socket, &masterSet);

	do
	{
		workingSet = masterSet;
		timeout.tv_sec = config.readtimeout;
		timeout.tv_usec = 0;
		int rc = select(maxD + 1, &workingSet, NULL, NULL, &timeout);
		if (rc < 0)
		{
#ifdef WIN32
			PutLog("Select error %d", WSAGetLastError());
#else
			PutLog("Select error %d", errno);
#endif
			break;
		}

		//if (rc == 0) // timeout
		//{
		//}

		if (rc > 0)
		{
			int dscs = rc;
			const char* ip = "";
			DWORD port = 0;
			for (int i = 0; i <= maxD && dscs > 0; i++)
			{
				if (FD_ISSET(i, &workingSet))
				{
					dscs--;
					if (i == socket)
					{
						// accept new connections
						do
						{
							sockaddr_in addr;
							int cb = sizeof(addr);
							SOCKET newSocket = accept(socket, (sockaddr*)&addr, (socklen_t*)&cb); //Accept(&addr);
							if ((int)newSocket <= 0)
								break;

							address[newSocket] = addr;
							SetNonBlock(newSocket);
							AddNewSocketToFD(newSocket);

							PutLog("Accept %d %s:%d", newSocket, inet_ntoa(addr.sin_addr), htons(addr.sin_port));
						} while (true);
					}
					else
					{
						std::map<SOCKET, PendingClient>::iterator fnd = pendingClients.find(i);
						if (fnd != pendingClients.end())
						{
							SOCKET newSocket = accept(i, NULL, 0); //Accept(&addr);
							if ((int)newSocket > 0)
							{
								SetNonBlock(newSocket);
								AddNewSocketToFD(newSocket);

								ClientHandler *ch = (fnd->second.url.empty()) ? new ClientHandler(fnd->second, newSocket) : new HttpClientHandler(fnd->second, newSocket);

								SOCKET cliSock = fnd->second.clientSocket;
								devices[cliSock] = ch;
							}

							// onlu one client allowed
							pendingClients.erase(fnd);
							RemoveConnection(i);
							closesocket(i);
						}
						else
						{
							bool removeSocket = !HandleClient(i);
							if (removeSocket)
							{
								CloseDevice(i);
							}
						}
					}
				}
			}
		}

	} while (!finishServer);
}

int main(int argc, char* argv[])
{
#ifdef WIN32
	WSADATA wsaData = { 0 };
	WSAStartup(MAKEWORD(2, 2), &wsaData);
#endif

	for (int i=1; argv[i]; i++)
	{
		if (strcmp(argv[i], "-l") == 0 && argv[i+1])
		{
			hLog = fopen(argv[i + 1], "a");
			i++;
		}
	}

	ServerData data;
	if (data.Start())
	{
		PutLog("Starting");

		logger.Start();
		Sender::Start();
		data.MainLoop();
	}

	data.Close();

	if (hLog  != NULL && hLog != stdout)
	{
		fclose(hLog);
	}

	logger.Stop();
	Sender::Stop();

#ifdef WIN32
	WSACleanup();
#endif
	return 0;
}
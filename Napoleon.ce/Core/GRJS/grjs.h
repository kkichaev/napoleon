#ifndef __GRJS_H
#define __GRJS_H

#ifdef WIN32
// link with Ws2_32.lib
#pragma comment(lib,"Ws2_32.lib")

#include <winsock2.h>
#include <ws2tcpip.h>

#else
#include "common.h"
#define _stricmp stricmp

#endif

#include <stdio.h>
#include <stdlib.h>   // Needed for _wtoi

//#include <mysql.h>
#include "thread.h"

#include <vector>
#include <map>

#include <time.h>

class ServerHandler;
class ServerData;

struct Config
{
	int port;
	int readtimeout;

	DWORD minClientPort, maxClientPort;
	
	//std::string dbHost;
	//std::string dbDatabase;
	//std::string dbLogin;
	//std::string dbPassword;
	//int dbPort;

	//std::string deviceLogTable;
	//std::string deviceConfigTable;


	bool Read();

	void SetValue(const std::string& key, const std::string& value);
};

struct Command
{
	DWORD command;
	DWORD id;
	DWORD dataLen;
	unsigned char *data;

	bool isHttp;

	std::map<std::string, std::string> headers;
	std::string url;
	std::string httpVersion;

	Command();
	~Command();

	bool Read(SOCKET sock);

	static bool Send(SOCKET socket, DWORD command, DWORD id, const std::string& message)
	{
		return Send(socket, command, id, (unsigned char*)message.c_str(), message.size());
	}
	static bool Send(SOCKET socket, DWORD command, DWORD id, unsigned char* data, DWORD dataLen);

	bool ReadHttp(SOCKET socket);
};

class Handler
{
public:
	DWORD id;
	SOCKET socket;

	time_t start, end;


	Handler(DWORD _id, SOCKET _socket) : id(_id), socket(_socket) 
	{
		start = time(NULL);
		end = 0;
	}

	virtual ~Handler() {}
	virtual bool Handle(SOCKET sock) = 0;
	virtual SOCKET ServerSocket() const { return 0; }

	virtual void Close(ServerData* server)
	{
		end = time(NULL);
	}

};

struct PendingClient
{
	SOCKET clientSocket;
	time_t started;
	DWORD port;
	ServerHandler *sh;

	std::map<std::string, std::string> headers;
	std::string url;
	std::string httpVersion;
};

class ClientHandler : public Handler
{
public:
	ClientHandler(const PendingClient& data, SOCKET serverSocket);
	
	virtual bool Handle(SOCKET sock);
	virtual void Close(ServerData* server);

	void ServerClosing();

	virtual SOCKET ServerSocket() const{ return serverSocket; }

	DWORD TraficClient() const { return traficClient; }
	DWORD TraficServer() const { return traficServer; }

	DWORD ServerID() const;

protected:
	bool TransmittData(SOCKET sock);
	
	virtual void ClientAccepted();
	virtual void SendData(SOCKET sock, const unsigned char* data, DWORD cb);

	ServerHandler* server;
	SOCKET serverSocket;

	DWORD traficClient;
	DWORD traficServer;

};

class HttpClientHandler : public ClientHandler
{
public:
	HttpClientHandler(const PendingClient& data, SOCKET serverSocket);

private:
	std::map<std::string, std::string> headers;
	std::string url;
	std::string httpVersion;
	std::string urlPrefix;

	virtual void ClientAccepted();
	virtual void SendData(SOCKET sock, const unsigned char* data, DWORD cb);
};

class ServerHandler : public Handler
{
public:
	typedef std::vector<ClientHandler*> ClientList;
	ClientList clients;
	std::string address;

	ServerHandler(DWORD id, SOCKET socket, const std::string& addr );
	
	void Add(ClientHandler* c) { clients.push_back(c); }
	void Remove(ClientHandler* c);
	virtual bool Handle(SOCKET sock);

	virtual void Close(ServerData* server);

	ClientHandler* FindClient(DWORD id);
	void ClientClosing(ClientHandler *cli);
};

class ServerData
{
public:
	typedef std::map<SOCKET, Handler*> HandlerMap;
	Config config;

protected:
	//Mutex threadMutex;

	SOCKET socket;

	fd_set masterSet;
	int maxD;

	HandlerMap devices;
	std::map<SOCKET, sockaddr_in> address;
	std::map<SOCKET, PendingClient> pendingClients;


public:
	ServerData();
	~ServerData();

	bool Start();
	void Close();
	void MainLoop();


	bool HandleClient(SOCKET socket);

	// call only in MainLoop thread
	void RemoveConnection(SOCKET socket);
	void CloseDevice(SOCKET socket);

	bool CheckServerID(DWORD id);

	Handler* RemoveDevice(SOCKET socket);
	HandlerMap::iterator Find(DWORD id);

	DWORD FindFreePort();
	SOCKET AllocListenSocket(DWORD port);
	void AddNewSocketToFD(SOCKET socket);
	bool AddrFromSocket(std::string* outAddr, SOCKET sock) const;

	bool AssignClient(SOCKET socket, const Command &command);
};

class Logger
{
public:
	void Add(const ClientHandler& h);
	void Add(const ServerHandler& s);
	
	void Start();
	void Stop();
};


class Sender
{
public:
	static void Start();
	static void Stop();

	static void Send(SOCKET socket, const unsigned char* data, DWORD cb);
	static void ForceClose(SOCKET socket);
};

#endif
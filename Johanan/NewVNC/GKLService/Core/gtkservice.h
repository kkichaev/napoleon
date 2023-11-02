#ifndef __GTK_H
#define __GTK_H

#ifdef WIN32
// link with Ws2_32.lib
#pragma comment(lib,"Ws2_32.lib")

#include <winsock2.h>
#include <ws2tcpip.h>

#else



#endif

#include <stdio.h>
#include <stdlib.h>   // Needed for _wtoi

#include <mysql.h>
#include "thread.h"
#include "packet.h"
#include "mutex.h"

#include <vector>
#include <map>

struct Config
{
	int port;
	int readtimeout;
	
	std::string dbHost;
	std::string dbDatabase;
	std::string dbLogin;
	std::string dbPassword;
	int dbPort;

	std::string deviceConnectsTable;
	std::string deviceConnectsDataTable;

	std::string uploadFolder;

	int vncPortMin;
	int vncPortMax;

	bool Read();

	void SetValue(const std::string& key, const std::string& value);
};

//struct DeviceSettings
//{
//	std::string id;
//	int port;
//};

class Packet;
struct DeviceEvents
{
	virtual void Closed() = 0;
};
class DeviceData
{
public:
	enum State { stInitial, stReadingData, stReady, stWSHandshake, stWSData};

	std::string ip;
	int port;

	DWORD userid;
	DWORD deviceid;
	DWORD id; // id from SQL devices table
	DWORD command;
	DWORD datalen;
	unsigned char *data;
	bool isDead;

	SOCKET vncSocket;
	DeviceEvents* handler;

	time_t lastPing;
	DWORD reqHistLine;
	std::string histFile;

	DeviceData(const char* addr, DWORD port);
	~DeviceData(); 

	bool Read(SOCKET socket);

	bool operator== (const DeviceData& src) const
	{
		return (id != 0 && id == src.id) || (userid == src.userid && deviceid == src.deviceid);
	}

	bool Ready() const { return state == stReady; }
	
	void EstablishVNCConnection(SOCKET vncSocket, DeviceEvents* handler)
	{
		this->vncSocket = vncSocket;
		this->handler = handler;
	}

	bool SendPacket(SOCKET socket, DWORD command, unsigned char* data, unsigned dataLen);

	bool IsWebSocket() const { return isWebSocket; }
	bool WrongPacket() const { return wrongPacket; }
	void CloseVNCConnection();

private:
	unsigned char *cp;
	State state;
	
	bool wrongPacket;
	bool isWebSocket;
	uint8_t frameType;
	uint8_t mask[4];

	std::string headerBuffer;
	std::map<std::string, std::string> headers;

	bool ReadData(SOCKET socket);

	bool ReadHandshake(SOCKET socket);
	bool MakeHandshake(SOCKET socket);

	bool ReadWSFrame(SOCKET socket);
	bool ReadWSData(SOCKET socket);

	bool SendCtl(SOCKET socket, uint8_t frameType);
	bool SendWSData(SOCKET socket, const uint8_t* data, unsigned size, uint8_t type);
};

//class ClientThread;
class ServerData
{
public:
	Config config;

protected:
	Mutex threadMutex;

	MYSQL *mysql;
	SOCKET socket;

	fd_set masterSet;
	int maxD;

	time_t lastLiveDeviceCheck;

	std::map<SOCKET, DeviceData*> devices;
	std::map<SOCKET, sockaddr_in> address;


public:
	ServerData();
	~ServerData();

	bool Start();
	void Close();
	void MainLoop();

	
	bool HandleClient(SOCKET socket);
	SOCKET FindDevice(const DeviceData& device);

	// call only in MainLoop thread
	void RemoveConnection(SOCKET socket);
	void CloseDevice(SOCKET socket);
	bool UpdateDeviceData(DeviceData* dd);

	DeviceData* RemoveDevice(SOCKET socket);

	void CreateVNCConnection(DeviceData* vncReq, DeviceData* device, SOCKET deviceSocket);

	void TryVNCRecconect(SOCKET socket, DeviceData* device, bool needStopVNC);

	void HandleHistoryRequests();
	void MarkRequestHandled(DWORD deviceID, const char* error = NULL);
	void PutHistoryLine(SOCKET s, DeviceData* dd);
};


void Trim(std::string* res, const std::string& _src, size_t offset, size_t size);

DWORD doCRC(DWORD Data, DWORD curCRC);

#endif
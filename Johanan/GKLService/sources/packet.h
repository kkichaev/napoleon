#ifndef __PACKET_H
#define __PACKET_H

#include <string>

#ifdef WIN32

#include <winsock2.h>
#include <ws2tcpip.h>

#else

#include "common.h"

#endif

class Packet
{
public:
	Packet();
	~Packet();

	bool ReadHeader(SOCKET socket);
	bool ReadData(SOCKET socket, unsigned timeout);

	DWORD userid;
	DWORD deviceid;
	DWORD command;
	DWORD datalen;
	
	unsigned char* data;
};

using namespace std;

#define PING_CMD			"PING"
#define SEND_DATA_CMD	"SEND DATA"
#define ACCEPT_CMD		"ACCEPT"
#define LOST_CONNECTION_CMD  "LOST CONNECTION"

class NetStream
{
public:
	NetStream(int timeout);
	~NetStream();

	void SetSocket(SOCKET socket) { this->socket = socket; }

	char GetChar();
	bool ReadUntil(std::string* buf, char stopSym);
	bool Read(unsigned char* buf, int len);

	int DataLength() const { return size - cp; }
	bool HaveData() const { return cp < size; }

	bool ReadData();
private:
	SOCKET socket;
	int timeout;

	char* buf;
	int size;
	int cp;
};

class PacketOld
{
public:
	PacketOld();
	~PacketOld();

	bool Read(NetStream &stream);
	bool Send(SOCKET socket);

	bool SendBuf(SOCKET socket);
	bool ReadBuf(NetStream& stream);

	void FreeBuffer();

	std::string command;
	std::string deviceID;

	int datalen;
	unsigned char* data;

private:
	void LoadOption(const std::string& key, const std::string& value);
};

int AvailRead(SOCKET socket);

#endif
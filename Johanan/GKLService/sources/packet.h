#ifndef __PACKET_H
#define __PACKET_H

#include <string>
#include <map>

#ifdef WIN32

#include <winsock2.h>
#include <ws2tcpip.h>

#else

#include "common.h"

#endif

using namespace std;

#define PING_CMD			"PING"
#define SEND_DATA_CMD	"SEND DATA"
#define ACCEPT_CMD		"ACCEPT"
#define LOST_CONNECTION_CMD  "LOST CONNECTION"

class NetStream;
struct Config;
struct ConnectionData;
struct DataBuffer;

class Packet
{
public:
	Packet(const Config& config);
	~Packet();

	bool Read(ConnectionData* data, SOCKET socket, int timeout = -1);
	bool Send(SOCKET socket, const ConnectionData& data);

	bool SendBuf(SOCKET socket, const DataBuffer& data);
	bool ReadBuf(DataBuffer *data, NetStream& stream);

        void PrepareHeader(std::string *header, const ConnectionData& data, unsigned long dataLen, bool isLast);
private:
	const Config& config;
};

int AvailRead(SOCKET socket);

#endif
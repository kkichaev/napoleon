#include "gtkservice.h"
#include "common.h"

#include <time.h>

using namespace std;

DWORD DEBUG_DEVICE = 1;

#ifdef WIN32
#include <varargs.h>
#else
#include <stdarg.h>
#include <time.h>
typedef int64_t __int64;
#endif

#ifndef WIN32
#include <sys/resource.h>

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

void PutLog(const char *str, ...)
{
	SYSTEMTIME st;
	GetLocalTime(&st);

	va_list args;
	va_start(args, str);
	fprintf(stdout, "%02d.%02d.%d %02d:%02d:%02d.%03d "
		, st.wDay, st.wMonth, st.wYear
		, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
	vfprintf(stdout, str, args);
	fprintf(stdout, "\n");
	va_end(args);

	fflush(stdout);
}

void PutMySQLError(MYSQL *conn)
{
	PutLog("Error %u %s", mysql_errno(conn), mysql_error(conn));
}

void PutMySQLError(MYSQL_STMT *conn)
{
	PutLog("Error %u %s", mysql_stmt_errno(conn), mysql_stmt_error(conn));
}

const DWORD MAX_LIVE_PING = 2 * 60; // 2 min

const DWORD CHECK_ALIVE_INTERVAL = 30;	 // 30 sec
const DWORD ALIVE_DEVICE_INTERVAL = 60; // 60 sec
const DWORD HISTORY_CHECK_INTERVAL = 5; // 5 sec

const char ERR_NO_DEVICE[] = "no_device";
const char ERR_DEVICE_IN_VNC[] = "vnc_mode_on";

//const char PACKET_TAG[] = "GKLS";
const DWORD PACKET_TAG = 0x474B4C53;

const DWORD CMD_NONE = 0x1111;
const DWORD CMD_PING = 0;
const DWORD CMD_VNC_DATA = 1;

const DWORD CMD_VNC_REQ = 2;  // response
const DWORD CMD_BAD_DATA = 3; // response
const DWORD CMD_VNC_START = 4;
const DWORD CMD_VNC_STOP = 5;
const DWORD CMD_REQ_HIST = 6;
const DWORD CMD_REQ_HIST_LINE = 8;
const DWORD CMD_HISTORY = 7;
const DWORD CMD_BAD_REQUEST = 9;

const DWORD CMD_PHP_VNC_REQ = 100;
const DWORD CMD_PHP_VNC_CANCEL = 101;
const DWORD CMD_PHP_VNC_ACCEPT = 102;
const DWORD CMD_PHP_VNC_FAIL = 103;
const DWORD CMD_PHP_VNC_RESTART = 104;

//
// Packet format 5 32 bits words + data
//
// GKLS  <--- packet tag \x47\x4B\x4C\x53
// userid
// devceid
// command
// datalen
// [data]


DeviceData::DeviceData(const char* addr, DWORD port) : 
	userid(0), deviceid(0), id(0), datalen(0), data(NULL), cp(NULL), 
	state(stInitial), vncSocket(0), handler(NULL),
	isWebSocket(false), isDead(false), lastPing(0), 
	wrongPacket(false), ip(addr), reqHistLine(0)
{
	this->port = port;
}

DeviceData::~DeviceData()
{
	if (handler != NULL)
		handler->Closed();

	free(data);
}

bool DeviceData::ReadData(SOCKET socket)
{
	do
	{
		int rc = recv(socket, (char*)cp, datalen - (cp - data), 0);
		if (rc < 0)
		{
			return SockIsBlockError();
		}
		if (rc == 0)
		{
			return false;
		}

		cp += rc;
		if ((cp - data) == datalen)
		{

			//FILE *out = fopen("./log.txt", "wb");
			//for (int i = 0; i < datalen; i++)
			//	fputc(data[i], out);
			//fclose(out);

			state = stReady;
			break;
		}
	} while (true);
	return true;
}

static const char WS_KEY[] = "Sec-WebSocket-Key";
static const uint8_t FRAME_TYPE_CLOSE = 0x8;
static const uint8_t FRAME_TYPE_PING = 0x9;
static const uint8_t FRAME_TYPE_PONG = 0xA;
static const uint8_t FRAME_TYPE_TEXT = 0x1;
static const uint8_t FRAME_TYPE_BINARY = 0x2;

class WebSocketHandshake {
	template <int N, typename T>
	struct static_for {
		void operator()(uint32_t *a, uint32_t *b) {
			static_for<N - 1, T>()(a, b);
			T::template f<N - 1>(a, b);
		}
	};

	template <typename T>
	struct static_for<0, T> {
		void operator()(uint32_t *a, uint32_t *hash) {}
	};

	template <int state>
	struct Sha1Loop {
		static inline uint32_t rol(uint32_t value, size_t bits) { return (value << bits) | (value >> (32 - bits)); }
		static inline uint32_t blk(uint32_t b[16], size_t i) {
			return rol(b[(i + 13) & 15] ^ b[(i + 8) & 15] ^ b[(i + 2) & 15] ^ b[i], 1);
		}

		template <int i>
		static inline void f(uint32_t *a, uint32_t *b) {
			switch (state) {
			case 1:
				a[i % 5] += ((a[(3 + i) % 5] & (a[(2 + i) % 5] ^ a[(1 + i) % 5])) ^ a[(1 + i) % 5]) + b[i] + 0x5a827999 + rol(a[(4 + i) % 5], 5);
				a[(3 + i) % 5] = rol(a[(3 + i) % 5], 30);
				break;
			case 2:
				b[i] = blk(b, i);
				a[(1 + i) % 5] += ((a[(4 + i) % 5] & (a[(3 + i) % 5] ^ a[(2 + i) % 5])) ^ a[(2 + i) % 5]) + b[i] + 0x5a827999 + rol(a[(5 + i) % 5], 5);
				a[(4 + i) % 5] = rol(a[(4 + i) % 5], 30);
				break;
			case 3:
				b[(i + 4) % 16] = blk(b, (i + 4) % 16);
				a[i % 5] += (a[(3 + i) % 5] ^ a[(2 + i) % 5] ^ a[(1 + i) % 5]) + b[(i + 4) % 16] + 0x6ed9eba1 + rol(a[(4 + i) % 5], 5);
				a[(3 + i) % 5] = rol(a[(3 + i) % 5], 30);
				break;
			case 4:
				b[(i + 8) % 16] = blk(b, (i + 8) % 16);
				a[i % 5] += (((a[(3 + i) % 5] | a[(2 + i) % 5]) & a[(1 + i) % 5]) | (a[(3 + i) % 5] & a[(2 + i) % 5])) + b[(i + 8) % 16] + 0x8f1bbcdc + rol(a[(4 + i) % 5], 5);
				a[(3 + i) % 5] = rol(a[(3 + i) % 5], 30);
				break;
			case 5:
				b[(i + 12) % 16] = blk(b, (i + 12) % 16);
				a[i % 5] += (a[(3 + i) % 5] ^ a[(2 + i) % 5] ^ a[(1 + i) % 5]) + b[(i + 12) % 16] + 0xca62c1d6 + rol(a[(4 + i) % 5], 5);
				a[(3 + i) % 5] = rol(a[(3 + i) % 5], 30);
				break;
			case 6:
				b[i] += a[4 - i];
			}
		}
	};

	static inline void sha1(uint32_t hash[5], uint32_t b[16]) {
		uint32_t a[5] = { hash[4], hash[3], hash[2], hash[1], hash[0] };
		static_for<16, Sha1Loop<1>>()(a, b);
		static_for<4, Sha1Loop<2>>()(a, b);
		static_for<20, Sha1Loop<3>>()(a, b);
		static_for<20, Sha1Loop<4>>()(a, b);
		static_for<20, Sha1Loop<5>>()(a, b);
		static_for<5, Sha1Loop<6>>()(a, hash);
	}

	static inline void base64(unsigned char *src, char *dst) {
		const char *b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		for (int i = 0; i < 18; i += 3) {
			*dst++ = b64[(src[i] >> 2) & 63];
			*dst++ = b64[((src[i] & 3) << 4) | ((src[i + 1] & 240) >> 4)];
			*dst++ = b64[((src[i + 1] & 15) << 2) | ((src[i + 2] & 192) >> 6)];
			*dst++ = b64[src[i + 2] & 63];
		}
		*dst++ = b64[(src[18] >> 2) & 63];
		*dst++ = b64[((src[18] & 3) << 4) | ((src[19] & 240) >> 4)];
		*dst++ = b64[((src[19] & 15) << 2)];
		*dst++ = '=';
	}

public:
	static inline void generate(const char input[24], char output[28]) {
		uint32_t b_output[5] = {
			 0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0
		};
		uint32_t b_input[16] = {
			 0, 0, 0, 0, 0, 0, 0x32353845, 0x41464135, 0x2d453931, 0x342d3437, 0x44412d39,
			 0x3543412d, 0x43354142, 0x30444338, 0x35423131, 0x80000000
		};

		for (int i = 0; i < 6; i++) {
			b_input[i] = (input[4 * i + 3] & 0xff) | (input[4 * i + 2] & 0xff) << 8 | (input[4 * i + 1] & 0xff) << 16 | (input[4 * i + 0] & 0xff) << 24;
		}
		sha1(b_output, b_input);
		uint32_t last_b[16] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 480 };
		sha1(b_output, last_b);
		for (int i = 0; i < 5; i++) {
			uint32_t tmp = b_output[i];
			char *bytes = (char *)&b_output[i];
			bytes[3] = tmp & 0xff;
			bytes[2] = (tmp >> 8) & 0xff;
			bytes[1] = (tmp >> 16) & 0xff;
			bytes[0] = (tmp >> 24) & 0xff;
		}
		base64((unsigned char *)b_output, output);
	}
};

bool DeviceData::MakeHandshake(SOCKET socket)
{
	std::string key = headers[WS_KEY];
	if (key.empty())
	{
		return false;
	}

	char hsBuf[29];
	WebSocketHandshake::generate(key.c_str(), hsBuf);

	std::string msg("HTTP/1.1 101 Switching Protocols\r\n");
	msg.append("Upgrade: websocket\r\n");
	msg.append("Connection: Upgrade\r\n");
	msg.append("Sec-WebSocket-Accept: ").append(hsBuf, 28).append("\r\n\r\n");

	bool ret = true;
	if (send(socket, msg.c_str(), msg.size(), 0) <= 0)
	{
		ret = false;
		PutLog("Bad handshake send");
	}

	state = stWSData;
	datalen = 0;
	free(data);
	data = NULL;
	return ret;
}

bool DeviceData::ReadWSData(SOCKET socket)
{
	if (!ReadData(socket))
	{
		isDead = true;
		return false;
	}

	if (state = stReady)
	{
		for (unsigned i = 0; i < datalen; i++) {
			data[i] = (data[i] ^ mask[i % 4]);
		}

		if (datalen < 5 * sizeof(DWORD))
			return false;

		DWORD* pd = (DWORD*)data;
		if (*pd++ != PACKET_TAG)
			return false;

		userid = *pd++;
		deviceid = *pd++;
		command = *pd++;
		DWORD crc = *pd++;
		datalen -= 5 * sizeof(DWORD);

		unsigned char* pb = NULL;
		if (datalen > 0)
		{
			pb = (unsigned char*)malloc(datalen);
			memcpy(pb, pd, datalen);
		}
		free(data);
		data = pb;
	}
	return true;
}

bool DeviceData::ReadWSFrame(SOCKET socket)
{
	if (datalen > 0)
		return ReadWSData(socket);

	uint8_t val[2];
	int rc = recv(socket, (char*)val, sizeof(val), 0);
	if (rc < sizeof(val))
	{
		isDead = !SockIsBlockError();
		return false;
	}

	bool haveMask = ((val[1] & 0x80) > 0);
	if (!haveMask)
	{
		return false;
	}

	bool isLast = ((val[0] & 0x80) > 0);
	frameType = (val[0] & 0x0F);

	mask[0] = 0;
	mask[1] = 0;
	mask[2] = 0;
	mask[3] = 0;

	datalen = (val[1] & 0x7f);
	unsigned hdrLen = (haveMask) ? 4 : 0;

	if (datalen == 0x7e) {
		hdrLen += 2;
	}
	else if (datalen == 0x7f) {
		hdrLen += 8;
	}

	if (hdrLen > 0) {
		uint8_t* hdr = (uint8_t*)alloca(hdrLen), *p;
		rc = recv(socket, (char*)hdr, hdrLen, 0);
		if (rc < (int)hdrLen)
		{
			return false;
		}

		p = hdr;
		if (datalen == 0x7e) {
			datalen = ntohs(*((uint16_t*)p));
			p += 2;
		}
		else if (datalen == 0x7f) {
			// works with 32 bits only not 64
			datalen = ntohl(*((uint32_t*)(p + 4)));
			p += 8;
		}
		for (int i = 0; i < 4; i++)
			mask[i] = p[i];
	}

	free(data);
	data = NULL;
	if (datalen > 0)
	{
		data = (unsigned char*)malloc(datalen);
		cp = data;

		return ReadWSData(socket);
	}

	if (frameType == FRAME_TYPE_PING)
	{
		command = CMD_NONE;
		return SendCtl(socket, FRAME_TYPE_PONG);
	}
	return true;
}

bool DeviceData::SendCtl(SOCKET socket, uint8_t frameType)
{
	uint16_t opCode = frameType;
	opCode |= 0x80;

	int rc = send(socket, (const char*)&opCode, sizeof(opCode), 0);
	return (rc > 0);
}

bool DeviceData::SendWSData(SOCKET dest, const uint8_t* data, unsigned size, uint8_t type) {
	unsigned packetLen = (size < 0x7e) ? size + 2 :
		(size < 0x10000) ? size + 4 :
		size + 10;

	uint8_t *buf = (uint8_t*)malloc(packetLen), *cp;
	if (buf == NULL)
		return false;

	*buf = (0x80 | type);
	if (size < 0x7e) {
		buf[1] = (uint8_t)size;
		cp = buf + 2;
	}
	else if (size < 0x10000) {
		buf[1] = 0x7e;
		*((uint16_t*)(buf + 2)) = htons((uint16_t)size);
		cp = buf + 4;
	}
	else {
		buf[1] = 0x7f;
		*((uint32_t*)(buf + 2)) = 0;
		*((uint32_t*)(buf + 6)) = htonl((uint32_t)size);
		cp = buf + 10;
	}

	memcpy(cp, data, size);

	unsigned sent = 0;
	while (sent < packetLen) {
		int result = send(dest, (const char*)buf + sent, packetLen - sent, 0);
		if (result == SOCKET_ERROR) {
			return false;
		}

		sent += result;
	}

	free(buf);
	return true;
}

bool DeviceData::ReadHandshake(SOCKET socket)
{
	bool ret = true;
	do
	{
		char buf[1000];
		int rc = recv(socket, buf, sizeof(buf), 0);

		if (rc < 0)
		{
			ret = SockIsBlockError();
			break;
		}
		if (rc == 0)
		{
			ret = false;
			break;
		}

		headerBuffer.append(buf, rc);
	} while (true);
	if (ret)
	{
		std::string::size_type pos = 0;
		while (true)
		{
			std::string::size_type epos = headerBuffer.find('\n', pos);
			if (epos != std::string::npos)
			{
				std::string::size_type div = headerBuffer.find(':', pos);
				if (div != std::string::npos && div < epos)
				{
					std::string key, value;
					Trim(&key, headerBuffer, pos, div - pos);
					Trim(&value, headerBuffer, div + 1, epos - div);
					headers[key] = value;
				}
				else
				{
					// if have empty liine do handshake
					if (epos == pos || (epos == pos + 1 && headerBuffer[pos] == '\r'))
					{
						break;
					}
				}
			}
			else
			{
				break;
			}
			pos = epos + 1;
		}
		return MakeHandshake(socket);
	}
	return ret;
}


bool DeviceData::Read(SOCKET socket)
{
	if (state == stWSHandshake)
		return ReadHandshake(socket);
	if (state == stReadingData)
		return ReadData(socket);
	if (state == stWSData)
		return ReadWSFrame(socket);
	if (state == stReady && isWebSocket)
	{
		state = stWSData;
		return ReadWSFrame(socket);
	}

	DWORD buf[5];
	int rc = recv(socket, (char*)buf, sizeof(DWORD), 0);
	if (rc <= 0)
	{
#ifdef WIN32
		PutLog("RECV error %d", WSAGetLastError());
#else
		PutLog("RECV error %d", errno);
#endif
		isDead = true;
		return false;
	}

	DWORD packetTag = *buf;
	command = CMD_NONE;

	if (*buf != PACKET_TAG)
	{
		if (strnicmp((const char*)buf, "GET", 3) == 0)
		{
			state = stWSHandshake;
			isWebSocket = true;
			return ReadHandshake(socket);
		}
		PutLog("%s:%d wrong packet tag %X", ip.c_str(), port, packetTag);
		wrongPacket = true;
		return false;
	}

	rc = recv(socket, (char*)buf, sizeof(buf), 0);
	if (rc < sizeof(buf))
		return false;


	wrongPacket = false;

	DWORD cc = -1;
	for (int i = 0; i < 4 * sizeof(DWORD); i++)
		cc = doCRC(*((unsigned char*)buf + i), cc);

	DWORD crc = buf[4];
	if (cc != crc && crc != 0)
	{
		wrongPacket = true;

		char outbuf[200];
		*outbuf = 0;
		PutLog("%s:%d wrong CRC got = %X crc = %X", ip.c_str(), port, crc, cc);
		int i = 0;
		unsigned char* pp = (unsigned char*)&packetTag;
		for (i = 0; i < sizeof(DWORD); i++)
		{
			sprintf(outbuf + strlen(outbuf), "%02X ", *(pp + i));
		}
		for (i = 0; i < 5 * sizeof(DWORD); i++)
		{
			sprintf(outbuf + strlen(outbuf), "%02X ", *((unsigned char*)buf + i));
		}
		//PutLog("Packet is %s", outbuf);
		return false;
	}
	//{
	//PutLog("CRC OK");
	//}

	userid = buf[0];
	deviceid = buf[1];
	command = buf[2];
	datalen = buf[3];


	free(data);
	data = NULL;

	if (datalen > 0)
	{
		data = (unsigned char*)malloc(datalen);
		cp = data;
		state = stReadingData;
		return ReadData(socket);
	}

	state = stReady;
	return true;
}

void DeviceData::CloseVNCConnection()
{
	if (handler != NULL)
		handler->Closed();
	handler = NULL;
	vncSocket = 0;
}

bool DeviceData::SendPacket(SOCKET socket, DWORD command, unsigned char* pdata, unsigned dataLen)
{
	unsigned pktLen = dataLen + 6 * sizeof(DWORD);
	//	unsigned pktLen = dataLen + 5 * sizeof(DWORD);
	unsigned char* buf = (unsigned char*)malloc(pktLen);
	DWORD* p = (DWORD*)buf;

	*p++ = PACKET_TAG;
	*p++ = userid;
	*p++ = deviceid;
	*p++ = command;
	*p++ = dataLen;
	*p++ = 0; // CRC
	if (dataLen > 0)
		memcpy(p, pdata, dataLen);

	bool res = true;
	if (isWebSocket)
	{
		res = SendWSData(socket, buf, pktLen, FRAME_TYPE_BINARY);
	}
	else
	{
		//std::string line;
		//for (unsigned cb = 0; cb < sizeof(DWORD) * 5; cb++)
		//{
		//	char hexBuf[5];
		//	sprintf(hexBuf, "%02X", buf[cb]);
		//	line += hexBuf;
		//	line += " ";
		//}
		//PutLog("H: %s", line.c_str());

		//line.clear();
		//if (dataLen > 0)
		//{
		//	for (unsigned cb = 0; cb < dataLen; cb++)
		//	{
		//		char hexBuf[5];
		//		sprintf(hexBuf, "%02X", buf[sizeof(DWORD) * 5 + cb]);
		//		line += hexBuf;
		//		line += " ";
		//	}
		//}
		//PutLog("D: %s", line.c_str());

		int rc = send(socket, (const char*)buf, pktLen, 0);
		res = (rc == pktLen);
	}
	free(buf);
	return res;
}

ServerData::ServerData() : mysql(NULL), socket(-1)
{
	lastLiveDeviceCheck = time(NULL);
}

ServerData::~ServerData()
{
	std::map<SOCKET, DeviceData*>::iterator i = devices.begin();
	for (; i != devices.end(); i++)
		delete i->second;
	devices.clear();
}

bool ServerData::Start()
{
	config.Read();

	socket = ::socket(AF_INET, SOCK_STREAM, 0);
	if (!config.dbDatabase.empty())
	{
		mysql = mysql_init(NULL);
		my_bool reconnect = 1;
		mysql_options(mysql, MYSQL_OPT_RECONNECT, &reconnect);

		if (mysql_real_connect(mysql, config.dbHost.c_str(), config.dbLogin.c_str(), config.dbPassword.c_str(), config.dbDatabase.c_str(), config.dbPort, NULL, 0) == NULL)
		{
			PutMySQLError(mysql);
			return false;
		}
		mysql_autocommit(mysql, 1);
	}

	int on = 1;
	int rc = setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, (char *)&on, sizeof(on));

	SetNonBlock(socket);
	//#ifdef WIN32
	//	ioctlsocket(socket, FIONBIO, (u_long*)&on);
	//#else
	//	int flags = fcntl(socket, F_GETFL, 0);
	//	fcntl(socket, F_SETFL, flags | O_NONBLOCK);
	//#endif

	sockaddr_in sockaddr;
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_addr.s_addr = INADDR_ANY;
	sockaddr.sin_port = htons(config.port);

	if (bind(socket, (struct sockaddr*)&sockaddr, sizeof(sockaddr)) == SOCKET_ERROR)
	{
		PutLog("Cant bind to %d", config.port);
		return false;
	}

	if (listen(socket, 5) == SOCKET_ERROR)
	{
		return false;
	}

	return true;
}

void ServerData::Close()
{
	if (socket > 0)
	{
		closesocket(socket);
		socket = -1;
	}

	if (mysql != NULL)
	{
		mysql_close(mysql);
		mysql = NULL;
	}
}

static void BindString(MYSQL_BIND* bind, const char* str, unsigned long* length, my_bool* is_null)
{
	bind->buffer_type = MYSQL_TYPE_STRING;
	bind->buffer = (void*)str;
	bind->buffer_length = *length;

	bind->is_null = (my_bool*)0;
	bind->error = (my_bool*)0;
	bind->length = length;
}

//static void BindInt(MYSQL_BIND* bind, int *value, my_bool* is_null)
//{
//	bind->buffer_type = MYSQL_TYPE_LONG;
//	bind->buffer = (void*)value;
//	bind->buffer_length = sizeof(*value);
//
//	bind->is_null = (my_bool*)0;
//	bind->error = (my_bool*)0;
//	bind->length = NULL;
//	bind->is_unsigned = false;
//}
//
static void BindUInt(MYSQL_BIND* bind, DWORD *value, my_bool* is_null)
{
	bind->buffer_type = MYSQL_TYPE_LONG;
	bind->buffer = (void*)value;
	bind->buffer_length = sizeof(*value);

	bind->is_null = (my_bool*)0;
	bind->error = (my_bool*)0;
	bind->length = NULL;
	bind->is_unsigned = true;
}

const char* ReadKeyValue(const char *p, const char* ep, unsigned int *key, unsigned int *value)
{
	while (p < ep && !isdigit(*p))
		p++;

	if (p >= ep)
		return NULL;

	*key = strtoul(p, (char**)&p, 10);

	while (p < ep && !isdigit(*p))
		p++;

	if (p >= ep)
		return NULL;

	*value = strtoul(p, (char**)&p, 10);
	return p;
}

static DWORD GetId(MYSQL *mysql, const DeviceData& dd)
{
	DWORD id = 0;
	char buf[200];
	sprintf(buf, "select id from `devices` where number = %d and user_id = (select id from `users` where userid = %d)", dd.deviceid, dd.userid);
	if (mysql_query(mysql, buf) != 0)
	{
		PutMySQLError(mysql);
		return id;
	}

	MYSQL_RES *res = mysql_store_result(mysql);
	if (res == NULL)
	{
		PutMySQLError(mysql);
		return id;
	}

	MYSQL_ROW row = mysql_fetch_row(res);
	if (row != NULL && row[0] != NULL)
		id = (DWORD)stoul(row[0]);

	mysql_free_result(res);
	if (id == 0)
	{
		PutLog("No device found for user %d and device number %d", dd.userid, dd.deviceid);
	}
	return id;
}

const DWORD	ALARMTYPE = 1073807363;
const DWORD HUMIDITY_SENSOR1 = 2147745840;
static bool HaveAlarm(const DeviceData& dd)
{
	unsigned char* p = dd.data;
	unsigned char* ep = (dd.data + dd.datalen);
	while (p < ep)
	{
		if (*(DWORD*)p == ALARMTYPE && *(p + sizeof(DWORD)) == '1')
			return true;
		//if (*p == HUMIDITY_SENSOR1 && p[1] == 0)
		//	return true;
		p += sizeof(DWORD);
		unsigned char *check = p;
		while (*p != '\0')
			p++;
		p++;

		//int rc = ((p - check) % 4);
		//if (rc != 0)
		//	p += (4 - rc);
	}
	return false;
}

bool ServerData::UpdateDeviceData(DeviceData* dd)
{
	dd->lastPing = time(NULL);

	// the fake mode
	if (mysql == NULL)
		return true;

	if (dd->id == 0)
	{
		dd->id = GetId(mysql, *dd);
		if (dd->id == 0)
			return false;
	}

	bool isAlarm = HaveAlarm(*dd);

	char buf[200];
	sprintf(buf, "DELETE FROM `device_connects` WHERE device_id=%d", dd->id);
	if (mysql_query(mysql, buf) != 0)
	{
		PutMySQLError(mysql);
		return true;
	}

	sprintf(buf, "INSERT INTO `device_connects` (device_id,time,alarm) VALUES (%d,UNIX_TIMESTAMP(),%d)", dd->id, (isAlarm ? 1 : 0));
	if (mysql_query(mysql, buf) != 0)
	{
		PutMySQLError(mysql);
		return true;
	}


	if (dd->datalen >= 8)
	{
		sprintf(buf, "INSERT INTO `device_connects_data` (device_id, param, value) VALUES (%d, ?, ?)", dd->id);
		MYSQL_STMT* stmt = mysql_stmt_init(mysql);
		if (mysql_stmt_prepare(stmt, buf, strlen(buf)) != 0)
		{
			PutMySQLError(stmt);
			mysql_stmt_close(stmt);
			return true;
		}

		MYSQL_BIND* bind = (MYSQL_BIND*)malloc(sizeof(MYSQL_BIND) * 2);
		DWORD key = 0, value = 0;
		BindUInt(bind, &key, NULL);
		//		BindUInt(bind + 1, &value, NULL);

		const char* p = (const char*)dd->data;
		const char* ep = (const char*)(dd->data + dd->datalen);
		while (p < ep)
		{
			key = *(DWORD*)p;
			p += sizeof(DWORD);

			unsigned long len = strlen(p), tlen;
			BindString(bind + 1, (char*)p, &len, NULL);

			mysql_stmt_bind_param(stmt, bind);

			len++;
			p += len;
			//int rc = len % 4;
			//if (rc != 0)
			//	p += (4 - rc);

			if (mysql_stmt_execute(stmt) != 0)
			{
				PutMySQLError(stmt);
			}
		}

		free(bind);
		mysql_stmt_close(stmt);
	}

	return true;
}

DeviceData* ServerData::RemoveDevice(SOCKET socket)
{
	DeviceData* ret = NULL;
	threadMutex.Lock();

	std::map<SOCKET, DeviceData*>::iterator fnd = devices.find(socket);
	if (fnd != devices.end())
	{
		ret = fnd->second;
		devices.erase(fnd);
	}
	threadMutex.Unlock();

	return ret;
}

void ServerData::CloseDevice(SOCKET socket)
{
	DeviceData* dd = RemoveDevice(socket);
	RemoveConnection(socket);

	if (dd != NULL)
	{
		delete dd;
	}

	//struct linger sl;
	//sl.l_onoff = 1;		/* non-zero value enables linger option in kernel */
	//sl.l_linger = 0;	/* timeout interval in seconds */
	//setsockopt(socket, SOL_SOCKET, SO_LINGER, &sl, sizeof(sl));

	PutLog("Close socket %d", socket);
	//	shutdown(socket, SHUT_RDWR);
	closesocket(socket);
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

SOCKET ServerData::FindDevice(const DeviceData& device)
{
	SOCKET ret = 0;

	threadMutex.Lock();
	std::map<SOCKET, DeviceData*>::iterator i = devices.begin();
	for (; i != devices.end(); i++)
	{
		if (!i->second->IsWebSocket() && *i->second == device)
		{
			ret = i->first;
			break;
		}
	}
	threadMutex.Unlock();

	return ret;
}

//void DumpPacket(unsigned char* buf, int size)
//{
//	if (size < 30)
//	{
//		char outbuf[200];
//		*outbuf = 0;
//		for (int i = 0; i < size; i++)
//		{
//			sprintf(outbuf + strlen(outbuf), "%02X ", *(buf + i));
//		}
//		PutLog(">> %s", outbuf);
//	}
//}

void ServerData::TryVNCRecconect(SOCKET socket, DeviceData* device, bool needStopVNC)
{
	if (needStopVNC)
	{
		device->SendPacket(socket, CMD_VNC_STOP, NULL, 0);
	}
	SOCKET wsSocket = 0;
	DeviceData* wsDevice = NULL;

	threadMutex.Lock();
	std::map<SOCKET, DeviceData*>::iterator i = devices.begin();
	for (; i != devices.end(); i++)
	{
		DeviceData *dd = i->second;
		if (dd->IsWebSocket() && (*dd)== (*device))
		{
			wsSocket = i->first;
			wsDevice = dd;
			break;
		}
	}
	threadMutex.Unlock();

	device->CloseVNCConnection();
	if (wsDevice != NULL)
	{
		PutLog("Sends vnc restart %d", wsSocket);
		wsDevice->SendPacket(wsSocket, CMD_PHP_VNC_RESTART, NULL, 0);
	}
	else
	{
		//		PutLog("No web socket to restart VNC");
	}
}

bool ServerData::HandleClient(SOCKET socket)
{
	DeviceData *dd = NULL;
	std::map<SOCKET, DeviceData*>::iterator fnd = devices.find(socket);
	if (fnd != devices.end())
	{
		dd = fnd->second;
		if (!dd->Read(socket))
		{
			if (dd->isDead)
			{
				PutLog("Remove (%d)%s dead socket %d", dd->deviceid, (dd->IsWebSocket() ? " WS" : ""), socket);
				CloseDevice(socket);
				return false;
			}

			if (!dd->IsWebSocket())
			{
				PutLog("%s:%d have wrong packet from socket %d. CLose device", dd->ip.c_str(), dd->port, socket);
				CloseDevice(socket);
				return false;
			}

			//if (!dd->IsWebSocket() && dd->WrongPacket() && dd->vncSocket != 0)
			//{
			//	TryVNCRecconect(socket, dd, true);
			//}
			return true;
		}
	}
	else
	{
		if (dd == NULL)
		{
			const char* ip = "";
			DWORD port = 0;

			std::map<SOCKET, sockaddr_in>::const_iterator fnd = address.find(socket);
			if (fnd != address.end())
			{
				ip = inet_ntoa(fnd->second.sin_addr);
				port = htons(fnd->second.sin_port);
			}

			dd = new DeviceData(ip, port);

			if (!dd->Read(socket))
			{
				delete dd;
				PutLog("Close socket DD %d", socket);
				closesocket(socket);
				return false;
			}

			PutLog("Accept %s:%d user %d, id %d on %d", ip, port, dd->userid, dd->deviceid, socket);
			SOCKET devSocket;
			while ((devSocket = FindDevice(*dd)) > 0)
			{
				CloseDevice(devSocket);
			}

			threadMutex.Lock();
			devices[socket] = dd;
			threadMutex.Unlock();

			if (!dd->IsWebSocket())
				TryVNCRecconect(socket, dd, false);
		}
	}

	bool ret = true;
	if (dd->Ready())
	{
		//PutLog("Device connected user %d, number %d, command %d", dd->userid, dd->deviceid, dd->command);

		// now we can handle client packet
		if (dd->command == CMD_PING)
		{
			if (!UpdateDeviceData(dd))
			{
				//dd->SendPacket(socket, CMD_BAD_DATA, NULL, 0);
				CloseDevice(socket);
				return false;
			}
			if (dd->vncSocket != 0)
			{
				PutLog("PING in VNC mode. try reconnect");
				TryVNCRecconect(socket, dd, false);
			}
		}
		else if (dd->command == CMD_HISTORY)
		{
			PutHistoryLine(socket, dd);
		}
		else if (dd->command == CMD_VNC_DATA)
		{
//			if (dd->id == DEBUG_DEVICE)
//				PutLog("Rcv from VNC device %d", dd->datalen);
			if (dd->vncSocket == 0)
			{
				return true;
				//dd->SendPacket(socket, CMD_BAD_DATA, NULL, 0);
				//CloseDevice(socket);
				//return false;
			}
			int rc = send(dd->vncSocket, (const char*)dd->data, dd->datalen, 0);
			if (rc <= 0)
			{
				PutLog("Send to vncDevice error ");
			}
			//DumpPacket(dd->data, dd->datalen);
		}
		else if (dd->command == CMD_PHP_VNC_REQ)
		{
			// request from PHP, not device. 
			PutLog("%s:%d request VNC userid %d, deviceid %d", dd->ip.c_str(), dd->port, dd->userid, dd->deviceid);


			char notAvailErr[] = "Device is not available now, please try again later";

			dd->vncSocket = socket;
			SOCKET reqSocket = FindDevice(*dd);
			if (reqSocket == 0)
			{
				dd->SendPacket(socket, CMD_PHP_VNC_FAIL, (unsigned char*)notAvailErr, sizeof(notAvailErr) - 1);
				CloseDevice(socket);
			}
			else
			{
				DeviceData* reqDevice = devices[reqSocket];
				if (reqDevice->vncSocket != 0)
				{
					char error[] = "Another user is connected, please try again later";
					dd->SendPacket(socket, CMD_PHP_VNC_FAIL, (unsigned char*)error, sizeof(error) - 1);
					CloseDevice(socket);
				}
				else
				{
					time_t curr = time(NULL);
					if (curr - reqDevice->lastPing > MAX_LIVE_PING)
					{
						dd->SendPacket(socket, CMD_PHP_VNC_FAIL, (unsigned char*)notAvailErr, sizeof(notAvailErr) - 1);
						CloseDevice(socket);
					}
					else
					{
						reqDevice->SendPacket(reqSocket, CMD_VNC_REQ, NULL, 0);
						CreateVNCConnection(dd, reqDevice, reqSocket);
					}
				}
			}
		}
		else if (dd->command == CMD_PHP_VNC_CANCEL)
		{
			// request from PHP, not device. 
			PutLog("Cancel VNC userid %d, deviceid %d", dd->userid, dd->deviceid);
			CloseDevice(socket);
			ret = false;
		}
	}

	time_t curr = time(NULL);
	if (curr - lastLiveDeviceCheck > CHECK_ALIVE_INTERVAL)
	{
		lastLiveDeviceCheck = curr;

		threadMutex.Lock();
		std::vector<SOCKET> removed;
		std::map<SOCKET, DeviceData*>::iterator i = devices.begin();
		for (; i != devices.end(); i++)
		{
			DeviceData *dd = i->second;
			if (!dd->IsWebSocket() && (curr - dd->lastPing > ALIVE_DEVICE_INTERVAL) && dd->vncSocket == 0)
			{
				PutLog("Remove device %d.%d", dd->userid, dd->deviceid);
				removed.push_back(i->first);
			}
		}
		threadMutex.Unlock();
		std::vector<SOCKET>::const_iterator ri = removed.begin();
		for (; ri != removed.end(); ri++)
		{
			CloseDevice(*ri);
		}

	}

	return ret;
}

static int BindToRandomPort(SOCKET socket, int minPort, int maxPort)
{
	srand((unsigned)time(NULL));

	int tryCount = 100;

	do
	{
		int port = rand() % (maxPort - minPort) + minPort;
		sockaddr_in sockaddr;
		sockaddr.sin_family = AF_INET;
		sockaddr.sin_addr.s_addr = INADDR_ANY;
		sockaddr.sin_port = htons(port);

		if (bind(socket, (struct sockaddr*)&sockaddr, sizeof(sockaddr)) != SOCKET_ERROR)
			return port;
	} while (tryCount-- > 0);

	return 0;
}

class VNCThread : public Thread, DeviceEvents
{
public:
	VNCThread(DeviceData* device, SOCKET vncSocket, SOCKET deviceSocket);
	~VNCThread();

	virtual void Execute();
	virtual void Closed();

private:
	DeviceData* device;
	SOCKET vncSocket;
	SOCKET deviceSocket;
	bool closing;
};

VNCThread::VNCThread(DeviceData* device, SOCKET vncSocket, SOCKET deviceSocket) : closing(false)
{
	this->device = device;
	this->vncSocket = vncSocket;
	this->deviceSocket = deviceSocket;

	device->EstablishVNCConnection(0, this);
}

VNCThread::~VNCThread()
{
	if (device != NULL)
	{
		if (device->vncSocket != 0) {
			PutLog("Close vnc socket %d thread delete", device->vncSocket);
			closesocket(device->vncSocket);
		}
		device->SendPacket(deviceSocket, CMD_VNC_STOP, NULL, 0);
		device->EstablishVNCConnection(0, NULL);
	}


	//	shutdown(vncSocket, SHUT_RDWR);
	PutLog("Close listen vncSocket %d", vncSocket);
	closesocket(vncSocket);
}

void VNCThread::Execute()
{
	struct timeval timeout;
	fd_set master, working;
	int maxD;

	listen(vncSocket, 5);

	FD_ZERO(&master);
	maxD = vncSocket;
	FD_SET(vncSocket, &master);

	do
	{
		memcpy(&working, &master, sizeof(master));
		timeout.tv_sec = 1;
		timeout.tv_usec = 0;

		int rc = select(maxD + 1, &working, NULL, NULL, &timeout);
		if (device == NULL || rc < 0)
			break;
		if (rc == 0)
		{
			if (closing)
				break;
			continue;
		}

		int dscCount = rc;
		DeviceData* dd = device;

		for (int i = 0; i <= maxD && dscCount > 0 && !closing; i++)
		{
			if (FD_ISSET(i, &working))
			{
				dscCount--;

				if (i == vncSocket)
				{
					SOCKET newSocket = accept(vncSocket, NULL, NULL);
					if ((int)newSocket > 0)
					{
						SetNonBlock(newSocket);

						if (dd->vncSocket == 0)
						{
							PutLog("VNC client accept %d", newSocket);

							dd->EstablishVNCConnection(newSocket, this);
							PutLog("Send VNC_START");
							if (!dd->SendPacket(deviceSocket, CMD_VNC_START, NULL, 0))
							{
								PutLog("Wrong socket");
								closing = true;
								device->EstablishVNCConnection(0, NULL);
								device = NULL;
								break;
							}

							FD_SET(newSocket, &master);
							if (newSocket > (SOCKET)maxD)
								maxD = newSocket;
						}
						else
						{
							/// only one connection
							PutLog("Close newSocket %d", newSocket);
							closesocket(newSocket);
						}
					}
				}
				else
				{
					do
					{
						char buffer[1000];
						rc = recv(i, buffer, sizeof(buffer), 0);
						if (rc < 0)
						{
							if (!SockIsBlockError())
							{
								closing = true;
								//PutLog("Error close Device");
							}
							break;
						}
						if (rc == 0)
						{
							//PutLog("No Data closing socket");
							closing = true;
							break;
						}
//						if(dd->id == DEBUG_DEVICE)
//							PutLog("Send to VNC device %d", rc);

						dd->SendPacket(deviceSocket, CMD_VNC_DATA, (unsigned char*)buffer, rc);
						//DumpPacket((unsigned char*)buffer, rc);
					} while (false);
				}
			}
		}

	} while (!closing);
	closing = false;

	//PutLog("Close VNC");
	closesocket(vncSocket);
	PutLog("Close vncSocket %d", vncSocket);
}


void VNCThread::Closed()
{
	if (device != NULL && device->vncSocket != 0) {
		PutLog("Close vnc socket %d", device->vncSocket);
		closesocket(device->vncSocket);
	}

	device = NULL;
	closing = true;
	PutLog("Close VNC device");
}

void ServerData::CreateVNCConnection(DeviceData* vncReq, DeviceData* device, SOCKET deviceSocket)
{
	SOCKET vncSocket = 0;
	int port = 0;
	vncSocket = ::socket(AF_INET, SOCK_STREAM, 0);
	int on = 1;
	int rc = setsockopt(vncSocket, SOL_SOCKET, SO_REUSEADDR, (char *)&on, sizeof(on));

	//struct linger sl;
	//sl.l_onoff = 1;		/* non-zero value enables linger option in kernel */
	//sl.l_linger = 0;	/* timeout interval in seconds */
	//setsockopt(vncSocket, SOL_SOCKET, SO_LINGER, &sl, sizeof(sl));

	SetNonBlock(vncSocket);

	port = BindToRandomPort(vncSocket, config.vncPortMin, config.vncPortMax);
	if (port == 0)
	{
		char error[] = "Can't assign port";

		vncReq->SendPacket(vncReq->vncSocket, CMD_PHP_VNC_FAIL, (unsigned char*)error, sizeof(error) - 1);

		PutLog("Can't create VNC port %d for userid %d, deviceid %d", device->userid, device->deviceid);
		CloseDevice(vncReq->vncSocket);
		return;
	}

	PutLog("Create VNC port %d for userid %d, deviceid %d socket %d", port, device->userid, device->deviceid, vncSocket);


	vncReq->SendPacket(vncReq->vncSocket, CMD_PHP_VNC_ACCEPT, (unsigned char*)&port, sizeof(port));
	if (vncSocket != 0)
	{
		VNCThread *thread = new VNCThread(device, vncSocket, deviceSocket);
		thread->Start();
	}
}

void ServerData::MarkRequestHandled(DWORD deviceID, const char* error)
{
	char *buf = (char*)malloc(2000);
	if(!error)
		sprintf(buf, "update device_history_request set `lines`=0 where device_id=%d", deviceID);
	else
		sprintf(buf, "update device_history_request set `lines`=0, `remark`='%s', finished=UNIX_TIMESTAMP() where device_id=%d", error, deviceID);

	if (mysql_query(mysql, buf) != 0)
	{
		PutMySQLError(mysql);
	}

	free(buf);
}

static DWORD DateToHistParam(time_t date)
{
	struct tm *time = localtime((const time_t*)&date);
	return ((time->tm_year + 1900) << 16) | ((time->tm_mon + 1) << 8) | time->tm_mday;
}

void ServerData::PutHistoryLine(SOCKET s, DeviceData* dd)
{
	bool needSendRequest = true;
	if (dd->datalen >= 8)
	{
		DWORD curLine = *(DWORD*)dd->data;
		DWORD lines = *((DWORD*)dd->data + 1);
		std::string data((const char*)dd->data + 8, dd->datalen - 8);
		
		//PutLog("Socket %d get history line %d/%d", s, curLine, lines);

		if (curLine == dd->reqHistLine)
		{
			FILE* f = fopen(dd->histFile.c_str(), "ab");
			if (f)
			{
				if (data.size() > 0)
				{
					fputs(data.c_str(), f);

					if (*data.rbegin() != '\n')
						fputs("\n", f);
				}
				else
				{
					fputs("\n", f);
				}
				fclose(f);
			}

			char *buf = (char*)malloc(2000);
			if (curLine < lines)
			{
				sprintf(buf, "update device_history_request set `lines`=%d, `cur_line`=%d where device_id=%d", lines, curLine, dd->id);
				dd->reqHistLine++;
			}
			else
			{
				sprintf(buf, "update device_history_request set `cur_line`=`lines`, `file_name`='%s', finished=UNIX_TIMESTAMP() where device_id=%d", 
					dd->histFile.c_str(), dd->id);
				needSendRequest = false;
			}
			if (mysql_query(mysql, buf) != 0)
			{
				PutMySQLError(mysql);
			}

			free(buf);
		}
	}
	if(needSendRequest)
		dd->SendPacket(s, CMD_REQ_HIST_LINE, (unsigned char*)&dd->reqHistLine, 4);
}

void ServerData::HandleHistoryRequests()
{
	const char *q = "select `device_id`,`hist_start`,`hist_end`, `name` from device_history_request, devices where device_history_request.device_id = devices.id and `lines` is null";
	if (mysql_query(mysql, q) != 0)
	{
		PutMySQLError(mysql);
		return ;
	}

	MYSQL_RES *res = mysql_store_result(mysql);
	if(!res)
	{
		PutMySQLError(mysql);
		return;
	}

	while (true)
	{
		MYSQL_ROW row = mysql_fetch_row(res);
		if (row == NULL || row[0] == NULL || row[1] == 0 || row[2] == NULL)
			break;

		DWORD id = (DWORD)stoul(row[0]);
		time_t start = stoul(row[1]);
		time_t end = stoul(row[2]);

		PutLog("History request for device %d", id);

		DeviceData dd("", 0);
		dd.id = id;
		SOCKET s = FindDevice(dd);
		const char* err = NULL;
		if (s == 0)
		{
			err = ERR_NO_DEVICE;
		}
		else
		{
			DeviceData* dev = devices[s];
			if (dev->vncSocket != 0)
			{
				err = ERR_DEVICE_IN_VNC;
			}
			else
			{
				dev->reqHistLine = 0;
				char buf[500];
				struct tm startTm = *localtime(&start);
				struct tm endTm = *localtime(&end);

				sprintf(buf, "%s_%d_%02d_%02d-%d_%02d_%02d.csv", (row[3] ? row[3] : "undef"), 
					startTm.tm_year + 1900, startTm.tm_mon + 1, startTm.tm_mday,
					endTm.tm_year + 1900, endTm.tm_mon + 1, endTm.tm_mday);

				dev->histFile = config.uploadFolder + buf;
				unlink(dev->histFile.c_str());

				DWORD histData[2];
				*(histData) = DateToHistParam(start);
				*(histData + 1) = DateToHistParam(end);
				dev->SendPacket(s, CMD_REQ_HIST, (unsigned char*)histData, 8);

				PutLog("Socket %d send history request %s", s, buf);
			}
		}
		MarkRequestHandled(id, err);
	}

	mysql_free_result(res);
}

void ServerData::MainLoop()
{
	fd_set workingSet;

	bool finishServer = false;
	struct timeval timeout;

	maxD = socket;
	FD_ZERO(&masterSet);
	FD_SET(socket, &masterSet);

	time_t lastHistoryCheck = 0;

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

		time_t ct = time(NULL);
		if ((ct - lastHistoryCheck) >= HISTORY_CHECK_INTERVAL)
		{
			lastHistoryCheck = ct;
			HandleHistoryRequests();
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
//PutLog("Accept %s:%d", inet_ntoa(addr.sin_addr), htons(addr.sin_port));
							SetNonBlock(newSocket);
							//int one = 1;
							//setsockopt(newSocket, SOL_TCP, TCP_NODELAY, &one, sizeof(one));

							FD_SET(newSocket, &masterSet);
							if (newSocket > (SOCKET)maxD)
								maxD = newSocket;
						} while (true);
					}
					else
					{
//PutLog("Handle socket %d", i);
						bool removeSocket = !HandleClient(i);
						if (removeSocket) 
						{
PutLog("Remove socekt %d", i);
							RemoveConnection(i);
						}
					}
				}
			}
		}

	} while (!finishServer);
}

////select `port` from `device_settings` where `id` = '1'
//bool ServerData::ReadDeviceSettings(const std::string& id, DeviceSettings* settings)
//{
//	settings->id = id;
//	settings->port = 9876;
//
//	return true;
//	//settings->id = id;
//	//settings->port = 0;
//
//	//std::string sql("select `port` from `");
//	//sql += config.deviceConfigTable; sql += "` where `id` = '"; sql += id; sql += "'";
//
//	//MYSQL_STMT* stmt = mysql_stmt_init(mysql);
//	//if (mysql_stmt_prepare(stmt, sql.c_str(), sql.size()) != 0)
//	//{
//	//	PutMySQLError(mysql);
//
//	//	mysql_stmt_close(stmt);
//	//	return false;
//	//}
//	//
//	//bool ret = false;
//	//if (mysql_stmt_execute(stmt) == 0)
//	//{
//	//	my_bool isNull;
//	//	MYSQL_BIND* bind = (MYSQL_BIND*)malloc(sizeof(MYSQL_BIND));
//	//	BindInt(bind, &settings->port, &isNull);
//
//	//	mysql_stmt_bind_result(stmt, bind);
//	//	mysql_stmt_fetch(stmt);
//
//	//	free(bind);
//
//	//	ret = true;
//	//} else
//	//	PutMySQLError(mysql);
//
//	//mysql_stmt_close(stmt);
//	//return ret;
//}


int main(int argc, char* argv[])
{
#ifdef WIN32
	WSADATA wsaData = { 0 };
	WSAStartup(MAKEWORD(2, 2), &wsaData);
#else
	struct rlimit core_limits;
	core_limits.rlim_cur = core_limits.rlim_max = RLIM_INFINITY;
	setrlimit(RLIMIT_CORE, &core_limits);
#endif

	ServerData data;
	if (data.Start())
	{
		PutLog("Starting");

		data.MainLoop();
	}

	data.Close();

#ifdef WIN32
	WSACleanup();
#endif
	return 0;
}

#ifndef __COMMON_H
#define __COMMON_H

void PutLog(const char *str, ...);

#ifndef WIN32
#include <sys/types.h>
#include <sys/io.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <netinet/tcp.h>

typedef uint8_t BYTE;
typedef uint32_t DWORD;
typedef uint32_t ULONG;
typedef uint32_t UINT;
typedef uint16_t WORD;
typedef int64_t __int64;
typedef uint16_t BOOL;

#define TRUE (BOOL)-1
#define FALSE (BOOL)0
#define MAX_PATH PATH_MAX

typedef struct _FILETIME {
	DWORD dwLowDateTime;
	DWORD dwHighDateTime;
} FILETIME, *LPFILETIME;

typedef struct _SYSTEMTIME {
	WORD wYear;
	WORD wMonth;
	WORD wDayOfWeek;
	WORD wDay;
	WORD wHour;
	WORD wMinute;
	WORD wSecond;
	WORD wMilliseconds;
} SYSTEMTIME, *LPSYSTEMTIME;

void GetLocalTime(SYSTEMTIME *st);
BOOL SystemTimeToFileTime(const SYSTEMTIME *lpSystemTime, LPFILETIME lpFileTime);
BOOL FileTimeToSystemTime(const FILETIME* lpFileTime, LPSYSTEMTIME lpSystemTime);

typedef int SOCKET;
const int SOCKET_ERROR = -1;

inline char* _itoa(int value, char* dest, int radix)
{
    sprintf(dest, "%d", value);
    return dest;
}


inline int strnicmp(const char*str, const char*str2, int n) { return strncasecmp(str, str2, n); }

inline int closesocket(SOCKET sock) { return close(sock); }

inline bool SockIsBlockError() { return errno == EWOULDBLOCK || errno == EAGAIN; }

inline void Sleep(int ms) { usleep(ms * 1000); }

inline int SetNonBlock(int socket) {
	int flags = fcntl(socket, F_GETFL, 0);
	return fcntl(socket, F_SETFL, flags | O_NONBLOCK);
}

#else

#include <io.h>
inline bool SockIsBlockError() { return WSAGetLastError() == WSAEWOULDBLOCK; }

inline int SetNonBlock(int socket) { 
	int on = 1;
	return ioctlsocket(socket, FIONBIO, (u_long*)&on); 
}

#endif


#endif
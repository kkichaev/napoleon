#ifndef __COMMON_H
#define __COMMON_H

#include <string>
#include <map>
typedef std::map<unsigned, std::string> ValuesMap;

#define GKL_SERVER_VERSION "1.0.0.1"

void PutLog(const char *str, ...);

extern const char NEED_CONNECT_CMD[];
extern const char OK_CMD[];
extern const char UPDATE_SCREEN_CMD[];

#ifndef WIN32
#include <sys/time.h>
#include <sys/types.h>
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

#include <inttypes.h>

#include <gtk/gtk.h>
#include <gdk/gdk.h>


typedef uint8_t BYTE;
typedef uint32_t DWORD;
typedef uint32_t ULONG;
typedef uint32_t UINT;
typedef uint16_t WORD;
typedef uint16_t BOOL;

typedef long LRESULT;
#define CALLBACK

typedef long WPARAM;
typedef long LPARAM;

typedef void* HWND;
typedef void* HDC;
typedef void* HANDLE;
typedef void* HGDIOBJ;
typedef void* HBRUSH;
typedef void* HPEN;
typedef void* HBITMAP;

typedef DWORD COLORREF;

typedef struct _DRAWITME {
    
} DRAWITEMSTRUCT;

#ifndef TRUE
    #define TRUE (BOOL)-1
#endif

#ifndef FALSE
    #define FALSE (BOOL)0
#endif

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

#define wsprintfA sprintf

inline int _stricmp(const char* val1, const char *val2) {
    return strcasecmp(val1, val2);
}

inline void closesocket(SOCKET sock) { close(sock); }

inline uint64_t GetTickCount64() {
    timeval ts;
    gettimeofday(&ts,0);
    return ((uint64_t)ts.tv_sec * 1000000 + ts.tv_usec);
}

inline void _ui64toa(uint64_t val, char *buf, int radix) {
    sprintf(buf, "%"PRIx64, val);
}

#define LOBYTE(w)           ((BYTE)(((DWORD)(w)) & 0xff))
#define GetBValue(rgb)      (LOBYTE(rgb))
#define GetGValue(rgb)      (LOBYTE(((WORD)(rgb)) >> 8))
#define GetRValue(rgb)      (LOBYTE((rgb)>>16))


#endif


#endif
#ifndef UNIXCOMPAT_H_INCLUDED
#define UNIXCOMPAT_H_INCLUDED

#include <wctype.h>
#include <wchar.h>
#include <string.h>
#include <stdint.h>
#include <time.h>
#include <errno.h>
#include <stdio.h>
#include <assert.h>
#include <stdlib.h>
#include <limits.h>

#include <string>

#define __cdecl
#define WINAPI

#ifdef APPLE
#define __off_t off_t
#endif

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

typedef void* HWND;
typedef void* HANDLE;

typedef wchar_t* BSTR;
typedef wchar_t* LPWSTR;
typedef const wchar_t* LPCTSTR;

const HANDLE INVALID_HANDLE_VALUE = (HANDLE)-1;
const int SOCKET_ERROR = -1;

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

typedef int SOCKET;
const SOCKET INVALID_SOCKET = -1;

inline int _wtoi(const wchar_t* str)
{
    wchar_t *ep;
    return (int)wcstol(str, &ep, 10);
}

inline DWORD _wtol(const wchar_t* str)
{
    wchar_t *ep;
    return (int)wcstoul(str, &ep, 10);
}

inline __int64 _atoi64(const char* src)
{
   return atoll(src);
}

inline double _wtof(const wchar_t* str)
{
   return wcstod(str, NULL);
}

inline __off_t _lseek (int __fd, __off_t __offset, int __whence)
{
   return lseek(__fd, __offset, __whence);
}

inline ssize_t _write (int __fd, __const void *__buf, size_t __n)
{
   return write(__fd, __buf, __n);
}

inline ssize_t _read(int __fd, void* __buf, size_t __n)
{
   return read(__fd, __buf, __n);
}

inline int _chsize(int __fd, off_t __size)
{
   return ftruncate(__fd, __size);
}

inline void _close(int __fd)
{
   close(__fd);
}

inline int _strnicmp(const char* __s1, const char* __s2, size_t __n)
{
   return strncasecmp(__s1, __s2, __n);
}

inline int _wcsnicmp(const wchar_t* __s1, const wchar_t* __s2, size_t __n)
{
   return wcsncasecmp(__s1, __s2, __n);
}

inline off_t _filelength(int __fd)
{
   struct stat fs;
   fstat(__fd, &fs);

   return fs.st_size;
}

inline int _creat(const char* __name, int __mode)
{
   return creat(__name, __mode);
}

inline DWORD GetTickCount()
{
   timespec t;
   clock_gettime(CLOCK_MONOTONIC, &t);
   return (DWORD)t.tv_sec;
}

inline int CompareFileTime(const FILETIME* f1, const FILETIME* f2)
{
   return (f1->dwHighDateTime == f2->dwHighDateTime) ?
      f1->dwLowDateTime - f2->dwLowDateTime :
      f1->dwHighDateTime - f2->dwHighDateTime;
}

inline int GetLastError() { return errno; }

void GetLocalTime(SYSTEMTIME *st);
BOOL SystemTimeToFileTime(const SYSTEMTIME *lpSystemTime, LPFILETIME lpFileTime);
BOOL FileTimeToSystemTime(const FILETIME* lpFileTime, LPSYSTEMTIME lpSystemTime);

const int MAX_WSPRINTF_BUF = 1000;
int wsprintf(wchar_t* str, const wchar_t* format, ...);

#define wsprintfW wsprintf
#define _swprintf wsprintf
#define wsprintfA sprintf

#define _fileno fileno

#define _unlink unlink
#define DeleteFileA unlink
#define MoveFileA rename

#define _stricmp strcasecmp
#define _wcsicmp wcscasecmp
#define _wcsdup wcsdup

#if WCHAR_MAX > 0x10000
#define UTF_CP "UTF32"
#else
#define UTF_CP "UTF16"
#endif

const char* ConvHelper(const char* __src, char* __dest, size_t __srcb, size_t __destb, const char* __from, const char* __to);

inline void OemToCharBuff(const char* src, char *dest, size_t destLen)
{
   ConvHelper(src, dest, destLen, destLen, "CP866", "CP1251");
}

inline void OemToCharA(const char* src, char *dest)
{
   size_t destLen = strlen(src);
   ConvHelper(src, dest, destLen, destLen, "CP866", "CP1251");
}

void CharUpper(wchar_t *str);
void CharUpperA(char *str);

#define OemToCharBuffA OemToCharBuff
#define _ASSERT assert

const char* ConvertPath(const std::string& src, std::string* dest);
const wchar_t* ConvertPath(const std::wstring& src, std::wstring* dest);
bool MakePath(const char* path);

#endif

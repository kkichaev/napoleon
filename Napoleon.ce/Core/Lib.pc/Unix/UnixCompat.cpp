#include "stdafx.h"
#include <time.h>

#include <string>
#include <stdarg.h>

#include <iconv.h>
#include <map>

#ifdef APPLE
#include <pthread.h>
#endif

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

void ConvertArgs(std::wstring *formatEx, const wchar_t* format)
{
    formatEx->clear();
    bool prevIsProc = false;
    for(const wchar_t *p = format; *p != L'\0'; p++ )
    {
        if( *p == 's' && prevIsProc )
            formatEx->append(1, L'l');
        formatEx->append(1, *p);
        prevIsProc = (*p == L'%');
    }
}

int wsprintf(wchar_t* str, const wchar_t* format, ...)
{
    va_list args;
    va_start(args, format);

    std::wstring formatEx;

    ConvertArgs(&formatEx, format);
    const wchar_t* expr = formatEx.c_str();
    int ret = vswprintf(str, MAX_WSPRINTF_BUF, expr, args);
    va_end(args);
    return ret;
}

struct ConvData
{
   iconv_t conv;
   pthread_mutex_t mutex;
};

class Convertors : public std::map<std::string, ConvData*>
{
public:
   Convertors() {}
   ~Convertors()
   {
      iterator i = begin();
      for( ; i != end(); i ++ )
      {
         iconv_close(i->second->conv);
         pthread_mutex_destroy(&i->second->mutex);
         free(i->second);
      }
   }

   ConvData* Get(const char* to, const char* from)
   {
      std::string key(to);
      key += from;

      iterator fnd = find(key);
      if( fnd != end() )
         return fnd->second;

      ConvData *data = (ConvData*)malloc(sizeof(ConvData));
      data->conv = iconv_open(to, from);
      pthread_mutex_init(&data->mutex, NULL);
      insert(value_type(key, data));

      return data;
   }
};

static Convertors convertors;

const char* ConvHelper(const char* __src, char* __dest, size_t __srcb, size_t __destb, const char* __from, const char* __to)
{
   const char* ret = __dest;
   size_t avail = __destb;
   ConvData* data = convertors.Get(__to, __from);

   pthread_mutex_lock(&data->mutex);
   try
   {
      iconv(data->conv, (char**)&__src, &__srcb, &__dest, &__destb);
      if( __destb > 0 )
         iconv(data->conv, NULL, NULL, &__dest, &__destb);

      avail -= __destb;
      if( *(unsigned short*)ret == 0xfeff && avail > 2)
      {
         if( *((unsigned short*)ret + 1) == 0 )
         {
            memmove((void*)ret, ret+4, avail - 4);
            *(long*)((char*)ret + avail-4) = 0;
         } else
         {
            memmove((void*)ret, ret+2, avail - 2);
            *(short*)((char*)ret + avail-2) = 0;
   //         *((char*)ret + avail-1) = '\0';
         }
      }
   }
   catch(...)
   {

   }
   pthread_mutex_unlock(&data->mutex);
   return ret;
}

void CharUpper(wchar_t *str)
{
   while( *str != L'\0' )
   {
      *str = towupper(*str);
      str++;
   }
}

void CharUpperA(char *str)
{
   while( *str != L'\0' )
   {
      *str = toupper(*str);
      str++;
   }
}

const char* ConvertPath(const std::string& src, std::string* dest)
{
   if( &src != dest )
      dest->assign(src);

    std::string::size_type npos = dest->find(L'\\');
   while( npos != std::string::npos )
   {
      dest->replace(npos, 1, 1, '/');
      npos = dest->find(L'\\', npos+1);
   }
   return dest->c_str();
}

const wchar_t* ConvertPath(const std::wstring& src, std::wstring* dest)
{
   if( &src != dest )
      dest->assign(src);

    std::wstring::size_type npos = dest->find(L'\\');
   while( npos != std::string::npos )
   {
      dest->replace(npos, 1, 1, '/');
      npos = (unsigned)dest->find(L'\\', npos+1);
   }
   return dest->c_str();
}

static bool do_mkdir(const char *path, mode_t mode)
{
    struct stat            st;
    bool status = true;

    if (stat(path, &st) != 0)
    {
        /* Directory does not exist. EEXIST for race condition */
        if (mkdir(path, mode) != 0 && errno != EEXIST)
            status = false;
    }
    else if (!S_ISDIR(st.st_mode))
    {
        errno = ENOTDIR;
        status = false;
    }

    return(status);
}

#include <sys/stat.h>

bool MakePath(const char *path)
{
    char           *pp;
    char           *sp;
    bool            status = true;
    char           *copypath = strdup(path);
    mode_t mode = (S_IRWXU|S_IRWXG);

    pp = copypath;
    while (status && (sp = strchr(pp, '/')) != 0)
    {
        if (sp != pp)
        {
            /* Neither root nor double slash in path */
            *sp = '\0';
            status = do_mkdir(copypath, mode);
            *sp = '/';
        }
        pp = sp + 1;
    }
    if (status)
        status = do_mkdir(path, mode);
    free(copypath);
    return (status);
}

/*
 * Copyright (C), 2009, ����� �������
 *
 * ��������� ������
 *
 * ert   02/05/2009   creating
 */
#include "stdafx.h"
#include <srvutility.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>


using namespace GRServer;
using namespace std;

const char COMMENT_SYM = ';';
const char START_SECTION_SYM = '[';
const char END_SECTION_SYM = ']';

const char* GRServer::WhiteSpaces = " \t\n\r\f\v";

IniReader::IniReader() : file(NULL)
{
}

IniReader::~IniReader()
{
   Close();
}

void IniReader::Close()
{
   if( file != NULL )
   {
      fclose(file);
      file = NULL;
   }
}

bool IniReader::Open(const char *fileName)
{
   file = fopen(fileName, "rt");
   return (file != NULL);
}

bool IniReader::ReadLine(string *line)
{
   line->clear();

   if( file == NULL || feof(file) ) return false;

   while( !feof(file) )
   {
      char buf[200];
      if( fgets(buf, sizeof(buf), file) == NULL )
         break;

      char *newLine = strchr(buf, '\n');
      if( newLine != NULL )
         *newLine = '\0';

      line->append(buf);

      if( newLine != NULL )
         break;
   }

   return true;
}

bool IniReader::ReadSection(const char *sectionName, IniReader::SectionList *items)
{
   items->clear();

   if( file == NULL || feof(file) ) return false;

   string line;
   bool modulesSection = false;
   string secName(sectionName);
   secName += END_SECTION_SYM;

   while( true )
   {
      if( !ReadLine(&line) )
         break;

      if( line[0] == COMMENT_SYM )
         continue;

      if( line[0] == START_SECTION_SYM )
      {
         modulesSection = (line.find(secName) == 1);
         continue;
      }

      if( !modulesSection )
         continue;

      if( !line.empty() )
         items->push_back(line);
   }

   return true;
}

#include "server.h"

//static std::string baseFolder;
//void GRServer::SetBaseFolder(const std::string& folder)
//{
//   baseFolder = folder;
//   if(*baseFolder.rbegin() != '/')
//      baseFolder.append(1, '/');
//}
//
//void GRServer::FullFileName(std::string *fullName, const char *fileName)
//{
//   while( *fileName == ' ' ) fileName++;
//
//#ifdef UNIX
//   if( !IsLocalName(fileName) )
//      fullName->assign(fileName);
//   else
//   {
//      fullName->assign(baseFolder).append(fileName);
//   }
//#else
//   USES_CONVERSION;
//
//   if( !IsLocalName(fileName) )
//	{
//      (*fullName) = fileName;
//	} else
//	{
//      wchar_t path[MAX_PATH];
//      GetModuleFileName(NULL, path, MAX_PATH);
//
//      wchar_t *p = wcsrchr(path, L'\\');
//      p[1] = L'\0';
//      fullName->assign( W2A_CP(path, CP_UTF8));
//      fullName->append(fileName);
//   }
//#endif
//}

void GRServer::DateTimeToString(std::string *str, const FILETIME &ft)
{
   char buf[50];
   SYSTEMTIME st;

   FileTimeToSystemTime(&ft, &st);
   wsprintfA(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);

   *str = buf;
}

void GRServer::StringToDateTime(FILETIME *ft, const std::string &str)
{
   SYSTEMTIME st;
   sscanf(str.c_str(), "%4d%2d%2d%2d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay,
          (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);

   SystemTimeToFileTime(&st, ft);
}

bool GRServer::IsFileExists(const std::string& fileName)
{
#ifdef UNIX
   struct stat sts;
   return (stat(fileName.c_str(), &sts)  == 0);
#else
   return (GetFileAttributesA(fileName.c_str()) != 0xFFFFFFFF);
#endif
}

void GRServer::GetServerVersion(std::wstring* version)
{
#ifdef UNIX
version->assign(L"");
#else
   wchar_t filebuf[MAX_PATH];
   GetModuleFileName(NULL, filebuf, sizeof(filebuf)/sizeof(filebuf[0]));

   DWORD handle = 0;
   DWORD size = GetFileVersionInfoSize(filebuf, &handle);
   if( size > 0 )
   {
      VOID *data = alloca(size);
      VS_FIXEDFILEINFO *vb;
      GetFileVersionInfo(filebuf, 0, size, data);
      if( VerQueryValue(data, L"\\",  (VOID**)&vb, (UINT*)&size) == TRUE )
      {
         wchar_t buf[50];
         wsprintf(buf, L"%d.%d.%d.%d", vb->dwProductVersionMS >> 16, vb->dwProductVersionMS & 0xFFFF,
            vb->dwProductVersionLS >> 16, vb->dwProductVersionLS & 0xFFFF);

         version->assign(buf);
      }
   }
#endif
}

//static char *RemoveSpaces(char *buf, char *ep)
//{
//   char *p = buf;
//   while( *p == ' ' && p < ep ) p++;
//
//   ep--;
//   while( ep >= p && *ep == ' ' ) ep--;
//   ep[1] = '\0';
//
//   return p;
//}

bool GRServer::ReadKeyValue(FILE* rd, std::string* key, std::string* value)
{
   std::string buf;
   do
   {
      if( !ReadLine(&buf, rd) )
         return false;
   } while( buf.size() > 0 && *buf.begin() == '#' );

   unsigned pos = (unsigned)buf.find('=');
   if( pos == std::string::npos )
      return false;

   key->assign(buf.substr(0, pos));
   value->assign(buf.substr(pos+1));

   Trim(key, *key);
   Trim(value, *value);
   return true;
}

static bool EatWhite(const wchar_t** sp)
{
   while( true )
   {
      wchar_t sym = *(*sp);
      if( sym == L'\0' )
         return false;
      if( !iswspace(sym) )
         return true;

      (*sp)++;
   }
}

static void ReadToken(std::wstring* token, const wchar_t** sp, bool upcase, const wchar_t *tsym)
{
   EatWhite(sp);
   token->clear();

   while( true )
   {
      wchar_t sym = *(*sp);
      if( sym == L'\0' )
         break;

      if( wcschr(tsym, sym) != NULL )
         break;

      token->append(1, (upcase) ? towupper(sym) : sym);
      (*sp)++;
   }
}

static bool ReadCode(std::wstring* code, const wchar_t** sp)
{
   bool ret = false;
   if( EatWhite(sp) && **sp == L'\'' )
   {
      (*sp)++;
      ReadToken(code, sp, false, L"'");
      if( **sp == L'\'' )
      {
         (*sp)++;
         ret = true;
      }
   }

   return ret;
}

static bool ReadUserCodes(std::vector<std::string>* users, const wchar_t** sp)
{
   bool ret = false;

   USES_CONVERSION;
   if( EatWhite(sp) && **sp == L'(' )
   {
      while( true )
      {
         std::wstring code;

         (*sp)++;
         if( ReadCode(&code, sp) )
         {
            users->push_back(W2A_CP(code.c_str(), CP_UTF8));
            if( EatWhite(sp) )
            {
               wchar_t sym = **sp;
               if( sym == L',' )
                  continue;

               if(sym == L')')
               {
                  (*sp)++;
                  ret = true;
               }
            }
         }
         break;
      }
   }
   return ret;
}

const char* GRServer::NULL_USER = "NULL_USER";
bool GRServer::ParseUserFilter(std::vector<std::string>* users, const wchar_t* sp)
{
   std::wstring val;
   ReadToken(&val, &sp, true, L"= \t\r\n");
   if( val.empty() )
      return false;
   if( *val.begin() == L'\"' && *val.rbegin() == L'\"' )
      val = val.substr(1, val.size()-2);
   if( val.compare(L"USERID") != 0 || !EatWhite(&sp))
      return false;

   bool ret = false;
   if( *sp == L'=' )
   {
      sp++;
      if( ReadCode(&val, &sp) )
      {
         USES_CONVERSION;
         users->push_back(W2A_CP(val.c_str(), CP_UTF8));
         ret = true;
      }
   } else
   {
      ReadToken(&val, &sp, true, L"( \t\r\n");
      if( val.compare(L"IS") == 0 )
      {
         ReadToken(&val, &sp, true, L" \t\r\n");
			if (val.compare(L"NULL") == 0) {
				users->push_back(NULL_USER);
				ret = true;
			}
      } else if( val.compare(L"IN") == 0 )
      {
         ret = ReadUserCodes(users, &sp);
      }
   }

   return ret;
}

bool GRServer::ReadLine(std::string* v, FILE *f)
{
   char buf[100];

   if( fgets(buf, sizeof(buf), f) == NULL ) return false;

   v->clear();

   do
   {
      char *p = strchr(buf, '\n');
      if( p != NULL )
      {
         *p = '\0';
         v->append(buf, p - buf);
         break;
      }

      v->append(buf, strlen(buf));
   } while( fgets(buf, sizeof(buf), f) != NULL );

   return true;
}

void GRServer::Trim(std::string* res, const std::string& _src, size_t offset, size_t size)
{
   const std::string& src = _src.substr(offset, size);

   size_t es = 0, ss = 0;
   std::string::const_reverse_iterator rb = src.rbegin();
   while( rb != src.rend() )
   {
      if( *rb != ' ' ) break;
      rb++;
      es++;
   }

   std::string::const_iterator b = src.begin();
   while( b != src.end() )
   {
      if( *b != ' ' ) break;
      b++;
      ss++;
   }

   res->assign(src.substr(ss, src.size() - es - ss));
}

static const wchar_t* ReadDigit(WORD* dest, const wchar_t* src, int len, const wchar_t* sep)
{
   wchar_t dig[5];
   int pos = 0;

   while( *src && (wcschr(sep, *src) == 0) && pos < len )
   {
      if( !iswdigit(*src) )
         return NULL;
      dig[pos++] = *src;
      src++;
   }
   dig[pos] = L'\0';

   *dest = _wtoi(dig);
   return src;
}

// ������� ���� ���� 20.04.2010 20-04-2010 10:20:15 20/04/2010 10:20:15
bool GRServer::StrToSystemTime(SYSTEMTIME *st, const wchar_t *src, const wchar_t** ep)
{
   src = ReadDigit(&st->wDay, src, 2, L"./-");
   if( src == NULL || st->wDay == 0 || st->wDay > 31 ) return false;

   src = ReadDigit(&st->wMonth, src+1, 2, L"./-");
	if (src == NULL || st->wMonth == 0 || st->wMonth > 12) return false;

   src = ReadDigit(&st->wYear, src+1, 4, L" '\"");
	if (src == NULL || st->wYear == 0) return false;

   if( *src == L' ' )
   {
      src = ReadDigit(&st->wHour, src+1, 2, L":");
		if (src == NULL || st->wHour >= 24) return false;

      src = ReadDigit(&st->wMinute, src+1, 2, L":");
		if (src == NULL || st->wMinute >= 60) return false;

		if (*src) 
		{
			src = ReadDigit(&st->wSecond, src + 1, 2, L"'\"");
			if (st->wSecond >= 60) return false;
		}
		else
			st->wSecond = 0;
   }

   if( ep != NULL && src != NULL )
      *ep = src;
   return true;
}

#ifdef UNIX
#include <uuid/uuid.h>

size_t GRServer::w16len(const wchar_t* src)
{
   size_t len = 0;
   unsigned short *usrc = (unsigned short*)src;
   while( *usrc != 0)
   {
      usrc++;
      len++;
   }
   return len;
}

inline wchar_t to_hex(char val) 
{
   return val < 10 ? L'0' + val : L'A' + val - 10;
}

void GRServer::GenerateID(std::wstring* out)
{
   uuid_t uid;
   uuid_generate_random(uid);   

   const char *ep = (const char*)uid + sizeof(uid);
   for (const char* el = (const char*)uid; el < ep; el++)
   {
      out->append(1, to_hex((*el & 0xF)));
      out->append(1, to_hex((*el & 0xF0) >> 4));
   }
}
#else
void GRServer::GenerateID(std::wstring* out)
{
   RPC_WSTR str;
   UUID uid;

   UuidCreateSequential(&uid);
   UuidToStringW(&uid, &str);

   for (unsigned short* el = str; *el; el++)
   {
      if (iswalnum(*el))
         out->append(1, (wchar_t)*el);
   }

   RpcStringFreeW(&str);
}
#endif

//int GRServer::GenerateDump(EXCEPTION_POINTERS* pExceptionPointers)
//{
//	BOOL bMiniDumpSuccessful;
//	WCHAR szPath[MAX_PATH]; 
//	WCHAR szFileName[MAX_PATH]; 
//	WCHAR* szAppName = L"AppName";
//	WCHAR* szVersion = L"v1.0";
//	DWORD dwBufferSize = MAX_PATH;
//	HANDLE hDumpFile;
//	SYSTEMTIME stLocalTime;
//	MINIDUMP_EXCEPTION_INFORMATION ExpParam;
//
//	GetLocalTime( &stLocalTime );
//	GetTempPath( dwBufferSize, szPath );
//
//	StringCchPrintf( szFileName, MAX_PATH, L"%s%s", szPath, szAppName );
//	CreateDirectory( szFileName, NULL );
//
//	StringCchPrintf( szFileName, MAX_PATH, L"%s%s\\%s-%04d%02d%02d-%02d%02d%02d-%ld-%ld.dmp", 
//		szPath, szAppName, szVersion, 
//		stLocalTime.wYear, stLocalTime.wMonth, stLocalTime.wDay, 
//		stLocalTime.wHour, stLocalTime.wMinute, stLocalTime.wSecond, 
//		GetCurrentProcessId(), GetCurrentThreadId());
//	hDumpFile = CreateFile(szFileName, GENERIC_READ|GENERIC_WRITE, 
//		FILE_SHARE_WRITE|FILE_SHARE_READ, 0, CREATE_ALWAYS, 0, 0);
//
//	ExpParam.ThreadId = GetCurrentThreadId();
//	ExpParam.ExceptionPointers = pExceptionPointers;
//	ExpParam.ClientPointers = TRUE;
//
//	bMiniDumpSuccessful = MiniDumpWriteDump(GetCurrentProcess(), GetCurrentProcessId(), 
//		hDumpFile, MiniDumpWithDataSegs, &ExpParam, NULL, NULL);
//
//	return EXCEPTION_EXECUTE_HANDLER;
//}

#ifdef UNIX
#include <dirent.h>

const char* GRServer::GetFolderFileName(std::string* fileName)
{
   char name[PATH_MAX];
   char dir[PATH_MAX];
   size_t off = fileName->find_last_of('/');
   if( off != std::string::npos )
   {
      strcpy(name, fileName->substr(off+1).c_str());
      strcpy(dir, fileName->substr(0,off).c_str());
   }
   else
   {
      strcpy(name, fileName->c_str());
      getcwd(dir, sizeof(dir));
   }

   DIR *d = opendir(dir);
   if( d != NULL )
   {
      dirent *dir;
      while((dir = readdir(d)) != NULL)
      {
         if(strcasecmp(name, dir->d_name) == 0)
         {
            fileName->assign(fileName->substr(0, off+1));
            fileName->append(dir->d_name);
            break;
         }
      }
      closedir(d);
   }
   return fileName->c_str();
}
#endif

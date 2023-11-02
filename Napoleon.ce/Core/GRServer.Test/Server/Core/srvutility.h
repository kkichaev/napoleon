/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Служебные классы
 *
 * ert   02/05/2009   creating
 */
#ifndef __GRSERVER_UTILITY_H
#define __GRSERVER_UTILITY_H

#include <vector>
#include <string>

namespace GRServer {

class CString;

class IniReader
{
public:
   typedef std::vector<std::string> SectionList;

   IniReader();
   ~IniReader();

   bool Open(const char *fileName);

   bool ReadSection(const char *sectionName, SectionList *items);

   void Close();

protected:
   bool ReadLine(std::string *line);

   FILE *file;
};

void FullFileName(std::string *fullName, const char *fileName);

inline bool IsLocalName(const char *fileName)
{
#ifdef UNIX
   return !( *fileName != '\0' && (*fileName == '/' || *fileName == '~') );
#else
   return !( *fileName != '\0' && fileName[1] != '\0' && ((*fileName == '\\' && fileName[1] == '\\') || fileName[1] == ':') );
#endif
}

void DateTimeToString(std::string *str, const FILETIME &ft);
void StringToDateTime(FILETIME *ft, const std::string &str);

bool IsFileExists(const std::string& fileName);

void GetServerVersion(std::wstring *version);

void GetLogFileName(CString *fileName); // log.cpp

bool ReadKeyValue(FILE* file, std::string* key, std::string* value);

extern const char* NULL_USER;

bool ParseUserFilter(std::vector<std::string>* users, const wchar_t* sp);

void JPEGAddComment(const char* fileName, const char* comment);

bool ReadLine(std::string* v, FILE *f);

void Trim(std::string* res, const std::string& _src, size_t offset = 0, size_t size = -1);

bool StrToSystemTime(SYSTEMTIME *st, const wchar_t *src, const wchar_t** ep);

//int GenerateDump(EXCEPTION_POINTERS* pExceptionPointers);

#ifdef UNIX
const char* GetFolderFileName(std::string* fileName);
#endif

#ifdef UNIX

size_t w16len(const wchar_t* src)
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

#define USES_WCONVERSION USES_CONVERSION

#define W16_32(lpw) \
( ((_lpw = lpw) == NULL) ? NULL : \
  (_srcb = w16len(_lpw) + 1, _destb = _srcb * sizeof(wchar_t), \
   (const wchar_t*)ConvHelper((const char*)_lpw, (char*)alloca(_destb), _srcb * sizeof(unsigned short), _destb, "UTF16", "UTF32")) \
)

#define W32_16(lpw) \
( ((_lpw = lpw) == NULL) ? NULL : \
  (_srcb = wcslen(_lpw) + 1, _destb = _srcb * sizeof(unsigned short), \
   (const wchar_t*)ConvHelper((const char*)_lpw, (char*)alloca(_destb), _srcb * sizeof(wchar_t), _destb, "UTF32", "UTF16")) \
)

#else

#define W16_32(lpw) lpw
#define W32_16(lpw) lpw
#define USES_WCONVERSION

#endif

}; // namespace GRServer

#endif

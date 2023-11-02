/*
 * Copyright (C), 2009, ����� �������
 *
 * ��������� ������
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

// return UTF8 file name
//void SetBaseFolder(const std::string& folder);
//void FullFileName(std::string *fullName, const char *fileName);

void DateTimeToString(std::string *str, const FILETIME &ft);
void StringToDateTime(FILETIME *ft, const std::string &str);

bool IsFileExists(const std::string& fileName);

void GetServerVersion(std::wstring *version);

void GetLogFileName(CString *fileName); // log.cpp

bool ReadKeyValue(FILE* file, std::string* key, std::string* value);

extern const char* NULL_USER;

bool ParseUserFilter(std::vector<std::string>* users, const wchar_t* sp);

bool ReadLine(std::string* v, FILE *f);

void Trim(std::string* res, const std::string& _src, size_t offset = 0, size_t size = -1);

bool StrToSystemTime(SYSTEMTIME *st, const wchar_t *src, const wchar_t** ep);

inline std::string& to_upper(std::string& dest, const std::string& str)
{
   dest.clear();
   for (std::string::const_iterator i = str.begin(); i != str.end(); i++)
      dest.append(1, ::toupper(*i));
   return dest;
}

inline std::wstring& to_upper(std::wstring& dest, const std::wstring& str)
{
   dest.clear();
   for (std::wstring::const_iterator i = str.begin(); i != str.end(); i++)
      dest.append(1, ::toupper(*i));
   return dest;
}

std::string base64_encode(std::string const& s, bool url = false);
std::string base64_encode_pem(std::string const& s);
std::string base64_encode_mime(std::string const& s);

std::string base64_decode(std::string const& s, bool remove_linebreaks = false);
std::string base64_encode(unsigned char const*, size_t len, bool url = false);

extern const char* WhiteSpaces;

inline std::string& rtrim(std::string& s, const char* t = WhiteSpaces)
{
   return s.erase(s.find_last_not_of(t) + 1);
}

// trim from beginning of string (left)
inline std::string& ltrim(std::string& s, const char* t = WhiteSpaces)
{
   return s.erase(0, s.find_first_not_of(t));
}

// trim from both ends of string (right then left)
inline std::string& trim(std::string& s, const char* t = WhiteSpaces)
{
   return ltrim(rtrim(s, t), t);
}

template<typename String>
bool is_same_text(const String& src, const String& text)
{
   std::string::const_iterator si = src.begin();
   std::string::const_iterator ti = text.begin();

   while (si != src.end() && ti != text.end())
   {
      if (::toupper(*si++) != ::toupper(*ti++))
         return false;
   }

   return ti == text.end();
}

void GenerateID(std::wstring* out);

#ifdef UNIX
const char* GetFolderFileName(std::string* fileName);
#endif

#ifdef UNIX

size_t w16len(const wchar_t* src);

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

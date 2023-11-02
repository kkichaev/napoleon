#ifndef __PROGRAM_H
#define __PROGRAM_H

struct Binary
{
   void *data;
   DWORD length;

   Binary() : data(NULL), length(0) {}
   ~Binary() { delete data; }
};

bool LoadPrice(std::vector<std::wstring>* sharedStrings, Binary* priceSheet, _TCHAR* fileName, _TCHAR* sheetName);

bool Do(const Binary& priceSheet, const wchar_t* baseFolder, const std::vector<std::wstring>& sharedStrings);
void Utf8ToUtf16(std::wstring *dest, const char *str, int len);
inline void Utf8ToUtf16(std::wstring *dest, const char *str) { Utf8ToUtf16(dest, str, strlen(str)); }

#include <map>
struct Attributes : public std::map<std::wstring, std::wstring>
{
   Attributes(const char **atts)
   {
      if( atts != NULL )
      {
         for( int i=0; atts[i]; i++ )
         {
            std::wstring name, value;
            Utf8ToUtf16(&name, atts[i++]);
            Utf8ToUtf16(&value, atts[i]);

            insert(value_type(name, value));
         }
      }
   }

   bool Find(std::wstring* value, const std::wstring& name) const
   {
      const_iterator fnd = find(name);

      if( fnd != end() )
      {
         *value = fnd->second;
         return true;
      }

      return false;
   }
};
#endif

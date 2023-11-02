#ifndef __DATE_PARSER_H
#define __DATE_PARSER_H

#include <string>
#include <vector>

class DateParser
{
public:
   DateParser();
   ~DateParser();

  /*
   * Формат UnixTime или DMYH(h)mS
   */
   bool SetFormat(const wchar_t* format);

   bool FromString(SYSTEMTIME* dest, const wchar_t* src) const;
   bool FromString(FILETIME* dest, const wchar_t* src) const
   {
      SYSTEMTIME st;
      if( !FromString(&st, src) )
         return false;
      SystemTimeToFileTime(&st, dest);
      return true;
   }

   bool ToString(std::wstring* dest, const SYSTEMTIME& src) const;
   bool ToString(std::wstring* dest, const FILETIME& src) const
   {
      SYSTEMTIME st;
      FileTimeToSystemTime(&src, &st);
      return ToString(dest, st);
   }

public:
   struct Item
   {
      static Item* Create(const std::wstring& src);

      virtual ~Item() {}

      virtual bool Init(const std::wstring& src) = 0;
      virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const = 0;
      virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const = 0;
   };

private:
   typedef std::vector<Item*> ItemList;
   ItemList items;
};

#endif
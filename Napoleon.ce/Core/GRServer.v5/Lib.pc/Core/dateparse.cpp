/*
* Copyright (C), 2009-2013, Денис Мосягин
*
* Date parser
*
* ert   21/06/2013   creating
*/
#include "stdafx.h"
#include <dateparse.h>
#include <pstream.h>

#include <atlconv.h>

using namespace GRServer;
//using namespace std;

static const wchar_t ItemTokens[] = L"DMYHhmS";
static const wchar_t UnixToken[] = L"UnixTime";
static const wchar_t UnixTokenMS[] = L"UnixTimeMS";

DateParser::DateParser()
{
}

DateParser::~DateParser()
{
   ItemList::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      delete (*i);
}

static bool CopySymbols(wchar_t* dest, const wchar_t* str, int len)
{
   while( len-- > 0 )
   {
      wchar_t sym = *str;
      if( !iswdigit(sym) )
         return false;
      *dest++ = *str++;
   }

   *dest = '\0';
   return true;
}

class YearItem : public DateParser::Item
{
public:
   enum Format { None, Short, Long };
   YearItem() : format(None) {}

   virtual bool Init(const std::wstring& src)
   {
      size_t len = src.size(); 
      format = (len == 2) ? Short : (len == 4) ? Long : None; 
      return (format != None);
   }

   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const
   {
      wchar_t buf[10];
      wsprintfW(buf, L"%d", (format == Short) ? src.wYear % 100 : src.wYear);
      dest->append(buf);
      return true;
   }

   virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const
   {
      wchar_t buf[10];
      int len = (format == Short) ? 2 : 4;
      if( !CopySymbols(buf, src, len) )
         return false;

      *ep = src + len;
      dest->wYear = _wtoi(buf);
      if( format == Short )
         dest->wYear += 2000;
      return true;
   }

protected:
   Format format;
};

class UnixTime : public DateParser::Item
{
	bool useMS;
public:
	UnixTime(bool useMS)
	{
		this->useMS = useMS;
	}

   virtual bool Init(const std::wstring& src) { return true; }
   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const
   {
      FILETIME ft;
      SystemTimeToFileTime(&src, &ft);

		long coef = 10000000;
		__int64 coef2 = 11644473600;
		if( useMS )
		{
			coef = 10000;
			coef2 = 11644473600000;
		}
      double val = (double)((*(__int64*)&ft) / coef - coef2);
      char buf[100];
      sprintf(buf, "%f", val);
		USES_CONVERSION;
      dest->append(A2W(buf));
      return true;
   }

   virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const
   {
      __int64 val = wcstoul(src, (wchar_t**)ep, 10);
		long coef = 10000000;
		__int64 coef2 = 11644473600;
		if( useMS )
		{
			coef = 10000;
			coef2 = 11644473600000;
		}
      val = (val + coef2) * coef;
      FileTimeToSystemTime((FILETIME*)&val, dest);
      return true;
   }
};

class MonthItem : public DateParser::Item
{
protected:
   MonthItem(WORD SYSTEMTIME::*sm) : format(None), member(sm) {}

public:
   enum Format { None, Short, Long };
   MonthItem() : format(None) { member = &SYSTEMTIME::wMonth; }

   virtual bool Init(const std::wstring& src)
   {
      size_t len = src.size(); 
      format = (len == 1) ? Short : (len == 2) ? Long : None; 
      return (format != None);
   }

   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const
   {
      wchar_t buf[10];
      wsprintfW(buf, (format == Short) ? L"%d" : L"%02d", (int)(src.*member));
      dest->append(buf);
      return true;
   }

   virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const
   {
      wchar_t buf[4];
      int len=0;
      while( iswdigit(*src) && len < 2 )
         buf[len++] = *src++;

      //if( len == 0 || (format == Long && len != 2) )
      if( len == 0 )
         return false;

      buf[len] = '\0';
      *ep = src;
      dest->*member = _wtoi(buf);
      return true;
   }

protected:
   Format format;
   WORD SYSTEMTIME::*member;
};

class DayItem : public MonthItem
{
public:
   DayItem() : MonthItem(&SYSTEMTIME::wDay) {}
};

class Hour24Item : public MonthItem
{
public:
   Hour24Item() : MonthItem(&SYSTEMTIME::wHour) {}
};

class Hour12Item : public MonthItem
{
public:
   Hour12Item() : MonthItem(&SYSTEMTIME::wHour) {}

   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const
   {
      wchar_t buf[10];
      int value = src.wHour;
      if( value == 0) value = 12;
      else if(value > 12) value -= 12;
      wsprintfW(buf, (format == Short) ? L"%d" : L"%02d", value);
      dest->append(buf);
      return true;
   }
};

class MinuteItem : public MonthItem
{
public:
   MinuteItem() : MonthItem(&SYSTEMTIME::wMinute) {}
};

class SecondItem : public MonthItem
{
public:
   SecondItem() : MonthItem(&SYSTEMTIME::wSecond) {}
};

class HourIndicator : public DateParser::Item
{
public:
   virtual bool Init(const std::wstring& src) { return true; }

   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const
   {
      dest->append( (src.wHour > 12) ? L"PM" : L"AM" );
      return true;
   }

   virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const
   {
      if( src[1] != L'M' )
         return false;
      if( *src == L'P' )
      {
         if( dest->wHour < 12 )
            dest->wHour += 12;
      } else  if( *src == L'M' )
      {
         if( dest->wHour > 12 )
            dest->wHour -= 12;
      } else
         return false;

      *ep = src + 2;
      return true;
   }
};

class TextItem : public DateParser::Item
{
public:
   virtual bool Init(const std::wstring& src) { text = src; return true; }
   
   virtual bool ToString(std::wstring* dest, const SYSTEMTIME& src) const { dest->append(text); return true; }
   
   virtual bool FromString(SYSTEMTIME* dest, const wchar_t* src, const wchar_t** ep) const
   {
      if( wcsncmp(src, text.c_str(), text.size()) != 0 )
         return false;

      *ep = src + text.size();
      return true;
   }

protected:
   std::wstring text;
};

DateParser::Item* DateParser::Item::Create(const std::wstring& str)
{
   Item* ret = NULL;
   if( str.size() == 0 )
      return ret;

   switch(*str.begin())
   {
   case L'D':
      ret = new DayItem();
      break;
   case L'M':
      ret = new MonthItem();
      break;
   case L'Y':
      ret = new YearItem();
      break;
   case L'H':
      ret = new Hour24Item();
      break;
   case L'h':
      ret = new Hour12Item();
      break;
   case L'm':
      ret = new MinuteItem();
      break;
   case L'S':
      ret = new SecondItem();
      break;
   default:
      ret = new TextItem();
      break;
   }
   if( ret->Init(str) == false )
   {
      delete ret;
      ret = NULL;
   }
   return ret;
}

bool DateParser::SetFormat(const wchar_t* format)
{
   if( wcscmp(format, UnixToken) == 0 )
   {
      items.push_back(new UnixTime(false));
      return true;
   }

	if( wcscmp(format, UnixTokenMS) == 0 )
   {
      items.push_back(new UnixTime(true));
      return true;
   }

   ParseStreamW ps(format, format + wcslen(format));

   HourIndicator* lastItem = NULL;

   std::wstring str;
   wchar_t curSym = ps.Current();
   for( str.append(1, curSym); ps.MoveNext(); curSym = ps.Current(), str.append(1, curSym) )
   {
      if( ps.Current() == curSym )
         continue;

      if( !wcschr(ItemTokens, curSym) && !wcschr(ItemTokens, ps.Current()) )
         continue;

      Item* item = Item::Create(str);
      if( item == NULL )
         break;

      if( curSym == L'h' && lastItem == NULL )
         lastItem = new HourIndicator();

      items.push_back(item);
      str.clear();
   }

   if( !ps.EOS() )
   return false;

   if( str.empty() == false )
   {
      Item* item = Item::Create(str);
      if( item == NULL )
         return false;
      items.push_back(item);
   }

   if( lastItem != NULL )
      items.push_back(lastItem);
   return true;
}

bool DateParser::FromString(SYSTEMTIME* dest, const wchar_t* src) const
{
   bool ret = true;
   memset(dest, 0, sizeof(*dest));
   ItemList::const_iterator i = items.begin();
   for( ; ret && i != items.end(); i++ )
      ret = (*i)->FromString(dest, src, &src);
   
   return ret;
}

bool DateParser::ToString(std::wstring* dest, const SYSTEMTIME& src) const
{
   bool ret = true;
   ItemList::const_iterator i = items.begin();
   for( ; ret && i != items.end(); i++ )
      ret = (*i)->ToString(dest, src);
   
   return ret;
}

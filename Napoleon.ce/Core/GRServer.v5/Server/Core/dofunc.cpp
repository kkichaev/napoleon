/*
 * Copyright (C), 2009-2022, Denis Mosiagin
 *
 * DoFunction
 *
 * ert   24/05/2010   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "event.h"
#include "objdef.h"
#include "sessobj.h"
#include "session.h"
#include "parse.h"
#include "folderset.h"
#include "server.h"
#include "srvutility.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <dateparse.h>
//#include "image_convert.h"

using namespace GRServer;

static const char jpegSignature[] = "\xFF\xD8\xFF";
static const char pngSignature[] = "\x89\x50\x4E\x47\x0D\x0A\x1A\x0A";

static bool IsJPEG(const BYTE* pb)
{
   return (memcmp(pb, jpegSignature, sizeof(jpegSignature) - 1) == 0);
}

static bool IsPNG(const BYTE* pb)
{
   return (memcmp(pb, pngSignature, sizeof(pngSignature) - 1) == 0);
}

class FuncParamResolver : public ParamResolver
{
public:
   FuncParamResolver(std::vector<Token>* params, Session* session, const SessionObject* object) :
      ParamResolver(params, session, object) {}

   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym)
   {
      if( endSym == L')' )
      {
         if( result.type != Token::ttNone )
            params->push_back(result);
         return true;
      }

      return ParamResolver::EndStatement(result, stream, endSym);
   }
};

struct CmpFunc
{
   bool operator()(IFunction * const & _l, IFunction * const& _r) const
   {
      return (wcscmp(_l->Name(), _r->Name()) < 0);
   }
};

typedef std::set<IFunction*, CmpFunc> FuncSet;
static FuncSet functionHolder;

//
// ���������� ��� ����, � � ���������� ��� ������. �������� - ������ ����/�������
//
struct Now : public IFunction
{
   virtual const wchar_t* Name() const { return L"Now"; }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
      SYSTEMTIME st;
      FILETIME ft;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &ft);

      bool parsed = false;
      if( params.size() > 0 )
      {
         const Token &tt = params.at(0);
         if( tt.type == Token::ttString )
         {
            std::wstring resStr;
            DateParser parser;

            parsed = (parser.SetFormat(tt.value.str->c_str()) && parser.ToString(&resStr, ft));
            if( parsed )
               (*result) = resStr;
         }
      }
      if( !parsed )
         (*result) = ft;

      return true;
   }
} nowFunc;

//
// ���������� ��� ����, � � ���������� ��� ������. �������� - ������ ����/�������
//
struct ServerTZ : public IFunction
{
   virtual const wchar_t* Name() const { return L"ServerTimeZone"; }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
#ifdef UNIX
      tzset();
		(*result) = timezone / 60;
#else
      TIME_ZONE_INFORMATION st;
      GetTimeZoneInformation(&st);

		(*result) = st.Bias;
#endif
      return true;
   }
} serverTZ;

//
// ��������� ������, ������
//  ������ ���� ������ ���� D[D] M[M] YY[YY] h[h] H[H] m[m] S[S]
//
struct FormatDate : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"FormatDate"; }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
      if( params.size() < 1 )
         return false;

      bool res = false;
      std::wstring resStr;
      const Token& t = params.at(0);
      if( t.type != Token::ttDateTime )
      {
         res = t.ToString(&resStr);
      } else
      {      
         bool setted = false;
         DateParser parser;
         if( params.size() > 1 )
         {
            const Token &tt = params.at(1);
            if( tt.type == Token::ttString )
               setted = parser.SetFormat(tt.value.str->c_str());
         }
         if( !setted )
            parser.SetFormat(L"DD/MM/YYYY HH:mm:SS");

         res = parser.ToString(&resStr, t.value.datetime);
      }

      if( res )
         *result = resStr;
      return res;
   }
} formatDateStrFunc;

//
// ��������� data, value, adjType
//   adjType = year, month, day, hour, minute, second
//
static int daysInMonths[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
class AdjustDate : public IFunction
{
public:
   AdjustDate() {}

   virtual const wchar_t* Name() const { return L"AdjustDate"; }

	bool IsLeapYear(int year)
	{
		if (year % 4 != 0) return false;
		if (year % 400 == 0) return true;
		if (year % 100 == 0) return false;
		return true;
	}


	int GetDaysInMonth(int year, int month)
	{
		int days = daysInMonths[month - 1];

		if (month == 2 && IsLeapYear(year)) // February of a leap year
			days += 1;

		return days;
	}


   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
      FILETIME ft;
      int value;

      bool res = false;
      *result = L"";

      int cnt = 0;
      if( params.size() == 3 )
      {
         if( params[0].type == Token::ttDateTime )
         {
            cnt++;
            ft = params[0].value.datetime;
         }
         if( params[1].type == Token::ttNumber )
         {
            cnt++;
            value = (int)params[1].value.number;
         }

         if( cnt == 2 && params[2].type == Token::ttString )
         {
            res = true;

            SYSTEMTIME st;
            const wchar_t* adjType = params[2].value.str->c_str();
            if( _wcsicmp(adjType, L"year") == 0 )
            {
               FileTimeToSystemTime(&ft, &st);
               st.wYear += value;
               SystemTimeToFileTime(&st, &ft);
            }
            else if( _wcsicmp(adjType, L"month") == 0 )
            {
               FileTimeToSystemTime(&ft, &st);
               int newMonth = (st.wMonth + value - 1); // �������� ������ � 0
               int addYear = 0;

					if (newMonth >= 12)
					{
						newMonth %= 12;
						addYear = newMonth / 12 + 1;
					}
					else if (newMonth < 0)
					{
						newMonth = 12 - (abs(newMonth) % 12);
						addYear = newMonth / 12 - 1;
					}
               newMonth++;

					int lastDay = GetDaysInMonth(st.wYear + addYear, newMonth);
					if (st.wDay > lastDay)
						st.wDay = lastDay;

               st.wMonth = newMonth;
               st.wYear += addYear;
               SystemTimeToFileTime(&st, &ft);
            }
            else if( _wcsicmp(adjType, L"day") == 0 )
            {
               __int64 adj = (__int64)value * (__int64)10000000 * 3600 * 24;
               *(__int64*)&ft += adj;
            }
            else if( _wcsicmp(adjType, L"hour") == 0 )
            {
               __int64 adj = (__int64)value * (__int64)10000000 * 3600;
               *(__int64*)&ft += adj;
            }
            else if( _wcsicmp(adjType, L"minute") == 0 )
            {
               __int64 adj = (__int64)value * (__int64)10000000 * 60;
               *(__int64*)&ft += adj;
            }
            else if( _wcsicmp(adjType, L"second") == 0 )
            {
               __int64 adj = (__int64)value * (__int64)10000000;
               *(__int64*)&ft += adj;
				}
				else if (_wcsicmp(adjType, L"startDay") == 0)
				{
					FileTimeToSystemTime(&ft, &st);
					st.wDay = 1;
					st.wHour = 0;
					st.wMinute = 0;
					st.wSecond = 0;
					st.wMilliseconds = 0;
					SystemTimeToFileTime(&st, &ft);
				}
				else if (_wcsicmp(adjType, L"lastDay") == 0)
				{
					FileTimeToSystemTime(&ft, &st);
					int isLeapYear = (st.wYear % 4) || ((st.wYear % 100 == 0) && (st.wYear % 400)) ? 0 : 1;
					st.wDay = (st.wMonth == 2) ? (28 + isLeapYear) : 31 - (st.wMonth - 1) % 7 % 2;
					SystemTimeToFileTime(&st, &ft);
				}
				else
               res = false;

            if( res )
               *result = ft;
         }
      }
      return res;
   }
} adjDate;

class ToDateFunction : public IFunction
{
public:
	ToDateFunction() {}

	virtual const wchar_t* Name() const { return L"ToDate"; }
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
	{
		bool res = false;
		if (params.size() == 1 && params[0].type == Token::ttString)
		{
			SYSTEMTIME st = { 0 };
			const wchar_t *ep;
			res = StrToSystemTime(&st, params[0].value.str->c_str(), &ep);
			if (res)
			{
				__int64 val;
				SystemTimeToFileTime(&st, (FILETIME*)&val);
            // move to local time
            tzset();
            val -= (__int64)timezone * 10000000;
            
				(*result) = (double)val;
			}
		}
		return res;
	}

} toDateFoo;

class FirstItem : public IFunction
{
public:
   FirstItem() {}

   virtual const wchar_t* Name() const { return L"FirstItem"; }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
      *result = L"";

      if( params.size() == 1 && params[0].type == Token::ttServObject )
      {
         const SessionObject *so = params[0].value.object;
			if( so->CurObjectIndex() == 0 ) // ���� ��� ������� ������ 1
            *result = L"1";
      }
      return true;
   }
} firstItem;

class IndexOf : public IFunction
{
public:
	IndexOf() {}

	virtual const wchar_t* Name() const { return L"IndexOf"; }

	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
	{
		*result = L"0";

		if (params.size() == 1 && params[0].type == Token::ttServObject)
		{
			wchar_t buf[100];
			const SessionObject *so = params[0].value.object;
			wsprintf(buf, L"%d", so->CurObjectIndex() + 1);
			*result = buf;
		}
		return true;
	}
} indexOf;

struct FFinder :  public IFunction
{
public:
   FFinder(const wchar_t* _name) : name(_name) {}

   virtual const wchar_t* Name() const { return name; }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object) { return false; }

protected:
   const wchar_t* name;
};

class KVReader : public IFunction, public Session::IHandler
{
public:
   KVReader();

   virtual const wchar_t* Name() const { return L"KeyValueReader"; }
   virtual void SessionClosed(ISession* s) { files.clear(); }

   // params: File,Key,ValueIndex
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

protected:
	class ValueList : public std::vector<std::wstring> {};
   typedef std::map<std::wstring, ValueList> Values;

   const Values& Load(const std::wstring& fileName, const std::string& baseFolder, Session* session);
   bool GetParams(const std::vector<Token>& params, std::wstring* file, std::wstring* key, int* index);

protected:
   std::map<std::wstring, Values> files;
} kvReader;

class DecodeFlag : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"DecodeFlag"; }
   // flags, item, if0, if1
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

protected:
   bool ReadParams(const std::vector<Token>& params, DWORD *flags, DWORD *item, std::wstring *if0, std::wstring *if1);

} decodeFlag;

class EncodeFlag : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"EncodeFlag"; }
   // flags, item, if0, if1
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

} encodeFlag;

class EncodeFlagValue : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"EncodeFlagValue"; }
   // flags, item, if0, if1
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

} encodeFlagValue;

class MaskShift : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"MaskShift"; }
   // value, mask shift
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
} maskShift;

class GetColor : public IFunction, public Session::IHandler
{
public:
   virtual const wchar_t* Name() const { return L"GetColor"; }
   virtual void SessionClosed(ISession* s);

// params id, type ("org", "price"), colorType ("face", "back"), defaultColor
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

	GetColor();

protected:
   struct ColorData
   {
      DWORD face;
      DWORD back;
   };
	class ColorMap : public std::map<std::wstring, ColorData> {};
	class TypeColorMap : public std::map<WORD, ColorMap>{};
   typedef std::map<ISession*, TypeColorMap> DataMap;

   DataMap data;
	Mutex mutex;

   struct Param
   {
      std::wstring id;
      WORD type;
      bool face;
      DWORD defaultColor;
      Session* session;

      bool Read(const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
   };

   bool ReadParams(Param* param, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
   bool FindData(Token* result, const Param& param);
   bool LoadData(Token* result, const Param& param);
} getColor;

class ConfigReader : public IFunction
{
public:
	ConfigReader() : keyIndex(-1), valueIndex(-1) {}

   virtual const wchar_t* Name() const { return L"ConfigReader"; }

   // params: File,Key,ValueIndex
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

protected:
   bool GetParams(const std::vector<Token>& params, std::wstring* key, int* index);

	int keyIndex, valueIndex;
} cfgReader;

class ToFileName : public IFunction
{
public:
	ToFileName() {}

	virtual const wchar_t* Name() const { return L"ToFileName"; }

	// params: stringName
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);

} toFileName;

//#define FREEIMAGE_LIB
//#include "FreeImage.h"

//class ResizePhoto : public IFunction
//{
//public:
//	ResizePhoto() 
//	{
//		//FreeImage_Initialise();
//	}
//
//	virtual const wchar_t* Name() const { return L"ResizePhoto"; }
//
//	// params: stringName
//	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
//} resizePhoto;

class DataExtention : public IFunction
{
	virtual const wchar_t* Name() const { return L"DataExtention"; }

	// params: stringName
	virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject);
} dataExt;

class RandomFileName: public IFunction
{
   virtual const wchar_t* Name() const { return L"RandomFileName"; }

   // params: stringName
   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* thisObject);
} rndFileName;

//
// UserDataFilter(fieldName, dataType, userid[, prefix])
//
struct AgentDataFilter : public IFunction
{
public:
   virtual const wchar_t* Name() const { return L"AgentDataFilter"; }

   static bool ReadStringParam(std::wstring* out, const std::vector<Token>& params, size_t index)
   {
      if (params.size() <= index)
         return false;
      const Token& src = params[index];
      return src.ToString(out);
   }

   virtual bool Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* object)
   {
      std::wstring fieldName, dataCategory, userid, prefix;
      size_t index = 0;
      if (!ReadStringParam(&fieldName, params, index++))
         return false;
      if (!ReadStringParam(&dataCategory, params, index++))
         return false;
      if (!ReadStringParam(&userid, params, index++))
         return false;
      ReadStringParam(&prefix, params, index++);

      std::wstring filter;
      filter.append(L"\"type\"='").append(dataCategory).append(L"' and \"userid\"='").append(userid).append(L"'");

      std::wstring out;
      ISessionObject* src = session->LoadObject(L"AgentData", NULL, filter.c_str());

      if (!prefix.empty())
         out.append(L" ").append(prefix).append(L" ");

      if (src != NULL && src->Self()->size() > 0)
      {
         // produce
         // and ("idItem" in (select "id" from "AgentData" where "userid"='code' and "type"='Price'))

         out.append(L"(\"").append(fieldName).append(L"\" in (select \"id\" from \"AgentData\" where \"userid\"='")
            .append(userid).append(L"' and \"type\"='").append(dataCategory).append(L"'))");
      }
      else 
      {
         out.append(L"(1 = 1)");
      }

      (*result) = out;
      return true;
   }
} userDataFunc;


void GRServer::CloseFunctions()
{
   FuncSet::iterator i = functionHolder.begin();
   for( ; i != functionHolder.end(); i++ )
      (*i)->Close();
}

bool GRServer::AddFunction(IFunction* f)
{
   std::pair<FuncSet::iterator, bool> res = functionHolder.insert(f);
   return res.second;
}

static bool DoFunc(Token* result, const std::vector<Token>& params, const std::wstring& name, Session* session, const SessionObject* object)
{
   bool ret = false;
   FFinder ffinder(name.c_str());
   FuncSet::iterator i = functionHolder.find(&ffinder);
   if( i != functionHolder.end() )
      ret = (*i)->Do(result, params, session, object);
   return ret;
}

//
//------------------------------------------ DecodeFlag ----------------------------------------------------
//
bool DecodeFlag::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   DWORD flags, item;
   std::wstring if0, if1;

   *result = L"";
   if( ReadParams(params, &flags, &item, &if0, &if1) )
   {
      if( (flags & item) == 0 ) *result = if0;
      else *result = if1;
   }
   return true;
}

bool DecodeFlag::ReadParams(const std::vector<Token>& params, DWORD *flags, DWORD *item, std::wstring *if0, std::wstring *if1)
{
   int ctr = 0;
   if( params.size() == 4 )
   {
      if( params[0].type == Token::ttNumber )
      {
         *flags = (DWORD)params[0].value.number;
         ctr++;
      }
      if( params[1].type == Token::ttNumber )
      {
         *item = (DWORD)params[1].value.number;
         ctr++;
      }
      if( params[2].type == Token::ttString )
      {
         *if0 = *params[2].value.str;
         ctr++;
      }
      if( params[3].type == Token::ttString )
      {
         *if1 = *params[3].value.str;
         ctr++;
      }
   }
   return (ctr==4);
}

//
//------------------------------------------ EncodeFlag ----------------------------------------------------
//
bool EncodeFlag::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   if( params.size() % 2 || params.size() == 0 )
      return false;


   DWORD flags = 0;

   bool res = true;
   std::vector<Token>::const_iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( (*i).type != Token::ttNumber )
      {
         res = false;
         break;
      }
      int oval = (DWORD)((*i).value.number);

      i++;

      if( oval > 0 )
      {
         if( (*i).type != Token::ttNumber )
         {
            res = false;
            break;
         }
         int val = (int)((*i).value.number - 1);

         if( val >= 0 && val < 32 )
            flags |= 1 << val;
      }
   }

   *result = flags;
   return res;
}

//
//------------------------------------------ EncodeFlagValue ----------------------------------------------------
//
bool EncodeFlagValue::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   if( params.size() % 2 || params.size() == 0 )
      return false;


   DWORD flags = 0;

   bool res = true;
   std::vector<Token>::const_iterator i = params.begin();
   for( ; i != params.end(); i++ )
   {
      if( (*i).type != Token::ttNumber )
      {
         res = false;
         break;
      }
      int oval = (DWORD)((*i).value.number);

      i++;

      if( oval > 0 )
      {
         if( (*i).type != Token::ttNumber )
         {
            res = false;
            break;
         }
         int val = (int)((*i).value.number + 0.05);
         flags |= val;
      }
   }

   *result = flags;
   return res;
}

//
//------------------------------------------ KVReader ----------------------------------------------------
//

KVReader::KVReader()
{
}

bool KVReader::GetParams(const std::vector<Token>& params, std::wstring* file, std::wstring* key, int* index)
{
   int v = 0;
   if( params.size() == 3 )
   {
      if( params[0].type == Token::ttString )
      {
         v++;
         *file = *params[0].value.str;
      }
      if( params[1].type == Token::ttString )
      {
         v++;
         *key = *params[1].value.str;
      }
      if( params[2].type == Token::ttNumber )
      {
         v++;
         *index = (int)params[2].value.number;
      }
   }

   return (v==3);
}

bool KVReader::Do(Token *result, const std::vector<Token> &params, Session *session, const SessionObject *thisObject)
{
   std::wstring file, key;
   int index;

   (*result) = L"";
   if( GetParams(params, &file, &key, &index) )
   {
      std::map<std::wstring, Values>::const_iterator fnd = files.find(file);
      const Values& v = (fnd == files.end()) ? Load(file, session->Config().ExchangeFolder(), session) : fnd->second;
      Values::const_iterator vf = v.find(key);
      if( vf != v.end() )
      {
         if( index < (int)vf->second.size() && index >= 0 )
            *result = vf->second[index];
      }
      session->AddHandler(this);
   }
   return true;
}

const KVReader::Values& KVReader::Load(const std::wstring& fileName, const std::string& baseFolder, Session* session)
{
   USES_CONVERSION;

   Values& v = files[fileName];

   std::string fn(baseFolder);
   fn += W2A_CP(fileName.c_str(), CP_UTF8);

   FILE* rd = fopen(fn.c_str(), "rt");
   if( rd != NULL )
   {
      USES_CONVERSION;

      std::string key, value;

      while( ReadKeyValue(rd, &key, &value) )
      {
         ValueList& vlist = v[A2W(key.c_str())];

         std::wstring::size_type off = 0, nextOff;
         while( true )
         {
            nextOff = value.find(';', off);
            vlist.push_back(A2W(value.substr(off,
               (nextOff != std::string::npos) ?
                  nextOff - off :
                  std::string::npos).c_str()));

            if( nextOff == std::string::npos )
               break;

            off = nextOff + 1;
         }
      }

      fclose(rd);
   }

   return v;
}

bool ToFileName::Do(Token *result, const std::vector<Token> &params, Session *session, const SessionObject *thisObject)
{
	std::wstring value;
	if (params.size() == 1 && params[0].ToString(&value))
	{
		std::wstring::iterator i = value.begin();

		for (; i != value.end(); i++)
		{
			if (!iswalnum(*i))
			{
				*i = L'_';
			}
		}
	}

	*result = value;
	return true;
}


//
//------------------------------------------ KVReader ----------------------------------------------------
//

bool ConfigReader::GetParams(const std::vector<Token>& params, std::wstring* key, int* index)
{
   int v = 0;
   if( params.size() == 2 )
   {
      if( params[0].type == Token::ttString )
      {
         v++;
         *key = *params[0].value.str;
      }
      if( params[1].type == Token::ttNumber )
      {
         v++;
         *index = (int)params[1].value.number;
      }
   }

   return (v==2);
}

bool ConfigReader::Do(Token *result, const std::vector<Token> &params, Session *session, const SessionObject *thisObject)
{
   std::wstring key;
   int index;

   (*result) = L"";
   if( GetParams(params, &key, &index) )
   {
		ISessionObject *iso = session->LoadObject(L"Config", NULL);
		if( iso != NULL )
		{
			ServObject *so = iso->Self();
			if( keyIndex == -1 )
				keyIndex = so->format->FindMember(L"key");
			if( valueIndex == -1 )
				valueIndex = so->format->FindMember(L"value");

			if( keyIndex >= 0 && valueIndex >= 0 )
			{
				for( unsigned i=0; i<so->size(); i++ )
				{
					Object* o = so->at(i);
					if( o->at(keyIndex).str->compare(key) == 0 )
					{
						std::wstring& value = (std::wstring&)*o->at(valueIndex).str;
						std::wstring::size_type off = 0, nextOff;
						while( true )
						{
							nextOff = value.find(L';', off);
							if( index <= 0 )
							{
								*result = value.substr(off, (nextOff != std::string::npos) ? nextOff - off : std::string::npos);
								break;
							}
							if( nextOff == std::string::npos )
								break;

							off = nextOff + 1;
							index--;
						}
					}
				}
			}
		}
	}
	return true;
}
//
//------------------------------------------ MaskShift ----------------------------------------------------
//
bool MaskShift::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   bool res = false;
   DWORD val, mask, shift, v = 0;

   if( params.size() == 3 )
   {
      if( params[0].type == Token::ttNumber )
      {
         v++;
         val = (DWORD)(params[0].value.number + 0.005);
      }
      if( params[1].type == Token::ttNumber )
      {
         v++;
         mask = (DWORD)(params[1].value.number + 0.005);
      }
      if( params[2].type == Token::ttNumber )
      {
         v++;
         shift = (DWORD)(params[2].value.number + 0.005);
      }

      if( v == 3 )
      {
         res = true;
         *result = (val & mask) >> shift;
      }
   }

   return res;

}

//
//------------------------------------------ GetColor ----------------------------------------------------
//
GetColor::GetColor()
{
	mutex.Init();
}

void GetColor::SessionClosed(ISession* s)
{
   DataMap::iterator fnd = data.find(s);
	if (fnd != data.end())
	{
		if (mutex.Acquire(1000))
		{
			data.erase(fnd);
			mutex.Release();
		}
	}
}

// params id, type ("org", "price"), colorType ("face", "back"), defaultColor
bool GetColor::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   *result = 0.0;

   Param param;
   if( param.Read(params, session, thisObject) )
   {
      if( !FindData(result, param) )
         LoadData(result, param);
   }

   return true;
}

bool GetColor::FindData(Token* result, const GetColor::Param& param)
{
   bool ret = false;
   DataMap::const_iterator fnd = data.find(param.session);
   if( fnd != data.end() )
   {
      TypeColorMap::const_iterator tcm = fnd->second.find(param.type);
      if( tcm != fnd->second.end() )
      {
         ColorMap::const_iterator cmi = tcm->second.find(param.id);
         if( cmi != tcm->second.end() )
         {
            *result = (param.face) ? cmi->second.face : cmi->second.back;
         } else
         {
            *result = param.defaultColor;
         }
         ret = true;
      }
   }

   return ret;
}

bool GetColor::LoadData(Token* result, const GetColor::Param& param)
{
   bool ret = false;
   wchar_t buf[50];
   wsprintf(buf, L"\"type\"=%d", (DWORD)param.type);
   SessionObject* so = param.session->Build(L"ColorsTable", false);
   if( so )
   {
      if( so->CreateReader(buf) )
      {
         so->Load(NULL);
         so->CloseReader();
      }

      int fi = so->format->FindMember(L"face");
      int bi = so->format->FindMember(L"back");
      int idi = so->format->FindMember(L"id");
      if( fi >= 0 && bi >= 0 && idi >= 0 )
      {
         ret = true;
         ColorMap clrs;
         SessionObject::const_iterator i = so->begin();
         for( ; i != so->end(); i++ )
         {
            ColorData data;
            data.back = (DWORD)(*i)->at(bi).number;
            data.face = (DWORD)(*i)->at(fi).number;

            const std::wstring& id = (const std::wstring&)*(*i)->at(idi).str;
            clrs[id] = data;
            if( id.compare(param.id) == 0 )
               *result = (double)((param.face) ? data.face : data.back);
         }
			
			if (mutex.Acquire(1000))
			{
				DataMap::const_iterator fnd = data.find(param.session);
				TypeColorMap& clrMap = data[param.session];
				clrMap[param.type] = clrs;

				if (fnd == data.end())
					param.session->AddHandler(this);
				
				mutex.Release();
			}
      }

      delete so;
   }

   return ret;
}

bool GetColor::Param::Read(const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
   int ctr = 0;
   this->session = session;
   if( params.size() >= 3 )
   {
      if( params[0].type == Token::ttString )
      {
         this->id = *params[0].value.str;
         ctr++;
      }
      if( params[1].type == Token::ttString )
      {
         const wchar_t* str = params[1].value.str->c_str();
         if( _wcsicmp(str, L"org") == 0 ) this->type = 1;
         else if( _wcsicmp(str, L"price") == 0 ) this->type = 0;
         else return false;

         ctr++;
      }
      if( params[2].type == Token::ttString )
      {
         const wchar_t* str = params[2].value.str->c_str();
         this->face = (_wcsicmp(str, L"face") == 0);
         ctr++;
      }
      if( params.size() == 4 )
      {
         if( params[3].type == Token::ttNumber )
         {
            this->defaultColor = (DWORD)params[3].value.number;
            ctr++;
         }
      } else
      {
         this->defaultColor = 0;
         ctr++;
      }
   }
   return (ctr==4);
}

bool DataExtention::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
{
	(*result) = L"";
	if (params.size() >= 2)
	{
		int ctr = 0;
		if (params[ctr].type == Token::ttServObject)
		{
			ServObject *so = NULL;
			so = params[ctr].value.object;

			ctr++;
			if (params[ctr].type == Token::ttString)
			{
				std::wstring fieldName;
				fieldName = *params[ctr].value.str;

				int idx = so->format->FindMember(fieldName.c_str());
				if (idx >= 0)
				{
					Object* o = so->at(thisObject->CurObjectIndex());
					IBinary* b = o->at(idx).binary;
					if (b != NULL && b->Size() > 0)
					{
						const BYTE* pb = b->Bytes();
						if (IsJPEG(pb))
							(*result) = L".jpeg";
						else if (IsPNG(pb))
							(*result) = L".png";
					}
				}
			}
		}
	}

	return true;
}

//bool ResizePhoto::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject *thisObject)
//{
//	(*result) = L"";
//	if (params.size() >= 3)
//	{
//		int ctr = 0;
//		ServObject *so = NULL;
//		std::wstring fieldName, destField;
//		DWORD maxSize;
//
//		if (params[ctr].type == Token::ttServObject)
//		{
//			so = params[ctr].value.object;
//			ctr++;
//			if (params[ctr].type == Token::ttString)
//			{
//				fieldName = *params[ctr].value.str;
//				ctr++;
//				if (params[ctr].type == Token::ttNumber)
//				{
//					maxSize = (DWORD) (params[ctr].value.number + 0.01);
//					ctr++;
//				}
//			}
//			if (params.size() > 3 && ctr == 3 && params[ctr].type == Token::ttString)
//				destField = *params[ctr++].value.str;
//		}
//		if(ctr >= 3)
//		{
//			int idx = so->format->FindMember(fieldName.c_str());
//			int destIdx = destField.empty() ? idx : so->format->FindMember(destField.c_str());
//			if (idx >= 0 && destIdx >= 0)
//			{
//				Object* o = so->at(thisObject->CurObjectIndex());
//				IBinary* b = o->at(idx).binary;
//				if (b != NULL && b->Size() > 0)
//				{
//#ifndef USE_INTEL_IPP
//					int _w, _h;
//					ImageFormat ifmt;
//					wchar_t bufPicImg[100];
//					IBinary* dest = Scale(b, maxSize, &_w, &_h, &ifmt);
//					wsprintf(bufPicImg, L"%d*%d", _w, _h);
//					delete o->at(destIdx).binary;
//					o->at(destIdx).binary = dest;
//					(*result) = bufPicImg;
//#else
//					const BYTE* pb = b->Bytes();
//					FIBITMAP *dib = NULL;
//					FIMEMORY *hmem = FreeImage_OpenMemory((BYTE*)pb, b->Size());
//					FREE_IMAGE_FORMAT format;
//					if (memcmp(pb, pngSignature, sizeof(pngSignature) - 1) == 0)
//					{
//						dib = FreeImage_LoadFromMemory((FREE_IMAGE_FORMAT)1, hmem, 0);
//						format = (FREE_IMAGE_FORMAT)1;
//					}
//					else if (memcmp(pb, jpegSignature, sizeof(jpegSignature) - 1) == 0)
//					{
//						dib = FreeImage_LoadFromMemory((FREE_IMAGE_FORMAT)0, hmem, 0);
//						format = (FREE_IMAGE_FORMAT)0;
//					}
//
//					if (dib != NULL)
//					{
//						int w = FreeImage_GetWidth(dib);
//						int h = FreeImage_GetHeight(dib);
//						int maxD = max(w, h);
//						wchar_t bufPicImg[100];
//						wsprintf(bufPicImg, L"%d*%d", w, h);
//
//						if ((DWORD)maxD > maxSize)
//						{
//							double coef = (double)maxSize / maxD;
//							int sW = (int)(w * coef);
//							int sH = (int)(h * coef);
//
//							FIBITMAP* newDib = FreeImage_Rescale(dib, sW, sH);
//							FIMEMORY* wrmem = FreeImage_OpenMemory();
//							FreeImage_SaveToMemory(format, newDib, wrmem, 0);
//
//							BYTE *mem_buffer = NULL;
//							DWORD size_in_bytes = 0;
//							FreeImage_AcquireMemory(wrmem, &mem_buffer, &size_in_bytes);
//
//							Binary *wrb = new Binary();
//							memcpy(wrb->Alloc(size_in_bytes), mem_buffer, size_in_bytes);
//							MemoryBinary *mb = new MemoryBinary(wrb);
//							o->at(destIdx).binary = mb;
//							FreeImage_CloseMemory(wrmem);
//							FreeImage_Unload(newDib);
//
//							if (idx == destIdx)
//								delete b;
//
//							wsprintf(bufPicImg, L"%d*%d", sW, sH);
//						}
//						else if (idx != destIdx)
//						{
//							Binary *wrb = new Binary();
//							memcpy(wrb->Alloc(b->Size()), pb, b->Size());
//							MemoryBinary *mb = new MemoryBinary(wrb);
//							o->at(destIdx).binary = mb;
//						}
//						FreeImage_Unload(dib);
//						(*result) = bufPicImg;
//					}
//					FreeImage_CloseMemory(hmem);
//#endif
//				}
//			}
//		}
//	}
//	return true;
//}

bool RandomFileName::Do(Token* result, const std::vector<Token>& params, Session* session, const SessionObject* thisObject)
{
   std::wstring res;
   GenerateID(&res);

   (*result) = res.c_str();
   return true;
}

//
//------------------------------------------ ParamResolver ----------------------------------------------------
//
ParamResolver::ParamResolver(std::vector<Token>* params, Session* session, const SessionObject* object)
{
   this->params = params;
   this->session = session;
   this->object = object;
}

bool ParamResolver::Do(const std::wstring& str)
{
   const wchar_t *p = str.c_str();
   const wchar_t *ep = p + str.size();
   StringStream ss(p, ep);

   return Do(ss);
}

bool ParamResolver::Do(StringStream& str)
{
   if( str.EOS() )
      return true;

   Token res;
   bool ret = ParseStr(&res, str, object, *(IResolver*)this, L";");

   if( ret )
      params->push_back(res);

   return ret;
}

bool ParamResolver::Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const
{
   return session->Resolve(dest, stream, val, thisObject);
}

bool ParamResolver::EndStatement(Token &result, StringStream &stream, wchar_t endSym)
{
   params->push_back(result);
   return false;
}

bool GRServer::DoFunction(Token* res, StringStream& stream, const std::wstring& funcName, Session* session, const SessionObject *thisObject)
{
   bool ret = false;

   Token tval;
   std::vector<Token> params;
   FuncParamResolver pr(&params, session, thisObject);
   stream.MoveNext();
   ret = ParseStr(&tval, stream, thisObject, pr, L",)");
   if( ret )
   {
      //if( pr.Do(stream) )
      //stream.MoveNext();
      ret = DoFunc(res, params, funcName, session, thisObject);
   }

   return ret;
}

void GRServer::InitFunctions()
{
	AddFunction(&nowFunc);
	AddFunction(&folderID);
	AddFunction(&adjDate);
	AddFunction(&firstItem);
	AddFunction(&kvReader);
	AddFunction(&cfgReader);
	AddFunction(&decodeFlag);
	AddFunction(&encodeFlag);
	AddFunction(&encodeFlagValue);
	AddFunction(&maskShift);
	AddFunction(&getColor);
	AddFunction(&serverTZ);
	AddFunction(&formatDateStrFunc);
	AddFunction(&toFileName);
	AddFunction(&indexOf);
	//AddFunction(&resizePhoto);
	AddFunction(&dataExt);
	AddFunction(&toDateFoo);
   AddFunction(&userDataFunc);
   AddFunction(&rndFileName);
}

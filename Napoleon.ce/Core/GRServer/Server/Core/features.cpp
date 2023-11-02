/*
 * Copyright (C), 2009 - 2011, Денис Мосягин
 *
 * Фичи
 *
 * ert   20/07/2011   creating
 */
#include "stdafx.h"
#include "grftrs.h"
#include <map>
#include "srvutility.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>
#include <Binary.h>
#include "parse.h"

#include "server.h"

using namespace GRServer;

static std::map<std::wstring, Token> features;

class Reslover : public IResolver
{
	virtual bool Resolve(Token *dest, StringStream &stream, const std::wstring &val, const SessionObject *thisObject) const {
		return false;
	}
	virtual bool EndStatement(Token &result, StringStream &stream, wchar_t endSym) {
		return false;
	}
};

bool GRServer::LoadFeatures(const std::string& ftrFileName)
{
   bool res = false;

	Reslover r;

   std::string fileName;
   FullFileName(&fileName, ftrFileName.c_str());
   FILE *rd = fopen(fileName.c_str(), "rt");
   if( rd != NULL )
   {
      USES_CONVERSION;
      std::string line;
      while( ReadLine(&line, rd) )
      {
         std::string name;
			size_t pos = line.find('=');

         Trim(&name, line.substr(0, pos));
			if (!name.empty())
			{
				Token val;
				if (pos != std::string::npos)
				{
					const wchar_t* p = A2W(line.substr(pos + 1).c_str());
					const wchar_t *ep = p + wcslen(p);
					StringStream ss(p, ep);
					ParseStr(&val, ss, NULL, r);
				}
				features.insert(std::map<std::wstring, Token>::value_type(A2W(name.c_str()), val));
			}
      }

      fclose(rd);
   }

   return res;
}

static void CopyFtrName(std::wstring* str, StringStream& stream)
{
   while(true)
   {
      stream.MoveNext();
      if( stream.EOS() ) break;

      wchar_t sym = stream.Current();
      if( iswalnum(sym) || sym == L'_' )
         str->append(1, sym);
      else
      {
         stream.Back();
         break;
      }
   }
}

static bool SkipToBracket(StringStream& stream)
{
   int level = 0;
   while( !stream.EOS() )
   {
      wchar_t sym = stream.Current();
      if( sym == L')' && level-- == 0 )
         break;
      if( sym == L'(' )
         level++;
      stream.MoveNext();
   }

   return !stream.EOS();
}

static bool SkipToOr(StringStream& stream)
{
   while( !stream.EOS() )
   {
      wchar_t sym = stream.Current();
      if( sym == L')' )
         break;
      if( sym == '|' )
      {
         stream.Back();
         break;
      }
      if( sym == L'(' )
      {
         stream.MoveNext();
         if( !SkipToBracket(stream) )
            break;
      }
      stream.MoveNext();
   }

   return !stream.EOS();
}

static bool ParseFeatures(StringStream& stream, const wchar_t* endStmt = L"")
{
   bool result = false;
   bool done = false;
   std::wstring var;

   for( ; !stream.EOS(); stream.MoveNext() )
   {
      if( !stream.EatWhite() )
         break;

      wchar_t sym = stream.Current();
      switch( sym )
      {
      case L'&':
         if( !result )
            SkipToOr(stream);
         break;
      case L'|':
         if( result )
         {
            if( *endStmt == L')' )
               SkipToBracket(stream);
            done = true;
         }
         break;
      case L'(':
         stream.MoveNext();
         result = ParseFeatures(stream, L")");
         break;
      case L'!':
         stream.MoveNext();
			if( !stream.EatWhite() )
				break;
			if( stream.Current() != L'(' )
			{
            var.clear();
            var.append(1, stream.Current());
            CopyFtrName(&var, stream);
            result = !(features.find(var) != features.end());
			}
         break;
      default:
         if( wcschr(endStmt, sym) != 0 )
         {
            done = true;
         } else
         {
            var.clear();
            var.append(1, sym);
            CopyFtrName(&var, stream);
            result = (features.find(var) != features.end());
         }
      }

      if( done )
         break;
   }

   return result;
}

bool GRServer::HaveFeature(const std::wstring& ftrExpr)
{
   StringStream ss(ftrExpr);
   return ParseFeatures(ss);
}

bool GRServer::GetFeatureValue(Token* res, const std::wstring& feature)
{
	std::map<std::wstring, Token>::const_iterator fnd = features.find(feature);
	if (fnd != features.end())
	{
		(*res) = fnd->second;
		return true;
	}
	return false;
}
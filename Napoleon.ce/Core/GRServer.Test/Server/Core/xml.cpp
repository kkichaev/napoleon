/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * XML wrapper
 *
 * ert   12/05/2009   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "srvutility.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

const int LINE_BUF_SIZE = 8192;

IXmlHandler::Attributes::Attributes(const char **atts)
{
   if( atts != NULL )
   {
      for( int i=0; atts[i]; i++ )
      {
         std::wstring name, value;
         XmlParser::Utf8ToUtf16(&name, atts[i++]);
         XmlParser::Utf8ToUtf16(&value, atts[i]);

         insert(value_type(name, value));
      }
   }
}

bool IXmlHandler::Attributes::Find(std::wstring* value, const std::wstring& name) const
{
   const_iterator fnd = find(name);

   if( fnd != end() )
   {
      *value = fnd->second;
      return true;
   }

   return false;
}

XmlParser::XmlParser()
{
	parser = XML_ParserCreate(NULL);
}

XmlParser::~XmlParser()
{
	if (parser != NULL)
		XML_ParserFree(parser);
	parser = NULL;
}

void XmlParser::Utf8ToUtf16(std::wstring *dest, const char *str, int len)
{
   if( len <= 0 ) len = (int)strlen(str);
   len++;

   wchar_t *tempW = (wchar_t*)alloca(len * sizeof(wchar_t));
#if UNIX
   ConvHelper(str, (char*)tempW, len, len * sizeof(wchar_t), "UTF8", UTF_CP);
   int wlen = len-1;
#else

   *tempW = L'\0';
   int wlen = MultiByteToWideChar(CP_UTF8, 0, str, len-1, tempW, len);
#endif
   dest->assign(tempW, wlen);
}

bool XmlParser::Parsing(const std::string &fileName, IXmlHandler *handler)
{
   if( handler == NULL || parser == NULL)
      return false;

   std::string fullName;
   FullFileName(&fullName, fileName.c_str());

   FILE *file = fopen(fullName.c_str(), "rb");
   if( file == NULL )
      return false;

	SetHandler(handler);

   XML_SetElementHandler(parser, (XML_StartElementHandler)StartElementHandler, (XML_EndElementHandler)EndElementHandler);
   XML_SetCharacterDataHandler(parser, (XML_CharacterDataHandler)CharacterDataHandler);
   XML_SetUserData(parser, this);

   fseek(file, 0, SEEK_END);
   int size = ftell(file);
   fseek(file, 0, SEEK_SET);

   char *lineBuf = (char*)malloc(size);
   fread(lineBuf, sizeof(char), size, file);
   fclose(file);

   bool retVal = (XML_Parse(parser, lineBuf, size, 1) != XML_STATUS_ERROR);
   free(lineBuf);

   return (handler->IsError()) ? false : retVal;
}


const wchar_t* XmlParser::GetError()
{
   return errorText.c_str();
}

void XmlParser::SetError(const wchar_t* errMsg)
{
   errorText = errMsg;
   if( parser != NULL )
      XML_StopParser(parser, XML_FALSE);
}

bool XmlParser::Parsing(const Binary& src, IXmlHandler *handler)
{
   USES_CONVERSION;

   errorText.clear();
   if( parser == NULL )
   {
      errorText = L"Не могу создать XML_Parser";
      return false;
   }

	SetHandler(handler);
	
	XML_SetElementHandler(parser, (XML_StartElementHandler)StartElementHandler, (XML_EndElementHandler)EndElementHandler);
   XML_SetCharacterDataHandler(parser, (XML_CharacterDataHandler)CharacterDataHandler);
   XML_SetUserData(parser, this);

   bool retVal = (XML_Parse(parser, (const char*)(const BYTE*)src, src.Size(), true) != XML_STATUS_ERROR);

   if( retVal == false )
   {
      if( errorText.empty() )
         errorText = A2W(XML_ErrorString(XML_GetErrorCode(parser)));
   } else if( handler->IsError() )
   {
      if( errorText.empty() )
         errorText = handler->GetError();
   }

   return (handler->IsError()) ? false : retVal;
}

void XmlParser::SetHandler(GRServer::IXmlHandler *handler)
{
	this->handler = handler;
	handler->owner = this;
}

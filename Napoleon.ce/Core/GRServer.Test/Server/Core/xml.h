/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * XML wrapper
 *
 * ert   12/05/2009   creating
 */
#ifndef __GRSERVER_XML_H
#define __GRSERVER_XML_H

#include <string>
#include <map>
#include "Binary.h"

#define XML_STATIC
#include <expat/expat.h>

namespace GRServer {

class XmlParser;

struct IXmlHandler
{
   struct Attributes : public std::map<std::wstring, std::wstring>
   {
      Attributes(const char **atts);
      bool Find(std::wstring* value, const std::wstring& name) const;
   };

   virtual ~IXmlHandler() {}

   virtual void StartElement(const std::wstring& name, const Attributes& atts) = 0;
   virtual void EndElement(const std::wstring& name) = 0;
   virtual void CharacterData(const std::wstring& name) = 0;

   virtual bool IsError() const = 0;
   virtual const wchar_t* GetError() const = 0;

	XmlParser *owner;
};

class XmlParser
{
public:
	XmlParser();
	~XmlParser();

   bool Parsing(const Binary& src, IXmlHandler *handler);
   bool Parsing(const std::string &fileName, IXmlHandler *handler);

   static void Utf8ToUtf16(std::wstring *dest, const char *str, int len=0);

	static void __cdecl StartElementHandler(XmlParser *parser, const char *name, const char **atts)
   {
      IXmlHandler::Attributes attributes(atts);
      std::wstring val;
      Utf8ToUtf16(&val, name);

      parser->handler->StartElement(val, attributes);
   }

	static void __cdecl EndElementHandler(XmlParser *parser, const char *name)
   {
      std::wstring val;
      Utf8ToUtf16(&val, name);

		parser->handler->EndElement(val);
   }

	static void __cdecl CharacterDataHandler(XmlParser *parser, const char *name, int len)
   {
      std::wstring val;
      Utf8ToUtf16(&val, name, len);

		parser->handler->CharacterData(val);
   }

   void SetHandler(IXmlHandler *handler);

   const wchar_t* GetError();

   void SetError(const wchar_t* errMsg);

protected:
	IXmlHandler *handler;
	XML_Parser parser;

	std::wstring errorText;
};

} // namespace GRServer

#endif

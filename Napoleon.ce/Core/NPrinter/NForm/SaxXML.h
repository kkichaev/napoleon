/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Tiny implementation SAX XML parser
 *
 *  ert   29/05/2008   creating
 */ 
#ifndef __SAX_XML_PARSER_H
#define __SAX_XML_PARSER_H

#include <string>
#include <map>

using namespace std;

class Stream
{
 public:
   Stream();
   ~Stream();

   bool Open(const wchar_t *fileName)
   {
      stream = _wfopen(fileName, L"rb");
      return stream != NULL;
   }

   void Close();

   wint_t Peek()
   {
      if( peek ) return curSym;
      Get();
      peek = true;
      return curSym;
   }

   wint_t Get()
   {
      if( Eof() ) return WEOF;
      if( peek )
      {
         peek = false;
         return curSym;
      }
      curSym = fgetwc(stream);
      return curSym;
   }

   bool Eof() const { return (stream == NULL || feof(stream) != 0); }

   wint_t EatWhite();

 protected:
   FILE *stream;
   wint_t curSym;
   bool peek;
};

typedef map<wstring, wstring> SAXAttributes;

struct ISAXHandler
{
   virtual void StartElement(const wchar_t *element, const SAXAttributes &attributes) = 0;
   virtual void EndElement(const wchar_t *element) = 0;
   virtual void Characters(const wchar_t *chars) = 0;
};

class SAXParser
{
 public:
   SAXParser();
   ~SAXParser();

   bool Parse(const wchar_t *fileName, ISAXHandler *handler);

 protected:
   enum State {none, startDocument, instruction, comment, startElement, attrName, attrValue, element, endElement};

   State state;
};

#endif

/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Tiny implementation SAX XML parser
 *
 *  ert   29/05/2008   creating
 */ 
#include "stdafx.h"
#include "SaxXML.h"

Stream::Stream() : curSym(WEOF), stream(NULL), peek(false)
{
}

Stream::~Stream()
{
   Close();
}

void Stream::Close()
{
   if( stream == NULL ) return;

   fclose(stream);
   stream = NULL;
   curSym = WEOF;
}

wint_t Stream::EatWhite()
{
   if( Eof() ) return WEOF;

   wint_t c;
   for( c = Peek(); iswspace(c) && !Eof(); )
      c = Get();

   if( peek ) peek = false;
   return c;
}

SAXParser::SAXParser() : state(none)
{
}

SAXParser::~SAXParser()
{
}

bool SAXParser::Parse(const wchar_t *fileName, ISAXHandler *handler)
{
   Stream stream;
   if( stream.Open(fileName) == false ) return false;

   state = startDocument;

   wstring elmnt, attribute, value, characters;
   SAXAttributes attributes;

   wint_t sym = WEOF;
   DWORD depth = 0;
   while( stream.Eof() == false )
   {
      sym = stream.Get();

      switch(state)
      {
         case startDocument:
            if( sym == L'<' )
            {
               sym = stream.Peek();
               if( sym == L'?' )
                  state = instruction;
               else if( sym == L'!' )
                  state = comment;
               else
                  state = startElement;
            } else if( sym == 0xFEFF )
               break;
            else
               return false;
            break;
         case instruction:
            if( sym == L'?' )
            {
               if( stream.Get() != L'>' )
                  return false;
               state = element;
            }
            break;
         case comment:
            if( sym == L'-' && stream.Get() == L'-' && stream.Get() == L'>' )
               state = element;
            break;
         case startElement:
            if( sym == L'/' )
            {
               if( stream.Get() != L'>' )
                  return false;
               state = element;

               handler->StartElement(elmnt.c_str(), attributes);
               depth++;

               handler->Characters(L"");

               handler->EndElement(elmnt.c_str());
               depth--;
               if( depth == 0 )
                  return true;
            } else if( sym == L'>' )
            {
               state = element;
               handler->StartElement(elmnt.c_str(), attributes);
               depth++;

               elmnt.clear();
               attributes.clear();
            } else if( iswspace(sym) )
            {
               sym = stream.EatWhite();
               if( sym == L'/' )
               {
                  if( stream.Get() != L'>' )
                     return false;

                  state = element;

                  handler->StartElement(elmnt.c_str(), attributes);
                  depth++;

                  handler->Characters(L"");

                  handler->EndElement(elmnt.c_str());
   
                  depth--;
                  if( depth == 0 )
                     return true;

                  elmnt.clear();
                  attributes.clear();
               } else if( sym == L'>' )
               {
                  state = element;
                  state = element;
                  handler->StartElement(elmnt.c_str(), attributes);
                  depth++;

                  elmnt.clear();
                  attributes.clear();
               } else
               {
                  state = attrName;
                  attribute += sym;
               }
            } else
               elmnt += sym;
            break;
         case attrName:
            if( sym == L'=' )
            {
               sym = stream.EatWhite();
               if( sym != L'\'' && sym != '"' )
                  return false;
               state = attrValue;
            } else if( iswspace(sym) )
               break;
            else
               attribute += sym;
            break;
         case attrValue:
            if( sym == L'\'' || sym == '"' )
            {
               attributes.insert(SAXAttributes::value_type(attribute, value));
               attribute.clear();
               value.clear();

               state = startElement;
            } else
               value += sym;
            break;
         case element:
            if( sym == L'<' )
            {
               if( characters.size() )
               {
                  handler->Characters(characters.c_str());
                  characters.clear();
               }
               sym = stream.Peek();
               if( sym == L'/' )
               {
                  sym = stream.Get();
                  state = endElement;
               } else if( sym == L'!' )
               {
                  sym = stream.Get();
                  state = comment;
               } else
                  state = startElement;
            } else
               characters += sym;
            break;
         case endElement:
            if( sym == L'>' )
            {
               state = element;
               handler->EndElement(elmnt.c_str());
               elmnt.clear();
               depth --;
               if( depth == 0 )
                  return true;
            } else
               elmnt += sym;
            break;
      }
   }

   return false;
}

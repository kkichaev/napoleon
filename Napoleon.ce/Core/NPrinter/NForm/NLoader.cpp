/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Загрузка из XML
 *
 *  ert   13/05/2008   creating
 */ 
#include "stdafx.h"
#include "IStreamable.h"
#include "SaxXML.h"

class ContentHandler : public ISAXHandler
{
 public:
   ContentHandler(FormMaker *fm);

   virtual void Characters(const wchar_t* pwchChars);
   virtual void EndElement(const wchar_t* pwchLocalName);
   virtual void StartElement(const wchar_t* pwchLocalName, const SAXAttributes &attributes);

 protected:
   enum FoemLoadState { loading = 1, loaded = 2 };

   WORD state;
   FormMaker *formMaker;
   IXMLStreamable *current;

   std::wstring propName;
   bool multiPage;
   int depth;
};

ContentHandler::ContentHandler(FormMaker *fm)
{
   formMaker = fm;
   current = NULL;
   state = 0;

   multiPage = false;
   depth = 0;
}

void ContentHandler::Characters(const wchar_t* pwchChars)
{
   if( propName.size() == 0 ) return;

   if( (state & loading) != 0 )
   {
      formMaker->SetProperty(propName.c_str(), pwchChars);
      return;
   }

   if( current == NULL ) return;

   current->SetProperty(propName.c_str(), pwchChars);
}

void ContentHandler::StartElement(const wchar_t* pwchLocalName, const SAXAttributes &attributes)
{
   propName.clear();
   propName.assign(pwchLocalName);

   depth++;

   if( current != NULL )
      current->StartElement(propName.c_str(), attributes);
   else if( propName.compare(L"object") == 0 )
   {
      if( (state & loaded) == 0 )
      {
         SAXAttributes::const_iterator fnd = attributes.find(L"name");
         if( fnd == attributes.end() || fnd->second.size() == 0 )
         {
            state |= loading;
            return;
         } else
         {
            state &= (~loading);
            state |= loaded;
         }
      } 
      current = IXMLStreamable::Create(attributes);
   } else if( propName.compare(L"MultiPage") == 0 )
      multiPage = true;
}

void ContentHandler::EndElement(const wchar_t* pwchLocalName)
{
   depth--;
   if( current != NULL && current->EndElement(pwchLocalName) )
   {
      if( current->Valid() )
         formMaker->AddCell(current);
      current = NULL;
   }
}
 
enum States { none, whitespace, startelement, endelement, characters };
  
bool FormMaker::Load(const wchar_t *fileNameW, IDataSource *source)
{
   SAXParser parser;
   ContentHandler ch(this);
   if( parser.Parse(fileNameW, &ch) == false )
      return false;

   this->source = source;

   CELLS::iterator i;
   for( i = cells.begin(); i != cells.end(); i++ )
      (*i)->BeforePrint(this->source);

   curPage = 1;
   current = cells.begin();
   return (cells.size() > 0);
}

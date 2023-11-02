/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Загрузка из XML
 *
 *  ert   13/05/2008   creating
 */ 
#ifndef __I_STREAMABLE_H
#define __I_STREAMABLE_H

#include "NForm.h"
#include <SaxXML.h>

struct IXMLStreamable : public IPrintable
{
   virtual bool StartElement(const wchar_t *name, const SAXAttributes &attributes) = 0;
   virtual bool SetProperty(const wchar_t *name, const wchar_t *value) = 0;

   // return true only on </object>
   virtual bool EndElement(const wchar_t *name) = 0;

   virtual bool Valid() const = 0;

   static IXMLStreamable* Create(const SAXAttributes &attributes);
};


#endif

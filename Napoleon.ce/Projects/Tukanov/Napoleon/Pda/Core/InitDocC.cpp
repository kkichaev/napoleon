/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include "Add.h"

wchar_t dtReturn[] = L"Возвраты";

struct ReturnFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new ReturnImpl(); }
   virtual void Free(IDocument* document) const { delete (ReturnImpl*)document; }
} returnFactory;

struct ReturnType : public DocType
{
   ReturnType() : DocType(dtReturn, &returnFactory, dtHaveSum)
   {
   }

   virtual const wchar_t* SendTypeName() const { return L"Returns"; }
};


void InitCustomDocTypeSet()
{
   docTypeManager.insert(new ReturnType());
}

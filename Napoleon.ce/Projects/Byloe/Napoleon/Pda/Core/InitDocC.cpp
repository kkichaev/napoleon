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

wchar_t dtIncass[] = L"Инкассация";

struct IncassFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new IncassImpl(); }
   virtual void Free(IDocument* document) const { delete (IncassImpl*)document; }
} incassFactory;

struct IncassDocType : public DocType
{
   IncassDocType() : DocType(dtIncass, &incassFactory, dtHaveSum) {}
   virtual const wchar_t* SendTypeName() const { return L"Incass"; }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new IncassDocType());
}

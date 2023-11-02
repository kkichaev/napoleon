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

wchar_t dtDocPay[] = L"Инкасс";

struct DocPayFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new IncassImpl(); }
   virtual void Free(IDocument* document) const { delete (IncassImpl*)document; }
} docPayFactory;

struct DocPayType : public DocType
{
   DocPayType() : DocType(dtDocPay, &docPayFactory, dtHaveSum) {}
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new DocPayType());
}

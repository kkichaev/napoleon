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

#include "RestOut.h"

wchar_t dtRestOut[] = L"Контроль остатков";

struct RestFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new RestOutImpl(); }
   virtual void Free(IDocument* document) const { delete (RestOutImpl*)document; }
} restFactory;

struct RestDT : public DocType
{
   RestDT() : DocType(dtRestOut, &restFactory, 0) {}
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new RestDT());
}
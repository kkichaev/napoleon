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

wchar_t dtDisplay[] = L"Выкладка";

struct DisplayFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new DisplayImpl(); }
   virtual void Free(IDocument* document) const { delete (DisplayImpl*)document; }
} displayFactory;

struct DisplayDT : public DocType
{
   DisplayDT() : DocType(dtDisplay, &displayFactory, 0) {}
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new DisplayDT());
}
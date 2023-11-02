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

wchar_t dtGoodsRest[] = L"Партии";

struct GoodsRestFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new GoodsRestImpl(); }
   virtual void Free(IDocument* document) const { delete (GoodsRestImpl*)document; }
} grFactory;

struct GoodsRestType : public DocType
{
   GoodsRestType() : DocType(dtGoodsRest, &grFactory, 0) {}
};


void InitCustomDocTypeSet()
{
   docTypeManager.insert(new GoodsRestType());
}

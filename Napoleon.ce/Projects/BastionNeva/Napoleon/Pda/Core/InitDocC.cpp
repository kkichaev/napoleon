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
#include <Add.h>

BEGIN_TYPE_REFLECTION(Pays)
   REGISTER_FILETIME_MEMBER(Pays, date)
   REGISTER_STRING_MEMBER(Pays, id)
   REGISTER_STRING_MEMBER(Pays, number)
   REGISTER_ULONG_SCALE_MEMBER(Pays, sum, SUM_SCALE)
END_TYPE_REFLECTION(Pays)

wchar_t dtPays[] = L"Оплаты";
struct PaysFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PaysImpl(); }
   virtual void Free(IDocument* document) const { delete (PaysImpl*)document; }
} paysFactory;

struct PaysType : public DocType
{
   PaysType() : DocType(dtPays, &paysFactory, dtHaveSum)
   {
   }

   virtual bool ShowInDocumentList() const { return true; }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new PaysType());
}
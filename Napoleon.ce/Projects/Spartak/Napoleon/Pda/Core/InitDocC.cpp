/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <DocType.h>
#include "OrgRmnts.h"
#include <InitDoc.h>
#include <FormEntries.h>

wchar_t dtRemnants[] = L"Остатки";

struct RmntsFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new OrgRemnantsImpl(); }
   virtual void Free(IDocument* document) const { delete (OrgRemnantsImpl*)document; }
} rmntsFactory;

struct RemnantsType : public DocType
{
   RemnantsType() : DocType(dtRemnants, &rmntsFactory, 0) {}
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new RemnantsType());
}

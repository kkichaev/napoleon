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

#include "Incass.h"
#include "Plan.h"

#include <ObjImpl.h>

wchar_t dtPlans[] = L"Планы";

struct PlanFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PlanImpl(); }
   virtual void Free(IDocument* document) const { delete (PlanImpl*)document; }
} planFactory;

struct PlanType : public DocType
{
   PlanType() : DocType(dtPlans, &planFactory, dtHaveSum)
   {
   }

   virtual bool ShowInDocumentList() const { return false; }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new PlanType());
   docTypeManager.insert(new IncassType());
}

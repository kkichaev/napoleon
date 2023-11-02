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

wchar_t dtDefects[] = L"Брак";
struct DefectFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new DefectImpl(); }
   virtual void Free(IDocument* document) const { delete (DefectImpl*)document; }
} defectFactory;

struct DefectType : public DocType
{
   DefectType() : 
      DocType(dtDefects, &defectFactory, dtHaveSum)
   {
   }
      virtual const wchar_t* SendTypeName() const { return L"Defect"; }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new DefectType());
}

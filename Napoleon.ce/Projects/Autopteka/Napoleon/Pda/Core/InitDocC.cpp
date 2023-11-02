/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <DocType.h>
#include "Add.h"
#include "Task.h"
#include "OrgRmnts.h"
#include <InitDoc.h>
#include <FormEntries.h>

wchar_t dtOrgInfo[] = L"Инфо";

struct InfoFactory : public IDocFactory
{
   virtual IDocument* Create() const { return NULL; }
   virtual void Free(IDocument* document) const { }
} infoFactory;

struct OrgInfoDT : public DocType
{
   OrgInfoDT() : DocType(dtOrgInfo, &infoFactory, 0) {}

   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const { OpenOrgInfo(orgid); }

   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
      const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const
   {
      *orgDocs = NULL;
      return false;
   }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new OrgInfoDT());
}

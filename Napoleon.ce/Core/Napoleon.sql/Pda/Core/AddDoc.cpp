/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Общая функция создания документа (для OrgList & OrgDocs)
 *
 *  ert   30/08/2010   creating
 *
 */
#include "stdafx.h"
#include "DocType.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DBImpl.h>

#include <ListForm.h>
#include "FormEntries.h"

bool AddNewDocument(ListForm* owner, const DocType* docType, const ROWID& orgID)
{
   return docType->CreateDocument(orgID);
}
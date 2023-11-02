/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Загрузчик типов докуменов
 *
 *  ert   22/03/2008   creating
 */
#ifndef _INIT_DOC_H
#define _INIT_DOC_H

#include <map>
#include "FormEntries.h"

#include "DocType.h"

struct WHDocType : public DocType
{
   WHDocType();
};

struct WhOutDocType : public DocType
{
   WhOutDocType();
};

#endif

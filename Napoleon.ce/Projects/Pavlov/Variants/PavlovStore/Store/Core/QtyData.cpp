/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Методы относящиеся к QTYData
 *
 *  ert   01/12/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "FormEntries.h"
#include "DocImpl.h"

#include <PicWindow.h>
#include <StdFuncs.h>

#include "NumInput.h"

#include <BaseDialog.h>

#ifdef SAVE_IN_PACK
WORD saveInPack=SAVE_IN_PACK;
#endif

QTYData::QTYData()
{
   id = L"";
   qty = 0;
   flags = 0;
   cost = 0;
   sum = 0;
   canChange = true;
}

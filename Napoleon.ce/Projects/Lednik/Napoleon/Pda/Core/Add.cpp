/*
 * Copyright (C), 2007-2011, Денис Мосягин
 *
 * Оплата накладных
 *
 *  ert   11/08/2011   creating
 *
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <BaseForm.h>
#include <BaseDialog.h>
#include <ListForm.h>

#include <FormEntries.h>

#include <Add.h>
//#include "NumInput.h"
#include "NplConfig.h"
#include <ObjImpl.h>
#include <EnterNumber.h>
#include "Add.h"

BEGIN_TYPE_REFLECTION(CustomCost)
   REGISTER_STRING_MEMBER(CustomCost, id)
   REGISTER_STRING_MEMBER(CustomCost, userid)
END_TYPE_REFLECTION(CustomCost)

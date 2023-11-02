/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Визиты
 *
 *  ert   08/12/2008   creating
 *  ert   23/06/2009   update
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <MainFrame.h>
#include "Visit.h"

#include <StdFuncs.h>
#include <ListForm.h>
#include <InitDoc.h>
#include <BaseDialog.h>
#include "PhotoFolder.h"
#include "PrfDlg.h"
#include <PicWindow.h>
#include <NplConfig.h>
#include "FileType.h"

void OpenVisit(VisitImpl *visit, bool retToDocList)
{
   VisitData *data = new VisitData(visit, retToDocList);
   _Module.GetFrame()->Load(IDD_VISIT, data);
}

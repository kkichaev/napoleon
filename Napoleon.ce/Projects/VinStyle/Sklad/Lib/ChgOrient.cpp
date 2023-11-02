/*
 * Copyright (C), 2007 - 2008, Денис Мосягин
 *
 * Изменить ориентацию экрана
 *
 *  ert   31/10/2008   creating
 */
#include "stdafx.h"
#include <StdFuncs.h>

bool ChangeOrientation(bool portrait)
{
   DEVMODE devMode = {0};

   devMode.dmSize = sizeof(devMode);
   devMode.dmFields = DM_DISPLAYORIENTATION;
   devMode.dmDisplayOrientation  = (portrait) ? DMDO_0 : DMDO_90;

   return (ChangeDisplaySettingsEx(NULL, &devMode, NULL, 0, NULL) == DISP_CHANGE_SUCCESSFUL);
}



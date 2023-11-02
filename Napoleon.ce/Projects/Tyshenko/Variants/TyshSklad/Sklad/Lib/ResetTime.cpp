/*
* Copyright (C), 2007, Денис Мосягин
*
* GetDeviceID
*
*  ert   06/12/2007   creating
*/

#include "stdafx.h"
#include <StdFuncs.h>

void ResetTime(SYSTEMTIME *st)
{
   st->wHour = 0;
   st->wMinute = 0;
   st->wSecond = 0;
   st->wMilliseconds = 0;
}

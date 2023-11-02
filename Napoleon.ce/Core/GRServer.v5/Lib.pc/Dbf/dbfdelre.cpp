#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>

void DataForm::DeleteRec()
{
  recBuf[0] = DelSym;
  WriteRec();
}

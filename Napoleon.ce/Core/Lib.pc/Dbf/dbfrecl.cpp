#include "stdafx.h"
#include <dbf.h>

void DataForm::RecallRec()
{
  recBuf[0] = ' ';
  WriteRec();
}

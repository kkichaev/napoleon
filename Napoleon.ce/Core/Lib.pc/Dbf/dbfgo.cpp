#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>

void DataForm::GoTop()
{
  cIndex = 0;
  eofFlag = false;
  ReadRec(cIndex);
}

void DataForm::GoBottom()
{
  cIndex = head->numRec-1;
  eofFlag = true;
  ReadRec(cIndex);
}

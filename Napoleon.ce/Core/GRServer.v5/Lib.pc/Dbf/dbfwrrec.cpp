#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

void DataForm::WriteRec()
{
  _lseek(sPtr,(long)head->headLen + cIndex * head->recLen, SEEK_SET );
  _write(sPtr,(uchar*)recBuf,head->recLen);
  mode |= flNeedReWrite;
}

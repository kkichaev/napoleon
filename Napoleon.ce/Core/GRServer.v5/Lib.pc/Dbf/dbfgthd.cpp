#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

int DataForm::GetHead( void *&buf )
{
  int len = head->headLen;
  buf = new uchar [ len ];
  _lseek( sPtr, 0, SEEK_SET );
  _read(sPtr,(uchar*)buf,len);
  return len;
}

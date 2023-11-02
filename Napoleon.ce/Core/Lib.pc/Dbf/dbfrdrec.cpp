#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

bool DataForm::ReadRec( long indx ) const
{
  if ( Error() != False ) return false;

  if( indx < 0 || indx >= head->numRec )  return false;

  if ( indx == cIndex+1  )
  {
    _read(sPtr,recBuf,head->recLen);
    cIndex++;
    return true;
  }
  cIndex = indx;
  ReadRec();
  return true;
}

void DataForm::ReadRec() const
{
  _lseek(sPtr,(long)head->headLen+(long)cIndex*head->recLen,SEEK_SET);
  _read(sPtr,recBuf,head->recLen);
}

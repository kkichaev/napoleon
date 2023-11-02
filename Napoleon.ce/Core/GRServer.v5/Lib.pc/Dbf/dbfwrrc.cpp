#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

bool DataForm::WriteRec(long rc)
{
	if( rc < 0 || rc > head->numRec )
		return false;
	if ( cIndex != rc-1 )
	{
		if( _lseek(sPtr, (long)head->headLen + rc * head->recLen, SEEK_SET ) < 0 )
			return false;
	}
	cIndex = rc;
	if( _write(sPtr,recBuf,head->recLen) < head->recLen )
		return false;

	mode |= flNeedReWrite;
	return true;
}

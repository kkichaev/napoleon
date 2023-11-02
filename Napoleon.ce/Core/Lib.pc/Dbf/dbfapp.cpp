#include "stdafx.h"
#include <dbf.h>
#ifdef UNIX
#else
#include <dos.h>
#endif
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>

void WriteDigit( char *p, long val, int wdh )
{
	char buf[200];
	sprintf(buf, "%*ld", wdh, val);
	memcpy(p, buf, strlen(buf));
	//  char *buf = new char[wdh+1], *sp = buf;
	//
	//  _ltoa( val, buf, 10 );
	//  int pos = strlen(buf) - 1;
	//  int i = wdh-1;
	//
	//  while ( pos >= 0 )
	//    p[i--] = sp[pos--];
	//
	//  while( i >= 0 ) p[i--] = ' ';
	//  delete buf;
}

bool DataForm::Append( const char *rcBuf )
{
	bool ret = true;

	recBuf[0] = ' ';
	if ( cIndex != head->numRec-1 )
	{
		cIndex = head->numRec;
		_lseek(sPtr,(long)head->headLen+(long)cIndex*head->recLen,SEEK_SET);
	}
	cIndex = head->numRec;
	mode |= flNeedReWrite;

	if( _write(sPtr,rcBuf,head->recLen) < head->recLen)
		ret = false;

	head->numRec++;

	return ret;
}

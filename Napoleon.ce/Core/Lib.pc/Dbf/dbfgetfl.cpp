#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>

char *DataForm::GetField( const char *fname ) const
{
  register int i;
  for ( i=0; i<numField; i++ )
    if( !_strnicmp(fname,(const char*)fields[i].name,sizeof(fields[i].name)) ) break;
  if ( i>=numField )  return (char*)0;
  return recBuf+fields[i].offset;
}

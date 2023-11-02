#include "stdafx.h"
#include <dbf.h>

void DataForm::Fill( const char *fld, const char *val )
{
  register signed i;
  unsigned char type;

  if ( Error() != False ) return;

  for ( i=0; i<numField; i++ )
    if( !strncmp(fld,(const char*)fields[i].name,sizeof(fields[i].name)) ) break;
  if ( i>=numField )  return;

  type = fields[i].type;
  char *src =  recBuf+fields[i].offset;
  i = fields[i].width;
  int ln = (int)strlen(val),
      off = (ln < i ) ? i - ln : 0;

  if ( ln > i ) ln = i;
  switch ( type )
  {
    case 'N':
    case 'F':
      if ( off ) memset(src,' ',off);
      memcpy(src+off,val,ln);
      return;
    default:
      memcpy(src,val,ln);
      if ( off ) memset(src+ln,' ',off);
  }
}

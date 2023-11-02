#include "stdafx.h"
#include <dbf.h>

int DataForm::GetWidth( const char *fname )
{
  int i;
  for ( i=0; i<numField; i++ )
    if( !strncmp(fname,(const char*)fields[i].name,sizeof(fields[i].name)) ) break;
  if ( i<=numField ) return fields[i].width;
  return 0;
}

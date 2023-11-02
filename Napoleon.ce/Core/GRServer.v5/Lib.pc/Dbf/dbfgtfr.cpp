#include "stdafx.h"
#include <dbf.h>

DBField* DataForm::GetFieldRef( const char *fname ) const
{
  register int i;
  for ( i=0; i<numField; i++ )
    if( !_strnicmp(fname,(const char*)fields[i].name,sizeof(fields[i].name)) )
      return fields+i;
  return NULL;
}

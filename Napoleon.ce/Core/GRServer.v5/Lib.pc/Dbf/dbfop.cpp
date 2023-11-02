#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>

const char* DataForm::operator[] (const char* fldName) const
{
  register int i;

  if ( Error() != False ) return NULL;

  for ( i=0; i<numField; i++ )
    if( !_strnicmp(fldName,(const char*)fields[i].name,sizeof(fields[i].name)) ) break;
  if ( i>=numField )  return NULL;

  return operator[] (i);

}

//char dateBuf[15];
static void FieldToDate(char* dest, const char *dataField )
{
  memcpy( dest, dataField+6, 2 );
  dest[2] = '.';
  memcpy( dest+3, dataField+4, 2 );
  dest[5] = '.';
  memcpy( dest+6, dataField, 4 );
  dest[10] = '\0';
  //return (const char*)dateBuf;
}

const char* DataForm::operator[] (int fldNum) const
{
  uchar *Sptr;
  unsigned short wdth;

  if ( Error() != False ||
       ((mode & flNoReadDelete) != 0 && *recBuf == '*') ) return (const char*)"";

  Sptr  = (uchar*)recBuf+fields[fldNum].offset;
  wdth  =  fields[fldNum].width;
  switch ( fields[fldNum].type )
  {
    case 'C':
       if( wdth == 0 ) wdth = *(unsigned short*)&(fields[fldNum].width);
      memcpy(str,Sptr,wdth);
      break;
    case 'N':
    case 'F':
      char *Dptr;
      int  wdh;
      Dptr = str;
      wdh = wdth;
      if( *Sptr == '.' )
      {
        str[0] = '0';
        Dptr  ++;
        wdth ++;
      } else
        if( *Sptr == '-' && *(Sptr+1) == '.' )
        {
          str[0] = Sptr[0];
          str[1] = '0';
          Dptr += 2;
          Sptr ++;
          wdh --;
          wdth ++;
        }
      memcpy(Dptr,Sptr,wdh);
      break;
    case 'L':
      str[0] = *Sptr;
      break;
    case 'D':
		 FieldToDate((char*)str, (const char *)Sptr);
      //strcpy( (char*)str, FieldToDate((const char *)Sptr) );
      return (const char*)str;
    default:
      *str = '\0';
      return (const char*)str;
  }
  str[wdth]='\0';
  return (const char*)str;
}

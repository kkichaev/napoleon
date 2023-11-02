#include "stdafx.h"
#include <dbf.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

#include <servobj.h>

void Format( char* &dst,
             const char *src, int &len,
             int Width, JustifyType Jst )
{
  int ln = (int)strlen((const char *)src);

  if( ln >= Width )
  {
    dst = new char [ln+1];
    strcpy(dst,src);
    dst[ln] = '\0';
    len = (int)ln;
    return;
  }

  dst = new char [Width+1];
  len = Width;

  switch ( Jst )
  {
    case NoJustify:
    case LeftJustify:
      strcpy( dst, src );
      memset( dst+ln, ' ', Width-ln );
      dst[len]='\0';
      break;
    case RightJustify:
      memset( dst, ' ', Width-ln );
      strcpy( dst+(Width-ln), src );
      dst[len]='\0';
      break;
    default:
      size_t hlf = (Width-ln)/2;
      memset( dst, ' ', hlf );
      strcpy( dst+hlf, src );
      memset( dst+hlf+ln, ' ', Width-hlf-ln );
      dst[len]='\0';
      break;
  }
  return;
}

//char numBuf[MaxStrLen];
static const char* FloatToStr(std::string* res, double vol, int Width, uchar Prec, int Type )
{
   char numBuf[MaxStrLen];
#ifdef UNIX
	if (!Type)
	{
		sprintf(numBuf, "%*.*f", Width, Prec, vol);
		//sprintf(numBuf, "%*.*f", Width, Prec, vol);
	}
	else
	{
		sprintf(numBuf, "%*.*G", Width, Prec, vol);
		//sprintf(numBuf, "%*.*G", Width, Prec, vol);
	}
#else
	if (!Type)
	{
		_sprintf_l(numBuf, "%*.*f", GRServer::ServObject::GetLocale(), Width, Prec, vol);
		//sprintf(numBuf, "%*.*f", Width, Prec, vol);
	}
	else
	{
		_sprintf_l(numBuf, "%*.*G", GRServer::ServObject::GetLocale(), Width, Prec, vol);
		//sprintf(numBuf, "%*.*G", Width, Prec, vol);
	}
#endif
  return Trunc(numBuf, res);
}

char pBase[MaxStrLen];
const char *Trunc( const char *src, std::string* buf )
{
   const char* p = src;
   const char *ep = src + strlen(src) - 1;
   while( p <= ep )
   {
      if( *ep != ' ')
         break;
      ep--;
   }

   while( p <= ep )
   {
      if( *p != ' ' )
         break;
      p++;
   }
   if( p <= ep )
      buf->assign(p, ep - p + 1);
   else
      buf->clear();

   return buf->c_str();
}
//  unsigned char *p = (unsigned char *)pBase;
//  int ln;
//  strcpy(pBase,src);
//
//  ln = strlen(pBase)+1;
//  while ( *p == ' ' ) p++;
//  memmove(pBase,p,ln-(int)(p-(unsigned char*)pBase));
//  p = (unsigned char*)pBase+strlen(pBase)-1;
//  while ( *p == ' ' && p > (unsigned char*)pBase ) p--;
//  *(p+1) = '\0';
//  return (const char*)pBase;
//}

void DataForm::Fill( char *rcBuf, const char *fldName, void *Val )
{
  int i;

  if ( Error() != False ) return;

  for ( i=0; i<numField; i++ )
    if( !strncmp(fldName,(const char*)fields[i].name,sizeof(fields[i].name)) )
    {
      Fill( rcBuf, i, Val );
      return;
    }
}

void DataForm::Fill( const char *fldName, double Val )
{
   Fill( recBuf, fldName, &Val );
}

void DataForm::Fill( char *rcBuf, int fldNum, void *Val )
{
  if( fldNum > numField || Error() != False ) return;

  char *Sr =  rcBuf+fields[fldNum].offset;
  int wdh   = fields[fldNum].width;
  int ln    = wdh;
  char *out_str;
  uchar t;
  DateType *dta;

  switch ( fields[fldNum].type )
  {
    case 'C':
      t = *((char*)(Val)+wdh);
      *((char*)(Val)+wdh) = '\0';
      Format( out_str, (const char*)Val, ln, wdh, LeftJustify );
      *((char*)(Val)+wdh) = t;
      break;
    case 'D':
      t = Sr[8];
      dta = (DateType*)Val;
      strcpy( Sr, (const char*)dta->year );
      strcpy( Sr+4, (const char*)dta->month );
      strcpy( Sr+6, (const char*)dta->day );
//      Sr[0] = '1';
//      Sr[1] = '9';
      Sr[8] = t;
      return;
    case 'N':
    case 'F':
    {
       std::string buf;
      Format( out_str, FloatToStr(&buf, *(double*)Val, wdh, fields[fldNum].prec, 0 ),
              ln, wdh,  RightJustify );
      break;
    }
  }
  memcpy( Sr, out_str, ln );
  delete out_str;

  return;
}

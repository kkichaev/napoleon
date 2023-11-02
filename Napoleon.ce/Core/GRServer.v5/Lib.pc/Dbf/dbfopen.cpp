#include "stdafx.h"
#include <dbf.h>
#include <fcntl.h>
#include <string.h>
#include <ctype.h>
#ifdef UNIX
#else
#include <io.h>
#include <share.h>
#include <stdio.h>
#include <stdlib.h>
#endif
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>


char fn[256];
char *ParseFileName( const char *fname )
{
  strcpy(fn,fname);
  char *p = fn + strlen(fn) - 1;

  int i = 0;
  while ( i<8 && *p != '.'&& *p != '\\' )
  {
    p--;
    i++;
  }
  if ( *p == '.' ) p[1] = '\0';
  return fn;
}

//char name[MaxFileName];
//const char *AddDefaultExt( const char* fname, const char *ext )
//{
//   strcpy( name, fname );
//   const char* p = strrchr(fname, '.');
//   if( (p == NULL) || (_strnicmp(p+1, ext, strlen(ext)) != 0) )
//   {
//      strcat(name, ".");
//      strcat(name, ext);
//   }
//   return name;
//}

bool DataForm::Open(const char *_fname, bool checkExt)
{
	fname = _fname;
	if (checkExt)
	{
		if(_stricmp(fname.substr(fname.size() - 4, 4).c_str(), ".dbf")!= 0)
			fname.append(".dbf");
	}
   //strcpy(fname, (checkExt) ? AddDefaultExt( _fname, "dbf" ) : _fname);

#ifdef _MSC_VER
   USES_CONVERSION;

    sPtr = _wsopen( A2W_CP(fname.c_str(), CP_UTF8), O_RDWR|O_BINARY, _SH_DENYNO, S_IREAD|S_IWRITE );
#elif UNIX
    sPtr = open( fname.c_str(), O_RDWR, S_IREAD|S_IWRITE );
#else
    sPtr = open( fname.c_str(), O_RDWR|O_BINARY|SH_DENYNONE, S_IREAD|S_IWRITE );
#endif
  if( sPtr == -1 )
  {
    SetError(detCantOpen);
    return false;
  }
  _lseek(sPtr,0,SEEK_SET);
  if( _read(sPtr,head,sizeof(DBHead)) != sizeof(DBHead))
  {
     _close(sPtr);
     sPtr = -1;
     return false;
  }
  numField = (head->headLen - sizeof(DBHead) - 1) / 32;
  fields   = new DBField [numField];
  recBuf   = new char [head->recLen];
  cIndex    = 0;

  if ( fields == NULL || recBuf == NULL )
  {
    SetError( detNoMemory );
    return false;
  }

  head->numRec = (_filelength(sPtr) - head->headLen) / head->recLen;

  _read(sPtr,(uchar*)fields,sizeof(DBField)*numField);
  delete str;
  str = new char [head->recLen+2];

  int i = 1;
  fields[0].offset = 1;
  for ( ; i<numField; i++ )
     fields[i].offset = (short)(fields[i-1].offset + fields[i-1].width);

  mode &= (~flDBError);
  mode &= (~flNoNeedClose);
  if( head->numRec > 0 ) ReadRec();
  else memset(recBuf,' ',head->recLen);

  eofFlag = false;

  return true;
}

bool DataForm::Create( const char *_fname, int num, DBRec *Recs )
{
	fname = _fname;
	if (*fname.rbegin() != '.')
		fname.append(".dbf");
	//strcpy(fname, AddDefaultExt( _fname, "dbf" ));
  int i = num-1;
  short len = 0;
  uchar memo = 3;

  numField = num;
  mode &= (~flDBError);

  fields = new DBField [numField];

// Set Fields
  for ( ; i>=0; i--)
  {
    memset(fields[i].name,0,sizeof(fields[i].name));
    strncpy( (char*)fields[i].name, (const char*)Recs[i].name,
             sizeof(fields[i].name) );

    if( (fields[i].type=(char)toupper(Recs[i].type)) == 'D' ) fields[i].width = (char)8;
    else fields[i].width = Recs[i].width;

    if( fields[i].type == 'D' || fields[i].type == 'C' ) fields[i].prec = 0;
    else fields[i].prec  = Recs[i].prec;

    fields[i].empty[0] = 0;
    fields[i].empty[1] = 0;
    memset(fields[i].empty1,0,sizeof(fields[i].empty1));

    len += fields[i].width;
  }
  i = 1;
  fields[0].offset = 1;
  for ( ; i<numField; i++ )
     fields[i].offset = (short)(fields[i-1].offset + fields[i-1].width);

// Set Head
  head->isMemo  = memo;
  head->numRec  = 0;
  head->recLen  = (short)(len+1);
  head->headLen = (short)((numField+1)*sizeof(DBField) + 1);
  memset((void*)head->noth,0,sizeof(head->noth));

  time_t Tm;
  struct tm *dta;
  time(&Tm);
  dta =  gmtime(&Tm);

  head->date[0] = (unsigned char)dta->tm_year;
  head->date[1] = (unsigned char)dta->tm_mon;
  head->date[2] = (unsigned char)dta->tm_mday;

// Write head
#ifdef UNIX
#else
  _fmode = O_BINARY;
#endif
  sPtr = _creat( fname.c_str(), S_IREAD|S_IWRITE );
  if( sPtr == -1 )
     return False;

  _write(sPtr,(uchar*)head,sizeof(DBHead));
  _write(sPtr,(uchar*)fields,sizeof(DBField)*numField);

  const char *t = "\r\x1a";
  _write(sPtr,t,2);

  delete str;
  str = new char [head->recLen+2];

  recBuf   = new char [head->recLen];
  memset(recBuf,' ',head->recLen);
  cIndex = 0;

  mode &= (~flDBError);
  mode &= (~flNoNeedClose);

  eofFlag = false;

  return true;
}

int DataForm::Field(const char *name) const
{
  int i;
  for ( i=0; i<numField; i++ )
  {
    if( _strnicmp((const char*)fields[i].name, name, sizeof(fields[i].name)) == 0 )
       return i;
  }

   return -1;
}

void DataForm::Delete()
{
	if (!fname.empty())
	{
		Close();
		_unlink(fname.c_str());
	}
}
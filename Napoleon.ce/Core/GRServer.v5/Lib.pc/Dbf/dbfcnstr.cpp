#include "stdafx.h"
#include <dbf.h>
#ifdef UNIX
#include <time.h>
#else
#include <dos.h>
#include <io.h>
#include <sys\stat.h>
#endif
#include <fcntl.h>
#include <stdio.h>

#ifdef _MSC_VER
#include <time.h>
#endif

unsigned int DBFBufSize  = 1024;

DataForm::DataForm() : sPtr(-1)
{
   fields  = NULL;
   recBuf  = NULL;
   mode = 0;
   head = new DBHead[1];
   str  = new char [16];
   mode |= (flNoReadDelete + flNoNeedClose);
   eofFlag = false;

   if ( head == NULL ) SetError( detNoMemory );
}

DataForm::~DataForm()
{
   Close();

   delete head;
   delete str;
}

void DataForm::Close()
{
   if( sPtr == -1 ) return;

   mode |= flNoNeedClose;
   uchar t = '\x1a';
   if ( mode & flNeedReWrite )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      head->date[0] = (BYTE)(st.wYear - 1900);
      head->date[1] = (BYTE)st.wMonth;
      head->date[2] = (BYTE)st.wDay;

      _lseek( sPtr,0,SEEK_SET );
      _write( sPtr,(uchar*)head, sizeof(DBHead) );
      long sz = (long)head->headLen+(long)head->numRec*head->recLen;
      _lseek(sPtr, sz, SEEK_SET);
      _write( sPtr, &t, sizeof(t) );
      _chsize(sPtr, sz + sizeof(t));
   }

   _close(sPtr);
   sPtr = -1;
   delete fields;
   delete recBuf;
}

long ReverceLong( long val )
{
   union ts
   {
      long val;
      char s[4];
   } t;

   t.val = val;
   char tc = t.s[0];
   t.s[0] = t.s[3];
   t.s[3] = tc;
   tc = t.s[2];
   t.s[2] = t.s[1];
   t.s[1] = tc;

   return t.val;
}

int ReverceInt( int val )
{
   union ts
   {
      int val;
      char s[2];
   } t;

   t.val = val;
   char tc = t.s[1];
   t.s[1] = t.s[0];
   t.s[0] = tc;
   return t.val;
}

void DataForm::WriteFieldsRef()
{
   //  long sPos = _tell( sPtr );
   _lseek( sPtr, sizeof(DBHead), SEEK_SET );
   _write( sPtr, fields, sizeof(DBField)*numField );
   //  _lseek( sPtr, sPos, SEEK_SET );
}

// Compress.cpp: определяет точку входа для консольного приложения.
//

#include "stdafx.h"
#include "Binary.h"
#include <zlib.h>
#include <io.h>

Binary* Compress(const Binary &srcBuf)
{
   z_stream stream;
 
   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   Binary *destBuf = new Binary();

   deflateInit(&stream, Z_BEST_COMPRESSION);
 
   BYTE *src = (BYTE*)(const char*)srcBuf;
   stream.avail_in = srcBuf.Size();
   stream.next_in = src;
  
   int size = srcBuf.Size() + sizeof(stream.adler);
   if( size < 1000 ) size = 1000;

   BYTE *dest = (BYTE*)destBuf->Alloc(size);
   stream.avail_out = size - sizeof(stream.adler);
   stream.next_out = dest;
 
   bool res = (deflate(&stream, Z_FINISH) == Z_STREAM_END && stream.avail_out != 0);

   DWORD outb = size - stream.avail_out;
   ToStreamBytes(dest + outb, stream.adler);
   destBuf->ReduceSize(outb + sizeof(stream.adler));

   deflateEnd(&stream);

   if( !res )
   {
      delete destBuf;
      destBuf = NULL;
   }
   return destBuf; 
}

int _tmain(int argc, _TCHAR* argv[])
{
   if( argc != 3 )
   {
      wchar_t *p = wcsrchr(argv[0], L'\\');
      if( p ) p++;
      else p = argv[0];
      wprintf(L"%s file dest", p);
      return 1;
   }

   FILE *rd = _wfopen(argv[1], L"rb");
   if( rd == NULL )
      return 1;

   int ret = 1;
   long len = _filelength(_fileno(rd));
   Binary src;
   BYTE *pb = src.Alloc(len);
   if( fread(pb, sizeof(BYTE), len, rd) == len )
   {
      Binary *dest = Compress(src);
      if( dest != NULL )
      {
         FILE *wr = _wfopen(argv[2], L"wb");
         if( wr != NULL )
         {
            fwrite((const BYTE*)(*dest), sizeof(BYTE), dest->Size(), wr);
            fclose(wr);
            ret = 0;
         }
      }
   }
   fclose(rd);
	return ret;
}


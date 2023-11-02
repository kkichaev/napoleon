/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * CompressWriter & Decompress
 *
 *  ert   02/08/2008   creating
 */ 
#include "stdafx.h"
#include <Compress.h>
#include <Binary.h>

Compressor::Compressor(StreamWriter *writer) : flushed(false)
{
   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   buf = (BYTE*)malloc(STREAM_BUF_SIZE);

   deflateInit(&stream, Z_DEFAULT_COMPRESSION);
   stream.next_in = NULL;
   stream.avail_in = 0;

   stream.next_out = buf;
   stream.avail_out = STREAM_BUF_SIZE;

   this->writer = writer;
}

Compressor::~Compressor()
{
   deflateEnd(&stream);
   delete buf;
}

bool Compressor::WriteCompress(void *p, int size)
{
   stream.next_in = (Bytef*)p;
   stream.avail_in = size;
   
   do
   {
      if( deflate(&stream, Z_NO_FLUSH) != Z_OK )
         return false;
   
      if( stream.avail_out == 0 )
      {
         writer->Write(buf, STREAM_BUF_SIZE);
         stream.avail_out = STREAM_BUF_SIZE;
         stream.next_out = buf;
      }
   } while( stream.avail_in != 0 );
   return true;
}  

bool Compressor::Flush()
{
   if( flushed ) return true;

   while(true)
   {
      int result = deflate(&stream, Z_FINISH);
      if( result == Z_STREAM_END )
      {
         int count = STREAM_BUF_SIZE - stream.avail_out;
         if( count )
            writer->Write(buf, count);
         break;
      }
      if( result != Z_OK ) // error
         return false;
         
      writer->Write(buf, STREAM_BUF_SIZE);
      
      stream.avail_out = STREAM_BUF_SIZE;
      stream.next_out = buf;
   }
   writer->Write(&stream.adler, sizeof(stream.adler));
   flushed = true;

   return true;
}

bool DecompressFile(FILE *srcFile, FILE *destFile, DWORD srcSize)
{
   z_stream stream;
   BYTE *src, *dest;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   inflateInit(&stream);

   src = (BYTE*)malloc(STREAM_BUF_SIZE);
   stream.avail_in = 0;
   stream.next_in = src;

   dest = (BYTE*)malloc(STREAM_BUF_SIZE);
   stream.avail_out = STREAM_BUF_SIZE;
   stream.next_out = dest;

   int ep;
   if( srcSize == 0 )
   {
      fflush(srcFile);
      fseek(srcFile, 0, SEEK_END);
      srcSize = ftell(srcFile) - sizeof(stream.adler);
      fseek(srcFile, 0, SEEK_SET);
      ep = srcSize;
   } else
   {
      srcSize -= sizeof(stream.adler);
      ep = srcSize + ftell(srcFile);
   }

   int ec = Z_OK;
   bool done   = false, error = false;
   DWORD adler;

   while( ec != Z_STREAM_END )
   {
      if( stream.avail_in == 0 )
      {
         int cp = ftell(srcFile);
         if( cp >= (int)ep )
            done = true;
         else
         {
            int readSize = (cp + STREAM_BUF_SIZE < ep) ? STREAM_BUF_SIZE : ep - cp;
            int len = fread(src, 1, readSize, srcFile);
            stream.avail_in = len;
            stream.next_in = src;
         }
      }

      ec = inflate(&stream, Z_NO_FLUSH);
      if( ec != Z_OK && ec != Z_STREAM_END )
      {
         error = true;
         break;
      }
      fwrite(dest, STREAM_BUF_SIZE - stream.avail_out, 1, destFile);
      stream.avail_out = STREAM_BUF_SIZE;
      stream.next_out = dest;
   }

   if( !error )
      fread(&adler, sizeof(adler), 1, srcFile);

   free(dest);
   free(src);
   inflateEnd(&stream);
   if( srcSize != 0 )
      adler = FromStreamBytes((BYTE*)&adler);

   return (error) ? false : (adler == stream.adler);
}

FILE* Decompress(const char *srcName, const char *destName)
{
   FILE *file = fopen(srcName, "rb"), *dest = fopen(destName, "w+b");
   if( file == NULL || dest == NULL || DecompressFile(file, dest) == false )
   {
      if( file ) fclose(file);
      if( dest ) fclose(dest);

      return NULL;
   }
   fclose(file);
   return dest;
}


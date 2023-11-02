/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Packet operations.
 *
 * ert   20/04/2009   creating
 */
#include "stdafx.h"
#include <zlib.h>
#include <packet.h>

using namespace GRServer;
using namespace std;

bool GRServer::Decompress(Binary *destBuf, Binary &srcBuf, DWORD destSize)
{
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   inflateInit(&stream);

   BYTE *src = (BYTE*) ((const char*)srcBuf);
   stream.avail_in = srcBuf.Size();// - sizeof(stream.adler);
   stream.next_in = src;

   BYTE *dest = (BYTE*)destBuf->Alloc(destSize);
   stream.avail_out = destSize;
   stream.next_out = dest;

   //DWORD adler = FromStreamBytes(src + srcBuf.Size() - sizeof(stream.adler));

   bool retval = (inflate(&stream, Z_NO_FLUSH) == Z_STREAM_END);

   destBuf->ReduceSize(destSize - stream.avail_out);

   inflateEnd(&stream);

   //return (retval) ? (stream.adler == adler) : false;
   return retval;
}

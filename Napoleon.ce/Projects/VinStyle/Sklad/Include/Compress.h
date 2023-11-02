/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * CompressWriter & Decompress
 *
 *  ert   02/08/2008   creating
 */
#ifndef __COMPRESS_WRITER_H
#define __COMPRESS_WRITER_H

#include <Streamer.h>
#include <zlib.h>

class Compressor
{
 public:
   Compressor(StreamWriter *writer);
   ~Compressor();

   bool WriteCompress(void *p, int size);
   long Position() const { return stream.total_in; }

   bool Flush();

protected:
   z_stream stream;
   bool flushed;

   BYTE *buf;

   StreamWriter *writer;
};

class CompressWriter : public StreamWriter, public Compressor
{
public:
   CompressWriter() : Compressor(&writer) {}

   virtual bool Write(void *p, int size) { return WriteCompress(p, size); }

   virtual long CurrentPos() const { return Position(); }

   virtual int Size() const
   {
      const Compressor *c = (const Compressor*)this;
      if( !const_cast<Compressor*>(c)->Flush() )
         return 0;
      return writer.Size();
   }

   virtual bool ToBytes(BYTE *array) const { return writer.ToBytes(array); }

private:
   StreamWriter writer;
};

class FileCompressWriter : public StreamWriter, public Compressor
{
 public:
   FileCompressWriter(FILE *f) : Compressor(&writer), writer(f) {}

   void Close() { writer.Close(); }

   FILE* Stream() const { return writer.Stream(); }

   virtual bool Write(void *p, int size) { return WriteCompress(p, size); }

   virtual long CurrentPos() const { return Position(); }

   virtual int Size() const
   {
      const Compressor *c = (const Compressor*)this;
      if( !const_cast<Compressor*>(c)->Flush() )
         return 0;
      return writer.Size();
   }

   virtual bool ToBytes(BYTE *array) const { return writer.ToBytes(array); }

private:
   FileWriter writer;
};

bool DecompressFile(FILE *src, FILE *dest, DWORD size = 0);
FILE* Decompress(const char *srcName, const char *destName);

#endif

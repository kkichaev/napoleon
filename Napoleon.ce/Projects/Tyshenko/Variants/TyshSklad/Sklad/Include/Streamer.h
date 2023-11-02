/*
* Copyright (C), 2007, ƒенис ћос€гин
*
* —ериализаци€ IReflectableData
*
* ert   23/07/2007   creating
*/ 

#ifndef __STREAMER_H
#define __STREAMER_H

// page size (4K - pointer)
#define STREAM_BUF_SIZE 4092

#include <stdio.h>
#include <StringHolder.h>
//
// ѕри десериализации строки не создаютс€ используютс€ "живые" указатели в потоке
//

class StreamReader
{
 public:
   StreamReader(const BYTE *buffer, int size);

   virtual bool Read(void *p, int size) const;

   virtual const wchar_t* GetString(int length) const
   {
      const wchar_t *cp = (const wchar_t *)readPos;

      if( length > readSize ) length = readSize;
      readPos += length;
      readSize -= length;
      return cp;
   }

   virtual long CurrentPos() const { return (long)readPos; }

   // очистить все промежуточные буфера
   virtual void ClearBuffers() {}

 protected: 
   mutable const BYTE *readPos;
   mutable int readSize;
};

class FileReader : public StreamReader
{
public:
   FileReader(FILE *_file) : StreamReader(NULL, 0), file(_file) {}

   virtual bool Read(void *p, int size) const { return fread(p, size, 1, file) == 1; }

   virtual const wchar_t* GetString(int length) const
   {
      wchar_t *buf = (wchar_t*)malloc(length), *cp;
      *buf = L'\0';
      fread(buf, length, 1, file);
      cp = sh.Add(buf);
      free(buf);
      return cp;
   }

   virtual long CurrentPos() const { return ftell(file); }

   virtual void ClearBuffers() { sh.Clear(); }

 protected: 
    FILE *file;
    mutable StringHolder sh;
};

class StreamWriter
{
 public:
   // инициализаци€ дл€ записи
   StreamWriter();
   virtual ~StreamWriter();

   // записать size байт в буфер
   virtual bool Write(void *p, int size);

   virtual long CurrentPos() const { return tailBlockSize; }

   // количество байт в потоке
   virtual int Size() const;

   // скопировать информацию в array sizeof(array) == Size()
   virtual bool ToBytes(BYTE *array) const;

 protected: 
   // освободить всю пам€ть
   void FreeBlocks();

   struct MemoryBlock
   {
      MemoryBlock *next;
      BYTE buffer[STREAM_BUF_SIZE];

      MemoryBlock() { next = NULL; }

      // chain memory blocks
      MemoryBlock(MemoryBlock *prev) { next=NULL; prev->next = this; }
   };

   MemoryBlock *head, *current;
   unsigned short tailBlockSize;
};

class FileWriter : public StreamWriter
{
public:
   // use вместе с Attach
   FileWriter() : wr(NULL) {}

   FileWriter(FILE *_wr) : wr(_wr) { fseek(wr, 0, SEEK_SET); }
   ~FileWriter() {}

   // работает с конструктором без параметров
   void Attach(FILE *_wr) { wr = _wr; }

   void Close() { if( wr != NULL ) fclose(wr); }

   FILE* Stream() const { return wr; }

   // записать size байт в буфер
   virtual bool Write(void *p, int size) { return (fwrite(p, size, 1, wr) == 1); }

   virtual long CurrentPos() const { return ftell(wr); }

   // количество байт в потоке
   virtual int Size() const { return ftell(wr); }

   // скопировать информацию в array sizeof(array) == Size()
   virtual bool ToBytes(BYTE *array) const
   { 
      int size = Size();
      fseek(wr, 0, SEEK_SET); 
      return (fread(array, size, 1, wr) == 1);
   }

protected:
   FILE *wr;
};

#endif

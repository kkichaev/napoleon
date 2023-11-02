/*
* Copyright (C), 2007, Денис Мосягин
*
* Сериализация IReflectableData
*
* ert   23/07/2007   creating
*/ 

#include "stdafx.h"
#include <Streamer.h>

StreamReader::StreamReader(const BYTE *buffer, int size)
{
   readPos = buffer;
   readSize = size;
}

bool StreamReader::Read(void *p, int size) const
{
   int cs;
   bool retVal = true;

   if( readSize < size )
   {
      cs = readSize;
      retVal = true;
   } else
      cs = size;

   memmove(p, readPos, cs);
   readPos += cs;
   readSize -= cs;

   return retVal;
}

StreamWriter::StreamWriter()
{
   head = NULL;
   current = NULL;
   tailBlockSize = 0;
}

StreamWriter::~StreamWriter()
{
   FreeBlocks();
}

bool StreamWriter::Write(void *p, int size)
{
   if( head == NULL )
   {
      head = new MemoryBlock();
      current = head;
   }

   BYTE *src = (BYTE*)p;
   while( true )
   {
      int blockSpace = sizeof(current->buffer) - tailBlockSize;
      if( blockSpace >= size )
      {
         memmove(current->buffer + tailBlockSize, src, size);
         tailBlockSize += size;
         return true;
      }

      memmove(current->buffer + tailBlockSize, src, blockSpace);
      src += blockSpace;
      size -= blockSpace;
      current = new MemoryBlock(current);
      tailBlockSize = 0;
   }
}

int StreamWriter::Size() const
{
   if( head == NULL ) return 0;
   int curSize = 0;

   MemoryBlock *p = head;
   while( true )
   {
      if( p->next == NULL )
      {
         curSize += tailBlockSize;
         break;
      }
      curSize += sizeof(p->buffer);
      p = p->next;
   }

   return curSize;
}

bool StreamWriter::ToBytes(BYTE *array) const
{
   if( head == NULL ) return true;

   BYTE *dest = array;
   MemoryBlock *p = head;

   while( true )
   {
      if( p->next == NULL )
      {
         memmove(dest, p->buffer, tailBlockSize);
         break;
      }
      memmove(dest, p->buffer, sizeof(p->buffer));
      dest += sizeof(p->buffer);
      p = p->next;
   }   

   return true;
}

void StreamWriter::FreeBlocks()
{
   while( head != NULL )
   {
      MemoryBlock *p = head->next;
      delete head;
      head = p;
   }

   tailBlockSize = 0;
}

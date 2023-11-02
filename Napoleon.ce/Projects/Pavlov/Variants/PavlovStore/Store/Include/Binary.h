/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Двоичный объект
 *
 * ert   16/09/2009   creating
 */
#ifndef __GR_BINARY_H
#define __GR_BINARY_H

#include <string>
#include <vector>
#include <C32.h>

class Binary
{
public:
   Binary(BYTE *buf, DWORD cb) { this->buf = buf; this->cb = cb; totalSize = cb; }
   Binary() : buf(NULL), cb(0) {}

   ~Binary() { free(buf); }

   BYTE* Alloc(DWORD cb)
   {
      free(buf);
      buf = (BYTE*)malloc(cb);
      this->cb = (buf!=NULL) ? cb : 0;
      totalSize = cb;

      return buf;
   }

   // in bytes
   DWORD Size() const { return cb; }

   void ReduceSize(DWORD newSize) { cb = newSize; }

   bool IsEmpty() const { return (cb == 0); }

   operator const BYTE* () const { return buf; }
   operator const char* () const { return (const char*)buf; }
   operator const wchar_t* () const { return (const wchar_t*)buf; }
   operator const unsigned short* () const { return (const unsigned short*)buf; }

   void Clear()
   {
      free(buf);
      buf = NULL;
      cb = 0;
   }

protected:
   BYTE *buf;
   DWORD cb, totalSize;
};

struct IBinary
{
   virtual ~IBinary() {}

   // указатель на данные "отдается" т.е. его не надо удалять. Его удаляет IBinary
   virtual void Assign(Binary* b) = 0;

   virtual DWORD Size() const = 0;
   virtual const BYTE* Bytes() const = 0;

   virtual void Close() = 0;
};

inline void ToStreamBytes(BYTE *dest, DWORD value)
{
   BYTE* pval = (BYTE*)&value + 3;
   *dest++ = *pval--;
   *dest++ = *pval--;
   *dest++ = *pval--;
   *dest = *pval;
}

inline DWORD FromStreamBytes(BYTE* value)
{
   return ((DWORD)*value << 24) | ((DWORD)*(value+1) << 16) | ((DWORD)*(value+2) << 8) | ((DWORD)*(value + 3));
}

inline unsigned CRC32(const Binary& src)
{
   return (CRC32(src, src.Size()) ^ 0xFFFFFFFF);
}


#endif


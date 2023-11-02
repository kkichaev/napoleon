/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * AES шифрование + CRC32
 *
 * ert   15/10/2009   creating
 */
#ifndef __GRSERVER_AES_H
#define __GRSERVER_AES_H

#include <Binary.h>

// используем 256 битный AES
typedef char Key[32];

Binary* AESEncode(const Binary& src, const Key key);

// len (in, out)
// при вызове - длина src, на выходе длина dest
Binary* AESDecode(const unsigned char* src, unsigned len, const Key key);

inline Binary* AESDecode(const Binary& srcB, const Key key)
{
   return AESDecode(srcB, srcB.Size(), key);
}


bool AESDecodeFile(FILE* dest, FILE* src, Key key);

void AESFree();

#endif
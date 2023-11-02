/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * CRC32
 *
 * ert   04/12/2009   creating
 */
#ifndef __CRC32_H
#define __CRC32_H

void CRC32_Init();

unsigned CRC32(const unsigned char* src, unsigned size, unsigned intcrc = 0xFFFFFFFF);

inline unsigned CRC32(unsigned char sym, unsigned crc)
{
   extern unsigned crc_tab[256];
   return ((crc >> 8) & 0x00FFFFFF) ^ crc_tab[(crc ^ sym) & 0xFF];
}

#endif
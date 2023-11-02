/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * CRC32
 *
 * ert   04/12/2009   creating
 */
#include "stdafx.h"
#include "C32.h"

unsigned CRC32(const unsigned char* src, unsigned size, unsigned crc)
{
   CRC32_Init();

   for( unsigned i=0; i<size; i++ )
   {
      crc = CRC32(*src++, crc);
   }

   return crc;
}

unsigned crc_tab[256];

static void crc32gentab()
{
   unsigned crc, poly;
   int i, j;

   poly = 0xEDB88320L;
   for (i = 0; i < 256; i++)
   {
      crc = i;
      for (j = 8; j > 0; j--)
      {
         if (crc & 1)
         {
            crc = (crc >> 1) ^ poly;
         }
         else
         {
            crc >>= 1;
         }
      }
      crc_tab[i] = crc;
   }
}

void CRC32_Init()
{
   if( crc_tab[1] == 0 ) crc32gentab();
}

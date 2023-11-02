/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Packet options
 *
 * ert   20/04/2009   creating
 */ 
#ifndef __GRPACKET_H
#define __GRPACKET_H

#include <Binary.h>
/*
   Заголовок пакета передается в кодировке ANSI только латинскими буквами
   тело пакета (DATA) UTF-16

   GRPACKET(SIZE);[GZIP(SIZE);][ENCODE(AES);]DATA;[DATA]
*/

/*
  типы полей

  s - строка
  n - целое число
  n(N) - число с N знаками после запятой
  b - двоичное число. Размер добавляется до четного байта
  d - дата (2009-09-16)
  t - время (16:28:30)
  dt - дата и время (2009-09-16 16:28:30)

  [Def] - объект с описанием Def
*/
//
//price[name:s,id:s,folder:n,qty:n,picture:b,cost[cost:n],weight:n]["test","0001",12,0:,[12.23][13][12.5],124.5]
//
//
//order[id:s,number:s,date:dt,sum:n,items[id:s,qty:n,cost:n]]["123","0001",2009-07-28 11:35:36,1500,["001",2,200]["002",1,1100]]
//

namespace GRServer {

// packet operations

bool Decompress(Binary *dest, Binary &src, DWORD destSize);
bool Compress(Binary *dest, Binary &src);

} // namespace GRServer

#endif

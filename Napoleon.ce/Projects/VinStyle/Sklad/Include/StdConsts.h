/*
* Copyright (C), 2007, Денис Мосягин
*
* Стандартные константы
*
*  ert   04/12/2007   creating
*/

#ifndef __STD_CONSTS_H
#define __STD_CONSTS_H

//
// Константы масштабирования
//
#define QTY_SCALE 1000
#define SUM_SCALE 100
#define PACK_SCALE 100
#define WEIGHT_SCALE 1000
#define DISCOUNT_SCALE 10
#define VOLUME_SCALE 10000

#ifndef GPS_SCALE_DEFINED
#define GPS_SCALE_DEFINED

const DWORD GPS_SCALE = 100000;
const DWORD GPS_SPEED_SCALE = 100;

#endif

//
// Размеры строк
//
#define MAX_ORG_ID 20
#define MAX_ORG_NAME 100

#define MAX_ITEM_ID 20
#define MAX_ITEM_NAME 100

#define MAX_FOLDER_NAME 100

#define MAX_LOGIN    30
#define MAX_PASSWORD 30

#define SEP_SYM         ';'

#endif

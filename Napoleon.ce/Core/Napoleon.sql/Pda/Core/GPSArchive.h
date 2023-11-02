/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Работа с архивом GPS данных
 *
 *  ert   25/08/2009   creating
 */ 
#ifndef __GPS_ARCHIVE_H
#define __GPS_ARCHIVE_H

#include <Apps.h>

const int GPS_ARCHIVE_SIZE = 7; // размер архива в днях

struct SendPacketParam;
class GPSArchive
{
public:
   static void AddCurrent(const Location &data);

   static int SerializeCurrent(SendPacketParam *sendParam);

   static int SerializeArchive(SendPacketParam *sendParam, WORD dayInterval);

   static void MoveCurrentToArchive(WORD archiveInterval);

};

#endif
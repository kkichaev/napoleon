/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Параметры синхронизации
 *
 *  ert   01/08/2007   creating
 */ 
#ifndef __SYNC_FORMAT_H
#define __SYNC_FORMAT_H

#include <Reflection.h>

// код для Sync Request
#define SYNC_PREFERENCE 1

class DataForm;
class StringHolder;
struct DBRec;

struct SyncFormat
{
   // имя типа для Reflection
   virtual const wchar_t* TypeName() const = 0;

   virtual const wchar_t* KeyField() const = 0;

#ifdef UNDER_CE

   virtual const wchar_t* FileName() const = 0;

   virtual bool Serialize(StreamWriter *writer, const IReflectableData &data) const
   {
      return data.GetType().Serialize(writer, data);
   }

   virtual bool Deserialize(IReflectableData *data, const StreamReader &reader) const
   {
      return data->GetType().Deserialize(data, reader);
   }

#else // UNDER_CE
   SyncFormat(const char *userID) { this->userID = userID; }

   const char *userID;

   virtual const char* FileName() const = 0;

   virtual const char* AltFileName() const = 0;

   // ansi <-> oem check
   virtual bool SetFromDB(IReflectableData *data, const DataForm &db, StringHolder *sh) const = 0;

   // ansi <-> oem check
   virtual bool SetToDB(DataForm *db, const IReflectableData &data) const = 0;

   virtual DBRec* BaseHeader(int *count) const = 0;
#endif // UNDER_CE
};


#endif


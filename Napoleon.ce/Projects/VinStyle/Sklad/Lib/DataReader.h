/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Чтение из потока в IReflectableData
 * 
 *  ert   25/09/2009   creating
 */
#ifndef __DATA_READER_H
#define __DATA_READER_H

#include <Reflection.h>
#include "NetExchange.h"

struct IReaderElement
{
   virtual ~IReaderElement() {}
   virtual bool Read(IReflectableData* data, ReceivedStream* stream) const = 0;
};

struct IBinaryWriter
{
   // Writer не удаляется вместе с BinaryReader его удалять нужно вручную
   virtual ~IBinaryWriter() {}

   // size может быть не четный, но читать надо всегда полное слово
   virtual bool Write(IReflectableData* data, ReceivedStream* stream, DWORD size) = 0;
};

typedef IBinaryWriter* (*GetBinaryWriter)(const wchar_t* fieldName);

class DataReader : public std::vector<IReaderElement*>
{
public:
   DataReader();
   ~DataReader();

   bool Read(IReflectableData* data, ReceivedStream* stream) const;

   static DataReader* DataReader::CreateReader(const DataReflector& type, ReceivedStream *stream, GetBinaryWriter gbw = NULL);

protected:
};

class BinaryFileWriter : public IBinaryWriter
{
public:
   BinaryFileWriter();
   virtual ~BinaryFileWriter();

   virtual void GetFileName(std::wstring* fileName) = 0;
   virtual void AfterWrite(IReflectableData* data, const std::wstring& fileName) {}
   virtual bool Write(IReflectableData* data, ReceivedStream* stream, DWORD size);

};

#endif
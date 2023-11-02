/*
* Copyright (C), 2007, Денис Мосягин
*
* Интерфейсы отражения данных и коллекции данных
*
* ert   01/04/2007   creating
* ert   23/07/2007   updating
*/ 

#ifndef _I_DATA_REFLECTION_H
#define _I_DATA_REFLECTION_H

#include <EventHolder.h>
#include <Streamer.h>
#include "OutStream.h"

struct IReflectableData;
class OutStream;

//
// формат типа
//
struct TypeFormat {};

//
// тип поля данных
//
struct MemberType
{
   enum DataTypes {String, Short, UShort, Integer, Unsigned, Long, ULong, Double, Float, DateTime, Collection, Parent, UserType, Int64 };

   MemberType(DataTypes type, const wchar_t *name, short size, short offset)
   {
      this->type = type; 
      this->name = name;
      this->size = size;
      this->offset = offset;
   }

   virtual ~MemberType() {}

   const wchar_t* name;
   short type; // DataTypes + user types
   short size;
   short offset;
   
   bool operator == (const MemberType &src) const
   {
      return (type == src.type && wcscmp(name, src.name) == 0 && size == src.size && offset == src.offset);
   }

   bool operator != (const MemberType &src) const { return !operator==(src); }

   // для элемента типа Collection src это IDataCollection*. Копируем содержимое
   virtual void  SetValue(IReflectableData *data, const void *src) const
   { 
      memcpy((char*)data + offset, src, size); 
   }

   // для элемент типа Collection возвращает IDataCollection*
   virtual void* GetValue(const IReflectableData &data) const
   { 
      return (char*)&data + offset; 
   }

   // установка формата для вывода в строку, если формат не установлен,
   // то вывод осуществляется в формате по умолчанию
   virtual void SetFormat(const TypeFormat &format) {}

   virtual void ToString(const IReflectableData &data, wchar_t *buf, int cch) const = 0;

   virtual void ToStream(OutStream* stream) const {}
   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const {}

   // загоняем в поток
   virtual bool Serialize(StreamWriter *streamer, const IReflectableData &data) const
   {
      return streamer->Write(GetValue(data), size);
   }

   // загружаем из потока
   virtual bool Deserialize(IReflectableData *data, const StreamReader &streamer) const
   {
      return streamer.Read((BYTE*)data + offset, size);
   }
};

//
// вернуть описатель типа
//
struct DataReflector;
const DataReflector& GetTypeReflector(const wchar_t *typeName);
const DataReflector* FindTypeReflector(const wchar_t *typeName);
void RemoveTypeReflector(const wchar_t *typeName);

//
// описание типа данных и возмоность получение, установки полей через отражение
//
struct DataReflector : public std::vector<MemberType*>
{
   typedef IReflectableData* (*TypeCreator)();

   // не делает копии ддля namе, имя должно существовать все время жизни DataReflector
   DataReflector(TypeCreator creator, const wchar_t *name) { this->creator = creator; this->name = name; }

   ~DataReflector();

   // создать экземпляр данных
   IReflectableData* Create() const { return creator(); }

   bool operator < (const DataReflector& src) const { return (wcscmp(name, src.name) < 0); }
   bool operator == (const DataReflector &src) const;
   bool operator != (const DataReflector &src) const { return !operator==(src); }

   // количество полей
   int Count() const;

   bool Serialize(StreamWriter *streamer, const IReflectableData &data) const;
   bool Deserialize(IReflectableData *data, const StreamReader &streamer) const;

   void ToStream(OutStream* stream, const wchar_t* typeName = NULL) const;
   void DataToStream(OutStream* stream, const IReflectableData& data) const;

   // получить тип поля
   const MemberType& Type(int index) const;
   const MemberType& Type(const wchar_t *field) const;

   const wchar_t* Name() const { return name; }

   int Find(const wchar_t *field, bool ignoreCase = false) const;

   void AddMember(MemberType *mt);

   void AddString(std::wstring *name) { allocatedStrings.push_back(name); }

protected:
   TypeCreator creator;
   std::vector<std::wstring*> allocatedStrings;
   const wchar_t *name;
};

//
// данные, у которых можно получить тип. Деструктор для полученного типа не вызывается
//
struct IReflectableData
{
   virtual ~IReflectableData() {}

   virtual const DataReflector& GetType() const = 0;
};


//
// интерфейс коллекции данных. коллекция оперирует с IReflectableData
//
struct IDataCollection
{
   virtual const DataReflector& DataType() const = 0;

   virtual int Count() const = 0;

   // ------ методы коллекции (получить, добавить, удалить, обновить) -------
   virtual bool Get(IReflectableData* data, int index) const = 0;

   virtual bool Add(const IReflectableData& data, int index) = 0;

   virtual bool Remove(int index) = 0;

   virtual bool Update(const IReflectableData& data, int index) = 0;

   virtual IReflectableData* GetItem(int index) = 0; // return reference

   virtual void Clear() = 0;

};

//
// для работы с базой данных
//
struct IDBFormat
{
   IDBFormat(const wchar_t *typeName, const wchar_t *pk) : 
      reflector(GetTypeReflector(typeName)), primaryKey(pk) {}

   IDBFormat(const DataReflector &r, const wchar_t *pk) :
      reflector(r), primaryKey(pk) {}

   // индекс поля или -1
   virtual int PrimaryKey() const { return reflector.Find(primaryKey); }

   const wchar_t *KeyName() const { return primaryKey; }

   const DataReflector& DataType() const { return reflector; }

 protected:
   const DataReflector& reflector;
   const wchar_t *primaryKey;
};

//
// rowid для БД
//
typedef __int64 ROWID;

const ROWID NO_ROWID = -1;

//
// формат в БД - это IDBFormat
//
struct IDBTable : public IDataCollection
{
};

//
// интерфейс для индикации прогресса
//
struct IProgressIndicator
{
   virtual void SetText(const wchar_t *text, bool resetPos = true) = 0;
   virtual void SetMax(int max) = 0;
   virtual void SetPos(int pos) = 0;
};

#endif

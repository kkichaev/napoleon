/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Работа с Database на WinCE
 *
 *  ert   20/11/2006   creating
 *  ert   13/04/2007   updating
 *  ert   23/07/2007   updating
 */ 
 
#ifndef _CE_TABLE_H
#define _CE_TABLE_H

#include <Reflection.h>
#include <SyncFormat.h>

const unsigned short DIRTY_FLAG = 1;
const DWORD FLAG_PROPID = 0xFFFF0000 + CEVT_UI2;

class AllocList : public std::vector<void*>
{
public:
   AllocList() {}
   ~AllocList();
};

// hold CEMemberType
class CEDBFormat : public IDBFormat
{
public:
   CEDBFormat(const wchar_t *typeName, const wchar_t *keyField) : IDBFormat(typeName, keyField) {}
   CEDBFormat(const DataReflector &r, const wchar_t *keyField) : IDBFormat(r, keyField) {}
   CEDBFormat(const SyncFormat &_fmt) : IDBFormat(_fmt.TypeName(), _fmt.KeyField()) {}

   // младшее слово - тип, верхнее номер поля
   CEPROPID CEType(WORD index) const { return MAKELONG(CEVT(reflector.Type(index).type), index); }
   CEPROPID CEType(const wchar_t *field) const 
   { 
      if( field == NULL || *field == L'\0' ) return 0;

      int index = reflector.Find(field);
      return (index<0) ? 0 : CEType(index); 
   }

   static WORD CEVT(short type);

   void SetProp(CEPROPVAL *prop, const IReflectableData &data, AllocList *allocated) const
   {
      WORD i = 0, countType = (WORD)reflector.Count();
      for( ;i != countType; i++ )
         SetProp(prop+i, data, i, allocated);
   }

   bool SetProp(CEPROPVAL *prop, const IReflectableData &data, const wchar_t *field, AllocList *allocated) const
   {
      int index = reflector.Find(field);
      if( index < 0 )
         return false;

      return SetProp(prop, data, index, allocated);
   }

   bool SetProp(CEPROPVAL *prop, const IReflectableData &data, int index, AllocList *allocated) const;

   void SetData(IReflectableData *data, CEPROPVAL *prop, int propCount) const;
};


//
// если в таблице есть первичный ключ, при сохранении записи добавляется поле флагов
//
struct IConvertor;
class CETable : public IDBTable
{
public:
   CETable(const IDBFormat& _format) : 
      format((const CEDBFormat&)_format), handle(INVALID_HANDLE_VALUE), 
      current(NULL), id(0), curTag(0), cFields(0)
      {
      }

   ~CETable()
   {
      Close();
   }

   static void Flush(PCEGUID pceguid = NULL); // flush all volumes 

   bool IsOpened() const { return (handle != INVALID_HANDLE_VALUE); }
   bool Open(const wchar_t *name, CEPROPID tag = 0);
   bool Open(const wchar_t *name, const wchar_t *tag);
   bool Create(const wchar_t *name, DWORD dbType=1, SORTORDERSPEC *sortSpec = NULL, WORD numOrderSpec = 0);
   void Close();

   static bool DeleteDB(const wchar_t *name);

   bool GetInfo(CEDBASEINFO *info) const;

   CEOID SetPos(int index) const;
   bool SetTag(CEPROPID tag, bool canCreate = true, DWORD sortFlag = 0);
   bool SetTag(const wchar_t *tagName, bool canCreate = true, DWORD sortFlag = 0);
   CEOID Seek(const CEPROPVAL &prop, DWORD seekType = CEDB_SEEK_VALUEFIRSTEQUAL) const; // поиск

   // поиск по полю
   CEOID Seek(const IReflectableData &data, int index, DWORD seekType = CEDB_SEEK_VALUEFIRSTEQUAL) const;
   CEOID Seek(const IReflectableData &data, const wchar_t *field, DWORD seekType = CEDB_SEEK_VALUEFIRSTEQUAL) const;

   CEOID MoveNext(bool next) const; // перемещение вперед/назад
   bool  Seek(CEOID oid) const
   { 
      ATLASSERT(handle != INVALID_HANDLE_VALUE); 
      return (CeSeekDatabase(handle, CEDB_SEEK_CEOID, oid, NULL) != NULL); 
   }

   bool GetCurrent(IReflectableData* data) const;
   CEOID WriteRecord(const IReflectableData& data, CEOID oid, bool setDirty = true);
   bool RemoveRecord(CEOID id);

   // --------------- IDataCollection impl. ------------------------------
   virtual const DataReflector& DataType() const { return format.DataType(); }

   virtual int Count() const;

   virtual bool Get(IReflectableData* data, int index) const;
   virtual IReflectableData* GetItem(int index) { return NULL; }

   // добавление только в конец списка
   virtual bool Add(const IReflectableData& data, int index) { return Update(data, -1); }

   virtual bool Remove(int index);

   virtual bool Update(const IReflectableData& data, int index);

protected:
   const CEDBFormat &format;

   CEOID id;
   CEPROPID curTag;
   mutable HANDLE handle;
   mutable CEPROPVAL *current;
   mutable WORD cFields;
};

inline WORD CEDBFormat::CEVT(short type)
{
   switch(type)
   {
      case MemberType::String:
         return CEVT_LPWSTR;
      case MemberType::Short:
         return CEVT_I2;
      case MemberType::UShort:
         return CEVT_UI2;
      case MemberType::Integer:
         return CEVT_I4;
      case MemberType::Unsigned:
         return CEVT_UI4;
      case MemberType::Long:
         return CEVT_I4;
      case MemberType::ULong:
         return CEVT_UI4;
      case MemberType::Double:
         return CEVT_R8;
      case MemberType::DateTime:
         return CEVT_FILETIME;
      case MemberType::Collection:
      case MemberType::UserType:
         return CEVT_BLOB;
   }
   return CEVT_BLOB;
}

#endif

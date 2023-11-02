/*
* Copyright (C), 2007, Денис Мосягин
*
* макросы для регистрации описаний
*
* ert   10/07/2007   creating
*/ 

#ifndef _TYPE_HOLDER_H
#define _TYPE_HOLDER_H

#include <MemberTypes.h>

//
// зарегистрировать описатель типа
//
void RegisterTypeReflector(DataReflector *type);

//
// макросы для регистрации описаний
//
class __RegisterHelper
{
public:
   __RegisterHelper( void (*pf)(void) ) { pf(); }
};

#include <stddef.h>
#define SizeOf(_s, _m) sizeof(((_s*)NULL)->_m)

//
// добавляет реализацию интерфейса IReflectableData в описание класса
//
#define DECLARE_TYPE_REFLECTION(_type) \
   static IReflectableData* Creator() { return new _type(); } \
   static const wchar_t* TypeName() { return L ## #_type; } \
   virtual const DataReflector& GetType() const { return GetTypeReflector(TypeName()); }

#define DECLARE_TYPE_REFLECTION2(_type, _typeName) \
   static IReflectableData* Creator() { return new _type(); } \
   static const wchar_t* TypeName() { return _typeName; } \
   virtual const DataReflector& GetType() const { return GetTypeReflector(TypeName()); }

//
// регистрация членов класса
//
#define BEGIN_TYPE_REFLECTION(_type) \
   static void Register ## _type() { \
   DataReflector *dataReflector = new DataReflector(_type::Creator, _type::TypeName());

#define CHAIN_REFLECTION(_type, _baseType) \
   dataReflector->AddMember(new ParentType(_baseType::TypeName()));

#define REGISTER_STRING_MEMBER(_type, _member) \
      dataReflector->AddMember(new StringType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_INTEGER_MEMBER(_type, _member) \
      dataReflector->AddMember(new IntegerType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_SHORT_MEMBER(_type, _member) \
      dataReflector->AddMember(new ShortType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_USHORT_MEMBER(_type, _member) \
      dataReflector->AddMember(new UShortType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_UNSIGNED_MEMBER(_type, _member) \
      dataReflector->AddMember(new UnsignedType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_LONG_MEMBER(_type, _member) \
      dataReflector->AddMember(new LongType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_SHORT_SCALE_MEMBER(_type, _member, _scale) \
      dataReflector->AddMember(new ShortScaleType(L ## #_member, offsetof(_type, _member), _scale));

#define REGISTER_SHORT_SCALE_MEMBER2(_type, _member, _scale, _hideRest) \
      dataReflector->AddMember(new ShortScaleType(L ## #_member, offsetof(_type, _member), _scale, _hideRest));

#define REGISTER_USHORT_SCALE_MEMBER(_type, _member, _scale) \
      dataReflector->AddMember(new UShortScaleType(L ## #_member, offsetof(_type, _member), _scale));

#define REGISTER_USHORT_SCALE_MEMBER2(_type, _member, _scale, _hideRest) \
      dataReflector->AddMember(new UShortScaleType(L ## #_member, offsetof(_type, _member), _scale, _hideRest));
 
#define REGISTER_LONG_SCALE_MEMBER(_type, _member, _scale) \
      dataReflector->AddMember(new LongScaleType(L ## #_member, offsetof(_type, _member), _scale));

#define REGISTER_LONG_SCALE_MEMBER2(_type, _member, _scale, _hideRest) \
      dataReflector->AddMember(new LongScaleType(L ## #_member, offsetof(_type, _member), _scale, _hideRest));

#define REGISTER_ULONG_MEMBER(_type, _member) \
      dataReflector->AddMember(new ULongType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_ULONG_ARRAY_MEMBER(_type, _member) \
      int count_member = SizeOf(_type, _member) / SizeOf(_type, _member[0]); \
      int len_member = wcslen(L ## #_member); \
      int offset_member = offsetof(_type, _member); \
      std::wstring tmp_member(L ## #_member); \
      tmp_member += L"$"; \
      for( int i_member = 0; i_member < count_member; i_member++ ) \
      { \
         wchar_t buf[10]; \
         _itow(i_member, buf, 10); \
         std::wstring *buf_member = new std::wstring(tmp_member); \
         (*buf_member) += buf; \
         dataReflector->AddString(buf_member); \
         dataReflector->AddMember(new ULongType(buf_member->c_str(), offset_member)); \
         offset_member += SizeOf(_type, _member[0]); \
      }

#define REGISTER_ULONG_ARRAY_SCALE_MEMBER(_type, _member, _scale) \
      int count_member = SizeOf(_type, _member) / SizeOf(_type, _member[0]); \
      int len_member = wcslen(L ## #_member); \
      int offset_member = offsetof(_type, _member); \
      std::wstring tmp_member(L ## #_member); \
      tmp_member += L"$"; \
      for( int i_member = 0; i_member < count_member; i_member++ ) \
      { \
         wchar_t buf[10]; \
         _itow(i_member, buf, 10); \
         std::wstring *buf_member = new std::wstring(tmp_member); \
         (*buf_member) += buf; \
         dataReflector->AddString(buf_member); \
         dataReflector->AddMember(new ULongScaleType(buf_member->c_str(), offset_member, _scale)); \
         offset_member += SizeOf(_type, _member[0]); \
      }

#define REGISTER_ULONG_SCALE_MEMBER(_type, _member, _scale) \
      dataReflector->AddMember(new ULongScaleType(L ## #_member, offsetof(_type, _member), _scale));

#define REGISTER_ULONG_SCALE_MEMBER2(_type, _member, _scale, _hideRest) \
      dataReflector->AddMember(new ULongScaleType(L ## #_member, offsetof(_type, _member), _scale, _hideRest));

#define REGISTER_DOUBLE_MEMBER(_type, _member) \
      dataReflector->AddMember(new DoubleType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_FILETIME_MEMBER(_type, _member) \
      dataReflector->AddMember(new DateTimeType(L ## #_member, offsetof(_type, _member)));

#define REGISTER_TIMESTAMP_MEMBER(_type, _member) \
      dataReflector->AddMember(new DateTimeType(L ## #_member, offsetof(_type, _member), DateTimeType::DateTimeFormat::DateTime));

#define REGISTER_TIME_MEMBER(_type, _member) \
      dataReflector->AddMember(new DateTimeType(L ## #_member, offsetof(_type, _member), DateTimeType::DateTimeFormat::Time));

#define REGISTER_COLLECTION_MEMBER(_type, _member, _itemType) \
      dataReflector->AddMember(new CollectionType<_itemType>(L ## #_member, offsetof(_type, _member)));

#define REGISTER_INT64_MEMBER(_type, _member) \
      dataReflector->AddMember(new Int64Type(L ## #_member, offsetof(_type, _member)));

#define END_TYPE_REFLECTION(_type) \
      RegisterTypeReflector(dataReflector); \
   } \
   static __RegisterHelper r ## _type(Register ## _type);


#endif

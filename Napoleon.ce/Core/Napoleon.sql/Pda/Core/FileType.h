/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * FileType Member
 *
 *  ert   25/11/2010   creating
 */
#ifndef __FILE_TYPE_MEMBER_H
#define __FILE_TYPE_MEMBER_H

#include <MemberTypes.h>

struct FileType : public StringType
{
   FileType(const wchar_t *name, short offset);

   virtual void ToStream(OutStream* stream) const;
   virtual void DataToStream(OutStream* stream, const IReflectableData& data) const;
};

#define REGISTER_FILE_MEMBER(_type, _member) \
      dataReflector->AddMember(new FileType(L ## #_member, offsetof(_type, _member)));

#endif
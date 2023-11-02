/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * FileType Member
 *
 *  ert   25/11/2010   creating
 */
#include "stdafx.h"
#include "FileType.h"
#include <OutStream.h>

FileType::FileType(const wchar_t *name, short offset) : StringType(name, offset)
{
}

void FileType::ToStream(OutStream* stream) const
{
   stream->Append(name);
   stream->Append(L":b");
}

void FileType::DataToStream(OutStream* stream, const IReflectableData& data) const
{
   wchar_t* p = *(wchar_t**)GetValue(data);
   FILE *rd = _wfopen(p, L"rb");
   DWORD len = 0;
   BYTE *buf = NULL;
   if( rd != NULL )
   {
      fseek(rd, 0, SEEK_END);
      len = ftell(rd);
      fseek(rd, 0, SEEK_SET);
      if( len > 0 )
      {
         buf = (BYTE*)malloc(len);
         fread(buf, sizeof(BYTE), len, rd);
      }
      fclose(rd);
   }
   stream->Append(buf, len);
   free(buf);
}

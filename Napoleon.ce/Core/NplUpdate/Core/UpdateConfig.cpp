/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Установка обновлений
 *
 * ert   16/03/2010   creating
 */
#include "stdafx.h"
#include "UpdateConfig.h"

UpdateConfig::UpdateConfig()
{
}

bool UpdateConfig::Load(const wchar_t* fileName)
{
   FILE *rd = _wfopen(fileName, L"rb");
   if( rd == NULL ) return false;

   bool err = !ReadString(rd, &category);
   err = (err || !ReadString(rd, &version));

   err = (err || (fread(login, sizeof(login), 1, rd) != 1));
   err = (err || (fread(password, sizeof(password), 1, rd) != 1));

   err = (err || !ReadString(rd, &rootFolder));
   if( !err && (*rootFolder.rbegin() != L'\\') ) rootFolder += L'\\';

   err = (err || !ReadString(rd, &action));

   if( !err )
   {
      WORD size = 0;
      fread(&size, sizeof(size), 1, rd);
      while( size-- > 0 )
      {
         std::wstring val;
         IPData data;
         if( !ReadString(rd, &data.ip) || fread(&data.port, sizeof(data.port), 1, rd) != 1 )
         {
            err = true;
            break;
         }
         address.push_back(data);
      }
   }

   ReadString(rd, &saveVersion);

   err = (err || !AddLoad(rd));

   fclose(rd);
   return !err;
}

bool UpdateConfig::Save(const wchar_t* fileName) const
{
   FILE *wr = _wfopen(fileName, L"wb");
   if( wr == NULL ) return false;

   WriteString(wr, category);
   WriteString(wr, version);

   fwrite(login, sizeof(login), 1, wr);
   fwrite(password, sizeof(password), 1, wr);

   WriteString(wr, rootFolder);
   WriteString(wr, action);

   WORD size = address.size();
   fwrite(&size, sizeof(size), 1, wr);
   for( int i=0; i<size; i++)
   {
      const IPData& data = address.at(i);
      WriteString(wr, data.ip);
      fwrite(&data.port, sizeof(data.port), 1, wr);
   }

   WriteString(wr, saveVersion);

   bool res = AddSave(wr);
   fclose(wr);

   return res;
}

void WriteString(FILE* wr, const std::wstring& str)
{
   WORD ssize = str.size();
   fwrite(&ssize, sizeof(ssize), 1, wr);
   fputws(str.c_str(), wr);
}

bool ReadString(FILE* rd, std::wstring* str)
{
   bool retVal;
   WORD ssize = 0;

   str->clear();
   retVal = (fread(&ssize, sizeof(ssize), 1, rd) == 1);
   if( ssize > 0 )
   {
      wchar_t* buf = (wchar_t*)alloca(ssize * sizeof(wchar_t));
      retVal = (fread(buf, sizeof(wchar_t), ssize, rd) == ssize);
      str->assign(buf, ssize);
   }

   return retVal;
}


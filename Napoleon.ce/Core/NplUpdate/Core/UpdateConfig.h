/*
* Copyright (C), 2007-2010, Денис Мосягин
*
* Update
*
*  ert   16/03/2010   creating
*/
#ifndef __NPL_UPDATE_CONFIG_H
#define __NPL_UPDATE_CONFIG_H

#include <string>
#include <vector>
#include <StdConsts.h>

#define UPDATE_PROGRAM L"NplUpdate.exe"

#define CHECK_UPDATE_ACTION L"check"
#define DO_UPDATE_ACTION    L"update"
#define INTERNAL_ACTION     L"internal"

class UpdateConfig
{
public:
   struct IPData
   {
      std::wstring ip;
      WORD port;
   };

   UpdateConfig();

   bool Load(const wchar_t* fileName);
   bool Save(const wchar_t* fileName) const;

   std::wstring category;
   std::wstring version;

   char login[MAX_LOGIN];
   char password[MAX_PASSWORD];

   std::wstring rootFolder;

   std::wstring action;

   std::vector<IPData> address;
   std::wstring saveVersion;

protected:
   virtual bool AddLoad(FILE *rd) { return true; }
   virtual bool AddSave(FILE* wr) const { return true; }
};

void WriteString(FILE* wr, const std::wstring& str);
bool ReadString(FILE* rd, std::wstring* str);

#endif

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация синхронизации прайс-листа
 *
 *  ert   04/02/2008   creating
 */ 
#include "stdafx.h"
#include <atldef.h>

#include <StringHolder.h>
#include <Exchange.h>
#include <Sync.h>
#include "Server.h"
#include "Config.h"

#include <dbf.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

static bool NeedSend(wchar_t *item)
{
   return (wcscmp(item, IP_W) != 0);
}

void LoadKVData(StreamWriter *writer, const char *fName, const wchar_t *keyName, const char *exchangeFolder)
{
   DataForm base;
   std::string fileName(exchangeFolder);
   fileName += fName;

   std::wstring values;
   USES_CONVERSION;

   if( !base.Open(fileName.c_str()) ) return;
   for( int rc = 0; base.ReadRec(rc); rc++ )
   {
      if( !values.empty() ) values += L';';
      values += A2W_CP(Trunc(base["NAME"]), CP_OEMCP);
      values += L'\t';
      values += A2W_CP(Trunc(base["ID"]), CP_OEMCP);
   }

   KeyValue cs;
   const DataReflector &reflector = cs.GetType();
   cs.key = (wchar_t*)keyName;
   cs.value = (wchar_t*)values.c_str();

   reflector.Serialize(writer, cs);
}

void LoadCostTypes(StreamWriter *writer, const char *exchangeFolder)
{
   LoadKVData(writer, "CTYPE.DBF", COST_TYPE, exchangeFolder);
}

void LoadFirms(StreamWriter *writer, const char *exchangeFolder)
{
   LoadKVData(writer, "FIRMS.DBF", SUPPL_TYPE, exchangeFolder);
}

bool ReadConfig(StreamWriter *writer, const char *exchangeFolder, const char *userID)
{
   NapoleonConfig config;
   KeyValue cs;
   const DataReflector &reflector = cs.GetType();
   USES_CONVERSION;
   
   const std::map<std::string, std::string>& keys = config.Keys();
   std::map<std::string, std::string>::const_iterator i = keys.begin();

   if( keys.size() != 0 )
   {
      for( ;i != keys.end(); i++ )
      {
         wchar_t *key = A2W(i->first.c_str());
         if( NeedSend(key) == false ) continue;

         cs.key = key;
         cs.value = A2W(i->second.c_str());
         reflector.Serialize(writer, cs);
      }
   }

   LoadCostTypes(writer, exchangeFolder);
   LoadFirms(writer, exchangeFolder);
   return true;
}

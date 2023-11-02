/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Отправка прайса
 *
 *  ert   08/08/2007   creating
 */ 
#include "stdafx.h"
#include <atldef.h>
#include <dbf.h>
#include <StringHolder.h>
#include <exchange.h>
#include <sync.h>
#include "Config.h"
#include <time.h>
#include <string>

#include "Server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <set>
#include <VConvert.h>

#include "add.h"

struct SendFoldersData : public ISendData
{
   SendFoldersData(const char *userID) : folder(userID) {}

   virtual const wchar_t *ErrorMessage() const { return L": отсутствуют папки товара"; }
   virtual const wchar_t *CMD() const { return SND_FOLDERS_W; }
   virtual const SyncFormat& Format() const { return folder; }

   virtual bool Prepare() const;

   SyncFolder folder;
};

bool SendFoldersData::Prepare() const
{
   DataForm base;
   std::string fileName(ExchangeFolder());
   fileName += Format().FileName();

   if( base.Open(fileName.c_str()) == false )
   {
      fileName = ExchangeFolder();
      fileName += Format().AltFileName();

      if( base.Open(fileName.c_str()) == false )
      {
         if( FailOnError() ) return false;
         return true;
      }
   }

   PrepareData(base);

   folder.PrepareAltPrice();

   DataForm base2;
   fileName = ExchangeFolder();
   fileName += SECOND_FOLDER;
   if( base2.Open(fileName.c_str()) )
       return PrepareData(base2);

   return true;
}

struct SendOrgsData : public ISendData
{
   SendOrgsData(const char *userID) : org(userID) {}

   virtual const wchar_t *ErrorMessage() const { return L": отсутствуют организации"; }
   virtual const wchar_t *CMD() const { return SND_ORGS_W; }
   virtual const SyncFormat& Format() const { return org; }

   SyncOrg org;
};

struct SendPriceData : public ISendData
{
   SendPriceData(const char *userID) : price(userID) {}

   virtual const wchar_t *ErrorMessage() const { return L": отсутствует товар"; };
   virtual const wchar_t *CMD() const { return SND_PRICE_W; }
   virtual const SyncFormat& Format() const { return price; }

   virtual bool Prepare() const;

   bool PreparePrices();
   SyncPrice price;
};

bool SendPriceData::Prepare() const
{
   DataForm base;
   std::string fileName(ExchangeFolder());
   fileName += Format().FileName();

   if( base.Open(fileName.c_str()) == false )
   {
      fileName = ExchangeFolder();
      fileName += Format().AltFileName();

      if( base.Open(fileName.c_str()) == false )
      {
         if( FailOnError() ) return false;
         return true;
      }
   }

   PrepareData(base);

   price.PrepareAltPrice();

   DataForm base2;
   fileName = ExchangeFolder();
   fileName += SECOND_PRICE;
   if( base2.Open(fileName.c_str()) )
       return PrepareData(base2);

   return true;
}

struct SendFOrgData : public ISendData
{
   SendFOrgData(const char *userID) : format(userID) {}

   virtual const wchar_t *ErrorMessage() const { return NULL; };
   virtual const wchar_t *CMD() const { return SND_FORGS_W; }
   virtual const SyncFormat& Format() const { return format; }
   virtual int MinSendSize() const { return 0xF; }

   SyncFOrg format;
};

static bool NeedSend(const wchar_t *item)
{
   return wcscmp(item, IP_W) != 0;
}

bool ReadConfig(StreamWriter *writer, const char *exchangeFolder)
{
   NapoleonConfig config;
   ConfigSend cs;
   const DataReflector &reflector = cs.GetType();
   USES_CONVERSION;
   
   const std::map<std::string, std::string>& keys = config.Keys();
   std::map<std::string, std::string>::const_iterator i = keys.begin();

   if( keys.size() == 0 ) return false;

   for( ;i != keys.end(); i++ )
   {
      wchar_t *key = A2W(i->first.c_str());
      if( NeedSend(key) == false ) continue;

      cs.key = key;
      cs.value = A2W(i->second.c_str());
      reflector.Serialize(writer, cs);
   }
   return true;
}

bool SendConfig(SOCKET sock, const char *exchangeFolder)
{
   CompressWriter writer;
   if( !ReadConfig(&writer, exchangeFolder) ) return false;

   int len = writer.Size();
   BYTE *bytes = (BYTE*)malloc(len);
   wchar_t cmd[CMD_LENGTH+1];

   writer.ToBytes(bytes);
   wsprintfW(cmd, L"%s %d", SND_CONFIG_W, len);
   
   SendResponse(sock, cmd);
   Send(sock, bytes, len);

   free(bytes);
   return true;
}

bool SendPrice(SOCKET sock, const char *param, const char *exchangeFolder)
{
   WORD dbVer;
   const char *ep = param;
   std::string userID;
   
   DebugMessage("ack price %s FOLDER=\"%s\"", param, exchangeFolder);

   dbVer = (unsigned short)strtol(ep, (char**)&ep, 10);
   if( CheckUser(ep, &ep, exchangeFolder, &userID) == false )
   {
      std::wstring answer(FAIL_RESPONSE);
      answer += L"пользователь неопределен";
      SendResponse(sock, answer.c_str());
      return false;
   }

   if( SendConfig(sock, exchangeFolder) )
      WaitResponse(sock);

   if( SendData(sock, dbVer, SendPriceData(userID.c_str())) == false )
      return false;
   WaitResponse(sock);

   if( SendData(sock, dbVer, SendFoldersData(userID.c_str())) == false )
      return false;
   WaitResponse(sock);

   if( SendData(sock, dbVer, SendOrgsData(userID.c_str())) == false )
      return false;
   WaitResponse(sock);

   if( SendData(sock, dbVer, SendFOrgData(userID.c_str())) )
      WaitResponse(sock);

   SendCustomPrice(sock, dbVer, userID.c_str());

   SendResponse(sock, BYE_CMD_W);
   WaitResponse(sock);
   return true;
}

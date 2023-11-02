/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Отправка остатков
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

struct RemnantsSetData
{
   std::wstring key;
   int qty;

   bool operator < (const RemnantsSetData& _el) const
   {
      return key < _el.key;
   }
};

bool PreparePriceRemnants(const char *user, StreamWriter *writer)
{
   SyncPrice priceFmt(user);
   DataForm base;
   std::string fileName(ExchangeFolder());
   fileName += priceFmt.FileName();

   if( !base.Open(fileName.c_str()) )
   {
      fileName = ExchangeFolder();
      fileName += priceFmt.AltFileName();

      if( base.Open(fileName.c_str()) == false )
         return false;
   }

   const DataReflector &reflector = GetTypeReflector(priceFmt.TypeName());
   Price* priceData = (Price*)reflector.Create();
   std::set<RemnantsSetData> dataSet;
   long rc = 0;
   for( ; base.ReadRec(rc); rc++ )
   {
      StringHolder sh;
      if( priceFmt.SetFromDB(priceData, base, &sh) == false )
         continue;

      RemnantsSetData data;
      data.key = priceData->id;
      data.qty = priceData->qty;

      if( dataSet.find(data) == dataSet.end() )
         dataSet.insert(data);
      rc = base.GetRecNo();
   }

   fileName = ExchangeFolder();
   fileName += SECOND_PRICE;
   DataForm base2;
   if( base2.Open(fileName.c_str()) )
   {
      for( rc = 0; base2.ReadRec(rc); rc++ )
      {
         StringHolder sh;
         if( priceFmt.SetFromDB(priceData, base2, &sh) == false )
            continue;

         RemnantsSetData data;
         data.key = priceData->id;
         data.qty = priceData->qty;

         if( dataSet.find(data) == dataSet.end() )
            dataSet.insert(data);
         rc = base2.GetRecNo();  
      }   
   }
   delete priceData;

   std::set<RemnantsSetData>::const_iterator i = dataSet.begin();
   PriceRemnants data;
   const DataReflector &remnantsReflector = data.GetType();
   for( ; i != dataSet.end(); i++ )
   {
      data.id = (wchar_t*)i->key.c_str();
      data.qty = i->qty;
      remnantsReflector.Serialize(writer, data);
   }
   return true;
}

bool SendPriceRemnants(SOCKET sock, WORD dbVer, const char *user)
{
   CompressWriter writer;
   if( !PreparePriceRemnants(user, &writer) )
   {
      std::wstring answer(FAIL_RESPONSE);
      answer += L": отсутствует товар";
      SendResponse(sock, answer.c_str());

      return false;
   }

   return SendStream(sock, writer, SND_REMNANTS_W, dbVer, 0);
}

bool SendRemnants(SOCKET sock, const char *param, const char *exchangeFolder)
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

   if( !SendPriceRemnants(sock, dbVer, userID.c_str()) )
      return false;

   WaitResponse(sock);

   SendResponse(sock, BYE_CMD_W);
   WaitResponse(sock);
   return true;
}

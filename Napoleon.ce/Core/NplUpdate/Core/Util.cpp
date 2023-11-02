/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Util functions
*
*  ert   09/11/2009   creating
*/
#include "stdafx.h"
#include "Util.h"
#include "NplUpdate.h"
#include "UpdateConfig.h"

bool ReadToken(std::wstring* value, const wchar_t *sp, const wchar_t **ep)
{
   bool quoted = false;
   value->clear();
   while( *sp == ' ' ) sp++;

   for( ; *sp != L'\0'; sp++ )
   {
      if( *sp == L'"' )
      {
         if( quoted )
         {
            quoted = false;
            sp++;
            break;
         }
         quoted = true;
         continue;
      }

      if( *sp == L' ' && !quoted ) break;
      value->append(1, *sp);
   }

   *ep = sp;
   return !quoted;
}

ReceivedStream* Receive(const wchar_t* command, const wchar_t* paramValue, const ProgConfig& config)
{
   NetworkExchange net;
   wchar_t buf1[100], buf2[100];

   ServerCommand cmd;
   cmd.command = (wchar_t*)command;

   cmd.param = (wchar_t*)paramValue;

   int len = mbstowcs(buf1, config.login, sizeof(config.login));
   buf1[len] = '\0';
   cmd.userid = buf1;

   len = mbstowcs(buf2, config.password, sizeof(config.password));
   buf2[len] = '\0';
   cmd.password = buf2;

   cmd.version = (wchar_t*)config.version.c_str();

   cmd.duration = 0;

   net.SetTimeout(NETWORK_TIMEOUT * 10);

   if( config.address.size() == 0 )
      return false;

   IPAddress ip1, ip2, *pip2 = NULL;

   const UpdateConfig::IPData &data = config.address.at(0);
   ip1.ip = data.ip;
   ip1.port = data.port;

   if( config.address.size() > 1 )
   {
      pip2 = &ip2;

      const UpdateConfig::IPData &data2 = config.address.at(1);
      ip2.ip = data2.ip;
      ip2.port = data2.port;
   }
   
   return net.Receive(&ip1, pip2, cmd, NULL, false);
}

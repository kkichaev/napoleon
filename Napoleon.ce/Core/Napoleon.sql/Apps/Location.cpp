/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Napoleon Apps RIL
 * 
 *  ert   10/09/2010   creating
 */ 
#include "stdafx.h"
#include <string>
#include <map>
#include "AppsModule.h"
#include <winsock2.h>
#include <ws2tcpip.h>
#include <Network.h>

const DWORD _RCV_TIMEOUT = 60 * 1000;
const char HTTP_PORT = 80;
const char CONTENT_LENGTH[] = "Content-Length";
const char GET[] = "GET ";
const char HTTP_VER[] = " HTTP/1.1\r\n";
const char HOST[] = "Host: ";

const int MAX_CACHE_SIZE = 1000; 
class LocationCache
{
public:
   LocationCache() {}

   struct Key
   {
      DWORD lac;
      DWORD cell;

      Key() : lac(0), cell(0) {}
      Key(DWORD l, DWORD c) { cell = c; lac = l; }
      Key(const Key& src) : lac(src.lac), cell(src.cell) {}

      bool operator< (const Key& src) const
      {
         if( lac < src.lac ) return true;
         if( lac > src.lac ) return false;

         return (cell < src.cell); 
      }
   };

   struct Value
   {
      int lon;
      int lat;

      Value() : lon(0), lat(0) {}
      Value(int ln, int lt) { lon = ln; lat = lt; }
      Value(const Value& src) : lon(src.lon), lat(src.lat) {}
   };

   bool Get(int* lon, int* lat, DWORD lac, DWORD cell) const;
   void Add(int lon, int lat, DWORD lac, DWORD cell);

protected:
   std::map<Key, Value> cache;

} locationCache;

bool LocationCache::Get(int* lon, int* lat, DWORD lac, DWORD cell) const
{
   std::map<Key, Value>::const_iterator fnd = cache.find(Key(lac, cell));
   if( fnd != cache.end() )
   {
      *lon = fnd->second.lon;
      *lat = fnd->second.lat;
      return true;
   }

   return false;
}

void LocationCache::Add(int lon, int lat, DWORD lac, DWORD cell)
{
   if( cache.size() >= MAX_CACHE_SIZE )
      cache.erase(cache.begin());

   cache[Key(lac, cell)] = Value(lon, lat);
}

static bool IsSuccess(const char *msg)
{
   while( !isspace(*msg) ) msg++;
   while( !isdigit(*msg) ) msg++;

   return (*msg == '2');
}

static DWORD ContenLength(const char* p, const char** ep)
{
   DWORD len = 0;
   p = strstr(p, CONTENT_LENGTH);
   if( p != NULL )
   {
      p += sizeof(CONTENT_LENGTH);
      while( !isdigit(*p) ) p++;
      len = atoi(p);

      *ep = p;
   }

   return len;
}

static char* ReceiveData(Network &network, DWORD *cb)
{
   char *buf = NULL;

   if( network.WaitData() && (*cb=network.AvailBytes()) != 0 )
   {
      buf = (char*)malloc(*cb);
      network.Receive((BYTE*)buf, cb);
   }

   return buf;
}

static const char* ReadResponse(Network& network)
{
   DWORD cb;
   char *message = NULL;
   char *buf = ReceiveData(network, &cb);
   const char* ep;

   if( buf == NULL )
      return message;

   if( IsSuccess(buf) )
   {
      DWORD rcvd = 0;
      DWORD len = ContenLength(buf, &ep);
      if( len > 0 )
      {
         while( ep != NULL && (ep=strstr(ep, "\r\n\r\n")) == NULL ) // receive header
         {
            free(buf);
            ep = buf = ReceiveData(network, &cb);
         }

         if( ep != NULL )
         {
            ep += 4;
            rcvd = cb - (ep - buf);
            if( rcvd > len ) rcvd = len;

            message = (char*)malloc(len+1);
            memcpy(message, ep, rcvd);

            if( rcvd < len )
            {
               DWORD rest = len - rcvd;
               if( !network.Receive((BYTE*)message + rcvd, &rest) || rest != len-rcvd )
               {
                  free(message);
                  message = NULL;
               }
            }
            if( message )
               message[len] = '\0';
         }
      }
   }
   free(buf);

   return message;
}

static const char* SendRequest(const char* host, WORD port, const char* _request)
{
   const char* response = NULL;
   Network network;

   std::string request(GET);
   request += _request;
   request += HTTP_VER;
   request += HOST;
   request += host;
   request += "\r\n\r\n";

   if( network.ConnectByName(host, port) )
   {
      if( network.Send((const BYTE*)request.c_str(), request.size()) )
      {
         network.SetTimeout(_RCV_TIMEOUT);
         response = ReadResponse(network);
      } else
      {
         Log("Can't send error %d", network.GetLastError());
      }
   } else
   {
      Log("Can't connect error %d", network.GetLastError());
   }

   return response;
}

static int GetValue(const char* p, const char** ep)
{
   while( !isdigit(*p) ) p++;
   int val = *p++ - '0';

   while( *p != '.' )
   {
      val *= 10;
      val += (*p++ - '0');
   }

   p++;
   int scale = 1;
   for( int i=0; i<4; i++, p++ )
   {
      if( !isdigit(*p) )
         break;

      val *= 10;
      val += (*p - '0');
      scale *= 10;
   }

   while( scale < GPS_SCALE )
   {
      scale *= 10;
      val *= 10;
   }

   *ep = p;
   return val;
}

static bool ParseResponse(int* lon, int* lat, const char* response)
{
   short step = 0;
   if( strncmp(response, "<error", sizeof("<error") - 1) != 0 )
   {
      const char *p = response;
      const char* ep = strstr(p, "latitude");
      if( ep != NULL )
      {
         DWORD val = GetValue(ep, &p);

         ep = strstr(p, "latitude");
         if( ep != NULL )
         {
            val += GetValue(ep, &p);
            val /= 2;
         }

         *lat = val;
         step++;
      }

      p = response;
      ep = strstr(p, "longitude");
      if( ep != NULL )
      {
         DWORD val = GetValue(ep, &p);

         ep = strstr(p, "longitude");
         if( ep != NULL )
         {
            val += GetValue(ep, &p);
            val /= 2;
         }

         *lon = val;
         step++;
      }
   }

   return (step==2);
}

static bool GetYandex(int* lon, int* lat, DWORD mcc, DWORD mnc, DWORD lac, DWORD cell)
{
   char request[100];
   sprintf(request, "/cellid_location/?&countrycode=%d&operatorid=%d&lac=%d&cellid=%d", mcc, mnc, lac, cell);

   bool res = false;
   const char* response = SendRequest("mobile.maps.yandex.net", HTTP_PORT, request);
   if( response )
   {
      res = ParseResponse(lon, lat, response);
      free((void*)response);
   }

   if( !res )
   {
      static char sv[100];
      if( strcmp(sv, request) )
      {
         Log("Error GetLocation %s", request);
         strcpy(sv, request);
      }
   }

   return res;
}

bool GSMModule::GetLocation(int* lon, int* lat, DWORD mcc, DWORD mnc, DWORD lac, DWORD cell)
{
   if( locationCache.Get(lon, lat, lac, cell) )
      return true;

   bool res = true;
   if( GetYandex(lon, lat, mcc, mnc, lac, cell) )
      locationCache.Add(*lon, *lat, lac, cell);
   else
      res = false;

   return res;
}

void GSMModule::StopLocation()
{
   Network::ReleaseConnection(0);
}
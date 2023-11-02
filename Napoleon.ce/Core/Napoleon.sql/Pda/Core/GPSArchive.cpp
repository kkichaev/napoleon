/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Работа с архивом GPS данных
 *
 *  ert   25/08/2009   creating
 */ 
#include "stdafx.h"
#include "GPSArchive.h"
#include <StdFuncs.h>
#include <Exchange.h>

#include <Module.h>
#include <NetExchange.h>

const wchar_t CURRENT_GPS[] = L"NapleonGPS.curr";
const wchar_t SENDED_GPS[] = L"NapleonGPS.snd";
const wchar_t ARCHIVE_GPS[] = L"NapleonGPS.arch";
const wchar_t TEMP_GPS[] = L"NapleonGPS.tmp";

static int Serialize(SendPacketParam *sendParam, FILE *f)
{
   GPSPos pos;
   const DataReflector &reflector = pos.GetType();

   reflector.ToStream(&sendParam->stream);

   int count = 0;
   while( !feof(f) )
   {
      Location val;
      if( fread(&val, sizeof(val), 1, f) > 0 )
      {
         pos.isGSM = (val.isGPS) ? 0 : 1;

         pos.date = val.date;
         pos.longitude = val.longitude;
         pos.latitude = val.latitude;
         pos.speed = val.speed;

         reflector.DataToStream(&sendParam->stream, pos);

         count++;
      }
   }

   return count;
}

static void AppendFile(const std::wstring& destName, const std::wstring& srcName)
{
   char buf[500];

   FILE *src = _wfopen(srcName.c_str(), L"rb");
   if( src == NULL ) return;

   FILE *dest = _wfopen(destName.c_str(), L"a+b");
   fseek(dest, 0, SEEK_END);

   while(!feof(src))
   {
      int len = fread(buf, sizeof(char), sizeof(buf), src);
      if( len > 0 )
         fwrite(buf, sizeof(char), len, dest);
   }

   fclose(src);
   fclose(dest);

   DeleteFile(srcName.c_str());
}

static void RemoveUnused(const std::wstring &destName, WORD interval)
{
   FILE *src = _wfopen(destName.c_str(), L"rb");
   if( src == NULL )
      return;

   FILETIME ft;
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &ft);

   __int64 val1 = *(__int64*)&ft;
   __int64 diff = (__int64)interval * 24 * 3600 * 10000000;

   std::wstring tempName;
   _Module.MakeFileName(&tempName, TEMP_GPS);
   FILE *dest = _wfopen(tempName.c_str(), L"wb");
   while( !feof(src) )
   {
      Location pos;
      if( fread(&pos, sizeof(pos), 1, src) > 0 )
      {
         __int64 *val2 = (__int64*)(&pos.date);
         if( val1 - *val2 <= diff )
            fwrite(&pos, sizeof(pos), 1, dest);
      }
   }
   fclose(src);
   fclose(dest);

   DeleteFile(destName.c_str());
   MoveFile(tempName.c_str(), destName.c_str());
}

void GPSArchive::AddCurrent(const Location &data)
{
   std::wstring fname;
   _Module.MakeFileName(&fname, CURRENT_GPS);

   FILE *f = _wfopen(fname.c_str(), L"ab");
   fwrite(&data, sizeof(data), 1, f);
   fclose(f);
}

int GPSArchive::SerializeCurrent(SendPacketParam *sendParam)
{
   std::wstring destName, srcName;
   _Module.MakeFileName(&destName, SENDED_GPS);
   _Module.MakeFileName(&srcName, CURRENT_GPS);

   if( !IsFileExist(srcName) )
      return 0;

   if( IsFileExist(destName) )
      AppendFile(destName, srcName);
   else
      MoveFile(srcName.c_str(), destName.c_str());

   FILE *dest= _wfopen(destName.c_str(), L"rb");
   int ret = Serialize(sendParam, dest);
   fclose(dest);

   return ret;
}

int GPSArchive::SerializeArchive(SendPacketParam *sendParam, WORD dayInterval)
{
   std::wstring destName, srcName;
   _Module.MakeFileName(&destName, SENDED_GPS);
   _Module.MakeFileName(&srcName, CURRENT_GPS);

   // add current to sended
   if( IsFileExist(srcName) )
   {
      if( IsFileExist(destName) )
         AppendFile(destName, srcName);
      else
         MoveFile(srcName.c_str(), destName.c_str());
   }

   // add sended to archive
   _Module.MakeFileName(&srcName, ARCHIVE_GPS);
   if( IsFileExist(destName) )
   {
      if( IsFileExist(srcName) )
         AppendFile(srcName, destName);
      else
         MoveFile(destName.c_str(), srcName.c_str());
   }

   FILE *f = _wfopen(srcName.c_str(), L"rb");
   if( f == NULL ) return 0;

   FILETIME ft;
   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;
   SystemTimeToFileTime(&st, &ft);

   __int64 val1 = *(__int64*)&ft;
   __int64 diff = (__int64)dayInterval * 24 * 3600 * 10000000;

   bool finded = false;
   while( !feof(f) )
   {
      Location pos;
      long fpos = ftell(f);

      if( fread(&pos, sizeof(pos), 1, f) > 0 )
      {
         __int64 *val2 = (__int64*)&pos.date;
         if( val1 - *val2 <= diff )
         {
            finded = true;
            fseek(f, fpos, SEEK_SET);
            break;
         }
      }
   }

   int ret = 0;
   if( finded )
      ret = Serialize(sendParam, f);

   fclose(f);
   return ret;
}

void GPSArchive::MoveCurrentToArchive(WORD archiveInterval)
{
   std::wstring destName, srcName;
   _Module.MakeFileName(&destName, ARCHIVE_GPS);
   _Module.MakeFileName(&srcName, SENDED_GPS);

   if( !IsFileExist(srcName) )
      return ;

   AppendFile(destName, srcName);

   if( archiveInterval != 0 )
      RemoveUnused(destName, archiveInterval);
}

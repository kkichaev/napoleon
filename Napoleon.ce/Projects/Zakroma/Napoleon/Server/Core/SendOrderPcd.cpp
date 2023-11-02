/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Передача накладных и оплат
 *
 *  ert   09/02/2008   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include <Exchange.h>
#include <Sync.h>
#include "Server.h"

using namespace std;

const char ordPrcMask[] = "S%s.SND";   // маска для отправки
const char dbfOrdMask[] = "POD%s.DBF"; // маска DBF 

bool MakeOrdStream(StreamWriter *writer, const char *userID)
{
   char userFile[30];
   string fileName(ExchangeFolder());
   wsprintf(userFile, ordPrcMask, userID);
   fileName += userFile;

   FILE *rd = fopen(fileName.c_str(), "rb");
   if( rd != NULL )
   {
      int len = _filelength(_fileno(rd));

      if( len > 0 )
      {
         BYTE *buf = (BYTE*)alloca(len);
         fread(buf, sizeof(BYTE), len, rd);
         writer->Write(buf, len);

         fclose(rd);
         return true;
      }
      fclose(rd);
   }

   return false;
}

void CreatedToFileTime(FILETIME *ft, const char *created)
{
   SYSTEMTIME st;

   sscanf(created, "%4d%2d%2d%2d%2d%2d", &st.wYear, &st.wMonth, &st.wDay, &st.wHour, &st.wMinute, &st.wSecond);
   SystemTimeToFileTime(&st, ft);
}

struct OrderProceededOld : public IReflectableData
{
   FILETIME created;
   DECLARE_TYPE_REFLECTION(OrderProceededOld)
};

BEGIN_TYPE_REFLECTION(OrderProceededOld)
   REGISTER_FILETIME_MEMBER(OrderProceededOld, created)
END_TYPE_REFLECTION(OrderProceededOld)

bool PrepareOrderProceeded(StreamWriter *writer, WORD dbVer, const char *userID)
{
   if( MakeOrdStream(writer, userID) ) return true;

   // создадим выходной файл
   char userFile[30];
   string dbName(ExchangeFolder());
   wsprintf(userFile, dbfOrdMask, userID);
   dbName += userFile;

   DataForm base;
   if( !base.Open(dbName.c_str()) ) return false;

   string fileName (ExchangeFolder());
   wsprintf(userFile, ordPrcMask, userID);
   fileName += userFile;
   FILE *wr = fopen(fileName.c_str(), "wb");

   if( wr == NULL ) return false;
   FileWriter fw(wr);

   StringHolder sh;
   if( dbVer > 0x1005 )
   {
      OrderProceeded op;
      const DataReflector &reflector = op.GetType();
      for( long rc = 0; base.ReadRec(rc); rc++ )
      {
         const char *p = base["CREATED"];
         if( p == NULL ) break;

         CreatedToFileTime(&op.created, p);

         p = base["REMARK"];
         if( p != NULL )
            op.remark = sh.Add(Trunc(p), CP_OEMCP);
         else
            op.remark = L"";
         reflector.Serialize(&fw, op);
      }
   } else
   {
      OrderProceededOld op;
      const DataReflector &reflector = op.GetType();
      for( long rc = 0; base.ReadRec(rc); rc++ )
      {
         const char *p = base["CREATED"];
         if( p == NULL ) break;

         CreatedToFileTime(&op.created, p);
         reflector.Serialize(&fw, op);
      }
   }

   base.Close();
   fclose(wr);
   _unlink(dbName.c_str());

   return MakeOrdStream(writer, userID);
}

void FreeOrderProceeded(const char *userID)
{
   char userFile[30];
   string fileName(ExchangeFolder());
   wsprintf(userFile, ordPrcMask, userID);
   fileName += userFile;

   _unlink(fileName.c_str());
}

bool CanSend(SOCKET sock)
{
   SendResponse(sock, ACK_ORD_PCD);

   char cmd[CMD_LENGTH+1];
   int cmdLen = CMD_LENGTH;
   if( Receive(sock, cmd, &cmdLen, TIME_OUT) && cmdLen > 0)
   {
      cmd[cmdLen] = '\0';
      return (strcmp(cmd, GOOD_RESPONSE) == 0);
   }

   return false;
}

bool SendOrderProceeded(SOCKET sock, WORD dbVer, const char *userID, bool checkCanSend)
{
   char cmd[CMD_LENGTH+1];
   int cmdLen;
   CompressWriter writer;

   if( !checkCanSend && SendPriceRemnants(sock, dbVer, userID) ) // !checkCanSend - при приеме заявки
      WaitResponse(sock);

   if( PrepareOrderProceeded(&writer, dbVer, userID) &&
      (!checkCanSend || CanSend(sock)) &&
      SendStream(sock, writer, SND_ORD_PCD, dbVer, 0) )
   {
      cmdLen = CMD_LENGTH;
      if( Receive(sock, cmd, &cmdLen, TIME_OUT * 10) && cmdLen > 0)
      {
         cmd[cmdLen] = '\0';
         if( strcmp(cmd, GOOD_RESPONSE) == 0 )
            FreeOrderProceeded(userID);
      }
   } 

   return false;
}

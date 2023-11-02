/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Совместимость с предыдущими версиями
 *
 * ert   02/07/2010   creating
 */
#include "stdafx.h"

#if 0
#include "session.h"

#include <atldef.h>

#include <compat.h> // используем <> т.к. это файл может быть разный для разных проектов (см. Суханов)

#include <Streamer.h>
#include <Compress.h>
#include "socket.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

#if defined(Imperia) || defined(Suchanov) || defined(Repnikov) || defined(Kirov_Pavel) || defined(ERCom) || defined(Michailov_V)
#define USE_FIRST_ID
#endif


struct FolderSend : public IReflectableData
{
   wchar_t *name;
   DWORD    id;
   WORD     size;
   WORD     level;
   DWORD    sort;

#if defined(USE_FIRST_ID)
   DWORD    firstID; // первый элемент прайс-листа
#endif

   DECLARE_TYPE_REFLECTION(FolderSend);
};
BEGIN_TYPE_REFLECTION(FolderSend)
   REGISTER_STRING_MEMBER(FolderSend, name)
   REGISTER_ULONG_MEMBER(FolderSend, id)
   REGISTER_USHORT_MEMBER(FolderSend, size)
   REGISTER_USHORT_MEMBER(FolderSend, level)
   REGISTER_ULONG_MEMBER(FolderSend, sort)
#if defined(USE_FIRST_ID)
   REGISTER_ULONG_MEMBER(FolderSend, firstID)
#endif
END_TYPE_REFLECTION(FolderSend)

#ifdef VISIT_DOC
struct Visit : public IReflectableData
{
   FILETIME date;
   wchar_t *id;
   wchar_t *remark;
   DWORD flags;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   DECLARE_TYPE_REFLECTION(Visit)
};
BEGIN_TYPE_REFLECTION(Visit)
   REGISTER_TIMESTAMP_MEMBER(Visit, date)
   REGISTER_STRING_MEMBER(Visit, id)
   REGISTER_STRING_MEMBER(Visit, remark)
   REGISTER_ULONG_MEMBER(Visit, flags)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(Visit, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(Visit, longitude, GPS_SCALE)
#endif
END_TYPE_REFLECTION(Visit)
#endif

static void ReadParam(std::string *buf, const char *param, const char **ep)
{
   while( *param && *param == ' ' )
      param++;

   while( *param && *param != ' ' )
   {
      if( *param != '\"' )
         buf->append(1, *param);
      param++;
   }

   *ep = (char*)param;
}

static inline bool SendResponse(Socket* socket, const wchar_t* data)
{
   return socket->Write((const BYTE*)data, CMD_LENGTH * sizeof(wchar_t));
}

static bool ReceiveData(Socket* socket, char* buf, int size, Binary *packet, HANDLE hStop)
{
   char *sbuf = buf;
   int rcv = size;

   if( packet != NULL && packet->Size() > CMD_LENGTH )
   {
      int sz = packet->Size() - CMD_LENGTH;
      memcpy(buf, (const BYTE*)(*packet) + CMD_LENGTH, sz);
      sbuf += sz;
      rcv -= sz;
   }

   return socket->ReadBuf((BYTE*)sbuf, rcv, 60000, hStop);
}

static bool Auth(const char *param, const char** ep, Session *session, int* dbVer)
{
   USES_CONVERSION;

   std::string login;
   std::string pwd;

   *ep = param;
   *dbVer = (unsigned short)strtol(*ep, (char**)ep, 10);
   ReadParam(&login, *ep, ep);
   ReadParam(&pwd, *ep, ep);

   return session->Auth(A2W(login.c_str()), A2W(pwd.c_str()));
}

static char* Decompress(const char* src, int len, int *destLen)
{
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   inflateInit(&stream);

   stream.avail_in = len;
   stream.next_in = (BYTE*)src;

   char *dest = (char*)malloc(len);
   stream.avail_out = len;
   stream.next_out = (BYTE*)dest;

   *destLen = 0;
   while(true)
   {
      int res = inflate(&stream, Z_NO_FLUSH);
      if( res == Z_STREAM_END )
      {
         *destLen += (len - stream.avail_out);
         break;
      }

      if( (res == Z_FINISH || res == Z_OK || Z_BUF_ERROR) && stream.avail_out == 0 )
      {
         *destLen += len;
         dest = (char*)realloc(dest, *destLen + len);

         stream.avail_out = len;
         stream.next_out = (BYTE*)dest + *destLen;
      } else
      {
         free(dest);
         dest = NULL;
         break;
      }
   }

   inflateEnd(&stream);
   return dest;
}

static void ClearMembers(IReflectableData *data)
{
   const DataReflector &r = data->GetType();

   __int64 ivalue = 0;
   wchar_t* value = L"";
   for( int i = r.Count() - 1; i >= 0; i-- )
   {
      const MemberType &m = r.Type(i);
      switch( m.type )
      {
      case MemberType::String:
         m.SetValue(data, &value);
         break;
      case MemberType::Parent:
      case MemberType::Collection:
         break;
      default:
         m.SetValue(data, &ivalue);
         break;
      }
   }
}

static bool SetObject(SessionObject* dest, IReflectableData* data, StreamReader &stream, int count)
{
   const DataReflector& type = data->GetType();
   OutStream os;
   type.ToStream(&os);
   while( count > 0 )
   {
      if( !type.Deserialize(data, stream) )
         break;

      type.DataToStream(&os, *data);
      count--;
   }
   bool ret = false;
   if( count == 0 )
   {
      const std::wstring& str = os.ToString();
      const wchar_t* sp = str.c_str();
      const wchar_t *ep = sp + str.size();
      ParseStreamW ps(sp, ep);

      ret = dest->Read(ps, dest->GetSession().GetFormatList());
   }
   return ret;
}

static Session::AckReturn DoReceive(Socket* socket, const char *param, Binary *packet, Session* session, const wchar_t* objName, bool compressed)
{
   Session::AckReturn ret = Session::arFail;

   const char *ep;
   int dbVer;
   if( Auth(param, &ep, session, &dbVer) == false )
   {
      std::wstring answer(FAIL_RESPONSE);
      answer += L" пользователь неопределен";
      SendResponse(socket, answer.c_str());

      return ret;
   }

   SendResponse(socket, GOOD_RESPONSE_W);

   int count = strtol(ep, (char**)&ep, 10);
   int size = strtol(ep, (char**)&ep, 10);

   char *buf = (char*)malloc(size);
   if( ReceiveData(socket, buf, size, packet, session->EvStop()) )
   {
      if( compressed )
      {
         int destLen;
         char *dbuf = Decompress(buf, size, &destLen);
         free(buf);
         buf = NULL;

         if( dbuf == NULL )
         {
            std::wstring answer(FAIL_RESPONSE);
            answer += L" ошибка распаковки";
            SendResponse(socket, answer.c_str());

            return ret;
         }

         buf = dbuf;
         size = destLen;
      }

      std::wstring destName(objName);
      std::wstring srcName(objName);
      size_t cp = srcName.find(L':');
      if( cp > 0 )
      {
         destName = srcName.substr(cp+1);
         srcName = srcName.substr(0, cp);
      } else
         destName = srcName;

      SessionObject* dest = session->Build(destName, false);
      const DataReflector* type = FindTypeReflector(srcName.c_str());
      if( type == NULL || dest == NULL )
      {
         std::wstring answer(FAIL_RESPONSE);
         answer += L" нет объекта '";
         answer += srcName;
         answer += L"'";
         SendResponse(socket, answer.c_str());

         return ret;
      }

      IReflectableData* data = type->Create();
      ClearMembers(data);
      StreamReader reader((BYTE*)buf, size);

      if( SetObject(dest, data, reader, count) )
      {
         if( dest->CreateWriter(NULL) && dest->Write(true) )
            ret = Session::arPrevious;
         dest->CloseWriter();
      }

      delete data;
      delete dest;
   }

   free(buf);

   if( ret == Session::arPrevious )
   {
      SendResponse(socket, ORDER_RESPONSE);
   }

   return ret;
}

static void SetNumData(IReflectableData* data, const MemberType& m, double val)
{
   OutStream os;
   m.ToStream(&os);

   const std::wstring& str = os.ToString();
   std::wstring::size_type pos = str.find(L'(');

   int scale = 0;
   if( pos != std::wstring::npos )
      scale = _wtoi(str.substr(pos+1).c_str());

   while( scale-- > 0 )
      val *= 10;

   switch(m.type)
   {
   case MemberType::Short:
      {
         short dv = (short)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::UShort:
      {
         unsigned short dv = (unsigned short)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::Integer:
      {
         int dv = (int)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::Unsigned:
      {
         unsigned dv = (unsigned)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::Long:
      {
         long dv = (long)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::ULong:
      {
         unsigned long dv = (unsigned long)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::Double:
      {
         m.SetValue(data, &val);
         break;
      }
   case MemberType::Float:
      {
         float dv = (float)val;
         m.SetValue(data, &dv);
         break;
      }
   case MemberType::Int64:
      {
         __int64 dv = (__int64)val;
         m.SetValue(data, &dv);
         break;
      }
   }
}

static void SetData(IReflectableData* data, const Object& src, const DataReflector& type, const Format& format)
{
   int size = type.Count();
   for( int i=0; i<size; i++ )
   {
      const MemberType& m = type.Type(i);
      int index = format.FindMember(m.name);

      if( index < 0 )
         continue;

      const MemberFormat& mf = format[index];
      const Member& srcM = src.at(index);

      switch(m.type)
      {
      case MemberType::String:
         if( mf.type == MemberFormat::mtString )
         {
            const wchar_t *val = srcM.str->c_str();
            m.SetValue(data, (const void*)&val);
         }
         break;

      case MemberType::Short:
      case MemberType::UShort:
      case MemberType::Integer:
      case MemberType::Unsigned:
      case MemberType::Long:
      case MemberType::ULong:
      case MemberType::Double:
      case MemberType::Float:
      case MemberType::Int64:
         if( mf.type == MemberFormat::mtNumber )
            SetNumData(data, m, srcM.number);
         break;

      case MemberType::DateTime:
         if( mf.type == MemberFormat::mtDateTime )
            m.SetValue(data, &srcM.datetime);
         break;

      case MemberType::Collection:
         if( mf.type == MemberFormat::mtObject )
         {
            ServObject *so = srcM.object;
            if( so != NULL && so->size() )
            {
               IDataCollection* dc = (IDataCollection*)m.GetValue(*data);
               const DataReflector& elType = dc->DataType();

               for( unsigned i=0; i<so->size(); i++ )
               {
                  IReflectableData* element = elType.Create();
                  ClearMembers(element);

                  SetData(element, *so->at(i), elType, *so->format);
                  dc->Add(*element, -1);

                  delete element;
               }
            }
         }
         break;
      }
   }
}

static bool SendObject(Socket* socket, Session* session, const wchar_t* objName, const wchar_t* command, bool sendFail, int dbVer, bool sendVer)
{
   bool ret = false;
   std::wstring on(objName), srcname;
   size_t pos = on.find(L':');
	if( pos == std::wstring::npos ) srcname = on;
   else
   {
      srcname = on.substr(pos+1);
      on = on.substr(0, pos);
   }
   const DataReflector* type = FindTypeReflector(on.c_str());
   const ISessionObject *iso = session->LoadObject(srcname.c_str(), NULL);
   const SessionObject *so = (iso == NULL) ? NULL : (SessionObject*)iso->Self();

   if( type == NULL || so == NULL )
   {
      if( sendFail )
      {
         std::wstring answer(FAIL_RESPONSE);
         answer += L" нет объекта '";
         answer += on;
         answer += L"'";
         SendResponse(socket, answer.c_str());
      } else
         ret = true;
      return ret;
   }

   bool updateFolderData = (on.compare(L"FolderSend") == 0);
   ret = true;
   if( so->size() > 0 )
   {
      CompressWriter cw;

      for( unsigned i=0; i < so->size(); i++ )
      {
         IReflectableData *data = type->Create();
         ClearMembers(data);

         const Object* o = so->at(i);
         SetData(data, *o, *type, *so->format);
         if( updateFolderData )
         {
            ((FolderSend*)data)->sort = ((FolderSend*)data)->id;
#if defined(USE_FIRST_ID)
            ((FolderSend*)data)->firstID = -1;
#endif
         }
         type->Serialize(&cw, *data);

         delete data;
      }

      int len = cw.Size();
      BYTE *bytes = (BYTE*)malloc(len);
      wchar_t cmd[CMD_LENGTH+1];

      cw.ToBytes(bytes);
      if( sendVer )
         wsprintfW(cmd, L"%s %d %d", command, dbVer, len);
      else
         wsprintfW(cmd, L"%s %d", command, len);

      SendResponse(socket, cmd);
      if( socket->Write(bytes, len) )
         socket->ReadBuf((BYTE*)cmd, CMD_LENGTH, 60000, session->EvStop());

      free(bytes);
   }

   return ret;
}

struct ObjSendData
{
   std::wstring name;
   std::wstring command;
   bool failIfNot;
   bool sendVer;


   ObjSendData(const std::wstring& n, const std::wstring& c, bool fail = true, bool sendVer = true)
   {
      name = n;
      command = c;
      failIfNot = fail;
      this->sendVer = sendVer;
   }
};

typedef std::vector<ObjSendData> SendList;

static Session::AckReturn DoSend(Socket* socket, const char *param, Session* session, const SendList& data)
{
   Session::AckReturn ret = Session::arFail;
   const char *ep;
   int dbVer;
   if( Auth(param, &ep, session, &dbVer) == false )
   {
      std::wstring answer(FAIL_RESPONSE);
      answer += L" пользователь неопределен";
      SendResponse(socket, answer.c_str());

      return ret;
   }

   ret = Session::arPrevious;
   SendList::const_iterator i = data.begin();
   for( ; i != data.end(); i++ )
   {
      if( !SendObject(socket, session, i->name.c_str(), i->command.c_str(), i->failIfNot, dbVer, i->sendVer) )
      {
         ret = Session::arFail;
         break;
      }
   }

   if( ret == Session::arPrevious )
   {
#ifndef NO_POD
      SendObject(socket, session, L"OrderProceeded", SND_ORD_PCD, false, dbVer, true);
#endif
#ifdef RCV_MESSAGE
      SendObject(socket, session, L"Message", SND_MESSAGE, false, dbVer, true);
#endif

      char cmd[CMD_LENGTH+1];
      SendResponse(socket, BYE_CMD_W);
      socket->ReadBuf((BYTE*)cmd, CMD_LENGTH, 60000, session->EvStop());

      session->Commit();
   }

   return ret;
}

#ifdef COST_MANAGER
static Session::AckReturn DoSendCost(Socket* socket, const char *param, Session* session, const SendList& data)
{
   Session::AckReturn ret = Session::arFail;
   const char *ep;
   int dbVer;
   if( Auth(param, &ep, session, &dbVer) == false )
   {
      std::wstring answer(FAIL_RESPONSE);
      answer += L" пользователь неопределен";
      SendResponse(socket, answer.c_str());

      return ret;
   }

   ret = Session::arPrevious;
   SendList::const_iterator i = data.begin();
   const ISessionObject *soI = session->LoadObject(i->name.c_str(), NULL);
   const SessionObject *so = (soI == NULL) ? NULL : (const SessionObject *)soI->Self();;
   if( so && so->size() )
   {
      int index = so->format->FindMember(L"data");
      if( index >= 0 )
      {
         const Object* o = so->at(0);
         const Member& m = o->at(index);

         CompressWriter cw;
         cw.Write((BYTE*)m.binary->Bytes(), m.binary->Size());

         int len = cw.Size();
         BYTE *buf = (BYTE*)malloc(len);
         cw.ToBytes(buf);

         wchar_t cmd[CMD_LENGTH+1];
         wsprintfW(cmd, L"%s %d %d", i->command.c_str(), dbVer, len);

         SendResponse(socket, cmd);
         if( socket->Write(buf, len) )
         {
            socket->ReadBuf((BYTE*)cmd, CMD_LENGTH, 60000, session->EvStop());

            SendResponse(socket, BYE_CMD_W);
            socket->ReadBuf((BYTE*)cmd, CMD_LENGTH, 60000, session->EvStop());

            session->Commit();
         }

         free(buf);
      }
   }

   return ret;
}
#endif

Session::AckReturn Session::ReadPreviousVersion(Binary* packet)
{
   if( packet->Size() < CMD_LENGTH )
      return arFail;

   char cmd[CMD_LENGTH + 1];
   memcpy(cmd, (const BYTE*)(*packet), CMD_LENGTH);
   cmd[CMD_LENGTH] = '\0';

   Session::AckReturn ret = arFail;
   if( !strncmp(cmd, SND_ORDER_W, sizeof(SND_ORDER_W)-1) )
      ret = DoReceive(socket, cmd + sizeof(SND_ORDER_W)-1, packet, this, rcvOrder.name, rcvOrder.compressed);
   else if( !strncmp(cmd, ACK_REMNANTS_W, sizeof(ACK_REMNANTS_W)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(L"PriceRemnants", SND_REMNANTS_W));
      ret = DoSend(socket, cmd + sizeof(ACK_REMNANTS_W)-1, this, sl);
   }
   else if( !strncmp(cmd, ACK_PRICE_W, sizeof(ACK_PRICE_W)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(CONFIG_STR, SND_CONFIG_W, false, false));
      sl.push_back(ObjSendData(L"Org", SND_ORGS_W));
      sl.push_back(ObjSendData(L"FolderSend:Folder", SND_FOLDERS_W));
      sl.push_back(ObjSendData(L"Price", SND_PRICE_W));
      sl.push_back(ObjSendData(L"OrgFolder", SND_FORGS_W, false));
#ifdef ORG_TASK
      sl.push_back(ObjSendData(L"Task", SND_ORG_TASK, false));
#endif
#ifdef PRICE_MATRIX
      sl.push_back(ObjSendData(L"Matrix", SND_MATRIX, false));
#endif
#ifdef Autopteka
      sl.push_back(ObjSendData(L"Incomes", SND_INCOME, false));
#endif
      ret = DoSend(socket, cmd + sizeof(ACK_PRICE_W)-1, this, sl);
   }
#ifdef Autopteka
   else if( !strncmp(cmd, ACK_VAN_PRICE, sizeof(ACK_VAN_PRICE)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(L"Config:VanConfig", SND_CONFIG_W, false, false));
      sl.push_back(ObjSendData(L"OrgVan", SND_ORGS_W));
      sl.push_back(ObjSendData(L"FolderSend:Folder", SND_FOLDERS_W));
      sl.push_back(ObjSendData(L"PriceVan", SND_PRICE_W));
      sl.push_back(ObjSendData(L"OrgFolder", SND_FORGS_W, false));
#ifdef PRICE_MATRIX
      sl.push_back(ObjSendData(L"Matrix", SND_MATRIX, false));
#endif
#ifdef ORG_TASK
      sl.push_back(ObjSendData(L"Task", SND_ORG_TASK, false));
#endif
      ret = DoSend(socket, cmd + sizeof(ACK_VAN_PRICE)-1, this, sl);
   }
   else if( !strncmp(cmd, SND_RPLMNT, sizeof(SND_RPLMNT)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_RPLMNT)-1, packet, this, L"Replenishment", true);
   }
   else if( !strncmp(cmd, SND_VAN_ORD, sizeof(SND_VAN_ORD)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_VAN_ORD)-1, packet, this, L"OrdVan", true);
   }
#endif
#ifdef COST_MANAGER
   else if( !strncmp(cmd, ACK_COSTS, sizeof(ACK_COSTS)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(L"Cost", SND_COSTS, false));
      ret = DoSendCost(socket, cmd + sizeof(ACK_COSTS)-1, this, sl);
   }
#endif
   else if( !strncmp(cmd, ACK_BALANCE, sizeof(ACK_BALANCE)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(L"Delivery", SND_DELIVERY_W, false));
      sl.push_back(ObjSendData(L"Payment", SND_PAY_W, false));
      ret = DoSend(socket, cmd + sizeof(ACK_BALANCE)-1, this, sl);
   }
   else if( !strncmp(cmd, ACK_PRCD, sizeof(ACK_PRCD)-1) )
   {
      SendList sl;
      sl.push_back(ObjSendData(L"OrderProceeded", SND_ORD_PCD, false));
      ret = DoSend(socket, cmd + sizeof(ACK_PRCD)-1, this, sl);
   }
#ifdef VISIT_DOC
   else if( !strncmp(cmd, SND_VISIT, sizeof(SND_VISIT)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_VISIT)-1, packet, this, L"Visit", true);
   }
#endif

#ifdef PROXY_DOC
   else if( !strncmp(cmd, SND_PROXY, sizeof(SND_PROXY)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_PROXY)-1, packet, this, rcvProxy.name, rcvProxy.compressed);
   }
#endif

#ifdef ORG_TASK
   else if( !strncmp(cmd, SND_ORG_DO, sizeof(SND_ORG_DO)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_ORG_DO)-1, packet, this, L"Task:TaskSend", true);
   }
#endif

#ifdef ORG_REMNANTS
   else if( !strncmp(cmd, SND_ORG_RMNTS, sizeof(SND_ORG_RMNTS)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_ORG_RMNTS)-1, packet, this, L"OrgRemnants", true);
   }
#endif
#ifdef ORG_SKU
   else if( !strncmp(cmd, SND_ORG_REST, sizeof(SND_ORG_REST)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_ORG_REST)-1, packet, this, rcvRest.name, rcvRest.compressed);
   }
#endif

#ifdef GPS_POS
   else if( !strncmp(cmd, SND_GPS_DATA, sizeof(SND_GPS_DATA)-1) )
   {
      ret = DoReceive(socket, cmd + sizeof(SND_GPS_DATA)-1, packet, this, L"GPSPos", true);
   }
#endif

   return ret;
}

//Session::AckReturn Session::ReadPreviousVersion(Binary* packet)
//{
//   return arFail;
//}

#else
#include "session.h"
#include <atldef.h>

#include <compat.h> // используем <> т.к. это файл может быть разный для разных проектов (см. Суханов)

#include <Streamer.h>
#include <Compress.h>
#include "socket.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
Session::AckReturn Session::ReadPreviousVersion(Binary* packet)
{
   return arFail;
}
#endif

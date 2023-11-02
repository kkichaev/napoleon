/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Создание и декодирование пакета
 * 
 *  ert   23/09/2009   creating
 */ 
#include "stdafx.h"
#include "NetExchange.h"
#include "OutStream.h"
#include <StdFuncs.h>
#include "DataReader.h"

#include <StdFuncs.h>

#include <zlib.h>

BEGIN_TYPE_REFLECTION(ServerCommand)
   REGISTER_STRING_MEMBER(ServerCommand, command)
   REGISTER_STRING_MEMBER(ServerCommand, param)
   REGISTER_STRING_MEMBER(ServerCommand, userid)
   REGISTER_STRING_MEMBER(ServerCommand, password)
   REGISTER_STRING_MEMBER(ServerCommand, version)
   REGISTER_STRING_MEMBER(ServerCommand, category)
   REGISTER_ULONG_MEMBER(ServerCommand, duration)
#ifdef CHECK_LOGIN_PROGID
   REGISTER_STRING_MEMBER(ServerCommand, progid)
#endif
END_TYPE_REFLECTION(ServerCommand)

BEGIN_TYPE_REFLECTION(ServerAnswer)
   REGISTER_USHORT_MEMBER(ServerAnswer, response)
   REGISTER_STRING_MEMBER(ServerAnswer, message)
END_TYPE_REFLECTION(ServerAnswer)

ServerCommand::ServerCommand()
{
   category = L"";
}

//
//-------------------------------------------------------- ReceivedStream ----------------------------------------------------------------
//
ReceivedStream::ReceivedStream() : isFile(false)
{
   buffer.memory = NULL;
}

ReceivedStream::~ReceivedStream()
{
   Clear();
}

static void MakeTempName(std::wstring* fileName)
{
   SYSTEMTIME st;
   FILETIME ft;
   wchar_t buf[MAX_PATH], *p;

   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &ft);
   GetModuleFileName(NULL, buf, sizeof(buf)/sizeof(buf[0]));

   p = wcsrchr(buf, L'\\');
   if( p == NULL ) p = buf;
   else p++;

   do
      _itow(ft.dwLowDateTime++, p, 16);
   while( GetFileAttributes(buf) != 0xFFFFFFFF );

   fileName->assign(buf);
}

void ReceivedStream::SetSize(DWORD size)
{
   DWORD canUseMem = GetAvailMem() / 4;

   if( size < canUseMem )
   {
      isFile = false;
      buffer.memory = new Binary();
      pb = buffer.memory->Alloc(size);
   } else
   {
      isFile = true;
      MakeTempName(&fileName);
      buffer.file = _wfopen(fileName.c_str(), L"w+b");
   }

   cp = 0;
   totSize = size;
}

void ReceivedStream::SkipObject()
{
   WORD count = (Get() == L'[') ? 0 : 1;
   while( !EOS() )
   {
      wchar_t sym = Get();
      if( sym == L'[' ) count++; // первый символ должен быть [
      else if( sym == L']' )
      {
         if( count <= 1 ) break;
         else count--;
      }
   }
}

void ReceivedStream::Add(const BYTE *b, DWORD cb)
{
   DWORD left = totSize - cp;
   if( left < cb ) cb = left;

   if( cb != 0 )
   {
      if( isFile ) fwrite(b, 1, cb, buffer.file);
      else memcpy(pb + cp, b, cb);
      cp += cb;
   }
}

void ReceivedStream::Clear()
{
   if( isFile )
   {
      fclose(buffer.file);
      DeleteFile(fileName.c_str());
   } else
      delete buffer.memory;

   isFile = false;
   buffer.memory = NULL;
   totSize = cp = 0;
}

void ReceivedStream::PrepareRead()
{
   cp = 0;
   if( isFile ) fseek(buffer.file, 0, SEEK_SET);
}

DWORD ReceivedStream::CopyTo(BYTE* buffer, DWORD cb)
{
   if( cp >= totSize ) return 0;

   DWORD left = totSize - cp;
   if( cb > left ) cb = left;

   if( isFile ) fread(buffer, sizeof(BYTE), cb, this->buffer.file);
   else memcpy(buffer, pb+cp, cb);

   return cb;
}

bool ReceivedStream::CopyUntill(std::wstring* value, wchar_t sym)
{
   while( cp < totSize )
   {
      wchar_t s = Get();
      if( s == sym ) return true;
      if( value != NULL ) value->append(1, s);
   }
   return false;
}

wchar_t ReceivedStream::Get()
{
   wchar_t s = (isFile) ? fgetwc(buffer.file) : *(wchar_t*)(pb + cp);
   cp += sizeof(wchar_t);

   return s;
}

void ReceivedStream::Unget(wchar_t sym)
{
   if( cp > sizeof(wchar_t) )
   {
      if( isFile ) ungetwc(sym, buffer.file);
      cp -= sizeof(wchar_t);
   }
}

DWORD ReceivedStream::CRC32()
{
   PrepareRead();

   const DWORD BUF_SIZE = 10240;
   DWORD crc = 0xFFFFFFFF;
   if( isFile )
   {
      BYTE *block = (BYTE*)malloc(BUF_SIZE);
      while( true )
      {
         DWORD cb = CopyTo(block, BUF_SIZE);
         if( cb == 0 )
         {
            crc  ^= 0xFFFFFFFF;
            break;
         } else
            crc = ::CRC32(block, cb, crc);
      }
      free(block);
   } else
   {
      crc = (::CRC32(pb, totSize) ^ 0xFFFFFFFF);
   }

   return crc;
}

bool ReceivedStream::DecompressTo(ReceivedStream* destStream)
{
   const DWORD BUF_SIZE = 10240;

   DWORD srcSize, destSize;
   BYTE *src, *dest;
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;
   inflateInit(&stream); 

   PrepareRead();

   DWORD cmpSize = totSize - sizeof(stream.adler);
   if( isFile )
   {
      srcSize = (cmpSize < BUF_SIZE) ? BUF_SIZE : cmpSize;
      src = (BYTE*)malloc(srcSize);
      stream.avail_in = srcSize;
      srcSize = CopyTo(src, srcSize);
   } else
   {
      src = pb;
      stream.avail_in = cmpSize;
      srcSize = cmpSize;
      cp = cmpSize;
   }
   stream.next_in = src;

   if( destStream->isFile )
   {
      destSize = (destStream->totSize < BUF_SIZE) ? BUF_SIZE : destStream->totSize;
      dest = (BYTE*)malloc(destSize);
      stream.avail_out = destSize;
   } else
   {
      stream.avail_out = destStream->totSize;
      destSize = destStream->totSize;
      dest = destStream->pb;
   }
   stream.next_out = dest;

   int ec = Z_OK;
   bool error = false;
   while( ec != Z_STREAM_END )
   {
      if( stream.avail_in == 0 )
      {
         stream.next_in = src;
         stream.avail_in = CopyTo(src, srcSize);
         if( stream.avail_in == 0 )
         {
            error = true;
            break;
         }
      }

      ec = inflate(&stream, Z_NO_FLUSH);
      if( ec != Z_OK && ec != Z_STREAM_END )
      {
         error = true;
         break;
      }

      if( destStream->isFile )
      {
         destStream->Add(dest, destSize - stream.avail_out);
         stream.avail_out = destSize;
         stream.next_out = dest;
      }
   }

   if( isFile ) free(src);
   if( destStream->isFile ) free(dest);

   if( !error )
   {
      DWORD adler;
      if( CopyTo((BYTE*)&adler, sizeof(adler)) == sizeof(adler) )
         error = (FromStreamBytes((BYTE*)&adler) != stream.adler);
      else
         error = true;
   }

   inflateEnd(&stream); 

   return !error;
 }

//
//-------------------------------------------------------- NetworkExchange ----------------------------------------------------------------
//
struct ConnectParam
{
   WORD port;
   std::wstring ip;
   Packet *packet;
   WORD timeout;

   bool pcktRcvd;
   bool establishConnect;

   NetworkExchange network;
};

DWORD NetworkExchange::TryConnect(ConnectParam *param)
{
   param->pcktRcvd = false;

   char *ip = (char*)alloca(param->ip.size() + 1);
   wcstombs(ip, param->ip.c_str(), param->ip.size() + 1);
   if( param->network.Connect(ip, param->port, param->establishConnect) )
   {
      param->network.Send(*param->packet, NULL);

      if( param->timeout != 0 )
         param->network.SetTimeout(param->timeout);

      wchar_t buf[10];
      DWORD len = sizeof(PACKET_TAG)-sizeof(wchar_t);
      *buf = '\0';
      if( ((Network&)param->network).Receive((BYTE*)buf, &len, NULL) )
         param->pcktRcvd = (wcsncmp(buf, PACKET_TAG, sizeof(PACKET_TAG)/sizeof(wchar_t) - 1) == 0);
   }

   return 0;
}

bool NetworkExchange::SetConnectResult(ConnectParam &p, ConnectParam &p2, HANDLE hThread)
{
   bool result = false;
   if( p.pcktRcvd )
   {
      result = p.pcktRcvd;
      p.network.CopyConnection(this);
      
      if( hThread != INVALID_HANDLE_VALUE )
      {
         p2.network.Close();
         TerminateThread(hThread, 0);
      }
   } else
   {
      if( hThread != INVALID_HANDLE_VALUE )
      {
         if( WaitForMultipleObjects(1, &hThread, FALSE, INFINITE) == WAIT_OBJECT_0 )
         {
            result = p2.pcktRcvd;
            p2.network.CopyConnection(this);
         }
      }
   }

   return result;
}

bool NetworkExchange::Connecting(const IPAddress* addr1, const IPAddress* addr2, const ServerCommand& cmd, WORD timeOut, bool establishConnect)
{
   OutStream stream;
   const DataReflector& type = cmd.GetType();
   type.ToStream(&stream);
   type.DataToStream(&stream, cmd);

   return Connecting(addr1, addr2, stream, timeOut, establishConnect);
}

bool NetworkExchange::Connecting(const IPAddress* addr1, const IPAddress* addr2, OutStream& stream, WORD timeOut, bool establishConnect)
{
   HANDLE hThreads[2];
   DWORD count = 1;

   Packet *packet = Packet::MakePacket(stream, GZIP_OPT);
   stream.Clear();

   ConnectParam p1, p2;
   p1.port = addr1->port;
   p1.ip = addr1->ip;
   p1.packet = packet;
   p1.timeout = timeOut;
   p1.establishConnect = establishConnect;
   hThreads[0] = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryConnect, &p1, 0, NULL);

   if( addr2 != NULL && !addr2->ip.empty() )
   {
      count++;
      p2.port = addr2->port;
      p2.ip = addr2->ip;
      p2.packet = packet;
      p2.timeout = timeOut;
      p2.establishConnect = establishConnect;
      hThreads[1] = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)TryConnect, &p2, 0, NULL);
   }

   bool result = false;
   DWORD res = WaitForMultipleObjects(count, hThreads, FALSE, INFINITE);
   if( res == WAIT_OBJECT_0 )
   {
      result = SetConnectResult(p1, p2, (count > 1) ? hThreads[1] : INVALID_HANDLE_VALUE);
   } else if( res == WAIT_OBJECT_0 + 1 )
   {
      result = SetConnectResult(p2, p1, hThreads[0]);
   }

   while( count-- > 0 )
      CloseHandle(hThreads[count]);

   delete packet;
   return result;
}

bool NetworkExchange::Receive(Binary *b)
{
   b->Clear();

   DWORD cb = AvailBytes();
   if( cb == 0 )
   {
      if( !WaitData() || (cb = AvailBytes()) == 0 )
         return false;
   }

   DWORD canUseMem = GetAvailMem() / 2;
   if( cb > canUseMem ) cb = canUseMem;

   return (recv(socket, (char*)b->Alloc(cb), cb, 0) != SOCKET_ERROR);
}

NetworkExchange::NetStream::NetStream(NetworkExchange& n) : network(n), sp(NULL), ep(NULL)
{
}

bool NetworkExchange::NetStream::ReadNextBuffer()
{
   if( network.Receive(&buffer) == false )
      return false;

   sp = buffer;
   ep = (const wchar_t*)((const BYTE*)buffer + buffer.Size());

   return true;
}

bool NetworkExchange::NetStream::CopyUntill(std::wstring *value, wchar_t sym)
{
   value->clear();

   if( sp == NULL && !ReadNextBuffer() ) return false;

   while( true )
   {
      while( sp < ep )
      {
         if( *sp == sym )
         {
            sp++;
            return true;
         }

         value->append(1, *sp);
         sp++;
      }

      if( !ReadNextBuffer() ) return false;
   }
}

bool NetworkExchange::NetStream::Receive(ReceivedStream* stream, DWORD size, IProgressIndicator *pf)
{
   stream->SetSize(size);

   if( pf != NULL ) pf->SetMax(size);

   DWORD rb = ((const BYTE*)ep - (const BYTE*)sp);// * sizeof(wchar_t);
   if( rb > size ) rb = size;
   if( rb > 0 )
   {
      size -= rb;
      stream->Add((const BYTE*)sp, rb);
   }

   Binary b;
   DWORD start = GetTickCount();
   while( size > 0 )
   {
      if( pf != NULL ) pf->SetPos(stream->CurPos());
      if( !network.Receive(&b) ) break;

      if( GetTickCount() - start > 10000 )
      {
         start = GetTickCount();
         SystemIdleTimerReset();
      }

      if( size <= b.Size() )
      {
         stream->Add(b, size);
         size = 0;
      } else
      {
         stream->Add(b);
         size -= b.Size();
      }
   }

   if( pf != NULL ) pf->SetPos(stream->CurPos());
   return (size == 0);
}

bool NetworkExchange::ReadPacket(NetStream &stream, std::vector<PacketOperator> *operations, ReceivedStream* rcvd, IProgressIndicator *pf)
{
   std::wstring value;

   if( !stream.CopyUntill(&value, PacketOperator::Separator) ) return false;

   PacketOperator packet(value);
   DWORD packetSize = _wtoi(packet.value.c_str());

   if( pf != NULL ) pf->SetText(L"Прием данных...");

   while( true )
   {
      std::wstring opStr;
      if( !stream.CopyUntill(&opStr, PacketOperator::Separator) )
         return false;

      if( opStr.compare(HEAD_END_TAG) == 0 )
         break;
      operations->push_back(PacketOperator(opStr));
   }

   return (packetSize == 0) ? false : stream.Receive(rcvd, packetSize, pf);
}

static ReceivedStream* RestorePacket(const std::vector<PacketOperator>& operations, ReceivedStream* rcvd)
{
   std::vector<PacketOperator>::const_iterator i = operations.begin();

   for( ; i != operations.end(); i++ )
   {
      if( i->name.compare(GZIP_OPT) == 0 )
      {
         DWORD size = _wtol(i->value.c_str());
         ReceivedStream *tstream = new ReceivedStream();
         tstream->SetSize(size);

         if( !rcvd->DecompressTo(tstream) )
         {
            delete tstream;
            delete rcvd;

            return NULL;
         }

         ReceivedStream *tp = rcvd;
         rcvd = tstream;
         delete tp;
      } else if( i->name.compare(CRC_OPT) == 0 )
      {
         DWORD crc = _wtol(i->value.c_str());
         if( crc != rcvd->CRC32() )
         {
            delete rcvd;
            return NULL;
         }
      }
   }

   return rcvd;
}

ReceivedStream* NetworkExchange::Receive(const IPAddress* addr1, const IPAddress* addr2, const ServerCommand& command,
                                         IProgressIndicator *pf, bool establishConnect)
{
   ReceivedStream *rcvd = NULL;
   if( pf != NULL ) pf->SetText(L"Соединение...");

   if( Connecting(addr1, addr2, command, timeout, establishConnect) )
   {
      NetStream stream(*this);
      std::vector<PacketOperator> operations;
      rcvd = new ReceivedStream();

      if( ReadPacket(stream, &operations, rcvd, pf) )
         rcvd = RestorePacket(operations, rcvd);
   }

   return rcvd;
}

ReceivedStream* NetworkExchange::Receive(const IPAddress* addr1, const IPAddress* addr2, OutStream& ostream, IProgressIndicator *pf)
{
   ReceivedStream *rcvd = NULL;
   if( pf != NULL ) pf->SetText(L"Соединение...");

   if( Connecting(addr1, addr2, ostream, timeout, true) )
   {
      NetStream stream(*this);
      std::vector<PacketOperator> operations;
      rcvd = new ReceivedStream();

      if( ReadPacket(stream, &operations, rcvd, pf) )
         rcvd = RestorePacket(operations, rcvd);
   }

   return rcvd;
}

ReceivedStream* NetworkExchange::ReceiveStream(IProgressIndicator *pf)
{
   ReceivedStream *rcvd = NULL;

   wchar_t buf[10];
   DWORD len = sizeof(PACKET_TAG)-sizeof(wchar_t);
   *buf = '\0';
   if( Network::Receive((BYTE*)buf, &len, NULL) && (wcsncmp(buf, PACKET_TAG, sizeof(PACKET_TAG)/sizeof(wchar_t) - 1) == 0) )
   {
      NetStream stream(*this);
      std::vector<PacketOperator> operations;
      rcvd = new ReceivedStream();

      if( ReadPacket(stream, &operations, rcvd, pf) )
         rcvd = RestorePacket(operations, rcvd);
   }
   return rcvd;
}

bool NetworkExchange::Send(OutStream& stream, IProgressIndicator *pf)
{
   Packet *packet = Packet::MakePacket(stream, GZIP_OPT);
   stream.Clear();
   
   bool res = Send(*packet, pf);
   delete packet;

   return res;
}

bool CheckAnswer(ReceivedStream* stream, std::wstring *answer)
{
   ServerAnswer sa;
   std::wstring name;

   if( !stream->CopyUntill(&name, L'[') ) return false;
   if( name.compare(L"ServerAnswer") != 0 ) return false;

   DataReader* reader = DataReader::CreateReader(sa.GetType(), stream);
   bool res = (reader != NULL && reader->Read(&sa, stream));
   *answer = sa.message;
   delete reader;

   return (res && (sa.response == 1));
}

bool ProcessStream(ReceivedStream* stream, ReceiveObjects &objects, IProgressIndicator *pi, bool *bContinue)
{
   if( bContinue != NULL ) *bContinue = false;

   DWORD start = GetTickCount();
   while( !stream->EOS() )
   {
      std::wstring name;
      if( !stream->CopyUntill(&name, L'[') )
         break;

      if( name.compare(L"StreamContinue") == 0 )
      {
         if( bContinue != NULL ) *bContinue = true;
         return true;
      }

      IReceiveObject* ro = objects.FindObject(name);
      if( ro == NULL )
      {
         wchar_t sym = 0;
         do
         {
            stream->SkipObject();
            if( stream->EOS() ) break;
            sym = stream->Get();
            stream->Unget(sym);
         } while( sym == L'[' );
         continue;
      }

      if( pi )
      {
         const wchar_t *pt = ro->ProgressText();
         if( pt != NULL ) pi->SetText(pt, false);
      }

      while( ro->Read(stream) )
      {
         if( pi )
            pi->SetPos(stream->CurPos());

         if( GetTickCount() - start > 10000 )
         {
            start = GetTickCount();
            SystemIdleTimerReset();
         }
      }
      ro->Close();
   }

   if( !stream->EOS() )
      return false;
   return true;
}

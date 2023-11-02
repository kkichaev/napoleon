/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * Send & Receive Packet.
 *
 * ert   20/04/2009   creating
 */
#include "stdafx.h"
#include <pstream.h>
#include <servobj.h>
#include <packet.h>
#include <socket.h>

using namespace GRServer;
using namespace std;

// !!! use in sizeof w/o sizeof(a[0]) !!!
const DWORD PACKET_RCV_TIMEOUT = 60000; // 60 sec

class SockStream
{
public:
   SockStream(Socket* s, HANDLE evStop, DWORD to) : timeout(to), socket(s), hStop(evStop)
   {
		p = ep = 0;
      //p = (const PType*)((const BYTE*)(*buf));
      //ep = (const PType*)((const BYTE*)(*buf) + buf->Size());
      //if( (buf->Size() % 2) != 0 ) ep++;
   }

   bool CopyUntill(std::wstring *value, wchar_t sym);

   // add rest to buffer + read from socket, if need
   bool ReadBuffer(BYTE* buf, DWORD size);

protected:
   DWORD timeout;
   Socket *socket;
   Binary buf;
#ifdef UNIX
   typedef unsigned short PType;
#else
   typedef wchar_t PType;
#endif
   const PType *p, *ep;
   HANDLE hStop;
};

class OPList : public std::list<PacketOperator>
{
public:
   OPList() {}
};

bool SockStream::CopyUntill(std::wstring *value, wchar_t sym)
{
   value->clear();

   while( true )
   {
      while( p < ep )
      {
         wchar_t cs = (*p);
         if( cs == sym )
         {
            p++;
            return true;
         }

         value->append(1, cs);
         p++;
      }

      if( !socket->Read(&buf, timeout, hStop) )
         return false;

      p = (const PType*)((const BYTE*)(buf));
      ep = (const PType*)((const BYTE*)(buf) + buf.Size());
      if( (buf.Size() % 2) != 0 ) ep++;
   }
}

bool SockStream::ReadBuffer(BYTE* outBuf, DWORD size)
{
   DWORD rb = (DWORD)((ep - p) * sizeof(PType));

   if( (this->buf.Size() % 2) != 0 ) rb--;
   if( rb > size ) rb = size;
   if( rb > 0 )
   {
		memcpy(outBuf, p, rb);
		outBuf += rb;
      size -= rb;
   }

	return (size != 0) ? socket->ReadBuf(outBuf, size, timeout, hStop) : true;
}

static bool ReadingGRPacket(SockStream *stream, OPList *operations, Binary *packet)
{
   std::wstring opStr;

	if (!stream->CopyUntill(&opStr, PacketOperator::Separator))
	{
		return false; // GRPACKET
	}

   PacketOperator packetTag(opStr);
	//if (packetTag.name.compare(PACKET_TAG) != 0)
	//{
	//	return false;
	//}

   DWORD packetSize = _wtoi(packetTag.value.c_str());

   while( true )
   {
		if (!stream->CopyUntill(&opStr, PacketOperator::Separator))
		{
			return false;
		}

      if( opStr.compare(HEAD_END_TAG) == 0 )
         break;
      operations->push_back(PacketOperator(opStr));
   }

   BYTE *buf = packet->Alloc(packetSize);
   return stream->ReadBuffer(buf, packetSize);
}

bool ExchangeList::Read(Binary* buf, Socket* socket, DWORD timeout, HANDLE evStop, IServObjectCreator* creator, bool pushEmptyObjects)
{
   bool ret = false;

	BYTE tag[sizeof(PACKET_TAG_U) - 1];

	if (socket->ReadBuf(tag, sizeof(PACKET_TAG_U) - 1, timeout, evStop) && memcmp(tag, PACKET_TAG_U, sizeof(PACKET_TAG_U) - 1) == 0)
   {
      OPList operations;
      Binary packet;
      SockStream sstream(socket, evStop, PACKET_RCV_TIMEOUT);

      if( ReadingGRPacket(&sstream, &operations, &packet) )
      {
         // Restore packet
         Binary pkt2, *curr = &packet, *next = &pkt2;

         list<PacketOperator>::const_iterator i = operations.begin();
         for( ; i != operations.end(); i++ )
         {
            if( i->name.compare(GZIP_OPT) == 0 )
            {
               DWORD size = _wtol(i->value.c_str());

					if (!Decompress(next, *curr, size))
					{
						readError = "decompress error";
						return false;
					}

               Binary *tp = curr;
               curr = next;
               tp->Clear();
            } else if( i->name.compare(CRC_OPT) == 0 )
            {
               DWORD crc = _wtoi(i->value.c_str());
					if (crc != CRC32(*curr))
					{
						readError = "CRC error";
						return false;
					}
            }
         }

#ifdef UNIX
         const unsigned short *start = (*curr);
         const unsigned short *end = start + curr->Size() / sizeof(unsigned short);
#else
         const wchar_t *start = (*curr);
         const wchar_t *end = start + curr->Size() / sizeof(wchar_t);
#endif

         ParseStreamU ps(start, end);
         ret = Read(ps, creator, pushEmptyObjects);
		}
		else
		{
			if (readError = NULL)
				readError = "grpacket error";
		}
	}
	else
	{
		readError = "can't read or packet tag";
	}

   return ret;
}

bool ExchangeList::Write(Socket* socket)
{
   OutStream outStream;
   ToString(&outStream);

   bool retVal = true;
   if( outStream.Size() )
   {
      Packet* pkt = Packet::MakePacket(outStream, (outStream.IsNeedCompress()) ? GZIP_OPT : L"");
      retVal = (pkt != NULL &&  socket->Write(*pkt));
      delete pkt;
      outStream.Clear();
   }

   return retVal;
}

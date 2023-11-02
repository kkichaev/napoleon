/*
 * Copyright (C), 2009, Денис Мосягин
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

#include <sstream>


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

#ifdef ENCODE_CONNECTION

#include "tomcrypt.h"
struct EncIniter {
public:
   EncIniter() {
      register_cipher(&aes_desc);
      register_prng(&sprng_desc);
      register_hash(&sha256_desc);
      init_LTM();
   }
};
static EncIniter initer;

const size_t BLOCK_SIZE = 1024;
static size_t pkcs7_pad(unsigned char* buf, size_t nb, int block_length, bool is_padding)
{
   unsigned char padval;
   //off_t idx;

   if (is_padding) {
      padval = (unsigned char)(block_length - (nb % block_length));
      padval = padval ? padval : block_length;

      memset(buf + nb, padval, padval);
      return nb + padval;
   }
   else {
      return nb - buf[nb - 1];
   }
}

Binary* DecodePacket(const Binary& srcB, const unsigned char* key)
{
   symmetric_CBC cbc;
   DWORD keyLen = 32;
   int rc = cbc_start(find_cipher("aes"), key + keyLen, key, keyLen, 0, &cbc);
   if (rc != CRYPT_OK)
   {
      return NULL;
   }

   Binary* res = new Binary();
   DWORD curLen = srcB.Size();
   unsigned char* dest = res->Alloc(curLen);
   unsigned char* src = (unsigned char*)((const BYTE*)srcB);

   DWORD outLen = 0;
   while (curLen > 0)
   {
      DWORD cb = BLOCK_SIZE;
      if (curLen <= cb)
         cb = curLen;

      cbc_decrypt(src, dest, cb, &cbc);
      if (curLen <= BLOCK_SIZE)
      {
         curLen = 0;
         size_t padcb = pkcs7_pad(dest, cb, aes_desc.block_length, false);
         if (padcb == 0 || padcb >= cb)
         {
            delete res;
            res = NULL;
         }
         else
         {
            cb = padcb;
         }
      }
      else
      {
         curLen -= cb;
         src += cb;
         dest += cb;
      }

      outLen += cb;
   }

   cbc_done(&cbc);

   res->ReduceSize(outLen);
   return res;
}

Binary* EncodePacket(const Binary& srcB, const unsigned char* key)
{
   symmetric_CBC cbc;
   DWORD keyLen = 32;
   int rc = cbc_start(find_cipher("aes"), key + keyLen, key, keyLen, 0, &cbc);
   if (rc != CRYPT_OK)
   {
      return NULL;
   }

   Binary* res = new Binary();
   DWORD curLen = (srcB.Size() / BLOCK_SIZE + 1) * BLOCK_SIZE + MAXBLOCKSIZE;

   size_t outLen = 0;
   size_t srcLen = srcB.Size();
   unsigned char* dest = res->Alloc(curLen);
   unsigned char* src = (unsigned char*)((const BYTE*)srcB);
   while (srcLen > 0)
   {
      DWORD cb = BLOCK_SIZE;
      if (srcLen <= cb)
      {
         cb = srcLen;

         unsigned char* tbuf = (unsigned char*)malloc(cb + MAXBLOCKSIZE);

         memcpy(tbuf, src, cb);
         cb = pkcs7_pad(tbuf, cb, aes_desc.block_length, true);

         cbc_encrypt(tbuf, dest, cb, &cbc);

         free(tbuf);
         srcLen = 0;
      }
      else
      {
         cbc_encrypt(src, dest, cb, &cbc);

         srcLen -= cb;
         src += cb;
         dest += cb;
      }

      outLen += cb;
   }

   cbc_done(&cbc);

   res->ReduceSize(outLen);
   return res;
}

bool ExchangeList::EncryptConnection(Socket* socket, Binary* pubKey)
{
   rsa_key pk;
   const BYTE* pb = *pubKey;
   int res = rsa_import(pb, pubKey->Size(), &pk);
   if (res != CRYPT_OK)
   {
      return false;
   }

   int prng = find_prng("sprng");

   DWORD keySize = 32;
   DWORD codeLen = keySize + aes_desc.block_length;
   BYTE* code = (BYTE*)malloc(codeLen);

   prng_state state;
   sprng_start(&state);
   sprng_ready(&state);
   sprng_read(code, codeLen, &state);
   sprng_done(&state);

   Binary* bres = new Binary();
   BYTE* out = bres->Alloc(300);
   DWORD len = bres->Size();

   int hs = find_hash("sha256");
   res = rsa_encrypt_key(code, keySize + aes_desc.block_length, out, &len, NULL, 0, 0, prng, hs, &pk);
   rsa_free(&pk);

   if (res != CRYPT_OK)
   {
      delete bres;
      free(code);
      return false;
   }

   bres->ReduceSize(len);

   std::wstringstream str;
   str << PACKET_TAG << L"(" << len << L");"<< GETSK << ";"<< HEAD_END_TAG<< ";";

   socket->Write(str.str());
   socket->Write(*bres);

   delete bres;

   fmtList->cryptData = code;
   return true;
}

#else

Binary* DecodePacket(const Binary& srcB, const unsigned char* key)
{
	return NULL;
}

Binary* EncodePacket(const Binary& srcB, const unsigned char* key)
{
	return NULL;
}

bool ExchangeList::EncryptConnection(Socket* socket, Binary* pubKey)
{
	return false;
}
#endif	

bool ExchangeList::Read(Socket* socket, DWORD timeout, HANDLE evStop, IServObjectCreator* creator, bool pushEmptyObjects)
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
            else if (i->name.compare(CRYPT_TAG) == 0) 
            {
               if (fmtList->cryptData != NULL) {
                  
                  //printf("Crypt packet rcv\n");
                  
                  Binary* res = DecodePacket(*curr, fmtList->cryptData);
                  if (res == NULL) {
                     readError = "Fail to decript packet";
                     
                     //printf("%s\n", readError);

                     return false;
                  }
                  curr->Clear();
                  curr = res;
               }
               else {
                  readError = "No crypt data";
                  return false;
               }
            }
            else if (i->name.compare(REQSK) == 0) {
               bool res = EncryptConnection(socket, curr);
               curr->Clear();
               if (!res)
               {
                  return false;
               }
               
               // well encrypt, now read packet
               return Read(socket, timeout, evStop, creator, pushEmptyObjects);
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
      Packet* pkt = Packet::MakePacket(outStream, (outStream.IsNeedCompress()) ? GZIP_OPT : L"", fmtList->cryptData);
      retVal = (pkt != NULL &&  socket->Write(*pkt));
      delete pkt;
      outStream.Clear();
   }

   return retVal;
}

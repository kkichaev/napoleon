/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Out Stream
 *
 * ert   22/09/2009   creating
 */
#include "stdafx.h"
#include "OutStream.h"
#include <zlib.h>

#include <servobj.h>


void OutStream::AppendQuoted(const std::wstring& tstr)
{
   Append(L'"');

   std::wstring::const_iterator i = tstr.begin();
   std::wstring::const_iterator ei = tstr.end();

   for( ; i != ei; i++ )
   {
      wchar_t sym = (*i);
      switch( sym )
      {
         case L'\\': Append(L"\\\\"); break;
         case L'/':  Append(L"\\/"); break;
         case L'"':  Append(L"\\\""); break;
         case L'\b': Append(L"\\b"); break;
         case L'\f': Append(L"\\f"); break;
         case L'\n': Append(L"\\n"); break;
         case L'\r': Append(L"\\r"); break;
         case L'\t': Append(L"\\t"); break;
         default: Append(sym); break;
      }
   }
   Append(L'"');
}

void OutStream::Append(const BYTE* buf, DWORD len)
{
   wchar_t tbuf[200];
   wsprintfW(tbuf, L"%d:", len);
   str.append(tbuf);

   if( len > 0 )
   {
      str.append((const wchar_t*)buf, len / 2);
      if( (len % 2) != 0 )
         Append((wchar_t)buf[len-1]);
   }
}

void OutStream::Append(unsigned long value, DWORD scale, bool blw0)
{
   wchar_t buf[200];
   wchar_t *p = buf;

   if( blw0 ) *p++ = L'-';
   if( scale != 0 )
   {
      DWORD count = 1, base = 10;
      while( base < scale ) { count++; base = base * 10; }
      wsprintfW(p, L"%lu.%0*lu", value / scale, count, value % scale);
   } else
      wsprintfW(p, L"%lu", value);

   str.append(buf);
}

void OutStream::Append(long value, int scale)
{
   if( value < 0 ) Append(-value, scale, true);
   else Append(value, scale, false);
}

void OutStream::Append(double value, int fraction)
{
   wchar_t buf[200];

   if( fraction == 0 )
   {
#ifdef _WIN32_WCE
      swprintf(buf, L"%d", (int)((value > 0) ? (value + 0.5) : (value - 0.5)));
#else
      if( (value >= 0 && value < 1000000000) || (value < 0 && value > -1000000000 ) )
         _swprintf(buf, L"%d", (int)((value > 0) ? (value + 0.5) : (value - 0.5)));
      else
      {
         __int64 ival;
         wchar_t sig[2];
         if( value < 0 )
         {
            *sig = L'-';
            *(sig+1) = 0;
            ival = (__int64)(-(value - 0.5));
         } else
         {
            *sig = '\0';
            ival = (__int64)(value + 0.5);
         }
         _swprintf(buf,  L"%s%d%09u", sig, (DWORD)(ival / 1000000000), (DWORD)(ival % 1000000000));
      }

#endif
   }
	else
	{
#ifdef _WIN32_WCE
		swprintf(buf, L"%.*f", fraction, value);
#else
#ifdef VS14
		if (isnan(value) || isinf(value))
			value = 0;
#endif
		//swprintf(buf, L"%.*f", fraction, value);
		_swprintf_l(buf, sizeof(buf) / sizeof(buf[0]), L"%.*f", GRServer::ServObject::GetLocale(), fraction, value);
#endif
		//// brute force
		//wchar_t *cp = wcschr(buf, L',');
		//if (cp != NULL)
		//	*cp = L'.';
	}

   str.append(buf);
}

void OutStream::Append(const FILETIME& ft, bool date, bool time)
{
   wchar_t buf[100];
   SYSTEMTIME st;
   FileTimeToSystemTime(&ft, &st);

   if( date )
   {
      wsprintfW(buf, L"%d-%02d-%02d", st.wYear, st.wMonth, st.wDay);
      str.append(buf);
   }

   if( time )
   {
      if( date ) str.append(1, L' ');
      wsprintfW(buf, L"%02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);
      str.append(buf);
   }
}

//
//------------------------------- Make Packet --------------------------------
//
Binary* Compress(const Binary &srcBuf)
{
   z_stream stream;

   stream.zalloc = NULL;
   stream.zfree = NULL;
   stream.opaque = NULL;

   Binary *destBuf = new Binary();

   deflateInit(&stream, Z_BEST_COMPRESSION);

   BYTE *src = (BYTE*)(const char*)srcBuf;
   stream.avail_in = srcBuf.Size();
   stream.next_in = src;

   int size = srcBuf.Size() + sizeof(stream.adler);
   if( size < 1000 ) size = 1000;

   BYTE *dest = (BYTE*)destBuf->Alloc(size);
   stream.avail_out = size - sizeof(stream.adler);
   stream.next_out = dest;

   bool res = (deflate(&stream, Z_FINISH) == Z_STREAM_END && stream.avail_out != 0);
	if (!res)
	{
		delete destBuf;
		destBuf = NULL;
	}
	else
	{
		DWORD outb = size - stream.avail_out;
		ToStreamBytes(dest + outb, (DWORD)stream.adler);
		destBuf->ReduceSize(outb + sizeof(stream.adler));
	}
   deflateEnd(&stream);

   return destBuf;
}



//
// return NULL если все сделано
//
static Binary* MakeOp(const Binary& src, const std::wstring& op, std::wstring* head)
{
   Binary* dest = NULL;
   wchar_t buf[200];

	bool useGZip = (op.compare(GZIP_OPT) == 0) && (src.Size() > 100);
	if (useGZip)
   {
      if( (dest = Compress(src)) != NULL )
      {
         wsprintfW(buf, GZIP_OPT L"(%d);", src.Size());
         (*head) = (const std::wstring&)buf + (*head);
		}
		else
			useGZip = false;
   } 
	if (!useGZip)
   {
      int crc = CRC32(src);
      wsprintfW(buf, CRC_OPT L"(%d);", crc);
      (*head) = (const std::wstring&)buf + (*head);
   }

   return dest;
}

//static unsigned pcktCount = 0;

Packet* Packet::MakePacket(OutStream& stream, const wchar_t* opts, const unsigned char* key)
{
	const std::wstring& str = stream.ToString();

	//char tbuf[50];
	//sprintf(tbuf, "pckt%d.txt", pcktCount++);
	//FILE *wr = fopen(tbuf, "wb");
	//if (wr)
	//{
	//	fwrite(str.c_str(), str.size() * sizeof(wchar_t), 1, wr);
	//	fclose(wr);
	//}

   std::wstring head;
   Binary *current = new Binary();
   DWORD cb = (DWORD)str.size();
#ifdef UNIX
   unsigned short* pt = (unsigned short*)current->Alloc(cb * sizeof(unsigned short));
   ConvHelper((const char*)str.c_str(), (char*)pt, cb * sizeof(wchar_t), cb * sizeof(unsigned short), "UTF32", "UTF16");
#else
   wmemcpy((wchar_t*)current->Alloc(cb * sizeof(wchar_t)), str.c_str(), cb);
#endif
   stream.Clear();

   if( *opts )
   {
      const wchar_t *p = opts;
      const wchar_t *ep = p;
      while( ep != NULL )
      {
         ep = wcschr(p, L';');
         std::wstring op;
         if( ep == NULL ) op.assign(p);
         else op.assign(p, ep - p);

         Binary *next = MakeOp(*current, op, &head);
         if( next != NULL )
         {
            delete current;
            current = next;
         }
      }
   }

   if (key != NULL)
   {
      //printf("Encode packet \n");

      Binary* next = EncodePacket(*current, key);
      if (next != NULL)
      {
         delete current;
         current = next;
         std::wstring tag(CRYPT_TAG); tag += L";";
         head = tag + head;
      }
   }
   else
   {
      if (head.find(GZIP_OPT) == std::wstring::npos)
      {
         Binary* next = MakeOp(*current, CRC_OPT, &head);
         if (next != NULL)
         {
            delete current;
            current = next;
         }
      }
   }

   wchar_t buf[200];
   wsprintfW(buf, PACKET_TAG L"(%d);", current->Size());
   head = (const std::wstring&)buf + head + L"DATA;";

   Packet *p = new Packet();
   p->head = head;
   p->data = current;

   return p;
}

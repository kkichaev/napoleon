/*
 * Copyright (C), 2009, ����� �������
 *
 * Out Stream
 *
 * ert   22/09/2009   creating
 */
#include "stdafx.h"
#include <iomanip>
#include "OutStream.h"
#include <zlib.h>

#include <servobj.h>

void OutStream::Append(const wchar_t* tstr) 
{
   size_t cb = wcslen(tstr);
   char16_t* buf = new char16_t [cb + 1];

   ConvHelper((const char*)tstr, (char*)buf, cb * sizeof(wchar_t), (cb + 1) * sizeof(unsigned short), "UTF32", "UTF16");

   str.append(buf, cb); 
   delete buf;
}

void OutStream::Append(wchar_t tsym) 
{
   wchar_t src[2];
   char16_t dst[2];
   src[0] = tsym;
   src[1] = 0;

   ConvHelper((const char*)src, (char*)dst, 1 * sizeof(wchar_t), 2 * sizeof(unsigned short), "UTF32", "UTF16");

   str.append(1, *dst); 
}

void OutStream::AppendQuoted(const std::wstring& tstr)
{
   size_t cb = tstr.size();
   char16_t* buf = new char16_t [cb + 1];

   ConvHelper((const char*)tstr.c_str(), (char*)buf, cb * sizeof(wchar_t), (cb + 1) * sizeof(unsigned short), "UTF32", "UTF16");
   str.append(1, u'"');
   const char16_t *ptr = buf, *ep = buf + cb;
   while( ptr < ep ) 
   {
      switch( *ptr )
      {
         case u'\\': str.append(u"\\\\"); break;
         case u'/':  str.append(u"\\/"); break;
         case u'"':  str.append(u"\\\""); break;
         case u'\b': str.append(u"\\b"); break;
         case u'\f': str.append(u"\\f"); break;
         case u'\n': str.append(u"\\n"); break;
         case u'\r': str.append(u"\\r"); break;
         case u'\t': str.append(u"\\t"); break;
         default: str.append(1, *ptr); break;
      }
      ptr++;
   }
   str.append(1, u'"');

   delete buf;

   // Append(L'"');

   // std::wstring::const_iterator i = tstr.begin();
   // std::wstring::const_iterator ei = tstr.end();

   // for( ; i != ei; i++ )
   // {
   //    wchar_t sym = (*i);
   //    switch( sym )
   //    {
   //       case u'\\': str.append(1, u"\\\\"); break;
   //       case u'/':  Append(u"\\/"); break;
   //       case u'"':  Append(u"\\\""); break;
   //       case u'\b': Append(u"\\b"); break;
   //       case u'\f': Append(u"\\f"); break;
   //       case u'\n': Append(u"\\n"); break;
   //       case u'\r': Append(u"\\r"); break;
   //       case u'\t': Append(u"\\t"); break;
   //       default: Append(sym); break;
   //    }
   // }
   // Append(L'"');
}

void OutStream::Append(const BYTE* buf, DWORD len)
{
   std::stringstream stream;
   stream << len << ':';
   Append(stream.str());
   if( len > 0 )
   {
      str.append((const char16_t*)buf, len / sizeof(char16_t));
      unsigned rest = len % sizeof(char16_t);
      if( rest != 0 )
      {
         char16_t wsyn = 0;
         char *dp = (char*)&wsyn;
         char *sp = (char*)buf + len - rest;
         while(rest--) *dp++ = *sp++;
         str.append(1, wsyn);
      }
   }

   // wchar_t tbuf[200];
   // wsprintfW(tbuf, L"%d:", len);
   // str.append(tbuf);

   // if( len > 0 )
   // {
   //    str.append((const wchar_t*)buf, len / sizeof(wchar_t));
   //    unsigned rest = len % sizeof(wchar_t);
   //    if( rest != 0 )
   //    {
   //       wchar_t wsyn = 0;
   //       char *dp = (char*)&wsyn;
   //       char *sp = (char*)buf + len - rest;
   //       while(rest--) *dp++ = *sp++;
   //       Append(wsyn);
   //    }
   // }
}

void OutStream::Append(unsigned long value, DWORD scale, bool blw0)
{
   std::stringstream stream;
   if(blw0)
      stream << '-';
   if( scale != 0 )
   {
      DWORD count = 1, base = 10;
      while( base < scale ) { count++; base = base * 10; }
      stream << value / scale << std::setfill('0') << std::setw(count) << value % scale;
   } else
      stream << value;
   Append(stream.str());   

   // wchar_t buf[200];
   // wchar_t *p = buf;

   // if( blw0 ) *p++ = L'-';
   // if( scale != 0 )
   // {
   //    DWORD count = 1, base = 10;
   //    while( base < scale ) { count++; base = base * 10; }
   //    wsprintfW(p, L"%lu.%0*lu", value / scale, count, value % scale);
   // } else
   //    wsprintfW(p, L"%lu", value);

   // str.append(buf);
}

void OutStream::Append(long value, int scale)
{
   if( value < 0 ) Append(-value, scale, true);
   else Append(value, scale, false);
}

void OutStream::Append(const std::string& src)
{
   size_t cb = src.size();
   char16_t* buf = new char16_t [cb + 1];

   ConvHelper((const char*)src.c_str(), (char*)buf, cb * sizeof(char), (cb + 1) * sizeof(unsigned short), "UTF8", "UTF16");

   str.append(buf, cb); 
   delete buf;

}

void OutStream::Append(double value, int fraction)
{
//   std::basic_stringstream<char16_t> stream;
   std::stringstream stream;
   if(fraction == 0)
   {
      int64_t val = (int64_t)((value > 0) ? (value + 0.5) : (value - 0.5));
      stream << val;
   }
   else
   {
      stream.precision(fraction);
      stream << std::fixed << value;
   }

   Append(stream.str());
   // const std::u16string& tstr = stream.str();
   // str.append(tstr);

//    wchar_t buf[200];

//    if( fraction == 0 )
//    {
// #ifdef _WIN32_WCE
//       swprintf(buf, L"%d", (int)((value > 0) ? (value + 0.5) : (value - 0.5)));
// #else
//       if( (value >= 0 && value < 1000000000) || (value < 0 && value > -1000000000 ) )
//          _swprintf(buf, L"%d", (int)((value > 0) ? (value + 0.5) : (value - 0.5)));
//       else
//       {
//          __int64 ival;
//          wchar_t sig[2];
//          if( value < 0 )
//          {
//             *sig = L'-';
//             *(sig+1) = 0;
//             ival = (__int64)(-(value - 0.5));
//          } else
//          {
//             *sig = '\0';
//             ival = (__int64)(value + 0.5);
//          }
//          _swprintf(buf,  L"%s%d%09u", sig, (DWORD)(ival / 1000000000), (DWORD)(ival % 1000000000));
//       }

// #endif
//    }
// 	else
// 	{
// #ifdef _WIN32_WCE
// 		swprintf(buf, L"%.*f", fraction, value);
// #else
// #ifdef VS14
// 		if (isnan(value) || isinf(value))
// 			value = 0;
// #endif
// 		//swprintf(buf, L"%.*f", fraction, value);
// 		swprintf(buf, sizeof(buf) / sizeof(buf[0]), L"%.*f", fraction, value);
// #endif
// 		//// brute force
// 		//wchar_t *cp = wcschr(buf, L',');
// 		//if (cp != NULL)
// 		//	*cp = L'.';
// 	}

//    str.append(buf);
}

void OutStream::Append(const FILETIME& ft, bool date, bool time)
{
   // wchar_t buf[100];
   SYSTEMTIME st;
   FileTimeToSystemTime(&ft, &st);

   if( date )
   {
      std::stringstream stream;
      stream << st.wYear << '-' << std::setfill('0') << std::setw(2) << st.wMonth << '-'
            << std::setfill('0') << std::setw(2) << st.wDay
         ;
      Append(stream.str());
      // wsprintfW(buf, L"%d-%02d-%02d", st.wYear, st.wMonth, st.wDay);
      // str.append(buf);
   }

   if( time )
   {
      if( date ) str.append(1, u' ');
      std::stringstream stream;
      stream << std::setfill('0') << std::setw(2)<< st.wHour << ':' 
            << std::setfill('0') << std::setw(2) << st.wMinute << ':'
            << std::setfill('0') << std::setw(2) << st.wSecond
         ;
      Append(stream.str());

      // wsprintfW(buf, L"%02d:%02d:%02d", st.wHour, st.wMinute, st.wSecond);
      // str.append(buf);
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
// return NULL ���� ��� �������
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

Packet* Packet::MakePacket(OutStream& stream, const wchar_t* opts)
{
	const std::u16string& str = stream.ToString();

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
   memcpy(pt, str.c_str(), cb * sizeof(char16_t));

   // ConvHelper((const char*)str.c_str(), (char*)pt, cb * sizeof(wchar_t), cb * sizeof(unsigned short), "UTF32", "UTF16");
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

   if( head.find(GZIP_OPT) == std::wstring::npos )
   {
      Binary *next = MakeOp(*current, CRC_OPT, &head);
      if( next != NULL )
      {
         delete current;
         current = next;
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

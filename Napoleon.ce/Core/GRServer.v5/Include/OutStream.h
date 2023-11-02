/*
 * Copyright (C), 2009, ����� �������
 *
 * Out Stream
 *
 * ert   22/09/2009   creating
 */
#ifndef __OUT_STREAM_H
#define __OUT_STREAM_H

#include <string>
#include <Binary.h>

#define PACKET_TAG    L"GRPACKET"
#define PACKET_TAG_U  "G\x0R\x0P\x0""A\x0""C\x0K\x0""E\x0T\x0"
#define HEAD_END_TAG  L"DATA"
#define GZIP_OPT      L"GZIP"
#define CRC_OPT       L"CRC"

class OutStream
{
public:
   OutStream() : needCompress(true) {}

   void Append(const std::string& str);
   void Append(const wchar_t* tstr);
   void Append(wchar_t tsym);

   void Append(long value, int scale);
   void Append(unsigned long value, int scale) { Append(value, scale, false); }
   void Append(double value, int fraction);

   void Append(const BYTE* tstr, DWORD len);

   void Append(const FILETIME& ft, bool date, bool time);

   void AppendQuoted(const std::wstring& tstr);

   void Append(const OutStream& stream) { str.append(stream.str); }
   void InsertToFront(const OutStream& stream) { str.insert(str.begin(), stream.str.begin(), stream.str.end()); }

   const std::u16string& ToString() const { return str; }

   void Clear() { str.clear(); needCompress = true; }

   DWORD Size() const { return (DWORD)(str.size() * sizeof(*str.c_str())); }

   void NeedCompress(bool compress) { needCompress = compress; }
   bool IsNeedCompress() const { return needCompress; }

protected:
   void Append(unsigned long value, DWORD scale, bool blw0);

protected:
   std::u16string str;
   bool needCompress;
};

struct Packet
{
   std::wstring head;
   Binary *data;

   ~Packet() { delete data; }

   // opts ��������� ����� ';'
   static Packet* MakePacket(OutStream& stream, const wchar_t* opts);
};

struct PacketOperator
{
   std::wstring name;
   std::wstring value;

   PacketOperator() {}

   PacketOperator(const std::wstring &op)
   {
      std::wstring::size_type fnd = op.find(L'(');

      if( fnd == std::wstring::npos )
         name.assign(op);
      else
      {
         std::wstring::size_type fnd2 = op.find(L')', fnd);

         name = op.substr(0, fnd);
         value = op.substr(fnd + 1, fnd2 - fnd - 1);
      }
   }

   static const wchar_t Separator = L';';
};

#endif

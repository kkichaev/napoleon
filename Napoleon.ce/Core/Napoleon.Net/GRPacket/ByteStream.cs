/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 * 
 * Библиотека для обмена пакетами с сервером
 * ByteStream
 * 
 * ert   14/11/2009   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.IO;
using ICSharpCode.SharpZipLib.Checksums;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using System.Threading;
using System.Net.Sockets;

namespace GRSoft.Network
{
   public class ByteStream
   {
      internal static string PACKET_STR_TAG = "GRPACKET";
      internal static string DATA_TAG = "DATA";

      internal static Byte[] PACKET_TAG = Encoding.Unicode.GetBytes(PACKET_STR_TAG);


      int curSym = 0;
      Byte[] bytes;

      public ByteStream(Byte[] bytes)
      {
         this.bytes = bytes;
      }

      public ByteStream(Packet packet)
      {
         CopyBytes(packet);
      }

      public ByteStream(PacketObject po)
      {
         Packet p = new Packet();
         po.ToPacket(p);
         CopyBytes(p);
      }

      private void CopyBytes(Packet packet)
      {
         bytes = new byte[packet.Size];
         int offset = 0;
         foreach (Byte[] pckt in packet)
         {
            int len = pckt.Length;
            Array.Copy(pckt, 0, bytes, offset, len);
            offset += len;
         }
      }

      public byte[] CopyBytes()
      {
         byte[] ret = new byte[bytes.Length];
         Array.Copy(bytes, ret, bytes.Length);
         return ret;
      }

      public bool EOS
      {
         get
         {
            return (bytes == null) ? true : (curSym >= bytes.Length);
         }
      }

      public char Current()
      {
         return GetChar(curSym);
      }

      public char Next()
      {
         return GetChar(curSym + 2);
      }

      char GetChar(int pos)
      {
         if (!EOS)
         {
            if (pos <= bytes.Length - 2)
               return Encoding.Unicode.GetChars(bytes, pos, 2)[0];

            if (pos == bytes.Length - 1)
            {
               Byte[] b = new Byte[2];
               b[0] = bytes[pos];
               b[1] = (byte)0;

               return Encoding.Unicode.GetChars(b)[0];
            }
         }
         return '\0';
      }

      public void MoveToFirst()
      {
         curSym = 0;
      }

      public bool MoveNext()
      {
         if (EOS) return false;

         if (curSym < bytes.Length - 2)
            curSym += 2;
         else
            curSym = bytes.Length;
         return true;
      }

      public bool EatWhite()
      {
         while (!EOS)
         {
            char sym = Current();
            if (!Char.IsWhiteSpace(sym)) return true;

            MoveNext();
         }

         return false;
      }

      public bool CopyUntill(out string res, char stopSym)
      {
         StringBuilder str = new StringBuilder();

         while (!EOS)
         {
            char sym = Current();
            if (sym == stopSym) break;

            str.Append(sym);
            MoveNext();
         }

         res = str.ToString();
         return !EOS;
      }

      internal bool CopyBytes(byte[] dest)
      {
         int sz = dest.Length;
         if (bytes.Length - curSym < sz) return false;

         Array.Copy(bytes, curSym, dest, 0, sz);

         curSym += sz;
         if (sz % 2 != 0) curSym++;

         return true;
      }

      static bool WaitPacket(Stream stream)
      {
         Byte[] buf = new Byte[PACKET_TAG.Length];
         while (true)
         {
            int i;
            int len = stream.Read(buf, 0, buf.Length);
            if (len < buf.Length)
               return false;

            for (i = PACKET_TAG.Length - 1; i >= 0; i--)
               if (buf[i] != PACKET_TAG[i]) break;

            if (i < 0) break;
         }

         return true;
      }

      static bool ReadTill(out string str, Stream stream, char stopSym)
      {
         StringBuilder sb = new StringBuilder();

         Byte[] buf = new Byte[2];

         str = "";
         while (true)
         {
            if (stream.Read(buf, 0, 2) != 2) return false;
            char sym = Encoding.Unicode.GetChars(buf)[0];

            if (sym == stopSym) break;
            sb.Append(sym);
         }

         str = sb.ToString();
         return true;
      }

      internal static bool ReadOptions(List<PacketOption> ops, Stream stream)
      {
         if (!WaitPacket(stream)) return false;

         // get packet options
         while (true)
         {
            string str;
            if (!ReadTill(out str, stream, ';')) return false;

            if (str.CompareTo(DATA_TAG) == 0) break;

            PacketOption op = new PacketOption(str);
            ops.Add(op);
         }

         return (ops.Count != 0);
      }

      public void Send(Stream stream, string operations)
      {
         string head = DATA_TAG + ";";
         if (operations.IndexOf(PacketObject.GZIP_TAG) < 0 && operations.IndexOf(PacketObject.CRC_TAG) < 0)
            operations += PacketObject.CRC_TAG;

         int sp = 0;
         int len = operations.Length;
         while (true)
         {
            int ep = operations.IndexOf(':', sp);
            string op = operations.Substring(sp, (ep < 0) ? len - sp : ep - sp);

            string param;
            bytes = PacketOperator.Encode(bytes, op, out param);
            head = param + head;

            if (ep < 0) break;
            sp = ep + 1;
         }

         string pktTag = PACKET_STR_TAG + "(" + bytes.Length.ToString() + ");";
         head = pktTag + head;

         byte[] headBytes = Encoding.Unicode.GetBytes(head);

         stream.Write(headBytes, 0, headBytes.Length);
         stream.Write(bytes, 0, bytes.Length);
      }

      public static ByteStream Receive(Stream stream, IProgress progress)
      {
         List<PacketOption> ops = new List<PacketOption>();
         if (!ReadOptions(ops, stream)) return null;

         int packetSize = Int32.Parse(ops[0].value);
         ops.RemoveAt(0);

         Byte[] packet = new Byte[packetSize];

         bool setProgress = false;
         if (progress != null && packetSize > 20000)
         {
            progress.SetText(String.Format("Чтение данных {0} КБ", packetSize / 1024));
            progress.SetMax(packetSize);
            setProgress = true;
         }

         int offset = 0;
         NetworkStream ns = stream as NetworkStream;
         while (packetSize > 0)
         {
            int to = ns.ReadTimeout / 10;
            while (!ns.DataAvailable && to > 0)
            {
               Thread.Sleep(10);
               to -= 10;
            }

            int readed = stream.Read(packet, offset, packetSize);
            if (readed == 0)
               break;

            if (setProgress)
               progress.AdvancePos(readed);

            packetSize -= readed;
            offset += readed;
         }

         if (packetSize > 0) return null;

         return DecodePacket(packet, ops);
      }

      private static ByteStream DecodePacket(byte[] packet, List<PacketOption> ops)
      {
         foreach (PacketOption op in ops)
         {
            packet = PacketOperator.Decode(packet, op);
            if (packet == null) break;
         }

         return (packet != null) ? new ByteStream(packet) : null;
      }
   }

   delegate byte[] Decoder(byte[] src, string value);
   delegate byte[] Encoder(byte[] src, out string value);
   class PacketOperator
   {
      struct Operator
      {
         internal string name;
         internal Decoder decoder;
         internal Encoder encoder;


         internal Operator(string n, Decoder decode, Encoder encoder)
         {
            this.name = n;
            this.decoder = decode;
            this.encoder = encoder;
         }
      }

      static Operator[] operators = new Operator[]
      {
         new Operator(PacketObject.GZIP_TAG, Decompress, Compress),
         new Operator(PacketObject.CRC_TAG, CheckCRC32, SetCRC32)
      };

      static byte[] Compress(byte[] src, out string value)
      {
         int len = src.Length;
         if (len < 1024) len = 1024;
         byte[] dest = new byte[len];
         MemoryStream ms = new MemoryStream(dest);

         DeflaterOutputStream compress = new DeflaterOutputStream(ms);
         try
         {
            compress.Write(src, 0, src.Length);
            compress.Finish();
            Array.Resize(ref dest, (int)compress.Position);
         }
         catch
         {
         }

         value = "(" + src.Length.ToString() + ")";
         return dest;
      }

      static byte[] Decompress(byte[] src, string value)
      {
         byte[] dest = new byte[int.Parse(value)];

         MemoryStream ms = new MemoryStream(src);
         Stream decompress = new InflaterInputStream(ms);

         try
         {
            decompress.Read(dest, 0, dest.Length);
         }
         catch
         {
            dest = null;
         }
         return dest;
      }

      static byte[] SetCRC32(byte[] src, out string value)
      {
         Crc32 crc = new Crc32();
         crc.Update(src);
         int crcVal = (int)crc.Value;
         value = "(" + crcVal.ToString() + ")";

         return src;
      }

      static byte[] CheckCRC32(byte[] src, string value)
      {
         Crc32 crc = new Crc32();
         crc.Update(src);
         int c1 = (int)crc.Value;
         int c2 = int.Parse(value);
         if (c1 != c2)
            return null;
         return src;
      }

      internal static byte[] Decode(byte[] src, PacketOption option)
      {
         byte[] dest = null;
         foreach (Operator op in operators)
         {
            if (op.name.CompareTo(option.name) == 0)
            {
               dest = op.decoder(src, option.value);
               break;
            }
         }

         return dest;
      }

      internal static byte[] Encode(byte[] src, string opName, out string param)
      {
         param = "";
         byte[] dest = null;
         foreach (Operator op in operators)
         {
            if (op.name.CompareTo(opName) == 0)
            {
               string val = "";
               dest = op.encoder(src, out val);
               param = opName + val + ";";
               break;
            }
         }

         return dest;
      }
   }

   struct PacketOption
   {
      internal PacketOption(string str)
      {
         int pos = str.IndexOf('(');
         if (pos < 0)
         {
            name = str;
            value = "";
         }
         else
         {
            int ep = str.IndexOf(')', pos + 1);
            if (ep < 0) ep = str.Length - 1;

            name = str.Substring(0, pos);
            value = str.Substring(pos + 1, ep - pos - 1);
         }
      }

      internal string name;
      internal string value;
   }
}
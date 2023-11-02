/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 * 
 * Библиотека для обмена пакетами с сервером
 * 
 * ert   12/11/2009   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.ComponentModel;

namespace GRSoft.Network
{
   public class Member
   {
      public double ToDouble()
      {
         TypeConverter tc =  TypeDescriptor.GetConverter(value);
         if (tc.CanConvertTo(typeof(double)))
         {
            return (double)tc.ConvertTo(value, typeof(double));
         }
         return 0;
      }

      public bool ToBool()
      {
         if (value is Boolean)
            return (bool)value;
         return false;
      }

      public Byte[] ToBytes()
      {
         return value as Byte[];
      }

      public override string ToString()
      {
         if (value is string) return value.ToString();
         return "";
      }

      public DateTime ToDateTime()
      {
         if (value is DateTime) return (DateTime)value;
         return new DateTime();
      }

      public ObjectList ToObjectList()
      {
         return value as ObjectList;
      }

      public object Value
      {
         get { return value; }
         set { this.value = value; }
      }

      object value;
   }

   public class Object : List<Member>
   {
      bool empty = false;

      public Object(Format format)
      {
         this.format = format;
         InitMembers();
      }

      public bool IsEmpty { get { return empty; } }

      public string Name { get { return format.Name; } }

      public Format Format
      {
         get { return format; }
      }

      public Member this[string name]
      {
         get
         {
            return GetMember(name);
         }

         set
         {
            Member m = GetMember(name);
            if (m != null)
               m.Value = value;
         }
      }

      public Member GetMember(string name)
      {
         int index = 0;
         foreach (MemberFormat mf in format)
         {
            if (mf.name.CompareTo(name) == 0)
            {
               return this[index];
            }

            index++;
         }

         return null;
      }

      public void ToPacket(Packet packet)
      {
         StringBuilder str = new StringBuilder();
         str.Append("[");

         int index = 0;
         foreach (MemberFormat mf in format)
         {
            if (index != 0) str.Append(",");

            Member m = this[index];
            mf.MemberToPacket(m, str, packet);
            index++;
         }

         str.Append("]");
         packet.Add(str.ToString());
/*
#if DEBUG
         File.AppendAllText("packet.txt", str.ToString() + "\n");
#endif
*/
      }

      public Object AddObject(string fieldName)
      {
         Member f = GetMember(fieldName);
         if (f == null)
            return null;

         if (f.Value == null)
         {
            Format cf = Format.Find(format.Name + '$' + fieldName);
            ObjectList ol = new ObjectList(cf);
            f.Value = ol;
         }

         return (f.Value as ObjectList).AddObject();
      }

      void InitMembers()
      {
         foreach (MemberFormat mf in format)
         {
            Member m = null;
            switch (mf.Type)
            {
               case MemberType.Binary:
               case MemberType.Object:
                  m = new Member();
                  m.Value = null;
                  break;

               case MemberType.DateTime:
                  m = new Member();
                  m.Value = DateTime.Now;
                  break;

               case MemberType.Number:
                  m = new Member();
                  m.Value = (int)0;
                  break;

               case MemberType.String:
                  m = new Member();
                  m.Value = "";
                  break;
            }

            if (m != null)
               this.Add(m);
         }
      }

      Format format;

      internal static Object Read(Format f, ByteStream stream)
      {
         Object obj = new Object(f);
         if (!obj.ReadMembers(stream))
            obj = null;

         return obj;
      }

      private bool ReadMembers(ByteStream stream)
      {
         if (stream.Current() != '[') return false;

         // empty object;
         if (stream.Next() == ']')
         {
            stream.MoveNext();
            stream.MoveNext();

            empty = true;
            return true;
         }

         int index = 0;
         foreach (MemberFormat mf in format)
         {
            stream.MoveNext();
            Member m = this[index++];

            if (!mf.ReadMember(m, stream))
               break;

            char sym = stream.Current();
            if (sym == ']')
            {
               stream.MoveNext();
               break;
            }
            else if (sym != ',')
               break;
         }

         return (index == this.Count);
      }
   }

   /// <summary>
   /// Коллекция объектов одного формата
   /// </summary>
   public class ObjectList : List<Object>
   {
      public ObjectList(Format format) { this.format = format; }

      protected ObjectList() { }

      public void ToPacket(Packet packet)
      {
         if (this.Count > 0)
         {
            format.ToPacket(packet);
            MembersToPacket(packet);
         }
      }

      public static ObjectList Read(ByteStream stream)
      {
         ObjectList ol = null;
         Format f = Format.Read(stream);
         if (f != null)
         {
            ol = new ObjectList(f);
            ol.ReadObjects(stream);
         }

         return ol;
      }

      /// <summary>
      /// создает новый объект и добавляет его в список
      /// </summary>
      /// <returns>созданный объект</returns>
      virtual public Object AddObject()
      {
         Object o = new Object(format);
         Add(o);

         return o;
      }

      public string Name
      {
         get { return format.Name; }
      }

      public int FindField(string name)
      {
         return format.MemberIndex(name);
      }

      public Member this[string fieldName]
      {
         get
         {
            return (Count > 0) ? this[0][fieldName] : null;
         }
      }

      protected object GetMemberValue(int objIndex, string name)
      {
         Object obj = this[objIndex];
         Member m = obj[name];
         return (m == null) ? null : m.Value;
      }

      protected object GetMemberValue(string name)
      {
         return GetMemberValue(0, name);
      }

      protected void SetMemberValue(string name, object value)
      {
         SetMemberValue(0, name, value);
      }

      protected void SetMemberValue(int objIndex, string name, object value)
      {
         Object obj = this[objIndex];
         Member m = obj[name];
         if (m != null)
            m.Value = value;
      }

      protected Format format;

      internal void MembersToPacket(Packet packet)
      {
         foreach (Object obj in this)
         {
            obj.ToPacket(packet);
         }
      }

      internal bool ReadObjects(ByteStream stream)
      {
         while (stream.Current() == '[')
         {
            Object obj = Object.Read(format, stream);
            if (obj != null)
            {
               if (!obj.IsEmpty)
                  Add(obj);
            }
            else
               return false;
         }
         return true;
      }
   }

   public class PacketObject : List<ObjectList>
   {
      public static string GZIP_TAG = "GZIP";
      public static string CRC_TAG = "CRC";

      bool compress;

      public PacketObject()
      {
         compress = true;
      }

      public PacketObject(bool compress)
      {
         this.compress = compress;
      }

      public void ToPacket(Packet packet)
      {
         foreach (ObjectList obj in this)
         {
            obj.ToPacket(packet);
         }
      }

      public ObjectList this[string name]
      {
         get
         {
            foreach (ObjectList co in this)
            {
               if (co.Name == name) return co;
            }

            return null;
         }
      }

      public void Send(Stream netStream, string op)
      {
         ByteStream stream = new ByteStream(this);
         if (op.Length == 0)
            op = CRC_TAG;

         stream.Send(netStream, op);
      }

      public void Send(Stream netStream)
      {
         if( compress ) Send(netStream, GZIP_TAG);
         else Send(netStream, CRC_TAG);
      }

      public bool Receive(Stream netStream, IProgress progress)
      {
         ByteStream stream = ByteStream.Receive(netStream, progress);

         if (stream == null)
            return false;

         //byte[] b = stream.CopyBytes();
         //FileStream fs = new FileStream("d:\\out.txt", FileMode.Append);
         //fs.Write(b, 0, b.Length);
         //fs.Close();

         ObjectList contObjList = null;
         while (true)
         {
            ObjectList ol = ObjectList.Read(stream);
            if (ol != null)
            {
               if (contObjList != null)
               {
                  if (contObjList.Name == ol.Name)
                     contObjList.AddRange(ol);
                  contObjList = null;
                  continue;
               }

               if (ol.Name == "StreamContinue")
               {
                  PacketObject po = new PacketObject();
                  po.Add(new ServerCommand(Commands.DONE, ""));
                  po.Send(netStream, "");

                  stream = ByteStream.Receive(netStream, progress);
                  if (stream == null)
                     break;

                  if (Count > 0)
                     contObjList = this[Count - 1];
               }
               else
                  Add(ol);
            } 
            else
               break;
         }
         return stream.EOS;
      }
   }

   public class Packet : List<Byte[]>
   {
      public Packet() { }
      public Packet(Byte[] bytes) { this.Add(bytes); }
      public Packet(PacketObject objects) { objects.ToPacket(this); }

      #region instance member
      public void Add(string value)
      {
         Add(Encoding.Unicode.GetBytes(value));
      }

      public int Size
      {
         get
         {
            int size = 0;
            foreach (Byte[] item in this)
            {
               size += item.Length;
            }

            return size;
         }
      }
      #endregion
   }
}

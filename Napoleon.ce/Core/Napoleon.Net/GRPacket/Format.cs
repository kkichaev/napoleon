/*
 * Copyright (C), 2009 - 2010, Гильдия разработчиков
 * 
 * Библиотека для обмена пакетами с сервером
 * Формат и связанные классы
 * 
 * ert   14/11/2009   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using System.Reflection;
using System.IO;

namespace GRSoft.Network
{
   #region MemberFormat classes

   public enum MemberType { String, Number, DateTime, Object, Binary }

   public abstract class MemberFormat
   {
      public MemberFormat(string name) { this.name = name; }

      public abstract MemberType Type { get; }

      public abstract void ToStream(StringBuilder str);

      public abstract void MemberToPacket(Member m, StringBuilder str, Packet packet);

      public abstract bool Read(ByteStream stream);

      public abstract bool ReadMember(Member m, ByteStream stream);

      public string name;

      internal static MemberFormat Read(string memberName, string formatName, char sym, ByteStream stream)
      {
         MemberFormat mf = null;

         if (sym == ':')
         {
            if (stream.MoveNext())
            {
               sym = stream.Current();
               switch (sym)
               {
                  case 's':
                     mf = new StringFormat(memberName);
                     break;
                  case 'n':
                     mf = new NumberFormat(memberName);
                     break;
                  case 'b':
                     mf = new BinaryFormat(memberName);
                     break;
                  case 'd':
                     if (stream.Next() == 't')
                     {
                        stream.MoveNext();
                        mf = new StampFormat(memberName);
                     }
                     else
                        mf = new DateFormat(memberName);
                     break;
                  case 't':
                     mf = new TimeFormat(memberName);
                     break;
               }
            }
         }
         else if (sym == '[')
         {
            //if (stream.MoveNext())
            mf = new ObjectFormat(memberName, formatName);
         }

         if (mf != null && !mf.Read(stream))
            mf = null;

         return mf;
      }
   }

   public class StringFormat : MemberFormat
   {
      public StringFormat(string name) : base(name) { }
      public override MemberType Type { get { return MemberType.String; } }
      public override void ToStream(StringBuilder str) { str.Append(":s"); }
      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         AddQuotedString(str, m.ToString());
      }
      public override bool Read(ByteStream stream) { return true; }
      public override bool ReadMember(Member m, ByteStream stream)
      {
         string str;

         if (ReadString(out str, stream))
         {
            m.Value = str;
            return true;
         }
         return false;
      }

      bool ReadString(out string dest, ByteStream stream)
      {
         StringBuilder str = new StringBuilder();

         if (stream.Current() != '"')
         {
            dest = "";
            return false;
         }

         stream.MoveNext();

         bool error = false;
         while (!error)
         {
            if (stream.EOS) error = true;
            else
            {
               char sym = stream.Current();
               if (sym == '"')
                  break;
               if (sym == '\\')
               {
                  if (!stream.MoveNext()) error = true;
                  else
                  {
                     char sym1 = stream.Current();
                     switch (sym1)
                     {
                        case '\\': break;
                        case '/': sym = sym1; break;
                        case '"': sym = sym1; break;
                        case 'b': sym = '\b'; break;
                        case 'f': sym = '\f'; break;
                        case 'n': sym = '\n'; break;
                        case 'r': sym = '\r'; break;
                        case 't': sym = '\t'; break;
                        default:
                           str.Append(sym);
                           sym = sym1;
                           break;
                     }
                  }
               }
               if (!error)
               {
                  str.Append(sym);
                  stream.MoveNext();
               }
            }
         }
         dest = str.ToString();
         return (error) ? false : stream.MoveNext();
      }

      void AddQuotedString(StringBuilder dest, string src)
      {
         char[] srcSym = src.ToCharArray();

         dest.Append('"');
         foreach (char sym in srcSym)
         {
            switch (sym)
            {
               case '\\': dest.Append("\\\\"); break;
               case '/': dest.Append("\\/"); break;
               case '"': dest.Append("\\\""); break;
               case '\b': dest.Append("\\b"); break;
               case '\f': dest.Append("\\f"); break;
               case '\n': dest.Append("\\n"); break;
               case '\r': dest.Append("\\r"); break;
               case '\t': dest.Append("\\t"); break;
               default: dest.Append(sym); break;
            }
         }
         dest.Append('"');
      }
   }

   public class NumberFormat : MemberFormat
   {
      private string decSep = null;
 
      public NumberFormat(string name) : base(name) { }
      public NumberFormat(string name, short fraction) : base(name) { this.fraction = fraction; }

      public override MemberType Type { get { return MemberType.Number; } }
      public override void ToStream(StringBuilder str)
      {
         str.Append(":n");
         if (fraction != 0)
            str.Append("(" + fraction.ToString() + ")");
      }
      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         string fmt = "F" + fraction.ToString();
         StringBuilder val = new StringBuilder(m.ToDouble().ToString(fmt));
         if (decSep == null)
            decSep = System.Globalization.CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator;

         if (decSep != ".")
            val.Replace(decSep, ".");
         str.Append(val.ToString());
      }
      public override bool Read(ByteStream stream)
      {
         if (stream.Next() == '(')
         {
            string val;
            stream.MoveNext(); // eat 'n'
            stream.MoveNext(); // eat '('
            if (!stream.CopyUntill(out val, ')'))
               return false;

            fraction = Int16.Parse(val);
         }
         else
            fraction = 0;
         return true;
      }

      public override bool ReadMember(Member m, ByteStream stream)
      {
         string sym = "0123456789.eE-+";
         StringBuilder dest = new StringBuilder();

         while (!stream.EOS)
         {
            char cur = stream.Current();
            if (sym.IndexOf(cur) < 0) break;

            dest.Append(cur);
            stream.MoveNext();
         }

         if (decSep == null)
            decSep = System.Globalization.CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator;
         if (decSep != ".")
            dest.Replace(".", decSep);

         m.Value = Double.Parse(dest.ToString());
         return true;
      }

      public short fraction;
   }

   public class BoolFormat : NumberFormat
   {
      public BoolFormat(string name) : base(name) { }
      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         str.Append(m.ToBool() ? "1" : "0");
      }
   }

   public abstract class BaseDateTime : MemberFormat
   {
      string format;

      public BaseDateTime(string name, string format) : base(name) { this.format = format; }

      public override MemberType Type { get { return MemberType.DateTime; } }

      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         str.Append(m.ToDateTime().ToString(format));
      }
      public override bool Read(ByteStream stream)
      {
         return true;
      }
      public override bool ReadMember(Member m, ByteStream stream)
      {
         StringBuilder str = new StringBuilder();
         do
         {
            char sym = stream.Current();
            if (sym == ',' || sym == ']')
               break;
            str.Append(sym);
         } while (stream.MoveNext());

         m.Value = DateTime.ParseExact(str.ToString(), format, null);
         return true;
      }
   }

   public class DateFormat : BaseDateTime
   {
      public DateFormat(string name) : base(name, "yyyy-MM-dd") { }
      public override void ToStream(StringBuilder str) { str.Append(":d"); }
   }

   public class TimeFormat : BaseDateTime
   {
      public TimeFormat(string name) : base(name, "HH:mm:ss") { }
      public override void ToStream(StringBuilder str) { str.Append(":t"); }
   }

   public class StampFormat : BaseDateTime
   {
      public StampFormat(string name) : base(name, "yyyy-MM-dd HH:mm:ss") { }
      public override void ToStream(StringBuilder str) { str.Append(":dt"); }
   }

   public class ObjectFormat : MemberFormat
   {
      public ObjectFormat(string name, string parent) : base(name) { this.parent = parent; }
      public override MemberType Type { get { return MemberType.Object; } }
      public override void ToStream(StringBuilder str)
      {
         Format format = Format.Find(parent + "$" + name);
         if (format != null)
            format.MembersToStream(str);
      }
      public Format GetFormat() { return Format.Find(parent + "$" + name); }

      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         ObjectList ol = m.ToObjectList();
         if (ol == null || ol.Count == 0) str.Append("[]");
         else
         {
            packet.Add(str.ToString());
#if TRACE_GRPACKET
            Debug.debugInfo.Append(str.ToString());
#endif
            str.Remove(0, str.Length);
            ol.MembersToPacket(packet);
         }
      }
      public override bool Read(ByteStream stream)
      {
         if (stream.MoveNext())
         {
            string fname = parent + "$" + name;

            Format f = new Format(fname);
            if (f.ReadMembers(stream))
            {
               Format.Add(f);
               return true;
            }
         }
         return false;
      }
      public override bool ReadMember(Member m, ByteStream stream)
      {
         Format f = Format.Find(parent + "$" + name);
         if (f == null) return false;

         ObjectList ol = new ObjectList(f);
         if (!ol.ReadObjects(stream)) return false;
         m.Value = ol;
         return true;
      }
      string parent;
   }

   public class BinaryFormat : MemberFormat
   {
      public BinaryFormat(string name) : base(name) { }
      public override MemberType Type { get { return MemberType.Binary; } }
      public override void ToStream(StringBuilder str) { str.Append(":b"); }
      public override void MemberToPacket(Member m, StringBuilder str, Packet packet)
      {
         Byte[] binary = m.ToBytes();
         if (binary == null) str.Append("0:");
         else
         {
            str.Append(binary.Length.ToString() + ":");
            packet.Add(str.ToString());
            str.Remove(0, str.Length);
            packet.Add(binary);
            if ((binary.Length % 2) != 0)
               packet.Add(new Byte[1] { 0 });
         }
      }
      public override bool Read(ByteStream stream) { return true; }
      public override bool ReadMember(Member m, ByteStream stream)
      {
         string size;
         if (!stream.CopyUntill(out size, ':') || !stream.MoveNext()) return false;

         Byte[] bytes = null;
         int len = Int32.Parse(size);
         if (len > 0)
         {
            bytes = new Byte[len];
            if (!stream.CopyBytes(bytes)) return false;
         }

         m.Value = bytes;
         return true;
      }
   }

   #endregion

   public class Format : List<MemberFormat>
   {
      public Format(string name) { this.name = name; }

      public static Format Read(ByteStream stream)
      {
         Format format = null;
         string name;
         if (stream.CopyUntill(out name, '[') && stream.MoveNext())
         {
            format = new Format(name);
            if (!format.ReadMembers(stream)) format = null;
            else Add(format);
         }
         return format;
      }

      public bool ReadMembers(ByteStream stream)
      {
         MemberFormat mf = null;
         StringBuilder memberName = new StringBuilder();

         bool done = false, error = false;
         while (!done && !error && !stream.EOS)
         {
            char sym = stream.Current();
            switch (sym)
            {
               case ',':
               case ']':
                  if (mf == null) error = true;
                  else
                  {
                     Add(mf);
                     memberName.Remove(0, memberName.Length);
                     done = (sym == ']');
                  }
                  break;

               case ':':
               case '[':
                  mf = MemberFormat.Read(memberName.ToString(), name, sym, stream);
                  if (sym == '[' && mf != null)
                  {
                     Add(mf);
                     memberName.Remove(0, memberName.Length);
                     if (stream.Current() == ']')
                        done = true;
                  }
                  error = (mf == null);
                  break;

               default:
                  memberName.Append(sym);
                  break;
            }

            //if (!done || stream.Next() != ']')
               stream.MoveNext();
         }

         return done;
      }

      public void ToPacket(Packet packet)
      {
         StringBuilder str = new StringBuilder();

         str.Append(name);
         MembersToStream(str);
         packet.Add(str.ToString());
#if TRACE_GRPACKET
         File.AppendAllText("packet.txt", str.ToString() + "\n");
#endif
      }

      public void MembersToStream(StringBuilder str)
      {
         str.Append("[");
         bool started = false;
         foreach (MemberFormat mf in this)
         {
            if (!started) started = true;
            else str.Append(",");

            str.Append(mf.name);
            mf.ToStream(str);
         }
         str.Append("]");
      }

      public MemberFormat FindMember(string name)
      {
         foreach(MemberFormat f in this)
         {
            if (f.name == name)
               return f;
         }

         return null;
      }

      public string Name { get { return name; } }

      string name;

      #region class methods

      public static Format Find(string name)
      {
         List<Format> srch = new List<Format>(formats);
         foreach (Format format in srch)
         {
            if (format.name.CompareTo(name) == 0)
               return format;
         }
         return null;
      }

      public Format CloneFormat(String name)
      {
         Format dest = new Format(name);
         foreach (MemberFormat mf in this)
         {
            ObjectFormat of = mf as ObjectFormat;
            if (of != null)
            {
               Format childSrc = of.GetFormat();
               if (childSrc != null)
               {
                  Format childDest = childSrc.CloneFormat(name + "$" + of.name);
                  dest.Add(new ObjectFormat(mf.name, name));
               }
            } else
               dest.Add(mf);
         }

         Add(dest);
         return dest;
      }

      /// <summary>
      /// ищет, если нет, то создает
      /// </summary>
      public static Format FindOrCreate(IDataSet dataSet)
      {
         string name = dataSet.Name;
         Type elementType = dataSet.ElementType;

         Format ret = Find(name);
         if (ret == null)
            ret = Create(name, elementType);
         else
            ret = ret.CheckFormat(elementType, dataSet.RcvdFields);

         if (ret == null)
            throw (new NullReferenceException("Format is null for " + elementType.ToString()));

         return ret;
      }

        public static Format FindOrCreate(string name, DataObject sample)
        {
            Type elementType = sample.GetType();

            Format ret = Find(name);
            if (ret == null)
                ret = Create(name, elementType);
            else
                ret = ret.CheckFormat(elementType, sample.srvFields);

            if (ret == null)
                throw (new NullReferenceException("Format is null for " + elementType.ToString()));

            return ret;
        }

        private Format CheckFormat(Type elementType, Dictionary<String,object> rcvdFields)
      {
         Format ret = new Format(name);
         FieldInfo[] fields = elementType.GetFields(BindingFlags.Instance | BindingFlags.Public);
         List<string> fieldNames = new List<string>();

         foreach (FieldInfo fi in fields)
         {
            object[] attrs = fi.GetCustomAttributes(false);
            if (attrs.Length == 1)
            {
               if (attrs[0] is ReferenceAttribute)
                  fieldNames.Add(((ReferenceAttribute)attrs[0]).Field);
               else if (attrs[0] is DataFieldAttribute)
                  fieldNames.Add(((DataFieldAttribute)attrs[0]).Name);
               else
                  fieldNames.Add(fi.Name);
            }
            else
               fieldNames.Add(fi.Name);
         }

         foreach (MemberFormat mf in this)
         {
            if (fieldNames.Contains(mf.name) || (rcvdFields != null && rcvdFields.ContainsKey(mf.name)))
               ret.Add(mf);
         }

         return (ret.Count == Count) ? this : ret;
      }

      public static bool Add(Format format)
      {
         int i = 0;
         for (; i < formats.Count; i++)
         {
            if (formats[i].name.CompareTo(format.name) == 0)
            {
               formats[i] = format;
               return true;
            }
         }

         formats.Add(format);
         return false;
      }

      static List<Format> formats = new List<Format>();
      #endregion

      internal int MemberIndex(string name)
      {
         int index = Count - 1;
         for (; index >= 0; index-- )
         {
            if (this[index].name == name)
               break;
         }

         return index;
      }

      static object GetAttribute(FieldInfo f, Type attibute)
      {
         object res = null;
         object[] atts = f.GetCustomAttributes(attibute, false);
         if (atts.Length > 0)
            res = atts[0];
         return res;
      }

      static MemberFormat GetFieldFormat(FieldInfo f, Type t, string fieldName, string typeName)
      {
         MemberFormat mf = null;
         Type ft = f.FieldType;
         DataFieldAttribute df = GetAttribute(f, typeof(DataFieldAttribute)) as DataFieldAttribute;
         if (df != null)
            fieldName = df.Name;

         if (ft == typeof(string))
         {
            mf = new StringFormat(fieldName);
         }
         else if (ft == typeof(int) || ft == typeof(long))
         {
            mf = new NumberFormat(fieldName);
         }
         else if (ft == typeof(bool))
         {
            mf = new BoolFormat(fieldName);
         }
         else if (ft == typeof(double))
         {
            PrecisionAttribute pa = GetAttribute(f, typeof(PrecisionAttribute)) as PrecisionAttribute;
            mf = new NumberFormat(fieldName, (pa == null) ? (short)2 : pa.Precision);
         }
         else if (ft == typeof(DateTime))
         {
            mf = new StampFormat(fieldName);
         }
         else if (ft == typeof(byte[]))
         {
            mf = new BinaryFormat(fieldName);
         }
         else
         {
            ReferenceAttribute ra = GetAttribute(f, typeof(ReferenceAttribute)) as ReferenceAttribute;
            if (ra != null)
            {
               FieldInfo[] refF = ft.GetFields(BindingFlags.Instance | BindingFlags.Public);
               foreach (FieldInfo refFi in refF)
               {
                  object ka = GetAttribute(refFi, typeof(KeyFieldAttribute));
                  if (ka != null)
                  {
                     mf = GetFieldFormat(refFi, ft, ra.Field, typeName);
                     break;
                  }
               }
            }
            else
            {
               //ItemTypeAttribute ia = GetAttribute(f, typeof(ItemTypeAttribute)) as ItemTypeAttribute;
               //if (ia != null)
               //{
                  //Type itemT = ia.ItemType;
               Type[] tp = f.FieldType.GetGenericArguments();
               if( tp.Length > 0 )
               {
                  Type itemT = tp[0];
                  if (typeof(DataObject).IsAssignableFrom(itemT))
                  {
                     string name = typeName + '$' + fieldName;
                     Format itemF = Find(name);
                     if (itemF == null)
                     {
                        itemF = Create(name, itemT);
                        Add(itemF);
                     }

                     mf = new ObjectFormat(fieldName, typeName);
                  }
               }
            }
         }

         return mf;
      }

      private static string findFmtName = null;
      static bool FindFmtByName(MemberFormat fnd)
      {
         return fnd.name == findFmtName;
      }

      public static Format Create(string name, Type type)
      {
         Format format = null;

         if (typeof(DataObject).IsAssignableFrom(type))
         {
            format = new Format(name);

            FieldInfo[] fields = type.GetFields(BindingFlags.Instance | BindingFlags.Public);
            foreach (FieldInfo f in fields)
            {
               MemberFormat mf = GetFieldFormat(f, type, f.Name, name);
               if (mf != null)
               {
                  findFmtName = mf.name;
                  if (format.Find(FindFmtByName) == null)
                     format.Add(mf);
               }
            }
            Add(format);
         }
         else
            throw new ArgumentException(type.ToString() + " должен быть порожден от DataObject");

         return format;
      }
   }
}


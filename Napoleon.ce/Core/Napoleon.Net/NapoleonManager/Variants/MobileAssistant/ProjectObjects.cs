using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class NapoleonTask : DataObject
   {
      public readonly static String OBJECT_NAME = "NapoleonTask";

      [KeyField]
      public string id = string.Empty;
      public string task = string.Empty;

      public DateTime start = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;

      public string userid = string.Empty;
   }

   class NapoleonTaskTemplate : DataObject, IComparable<NapoleonTaskTemplate>
   {
      public readonly static String OBJECT_NAME = "NapoleonTaskTemplate";

      [KeyField]
      public string id = string.Empty;
      public string task = string.Empty;

      public string Text { get { return task; } }

      public int CompareTo(NapoleonTaskTemplate other)
      {
         return Text.CompareTo(other.Text);
      }
   }

   class NapoleonTaskResponse : BaseDocument
   {
      public readonly static String OBJECT_NAME = "NapoleonTaskResponse";

      public class Item : DataObject
      {
         public byte[] id = null;
      }

      public List<Item> items = new List<Item>();
   }

   public class NapoleonOrderDogorvor : DataObject
   {
      public static readonly string OBJECT_NAME = "Dogovor";

      [KeyField]
      public string id;
      public string name;
   }
}

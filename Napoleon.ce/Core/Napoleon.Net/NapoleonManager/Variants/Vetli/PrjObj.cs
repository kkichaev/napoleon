using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class TimeTracking : DataObject
   {
      public static readonly string OBJECT_NAME = "TimeTracking";

      public int month = 0;
      public int year = 0;
      public double cost = 0.0;
      public List<Item> items = new List<Item>();

      public class Item : DataObject
      {
         public DateTime date;

         [Reference("Agents", "userid")]
         public Agent agent = null;
         public string start = string.Empty;
         public string finish = string.Empty;
         public double km = 0.0;

         public DateTime Date { get { return date; } set { date = value; } }
         public Agent Agent { get { return agent; } set { agent = value; } }
         public string Start { get { return start; } set { start = value; } }
         public string Finish { get { return finish; } set { finish = value; } }
         public double KM { get { return km; } set { km = value; } }
      }
   }

   public partial class Visit
   {
      public partial class VisitItem
      {
         public string did = string.Empty;
         public string dval = string.Empty;
      }
   }

   class TypeDistrib : DataObject
   {
      public static readonly String OBJECT_NAME = "TypeDistrib";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }

      public override string ToString()
      {
         return Text;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

}

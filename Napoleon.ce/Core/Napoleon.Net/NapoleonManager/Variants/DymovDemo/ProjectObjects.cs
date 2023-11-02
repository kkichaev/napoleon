using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class OrgPlan : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgPlan";

      public string id = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string userid = "";
      public double value = 0.0;
      public DateTime changed = DateTime.MinValue;
      public DateTime created = DateTime.MinValue;

      public OrgPlan() { }

      public string Key { get { return userid + "|" + id; } }

      public OrgPlan(string key, string id)
      {
         this.userid = key;
         this.id = id;
      }
   }
}

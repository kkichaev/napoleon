using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class CommonIncass : DataObject
   {
      public static readonly string OBJECT_NAME = "CommonIncass";

      public DateTime created = DateTime.MinValue;
      public DateTime sended = DateTime.MinValue;
      public string remark = string.Empty;
      public string userid = string.Empty;

      [ItemType(typeof(CommonIncassItem))]
      public List<CommonIncassItem> items = null;
   }

   public class CommonIncassItem : DataObject
   {
      [Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;
      public string id = "";

      public double sum = 0;
   }

   public class Procuration : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Procuration";

      public double qty = 0.0;
      public string fio = string.Empty;
      public string parent = string.Empty;
      public string route = string.Empty;
   }
}

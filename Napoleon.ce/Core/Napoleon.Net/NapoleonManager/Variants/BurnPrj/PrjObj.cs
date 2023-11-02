using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class DivisionOrg : DataObject
   {
      public static readonly String OBJECT_NAME = "DivisionOrg";

      [KeyField]
      public int id = -1;

      public class DivisionOrgItem : DataObject
      {
         public string id = string.Empty;
      }

      [ItemType(typeof(DivisionOrgItem))]
      public List<DivisionOrgItem> items = new List<DivisionOrgItem>();
   }

   public partial class Org : DataObject
   {
      public string formatTT = string.Empty;
      public int rem = 0;

      public string FormatTT
      {
         get
         {
            return formatTT;
         }

         set
         {
            formatTT = value;
         }
      }

      public string DisplayName
      {
         get { return name; }
      }
   }

   public class Distributor : DataObject
   {
      public static readonly string OBJECT_NAME = "Distributors";

      [KeyField]
      public string id = "";

      public string name = "";

      public override string ToString() { return name; }

      public double disc = 0.0;
   }

   public partial class Price
   {
      public class PriceItem : DataObject
      {
         public double qty = 0.0;
         public double cost = 0.0;
      }

      public List<PriceItem> items = new List<PriceItem>();

      public int rem = 0;
   }

}

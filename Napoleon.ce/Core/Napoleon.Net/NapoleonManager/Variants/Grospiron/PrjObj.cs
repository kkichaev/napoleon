using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class ListItem : DataObject
   {
      public string name = "";
   }

   public class ListItemSource : SimpleDataSet<ListItem>
   {
      public static readonly string BRAND_OBJECT = "Brands";
      public static readonly string FORMAT_OBJECT = "OrgFormat";
      public static readonly string CITIES_OBJECT = "Cities";


      public ListItemSource(string name) : base(name, false) { }

      public string[] Items
      {
         get
         {
            List<string> ret = new List<string>();
            foreach (ListItem li in Values)
               ret.Add(li.name);

            return ret.ToArray();
         }
      }
   }

   public partial class Org : DataObject
   {
      public string formatTT = string.Empty;
      public string city = "";
      public string brand = "";
      public string address2 = "";

      public int hidden = 0;

      public string FormatTT
      {
         get
         {
            return formatTT;
         }

         //set
         //{
         //    formatTT = value;
         //}

      }

      public bool Contains(string text)
      {
         return name.ToUpper().Contains(text) || address.ToUpper().Contains(text) || city.ToUpper().Contains(text) || 
            brand.ToUpper().Contains(text) || address2.ToUpper().Contains(text);
      }

      public string City { get { return city; } }
      public string Brand { get { return brand; } }
      public string Address2 { get { return address2; } }

      public string DisplayName
      {
         get { return name; }
      }

      public bool IsValid
      {
         get { return name.Length > 0 && city.Length > 0 && brand.Length > 0 && address2.Length > 0 && formatTT.Length > 0; }
      }

      public string AgentName { get { return agent != null ? agent.Name : String.Format("Агент с кодом <{0}>", userid); } }
   }

   partial class MatrixItem
   {
      public int order = 0;
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

   public partial class Matrix
   {
      public int priority = 0;
      public int rem = 0;
   }
}

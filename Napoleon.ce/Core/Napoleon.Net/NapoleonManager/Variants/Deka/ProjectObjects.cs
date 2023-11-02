using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class Org : DataObject
   {
      public class Contact : DataObject
      {
         public string name = "";
         public string id = "";
         public string phone = "";

         public Contact() { }
         public Contact(string id) { this.id = id; }
      }

      public List<Contact> contacts = new List<Contact>();
      public string userid = string.Empty;
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
         public string id = string.Empty;
         public double qty = 0.0;
         public double cost = 0.0;
      }

      public List<PriceItem> items = new List<PriceItem>();
   }
}

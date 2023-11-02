using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class Sklad : DataObject
   {
      public static readonly string OBJECT_NAME = "Sklads";

      public string name = "";
      
      [KeyField]
      public string id = "";

      public string Name { get { return name; } }
      public string ID { get { return id; } }
   }

   class PriceSklads : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceSklads";

      public string idwh = "";

      [KeyField]
      public string id = "";
   }

   class PriceFolderOrder : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceFolderOrder";

      [KeyField]
      public string id = "";
      public string fid = "";
      public int ord = 0;
   }

   public partial class Price : DataObject
   {
      public int ord = -1;
   }
   
   public partial class ManagerFolder : DataObject
   {
      public int hidden = 0;
   }

   public class OrgProp : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgProp";

      [KeyField]
      public string id = string.Empty;

      public string userid = string.Empty;
      public int script = -1;
      public string matrix = string.Empty;
   }

   public class StringCause : DataObject
   {
      public static readonly string OBJECT_NAME = "StringCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public String Text 
      {
         get 
         {
            return text;
         }

         set
         {
            text = value;
         }

      }
   }
}

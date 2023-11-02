using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ArchReturns : Returns
   {
      public static new readonly String OBJECT_NAME = "ArchReturns";
   }

   class ArchSales : Sales
   { 
      public static new readonly String OBJECT_NAME = "ArchSales";
   }

   partial class Division
   {
      public string delay = string.Empty;
   }

   public class SalesBan : DataObject
   {
      public static new readonly String OBJECT_NAME = "SalesBan";

      [KeyField]
      public string id = string.Empty;
      public string userid = string.Empty;

      public string delay = string.Empty;
      public int value = 0;
   }

}

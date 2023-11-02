using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ArchSales : Sales
   { 
      public static new readonly String OBJECT_NAME = "ArchSales";
   }

   class SalesBan : DataObject
   {
      public static new readonly String OBJECT_NAME = "SalesBan";

      [KeyField]
      public string id = string.Empty;
      public string userid = string.Empty;
      public int value = 0;
   }
}

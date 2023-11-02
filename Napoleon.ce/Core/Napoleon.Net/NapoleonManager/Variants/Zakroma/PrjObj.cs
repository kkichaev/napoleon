using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
      
   }

   public class Bonus : Order
   {
      public static readonly new string OBJECT_NAME = "Bonus";
   }

   public class Claim : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Claim";
   }
}

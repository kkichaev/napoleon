using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class Order 
   {
      public double discount = 0.0;
   }

   public partial class OrderItem
   {
      public double discount = 0.0;

      public double Discount 
      {
         get
         {
            return discount;
         }

         set
         {
            discount = value;  
         }
      }
   }
}

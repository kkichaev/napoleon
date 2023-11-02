using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class Price
   {
      public class PriceQtyItem : DataObject
      {
         public double qty = 0.0;
      }

      [ItemType(typeof(PriceQtyItem))]
      public List<PriceQtyItem> whQty = new List<PriceQtyItem>();
   }
}

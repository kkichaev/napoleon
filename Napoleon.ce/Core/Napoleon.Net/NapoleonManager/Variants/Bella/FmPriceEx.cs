using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmPriceEx: FmPrice
   {
      protected override double getCost(Price p)
      {
         return p.cost != null && p.cost.Length > 1 ? p.cost[1] : 0.0;
      }
   }
}

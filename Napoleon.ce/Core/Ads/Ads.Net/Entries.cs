using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.Ads
{
   class FormEnties
   {
      public static FmOrder CreateOrderForm()
      {
         return new FmOrder();
      }

      public static FmUserOrder CreateUserOrder()
      {
         return new FmUserOrder();
      }

      internal static FmRoute CreateRouteForm(string idBrigade, DateTime date)
      {
         return new FmRoute(idBrigade, date);
      }
   }
}

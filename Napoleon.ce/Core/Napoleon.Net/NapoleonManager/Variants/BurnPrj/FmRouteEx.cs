using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{ 
   class FmRouteEx : FmRoute
   {
      public FmRouteEx(string idAgent, DateTime date)
         :base(idAgent, date)
      {
         clmnSum.Visible = false;
      }

   }
}

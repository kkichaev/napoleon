using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class RouteEx : Route
   {
      public RouteEx()
      {
         Manager m = (Manager)CurrentUser.user;
         routeWasChanged.SetEnabled(m.HaveRight(RightTokens.Get("EditRouteRight"), RightActions.Write));
      }
   }
}

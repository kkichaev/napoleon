using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{ 
   class FmCensusEx :FmCensus
   {
      protected override void UpdateOrgLabel(Org org)
      {
         base.UpdateOrgLabel(org);

         if (org != null)
         {
            if (org.contacts.Count > 0)
            {
               Org.Contact c = org.contacts[0];
               orgAddress.Text += String.Format(", {0} ({1})", c.name, c.phone);
            }
         }
      }
   }
}

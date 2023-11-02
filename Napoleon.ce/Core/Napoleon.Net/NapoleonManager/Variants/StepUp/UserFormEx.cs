using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner):base(owner)
      {
         userDetails.TabPages.Remove(userDetails.TabPages[1]);
         userDetails.TabPages.Remove(userDetails.TabPages[1]);
      }

      protected override bool NeedUpdateOrg()
      {
         return true;
      }
   }
}

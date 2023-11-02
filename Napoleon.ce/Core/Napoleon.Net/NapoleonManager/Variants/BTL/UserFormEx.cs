using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner):base(owner)
      {
         userDetails.TabPages.Remove(udOrgs);
         userDetails.TabPages.Remove(udMatrix);
      }
   }
}

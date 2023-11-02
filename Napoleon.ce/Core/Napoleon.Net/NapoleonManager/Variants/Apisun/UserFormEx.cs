using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner):
         base(owner)
      { 
      }
      protected override void AdjustForm()
      {
         base.AdjustForm();
         userDetails.TabPages.Remove(udMatrix);
         owner.tsbMatrixDesigner.Visible = false;
         btnEditRoute.Visible = false;
      }
   }
}

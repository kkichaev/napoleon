using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner) : base(owner)
      {
         userDetails.TabPages.Remove(udMatrix);
         owner.tsbMatrixDesigner.Visible = false;
         owner.setColor.Visible = false;
         btnEditRoute.Visible = false;
         btnOrgLocation.Location = new System.Drawing.Point(0, btnOrgLocation.Location.Y);
      }
   }
}

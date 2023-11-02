using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public UserFormEx(Divisions owner)
         :base(owner)
      {
         dgvOrgs.CellFormatting += dgvOrgs_CellFormatting;
      }

      void dgvOrgs_CellFormatting(object sender, System.Windows.Forms.DataGridViewCellFormattingEventArgs e)
      {
         Org o = dgvOrgs.Rows[e.RowIndex].DataBoundItem as Org;

         if (o != null)
         {
            e.CellStyle.ForeColor = o.Color;
         }
      }
   }
}

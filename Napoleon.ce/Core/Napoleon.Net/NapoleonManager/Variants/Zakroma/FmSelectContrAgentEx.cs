using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmSelectContrAgentEx : FmSelectContrAgent
   {
      protected override void DrawCell(System.Windows.Forms.DataGridViewCellFormattingEventArgs e)
      {
         base.DrawCell(e);

         DataGridViewRow row = dgvOrgs.Rows[e.RowIndex];
         Org o = row.DataBoundItem as Org;
         if (o != null)
         {
            e.CellStyle.ForeColor = o.Color;
         }
      }
   }
}

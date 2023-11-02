using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class RemnantsOverviewEx : RemnantsOverview
   {
      public RemnantsOverviewEx()
      {
         DataGridViewTextBoxColumn tara = new DataGridViewTextBoxColumn();
         tara.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         tara.DataPropertyName = "Tara";
         tara.HeaderText = "Тара";
         tara.Name = "dgvRemnantsItemsTara";

         dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] { tara });
      }
   }
}

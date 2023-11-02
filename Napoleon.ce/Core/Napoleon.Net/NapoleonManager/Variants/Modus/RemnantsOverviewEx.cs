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
         DataGridViewTextBoxColumn qtyWh = new DataGridViewTextBoxColumn();
         qtyWh.HeaderText = "Склад";
         qtyWh.DataPropertyName = "QtyWh";
         qtyWh.DisplayIndex = 1;
         qtyWh.Width = 55;

         DataGridViewTextBoxColumn qtySh = new DataGridViewTextBoxColumn();
         qtySh.HeaderText = "Полка";
         qtySh.DataPropertyName = "QtySh";
         qtySh.DisplayIndex = 2;
         qtySh.Width = 55;

         dgvItems.Columns.Add(qtyWh);
         dgvItems.Columns.Add(qtySh);
      }
   }
}

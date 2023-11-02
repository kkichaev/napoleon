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
         dgvRemnantsItemsQty.Visible = false;

         DataGridViewCheckBoxColumn sklad = new DataGridViewCheckBoxColumn();
         sklad.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         sklad.DataPropertyName = "IsSklad";
         sklad.HeaderText = "Склад";
         sklad.Name = "dgvRemnantsItemsSklad";

         DataGridViewCheckBoxColumn shelf = new DataGridViewCheckBoxColumn();
         shelf.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         shelf.DataPropertyName = "IsShelf";
         shelf.HeaderText = "Полка";
         shelf.Name = "dgvRemnantsItemsShelf";

         dgvItems.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] { sklad, shelf });
      }
   }
}

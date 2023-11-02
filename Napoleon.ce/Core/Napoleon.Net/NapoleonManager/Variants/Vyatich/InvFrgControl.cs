using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class InvFrgControl : UserControl, DataObjectViewer
   {
      public InvFrgControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         InvFrg f = dataObject as InvFrg;

         if (f != null && f.items != null)
         {
            List<InvFrgItem> d = new List<InvFrgItem>();
            d.AddRange(f.items);
            d.Sort((x, y) => { return x.Item.CompareTo(y.Item); });

            dataGridView1.DataSource = d;
         }
      }

      private void dataGridView1_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow row = ((DataGridView)sender).Rows[e.RowIndex];

         InvFrgItem item = row.DataBoundItem as InvFrgItem;

         if (item != null) 
         {
            if (item.isnew == 1)
               e.CellStyle.BackColor = Color.Red;
            else if (item.exist == 1)
               e.CellStyle.BackColor = Color.Gray;
            else
               e.CellStyle.BackColor = Color.White;
         }
      }
   }
}

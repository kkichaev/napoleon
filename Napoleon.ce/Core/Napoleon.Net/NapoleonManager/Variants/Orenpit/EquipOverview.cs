using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class EquipOverview : UserControl, DataObjectViewer
   {
      public EquipOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         Order o = dataObject as Order;
         if (o != null)
         {
            if (o.remark.Length == 0)
               orderRemark.Hide();
            else
               orderRemark.Text = o.remark;
            dgvItems.DataSource = o.items;
         }
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ReturnOverview : UserControl, DataObjectViewer
   {
      public ReturnOverview()
      {
         InitializeComponent();
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         Returns o = dataObject as Returns;
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

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class RejectActOverview : UserControl, DataObjectViewer
   {
      public RejectActOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         RejectAct o = dataObject as RejectAct;
         if (o != null)
         {
            dgvItems.DataSource = o.items;
         }
      }
   }
}

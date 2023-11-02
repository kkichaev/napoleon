using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ReturnReqOverview : UserControl, DataObjectViewer
   {
      public ReturnReqOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         ReturnRequest o = dataObject as ReturnRequest;
         if (o != null)
         {
            dgvItems.DataSource = o.items;
         }
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class RemnantsOverview : UserControl, DataObjectViewer
   {
      public RemnantsOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }
      
      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         OrgRemnants o = dataObject as OrgRemnants;
         dgvItems.DataSource = o.items;
      }
   }
}

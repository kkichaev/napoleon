using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ContractOverview : UserControl, DataObjectViewer
   {
      BindingList<ContractItem> datasource = new BindingList<ContractItem>();

      public ContractOverview()
      {
         InitializeComponent();
         dgvItems.DataSource = datasource;
      }
      
      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         datasource.Clear();
         Contract o = dataObject as Contract;

         foreach (ContractItem ci in o.items)
            datasource.Add(ci);

         dgvItems.Refresh();
      }
   }
}

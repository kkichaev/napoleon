using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmCMonitor : UserControl, DataObjectViewer
   {
      public FmCMonitor()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject)
      {
         CMonitoring cm = dataObject as CMonitoring;
         if (cm != null)
         {
            dgvItems.DataSource = cm.items;
         }
         else
            dgvItems.DataSource = null;
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DistirbDetail : UserControl, DataObjectViewer
   {
      public DistirbDetail()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject)
      {
         Distrib d = dataObject as Distrib;
         if (d != null)
         {
            dgvItems.DataSource = d.items;
         }
         else
            dgvItems.DataSource = null;
      }
   }
}

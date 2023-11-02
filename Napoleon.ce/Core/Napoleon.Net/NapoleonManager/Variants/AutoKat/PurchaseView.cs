using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class PurchaseView : UserControl, DataObjectViewer
   {
      public PurchaseView()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject)
      {
         Purchase o = dataObject as Purchase;
         if( o != null)
         {
            dgvItems.DataSource = o.items;
         }
      }
   }
}

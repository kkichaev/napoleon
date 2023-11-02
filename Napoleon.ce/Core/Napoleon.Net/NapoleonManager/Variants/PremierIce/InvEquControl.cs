using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class InvEquControl : UserControl, DataObjectViewer
   {
      public InvEquControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         InvEqu f = dataObject as InvEqu;

         if (f != null && f.items != null)
         {
            List<InvEquItem> d = new List<InvEquItem>();
            d.AddRange(f.items);
            d.Sort((x, y) => { return x.Item.CompareTo(y.Item); });

            dataGridView1.DataSource = d;
         }
      }
   }
}

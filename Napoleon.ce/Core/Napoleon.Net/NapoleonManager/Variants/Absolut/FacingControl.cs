using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FacingControl : UserControl, DataObjectViewer
   {
      public FacingControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         Facing f = dataObject as Facing;

         if (f != null && f.items != null)
         {
            List<FacingItem> d = new List<FacingItem>();
            d.AddRange(f.items);
            d.Sort((x, y) => { return x.Item.CompareTo(y.Item); });

            dataGridView1.DataSource = d;
         }
      }
   }
}

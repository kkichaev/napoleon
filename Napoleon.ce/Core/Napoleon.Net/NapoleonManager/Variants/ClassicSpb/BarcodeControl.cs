using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class BarcodeControl : UserControl, DataObjectViewer
   {
      public BarcodeControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         Barcode f = dataObject as Barcode;

         if (f != null && f.items != null)
         {
            List<BarcodeItem> d = new List<BarcodeItem>();
            d.AddRange(f.items);
            d.Sort((x, y) => { return x.Item.CompareTo(y.Item); });

            dataGridView1.DataSource = d;
         }
      }
   }
}

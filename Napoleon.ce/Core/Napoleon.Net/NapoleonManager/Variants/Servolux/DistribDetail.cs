using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class DistribDetail : UserControl
   {
      public DistribDetail()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(DistribOrg doc)
      {
         dgvItems.DataSource = doc.items;

         String text = String.Format("Место реализации {0} / {1}", doc.pType == null ? doc.priceType : doc.pType.name, doc.thermalState);
         lbText.Text = text;
      }
   }
}

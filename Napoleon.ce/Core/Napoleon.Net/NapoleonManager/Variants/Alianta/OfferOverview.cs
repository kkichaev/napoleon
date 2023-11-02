using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class OfferOverview : UserControl, DataObjectViewer
   {
      public OfferOverview()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         AliantaOffer o = dataObject as AliantaOffer;
         if (o != null)
         {
            dgvItems.DataSource = o.items;
         }
      }
   }
}

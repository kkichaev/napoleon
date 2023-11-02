using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class DShipmentView : UserControl, DataObjectViewer
   {
      public DShipmentView()
      {
         InitializeComponent();
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         DShipment s = dataObject as DShipment;

         if (s != null)
            grid.DataSource = s.items;
      }
   }
}

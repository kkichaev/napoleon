using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class DReturnView : UserControl, DataObjectViewer
   {
      public DReturnView()
      {
         InitializeComponent();
      }

      public void SetData(GRSoft.Network.DataObject dataObject)
      {
         DReturn s = dataObject as DReturn;

         if (s != null)
            grid.DataSource = s.items;
      }
   }
}

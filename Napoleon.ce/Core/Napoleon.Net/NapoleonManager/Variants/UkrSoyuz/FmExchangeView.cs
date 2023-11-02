using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.IO;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmExchangeView : UserControl, DataObjectViewer
   {
      public FmExchangeView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      
      public void SetData(Network.DataObject obj)
      {
         ExchDoc d = obj as ExchDoc;
         grid.DataSource = d.items;
      }
   }
}

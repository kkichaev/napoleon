using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class TareView : UserControl, DataObjectViewer
   {
      public TareView()
      {
         InitializeComponent();
      }

      public void InitDataSet(List<Tare.Item> list)
      {
         grid.DataSource = list;
      }

      public void SetData(Network.DataObject dataObject)
      {
         Tare a = dataObject as Tare;

         if (a != null)
            InitDataSet(a.items);
      }
   }

}

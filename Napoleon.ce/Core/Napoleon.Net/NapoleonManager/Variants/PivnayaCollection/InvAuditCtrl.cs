using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class InvAuditCtrl : UserControl, DataObjectViewer
   {
      public InvAuditCtrl()
      {
         InitializeComponent();
      }

      public void InitDataSet(List<InvAuditItem> list)
      {
         grid.DataSource = list;
      }

      public void SetData(Network.DataObject dataObject)
      {
         InvAudit a = dataObject as InvAudit;

         if (a != null)
            InitDataSet(a.items);
      }
   }

   public partial class InvAuditItem
   {
      public string Name { get { return inv == null ? "" : inv.name; } }
      public double Qty { get { return qty; } }
      public double Fact { get { return fact; } }
      public bool Clear { get { return clear != 0; } }
      public double Good { get { return good; } }
   }
}

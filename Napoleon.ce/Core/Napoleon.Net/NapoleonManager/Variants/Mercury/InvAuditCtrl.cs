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
         List<InvAuditData> data = new List<InvAuditData>();
         if (list != null)
            foreach (InvAuditItem i in list)
            {
               InvAuditData d = new InvAuditData();

               if (i.isnew == 1)
                  d.fact = i.id;
               else
                  d.plan = i.id;
               data.Add(d);
            }

         grid.DataSource = data;
      }

      public void SetData(Network.DataObject dataObject)
      {
         InvAudit a = dataObject as InvAudit;

         if (a != null)
            InitDataSet(a.items);
      }
   }

   class InvAuditData
   {
      public string plan;
      public string fact;

      public string Plan { get { return plan; } }
      public string Fact { get { return fact; } }
   }
}

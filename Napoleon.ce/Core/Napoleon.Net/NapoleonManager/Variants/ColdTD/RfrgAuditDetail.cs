using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class RfrgAuditDetail : UserControl
   {
      public RfrgAuditDetail()
      {
         InitializeComponent();
      }

      protected override void OnPaint(PaintEventArgs pe)
      {
         base.OnPaint(pe);
      }

      public void SetSource(RfrgAudit doc)
      {
         String text = String.Format("Эксклюзивность {0}", doc.exclusive);
         List<RfrgAudit.Item> items = new List<RfrgAudit.Item>();
         items.AddRange(doc.items);
         dgvItems.DataSource = items;
      }
   }
}

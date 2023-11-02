using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class OrderBundleOverview : UserControl, DataObjectViewer
   {
      public OrderBundleOverview()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         List<SrcData> data = new List<SrcData>();

         OrderBundle bdoc = dataObject as OrderBundle;
         foreach(Order doc in bdoc.documents())
         {
            data.Add(new SrcData(doc.firmCode));
            doc.items.ForEach(x => data.Add(new SrcData(x)));
         }

         dgvItems.DataSource = data;
      }

      class SrcData
      {
         string name;
         string qty;
         string cost;

         public string Item { get { return name; } }
         public string Qty { get { return qty; } }
         public string SCost { get { return cost; } }

         public SrcData(string firm)
         {
            this.name = Factory.Get(firm).name;
         }

         public SrcData(OrderItem src)
         {
            name = src.Item;
            qty = src.Qty.ToString();
            cost = src.SCost;
         }
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         SrcData s = dgvItems.Rows[e.RowIndex].DataBoundItem as SrcData;
         if (s.Qty == null)
            e.CellStyle.BackColor = Color.LightGray;
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmDistribView : UserControl, DataObjectViewer
   {
      public FmDistribView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject dataObject)
      {
         OrgDistrib dd = dataObject as OrgDistrib;
         List<DistribItemView> data = new List<DistribItemView>();

         if (dd != null)
         {
            foreach(OrgDistrib.OrgDistribItem i in dd.items)
            {
               DistribItemView d = new DistribItemView();
               d.Name = i.item.Name;
               d.Qty = i.qty;
               data.Add(d);
            }
         }

         for (int i = 0; i < data.Count; i++)
            data[i].Pos = i + 1;
         
         grid.DataSource = data;
      }

      private Image MakePic(byte[] p)
      {
         Image result = null;
         
         using (Stream stream = new MemoryStream(p))
         {
            try
            {
               result = Image.FromStream(stream);
            }
            catch (Exception) { }
         }

         return result;
      }

      private class DistribItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public double Qty { get; set; }
      }
   }
}

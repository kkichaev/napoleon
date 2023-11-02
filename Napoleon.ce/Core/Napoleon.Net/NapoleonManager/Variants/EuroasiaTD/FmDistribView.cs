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
   public partial class FmDistribView : UserControl, DataObjectViewer
   {
      public FmDistribView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      
      public void SetData(Network.DataObject obj)
      {
         List<DistribItemView> list = new List<DistribItemView>();
         Distrib d = obj as Distrib;

         if (d != null)
         {
            foreach(Distrib.Item i in d.items)
            {
               DistribItemView v = new DistribItemView();
               v.Name = i.item.Name;
               v.Remark = i.remark;
               v.Qty = i.qty;
               v.Cost = i.cost;

               list.Add(v);
            }
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         BindingList<DistribItemView> data = new BindingList<DistribItemView>();
         int pos = 1;
         list.ForEach((i) => { i.Pos = pos++; data.Add(i); });
         grid.DataSource = data;
      }

      private class DistribItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public string Remark { get; set; }
         public double Qty { get; set; }
         public double Cost { get; set; }
      }
   }
}

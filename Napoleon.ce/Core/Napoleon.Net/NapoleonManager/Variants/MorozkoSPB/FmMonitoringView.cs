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
   public partial class FmMonitoringView : UserControl, DataObjectViewer
   {
      public FmMonitoringView()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
      }

      
      public void SetData(Network.DataObject obj)
      {
         List<MonitoringItemView> list = new List<MonitoringItemView>();
         MonitoringW d = obj as MonitoringW;

         if (d != null)
         {
            foreach (MonitoringW.Item i in d.items)
            {
               MonitoringItemView v = new MonitoringItemView();
               v.Name = i.item.Name;
               v.Cost = i.cost;
               v.Cost1 = i.cost1; 
               v.Cost2 = i.cost2;

               list.Add(v);
            }
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         BindingList<MonitoringItemView> data = new BindingList<MonitoringItemView>();
         int pos = 1;
         list.ForEach((i) => { i.Pos = pos++; data.Add(i); });
         grid.DataSource = data;
      }

      private class MonitoringItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public double Cost { get; set; }
         public double Cost1 { get; set; }
         public double Cost2 { get; set; }
      }
   }
}

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
         CMonitoring d = obj as CMonitoring;

         if (d != null)
         {
           foreach (CMonitoring.Item i in d.items)
            {
               MonitoringItemView v = new MonitoringItemView();
               v.Name = i.item.Name;
               v.Cost = i.cost;

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
      }
   }
}

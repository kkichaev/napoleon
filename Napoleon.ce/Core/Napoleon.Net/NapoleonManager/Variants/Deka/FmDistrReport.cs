using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDistrReport : Form
   {
      SimpleDataSet<Distributor> distribs;
      public FmDistrReport()
      {
         InitializeComponent();

         distribs = DataModule.Get(Distributor.OBJECT_NAME) as SimpleDataSet<Distributor> ??
            new SimpleDataSet<Distributor>(Distributor.OBJECT_NAME, false);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      void RefreshData()
      {
         List<IDataSet> updSet = new List<IDataSet>();

         updSet.Add(distribs);
         FmWait.StdDataRefresh(this, updSet, DoLoadData);
      }

      void DoLoadData()
      {
         foreach (Distributor d in distribs.Data)
            lbItems.Items.Add(d);
      }

      public void SetPeriod(DateTime start, DateTime end)
      {
         dpReport.Start = start;
         dpReport.Finish = end;
      }

      private void cbSelectAll_CheckedChanged(object sender, EventArgs e)
      {
         for( int i=0; i<lbItems.Items.Count; i++)
            lbItems.SetItemChecked(i, cbSelectAll.Checked);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         ReportData rd = new ReportData();
         rd.start = dpReport.Start;
         rd.end = dpReport.Finish;

         foreach (Distributor d in lbItems.CheckedItems)
            rd.items.Add(new ReportData.Item(d.id));

         Config.GetConfig().GetConnection().ReceiveTimeout = 10 * 30 * 1000;
         ReportResult.DoReport("distrib_report", rd, this);
      }

      public class ReportData : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime end;

         public class Item : GRSoft.Network.DataObject
         {
            public string id = "";

            public Item() { }

            public Item(string id) { this.id = id; }
         }

         public List<Item> items = new List<Item>();
      }
   }
}

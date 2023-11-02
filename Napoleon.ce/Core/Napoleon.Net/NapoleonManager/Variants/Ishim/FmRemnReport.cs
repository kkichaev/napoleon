using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRemnReport : Form
   {
      public FmRemnReport()
      {
         InitializeComponent();
      }

      private class Data : Network.DataObject
      {
         public string divName = "";
         public string userids = "";
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
      }

      private void FmRemnReport_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            List<Division> list = mc.AllDivisions;
            list.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            list.ForEach((d) => { cbDiv.Items.Add(d); });

            if (cbDiv.Items.Count > 0)
               cbDiv.SelectedIndex = 0;
         }

      }

      private void button1_Click(object sender, EventArgs e)
      {
         Division d = cbDiv.SelectedItem as Division;

         if (d != null)
         {
            Data data = new Data();
            data.divName = d.name;
            data.userids = CollectUserIDS(d);
            data.start = dtpStart.Value.Date;
            data.finish = dtpFinish.Value.Date;

            ReportResult.DoReport("remnants_report", data, this);
         }
      }

      private string CollectUserIDS(Division d)
      {
         StringBuilder sb = new StringBuilder();

         foreach (GRSoft.NapoleonManager.Division.DivisionAgent a in d.agents)
         {
            if (sb.Length > 0)
               sb.Append(",");

            sb.Append(a.id);
         }

         return sb.ToString();
      }
   }
}

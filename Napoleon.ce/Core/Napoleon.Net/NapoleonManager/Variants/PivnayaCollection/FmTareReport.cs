using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmTareReport : Form
   {
      public FmTareReport()
      {
         InitializeComponent();
      }

      private void FmAuditReportParams_Load(object sender, EventArgs e)
      {
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            List<Agent> agents = new List<Agent>();
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;
               agents.Add(da.agent);
            }
            agents.Sort();
            Agent a = new Agent();
            a.name = "<Все>";

            agents.Insert(0, a);

            agents.ForEach(x => cbAgents.Items.Add(x));
            cbAgents.SelectedIndex = 0;
         }
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         ReportData data = new ReportData();
         data.start = dpv.Start;
         data.finish = dpv.Finish;

         Agent sel = cbAgents.SelectedItem as Agent;
         if (sel.id.Length == 0)
         {
            foreach (Division.DivisionAgent da in (CurrentUser.user as Manager).Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;
               data.items.Add(new ReportData.Item(da.id));
            }

         }
         else
            data.items.Add(new ReportData.Item(sel.id));

         ReportResult.DoReport("tare_report", data, this);
      }

      public class ReportData : Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;

         public class Item : Network.DataObject
         {
            public string id = "";

            public Item() { }
            public Item(string id) { this.id = id; }
         }

         public List<Item> items = new List<Item>();
      }
   }
}

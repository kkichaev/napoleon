using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public partial class FmIncassRptParam : Form
   {
      public FmIncassRptParam()
      {
         InitializeComponent();

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> list = new List<Agent>();

            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  list.Add(a.agent);

            list.Sort(new Comparison<Agent>(delegate (Agent lhs, Agent rhs) {return lhs.name.CompareTo(rhs.name);}));
            foreach(Agent a in list)
               cbAgents.Items.Add(a);
            
            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;
         cbAgents.Enabled = false;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;

         cbDivisions.Enabled = false;
      }

      public static void Do(DateTime start, DateTime finish)
      {
         FmIncassRptParam form = new FmIncassRptParam();
         form.dtpStart.Value = start;
         form.dtpFinish.Value = finish;

         form.Show();
      }

      private void rbAll_CheckedChanged(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = false;

         if (rbAgents.Checked)
            cbAgents.Enabled = true;
         else if (rbDivision.Checked)
            cbDivisions.Enabled = true;
      }

      private void CollectUserIDS(List<RepData.Item> users)
      {
         StringBuilder sb = new StringBuilder();

         if (rbAll.Checked) 
         {
            Manager m = CurrentUser.user as Manager;

            if (m != null)
            {
               foreach (Agent a in m.GetAgents().Values)
               {
                  RepData.Item item = new RepData.Item();
                  item.id = a.id;
                  users.Add(item);
               }
            }
         }
         else if (rbAgents.Checked)
         {
               RepData.Item item = new RepData.Item();
               item.id = ((Agent)cbAgents.SelectedItem).id;
               users.Add(item);
         }
         else if (rbDivision.Checked)
         {
            Division d = (Division)cbDivisions.SelectedItem;

            foreach (Division.DivisionAgent a in d.GetAllAgents())
            {
               RepData.Item item = new RepData.Item();
               item.id = a.id;
               users.Add(item);
            }
         }
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         RepData data = new RepData();
         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date;
         CollectUserIDS(data.agents);

         ReportResult.DoReport("incass_report", data, this);
      }

      class RepData : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";
         }

         public List<Item> agents = new List<Item>();
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
      }
   }
}

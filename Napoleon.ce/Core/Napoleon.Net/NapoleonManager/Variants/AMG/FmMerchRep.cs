using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmMerchRep : Form
   {
      public static void Do(DateTime start, DateTime finish, Form parent)
      {
         FmMerchRep form = new FmMerchRep();
         form.dtpFinish.Value = finish;
         form.dtpStart.Value = start;
         form.Show();
      }

      public FmMerchRep()
      {
         InitializeComponent();
      }

      private void FmMerchRep_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgents.Items.Contains(a.agent) == false)
                  cbAgents.Items.Add(a.agent);

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
         }

         cbAgents.Sorted = true;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;
      }

      private List<RepData.Item> CollectUserIDS()
      {
         List<RepData.Item> users = new List<RepData.Item>();
         StringBuilder sb = new StringBuilder();

         if (rbAgents.Checked)
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

         return users;
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

      class RepData : GRSoft.Network.DataObject
      {
         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";
         }

         public List<Item> agents = new List<Item>();
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string title = string.Empty;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         RepData rd = new RepData();
         rd.start = dtpStart.Value.Date;
         rd.finish = dtpFinish.Value.Date;
         rd.agents = CollectUserIDS();
         rd.title = GetReportTitle();

         ReportResult.DoReport("merch_rep", rd, this);
      }

      private string GetReportTitle()
      {
         String res = string.Empty;

         res = rbDivision.Checked && cbDivisions.SelectedItem as Division != null ?
            ((Division)cbDivisions.SelectedItem).Name : string.Empty;

         if (res.Trim().Length == 0)
            res = rbAgents.Checked && cbAgents.SelectedItem as Agent != null ?
               ((Agent)cbAgents.SelectedItem).Name : string.Empty;

         return res;
      }

   }
}

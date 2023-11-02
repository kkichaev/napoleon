using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmVisitReport : Form
   {
      public FmVisitReport()
      {
         InitializeComponent();
         dtpBegin.Value = DateTime.Now.Date;
         dtpEnd.Value = DateTime.Now.Date;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  cbAgent.Items.Add(a.agent);

            cbDivision.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivision.Items.Add(d);
         }

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

         if (cbDivision.Items.Count > 0)
            cbDivision.SelectedIndex = 0;
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         RadioButton rb = sender  as RadioButton;
         if (rb != null)
         {
            cbAgent.Enabled = rb.Checked;
            cbDivision.Enabled = !rb.Checked;
         }
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         RadioButton rb = sender as RadioButton;
         if (rb != null)
         {
            cbAgent.Enabled = !rb.Checked;
            cbDivision.Enabled = rb.Checked;
         }
      }

      private void button1_Click(object sender, EventArgs e)
      {
         Param data = new Param();
         data.start = dtpBegin.Value.Date;
         data.finish = dtpEnd.Value.Date;
         if (rbAgent.Checked)
         {
            Agent sel = cbAgent.SelectedItem as Agent;
            if (sel != null)
               data.agents.Add(new Param.Item(sel.id));
         }
         else
         {
            Division d = cbDivision.SelectedItem as Division;
            if (d != null)
               foreach (Division.DivisionAgent da in d.GetAllAgents())
               {
                  data.agents.Add(new Param.Item(da.id));
               }
         }

         ReportResult.DoReport("agent_visit", data, this);
      }

      class Param : GRSoft.Network.DataObject
      {  
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;

         public class Item : GRSoft.Network.DataObject
         {
            public String id = "";

            public Item(String id) { this.id = id; }
            public Item() { }
         }
         public List<Item> agents = new List<Item>();
      }
   }
}

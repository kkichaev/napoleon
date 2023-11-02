using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmWSReportParams : Form
   {
      String rep;

      public FmWSReportParams()
      {
         InitializeComponent();

         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;

         if (MainForm.Instance.CheckIsMainDataPresents(true) == false)
            return;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            List<Agent> agents = new List<Agent>();
            foreach (Division.DivisionAgent da in m.Division.GetAllAgents())
            {
               if (da.agent != null)
                  agents.Add(da.agent);
            }

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);
            cbDivisions.SelectedIndex = 0;

            agents.Sort();
            agents.ForEach(x => cbAgents.Items.Add(x));
            cbAgents.SelectedIndex = 0;
         }

         RefreshCtrlStates(false);
      }

      public void SetReport(String title, String rep)
      {
         Text = title + " параметры отчета";
         this.rep = rep;
      }

      void RefreshCtrlStates(bool divisionEnable)
      {
         if (rbDivisions.Checked != divisionEnable)
            rbDivisions.Checked = divisionEnable;

         if (rbAgents.Checked == divisionEnable)
            rbAgents.Checked = !divisionEnable;
         
         cbDivisions.Enabled = divisionEnable;
         cbAgents.Enabled = !divisionEnable;
      }

      private void rbDivisions_CheckedChanged(object sender, EventArgs e)
      {
         RefreshCtrlStates(rbDivisions.Checked);
      }

      private void rbAgents_CheckedChanged(object sender, EventArgs e)
      {
         RefreshCtrlStates(rbDivisions.Checked);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         RepParam rp = new RepParam();

         rp.start = dpv.Start;
         rp.finish = dpv.Finish;

         if (rbAgents.Checked)
            rp.agents.Add(new AgentItem((cbAgents.SelectedItem as Agent).id));
         else
         {
            Division sel = cbDivisions.SelectedItem as Division;
            foreach(Division.DivisionAgent da in sel.GetAllAgents())
               rp.agents.Add(new AgentItem(da.id));
         }

         ReportResult.DoReport(rep, rp, this);
      }


      class RepParam : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;

         public List<AgentItem> agents = new List<AgentItem>();
      }

      class AgentItem : GRSoft.Network.DataObject
      {
         public string id = "";
         public AgentItem() { }
         public AgentItem(string ai) { id = ai; }
      }

   }
}

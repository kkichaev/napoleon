using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DailyReportParams : Form
   {
      public class Data 
      {
         public DateTime date;
         public DateTime dateEnd;
         
         public Agent agent;
         public Division division;

         public List<Agent> Agents
         {
            get
            {
               List<Agent> ret = new List<Agent>();
               if (agent != null)
                  ret.Add(agent);
               else if(division != null)
               {
                  foreach (Division.DivisionAgent da in division.GetAllAgents())
                  {
                     if( da.agent != null )
                        ret.Add(da.agent);
                  }
               }
               return ret;
            }
         }
      }
      Data data;

      internal DailyReportParams(Data data)
      {
         this.data = data;
         InitializeComponent();
         dtpBegin.Value = data.date;
         dtpEnd.Value = data.dateEnd;

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

         if (data.agent != null)
         {
            rbAgents.Checked = true;
            cbAgents.SelectedItem = data.agent;
         }
         else
         {
            rbDivision.Checked = true;
            cbDivisions.SelectedItem = data.division;
         }
      }

      public void SetSelectedAgent(string id)
      {
         foreach (Agent a in cbAgents.Items)
            if (a.id == id)
            {
               cbAgents.SelectedItem = a;
               rbAgents.Checked = true;
               rbDivision.Checked = false;
               break;
            }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         data.date = dtpBegin.Value.Date;
         data.dateEnd = dtpEnd.Value.Date;
         if (rbAgents.Checked)
         {
            data.agent = cbAgents.SelectedItem as Agent;
            data.division = null;
         }
         else
         {
            data.division = cbDivisions.SelectedItem as Division;
            data.agent = null;
         }
         base.OnClosing(e);
      }

      private void rbDivision_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = false;
         cbDivisions.Enabled = true;
      }

      private void rbAgents_Click(object sender, EventArgs e)
      {
         cbAgents.Enabled = true;
         cbDivisions.Enabled = false;
      }
   }
}

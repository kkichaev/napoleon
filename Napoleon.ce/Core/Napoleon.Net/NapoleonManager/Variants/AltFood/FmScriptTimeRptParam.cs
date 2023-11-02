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
   public partial class FmScriptTimeRptParam : Form
   {
      public FmScriptTimeRptParam()
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
         }

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;

         cbAgents.Enabled = true;
      }

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value.Date.AddDays(1); } set { dtpFinish.Value = value; } }
      public string UserIDS { get { return cbAgents.SelectedItem as Agent != null ? "'" + ((Agent)cbAgents.SelectedItem).id + "'" : string.Empty; } }
   }
}

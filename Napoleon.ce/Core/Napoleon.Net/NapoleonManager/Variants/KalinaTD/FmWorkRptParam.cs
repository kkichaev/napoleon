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
   public partial class FmWorkRptParam : Form
   {
      public FmWorkRptParam()
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
            addDivisions(m.Division);
         }

         if (cbAgents.Items.Count > 0)
            cbAgents.SelectedIndex = 0;
         cbAgents.Enabled = false;

         if (cbDivisions.Items.Count > 0)
            cbDivisions.SelectedIndex = 0;

         cbDivisions.Enabled = false;
      }

      private void addDivisions(Division m) 
      {
         foreach (Division d in m.Childs)
         {
            if (d.Childs.Count > 0)
               addDivisions(d);
            cbDivisions.Items.Add(d);
         }
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

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value.Date; } set { dtpFinish.Value = value; } }
      public string UserIDS { get { return CollectUserIDS(); } }

      private string CollectUserIDS()
      {
         StringBuilder sb = new StringBuilder();

         if (rbAll.Checked) 
         {
            Manager m = CurrentUser.user as Manager;

            foreach (Agent a in m.GetAgents().Values)
               AppendToListVal(a.id, ",", sb);
         }
         else if (rbAgents.Checked)
            sb.Append("'").Append(((Agent)cbAgents.SelectedItem).id).Append("'");
         else if (rbDivision.Checked)
         {
            Division d = (Division)cbDivisions.SelectedItem;

            foreach (Division.DivisionAgent a in d.GetAllAgents())
               AppendToListVal(a.id, ",", sb);
         }

         return sb.ToString();
      }

      private void AppendToListVal(String v, String delim, StringBuilder res)
      {
         if (res.Length > 0)
            res.Append(delim);

         res.Append("'").Append(v).Append("'");
      }
   }


}

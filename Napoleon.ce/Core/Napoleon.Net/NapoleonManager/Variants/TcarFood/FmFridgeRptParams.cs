using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFridgeRptParams : Form
   {
      public FmFridgeRptParams()
      {
         InitializeComponent();
      }

      public string UserIDS {
         get
         {
            StringBuilder sb = new StringBuilder();

            foreach (Agent a in CollectAgents())
               if (a != null)
               {
                  if (sb.Length > 0)
                     sb.Append(", ");

                  sb.Append("'").Append(a.id).Append("'");
               }

            return sb.ToString();
         }
      }

      public string Report { 
         get
         {
            return rbPrez.Checked ? rbPrez.Tag.ToString() : rbDoc.Tag.ToString();
         }
      }

      private void rb_CheckedChanged(object sender, EventArgs e)
      {
         cbAgent.Enabled = rbAgent.Checked;
         cbDivision.Enabled = rbDivision.Checked;
      }

      private void FmFridgeRptParams_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;

         if (m != null)
         {
            List<Agent> lsa = new List<Agent>();
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && cbAgent.Items.Contains(a.agent) == false)
                  lsa.Add(a.agent);

            lsa.Sort((x, y) => { return x.name.CompareTo(y.name); });

            foreach(Agent a in lsa)
               cbAgent.Items.Add(a);

            List<Division> lsd = new List<Division>();
            lsd.Add(m.Division);
            foreach (Division d in m.Childs)
               lsd.Add(d);

            lsd.Sort((x, y) => { return x.name.CompareTo(y.name); });

            foreach(Division d in lsd)
               cbDivision.Items.Add(d);

            if (cbAgent.Items.Count > 0)
               cbAgent.SelectedIndex = 0;

            if (cbDivision.Items.Count > 0)
               cbDivision.SelectedIndex = 0;
         }
      }

      private List<Agent> CollectAgents()
      {
         List<Agent> result = new List<Agent>();

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;
            result.Add(agent);
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null && agent.agent != null)
                        result.Add(agent.agent);
                  };
               }
            }
         }
         else
         {
            Manager m = CurrentUser.user as Manager;

            if (m != null)
               result.AddRange(m.GetAgents().Values);
         }

         return result;
      }
   }
}

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
   public partial class FmSyncParam : Form
   {
      public DataSet<int, Order> dsOrder;
      public DataSet<string, Price> dsPrice;
      protected const string FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";

      public FmSyncParam()
      {
         InitializeComponent();

         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ?? 
            new DataSet<int, Order>(Order.OBJECT_NAME);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? 
            new DataSet<string, Price>(Price.OBJECT_NAME);

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

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;

            cbDivisions.Items.Add(m.Division);
            foreach (Division d in m.Childs)
               cbDivisions.Items.Add(d);

            if (cbDivisions.Items.Count > 0)
               cbDivisions.SelectedIndex = 0;
         }

         cbAgents.Enabled = false;
         cbDivisions.Enabled = false;
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

      private String AgentWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgents.Checked && cbAgents.SelectedItem != null)
         {
            Agent agent = cbAgents.SelectedItem as Agent;
            result.Append("\"userid\" = '").Append(agent.id).Append("'");
         }
         else if (rbDivision.Checked && cbDivisions.SelectedItem != null)
         {
            Division division = cbDivisions.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  result.Append("\"userid\" in (");

                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();
                  List<string> ids = new List<string>();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null)
                        ids.Add(String.Format("'{0}'", agent.id));
                  };

                  result.Append(String.Join(",", ids.ToArray()));
                  result.Append(")");
               }
            }
         }

         return result.ToString();
      }

      public string AgentIds { 
         get
         {
            StringBuilder res = new StringBuilder();

            if (rbAll.Checked)
            {
               foreach (object o in cbAgents.Items) 
               {
                  Agent a = (Agent)o;
                  AddRes(res, a.id);
               }
            }
            else if (rbDivision.Checked)
            {
               Division d = cbDivisions.SelectedItem as Division;

               if (d != null)
               {
                  foreach (GRSoft.NapoleonManager.Division.DivisionAgent a in d.agents)
                     AddRes(res, a.id);
               }
            }
            else if (rbAgents.Checked)
            {
               Agent a = cbAgents.SelectedItem as Agent;

               if (a != null)
                  AddRes(res, a.id);
            }


            return res.ToString();
         } 
      }

      private void AddRes(StringBuilder result, string p)
      {
         if (result.Length > 0)
            result.Append(',');
         result.Append(p);
      }
   }
}
